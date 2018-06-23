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
package org.eclipse.ecf.internal.bulletinboard.commons.parsing;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasePatternDescriptor implements IPatternDescriptor {

    private Pattern pattern;

    String[] parameters;

    public  BasePatternDescriptor(Pattern pattern, String[] parameters) {
        super();
        this.pattern = pattern;
        this.parameters = parameters;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Map<String, Object> getValueMap(Matcher m) {
        HashMap<String, Object> map = new HashMap<String, Object>(parameters.length);
        int i = 0;
        for (String param : parameters) {
            map.put(param, m.group(++i));
        }
        return map;
    }

    public String[] getParameters() {
        return parameters;
    }
}
