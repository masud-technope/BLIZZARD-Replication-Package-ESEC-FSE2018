/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Ferenc Hechler, ferenc_hechler@users.sourceforge.net -  83258 [jar exporter] Deploy java application as executable jar
 *     Ferenc Hechler, ferenc_hechler@users.sourceforge.net - 262768 [jar exporter] Jardesc for normal Jar contains <fatjar builder="...
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.jarpackager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptorProxy;
import org.eclipse.ltk.core.refactoring.history.IRefactoringHistoryService;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.ui.jarpackager.IJarDescriptionWriter;
import org.eclipse.jdt.ui.jarpackager.JarPackageData;
import org.eclipse.jdt.internal.ui.IJavaStatusConstants;
import org.eclipse.jdt.internal.ui.JavaPlugin;

/**
 * Writes a JarPackage to an underlying OutputStream
 */
public class JarPackageWriter extends Object implements IJarDescriptionWriter {

    private final OutputStream fOutputStream;

    private final String fEncoding;

    /**
	 * Create a JarPackageWriter on the given output stream. It is the clients responsibility to
	 * close the output stream.
	 * 
	 * @param outputStream the the output stream
	 * @param encoding the encoding
	 */
    public  JarPackageWriter(OutputStream outputStream, String encoding) {
        Assert.isNotNull(outputStream);
        fOutputStream = new BufferedOutputStream(outputStream);
        fEncoding = encoding;
    }

    @Override
    public void write(JarPackageData jarPackage) throws CoreException {
        try {
            writeXML(jarPackage);
        } catch (IOException ex) {
            String message = (ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "");
            throw new CoreException(new Status(IStatus.ERROR, JavaPlugin.getPluginId(), IJavaStatusConstants.INTERNAL_ERROR, message, ex));
        }
    }

    /**
	 * Writes a XML representation of the JAR specification to to the underlying stream.
	 * 
	 * @param jarPackage the JAR package data
	 * @exception IOException if writing to the underlying stream fails
	 */
    public void writeXML(JarPackageData jarPackage) throws IOException {
        Assert.isNotNull(jarPackage);
        DocumentBuilder docBuilder = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        try {
            docBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new IOException(JarPackagerMessages.JarWriter_error_couldNotGetXmlBuilder);
        }
        Document document = docBuilder.newDocument();
        // Create the document
        Element xmlJarDesc = document.createElement(JarPackagerUtil.DESCRIPTION_EXTENSION);
        document.appendChild(xmlJarDesc);
        xmlWriteJarLocation(jarPackage, document, xmlJarDesc);
        xmlWriteOptions(jarPackage, document, xmlJarDesc);
        xmlWriteRefactoring(jarPackage, document, xmlJarDesc);
        xmlWriteSelectedProjects(jarPackage, document, xmlJarDesc);
        if (jarPackage.areGeneratedFilesExported())
            xmlWriteManifest(jarPackage, document, xmlJarDesc);
        xmlWriteSelectedElements(jarPackage, document, xmlJarDesc);
        try {
            // Write the document to the stream
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            //$NON-NLS-1$
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, fEncoding);
            //$NON-NLS-1$
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            //$NON-NLS-1$ //$NON-NLS-2$
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(fOutputStream);
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new IOException(JarPackagerMessages.JarWriter_error_couldNotTransformToXML);
        }
    }

    private void xmlWriteJarLocation(JarPackageData jarPackage, Document document, Element xmlJarDesc) throws DOMException {
        Element jar = document.createElement(JarPackagerUtil.JAR_EXTENSION);
        xmlJarDesc.appendChild(jar);
        //$NON-NLS-1$
        jar.setAttribute("path", jarPackage.getJarLocation().toPortableString());
    }

    private void xmlWriteOptions(JarPackageData jarPackage, Document document, Element xmlJarDesc) throws DOMException {
        //$NON-NLS-1$
        Element options = document.createElement("options");
        xmlJarDesc.appendChild(options);
        //$NON-NLS-2$ //$NON-NLS-1$
        options.setAttribute("overwrite", "" + jarPackage.allowOverwrite());
        //$NON-NLS-2$ //$NON-NLS-1$
        options.setAttribute("compress", "" + jarPackage.isCompressed());
        //$NON-NLS-2$ //$NON-NLS-1$
        options.setAttribute("exportErrors", "" + jarPackage.areErrorsExported());
        //$NON-NLS-2$ //$NON-NLS-1$
        options.setAttribute("exportWarnings", "" + jarPackage.exportWarnings());
        //$NON-NLS-2$ //$NON-NLS-1$
        options.setAttribute("saveDescription", "" + jarPackage.isDescriptionSaved());
        //$NON-NLS-1$
        options.setAttribute("descriptionLocation", jarPackage.getDescriptionLocation().toPortableString());
        //$NON-NLS-2$ //$NON-NLS-1$
        options.setAttribute("useSourceFolders", "" + jarPackage.useSourceFolderHierarchy());
        //$NON-NLS-2$ //$NON-NLS-1$
        options.setAttribute("buildIfNeeded", "" + jarPackage.isBuildingIfNeeded());
        //$NON-NLS-1$//$NON-NLS-2$
        options.setAttribute("includeDirectoryEntries", "" + jarPackage.areDirectoryEntriesIncluded());
        //$NON-NLS-1$//$NON-NLS-2$
        options.setAttribute("storeRefactorings", "" + jarPackage.isRefactoringAware());
    }

    private void xmlWriteRefactoring(JarPackageData jarPackage, Document document, Element xmlJarDesc) throws DOMException {
        //$NON-NLS-1$
        Element refactoring = document.createElement("storedRefactorings");
        xmlJarDesc.appendChild(refactoring);
        //$NON-NLS-1$ //$NON-NLS-2$
        refactoring.setAttribute("structuralOnly", "" + jarPackage.isExportStructuralOnly());
        //$NON-NLS-1$ //$NON-NLS-2$
        refactoring.setAttribute("deprecationInfo", "" + jarPackage.isDeprecationAware());
        final IProject[] projects = jarPackage.getRefactoringProjects();
        if (projects != null && projects.length > 0) {
            for (int index = 0; index < projects.length; index++) //$NON-NLS-1$
            refactoring.setAttribute(//$NON-NLS-1$
            "project" + (index + 1), //$NON-NLS-1$
            projects[index].getName());
        }
        final RefactoringDescriptorProxy[] proxies = jarPackage.getRefactoringDescriptors();
        if (proxies != null && proxies.length > 0) {
            int count = 1;
            IRefactoringHistoryService service = RefactoringCore.getHistoryService();
            try {
                service.connect();
                for (int index = 0; index < proxies.length; index++, count++) {
                    try {
                        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        service.writeRefactoringDescriptors(new RefactoringDescriptorProxy[] { proxies[index] }, stream, RefactoringDescriptor.NONE, true, null);
                        //$NON-NLS-1$ //$NON-NLS-2$
                        refactoring.setAttribute("refactoring" + count, stream.toString("UTF-8"));
                    } catch (CoreException exception) {
                        JavaPlugin.log(exception);
                    } catch (UnsupportedEncodingException exception) {
                        Assert.isTrue(false);
                    }
                }
            } finally {
                service.disconnect();
            }
        }
    }

    private void xmlWriteManifest(JarPackageData jarPackage, Document document, Element xmlJarDesc) throws DOMException {
        //$NON-NLS-1$
        Element manifest = document.createElement("manifest");
        xmlJarDesc.appendChild(manifest);
        //$NON-NLS-1$
        manifest.setAttribute("manifestVersion", jarPackage.getManifestVersion());
        //$NON-NLS-2$ //$NON-NLS-1$
        manifest.setAttribute("usesManifest", "" + jarPackage.usesManifest());
        //$NON-NLS-2$ //$NON-NLS-1$
        manifest.setAttribute("reuseManifest", "" + jarPackage.isManifestReused());
        //$NON-NLS-2$ //$NON-NLS-1$
        manifest.setAttribute("saveManifest", "" + jarPackage.isManifestSaved());
        //$NON-NLS-2$ //$NON-NLS-1$
        manifest.setAttribute("generateManifest", "" + jarPackage.isManifestGenerated());
        //$NON-NLS-1$
        manifest.setAttribute("manifestLocation", jarPackage.getManifestLocation().toPortableString());
        if (jarPackage.getManifestMainClass() != null)
            //$NON-NLS-1$
            manifest.setAttribute("mainClassHandleIdentifier", jarPackage.getManifestMainClass().getHandleIdentifier());
        xmlWriteSealingInfo(jarPackage, document, manifest);
    }

    private void xmlWriteSealingInfo(JarPackageData jarPackage, Document document, Element manifest) throws DOMException {
        //$NON-NLS-1$
        Element sealing = document.createElement("sealing");
        manifest.appendChild(sealing);
        //$NON-NLS-2$ //$NON-NLS-1$
        sealing.setAttribute("sealJar", "" + jarPackage.isJarSealed());
        //$NON-NLS-1$
        Element packagesToSeal = document.createElement("packagesToSeal");
        sealing.appendChild(packagesToSeal);
        add(jarPackage.getPackagesToSeal(), packagesToSeal, document);
        //$NON-NLS-1$
        Element packagesToUnSeal = document.createElement("packagesToUnSeal");
        sealing.appendChild(packagesToUnSeal);
        add(jarPackage.getPackagesToUnseal(), packagesToUnSeal, document);
    }

    private void xmlWriteSelectedElements(JarPackageData jarPackage, Document document, Element xmlJarDesc) throws DOMException {
        //$NON-NLS-1$
        Element selectedElements = document.createElement("selectedElements");
        xmlJarDesc.appendChild(selectedElements);
        //$NON-NLS-2$ //$NON-NLS-1$
        selectedElements.setAttribute("exportClassFiles", "" + jarPackage.areClassFilesExported());
        //$NON-NLS-2$ //$NON-NLS-1$
        selectedElements.setAttribute("exportOutputFolder", "" + jarPackage.areOutputFoldersExported());
        //$NON-NLS-2$ //$NON-NLS-1$
        selectedElements.setAttribute("exportJavaFiles", "" + jarPackage.areJavaFilesExported());
        Object[] elements = jarPackage.getElements();
        for (int i = 0; i < elements.length; i++) {
            Object element = elements[i];
            if (element instanceof IResource)
                add((IResource) element, selectedElements, document);
            else if (element instanceof IJavaElement)
                add((IJavaElement) element, selectedElements, document);
        // Note: Other file types are not handled by this writer
        }
    }

    private void xmlWriteSelectedProjects(JarPackageData jarPackage, Document document, Element xmlJarDesc) throws DOMException {
        //$NON-NLS-1$
        Element selectedElements = document.createElement("selectedProjects");
        xmlJarDesc.appendChild(selectedElements);
        Object[] elements = jarPackage.getRefactoringProjects();
        for (int index = 0; index < elements.length; index++) {
            Object element = elements[index];
            if (element instanceof IResource)
                add((IResource) element, selectedElements, document);
        }
    }

    /**
	 * Closes this stream. It is the client's responsibility to close the stream.
	 * 
	 * @throws CoreException if closing the stream fails
	 */
    @Override
    public void close() throws CoreException {
        if (fOutputStream != null) {
            try {
                fOutputStream.close();
            } catch (IOException ex) {
                String message = (ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "");
                throw new CoreException(new Status(IStatus.ERROR, JavaPlugin.getPluginId(), IJavaStatusConstants.INTERNAL_ERROR, message, ex));
            }
        }
    }

    private void add(IResource resource, Element parent, Document document) {
        if (resource.getType() == IResource.PROJECT) {
            //$NON-NLS-1$
            Element element = document.createElement("project");
            parent.appendChild(element);
            //$NON-NLS-1$
            element.setAttribute("name", resource.getName());
        } else if (resource.getType() == IResource.FILE) {
            //$NON-NLS-1$
            Element element = document.createElement("file");
            parent.appendChild(element);
            //$NON-NLS-1$
            element.setAttribute("path", resource.getFullPath().toString());
        } else if (resource.getType() == IResource.FOLDER) {
            //$NON-NLS-1$
            Element element = document.createElement("folder");
            parent.appendChild(element);
            //$NON-NLS-1$
            element.setAttribute("path", resource.getFullPath().toString());
        }
    }

    private void add(IJavaElement javaElement, Element parent, Document document) {
        //$NON-NLS-1$
        Element element = document.createElement("javaElement");
        parent.appendChild(element);
        //$NON-NLS-1$
        element.setAttribute("handleIdentifier", javaElement.getHandleIdentifier());
    }

    private void add(IPackageFragment[] packages, Element parent, Document document) {
        for (int i = 0; i < packages.length; i++) {
            //$NON-NLS-1$
            Element pkg = document.createElement("package");
            parent.appendChild(pkg);
            //$NON-NLS-1$
            pkg.setAttribute("handleIdentifier", packages[i].getHandleIdentifier());
        }
    }

    /*
	 * This writer always returns OK
	 */
    @Override
    public IStatus getStatus() {
        return Status.OK_STATUS;
    }
}
