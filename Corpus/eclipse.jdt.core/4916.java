/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.core.search.matching;

import java.io.IOException;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.internal.core.index.*;

public class MultiTypeDeclarationPattern extends JavaSearchPattern {

    public char[][] simpleNames;

    public char[][] qualifications;

    // set to CLASS_SUFFIX for only matching classes
    // set to INTERFACE_SUFFIX for only matching interfaces
    // set to ENUM_SUFFIX for only matching enums
    // set to ANNOTATION_TYPE_SUFFIX for only matching annotation types
    // set to TYPE_SUFFIX for matching both classes and interfaces
    public char typeSuffix;

    protected static char[][] CATEGORIES = { TYPE_DECL };

    public  MultiTypeDeclarationPattern(char[][] qualifications, char[][] simpleNames, char typeSuffix, int matchRule) {
        this(matchRule);
        if (this.isCaseSensitive || qualifications == null) {
            this.qualifications = qualifications;
        } else {
            int length = qualifications.length;
            this.qualifications = new char[length][];
            for (int i = 0; i < length; i++) this.qualifications[i] = CharOperation.toLowerCase(qualifications[i]);
        }
        // null simple names are allowed (should return all names)
        if (simpleNames != null) {
            if (this.isCaseSensitive || this.isCamelCase) {
                this.simpleNames = simpleNames;
            } else {
                int length = simpleNames.length;
                this.simpleNames = new char[length][];
                for (int i = 0; i < length; i++) this.simpleNames[i] = CharOperation.toLowerCase(simpleNames[i]);
            }
        }
        this.typeSuffix = typeSuffix;
        // only used to report type declarations, not their positions
        this.mustResolve = typeSuffix != TYPE_SUFFIX;
    }

     MultiTypeDeclarationPattern(int matchRule) {
        super(TYPE_DECL_PATTERN, matchRule);
    }

    public SearchPattern getBlankPattern() {
        return new QualifiedTypeDeclarationPattern(R_EXACT_MATCH | R_CASE_SENSITIVE);
    }

    public char[][] getIndexCategories() {
        return CATEGORIES;
    }

    public boolean matchesDecodedKey(SearchPattern decodedPattern) {
        QualifiedTypeDeclarationPattern pattern = (QualifiedTypeDeclarationPattern) decodedPattern;
        // check type suffix
        if (this.typeSuffix != pattern.typeSuffix && this.typeSuffix != TYPE_SUFFIX) {
            if (!matchDifferentTypeSuffixes(this.typeSuffix, pattern.typeSuffix)) {
                return false;
            }
        }
        // check qualified name
        if (this.qualifications != null) {
            int count = 0;
            int max = this.qualifications.length;
            if (max == 0 && pattern.qualification.length > 0) {
                return false;
            }
            if (max > 0) {
                for (; count < max; count++) if (matchesName(this.qualifications[count], pattern.qualification))
                    break;
                if (count == max)
                    return false;
            }
        }
        // check simple name (null are allowed)
        if (this.simpleNames == null)
            return true;
        int count = 0;
        int max = this.simpleNames.length;
        for (; count < max; count++) if (matchesName(this.simpleNames[count], pattern.simpleName))
            break;
        return count < max;
    }

    public EntryResult[] queryIn(Index index) throws IOException {
        if (this.simpleNames == null) {
            // match rule is irrelevant when the key is null
            return index.query(getIndexCategories(), null, -1);
        }
        int count = -1;
        int numOfNames = this.simpleNames.length;
        EntryResult[][] allResults = numOfNames > 1 ? new EntryResult[numOfNames][] : null;
        for (int i = 0; i < numOfNames; i++) {
            char[] key = this.simpleNames[i];
            int matchRule = getMatchRule();
            switch(getMatchMode()) {
                case R_PREFIX_MATCH:
                    // do a prefix query with the simpleName
                    break;
                case R_EXACT_MATCH:
                    // do a prefix query with the simpleName
                    matchRule &= ~R_EXACT_MATCH;
                    matchRule |= R_PREFIX_MATCH;
                    key = CharOperation.append(key, SEPARATOR);
                    break;
                case R_PATTERN_MATCH:
                    if (key[key.length - 1] != '*')
                        key = CharOperation.concat(key, ONE_STAR, SEPARATOR);
                    break;
                case R_REGEXP_MATCH:
                    // TODO (frederic) implement regular expression match
                    break;
                case R_CAMELCASE_MATCH:
                case R_CAMELCASE_SAME_PART_COUNT_MATCH:
                    // do a prefix query with the simpleName
                    break;
            }
            // match rule is irrelevant when the key is null
            EntryResult[] entries = index.query(getIndexCategories(), key, matchRule);
            if (entries != null) {
                if (allResults == null)
                    return entries;
                allResults[++count] = entries;
            }
        }
        if (count == -1)
            return null;
        int total = 0;
        for (int i = 0; i <= count; i++) total += allResults[i].length;
        EntryResult[] allEntries = new EntryResult[total];
        int next = 0;
        for (int i = 0; i <= count; i++) {
            EntryResult[] entries = allResults[i];
            System.arraycopy(entries, 0, allEntries, next, entries.length);
            next += entries.length;
        }
        return allEntries;
    }

    protected StringBuffer print(StringBuffer output) {
        switch(this.typeSuffix) {
            case CLASS_SUFFIX:
                //$NON-NLS-1$
                output.append("MultiClassDeclarationPattern: ");
                break;
            case CLASS_AND_INTERFACE_SUFFIX:
                //$NON-NLS-1$
                output.append("MultiClassAndInterfaceDeclarationPattern: ");
                break;
            case CLASS_AND_ENUM_SUFFIX:
                //$NON-NLS-1$
                output.append("MultiClassAndEnumDeclarationPattern: ");
                break;
            case INTERFACE_SUFFIX:
                //$NON-NLS-1$
                output.append("MultiInterfaceDeclarationPattern: ");
                break;
            case INTERFACE_AND_ANNOTATION_SUFFIX:
                //$NON-NLS-1$
                output.append("MultiInterfaceAndAnnotationDeclarationPattern: ");
                break;
            case ENUM_SUFFIX:
                //$NON-NLS-1$
                output.append("MultiEnumDeclarationPattern: ");
                break;
            case ANNOTATION_TYPE_SUFFIX:
                //$NON-NLS-1$
                output.append("MultiAnnotationTypeDeclarationPattern: ");
                break;
            default:
                //$NON-NLS-1$
                output.append("MultiTypeDeclarationPattern: ");
                break;
        }
        if (this.qualifications != null) {
            //$NON-NLS-1$
            output.append("qualifications: <");
            for (int i = 0; i < this.qualifications.length; i++) {
                output.append(this.qualifications[i]);
                if (i < this.qualifications.length - 1)
                    //$NON-NLS-1$
                    output.append(//$NON-NLS-1$
                    ", ");
            }
            //$NON-NLS-1$
            output.append("> ");
        }
        if (this.simpleNames != null) {
            //$NON-NLS-1$
            output.append("simpleNames: <");
            for (int i = 0; i < this.simpleNames.length; i++) {
                output.append(this.simpleNames[i]);
                if (i < this.simpleNames.length - 1)
                    //$NON-NLS-1$
                    output.append(//$NON-NLS-1$
                    ", ");
            }
            //$NON-NLS-1$
            output.append(">");
        }
        return super.print(output);
    }
}
