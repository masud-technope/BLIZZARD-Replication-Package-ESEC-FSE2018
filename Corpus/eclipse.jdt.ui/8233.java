/*******************************************************************************
 * Copyright (c) 2009, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Ferenc Hechler, ferenc_hechler@users.sourceforge.net - 83258 [jar exporter] Deploy java application as executable jar
 *     Ferenc Hechler, ferenc_hechler@users.sourceforge.net - 262766 [jar exporter] ANT file for Jar-in-Jar option contains relative path to jar-rsrc-loader.zip
 *     Ferenc Hechler, ferenc_hechler@users.sourceforge.net - 262763 [jar exporter] remove Built-By attribute in ANT files from Fat JAR Exporter
 *     Ferenc Hechler, ferenc_hechler@users.sourceforge.net - 269201 [jar exporter] ant file produced by Export runnable jar contains absolut paths instead of relative to workspace
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.jarpackagerfat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * @since 3.5
 */
public class UnpackFatJarAntExporter extends FatJarAntExporter {

    public  UnpackFatJarAntExporter(IPath antScriptLocation, IPath jarLocation, ILaunchConfiguration launchConfiguration) {
        super(antScriptLocation, jarLocation, launchConfiguration);
    }

    @Override
    protected void buildANTScript(IPath antScriptLocation, String projectName, IPath absJarfile, String mainClass, SourceInfo[] sourceInfos) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(antScriptLocation.toFile());
            String absJarname = absJarfile.toString();
            DocumentBuilder docBuilder = null;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            try {
                docBuilder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException ex) {
                throw new IOException(FatJarPackagerMessages.FatJarPackageAntScript_error_couldNotGetXmlBuilder);
            }
            Document document = docBuilder.newDocument();
            Node comment;
            // Create the document
            //$NON-NLS-1$
            Element project = document.createElement("project");
            //$NON-NLS-1$ //$NON-NLS-2$
            project.setAttribute("name", "Create Runnable Jar for Project " + projectName);
            //$NON-NLS-1$ //$NON-NLS-2$
            project.setAttribute("default", "create_run_jar");
            document.appendChild(project);
            //$NON-NLS-1$
            comment = document.createComment("this file was created by Eclipse Runnable JAR Export Wizard");
            project.appendChild(comment);
            //$NON-NLS-1$
            comment = document.createComment("ANT 1.7 is required                                        ");
            project.appendChild(comment);
            addBaseDirProperties(document, project);
            //$NON-NLS-1$
            Element target = document.createElement("target");
            //$NON-NLS-1$ //$NON-NLS-2$
            target.setAttribute("name", "create_run_jar");
            project.appendChild(target);
            //$NON-NLS-1$
            Element jar = document.createElement("jar");
            //$NON-NLS-1$s
            jar.setAttribute("destfile", substituteBaseDirs(absJarname));
            //$NON-NLS-1$ //$NON-NLS-2$
            jar.setAttribute("filesetmanifest", "mergewithoutmain");
            target.appendChild(jar);
            //$NON-NLS-1$
            Element manifest = document.createElement("manifest");
            jar.appendChild(manifest);
            //$NON-NLS-1$
            Element attribute = document.createElement("attribute");
            //$NON-NLS-1$ //$NON-NLS-2$s
            attribute.setAttribute("name", "Main-Class");
            //$NON-NLS-1$
            attribute.setAttribute("value", mainClass);
            manifest.appendChild(attribute);
            //$NON-NLS-1$
            attribute = document.createElement("attribute");
            //$NON-NLS-1$ //$NON-NLS-2$s
            attribute.setAttribute("name", "Class-Path");
            //$NON-NLS-1$ //$NON-NLS-2$
            attribute.setAttribute("value", ".");
            manifest.appendChild(attribute);
            for (int i = 0; i < sourceInfos.length; i++) {
                SourceInfo sourceInfo = sourceInfos[i];
                if (sourceInfo.isJar) {
                    Element zipfileset = //$NON-NLS-1$
                    document.createElement(//$NON-NLS-1$
                    "zipfileset");
                    zipfileset.setAttribute("src", substituteBaseDirs(//$NON-NLS-1$
                    sourceInfo.absPath));
                    //$NON-NLS-1$ //$NON-NLS-2$
                    zipfileset.setAttribute("excludes", "META-INF/*.SF");
                    jar.appendChild(zipfileset);
                } else {
                    Element fileset = //$NON-NLS-1$
                    document.createElement(//$NON-NLS-1$
                    "fileset");
                    fileset.setAttribute("dir", substituteBaseDirs(//$NON-NLS-1$
                    sourceInfo.absPath));
                    jar.appendChild(fileset);
                }
            }
            try {
                // Write the document to the stream
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                //$NON-NLS-1$
                transformer.setOutputProperty(//$NON-NLS-1$
                OutputKeys.METHOD, //$NON-NLS-1$
                "xml");
                //$NON-NLS-1$
                transformer.setOutputProperty(//$NON-NLS-1$
                OutputKeys.ENCODING, //$NON-NLS-1$
                "UTF-8");
                //$NON-NLS-1$
                transformer.setOutputProperty(//$NON-NLS-1$
                OutputKeys.INDENT, //$NON-NLS-1$
                "yes");
                //$NON-NLS-1$ //$NON-NLS-2$
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(outputStream);
                transformer.transform(source, result);
            } catch (TransformerException e) {
                throw new IOException(FatJarPackagerMessages.FatJarPackageAntScript_error_couldNotTransformToXML);
            }
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
