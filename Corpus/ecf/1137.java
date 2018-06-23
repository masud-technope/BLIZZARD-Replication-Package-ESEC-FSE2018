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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Describes a pattern that can be used by a generic parser.
 * 
 * @author Erkki
 */
public interface IPatternDescriptor {

    public static final String ID_PARAM = "id";

    public static final String NAME_PARAM = "name";

    public Pattern getPattern();

    public String[] getParameters();

    public Map<String, Object> getValueMap(Matcher m);
}
