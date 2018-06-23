/*******************************************************************************
 * Copyright (c) 2013, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.tests.dom;

import java.util.*;
import junit.framework.Test;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.jdom.*;
import org.eclipse.jdt.core.util.IModifierConstants;

@SuppressWarnings({ "rawtypes" })
public class ASTConverterAST4Test extends ConverterTestSetup {

    public void setUpSuite() throws Exception {
        super.setUpSuite();
        this.ast = AST.newAST(getJLS4());
    }

    public  ASTConverterAST4Test(String name) {
        super(name);
    }

    static {
    //		TESTS_NUMBERS = new int[] { 356 };
    }

    public static Test suite() {
        return buildModelTestSuite(ASTConverterAST4Test.class);
    }

    /** 
	 * Internal access method to VariableDeclarationFragment#setExtraDimensions() for avoiding deprecated warnings.
	 *
	 * @param node
	 * @param dimensions
	 * @deprecated
	 */
    private void internalSetExtraDimensions(VariableDeclarationFragment node, int dimensions) {
        node.setExtraDimensions(dimensions);
    }

    /** 
	 * Internal access method to MethodDeclaration#thrownExceptions() for avoiding deprecated warnings.
	 * @deprecated
	 */
    private List internalThrownExceptions(MethodDeclaration methodDeclaration) {
        return methodDeclaration.thrownExceptions();
    }

    /**
	 * @deprecated
	 */
    private Type componentType(ArrayType array) {
        return array.getComponentType();
    }

    public void test0001() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0001", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        // check that we have the right tree
        CompilationUnit unit = this.ast.newCompilationUnit();
        PackageDeclaration packageDeclaration = this.ast.newPackageDeclaration();
        //$NON-NLS-1$
        packageDeclaration.setName(this.ast.newSimpleName("test0001"));
        unit.setPackage(packageDeclaration);
        ImportDeclaration importDeclaration = this.ast.newImportDeclaration();
        QualifiedName name = this.ast.newQualifiedName(//$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "java"), //$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "util"));
        importDeclaration.setName(name);
        importDeclaration.setOnDemand(true);
        unit.imports().add(importDeclaration);
        TypeDeclaration type = this.ast.newTypeDeclaration();
        type.setInterface(false);
        type.modifiers().add(this.ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
        //$NON-NLS-1$
        type.setName(this.ast.newSimpleName("Test"));
        MethodDeclaration methodDeclaration = this.ast.newMethodDeclaration();
        methodDeclaration.setConstructor(false);
        methodDeclaration.modifiers().add(this.ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
        methodDeclaration.modifiers().add(this.ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
        //$NON-NLS-1$
        methodDeclaration.setName(this.ast.newSimpleName("main"));
        methodDeclaration.setReturnType2(this.ast.newPrimitiveType(PrimitiveType.VOID));
        SingleVariableDeclaration variableDeclaration = this.ast.newSingleVariableDeclaration();
        //$NON-NLS-1$
        variableDeclaration.setType(this.ast.newArrayType(this.ast.newSimpleType(this.ast.newSimpleName("String"))));
        //$NON-NLS-1$
        variableDeclaration.setName(this.ast.newSimpleName("args"));
        methodDeclaration.parameters().add(variableDeclaration);
        org.eclipse.jdt.core.dom.Block block = this.ast.newBlock();
        MethodInvocation methodInvocation = this.ast.newMethodInvocation();
        name = this.ast.newQualifiedName(//$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "System"), //$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "out"));
        methodInvocation.setExpression(name);
        //$NON-NLS-1$
        methodInvocation.setName(this.ast.newSimpleName("println"));
        InfixExpression infixExpression = this.ast.newInfixExpression();
        infixExpression.setOperator(InfixExpression.Operator.PLUS);
        StringLiteral literal = this.ast.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue("Hello");
        infixExpression.setLeftOperand(literal);
        literal = this.ast.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue(" world");
        //$NON-NLS-1$
        infixExpression.setRightOperand(literal);
        methodInvocation.arguments().add(infixExpression);
        ExpressionStatement expressionStatement = this.ast.newExpressionStatement(methodInvocation);
        block.statements().add(expressionStatement);
        methodDeclaration.setBody(block);
        type.bodyDeclarations().add(methodDeclaration);
        unit.types().add(type);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", result.subtreeMatch(new ASTMatcher(), unit));
        String expected = "package test0001;\n" + "import java.util.*;\n" + "public class Test {\n" + "	public static void main(String[] args) {\n" + "		System.out.println(\"Hello\" + \" world\");\n" + "	}\n" + "}";
        checkSourceRange(result, expected, source);
    }

    /**
	 * Test allocation expression: new Object() ==> ClassInstanceCreation
	 */
    public void test0002() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0002", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        ClassInstanceCreation classInstanceCreation = this.ast.newClassInstanceCreation();
        //$NON-NLS-1$
        classInstanceCreation.setType(this.ast.newSimpleType(this.ast.newSimpleName("Object")));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", classInstanceCreation.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "new Object()", source);
    }

    /**
	 * Test allocation expression: new java.lang.Object() ==> ClassInstanceCreation
	 */
    public void test0003() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0003", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        ClassInstanceCreation classInstanceCreation = this.ast.newClassInstanceCreation();
        QualifiedName name = this.ast.newQualifiedName(this.ast.newQualifiedName(//$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "java"), //$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "lang")), //$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "Object"));
        classInstanceCreation.setType(this.ast.newSimpleType(name));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", classInstanceCreation.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "new java.lang.Object()", source);
    }

    /**
	 * Test allocation expression: new java.lang.Exception("ERROR") ==> ClassInstanceCreation
	 */
    public void test0004() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0004", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        ClassInstanceCreation classInstanceCreation = this.ast.newClassInstanceCreation();
        QualifiedName name = this.ast.newQualifiedName(this.ast.newQualifiedName(//$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "java"), //$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "lang")), //$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "Exception"));
        classInstanceCreation.setType(this.ast.newSimpleType(name));
        StringLiteral literal = this.ast.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue("ERROR");
        classInstanceCreation.arguments().add(literal);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", classInstanceCreation.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "new java.lang.Exception(\"ERROR\")", source);
    }

    /**
	 * Test allocation expression: new java.lang.Object() {} ==> ClassInstanceCreation
	 */
    public void test0005() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0005", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        ClassInstanceCreation classInstanceCreation = this.ast.newClassInstanceCreation();
        QualifiedName name = this.ast.newQualifiedName(this.ast.newQualifiedName(//$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "java"), //$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "lang")), //$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "Object"));
        classInstanceCreation.setType(this.ast.newSimpleType(name));
        AnonymousClassDeclaration anonymousClassDeclaration = this.ast.newAnonymousClassDeclaration();
        classInstanceCreation.setAnonymousClassDeclaration(anonymousClassDeclaration);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", classInstanceCreation.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "new java.lang.Object() {}", source);
        ClassInstanceCreation classInstanceCreation2 = (ClassInstanceCreation) expression;
        Type type = classInstanceCreation2.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "java.lang.Object", source);
    }

    /**
	 * Test allocation expression: new java.lang.Runnable() { public void run() {}} ==> ClassInstanceCreation
	 */
    public void test0006() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0006", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        ClassInstanceCreation classInstanceCreation = this.ast.newClassInstanceCreation();
        QualifiedName name = this.ast.newQualifiedName(this.ast.newQualifiedName(//$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "java"), //$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "lang")), //$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "Runnable"));
        classInstanceCreation.setType(this.ast.newSimpleType(name));
        MethodDeclaration methodDeclaration = this.ast.newMethodDeclaration();
        methodDeclaration.setBody(this.ast.newBlock());
        methodDeclaration.setConstructor(false);
        methodDeclaration.modifiers().add(this.ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
        //$NON-NLS-1$
        methodDeclaration.setName(this.ast.newSimpleName("run"));
        methodDeclaration.setReturnType2(this.ast.newPrimitiveType(PrimitiveType.VOID));
        AnonymousClassDeclaration anonymousClassDeclaration = this.ast.newAnonymousClassDeclaration();
        anonymousClassDeclaration.bodyDeclarations().add(methodDeclaration);
        classInstanceCreation.setAnonymousClassDeclaration(anonymousClassDeclaration);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", classInstanceCreation.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "new java.lang.Runnable() { public void run() {}}", source);
    }

    /**
	 * Test allocation expression: new Test().new D() ==> ClassInstanceCreation
	 */
    public void test0007() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0007", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not an ExpressionStatement", node instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        ASTNode expression = (ASTNode) ((MethodInvocation) expressionStatement.getExpression()).arguments().get(0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        ClassInstanceCreation classInstanceCreation = this.ast.newClassInstanceCreation();
        //$NON-NLS-1$
        classInstanceCreation.setType(this.ast.newSimpleType(this.ast.newSimpleName("D")));
        ClassInstanceCreation classInstanceCreationExpression = this.ast.newClassInstanceCreation();
        //$NON-NLS-1$
        classInstanceCreationExpression.setType(this.ast.newSimpleType(this.ast.newSimpleName("Test")));
        classInstanceCreation.setExpression(classInstanceCreationExpression);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", classInstanceCreation.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "new Test().new D()", source);
    }

    /**
	 * Test allocation expression: new int[] {1, 2, 3, 4} ==> ArrayCreation
	 */
    public void test0008() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0008", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        ArrayCreation arrayCreation = this.ast.newArrayCreation();
        arrayCreation.setType(this.ast.newArrayType(this.ast.newPrimitiveType(PrimitiveType.INT), 1));
        ArrayInitializer arrayInitializer = this.ast.newArrayInitializer();
        //$NON-NLS-1$
        arrayInitializer.expressions().add(this.ast.newNumberLiteral("1"));
        //$NON-NLS-1$
        arrayInitializer.expressions().add(this.ast.newNumberLiteral("2"));
        //$NON-NLS-1$
        arrayInitializer.expressions().add(this.ast.newNumberLiteral("3"));
        //$NON-NLS-1$
        arrayInitializer.expressions().add(this.ast.newNumberLiteral("4"));
        arrayCreation.setInitializer(arrayInitializer);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", arrayCreation.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "new int[] {1, 2, 3, 4}", source);
    }

    /**
	 * Test allocation expression: new int[][] {{1}, {2}} ==> ArrayCreation
	 */
    public void test0009() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0009", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        ArrayCreation arrayCreation = this.ast.newArrayCreation();
        arrayCreation.setType(this.ast.newArrayType(this.ast.newPrimitiveType(PrimitiveType.INT), 2));
        ArrayInitializer arrayInitializer = this.ast.newArrayInitializer();
        ArrayInitializer innerArrayInitializer = this.ast.newArrayInitializer();
        //$NON-NLS-1$
        innerArrayInitializer.expressions().add(this.ast.newNumberLiteral("1"));
        arrayInitializer.expressions().add(innerArrayInitializer);
        innerArrayInitializer = this.ast.newArrayInitializer();
        //$NON-NLS-1$
        innerArrayInitializer.expressions().add(this.ast.newNumberLiteral("2"));
        arrayInitializer.expressions().add(innerArrayInitializer);
        arrayCreation.setInitializer(arrayInitializer);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", arrayCreation.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "new int[][] {{1}, {2}}", source);
    }

    /**
	 * Test allocation expression: new int[3] ==> ArrayCreation
	 */
    public void test0010() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0010", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        ArrayCreation arrayCreation = this.ast.newArrayCreation();
        arrayCreation.setType(this.ast.newArrayType(this.ast.newPrimitiveType(PrimitiveType.INT), 1));
        //$NON-NLS-1$
        arrayCreation.dimensions().add(this.ast.newNumberLiteral("3"));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", arrayCreation.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "new int[3]", source);
    }

    /**
	 * Test allocation expression: new int[3][] ==> ArrayCreation
	 */
    public void test0011() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0011", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        ArrayCreation arrayCreation = this.ast.newArrayCreation();
        arrayCreation.setType(this.ast.newArrayType(this.ast.newPrimitiveType(PrimitiveType.INT), 2));
        //$NON-NLS-1$
        arrayCreation.dimensions().add(this.ast.newNumberLiteral("3"));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", arrayCreation.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "new int[3][]", source);
    }

    /**
	 * Test allocation expression: new int[][] {{},{}} ==> ArrayCreation
	 */
    public void test0012() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0012", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        ArrayCreation arrayCreation = this.ast.newArrayCreation();
        arrayCreation.setType(this.ast.newArrayType(this.ast.newPrimitiveType(PrimitiveType.INT), 2));
        ArrayInitializer arrayInitializer = this.ast.newArrayInitializer();
        ArrayInitializer innerArrayInitializer = this.ast.newArrayInitializer();
        arrayInitializer.expressions().add(innerArrayInitializer);
        innerArrayInitializer = this.ast.newArrayInitializer();
        arrayInitializer.expressions().add(innerArrayInitializer);
        arrayCreation.setInitializer(arrayInitializer);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", arrayCreation.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "new int[][] {{}, {}}", source);
    }

    /**
	 * int i; ==> VariableDeclarationFragment
	 */
    public void test0013() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0013", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("i"));
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int i;", source);
    }

    /**
	 * int i = 0; ==> VariableDeclarationFragment
	 */
    public void test0014() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0014", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        variableDeclarationFragment.setInitializer(this.ast.newNumberLiteral("0"));
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int i = 0;", source);
    }

    /**
	 * i = 1; ==> ExpressionStatement(Assignment)
	 */
    public void test0015() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0015", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        Assignment assignment = this.ast.newAssignment();
        //$NON-NLS-1$
        assignment.setLeftHandSide(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        assignment.setRightHandSide(this.ast.newNumberLiteral("1"));
        assignment.setOperator(Assignment.Operator.ASSIGN);
        ExpressionStatement statement = this.ast.newExpressionStatement(assignment);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "i = 1;", source);
    }

    /**
	 * i += 2; ==> ExpressionStatement(Assignment)
	 */
    public void test0016() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0016", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        Assignment assignment = this.ast.newAssignment();
        //$NON-NLS-1$
        assignment.setLeftHandSide(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        assignment.setRightHandSide(this.ast.newNumberLiteral("2"));
        assignment.setOperator(Assignment.Operator.PLUS_ASSIGN);
        ExpressionStatement statement = this.ast.newExpressionStatement(assignment);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "i += 2;", source);
    }

    /**
	 * i -= 2; ==> ExpressionStatement(Assignment)
	 */
    public void test0017() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0017", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        Assignment assignment = this.ast.newAssignment();
        //$NON-NLS-1$
        assignment.setLeftHandSide(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        assignment.setRightHandSide(this.ast.newNumberLiteral("2"));
        assignment.setOperator(Assignment.Operator.MINUS_ASSIGN);
        ExpressionStatement statement = this.ast.newExpressionStatement(assignment);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "i -= 2;", source);
    }

    /**
	 * i *= 2; ==> ExpressionStatement(Assignment)
	 */
    public void test0018() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0018", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        Assignment assignment = this.ast.newAssignment();
        //$NON-NLS-1$
        assignment.setLeftHandSide(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        assignment.setRightHandSide(this.ast.newNumberLiteral("2"));
        assignment.setOperator(Assignment.Operator.TIMES_ASSIGN);
        ExpressionStatement statement = this.ast.newExpressionStatement(assignment);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "i *= 2;", source);
    }

    /**
	 * i /= 2; ==> ExpressionStatement(Assignment)
	 */
    public void test0019() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0019", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        Assignment assignment = this.ast.newAssignment();
        //$NON-NLS-1$
        assignment.setLeftHandSide(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        assignment.setRightHandSide(this.ast.newNumberLiteral("2"));
        assignment.setOperator(Assignment.Operator.DIVIDE_ASSIGN);
        ExpressionStatement statement = this.ast.newExpressionStatement(assignment);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "i /= 2;", source);
    }

    /**
	 * i &= 2 ==> ExpressionStatement(Assignment)
	 */
    public void test0020() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0020", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        Assignment assignment = this.ast.newAssignment();
        //$NON-NLS-1$
        assignment.setLeftHandSide(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        assignment.setRightHandSide(this.ast.newNumberLiteral("2"));
        assignment.setOperator(Assignment.Operator.BIT_AND_ASSIGN);
        ExpressionStatement statement = this.ast.newExpressionStatement(assignment);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "i &= 2;", source);
    }

    /**
	 * i |= 2; ==> ExpressionStatement(Assignment)
	 */
    public void test0021() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0021", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        Assignment assignment = this.ast.newAssignment();
        //$NON-NLS-1$
        assignment.setLeftHandSide(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        assignment.setRightHandSide(this.ast.newNumberLiteral("2"));
        assignment.setOperator(Assignment.Operator.BIT_OR_ASSIGN);
        ExpressionStatement statement = this.ast.newExpressionStatement(assignment);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "i |= 2;", source);
    }

    /**
	 * i ^= 2; ==> ExpressionStatement(Assignment)
	 */
    public void test0022() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0022", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        Assignment assignment = this.ast.newAssignment();
        //$NON-NLS-1$
        assignment.setLeftHandSide(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        assignment.setRightHandSide(this.ast.newNumberLiteral("2"));
        assignment.setOperator(Assignment.Operator.BIT_XOR_ASSIGN);
        ExpressionStatement statement = this.ast.newExpressionStatement(assignment);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "i ^= 2;", source);
    }

    /**
	 * i %= 2; ==> ExpressionStatement(Assignment)
	 */
    public void test0023() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0023", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        Assignment assignment = this.ast.newAssignment();
        //$NON-NLS-1$
        assignment.setLeftHandSide(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        assignment.setRightHandSide(this.ast.newNumberLiteral("2"));
        assignment.setOperator(Assignment.Operator.REMAINDER_ASSIGN);
        ExpressionStatement statement = this.ast.newExpressionStatement(assignment);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "i %= 2;", source);
    }

    /**
	 * i <<= 2; ==> ExpressionStatement(Assignment)
	 */
    public void test0024() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0024", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        Assignment assignment = this.ast.newAssignment();
        //$NON-NLS-1$
        assignment.setLeftHandSide(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        assignment.setRightHandSide(this.ast.newNumberLiteral("2"));
        assignment.setOperator(Assignment.Operator.LEFT_SHIFT_ASSIGN);
        ExpressionStatement statement = this.ast.newExpressionStatement(assignment);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "i <<= 2;", source);
    }

    /**
	 * i >>= 2; ==> ExpressionStatement(Assignment)
	 */
    public void test0025() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0025", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        Assignment assignment = this.ast.newAssignment();
        //$NON-NLS-1$
        assignment.setLeftHandSide(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        assignment.setRightHandSide(this.ast.newNumberLiteral("2"));
        assignment.setOperator(Assignment.Operator.RIGHT_SHIFT_SIGNED_ASSIGN);
        ExpressionStatement statement = this.ast.newExpressionStatement(assignment);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "i >>= 2;", source);
    }

    /**
	 * i >>>= 2; ==> ExpressionStatement(Assignment)
	 */
    public void test0026() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0026", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        Assignment assignment = this.ast.newAssignment();
        //$NON-NLS-1$
        assignment.setLeftHandSide(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        assignment.setRightHandSide(this.ast.newNumberLiteral("2"));
        assignment.setOperator(Assignment.Operator.RIGHT_SHIFT_UNSIGNED_ASSIGN);
        ExpressionStatement statement = this.ast.newExpressionStatement(assignment);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "i >>>= 2;", source);
    }

    /**
	 * --i; ==> ExpressionStatement(PrefixExpression)
	 */
    public void test0027() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0027", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        PrefixExpression prefixExpression = this.ast.newPrefixExpression();
        //$NON-NLS-1$
        prefixExpression.setOperand(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        prefixExpression.setOperator(PrefixExpression.Operator.DECREMENT);
        ExpressionStatement statement = this.ast.newExpressionStatement(prefixExpression);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "--i;", source);
    }

    /**
	 * --i; ==> ExpressionStatement(PrefixExpression)
	 */
    public void test0028() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0028", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        PrefixExpression prefixExpression = this.ast.newPrefixExpression();
        //$NON-NLS-1$
        prefixExpression.setOperand(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        prefixExpression.setOperator(PrefixExpression.Operator.INCREMENT);
        ExpressionStatement statement = this.ast.newExpressionStatement(prefixExpression);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "++i;", source);
    }

    /**
	 * i--; ==> ExpressionStatement(PostfixExpression)
	 */
    public void test0029() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0029", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        PostfixExpression postfixExpression = this.ast.newPostfixExpression();
        //$NON-NLS-1$
        postfixExpression.setOperand(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        postfixExpression.setOperator(PostfixExpression.Operator.DECREMENT);
        ExpressionStatement statement = this.ast.newExpressionStatement(postfixExpression);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "i--;", source);
    }

    /**
	 * i++; ==> ExpressionStatement(PostfixExpression)
	 */
    public void test0030() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0030", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        PostfixExpression postfixExpression = this.ast.newPostfixExpression();
        //$NON-NLS-1$
        postfixExpression.setOperand(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        postfixExpression.setOperator(PostfixExpression.Operator.INCREMENT);
        ExpressionStatement statement = this.ast.newExpressionStatement(postfixExpression);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "i++;", source);
    }

    /**
	 * (String) o; ==> ExpressionStatement(CastExpression)
	 */
    public void test0031() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0031", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("s"));
        CastExpression castExpression = this.ast.newCastExpression();
        //$NON-NLS-1$
        castExpression.setExpression(this.ast.newSimpleName("o"));
        //$NON-NLS-1$
        castExpression.setType(this.ast.newSimpleType(this.ast.newSimpleName("String")));
        variableDeclarationFragment.setInitializer(castExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        //$NON-NLS-1$
        statement.setType(this.ast.newSimpleType(this.ast.newSimpleName("String")));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "String s = (String) o;", source);
    }

    /**
	 * (int) d; ==> ExpressionStatement(CastExpression)
	 */
    public void test0032() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0032", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("i"));
        CastExpression castExpression = this.ast.newCastExpression();
        //$NON-NLS-1$
        castExpression.setExpression(this.ast.newSimpleName("d"));
        //$NON-NLS-1$
        castExpression.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        variableDeclarationFragment.setInitializer(castExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        //$NON-NLS-1$
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int i = (int) d;", source);
    }

    /**
	 * (float) d; ==> ExpressionStatement(CastExpression)
	 */
    public void test0033() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0033", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("f"));
        CastExpression castExpression = this.ast.newCastExpression();
        //$NON-NLS-1$
        castExpression.setExpression(this.ast.newSimpleName("d"));
        //$NON-NLS-1$
        castExpression.setType(this.ast.newPrimitiveType(PrimitiveType.FLOAT));
        variableDeclarationFragment.setInitializer(castExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        //$NON-NLS-1$
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.FLOAT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "float f = (float) d;", source);
    }

    /**
	 * (byte) d; ==> ExpressionStatement(CastExpression)
	 */
    public void test0034() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0034", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("b"));
        CastExpression castExpression = this.ast.newCastExpression();
        //$NON-NLS-1$
        castExpression.setExpression(this.ast.newSimpleName("d"));
        //$NON-NLS-1$
        castExpression.setType(this.ast.newPrimitiveType(PrimitiveType.BYTE));
        variableDeclarationFragment.setInitializer(castExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        //$NON-NLS-1$
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.BYTE));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "byte b = (byte) d;", source);
    }

    /**
	 * (short) d; ==> ExpressionStatement(CastExpression)
	 */
    public void test0035() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0035", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("s"));
        CastExpression castExpression = this.ast.newCastExpression();
        //$NON-NLS-1$
        castExpression.setExpression(this.ast.newSimpleName("d"));
        //$NON-NLS-1$
        castExpression.setType(this.ast.newPrimitiveType(PrimitiveType.SHORT));
        variableDeclarationFragment.setInitializer(castExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        //$NON-NLS-1$
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.SHORT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "short s = (short) d;", source);
    }

    /**
	 * (long) d; ==> ExpressionStatement(CastExpression)
	 */
    public void test0036() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0036", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("l"));
        CastExpression castExpression = this.ast.newCastExpression();
        //$NON-NLS-1$
        castExpression.setExpression(this.ast.newSimpleName("d"));
        //$NON-NLS-1$
        castExpression.setType(this.ast.newPrimitiveType(PrimitiveType.LONG));
        variableDeclarationFragment.setInitializer(castExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        //$NON-NLS-1$
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.LONG));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "long l = (long) d;", source);
    }

    /**
	 * (char) i; ==> ExpressionStatement(CastExpression)
	 */
    public void test0037() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0037", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("c"));
        CastExpression castExpression = this.ast.newCastExpression();
        //$NON-NLS-1$
        castExpression.setExpression(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        castExpression.setType(this.ast.newPrimitiveType(PrimitiveType.CHAR));
        variableDeclarationFragment.setInitializer(castExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        //$NON-NLS-1$
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.CHAR));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "char c = (char) i;", source);
    }

    /**
	 * int.class; ==> ExpressionStatement(TypeLiteral)
	 */
    public void test0038() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0038", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("c"));
        TypeLiteral typeLiteral = this.ast.newTypeLiteral();
        typeLiteral.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        variableDeclarationFragment.setInitializer(typeLiteral);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        //$NON-NLS-1$
        statement.setType(this.ast.newSimpleType(this.ast.newSimpleName("Class")));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(((VariableDeclarationFragment) ((VariableDeclarationStatement) node).fragments().get(0)).getInitializer(), "int.class", source);
    }

    /**
	 * void.class; ==> ExpressionStatement(TypeLiteral)
	 */
    public void test0039() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0039", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("c"));
        TypeLiteral typeLiteral = this.ast.newTypeLiteral();
        typeLiteral.setType(this.ast.newPrimitiveType(PrimitiveType.VOID));
        variableDeclarationFragment.setInitializer(typeLiteral);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        //$NON-NLS-1$
        statement.setType(this.ast.newSimpleType(this.ast.newSimpleName("Class")));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(((VariableDeclarationFragment) ((VariableDeclarationStatement) node).fragments().get(0)).getInitializer(), "void.class", source);
    }

    /**
	 * double.class; ==> ExpressionStatement(TypeLiteral)
	 */
    public void test0040() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0040", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("c"));
        TypeLiteral typeLiteral = this.ast.newTypeLiteral();
        typeLiteral.setType(this.ast.newPrimitiveType(PrimitiveType.DOUBLE));
        variableDeclarationFragment.setInitializer(typeLiteral);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        //$NON-NLS-1$
        statement.setType(this.ast.newSimpleType(this.ast.newSimpleName("Class")));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(((VariableDeclarationFragment) ((VariableDeclarationStatement) node).fragments().get(0)).getInitializer(), "double.class", source);
    }

    /**
	 * long.class; ==> ExpressionStatement(TypeLiteral)
	 */
    public void test0041() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0041", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("c"));
        TypeLiteral typeLiteral = this.ast.newTypeLiteral();
        typeLiteral.setType(this.ast.newPrimitiveType(PrimitiveType.LONG));
        variableDeclarationFragment.setInitializer(typeLiteral);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        //$NON-NLS-1$
        statement.setType(this.ast.newSimpleType(this.ast.newSimpleName("Class")));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(((VariableDeclarationFragment) ((VariableDeclarationStatement) node).fragments().get(0)).getInitializer(), "long.class", source);
    }

    /**
	 * false ==> BooleanLiteral
	 */
    public void test0042() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0042", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        BooleanLiteral literal = this.ast.newBooleanLiteral(false);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", literal.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "false", source);
    }

    /**
	 * true ==> BooleanLiteral
	 */
    public void test0043() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0043", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        BooleanLiteral literal = this.ast.newBooleanLiteral(true);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", literal.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "true", source);
    }

    /**
	 * null ==> NullLiteral
	 */
    public void test0044() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0044", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        NullLiteral literal = this.ast.newNullLiteral();
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", literal.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "null", source);
    }

    /**
	 * CharLiteral ==> CharacterLiteral
	 */
    public void test0045() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0045", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        CharacterLiteral literal = this.ast.newCharacterLiteral();
        //$NON-NLS-1$
        literal.setEscapedValue("'c'");
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", literal.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "'c'", source);
    }

    /**
	 * DoubleLiteral ==> NumberLiteral
	 */
    public void test0046() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0046", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        //$NON-NLS-1$
        NumberLiteral literal = this.ast.newNumberLiteral("1.00001");
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", literal.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "1.00001", source);
    }

    /**
	 * FloatLiteral ==> NumberLiteral
	 */
    public void test0047() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0047", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        //$NON-NLS-1$
        NumberLiteral literal = this.ast.newNumberLiteral("1.00001f");
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", literal.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "1.00001f", source);
    }

    /**
	 * IntLiteral ==> NumberLiteral
	 */
    public void test0048() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0048", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        //$NON-NLS-1$
        NumberLiteral literal = this.ast.newNumberLiteral("30000");
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", literal.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "30000", source);
    }

    /**
	 * IntLiteralMinValue ==> NumberLiteral
	 */
    public void test0049() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0049", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        //$NON-NLS-1$
        NumberLiteral literal = this.ast.newNumberLiteral("-2147483648");
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", literal.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "-2147483648", source);
    }

    /**
	 * LongLiteral ==> NumberLiteral
	 */
    public void test0050() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0050", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        //$NON-NLS-1$
        NumberLiteral literal = this.ast.newNumberLiteral("2147483648L");
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", literal.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "2147483648L", source);
    }

    /**
	 * LongLiteral ==> NumberLiteral (negative value)
	 */
    public void test0051() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0051", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        //$NON-NLS-1$
        NumberLiteral literal = this.ast.newNumberLiteral("2147483648L");
        PrefixExpression prefixExpression = this.ast.newPrefixExpression();
        prefixExpression.setOperand(literal);
        prefixExpression.setOperator(PrefixExpression.Operator.MINUS);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", prefixExpression.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "-2147483648L", source);
    }

    /**
	 * LongLiteralMinValue ==> NumberLiteral
	 */
    public void test0052() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0052", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        //$NON-NLS-1$
        NumberLiteral literal = this.ast.newNumberLiteral("-9223372036854775808L");
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", literal.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "-9223372036854775808L", source);
    }

    /**
	 * ExtendedStringLiteral ==> StringLiteral
	 */
    public void test0053() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0053", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        /*
		StringLiteral literal = this.ast.newStringLiteral();//$NON-NLS-1$
		literal.setLiteralValue("Hello World");*/
        InfixExpression infixExpression = this.ast.newInfixExpression();
        infixExpression.setOperator(InfixExpression.Operator.PLUS);
        StringLiteral literal = this.ast.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue("Hello");
        infixExpression.setLeftOperand(literal);
        literal = this.ast.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue(" World");
        //$NON-NLS-1$
        infixExpression.setRightOperand(literal);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", infixExpression.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "\"Hello\" + \" World\"", source);
    }

    /**
	 * AND_AND_Expression ==> InfixExpression
	 */
    public void test0054() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0054", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 2);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("b3"));
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("b"));
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newSimpleName("b2"));
        infixExpression.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
        variableDeclarationFragment.setInitializer(infixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.BOOLEAN));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "boolean b3 = b && b2;", source);
    }

    /**
	 * OR_OR_Expression ==> InfixExpression
	 */
    public void test0055() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0055", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 2);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("b3"));
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("b"));
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newSimpleName("b2"));
        infixExpression.setOperator(InfixExpression.Operator.CONDITIONAL_OR);
        variableDeclarationFragment.setInitializer(infixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.BOOLEAN));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "boolean b3 = b || b2;", source);
    }

    /**
	 * EqualExpression ==> InfixExpression
	 */
    public void test0056() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0056", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 2);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("b3"));
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("b"));
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newSimpleName("b2"));
        infixExpression.setOperator(InfixExpression.Operator.EQUALS);
        variableDeclarationFragment.setInitializer(infixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.BOOLEAN));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "boolean b3 = b == b2;", source);
    }

    /**
	 * BinaryExpression (+) ==> InfixExpression
	 */
    public void test0057() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0057", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 2);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("n"));
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newSimpleName("j"));
        infixExpression.setOperator(InfixExpression.Operator.PLUS);
        variableDeclarationFragment.setInitializer(infixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int n = i + j;", source);
    }

    /**
	 * BinaryExpression (-) ==> InfixExpression
	 */
    public void test0058() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0058", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 2);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("n"));
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newSimpleName("j"));
        infixExpression.setOperator(InfixExpression.Operator.MINUS);
        variableDeclarationFragment.setInitializer(infixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int n = i - j;", source);
    }

    /**
	 * BinaryExpression (*) ==> InfixExpression
	 */
    public void test0059() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0059", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 2);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("n"));
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newSimpleName("j"));
        infixExpression.setOperator(InfixExpression.Operator.TIMES);
        variableDeclarationFragment.setInitializer(infixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int n = i * j;", source);
    }

    /**
	 * BinaryExpression (/) ==> InfixExpression
	 */
    public void test0060() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0060", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 2);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("n"));
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newSimpleName("j"));
        infixExpression.setOperator(InfixExpression.Operator.DIVIDE);
        variableDeclarationFragment.setInitializer(infixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int n = i / j;", source);
    }

    /**
	 * BinaryExpression (%) ==> InfixExpression
	 */
    public void test0061() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0061", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 2);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("n"));
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newSimpleName("j"));
        infixExpression.setOperator(InfixExpression.Operator.REMAINDER);
        variableDeclarationFragment.setInitializer(infixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int n = i % j;", source);
    }

    /**
	 * BinaryExpression (^) ==> InfixExpression
	 */
    public void test0062() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0062", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 2);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("n"));
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newSimpleName("j"));
        infixExpression.setOperator(InfixExpression.Operator.XOR);
        variableDeclarationFragment.setInitializer(infixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int n = i ^ j;", source);
    }

    /**
	 * BinaryExpression (&) ==> InfixExpression
	 */
    public void test0063() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0063", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 2);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("n"));
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newSimpleName("j"));
        infixExpression.setOperator(InfixExpression.Operator.AND);
        variableDeclarationFragment.setInitializer(infixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int n = i & j;", source);
    }

    /**
	 * BinaryExpression (|) ==> InfixExpression
	 */
    public void test0064() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0064", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 2);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("n"));
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newSimpleName("j"));
        infixExpression.setOperator(InfixExpression.Operator.OR);
        variableDeclarationFragment.setInitializer(infixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int n = i | j;", source);
    }

    /**
	 * BinaryExpression (<) ==> InfixExpression
	 */
    public void test0065() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0065", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 2);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("b2"));
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("b"));
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newSimpleName("b1"));
        infixExpression.setOperator(InfixExpression.Operator.LESS);
        variableDeclarationFragment.setInitializer(infixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.BOOLEAN));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "boolean b2 = b < b1;", source);
    }

    /**
	 * BinaryExpression (<=) ==> InfixExpression
	 */
    public void test0066() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0066", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 2);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("b2"));
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("b"));
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newSimpleName("b1"));
        infixExpression.setOperator(InfixExpression.Operator.LESS_EQUALS);
        variableDeclarationFragment.setInitializer(infixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.BOOLEAN));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "boolean b2 = b <= b1;", source);
    }

    /**
	 * BinaryExpression (>) ==> InfixExpression
	 */
    public void test0067() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0067", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 2);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("b2"));
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("b"));
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newSimpleName("b1"));
        infixExpression.setOperator(InfixExpression.Operator.GREATER);
        variableDeclarationFragment.setInitializer(infixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.BOOLEAN));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "boolean b2 = b > b1;", source);
    }

    /**
	 * BinaryExpression (>=) ==> InfixExpression
	 */
    public void test0068() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0068", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 2);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("b2"));
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("b"));
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newSimpleName("b1"));
        infixExpression.setOperator(InfixExpression.Operator.GREATER_EQUALS);
        variableDeclarationFragment.setInitializer(infixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.BOOLEAN));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "boolean b2 = b >= b1;", source);
    }

    /**
	 * BinaryExpression (!=) ==> InfixExpression
	 */
    public void test0069() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0069", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 2);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("b2"));
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("b"));
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newSimpleName("b1"));
        infixExpression.setOperator(InfixExpression.Operator.NOT_EQUALS);
        variableDeclarationFragment.setInitializer(infixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.BOOLEAN));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "boolean b2 = b != b1;", source);
    }

    /**
	 * InstanceofExpression ==> InfixExpression
	 */
    public void test0070() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0070", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("b"));
        InstanceofExpression instanceOfExpression = this.ast.newInstanceofExpression();
        //$NON-NLS-1$
        instanceOfExpression.setLeftOperand(this.ast.newSimpleName("o"));
        //$NON-NLS-1$
        SimpleType simpleType = this.ast.newSimpleType(this.ast.newSimpleName("Integer"));
        instanceOfExpression.setRightOperand(simpleType);
        variableDeclarationFragment.setInitializer(instanceOfExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.BOOLEAN));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "boolean b = o instanceof Integer;", source);
    }

    /**
	 * InstanceofExpression ==> InfixExpression
	 */
    public void test0071() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0071", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("b"));
        InstanceofExpression instanceOfExpression = this.ast.newInstanceofExpression();
        //$NON-NLS-1$
        instanceOfExpression.setLeftOperand(this.ast.newSimpleName("o"));
        QualifiedName name = this.ast.newQualifiedName(this.ast.newQualifiedName(//$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "java"), //$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "lang")), //$NON-NLS-1$
        this.ast.newSimpleName("Integer"));
        Type type = this.ast.newSimpleType(name);
        instanceOfExpression.setRightOperand(type);
        variableDeclarationFragment.setInitializer(instanceOfExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.BOOLEAN));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "boolean b = o instanceof java.lang.Integer;", source);
    }

    /**
	 * UnaryExpression (!) ==> PrefixExpression
	 */
    public void test0072() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0072", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("b1"));
        PrefixExpression prefixExpression = this.ast.newPrefixExpression();
        prefixExpression.setOperator(PrefixExpression.Operator.NOT);
        //$NON-NLS-1$
        prefixExpression.setOperand(this.ast.newSimpleName("b"));
        variableDeclarationFragment.setInitializer(prefixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.BOOLEAN));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "boolean b1 = !b;", source);
    }

    /**
	 * UnaryExpression (~) ==> PrefixExpression
	 */
    public void test0073() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0073", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("n"));
        PrefixExpression prefixExpression = this.ast.newPrefixExpression();
        prefixExpression.setOperator(PrefixExpression.Operator.COMPLEMENT);
        //$NON-NLS-1$
        prefixExpression.setOperand(this.ast.newSimpleName("i"));
        variableDeclarationFragment.setInitializer(prefixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int n = ~i;", source);
    }

    /**
	 * UnaryExpression (+) ==> PrefixExpression
	 */
    public void test0074() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0074", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("i"));
        PrefixExpression prefixExpression = this.ast.newPrefixExpression();
        prefixExpression.setOperator(PrefixExpression.Operator.PLUS);
        //$NON-NLS-1$
        prefixExpression.setOperand(this.ast.newNumberLiteral("2"));
        variableDeclarationFragment.setInitializer(prefixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int i = +2;", source);
    }

    /**
	 * UnaryExpression (-) ==> PrefixExpression
	 */
    public void test0075() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0075", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("i"));
        PrefixExpression prefixExpression = this.ast.newPrefixExpression();
        prefixExpression.setOperator(PrefixExpression.Operator.MINUS);
        //$NON-NLS-1$
        prefixExpression.setOperand(this.ast.newNumberLiteral("2"));
        variableDeclarationFragment.setInitializer(prefixExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int i = -2;", source);
    }

    /**
	 * ConditionalExpression ==> ConditionalExpression
	 */
    public void test0076() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0076", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("b"));
        ConditionalExpression conditionalExpression = this.ast.newConditionalExpression();
        InfixExpression condition = this.ast.newInfixExpression();
        //$NON-NLS-1$
        condition.setLeftOperand(this.ast.newSimpleName("args"));
        //$NON-NLS-1$
        condition.setRightOperand(this.ast.newNullLiteral());
        condition.setOperator(InfixExpression.Operator.NOT_EQUALS);
        conditionalExpression.setExpression(condition);
        conditionalExpression.setThenExpression(this.ast.newBooleanLiteral(true));
        conditionalExpression.setElseExpression(this.ast.newBooleanLiteral(false));
        variableDeclarationFragment.setInitializer(conditionalExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.BOOLEAN));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "boolean b = args != null ? true : false;", source);
    }

    /**
	 * ConditionalExpression ==> ConditionalExpression
	 */
    public void test0077() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0077", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("i"));
        ConditionalExpression conditionalExpression = this.ast.newConditionalExpression();
        conditionalExpression.setExpression(this.ast.newBooleanLiteral(true));
        QualifiedName name = this.ast.newQualifiedName(//$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "args"), //$NON-NLS-1$
        this.ast.newSimpleName("length"));
        conditionalExpression.setThenExpression(name);
        //$NON-NLS-1$
        conditionalExpression.setElseExpression(this.ast.newNumberLiteral("0"));
        variableDeclarationFragment.setInitializer(conditionalExpression);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int i = true ? args.length: 0;", source);
    }

    /**
	 * MessageSend ==> SuperMethodInvocation
	 */
    public void test0078() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0078", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        SuperMethodInvocation superMethodInvocation = this.ast.newSuperMethodInvocation();
        //$NON-NLS-1$
        superMethodInvocation.setName(this.ast.newSimpleName("bar"));
        ExpressionStatement statement = this.ast.newExpressionStatement(superMethodInvocation);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "super.bar();", source);
    }

    /**
	 * MessageSend ==> SuperMethodInvocation
	 */
    public void test0079() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0079", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        SuperMethodInvocation superMethodInvocation = this.ast.newSuperMethodInvocation();
        //$NON-NLS-1$
        superMethodInvocation.setName(this.ast.newSimpleName("bar"));
        //$NON-NLS-1$
        superMethodInvocation.arguments().add(this.ast.newNumberLiteral("4"));
        ExpressionStatement statement = this.ast.newExpressionStatement(superMethodInvocation);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "super.bar(4);", source);
    }

    /**
	 * MessageSend ==> MethodInvocation
	 */
    public void test0080() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0080", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        MethodInvocation methodInvocation = this.ast.newMethodInvocation();
        //$NON-NLS-1$
        methodInvocation.setName(this.ast.newSimpleName("bar"));
        //$NON-NLS-1$
        methodInvocation.arguments().add(this.ast.newNumberLiteral("4"));
        ExpressionStatement statement = this.ast.newExpressionStatement(methodInvocation);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "bar(4);", source);
    }

    /**
	 * MessageSend ==> MethodInvocation
	 */
    public void test0081() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0081", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        MethodInvocation methodInvocation = this.ast.newMethodInvocation();
        //$NON-NLS-1$
        methodInvocation.setName(this.ast.newSimpleName("bar"));
        methodInvocation.setExpression(this.ast.newThisExpression());
        //$NON-NLS-1$
        methodInvocation.arguments().add(this.ast.newNumberLiteral("4"));
        ExpressionStatement statement = this.ast.newExpressionStatement(methodInvocation);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "this.bar(4);", source);
    }

    /**
	 * ForStatement ==> ForStatement
	 */
    public void test0082() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0082", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        ForStatement forStatement = this.ast.newForStatement();
        forStatement.setBody(this.ast.newEmptyStatement());
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", forStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "for (;;);", source);
    }

    /**
	 * ForStatement ==> ForStatement
	 */
    public void test0083() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0083", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        ForStatement forStatement = this.ast.newForStatement();
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        variableDeclarationFragment.setInitializer(this.ast.newNumberLiteral("0"));
        VariableDeclarationExpression variableDeclarationExpression = this.ast.newVariableDeclarationExpression(variableDeclarationFragment);
        variableDeclarationExpression.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        forStatement.initializers().add(variableDeclarationExpression);
        PostfixExpression postfixExpression = this.ast.newPostfixExpression();
        //$NON-NLS-1$
        postfixExpression.setOperand(this.ast.newSimpleName("i"));
        postfixExpression.setOperator(PostfixExpression.Operator.INCREMENT);
        forStatement.updaters().add(postfixExpression);
        forStatement.setBody(this.ast.newBlock());
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("i"));
        infixExpression.setOperator(InfixExpression.Operator.LESS);
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newNumberLiteral("10"));
        forStatement.setExpression(infixExpression);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", forStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "for (int i = 0; i < 10; i++) {}", source);
    }

    /**
	 * ForStatement ==> ForStatement
	 */
    public void test0084() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0084", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        ForStatement forStatement = this.ast.newForStatement();
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        variableDeclarationFragment.setInitializer(this.ast.newNumberLiteral("0"));
        VariableDeclarationExpression variableDeclarationExpression = this.ast.newVariableDeclarationExpression(variableDeclarationFragment);
        variableDeclarationExpression.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        forStatement.initializers().add(variableDeclarationExpression);
        PostfixExpression postfixExpression = this.ast.newPostfixExpression();
        //$NON-NLS-1$
        postfixExpression.setOperand(this.ast.newSimpleName("i"));
        postfixExpression.setOperator(PostfixExpression.Operator.INCREMENT);
        forStatement.updaters().add(postfixExpression);
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("i"));
        infixExpression.setOperator(InfixExpression.Operator.LESS);
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newNumberLiteral("10"));
        forStatement.setExpression(infixExpression);
        forStatement.setBody(this.ast.newEmptyStatement());
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", forStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "for (int i = 0; i < 10; i++);", source);
    }

    /**
	 * ForStatement ==> ForStatement
	 */
    public void test0085() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0085", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        ForStatement forStatement = this.ast.newForStatement();
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        variableDeclarationFragment.setInitializer(this.ast.newNumberLiteral("0"));
        VariableDeclarationExpression variableDeclarationExpression = this.ast.newVariableDeclarationExpression(variableDeclarationFragment);
        variableDeclarationExpression.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        forStatement.initializers().add(variableDeclarationExpression);
        PostfixExpression postfixExpression = this.ast.newPostfixExpression();
        //$NON-NLS-1$
        postfixExpression.setOperand(this.ast.newSimpleName("i"));
        postfixExpression.setOperator(PostfixExpression.Operator.INCREMENT);
        forStatement.updaters().add(postfixExpression);
        forStatement.setBody(this.ast.newEmptyStatement());
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", forStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "for (int i = 0;; i++);", source);
    }

    /**
	 * ForStatement ==> ForStatement
	 */
    public void test0086() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0086", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        ForStatement forStatement = this.ast.newForStatement();
        PostfixExpression postfixExpression = this.ast.newPostfixExpression();
        //$NON-NLS-1$
        postfixExpression.setOperand(this.ast.newSimpleName("i"));
        postfixExpression.setOperator(PostfixExpression.Operator.INCREMENT);
        forStatement.updaters().add(postfixExpression);
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("i"));
        infixExpression.setOperator(InfixExpression.Operator.LESS);
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newNumberLiteral("10"));
        forStatement.setExpression(infixExpression);
        forStatement.setBody(this.ast.newEmptyStatement());
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", forStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "for (; i < 10; i++);", source);
    }

    /**
	 * ForStatement ==> ForStatement
	 */
    public void test0087() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0087", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        ForStatement forStatement = this.ast.newForStatement();
        PostfixExpression postfixExpression = this.ast.newPostfixExpression();
        //$NON-NLS-1$
        postfixExpression.setOperand(this.ast.newSimpleName("i"));
        postfixExpression.setOperator(PostfixExpression.Operator.INCREMENT);
        forStatement.updaters().add(postfixExpression);
        forStatement.setBody(this.ast.newEmptyStatement());
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", forStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "for (;;i++);", source);
    }

    /**
	 * LocalDeclaration ==> VariableDeclarationStatement
	 */
    public void test0088() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0088", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("i"));
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int i;", source);
    }

    /**
	 * LocalDeclaration ==> VariableDeclarationStatement
	 */
    public void test0089() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0089", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("s"));
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        QualifiedName name = this.ast.newQualifiedName(this.ast.newQualifiedName(//$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "java"), //$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "lang")), //$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "String"));
        statement.setType(this.ast.newSimpleType(name));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "java.lang.String s;", source);
    }

    /**
	 * LocalDeclaration ==> VariableDeclarationStatement
	 */
    public void test0090() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0090", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        ArrayInitializer initializer = this.ast.newArrayInitializer();
        //$NON-NLS-1$
        initializer.expressions().add(this.ast.newNumberLiteral("1"));
        //$NON-NLS-1$
        initializer.expressions().add(this.ast.newNumberLiteral("2"));
        variableDeclarationFragment.setInitializer(initializer);
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("tab"));
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(this.ast.newArrayType(this.ast.newPrimitiveType(PrimitiveType.INT), 1));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int[] tab = {1, 2};", source);
    }

    /**
	 * Argument ==> SingleVariableDeclaration
	 */
    public void test0091() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0091", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        MethodDeclaration method = (MethodDeclaration) ((TypeDeclaration) ((CompilationUnit) result).types().get(0)).bodyDeclarations().get(0);
        SingleVariableDeclaration node = (SingleVariableDeclaration) method.parameters().get(0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        SingleVariableDeclaration variableDeclaration = this.ast.newSingleVariableDeclaration();
        //$NON-NLS-1$
        variableDeclaration.setType(this.ast.newSimpleType(this.ast.newSimpleName("String")));
        //$NON-NLS-1$
        variableDeclaration.setName(this.ast.newSimpleName("s"));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", variableDeclaration.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "String s", source);
    }

    /**
	 * Argument ==> SingleVariableDeclaration
	 */
    public void test0092() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0092", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        MethodDeclaration method = (MethodDeclaration) ((TypeDeclaration) ((CompilationUnit) result).types().get(0)).bodyDeclarations().get(0);
        SingleVariableDeclaration node = (SingleVariableDeclaration) method.parameters().get(0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        SingleVariableDeclaration variableDeclaration = this.ast.newSingleVariableDeclaration();
        variableDeclaration.modifiers().add(this.ast.newModifier(Modifier.ModifierKeyword.FINAL_KEYWORD));
        //$NON-NLS-1$
        variableDeclaration.setType(this.ast.newSimpleType(this.ast.newSimpleName("String")));
        //$NON-NLS-1$
        variableDeclaration.setName(this.ast.newSimpleName("s"));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", variableDeclaration.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "final String s", source);
        //$NON-NLS-1$
        assertEquals("Wrong dimension", 0, node.getExtraDimensions());
    }

    /**
	 * Break ==> BreakStatement
	 */
    public void test0093() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0093", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        ForStatement forStatement = (ForStatement) node;
        BreakStatement statement = (BreakStatement) ((Block) forStatement.getBody()).statements().get(0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", statement);
        BreakStatement breakStatement = this.ast.newBreakStatement();
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", breakStatement.subtreeMatch(new ASTMatcher(), statement));
        //$NON-NLS-1$
        checkSourceRange(statement, "break;", source);
    }

    /**
	 * Continue ==> ContinueStatement
	 */
    public void test0094() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0094", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        ForStatement forStatement = (ForStatement) node;
        ContinueStatement statement = (ContinueStatement) ((Block) forStatement.getBody()).statements().get(0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", statement);
        ContinueStatement continueStatement = this.ast.newContinueStatement();
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", continueStatement.subtreeMatch(new ASTMatcher(), statement));
        //$NON-NLS-1$
        checkSourceRange(statement, "continue;", source);
    }

    /**
	 * Continue with Label ==> ContinueStatement
	 */
    public void test0095() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0095", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        LabeledStatement labeledStatement = (LabeledStatement) getASTNode((CompilationUnit) result, 0, 0, 0);
        ForStatement forStatement = (ForStatement) labeledStatement.getBody();
        ContinueStatement statement = (ContinueStatement) ((Block) forStatement.getBody()).statements().get(0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", statement);
        ContinueStatement continueStatement = this.ast.newContinueStatement();
        //$NON-NLS-1$
        continueStatement.setLabel(this.ast.newSimpleName("label"));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", continueStatement.subtreeMatch(new ASTMatcher(), statement));
        //$NON-NLS-1$
        checkSourceRange(statement, "continue label;", source);
    }

    /**
	 * Break + label  ==> BreakStatement
	 */
    public void test0096() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0096", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        LabeledStatement labeledStatement = (LabeledStatement) getASTNode((CompilationUnit) result, 0, 0, 0);
        ForStatement forStatement = (ForStatement) labeledStatement.getBody();
        BreakStatement statement = (BreakStatement) ((Block) forStatement.getBody()).statements().get(0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", statement);
        BreakStatement breakStatement = this.ast.newBreakStatement();
        //$NON-NLS-1$
        breakStatement.setLabel(this.ast.newSimpleName("label"));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", breakStatement.subtreeMatch(new ASTMatcher(), statement));
        //$NON-NLS-1$
        checkSourceRange(statement, "break label;", source);
    }

    /**
	 * SwitchStatement ==> SwitchStatement
	 */
    public void test0097() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0097", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        SwitchStatement switchStatement = this.ast.newSwitchStatement();
        //$NON-NLS-1$
        switchStatement.setExpression(this.ast.newSimpleName("i"));
        SwitchCase _case = this.ast.newSwitchCase();
        //$NON-NLS-1$
        _case.setExpression(this.ast.newNumberLiteral("1"));
        switchStatement.statements().add(_case);
        switchStatement.statements().add(this.ast.newBreakStatement());
        _case = this.ast.newSwitchCase();
        //$NON-NLS-1$
        _case.setExpression(this.ast.newNumberLiteral("2"));
        switchStatement.statements().add(_case);
        MethodInvocation methodInvocation = this.ast.newMethodInvocation();
        QualifiedName name = this.ast.newQualifiedName(//$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "System"), //$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "out"));
        methodInvocation.setExpression(name);
        //$NON-NLS-1$
        methodInvocation.setName(this.ast.newSimpleName("println"));
        //$NON-NLS-1$
        methodInvocation.arguments().add(this.ast.newNumberLiteral("2"));
        ExpressionStatement expressionStatement = this.ast.newExpressionStatement(methodInvocation);
        switchStatement.statements().add(expressionStatement);
        switchStatement.statements().add(this.ast.newBreakStatement());
        _case = this.ast.newSwitchCase();
        _case.setExpression(null);
        switchStatement.statements().add(_case);
        methodInvocation = this.ast.newMethodInvocation();
        name = this.ast.newQualifiedName(//$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "System"), //$NON-NLS-1$
        this.ast.newSimpleName(//$NON-NLS-1$
        "out"));
        methodInvocation.setExpression(name);
        //$NON-NLS-1$
        methodInvocation.setName(this.ast.newSimpleName("println"));
        StringLiteral literal = this.ast.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue("default");
        methodInvocation.arguments().add(literal);
        expressionStatement = this.ast.newExpressionStatement(methodInvocation);
        switchStatement.statements().add(expressionStatement);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", switchStatement.subtreeMatch(new ASTMatcher(), node));
        String expectedSource = //$NON-NLS-1$
        "switch(i) {\n" + "			case 1: \n" + //$NON-NLS-1$
        "              break;\n" + "			case 2:\n" + "				System.out.println(2);\n" + //$NON-NLS-1$
        "              break;\n" + //$NON-NLS-1$
        "          default:\n" + "				System.out.println(\"default\");\n" + "		}";
        checkSourceRange(node, expectedSource, source);
        SwitchStatement switchStatement2 = (SwitchStatement) node;
        List statements = switchStatement2.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 7, statements.size());
        Statement stmt = (Statement) statements.get(5);
        //$NON-NLS-1$
        assertTrue("Not a case statement", stmt instanceof SwitchCase);
        SwitchCase switchCase = (SwitchCase) stmt;
        //$NON-NLS-1$
        assertTrue("Not the default case", switchCase.isDefault());
    }

    /**
	 * EmptyStatement ==> EmptyStatement
	 */
    public void test0098() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0098", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        EmptyStatement emptyStatement = this.ast.newEmptyStatement();
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", emptyStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, ";", source);
    }

    /**
	 * DoStatement ==> DoStatement
	 */
    public void test0099() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0099", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        DoStatement doStatement = this.ast.newDoStatement();
        Block block = this.ast.newBlock();
        block.statements().add(this.ast.newEmptyStatement());
        doStatement.setBody(block);
        doStatement.setExpression(this.ast.newBooleanLiteral(true));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", doStatement.subtreeMatch(new ASTMatcher(), node));
        String expectedSource = //$NON-NLS-1$
        "do {;\n" + "		} while(true);";
        checkSourceRange(node, expectedSource, source);
    }

    /**
	 * WhileStatement ==> WhileStatement
	 */
    public void test0100() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0100", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        WhileStatement whileStatement = this.ast.newWhileStatement();
        whileStatement.setExpression(this.ast.newBooleanLiteral(true));
        whileStatement.setBody(this.ast.newEmptyStatement());
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", whileStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "while(true);", source);
    }

    /**
	 * WhileStatement ==> WhileStatement
	 */
    public void test0101() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0101", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        WhileStatement whileStatement = this.ast.newWhileStatement();
        whileStatement.setExpression(this.ast.newBooleanLiteral(true));
        whileStatement.setBody(this.ast.newBlock());
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", whileStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "while(true) {}", source);
    }

    /**
	 * ExtendedStringLiteral ==> StringLiteral
	 */
    public void test0102() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0102", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        InfixExpression infixExpression = this.ast.newInfixExpression();
        infixExpression.setOperator(InfixExpression.Operator.PLUS);
        //$NON-NLS-1$
        StringLiteral literal = this.ast.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue("Hello");
        infixExpression.setLeftOperand(literal);
        //$NON-NLS-1$
        literal = this.ast.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue(" World");
        infixExpression.setRightOperand(literal);
        //$NON-NLS-1$
        literal = this.ast.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue("!");
        infixExpression.extendedOperands().add(literal);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", infixExpression.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "\"Hello\" + \" World\" + \"!\"", source);
    }

    /**
	 * ExtendedStringLiteral ==> StringLiteral
	 */
    public void test0103() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0103", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        InfixExpression infixExpression = this.ast.newInfixExpression();
        infixExpression.setOperator(InfixExpression.Operator.PLUS);
        //$NON-NLS-1$
        StringLiteral literal = this.ast.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue("Hello");
        infixExpression.setLeftOperand(literal);
        //$NON-NLS-1$
        literal = this.ast.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue(" World");
        infixExpression.setRightOperand(literal);
        //$NON-NLS-1$
        literal = this.ast.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue("!");
        infixExpression.extendedOperands().add(literal);
        //$NON-NLS-1$
        literal = this.ast.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue("!");
        infixExpression.extendedOperands().add(literal);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", infixExpression.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "\"Hello\" + \" World\" + \"!\" + \"!\"", source);
    }

    /**
	 * ExtendedStringLiteral ==> StringLiteral
	 */
    public void test0104() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0104", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        InfixExpression infixExpression = this.ast.newInfixExpression();
        infixExpression.setOperator(InfixExpression.Operator.PLUS);
        //$NON-NLS-1$
        StringLiteral literal = this.ast.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue("Hello");
        infixExpression.setLeftOperand(literal);
        //$NON-NLS-1$
        literal = this.ast.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue(" World");
        infixExpression.setRightOperand(literal);
        //$NON-NLS-1$
        literal = this.ast.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue("!");
        infixExpression.extendedOperands().add(literal);
        //$NON-NLS-1$
        NumberLiteral numberLiteral = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        numberLiteral.setToken("4");
        infixExpression.extendedOperands().add(numberLiteral);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", infixExpression.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "\"Hello\" + \" World\" + \"!\" + 4", source);
    }

    /**
	 * NumberLiteral ==> InfixExpression
	 */
    public void test0105() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0105", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        InfixExpression infixExpression = this.ast.newInfixExpression();
        infixExpression.setOperator(InfixExpression.Operator.PLUS);
        //$NON-NLS-1$
        NumberLiteral literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("4");
        infixExpression.setLeftOperand(literal);
        //$NON-NLS-1$
        literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("5");
        infixExpression.setRightOperand(literal);
        //$NON-NLS-1$
        literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("6");
        infixExpression.extendedOperands().add(literal);
        //$NON-NLS-1$
        literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("4");
        infixExpression.extendedOperands().add(literal);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", infixExpression.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "4 + 5 + 6 + 4", source);
    }

    /**
	 * NumberLiteral ==> InfixExpression
	 */
    public void test0106() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0106", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        InfixExpression infixExpression = this.ast.newInfixExpression();
        infixExpression.setOperator(InfixExpression.Operator.MINUS);
        //$NON-NLS-1$
        NumberLiteral literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("4");
        infixExpression.setLeftOperand(literal);
        //$NON-NLS-1$
        literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("5");
        infixExpression.setRightOperand(literal);
        InfixExpression infixExpression2 = this.ast.newInfixExpression();
        infixExpression2.setOperator(InfixExpression.Operator.PLUS);
        infixExpression2.setLeftOperand(infixExpression);
        //$NON-NLS-1$
        literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("6");
        infixExpression2.setRightOperand(literal);
        InfixExpression infixExpression3 = this.ast.newInfixExpression();
        infixExpression3.setOperator(InfixExpression.Operator.PLUS);
        infixExpression3.setLeftOperand(infixExpression2);
        //$NON-NLS-1$
        literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("4");
        infixExpression3.setRightOperand(literal);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", infixExpression3.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "4 - 5 + 6 + 4", source);
    }

    /**
	 * NumberLiteral ==> InfixExpression
	 */
    public void test0107() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0107", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        InfixExpression infixExpression = this.ast.newInfixExpression();
        infixExpression.setOperator(InfixExpression.Operator.MINUS);
        //$NON-NLS-1$
        NumberLiteral literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("4");
        infixExpression.setLeftOperand(literal);
        //$NON-NLS-1$
        literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("5");
        infixExpression.setRightOperand(literal);
        //$NON-NLS-1$
        literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("6");
        infixExpression.extendedOperands().add(literal);
        //$NON-NLS-1$
        literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("4");
        infixExpression.extendedOperands().add(literal);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", infixExpression.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "4 - 5 - 6 - 4", source);
    }

    /**
	 * NumberLiteral ==> InfixExpression
	 */
    public void test0108() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0108", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        InfixExpression infixExpression = this.ast.newInfixExpression();
        infixExpression.setOperator(InfixExpression.Operator.PLUS);
        //$NON-NLS-1$
        StringLiteral stringLiteral = this.ast.newStringLiteral();
        //$NON-NLS-1$
        stringLiteral.setLiteralValue("4");
        infixExpression.setLeftOperand(stringLiteral);
        //$NON-NLS-1$
        NumberLiteral literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("5");
        infixExpression.setRightOperand(literal);
        //$NON-NLS-1$
        literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("6");
        infixExpression.extendedOperands().add(literal);
        //$NON-NLS-1$
        literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("4");
        infixExpression.extendedOperands().add(literal);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", infixExpression.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "\"4\" + 5 + 6 + 4", source);
    }

    /**
	 * NumberLiteral ==> InfixExpression
	 */
    public void test0109() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0109", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        InfixExpression infixExpression = this.ast.newInfixExpression();
        infixExpression.setOperator(InfixExpression.Operator.MINUS);
        //$NON-NLS-1$
        StringLiteral stringLiteral = this.ast.newStringLiteral();
        //$NON-NLS-1$
        stringLiteral.setLiteralValue("4");
        infixExpression.setLeftOperand(stringLiteral);
        //$NON-NLS-1$
        NumberLiteral literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("5");
        infixExpression.setRightOperand(literal);
        InfixExpression infixExpression2 = this.ast.newInfixExpression();
        infixExpression2.setOperator(InfixExpression.Operator.PLUS);
        infixExpression2.setLeftOperand(infixExpression);
        //$NON-NLS-1$
        literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("6");
        infixExpression2.setRightOperand(literal);
        InfixExpression infixExpression3 = this.ast.newInfixExpression();
        infixExpression3.setOperator(InfixExpression.Operator.PLUS);
        infixExpression3.setLeftOperand(infixExpression2);
        //$NON-NLS-1$
        literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("4");
        infixExpression3.setRightOperand(literal);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", infixExpression3.subtreeMatch(new ASTMatcher(), expression));
        //$NON-NLS-1$
        checkSourceRange(expression, "\"4\" - 5 + 6 + 4", source);
    }

    /**
	 * ReturnStatement ==> ReturnStatement
	 */
    public void test0110() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0110", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        ReturnStatement returnStatement = this.ast.newReturnStatement();
        NumberLiteral literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("2");
        returnStatement.setExpression(literal);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", returnStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "return 2;", source);
    }

    /**
	 * ReturnStatement ==> ReturnStatement
	 */
    public void test0111() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0111", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        ReturnStatement returnStatement = this.ast.newReturnStatement();
        NumberLiteral literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("2");
        returnStatement.setExpression(literal);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", returnStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "return 2\\u003B", source);
    }

    /**
	 * SynchronizedStatement ==> SynchronizedStatement
	 */
    public void test0112() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0112", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        SynchronizedStatement synchronizedStatement = this.ast.newSynchronizedStatement();
        synchronizedStatement.setExpression(this.ast.newThisExpression());
        synchronizedStatement.setBody(this.ast.newBlock());
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", synchronizedStatement.subtreeMatch(new ASTMatcher(), node));
        String expectedSource = //$NON-NLS-1$
        "synchronized(this) {\n" + "		}";
        checkSourceRange(node, expectedSource, source);
    }

    /**
	 * TryStatement ==> TryStatement
	 */
    public void test0113() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0113", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        TryStatement tryStatement = this.ast.newTryStatement();
        tryStatement.setBody(this.ast.newBlock());
        tryStatement.setFinally(this.ast.newBlock());
        CatchClause catchBlock = this.ast.newCatchClause();
        catchBlock.setBody(this.ast.newBlock());
        SingleVariableDeclaration exceptionVariable = this.ast.newSingleVariableDeclaration();
        //$NON-NLS-1$
        exceptionVariable.setName(this.ast.newSimpleName("e"));
        //$NON-NLS-1$
        exceptionVariable.setType(this.ast.newSimpleType(this.ast.newSimpleName("Exception")));
        catchBlock.setException(exceptionVariable);
        tryStatement.catchClauses().add(catchBlock);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", tryStatement.subtreeMatch(new ASTMatcher(), node));
        String expectedSource = //$NON-NLS-1$
        "try {\n" + "		} catch(Exception e) {\n" + "		} finally {\n" + "		}";
        checkSourceRange(node, expectedSource, source);
    }

    /**
	 * TryStatement ==> TryStatement
	 */
    public void test0114() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0114", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        TryStatement tryStatement = this.ast.newTryStatement();
        tryStatement.setBody(this.ast.newBlock());
        CatchClause catchBlock = this.ast.newCatchClause();
        catchBlock.setBody(this.ast.newBlock());
        SingleVariableDeclaration exceptionVariable = this.ast.newSingleVariableDeclaration();
        //$NON-NLS-1$
        exceptionVariable.setName(this.ast.newSimpleName("e"));
        //$NON-NLS-1$
        exceptionVariable.setType(this.ast.newSimpleType(this.ast.newSimpleName("Exception")));
        catchBlock.setException(exceptionVariable);
        tryStatement.catchClauses().add(catchBlock);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", tryStatement.subtreeMatch(new ASTMatcher(), node));
        String expectedSource = //$NON-NLS-1$
        "try {\n" + "		} catch(Exception e) {\n" + "		}";
        checkSourceRange(node, expectedSource, source);
    }

    /**
	 * TryStatement ==> TryStatement
	 */
    public void test0115() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0115", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        TryStatement tryStatement = this.ast.newTryStatement();
        Block block = this.ast.newBlock();
        ReturnStatement returnStatement = this.ast.newReturnStatement();
        NumberLiteral literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("2");
        returnStatement.setExpression(literal);
        block.statements().add(returnStatement);
        tryStatement.setBody(block);
        CatchClause catchBlock = this.ast.newCatchClause();
        catchBlock.setBody(this.ast.newBlock());
        SingleVariableDeclaration exceptionVariable = this.ast.newSingleVariableDeclaration();
        //$NON-NLS-1$
        exceptionVariable.setName(this.ast.newSimpleName("e"));
        //$NON-NLS-1$
        exceptionVariable.setType(this.ast.newSimpleType(this.ast.newSimpleName("Exception")));
        catchBlock.setException(exceptionVariable);
        tryStatement.catchClauses().add(catchBlock);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", tryStatement.subtreeMatch(new ASTMatcher(), node));
        String expectedSource = //$NON-NLS-1$
        "try {\n" + "			return 2;\n" + "		} catch(Exception e) {\n" + "		}";
        checkSourceRange(node, expectedSource, source);
    }

    /**
	 * ThrowStatement ==> ThrowStatement
	 */
    public void test0116() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0116", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        ThrowStatement throwStatement = this.ast.newThrowStatement();
        //$NON-NLS-1$
        throwStatement.setExpression(this.ast.newSimpleName("e"));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", throwStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "throw e   \\u003B", source);
    }

    /**
	 * ThrowStatement ==> ThrowStatement
	 */
    public void test0117() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0117", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        ThrowStatement throwStatement = this.ast.newThrowStatement();
        //$NON-NLS-1$
        throwStatement.setExpression(this.ast.newSimpleName("e"));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", throwStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "throw e /* comment in the middle of a throw */  \\u003B", source);
    }

    /**
	 * ThrowStatement ==> ThrowStatement
	 */
    public void test0118() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0118", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        ThrowStatement throwStatement = this.ast.newThrowStatement();
        //$NON-NLS-1$
        throwStatement.setExpression(this.ast.newSimpleName("e"));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", throwStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "throw e /* comment in the middle of a throw */  \\u003B", source);
    }

    /**
	 * IfStatement ==> IfStatement
	 */
    public void test0119() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0119", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        IfStatement ifStatement = this.ast.newIfStatement();
        ifStatement.setExpression(this.ast.newBooleanLiteral(true));
        ifStatement.setThenStatement(this.ast.newEmptyStatement());
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", ifStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "if (true)\\u003B", source);
    }

    /**
	 * IfStatement ==> IfStatement
	 */
    public void test0120() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0120", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        IfStatement ifStatement = this.ast.newIfStatement();
        ifStatement.setExpression(this.ast.newBooleanLiteral(true));
        ifStatement.setThenStatement(this.ast.newEmptyStatement());
        ifStatement.setElseStatement(this.ast.newEmptyStatement());
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", ifStatement.subtreeMatch(new ASTMatcher(), node));
        String expectedSource = //$NON-NLS-1$
        "if (true)\\u003B\n" + //$NON-NLS-1$
        "\t\telse ;";
        checkSourceRange(node, expectedSource, source);
    }

    /**
	 * IfStatement ==> IfStatement
	 */
    public void test0121() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0121", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        IfStatement ifStatement = this.ast.newIfStatement();
        ifStatement.setExpression(this.ast.newBooleanLiteral(true));
        ifStatement.setThenStatement(this.ast.newBlock());
        ifStatement.setElseStatement(this.ast.newEmptyStatement());
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", ifStatement.subtreeMatch(new ASTMatcher(), node));
        String expectedSource = //$NON-NLS-1$
        "if (true) {}\n" + "		else ;";
        checkSourceRange(node, expectedSource, source);
    }

    /**
	 * IfStatement ==> IfStatement
	 */
    public void test0122() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0122", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        IfStatement ifStatement = this.ast.newIfStatement();
        ifStatement.setExpression(this.ast.newBooleanLiteral(true));
        ReturnStatement returnStatement = this.ast.newReturnStatement();
        NumberLiteral literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("2");
        returnStatement.setExpression(literal);
        ifStatement.setThenStatement(returnStatement);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", ifStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "if (true) return 2\\u003B", source);
    }

    /**
	 * IfStatement ==> IfStatement
	 */
    public void test0123() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0123", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        IfStatement ifStatement = this.ast.newIfStatement();
        ifStatement.setExpression(this.ast.newBooleanLiteral(true));
        ReturnStatement returnStatement = this.ast.newReturnStatement();
        NumberLiteral literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("2");
        returnStatement.setExpression(literal);
        ifStatement.setThenStatement(returnStatement);
        returnStatement = this.ast.newReturnStatement();
        literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("3");
        returnStatement.setExpression(literal);
        ifStatement.setElseStatement(returnStatement);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", ifStatement.subtreeMatch(new ASTMatcher(), node));
        String expectedSource = //$NON-NLS-1$
        "if (true) return 2;\n" + "		else return 3;";
        checkSourceRange(node, expectedSource, source);
    }

    /**
	 * Multiple local declaration => VariabledeclarationStatement
	 */
    public void test0124() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0124", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment fragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        fragment.setName(this.ast.newSimpleName("x"));
        NumberLiteral literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("10");
        fragment.setInitializer(literal);
        internalSetExtraDimensions(fragment, 0);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(fragment);
        fragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        fragment.setName(this.ast.newSimpleName("z"));
        fragment.setInitializer(this.ast.newNullLiteral());
        internalSetExtraDimensions(fragment, 1);
        statement.fragments().add(fragment);
        fragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        fragment.setName(this.ast.newSimpleName("i"));
        internalSetExtraDimensions(fragment, 0);
        statement.fragments().add(fragment);
        fragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        fragment.setName(this.ast.newSimpleName("j"));
        internalSetExtraDimensions(fragment, 2);
        statement.fragments().add(fragment);
        statement.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        VariableDeclarationFragment[] fragments = (VariableDeclarationFragment[]) ((VariableDeclarationStatement) node).fragments().toArray(new VariableDeclarationFragment[4]);
        //$NON-NLS-1$
        assertTrue("fragments.length != 4", fragments.length == 4);
        //$NON-NLS-1$
        checkSourceRange(fragments[0], "x= 10", source);
        //$NON-NLS-1$
        checkSourceRange(fragments[1], "z[] = null", source);
        //$NON-NLS-1$
        checkSourceRange(fragments[2], "i", source);
        //$NON-NLS-1$
        checkSourceRange(fragments[3], "j[][]", source);
        //$NON-NLS-1$
        checkSourceRange(node, "int x= 10, z[] = null, i, j[][];", source);
    }

    /**
	 * Multiple local declaration => VariabledeclarationStatement
	 */
    public void test0125() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0125", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        VariableDeclarationFragment fragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        fragment.setName(this.ast.newSimpleName("x"));
        NumberLiteral literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("10");
        fragment.setInitializer(literal);
        internalSetExtraDimensions(fragment, 0);
        VariableDeclarationStatement statement = this.ast.newVariableDeclarationStatement(fragment);
        fragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        fragment.setName(this.ast.newSimpleName("z"));
        fragment.setInitializer(this.ast.newNullLiteral());
        internalSetExtraDimensions(fragment, 1);
        statement.fragments().add(fragment);
        fragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        fragment.setName(this.ast.newSimpleName("i"));
        internalSetExtraDimensions(fragment, 0);
        statement.fragments().add(fragment);
        fragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        fragment.setName(this.ast.newSimpleName("j"));
        internalSetExtraDimensions(fragment, 2);
        statement.fragments().add(fragment);
        statement.setType(this.ast.newArrayType(this.ast.newPrimitiveType(PrimitiveType.INT), 1));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", statement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int[] x= 10, z[] = null, i, j[][];", source);
        VariableDeclarationFragment[] fragments = (VariableDeclarationFragment[]) ((VariableDeclarationStatement) node).fragments().toArray(new VariableDeclarationFragment[4]);
        //$NON-NLS-1$
        assertTrue("fragments.length != 4", fragments.length == 4);
        //$NON-NLS-1$
        checkSourceRange(fragments[0], "x= 10", source);
        //$NON-NLS-1$
        checkSourceRange(fragments[1], "z[] = null", source);
        //$NON-NLS-1$
        checkSourceRange(fragments[2], "i", source);
        //$NON-NLS-1$
        checkSourceRange(fragments[3], "j[][]", source);
    }

    /**
	 * ForStatement
	 */
    public void test0126() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0126", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        ForStatement forStatement = this.ast.newForStatement();
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("tab"));
        //$NON-NLS-1$
        variableDeclarationFragment.setInitializer(this.ast.newNullLiteral());
        internalSetExtraDimensions(variableDeclarationFragment, 1);
        VariableDeclarationExpression variableDeclarationExpression = this.ast.newVariableDeclarationExpression(variableDeclarationFragment);
        //$NON-NLS-1$
        variableDeclarationExpression.setType(this.ast.newArrayType(this.ast.newSimpleType(this.ast.newSimpleName("String")), 1));
        forStatement.initializers().add(variableDeclarationExpression);
        PrefixExpression prefixExpression = this.ast.newPrefixExpression();
        //$NON-NLS-1$
        prefixExpression.setOperand(this.ast.newSimpleName("i"));
        prefixExpression.setOperator(PrefixExpression.Operator.INCREMENT);
        forStatement.updaters().add(prefixExpression);
        forStatement.setBody(this.ast.newBlock());
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", forStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "for (String[] tab[] = null;; ++i) {}", source);
        //$NON-NLS-1$
        checkSourceRange((ASTNode) ((ForStatement) node).updaters().get(0), "++i", source);
        //$NON-NLS-1$
        checkSourceRange((ASTNode) ((ForStatement) node).initializers().get(0), "String[] tab[] = null", source);
    }

    /**
	 * ForStatement
	 */
    public void test0127() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0127", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        ForStatement forStatement = this.ast.newForStatement();
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("tab"));
        //$NON-NLS-1$
        variableDeclarationFragment.setInitializer(this.ast.newNullLiteral());
        internalSetExtraDimensions(variableDeclarationFragment, 1);
        VariableDeclarationExpression variableDeclarationExpression = this.ast.newVariableDeclarationExpression(variableDeclarationFragment);
        //$NON-NLS-1$
        variableDeclarationExpression.setType(this.ast.newSimpleType(this.ast.newSimpleName("String")));
        forStatement.initializers().add(variableDeclarationExpression);
        PrefixExpression prefixExpression = this.ast.newPrefixExpression();
        //$NON-NLS-1$
        prefixExpression.setOperand(this.ast.newSimpleName("i"));
        prefixExpression.setOperator(PrefixExpression.Operator.INCREMENT);
        forStatement.updaters().add(prefixExpression);
        forStatement.setBody(this.ast.newBlock());
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", forStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "for (String tab[] = null;; ++i) {}", source);
        //$NON-NLS-1$
        checkSourceRange((ASTNode) ((ForStatement) node).updaters().get(0), "++i", source);
        //$NON-NLS-1$
        checkSourceRange((ASTNode) ((ForStatement) node).initializers().get(0), "String tab[] = null", source);
    }

    /**
	 * ForStatement
	 */
    public void test0128() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0128", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        ForStatement forStatement = this.ast.newForStatement();
        VariableDeclarationFragment variableDeclarationFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        variableDeclarationFragment.setName(this.ast.newSimpleName("tab"));
        //$NON-NLS-1$
        variableDeclarationFragment.setInitializer(this.ast.newNullLiteral());
        internalSetExtraDimensions(variableDeclarationFragment, 1);
        VariableDeclarationExpression variableDeclarationExpression = this.ast.newVariableDeclarationExpression(variableDeclarationFragment);
        //$NON-NLS-1$
        variableDeclarationExpression.setType(this.ast.newSimpleType(this.ast.newSimpleName("String")));
        forStatement.initializers().add(variableDeclarationExpression);
        PostfixExpression postfixExpression = this.ast.newPostfixExpression();
        //$NON-NLS-1$
        postfixExpression.setOperand(this.ast.newSimpleName("i"));
        postfixExpression.setOperator(PostfixExpression.Operator.INCREMENT);
        forStatement.updaters().add(postfixExpression);
        forStatement.setBody(this.ast.newBlock());
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", forStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "for (String tab[] = null;; i++/**/) {}", source);
        //$NON-NLS-1$
        checkSourceRange((ASTNode) ((ForStatement) node).updaters().get(0), "i++", source);
        //$NON-NLS-1$
        checkSourceRange((ASTNode) ((ForStatement) node).initializers().get(0), "String tab[] = null", source);
    }

    /**
	 * FieldDeclaration
	 */
    public void test0129() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0129", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a FieldDeclaration", node instanceof FieldDeclaration);
        VariableDeclarationFragment frag = (VariableDeclarationFragment) ((FieldDeclaration) node).fragments().get(0);
        //$NON-NLS-1$
        assertTrue("Not a declaration", frag.getName().isDeclaration());
        VariableDeclarationFragment fragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        fragment.setName(this.ast.newSimpleName("i"));
        internalSetExtraDimensions(fragment, 0);
        FieldDeclaration fieldDeclaration = this.ast.newFieldDeclaration(fragment);
        fieldDeclaration.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", fieldDeclaration.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "int i;", source);
    }

    /**
	 * FieldDeclaration
	 */
    public void test0130() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0130", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a FieldDeclaration", node instanceof FieldDeclaration);
        VariableDeclarationFragment fragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        fragment.setName(this.ast.newSimpleName("x"));
        NumberLiteral literal = this.ast.newNumberLiteral();
        //$NON-NLS-1$
        literal.setToken("10");
        fragment.setInitializer(literal);
        internalSetExtraDimensions(fragment, 0);
        FieldDeclaration fieldDeclaration = this.ast.newFieldDeclaration(fragment);
        fieldDeclaration.modifiers().add(this.ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
        fieldDeclaration.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        fragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        fragment.setName(this.ast.newSimpleName("y"));
        internalSetExtraDimensions(fragment, 1);
        fragment.setInitializer(this.ast.newNullLiteral());
        fieldDeclaration.fragments().add(fragment);
        fragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        fragment.setName(this.ast.newSimpleName("i"));
        internalSetExtraDimensions(fragment, 0);
        fieldDeclaration.fragments().add(fragment);
        fragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        fragment.setName(this.ast.newSimpleName("j"));
        internalSetExtraDimensions(fragment, 2);
        fieldDeclaration.fragments().add(fragment);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", fieldDeclaration.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "public int x= 10, y[] = null, i, j[][];", source);
        VariableDeclarationFragment[] fragments = (VariableDeclarationFragment[]) ((FieldDeclaration) node).fragments().toArray(new VariableDeclarationFragment[4]);
        //$NON-NLS-1$
        assertTrue("fragments.length != 4", fragments.length == 4);
        //$NON-NLS-1$
        checkSourceRange(fragments[0], "x= 10", source);
        //$NON-NLS-1$
        checkSourceRange(fragments[1], "y[] = null", source);
        //$NON-NLS-1$
        checkSourceRange(fragments[2], "i", source);
        //$NON-NLS-1$
        checkSourceRange(fragments[3], "j[][]", source);
    }

    /**
	 * Argument with final modifier
	 */
    public void test0131() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0131", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a MethodDeclaration", node instanceof MethodDeclaration);
        //$NON-NLS-1$
        assertTrue("Not a declaration", ((MethodDeclaration) node).getName().isDeclaration());
        List parameters = ((MethodDeclaration) node).parameters();
        //$NON-NLS-1$
        assertTrue("Parameters.length != 1", parameters.size() == 1);
        SingleVariableDeclaration arg = (SingleVariableDeclaration) ((MethodDeclaration) node).parameters().get(0);
        SingleVariableDeclaration singleVariableDeclaration = this.ast.newSingleVariableDeclaration();
        singleVariableDeclaration.modifiers().add(this.ast.newModifier(Modifier.ModifierKeyword.FINAL_KEYWORD));
        //$NON-NLS-1$
        singleVariableDeclaration.setName(this.ast.newSimpleName("i"));
        singleVariableDeclaration.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", singleVariableDeclaration.subtreeMatch(new ASTMatcher(), arg));
        //$NON-NLS-1$
        checkSourceRange(node, "void foo(final int i) {}", source);
        //$NON-NLS-1$
        checkSourceRange(arg, "final int i", source);
    }

    /**
	 * Check javadoc for MethodDeclaration
	 * @deprecated marking deprecated since using deprecated code
	 */
    public void test0132() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0132", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a MethodDeclaration", node instanceof MethodDeclaration);
        Javadoc actualJavadoc = ((MethodDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        checkSourceRange(node, "/** JavaDoc Comment*/\n  void foo(final int i) {}", source);
        //$NON-NLS-1$
        checkSourceRange(actualJavadoc, "/** JavaDoc Comment*/", source);
    }

    /**
	 * Check javadoc for MethodDeclaration
	 */
    public void test0133() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0133", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a MethodDeclaration", node instanceof MethodDeclaration);
        Javadoc actualJavadoc = ((MethodDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        assertTrue("Javadoc must be null", actualJavadoc == null);
        //$NON-NLS-1$
        checkSourceRange(node, "void foo(final int i) {}", source);
    }

    /**
	 * Check javadoc for MethodDeclaration
	 */
    public void test0134() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0134", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a MethodDeclaration", node instanceof MethodDeclaration);
        Javadoc actualJavadoc = ((MethodDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        assertTrue("Javadoc must be null", actualJavadoc == null);
        //$NON-NLS-1$
        checkSourceRange(node, "void foo(final int i) {}", source);
    }

    /**
	 * Check javadoc for FieldDeclaration
	 * @deprecated marking deprecated since using deprecated code
	 */
    public void test0135() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0135", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a FieldDeclaration", node instanceof FieldDeclaration);
        //		Javadoc actualJavadoc = ((FieldDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        checkSourceRange(node, "/** JavaDoc Comment*/\n  int i;", source);
    }

    /**
	 * Check javadoc for FieldDeclaration
	 */
    public void test0136() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0136", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a FieldDeclaration", node instanceof FieldDeclaration);
        Javadoc actualJavadoc = ((FieldDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        assertTrue("Javadoc must be null", actualJavadoc == null);
        //$NON-NLS-1$
        checkSourceRange(node, "int i;", source);
    }

    /**
	 * Check javadoc for FieldDeclaration
	 */
    public void test0137() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0137", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a FieldDeclaration", node instanceof FieldDeclaration);
        Javadoc actualJavadoc = ((FieldDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        assertTrue("Javadoc must be null", actualJavadoc == null);
        //$NON-NLS-1$
        checkSourceRange(node, "int i;", source);
    }

    /**
	 * Check javadoc for TypeDeclaration
	 */
    public void test0138() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0138", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a TypeDeclaration", node instanceof TypeDeclaration);
        Javadoc actualJavadoc = ((TypeDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        assertTrue("Javadoc must be null", actualJavadoc == null);
        String expectedContents = //$NON-NLS-1$
        "public class Test {\n" + //$NON-NLS-1$
        "  int i;\n" + //$NON-NLS-1$
        "}";
        //$NON-NLS-1$
        checkSourceRange(node, expectedContents, source);
    }

    /**
	 * Check javadoc for TypeDeclaration
	 */
    public void test0139() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0139", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a TypeDeclaration", node instanceof TypeDeclaration);
        Javadoc actualJavadoc = ((TypeDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        assertTrue("Javadoc must be null", actualJavadoc == null);
        String expectedContents = //$NON-NLS-1$
        "public class Test {\n" + //$NON-NLS-1$
        "  int i;\n" + //$NON-NLS-1$
        "}";
        //$NON-NLS-1$
        checkSourceRange(node, expectedContents, source);
    }

    /**
	 * Check javadoc for TypeDeclaration
	 * @deprecated marking deprecated since using deprecated code
	 */
    public void test0140() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0140", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a TypeDeclaration", node instanceof TypeDeclaration);
        Javadoc actualJavadoc = ((TypeDeclaration) node).getJavadoc();
        String expectedContents = //$NON-NLS-1$
        "/** JavaDoc Comment*/\n" + //$NON-NLS-1$
        "public class Test {\n" + //$NON-NLS-1$
        "  int i;\n" + //$NON-NLS-1$
        "}";
        //$NON-NLS-1$
        checkSourceRange(node, expectedContents, source);
        //$NON-NLS-1$
        checkSourceRange(actualJavadoc, "/** JavaDoc Comment*/", source);
    }

    /**
	 * Check javadoc for MemberTypeDeclaration
	 * @deprecated marking deprecated since using deprecated code
	 */
    public void test0141() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0141", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a TypeDeclaration", node instanceof TypeDeclaration);
        Javadoc actualJavadoc = ((TypeDeclaration) node).getJavadoc();
        String expectedContents = //$NON-NLS-1$
        "/** JavaDoc Comment*/\n" + //$NON-NLS-1$
        "  class B {}";
        //$NON-NLS-1$
        checkSourceRange(node, expectedContents, source);
        //$NON-NLS-1$
        checkSourceRange(actualJavadoc, "/** JavaDoc Comment*/", source);
    }

    /**
	 * Check javadoc for MemberTypeDeclaration
	 */
    public void test0142() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0142", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a TypeDeclaration", node instanceof TypeDeclaration);
        Javadoc actualJavadoc = ((TypeDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        assertTrue("Javadoc must be null", actualJavadoc == null);
        //$NON-NLS-1$
        checkSourceRange(node, "class B {}", source);
    }

    /**
	 * Check javadoc for MemberTypeDeclaration
	 */
    public void test0143() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0143", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a TypeDeclaration", node instanceof TypeDeclaration);
        Javadoc actualJavadoc = ((TypeDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        assertTrue("Javadoc must be null", actualJavadoc == null);
        //$NON-NLS-1$
        checkSourceRange(node, "public static class B {}", source);
    }

    /**
	 * Check javadoc for MemberTypeDeclaration
	 */
    public void test0144() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0144", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a TypeDeclaration", node instanceof TypeDeclaration);
        Javadoc actualJavadoc = ((TypeDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        assertTrue("Javadoc must be null", actualJavadoc == null);
        //$NON-NLS-1$
        checkSourceRange(node, "public static class B {}", source);
    }

    /**
	 * Checking initializers
	 */
    public void test0145() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0145", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        checkSourceRange(node, "{}", source);
    }

    /**
	 * Checking initializers
	 */
    public void test0146() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0146", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        checkSourceRange(node, "static {}", source);
    }

    /**
	 * Checking initializers
	 * @deprecated marking deprecated since using deprecated code
	 */
    public void test0147() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0147", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        Javadoc actualJavadoc = ((Initializer) node).getJavadoc();
        //$NON-NLS-1$
        assertNotNull("Javadoc comment should no be null", actualJavadoc);
        String expectedContents = //$NON-NLS-1$
        "/** JavaDoc Comment*/\n" + //$NON-NLS-1$
        "  static {}";
        //$NON-NLS-1$
        checkSourceRange(node, expectedContents, source);
        //$NON-NLS-1$
        checkSourceRange(actualJavadoc, "/** JavaDoc Comment*/", source);
    }

    /**
	 * Checking initializers
	 * @deprecated marking deprecated since using deprecated code
	 */
    public void test0148() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0148", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        Javadoc actualJavadoc = ((Initializer) node).getJavadoc();
        //$NON-NLS-1$
        assertNotNull("Javadoc comment should not be null", actualJavadoc);
        String expectedContents = //$NON-NLS-1$
        "/** JavaDoc Comment*/\n" + //$NON-NLS-1$
        "  {}";
        //$NON-NLS-1$
        checkSourceRange(node, expectedContents, source);
        //$NON-NLS-1$
        checkSourceRange(actualJavadoc, "/** JavaDoc Comment*/", source);
    }

    /**
	 * Checking initializers
	 */
    public void test0149() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0149", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        Javadoc actualJavadoc = ((Initializer) node).getJavadoc();
        //$NON-NLS-1$
        assertNull("Javadoc comment should be null", actualJavadoc);
        //$NON-NLS-1$
        checkSourceRange(node, "{}", source);
    }

    /**
	 * Checking syntax error
	 */
    public void test0150() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0150", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit unit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertTrue("The compilation unit is malformed", !isMalformed(unit));
        //$NON-NLS-1$
        assertTrue("The package declaration is not malformed", isMalformed(unit.getPackage()));
        List imports = unit.imports();
        //$NON-NLS-1$
        assertTrue("The imports list size is not one", imports.size() == 1);
        //$NON-NLS-1$
        assertTrue("The first import is malformed", !isMalformed((ASTNode) imports.get(0)));
    }

    /**
	 * Checking syntax error
	 */
    public void test0151() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0151", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The compilation unit is malformed", !isMalformed(result));
    }

    /**
	 * Checking syntax error
	 */
    public void test0152() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0152", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The compilation unit is malformed", !isMalformed(result));
        ASTNode node = getASTNode((CompilationUnit) result, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The type is malformed", !isMalformed(node));
        node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The field is not malformed", isMalformed(node));
        node = getASTNode((CompilationUnit) result, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The method is not malformed", isMalformed(node));
    }

    /**
	 * Checking syntax error
	 */
    public void test0153() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0153", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The compilation unit is malformed", !isMalformed(result));
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The method is not original", isOriginal(node));
        //$NON-NLS-1$
        assertTrue("The method is not malformed", isMalformed(node));
    }

    /**
	 * Checking binding of package declaration
	 */
    public void test0154() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0154", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        IBinding binding = compilationUnit.getPackage().getName().resolveBinding();
        //$NON-NLS-1$
        assertNotNull("The package binding is null", binding);
        //$NON-NLS-1$
        assertTrue("The binding is not a package binding", binding instanceof IPackageBinding);
        IPackageBinding packageBinding = (IPackageBinding) binding;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The package name is incorrect", "test0154", packageBinding.getName());
        IBinding binding2 = compilationUnit.getPackage().getName().resolveBinding();
        //$NON-NLS-1$
        assertTrue("The package binding is not canonical", binding == binding2);
    }

    /**
	 * Checking arguments positions
	 */
    public void test0155() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0155", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertTrue("The result is not a method declaration", node instanceof MethodDeclaration);
        MethodDeclaration methodDecl = (MethodDeclaration) node;
        List parameters = methodDecl.parameters();
        //$NON-NLS-1$
        assertTrue("The parameters size is different from 2", parameters.size() == 2);
        Object parameter = parameters.get(0);
        //$NON-NLS-1$
        assertTrue("The parameter is not a SingleVariableDeclaration", parameter instanceof SingleVariableDeclaration);
        //$NON-NLS-1$
        checkSourceRange((ASTNode) parameter, "int i", source);
        parameter = parameters.get(1);
        //$NON-NLS-1$
        assertTrue("The parameter is not a SingleVariableDeclaration", parameter instanceof SingleVariableDeclaration);
        //$NON-NLS-1$
        checkSourceRange((ASTNode) parameter, "final boolean b", source);
    }

    /**
	 * Checking arguments positions
	 */
    public void test0156() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0156", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertTrue("The result is not a method declaration", node instanceof MethodDeclaration);
        MethodDeclaration methodDecl = (MethodDeclaration) node;
        List parameters = methodDecl.parameters();
        //$NON-NLS-1$
        assertTrue("The parameters size is different from 1", parameters.size() == 1);
        Object parameter = parameters.get(0);
        //$NON-NLS-1$
        assertTrue("The parameter is not a SingleVariableDeclaration", parameter instanceof SingleVariableDeclaration);
        //$NON-NLS-1$
        checkSourceRange((ASTNode) parameter, "int i", source);
        Block block = methodDecl.getBody();
        List statements = block.statements();
        //$NON-NLS-1$
        assertTrue("The statements size is different from 2", statements.size() == 2);
        ASTNode statement = (ASTNode) statements.get(0);
        //$NON-NLS-1$
        assertTrue("The statements[0] is a postfixExpression statement", statement instanceof ExpressionStatement);
    }

    /**
	 * Check canonic binding for fields
	 */
    public void test0157() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "", "Test0157.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        List types = compilationUnit.types();
        //$NON-NLS-1$
        assertTrue("The types list is empty", types.size() != 0);
        TypeDeclaration typeDeclaration = (TypeDeclaration) types.get(0);
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("Type binding is null", typeBinding);
        //$NON-NLS-1$
        assertTrue("The type binding is canonical", typeBinding == typeDeclaration.resolveBinding());
        List bodyDeclarations = typeDeclaration.bodyDeclarations();
        //$NON-NLS-1$
        assertTrue("The body declaration list is empty", bodyDeclarations.size() != 0);
        BodyDeclaration bodyDeclaration = (BodyDeclaration) bodyDeclarations.get(0);
        //$NON-NLS-1$
        assertTrue("This is not a field", bodyDeclaration instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) bodyDeclaration;
        List variableFragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertTrue("The fragment list is empty", variableFragments.size() != 0);
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) variableFragments.get(0);
        IVariableBinding variableBinding = fragment.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("the field binding is null", variableBinding);
        assertFalse("Not a parameter", variableBinding.isParameter());
        //$NON-NLS-1$
        assertTrue("The field binding is not canonical", variableBinding == fragment.resolveBinding());
        typeBinding = variableBinding.getType();
        //$NON-NLS-1$
        assertTrue("The type is not an array type", typeBinding.isArray());
        //$NON-NLS-1$
        assertTrue("The type binding for the field is not canonical", typeBinding == variableBinding.getType());
        SimpleName name = fragment.getName();
        //$NON-NLS-1$
        assertTrue("is a declaration", name.isDeclaration());
        IBinding binding = name.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertEquals("wrong type", IBinding.VARIABLE, binding.getKind());
        //$NON-NLS-1$
        assertTrue("not a field", ((IVariableBinding) binding).isField());
    }

    /**
	 * Check canonic bindings for fields
	 */
    public void test0158() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "", "Test0158.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        List types = compilationUnit.types();
        //$NON-NLS-1$
        assertTrue("The types list is empty", types.size() != 0);
        TypeDeclaration typeDeclaration = (TypeDeclaration) types.get(0);
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("Type binding is null", typeBinding);
        //$NON-NLS-1$
        assertTrue("The type binding is canonical", typeBinding == typeDeclaration.resolveBinding());
        SimpleName simpleName = typeDeclaration.getName();
        //$NON-NLS-1$
        assertTrue("is a declaration", simpleName.isDeclaration());
        IBinding binding = simpleName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertEquals("wrong type", IBinding.TYPE, binding.getKind());
        //$NON-NLS-1$
        assertEquals("wrong name", simpleName.getIdentifier(), binding.getName());
        List bodyDeclarations = typeDeclaration.bodyDeclarations();
        //$NON-NLS-1$
        assertTrue("The body declaration list is empty", bodyDeclarations.size() != 0);
        BodyDeclaration bodyDeclaration = (BodyDeclaration) bodyDeclarations.get(0);
        //$NON-NLS-1$
        assertTrue("This is not a field", bodyDeclaration instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) bodyDeclaration;
        List variableFragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertTrue("The fragment list is empty", variableFragments.size() != 0);
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) variableFragments.get(0);
        IVariableBinding variableBinding = fragment.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("the field binding is null", variableBinding);
        //$NON-NLS-1$
        assertTrue("The field binding is not canonical", variableBinding == fragment.resolveBinding());
        ITypeBinding typeBinding2 = variableBinding.getType();
        //$NON-NLS-1$
        assertTrue("The type is not an array type", typeBinding2.isArray());
        //$NON-NLS-1$
        assertTrue("The type binding for the field is not canonical", typeBinding2 == variableBinding.getType());
        //$NON-NLS-1$
        assertTrue("The type binding for the field is not canonical with the declaration type binding", typeBinding == typeBinding2.getElementType());
    }

    /**
	 * Define an anonymous type
	 */
    public void test0159() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0159", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The result is not a compilation unit", result instanceof CompilationUnit);
    }

    /**
	 * Check bindings for multiple field declarations
	 */
    public void test0160() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0160", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        List types = compilationUnit.types();
        //$NON-NLS-1$
        assertTrue("The types list is empty", types.size() != 0);
        TypeDeclaration typeDeclaration = (TypeDeclaration) types.get(0);
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("Type binding is null", typeBinding);
        //$NON-NLS-1$
        assertTrue("The type binding is canonical", typeBinding == typeDeclaration.resolveBinding());
        List bodyDeclarations = typeDeclaration.bodyDeclarations();
        //$NON-NLS-1$
        assertTrue("The body declaration list is empty", bodyDeclarations.size() != 0);
        BodyDeclaration bodyDeclaration = (BodyDeclaration) bodyDeclarations.get(0);
        //$NON-NLS-1$
        assertTrue("This is not a field", bodyDeclaration instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) bodyDeclaration;
        List variableFragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertTrue("The fragment list size is not 2", variableFragments.size() == 2);
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) variableFragments.get(0);
        IVariableBinding variableBinding1 = fragment.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("the field binding is null", variableBinding1);
        //$NON-NLS-1$
        assertTrue("The field binding is not canonical", variableBinding1 == fragment.resolveBinding());
        ITypeBinding type1 = variableBinding1.getType();
        //$NON-NLS-1$
        assertNotNull("The type is null", type1);
        //$NON-NLS-1$
        assertTrue("The field type is canonical", type1 == variableBinding1.getType());
        //$NON-NLS-1$
        assertTrue("The type is not an array type", type1.isArray());
        //$NON-NLS-1$
        assertTrue("The type dimension is 1", type1.getDimensions() == 1);
        fragment = (VariableDeclarationFragment) variableFragments.get(1);
        IVariableBinding variableBinding2 = fragment.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("the field binding is null", variableBinding2);
        //$NON-NLS-1$
        assertTrue("The field binding is not canonical", variableBinding2 == fragment.resolveBinding());
        ITypeBinding type2 = variableBinding2.getType();
        type2 = variableBinding2.getType();
        //$NON-NLS-1$
        assertNotNull("The type is null", type2);
        //$NON-NLS-1$
        assertTrue("The field type is canonical", type2 == variableBinding2.getType());
        //$NON-NLS-1$
        assertTrue("The type is not an array type", type2.isArray());
        //$NON-NLS-1$
        assertTrue("The type dimension is 2", type2.getDimensions() == 2);
        //$NON-NLS-1$
        assertTrue("Element type is canonical", type1.getElementType() == type2.getElementType());
        //$NON-NLS-1$
        assertTrue("type1.id < type2.id", variableBinding1.getVariableId() < variableBinding2.getVariableId());
    }

    /**
	 * Check ITypeBinding APIs:
	 *  - getModifiers()
	 *  - getElementType() when it is not an array type
	 *  - getDimensions() when it is not an array type
	 *  - getDeclaringClass()
	 *  - getDeclaringName()
	 *  - getName()
	 *  - isNested()
	 *  - isAnonymous()
	 *  - isLocal()
	 *  - isMember()
	 *  - isArray()
	 *  - getDeclaredMethods() => returns binding for default constructor
	 *  - isPrimitive()
	 *  - isTopLevel()
	 *  - getSuperclass()
	 */
    public void test0161() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0161", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        List types = compilationUnit.types();
        //$NON-NLS-1$
        assertTrue("The types list is empty", types.size() != 0);
        TypeDeclaration typeDeclaration = (TypeDeclaration) types.get(0);
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("The type binding should not be null", typeBinding);
        //$NON-NLS-1$
        assertEquals("The modifier is PUBLIC", Modifier.PUBLIC, typeBinding.getModifiers());
        //$NON-NLS-1$
        assertNull("There is no element type", typeBinding.getElementType());
        //$NON-NLS-1$
        assertEquals("There is no dimension", 0, typeBinding.getDimensions());
        //$NON-NLS-1$
        assertNull("This is not a member type", typeBinding.getDeclaringClass());
        IMethodBinding[] methods = typeBinding.getDeclaredMethods();
        //$NON-NLS-1$
        assertEquals("Contains the default constructor", 1, methods.length);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The name is not Test", "Test", typeBinding.getName());
        //$NON-NLS-1$
        assertTrue("An anonymous class", !typeBinding.isAnonymous());
        //$NON-NLS-1$
        assertTrue("A local class", !typeBinding.isLocal());
        //$NON-NLS-1$
        assertTrue("A nested class", !typeBinding.isNested());
        //$NON-NLS-1$
        assertTrue("A member class", !typeBinding.isMember());
        //$NON-NLS-1$
        assertTrue("An array", !typeBinding.isArray());
        //$NON-NLS-1$
        assertTrue("Not a class", typeBinding.isClass());
        //$NON-NLS-1$
        assertTrue("An interface", !typeBinding.isInterface());
        //$NON-NLS-1$
        assertTrue("Not from source", typeBinding.isFromSource());
        //$NON-NLS-1$
        assertTrue("Is nested", typeBinding.isTopLevel());
        //$NON-NLS-1$
        assertTrue("A primitive type", !typeBinding.isPrimitive());
        ITypeBinding superclass = typeBinding.getSuperclass();
        //$NON-NLS-1$
        assertNotNull("No superclass", superclass);
        //$NON-NLS-1$
        assertTrue("From source", !superclass.isFromSource());
        ITypeBinding supersuperclass = superclass.getSuperclass();
        //$NON-NLS-1$
        assertNull("No superclass for java.lang.Object", supersuperclass);
        ITypeBinding[] interfaces = typeBinding.getInterfaces();
        //$NON-NLS-1$
        assertNotNull("No interfaces", interfaces);
        //$NON-NLS-1$
        assertEquals("More then one super interface", 1, interfaces.length);
        //$NON-NLS-1$
        assertTrue("is not an interface", interfaces[0].isInterface());
        //$NON-NLS-1$
        assertTrue("From source", !interfaces[0].isFromSource());
        //$NON-NLS-1$
        assertEquals("Has fields", 0, typeBinding.getDeclaredFields().length);
    }

    /**
	 * Check ITypeBinding APIs:
	 *  - getModifiers()
	 *  - getElementType() when it is not an array type
	 *  - getDimensions() when it is not an array type
	 *  - getDeclaringClass()
	 *  - getDeclaringName()
	 *  - getName()
	 *  - isNested()
	 *  - isAnonymous()
	 *  - isLocal()
	 *  - isMember()
	 *  - isArray()
	 *  - getDeclaredMethods() => returns binding for default constructor
	 *  - isPrimitive()
	 *  - isTopLevel()
	 *  - getSuperclass()
	 */
    public void test0162() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0162", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        List types = compilationUnit.types();
        //$NON-NLS-1$
        assertTrue("The types list is empty", types.size() != 0);
        TypeDeclaration typeDeclaration = (TypeDeclaration) types.get(0);
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("The type binding should not be null", typeBinding);
        //$NON-NLS-1$
        assertEquals("The modifier is PUBLIC", Modifier.PUBLIC, typeBinding.getModifiers());
        //$NON-NLS-1$
        assertNull("There is no element type", typeBinding.getElementType());
        //$NON-NLS-1$
        assertEquals("There is no dimension", 0, typeBinding.getDimensions());
        //$NON-NLS-1$
        assertNull("This is not a member type", typeBinding.getDeclaringClass());
        IMethodBinding[] methods = typeBinding.getDeclaredMethods();
        //$NON-NLS-1$
        assertEquals("Contains no methos", 0, methods.length);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The name is not Test", "Test", typeBinding.getName());
        //$NON-NLS-1$
        assertTrue("An anonymous class", !typeBinding.isAnonymous());
        //$NON-NLS-1$
        assertTrue("A local class", !typeBinding.isLocal());
        //$NON-NLS-1$
        assertTrue("A nested class", !typeBinding.isNested());
        //$NON-NLS-1$
        assertTrue("A member class", !typeBinding.isMember());
        //$NON-NLS-1$
        assertTrue("An array", !typeBinding.isArray());
        //$NON-NLS-1$
        assertTrue("A class", !typeBinding.isClass());
        //$NON-NLS-1$
        assertTrue("Not an interface", typeBinding.isInterface());
        //$NON-NLS-1$
        assertTrue("Not from source", typeBinding.isFromSource());
        //$NON-NLS-1$
        assertTrue("Is nested", typeBinding.isTopLevel());
        //$NON-NLS-1$
        assertTrue("A primitive type", !typeBinding.isPrimitive());
        ITypeBinding superclass = typeBinding.getSuperclass();
        //$NON-NLS-1$
        assertNull("No superclass", superclass);
        //$NON-NLS-1$
        assertEquals("Has fields", 0, typeBinding.getDeclaredFields().length);
    }

    /**
	 * Test binding for anonymous declaration: new java.lang.Object() {}
	 */
    public void test0163() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0163", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        //$NON-NLS-1$
        assertTrue("Not an anonymous type declaration", expression instanceof ClassInstanceCreation);
        ClassInstanceCreation anonymousClass = (ClassInstanceCreation) expression;
        ITypeBinding typeBinding = anonymousClass.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$
        assertTrue("Not an anonymous class", typeBinding.isAnonymous());
        //$NON-NLS-1$
        assertEquals("The modifier is not default", Modifier.NONE, typeBinding.getModifiers());
        //$NON-NLS-1$
        assertNull("There is no element type", typeBinding.getElementType());
        //$NON-NLS-1$
        assertEquals("There is no dimension", 0, typeBinding.getDimensions());
        //$NON-NLS-1$
        assertNotNull("This is a member type", typeBinding.getDeclaringClass());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The name is not empty", "", typeBinding.getName());
        IMethodBinding[] methods = typeBinding.getDeclaredMethods();
        //$NON-NLS-1$
        assertEquals("Contains the default constructor", 1, methods.length);
        //$NON-NLS-1$
        assertTrue("Not a local class", typeBinding.isLocal());
        //$NON-NLS-1$
        assertTrue("Not a nested class", typeBinding.isNested());
        //$NON-NLS-1$
        assertTrue("A member class", !typeBinding.isMember());
        //$NON-NLS-1$
        assertTrue("An array", !typeBinding.isArray());
        //$NON-NLS-1$
        assertTrue("Not a class", typeBinding.isClass());
        //$NON-NLS-1$
        assertTrue("An interface", !typeBinding.isInterface());
        //$NON-NLS-1$
        assertTrue("Not from source", typeBinding.isFromSource());
        //$NON-NLS-1$
        assertTrue("Is a top level", !typeBinding.isTopLevel());
        //$NON-NLS-1$
        assertTrue("A primitive type", !typeBinding.isPrimitive());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong qualified name", "", typeBinding.getQualifiedName());
        ITypeBinding superclass = typeBinding.getSuperclass();
        //$NON-NLS-1$
        assertNotNull("No superclass", superclass);
        //$NON-NLS-1$
        assertEquals("Has fields", 0, typeBinding.getDeclaredFields().length);
    }

    /**
	 * Test binding for member type declaration
	 */
    public void test0164() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0164", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("Not an type declaration", node instanceof TypeDeclaration);
        TypeDeclaration typeDeclaration = (TypeDeclaration) node;
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$
        assertTrue("An anonymous class", !typeBinding.isAnonymous());
        //$NON-NLS-1$
        assertEquals("The modifier is not default", Modifier.PRIVATE, typeBinding.getModifiers());
        //$NON-NLS-1$
        assertNull("There is no element type", typeBinding.getElementType());
        //$NON-NLS-1$
        assertEquals("There is no dimension", 0, typeBinding.getDimensions());
        //$NON-NLS-1$
        assertNotNull("This is not a member type", typeBinding.getDeclaringClass());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The name is not 'B'", "B", typeBinding.getName());
        IMethodBinding[] methods = typeBinding.getDeclaredMethods();
        //$NON-NLS-1$
        assertEquals("Contains the default constructor", 1, methods.length);
        //$NON-NLS-1$
        assertTrue("A local class", !typeBinding.isLocal());
        //$NON-NLS-1$
        assertTrue("Not a nested class", typeBinding.isNested());
        //$NON-NLS-1$
        assertTrue("Not a member class", typeBinding.isMember());
        //$NON-NLS-1$
        assertTrue("An array", !typeBinding.isArray());
        //$NON-NLS-1$
        assertTrue("Not a class", typeBinding.isClass());
        //$NON-NLS-1$
        assertTrue("An interface", !typeBinding.isInterface());
        //$NON-NLS-1$
        assertTrue("Not from source", typeBinding.isFromSource());
        //$NON-NLS-1$
        assertTrue("Is a top level", !typeBinding.isTopLevel());
        //$NON-NLS-1$
        assertTrue("A primitive type", !typeBinding.isPrimitive());
        ITypeBinding superclass = typeBinding.getSuperclass();
        //$NON-NLS-1$
        assertNotNull("No superclass", superclass);
        //$NON-NLS-1$
        assertEquals("Has fields", 0, typeBinding.getDeclaredFields().length);
    }

    /**
	 * Test binding for local type declaration
	 */
    public void test0165() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0165", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("Not an type declaration", node instanceof TypeDeclarationStatement);
        TypeDeclarationStatement statement = (TypeDeclarationStatement) node;
        AbstractTypeDeclaration typeDeclaration = statement.getDeclaration();
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$
        assertTrue("An anonymous class", !typeBinding.isAnonymous());
        //$NON-NLS-1$
        assertEquals("The modifier is not default", Modifier.NONE, typeBinding.getModifiers());
        //$NON-NLS-1$
        assertNull("There is no element type", typeBinding.getElementType());
        //$NON-NLS-1$
        assertEquals("There is no dimension", 0, typeBinding.getDimensions());
        //$NON-NLS-1$
        assertNotNull("This is not a member type", typeBinding.getDeclaringClass());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The name is not 'C'", "C", typeBinding.getName());
        IMethodBinding[] methods = typeBinding.getDeclaredMethods();
        //$NON-NLS-1$
        assertEquals("Contains the default constructor", 1, methods.length);
        //$NON-NLS-1$
        assertTrue("Not a local class", typeBinding.isLocal());
        //$NON-NLS-1$
        assertTrue("Not a nested class", typeBinding.isNested());
        //$NON-NLS-1$
        assertTrue("A member class", !typeBinding.isMember());
        //$NON-NLS-1$
        assertTrue("An array", !typeBinding.isArray());
        //$NON-NLS-1$
        assertTrue("Not a class", typeBinding.isClass());
        //$NON-NLS-1$
        assertTrue("An interface", !typeBinding.isInterface());
        //$NON-NLS-1$
        assertTrue("Not from source", typeBinding.isFromSource());
        //$NON-NLS-1$
        assertTrue("Is a top level", !typeBinding.isTopLevel());
        //$NON-NLS-1$
        assertTrue("A primitive type", !typeBinding.isPrimitive());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong qualified name", "", typeBinding.getQualifiedName());
        ITypeBinding superclass = typeBinding.getSuperclass();
        //$NON-NLS-1$
        assertNotNull("No superclass", superclass);
        //$NON-NLS-1$
        assertEquals("Has fields", 0, typeBinding.getDeclaredFields().length);
    }

    /**
	 * Multiple local declaration => VariabledeclarationStatement
	 */
    public void test0166() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0166", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertTrue("Fragment list is not 4 ", fragments.size() == 4);
        VariableDeclarationFragment fragment1 = (VariableDeclarationFragment) fragments.get(0);
        IVariableBinding binding1 = fragment1.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("Binding is null", binding1);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name for binding1", "x", binding1.getName());
        //$NON-NLS-1$
        assertEquals("wrong modifier for binding1", 0, binding1.getModifiers());
        //$NON-NLS-1$
        assertTrue("a field", !binding1.isField());
        //$NON-NLS-1$
        assertNull("declaring class is not null", binding1.getDeclaringClass());
        ITypeBinding typeBinding1 = binding1.getType();
        //$NON-NLS-1$
        assertNotNull("typeBinding1 is null", typeBinding1);
        //$NON-NLS-1$
        assertTrue("typeBinding1 is not a primitive type", typeBinding1.isPrimitive());
        //$NON-NLS-1$
        assertTrue("typeBinding1 is not canonical", typeBinding1 == binding1.getType());
        VariableDeclarationFragment fragment2 = (VariableDeclarationFragment) fragments.get(1);
        IVariableBinding binding2 = fragment2.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("Binding is null", binding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name for binding2", "z", binding2.getName());
        //$NON-NLS-1$
        assertEquals("wrong modifier for binding2", 0, binding2.getModifiers());
        //$NON-NLS-1$
        assertTrue("a field", !binding2.isField());
        //$NON-NLS-1$
        assertNull("declaring class is not null", binding2.getDeclaringClass());
        ITypeBinding typeBinding2 = binding2.getType();
        //$NON-NLS-1$
        assertNotNull("typeBinding2 is null", typeBinding2);
        //$NON-NLS-1$
        assertTrue("typeBinding2 is not an array type", typeBinding2.isArray());
        //$NON-NLS-1$
        assertTrue("typeBinding2 is not canonical", typeBinding2 == binding2.getType());
        //$NON-NLS-1$
        assertTrue("primitive type is not canonical", typeBinding1 == typeBinding2.getElementType());
        //$NON-NLS-1$
        assertEquals("dimension is 1", 1, typeBinding2.getDimensions());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("it is not int[]", "int[]", typeBinding2.getName());
        VariableDeclarationFragment fragment3 = (VariableDeclarationFragment) fragments.get(2);
        IVariableBinding binding3 = fragment3.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("Binding is null", binding3);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name for binding3", "i", binding3.getName());
        //$NON-NLS-1$
        assertEquals("wrong modifier for binding3", 0, binding3.getModifiers());
        //$NON-NLS-1$
        assertTrue("a field", !binding3.isField());
        //$NON-NLS-1$
        assertNull("declaring class is not null", binding3.getDeclaringClass());
        ITypeBinding typeBinding3 = binding3.getType();
        //$NON-NLS-1$
        assertNotNull("typeBinding3 is null", typeBinding3);
        //$NON-NLS-1$
        assertTrue("typeBinding3 is not an primitive type", typeBinding3.isPrimitive());
        //$NON-NLS-1$
        assertTrue("typeBinding3 is not canonical", typeBinding3 == binding3.getType());
        //$NON-NLS-1$
        assertTrue("primitive type is not canonical", typeBinding1 == typeBinding3);
        //$NON-NLS-1$
        assertEquals("dimension is 0", 0, typeBinding3.getDimensions());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("it is not the primitive type int", "int", typeBinding3.getName());
        VariableDeclarationFragment fragment4 = (VariableDeclarationFragment) fragments.get(3);
        IVariableBinding binding4 = fragment4.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("Binding is null", binding4);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name for binding4", "j", binding4.getName());
        //$NON-NLS-1$
        assertEquals("wrong modifier for binding4", 0, binding4.getModifiers());
        //$NON-NLS-1$
        assertTrue("a field", !binding4.isField());
        //$NON-NLS-1$
        assertNull("declaring class is not null", binding4.getDeclaringClass());
        ITypeBinding typeBinding4 = binding4.getType();
        //$NON-NLS-1$
        assertNotNull("typeBinding4 is null", typeBinding4);
        //$NON-NLS-1$
        assertTrue("typeBinding4 is not an array type", typeBinding4.isArray());
        //$NON-NLS-1$
        assertTrue("typeBinding4 is not canonical", typeBinding4 == binding4.getType());
        //$NON-NLS-1$
        assertTrue("primitive type is not canonical", typeBinding1 == typeBinding4.getElementType());
        //$NON-NLS-1$
        assertEquals("dimension is 2", 2, typeBinding4.getDimensions());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("it is not int[][]", "int[][]", typeBinding4.getName());
        //$NON-NLS-1$
        assertTrue("ids in the wrong order", binding1.getVariableId() < binding2.getVariableId());
        //$NON-NLS-1$
        assertTrue("ids in the wrong order", binding2.getVariableId() < binding3.getVariableId());
        //$NON-NLS-1$
        assertTrue("ids in the wrong order", binding3.getVariableId() < binding4.getVariableId());
    }

    /**
	 * Check source position for new Test[1+2].length.
	 */
    public void test0167() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0167", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("Instance of VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertTrue("fragment list size is not 1", fragments.size() == 1);
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        Expression initialization = fragment.getInitializer();
        //$NON-NLS-1$
        assertNotNull("No initialization", initialization);
        //$NON-NLS-1$
        assertTrue("Not a FieldAccess", initialization instanceof FieldAccess);
        //$NON-NLS-1$
        checkSourceRange(initialization, "new Test[1+2].length", source);
    }

    /**
	 * Check package binding: test0168.test
	 */
    public void test0168() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0168.test1", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        List types = compilationUnit.types();
        //$NON-NLS-1$
        assertTrue("The types list is empty", types.size() != 0);
        TypeDeclaration typeDeclaration = (TypeDeclaration) types.get(0);
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("Binding not null", typeBinding);
        IPackageBinding packageBinding = typeBinding.getPackage();
        //$NON-NLS-1$
        assertNotNull("No package binding", packageBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "test0168.test1", packageBinding.getName());
        String[] components = packageBinding.getNameComponents();
        //$NON-NLS-1$
        assertNotNull("no components", components);
        //$NON-NLS-1$
        assertTrue("components size != 2", components.length == 2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong component name", "test0168", components[0]);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong component name", "test1", components[1]);
        //$NON-NLS-1$
        assertEquals("wrong type", IBinding.PACKAGE, packageBinding.getKind());
        //$NON-NLS-1$
        assertTrue("Unnamed package", !packageBinding.isUnnamed());
        //$NON-NLS-1$
        assertTrue("Package binding is not canonical", packageBinding == typeBinding.getPackage());
    }

    /**
	 * Check package binding: test0169
	 */
    public void test0169() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0169", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        List types = compilationUnit.types();
        //$NON-NLS-1$
        assertTrue("The types list is empty", types.size() != 0);
        TypeDeclaration typeDeclaration = (TypeDeclaration) types.get(0);
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("Binding not null", typeBinding);
        IPackageBinding packageBinding = typeBinding.getPackage();
        //$NON-NLS-1$
        assertNotNull("No package binding", packageBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "test0169", packageBinding.getName());
        String[] components = packageBinding.getNameComponents();
        //$NON-NLS-1$
        assertNotNull("no components", components);
        //$NON-NLS-1$
        assertTrue("components size != 1", components.length == 1);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong component name", "test0169", components[0]);
        //$NON-NLS-1$
        assertEquals("wrong type", IBinding.PACKAGE, packageBinding.getKind());
        //$NON-NLS-1$
        assertTrue("Unnamed package", !packageBinding.isUnnamed());
        //$NON-NLS-1$
        assertTrue("Package binding is not canonical", packageBinding == typeBinding.getPackage());
    }

    /**
	 * Check package binding: test0170
	 */
    public void test0170() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "", "Test0170.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        List types = compilationUnit.types();
        //$NON-NLS-1$
        assertTrue("The types list is empty", types.size() != 0);
        TypeDeclaration typeDeclaration = (TypeDeclaration) types.get(0);
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("Binding not null", typeBinding);
        IPackageBinding packageBinding = typeBinding.getPackage();
        //$NON-NLS-1$
        assertNotNull("No package binding", packageBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "", packageBinding.getName());
        String[] components = packageBinding.getNameComponents();
        //$NON-NLS-1$
        assertNotNull("no components", components);
        //$NON-NLS-1$
        assertTrue("components size != 0", components.length == 0);
        //$NON-NLS-1$
        assertEquals("wrong type", IBinding.PACKAGE, packageBinding.getKind());
        //$NON-NLS-1$
        assertTrue("Not an unnamed package", packageBinding.isUnnamed());
        //$NON-NLS-1$
        assertTrue("Package binding is not canonical", packageBinding == typeBinding.getPackage());
    }

    /**
	 * Check package binding: test0171
	 */
    public void test0171() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0171", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        List types = compilationUnit.types();
        //$NON-NLS-1$
        assertTrue("The types list is empty", types.size() == 2);
        TypeDeclaration typeDeclaration = (TypeDeclaration) types.get(0);
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("Binding not null", typeBinding);
        IPackageBinding packageBinding = typeBinding.getPackage();
        //$NON-NLS-1$
        assertNotNull("No package binding", packageBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "test0171", packageBinding.getName());
        String[] components = packageBinding.getNameComponents();
        //$NON-NLS-1$
        assertNotNull("no components", components);
        //$NON-NLS-1$
        assertTrue("components size != 1", components.length == 1);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong component name", "test0171", components[0]);
        //$NON-NLS-1$
        assertEquals("wrong type", IBinding.PACKAGE, packageBinding.getKind());
        //$NON-NLS-1$
        assertTrue("Unnamed package", !packageBinding.isUnnamed());
        //$NON-NLS-1$
        assertTrue("Package binding is not canonical", packageBinding == typeBinding.getPackage());
        typeDeclaration = (TypeDeclaration) types.get(1);
        typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("Binding not null", typeBinding);
        IPackageBinding packageBinding2 = typeBinding.getPackage();
        //$NON-NLS-1$
        assertNotNull("No package binding", packageBinding);
        //$NON-NLS-1$
        assertTrue("Package binding is not canonical", packageBinding == packageBinding2);
    }

    /**
	 * Check method binding
	 */
    public void test0172() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0172", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        List types = compilationUnit.types();
        //$NON-NLS-1$
        assertTrue("The types list is empty", types.size() != 0);
        TypeDeclaration typeDeclaration = (TypeDeclaration) types.get(0);
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("Binding not null", typeBinding);
        IMethodBinding[] methods = typeBinding.getDeclaredMethods();
        //$NON-NLS-1$
        assertEquals("methods.length != 4", 4, methods.length);
        List bodyDeclarations = typeDeclaration.bodyDeclarations();
        //$NON-NLS-1$
        assertEquals("body declaration size != 3", 3, bodyDeclarations.size());
        MethodDeclaration method1 = (MethodDeclaration) bodyDeclarations.get(0);
        IMethodBinding methodBinding1 = method1.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No method binding for foo", methodBinding1);
        SimpleName simpleName = method1.getName();
        //$NON-NLS-1$
        assertTrue("not a declaration", simpleName.isDeclaration());
        IBinding binding = simpleName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertEquals("wrong name", binding.getName(), simpleName.getIdentifier());
        // search method foo
        IMethodBinding methodBinding = null;
        loop: for (int i = 0, max = methods.length; i < max; i++) {
            IMethodBinding currentMethod = methods[i];
            if ("foo".equals(currentMethod.getName())) {
                methodBinding = currentMethod;
                break loop;
            }
        }
        assertNotNull("Cannot be null", methodBinding);
        //$NON-NLS-1$
        assertTrue("Canonical method binding", methodBinding1 == methodBinding);
        //$NON-NLS-1$
        assertTrue("declaring class is canonical", typeBinding == methodBinding1.getDeclaringClass());
        ITypeBinding[] exceptionTypes = methodBinding1.getExceptionTypes();
        //$NON-NLS-1$
        assertNotNull("No exception types", exceptionTypes);
        //$NON-NLS-1$
        assertEquals("One exception", 1, exceptionTypes.length);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name for exception", "IOException", exceptionTypes[0].getName());
        //$NON-NLS-1$
        assertEquals("wrong modifier", Modifier.NONE, methodBinding1.getModifiers());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name for method", "foo", methodBinding1.getName());
        ITypeBinding[] parameters = methodBinding1.getParameterTypes();
        //$NON-NLS-1$
        assertNotNull("No parameters", parameters);
        //$NON-NLS-1$
        assertEquals("wrong size", 1, parameters.length);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong type", "int[]", parameters[0].getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong return type", "void", methodBinding1.getReturnType().getName());
        //$NON-NLS-1$
        assertTrue("A constructor", !methodBinding1.isConstructor());
        MethodDeclaration method2 = (MethodDeclaration) bodyDeclarations.get(1);
        IMethodBinding methodBinding2 = method2.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No method binding for main", methodBinding2);
        // search main
        methodBinding = null;
        loop: for (int i = 0, max = methods.length; i < max; i++) {
            IMethodBinding currentMethod = methods[i];
            if ("main".equals(currentMethod.getName())) {
                methodBinding = currentMethod;
                break loop;
            }
        }
        assertNotNull("Cannot be null", methodBinding);
        //$NON-NLS-1$
        assertTrue("Canonical method binding", methodBinding2 == methodBinding);
        //$NON-NLS-1$
        assertTrue("declaring class is canonical", typeBinding == methodBinding2.getDeclaringClass());
        ITypeBinding[] exceptionTypes2 = methodBinding2.getExceptionTypes();
        //$NON-NLS-1$
        assertNotNull("No exception types", exceptionTypes2);
        //$NON-NLS-1$
        assertEquals("No exception", 0, exceptionTypes2.length);
        //$NON-NLS-1$
        assertEquals("wrong modifier", Modifier.PUBLIC | Modifier.STATIC, methodBinding2.getModifiers());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name for method", "main", methodBinding2.getName());
        ITypeBinding[] parameters2 = methodBinding2.getParameterTypes();
        //$NON-NLS-1$
        assertNotNull("No parameters", parameters2);
        //$NON-NLS-1$
        assertEquals("wrong size", 1, parameters2.length);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong type for parameter2[0]", "String[]", parameters2[0].getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong return type", "void", methodBinding2.getReturnType().getName());
        //$NON-NLS-1$
        assertTrue("A constructor", !methodBinding2.isConstructor());
        MethodDeclaration method3 = (MethodDeclaration) bodyDeclarations.get(2);
        IMethodBinding methodBinding3 = method3.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No method binding for bar", methodBinding3);
        // search method bar
        methodBinding = null;
        loop: for (int i = 0, max = methods.length; i < max; i++) {
            IMethodBinding currentMethod = methods[i];
            if ("bar".equals(currentMethod.getName())) {
                methodBinding = currentMethod;
                break loop;
            }
        }
        assertNotNull("Cannot be null", methodBinding);
        //$NON-NLS-1$
        assertTrue("Canonical method binding", methodBinding3 == methodBinding);
        //$NON-NLS-1$
        assertTrue("declaring class is canonical", typeBinding == methodBinding3.getDeclaringClass());
        ITypeBinding[] exceptionTypes3 = methodBinding3.getExceptionTypes();
        //$NON-NLS-1$
        assertNotNull("No exception types", exceptionTypes3);
        //$NON-NLS-1$
        assertEquals("No exception", 1, exceptionTypes3.length);
        //$NON-NLS-1$
        assertEquals("wrong modifier", Modifier.PRIVATE, methodBinding3.getModifiers());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name for method", "bar", methodBinding3.getName());
        ITypeBinding[] parameters3 = methodBinding3.getParameterTypes();
        //$NON-NLS-1$
        assertNotNull("No parameters", parameters3);
        //$NON-NLS-1$
        assertEquals("wrong size", 1, parameters3.length);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong type", "String", parameters3[0].getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong return type", "String", methodBinding3.getReturnType().getName());
        //$NON-NLS-1$
        assertTrue("A constructor", !methodBinding3.isConstructor());
        //$NON-NLS-1$
        assertTrue("The binding is not canonical", parameters3[0] == methodBinding3.getReturnType());
    }

    /**
	 * i++; IVariableBinding
	 */
    public void test0173() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0173", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("Not an expressionStatement", node instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        Expression ex = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a postfixexpression", ex instanceof PostfixExpression);
        PostfixExpression postfixExpression = (PostfixExpression) ex;
        Expression expr = postfixExpression.getOperand();
        //$NON-NLS-1$
        assertTrue("Not a simpleName", expr instanceof SimpleName);
        SimpleName name = (SimpleName) expr;
        //$NON-NLS-1$
        assertTrue("a declaration", !name.isDeclaration());
        IBinding binding = name.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("VariableDeclarationStatement", node2 instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node2;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertTrue("No fragment", fragments.size() == 1);
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        IVariableBinding variableBinding = fragment.resolveBinding();
        assertTrue(variableBinding == binding);
    }

    /**
	 * i++; IVariableBinding (field)
	 */
    public void test0174() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0174", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 1, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("Not an expressionStatement", node instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        Expression ex = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a postfixexpression", ex instanceof PostfixExpression);
        PostfixExpression postfixExpression = (PostfixExpression) ex;
        Expression expr = postfixExpression.getOperand();
        //$NON-NLS-1$
        assertTrue("Not a simpleName", expr instanceof SimpleName);
        SimpleName name = (SimpleName) expr;
        IBinding binding = name.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertTrue("FieldDeclaration", node2 instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node2;
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertTrue("No fragment", fragments.size() == 1);
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        IVariableBinding variableBinding = fragment.resolveBinding();
        assertTrue(variableBinding == binding);
    }

    /**
	 * int i = 0; Test IntBinding for the field declaration and the 0 literal
	 */
    public void test0175() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0175", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertTrue("VariableDeclarationStatement", node2 instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node2;
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertTrue("No fragment", fragments.size() == 1);
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        IVariableBinding variableBinding = fragment.resolveBinding();
        ITypeBinding typeBinding = fragment.getInitializer().resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$
        assertTrue("Not a primitive type", typeBinding.isPrimitive());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not int", "int", typeBinding.getName());
        assertTrue(variableBinding.getType() == typeBinding);
    }

    /**
	 * ThisReference
	 */
    public void test0176() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0176", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 1, 0);
        //$NON-NLS-1$
        assertTrue("Return statement", node2 instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) node2;
        //$NON-NLS-1$
        assertTrue("Not a field access", returnStatement.getExpression() instanceof FieldAccess);
        FieldAccess fieldAccess = (FieldAccess) returnStatement.getExpression();
        ITypeBinding typeBinding = fieldAccess.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$
        assertTrue("Not a primitive type", typeBinding.isPrimitive());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not int", "int", typeBinding.getName());
        Expression expr = fieldAccess.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a this expression", expr instanceof ThisExpression);
        ThisExpression thisExpression = (ThisExpression) expr;
        ITypeBinding typeBinding2 = thisExpression.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding2", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not Test", "Test", typeBinding2.getName());
    }

    /**
	 * i++; IVariableBinding
	 */
    public void test0177() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0177", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 1, 1);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("Not an expressionStatement", node instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        Expression ex = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a postfixexpression", ex instanceof PostfixExpression);
        PostfixExpression postfixExpression = (PostfixExpression) ex;
        Expression expr = postfixExpression.getOperand();
        //$NON-NLS-1$
        assertTrue("Not a simpleName", expr instanceof SimpleName);
        SimpleName name = (SimpleName) expr;
        IBinding binding = name.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 1, 0);
        //$NON-NLS-1$
        assertTrue("VariableDeclarationStatement", node2 instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node2;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertTrue("No fragment", fragments.size() == 1);
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        IVariableBinding variableBinding = fragment.resolveBinding();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("return type is not int", "int", variableBinding.getType().getName());
        assertTrue(variableBinding == binding);
    }

    /**
	 * SuperReference
	 */
    public void test0178() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0178", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 1, 0, 0);
        //$NON-NLS-1$
        assertTrue("Return statement", node2 instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) node2;
        Expression expr = returnStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a field access", expr instanceof SuperFieldAccess);
        SuperFieldAccess fieldAccess = (SuperFieldAccess) expr;
        ITypeBinding typeBinding = fieldAccess.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$
        assertTrue("Not a primitive type", typeBinding.isPrimitive());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not int", "int", typeBinding.getName());
    }

    /**
	 * Allocation expression
	 */
    public void test0179() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0179", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("VariableDeclarationStatement", node2 instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node2;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertTrue("No fragment", fragments.size() == 1);
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        IVariableBinding variableBinding = fragment.resolveBinding();
        Expression initialization = fragment.getInitializer();
        ITypeBinding typeBinding = initialization.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        assertTrue(variableBinding.getType() == typeBinding);
    }

    /**
	 * Allocation expression
	 */
    public void test0180() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0180", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("VariableDeclarationStatement", node2 instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node2;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertTrue("No fragment", fragments.size() == 1);
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        IVariableBinding variableBinding = fragment.resolveBinding();
        Expression initialization = fragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("No an array creation", initialization instanceof ArrayCreation);
        ITypeBinding typeBinding = initialization.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$
        assertTrue("Not an array", typeBinding.isArray());
        assertTrue(variableBinding.getType() == typeBinding);
    }

    /**
	 * Allocation expression
	 */
    public void test0181() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0181", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("VariableDeclarationStatement", node2 instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node2;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertTrue("No fragment", fragments.size() == 1);
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        IVariableBinding variableBinding = fragment.resolveBinding();
        Expression initialization = fragment.getInitializer();
        ITypeBinding typeBinding = initialization.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$
        assertTrue("Not an array", typeBinding.isArray());
        assertTrue(variableBinding.getType() == typeBinding);
    }

    /**
	 * BinaryExpression
	 */
    public void test0182() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0182", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertTrue("IfStatement", node2 instanceof IfStatement);
        IfStatement ifStatement = (IfStatement) node2;
        Expression expr = ifStatement.getExpression();
        //$NON-NLS-1$
        assertNotNull("No condition", expr);
        ITypeBinding typeBinding = expr.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not a boolean", "boolean", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(expr, "i < 10", source);
    }

    /**
	 * BinaryExpression
	 */
    public void test0183() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0183", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertTrue("IfStatement", node2 instanceof IfStatement);
        IfStatement ifStatement = (IfStatement) node2;
        Expression expr = ifStatement.getExpression();
        //$NON-NLS-1$
        assertNotNull("No condition", expr);
        ITypeBinding typeBinding = expr.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not a boolean", "boolean", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(expr, "i < 10 && i < 20", source);
    }

    /**
	 * BinaryExpression
	 */
    public void test0184() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0184", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertTrue("IfStatement", node2 instanceof IfStatement);
        IfStatement ifStatement = (IfStatement) node2;
        Expression expr = ifStatement.getExpression();
        //$NON-NLS-1$
        assertNotNull("No condition", expr);
        ITypeBinding typeBinding = expr.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not a boolean", "boolean", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(expr, "i < 10 || i < 20", source);
    }

    /**
	 * BinaryExpression
	 */
    public void test0185() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0185", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertTrue("IfStatement", node2 instanceof IfStatement);
        IfStatement ifStatement = (IfStatement) node2;
        Expression expr = ifStatement.getExpression();
        //$NON-NLS-1$
        assertNotNull("No condition", expr);
        ITypeBinding typeBinding = expr.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not a boolean", "boolean", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(expr, "i == 10", source);
    }

    /**
	 * BinaryExpression
	 */
    public void test0186() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0186", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertTrue("IfStatement", node2 instanceof IfStatement);
        IfStatement ifStatement = (IfStatement) node2;
        Expression expr = ifStatement.getExpression();
        //$NON-NLS-1$
        assertNotNull("No condition", expr);
        ITypeBinding typeBinding = expr.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not a boolean", "boolean", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(expr, "o == o", source);
    }

    /**
	 * BinaryExpression
	 */
    public void test0187() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0187", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertTrue("IfStatement", node2 instanceof WhileStatement);
        WhileStatement whileStatement = (WhileStatement) node2;
        Expression expr = whileStatement.getExpression();
        //$NON-NLS-1$
        assertNotNull("No condition", expr);
        ITypeBinding typeBinding = expr.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not a boolean", "boolean", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(expr, "i <= 10", source);
    }

    /**
	 * BinaryExpression
	 */
    public void test0188() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0188", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 2);
        //$NON-NLS-1$
        assertTrue("DoStatement", node2 instanceof DoStatement);
        DoStatement statement = (DoStatement) node2;
        Expression expr = statement.getExpression();
        //$NON-NLS-1$
        assertNotNull("No condition", expr);
        ITypeBinding typeBinding = expr.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not a boolean", "boolean", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(expr, "i <= 10", source);
    }

    /**
	 * BinaryExpression
	 */
    public void test0189() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0189", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("ForStatement", node2 instanceof ForStatement);
        ForStatement statement = (ForStatement) node2;
        Expression expr = statement.getExpression();
        //$NON-NLS-1$
        assertNotNull("No condition", expr);
        ITypeBinding typeBinding = expr.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not a boolean", "boolean", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(expr, "i < 10", source);
    }

    /**
	 * BinaryExpression
	 */
    public void test0190() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0190", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 2, 1);
        //$NON-NLS-1$
        assertTrue("IfStatement", node2 instanceof IfStatement);
        IfStatement statement = (IfStatement) node2;
        Expression expr = statement.getExpression();
        //$NON-NLS-1$
        assertNotNull("No condition", expr);
        ITypeBinding typeBinding = expr.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not a boolean", "boolean", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(expr, "scanner.x < selection.start && selection.start < scanner.y", source);
    }

    /**
	 * BinaryExpression
	 */
    public void test0191() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0191", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertTrue("ExpressionStatement", node2 instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node2;
        Expression ex = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Assignment", ex instanceof Assignment);
        Assignment statement = (Assignment) ex;
        Expression rightExpr = statement.getRightHandSide();
        //$NON-NLS-1$
        assertTrue("Not an infix expression", rightExpr instanceof InfixExpression);
        InfixExpression infixExpression = (InfixExpression) rightExpr;
        Expression expr = infixExpression.getRightOperand();
        //$NON-NLS-1$
        assertNotNull("No right hand side expression", expr);
        ITypeBinding typeBinding = expr.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not a boolean", "boolean", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(expr, "2 < 20", source);
    }

    /**
	 * Initializer
	 */
    public void test0192() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0192", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("VariableDeclarationStatement", node2 instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node2;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertTrue("No fragment", fragments.size() == 1);
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        IVariableBinding variableBinding = fragment.resolveBinding();
        Expression initialization = fragment.getInitializer();
        ITypeBinding typeBinding = initialization.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        assertTrue(variableBinding.getType() == typeBinding);
        //$NON-NLS-1$
        checkSourceRange(initialization, "0", source);
    }

    /**
	 * Initializer
	 */
    public void test0193() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0193", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 1, 0);
        //$NON-NLS-1$
        assertTrue("VariableDeclarationStatement", node2 instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node2;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertTrue("No fragment", fragments.size() == 1);
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        IVariableBinding variableBinding = fragment.resolveBinding();
        Expression initialization = fragment.getInitializer();
        ITypeBinding typeBinding = initialization.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        assertTrue(variableBinding.getType() == typeBinding);
        //$NON-NLS-1$
        checkSourceRange(initialization, "new Inner()", source);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong type", "Inner", typeBinding.getName());
    }

    /**
	 * Initializer
	 */
    public void test0194() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0194", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 1, 0);
        //$NON-NLS-1$
        assertTrue("VariableDeclarationStatement", node2 instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node2;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertTrue("No fragment", fragments.size() == 1);
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        IVariableBinding variableBinding = fragment.resolveBinding();
        Expression initialization = fragment.getInitializer();
        ITypeBinding typeBinding = initialization.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        assertTrue(variableBinding.getType() == typeBinding);
        //$NON-NLS-1$
        checkSourceRange(initialization, "new Inner[10]", source);
        //$NON-NLS-1$
        assertTrue("Not an array", typeBinding.isArray());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong type", "Inner[]", typeBinding.getName());
    }

    /**
	 * Initializer
	 */
    public void test0195() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0195", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 1, 0, 1);
        //$NON-NLS-1$
        assertTrue("ExpressionStatement", node2 instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node2;
        Expression ex = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("MethodInvocation", ex instanceof MethodInvocation);
        MethodInvocation methodInvocation = (MethodInvocation) ex;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation, "a.useFile(/*]*/a.getFile()/*[*/)", source);
        List list = methodInvocation.arguments();
        //$NON-NLS-1$
        assertTrue("Parameter list not empty", list.size() == 1);
        Expression parameter = (Expression) list.get(0);
        //$NON-NLS-1$
        assertTrue("Not a method invocation", parameter instanceof MethodInvocation);
        ITypeBinding typeBinding = parameter.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not a boolean", "File", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(parameter, "a.getFile()", source);
    }

    /**
	 * Initializer
	 */
    public void test0196() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0196", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 1, 2);
        //$NON-NLS-1$
        assertTrue("ExpressionStatement", node2 instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node2;
        Expression ex = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Assignment", ex instanceof Assignment);
        Assignment statement = (Assignment) ex;
        Expression rightExpr = statement.getRightHandSide();
        //$NON-NLS-1$
        assertTrue("Not an instanceof expression", rightExpr instanceof InstanceofExpression);
        ITypeBinding typeBinding = rightExpr.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong type", "boolean", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(rightExpr, "inner instanceof Inner", source);
    }

    /**
	 * Initializer
	 */
    public void test0197() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0197", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        assertEquals("Not a compilation unit", ASTNode.COMPILATION_UNIT, result.getNodeType());
        CompilationUnit unit = (CompilationUnit) result;
        assertProblemsSize(unit, 0);
        ASTNode node2 = getASTNode(unit, 1, 0, 1);
        //$NON-NLS-1$
        assertTrue("ExpressionStatement", node2 instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node2;
        Expression ex = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("MethodInvocation", ex instanceof MethodInvocation);
        MethodInvocation methodInvocation = (MethodInvocation) ex;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation, "a.getFile()/*[*/.getName()", source);
        Expression receiver = methodInvocation.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a method invocation", receiver instanceof MethodInvocation);
        MethodInvocation methodInvocation2 = (MethodInvocation) receiver;
        ITypeBinding typeBinding = methodInvocation2.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "File", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(methodInvocation2, "a.getFile()", source);
    }

    /**
	 * Initializer
	 */
    public void test0198() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0198", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("ReturnStatement", node2 instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) node2;
        Expression expr = returnStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not an infixExpression", expr instanceof InfixExpression);
        InfixExpression infixExpression = (InfixExpression) expr;
        Expression left = infixExpression.getLeftOperand();
        //$NON-NLS-1$
        assertTrue("Not an InfixExpression", left instanceof InfixExpression);
        InfixExpression infixExpression2 = (InfixExpression) left;
        Expression right = infixExpression2.getRightOperand();
        //$NON-NLS-1$
        assertTrue("Not an InfixExpression", right instanceof InfixExpression);
        InfixExpression infixExpression3 = (InfixExpression) right;
        //$NON-NLS-1$
        assertEquals("A multiplication", InfixExpression.Operator.TIMES, infixExpression3.getOperator());
        ITypeBinding typeBinding = infixExpression3.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not int", "int", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(infixExpression3, "20 * 30", source);
    }

    /**
	 * Initializer
	 */
    public void test0199() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0199", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("VariableDeclarationStatement", node2 instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node2;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertTrue("No fragment", fragments.size() == 1);
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        Expression initialization = fragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not an infixExpression", initialization instanceof InfixExpression);
        InfixExpression infixExpression = (InfixExpression) initialization;
        Expression left = infixExpression.getLeftOperand();
        //$NON-NLS-1$
        assertTrue("Not an InfixExpression", left instanceof InfixExpression);
        InfixExpression infixExpression2 = (InfixExpression) left;
        Expression right = infixExpression2.getRightOperand();
        //$NON-NLS-1$
        assertTrue("Not an InfixExpression", right instanceof InfixExpression);
        InfixExpression infixExpression3 = (InfixExpression) right;
        //$NON-NLS-1$
        assertEquals("A multiplication", InfixExpression.Operator.TIMES, infixExpression3.getOperator());
        ITypeBinding typeBinding = infixExpression3.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not int", "int", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(infixExpression3, "10 * 30", source);
    }

    /**
	 * Initializer
	 */
    public void test0200() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0200", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 1, 0, 0);
        //$NON-NLS-1$
        assertTrue("VariableDeclarationStatement", node2 instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node2;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertTrue("No fragment", fragments.size() == 1);
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        Expression initialization = fragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not an infixExpression", initialization instanceof FieldAccess);
        FieldAccess fieldAccess = (FieldAccess) initialization;
        Expression receiver = fieldAccess.getExpression();
        //$NON-NLS-1$
        assertTrue("ArrayCreation", receiver instanceof ArrayCreation);
        ArrayCreation arrayCreation = (ArrayCreation) receiver;
        List dimensions = arrayCreation.dimensions();
        //$NON-NLS-1$
        assertEquals("Wrong dimension", 1, dimensions.size());
        Expression dim = (Expression) dimensions.get(0);
        //$NON-NLS-1$
        assertTrue("InfixExpression", dim instanceof InfixExpression);
        InfixExpression infixExpression = (InfixExpression) dim;
        ITypeBinding typeBinding = infixExpression.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not int", "int", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(infixExpression, "1 + 2", source);
    }

    /**
	 * Position inside for statement: PR 3300
	 */
    public void test0201() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0201", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertTrue("ForStatement", node2 instanceof ForStatement);
        ForStatement forStatement = (ForStatement) node2;
        List initializers = forStatement.initializers();
        //$NON-NLS-1$
        assertTrue("wrong size", initializers.size() == 1);
        Expression init = (Expression) initializers.get(0);
        //$NON-NLS-1$
        checkSourceRange(init, "int i= 0", source);
    }

    /**
	 * PR 7386
	 */
    public void test0202() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0202", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertTrue("FieldDeclaration", node2 instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node2;
        //$NON-NLS-1$
        checkSourceRange(fieldDeclaration, "int f= (2);", source);
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        Expression initialization = fragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a parenthesized expression", initialization instanceof ParenthesizedExpression);
        //$NON-NLS-1$
        checkSourceRange(initialization, "(2)", source);
        ITypeBinding typeBinding = initialization.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("no binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("not int", "int", typeBinding.getName());
    }

    /**
	 * PR 7386
	 */
    public void test0203() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0203", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertTrue("FieldDeclaration", node2 instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node2;
        //$NON-NLS-1$
        checkSourceRange(fieldDeclaration, "int f= (2);", source);
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        Expression initialization = fragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a parenthesized expression", initialization instanceof ParenthesizedExpression);
        ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) initialization;
        //$NON-NLS-1$
        checkSourceRange(parenthesizedExpression, "(2)", source);
        Expression expr = parenthesizedExpression.getExpression();
        //$NON-NLS-1$
        checkSourceRange(expr, "2", source);
        ITypeBinding typeBinding = expr.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("no binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("not int", "int", typeBinding.getName());
        //$NON-NLS-1$
        assertTrue("type binding is canonical", typeBinding == parenthesizedExpression.resolveTypeBinding());
    }

    /**
	 * PR 7386
	 */
    public void test0204() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0204", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertTrue("FieldDeclaration", node2 instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node2;
        //$NON-NLS-1$
        checkSourceRange(fieldDeclaration, "int f= ((2));", source);
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        Expression initialization = fragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a parenthesized expression", initialization instanceof ParenthesizedExpression);
        ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) initialization;
        //$NON-NLS-1$
        checkSourceRange(parenthesizedExpression, "((2))", source);
        Expression expr = parenthesizedExpression.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a parenthesized expression", expr instanceof ParenthesizedExpression);
        ParenthesizedExpression parenthesizedExpression2 = (ParenthesizedExpression) expr;
        //$NON-NLS-1$
        checkSourceRange(parenthesizedExpression2, "(2)", source);
        expr = parenthesizedExpression2.getExpression();
        //$NON-NLS-1$
        checkSourceRange(expr, "2", source);
        ITypeBinding typeBinding = expr.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("no binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("not int", "int", typeBinding.getName());
        typeBinding = parenthesizedExpression.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("no binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("not int", "int", typeBinding.getName());
        //$NON-NLS-1$
        assertTrue("type binding is canonical", typeBinding == parenthesizedExpression2.resolveTypeBinding());
    }

    /**
	 * Local class end position when trailing comment
	 */
    public void test0205() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0205", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("TypeDeclarationStatement", node2 instanceof TypeDeclarationStatement);
        TypeDeclarationStatement typeDeclarationStatement = (TypeDeclarationStatement) node2;
        AbstractTypeDeclaration typeDeclaration = typeDeclarationStatement.getDeclaration();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "AA", typeDeclaration.getName().getIdentifier());
        //$NON-NLS-1$
        checkSourceRange(typeDeclaration, "class AA extends Test {}", source);
    }

    /**
	 * QualifiedName
	 */
    public void test0206() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0206", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 5, 0);
        //$NON-NLS-1$
        assertTrue("ReturnStatement", node2 instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) node2;
        Expression expr = returnStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a qualifiedName", expr instanceof QualifiedName);
        QualifiedName qualifiedName = (QualifiedName) expr;
        ITypeBinding typeBinding = expr.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not an int (typeBinding)", "int", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(qualifiedName, "field1.field2.field3.field4.i", source);
        //$NON-NLS-1$
        assertTrue("Not a simple name", qualifiedName.getName().isSimpleName());
        SimpleName simpleName = qualifiedName.getName();
        //$NON-NLS-1$
        assertTrue("a declaration", !simpleName.isDeclaration());
        //$NON-NLS-1$
        checkSourceRange(simpleName, "i", source);
        ITypeBinding typeBinding2 = simpleName.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No typebinding2", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not an int (typeBinding2)", "int", typeBinding2.getName());
        IBinding binding = simpleName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertTrue("VariableBinding", binding instanceof IVariableBinding);
        IVariableBinding variableBinding = (IVariableBinding) binding;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not Test", "Test", variableBinding.getDeclaringClass().getName());
        //$NON-NLS-1$
        assertEquals("Not default", Modifier.PUBLIC, variableBinding.getModifiers());
        Name qualifierName = qualifiedName.getQualifier();
        //$NON-NLS-1$
        assertTrue("Not a qualified name", qualifierName.isQualifiedName());
        //$NON-NLS-1$
        checkSourceRange(qualifierName, "field1.field2.field3.field4", source);
        ITypeBinding typeBinding5 = qualifierName.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding5", typeBinding5);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not Test", "Test", typeBinding5.getName());
        qualifiedName = (QualifiedName) qualifierName;
        simpleName = qualifiedName.getName();
        //$NON-NLS-1$
        checkSourceRange(simpleName, "field4", source);
        ITypeBinding typeBinding6 = simpleName.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding6", typeBinding6);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not Test", "Test", typeBinding6.getName());
        qualifierName = qualifiedName.getQualifier();
        //$NON-NLS-1$
        assertTrue("Not a qualified name", qualifierName.isQualifiedName());
        //$NON-NLS-1$
        checkSourceRange(qualifierName, "field1.field2.field3", source);
        ITypeBinding typeBinding7 = qualifierName.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding7", typeBinding7);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not Test", "Test", typeBinding7.getName());
        qualifiedName = (QualifiedName) qualifierName;
        simpleName = qualifiedName.getName();
        //$NON-NLS-1$
        checkSourceRange(simpleName, "field3", source);
        qualifierName = qualifiedName.getQualifier();
        //$NON-NLS-1$
        assertTrue("Not a qualified name", qualifierName.isQualifiedName());
        //$NON-NLS-1$
        checkSourceRange(qualifierName, "field1.field2", source);
        ITypeBinding typeBinding3 = qualifierName.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding3", typeBinding3);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not Test", "Test", typeBinding3.getName());
        qualifiedName = (QualifiedName) qualifierName;
        simpleName = qualifiedName.getName();
        //$NON-NLS-1$
        checkSourceRange(simpleName, "field2", source);
        qualifierName = qualifiedName.getQualifier();
        //$NON-NLS-1$
        assertTrue("Not a simple name", qualifierName.isSimpleName());
        //$NON-NLS-1$
        assertTrue("a declaration", !((SimpleName) qualifierName).isDeclaration());
        //$NON-NLS-1$
        checkSourceRange(qualifierName, "field1", source);
        ITypeBinding typeBinding4 = qualifierName.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding4", typeBinding4);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not Test", "Test", typeBinding4.getName());
    }

    /**
	 * Check javadoc for MethodDeclaration
	 * @deprecated marking deprecated since using deprecated code
	 */
    public void test0207() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0207", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a MethodDeclaration", node instanceof MethodDeclaration);
        Javadoc actualJavadoc = ((MethodDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        checkSourceRange(node, "/** JavaDoc Comment*/\n  void foo(final int i) {}", source);
        //$NON-NLS-1$
        checkSourceRange(actualJavadoc, "/** JavaDoc Comment*/", source);
    }

    /**
	 * Check javadoc for MethodDeclaration
	 */
    public void test0208() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0208", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a MethodDeclaration", node instanceof MethodDeclaration);
        Javadoc actualJavadoc = ((MethodDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        assertTrue("Javadoc must be null", actualJavadoc == null);
        //$NON-NLS-1$
        checkSourceRange(node, "void foo(final int i) {}", source);
    }

    /**
	 * Check javadoc for MethodDeclaration
	 */
    public void test0209() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0209", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a MethodDeclaration", node instanceof MethodDeclaration);
        Javadoc actualJavadoc = ((MethodDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        assertTrue("Javadoc must be null", actualJavadoc == null);
        //$NON-NLS-1$
        checkSourceRange(node, "void foo(final int i) {}", source);
    }

    /**
	 * Check javadoc for FieldDeclaration
	 * @deprecated marking deprecated since using deprecated code
	 */
    public void test0210() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0210", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a FieldDeclaration", node instanceof FieldDeclaration);
        //		Javadoc actualJavadoc = ((FieldDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        checkSourceRange(node, "/** JavaDoc Comment*/\n  int i;", source);
    }

    /**
	 * Check javadoc for FieldDeclaration
	 */
    public void test0211() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0211", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a FieldDeclaration", node instanceof FieldDeclaration);
        Javadoc actualJavadoc = ((FieldDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        assertTrue("Javadoc must be null", actualJavadoc == null);
        //$NON-NLS-1$
        checkSourceRange(node, "int i;", source);
    }

    /**
	 * Check javadoc for FieldDeclaration
	 */
    public void test0212() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0212", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a FieldDeclaration", node instanceof FieldDeclaration);
        Javadoc actualJavadoc = ((FieldDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        assertTrue("Javadoc must be null", actualJavadoc == null);
        //$NON-NLS-1$
        checkSourceRange(node, "int i;", source);
    }

    /**
	 * Check javadoc for TypeDeclaration
	 */
    public void test0213() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0213", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a TypeDeclaration", node instanceof TypeDeclaration);
        Javadoc actualJavadoc = ((TypeDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        assertTrue("Javadoc must be null", actualJavadoc == null);
        String expectedContents = //$NON-NLS-1$
        "public class Test {\n" + //$NON-NLS-1$
        "  int i;\n" + //$NON-NLS-1$
        "}";
        //$NON-NLS-1$
        checkSourceRange(node, expectedContents, source);
    }

    /**
	 * Check javadoc for TypeDeclaration
	 */
    public void test0214() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0214", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a TypeDeclaration", node instanceof TypeDeclaration);
        Javadoc actualJavadoc = ((TypeDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        assertTrue("Javadoc must be null", actualJavadoc == null);
        String expectedContents = //$NON-NLS-1$
        "public class Test {\n" + //$NON-NLS-1$
        "  int i;\n" + //$NON-NLS-1$
        "}";
        //$NON-NLS-1$
        checkSourceRange(node, expectedContents, source);
    }

    /**
	 * Check javadoc for TypeDeclaration
	 * @deprecated marking deprecated since using deprecated code
	 */
    public void test0215() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0215", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a TypeDeclaration", node instanceof TypeDeclaration);
        Javadoc actualJavadoc = ((TypeDeclaration) node).getJavadoc();
        String expectedContents = //$NON-NLS-1$
        "/** JavaDoc Comment*/\n" + //$NON-NLS-1$
        "public class Test {\n" + //$NON-NLS-1$
        "  int i;\n" + //$NON-NLS-1$
        "}";
        //$NON-NLS-1$
        checkSourceRange(node, expectedContents, source);
        //$NON-NLS-1$
        checkSourceRange(actualJavadoc, "/** JavaDoc Comment*/", source);
    }

    /**
	 * Check javadoc for MemberTypeDeclaration
	 * @deprecated marking deprecated since using deprecated code
	 */
    public void test0216() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0216", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a TypeDeclaration", node instanceof TypeDeclaration);
        Javadoc actualJavadoc = ((TypeDeclaration) node).getJavadoc();
        String expectedContents = //$NON-NLS-1$
        "/** JavaDoc Comment*/\n" + //$NON-NLS-1$
        "  class B {}";
        //$NON-NLS-1$
        checkSourceRange(node, expectedContents, source);
        //$NON-NLS-1$
        checkSourceRange(actualJavadoc, "/** JavaDoc Comment*/", source);
    }

    /**
	 * Check javadoc for MemberTypeDeclaration
	 */
    public void test0217() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0217", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a TypeDeclaration", node instanceof TypeDeclaration);
        Javadoc actualJavadoc = ((TypeDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        assertTrue("Javadoc must be null", actualJavadoc == null);
        //$NON-NLS-1$
        checkSourceRange(node, "class B {}", source);
    }

    /**
	 * Check javadoc for MemberTypeDeclaration
	 */
    public void test0218() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0218", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a TypeDeclaration", node instanceof TypeDeclaration);
        Javadoc actualJavadoc = ((TypeDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        assertTrue("Javadoc must be null", actualJavadoc == null);
        //$NON-NLS-1$
        checkSourceRange(node, "public static class B {}", source);
    }

    /**
	 * Check javadoc for MemberTypeDeclaration
	 */
    public void test0219() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0219", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        assertTrue("The node is not a TypeDeclaration", node instanceof TypeDeclaration);
        Javadoc actualJavadoc = ((TypeDeclaration) node).getJavadoc();
        //$NON-NLS-1$
        assertTrue("Javadoc must be null", actualJavadoc == null);
        //$NON-NLS-1$
        checkSourceRange(node, "public static class B {}", source);
    }

    /**
	 * Checking initializers
	 */
    public void test0220() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0220", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        checkSourceRange(node, "{}", source);
    }

    /**
	 * Checking initializers
	 */
    public void test0221() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0221", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        //$NON-NLS-1$
        checkSourceRange(node, "static {}", source);
    }

    /**
	 * Checking initializers
	 * @deprecated marking deprecated since using deprecated code
	 */
    public void test0222() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0222", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        Javadoc actualJavadoc = ((Initializer) node).getJavadoc();
        //$NON-NLS-1$
        assertNotNull("Javadoc comment should no be null", actualJavadoc);
        String expectedContents = //$NON-NLS-1$
        "/** JavaDoc Comment*/\n" + //$NON-NLS-1$
        "  static {}";
        //$NON-NLS-1$
        checkSourceRange(node, expectedContents, source);
        //$NON-NLS-1$
        checkSourceRange(actualJavadoc, "/** JavaDoc Comment*/", source);
    }

    /**
	 * Checking initializers
	 * @deprecated marking deprecated since using deprecated code
	 */
    public void test0223() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0223", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        Javadoc actualJavadoc = ((Initializer) node).getJavadoc();
        //$NON-NLS-1$
        assertNotNull("Javadoc comment should not be null", actualJavadoc);
        String expectedContents = //$NON-NLS-1$
        "/** JavaDoc Comment*/\n" + //$NON-NLS-1$
        "  {}";
        //$NON-NLS-1$
        checkSourceRange(node, expectedContents, source);
        //$NON-NLS-1$
        checkSourceRange(actualJavadoc, "/** JavaDoc Comment*/", source);
    }

    /**
	 * Checking initializers
	 */
    public void test0224() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0224", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        Javadoc actualJavadoc = ((Initializer) node).getJavadoc();
        //$NON-NLS-1$
        assertNull("Javadoc comment should be null", actualJavadoc);
        //$NON-NLS-1$
        checkSourceRange(node, "{}", source);
    }

    /**
	 * Continue ==> ContinueStatement
	 */
    public void test0225() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0225", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        LabeledStatement labeledStatement = (LabeledStatement) getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        checkSourceRange(labeledStatement.getLabel(), "label", source);
        ForStatement forStatement = (ForStatement) labeledStatement.getBody();
        ContinueStatement statement = (ContinueStatement) ((Block) forStatement.getBody()).statements().get(0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", statement);
        ContinueStatement continueStatement = this.ast.newContinueStatement();
        //$NON-NLS-1$
        continueStatement.setLabel(this.ast.newSimpleName("label"));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", continueStatement.subtreeMatch(new ASTMatcher(), statement));
        //$NON-NLS-1$
        checkSourceRange(statement, "continue label;", source);
        //$NON-NLS-1$
        checkSourceRange(statement.getLabel(), "label", source);
    }

    /**
	 * Break + label  ==> BreakStatement
	 */
    public void test0226() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0226", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        LabeledStatement labeledStatement = (LabeledStatement) getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        checkSourceRange(labeledStatement.getLabel(), "label", source);
        ForStatement forStatement = (ForStatement) labeledStatement.getBody();
        BreakStatement statement = (BreakStatement) ((Block) forStatement.getBody()).statements().get(0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", statement);
        BreakStatement breakStatement = this.ast.newBreakStatement();
        //$NON-NLS-1$
        breakStatement.setLabel(this.ast.newSimpleName("label"));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", breakStatement.subtreeMatch(new ASTMatcher(), statement));
        //$NON-NLS-1$
        checkSourceRange(statement, "break label;", source);
        //$NON-NLS-1$
        checkSourceRange(statement.getLabel(), "label", source);
    }

    /**
	 * QualifiedName
	 */
    public void test0227() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0227", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 3, 2, 0);
        //$NON-NLS-1$
        assertTrue("ReturnStatement", node2 instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) node2;
        Expression expr = returnStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a qualifiedName", expr instanceof QualifiedName);
        QualifiedName qualifiedName = (QualifiedName) expr;
        ITypeBinding typeBinding = expr.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not an long (typeBinding)", "long", typeBinding.getName());
        //$NON-NLS-1$
        checkSourceRange(qualifiedName, "field.fB.fA.j", source);
        SimpleName simpleName = qualifiedName.getName();
        //$NON-NLS-1$
        checkSourceRange(simpleName, "j", source);
        ITypeBinding typeBinding2 = simpleName.resolveTypeBinding();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not an long (typeBinding2)", "long", typeBinding2.getName());
        IBinding binding = simpleName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertTrue("VariableBinding", binding instanceof IVariableBinding);
        IVariableBinding variableBinding = (IVariableBinding) binding;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not A", "A", variableBinding.getDeclaringClass().getName());
        //$NON-NLS-1$
        assertEquals("Not default", Modifier.NONE, variableBinding.getModifiers());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "j", variableBinding.getName());
        Name qualifierName = qualifiedName.getQualifier();
        //$NON-NLS-1$
        assertTrue("Not a qualified name", qualifierName.isQualifiedName());
        //$NON-NLS-1$
        checkSourceRange(qualifierName, "field.fB.fA", source);
        qualifiedName = (QualifiedName) qualifierName;
        ITypeBinding typeBinding3 = qualifiedName.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding3", typeBinding3);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not an A", "A", typeBinding3.getName());
        simpleName = qualifiedName.getName();
        //$NON-NLS-1$
        checkSourceRange(simpleName, "fA", source);
        ITypeBinding typeBinding4 = simpleName.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No typeBinding4", typeBinding4);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not an A", "A", typeBinding4.getName());
        IBinding binding2 = qualifiedName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding2", binding2);
        //$NON-NLS-1$
        assertTrue("VariableBinding", binding2 instanceof IVariableBinding);
        IVariableBinding variableBinding2 = (IVariableBinding) binding2;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not B", "B", variableBinding2.getDeclaringClass().getName());
        //$NON-NLS-1$
        assertEquals("Not default", Modifier.NONE, variableBinding2.getModifiers());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "fA", variableBinding2.getName());
        qualifierName = qualifiedName.getQualifier();
        //$NON-NLS-1$
        assertTrue("Not a qualified name", qualifierName.isQualifiedName());
        //$NON-NLS-1$
        checkSourceRange(qualifierName, "field.fB", source);
        qualifiedName = (QualifiedName) qualifierName;
        ITypeBinding typeBinding5 = qualifiedName.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No typeBinding5", typeBinding5);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not a B", "B", typeBinding5.getName());
        simpleName = qualifiedName.getName();
        //$NON-NLS-1$
        checkSourceRange(simpleName, "fB", source);
        ITypeBinding typeBinding6 = simpleName.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No typebinding6", typeBinding6);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("not a B", "B", typeBinding6.getName());
        IBinding binding3 = qualifiedName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding2", binding3);
        //$NON-NLS-1$
        assertTrue("VariableBinding", binding3 instanceof IVariableBinding);
        IVariableBinding variableBinding3 = (IVariableBinding) binding3;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not C", "C", variableBinding3.getDeclaringClass().getName());
        //$NON-NLS-1$
        assertEquals("Not default", Modifier.NONE, variableBinding3.getModifiers());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "fB", variableBinding3.getName());
        qualifierName = qualifiedName.getQualifier();
        //$NON-NLS-1$
        assertTrue("Not a simple name", qualifierName.isSimpleName());
        //$NON-NLS-1$
        checkSourceRange(qualifierName, "field", source);
        simpleName = (SimpleName) qualifierName;
        ITypeBinding typeBinding7 = simpleName.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No typeBinding7", typeBinding7);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not a C", "C", typeBinding7.getName());
        IBinding binding4 = simpleName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding4", binding4);
        //$NON-NLS-1$
        assertTrue("VariableBinding", binding4 instanceof IVariableBinding);
        IVariableBinding variableBinding4 = (IVariableBinding) binding4;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not Test", "Test", variableBinding4.getDeclaringClass().getName());
        //$NON-NLS-1$
        assertEquals("Not public", Modifier.PUBLIC, variableBinding4.getModifiers());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "field", variableBinding4.getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong return type", "C", variableBinding4.getType().getName());
    }

    /**
	 * QualifiedName as TypeReference
	 */
    public void test0228() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0228", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 1, 0);
        //$NON-NLS-1$
        assertTrue("ReturnStatement", node2 instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) node2;
        Expression expr = returnStatement.getExpression();
        //$NON-NLS-1$
        checkSourceRange(expr, "test0228.Test.foo()", source);
        //$NON-NLS-1$
        assertTrue("MethodInvocation", expr instanceof MethodInvocation);
        MethodInvocation methodInvocation = (MethodInvocation) expr;
        Expression qualifier = methodInvocation.getExpression();
        //$NON-NLS-1$
        assertNotNull("no qualifier", qualifier);
        //$NON-NLS-1$
        assertTrue("QualifiedName", qualifier instanceof QualifiedName);
        QualifiedName qualifiedName = (QualifiedName) qualifier;
        //$NON-NLS-1$
        checkSourceRange(qualifiedName, "test0228.Test", source);
        ITypeBinding typeBinding = qualifiedName.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong type", "Test", typeBinding.getName());
        IBinding binding = qualifiedName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertEquals("Not a type", IBinding.TYPE, binding.getKind());
    }

    /**
	 * MethodInvocation
	 */
    public void test0229() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0229", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("ExpressionStatement", node2 instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node2;
        Expression expr = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("MethodInvocation", expr instanceof MethodInvocation);
        //$NON-NLS-1$
        checkSourceRange(expr, "System.err.println()", source);
        MethodInvocation methodInvocation = (MethodInvocation) expr;
        Expression qualifier = methodInvocation.getExpression();
        //$NON-NLS-1$
        assertTrue("QualifiedName", qualifier instanceof QualifiedName);
        QualifiedName qualifiedName = (QualifiedName) qualifier;
        ITypeBinding typeBinding = qualifier.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "PrintStream", typeBinding.getName());
        IBinding binding = qualifiedName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertTrue("VariableBinding", binding instanceof IVariableBinding);
        IVariableBinding variableBinding = (IVariableBinding) binding;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "err", variableBinding.getName());
        SimpleName methodName = methodInvocation.getName();
        IBinding binding2 = methodName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding2", binding2);
    }

    /**
	 * MethodInvocation
	 */
    public void test0230() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0230", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 1, 0);
        //$NON-NLS-1$
        assertTrue("ExpressionStatement", node2 instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node2;
        Expression expr = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("MethodInvocation", expr instanceof MethodInvocation);
        //$NON-NLS-1$
        checkSourceRange(expr, "err.println()", source);
        MethodInvocation methodInvocation = (MethodInvocation) expr;
        Expression qualifier = methodInvocation.getExpression();
        //$NON-NLS-1$
        assertTrue("SimpleName", qualifier instanceof SimpleName);
        SimpleName name = (SimpleName) qualifier;
        IBinding binding = name.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "err", binding.getName());
        ITypeBinding typeBinding = name.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wron type name", "PrintStream", typeBinding.getName());
    }

    /**
	 * MethodInvocation
	 */
    public void test0231() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0231", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("ExpressionStatement", node2 instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node2;
        Expression expr = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("MethodInvocation", expr instanceof MethodInvocation);
        //$NON-NLS-1$
        checkSourceRange(expr, "System.err.println()", source);
        MethodInvocation methodInvocation = (MethodInvocation) expr;
        Expression qualifier = methodInvocation.getExpression();
        //$NON-NLS-1$
        assertTrue("QualifiedName", qualifier instanceof QualifiedName);
        QualifiedName qualifiedName = (QualifiedName) qualifier;
        ITypeBinding typeBinding = qualifier.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "PrintStream", typeBinding.getName());
        IBinding binding = qualifiedName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertTrue("VariableBinding", binding instanceof IVariableBinding);
        IVariableBinding variableBinding = (IVariableBinding) binding;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "err", variableBinding.getName());
        SimpleName methodName = methodInvocation.getName();
        IBinding binding2 = methodName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding2", binding2);
        Name name = qualifiedName.getQualifier();
        //$NON-NLS-1$
        assertTrue("SimpleName", name.isSimpleName());
        SimpleName simpleName = (SimpleName) name;
        ITypeBinding typeBinding2 = simpleName.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No typeBinding2", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong type name", "System", typeBinding2.getName());
    }

    /**
	 * MethodInvocation
	 */
    public void test0232() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0232", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node2 = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("VariableDeclarationStatement", node2 instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node2;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression initialization = variableDeclarationFragment.getInitializer();
        ITypeBinding typeBinding = initialization.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$
        assertTrue("Not a primitive type", typeBinding.isPrimitive());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "int", typeBinding.getName());
        //$NON-NLS-1$
        assertTrue("QualifiedName", initialization instanceof QualifiedName);
        QualifiedName qualifiedName = (QualifiedName) initialization;
        SimpleName simpleName = qualifiedName.getName();
        ITypeBinding typeBinding2 = simpleName.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding2);
        //$NON-NLS-1$
        assertTrue("Not a primitive type", typeBinding2.isPrimitive());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "int", typeBinding2.getName());
        IBinding binding = simpleName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertTrue("IVariableBinding", binding instanceof IVariableBinding);
        IVariableBinding variableBinding = (IVariableBinding) binding;
        //$NON-NLS-1$
        assertNull("No declaring class", variableBinding.getDeclaringClass());
    }

    /**
	 * Checking that only syntax errors are reported for the MALFORMED tag
	 */
    public void test0233() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0233", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", result);
        //$NON-NLS-1$
        assertTrue("The compilation unit is malformed", !isMalformed(result));
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit unit = (CompilationUnit) result;
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertTrue("The fiels is not malformed", !isMalformed(node));
        //$NON-NLS-1$
        assertEquals("No problem found", 1, unit.getMessages().length);
        //$NON-NLS-1$
        assertEquals("No problem found", 1, unit.getProblems().length);
    }

    /**
	 * Checking that null is returned for a resolveBinding if the type is unknown
	 */
    public void test0234() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0234", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true, true, true);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertTrue("The fiels is not malformed", !isMalformed(node));
        CompilationUnit unit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("No problem found", 1, unit.getMessages().length);
        //$NON-NLS-1$
        assertEquals("No problem found", 1, unit.getProblems().length);
        //$NON-NLS-1$
        assertTrue("FieldDeclaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        IVariableBinding variableBinding = fragment.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", variableBinding);
        assertEquals("Ltest0234/Test;.field)LList;", variableBinding.getKey());
    }

    /**
	 * Checking that null is returned for a resolveBinding if the type is unknown
	 */
    public void test0234_2() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0234", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true, true, false);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertTrue("The fiels is not malformed", !isMalformed(node));
        CompilationUnit unit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("No problem found", 1, unit.getMessages().length);
        //$NON-NLS-1$
        assertEquals("No problem found", 1, unit.getProblems().length);
        //$NON-NLS-1$
        assertTrue("FieldDeclaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        IVariableBinding variableBinding = fragment.resolveBinding();
        //$NON-NLS-1$
        assertNull("Got a binding", variableBinding);
    }

    /**
	 * Checking that null is returned for a resolveBinding if the type is unknown
	 */
    public void test0235() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0235", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertTrue("The fiels is not malformed", !isMalformed(node));
        CompilationUnit unit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("problems found", 0, unit.getMessages().length);
        //$NON-NLS-1$
        assertEquals("problems found", 0, unit.getProblems().length);
        //$NON-NLS-1$
        assertTrue("FieldDeclaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
        IVariableBinding variableBinding = fragment.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", variableBinding);
    }

    /**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=9452
	 */
    public void test0237() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "junit.framework", "TestCase.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
    }

    /**
	 * Check ThisExpression
	 */
    public void test0238() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0238", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        char[] source = sourceUnit.getSource().toCharArray();
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a type declaration statement", node instanceof TypeDeclarationStatement);
        TypeDeclarationStatement typeDeclarationStatement = (TypeDeclarationStatement) node;
        AbstractTypeDeclaration typeDecl = typeDeclarationStatement.getDeclaration();
        Object o = typeDecl.bodyDeclarations().get(0);
        //$NON-NLS-1$
        assertTrue("Not a method", o instanceof MethodDeclaration);
        MethodDeclaration methodDecl = (MethodDeclaration) o;
        Block block = methodDecl.getBody();
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("Not 1", 1, statements.size());
        Statement stmt = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not a return statement", stmt instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) stmt;
        Expression expr = returnStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a method invocation", expr instanceof MethodInvocation);
        MethodInvocation methodInvocation = (MethodInvocation) expr;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation, "Test.this.bar()", source);
        Expression qualifier = methodInvocation.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a ThisExpression", qualifier instanceof ThisExpression);
        ThisExpression thisExpression = (ThisExpression) qualifier;
        Name name = thisExpression.getQualifier();
        IBinding binding = name.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Test", binding.getName());
    }

    /**
	 * Check ThisExpression
	 */
    public void test0239() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0239", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 1, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a type declaration statement", node instanceof TypeDeclarationStatement);
        TypeDeclarationStatement typeDeclarationStatement = (TypeDeclarationStatement) node;
        AbstractTypeDeclaration typeDecl = typeDeclarationStatement.getDeclaration();
        Object o = typeDecl.bodyDeclarations().get(0);
        //$NON-NLS-1$
        assertTrue("Not a method", o instanceof MethodDeclaration);
        MethodDeclaration methodDecl = (MethodDeclaration) o;
        Block block = methodDecl.getBody();
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("Not 1", 1, statements.size());
        Statement stmt = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not a return statement", stmt instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) stmt;
        Expression expr = returnStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a SuperMethodInvocation", expr instanceof SuperMethodInvocation);
        SuperMethodInvocation superMethodInvocation = (SuperMethodInvocation) expr;
        Name name = superMethodInvocation.getQualifier();
        IBinding binding = name.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertTrue("A type binding", binding instanceof ITypeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not Test", "Test", binding.getName());
        Name methodName = superMethodInvocation.getName();
        IBinding binding2 = methodName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding2", binding2);
        //$NON-NLS-1$
        assertTrue("No an IMethodBinding", binding2 instanceof IMethodBinding);
        IMethodBinding methodBinding = (IMethodBinding) binding2;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not bar", "bar", methodBinding.getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not T", "T", methodBinding.getDeclaringClass().getName());
    }

    /**
	 * Check FieldAccess
	 */
    public void test0240() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0240", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a type declaration statement", node instanceof TypeDeclarationStatement);
        TypeDeclarationStatement typeDeclarationStatement = (TypeDeclarationStatement) node;
        AbstractTypeDeclaration typeDecl = typeDeclarationStatement.getDeclaration();
        Object o = typeDecl.bodyDeclarations().get(0);
        //$NON-NLS-1$
        assertTrue("Not a method", o instanceof MethodDeclaration);
        MethodDeclaration methodDecl = (MethodDeclaration) o;
        Block block = methodDecl.getBody();
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("Not 1", 1, statements.size());
        Statement stmt = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not a return statement", stmt instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) stmt;
        Expression expr = returnStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a field access", expr instanceof FieldAccess);
        FieldAccess fieldAccess = (FieldAccess) expr;
        Expression qualifier = fieldAccess.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a ThisExpression", qualifier instanceof ThisExpression);
        ThisExpression thisExpression = (ThisExpression) qualifier;
        Name name = thisExpression.getQualifier();
        IBinding binding = name.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not Test", "Test", binding.getName());
        Name fieldName = fieldAccess.getName();
        IBinding binding2 = fieldName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding2", binding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "f", binding2.getName());
        //$NON-NLS-1$
        assertEquals("Wrong modifier", Modifier.PUBLIC, binding2.getModifiers());
        ITypeBinding typeBinding = fieldName.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not int", "int", typeBinding.getName());
    }

    /**
	 * Check order of body declarations
	 */
    public void test0241() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0241", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0);
        //$NON-NLS-1$
        assertTrue("Not a type declaration", node instanceof TypeDeclaration);
        //$NON-NLS-1$
        assertTrue("Not a declaration", ((TypeDeclaration) node).getName().isDeclaration());
        //$NON-NLS-1$
        assertEquals("Wrong size", 11, ((TypeDeclaration) node).bodyDeclarations().size());
        node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a field declaration", node instanceof FieldDeclaration);
        node = getASTNode((CompilationUnit) result, 0, 1);
        //$NON-NLS-1$
        assertTrue("Not a MethodDeclaration", node instanceof MethodDeclaration);
        node = getASTNode((CompilationUnit) result, 0, 2);
        //$NON-NLS-1$
        assertTrue("Not a Type declaration", node instanceof TypeDeclaration);
        node = getASTNode((CompilationUnit) result, 0, 3);
        //$NON-NLS-1$
        assertTrue("Not a Type declaration", node instanceof TypeDeclaration);
        node = getASTNode((CompilationUnit) result, 0, 4);
        //$NON-NLS-1$
        assertTrue("Not a MethodDeclaration", node instanceof MethodDeclaration);
        node = getASTNode((CompilationUnit) result, 0, 5);
        //$NON-NLS-1$
        assertTrue("Not a field declaration", node instanceof FieldDeclaration);
        node = getASTNode((CompilationUnit) result, 0, 6);
        //$NON-NLS-1$
        assertTrue("Not a MethodDeclaration", node instanceof MethodDeclaration);
        node = getASTNode((CompilationUnit) result, 0, 7);
        //$NON-NLS-1$
        assertTrue("Not a field declaration", node instanceof FieldDeclaration);
        node = getASTNode((CompilationUnit) result, 0, 8);
        //$NON-NLS-1$
        assertTrue("Not a field declaration", node instanceof FieldDeclaration);
        node = getASTNode((CompilationUnit) result, 0, 9);
        //$NON-NLS-1$
        assertTrue("Not a MethodDeclaration", node instanceof MethodDeclaration);
        node = getASTNode((CompilationUnit) result, 0, 10);
        //$NON-NLS-1$
        assertTrue("Not a Type declaration", node instanceof TypeDeclaration);
    }

    /**
	 * Check ThisExpression
	 */
    public void test0242() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0242", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 1, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a type declaration statement", node instanceof TypeDeclarationStatement);
        TypeDeclarationStatement typeDeclarationStatement = (TypeDeclarationStatement) node;
        AbstractTypeDeclaration typeDecl = typeDeclarationStatement.getDeclaration();
        Object o = typeDecl.bodyDeclarations().get(0);
        //$NON-NLS-1$
        assertTrue("Not a method", o instanceof MethodDeclaration);
        MethodDeclaration methodDecl = (MethodDeclaration) o;
        Block block = methodDecl.getBody();
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("Not 1", 1, statements.size());
        Statement stmt = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not a return statement", stmt instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) stmt;
        Expression expr = returnStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a SuperFieldAccess", expr instanceof SuperFieldAccess);
        SuperFieldAccess superFieldAccess = (SuperFieldAccess) expr;
        Name name = superFieldAccess.getQualifier();
        IBinding binding = name.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertTrue("A type binding", binding instanceof ITypeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not Test", "Test", binding.getName());
        Name fieldName = superFieldAccess.getName();
        IBinding binding2 = fieldName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding2", binding2);
        //$NON-NLS-1$
        assertTrue("No an IVariableBinding", binding2 instanceof IVariableBinding);
        IVariableBinding variableBinding = (IVariableBinding) binding2;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not f", "f", variableBinding.getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not T", "T", variableBinding.getDeclaringClass().getName());
        ITypeBinding typeBinding2 = fieldName.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not int", "int", typeBinding2.getName());
    }

    /**
	 * Check catch clause positions:
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10570
	 */
    public void test0243() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0243", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a try statement", node instanceof TryStatement);
        TryStatement tryStatement = (TryStatement) node;
        List catchClauses = tryStatement.catchClauses();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, catchClauses.size());
        CatchClause catchClause = (CatchClause) catchClauses.get(0);
        //$NON-NLS-1$
        checkSourceRange(catchClause, "catch (Exception e){m();}", source);
    }

    /**
	 * Check catch clause positions:
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10570
	 */
    public void test0244() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0244", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a try statement", node instanceof TryStatement);
        TryStatement tryStatement = (TryStatement) node;
        List catchClauses = tryStatement.catchClauses();
        //$NON-NLS-1$
        assertEquals("wrong size", 2, catchClauses.size());
        CatchClause catchClause = (CatchClause) catchClauses.get(0);
        //$NON-NLS-1$
        checkSourceRange(catchClause, "catch (RuntimeException e){m();}", source);
        catchClause = (CatchClause) catchClauses.get(1);
        //$NON-NLS-1$
        checkSourceRange(catchClause, "catch(Exception e) {}", source);
    }

    /**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=10587
	 */
    public void test0245() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0245", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit unit = (CompilationUnit) result;
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertTrue("Not a return statement", node instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) node;
        Expression expr = returnStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("not a name", expr instanceof Name);
        Name name = (Name) expr;
        IBinding binding = name.resolveBinding();
        //$NON-NLS-1$
        assertTrue("Not a variable binding", binding instanceof IVariableBinding);
        IVariableBinding variableBinding = (IVariableBinding) binding;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not i", "i", variableBinding.getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not int", "int", variableBinding.getType().getName());
        ASTNode declaringNode = unit.findDeclaringNode(variableBinding);
        //$NON-NLS-1$
        assertNotNull("No declaring node", declaringNode);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationFragment", declaringNode instanceof VariableDeclarationFragment);
    }

    /**
	 * Test binding resolution for import declaration
	 */
    public void test0246() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0246", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit unit = (CompilationUnit) result;
        List imports = unit.imports();
        //$NON-NLS-1$
        assertEquals("wrong imports size", 2, imports.size());
        ImportDeclaration importDeclaration = (ImportDeclaration) imports.get(0);
        //$NON-NLS-1$
        assertTrue("Not on demand", importDeclaration.isOnDemand());
        //$NON-NLS-1$
        checkSourceRange(importDeclaration, "import java.util.*;", source);
        IBinding binding = importDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertEquals("Wrong type", IBinding.PACKAGE, binding.getKind());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "java.util", binding.getName());
        importDeclaration = (ImportDeclaration) imports.get(1);
        //$NON-NLS-1$
        assertTrue("On demand", !importDeclaration.isOnDemand());
        //$NON-NLS-1$
        checkSourceRange(importDeclaration, "import java.io.IOException;", source);
        binding = importDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertEquals("Wrong type", IBinding.TYPE, binding.getKind());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "IOException", binding.getName());
    }

    /**
	 * Test binding resolution for import declaration
	 */
    public void test0247() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0247", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit unit = (CompilationUnit) result;
        PackageDeclaration packageDeclaration = unit.getPackage();
        //$NON-NLS-1$
        checkSourceRange(packageDeclaration, "package test0247;", source);
        IPackageBinding binding = packageDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertEquals("Wrong type", IBinding.PACKAGE, binding.getKind());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "test0247", binding.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10592
	 */
    public void test0248() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0248", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node instanceof MethodDeclaration);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        List parameters = methodDeclaration.parameters();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, parameters.size());
        SingleVariableDeclaration singleVariableDeclaration = (SingleVariableDeclaration) parameters.get(0);
        Name name = singleVariableDeclaration.getName();
        IBinding binding = name.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertTrue("Not a variable binding", binding instanceof IVariableBinding);
        IVariableBinding variableBinding = (IVariableBinding) binding;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "i", variableBinding.getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong type", "int", variableBinding.getType().getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10592
	 */
    public void test0249() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0249", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 2, 1);
        //$NON-NLS-1$
        assertTrue("Not an ExpressionStatement", node instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not an assignment", expression instanceof Assignment);
        Assignment assignment = (Assignment) expression;
        Expression leftHandSide = assignment.getLeftHandSide();
        //$NON-NLS-1$
        assertTrue("Not a qualified name", leftHandSide instanceof QualifiedName);
        QualifiedName qualifiedName = (QualifiedName) leftHandSide;
        Name simpleName = qualifiedName.getName();
        IBinding binding = simpleName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no binding", binding);
        //$NON-NLS-1$
        assertTrue("Not a IVariableBinding", binding instanceof IVariableBinding);
        IVariableBinding variableBinding = (IVariableBinding) binding;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "k", variableBinding.getName());
        //$NON-NLS-1$
        assertEquals("Wrong modifier", Modifier.STATIC, variableBinding.getModifiers());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong type", "int", variableBinding.getType().getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong declaring class name", "j", variableBinding.getDeclaringClass().getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10592
	 */
    public void test0250() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0250", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node instanceof MethodDeclaration);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        List parameters = methodDeclaration.parameters();
        //$NON-NLS-1$
        assertEquals("wrong size", 2, parameters.size());
        SingleVariableDeclaration singleVariableDeclaration = (SingleVariableDeclaration) parameters.get(0);
        Name name = singleVariableDeclaration.getName();
        IBinding binding = name.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertTrue("Not a variable binding", binding instanceof IVariableBinding);
        IVariableBinding variableBinding = (IVariableBinding) binding;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "i", variableBinding.getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong type", "int", variableBinding.getType().getName());
    }

    /**
	 * Check qualified name resolution for static fields
	 */
    public void test0251() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0251", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a ExpressionStatement", node instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a method invocation", expression instanceof MethodInvocation);
        MethodInvocation methodInvocation = (MethodInvocation) expression;
        //$NON-NLS-1$
        checkSourceRange(methodInvocation, "java.lang.System.out.println()", source);
        Expression qualifier = methodInvocation.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a qualified name", qualifier instanceof QualifiedName);
        //$NON-NLS-1$
        checkSourceRange(qualifier, "java.lang.System.out", source);
        QualifiedName qualifiedName = (QualifiedName) qualifier;
        Name typeName = qualifiedName.getQualifier();
        //$NON-NLS-1$
        assertTrue("Not a QualifiedName", typeName instanceof QualifiedName);
        QualifiedName qualifiedTypeName = (QualifiedName) typeName;
        IBinding binding = qualifiedTypeName.getName().resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "System", binding.getName());
        binding = qualifiedTypeName.getQualifier().resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding2", binding);
        //$NON-NLS-1$
        assertEquals("Wrong type binding", IBinding.PACKAGE, binding.getKind());
    }

    /**
	 * Check binding for anonymous class
	 */
    public void test0252() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0252", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 1);
        //$NON-NLS-1$
        assertTrue("Not a return statement", node instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) node;
        Expression expression = returnStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a classinstancecreation", expression instanceof ClassInstanceCreation);
        ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
        IMethodBinding methodBinding = classInstanceCreation.resolveConstructorBinding();
        //$NON-NLS-1$
        assertNotNull("No methodBinding", methodBinding);
        //$NON-NLS-1$
        assertTrue("Not a constructor", methodBinding.isConstructor());
        //$NON-NLS-1$
        assertTrue("Not an anonymous class", methodBinding.getDeclaringClass().isAnonymous());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not an anonymous class of java.lang.Object", "Object", methodBinding.getDeclaringClass().getSuperclass().getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Not an anonymous class of java.lang.Object", "java.lang", methodBinding.getDeclaringClass().getSuperclass().getPackage().getName());
    }

    /**
	 * Check binding for allocation expression
	 */
    public void test0253() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0253", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a return statement", node instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) node;
        Expression expression = returnStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a classinstancecreation", expression instanceof ClassInstanceCreation);
        ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
        IMethodBinding methodBinding = classInstanceCreation.resolveConstructorBinding();
        //$NON-NLS-1$
        assertNotNull("No methodBinding", methodBinding);
        //$NON-NLS-1$
        assertTrue("Not a constructor", methodBinding.isConstructor());
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, methodBinding.getParameterTypes().length);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong type", "String", methodBinding.getParameterTypes()[0].getName());
    }

    /**
	 * Check binding for allocation expression
	 */
    public void test0254() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0254", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 1, 0);
        //$NON-NLS-1$
        assertTrue("Not a return statement", node instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) node;
        Expression expression = returnStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a class instance creation", expression instanceof ClassInstanceCreation);
        ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
        IMethodBinding binding = classInstanceCreation.resolveConstructorBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong type", "C", binding.getDeclaringClass().getName());
    }

    /**
	 * Check binding for allocation expression
	 */
    public void test0255() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0255", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not an ExpressionStatement", node instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a MethodInvocation", expression instanceof MethodInvocation);
        MethodInvocation methodInvocation = (MethodInvocation) expression;
        List arguments = methodInvocation.arguments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, arguments.size());
        Expression expression2 = (Expression) arguments.get(0);
        //$NON-NLS-1$
        assertTrue("Not a CastExpression", expression2 instanceof CastExpression);
        CastExpression castExpression = (CastExpression) expression2;
        Type type = castExpression.getType();
        ITypeBinding binding = type.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertTrue("Not an array type", binding.isArray());
    }

    /**
	 * Check binding for allocation expression
	 */
    public void test0256() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0256", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not an ExpressionStatement", node instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a MethodInvocation", expression instanceof MethodInvocation);
        MethodInvocation methodInvocation = (MethodInvocation) expression;
        List arguments = methodInvocation.arguments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, arguments.size());
        Expression expression2 = (Expression) arguments.get(0);
        //$NON-NLS-1$
        assertTrue("Not a CastExpression", expression2 instanceof CastExpression);
        CastExpression castExpression = (CastExpression) expression2;
        Type type = castExpression.getType();
        //$NON-NLS-1$
        assertTrue("Not a simple type", type.isSimpleType());
        SimpleType simpleType = (SimpleType) type;
        ITypeBinding binding = type.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertTrue("Not a class", binding.isClass());
        Name name = simpleType.getName();
        IBinding binding2 = name.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding2", binding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong type", "Object", binding2.getName());
    }

    /**
	 * Check binding for allocation expression
	 */
    public void test0257() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0257", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not an ExpressionStatement", node instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a MethodInvocation", expression instanceof MethodInvocation);
        MethodInvocation methodInvocation = (MethodInvocation) expression;
        List arguments = methodInvocation.arguments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, arguments.size());
        Expression expression2 = (Expression) arguments.get(0);
        //$NON-NLS-1$
        assertTrue("Not a CastExpression", expression2 instanceof CastExpression);
        CastExpression castExpression = (CastExpression) expression2;
        Type type = castExpression.getType();
        //$NON-NLS-1$
        assertTrue("Not a primitive type", type.isPrimitiveType());
        PrimitiveType primitiveType = (PrimitiveType) type;
        //$NON-NLS-1$
        assertEquals("Not int", PrimitiveType.INT, primitiveType.getPrimitiveTypeCode());
    }

    /**
	 * Check binding for allocation expression
	 */
    public void test0258() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0258", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not an ExpressionStatement", node instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a MethodInvocation", expression instanceof MethodInvocation);
        MethodInvocation methodInvocation = (MethodInvocation) expression;
        List arguments = methodInvocation.arguments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, arguments.size());
        Expression expression2 = (Expression) arguments.get(0);
        //$NON-NLS-1$
        assertTrue("Not a CastExpression", expression2 instanceof CastExpression);
        CastExpression castExpression = (CastExpression) expression2;
        Type type = castExpression.getType();
        //$NON-NLS-1$
        assertTrue("Not a simple type", type.isSimpleType());
        SimpleType simpleType = (SimpleType) type;
        ITypeBinding binding = type.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertTrue("Not a class", binding.isClass());
        Name name = simpleType.getName();
        IBinding binding2 = name.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding2", binding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong type", "Object", binding2.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10663
	 */
    public void test0259() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0259", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10592
	 */
    public void test0260() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0260", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node instanceof MethodDeclaration);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        List parameters = methodDeclaration.parameters();
        //$NON-NLS-1$
        assertEquals("wrong size", 2, parameters.size());
        SingleVariableDeclaration singleVariableDeclaration = (SingleVariableDeclaration) parameters.get(0);
        IBinding binding = singleVariableDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        Name name = singleVariableDeclaration.getName();
        //$NON-NLS-1$
        assertTrue("Not a simple name", name instanceof SimpleName);
        SimpleName simpleName = (SimpleName) name;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "i", simpleName.getIdentifier());
        IBinding binding2 = name.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding2);
        //$NON-NLS-1$
        assertTrue("binding == binding2", binding == binding2);
        //$NON-NLS-1$
        assertTrue("Not a variable binding", binding2 instanceof IVariableBinding);
        IVariableBinding variableBinding = (IVariableBinding) binding2;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "i", variableBinding.getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong type", "int", variableBinding.getType().getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10679
	 */
    public void test0261() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0261", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("Wrong msg size", 1, compilationUnit.getMessages().length);
        //$NON-NLS-1$
        assertEquals("Wrong pb size", 1, compilationUnit.getProblems().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a return statement", node instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) node;
        Expression expression = returnStatement.getExpression();
        ITypeBinding binding = expression.resolveTypeBinding();
        //$NON-NLS-1$
        assertNull("got a binding", binding);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10676
	 */
    public void test0262() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0262", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not an ExpressionStatement", node instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        Expression expr = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a MethodInvocation", expr instanceof MethodInvocation);
        MethodInvocation methodInvocation = (MethodInvocation) expr;
        List arguments = methodInvocation.arguments();
        //$NON-NLS-1$
        assertEquals("Wrong argument list size", 1, arguments.size());
        Expression expr2 = (Expression) arguments.get(0);
        //$NON-NLS-1$
        assertTrue("Not a class instance creation", expr2 instanceof ClassInstanceCreation);
        ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expr2;
        arguments = classInstanceCreation.arguments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, arguments.size());
        Expression expression2 = (Expression) arguments.get(0);
        //$NON-NLS-1$
        assertTrue("Not a string literal", expression2 instanceof StringLiteral);
        StringLiteral stringLiteral = (StringLiteral) expression2;
        ITypeBinding typeBinding = stringLiteral.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "String", typeBinding.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10700
	 */
    public void test0263() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0263", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not an ExpressionStatement", node instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        Expression expr = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a MethodInvocation", expr instanceof MethodInvocation);
        MethodInvocation methodInvocation = (MethodInvocation) expr;
        List arguments = methodInvocation.arguments();
        //$NON-NLS-1$
        assertEquals("Wrong argument list size", 1, arguments.size());
        Expression expr2 = (Expression) arguments.get(0);
        //$NON-NLS-1$
        assertTrue("Not a simple name", expr2 instanceof SimpleName);
        SimpleName simpleName = (SimpleName) expr2;
        IBinding binding = simpleName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10699
	 */
    public void test0264() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0264", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong fragment size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a classinstancecreation", expression instanceof ClassInstanceCreation);
        ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
        AnonymousClassDeclaration anonymousClassDeclaration = classInstanceCreation.getAnonymousClassDeclaration();
        //$NON-NLS-1$
        assertNotNull("No anonymousclassdeclaration", anonymousClassDeclaration);
        String expectedSourceRange = //$NON-NLS-1$
        "{\n" + "			void m(int k){\n" + "				k= i;\n" + "			}\n" + "		}";
        checkSourceRange(anonymousClassDeclaration, expectedSourceRange, source);
        List bodyDeclarations = anonymousClassDeclaration.bodyDeclarations();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, bodyDeclarations.size());
        BodyDeclaration bodyDeclaration = (BodyDeclaration) bodyDeclarations.get(0);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", bodyDeclaration instanceof MethodDeclaration);
        MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "m", methodDeclaration.getName().getIdentifier());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10698
	 */
    public void test0265() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0265", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10759
	 */
    public void test0266() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0266", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 1, 0);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        Type type = variableDeclarationStatement.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "Inner\\u005b]", source);
        //$NON-NLS-1$
        assertTrue("Not an array type", type.isArrayType());
        ArrayType arrayType = (ArrayType) type;
        Type type2 = arrayType.getElementType();
        //$NON-NLS-1$
        assertTrue("Not a simple type", type2.isSimpleType());
        SimpleType simpleType = (SimpleType) type2;
        //$NON-NLS-1$
        checkSourceRange(simpleType, "Inner", source);
        Name name = simpleType.getName();
        //$NON-NLS-1$
        assertTrue("not a simple name", name.isSimpleName());
        SimpleName simpleName = (SimpleName) name;
        //$NON-NLS-1$
        checkSourceRange(simpleName, "Inner", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10759
	 */
    public void test0267() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0267", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 1, 0);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        Type type = variableDeclarationStatement.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "Inner[]", source);
        //$NON-NLS-1$
        assertTrue("Not an array type", type.isArrayType());
        ArrayType arrayType = (ArrayType) type;
        Type type2 = arrayType.getElementType();
        //$NON-NLS-1$
        assertTrue("Not a simple type", type2.isSimpleType());
        SimpleType simpleType = (SimpleType) type2;
        //$NON-NLS-1$
        checkSourceRange(simpleType, "Inner", source);
        Name name = simpleType.getName();
        //$NON-NLS-1$
        assertTrue("not a simple name", name.isSimpleName());
        SimpleName simpleName = (SimpleName) name;
        //$NON-NLS-1$
        checkSourceRange(simpleName, "Inner", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10759
	 */
    public void test0268() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0268", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 1, 0);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        Type type = variableDeclarationStatement.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "test0268.Test.Inner[]", source);
        //$NON-NLS-1$
        assertTrue("Not an array type", type.isArrayType());
        ArrayType arrayType = (ArrayType) type;
        Type type2 = arrayType.getElementType();
        //$NON-NLS-1$
        assertTrue("Not a simple type", type2.isSimpleType());
        SimpleType simpleType = (SimpleType) type2;
        //$NON-NLS-1$
        checkSourceRange(simpleType, "test0268.Test.Inner", source);
        Name name = simpleType.getName();
        //$NON-NLS-1$
        assertTrue("not a qualified name", name.isQualifiedName());
        //$NON-NLS-1$
        checkSourceRange(name, "test0268.Test.Inner", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10759
	 */
    public void test0269() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0269", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 1, 0);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        Type type = variableDeclarationStatement.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "test0269.Test.Inner[/**/]", source);
        //$NON-NLS-1$
        assertTrue("Not an array type", type.isArrayType());
        ArrayType arrayType = (ArrayType) type;
        Type type2 = arrayType.getElementType();
        //$NON-NLS-1$
        assertTrue("Not a simple type", type2.isSimpleType());
        SimpleType simpleType = (SimpleType) type2;
        //$NON-NLS-1$
        checkSourceRange(simpleType, "test0269.Test.Inner", source);
        Name name = simpleType.getName();
        //$NON-NLS-1$
        assertTrue("not a qualified name", name.isQualifiedName());
        //$NON-NLS-1$
        checkSourceRange(name, "test0269.Test.Inner", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10759
	 */
    public void test0270() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0270", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 1, 0);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        Type type = variableDeclarationStatement.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "test0270.Test.Inner", source);
        //$NON-NLS-1$
        assertTrue("Not a simple type", type.isSimpleType());
        SimpleType simpleType = (SimpleType) type;
        Name name = simpleType.getName();
        //$NON-NLS-1$
        assertTrue("not a qualified name", name.isQualifiedName());
        //$NON-NLS-1$
        checkSourceRange(name, "test0270.Test.Inner", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10759
	 */
    public void test0271() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0271", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 1, 0);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        Type type = variableDeclarationStatement.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "test0271.Test.Inner[]", source);
        //$NON-NLS-1$
        assertTrue("Not an array type", type.isArrayType());
        ArrayType arrayType = (ArrayType) type;
        Type type2 = arrayType.getElementType();
        //$NON-NLS-1$
        assertTrue("Not a simple type", type2.isSimpleType());
        SimpleType simpleType = (SimpleType) type2;
        //$NON-NLS-1$
        checkSourceRange(simpleType, "test0271.Test.Inner", source);
        Name name = simpleType.getName();
        //$NON-NLS-1$
        assertTrue("not a qualified name", name.isQualifiedName());
        //$NON-NLS-1$
        checkSourceRange(name, "test0271.Test.Inner", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10843
	 */
    public void test0272() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0272", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a For statement", node instanceof ForStatement);
        ForStatement forStatement = (ForStatement) node;
        //$NON-NLS-1$
        checkSourceRange(forStatement, "for (int i= 0; i < 10; i++) foo();", source);
        Statement action = forStatement.getBody();
        //$NON-NLS-1$
        checkSourceRange(action, "foo();", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10843
	 */
    public void test0273() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0273", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a For statement", node instanceof ForStatement);
        ForStatement forStatement = (ForStatement) node;
        //$NON-NLS-1$
        checkSourceRange(forStatement, "for (int i= 0; i < 10; i++) { foo(); }", source);
        Statement action = forStatement.getBody();
        //$NON-NLS-1$
        checkSourceRange(action, "{ foo(); }", source);
        //$NON-NLS-1$
        assertTrue("Not a block", action instanceof Block);
        Block block = (Block) action;
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, statements.size());
        Statement stmt = (Statement) statements.get(0);
        //$NON-NLS-1$
        checkSourceRange(stmt, "foo();", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10843
	 */
    public void test0274() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0274", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 1);
        //$NON-NLS-1$
        assertTrue("Not a While statement", node instanceof WhileStatement);
        WhileStatement whileStatement = (WhileStatement) node;
        //$NON-NLS-1$
        checkSourceRange(whileStatement, "while (i < 10) { foo(i++); }", source);
        Statement action = whileStatement.getBody();
        //$NON-NLS-1$
        checkSourceRange(action, "{ foo(i++); }", source);
        //$NON-NLS-1$
        assertTrue("Not a block", action instanceof Block);
        Block block = (Block) action;
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, statements.size());
        Statement stmt = (Statement) statements.get(0);
        //$NON-NLS-1$
        checkSourceRange(stmt, "foo(i++);", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10843
	 */
    public void test0275() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0275", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 1);
        //$NON-NLS-1$
        assertTrue("Not a While statement", node instanceof WhileStatement);
        WhileStatement whileStatement = (WhileStatement) node;
        //$NON-NLS-1$
        checkSourceRange(whileStatement, "while (i < 10) foo(i++);", source);
        Statement action = whileStatement.getBody();
        //$NON-NLS-1$
        checkSourceRange(action, "foo(i++);", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10798
	 */
    public void test0276() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0276", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node instanceof MethodDeclaration);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        String expectedSource = //$NON-NLS-1$
        "public void foo() {\n" + "		foo();\n" + //$NON-NLS-1$
        "	}";
        checkSourceRange(methodDeclaration, expectedSource, source);
        expectedSource = //$NON-NLS-1$
        "{\n" + "		foo();\n" + //$NON-NLS-1$
        "	}";
        checkSourceRange(methodDeclaration.getBody(), expectedSource, source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10798
	 */
    public void test0277() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0277", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node instanceof MethodDeclaration);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        String expectedSource = //$NON-NLS-1$
        "public void foo() {\n" + //$NON-NLS-1$
        "	}";
        checkSourceRange(methodDeclaration, expectedSource, source);
        expectedSource = //$NON-NLS-1$
        "{\n" + //$NON-NLS-1$
        "	}";
        checkSourceRange(methodDeclaration.getBody(), expectedSource, source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10861
	 */
    public void test0278() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0278", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a Field declaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        //$NON-NLS-1$
        checkSourceRange(fieldDeclaration, "Class c = java.lang.String.class;", source);
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a type literal", expression instanceof TypeLiteral);
        ITypeBinding typeBinding = expression.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Class", typeBinding.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10861
	 */
    public void test0279() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0279", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        //$NON-NLS-1$
        checkSourceRange(variableDeclarationStatement, "Class c = java.lang.String.class;", source);
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a type literal", expression instanceof TypeLiteral);
        ITypeBinding typeBinding = expression.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Class", typeBinding.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10865
	 * Check well known types
	 */
    public void test0280() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0280", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        AST newAst = result.getAST();
        //$NON-NLS-1$
        ITypeBinding typeBinding = newAst.resolveWellKnownType("boolean");
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "boolean", typeBinding.getName());
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("char");
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "char", typeBinding.getName());
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("byte");
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "byte", typeBinding.getName());
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("short");
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "short", typeBinding.getName());
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("int");
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "int", typeBinding.getName());
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("long");
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "long", typeBinding.getName());
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("float");
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "float", typeBinding.getName());
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("double");
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "double", typeBinding.getName());
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("void");
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "void", typeBinding.getName());
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.Object");
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Object", typeBinding.getName());
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.String");
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "String", typeBinding.getName());
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.StringBuffer");
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "StringBuffer", typeBinding.getName());
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.Throwable");
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Throwable", typeBinding.getName());
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.Exception");
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Exception", typeBinding.getName());
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.RuntimeException");
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "RuntimeException", typeBinding.getName());
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.Error");
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Error", typeBinding.getName());
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.Class");
        //$NON-NLS-1$
        assertNotNull("No typeBinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Class", typeBinding.getName());
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.Runnable");
        //$NON-NLS-1$
        assertNull("typeBinding not null", typeBinding);
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.Cloneable");
        //$NON-NLS-1$
        assertNotNull("typeBinding not null", typeBinding);
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.io.Serializable");
        //$NON-NLS-1$
        assertNotNull("typeBinding not null", typeBinding);
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.Boolean");
        //$NON-NLS-1$
        assertNotNull("typeBinding not null", typeBinding);
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.Byte");
        //$NON-NLS-1$
        assertNotNull("typeBinding not null", typeBinding);
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.Character");
        //$NON-NLS-1$
        assertNotNull("typeBinding not null", typeBinding);
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.Double");
        //$NON-NLS-1$
        assertNotNull("typeBinding not null", typeBinding);
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.Float");
        //$NON-NLS-1$
        assertNotNull("typeBinding not null", typeBinding);
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.Integer");
        //$NON-NLS-1$
        assertNotNull("typeBinding not null", typeBinding);
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.Long");
        //$NON-NLS-1$
        assertNotNull("typeBinding not null", typeBinding);
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.Short");
        //$NON-NLS-1$
        assertNotNull("typeBinding not null", typeBinding);
        //$NON-NLS-1$
        typeBinding = newAst.resolveWellKnownType("java.lang.Void");
        //$NON-NLS-1$
        assertNotNull("typeBinding not null", typeBinding);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10874
	 */
    public void test0281() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0281", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a Field declaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        //$NON-NLS-1$
        checkSourceRange(fieldDeclaration, "Object o= /*]*/new Object()/*[*/;", source);
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        checkSourceRange(expression, "new Object()", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10874
	 */
    public void test0282() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0282", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a Field declaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        //$NON-NLS-1$
        checkSourceRange(fieldDeclaration, "boolean b = /*]*/true/*[*/;", source);
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        checkSourceRange(expression, "true", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10874
	 */
    public void test0283() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0283", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a Field declaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        //$NON-NLS-1$
        checkSourceRange(fieldDeclaration, "char c = /*]*/'c'/*[*/;", source);
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        checkSourceRange(expression, "'c'", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10874
	 */
    public void test0284() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0284", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a Field declaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        //$NON-NLS-1$
        checkSourceRange(fieldDeclaration, "Object o = /*]*/null/*[*/;", source);
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        checkSourceRange(expression, "null", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10874
	 */
    public void test0285() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0285", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a Field declaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        //$NON-NLS-1$
        checkSourceRange(fieldDeclaration, "Object o = /*]*/Object.class/*[*/;", source);
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        checkSourceRange(expression, "Object.class", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10874
	 */
    public void test0286() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0286", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a Field declaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        //$NON-NLS-1$
        checkSourceRange(fieldDeclaration, "int i = /**/(2)/**/;", source);
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        checkSourceRange(expression, "(2)", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10874
	 */
    public void test0287() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0287", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a Field declaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        //$NON-NLS-1$
        checkSourceRange(fieldDeclaration, "String[] tab = /**/new String[3]/**/;", source);
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        checkSourceRange(expression, "new String[3]", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10874
	 */
    public void test0288() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0288", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a Field declaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        //$NON-NLS-1$
        checkSourceRange(fieldDeclaration, "String[] tab = /**/{ }/**/;", source);
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        checkSourceRange(expression, "{ }", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10874
	 */
    public void test0289() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0289", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 1);
        //$NON-NLS-1$
        assertTrue("Not a Field declaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        //$NON-NLS-1$
        checkSourceRange(fieldDeclaration, "String s = /**/tab1[0]/**/;", source);
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        checkSourceRange(expression, "tab1[0]", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10874
	 */
    public void test0290() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0290", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a Field declaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        //$NON-NLS-1$
        checkSourceRange(fieldDeclaration, "Object o = /*]*/new java.lang.Object()/*[*/;", source);
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        checkSourceRange(expression, "new java.lang.Object()", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10898
	 */
    public void test0291() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0291", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit unit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("no errors", 1, unit.getMessages().length);
        //$NON-NLS-1$
        assertEquals("no errors", 1, unit.getProblems().length);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10913
	 */
    public void test0292() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0292", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a return statement", node instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) node;
        Expression expression = returnStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a qualifiedName", expression instanceof QualifiedName);
        QualifiedName qualifiedName = (QualifiedName) expression;
        SimpleName simpleName = qualifiedName.getName();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "x", simpleName.getIdentifier());
        IBinding binding = simpleName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("NO binding", binding);
        //$NON-NLS-1$
        assertTrue("Not a variable binding", binding instanceof IVariableBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "x", binding.getName());
        Name name = qualifiedName.getQualifier();
        //$NON-NLS-1$
        assertTrue("Not a simpleName", name instanceof SimpleName);
        SimpleName simpleName2 = (SimpleName) name;
        IBinding binding2 = simpleName2.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding2", binding2);
        //$NON-NLS-1$
        assertTrue("Not a type binding", binding2 instanceof ITypeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Test", binding2.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10933
 	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10935
	 */
    public void test0293() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0293", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a class instance creation", expression instanceof ClassInstanceCreation);
        ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
        AnonymousClassDeclaration anonymousClassDeclaration = classInstanceCreation.getAnonymousClassDeclaration();
        //$NON-NLS-1$
        assertNotNull("No body", anonymousClassDeclaration);
        String expectedSource = //$NON-NLS-1$
        "{\n" + "			public void run() {\n" + "				/*]*/foo();/*[*/\n" + "			}\n" + //$NON-NLS-1$
        "		}";
        checkSourceRange(anonymousClassDeclaration, expectedSource, source);
        expectedSource = //$NON-NLS-1$
        "run= new Runnable() {\n" + "			public void run() {\n" + "				/*]*/foo();/*[*/\n" + "			}\n" + //$NON-NLS-1$
        "		}";
        checkSourceRange(variableDeclarationFragment, expectedSource, source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10984
	 */
    public void test0294() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0294", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node instanceof MethodDeclaration);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        String expectedSource = //$NON-NLS-1$
        "public void fails() {\n" + "		foo()\n" + //$NON-NLS-1$
        "	}";
        checkSourceRange(methodDeclaration, expectedSource, source, /*expectMalformed*/
        true);
        Block block = methodDeclaration.getBody();
        expectedSource = //$NON-NLS-1$
        "{\n" + "		foo()\n" + //$NON-NLS-1$
        "	}";
        checkSourceRange(block, expectedSource, source);
        node = getASTNode(compilationUnit, 0, 1);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node instanceof MethodDeclaration);
        methodDeclaration = (MethodDeclaration) node;
        block = methodDeclaration.getBody();
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10986
	 */
    public void test0295() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0295", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true, false, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, compilationUnit.getMessages().length);
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, compilationUnit.getProblems().length);
        ASTNode node = getASTNode(compilationUnit, 0, 1, 0);
        //$NON-NLS-1$
        assertTrue("Not an ExpressionStatement", node instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("not a method invocation", expression instanceof MethodInvocation);
        MethodInvocation methodInvocation = (MethodInvocation) expression;
        ITypeBinding typeBinding = methodInvocation.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        assertEquals("LList;", typeBinding.getKey());
        assertEquals("Ltest0295/Test;.g()LList;", methodInvocation.resolveMethodBinding().getKey());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10984
	 */
    public void test0296() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0296", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node instanceof MethodDeclaration);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        String expectedSource = //$NON-NLS-1$
        "public void fails() {\n" + "		foo()\n" + //$NON-NLS-1$
        "	}";
        checkSourceRange(methodDeclaration, expectedSource, source, /*expectMalformed*/
        true);
        Block block = methodDeclaration.getBody();
        expectedSource = //$NON-NLS-1$
        "{\n" + "		foo()\n" + //$NON-NLS-1$
        "	}";
        checkSourceRange(block, expectedSource, source);
        node = getASTNode(compilationUnit, 0, 1);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node instanceof MethodDeclaration);
        methodDeclaration = (MethodDeclaration) node;
        block = methodDeclaration.getBody();
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11037
	 */
    public void test0297() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0297", "Test.java");
        runConversion(getJLS4(), sourceUnit, false);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10984
	 */
    public void test0298() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0298", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a ReturnStatement", node instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) node;
        Expression expression = returnStatement.getExpression();
        //$NON-NLS-1$
        checkSourceRange(expression, "a().length != 3", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10874
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11104
	 */
    public void test0299() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0299", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a Field declaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        //$NON-NLS-1$
        checkSourceRange(fieldDeclaration, "int i = (/**/2/**/);", source);
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a parenthesized expression", expression instanceof ParenthesizedExpression);
        ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) expression;
        Expression expression2 = parenthesizedExpression.getExpression();
        //$NON-NLS-1$
        checkSourceRange(expression2, "2", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10874
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11104
	 */
    public void test0300() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0300", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a Field declaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        //$NON-NLS-1$
        checkSourceRange(fieldDeclaration, "boolean b = /**/true/**/;", source);
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        checkSourceRange(expression, "true", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=10874
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11104
	 */
    public void test0301() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0301", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a Field declaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        //$NON-NLS-1$
        checkSourceRange(fieldDeclaration, "Object o = /**/null/**/;", source);
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        checkSourceRange(expression, "null", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11106
	 */
    public void test0302() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0302", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a DoStatement", node instanceof DoStatement);
        DoStatement doStatement = (DoStatement) node;
        String expectedSource = //$NON-NLS-1$
        "do\n" + "			foo();\n" + "		while(1 < 10);";
        checkSourceRange(doStatement, expectedSource, source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11129
	 */
    public void test0303() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0303", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 1);
        //$NON-NLS-1$
        assertTrue("Not an ExpressionStatement", node instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        Expression expression2 = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not an Assignement", expression2 instanceof Assignment);
        Assignment assignment = (Assignment) expression2;
        Expression expression = assignment.getRightHandSide();
        //$NON-NLS-1$
        assertTrue("Not a CastExpression", expression instanceof CastExpression);
        CastExpression castExpression = (CastExpression) expression;
        ITypeBinding typeBinding = castExpression.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "char", typeBinding.getName());
        Type type = castExpression.getType();
        ITypeBinding typeBinding2 = type.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding2", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "char", typeBinding2.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11151
	 */
    public void test0304() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0304", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("not a method declaration", node instanceof MethodDeclaration);
        //$NON-NLS-1$
        checkSourceRange(node, "public void foo(int arg);", source);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        Block block = methodDeclaration.getBody();
        //$NON-NLS-1$
        assertNull("Has a body", block);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11125
	 */
    public void test0305() {
        char[] source = (//$NON-NLS-1$
        "package test0305;\n" + //$NON-NLS-1$
        "\n" + //$NON-NLS-1$
        "class Test {\n" + "	public void foo(int arg) {}\n" + //$NON-NLS-1$
        "}").toCharArray();
        //$NON-NLS-1$
        IJavaProject project = getJavaProject("Converter");
        //$NON-NLS-1$
        ASTNode result = runConversion(getJLS4(), source, "Test.java", project, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0);
        //$NON-NLS-1$
        assertTrue("not a TypeDeclaration", node instanceof TypeDeclaration);
        TypeDeclaration typeDeclaration = (TypeDeclaration) node;
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Test", typeBinding.getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong package", "test0305", typeBinding.getPackage().getName());
        //$NON-NLS-1$
        assertTrue("Not an interface", typeBinding.isClass());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11125
	 */
    public void test0306() {
        char[] source = (//$NON-NLS-1$
        "package java.lang;\n" + //$NON-NLS-1$
        "\n" + //$NON-NLS-1$
        "class Object {\n" + "	public void foo(int arg) {}\n" + //$NON-NLS-1$
        "}").toCharArray();
        //$NON-NLS-1$
        IJavaProject project = getJavaProject("Converter");
        //$NON-NLS-1$
        ASTNode result = runConversion(getJLS4(), source, "Object.java", project, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0);
        //$NON-NLS-1$
        assertTrue("not a TypeDeclaration", node instanceof TypeDeclaration);
        TypeDeclaration typeDeclaration = (TypeDeclaration) node;
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Object", typeBinding.getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong package", "java.lang", typeBinding.getPackage().getName());
        //$NON-NLS-1$
        assertTrue("Not an interface", typeBinding.isClass());
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, typeBinding.getDeclaredMethods().length);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11371
	 */
    public void test0307() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0307", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 1, 0);
        //$NON-NLS-1$
        assertTrue("not a method declaration", node instanceof MethodDeclaration);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        Block block = methodDeclaration.getBody();
        //$NON-NLS-1$
        assertNotNull("No body", block);
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not a super constructor invocation", statement instanceof SuperConstructorInvocation);
        checkSourceRange(statement, "super(10);", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11371
	 */
    public void test0308() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0308", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 1, 0);
        //$NON-NLS-1$
        assertTrue("not a method declaration", node instanceof MethodDeclaration);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        Block block = methodDeclaration.getBody();
        //$NON-NLS-1$
        assertNotNull("No body", block);
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not a super constructor invocation", statement instanceof SuperConstructorInvocation);
        SuperConstructorInvocation superConstructorInvocation = (SuperConstructorInvocation) statement;
        IMethodBinding methodBinding = superConstructorInvocation.resolveConstructorBinding();
        //$NON-NLS-1$
        assertNotNull("No methodBinding", methodBinding);
        IMethodBinding methodBinding2 = methodDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No methodBinding2", methodBinding2);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11380
	 */
    public void test0309() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0309", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a conditional expression", expression instanceof ConditionalExpression);
        ConditionalExpression conditionalExpression = (ConditionalExpression) expression;
        ITypeBinding typeBinding = conditionalExpression.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "int", typeBinding.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11380
	 */
    public void test0310() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0310", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("not a FieldDeclaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a qualified name", expression instanceof QualifiedName);
        QualifiedName qualifiedName = (QualifiedName) expression;
        Name qualifier = qualifiedName.getQualifier();
        IBinding binding = qualifier.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "I", binding.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11638
	 */
    public void test0311() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0311", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 1);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("not a class instance creation", expression instanceof ClassInstanceCreation);
        ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
        AnonymousClassDeclaration anonymousClassDeclaration = classInstanceCreation.getAnonymousClassDeclaration();
        //$NON-NLS-1$
        assertNotNull("No body", anonymousClassDeclaration);
        List bodyDeclarations = anonymousClassDeclaration.bodyDeclarations();
        //$NON-NLS-1$
        assertEquals("wrong size for body declarations", 1, bodyDeclarations.size());
        BodyDeclaration bodyDeclaration = (BodyDeclaration) bodyDeclarations.get(0);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", bodyDeclaration instanceof MethodDeclaration);
        MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
        Block block = methodDeclaration.getBody();
        //$NON-NLS-1$
        assertNotNull("no body", block);
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("Wrong size for statements", 1, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("not a variable declaration statement", statement instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement2 = (VariableDeclarationStatement) statement;
        List fragments2 = variableDeclarationStatement2.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size for fragments2", 1, fragments2.size());
        VariableDeclarationFragment variableDeclarationFragment2 = (VariableDeclarationFragment) fragments2.get(0);
        Expression expression2 = variableDeclarationFragment2.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a name", expression2 instanceof Name);
        Name name = (Name) expression2;
        //$NON-NLS-1$
        checkSourceRange(name, "j", source);
        IBinding binding = name.resolveBinding();
        ASTNode declaringNode = compilationUnit.findDeclaringNode(binding);
        //$NON-NLS-1$
        assertNotNull("No declaring node", declaringNode);
        //$NON-NLS-1$
        checkSourceRange(declaringNode, "int j", source);
        //$NON-NLS-1$
        assertTrue("Not a single variable declaration", declaringNode instanceof SingleVariableDeclaration);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11638
	 * There is a error in this source. A is unresolved. Then there is no
	 * declaring node.
	 */
    public void test0312() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0312", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 1);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("not a class instance creation", expression instanceof ClassInstanceCreation);
        ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
        AnonymousClassDeclaration anonymousClassDeclaration = classInstanceCreation.getAnonymousClassDeclaration();
        //$NON-NLS-1$
        assertNotNull("No body", anonymousClassDeclaration);
        List bodyDeclarations = anonymousClassDeclaration.bodyDeclarations();
        //$NON-NLS-1$
        assertEquals("wrong size for body declarations", 1, bodyDeclarations.size());
        BodyDeclaration bodyDeclaration = (BodyDeclaration) bodyDeclarations.get(0);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", bodyDeclaration instanceof MethodDeclaration);
        MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
        Block block = methodDeclaration.getBody();
        //$NON-NLS-1$
        assertNotNull("no body", block);
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("Wrong size for statements", 1, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("not a variable declaration statement", statement instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement2 = (VariableDeclarationStatement) statement;
        List fragments2 = variableDeclarationStatement2.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size for fragments2", 1, fragments2.size());
        VariableDeclarationFragment variableDeclarationFragment2 = (VariableDeclarationFragment) fragments2.get(0);
        Expression expression2 = variableDeclarationFragment2.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a name", expression2 instanceof Name);
        Name name = (Name) expression2;
        //$NON-NLS-1$
        checkSourceRange(name, "j", source);
        IBinding binding = name.resolveBinding();
        ASTNode declaringNode = compilationUnit.findDeclaringNode(binding);
        //$NON-NLS-1$
        assertNotNull("No declaring node is available", declaringNode);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11659
	 */
    public void test0313() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0313", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not an InfixExpression", expression instanceof InfixExpression);
        InfixExpression infixExpression = (InfixExpression) expression;
        //$NON-NLS-1$
        checkSourceRange(infixExpression, "i+j", source);
        Expression expression2 = infixExpression.getLeftOperand();
        //$NON-NLS-1$
        checkSourceRange(expression2, "i", source);
        //$NON-NLS-1$
        assertTrue("Not a name", expression2 instanceof Name);
        Name name = (Name) expression2;
        IBinding binding = name.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        ASTNode astNode = compilationUnit.findDeclaringNode(binding);
        //$NON-NLS-1$
        assertNotNull("No declaring node", astNode);
        //$NON-NLS-1$
        checkSourceRange(astNode, "int i", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=12326
	 */
    public void test0314() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0314", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        //$NON-NLS-1$
        assertNotNull("No result", result);
        //$NON-NLS-1$
        assertTrue("Not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("Wrong line number", 1, compilationUnit.getLineNumber(0));
        // ensure that last character is on the last line
        //$NON-NLS-1$
        assertEquals("Wrong line number", 3, compilationUnit.getLineNumber(source.length - 1));
        // source.length is beyond the size of the compilation unit source
        //$NON-NLS-1$
        assertEquals("Wrong line number", -1, compilationUnit.getLineNumber(source.length));
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=12326
	 */
    public void test0315() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0315", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a Return statement", node instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) node;
        Expression expression = returnStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not an instanceof expression", expression instanceof InstanceofExpression);
        InstanceofExpression instanceOfExpression = (InstanceofExpression) expression;
        Type rightOperand = instanceOfExpression.getRightOperand();
        //$NON-NLS-1$
        assertTrue("Not a simpleType", rightOperand instanceof SimpleType);
        SimpleType simpleType = (SimpleType) rightOperand;
        Name n = simpleType.getName();
        //$NON-NLS-1$
        assertTrue("Not a qualified name", n instanceof QualifiedName);
        QualifiedName name = (QualifiedName) n;
        //$NON-NLS-1$
        checkSourceRange(name, "java.io.Serializable", source);
        ITypeBinding typeBinding = name.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Serializable", typeBinding.getName());
        Name qualifier = name.getQualifier();
        //$NON-NLS-1$
        assertTrue("Not a qualified name", qualifier instanceof QualifiedName);
        ITypeBinding typeBinding2 = qualifier.resolveTypeBinding();
        //$NON-NLS-1$
        assertNull("typebinding2 is not null", typeBinding2);
        IBinding binding = qualifier.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no binding", binding);
        //$NON-NLS-1$
        assertEquals("Wrong type", IBinding.PACKAGE, binding.getKind());
        IPackageBinding pBinding = (IPackageBinding) binding;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "java.io", pBinding.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=12454
	 */
    public void test0316() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "", "Hello.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No result", result);
        //$NON-NLS-1$
        assertTrue("Not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, compilationUnit.getMessages().length);
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, compilationUnit.getProblems().length);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=12781
	 */
    public void test0317() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0317", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a return statement", node instanceof ReturnStatement);
        ReturnStatement returnStatement = (ReturnStatement) node;
        Expression expression = returnStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("not an instanceof expression", expression instanceof InstanceofExpression);
        InstanceofExpression instanceOfExpression = (InstanceofExpression) expression;
        Expression left = instanceOfExpression.getLeftOperand();
        //$NON-NLS-1$
        assertTrue("Not a Name", left instanceof Name);
        Name name = (Name) left;
        IBinding binding = name.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "x", binding.getName());
        ITypeBinding typeBinding = name.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No typebinding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong type", "Object", typeBinding.getName());
        Type right = instanceOfExpression.getRightOperand();
        //$NON-NLS-1$
        assertTrue("Not a simpleType", right instanceof SimpleType);
        SimpleType simpleType = (SimpleType) right;
        name = simpleType.getName();
        //$NON-NLS-1$
        assertTrue("Not a simpleName", name instanceof SimpleName);
        SimpleName simpleName = (SimpleName) name;
        IBinding binding2 = simpleName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding2", binding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Vector", binding2.getName());
        ITypeBinding typeBinding2 = simpleName.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No typeBinding2", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Vector", typeBinding2.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=13233
	 */
    public void test0318() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0318", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit unit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("No error", 1, unit.getMessages().length);
        //$NON-NLS-1$
        assertEquals("No error", 1, unit.getProblems().length);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=13807
	 */
    public void test0319() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0319", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not an array creation", expression instanceof ArrayCreation);
        ArrayCreation arrayCreation = (ArrayCreation) expression;
        ITypeBinding typeBinding = arrayCreation.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("no type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object[]", typeBinding.getName());
        ArrayType arrayType = arrayCreation.getType();
        ITypeBinding typeBinding2 = arrayType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no type binding2", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object[]", typeBinding2.getName());
        Type type = arrayType.getElementType();
        //$NON-NLS-1$
        assertTrue("Not a simple type", type instanceof SimpleType);
        SimpleType simpleType = (SimpleType) type;
        ITypeBinding typeBinding3 = simpleType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no type binding3", typeBinding3);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object", typeBinding3.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=13807
	 */
    public void test0320() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0320", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        Type type = variableDeclarationStatement.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "int[]", source);
        //$NON-NLS-1$
        assertTrue("Not an array type", type.isArrayType());
        ArrayType arrayType = (ArrayType) type;
        ITypeBinding typeBinding = arrayType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        Type elementType = arrayType.getElementType();
        //$NON-NLS-1$
        assertTrue("Not a simple type", elementType.isPrimitiveType());
        ITypeBinding typeBinding2 = elementType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding2", typeBinding2);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=13807
	 */
    public void test0321() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0321", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        Type type = variableDeclarationStatement.getType();
        //$NON-NLS-1$
        assertTrue("Not an array type", type.isArrayType());
        ArrayType arrayType = (ArrayType) type;
        ITypeBinding typeBinding = arrayType.resolveBinding();
        //$NON-NLS-1$
        checkSourceRange(type, "java.lang.Object[][]", source);
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        Type elementType = componentType(arrayType);
        ITypeBinding typeBinding2 = elementType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding2", typeBinding2);
        //$NON-NLS-1$
        assertEquals("wrong dimension", 1, typeBinding2.getDimensions());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object[]", typeBinding2.getName());
        //$NON-NLS-1$
        assertTrue("Not an array type", elementType.isArrayType());
        Type elementType2 = componentType(((ArrayType) elementType));
        //$NON-NLS-1$
        assertTrue("Not a simple type", elementType2.isSimpleType());
        ITypeBinding typeBinding3 = elementType2.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding3", typeBinding3);
        //$NON-NLS-1$
        assertEquals("wrong dimension", 0, typeBinding3.getDimensions());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object", typeBinding3.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=13231
	 */
    public void test0322() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0322", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a FieldDeclaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a null literal", expression instanceof NullLiteral);
        NullLiteral nullLiteral = (NullLiteral) expression;
        ITypeBinding typeBinding = nullLiteral.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("no type binding", typeBinding);
        //$NON-NLS-1$
        assertTrue("Not the null type", typeBinding.isNullType());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong qualified name", typeBinding.getQualifiedName(), "null");
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=14198
	 */
    public void test0323() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0323", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 1);
        //$NON-NLS-1$
        assertTrue("Not an ExpressionStatement", node instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        Expression expression2 = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not an Assignement", expression2 instanceof Assignment);
        Assignment assignment = (Assignment) expression2;
        Expression expression = assignment.getRightHandSide();
        //$NON-NLS-1$
        assertTrue("Not a CastExpression", expression instanceof CastExpression);
        CastExpression castExpression = (CastExpression) expression;
        ITypeBinding typeBinding = castExpression.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Object", typeBinding.getName());
        Type type = castExpression.getType();
        ITypeBinding typeBinding2 = type.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding2", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Object", typeBinding2.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=14198
	 */
    public void test0324() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0324", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 1);
        //$NON-NLS-1$
        assertTrue("Not an ExpressionStatement", node instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        Expression expression2 = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not an Assignement", expression2 instanceof Assignment);
        Assignment assignment = (Assignment) expression2;
        Expression expression = assignment.getRightHandSide();
        //$NON-NLS-1$
        assertTrue("Not a CastExpression", expression instanceof CastExpression);
        CastExpression castExpression = (CastExpression) expression;
        ITypeBinding typeBinding = castExpression.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Object[]", typeBinding.getName());
        Type type = castExpression.getType();
        ITypeBinding typeBinding2 = type.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding2", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Object[]", typeBinding2.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=14198
	 */
    public void test0325() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0325", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 1);
        //$NON-NLS-1$
        assertTrue("Not an ExpressionStatement", node instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        Expression expression2 = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not an Assignement", expression2 instanceof Assignment);
        Assignment assignment = (Assignment) expression2;
        Expression expression = assignment.getRightHandSide();
        //$NON-NLS-1$
        assertTrue("Not a CastExpression", expression instanceof CastExpression);
        CastExpression castExpression = (CastExpression) expression;
        ITypeBinding typeBinding = castExpression.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "int[]", typeBinding.getName());
        Type type = castExpression.getType();
        ITypeBinding typeBinding2 = type.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding2", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "int[]", typeBinding2.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=14217
	 */
    public void test0326() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0326", "A.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        char[] source = sourceUnit.getSource().toCharArray();
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 1, 0);
        //$NON-NLS-1$
        assertTrue("Not an ExpressionStatement", node instanceof ExpressionStatement);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        //$NON-NLS-1$
        checkSourceRange(expressionStatement.getExpression(), "a().f= a()", source);
        //$NON-NLS-1$
        checkSourceRange(expressionStatement, "a().f= a();", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=14198
	 */
    public void test0327() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0327", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$<
        assertEquals("Wrong number of errors", 2, compilationUnit.getProblems().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not an VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a CastExpression", expression instanceof CastExpression);
        CastExpression castExpression = (CastExpression) expression;
        ITypeBinding typeBinding = castExpression.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No typebinding", typeBinding);
        assertEquals("Wrong name", "String", typeBinding.getName());
        Type type = castExpression.getType();
        ITypeBinding typeBinding2 = type.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding2", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "String", typeBinding2.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=13807
	 */
    public void test0328() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0328", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        Type type = variableDeclarationStatement.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "java.lang.Object[]", source);
        //$NON-NLS-1$
        assertTrue("Not an array type", type.isArrayType());
        ArrayType arrayType = (ArrayType) type;
        ITypeBinding typeBinding = arrayType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object[]", typeBinding.getName());
        Type elementType = arrayType.getElementType();
        //$NON-NLS-1$
        assertTrue("Not a simple type", elementType.isSimpleType());
        ITypeBinding typeBinding2 = elementType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding2", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object", typeBinding2.getName());
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a array creation", expression instanceof ArrayCreation);
        ITypeBinding typeBinding3 = expression.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No typeBinding3", typeBinding3);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object[]", typeBinding3.getName());
        ArrayCreation arrayCreation = (ArrayCreation) expression;
        ArrayInitializer arrayInitializer = arrayCreation.getInitializer();
        //$NON-NLS-1$
        assertNotNull("not array initializer", arrayInitializer);
        ITypeBinding typeBinding4 = arrayInitializer.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No typeBinding4", typeBinding3);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object[]", typeBinding4.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=13807
	 */
    public void test0329() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0329", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        Type type = variableDeclarationStatement.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "java.lang.Object[]", source);
        //$NON-NLS-1$
        assertTrue("Not an array type", type.isArrayType());
        ArrayType arrayType = (ArrayType) type;
        ITypeBinding typeBinding = arrayType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object[]", typeBinding.getName());
        Type elementType = arrayType.getElementType();
        //$NON-NLS-1$
        assertTrue("Not a simple type", elementType.isSimpleType());
        ITypeBinding typeBinding2 = elementType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding2", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object", typeBinding2.getName());
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a array creation", expression instanceof ArrayCreation);
        ITypeBinding typeBinding3 = expression.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No typeBinding3", typeBinding3);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object[]", typeBinding3.getName());
        ArrayCreation arrayCreation = (ArrayCreation) expression;
        ArrayInitializer arrayInitializer = arrayCreation.getInitializer();
        //$NON-NLS-1$
        assertNotNull("not array initializer", arrayInitializer);
        ITypeBinding typeBinding4 = arrayInitializer.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No typeBinding4", typeBinding3);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object[]", typeBinding4.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=14313
	 */
    public void test0330() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0330", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("wrong size", 2, compilationUnit.getMessages().length);
        //$NON-NLS-1$
        assertEquals("wrong size", 2, compilationUnit.getProblems().length);
        ASTNode node = getASTNode(compilationUnit, 0);
        //$NON-NLS-1$
        assertTrue("Not a type declaration", node.getNodeType() == ASTNode.TYPE_DECLARATION);
        TypeDeclaration typeDeclaration = (TypeDeclaration) node;
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no type binding", typeBinding);
        IMethodBinding[] methods = typeBinding.getDeclaredMethods();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, methods.length);
        //$NON-NLS-1$
        assertTrue("not a constructor", methods[0].isConstructor());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("wrong name", !methods[0].getName().equals("foo"));
        node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a methodDeclaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        IMethodBinding methodBinding = methodDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNull("method binding not null", methodBinding);
        node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a return statement", node.getNodeType() == ASTNode.RETURN_STATEMENT);
        ReturnStatement returnStatement = (ReturnStatement) node;
        Expression expression = returnStatement.getExpression();
        ITypeBinding typeBinding2 = expression.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("no type binding2", typeBinding2);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=14322
	 */
    public void test0331() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0331", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not an VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a QualifiedName", expression instanceof QualifiedName);
        QualifiedName qualifiedName = (QualifiedName) expression;
        IBinding binding = qualifiedName.getName().resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no binding", binding);
        //$NON-NLS-1$
        assertEquals("Wrong type", IBinding.VARIABLE, binding.getKind());
        IVariableBinding variableBinding = (IVariableBinding) binding;
        //$NON-NLS-1$
        assertTrue("Not a field", variableBinding.isField());
        //$NON-NLS-1$
        assertNull("Got a declaring class", variableBinding.getDeclaringClass());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "length", variableBinding.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=14403
	 */
    public void test0332() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0332", "LocalSelectionTransfer.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=13807
	 */
    public void test0333() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0333", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not an array creation", expression instanceof ArrayCreation);
        ArrayCreation arrayCreation = (ArrayCreation) expression;
        ITypeBinding typeBinding = arrayCreation.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("no type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object[][]", typeBinding.getName());
        ArrayType arrayType = arrayCreation.getType();
        ITypeBinding typeBinding2 = arrayType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no type binding2", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object[][]", typeBinding2.getName());
        Type type = arrayType.getElementType();
        //$NON-NLS-1$
        assertTrue("Not a simple type", type instanceof SimpleType);
        SimpleType simpleType = (SimpleType) type;
        ITypeBinding typeBinding3 = simpleType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no type binding3", typeBinding3);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object", typeBinding3.getName());
        type = componentType(arrayType);
        //$NON-NLS-1$
        assertTrue("Not an array type", type instanceof ArrayType);
        ArrayType arrayType2 = (ArrayType) type;
        ITypeBinding typeBinding4 = arrayType2.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no type binding4", typeBinding4);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object[]", typeBinding4.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=13807
	 */
    public void test0334() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0334", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertTrue("Not a VariableDeclarationStatement", node instanceof VariableDeclarationStatement);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not an array creation", expression instanceof ArrayCreation);
        ArrayCreation arrayCreation = (ArrayCreation) expression;
        ITypeBinding typeBinding = arrayCreation.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("no type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object[][][]", typeBinding.getName());
        ArrayType arrayType = arrayCreation.getType();
        //$NON-NLS-1$
        checkSourceRange(arrayType, "Object[10][][]", source);
        ITypeBinding typeBinding2 = arrayType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no type binding2", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object[][][]", typeBinding2.getName());
        Type type = arrayType.getElementType();
        //$NON-NLS-1$
        assertTrue("Not a simple type", type instanceof SimpleType);
        SimpleType simpleType = (SimpleType) type;
        //$NON-NLS-1$
        checkSourceRange(simpleType, "Object", source);
        ITypeBinding typeBinding3 = simpleType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no type binding3", typeBinding3);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object", typeBinding3.getName());
        type = componentType(arrayType);
        //$NON-NLS-1$
        assertTrue("Not an array type", type instanceof ArrayType);
        ArrayType arrayType2 = (ArrayType) type;
        //$NON-NLS-1$
        checkSourceRange(arrayType2, "Object[10][]", source);
        ITypeBinding typeBinding4 = arrayType2.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no type binding4", typeBinding4);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object[][]", typeBinding4.getName());
        type = componentType(arrayType2);
        //$NON-NLS-1$
        assertTrue("Not an array type", type instanceof ArrayType);
        ArrayType arrayType3 = (ArrayType) type;
        ITypeBinding typeBinding5 = arrayType3.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no type binding5", typeBinding5);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Object[]", typeBinding5.getName());
        //$NON-NLS-1$
        checkSourceRange(arrayType3, "Object[10]", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=14526
	 */
    public void test0335() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0335", "ExceptionTestCaseTest.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0);
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getProblems().length);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("not a type declaration", node instanceof TypeDeclaration);
        TypeDeclaration typeDeclaration = (TypeDeclaration) node;
        Type superclassType = typeDeclaration.getSuperclassType();
        //$NON-NLS-1$
        assertNotNull("no super class", superclassType);
        assertEquals("Wrong type", superclassType.getNodeType(), ASTNode.SIMPLE_TYPE);
        SimpleType simpleType = (SimpleType) superclassType;
        Name name = simpleType.getName();
        //$NON-NLS-1$
        assertTrue("not a qualified name", name.isQualifiedName());
        QualifiedName qualifiedName = (QualifiedName) name;
        name = qualifiedName.getQualifier();
        //$NON-NLS-1$
        assertTrue("not a qualified name", name.isQualifiedName());
        qualifiedName = (QualifiedName) name;
        name = qualifiedName.getQualifier();
        //$NON-NLS-1$
        assertTrue("not a simple name", name.isSimpleName());
        SimpleName simpleName = (SimpleName) name;
        IBinding binding = simpleName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no binding", binding);
        //$NON-NLS-1$
        assertEquals("wrong type", IBinding.PACKAGE, binding.getKind());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "junit", binding.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=14526
	 */
    public void test0336() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0336", "SorterTest.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getProblems().length);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("not a type declaration", node instanceof TypeDeclaration);
        TypeDeclaration typeDeclaration = (TypeDeclaration) node;
        List superInterfaces = typeDeclaration.superInterfaceTypes();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, superInterfaces.size());
        Type type = (Type) superInterfaces.get(0);
        assertEquals("wrong type", type.getNodeType(), ASTNode.SIMPLE_TYPE);
        SimpleType simpleType = (SimpleType) type;
        Name name = simpleType.getName();
        //$NON-NLS-1$
        assertTrue("not a qualified name", name.isQualifiedName());
        QualifiedName qualifiedName = (QualifiedName) name;
        name = qualifiedName.getQualifier();
        //$NON-NLS-1$
        assertTrue("not a simple name", name.isSimpleName());
        SimpleName simpleName = (SimpleName) name;
        IBinding binding = simpleName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no binding", binding);
        //$NON-NLS-1$
        assertEquals("wrong type", IBinding.TYPE, binding.getKind());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "Sorter", binding.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=14602
	 */
    public void test0337() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0337", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("not a field declaration", node instanceof FieldDeclaration);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        //$NON-NLS-1$
        checkSourceRange(variableDeclarationFragment, "message= Test.m(\"s\", new String[]{\"g\"})", source);
        //$NON-NLS-1$
        checkSourceRange(fieldDeclaration, "String message= Test.m(\"s\", new String[]{\"g\"});", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=14852
	 */
    public void test0338() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0338", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("not a MethodDeclaration", node instanceof MethodDeclaration);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        List thrownExceptions = internalThrownExceptions(methodDeclaration);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, thrownExceptions.size());
        Name name = (Name) thrownExceptions.get(0);
        IBinding binding = name.resolveBinding();
        //$NON-NLS-1$
        assertEquals("wrong type", IBinding.TYPE, binding.getKind());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "IOException", binding.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=15061
	 */
    public void test0339() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0339", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("No errors found", 3, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("not a Type declaration", node instanceof TypeDeclaration);
        TypeDeclaration typeDeclaration = (TypeDeclaration) node;
        List bodyDeclarations = typeDeclaration.bodyDeclarations();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, bodyDeclarations.size());
        MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclarations.get(0);
        //$NON-NLS-1$
        checkSourceRange(methodDeclaration, "int doQuery(boolean x);", source);
        node = getASTNode(compilationUnit, 0, 1);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("not a MethodDeclaration", node instanceof MethodDeclaration);
        String expectedSource = //$NON-NLS-1$
        "public void setX(boolean x) {\n" + " 		{\n" + "		z\n" + //$NON-NLS-1$
        "	}\n" + //$NON-NLS-1$
        "}";
        checkSourceRange(node, expectedSource, source, /*expectMalformed*/
        true);
        int methodEndPosition = node.getStartPosition() + node.getLength();
        node = getASTNode(compilationUnit, 0);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("not a TypeDeclaration", node instanceof TypeDeclaration);
        int typeEndPosition = node.getStartPosition() + node.getLength();
        //$NON-NLS-1$
        assertEquals("different positions", methodEndPosition, typeEndPosition);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=14852
	 */
    public void test0340() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "p3", "B.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("Not an expression statement", node.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
        ExpressionStatement expressionStatement = (ExpressionStatement) node;
        Expression expression = expressionStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not an method invocation", expression.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation = (MethodInvocation) expression;
        Expression expression2 = methodInvocation.getExpression();
        //$NON-NLS-1$
        assertNotNull("No receiver", expression2);
        ITypeBinding binding = expression2.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", binding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "A", binding.getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "p2", binding.getPackage().getName());
        //$NON-NLS-1$
        assertTrue("Not a qualified name", expression2.getNodeType() == ASTNode.QUALIFIED_NAME);
        QualifiedName qualifiedName = (QualifiedName) expression2;
        SimpleName simpleName = qualifiedName.getName();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "A", simpleName.getIdentifier());
        ITypeBinding typeBinding = simpleName.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "A", typeBinding.getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "p2", typeBinding.getPackage().getName());
        Name name = qualifiedName.getQualifier();
        //$NON-NLS-1$
        assertTrue("Not a simple name", name.getNodeType() == ASTNode.SIMPLE_NAME);
        SimpleName simpleName2 = (SimpleName) name;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "p2", simpleName2.getIdentifier());
        IBinding binding2 = simpleName2.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding2);
        //$NON-NLS-1$
        assertEquals("wrong type", IBinding.PACKAGE, binding2.getKind());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "p2", binding2.getName());
        node = getASTNode(compilationUnit, 0, 1, 0);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("Not an expression statement", node.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
        ExpressionStatement expressionStatement2 = (ExpressionStatement) node;
        Expression expression3 = expressionStatement2.getExpression();
        //$NON-NLS-1$
        assertTrue("Not an method invocation", expression3.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation2 = (MethodInvocation) expression3;
        Expression expression4 = methodInvocation2.getExpression();
        //$NON-NLS-1$
        assertNotNull("No receiver", expression4);
        ITypeBinding binding3 = expression4.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", binding3);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "A", binding3.getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong name", "p1", binding3.getPackage().getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=15804
	 */
    public void test0341() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0341", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 1, 0);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("Not an if statement", node.getNodeType() == ASTNode.IF_STATEMENT);
        String expectedSource = //$NON-NLS-1$
        "if (field != null) {\n" + "			throw new IOException();\n" + "		} else if (field == null) {\n" + "			throw new MalformedURLException();\n" + "		} else if (field == null) {\n" + "			throw new InterruptedIOException();\n" + "		} else {\n" + "			throw new UnsupportedEncodingException();\n" + //$NON-NLS-1$
        "		}";
        checkSourceRange(node, expectedSource, source);
        IfStatement ifStatement = (IfStatement) node;
        Statement thenStatement = ifStatement.getThenStatement();
        expectedSource = //$NON-NLS-1$
        "{\n" + "			throw new IOException();\n" + //$NON-NLS-1$
        "		}";
        checkSourceRange(thenStatement, expectedSource, source);
        Statement elseStatement = ifStatement.getElseStatement();
        expectedSource = //$NON-NLS-1$
        "if (field == null) {\n" + "			throw new MalformedURLException();\n" + "		} else if (field == null) {\n" + "			throw new InterruptedIOException();\n" + "		} else {\n" + "			throw new UnsupportedEncodingException();\n" + //$NON-NLS-1$
        "		}";
        checkSourceRange(elseStatement, expectedSource, source);
        //$NON-NLS-1$
        assertTrue("Not a if statement", elseStatement.getNodeType() == ASTNode.IF_STATEMENT);
        ifStatement = (IfStatement) elseStatement;
        thenStatement = ifStatement.getThenStatement();
        expectedSource = //$NON-NLS-1$
        "{\n" + "			throw new MalformedURLException();\n" + //$NON-NLS-1$
        "		}";
        checkSourceRange(thenStatement, expectedSource, source);
        elseStatement = ifStatement.getElseStatement();
        expectedSource = //$NON-NLS-1$
        "if (field == null) {\n" + "			throw new InterruptedIOException();\n" + "		} else {\n" + "			throw new UnsupportedEncodingException();\n" + //$NON-NLS-1$
        "		}";
        checkSourceRange(elseStatement, expectedSource, source);
        //$NON-NLS-1$
        assertTrue("Not a if statement", elseStatement.getNodeType() == ASTNode.IF_STATEMENT);
        ifStatement = (IfStatement) elseStatement;
        thenStatement = ifStatement.getThenStatement();
        expectedSource = //$NON-NLS-1$
        "{\n" + "			throw new InterruptedIOException();\n" + //$NON-NLS-1$
        "		}";
        checkSourceRange(thenStatement, expectedSource, source);
        elseStatement = ifStatement.getElseStatement();
        expectedSource = //$NON-NLS-1$
        "{\n" + "			throw new UnsupportedEncodingException();\n" + //$NON-NLS-1$
        "		}";
        checkSourceRange(elseStatement, expectedSource, source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=15657
	 * @deprecated marked deprecated to suppress JDOM-related deprecation warnings
	 */
    public void test0342() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0342", "Test.java");
        IDOMCompilationUnit dcompUnit = new DOMFactory().createCompilationUnit(sourceUnit.getSource(), sourceUnit.getElementName());
        //$NON-NLS-1$
        assertNotNull("dcompUnit is null", dcompUnit);
        // searching class
        IDOMType classNode = null;
        Enumeration children = dcompUnit.getChildren();
        //$NON-NLS-1$
        assertNotNull("dcompUnit has no children", children);
        while (children.hasMoreElements()) {
            IDOMNode child = (IDOMNode) children.nextElement();
            if (child.getNodeType() == IDOMNode.TYPE) {
                classNode = (IDOMType) child;
                break;
            }
        }
        //$NON-NLS-1$
        assertNotNull("classNode is null", classNode);
        // searching for methods
        children = classNode.getChildren();
        //$NON-NLS-1$
        assertNotNull("classNode has no children", children);
        while (children.hasMoreElements()) {
            IDOMNode child = (IDOMNode) children.nextElement();
            if (child.getNodeType() == IDOMNode.METHOD) {
                IDOMMethod childMethod = (IDOMMethod) child;
                // returnType is always null;
                String returnType = childMethod.getReturnType();
                if (childMethod.isConstructor()) {
                    assertNull(returnType);
                } else {
                    assertNotNull(//$NON-NLS-1$
                    "no return type", //$NON-NLS-1$
                    returnType);
                }
            }
        }
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=16051
	 */
    public void test0343() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0343", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 1, 1);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("Not an if statement", node.getNodeType() == ASTNode.IF_STATEMENT);
        String expectedSource = //$NON-NLS-1$
        "if (flag)\n" + "			i= 10;";
        checkSourceRange(node, expectedSource, source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=16132
	 */
    public void test0344() throws JavaModelException {
        IJavaProject project = null;
        String pb_assert = null;
        String compiler_source = null;
        String compiler_compliance = null;
        try {
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0344", "Test.java");
            project = sourceUnit.getJavaProject();
            pb_assert = project.getOption(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, true);
            compiler_source = project.getOption(JavaCore.COMPILER_SOURCE, true);
            compiler_compliance = project.getOption(JavaCore.COMPILER_COMPLIANCE, true);
            project.setOption(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.ERROR);
            project.setOption(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_4);
            project.setOption(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_4);
            ASTNode result = runConversion(getJLS4(), sourceUnit, true);
            //$NON-NLS-1$
            assertNotNull("No compilation unit", result);
            //$NON-NLS-1$
            assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
            CompilationUnit compilationUnit = (CompilationUnit) result;
            //$NON-NLS-1$
            assertEquals("errors found", 0, compilationUnit.getMessages().length);
        } finally {
            if (project != null) {
                project.setOption(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, pb_assert);
                project.setOption(JavaCore.COMPILER_SOURCE, compiler_source);
                project.setOption(JavaCore.COMPILER_COMPLIANCE, compiler_compliance);
            }
        }
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=17922
	 */
    public void test0345() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0345", "A.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("Not an field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        List fragments = fieldDeclaration.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not an ArrayCreation", expression.getNodeType() == ASTNode.ARRAY_CREATION);
        ArrayCreation arrayCreation = (ArrayCreation) expression;
        ArrayType arrayType = arrayCreation.getType();
        IBinding binding2 = arrayType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no binding2", binding2);
        //$NON-NLS-1$
        assertEquals("not a type", binding2.getKind(), IBinding.TYPE);
        ITypeBinding typeBinding2 = (ITypeBinding) binding2;
        //$NON-NLS-1$
        assertTrue("Not an array type binding2", typeBinding2.isArray());
        Type type = arrayType.getElementType();
        //$NON-NLS-1$
        assertTrue("Not a simple type", type.isSimpleType());
        SimpleType simpleType = (SimpleType) type;
        Name name = simpleType.getName();
        //$NON-NLS-1$
        assertTrue("QualifiedName", name.getNodeType() == ASTNode.QUALIFIED_NAME);
        SimpleName simpleName = ((QualifiedName) name).getName();
        IBinding binding = simpleName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no binding", binding);
        //$NON-NLS-1$
        assertEquals("not a type", binding.getKind(), IBinding.TYPE);
        ITypeBinding typeBinding = (ITypeBinding) binding;
        //$NON-NLS-1$
        assertTrue("An array type binding", !typeBinding.isArray());
        Type type2 = fieldDeclaration.getType();
        //$NON-NLS-1$
        assertTrue("Not a array type", type2.isArrayType());
        ArrayType arrayType2 = (ArrayType) type2;
        Type type3 = arrayType2.getElementType();
        //$NON-NLS-1$
        assertTrue("Not a simple type", type3.isSimpleType());
        SimpleType simpleType2 = (SimpleType) type3;
        Name name2 = simpleType2.getName();
        //$NON-NLS-1$
        assertTrue("Not a qualified name", name2.getNodeType() == ASTNode.QUALIFIED_NAME);
        SimpleName simpleName2 = ((QualifiedName) name2).getName();
        IBinding binding3 = simpleName2.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no binding", binding3);
        //$NON-NLS-1$
        assertEquals("not a type", binding3.getKind(), IBinding.TYPE);
        ITypeBinding typeBinding3 = (ITypeBinding) binding3;
        //$NON-NLS-1$
        assertTrue("An array type binding", !typeBinding3.isArray());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=18138
	 */
    public void test0346() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0346", "Test2.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("Not an variable declaration", node.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        Type type = variableDeclarationStatement.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "Vector", source);
        //$NON-NLS-1$
        assertTrue("not an array type", !type.isArrayType());
        //$NON-NLS-1$
        assertTrue("Not a simple type", type.isSimpleType());
        SimpleType simpleType = (SimpleType) type;
        Name name = simpleType.getName();
        //$NON-NLS-1$
        assertTrue("Not a simpleName", name.isSimpleName());
        SimpleName simpleName = (SimpleName) name;
        IBinding binding = simpleName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertEquals("Wrong type", IBinding.TYPE, binding.getKind());
        ITypeBinding typeBinding = (ITypeBinding) binding;
        //$NON-NLS-1$
        assertTrue("An array", !typeBinding.isArray());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Vector", binding.getName());
        ITypeBinding typeBinding2 = simpleType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", typeBinding2);
        //$NON-NLS-1$
        assertEquals("Wrong type", IBinding.TYPE, typeBinding2.getKind());
        //$NON-NLS-1$
        assertTrue("An array", !typeBinding2.isArray());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Vector", typeBinding2.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=18138
	 */
    public void test0347() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0347", "Test2.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("Not an variable declaration", node.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        Type type = variableDeclarationStatement.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "Vector[]", source);
        //$NON-NLS-1$
        assertTrue("not an array type", type.isArrayType());
        ArrayType arrayType = (ArrayType) type;
        ITypeBinding binding = arrayType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertEquals("Wrong type", IBinding.TYPE, binding.getKind());
        //$NON-NLS-1$
        assertTrue("Not an array type", binding.isArray());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Vector[]", binding.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=18138
	 */
    public void test0348() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0348", "Test2.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("Not an variable declaration", node.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        Type type = variableDeclarationStatement.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "Vector[][]", source);
        //$NON-NLS-1$
        assertTrue("not an array type", type.isArrayType());
        ArrayType arrayType = (ArrayType) type;
        ITypeBinding binding = arrayType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertEquals("Wrong type", IBinding.TYPE, binding.getKind());
        //$NON-NLS-1$
        assertTrue("Not an array type", binding.isArray());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Vector[][]", binding.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=18138
	 */
    public void test0349() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0349", "Test2.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("Not an field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        Type type = fieldDeclaration.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "Vector[][]", source);
        //$NON-NLS-1$
        assertTrue("not an array type", type.isArrayType());
        ArrayType arrayType = (ArrayType) type;
        ITypeBinding binding = arrayType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertEquals("Wrong type", IBinding.TYPE, binding.getKind());
        //$NON-NLS-1$
        assertTrue("Not an array type", binding.isArray());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Vector[][]", binding.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=18138
	 */
    public void test0350() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0350", "Test2.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("Not an field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION);
        FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        Type type = fieldDeclaration.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "Vector", source);
        //$NON-NLS-1$
        assertTrue("not a simple type", type.isSimpleType());
        SimpleType simpleType = (SimpleType) type;
        ITypeBinding binding = simpleType.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertEquals("Wrong type", IBinding.TYPE, binding.getKind());
        //$NON-NLS-1$
        assertTrue("An array type", binding.isClass());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "Vector", binding.getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=18169
	 */
    public void test0351() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0351", "Test2.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("Not an method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        List parameters = methodDeclaration.parameters();
        //$NON-NLS-1$
        assertEquals("wrong size", 2, parameters.size());
        SingleVariableDeclaration singleVariableDeclaration = (SingleVariableDeclaration) parameters.get(0);
        //$NON-NLS-1$
        checkSourceRange(singleVariableDeclaration, "int a", source);
        singleVariableDeclaration = (SingleVariableDeclaration) parameters.get(1);
        //$NON-NLS-1$
        checkSourceRange(singleVariableDeclaration, "int[] b", source);
        node = getASTNode(compilationUnit, 0, 1);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("Not an method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        methodDeclaration = (MethodDeclaration) node;
        parameters = methodDeclaration.parameters();
        //$NON-NLS-1$
        assertEquals("wrong size", 2, parameters.size());
        singleVariableDeclaration = (SingleVariableDeclaration) parameters.get(0);
        //$NON-NLS-1$
        checkSourceRange(singleVariableDeclaration, "int a", source);
        singleVariableDeclaration = (SingleVariableDeclaration) parameters.get(1);
        //$NON-NLS-1$
        checkSourceRange(singleVariableDeclaration, "int b[]", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=18169
	 */
    public void test0352() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0352", "Test2.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("Not an method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        List parameters = methodDeclaration.parameters();
        //$NON-NLS-1$
        assertEquals("wrong size", 2, parameters.size());
        SingleVariableDeclaration singleVariableDeclaration = (SingleVariableDeclaration) parameters.get(0);
        //$NON-NLS-1$
        checkSourceRange(singleVariableDeclaration, "final int a", source);
        singleVariableDeclaration = (SingleVariableDeclaration) parameters.get(1);
        //$NON-NLS-1$
        checkSourceRange(singleVariableDeclaration, "final int[] b", source);
        node = getASTNode(compilationUnit, 0, 1);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("Not an method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        methodDeclaration = (MethodDeclaration) node;
        parameters = methodDeclaration.parameters();
        //$NON-NLS-1$
        assertEquals("wrong size", 2, parameters.size());
        singleVariableDeclaration = (SingleVariableDeclaration) parameters.get(0);
        //$NON-NLS-1$
        checkSourceRange(singleVariableDeclaration, "final int a", source);
        singleVariableDeclaration = (SingleVariableDeclaration) parameters.get(1);
        //$NON-NLS-1$
        checkSourceRange(singleVariableDeclaration, "final int b[]", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=18042
	 */
    public void test0353() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0353", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("not null", node);
        //$NON-NLS-1$
        assertTrue("Not an variable declaration", node.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        Type type = variableDeclarationStatement.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "InputStream", source);
        //$NON-NLS-1$
        assertTrue("not a simple type", type.isSimpleType());
        ITypeBinding binding = type.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
        //$NON-NLS-1$
        assertEquals("Wrong type", IBinding.TYPE, binding.getKind());
        //$NON-NLS-1$
        assertTrue("Not a class", binding.isClass());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "InputStream", binding.getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong package", "java.io", binding.getPackage().getName());
        SimpleType simpleType = (SimpleType) type;
        Name name = simpleType.getName();
        IBinding binding2 = name.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding2);
        //$NON-NLS-1$
        assertEquals("Wrong type", IBinding.TYPE, binding2.getKind());
        ITypeBinding typeBinding = (ITypeBinding) binding2;
        //$NON-NLS-1$
        assertTrue("Not a class", typeBinding.isClass());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong name", "InputStream", typeBinding.getName());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong package", "java.io", typeBinding.getPackage().getName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=19851
	 */
    public void test0354() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0354", "Test.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 2, compilationUnit.getMessages().length);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=20520
	 */
    public void test0355() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0355", "Foo.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not an if statement", node.getNodeType() == ASTNode.IF_STATEMENT);
        IfStatement ifStatement = (IfStatement) node;
        Expression condition = ifStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not an infixExpression", condition.getNodeType() == ASTNode.INFIX_EXPRESSION);
        InfixExpression infixExpression = (InfixExpression) condition;
        Expression expression = infixExpression.getLeftOperand();
        //$NON-NLS-1$
        assertTrue("Not a method invocation expression", expression.getNodeType() == ASTNode.METHOD_INVOCATION);
        MethodInvocation methodInvocation = (MethodInvocation) expression;
        Expression expression2 = methodInvocation.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a parenthesis expression", expression2.getNodeType() == ASTNode.PARENTHESIZED_EXPRESSION);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=20865
	 */
    public void test0356() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0356", "X.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 1, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a variable declaration statement", node.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        Type type = variableDeclarationStatement.getType();
        ITypeBinding binding = type.resolveBinding();
        assertNotNull("Binding should NOT be null for type: " + type, binding);
        // Verify that class instance creation has a null binding
        List fragments = variableDeclarationStatement.fragments();
        assertEquals("Expect only one fragment for VariableDeclarationStatement: " + variableDeclarationStatement, 1, fragments.size());
        node = (ASTNode) fragments.get(0);
        //$NON-NLS-1$
        assertEquals("Not a variable declaration fragment", ASTNode.VARIABLE_DECLARATION_FRAGMENT, node.getNodeType());
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) node;
        Expression initializer = fragment.getInitializer();
        //$NON-NLS-1$
        assertEquals("Expect a class instance creation for initializer: " + initializer, ASTNode.CLASS_INSTANCE_CREATION, initializer.getNodeType());
        ClassInstanceCreation instanceCreation = (ClassInstanceCreation) initializer;
        type = instanceCreation.getType();
        binding = type.resolveBinding();
        assertNull("Binding should BE null for type: " + type, binding);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=21757
	 */
    public void test0357() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0357", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a type declaration statement", node.getNodeType() == ASTNode.TYPE_DECLARATION);
        TypeDeclaration typeDeclaration = (TypeDeclaration) node;
        SimpleName name = typeDeclaration.getName();
        //$NON-NLS-1$
        checkSourceRange(name, "A", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=21768
	 */
    public void test0358() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0358", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration statement", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        SimpleName name = methodDeclaration.getName();
        //$NON-NLS-1$
        checkSourceRange(name, "mdd", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=21768
	 */
    public void test0359() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0359", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration statement", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        SimpleName name = methodDeclaration.getName();
        //$NON-NLS-1$
        checkSourceRange(name, "mdd", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=21916
	 */
    public void test0360() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0360", "X.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a for statement", node.getNodeType() == ASTNode.FOR_STATEMENT);
        ForStatement forStatement = (ForStatement) node;
        List initializers = forStatement.initializers();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, initializers.size());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=21916
	 */
    public void test0361() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0361", "X.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a for statement", node.getNodeType() == ASTNode.FOR_STATEMENT);
        ForStatement forStatement = (ForStatement) node;
        List initializers = forStatement.initializers();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, initializers.size());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=21916
	 */
    public void test0362() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0362", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, false);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        ForStatement forStatement = this.ast.newForStatement();
        VariableDeclarationFragment iFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        iFragment.setName(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        iFragment.setInitializer(this.ast.newNumberLiteral("0"));
        VariableDeclarationFragment jFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        jFragment.setName(this.ast.newSimpleName("j"));
        //$NON-NLS-1$
        jFragment.setInitializer(this.ast.newNumberLiteral("0"));
        VariableDeclarationFragment kFragment = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        kFragment.setName(this.ast.newSimpleName("k"));
        //$NON-NLS-1$
        kFragment.setInitializer(this.ast.newNumberLiteral("0"));
        VariableDeclarationExpression variableDeclarationExpression = this.ast.newVariableDeclarationExpression(iFragment);
        variableDeclarationExpression.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        variableDeclarationExpression.fragments().add(jFragment);
        variableDeclarationExpression.fragments().add(kFragment);
        forStatement.initializers().add(variableDeclarationExpression);
        PostfixExpression iPostfixExpression = this.ast.newPostfixExpression();
        //$NON-NLS-1$
        iPostfixExpression.setOperand(this.ast.newSimpleName("i"));
        iPostfixExpression.setOperator(PostfixExpression.Operator.INCREMENT);
        forStatement.updaters().add(iPostfixExpression);
        PostfixExpression jPostfixExpression = this.ast.newPostfixExpression();
        //$NON-NLS-1$
        jPostfixExpression.setOperand(this.ast.newSimpleName("j"));
        jPostfixExpression.setOperator(PostfixExpression.Operator.INCREMENT);
        forStatement.updaters().add(jPostfixExpression);
        PostfixExpression kPostfixExpression = this.ast.newPostfixExpression();
        //$NON-NLS-1$
        kPostfixExpression.setOperand(this.ast.newSimpleName("k"));
        kPostfixExpression.setOperator(PostfixExpression.Operator.INCREMENT);
        forStatement.updaters().add(kPostfixExpression);
        forStatement.setBody(this.ast.newBlock());
        InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("i"));
        infixExpression.setOperator(InfixExpression.Operator.LESS);
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newNumberLiteral("10"));
        forStatement.setExpression(infixExpression);
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", forStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        checkSourceRange(node, "for (int i=0, j=0, k=0; i<10 ; i++, j++, k++) {}", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=22939
	 */
    public void test0363() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0363", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 1);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a variable declaration statement", node.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a parenthesized expression", expression.getNodeType() == ASTNode.PARENTHESIZED_EXPRESSION);
        Expression expression2 = ((ParenthesizedExpression) expression).getExpression();
        //$NON-NLS-1$
        checkSourceRange(expression2, "xxxx", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11529
	 */
    public void test0364() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0364", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a variable declaration statement", node.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        //$NON-NLS-1$
        checkSourceRange(variableDeclarationStatement, "int local;", source);
        SimpleName simpleName = variableDeclarationFragment.getName();
        IBinding binding = simpleName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=11529
	 */
    public void test0365() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0365", "A.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a for statement", node.getNodeType() == ASTNode.FOR_STATEMENT);
        ForStatement forStatement = (ForStatement) node;
        List initializers = forStatement.initializers();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, initializers.size());
        VariableDeclarationExpression variableDeclarationExpression = (VariableDeclarationExpression) initializers.get(0);
        List fragments = variableDeclarationExpression.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        SimpleName simpleName = variableDeclarationFragment.getName();
        IBinding binding = simpleName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No binding", binding);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23048
	 */
    public void test0366() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0366", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a for statement", node.getNodeType() == ASTNode.FOR_STATEMENT);
        ForStatement forStatement = (ForStatement) node;
        //$NON-NLS-1$
        checkSourceRange(forStatement, "for (int i = 0; i < 5; ++i);", source);
        Statement statement = forStatement.getBody();
        //$NON-NLS-1$
        assertTrue("Not an empty statement", statement.getNodeType() == ASTNode.EMPTY_STATEMENT);
        //$NON-NLS-1$
        checkSourceRange(statement, ";", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23048
	 */
    public void test0367() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0367", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a while statement", node.getNodeType() == ASTNode.WHILE_STATEMENT);
        WhileStatement whileStatement = (WhileStatement) node;
        //$NON-NLS-1$
        checkSourceRange(whileStatement, "while(i == 2);", source);
        Statement statement = whileStatement.getBody();
        //$NON-NLS-1$
        assertTrue("Not an empty statement", statement.getNodeType() == ASTNode.EMPTY_STATEMENT);
        //$NON-NLS-1$
        checkSourceRange(statement, ";", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23048
	 */
    public void test0368() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0368", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertProblemsSize(compilationUnit, 1, "The label test is never explicitly referenced");
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a labeled statement", node.getNodeType() == ASTNode.LABELED_STATEMENT);
        LabeledStatement labeledStatement = (LabeledStatement) node;
        //$NON-NLS-1$
        checkSourceRange(labeledStatement, "test:;", source);
        Statement statement = labeledStatement.getBody();
        //$NON-NLS-1$
        assertTrue("Not an empty statement", statement.getNodeType() == ASTNode.EMPTY_STATEMENT);
        //$NON-NLS-1$
        checkSourceRange(statement, ";", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23048
	 */
    public void test0369() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0369", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertProblemsSize(compilationUnit, 1, "The label test is never explicitly referenced");
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a labeled statement", node.getNodeType() == ASTNode.LABELED_STATEMENT);
        LabeledStatement labeledStatement = (LabeledStatement) node;
        //$NON-NLS-1$
        checkSourceRange(labeledStatement, "test:\\u003B", source);
        Statement statement = labeledStatement.getBody();
        //$NON-NLS-1$
        assertTrue("Not an empty statement", statement.getNodeType() == ASTNode.EMPTY_STATEMENT);
        //$NON-NLS-1$
        checkSourceRange(statement, "\\u003B", source);
    }

    /**
	 * DoStatement ==> DoStatement
	 */
    public void test0370() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0370", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", node);
        DoStatement doStatement = this.ast.newDoStatement();
        doStatement.setBody(this.ast.newEmptyStatement());
        doStatement.setExpression(this.ast.newBooleanLiteral(true));
        //$NON-NLS-1$
        assertTrue("Both AST trees should be identical", doStatement.subtreeMatch(new ASTMatcher(), node));
        //$NON-NLS-1$
        String expectedSource = "do ; while(true);";
        checkSourceRange(node, expectedSource, source);
        DoStatement doStatement2 = (DoStatement) node;
        Statement statement = doStatement2.getBody();
        //$NON-NLS-1$
        assertTrue("Not an empty statement", statement.getNodeType() == ASTNode.EMPTY_STATEMENT);
        //$NON-NLS-1$
        checkSourceRange(statement, ";", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23048
	 */
    public void test0371() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0371", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a labeled statement", node.getNodeType() == ASTNode.IF_STATEMENT);
        IfStatement ifStatement = (IfStatement) node;
        //$NON-NLS-1$
        checkSourceRange(ifStatement, "if (i == 6);", source);
        Statement statement = ifStatement.getThenStatement();
        //$NON-NLS-1$
        assertTrue("Not an empty statement", statement.getNodeType() == ASTNode.EMPTY_STATEMENT);
        //$NON-NLS-1$
        checkSourceRange(statement, ";", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23048
	 */
    public void test0372() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0372", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a labeled statement", node.getNodeType() == ASTNode.IF_STATEMENT);
        IfStatement ifStatement = (IfStatement) node;
        //$NON-NLS-1$
        checkSourceRange(ifStatement, "if (i == 6) {} else ;", source);
        Statement statement = ifStatement.getElseStatement();
        //$NON-NLS-1$
        assertTrue("Not an empty statement", statement.getNodeType() == ASTNode.EMPTY_STATEMENT);
        //$NON-NLS-1$
        checkSourceRange(statement, ";", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23118
	 */
    public void test0373() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0373", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 1, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a for statement", node.getNodeType() == ASTNode.FOR_STATEMENT);
        ForStatement forStatement = (ForStatement) node;
        Statement statement = forStatement.getBody();
        //$NON-NLS-1$
        assertTrue("Not a block statement", statement.getNodeType() == ASTNode.BLOCK);
        Block block = (Block) statement;
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, statements.size());
        Statement statement2 = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not a break statement", statement2.getNodeType() == ASTNode.BREAK_STATEMENT);
        BreakStatement breakStatement = (BreakStatement) statement2;
        //$NON-NLS-1$
        checkSourceRange(breakStatement, "break;", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23118
	 */
    public void test0374() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0374", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a for statement", node.getNodeType() == ASTNode.FOR_STATEMENT);
        ForStatement forStatement = (ForStatement) node;
        Statement statement = forStatement.getBody();
        //$NON-NLS-1$
        assertTrue("Not a block statement", statement.getNodeType() == ASTNode.BLOCK);
        Block block = (Block) statement;
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, statements.size());
        Statement statement2 = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not a break statement", statement2.getNodeType() == ASTNode.CONTINUE_STATEMENT);
        ContinueStatement continueStatement = (ContinueStatement) statement2;
        //$NON-NLS-1$
        checkSourceRange(continueStatement, "continue;", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23052
	 */
    public void test0375() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0375", "A.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("problems found", 1, compilationUnit.getMessages().length);
        List imports = compilationUnit.imports();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, imports.size());
        ImportDeclaration importDeclaration = (ImportDeclaration) imports.get(0);
        IBinding binding = importDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no binding", binding);
        //$NON-NLS-1$
        assertEquals("Not a type binding", IBinding.TYPE, binding.getKind());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=22939
	 */
    public void test0376() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0376", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a variable declaration statement", node.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a cast expression", expression.getNodeType() == ASTNode.CAST_EXPRESSION);
        Type type = ((CastExpression) expression).getType();
        //$NON-NLS-1$
        checkSourceRange(type, "A", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23050
	 */
    public void test0377() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0377", "A.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a variable declaration statement", node.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        IVariableBinding variableBinding = variableDeclarationFragment.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No variable binding", variableBinding);
        //$NON-NLS-1$
        assertEquals("Wrong modifier", IModifierConstants.ACC_FINAL, variableBinding.getModifiers());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=22161
	 */
    public void test0378() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0378", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a type declaration", node.getNodeType() == ASTNode.TYPE_DECLARATION);
        TypeDeclaration typeDeclaration = (TypeDeclaration) node;
        SimpleName name = typeDeclaration.getName();
        //$NON-NLS-1$
        checkSourceRange(name, "B", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=22161
	 */
    public void test0379() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0379", "Test.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode expression = getASTNodeToCompare((CompilationUnit) result);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", expression);
        //$NON-NLS-1$
        assertTrue("Not a class instance creation", expression.getNodeType() == ASTNode.CLASS_INSTANCE_CREATION);
        ClassInstanceCreation classInstanceCreation2 = (ClassInstanceCreation) expression;
        Type type = classInstanceCreation2.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "Object", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=22054
	 */
    public void test0380() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0380", "A.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a return statement", node.getNodeType() == ASTNode.RETURN_STATEMENT);
        ReturnStatement returnStatement = (ReturnStatement) node;
        Expression expression = returnStatement.getExpression();
        //$NON-NLS-1$
        assertTrue("Not a super method invocation", expression.getNodeType() == ASTNode.SUPER_METHOD_INVOCATION);
        SuperMethodInvocation superMethodInvocation = (SuperMethodInvocation) expression;
        ITypeBinding typeBinding = superMethodInvocation.resolveTypeBinding();
        //$NON-NLS-1$
        assertNotNull("no type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong declaring class", typeBinding.getSuperclass().getName(), "Object");
        SimpleName simpleName = superMethodInvocation.getName();
        IBinding binding = simpleName.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("no binding", binding);
        //$NON-NLS-1$
        assertEquals("Wrong type", IBinding.METHOD, binding.getKind());
        IMethodBinding methodBinding = (IMethodBinding) binding;
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong declaring class", methodBinding.getDeclaringClass().getName(), "Object");
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23054
	 */
    public void test0381() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0381", "A.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a type declaration", node.getNodeType() == ASTNode.TYPE_DECLARATION);
        TypeDeclaration typeDeclaration = (TypeDeclaration) node;
        Javadoc javadoc = typeDeclaration.getJavadoc();
        //$NON-NLS-1$
        assertNull("Javadoc not null", javadoc);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=22154
	 */
    public void test0382() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0382", "A.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a type declaration", node.getNodeType() == ASTNode.TYPE_DECLARATION);
        TypeDeclaration typeDeclaration = (TypeDeclaration) node;
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong fully qualified name", typeBinding.getQualifiedName(), "test0382.A");
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=22154
	 */
    public void test0383() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0383", "A.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a type declaration", node.getNodeType() == ASTNode.TYPE_DECLARATION);
        TypeDeclaration typeDeclaration = (TypeDeclaration) node;
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong fully qualified name", typeBinding.getQualifiedName(), "test0383.A.B");
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=22154
	 */
    public void test0384() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0384", "A.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a type declaration", node.getNodeType() == ASTNode.TYPE_DECLARATION);
        TypeDeclaration typeDeclaration = (TypeDeclaration) node;
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong fully qualified name", typeBinding.getQualifiedName(), "test0384.A.B.D");
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23117
	 */
    public void test0385() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0385", "A.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 1, compilationUnit.getMessages().length);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23259
	 */
    public void test0386() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0386", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a switch statement", node.getNodeType() == ASTNode.SWITCH_STATEMENT);
        SwitchStatement switchStatement = (SwitchStatement) node;
        List statements = switchStatement.statements();
        //$NON-NLS-1$
        assertEquals("Wrong size", 5, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not a case statement", statement.getNodeType() == ASTNode.SWITCH_CASE);
        //$NON-NLS-1$
        checkSourceRange(statement, "case 1:", source);
        statement = (Statement) statements.get(3);
        //$NON-NLS-1$
        assertTrue("Not a default case statement", statement.getNodeType() == ASTNode.SWITCH_CASE);
        //$NON-NLS-1$
        checkSourceRange(statement, "default :", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=22939
	 */
    public void test0387() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0387", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a variable declaration statement", node.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not a cast expression", expression.getNodeType() == ASTNode.CAST_EXPRESSION);
        Type type = ((CastExpression) expression).getType();
        //$NON-NLS-1$
        checkSourceRange(type, "A", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=22154
	 */
    public void test0388() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0388", "A.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a type declaration", node.getNodeType() == ASTNode.TYPE_DECLARATION);
        TypeDeclaration typeDeclaration = (TypeDeclaration) node;
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong qualified name", "test0388.A", typeBinding.getQualifiedName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=22154
	 */
    public void test0389() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0389", "A.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a type declaration", node.getNodeType() == ASTNode.TYPE_DECLARATION);
        TypeDeclaration typeDeclaration = (TypeDeclaration) node;
        ITypeBinding typeBinding = typeDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong qualified name", "test0389.A.B", typeBinding.getQualifiedName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=22154
	 */
    public void test0390() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0390", "A.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        Type type = methodDeclaration.getReturnType2();
        ITypeBinding typeBinding = type.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong qualified name", "int", typeBinding.getQualifiedName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=22154
	 */
    public void test0391() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0391", "A.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        Type type = methodDeclaration.getReturnType2();
        ITypeBinding typeBinding = type.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong qualified name", "int[]", typeBinding.getQualifiedName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=22154
	 */
    public void test0392() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0392", "A.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        Type type = methodDeclaration.getReturnType2();
        ITypeBinding typeBinding = type.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong qualified name", "java.lang.String[]", typeBinding.getQualifiedName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23284
	 */
    public void test0393() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0393", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        Type type = methodDeclaration.getReturnType2();
        //$NON-NLS-1$
        checkSourceRange(type, "String", source);
        ITypeBinding typeBinding = type.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$
        assertEquals("Wrong dimension", 0, typeBinding.getDimensions());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong qualified name", "java.lang.String", typeBinding.getQualifiedName());
        //$NON-NLS-1$
        assertEquals("Wrong dimension", 1, methodDeclaration.getExtraDimensions());
        IMethodBinding methodBinding = methodDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No method binding", methodBinding);
        ITypeBinding typeBinding2 = methodBinding.getReturnType();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong qualified name", "java.lang.String[]", typeBinding2.getQualifiedName());
        //$NON-NLS-1$
        assertEquals("Wrong dimension", 1, typeBinding2.getDimensions());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23284
	 */
    public void test0394() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0394", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        Type type = methodDeclaration.getReturnType2();
        //$NON-NLS-1$
        checkSourceRange(type, "String", source);
        ITypeBinding typeBinding = type.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong qualified name", "java.lang.String", typeBinding.getQualifiedName());
        //$NON-NLS-1$
        assertEquals("Wrong dimension", 0, methodDeclaration.getExtraDimensions());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23284
	 */
    public void test0395() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0395", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        //$NON-NLS-1$
        assertNotNull("No compilation unit", result);
        //$NON-NLS-1$
        assertTrue("result is not a compilation unit", result instanceof CompilationUnit);
        CompilationUnit compilationUnit = (CompilationUnit) result;
        //$NON-NLS-1$
        assertEquals("errors found", 0, compilationUnit.getMessages().length);
        ASTNode node = getASTNode(compilationUnit, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        Type type = methodDeclaration.getReturnType2();
        //$NON-NLS-1$
        checkSourceRange(type, "String[]", source);
        ITypeBinding typeBinding = type.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$
        assertEquals("Wrong dimension", 1, typeBinding.getDimensions());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong qualified name", "java.lang.String[]", typeBinding.getQualifiedName());
        //$NON-NLS-1$
        assertEquals("Wrong dimension", 1, methodDeclaration.getExtraDimensions());
        IMethodBinding methodBinding = methodDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No method binding", methodBinding);
        ITypeBinding typeBinding2 = methodBinding.getReturnType();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong qualified name", "java.lang.String[][]", typeBinding2.getQualifiedName());
        //$NON-NLS-1$
        assertEquals("Wrong dimension", 2, typeBinding2.getDimensions());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23284
	 */
    public void test0396() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0396", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration method = (MethodDeclaration) node;
        SingleVariableDeclaration singleVariableDeclaration = (SingleVariableDeclaration) method.parameters().get(0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", singleVariableDeclaration);
        //$NON-NLS-1$
        checkSourceRange(singleVariableDeclaration, "final String s[]", source);
        Type type = singleVariableDeclaration.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "String", source);
        //$NON-NLS-1$
        assertEquals("Wrong dimension", 1, singleVariableDeclaration.getExtraDimensions());
        ITypeBinding typeBinding = type.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$
        assertTrue("An array binding", !typeBinding.isArray());
        //$NON-NLS-1$
        assertEquals("Wrong dimension", 0, typeBinding.getDimensions());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong fully qualified name", "java.lang.String", typeBinding.getQualifiedName());
        IVariableBinding variableBinding = singleVariableDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No variable binding", variableBinding);
        assertTrue("Is a parameter", variableBinding.isParameter());
        ITypeBinding typeBinding2 = variableBinding.getType();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding2);
        //$NON-NLS-1$
        assertTrue("Not an array binding", typeBinding2.isArray());
        //$NON-NLS-1$
        assertEquals("Wrong dimension", 1, typeBinding2.getDimensions());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong fully qualified name", "java.lang.String[]", typeBinding2.getQualifiedName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23284
	 */
    public void test0397() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0397", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration method = (MethodDeclaration) node;
        SingleVariableDeclaration singleVariableDeclaration = (SingleVariableDeclaration) method.parameters().get(0);
        //$NON-NLS-1$
        assertNotNull("Expression should not be null", singleVariableDeclaration);
        //$NON-NLS-1$
        checkSourceRange(singleVariableDeclaration, "final String[] \\u0073\\u005B][]", source);
        Type type = singleVariableDeclaration.getType();
        //$NON-NLS-1$
        checkSourceRange(type, "String[]", source);
        //$NON-NLS-1$
        assertEquals("Wrong dimension", 2, singleVariableDeclaration.getExtraDimensions());
        ITypeBinding typeBinding = type.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding);
        //$NON-NLS-1$
        assertTrue("Not an array binding", typeBinding.isArray());
        //$NON-NLS-1$
        assertEquals("Wrong dimension", 1, typeBinding.getDimensions());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong fully qualified name", "java.lang.String[]", typeBinding.getQualifiedName());
        IVariableBinding variableBinding = singleVariableDeclaration.resolveBinding();
        //$NON-NLS-1$
        assertNotNull("No variable binding", variableBinding);
        assertTrue("Is a parameter", variableBinding.isParameter());
        ITypeBinding typeBinding2 = variableBinding.getType();
        //$NON-NLS-1$
        assertNotNull("No type binding", typeBinding2);
        //$NON-NLS-1$
        assertTrue("Not an array binding", typeBinding2.isArray());
        //$NON-NLS-1$
        assertEquals("Wrong dimension", 3, typeBinding2.getDimensions());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong fully qualified name", "java.lang.String[][][]", typeBinding2.getQualifiedName());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23362
	 */
    public void test0398() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0398", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a variable declaration statement", node.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT);
        VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
        List fragments = variableDeclarationStatement.fragments();
        //$NON-NLS-1$
        assertEquals("wrong size", 1, fragments.size());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragments.get(0);
        Expression expression = variableDeclarationFragment.getInitializer();
        //$NON-NLS-1$
        assertTrue("Not an infix expression", expression.getNodeType() == ASTNode.INFIX_EXPRESSION);
        InfixExpression infixExpression = (InfixExpression) expression;
        //$NON-NLS-1$
        checkSourceRange(infixExpression, "(1 + 2) * 3", source);
        Expression expression2 = infixExpression.getLeftOperand();
        //$NON-NLS-1$
        assertTrue("Not an parenthesis expression", expression2.getNodeType() == ASTNode.PARENTHESIZED_EXPRESSION);
        ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) expression2;
        Expression expression3 = parenthesizedExpression.getExpression();
        //$NON-NLS-1$
        assertTrue("Not an infix expression", expression3.getNodeType() == ASTNode.INFIX_EXPRESSION);
        //$NON-NLS-1$
        checkSourceRange(expression3, "1 + 2", source);
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=22306
	 */
    public void test0399() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0399", "A.java");
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        //$NON-NLS-1$
        assertTrue("Not a constructor", methodDeclaration.isConstructor());
        Block block = methodDeclaration.getBody();
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 2, statements.size());
    }

    /**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=22306
	 */
    public void test0400() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ICompilationUnit sourceUnit = getCompilationUnit("Converter", "src", "test0400", "A.java");
        char[] source = sourceUnit.getSource().toCharArray();
        ASTNode result = runConversion(getJLS4(), sourceUnit, true);
        ASTNode node = getASTNode((CompilationUnit) result, 0, 0);
        assertNotNull(node);
        //$NON-NLS-1$
        assertTrue("Not a method declaration", node.getNodeType() == ASTNode.METHOD_DECLARATION);
        MethodDeclaration methodDeclaration = (MethodDeclaration) node;
        //$NON-NLS-1$
        assertTrue("Not a constructor", methodDeclaration.isConstructor());
        Block block = methodDeclaration.getBody();
        List statements = block.statements();
        //$NON-NLS-1$
        assertEquals("wrong size", 3, statements.size());
        Statement statement = (Statement) statements.get(0);
        //$NON-NLS-1$
        assertTrue("Not an superconstructorinvocation", statement.getNodeType() == ASTNode.SUPER_CONSTRUCTOR_INVOCATION);
        //$NON-NLS-1$
        checkSourceRange(statement, "super();", source);
    }
}
