package org.eclipse.ecf.tests.osgi.services.remoteserviceadmin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescriptionWriter;
import org.osgi.service.remoteserviceadmin.EndpointDescription;

public class EDEFBundleGenerator {

    //$NON-NLS-1$
    private static final String MANIFEST_VERSION = "Manifest-Version";

    //$NON-NLS-1$
    private static final String BUNDLE_MANIFEST_VERSION = "Bundle-ManifestVersion";

    //$NON-NLS-1$
    private static final String BUNDLE_NAME = "Bundle-Name";

    //$NON-NLS-1$
    private static final String BUNDLE_SYMBOLIC_NAME = "Bundle-SymbolicName";

    //$NON-NLS-1$
    private static final String BUNDLE_VERSION = "Bundle-Version";

    //$NON-NLS-1$
    private static final String REMOTE_SERVICE = "Remote-Service";

    //$NON-NLS-1$
    private static final String EDEF_FILENAME = "edef.xml";

    private File targetBundleDirectory;

    private String targetBundleSymbolicName;

    private String targetBundleVersion;

    private EndpointDescriptionWriter edWriter;

    public  EDEFBundleGenerator(File targetBundleDirectory, String targetBundleSymbolicName, String targetBundleVersion, EndpointDescriptionWriter edWriter) {
        this.targetBundleDirectory = targetBundleDirectory;
        this.targetBundleSymbolicName = targetBundleSymbolicName;
        this.targetBundleVersion = targetBundleVersion;
        this.edWriter = (edWriter == null) ? new EndpointDescriptionWriter() : edWriter;
    }

    public void generateEDEFBundle(EndpointDescription[] endpointDescriptions) throws IOException {
        Manifest mf = new Manifest();
        Attributes attribs = mf.getMainAttributes();
        //$NON-NLS-1$
        attribs.putValue(MANIFEST_VERSION, "1.0");
        //$NON-NLS-1$
        attribs.putValue(BUNDLE_MANIFEST_VERSION, "2");
        //$NON-NLS-1$
        attribs.putValue(BUNDLE_NAME, "RSA EDEF - " + targetBundleSymbolicName + "." + targetBundleVersion);
        attribs.putValue(BUNDLE_SYMBOLIC_NAME, targetBundleSymbolicName);
        attribs.putValue(BUNDLE_VERSION, targetBundleVersion);
        attribs.putValue(REMOTE_SERVICE, EDEF_FILENAME);
        File targetBundleFile = new File(targetBundleDirectory, targetBundleSymbolicName + "_" + targetBundleVersion + ".jar");
        if (targetBundleFile.exists())
            throw new IOException("file " + targetBundleFile + " already exists.  Cannot overrite");
        JarOutputStream jos = null;
        try {
            jos = new JarOutputStream(new FileOutputStream(targetBundleFile), mf);
            ZipEntry edefFileEntry = new ZipEntry(EDEF_FILENAME);
            jos.putNextEntry(edefFileEntry);
            OutputStreamWriter outsWriter = new OutputStreamWriter(jos);
            outsWriter.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\n");
            edWriter.writeEndpointDescriptions(outsWriter, endpointDescriptions);
            outsWriter.flush();
            jos.closeEntry();
            jos.finish();
        } finally {
            if (jos != null)
                jos.close();
        }
    }
}
