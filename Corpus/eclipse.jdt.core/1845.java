/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.codeassist.impl;

import java.util.Iterator;
import java.util.Map;
import org.eclipse.jdt.core.compiler.CharOperation;

public class AssistOptions {

    /**
	 * Option IDs
	 */
    public static final String OPTION_PerformVisibilityCheck = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.visibilityCheck";

    public static final String OPTION_ForceImplicitQualification = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.forceImplicitQualification";

    public static final String OPTION_FieldPrefixes = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.fieldPrefixes";

    public static final String OPTION_StaticFieldPrefixes = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.staticFieldPrefixes";

    public static final String OPTION_LocalPrefixes = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.localPrefixes";

    public static final String OPTION_ArgumentPrefixes = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.argumentPrefixes";

    public static final String OPTION_FieldSuffixes = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.fieldSuffixes";

    public static final String OPTION_StaticFieldSuffixes = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.staticFieldSuffixes";

    public static final String OPTION_LocalSuffixes = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.localSuffixes";

    public static final String OPTION_ArgumentSuffixes = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.argumentSuffixes";

    //$NON-NLS-1$
    public static final String ENABLED = "enabled";

    //$NON-NLS-1$
    public static final String DISABLED = "disabled";

    public boolean checkVisibility = false;

    public boolean forceImplicitQualification = false;

    public char[][] fieldPrefixes = null;

    public char[][] staticFieldPrefixes = null;

    public char[][] localPrefixes = null;

    public char[][] argumentPrefixes = null;

    public char[][] fieldSuffixes = null;

    public char[][] staticFieldSuffixes = null;

    public char[][] localSuffixes = null;

    public char[][] argumentSuffixes = null;

    /** 
	 * Initializing the assist options with default settings
	 */
    public  AssistOptions() {
    // Initializing the assist options with default settings
    }

    /** 
	 * Initializing the assist options with external settings
	 */
    public  AssistOptions(Map settings) {
        if (settings == null)
            return;
        // filter options which are related to the assist component
        Iterator entries = settings.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            if (!(entry.getKey() instanceof String))
                continue;
            if (!(entry.getValue() instanceof String))
                continue;
            String optionID = (String) entry.getKey();
            String optionValue = (String) entry.getValue();
            if (optionID.equals(OPTION_PerformVisibilityCheck)) {
                if (optionValue.equals(ENABLED)) {
                    this.checkVisibility = true;
                } else if (optionValue.equals(DISABLED)) {
                    this.checkVisibility = false;
                }
                continue;
            } else if (optionID.equals(OPTION_ForceImplicitQualification)) {
                if (optionValue.equals(ENABLED)) {
                    this.forceImplicitQualification = true;
                } else if (optionValue.equals(DISABLED)) {
                    this.forceImplicitQualification = false;
                }
                continue;
            } else if (optionID.equals(OPTION_FieldPrefixes)) {
                if (optionValue.length() == 0) {
                    this.fieldPrefixes = null;
                } else {
                    this.fieldPrefixes = CharOperation.splitAndTrimOn(',', optionValue.toCharArray());
                }
                continue;
            } else if (optionID.equals(OPTION_StaticFieldPrefixes)) {
                if (optionValue.length() == 0) {
                    this.staticFieldPrefixes = null;
                } else {
                    this.staticFieldPrefixes = CharOperation.splitAndTrimOn(',', optionValue.toCharArray());
                }
                continue;
            } else if (optionID.equals(OPTION_LocalPrefixes)) {
                if (optionValue.length() == 0) {
                    this.localPrefixes = null;
                } else {
                    this.localPrefixes = CharOperation.splitAndTrimOn(',', optionValue.toCharArray());
                }
                continue;
            } else if (optionID.equals(OPTION_ArgumentPrefixes)) {
                if (optionValue.length() == 0) {
                    this.argumentPrefixes = null;
                } else {
                    this.argumentPrefixes = CharOperation.splitAndTrimOn(',', optionValue.toCharArray());
                }
                continue;
            } else if (optionID.equals(OPTION_FieldSuffixes)) {
                if (optionValue.length() == 0) {
                    this.fieldSuffixes = null;
                } else {
                    this.fieldSuffixes = CharOperation.splitAndTrimOn(',', optionValue.toCharArray());
                }
                continue;
            } else if (optionID.equals(OPTION_StaticFieldSuffixes)) {
                if (optionValue.length() == 0) {
                    this.staticFieldSuffixes = null;
                } else {
                    this.staticFieldSuffixes = CharOperation.splitAndTrimOn(',', optionValue.toCharArray());
                }
                continue;
            } else if (optionID.equals(OPTION_LocalSuffixes)) {
                if (optionValue.length() == 0) {
                    this.localSuffixes = null;
                } else {
                    this.localSuffixes = CharOperation.splitAndTrimOn(',', optionValue.toCharArray());
                }
                continue;
            } else if (optionID.equals(OPTION_ArgumentSuffixes)) {
                if (optionValue.length() == 0) {
                    this.argumentSuffixes = null;
                } else {
                    this.argumentSuffixes = CharOperation.splitAndTrimOn(',', optionValue.toCharArray());
                }
                continue;
            }
        }
    }
}
