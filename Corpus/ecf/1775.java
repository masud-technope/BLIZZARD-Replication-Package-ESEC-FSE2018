/******************************************************************************* 
 * Copyright (c) 2010-2011 Naumen. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Pavel Samolisov - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.remoteservice.rpc.client;

import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.client.*;

/**
 * Trivial parameter serializer - just copy a parameter value
 * 
 * @author psamolisov
 */
public class TrivialParameterServializer implements IRemoteCallParameterSerializer {

    /**
	 * @throws NotSerializableException  
	 */
    public IRemoteCallParameter[] serializeParameter(String endpoint, IRemoteCall call, IRemoteCallable callable, IRemoteCallParameter[] currentParameters, Object[] paramToSerialize) throws NotSerializableException {
        List results = new ArrayList();
        if (paramToSerialize != null) {
            for (int i = 0; i < paramToSerialize.length; i++) {
                IRemoteCallParameter p = new RemoteCallParameter(currentParameters[i].getName(), paramToSerialize[i] == null ? currentParameters[i].getValue() : paramToSerialize[i]);
                results.add(p);
            }
        }
        return (IRemoteCallParameter[]) results.toArray(new IRemoteCallParameter[results.size()]);
    }

    /**
	 * All parameters will be serialized in the Apache XML-RPC library. We shouldn't serialize any parameters
	 * by default. 
	 * 
	 * @return the parameter value
	 */
    public IRemoteCallParameter serializeParameter(String endpoint, IRemoteCall call, IRemoteCallable callable, IRemoteCallParameter paramDefault, Object paramToSerialize) {
        // Just return a parameter		
        return new RemoteCallParameter(paramDefault.getName(), paramToSerialize == null ? paramDefault.getValue() : paramToSerialize);
    }
}
