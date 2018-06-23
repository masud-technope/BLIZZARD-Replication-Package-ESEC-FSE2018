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

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ApiToolingApiuseAntTaskTests extends AntRunnerTestCase {

    @Override
    public String getTestResourcesFolder() {
        //$NON-NLS-1$
        return "apitooling.apiuse/";
    }

    private IFolder runTaskAndVerify(String resourceName) throws Exception, CoreException, ParserConfigurationException, SAXException, IOException {
        IFolder buildFolder = newTest(getTestResourcesFolder(), new String[] { resourceName, //$NON-NLS-1$
        "profile" });
        //$NON-NLS-1$
        String buildXMLPath = buildFolder.getFile("build.xml").getLocation().toOSString();
        Properties properties = new Properties();
        //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("baseline_location", buildFolder.getFile("OSGiProduct.zip").getLocation().toOSString());
        //$NON-NLS-1$ //$NON-NLS-2$
        properties.put("report_location", buildFolder.getLocation().append("report").toOSString());
        //$NON-NLS-1$
        properties.put("filter_location", buildFolder.getLocation().toOSString());
        //$NON-NLS-1$
        runAntScript(buildXMLPath, new String[] { "run" }, buildFolder.getLocation().toOSString(), properties);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse("allNonApiBundles must not exist", buildFolder.getFolder("allNonApiBundles").exists());
        //$NON-NLS-1$
        IFolder reportFolder = buildFolder.getFolder("report");
        //$NON-NLS-1$
        assertTrue("report folder must exist", reportFolder.exists());
        //$NON-NLS-1$
        assertTrue("xml folder must exist", reportFolder.exists());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("meta.xml must exist", reportFolder.getFile("meta.xml").exists());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("not_searched.xml must exist", reportFolder.getFile("not_searched.xml").exists());
        return reportFolder;
    }

    public void test1() throws Exception {
        //$NON-NLS-1$
        IFolder reportFolder = runTaskAndVerify("test1");
        //$NON-NLS-1$
        InputSource is = new InputSource(reportFolder.getFile("not_searched.xml").getContents());
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(is);
        //$NON-NLS-1$
        NodeList elems = doc.getElementsByTagName("component");
        for (int index = 0; index < elems.getLength(); ++index) {
            //$NON-NLS-1$
            String value = elems.item(index).getAttributes().getNamedItem("id").getNodeValue();
            boolean pass = false;
            if (//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            value.startsWith("org.eclipse.osgi") || value.contains("illegaluse") || value.contains("oldstyle")) {
                pass = true;
            }
            //$NON-NLS-1$
            assertTrue(value + " should have been filtered out.", pass);
        }
    }

    public void test2() throws Exception {
        //$NON-NLS-1$
        IFolder reportFolder = runTaskAndVerify("test2");
        IResource[] members = reportFolder.members();
        boolean valid = false;
        boolean validDir = false;
        for (int index = 0; index < members.length; index++) {
            if (!members[index].getLocation().toFile().isDirectory()) {
                continue;
            }
            //$NON-NLS-1$
            valid = members[index].getName().startsWith("org.example");
            //$NON-NLS-1$
            assertTrue(members[index].getName() + " should have been filtered out", valid);
            File[] dirs = members[index].getLocation().toFile().listFiles();
            for (int i = 0; i < dirs.length; i++) {
                validDir = //$NON-NLS-1$
                dirs[i].getName().startsWith(//$NON-NLS-1$
                "org.example");
                assertTrue(//$NON-NLS-1$
                dirs[i].getName() + " should have been filtered out", //$NON-NLS-1$
                validDir);
            }
        }
        //$NON-NLS-1$
        assertTrue("None of the example plug-ins were scanned", valid);
        //$NON-NLS-1$
        assertTrue("None of the example plug-ins were scanned", validDir);
    }

    public void test3() throws Exception {
        //$NON-NLS-1$
        IFolder reportFolder = runTaskAndVerify("test3");
        IResource[] members = reportFolder.members();
        boolean valid = false;
        boolean validDir = false;
        for (int index = 0; index < members.length; index++) {
            if (!members[index].getLocation().toFile().isDirectory()) {
                continue;
            }
            //$NON-NLS-1$
            valid = members[index].getName().startsWith("org.example");
            //$NON-NLS-1$
            assertTrue(members[index].getName() + " should have been filtered out", valid);
            File[] dirs = members[index].getLocation().toFile().listFiles();
            for (int i = 0; i < dirs.length; i++) {
                validDir = //$NON-NLS-1$
                dirs[i].getName().startsWith(//$NON-NLS-1$
                "org.example");
                assertTrue(//$NON-NLS-1$
                dirs[i].getName() + " should have been filtered out", //$NON-NLS-1$
                validDir);
            }
        }
        //$NON-NLS-1$
        assertTrue("None of the example plug-ins were scanned", valid);
        //$NON-NLS-1$
        assertTrue("None of the example plug-ins were scanned", validDir);
    }

    /**
	 * Tests that a use scan will find illegal use problems that can be filtered
	 *
	 * @throws Exception
	 */
    public void testIllegalUse() throws Exception {
        //$NON-NLS-1$
        IFolder reportFolder = runTaskAndVerify("testIllegalUse");
        IResource[] members = reportFolder.members();
        boolean valid = false;
        boolean validDir = false;
        for (int index = 0; index < members.length; index++) {
            if (!members[index].getLocation().toFile().isDirectory()) {
                continue;
            }
            //$NON-NLS-1$
            valid = members[index].getName().startsWith("org.eclipse.osgi");
            //$NON-NLS-1$
            assertTrue(members[index].getName() + " should have been filtered out", valid);
            File[] dirs = members[index].getLocation().toFile().listFiles();
            for (int i = 0; i < dirs.length; i++) {
                validDir = //$NON-NLS-1$
                dirs[i].getName().startsWith(//$NON-NLS-1$
                "org.example.test.illegaluse");
                assertTrue(//$NON-NLS-1$
                dirs[i].getName() + " should have been filtered out", //$NON-NLS-1$
                validDir);
            }
        }
    // This test is not working properly, see Bug 405302
    // assertTrue("The illegal use plug-in was not scanned", valid);
    // assertTrue("The illegal use plug-in was not scanned", validDir);
    }

    /**
	 * Tests that a use scan will find illegal use problems that can be filtered
	 *
	 * @throws Exception
	 */
    public void testIllegalUseFiltered() throws Exception {
        //$NON-NLS-1$
        IFolder reportFolder = runTaskAndVerify("testIllegalUseFiltered");
        IResource[] members = reportFolder.members();
        for (int index = 0; index < members.length; index++) {
            if (members[index].getLocation().toFile().isDirectory()) {
                fail(//$NON-NLS-1$
                members[index].getName() + " should have been filtered using a .api_filters file");
            }
        }
    }

    /**
	 * Tests that a use scan will find problems when a required bundle is an old
	 * style (pre-OSGi) plug-in. Old style plug-ins are only supported when
	 * running in the same JRE as an OSGi runtime.
	 *
	 * @throws Exception
	 */
    public void testOldStylePlugin() throws Exception {
        //$NON-NLS-1$
        IFolder reportFolder = runTaskAndVerify("testOldStylePlugin");
        IResource[] members = reportFolder.members();
        boolean valid = false;
        boolean validDir = false;
        for (int index = 0; index < members.length; index++) {
            if (!members[index].getLocation().toFile().isDirectory()) {
                continue;
            }
            //$NON-NLS-1$
            valid = members[index].getName().startsWith("org.eclipse.osgi");
            //$NON-NLS-1$
            assertTrue(members[index].getName() + " should have been filtered out", valid);
            File[] dirs = members[index].getLocation().toFile().listFiles();
            for (int i = 0; i < dirs.length; i++) {
                validDir = //$NON-NLS-1$
                dirs[i].getName().startsWith(//$NON-NLS-1$
                "org.example.test.oldstyle.usage");
                assertTrue(//$NON-NLS-1$
                dirs[i].getName() + " should have been filtered out", //$NON-NLS-1$
                validDir);
            }
        }
        //$NON-NLS-1$
        assertTrue("The old style plug-in was not scanned", valid);
        //$NON-NLS-1$
        assertTrue("The old style plug-in was not scanned", validDir);
    }
}
