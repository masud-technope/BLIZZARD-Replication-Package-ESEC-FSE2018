/*******************************************************************************
 * Copyright (c) 2005, 2006 Erkki Lindpere and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erkki Lindpere - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.bulletinboard.commons;

import java.util.regex.Pattern;

public class PatternFactoryPair {

    public  PatternFactoryPair(Pattern pattern, IBBObjectFactory factory) {
        this.pattern = pattern;
        this.factory = factory;
    }

    private Pattern pattern;

    private IBBObjectFactory factory;

    public IBBObjectFactory getFactory() {
        return factory;
    }

    public Pattern getPattern() {
        return pattern;
    }
}
