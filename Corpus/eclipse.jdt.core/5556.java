/*******************************************************************************
 * Copyright (c) 2006, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jesper Steen Moeller - Contributions for:
 *         Bug 407297: [1.8][compiler] Control generation of parameter names by option
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.tool;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Class used to handle options in the EclipseFileManager and the EclipseCompiler
 */
public final class Options {

    private static final Set<String> ZERO_ARGUMENT_OPTIONS;

    private static final Set<String> ONE_ARGUMENT_OPTIONS;

    private static final Set<String> FILE_MANAGER_OPTIONS;

    static {
        ZERO_ARGUMENT_OPTIONS = new HashSet();
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-progress");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-proceedOnError");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-proceedOnError:Fatal");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-time");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-v");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-version");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-showversion");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-deprecation");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-help");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-?");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-help:warn");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-?:warn");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-noExit");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-verbose");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-referenceInfo");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-inlineJSR");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-g");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-g:none");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-warn:none");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-preserveAllLocals");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-enableJavadoc");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-Xemacs");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-X");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-O");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-1.3");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-1.4");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-1.5");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-5");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-5.0");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-1.6");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-6");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-6.0");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-1.7");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-7");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-7.0");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-1.8");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-8");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-8.0");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-proc:only");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-proc:none");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-XprintProcessorInfo");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-XprintRounds");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-parameters");
        //$NON-NLS-1$
        Options.ZERO_ARGUMENT_OPTIONS.add("-genericsignature");
        FILE_MANAGER_OPTIONS = new HashSet();
        //$NON-NLS-1$
        Options.FILE_MANAGER_OPTIONS.add("-bootclasspath");
        //$NON-NLS-1$
        Options.FILE_MANAGER_OPTIONS.add("-encoding");
        //$NON-NLS-1$
        Options.FILE_MANAGER_OPTIONS.add("-d");
        //$NON-NLS-1$
        Options.FILE_MANAGER_OPTIONS.add("-classpath");
        //$NON-NLS-1$
        Options.FILE_MANAGER_OPTIONS.add("-cp");
        //$NON-NLS-1$
        Options.FILE_MANAGER_OPTIONS.add("-sourcepath");
        //$NON-NLS-1$
        Options.FILE_MANAGER_OPTIONS.add("-extdirs");
        //$NON-NLS-1$
        Options.FILE_MANAGER_OPTIONS.add("-endorseddirs");
        //$NON-NLS-1$
        Options.FILE_MANAGER_OPTIONS.add("-s");
        //$NON-NLS-1$
        Options.FILE_MANAGER_OPTIONS.add("-processorpath");
        ONE_ARGUMENT_OPTIONS = new HashSet();
        Options.ONE_ARGUMENT_OPTIONS.addAll(Options.FILE_MANAGER_OPTIONS);
        //$NON-NLS-1$
        Options.ONE_ARGUMENT_OPTIONS.add("-log");
        //$NON-NLS-1$
        Options.ONE_ARGUMENT_OPTIONS.add("-repeat");
        //$NON-NLS-1$
        Options.ONE_ARGUMENT_OPTIONS.add("-maxProblems");
        //$NON-NLS-1$
        Options.ONE_ARGUMENT_OPTIONS.add("-source");
        //$NON-NLS-1$
        Options.ONE_ARGUMENT_OPTIONS.add("-target");
        //$NON-NLS-1$
        Options.ONE_ARGUMENT_OPTIONS.add("-processor");
        //$NON-NLS-1$
        Options.ONE_ARGUMENT_OPTIONS.add("-classNames");
        //$NON-NLS-1$
        Options.ONE_ARGUMENT_OPTIONS.add("-properties");
    }

    public static int processOptionsFileManager(String option) {
        if (option == null)
            return -1;
        if (Options.FILE_MANAGER_OPTIONS.contains(option)) {
            return 1;
        }
        return -1;
    }

    public static int processOptions(String option) {
        if (option == null)
            return -1;
        if (Options.ZERO_ARGUMENT_OPTIONS.contains(option)) {
            return 0;
        }
        if (Options.ONE_ARGUMENT_OPTIONS.contains(option)) {
            return 1;
        }
        if (//$NON-NLS-1$
        option.startsWith("-g")) {
            int length = option.length();
            if (length > 3) {
                StringTokenizer tokenizer = new //$NON-NLS-1$
                StringTokenizer(//$NON-NLS-1$
                option.substring(3, option.length()), //$NON-NLS-1$
                ",");
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    if (//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                    "vars".equals(token) || "lines".equals(token) || "source".equals(token)) {
                        continue;
                    }
                    return -1;
                }
                return 0;
            }
            return -1;
        }
        if (//$NON-NLS-1$
        option.startsWith("-warn")) {
            int length = option.length();
            if (length <= 6) {
                return -1;
            }
            int warnTokenStart;
            switch(option.charAt(6)) {
                case '+':
                    warnTokenStart = 7;
                    break;
                case '-':
                    warnTokenStart = 7;
                    break;
                default:
                    warnTokenStart = 6;
            }
            StringTokenizer tokenizer = new //$NON-NLS-1$
            StringTokenizer(//$NON-NLS-1$
            option.substring(warnTokenStart, option.length()), //$NON-NLS-1$
            ",");
            int tokenCounter = 0;
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                tokenCounter++;
                if (//$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "allDeadCode") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "allDeprecation") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "allJavadoc") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "allOver-ann") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "assertIdentifier") || //$NON-NLS-1$
                token.equals("boxing") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "charConcat") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "compareIdentical") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "conditionAssign") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "constructorName") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "deadCode") || //$NON-NLS-1$
                token.equals("dep-ann") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "deprecation") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "discouraged") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "emptyBlock") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "enumIdentifier") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "enumSwitch") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "fallthrough") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "fieldHiding") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "finalBound") || //$NON-NLS-1$
                token.equals("finally") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "forbidden") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "hashCode") || //$NON-NLS-1$
                token.equals("hiding") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "includeAssertNull") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "incomplete-switch") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "indirectStatic") || token.equals("interfaceNonInherited") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "intfAnnotation") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "intfNonInherited") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "intfRedundant") || //$NON-NLS-1$
                token.equals("javadoc") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "localHiding") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "maskedCatchBlock") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "maskedCatchBlocks") || //$NON-NLS-1$
                token.equals("nls") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "noEffectAssign") || token.equals("noImplicitStringConversion") || //$NON-NLS-1$
                token.equals("null") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "nullDereference") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "over-ann") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "packageDefaultMethod") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "paramAssign") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "pkgDefaultMethod") || //$NON-NLS-1$
                token.equals("raw") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "semicolon") || //$NON-NLS-1$
                token.equals("serial") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "specialParamHiding") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "static-access") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "staticReceiver") || //$NON-NLS-1$
                token.equals("super") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "suppress") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "syncOverride") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "synthetic-access") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "syntheticAccess") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "typeHiding") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "unchecked") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "unnecessaryElse") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "unnecessaryOperator") || token.equals("unqualified-field-access") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "unqualifiedField") || //$NON-NLS-1$
                token.equals("unsafe") || //$NON-NLS-1$
                token.equals("unused") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "unusedArgument") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "unusedArguments") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "unusedImport") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "unusedImports") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "unusedLabel") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "unusedLocal") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "unusedLocals") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "unusedPrivate") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "unusedThrown") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "unusedTypeArgs") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "uselessTypeCheck") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "varargsCast") || //$NON-NLS-1$
                token.equals(//$NON-NLS-1$
                "warningToken")) {
                    continue;
                } else if (//$NON-NLS-1$
                token.equals("tasks")) {
                    //$NON-NLS-1$
                    String //$NON-NLS-1$
                    taskTags = "";
                    int start = token.indexOf('(');
                    int end = token.indexOf(')');
                    if (start >= 0 && end >= 0 && start < end) {
                        taskTags = token.substring(start + 1, end).trim();
                        taskTags = taskTags.replace('|', ',');
                    }
                    if (taskTags.length() == 0) {
                        return -1;
                    }
                    continue;
                } else {
                    return -1;
                }
            }
            if (tokenCounter == 0) {
                return -1;
            } else {
                return 0;
            }
        }
        if (//$NON-NLS-1$
        option.startsWith("-nowarn")) {
            switch(option.length()) {
                case 7:
                    return 0;
                case 8:
                    return -1;
                default:
                    int foldersStart = option.indexOf('[') + 1;
                    int foldersEnd = option.lastIndexOf(']');
                    if (foldersStart <= 8 || foldersEnd == -1 || foldersStart > foldersEnd || foldersEnd < option.length() - 1) {
                        return -1;
                    }
                    String folders = option.substring(foldersStart, foldersEnd);
                    if (folders.length() > 0) {
                        return 0;
                    } else {
                        return -1;
                    }
            }
        }
        if (//$NON-NLS-1$
        option.startsWith("-J") || //$NON-NLS-1$
        option.startsWith(//$NON-NLS-1$
        "-X") || //$NON-NLS-1$
        option.startsWith("-A")) {
            return 0;
        }
        return -1;
    }
}
