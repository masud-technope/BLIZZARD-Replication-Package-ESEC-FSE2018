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

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import org.eclipse.ecf.bulletinboard.IBBObject;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.internal.bulletinboard.commons.IBBObjectFactory;
import org.eclipse.ecf.internal.bulletinboard.commons.util.StringUtil;

public class GenericParser {

    private final Namespace namespace;

    private final URL baseURL;

    public  GenericParser(Namespace namespace, URL baseURL) {
        super();
        this.namespace = namespace;
        this.baseURL = baseURL;
    }

    public IBBObject parseSingleIdName(IPatternDescriptor pattern, CharSequence sequence, IBBObjectFactory factory) {
        return (IBBObject) parseIdNamePairs(pattern, sequence, factory, false, false);
    }

    public Map<ID, IBBObject> parseMultiIdName(IPatternDescriptor pattern, CharSequence sequence, IBBObjectFactory factory, boolean preserveOrder) {
        return (Map<ID, IBBObject>) parseIdNamePairs(pattern, sequence, factory, true, preserveOrder);
    }

    private Object parseIdNamePairs(IPatternDescriptor pattern, CharSequence sequence, IBBObjectFactory factory, boolean expectMultipleMatches, boolean preserveOrder) {
        final Matcher m = pattern.getPattern().matcher(sequence);
        Map<ID, IBBObject> objectMap = null;
        if (expectMultipleMatches) {
            if (preserveOrder) {
                objectMap = new LinkedHashMap<ID, IBBObject>();
            } else {
                objectMap = new HashMap<ID, IBBObject>();
            }
        }
        while (m.find()) {
            final Map<String, Object> values = pattern.getValueMap(m);
            ID id = null;
            try {
                id = factory.createBBObjectId(namespace, baseURL, (String) values.get(IPatternDescriptor.ID_PARAM));
            } catch (final IDCreateException e) {
                e.printStackTrace();
            }
            final String name = StringUtil.stripHTMLTrim((String) values.get(IPatternDescriptor.NAME_PARAM));
            final IBBObject obj = factory.createBBObject(id, new String(name), values);
            if (expectMultipleMatches) {
                objectMap.put(id, obj);
            } else {
                return obj;
            }
        }
        if (expectMultipleMatches) {
            return objectMap;
        } else {
            return null;
        }
    }
}
