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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.security.*;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class ScpUtil implements UserInfo, UIKeyboardInteractive {

    public static final String SCP_SSHHOMEDIRECTORY = System.getProperty("org.eclipse.ecf.filetransfer.scp.util.sshHomeDirectory", "sshHomeDirectory");

    public static final String SCP_PUBLICKEYFILE = System.getProperty("org.eclipse.ecf.filetransfer.scp.util.keyFile", //$NON-NLS-1$
    "keyFile");

    public static final String SCP_KNOWNHOSTSFILE = System.getProperty(//$NON-NLS-1$
    "org.eclipse.ecf.filetransfer.scp.util.knownHostsFile", //$NON-NLS-1$
    "knownHostsFile");

    public static final int DEFAULT_SCP_PORT = Integer.parseInt(System.getProperty("org.eclipse.ecf.filetransfer.scp.util.scpPort", "22"));

    private IScpFileTransfer handler;

    private String password;

    private String passphrase;

    private Session session;

    private String sshHome = null;

    private String keyFile = null;

    private String knownHostsFile = null;

    public  ScpUtil(IScpFileTransfer handler) throws JSchException, IOException, UnsupportedCallbackException {
        this.handler = handler;
        final JSch jsch = new JSch();
        final URL url = handler.getTargetURL();
        int port = url.getPort();
        if (port == -1)
            port = DEFAULT_SCP_PORT;
        setupOptions(jsch);
        promptUsername();
        String username = handler.getUsername();
        if (username == null)
            throw new IOException(Messages.ScpUtil_EXCEPTION_USERNAME_NOT_NULL);
        session = jsch.getSession(handler.getUsername(), url.getHost(), port);
        setupProxy();
        session.setUserInfo(this);
    }

    Session getSession() {
        return session;
    }

    void promptUsername() throws IOException, UnsupportedCallbackException {
        final IConnectContext connectContext = handler.getConnectContext();
        if (connectContext != null) {
            final CallbackHandler callbackHandler = connectContext.getCallbackHandler();
            if (handler != null) {
                final Callback[] callbacks = new Callback[2];
                final NameCallback nc = new NameCallback(Messages.ScpOutgoingFileTransfer_USERNAME_PROMPT);
                String user = handler.getUsername();
                if (user != null)
                    nc.setName(user);
                callbacks[0] = nc;
                callbacks[1] = new PasswordCallback(Messages.ScpOutgoingFileTransfer_PASSWORD_PROMPT);
                callbackHandler.handle(callbacks);
                handler.setUsername(nc.getName());
            }
        }
    }

    String promptCredentials(boolean usePassphrase) {
        try {
            final IConnectContext connectContext = handler.getConnectContext();
            if (connectContext != null) {
                final CallbackHandler callbackHandler = connectContext.getCallbackHandler();
                if (handler != null) {
                    final Callback[] callbacks = new Callback[2];
                    final NameCallback nc = new NameCallback(Messages.ScpOutgoingFileTransfer_USERNAME_PROMPT);
                    String user = handler.getUsername();
                    if (user != null)
                        nc.setName(user);
                    callbacks[0] = nc;
                    if (usePassphrase) {
                        callbacks[1] = new PassphraseCallback(Messages.ScpOutgoingFileTransfer_PASSPHRASE_PROMPT);
                    } else
                        callbacks[1] = new PasswordCallback(Messages.ScpOutgoingFileTransfer_PASSWORD_PROMPT);
                    callbackHandler.handle(callbacks);
                    handler.setUsername(nc.getName());
                    if (usePassphrase) {
                        passphrase = ((PassphraseCallback) callbacks[1]).getPassphrase();
                    } else
                        password = ((PasswordCallback) callbacks[1]).getPassword();
                }
            }
            return (usePassphrase) ? this.passphrase : this.password;
        } catch (final Exception e) {
            return null;
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#getPassphrase()
	 */
    public String getPassphrase() {
        return promptCredentials(true);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#getPassword()
	 */
    public String getPassword() {
        return promptCredentials(false);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#promptPassphrase(java.lang.String)
	 */
    public boolean promptPassphrase(String message) {
        return (keyFile != null);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#promptPassword(java.lang.String)
	 */
    public boolean promptPassword(String message) {
        return true;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#promptYesNo(java.lang.String)
	 */
    public boolean promptYesNo(String message) {
        return true;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#showMessage(java.lang.String)
	 */
    public void showMessage(String message) {
    // do nothing
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jcraft.jsch.UIKeyboardInteractive#promptKeyboardInteractive(java.
	 * lang.String, java.lang.String, java.lang.String, java.lang.String[],
	 * boolean[])
	 */
    public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
        promptCredentials(false);
        return new String[] { password };
    }

    /**
	 */
    void setupProxy() {
        com.jcraft.jsch.Proxy jProxy = null;
        final Proxy proxy = handler.getProxy();
        if (proxy != null) {
            final String hostname = proxy.getAddress().getHostName();
            final int port = proxy.getAddress().getPort();
            if (proxy.getType().equals(Proxy.Type.HTTP)) {
                if (port == -1)
                    jProxy = new ProxyHTTP(hostname);
                else
                    jProxy = new ProxyHTTP(hostname, port);
            } else if (proxy.getType().equals(Proxy.Type.SOCKS)) {
                if (port == -1)
                    jProxy = new ProxySOCKS5(hostname);
                else
                    jProxy = new ProxySOCKS5(hostname, port);
            }
            if (jProxy != null)
                session.setProxy(jProxy);
        }
    }

    private void setupOptions(JSch jsch) {
        // Get sshHome
        sshHome = getProperty(SCP_SSHHOMEDIRECTORY, sshHome);
        if (sshHome == null) {
            //$NON-NLS-1$
            final String userHome = System.getProperty("user.home");
            if (userHome != null) {
                File dir = new //$NON-NLS-1$
                File(//$NON-NLS-1$
                userHome + File.separator + ".ssh");
                if (dir.exists())
                    sshHome = dir.getAbsolutePath();
                else
                    dir = new File(//$NON-NLS-1$
                    userHome + File.separator + //$NON-NLS-1$
                    "ssh");
                if (dir.exists())
                    sshHome = dir.getAbsolutePath();
            }
        }
        keyFile = getProperty(SCP_PUBLICKEYFILE);
        if (keyFile != null) {
            if (!(new File(keyFile).exists()))
                keyFile = null;
        } else {
            //$NON-NLS-1$
            File file = new File(sshHome + File.separator + "id_dsa");
            if (file.exists())
                keyFile = file.getAbsolutePath();
            else {
                file = new //$NON-NLS-1$
                File(//$NON-NLS-1$
                sshHome + File.separator + "id_rsa");
                if (file.exists())
                    keyFile = file.getAbsolutePath();
            }
        }
        if (keyFile != null) {
            try {
                jsch.addIdentity(keyFile);
            } catch (final JSchException e) {
                Activator.getDefault().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, Messages.ScpOutgoingFileTransfer_EXCEPTION_SETTING_SSH_IDENTITY, e));
            }
        }
        knownHostsFile = getProperty(SCP_KNOWNHOSTSFILE);
        if (knownHostsFile != null) {
            if (!(new File(knownHostsFile).exists()))
                knownHostsFile = null;
        } else {
            //$NON-NLS-1$
            final File file = new File(sshHome + File.separator + "known_hosts");
            if (!file.exists()) {
                knownHostsFile = null;
            } else {
                knownHostsFile = file.getAbsolutePath();
            }
        }
        if (knownHostsFile != null) {
            try {
                jsch.setKnownHosts(knownHostsFile);
            } catch (final JSchException e) {
                Activator.getDefault().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, Messages.ScpOutgoingFileTransfer_EXCEPTION_SETTING_KNOWN_HOSTS, e));
            }
        }
    }

    private String getProperty(String key, String def) {
        final Map options = handler.getOptions();
        if (options == null)
            return def;
        final String result = (String) options.get(key);
        if (result == null)
            return def;
        return result;
    }

    private String getProperty(String key) {
        return getProperty(key, null);
    }

    String trimTargetFile(String string) {
        return string;
    }

    void checkAck(InputStream ins) throws IOException {
        checkAck(ins.read(), ins);
    }

    void checkAck(int b, InputStream ins) throws IOException {
        if (b == 0)
            return;
        if (b == -1)
            throw new IOException(Messages.ScpUtil_EXCEPTION_UNKNOWN_SCP_ERROR);
        if (b == 1 || b == 2) {
            final StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = ins.read();
                sb.append((char) c);
            } while (c != '\n');
            if (// error
            b == 1) {
                throw new IOException(NLS.bind(Messages.ScpUtil_SCP_ERROR, sb.toString()));
            }
            if (// fatal error
            b == 2) {
                throw new IOException(NLS.bind(Messages.ScpUtil_SCP_ERROR, sb.toString()));
            }
        }
    }

    void sendZeroToStream(OutputStream outs) throws IOException {
        // send '\0'
        final byte[] buf = new byte[1];
        buf[0] = 0;
        outs.write(buf, 0, 1);
        outs.flush();
    }

    void dispose() {
        if (session != null) {
            session.disconnect();
            session = null;
        }
        handler = null;
        password = null;
        passphrase = null;
        sshHome = null;
        keyFile = null;
        knownHostsFile = null;
    }
}
