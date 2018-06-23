package org.eclipse.ecf.remoteservice;

import java.lang.reflect.InvocationHandler;

/**
 * @since 8.0
 */
public interface IRemoteServiceProxyCreator {

    public Object createProxy(ClassLoader classloader, Class[] interfaces, InvocationHandler handler);
}
