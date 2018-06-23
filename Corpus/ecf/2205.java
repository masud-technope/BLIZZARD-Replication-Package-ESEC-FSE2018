package org.eclipse.ecf.osgi.services.remoteserviceadmin;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.internal.osgi.services.remoteserviceadmin.DebugOptions;
import org.eclipse.ecf.internal.osgi.services.remoteserviceadmin.LogUtility;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ExportReference;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportReference;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

/**
 * @since 4.3
 */
public class DebugRemoteServiceAdminListener implements RemoteServiceAdminListener {

    public static final SimpleDateFormat sdf = new SimpleDateFormat(//$NON-NLS-1$
    "HH:mm:ss.SSS");

    public static final int EXPORT_MASK = RemoteServiceAdminEvent.EXPORT_ERROR | RemoteServiceAdminEvent.EXPORT_REGISTRATION | RemoteServiceAdminEvent.EXPORT_UNREGISTRATION | RemoteServiceAdminEvent.EXPORT_WARNING;

    public static final int IMPORT_MASK = RemoteServiceAdminEvent.IMPORT_ERROR | RemoteServiceAdminEvent.IMPORT_REGISTRATION | RemoteServiceAdminEvent.IMPORT_UNREGISTRATION | RemoteServiceAdminEvent.IMPORT_WARNING;

    public static final int ALL_MASK = EXPORT_MASK | IMPORT_MASK;

    protected final PrintWriter writer;

    // default is all events
    protected int eventMask = ALL_MASK;

    protected boolean writeEndpoint;

    protected EndpointDescriptionWriter edWriter;

    public  DebugRemoteServiceAdminListener(PrintWriter writer, int eventMask, boolean writeEndpoint) {
        Assert.isNotNull(writer);
        this.writer = writer;
        this.eventMask = eventMask;
        this.writeEndpoint = writeEndpoint;
        if (this.writeEndpoint)
            edWriter = new EndpointDescriptionWriter();
    }

    public  DebugRemoteServiceAdminListener(PrintWriter writer, int mask) {
        this(writer, mask, true);
    }

    public  DebugRemoteServiceAdminListener(PrintWriter writer) {
        this(writer, ALL_MASK);
    }

    public  DebugRemoteServiceAdminListener(int mask, boolean writeEndpoint) {
        this(new PrintWriter(System.out), mask, writeEndpoint);
    }

    public  DebugRemoteServiceAdminListener(int mask) {
        this(mask, true);
    }

    public  DebugRemoteServiceAdminListener() {
        this(ALL_MASK);
    }

    public int getEventMask() {
        return this.eventMask;
    }

    public void setEventMask(int eventMask) {
        this.eventMask = eventMask;
    }

    protected boolean allow(int type, int mask) {
        return (type & mask) > 0;
    }

    public void remoteAdminEvent(RemoteServiceAdminEvent event) {
        if (!(event instanceof RemoteServiceAdmin.RemoteServiceAdminEvent))
            return;
        if (allow(event.getType(), this.eventMask))
            printEvent((RemoteServiceAdmin.RemoteServiceAdminEvent) event);
    }

    protected String eventTypeToString(int type) {
        switch(type) {
            case RemoteServiceAdminEvent.EXPORT_ERROR:
                //$NON-NLS-1$
                return "EXPORT_ERROR";
            case RemoteServiceAdminEvent.EXPORT_REGISTRATION:
                //$NON-NLS-1$
                return "EXPORT_REGISTRATION";
            case RemoteServiceAdminEvent.EXPORT_UNREGISTRATION:
                //$NON-NLS-1$
                return "EXPORT_UNREGISTRATION";
            case RemoteServiceAdminEvent.EXPORT_UPDATE:
                //$NON-NLS-1$
                return "EXPORT_UPDATE";
            case RemoteServiceAdminEvent.EXPORT_WARNING:
                //$NON-NLS-1$
                return "EXPORT_WARNING";
            case RemoteServiceAdminEvent.IMPORT_ERROR:
                //$NON-NLS-1$
                return "IMPORT_ERROR";
            case RemoteServiceAdminEvent.IMPORT_REGISTRATION:
                //$NON-NLS-1$
                return "IMPORT_REGISTRATION";
            case RemoteServiceAdminEvent.IMPORT_UNREGISTRATION:
                //$NON-NLS-1$
                return "IMPORT_UNREGISTRATION";
            case RemoteServiceAdminEvent.IMPORT_UPDATE:
                //$NON-NLS-1$
                return "IMPORT_UPDATE";
            case RemoteServiceAdminEvent.IMPORT_WARNING:
                //$NON-NLS-1$
                return "IMPORT_WARNING";
            default:
                //$NON-NLS-1$
                return "UNKNOWN";
        }
    }

    protected void writeRemoteReference(StringBuffer buf, ServiceReference<?> ref, ID containerID, long remoteServiceID) {
        this.writer.println(buf.append(ref).append(";cID=").append(containerID).append(";rsId=").append(//$NON-NLS-1$ //$NON-NLS-2$
        remoteServiceID).toString());
    }

    protected void printEvent(RemoteServiceAdmin.RemoteServiceAdminEvent event) {
        ID cID = event.getContainerID();
        StringBuffer buf = //$NON-NLS-1$
        new StringBuffer(sdf.format(new Date())).append(";").append(eventTypeToString(event.getType()));
        switch(event.getType()) {
            case RemoteServiceAdminEvent.EXPORT_REGISTRATION:
            case RemoteServiceAdminEvent.EXPORT_UNREGISTRATION:
            case RemoteServiceAdminEvent.EXPORT_UPDATE:
            case RemoteServiceAdminEvent.EXPORT_WARNING:
                ExportReference exRef = (RemoteServiceAdmin.ExportReference) event.getExportReference();
                if (exRef != null) {
                    writeRemoteReference(buf.append(";exportedSR="), exRef.getExportedService(), cID, //$NON-NLS-1$
                    exRef.getRemoteServiceId());
                    if (this.writeEndpoint)
                        writeEndpoint(exRef.getEndpointDescription());
                }
                break;
            case RemoteServiceAdminEvent.IMPORT_REGISTRATION:
            case RemoteServiceAdminEvent.IMPORT_UNREGISTRATION:
            case RemoteServiceAdminEvent.IMPORT_UPDATE:
            case RemoteServiceAdminEvent.IMPORT_WARNING:
                ImportReference imRef = (RemoteServiceAdmin.ImportReference) event.getImportReference();
                if (imRef != null) {
                    writeRemoteReference(buf.append(";importedSR="), imRef.getImportedService(), cID, //$NON-NLS-1$
                    imRef.getRemoteServiceId());
                    if (this.writeEndpoint)
                        writeEndpoint(imRef.getEndpointDescription());
                }
                break;
            case RemoteServiceAdminEvent.EXPORT_ERROR:
            case RemoteServiceAdminEvent.IMPORT_ERROR:
                writer.println(buf.toString());
                Throwable t = event.getException();
                if (t != null)
                    t.printStackTrace(this.writer);
                break;
        }
        writer.flush();
    }

    protected void writeEndpoint(EndpointDescription endpointDescription) {
        try {
            if (endpointDescription != null) {
                //$NON-NLS-1$
                this.writer.println(//$NON-NLS-1$
                "--Endpoint Description---");
                this.edWriter.writeEndpointDescription(this.writer, endpointDescription);
                //$NON-NLS-1$
                this.writer.println(//$NON-NLS-1$
                "---End Endpoint Description");
            }
        } catch (Exception e) {
            LogUtility.logError("writeEndpoint", DebugOptions.EXCEPTIONS_CATCHING, this.getClass(), "Could not write endpoint description", e);
        }
    }
}
