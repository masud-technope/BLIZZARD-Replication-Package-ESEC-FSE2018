/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *               Cloudsmith, Inc. - additional API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.filetransfer.scp;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import java.io.*;
import java.net.URL;
import java.util.Map;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.core.util.StringUtils;
import org.eclipse.ecf.filetransfer.SendFileTransferException;
import org.eclipse.ecf.provider.filetransfer.outgoing.AbstractOutgoingFileTransfer;
import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class ScpOutgoingFileTransfer extends AbstractOutgoingFileTransfer implements IScpFileTransfer {

    private static final String SCP_COMMAND = System.getProperty("org.eclipse.ecf.filetransfer.scp.outgoing.scpcommand", //$NON-NLS-1$; //$NON-NLS-1$
    "scp -p -t ");

    private static final String SCP_EXEC = System.getProperty("org.eclipse.ecf.filetransfer.scp.outgoing.scpcommand", //$NON-NLS-1$
    "exec");

    String username;

    private ChannelExec channel;

    private InputStream responseStream;

    private ScpUtil scpUtil;

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.provider.filetransfer.outgoing.AbstractOutgoingFileTransfer
	 * #openStreams()
	 */
    protected void openStreams() throws SendFileTransferException {
        try {
            final File localFile = getFileTransferInfo().getFile();
            // Set input stream from local file
            setInputStream(new BufferedInputStream(new FileInputStream(localFile)));
            final URL url = getRemoteFileURL();
            this.username = url.getUserInfo();
            scpUtil = new ScpUtil(this);
            final Session s = scpUtil.getSession();
            s.connect();
            final String targetFileName = scpUtil.trimTargetFile(url.getPath());
            final String command = SCP_COMMAND + targetFileName;
            channel = (ChannelExec) s.openChannel(SCP_EXEC);
            channel.setCommand(command);
            final OutputStream outs = channel.getOutputStream();
            responseStream = channel.getInputStream();
            channel.connect();
            scpUtil.checkAck(responseStream);
            sendFileNameAndSize(localFile, targetFileName, outs, responseStream);
            setOutputStream(outs);
        } catch (final Exception e) {
            throw new SendFileTransferException(NLS.bind(Messages.ScpOutgoingFileTransfer_EXCEPTION_CONNECTING, getRemoteFileURL().toString()), e);
        }
    }

    public Map getOptions() {
        return options;
    }

    public URL getTargetURL() {
        return getRemoteFileURL();
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    private void sendFileNameAndSize(File localFile, String fileName, OutputStream outs, InputStream ins) throws IOException {
        // send "C0644 filesize filename", where filename should not include '/'
        final long filesize = localFile.length();
        String[] targetFile = StringUtils.split(fileName, '/');
        //$NON-NLS-1$
        final StringBuffer command = new StringBuffer("C0644 ");
        command.append(filesize).append(" ").append(targetFile[targetFile.length - 1]).append(//$NON-NLS-1$ //$NON-NLS-2$
        "\n");
        outs.write(command.toString().getBytes());
        outs.flush();
        scpUtil.checkAck(ins);
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
            if (scpUtil != null) {
                scpUtil.sendZeroToStream(remoteFileContents);
                scpUtil.checkAck(responseStream);
            }
            if (channel != null) {
                channel.disconnect();
                channel = null;
            }
            if (scpUtil != null) {
                scpUtil.dispose();
                scpUtil = null;
            }
        } catch (final IOException e) {
            exception = e;
        }
        username = null;
        super.hardClose();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.provider.filetransfer.outgoing.AbstractOutgoingFileTransfer
	 * #setupProxy(org.eclipse.ecf.core.util.Proxy)
	 */
    protected void setupProxy(Proxy proxy) {
        this.proxy = proxy;
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

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.internal.provider.filetransfer.scp.IScpFileTransfer#
	 * getConnectContext()
	 */
    public IConnectContext getConnectContext() {
        return connectContext;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.internal.provider.filetransfer.scp.IScpFileTransfer#
	 * setUsername(java.lang.String)
	 */
    public void setUsername(String username) {
        this.username = username;
    }
}
