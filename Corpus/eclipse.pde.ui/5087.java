/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.HostSpecification;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.internal.core.util.CoreUtility;

public class JavadocLocationManager {

    //$NON-NLS-1$
    public static final String JAVADOC_ID = "org.eclipse.pde.core.javadoc";

    private HashMap<String, Set<String>> fLocations;

    public String getJavadocLocation(IPluginModelBase model) {
        try {
            File file = new File(model.getInstallLocation());
            if (file.isDirectory()) {
                File doc = new //$NON-NLS-1$
                File(//$NON-NLS-1$
                file, //$NON-NLS-1$
                "doc");
                if (//$NON-NLS-1$
                new File(doc, "package-list").exists())
                    return doc.toURL().toString();
            } else if (//$NON-NLS-1$
            CoreUtility.jarContainsResource(file, "doc/package-list", false)) {
                //$NON-NLS-1$ //$NON-NLS-2$
                return "jar:" + file.toURL().toString() + "!/doc";
            }
            return getEntry(model);
        } catch (MalformedURLException e) {
            PDECore.log(e);
            return null;
        }
    }

    private synchronized String getEntry(IPluginModelBase model) {
        initialize();
        BundleDescription desc = model.getBundleDescription();
        if (desc != null) {
            HostSpecification host = desc.getHost();
            String id = host == null ? desc.getSymbolicName() : host.getName();
            if (id != null) {
                Iterator<String> iter = fLocations.keySet().iterator();
                while (iter.hasNext()) {
                    String location = iter.next().toString();
                    Set<String> set = fLocations.get(location);
                    if (set.contains(id))
                        return location;
                }
            }
        }
        return null;
    }

    private synchronized void initialize() {
        if (fLocations != null)
            return;
        fLocations = new HashMap();
        IExtension[] extensions = PDECore.getDefault().getExtensionsRegistry().findExtensions(JAVADOC_ID, false);
        for (int i = 0; i < extensions.length; i++) {
            IPluginModelBase base = PluginRegistry.findModel(extensions[i].getContributor().getName());
            // only search external models
            if (base == null || base.getUnderlyingResource() != null)
                continue;
            processExtension(extensions[i], base);
        }
    }

    private void processExtension(IExtension extension, IPluginModelBase base) {
        IConfigurationElement[] children = extension.getConfigurationElements();
        for (int i = 0; i < children.length; i++) {
            if (//$NON-NLS-1$
            children[i].getName().equals("javadoc")) {
                String path = //$NON-NLS-1$
                children[i].getAttribute(//$NON-NLS-1$
                "path");
                if (path == null)
                    continue;
                try {
                    new URL(path);
                    processPlugins(path, children[i].getChildren());
                } catch (MalformedURLException e) {
                    String attr = children[i].getAttribute("archive");
                    boolean archive = attr == null ? false : "true".equals(attr);
                    IPath modelPath = new Path(base.getInstallLocation());
                    StringBuffer buffer = new StringBuffer();
                    File file = modelPath.toFile();
                    if (file.exists()) {
                        try {
                            buffer.append(file.toURI().toURL());
                        } catch (MalformedURLException e1) {
                            buffer.append("file:/");
                            buffer.append(modelPath.toPortableString());
                        }
                        if (file.isFile()) {
                            buffer.append("!/");
                            archive = true;
                        }
                    }
                    buffer.append(path);
                    if (archive) {
                        buffer.insert(0, "jar:");
                        if (buffer.indexOf("!") == -1) {
                            buffer.append("!/");
                        }
                    }
                    processPlugins(buffer.toString(), children[i].getChildren());
                }
            }
        }
    }

    private void processPlugins(String path, IConfigurationElement[] plugins) {
        for (int i = 0; i < plugins.length; i++) {
            if (//$NON-NLS-1$
            plugins[i].getName().equals("plugin")) {
                String id = //$NON-NLS-1$
                plugins[i].getAttribute(//$NON-NLS-1$
                "id");
                if (id == null)
                    continue;
                Set<String> set = fLocations.get(path);
                if (set == null) {
                    set = new HashSet();
                    fLocations.put(path, set);
                }
                set.add(id);
            }
        }
    }

    public synchronized void reset() {
        fLocations = null;
    }
}
