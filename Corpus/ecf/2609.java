/****************************************************************************
 * Copyright (c) 2013 Composent and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Scott lewis - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.tests.osgi.services.remoteserviceadmin;

import java.util.concurrent.Future;

public interface TestServiceInterface1Async {

    Future<String> doStuff1Async();
}
