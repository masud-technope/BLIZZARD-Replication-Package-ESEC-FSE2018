/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.eval;

import java.util.Map;
import org.eclipse.jdt.core.compiler.*;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;

/**
 * A variables evaluator compiles the global variables of an evaluation context and returns
 * the corresponding class files. Or it reports problems against these variables.
 */
public class VariablesEvaluator extends Evaluator implements EvaluationConstants {

    /**
 * Creates a new global variables evaluator.
 */
     VariablesEvaluator(EvaluationContext context, INameEnvironment environment, Map<String, String> options, IRequestor requestor, IProblemFactory problemFactory) {
        super(context, environment, options, requestor, problemFactory);
    }

    /**
 * @see org.eclipse.jdt.internal.eval.Evaluator
 */
    protected void addEvaluationResultForCompilationProblem(Map<char[], EvaluationResult> resultsByIDs, CategorizedProblem problem, char[] cuSource) {
        // set evaluation id and type to an internal problem by default
        char[] evaluationID = cuSource;
        int evaluationType = EvaluationResult.T_INTERNAL;
        int pbLine = problem.getSourceLineNumber();
        int currentLine = 1;
        // check package declaration
        char[] packageName = getPackageName();
        if (packageName.length > 0) {
            if (pbLine == 1) {
                // set evaluation id and type
                evaluationID = packageName;
                evaluationType = EvaluationResult.T_PACKAGE;
                // shift line number, source start and source end
                problem.setSourceLineNumber(1);
                problem.setSourceStart(0);
                problem.setSourceEnd(evaluationID.length - 1);
            }
            currentLine++;
        }
        // check imports
        char[][] imports = this.context.imports;
        if ((currentLine <= pbLine) && (pbLine < (currentLine + imports.length))) {
            // set evaluation id and type
            evaluationID = imports[pbLine - currentLine];
            evaluationType = EvaluationResult.T_IMPORT;
            // shift line number, source start and source end
            problem.setSourceLineNumber(1);
            problem.setSourceStart(0);
            problem.setSourceEnd(evaluationID.length - 1);
        }
        // + 1 to skip the class declaration line
        currentLine += imports.length + 1;
        // check variable declarations
        int varCount = this.context.variableCount;
        if ((currentLine <= pbLine) && (pbLine < currentLine + varCount)) {
            GlobalVariable var = this.context.variables[pbLine - currentLine];
            // set evaluation id and type
            evaluationID = var.getName();
            evaluationType = EvaluationResult.T_VARIABLE;
            // shift line number, source start and source end
            int pbStart = problem.getSourceStart() - var.declarationStart;
            int pbEnd = problem.getSourceEnd() - var.declarationStart;
            int typeLength = var.getTypeName().length;
            if ((0 <= pbStart) && (pbEnd < typeLength)) {
                // problem on the type of the variable
                problem.setSourceLineNumber(-1);
            } else {
                // problem on the name of the variable
                // type length + space
                pbStart -= typeLength + 1;
                // type length + space
                pbEnd -= typeLength + 1;
                problem.setSourceLineNumber(0);
            }
            problem.setSourceStart(pbStart);
            problem.setSourceEnd(pbEnd);
        }
        // not needed any longer
        currentLine = -1;
        // check variable initializers
        for (int i = 0; i < varCount; i++) {
            GlobalVariable var = this.context.variables[i];
            char[] initializer = var.getInitializer();
            int initializerLength = initializer == null ? 0 : initializer.length;
            if ((var.initializerStart <= problem.getSourceStart()) && (problem.getSourceEnd() < var.initializerStart + var.name.length)) {
                /* Problem with the variable name.
			   Ignore because it must have already been reported
			   when checking the declaration.
			 */
                return;
            } else if ((var.initExpressionStart <= problem.getSourceStart()) && (problem.getSourceEnd() < var.initExpressionStart + initializerLength)) {
                // set evaluation id and type
                evaluationID = var.name;
                evaluationType = EvaluationResult.T_VARIABLE;
                // shift line number, source start and source end
                problem.setSourceLineNumber(pbLine - var.initializerLineStart + 1);
                problem.setSourceStart(problem.getSourceStart() - var.initExpressionStart);
                problem.setSourceEnd(problem.getSourceEnd() - var.initExpressionStart);
                break;
            }
        }
        EvaluationResult result = resultsByIDs.get(evaluationID);
        if (result == null) {
            resultsByIDs.put(evaluationID, new EvaluationResult(evaluationID, evaluationType, new CategorizedProblem[] { problem }));
        } else {
            result.addProblem(problem);
        }
    }

    /**
 * @see org.eclipse.jdt.internal.eval.Evaluator
 */
    protected char[] getClassName() {
        return CharOperation.concat(EvaluationConstants.GLOBAL_VARS_CLASS_NAME_PREFIX, Integer.toString(EvaluationContext.VAR_CLASS_COUNTER + 1).toCharArray());
    }

    /**
 * Creates and returns a compiler for this evaluator.
 */
    Compiler getCompiler(ICompilerRequestor compilerRequestor) {
        Compiler compiler = super.getCompiler(compilerRequestor);
        // Initialize the compiler's lookup environment with the already compiled super class
        IBinaryType binaryType = this.context.getRootCodeSnippetBinary();
        if (binaryType != null) {
            compiler.lookupEnvironment.cacheBinaryType(binaryType, /*no access restriction*/
            null);
        }
        // and the installed global variable classes
        VariablesInfo installedVars = this.context.installedVars;
        if (installedVars != null) {
            ClassFile[] classFiles = installedVars.classFiles;
            for (int i = 0; i < classFiles.length; i++) {
                ClassFile classFile = classFiles[i];
                IBinaryType binary = null;
                try {
                    binary = new ClassFileReader(classFile.getBytes(), null);
                } catch (ClassFormatException e) {
                    e.printStackTrace();
                }
                compiler.lookupEnvironment.cacheBinaryType(binary, /*no access restriction*/
                null);
            }
        }
        return compiler;
    }

    /**
 * Returns the name of package of the current compilation unit.
 */
    protected char[] getPackageName() {
        return this.context.packageName;
    }

    /**
 * @see org.eclipse.jdt.internal.eval.Evaluator
 */
    protected char[] getSource() {
        StringBuffer buffer = new StringBuffer();
        int lineNumberOffset = 1;
        // package declaration
        char[] packageName = getPackageName();
        if (packageName.length != 0) {
            //$NON-NLS-1$
            buffer.append("package ");
            buffer.append(packageName);
            buffer.append(';').append(this.context.lineSeparator);
            lineNumberOffset++;
        }
        // import declarations
        char[][] imports = this.context.imports;
        for (int i = 0; i < imports.length; i++) {
            //$NON-NLS-1$
            buffer.append("import ");
            buffer.append(imports[i]);
            buffer.append(';').append(this.context.lineSeparator);
            lineNumberOffset++;
        }
        // class declaration
        //$NON-NLS-1$
        buffer.append("public class ");
        buffer.append(getClassName());
        //$NON-NLS-1$
        buffer.append(" extends ");
        buffer.append(PACKAGE_NAME);
        //$NON-NLS-1$
        buffer.append(".");
        buffer.append(ROOT_CLASS_NAME);
        //$NON-NLS-1$
        buffer.append(" {").append(this.context.lineSeparator);
        lineNumberOffset++;
        // field declarations
        GlobalVariable[] vars = this.context.variables;
        VariablesInfo installedVars = this.context.installedVars;
        for (int i = 0; i < this.context.variableCount; i++) {
            GlobalVariable var = vars[i];
            //$NON-NLS-1$
            buffer.append("\tpublic static ");
            var.declarationStart = buffer.length();
            buffer.append(var.typeName);
            //$NON-NLS-1$
            buffer.append(" ");
            char[] varName = var.name;
            buffer.append(varName);
            buffer.append(';').append(this.context.lineSeparator);
            lineNumberOffset++;
        }
        // field initializations
        //$NON-NLS-1$
        buffer.append("\tstatic {").append(this.context.lineSeparator);
        lineNumberOffset++;
        for (int i = 0; i < this.context.variableCount; i++) {
            GlobalVariable var = vars[i];
            char[] varName = var.name;
            GlobalVariable installedVar = installedVars == null ? null : installedVars.varNamed(varName);
            if (installedVar == null || !CharOperation.equals(installedVar.typeName, var.typeName)) {
                // Initialize with initializer if there was no previous value
                char[] initializer = var.initializer;
                if (initializer != null) {
                    //$NON-NLS-1$
                    buffer.append("\t\ttry {").append(//$NON-NLS-1$
                    this.context.lineSeparator);
                    lineNumberOffset++;
                    var.initializerLineStart = lineNumberOffset;
                    //$NON-NLS-1$
                    buffer.append(//$NON-NLS-1$
                    "\t\t\t");
                    var.initializerStart = buffer.length();
                    buffer.append(varName);
                    //$NON-NLS-1$
                    buffer.append(//$NON-NLS-1$
                    "= ");
                    var.initExpressionStart = buffer.length();
                    buffer.append(initializer);
                    lineNumberOffset += numberOfCRs(initializer);
                    buffer.append(';').append(this.context.lineSeparator);
                    //$NON-NLS-1$
                    buffer.append("\t\t} catch (Throwable e) {").append(//$NON-NLS-1$
                    this.context.lineSeparator);
                    //$NON-NLS-1$
                    buffer.append("\t\t\te.printStackTrace();").append(//$NON-NLS-1$
                    this.context.lineSeparator);
                    //$NON-NLS-1$
                    buffer.append("\t\t}").append(//$NON-NLS-1$
                    this.context.lineSeparator);
                    lineNumberOffset// 4 CRs
                     += 4;
                }
            } else {
                // Initialize with previous value if name and type are the same
                //$NON-NLS-1$
                buffer.append("\t\t");
                buffer.append(varName);
                //$NON-NLS-1$
                buffer.append("= ");
                char[] installedPackageName = installedVars.packageName;
                if (installedPackageName != null && installedPackageName.length != 0) {
                    buffer.append(installedPackageName);
                    //$NON-NLS-1$
                    buffer.append(//$NON-NLS-1$
                    ".");
                }
                buffer.append(installedVars.className);
                //$NON-NLS-1$
                buffer.append(".");
                buffer.append(varName);
                buffer.append(';').append(this.context.lineSeparator);
                lineNumberOffset++;
            }
        }
        //$NON-NLS-1$
        buffer.append("\t}").append(this.context.lineSeparator);
        // end of class declaration
        buffer.append('}').append(this.context.lineSeparator);
        // return result
        int length = buffer.length();
        char[] result = new char[length];
        buffer.getChars(0, length, result, 0);
        return result;
    }

    /**
 * Returns the number of cariage returns included in the given source.
 */
    private int numberOfCRs(char[] source) {
        int numberOfCRs = 0;
        boolean lastWasCR = false;
        for (int i = 0; i < source.length; i++) {
            char currentChar = source[i];
            switch(currentChar) {
                case '\r':
                    lastWasCR = true;
                    numberOfCRs++;
                    break;
                case '\n':
                    if (// merge CR-LF
                    !lastWasCR)
                        // merge CR-LF
                        numberOfCRs++;
                    lastWasCR = false;
                    break;
                default:
                    lastWasCR = false;
            }
        }
        return numberOfCRs;
    }
}
