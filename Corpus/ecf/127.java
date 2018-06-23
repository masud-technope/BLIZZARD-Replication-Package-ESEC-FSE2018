/*******************************************************************************
 *  Copyright (c) 2000, 2008 IBM Corporation, 2015 Composent, Inc and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *     Composent, Inc - Changes for use in RSA discovery view
 *******************************************************************************/
package org.eclipse.ecf.remoteservices.ui;

import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.ecf.internal.remoteservices.ui.Activator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * @since 3.3
 */
public class RSAImageRegistry {

    private static ImageRegistry PLUGIN_REGISTRY;

    //$NON-NLS-1$
    public static final String ICONS_PATH = "icons/";

    //$NON-NLS-1$
    private static final String PATH_OBJ = ICONS_PATH + "obj16/";

    //$NON-NLS-1$
    private static final String PATH_OVR = ICONS_PATH + "ovr16/";

    //$NON-NLS-1$
    public static final String IMG_INTERFACE_OBJ = "int_obj.gif";

    //$NON-NLS-1$
    public static final String IMG_PROPERTY_OBJ = "property_obj.gif";

    //$NON-NLS-1$
    public static final String IMG_RSPROXY_OBJ = "rsvcproxy_obj_tbg.gif";

    //$NON-NLS-1$
    public static final String IMG_RS_OBJ = "rsvc_obj_tbg.gif";

    //$NON-NLS-1$
    public static final String IMG_ENDPOINTDESCRIPTION_OBJ = "endpoint_obj.gif";

    //$NON-NLS-1$
    public static final String IMG_RSA_OBJ = "rsa_obj.gif";

    public static final ImageDescriptor INTERFACE_OBJ = create(PATH_OBJ, IMG_INTERFACE_OBJ);

    public static final ImageDescriptor RSPROXY_OBJ = create(PATH_OBJ, IMG_RSPROXY_OBJ);

    public static final ImageDescriptor RS_OBJ = create(PATH_OBJ, IMG_RS_OBJ);

    public static final ImageDescriptor RSA_OBJ = create(PATH_OBJ, IMG_RSA_OBJ);

    public static final ImageDescriptor ENDPOINTDESCRIPTION_OBJ = create(PATH_OBJ, IMG_ENDPOINTDESCRIPTION_OBJ);

    //$NON-NLS-1$
    public static final ImageDescriptor DESC_ERROR_ST_OBJ = create(PATH_OBJ, "error_st_obj.gif");

    //$NON-NLS-1$
    public static final ImageDescriptor DESC_WARNING_ST_OBJ = create(PATH_OBJ, "warning_st_obj.gif");

    //$NON-NLS-1$
    public static final ImageDescriptor DESC_LOCATION = create(PATH_OBJ, "location_obj.gif");

    //$NON-NLS-1$
    public static final ImageDescriptor DESC_SERVICE_OBJ = create(PATH_OBJ, "int_obj.gif");

    //$NON-NLS-1$
    public static final ImageDescriptor DESC_ASYNC_SERVICE_OBJ = create(PATH_OBJ, "async_int_obj.gif");

    //$NON-NLS-1$
    public static final ImageDescriptor DESC_PROPERTY_OBJ = create(PATH_OBJ, "property_obj.gif");

    //$NON-NLS-1$
    public static final ImageDescriptor DESC_PACKAGE_OBJ = create(PATH_OBJ, "package_obj.gif");

    //$NON-NLS-1$
    public static final ImageDescriptor DESC_RSPROXY_CO = create(PATH_OVR, "rsvcproxy_co_alpha.gif");

    //$NON-NLS-1$
    public static final ImageDescriptor DESC_HOST_OBJ = create(PATH_OBJ, "IHost.gif");

    //$NON-NLS-1$
    public static final ImageDescriptor DESC_SERVICEID_OBJ = create(PATH_OBJ, "IServiceID.gif");

    private static final void initialize() {
        PLUGIN_REGISTRY = Activator.getDefault().getImageRegistry();
        manage(IMG_INTERFACE_OBJ, INTERFACE_OBJ);
        manage(IMG_PROPERTY_OBJ, DESC_PROPERTY_OBJ);
        manage(IMG_RSPROXY_OBJ, RSPROXY_OBJ);
    }

    private static ImageDescriptor create(String prefix, String name) {
        return ImageDescriptor.createFromURL(makeIconURL(prefix, name));
    }

    public static Image get(String key) {
        if (PLUGIN_REGISTRY == null)
            initialize();
        return PLUGIN_REGISTRY.get(key);
    }

    private static URL makeIconURL(String prefix, String name) {
        //$NON-NLS-1$
        String path = "$nl$/" + prefix + name;
        return FileLocator.find(Activator.getDefault().getBundle(), new Path(path), null);
    }

    public static Image manage(String key, ImageDescriptor desc) {
        Image image = desc.createImage();
        PLUGIN_REGISTRY.put(key, image);
        return image;
    }
}
