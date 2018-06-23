/*******************************************************************************
 * Copyright (c) 2006, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.tests.dom;

import java.util.List;
import junit.framework.Test;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;

@SuppressWarnings("rawtypes")
public class ASTConverterRecoveryTest extends ConverterTestSetup {

    public  ASTConverterRecoveryTest(String name) {
        super(name);
    }

    static {
    //		TESTS_NAMES = new String[] {"test0003"};
    //		TESTS_NUMBERS =  new int[] { 19, 20 };
    }

    public static Test suite() {
        return buildModelTestSuite(ASTConverterRecoveryTest.class);
    }

    public void setUpSuite() throws Exception {
        super.setUpSuite();
        this.ast = AST.newAST(getJLS3());
    }

    public void test0001() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "\n" + "public class X {\n" + "	void foo() {\n" + "	    bar(0)\n" + "	    baz(1);\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    bar(0);\n" + "    baz(1);\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        Block block = methodDeclaration.getBody();
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 2, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;
        //$NON-NLS-1$
        checkSourceRange(expressionStatement, "bar(0)", source);
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation = (MethodInvocation) expression;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation, "bar(0)", source);
        List list = methodInvocation.arguments();
        //$NON-NLS-1$
        assertTrue("Parameter list is empty", list.size() == 1);
        Expression parameter = (Expression) list.get(0);
        //$NON-NLS-1$
        assertTrue("Not a number", parameter instanceof NumberLiteral);
        ITypeBinding typeBinding = parameter.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not int", "int", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(parameter, "0", source);
        Statement statement2 = (Statement) statements.get(1);
        //$NON-NLS-1$
        assertTrue("Not an expression statement", statement2.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
        ExpressionStatement expressionStatement2 = (ExpressionStatement) statement2;
        //$NON-NLS-1$
        checkSourceRange(expressionStatement2, "baz(1);", source);
        Expression expression2 = expressionStatement2.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a method invocation", expression2.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation2 = (MethodInvocation) expression2;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation2, "baz(1)", source);
        List list2 = methodInvocation2.arguments();
        //$NON-NLS-1$
        assertTrue("Parameter list is empty", list2.size() == 1);
        Expression parameter2 = (Expression) list2.get(0);
        //$NON-NLS-1$
        assertTrue("Not a number", parameter2 instanceof NumberLiteral);
        ITypeBinding typeBinding2 = parameter2.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not int", "int", typeBinding2.getName());
        //$NON-NLS-1$
        checkSourceRange(parameter2, "1", source);
    }

    public void test0002() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "\n" + "public class X {\n" + "	void foo() {\n" + "	    baz(0);\n" + "	    bar(1,\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    baz(0);\n" + "    bar(1);\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        Block block = methodDeclaration.getBody();
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 2, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;
        //$NON-NLS-1$
        checkSourceRange(expressionStatement, "baz(0);", source);
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation = (MethodInvocation) expression;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation, "baz(0)", source);
        List list = methodInvocation.arguments();
        //$NON-NLS-1$
        assertTrue("Parameter list is empty", list.size() == 1);
        Expression parameter = (Expression) list.get(0);
        //$NON-NLS-1$
        assertTrue("Not a number", parameter instanceof NumberLiteral);
        ITypeBinding typeBinding = parameter.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not int", "int", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(parameter, "0", source);
        Statement statement2 = (Statement) statements.get(1);
        //$NON-NLS-1$
        assertTrue("Not an expression statement", statement2.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
        ExpressionStatement expressionStatement2 = (ExpressionStatement) statement2;
        //$NON-NLS-1$
        checkSourceRange(expressionStatement2, "bar(1", source);
        Expression expression2 = expressionStatement2.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a method invocation", expression2.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation2 = (MethodInvocation) expression2;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation2, "bar(1", source);
        List list2 = methodInvocation2.arguments();
        //$NON-NLS-1$
        assertTrue("Parameter list is empty", list2.size() == 1);
        Expression parameter2 = (Expression) list2.get(0);
        //$NON-NLS-1$
        assertTrue("Not a number", parameter2 instanceof NumberLiteral);
        ITypeBinding typeBinding2 = parameter2.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not int", "int", typeBinding2.getName());
        //$NON-NLS-1$
        checkSourceRange(parameter2, "1", source);
    }

    public void test0003() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "\n" + "public class X {\n" + "	void foo() {\n" + "	    baz(0);\n" + "	    bar(1,\n" + "	    foo(3);\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    baz(0);\n" + "    bar(1,foo(3));\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        Block block = methodDeclaration.getBody();
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 2, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;
        //$NON-NLS-1$
        checkSourceRange(expressionStatement, "baz(0);", source);
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation = (MethodInvocation) expression;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation, "baz(0)", source);
        List list = methodInvocation.arguments();
        //$NON-NLS-1$
        assertTrue("Parameter list is empty", list.size() == 1);
        Expression parameter = (Expression) list.get(0);
        //$NON-NLS-1$
        assertTrue("Not a number", parameter instanceof NumberLiteral);
        ITypeBinding typeBinding = parameter.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not int", "int", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(parameter, "0", source);
        Statement statement2 = (Statement) statements.get(1);
        //$NON-NLS-1$
        assertTrue("Not an expression statement", statement2.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
        ExpressionStatement expressionStatement2 = (ExpressionStatement) statement2;
        //$NON-NLS-1$
        checkSourceRange(expressionStatement2, "bar(1,\n\t    foo(3);", source);
        Expression expression2 = expressionStatement2.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a method invocation", expression2.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation2 = (MethodInvocation) expression2;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation2, "bar(1,\n\t    foo(3)", source);
        List list2 = methodInvocation2.arguments();
        //$NON-NLS-1$
        assertTrue("Parameter list is empty", list2.size() == 2);
        Expression parameter2 = (Expression) list2.get(0);
        //$NON-NLS-1$
        assertTrue("Not a Number", parameter2 instanceof NumberLiteral);
        parameter2 = (Expression) list2.get(1);
        //$NON-NLS-1$
        assertTrue("Not a method invocation", parameter2 instanceof MethodInvocation);
        MethodInvocation methodInvocation3 = (MethodInvocation) parameter2;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation3, "foo(3)", source);
    }

    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=124296
    public void test0004() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "\n" + "public class X {\n" + "	void foo() {\n" + "	    int var= 123\n" + "	    System.out.println(var);\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    int var=123;\n" + "    System.out.println(var);\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        Block block = methodDeclaration.getBody();
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 2, statements.size());
        Statement statement1 = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not an expression statement", statement1.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) statement1;
        //$NON-NLS-1$
        checkSourceRange(variableDeclarationStatement, "int var= 123", source);
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        //$NON-NLS-1$
        checkSourceRange(variableDeclarationFragment, "var= 123", source);
    }

    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=126148
    public void test0005() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "\n" + "public class X {\n" + "	void foo() {\n" + "	    String[] s =  {\"\",,,};\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    String[] s={\"\",$missing$};\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        Block block = methodDeclaration.getBody();
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
        Statement statement1 = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not an expression variable declaration statement", statement1.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) statement1;
        //$NON-NLS-1$
        checkSourceRange(variableDeclarationStatement, "String[] s =  {\"\",,,};", source);
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        //$NON-NLS-1$
        checkSourceRange(variableDeclarationFragment, "s =  {\"\",,,}", source);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not an array initializer", expression.getNodeType() == ASTNode.ARRAY_INITIALIZER);
        ArrayInitializer arrayInitializer = (ArrayInitializer) expression;
        //$NON-NLS-1$
        checkSourceRange(arrayInitializer, "{\"\",,,}", source);
        List expressions = arrayInitializer.expressions();
        //$NON-NLS-1$
        assertEquals("wrong size", 2, expressions.size());
        Expression expression1 = (Expression) expressions.get(0);
        //$NON-NLS-1$
        assertTrue("Not a string literal", expression1.getNodeType() == ASTNode.STRING_LITERAL);
        StringLiteral stringLiteral = (StringLiteral) expression1;
        //$NON-NLS-1$
        checkSourceRange(stringLiteral, "\"\"", source);
        Expression expression2 = (Expression) expressions.get(1);
        //$NON-NLS-1$
        assertTrue("Not a string literal", expression2.getNodeType() == ASTNode.SIMPLE_NAME);
        SimpleName simpleName = (SimpleName) expression2;
        //$NON-NLS-1$
        checkSourceRange(simpleName, ",", source);
    }

    // check RECOVERED flag (insert tokens)
    public void test0006() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "\n" + "public class X {\n" + "	void foo() {\n" + "	    bar()\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    bar();\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
        Block block = methodDeclaration.getBody();
        assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) == 0);
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;
        //$NON-NLS-1$
        checkSourceRange(expressionStatement, "bar()", source);
        assertTrue("Not flag as RECOVERED", (expressionStatement.getFlags() & ASTNode.RECOVERED) != 0);
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation = (MethodInvocation) expression;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation, "bar()", source);
        assertTrue("Flag as RECOVERED", (methodInvocation.getFlags() & ASTNode.RECOVERED) == 0);
    }

    // check RECOVERED flag (insert tokens)
    public void test0007() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "\n" + "public class X {\n" + "	void foo() {\n" + "	    bar(baz()\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    bar(baz());\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
        Block block = methodDeclaration.getBody();
        assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) == 0);
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;
        //$NON-NLS-1$
        checkSourceRange(expressionStatement, "bar(baz()", source);
        assertTrue("Not flag as RECOVERED", (expressionStatement.getFlags() & ASTNode.RECOVERED) != 0);
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation = (MethodInvocation) expression;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation, "bar(baz()", source);
        assertTrue("Not flag as RECOVERED", (methodInvocation.getFlags() & ASTNode.RECOVERED) != 0);
        List arguments = methodInvocation.arguments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, arguments.size());
        Expression argument = (Expression) arguments.get(0);
        //$NON-NLS-1$
        assertTrue("Not a method invocation", argument.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation2 = (MethodInvocation) argument;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation2, "baz()", source);
        assertTrue("Flag as RECOVERED", (methodInvocation2.getFlags() & ASTNode.RECOVERED) == 0);
    }

    // check RECOVERED flag (insert tokens)
    public void test0008() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "\n" + "public class X {\n" + "	void foo() {\n" + "	    for(int i\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    for (int i; ; )     ;\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
        Block block = methodDeclaration.getBody();
        assertTrue("Not flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) != 0);
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not a for statement", statement.getNodeType() == ASTNode.FOR_STATEMENT);
        ForStatement forStatement = (ForStatement) statement;
        //$NON-NLS-1$
        checkSourceRange(forStatement, "for(int i", source);
        assertTrue("Not flag as RECOVERED", (forStatement.getFlags() & ASTNode.RECOVERED) != 0);
        List initializers = forStatement.initializers();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
        Expression expression = (Expression) initializers.get(0);
        //$NON-NLS-1$
        assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.VARIABLE_DECLARATION_EXPRESSION);
        VariableDeclarationExpression variableDeclarationExpression = (VariableDeclarationExpression) expression;
        //$NON-NLS-1$
        checkSourceRange(variableDeclarationExpression, "int i", source);
        assertTrue("Not flag as RECOVERED", (variableDeclarationExpression.getFlags() & ASTNode.RECOVERED) != 0);
        List fragments = variableDeclarationExpression.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        //$NON-NLS-1$
        checkSourceRange(fragment, "i", source);
        assertTrue("Not flag as RECOVERED", (fragment.getFlags() & ASTNode.RECOVERED) != 0);
        SimpleName name = fragment.getName();
        //$NON-NLS-1$
        checkSourceRange(name, "i", source);
        assertTrue("Flag as RECOVERED", (name.getFlags() & ASTNode.RECOVERED) == 0);
        Statement statement2 = forStatement.getBody();
        //$NON-NLS-1$
        assertTrue("Not an empty statement", statement2.getNodeType() == ASTNode.EMPTY_STATEMENT);
        EmptyStatement emptyStatement = (EmptyStatement) statement2;
        assertEquals("Wrong start position", fragment.getStartPosition() + fragment.getLength(), emptyStatement.getStartPosition());
        assertEquals("Wrong length", 0, emptyStatement.getLength());
        assertTrue("Not flag as RECOVERED", (emptyStatement.getFlags() & ASTNode.RECOVERED) != 0);
    }

    // check RECOVERED flag (remove tokens)
    public void test0009() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "\n" + "public class X {\n" + "	void foo() {\n" + "	    bar(baz());#\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    bar(baz());\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
        Block block = methodDeclaration.getBody();
        assertTrue("Not flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) != 0);
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;
        //$NON-NLS-1$
        checkSourceRange(expressionStatement, "bar(baz());", source);
        assertTrue("Flag as RECOVERED", (expressionStatement.getFlags() & ASTNode.RECOVERED) == 0);
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation = (MethodInvocation) expression;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation, "bar(baz())", source);
        assertTrue("Flag as RECOVERED", (methodInvocation.getFlags() & ASTNode.RECOVERED) == 0);
        List arguments = methodInvocation.arguments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, arguments.size());
        Expression argument = (Expression) arguments.get(0);
        //$NON-NLS-1$
        assertTrue("Not a method invocation", argument.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation2 = (MethodInvocation) argument;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation2, "baz()", source);
        assertTrue("Flag as RECOVERED", (methodInvocation2.getFlags() & ASTNode.RECOVERED) == 0);
    }

    // check RECOVERED flag (remove tokens)
    public void test0010() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "\n" + "public class X {\n" + "	void foo() {\n" + "	    bar(baz())#;\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    bar(baz());\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
        Block block = methodDeclaration.getBody();
        assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) == 0);
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;
        //$NON-NLS-1$
        checkSourceRange(expressionStatement, "bar(baz())#;", source);
        assertTrue("Not flag as RECOVERED", (expressionStatement.getFlags() & ASTNode.RECOVERED) != 0);
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation = (MethodInvocation) expression;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation, "bar(baz())", source);
        assertTrue("Flag as RECOVERED", (methodInvocation.getFlags() & ASTNode.RECOVERED) == 0);
        List arguments = methodInvocation.arguments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, arguments.size());
        Expression argument = (Expression) arguments.get(0);
        //$NON-NLS-1$
        assertTrue("Not a method invocation", argument.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation2 = (MethodInvocation) argument;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation2, "baz()", source);
        assertTrue("Flag as RECOVERED", (methodInvocation2.getFlags() & ASTNode.RECOVERED) == 0);
    }

    // check RECOVERED flag (remove tokens)
    public void test0011() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "\n" + "public class X {\n" + "	void foo() {\n" + "	    bar(baz()#);\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    bar(baz());\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
        Block block = methodDeclaration.getBody();
        assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) == 0);
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;
        //$NON-NLS-1$
        checkSourceRange(expressionStatement, "bar(baz()#);", source);
        assertTrue("Flag as RECOVERED", (expressionStatement.getFlags() & ASTNode.RECOVERED) == 0);
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation = (MethodInvocation) expression;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation, "bar(baz()#)", source);
        assertTrue("Not flag as RECOVERED", (methodInvocation.getFlags() & ASTNode.RECOVERED) != 0);
        List arguments = methodInvocation.arguments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, arguments.size());
        Expression argument = (Expression) arguments.get(0);
        //$NON-NLS-1$
        assertTrue("Not a method invocation", argument.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation2 = (MethodInvocation) argument;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation2, "baz()", source);
        assertTrue("Flag as RECOVERED", (methodInvocation2.getFlags() & ASTNode.RECOVERED) == 0);
    }

    // check RECOVERED flag (insert tokens)
    public void test0012() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "\n" + "public class X {\n" + "	void foo() {\n" + "	    bar()#\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    bar();\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
        Block block = methodDeclaration.getBody();
        assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) == 0);
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;
        //$NON-NLS-1$
        checkSourceRange(expressionStatement, "bar()#", source);
        assertTrue("Not flag as RECOVERED", (expressionStatement.getFlags() & ASTNode.RECOVERED) != 0);
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation = (MethodInvocation) expression;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation, "bar()", source);
        assertTrue("Flag as RECOVERED", (methodInvocation.getFlags() & ASTNode.RECOVERED) == 0);
    }

    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=129555
    public void test0013() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "\n" + "public class X {\n" + "	void foo() {\n" + "	    a[0]\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    a[0]=$missing$;\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
        Block block = methodDeclaration.getBody();
        assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) != 0);
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;
        //$NON-NLS-1$
        checkSourceRange(expressionStatement, "a[0]", source);
        assertTrue("Not flag as RECOVERED", (expressionStatement.getFlags() & ASTNode.RECOVERED) != 0);
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not an assigment", expression.getNodeType() == ASTNode.ASSIGNMENT);
        Assignment assignment = (Assignment) expression;
        //$NON-NLS-1$
        checkSourceRange(assignment, "a[0]", source);
        assertTrue("Flag as RECOVERED", (assignment.getFlags() & ASTNode.RECOVERED) != 0);
        Expression rhs = assignment.getRightHandSide();
        //$NON-NLS-1$
        assertTrue("Not a simple name", rhs.getNodeType() == ASTNode.SIMPLE_NAME);
        SimpleName simpleName = (SimpleName) rhs;
        //$NON-NLS-1$
        assertEquals("Not length isn't correct", 0, simpleName.getLength());
    }

    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=129909
    public void _test0014() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "\n" + "public class X {\n" + "	void foo() {\n" + "	    int[] = a[0];\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    int[] $missing$=a[0];\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
        Block block = methodDeclaration.getBody();
        assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) == 0);
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not a variable declaration statement", statement.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) statement;
        //$NON-NLS-1$
        checkSourceRange(variableDeclarationStatement, "int[] = a[0];", source);
        assertTrue("Not flag as RECOVERED", (variableDeclarationStatement.getFlags() & ASTNode.RECOVERED) != 0);
        List fragments = variableDeclarationStatement.fragments();
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        SimpleName simpleName = fragment.getName();
        //$NON-NLS-1$
        assertEquals("Not length isn't correct", 0, simpleName.getLength());
    }

    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=143212
    public void test0015() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "\n" + "public class X {\n" + "	void foo() {\n" + "	    assert 0 == 0 : a[0;\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    assert 0 == 0 : a[0];\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
        Block block = methodDeclaration.getBody();
        assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) == 0);
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not an assert statement", statement.getNodeType() == ASTNode.ASSERT_STATEMENT);
        AssertStatement assertStatement = (AssertStatement) statement;
        //$NON-NLS-1$
        checkSourceRange(assertStatement, "assert 0 == 0 : a[0;", source);
        assertTrue("Flag as RECOVERED", (assertStatement.getFlags() & ASTNode.RECOVERED) == 0);
        Expression message = assertStatement.getMessage();
        //$NON-NLS-1$
        assertTrue("No message expression", message != null);
        //$NON-NLS-1$
        checkSourceRange(message, "a[0", source);
        assertTrue("Not flag as RECOVERED", (message.getFlags() & ASTNode.RECOVERED) != 0);
    }

    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=143212
    public void test0016() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "\n" + "public class X {\n" + "	void foo() {\n" + "	    assert 0 == 0 : foo(;\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    assert 0 == 0 : foo();\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
        Block block = methodDeclaration.getBody();
        assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) == 0);
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not an assert statement", statement.getNodeType() == ASTNode.ASSERT_STATEMENT);
        AssertStatement assertStatement = (AssertStatement) statement;
        //$NON-NLS-1$
        checkSourceRange(assertStatement, "assert 0 == 0 : foo(;", source);
        assertTrue("Flag as RECOVERED", (assertStatement.getFlags() & ASTNode.RECOVERED) == 0);
        Expression message = assertStatement.getMessage();
        //$NON-NLS-1$
        assertTrue("No message expression", message != null);
        //$NON-NLS-1$
        checkSourceRange(message, "foo(", source);
        assertTrue("Not flag as RECOVERED", (message.getFlags() & ASTNode.RECOVERED) != 0);
    }

    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=143212
    public void test0017() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "\n" + "public class X {\n" + "	void foo() {\n" + "	    assert 0 == 0 : (\"aa\";\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    assert 0 == 0 : (\"aa\");\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
        Block block = methodDeclaration.getBody();
        assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) == 0);
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not an assert statement", statement.getNodeType() == ASTNode.ASSERT_STATEMENT);
        AssertStatement assertStatement = (AssertStatement) statement;
        //$NON-NLS-1$
        checkSourceRange(assertStatement, "assert 0 == 0 : (\"aa\";", source);
        assertTrue("Flag as RECOVERED", (assertStatement.getFlags() & ASTNode.RECOVERED) == 0);
        Expression message = assertStatement.getMessage();
        //$NON-NLS-1$
        assertTrue("No message expression", message != null);
        //$NON-NLS-1$
        checkSourceRange(message, "(\"aa\"", source);
        assertTrue("Not flag as RECOVERED", (message.getFlags() & ASTNode.RECOVERED) != 0);
    }

    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=239117
    public void test0018() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[0];
        ASTResult result = this.buildMarkedAST("/Converter/src/p/X.java", "package p;\n" + "public class X {\n" + "	void m(Object var) {\n" + "		if (1==1 && var.equals(1)[*1*][*1*] {\n" + "		}\n" + "	}\n" + "}");
        assertASTResult("===== AST =====\n" + "package p;\n" + "public class X {\n" + "  void m(  Object var){\n" + "    if (1 == 1 && var.equals(1))     [*1*];[*1*]\n" + "  }\n" + "}\n" + "\n" + "===== Details =====\n" + "1:EMPTY_STATEMENT,[77,0],,RECOVERED,[N/A]\n" + "===== Problems =====\n" + "1. WARNING in /Converter/src/p/X.java (at line 4)\n" + "	if (1==1 && var.equals(1) {\n" + "	    ^^^^\n" + "Comparing identical expressions\n" + "2. ERROR in /Converter/src/p/X.java (at line 4)\n" + "	if (1==1 && var.equals(1) {\n" + "	                ^^^^^^\n" + "The method equals(Object) in the type Object is not applicable for the arguments (int)\n" + "3. ERROR in /Converter/src/p/X.java (at line 4)\n" + "	if (1==1 && var.equals(1) {\n" + "	                        ^\n" + "Syntax error, insert \") Statement\" to complete BlockStatements\n", result);
    }

    //https://bugs.eclipse.org/bugs/show_bug.cgi?id=329998
    public void test0019() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "public class X {\n" + "	void foo() {\n" + "		return new Object() {hash};\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    return new Object(){\n" + "    }\n" + ";\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        Block block = methodDeclaration.getBody();
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not a return statement", statement.getNodeType() == ASTNode.RETURN_STATEMENT);
        ReturnStatement returnStatement = (ReturnStatement) statement;
        //$NON-NLS-1$
        checkSourceRange(returnStatement, "return new Object() {hash};", source);
        Expression expression = returnStatement.getExpression();
        //$NON-NLS-1$
        checkSourceRange(expression, "new Object() {hash}", source);
    }

    //https://bugs.eclipse.org/bugs/show_bug.cgi?id=329998
    public void test0020() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "public class X {\n" + "	void foo() {\n" + "		field= new Object() {hash};\n" + "	}\n" + "}\n");
        char[] source = this.workingCopies[0].getSource().toCharArray();
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "    field=new Object(){\n" + "    }\n" + ";\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        Block block = methodDeclaration.getBody();
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;
        //$NON-NLS-1$
        checkSourceRange(expressionStatement, "field= new Object() {hash};", source);
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not an assignment", expression.getNodeType() == ASTNode.ASSIGNMENT);
        Assignment assignment = (Assignment) expression;
        Expression anonymousClassDeclaration = assignment.getRightHandSide();
        //$NON-NLS-1$
        checkSourceRange(anonymousClassDeclaration, "new Object() {hash}", source);
        //$NON-NLS-1$
        checkSourceRange(assignment, "field= new Object() {hash}", source);
    }

    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=340691
    public void test0021() throws JavaModelException {
        this.workingCopies = new ICompilationUnit[1];
        this.workingCopies[0] = getWorkingCopy("/Converter/src/test/X.java", "package test;\n" + "public class X {\n" + "	void foo() {\n" + "		synchronized new Object();\n" + "	}\n" + "}\n");
        ASTNode result = runConversion(getJLS3(), this.workingCopies[0], true, true);
        assertASTNodeEquals("package test;\n" + "public class X {\n" + "  void foo(){\n" + "  }\n" + "}\n", result);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        Block block = methodDeclaration.getBody();
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 0, statements.size());
    }
}
