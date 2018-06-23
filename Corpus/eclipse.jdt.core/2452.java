/*******************************************************************************
 * Copyright (c) 2000, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.tests.eval;

import java.util.Map;
import junit.framework.Test;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReasons;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
import org.eclipse.jdt.internal.eval.GlobalVariable;

/**
 * Negative tests for code snippet. Only compilation problems should be reported in
 * these tests.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class NegativeCodeSnippetTest extends EvaluationTest implements ProblemSeverities, ProblemReasons {

    /**
 * Creates a new NegativeCodeSnippetTest.
 */
    public  NegativeCodeSnippetTest(String name) {
        super(name);
    }

    public static Test suite() {
        return setupSuite(testClass());
    }

    /**
 * Test a scenario where the change of the package declaration causes a problem in a code snippet.
 */
    public void testChangePackage() {
        if (isJRockitVM())
            return;
        try {
            // define the package
            this.context.setPackageName("java.util.zip".toCharArray());
            // evaluate a code snippet that uses this variable
            char[] codeSnippet = "new InflaterInputStream(new java.io.ByteArrayInputStream(new byte[0])).len".toCharArray();
            evaluateWithExpectedDisplayString(codeSnippet, "0".toCharArray());
            // back to the default package but with correct import
            this.context.setPackageName(new char[0]);
            this.context.setImports(new char[][] { "java.util.zip.*".toCharArray() });
            // evaluate same code snippet
            evaluateWithExpectedProblem(codeSnippet, // The field len is not visible
            newProblem(IProblem.NotVisibleField, Error, 71, 73, 1));
            // back to the default package and with no imports
            this.context.setImports(new char[0][]);
            // evaluate same code snippet
            evaluateWithExpectedProblem(codeSnippet, // The type InflaterInputStream is undefined
            newProblem(IProblem.UndefinedType, Error, 4, 22, 1));
        } finally {
            // Clean up
            this.context.setPackageName(new char[0]);
            this.context.setImports(new char[0][]);
        }
    }

    public static Class testClass() {
        return NegativeCodeSnippetTest.class;
    }

    /**
 * Test a code snippet which declares a class that uses an expression as a returned statement
 * in one of its methods.
 */
    public void testExpressionInInnerClass() {
        //TODO (david) Syntax error diagnose should be improved in this case.
        evaluateWithExpectedProblem(buildCharArray(new String[] { "class X {", "	int foo() {", "		1 + 1", "	}", "}", "return new X().foo();" }), // Syntax error on token "+"
        newProblem(IProblem.ParsingError, Error, 21, 21, 2));
    }

    /**
 * Test extra closing curly bracket.
 */
    public void testExtraClosingCurlyBracket() {
        //TODO (david) Syntax error diagnose should be improved in this case.
        // just an expression with an extra curly bracket
        evaluateWithExpectedProblem("1 + 2}".toCharArray(), // Unmatched bracket
        newProblem(IProblem.ParsingError, Error, 0, 0, 1));
        // a statement followed by an unreachable expression with an extra curly bracket
        evaluateWithExpectedProblem(buildCharArray(new String[] { "return 1 + 1;", " 2 + 2}" }), // Unmatched bracket
        newProblem(IProblem.ParsingError, Error, 15, 15, 2));
    }

    /**
 * Test extra open round bracket.
 */
    public void testExtraOpenRoundBracket() {
        evaluateWithExpectedProblem("foo((a);".toCharArray(), // Unmatched bracket
        newProblem(IProblem.ParsingErrorInsertToComplete, Error, 6, 6, 1));
    }

    /**
 * Test a code snippet that contains an expression followed by a semi-colon.
 */
    public void testExtraSemiColonInExpression() {
        evaluateWithExpectedProblem("1;".toCharArray(), // Syntax error on token EOF
        newProblem(IProblem.ParsingErrorInsertToComplete, Error, 0, 0, 1));
    }

    /**
 * Test access to a non existing field.
 * (regression test for bug 25250 Scrapbook shows wrong error message)
 */
    public void testInvalidField() {
        evaluateWithExpectedProblem(("String s = \"\";\n" + "s.length").toCharArray(), "length cannot be resolved or is not a field\n");
    }

    /**
 * Test a code snippet which is valid but the evaluation context imports have problems.
 */
    public void testInvalidImport() {
        try {
            // problem on the first import
            this.context.setImports(new char[][] { "bar.Y".toCharArray() });
            evaluateWithExpectedImportProblem(buildCharArray(new String[] { "class X {", "	Y foo = new Y();", "}", "return new X().foo;" }), "bar.Y".toCharArray(), // The import bar.Y could not be resolved
            newProblem(IProblem.ImportNotFound, Error, 0, 4, 1));
            // problem on the second import
            this.context.setImports(new char[][] { "java.io.*".toCharArray(), "{".toCharArray() });
            evaluateWithExpectedImportProblem(buildCharArray(new String[] { "new File(\"c:\\temp\")" }), "{".toCharArray(), // Syntax error on token "{", "Identifier" expected
            newProblem(IProblem.ParsingErrorInvalidToken, Error, 0, 0, 1));
        } finally {
            // Clean up
            this.context.setImports(new char[0][]);
        }
    }

    /**
 * Test use of this.
 */
    public void testInvalidUseOfThisInSnippet() {
        evaluateWithExpectedProblem("this".toCharArray(), "Cannot use this in a static context\n");
    }

    /**
 * Test use of this.
 */
    public void testInvalidUseOfThisInSnippet2() {
        // just an expression with an extra curly bracket
        evaluateWithExpectedProblem("return this;".toCharArray(), "Cannot use this in a static context\n");
    }

    /**
 * Test a code snippet that misses a closing round bracket.
 */
    public void testMissingClosingRoundBracket() {
        evaluateWithExpectedProblem(buildCharArray(new String[] { "System.out.println(\"3 + 3\";" }), // Unmatched bracket
        newProblem(IProblem.ParsingErrorInsertToComplete, Error, 19, 25, 1));
    }

    /**
 * Test a code snippet that contains a string that misses the closing double quote .
 */
    public void testMissingDoubleQuote() {
        evaluateWithExpectedProblem(buildCharArray(new String[] { "System.out.println(\"3 + 3 = );", "3 + 3" }), // String literal is not properly closed by a double-quote
        newProblem(IProblem.UnterminatedString, Error, 19, 29, 1));
    }

    /**
 * Test an expression which is not the last statement.
 */
    public void testNonLastExpressionStatement() {
        evaluateWithExpectedProblem(buildCharArray(new String[] { "1 == '1';", "true" }), // Syntax error on token "=="
        newProblem(IProblem.ParsingErrorInvalidToken, Error, 2, 3, 1));
    }

    /**
 * Test a problem in the returned expression.
 */
    public void testProblemInExpression() {
        evaluateWithExpectedProblem("new Object(); 3 + ".toCharArray(), // Syntax error on token '+'
        newProblem(IProblem.ParsingErrorDeleteToken, Error, 16, 16, 1));
    }

    /**
 * Test a problem in the returned expression.
 */
    public void testProblemInExpression2() {
        evaluateWithExpectedProblem("new UnknownClass()".toCharArray(), // UnknownClass cannot be resolved to a type
        newProblem(IProblem.UndefinedType, Error, 4, 15, 1));
    }

    /**
 * Test a code snippet which declares a class that has a problem.
 */
    public void testProblemInInnerClass() {
        // class declared before the last expression
        evaluateWithExpectedProblem(buildCharArray(new String[] { "class X {", "	Y foo = new Y();", "}", "return new X().foo;" }), // The type Y is undefined
        newProblem(IProblem.UndefinedType, Error, 11, 11, 2));
        // class declared as part of the last expression
        evaluateWithExpectedWarningAndDisplayString(buildCharArray(new String[] { "return new Object() {", "	public String toString() {", "		int i = 0;", "		return \"an inner class\";", "	}", "};" }), new CategorizedProblem[] { // The local variable i is never used
        newProblem(IProblem.LocalVariableIsNeverUsed, Warning, 56, 56, 3) }, "an inner class".toCharArray());
    }

    /**
 * Test a problem in the statement before the returned expression.
 */
    public void testProblemInPreviousStatement() {
        //TODO (david) Syntax error diagnose should be improved in this case.
        evaluateWithExpectedProblem(buildCharArray(new String[] { "return foo(a a);", "1 + 3" }), //  Syntax error on token "a"
        newProblem(IProblem.ParsingErrorDeleteToken, Error, 13, 13, 1));
    }

    /**
 * Test a code snippet that has a problem in a return statement.
 */
    public void testProblemInReturnStatement() {
        evaluateWithExpectedProblem("return 1 ++ 1;".toCharArray(), // Invalid argument to operation ++/--
        newProblem(IProblem.InvalidUnaryExpression, Error, 7, 7, 1));
    }

    /**
 * Test a scenario where the removal of an import causes a problem in a code snippet.
 */
    public void testRemoveImport() {
        try {
            // define the import
            this.context.setImports(new char[][] { "java.io.*".toCharArray() });
            // evaluate a code snippet that uses this variable
            char[] codeSnippet = "new File(\"c:\\\\temp\")".toCharArray();
            evaluateWithExpectedDisplayString(codeSnippet, "c:\\temp".toCharArray());
            // remove the import
            this.context.setImports(new char[0][]);
            // evaluate same code snippet
            evaluateWithExpectedProblem(codeSnippet, // The type File is undefined
            newProblem(IProblem.UndefinedType, Error, 4, 7, 1));
        } finally {
            // Clean up
            this.context.setImports(new char[0][]);
        }
    }

    /**
 * Test a scenario where the removal of a variable causes a problem in a code snippet.
 */
    public void testRemoveVariable() {
        GlobalVariable var = null;
        try {
            // define the variable
            var = this.context.newVariable("int".toCharArray(), "i".toCharArray(), "1".toCharArray());
            installVariables(1);
            // evaluate a code snippet that uses this variable
            char[] codeSnippet = "i".toCharArray();
            evaluateWithExpectedDisplayString(codeSnippet, "1".toCharArray());
            // remove the variable
            this.context.deleteVariable(var);
            installVariables(0);
            // evaluate same code snippet
            evaluateWithExpectedProblem(codeSnippet, // i cannot be resolved to a variable
            newProblem(IProblem.UnresolvedVariable, Error, 0, 0, 1));
        } finally {
            // Clean up
            if (var != null) {
                this.context.deleteVariable(var);
            }
        }
    }

    /**
 * Test a code snippet that contains an expression which is not reachable.
 */
    public void testUnreachableExpression() {
        evaluateWithExpectedProblem(buildCharArray(new String[] { "return 1 + 1;", "2 + 2" }), // Unreachable code
        newProblem(IProblem.CodeCannotBeReached, Error, 14, 18, 2));
    }

    /**
 * Test a code snippet which is valid but never uses the evaluation context imports.
 * (regression test for bug 18922 Scrapbook does not come back when errors in snippet)
 */
    public void testUnusedImport() {
        try {
            this.context.setImports(new char[][] { "java.util.*".toCharArray() });
            // evaluate with import as error
            Map options = getCompilerOptions();
            options.put(CompilerOptions.OPTION_ReportUnusedImport, CompilerOptions.ERROR);
            // force evaluation so that the following evaluation will use a CodeSnippetEvaluator instead
            // of a VariableEvualator
            evaluateWithExpectedValue("1".toCharArray(), "1".toCharArray(), "int".toCharArray());
            evaluateWithExpectedImportProblem("new String(\"NOPE\")".toCharArray(), "java.util.*".toCharArray(), options, // The import java.util.* is unused
            newProblem(IProblem.UnusedImport, Error, 0, 10, 1));
        } finally {
            // Clean up
            this.context.setImports(new char[0][]);
        }
    }

    /**
 * Test a code snippet that has warnings but no errors.
 */
    public void testWarning() {
        evaluateWithExpectedWarningAndDisplayString(buildCharArray(new String[] { "int i;", "1 + 1" }), new CategorizedProblem[] { // The local variable i is never used
        newProblem(IProblem.LocalVariableIsNeverUsed, Warning, 4, 4, 1) }, "2".toCharArray());
    }
}
