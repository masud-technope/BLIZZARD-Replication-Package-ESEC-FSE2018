/*******************************************************************************
 *  Copyright (c) 2005, 2015 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.wizards.site;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.pde.internal.core.natures.PDE;
import org.eclipse.pde.internal.core.site.WorkspaceSiteModel;
import org.eclipse.pde.internal.core.util.CoreUtility;
import org.eclipse.pde.internal.ui.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ISetSelectionTarget;

public class NewSiteProjectCreationOperation extends WorkspaceModifyOperation {

    private Display fDisplay;

    private IProject fProject;

    private IPath fPath;

    private String fWebLocation;

    public  NewSiteProjectCreationOperation(Display display, IProject project, IPath path, String webLocation) {
        fDisplay = display;
        fProject = project;
        fPath = path;
        fWebLocation = webLocation;
    }

    @Override
    protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
        int numUnits = fWebLocation == null ? 3 : 4;
        monitor.beginTask(PDEUIMessages.NewSiteWizard_creatingProject, numUnits);
        CoreUtility.createProject(fProject, fPath, monitor);
        fProject.open(monitor);
        CoreUtility.addNatureToProject(fProject, PDE.SITE_NATURE, monitor);
        monitor.worked(1);
        if (fWebLocation != null) {
            CoreUtility.createFolder(fProject.getFolder(fWebLocation));
            createXSLFile();
            createCSSFile();
            createHTMLFile();
            monitor.worked(1);
        }
        monitor.subTask(PDEUIMessages.NewSiteWizard_creatingManifest);
        IFile file = createSiteManifest();
        monitor.worked(1);
        openFile(file);
        monitor.worked(1);
    }

    /**
	 * @return location of the created site.xml
	 * @throws CoreException
	 */
    private IFile createSiteManifest() throws CoreException {
        //$NON-NLS-1$
        IFile file = fProject.getFile("site.xml");
        if (file.exists())
            return file;
        WorkspaceSiteModel model = new WorkspaceSiteModel(file);
        model.getSite();
        // Save the model
        model.save();
        model.dispose();
        // Set the default editor
        IDE.setDefaultEditor(file, IPDEUIConstants.SITE_EDITOR_ID);
        return file;
    }

    private void openFile(final IFile file) {
        fDisplay.asyncExec(new Runnable() {

            @Override
            public void run() {
                IWorkbenchWindow ww = PDEPlugin.getActiveWorkbenchWindow();
                if (ww == null) {
                    return;
                }
                IWorkbenchPage page = ww.getActivePage();
                if (page == null || !file.exists())
                    return;
                IWorkbenchPart focusPart = page.getActivePart();
                if (focusPart instanceof ISetSelectionTarget) {
                    ISelection selection = new StructuredSelection(file);
                    ((ISetSelectionTarget) focusPart).selectReveal(selection);
                }
                try {
                    page.openEditor(new FileEditorInput(file), IPDEUIConstants.SITE_EDITOR_ID);
                } catch (PartInitException e) {
                }
            }
        });
    }

    private void createHTMLFile() {
        StringWriter swriter = new StringWriter();
        PrintWriter writer = new PrintWriter(swriter);
        //$NON-NLS-1$
        writer.println("<html>");
        //$NON-NLS-1$
        writer.println("<head>");
        //$NON-NLS-1$ //$NON-NLS-2$
        writer.println("<title>" + fProject.getName() + "</title>");
        //$NON-NLS-1$
        writer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        //$NON-NLS-1$ //$NON-NLS-2$
        writer.println("<style>@import url(\"" + fWebLocation + "/site.css\");</style>");
        //$NON-NLS-1$
        writer.println("<script type=\"text/javascript\">");
        //$NON-NLS-1$
        writer.println("	var returnval = 0;");
        //$NON-NLS-1$
        writer.println("	var stylesheet, xmlFile, cache, doc;");
        //$NON-NLS-1$
        writer.println("	function init(){");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		// NSCP 7.1+ / Mozilla 1.4.1+ / Safari");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		// Use the standard DOM Level 2 technique, if it is supported");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		if (document.implementation && document.implementation.createDocument) {");
        writer.println("			xmlFile = document.implementation.createDocument(\"\", \"\", null);");
        writer.println("			stylesheet = document.implementation.createDocument(\"\", \"\", null);");
        writer.println("			if (xmlFile.load){");
        writer.println("				xmlFile.load(\"site.xml\");");
        //$NON-NLS-1$ //$NON-NLS-2$
        writer.println("				stylesheet.load(\"" + fWebLocation + "/site.xsl\");");
        writer.println("			} else {");
        //$NON-NLS-1$ //$NON-NLS-2$
        writer.println("				alert(\"" + PDEUIMessages.SiteHTML_loadError + "\");");
        writer.println("			}");
        writer.println("			xmlFile.addEventListener(\"load\", transform, false);");
        writer.println("			stylesheet.addEventListener(\"load\", transform, false);");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		}");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		//IE 6.0+ solution");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		else if (window.ActiveXObject) {");
        writer.println("			xmlFile = new ActiveXObject(\"msxml2.DOMDocument.3.0\");");
        writer.println("			xmlFile.async = false;");
        writer.println("			xmlFile.load(\"site.xml\");");
        writer.println("			stylesheet = new ActiveXObject(\"msxml2.FreeThreadedDOMDocument.3.0\");");
        writer.println("			stylesheet.async = false;");
        //$NON-NLS-1$ //$NON-NLS-2$
        writer.println("			stylesheet.load(\"" + fWebLocation + "/site.xsl\");");
        writer.println("			cache = new ActiveXObject(\"msxml2.XSLTemplate.3.0\");");
        writer.println("			cache.stylesheet = stylesheet;");
        writer.println("			transformData();");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		}");
        //$NON-NLS-1$
        writer.println("	}");
        //$NON-NLS-1$
        writer.println("	// separate transformation function for IE 6.0+");
        //$NON-NLS-1$
        writer.println("	function transformData(){");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		var processor = cache.createProcessor();");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		processor.input = xmlFile;");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		processor.transform();");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		data.innerHTML = processor.output;");
        //$NON-NLS-1$
        writer.println("	}");
        //$NON-NLS-1$
        writer.println("	// separate transformation function for NSCP 7.1+ and Mozilla 1.4.1+ ");
        //$NON-NLS-1$
        writer.println("	function transform(){");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		returnval+=1;");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		if (returnval==2){");
        writer.println("			var processor = new XSLTProcessor();");
        writer.println("			processor.importStylesheet(stylesheet); ");
        writer.println("			doc = processor.transformToDocument(xmlFile);");
        writer.println("			document.getElementById(\"data\").innerHTML = doc.documentElement.innerHTML;");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		}");
        //$NON-NLS-1$
        writer.println("	}");
        //$NON-NLS-1$
        writer.println("</script>");
        //$NON-NLS-1$
        writer.println("</head>");
        //$NON-NLS-1$
        writer.println("<body onload=\"init();\">");
        //$NON-NLS-1$
        writer.println("<!--[insert static HTML here]-->");
        //$NON-NLS-1$
        writer.println("<div id=\"data\"><!-- this is where the transformed data goes --></div>");
        //$NON-NLS-1$
        writer.println("</body>");
        //$NON-NLS-1$
        writer.println("</html>");
        writer.flush();
        //$NON-NLS-1$
        writeFile(fProject.getFile("index.html"), swriter);
    }

    private void createCSSFile() {
        StringWriter swriter = new StringWriter();
        PrintWriter writer = new PrintWriter(swriter);
        //$NON-NLS-1$
        writer.println("<STYLE type=\"text/css\">");
        //$NON-NLS-1$
        writer.println("td.spacer {padding-bottom: 10px; padding-top: 10px;}");
        //$NON-NLS-1$
        writer.println(".title { font-family: sans-serif; color: #99AACC;}");
        //$NON-NLS-1$
        writer.println(".bodyText { font-family: sans-serif; font-size: 9pt; color:#000000;  }");
        //$NON-NLS-1$
        writer.println(".sub-header { font-family: sans-serif; font-style: normal; font-weight: bold; font-size: 9pt; color: white;}");
        //$NON-NLS-1$
        writer.println(".log-text {font-family: sans-serif; font-style: normal; font-weight: lighter; font-size: 8pt; color:black;}");
        //$NON-NLS-1$
        writer.println(".big-header { font-family: sans-serif; font-style: normal; font-weight: bold; font-size: 9pt; color: white; border-top:10px solid white;}");
        //$NON-NLS-1$
        writer.println(".light-row {background:#FFFFFF}");
        //$NON-NLS-1$
        writer.println(".dark-row {background:#EEEEFF}");
        //$NON-NLS-1$
        writer.println(".header {background:#99AADD}");
        //$NON-NLS-1$
        writer.println("#indent {word-wrap : break-word;width :300px;text-indent:10px;}");
        //$NON-NLS-1$
        writer.println("</STYLE>");
        writer.flush();
        //$NON-NLS-1$
        writeFile(fProject.getFile(fWebLocation + "/site.css"), swriter);
    }

    private void createXSLFile() {
        StringWriter swriter = new StringWriter();
        PrintWriter writer = new PrintWriter(swriter);
        //$NON-NLS-1$
        writer.println("<xsl:stylesheet version = '1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform' xmlns:msxsl=\"urn:schemas-microsoft-com:xslt\">");
        //$NON-NLS-1$
        writer.println("<xsl:output method=\"html\" encoding=\"UTF-8\"/>");
        //$NON-NLS-1$
        writer.println("<xsl:key name=\"cat\" match=\"category\" use=\"@name\"/>");
        //$NON-NLS-1$
        writer.println("<xsl:template match=\"/\">");
        //$NON-NLS-1$
        writer.println("<xsl:for-each select=\"site\">");
        //$NON-NLS-1$
        writer.println("	<html>");
        //$NON-NLS-1$
        writer.println("	<head>");
        //$NON-NLS-1$ //$NON-NLS-2$
        writer.println("	<title>" + fProject.getName() + "</title>");
        //$NON-NLS-1$ //$NON-NLS-2$
        writer.println("	<style>@import url(\"" + fWebLocation + "/site.css\");</style>");
        //$NON-NLS-1$
        writer.println("	</head>");
        //$NON-NLS-1$
        writer.println("	<body>");
        //$NON-NLS-1$ //$NON-NLS-2$
        writer.println("	<h1 class=\"title\">" + fProject.getName() + "</h1>");
        //$NON-NLS-1$
        writer.println("	<p class=\"bodyText\"><xsl:value-of select=\"description\"/></p>");
        //$NON-NLS-1$
        writer.println("	<table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"2\">");
        //$NON-NLS-1$
        writer.println("	<xsl:for-each select=\"category-def\">");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		<xsl:sort select=\"@label\" order=\"ascending\" case-order=\"upper-first\"/>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		<xsl:sort select=\"@name\" order=\"ascending\" case-order=\"upper-first\"/>");
        //$NON-NLS-1$
        writer.println("	<xsl:if test=\"count(key('cat',@name)) != 0\">");
        writer.println("			<tr class=\"header\">");
        writer.println("				<td class=\"sub-header\" width=\"30%\">");
        writer.println("					<xsl:value-of select=\"@name\"/>");
        writer.println("				</td>");
        writer.println("				<td class=\"sub-header\" width=\"70%\">");
        writer.println("					<xsl:value-of select=\"@label\"/>");
        writer.println("				</td>");
        writer.println("			</tr>");
        writer.println("			<xsl:for-each select=\"key('cat',@name)\">");
        writer.println("			<xsl:sort select=\"ancestor::feature//@version\" order=\"ascending\"/>");
        writer.println("			<xsl:sort select=\"ancestor::feature//@id\" order=\"ascending\" case-order=\"upper-first\"/>");
        writer.println("			<tr>");
        writer.println("				<xsl:choose>");
        writer.println("				<xsl:when test=\"(position() mod 2 = 1)\">");
        writer.println("					<xsl:attribute name=\"class\">dark-row</xsl:attribute>");
        writer.println("				</xsl:when>");
        writer.println("				<xsl:otherwise>");
        writer.println("					<xsl:attribute name=\"class\">light-row</xsl:attribute>");
        writer.println("				</xsl:otherwise>");
        writer.println("				</xsl:choose>");
        writer.println("				<td class=\"log-text\" id=\"indent\">");
        writer.println("						<xsl:choose>");
        writer.println("						<xsl:when test=\"ancestor::feature//@label\">");
        writer.println("							<a href=\"{ancestor::feature//@url}\"><xsl:value-of select=\"ancestor::feature//@label\"/></a>");
        writer.println("							<br/>");
        writer.println("							<div id=\"indent\">");
        writer.println("							(<xsl:value-of select=\"ancestor::feature//@id\"/> - <xsl:value-of select=\"ancestor::feature//@version\"/>)");
        writer.println("							</div>");
        writer.println("						</xsl:when>");
        writer.println("						<xsl:otherwise>");
        writer.println("						<a href=\"{ancestor::feature//@url}\"><xsl:value-of select=\"ancestor::feature//@id\"/> - <xsl:value-of select=\"ancestor::feature//@version\"/></a>");
        writer.println("						</xsl:otherwise>");
        writer.println("						</xsl:choose>");
        writer.println("						<br />");
        writer.println("				</td>");
        writer.println("				<td>");
        writer.println("					<table>");
        writer.println("						<xsl:if test=\"ancestor::feature//@os\">");
        writer.println("							<tr><td class=\"log-text\" id=\"indent\">Operating Systems:</td>");
        writer.println("							<td class=\"log-text\" id=\"indent\"><xsl:value-of select=\"ancestor::feature//@os\"/></td>");
        writer.println("							</tr>");
        writer.println("						</xsl:if>");
        writer.println("						<xsl:if test=\"ancestor::feature//@ws\">");
        writer.println("							<tr><td class=\"log-text\" id=\"indent\">Windows Systems:</td>");
        writer.println("							<td class=\"log-text\" id=\"indent\"><xsl:value-of select=\"ancestor::feature//@ws\"/></td>");
        writer.println("							</tr>");
        writer.println("						</xsl:if>");
        writer.println("						<xsl:if test=\"ancestor::feature//@nl\">");
        writer.println("							<tr><td class=\"log-text\" id=\"indent\">Languages:</td>");
        writer.println("							<td class=\"log-text\" id=\"indent\"><xsl:value-of select=\"ancestor::feature//@nl\"/></td>");
        writer.println("							</tr>");
        writer.println("						</xsl:if>");
        writer.println("						<xsl:if test=\"ancestor::feature//@arch\">");
        writer.println("							<tr><td class=\"log-text\" id=\"indent\">Architecture:</td>");
        writer.println("							<td class=\"log-text\" id=\"indent\"><xsl:value-of select=\"ancestor::feature//@arch\"/></td>");
        writer.println("							</tr>");
        writer.println("						</xsl:if>");
        writer.println("					</table>");
        writer.println("				</td>");
        writer.println("			</tr>");
        writer.println("			</xsl:for-each>");
        writer.println("			<tr><td class=\"spacer\"><br/></td><td class=\"spacer\"><br/></td></tr>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		</xsl:if>");
        //$NON-NLS-1$
        writer.println("	</xsl:for-each>");
        //$NON-NLS-1$
        writer.println("	<xsl:if test=\"count(feature)  &gt; count(feature/category)\">");
        //$NON-NLS-1$
        writer.println("	<tr class=\"header\">");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		<td class=\"sub-header\" colspan=\"2\">");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		Uncategorized");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		</td>");
        //$NON-NLS-1$
        writer.println("	</tr>");
        //$NON-NLS-1$
        writer.println("	</xsl:if>");
        //$NON-NLS-1$
        writer.println("	<xsl:choose>");
        //$NON-NLS-1$
        writer.println("	<xsl:when test=\"function-available('msxsl:node-set')\">");
        //$NON-NLS-1$
        writer.println("	   <xsl:variable name=\"rtf-nodes\">");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		<xsl:for-each select=\"feature[not(category)]\">");
        writer.println("			<xsl:sort select=\"@id\" order=\"ascending\" case-order=\"upper-first\"/>");
        writer.println("			<xsl:sort select=\"@version\" order=\"ascending\" />");
        writer.println("			<xsl:value-of select=\".\"/>");
        writer.println("			<xsl:copy-of select=\".\" />");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		</xsl:for-each>");
        //$NON-NLS-1$
        writer.println("	   </xsl:variable>");
        //$NON-NLS-1$
        writer.println("	   <xsl:variable name=\"myNodeSet\" select=\"msxsl:node-set($rtf-nodes)/*\"/>");
        //$NON-NLS-1$
        writer.println("	<xsl:for-each select=\"$myNodeSet\">");
        //$NON-NLS-1$
        writer.println("	<tr>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		<xsl:choose>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		<xsl:when test=\"position() mod 2 = 1\">");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		<xsl:attribute name=\"class\">dark-row</xsl:attribute>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		</xsl:when>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		<xsl:otherwise>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		<xsl:attribute name=\"class\">light-row</xsl:attribute>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		</xsl:otherwise>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		</xsl:choose>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		<td class=\"log-text\" id=\"indent\">");
        writer.println("			<xsl:choose>");
        writer.println("			<xsl:when test=\"@label\">");
        writer.println("				<a href=\"{@url}\"><xsl:value-of select=\"@label\"/></a>");
        writer.println("				<br />");
        writer.println("				<div id=\"indent\">");
        writer.println("				(<xsl:value-of select=\"@id\"/> - <xsl:value-of select=\"@version\"/>)");
        writer.println("				</div>");
        writer.println("			</xsl:when>");
        writer.println("			<xsl:otherwise>");
        writer.println("				<a href=\"{@url}\"><xsl:value-of select=\"@id\"/> - <xsl:value-of select=\"@version\"/></a>");
        writer.println("			</xsl:otherwise>");
        writer.println("			</xsl:choose>");
        writer.println("			<br /><br />");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		</td>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		<td>");
        writer.println("			<table>");
        writer.println("				<xsl:if test=\"@os\">");
        writer.println("					<tr><td class=\"log-text\" id=\"indent\">Operating Systems:</td>");
        writer.println("					<td class=\"log-text\" id=\"indent\"><xsl:value-of select=\"@os\"/></td>");
        writer.println("					</tr>");
        writer.println("				</xsl:if>");
        writer.println("				<xsl:if test=\"@ws\">");
        writer.println("					<tr><td class=\"log-text\" id=\"indent\">Windows Systems:</td>");
        writer.println("					<td class=\"log-text\" id=\"indent\"><xsl:value-of select=\"@ws\"/></td>");
        writer.println("					</tr>");
        writer.println("				</xsl:if>");
        writer.println("				<xsl:if test=\"@nl\">");
        writer.println("					<tr><td class=\"log-text\" id=\"indent\">Languages:</td>");
        writer.println("					<td class=\"log-text\" id=\"indent\"><xsl:value-of select=\"@nl\"/></td>");
        writer.println("					</tr>");
        writer.println("				</xsl:if>");
        writer.println("				<xsl:if test=\"@arch\">");
        writer.println("					<tr><td class=\"log-text\" id=\"indent\">Architecture:</td>");
        writer.println("					<td class=\"log-text\" id=\"indent\"><xsl:value-of select=\"@arch\"/></td>");
        writer.println("					</tr>");
        writer.println("				</xsl:if>");
        writer.println("			</table>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		</td>");
        //$NON-NLS-1$
        writer.println("	</tr>");
        //$NON-NLS-1$
        writer.println("	</xsl:for-each>");
        //$NON-NLS-1$
        writer.println("	</xsl:when>");
        //$NON-NLS-1$
        writer.println("	<xsl:otherwise>");
        //$NON-NLS-1$
        writer.println("	<xsl:for-each select=\"feature[not(category)]\">");
        //$NON-NLS-1$
        writer.println("	<xsl:sort select=\"@id\" order=\"ascending\" case-order=\"upper-first\"/>");
        //$NON-NLS-1$
        writer.println("	<xsl:sort select=\"@version\" order=\"ascending\" />");
        //$NON-NLS-1$
        writer.println("	<tr>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		<xsl:choose>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		<xsl:when test=\"count(preceding-sibling::feature[not(category)]) mod 2 = 1\">");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		<xsl:attribute name=\"class\">dark-row</xsl:attribute>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		</xsl:when>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		<xsl:otherwise>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		<xsl:attribute name=\"class\">light-row</xsl:attribute>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		</xsl:otherwise>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		</xsl:choose>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		<td class=\"log-text\" id=\"indent\">");
        writer.println("			<xsl:choose>");
        writer.println("			<xsl:when test=\"@label\">");
        writer.println("				<a href=\"{@url}\"><xsl:value-of select=\"@label\"/></a>");
        writer.println("				<br />");
        writer.println("				<div id=\"indent\">");
        writer.println("				(<xsl:value-of select=\"@id\"/> - <xsl:value-of select=\"@version\"/>)");
        writer.println("				</div>");
        writer.println("			</xsl:when>");
        writer.println("			<xsl:otherwise>");
        writer.println("				<a href=\"{@url}\"><xsl:value-of select=\"@id\"/> - <xsl:value-of select=\"@version\"/></a>");
        writer.println("			</xsl:otherwise>");
        writer.println("			</xsl:choose>");
        writer.println("			<br /><br />");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		</td>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		<td>");
        writer.println("			<table>");
        writer.println("				<xsl:if test=\"@os\">");
        writer.println("					<tr><td class=\"log-text\" id=\"indent\">Operating Systems:</td>");
        writer.println("					<td class=\"log-text\" id=\"indent\"><xsl:value-of select=\"@os\"/></td>");
        writer.println("					</tr>");
        writer.println("				</xsl:if>");
        writer.println("				<xsl:if test=\"@ws\">");
        writer.println("					<tr><td class=\"log-text\" id=\"indent\">Windows Systems:</td>");
        writer.println("					<td class=\"log-text\" id=\"indent\"><xsl:value-of select=\"@ws\"/></td>");
        writer.println("					</tr>");
        writer.println("				</xsl:if>");
        writer.println("				<xsl:if test=\"@nl\">");
        writer.println("					<tr><td class=\"log-text\" id=\"indent\">Languages:</td>");
        writer.println("					<td class=\"log-text\" id=\"indent\"><xsl:value-of select=\"@nl\"/></td>");
        writer.println("					</tr>");
        writer.println("				</xsl:if>");
        writer.println("				<xsl:if test=\"@arch\">");
        writer.println("					<tr><td class=\"log-text\" id=\"indent\">Architecture:</td>");
        writer.println("					<td class=\"log-text\" id=\"indent\"><xsl:value-of select=\"@arch\"/></td>");
        writer.println("					</tr>");
        writer.println("				</xsl:if>");
        writer.println("			</table>");
        //$NON-NLS-1$
        writer.println(//$NON-NLS-1$
        "		</td>");
        //$NON-NLS-1$
        writer.println("	</tr>");
        //$NON-NLS-1$
        writer.println("	</xsl:for-each>");
        //$NON-NLS-1$
        writer.println("	</xsl:otherwise>");
        //$NON-NLS-1$
        writer.println("	</xsl:choose>");
        //$NON-NLS-1$
        writer.println("	</table>");
        //$NON-NLS-1$
        writer.println("	</body>");
        //$NON-NLS-1$
        writer.println("	</html>");
        //$NON-NLS-1$
        writer.println("</xsl:for-each>");
        //$NON-NLS-1$
        writer.println("</xsl:template>");
        //$NON-NLS-1$
        writer.println("</xsl:stylesheet>");
        writer.flush();
        //$NON-NLS-1$
        writeFile(fProject.getFile(fWebLocation + "/site.xsl"), swriter);
    }

    private void writeFile(IFile file, StringWriter swriter) {
        try {
            //$NON-NLS-1$
            ByteArrayInputStream stream = new ByteArrayInputStream(swriter.toString().getBytes("UTF8"));
            if (file.exists()) {
                file.setContents(stream, false, false, null);
            } else {
                file.create(stream, false, null);
            }
            stream.close();
            swriter.close();
        } catch (Exception e) {
            PDEPlugin.logException(e);
        }
    }
}
