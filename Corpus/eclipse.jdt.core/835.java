/*******************************************************************************
 * Copyright (c) 2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    wharley@bea.com - initial API and implementation
 *******************************************************************************/
package targets.infrastructure;

import org.eclipse.jdt.apt.pluggable.tests.annotations.Message6;

/**
 * A simple class with no annotations, to test compilation of vanilla projects
 */
public class NoAnno {

    // This is here to verify that we have access to the annotations jar from within the test project.
    Class<?> _annoClass = Message6.class;
}
