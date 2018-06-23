package org.eclipse.ecf.twitter.client;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.IContainer;

public interface IStartApp {

    public IContainer createClient() throws ContainerCreateException;
}
