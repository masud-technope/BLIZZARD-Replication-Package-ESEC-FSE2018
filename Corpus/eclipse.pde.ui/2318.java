/*******************************************************************************
 * Copyright (c) 2010, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.anttasks.tests;

import java.io.IOException;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ApiToolingCompareAntTaskTests extends AntRunnerTestCase {

    @Override
    public String getTestResourcesFolder() {
        //$NON-NLS-1$
        return "apitooling.compare/";
    }

    public void test1() throws Exception {
        //$NON-NLS-1$
        runTaskAndVerify("test1");
    }

    private void runTaskAndVerify(String resourceName) throws Exception, CoreException, ParserConfigurationException, SAXException, IOException {
        IFolder buildFolder = newTest(getTestResourcesFolder(), new String[] { resourceName, //$NON-NLS-1$
        "profile" });
        //$NON-NLS-1$
        String buildXMLPath = buildFolder.getFile("build.xml").getLocation().toOSString();
        Properties properties = new Properties();
        //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("baseline_location", buildFolder.getFile("rcpapp_1.0.0.zip").getLocation().toOSString());
        //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("profile_location", buildFolder.getFile("rcpapp_2.0.0.zip").getLocation().toOSString());
        //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("report_location", buildFolder.getLocation().append("report").toOSString());
        //$NON-NLS-1$
        properties.put("filter_location", buildFolder.getLocation().toOSString());
        //$NON-NLS-1$
        runAntScript(buildXMLPath, new String[] { "run" }, buildFolder.getLocation().toOSString(), properties);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse("allNonApiBundles must not exist", buildFolder.getFolder("allNonApiBundles").exists());
        //$NON-NLS-1$
        IFolder folder = buildFolder.getFolder("report");
        //$NON-NLS-1$
        assertTrue("report folder must exist", folder.exists());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("report xml must exist", folder.getFile("compare.xml").exists());
        //$NON-NLS-1$
        InputSource is = new InputSource(folder.getFile("compare.xml").getContents());
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(is);
        //$NON-NLS-1$
        NodeList elems = doc.getElementsByTagName("delta");
        boolean found = false;
        for (int i = 0; i < elems.getLength(); i++) {
            //$NON-NLS-1$
            String value = elems.item(i).getAttributes().getNamedItem("componentId").getNodeValue();
            //$NON-NLS-1$ //$NON-NLS-2$
            assertFalse("org.example.rcpintro should have been filtered out.", value.startsWith("org.example.rcpintro"));
            if (//$NON-NLS-1$
            value.startsWith("org.example.rcpmail")) {
                found = true;
            }
        }
        //$NON-NLS-1$
        assertTrue("org.example.rcpmail should be present", found);
    }

    public void test2() throws Exception {
        //$NON-NLS-1$
        runTaskAndVerify("test2");
    }

    public void test3() throws Exception {
        //$NON-NLS-1$
        runTaskAndVerify("test3");
    }
}
