/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.filetransfer.scp;

import com.jcraft.jsch.*;
import java.io.*;
import java.net.URL;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.filetransfer.IFileTransferPausable;
import org.eclipse.ecf.filetransfer.IncomingFileTransferException;
import org.eclipse.ecf.provider.filetransfer.retrieve.AbstractRetrieveFileTransfer;
import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class ScpRetrieveFileTransfer extends AbstractRetrieveFileTransfer implements IScpFileTransfer {

    private static final String SCP_COMMAND = System.getProperty("org.eclipse.ecf.filetransfer.scp.retrieve.scpcommand", //$NON-NLS-1$
    "scp -f ");

    private static final String SCP_EXEC = System.getProperty("org.eclipse.ecf.filetransfer.scp.retrieve.scpcommand", //$NON-NLS-1$
    "exec");

    String username;

    Channel channel;

    OutputStream responseStream;

    private ScpUtil scpUtil;

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.provider.filetransfer.retrieve.AbstractRetrieveFileTransfer
	 * #doPause()
	 */
    protected boolean doPause() {
        return false;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.provider.filetransfer.retrieve.AbstractRetrieveFileTransfer
	 * #doResume()
	 */
    protected boolean doResume() {
        return false;
    }

    public URL getTargetURL() {
        return getRemoteFileURL();
    }

    public Map getOptions() {
        return options;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.provider.filetransfer.outgoing.AbstractOutgoingFileTransfer
	 * #openStreams()
	 */
    protected void openStreams() throws IncomingFileTransferException {
        try {
            // Set input stream from local file
            final URL url = getRemoteFileURL();
            this.username = url.getUserInfo();
            scpUtil = new ScpUtil(this);
            final Session s = scpUtil.getSession();
            s.connect();
            final String command = SCP_COMMAND + scpUtil.trimTargetFile(url.getPath());
            channel = s.openChannel(SCP_EXEC);
            ((ChannelExec) channel).setCommand(command);
            channel.connect();
            final InputStream ins = channel.getInputStream();
            responseStream = channel.getOutputStream();
            scpUtil.sendZeroToStream(responseStream);
            final int c = checkAck(ins);
            if (c != 'C')
                throw new IOException(Messages.ScpRetrieveFileTransfer_EXCEPTION_SCP_PROTOCOL);
            // read '0644 '
            final byte[] buf = new byte[1024];
            ins.read(buf, 0, 5);
            setFileLength(readFileSize(ins, buf));
            readFileName(ins, buf);
            // set input stream for reading rest of file
            remoteFileContents = ins;
            scpUtil.sendZeroToStream(responseStream);
            fireReceiveStartEvent();
        } catch (final Exception e) {
            channel = null;
            username = null;
            throw new IncomingFileTransferException(NLS.bind(Messages.ScpRetrieveFileTransfer_EXCEPTION_CONNECTING, getRemoteFileURL().toString()), e);
        }
    }

    static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // -1
        if (b == 0)
            return b;
        if (b == -1)
            return b;
        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');
            if (// error
            b == 1 || b == 2) {
                throw new IOException(Messages.ScpRetrieveFileTransfer_EXCEPTION_SCP_PROTOCOL + ": " + sb.toString());
            }
        }
        return b;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.provider.filetransfer.retrieve.AbstractRetrieveFileTransfer
	 * #handleReceivedData(byte[], int, double,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
    protected void handleReceivedData(byte[] buf, int bytes, double factor, IProgressMonitor monitor) throws IOException {
        if (bytes == -1) {
            done = true;
        } else {
            int fileBytes = bytes;
            if ((bytesReceived + bytes) > fileLength) {
                fileBytes = (int) (fileLength - bytesReceived);
            }
            bytesReceived += fileBytes;
            localFileContents.write(buf, 0, fileBytes);
            fireTransferReceiveDataEvent();
            monitor.worked((int) Math.round(factor * fileBytes));
            if (fileBytes != bytes) {
                scpUtil.checkAck(buf[fileBytes], remoteFileContents);
                done = true;
            }
        }
    }

    private long readFileSize(InputStream ins, byte[] buf) throws IOException {
        long filesize = 0L;
        while (true) {
            if (ins.read(buf, 0, 1) < 0) {
                throw new IOException(Messages.ScpRetrieveFileTransfer_EXCEPTION_ERROR_READING_FILE);
            }
            if (buf[0] == ' ')
                break;
            filesize = filesize * 10L + (buf[0] - '0');
        }
        return filesize;
    }

    private String readFileName(InputStream ins, byte[] buf) throws IOException {
        String file = null;
        for (int i = 0; ; i++) {
            ins.read(buf, i, 1);
            if (buf[i] == (byte) 0x0a) {
                file = new String(buf, 0, i);
                break;
            }
        }
        return file;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.provider.filetransfer.outgoing.AbstractOutgoingFileTransfer
	 * #hardClose()
	 */
    protected void hardClose() {
        try {
            if (remoteFileContents != null && scpUtil != null) {
                scpUtil.sendZeroToStream(responseStream);
                scpUtil.dispose();
                scpUtil = null;
                remoteFileContents = null;
                responseStream = null;
            }
        } catch (final IOException e) {
            exception = e;
        } finally {
            super.hardClose();
            channel = null;
            username = null;
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.provider.filetransfer.retrieve.AbstractRetrieveFileTransfer
	 * #getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        if (adapter == null)
            return null;
        if (adapter.equals(IFileTransferPausable.class))
            return null;
        return super.getAdapter(adapter);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.provider.filetransfer.retrieve.AbstractRetrieveFileTransfer
	 * #setupProxy(org.eclipse.ecf.core.util.Proxy)
	 */
    protected void setupProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.provider.filetransfer.scp.IScpFileTransfer#getConnectContext
	 * ()
	 */
    public IConnectContext getConnectContext() {
        return connectContext;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.provider.filetransfer.scp.IScpFileTransfer#getUsername()
	 */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.provider.filetransfer.scp.IScpFileTransfer#promptPassphrase
	 * ()
	 */
    public boolean promptPassphrase() {
        // return (keyFile != null);
        return false;
    }

    public Proxy getProxy() {
        return this.proxy;
    }
}
