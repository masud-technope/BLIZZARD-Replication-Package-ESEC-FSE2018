/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Gábor Kövesdán - Contribution for Bug 350000 - [content assist] Include non-prefix matches in auto-complete suggestions
 *******************************************************************************/
package org.eclipse.jdt.internal.codeassist.impl;

import java.util.Map;
import org.eclipse.jdt.core.compiler.CharOperation;

@SuppressWarnings("rawtypes")
public class AssistOptions {

    /**
	 * Option IDs
	 */
    public static final String OPTION_PerformVisibilityCheck = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.visibilityCheck";

    public static final String OPTION_PerformDeprecationCheck = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.deprecationCheck";

    public static final String OPTION_ForceImplicitQualification = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.forceImplicitQualification";

    public static final String OPTION_FieldPrefixes = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.fieldPrefixes";

    public static final String OPTION_StaticFieldPrefixes = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.staticFieldPrefixes";

    public static final String OPTION_StaticFinalFieldPrefixes = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.staticFinalFieldPrefixes";

    public static final String OPTION_LocalPrefixes = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.localPrefixes";

    public static final String OPTION_ArgumentPrefixes = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.argumentPrefixes";

    public static final String OPTION_FieldSuffixes = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.fieldSuffixes";

    public static final String OPTION_StaticFieldSuffixes = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.staticFieldSuffixes";

    public static final String OPTION_StaticFinalFieldSuffixes = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.staticFinalFieldSuffixes";

    public static final String OPTION_LocalSuffixes = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.localSuffixes";

    public static final String OPTION_ArgumentSuffixes = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.argumentSuffixes";

    public static final String OPTION_PerformForbiddenReferenceCheck = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.forbiddenReferenceCheck";

    public static final String OPTION_PerformDiscouragedReferenceCheck = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.discouragedReferenceCheck";

    public static final String OPTION_CamelCaseMatch = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.camelCaseMatch";

    public static final String OPTION_SubstringMatch = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.substringMatch";

    public static final String OPTION_SuggestStaticImports = //$NON-NLS-1$
    "org.eclipse.jdt.core.codeComplete.suggestStaticImports";

    //$NON-NLS-1$
    public static final String ENABLED = "enabled";

    //$NON-NLS-1$
    public static final String DISABLED = "disabled";

    public boolean checkVisibility = false;

    public boolean checkDeprecation = false;

    public boolean checkForbiddenReference = false;

    public boolean checkDiscouragedReference = false;

    public boolean forceImplicitQualification = false;

    public boolean camelCaseMatch = true;

    public boolean substringMatch = true;

    public boolean suggestStaticImport = true;

    public char[][] fieldPrefixes = null;

    public char[][] staticFieldPrefixes = null;

    public char[][] staticFinalFieldPrefixes = null;

    public char[][] localPrefixes = null;

    public char[][] argumentPrefixes = null;

    public char[][] fieldSuffixes = null;

    public char[][] staticFieldSuffixes = null;

    public char[][] staticFinalFieldSuffixes = null;

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
        set(settings);
    }

    public void set(Map optionsMap) {
        Object optionValue;
        if ((optionValue = optionsMap.get(OPTION_PerformVisibilityCheck)) != null) {
            if (ENABLED.equals(optionValue)) {
                this.checkVisibility = true;
            } else if (DISABLED.equals(optionValue)) {
                this.checkVisibility = false;
            }
        }
        if ((optionValue = optionsMap.get(OPTION_ForceImplicitQualification)) != null) {
            if (ENABLED.equals(optionValue)) {
                this.forceImplicitQualification = true;
            } else if (DISABLED.equals(optionValue)) {
                this.forceImplicitQualification = false;
            }
        }
        if ((optionValue = optionsMap.get(OPTION_FieldPrefixes)) != null) {
            if (optionValue instanceof String) {
                String stringValue = (String) optionValue;
                if (stringValue.length() > 0) {
                    this.fieldPrefixes = splitAndTrimOn(',', stringValue.toCharArray());
                } else {
                    this.fieldPrefixes = null;
                }
            }
        }
        if ((optionValue = optionsMap.get(OPTION_StaticFieldPrefixes)) != null) {
            if (optionValue instanceof String) {
                String stringValue = (String) optionValue;
                if (stringValue.length() > 0) {
                    this.staticFieldPrefixes = splitAndTrimOn(',', stringValue.toCharArray());
                } else {
                    this.staticFieldPrefixes = null;
                }
            }
        }
        if ((optionValue = optionsMap.get(OPTION_StaticFinalFieldPrefixes)) != null) {
            if (optionValue instanceof String) {
                String stringValue = (String) optionValue;
                if (stringValue.length() > 0) {
                    this.staticFinalFieldPrefixes = splitAndTrimOn(',', stringValue.toCharArray());
                } else {
                    this.staticFinalFieldPrefixes = null;
                }
            }
        }
        if ((optionValue = optionsMap.get(OPTION_LocalPrefixes)) != null) {
            if (optionValue instanceof String) {
                String stringValue = (String) optionValue;
                if (stringValue.length() > 0) {
                    this.localPrefixes = splitAndTrimOn(',', stringValue.toCharArray());
                } else {
                    this.localPrefixes = null;
                }
            }
        }
        if ((optionValue = optionsMap.get(OPTION_ArgumentPrefixes)) != null) {
            if (optionValue instanceof String) {
                String stringValue = (String) optionValue;
                if (stringValue.length() > 0) {
                    this.argumentPrefixes = splitAndTrimOn(',', stringValue.toCharArray());
                } else {
                    this.argumentPrefixes = null;
                }
            }
        }
        if ((optionValue = optionsMap.get(OPTION_FieldSuffixes)) != null) {
            if (optionValue instanceof String) {
                String stringValue = (String) optionValue;
                if (stringValue.length() > 0) {
                    this.fieldSuffixes = splitAndTrimOn(',', stringValue.toCharArray());
                } else {
                    this.fieldSuffixes = null;
                }
            }
        }
        if ((optionValue = optionsMap.get(OPTION_StaticFieldSuffixes)) != null) {
            if (optionValue instanceof String) {
                String stringValue = (String) optionValue;
                if (stringValue.length() > 0) {
                    this.staticFieldSuffixes = splitAndTrimOn(',', stringValue.toCharArray());
                } else {
                    this.staticFieldSuffixes = null;
                }
            }
        }
        if ((optionValue = optionsMap.get(OPTION_StaticFinalFieldSuffixes)) != null) {
            if (optionValue instanceof String) {
                String stringValue = (String) optionValue;
                if (stringValue.length() > 0) {
                    this.staticFinalFieldSuffixes = splitAndTrimOn(',', stringValue.toCharArray());
                } else {
                    this.staticFinalFieldSuffixes = null;
                }
            }
        }
        if ((optionValue = optionsMap.get(OPTION_LocalSuffixes)) != null) {
            if (optionValue instanceof String) {
                String stringValue = (String) optionValue;
                if (stringValue.length() > 0) {
                    this.localSuffixes = splitAndTrimOn(',', stringValue.toCharArray());
                } else {
                    this.localSuffixes = null;
                }
            }
        }
        if ((optionValue = optionsMap.get(OPTION_ArgumentSuffixes)) != null) {
            if (optionValue instanceof String) {
                String stringValue = (String) optionValue;
                if (stringValue.length() > 0) {
                    this.argumentSuffixes = splitAndTrimOn(',', stringValue.toCharArray());
                } else {
                    this.argumentSuffixes = null;
                }
            }
        }
        if ((optionValue = optionsMap.get(OPTION_PerformForbiddenReferenceCheck)) != null) {
            if (ENABLED.equals(optionValue)) {
                this.checkForbiddenReference = true;
            } else if (DISABLED.equals(optionValue)) {
                this.checkForbiddenReference = false;
            }
        }
        if ((optionValue = optionsMap.get(OPTION_PerformDiscouragedReferenceCheck)) != null) {
            if (ENABLED.equals(optionValue)) {
                this.checkDiscouragedReference = true;
            } else if (DISABLED.equals(optionValue)) {
                this.checkDiscouragedReference = false;
            }
        }
        if ((optionValue = optionsMap.get(OPTION_CamelCaseMatch)) != null) {
            if (ENABLED.equals(optionValue)) {
                this.camelCaseMatch = true;
            } else if (DISABLED.equals(optionValue)) {
                this.camelCaseMatch = false;
            }
        }
        if ((optionValue = optionsMap.get(OPTION_SubstringMatch)) != null) {
            if (ENABLED.equals(optionValue)) {
                this.substringMatch = true;
            } else if (DISABLED.equals(optionValue)) {
                this.substringMatch = false;
            }
        }
        if ((optionValue = optionsMap.get(OPTION_PerformDeprecationCheck)) != null) {
            if (ENABLED.equals(optionValue)) {
                this.checkDeprecation = true;
            } else if (DISABLED.equals(optionValue)) {
                this.checkDeprecation = false;
            }
        }
        if ((optionValue = optionsMap.get(OPTION_SuggestStaticImports)) != null) {
            if (ENABLED.equals(optionValue)) {
                this.suggestStaticImport = true;
            } else if (DISABLED.equals(optionValue)) {
                this.suggestStaticImport = false;
            }
        }
    }

    private char[][] splitAndTrimOn(char divider, char[] arrayToSplit) {
        char[][] result = CharOperation.splitAndTrimOn(',', arrayToSplit);
        int length = result.length;
        int resultCount = 0;
        for (int i = 0; i < length; i++) {
            if (result[i].length != 0) {
                result[resultCount++] = result[i];
            }
        }
        if (resultCount != length) {
            System.arraycopy(result, 0, result = new char[resultCount][], 0, resultCount);
        }
        return result;
    }
}
