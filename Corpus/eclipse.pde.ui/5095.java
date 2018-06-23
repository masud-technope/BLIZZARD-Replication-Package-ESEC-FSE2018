/*******************************************************************************
 * Copyright (c) 2005, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     EclipseSource Corporation - ongoing enhancements
 *******************************************************************************/
package org.eclipse.pde.internal.ui.wizards.product;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.osgi.util.NLS;
import org.eclipse.pde.core.IBaseModel;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.core.TargetPlatformHelper;
import org.eclipse.pde.internal.core.iproduct.IProduct;
import org.eclipse.pde.internal.core.plugin.WorkspacePluginModelBase;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.pde.internal.ui.util.ModelModification;
import org.eclipse.pde.internal.ui.util.PDEModelUtility;
import org.eclipse.pde.internal.ui.wizards.templates.ControlStack;
import org.eclipse.pde.ui.templates.IVariableProvider;
import org.eclipse.swt.widgets.Shell;

public class ProductIntroOperation extends BaseManifestOperation implements IVariableProvider {

    protected String fIntroId;

    private Shell fShell;

    private IProduct fProduct;

    private IProject fProject;

    //$NON-NLS-1$
    private static final String INTRO_POINT = "org.eclipse.ui.intro";

    //$NON-NLS-1$
    private static final String INTRO_CONFIG_POINT = "org.eclipse.ui.intro.config";

    //$NON-NLS-1$
    private static final String INTRO_CLASS = "org.eclipse.ui.intro.config.CustomizableIntroPart";

    //$NON-NLS-1$
    private static final String KEY_PRODUCT_NAME = "productName";

    public  ProductIntroOperation(IProduct product, String pluginId, String introId, Shell shell) {
        super(shell, pluginId);
        fIntroId = introId;
        fProduct = product;
        fProject = PluginRegistry.findModel(pluginId).getUnderlyingResource().getProject();
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
            IFile file = getFile();
            if (!file.exists()) {
                createNewFile(file);
            } else {
                modifyExistingFile(file, monitor);
            }
            updateSingleton(monitor);
            generateFiles(monitor);
        } catch (CoreException e) {
            throw new InvocationTargetException(e);
        }
    }

    private void createNewFile(IFile file) throws CoreException {
        WorkspacePluginModelBase model = (WorkspacePluginModelBase) getModel(file);
        IPluginBase base = model.getPluginBase();
        base.setSchemaVersion(TargetPlatformHelper.getSchemaVersion());
        base.add(createIntroExtension(model));
        base.add(createIntroConfigExtension(model));
        model.save();
    }

    private IPluginExtension createIntroExtension(IPluginModelBase model) throws CoreException {
        IPluginExtension extension = model.getFactory().createExtension();
        extension.setPoint(INTRO_POINT);
        extension.add(createIntroExtensionContent(extension));
        extension.add(createIntroBindingExtensionContent(extension));
        return extension;
    }

    private IPluginExtension createIntroConfigExtension(IPluginModelBase model) throws CoreException {
        IPluginExtension extension = model.getFactory().createExtension();
        extension.setPoint(INTRO_CONFIG_POINT);
        extension.add(createIntroConfigExtensionContent(extension));
        return extension;
    }

    private IPluginElement createIntroExtensionContent(IPluginExtension extension) throws CoreException {
        IPluginElement element = extension.getModel().getFactory().createElement(extension);
        //$NON-NLS-1$
        element.setName("intro");
        //$NON-NLS-1$
        element.setAttribute("id", fIntroId);
        //$NON-NLS-1$
        element.setAttribute("class", INTRO_CLASS);
        return element;
    }

    private IPluginElement createIntroBindingExtensionContent(IPluginExtension extension) throws CoreException {
        IPluginElement element = extension.getModel().getFactory().createElement(extension);
        //$NON-NLS-1$
        element.setName("introProductBinding");
        //$NON-NLS-1$
        element.setAttribute("productId", fProduct.getProductId());
        //$NON-NLS-1$
        element.setAttribute("introId", fIntroId);
        return element;
    }

    private IPluginElement createIntroConfigExtensionContent(IPluginExtension extension) throws CoreException {
        IPluginElement element = extension.getModel().getFactory().createElement(extension);
        //$NON-NLS-1$
        element.setName("config");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("id", fPluginId + ".introConfigId");
        //$NON-NLS-1$
        element.setAttribute("introId", fIntroId);
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("content", "introContent.xml");
        element.add(createPresentationElement(element));
        return element;
    }

    private IPluginElement createPresentationElement(IPluginElement parent) throws CoreException {
        IPluginElement presentation = null;
        IPluginElement implementation = null;
        IExtensionsModelFactory factory = parent.getModel().getFactory();
        presentation = factory.createElement(parent);
        //$NON-NLS-1$
        presentation.setName("presentation");
        //$NON-NLS-1$ //$NON-NLS-2$
        presentation.setAttribute("home-page-id", "root");
        implementation = factory.createElement(presentation);
        //$NON-NLS-1$
        implementation.setName("implementation");
        //$NON-NLS-1$ //$NON-NLS-2$
        implementation.setAttribute("kind", "html");
        //$NON-NLS-1$ //$NON-NLS-2$
        implementation.setAttribute("style", "content/shared.css");
        //$NON-NLS-1$ //$NON-NLS-2$
        implementation.setAttribute("os", "win32,linux,macosx");
        presentation.add(implementation);
        return presentation;
    }

    private void modifyExistingFile(IFile file, IProgressMonitor monitor) throws CoreException {
        IStatus status = PDEPlugin.getWorkspace().validateEdit(new IFile[] { file }, fShell);
        if (status.getSeverity() != IStatus.OK)
            //$NON-NLS-1$
            throw new CoreException(new Status(IStatus.ERROR, "org.eclipse.pde.ui", IStatus.ERROR, NLS.bind(PDEUIMessages.ProductDefinitionOperation_readOnly, fPluginId), null));
        ModelModification mod = new ModelModification(file) {

            @Override
            protected void modifyModel(IBaseModel model, IProgressMonitor monitor) throws CoreException {
                if (!(model instanceof IPluginModelBase))
                    return;
                IPluginModelBase pluginModel = (IPluginModelBase) model;
                IPluginExtension extension = getExtension(pluginModel, INTRO_POINT);
                if (extension == null) {
                    extension = createIntroExtension(pluginModel);
                    pluginModel.getPluginBase().add(extension);
                } else {
                    extension.add(createIntroExtensionContent(extension));
                    extension.add(createIntroBindingExtensionContent(extension));
                }
                extension = getExtension(pluginModel, INTRO_CONFIG_POINT);
                if (extension == null) {
                    extension = createIntroConfigExtension(pluginModel);
                    pluginModel.getPluginBase().add(extension);
                } else {
                    extension.add(createIntroConfigExtensionContent(extension));
                }
            }
        };
        PDEModelUtility.modifyModel(mod, monitor);
    }

    private IPluginExtension getExtension(IPluginModelBase model, String tPoint) {
        IPluginExtension[] extensions = model.getPluginBase().getExtensions();
        for (int i = 0; i < extensions.length; i++) {
            String point = extensions[i].getPoint();
            if (tPoint.equals(point)) {
                return extensions[i];
            }
        }
        return null;
    }

    protected void generateFiles(IProgressMonitor monitor) throws CoreException {
        monitor.setTaskName(PDEUIMessages.AbstractTemplateSection_generating);
        URL locationUrl = null;
        try {
            //$NON-NLS-1$
            locationUrl = new URL(PDEPlugin.getDefault().getInstallURL(), "templates_3.1/intro/");
        } catch (MalformedURLException e1) {
            return;
        }
        try {
            locationUrl = FileLocator.resolve(locationUrl);
            locationUrl = FileLocator.toFileURL(locationUrl);
        } catch (IOException e) {
            return;
        }
        if (//$NON-NLS-1$
        "file".equals(locationUrl.getProtocol())) {
            File templateDirectory = new File(locationUrl.getFile());
            if (!templateDirectory.exists())
                return;
            generateFiles(templateDirectory, fProject, true, false, monitor);
        }
        //$NON-NLS-1$
        monitor.subTask("");
        monitor.worked(1);
    }

    private void generateFiles(File src, IContainer dst, boolean firstLevel, boolean binary, IProgressMonitor monitor) throws CoreException {
        File[] members = src.listFiles();
        for (int i = 0; i < members.length; i++) {
            File member = members[i];
            if (//$NON-NLS-1$
            member.getName().equals("ext.xml") || //$NON-NLS-1$
            member.getName().equals(//$NON-NLS-1$
            "java") || member.getName().equals("concept3.xhtml") || member.getName().equals("extContent.xhtml"))
                continue;
            else if (member.isDirectory()) {
                IContainer dstContainer = null;
                if (firstLevel) {
                    binary = false;
                    if (//$NON-NLS-1$
                    member.getName().equals(//$NON-NLS-1$
                    "bin")) {
                        binary = true;
                        dstContainer = dst;
                    }
                }
                if (dstContainer == null) {
                    dstContainer = dst.getFolder(new Path(member.getName()));
                }
                if (dstContainer instanceof IFolder && !dstContainer.exists())
                    ((IFolder) dstContainer).create(true, true, monitor);
                generateFiles(member, dstContainer, false, binary, monitor);
            } else {
                if (firstLevel)
                    binary = false;
                InputStream in = null;
                try {
                    in = new FileInputStream(member);
                    copyFile(member.getName(), in, dst, binary, monitor);
                } catch (IOException ioe) {
                } finally {
                    if (in != null)
                        try {
                            in.close();
                        } catch (IOException ioe2) {
                        }
                }
            }
        }
    }

    private void copyFile(String fileName, InputStream input, IContainer dst, boolean binary, IProgressMonitor monitor) throws CoreException {
        monitor.subTask(fileName);
        IFile dstFile = dst.getFile(new Path(fileName));
        try {
            InputStream stream = getProcessedStream(fileName, input, binary);
            if (dstFile.exists()) {
                dstFile.setContents(stream, true, true, monitor);
            } else {
                dstFile.create(stream, true, monitor);
            }
            stream.close();
        } catch (IOException e) {
        }
    }

    private InputStream getProcessedStream(String fileName, InputStream stream, boolean binary) throws IOException, CoreException {
        if (binary)
            return stream;
        InputStreamReader reader = new InputStreamReader(stream);
        int bufsize = 1024;
        char[] cbuffer = new char[bufsize];
        int read = 0;
        StringBuffer keyBuffer = new StringBuffer();
        StringBuffer outBuffer = new StringBuffer();
        ControlStack preStack = new ControlStack();
        preStack.setValueProvider(this);
        boolean replacementMode = false;
        while (read != -1) {
            read = reader.read(cbuffer);
            for (int i = 0; i < read; i++) {
                char c = cbuffer[i];
                if (preStack.getCurrentState() == false) {
                    continue;
                }
                if (c == '$') {
                    if (replacementMode) {
                        replacementMode = false;
                        String key = keyBuffer.toString();
                        String value = //$NON-NLS-1$
                        key.length() == //$NON-NLS-1$
                        0 ? "$" : getReplacementString(fileName, key);
                        outBuffer.append(value);
                        keyBuffer.delete(0, keyBuffer.length());
                    } else {
                        replacementMode = true;
                    }
                } else {
                    if (replacementMode)
                        keyBuffer.append(c);
                    else {
                        outBuffer.append(c);
                    }
                }
            }
        }
        return new ByteArrayInputStream(outBuffer.toString().getBytes(fProject.getDefaultCharset()));
    }

    private String getReplacementString(String fileName, String key) {
        if (key.equals(KEY_PRODUCT_NAME)) {
            return fProduct.getName();
        }
        return key;
    }

    @Override
    public Object getValue(String variable) {
        return null;
    }
}
