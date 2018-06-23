/* Copyright (c) 2006-2009 Jan S. Rellermeyer
 * Systems Group,
 * Institute for Pervasive Computing, ETH Zurich.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of ETH Zurich nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.iks.r_osgi.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import ch.ethz.iks.util.StringUtils;

/**
 * <p>
 * DeliverServiceMessage is used to bring an OSGi service to a remote machine.
 * The service interface is transferred and if specified, also an abstract class
 * that can contain code that behaves like a smart proxy and moves some methods
 * to the client machine.
 * </p>
 * <p>
 * Currently, R-OSGi does not check for class dependencies so if an interface
 * method uses custom objects as parameter and it can not be assumed that the
 * client already has these custom objects, they have to be defined as class
 * injections. However, all inner classes of injections and possible inner
 * classes of the abstract class provided as smart proxy object are
 * automatically added to the class injections.
 * </p>
 * 
 * @author Jan S. Rellermeyer, ETH Zurich.
 * @since 0.1
 */
public final class DeliverServiceMessage extends RemoteOSGiMessage {

    /**
	 * the service ID.
	 */
    private String serviceID;

    /**
	 * The class name of the interface that describes the service.
	 */
    private String[] serviceInterfaceNames;

    /**
	 * Optionally, the class name of a smart proxy class.
	 */
    private String smartProxyName;

    /**
	 * the injections.
	 */
    private Map injections;

    /**
	 * the imports.
	 */
    private String imports;

    /**
	 * the exports.
	 */
    private String exports;

    /**
	 * Optional imports
	 */
    private String optionalImports = "";

    /**
	 * Create a new DeliverServiceMessage.
	 * 
	 */
    public  DeliverServiceMessage() {
        super(DELIVER_SERVICE);
    }

    /**
	 * Create a new DeliverServiceMessage from a network packet.
	 * 
	 * <pre>
	 *         0                   1                   2                   3
	 *         0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |       R-OSGi header (function = Service = 2)                  |
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |   length of &lt;ServiceID&gt;     |    &lt;ServiceID&gt; String       \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |          imports                                              \ 
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |          exports                                              \ 
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |          interface name                                       \ 
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        | length of &lt;ProxyClassName&gt;    |    &lt;ProxyClassName&gt; String    \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |    number of injection blocks   |   class inj blocks          \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>
	 * 
	 * @param input
	 *            a <code>DataInput</code> that provides the body of a R-OSGi
	 *            network packet.
	 * @throws IOException
	 *             in case of parse errors.
	 */
     DeliverServiceMessage(final ObjectInputStream input) throws IOException {
        super(DELIVER_SERVICE);
        // the serviceID
        serviceID = input.readUTF();
        // imports
        imports = input.readUTF();
        // exports
        exports = input.readUTF();
        // interface names
        serviceInterfaceNames = readStringArray(input);
        // smart proxy name, if defined.
        final String p = input.readUTF();
        //$NON-NLS-1$
        smartProxyName = "".equals(p) ? null : p;
        // process all class injections
        final short blocks = input.readShort();
        injections = new HashMap(blocks);
        for (short i = 0; i < blocks; i++) {
            injections.put(input.readUTF(), readBytes(input));
        }
        // generate option imports from injections
        final Set set = new HashSet();
        // no need to add imports twice
        final String[] imp = (imports != null ? StringUtils.stringToArray(imports, ",") : new String[0]);
        for (int i = 0; i < imp.length; i++) {
            String string = imp[i].trim();
            set.add(string);
        }
        for (final Iterator itr = injections.keySet().iterator(); itr.hasNext(); ) {
            final String className = (String) itr.next();
            final int lastIndexOf = className.lastIndexOf("/");
            final String pkgName = className.substring(0, lastIndexOf).replace('/', '.');
            if (!set.contains(pkgName)) {
                set.add(pkgName);
                optionalImports += (optionalImports.length() > 0 ? ", " : "") + pkgName + ";resolution:=optional";
            }
        }
    }

    /**
	 * write the message body to a stream.
	 * 
	 * @param out
	 *            the output stream.
	 * @throws IOException
	 *             in case of parse errors.
	 */
    public void writeBody(final ObjectOutputStream out) throws IOException {
        out.writeUTF(serviceID);
        out.writeUTF(imports);
        out.writeUTF(exports);
        writeStringArray(out, serviceInterfaceNames);
        //$NON-NLS-1$
        out.writeUTF(smartProxyName == null ? "" : smartProxyName);
        final short blocks = (short) injections.size();
        out.writeShort(blocks);
        final String[] injectionNames = (String[]) injections.keySet().toArray(new String[blocks]);
        for (short i = 0; i < blocks; i++) {
            out.writeUTF(injectionNames[i]);
            writeBytes(out, (byte[]) injections.get(injectionNames[i]));
        }
    }

    /**
	 * get the service ID.
	 * 
	 * @return the service ID.
	 */
    public String getServiceID() {
        return serviceID;
    }

    /**
	 * set the service ID.
	 * 
	 * @param serviceID
	 *            the service ID.
	 */
    public void setServiceID(final String serviceID) {
        this.serviceID = serviceID;
    }

    /**
	 * set the injections.
	 * 
	 * @param injections
	 *            the injections.
	 */
    public void setInjections(final Map injections) {
        this.injections = injections;
    }

    /**
	 * get the interface name of the delivered service.
	 * 
	 * @return the class name of the interface.
	 */
    public String[] getInterfaceNames() {
        return serviceInterfaceNames;
    }

    /**
	 * set the interface names.
	 * 
	 * @param interfaceNames
	 *            the interface class names.
	 */
    public void setInterfaceNames(final String[] interfaceNames) {
        serviceInterfaceNames = interfaceNames;
    }

    /**
	 * convenience method to get the bytes of the interface class.
	 * 
	 * @return the interface class.
	 */
    public byte[] getInterfaceClass() {
        return (byte[]) injections.get(serviceInterfaceNames[0].replace('.', '/') + //$NON-NLS-1$
        ".class");
    }

    /**
	 * get the smart proxy class name.
	 * 
	 * @return the class name of the smart proxy or null of undefined.
	 */
    public String getSmartProxyName() {
        return smartProxyName;
    }

    /**
	 * set the smart proxy name.
	 * 
	 * @param smartProxyName
	 *            the smart proxy name.
	 */
    public void setSmartProxyName(final String smartProxyName) {
        this.smartProxyName = smartProxyName;
    }

    /**
	 * convenience method to get the bytes of the smart proxy class.
	 * 
	 * @return the class or null if undefined.
	 */
    public byte[] getProxyClass() {
        if (smartProxyName == null) {
            return null;
        }
        return (byte[]) injections.get(smartProxyName.replace('.', '/') + //$NON-NLS-1$
        ".class");
    }

    /**
	 * get the list of class injection.
	 * 
	 * @return a <code>List</code> of class names.
	 */
    public Map getInjections() {
        return injections;
    }

    /**
	 * get the imports.
	 * 
	 * @return the imports.
	 */
    public String getImports() {
        return imports;
    }

    /**
	 * set the imports.
	 * 
	 * @param imports
	 *            the imports.
	 */
    public void setImports(final String imports) {
        this.imports = imports;
    }

    /**
	 * get the exports.
	 * 
	 * @return the exports.
	 */
    public String getExports() {
        return exports;
    }

    /**
	 * set the exports.
	 * 
	 * @param exports
	 *            the exports.
	 */
    public void setExports(final String exports) {
        this.exports = exports;
    }

    /**
	 * String representation for debug outputs.
	 * 
	 * @return a string representation.
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        //$NON-NLS-1$
        buffer.append("[DELIVER_SERVICE] - XID: ");
        buffer.append(xid);
        //$NON-NLS-1$
        buffer.append(", serviceID: ");
        buffer.append(serviceID);
        //$NON-NLS-1$
        buffer.append(", serviceInterfaceName: ");
        buffer.append(Arrays.asList(serviceInterfaceNames));
        if (smartProxyName != null) {
            //$NON-NLS-1$
            buffer.append(" smartProxy: ");
            buffer.append(smartProxyName);
        }
        if (injections.size() > 0) {
            //$NON-NLS-1$
            buffer.append(", classInjections ");
            buffer.append(injections.keySet());
        }
        return buffer.toString();
    }

    public String getOptionalImports() {
        return optionalImports;
    }
}
