/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.tests.dom;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import org.eclipse.jdt.core.dom.*;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ASTTest extends org.eclipse.jdt.core.tests.junit.extension.TestCase {

    /**
	 * Internal synonym for deprecated constant AST.JSL3
	 * to alleviate deprecation warnings.
	 * @deprecated
	 */
    /*package*/
    static final int JLS3_INTERNAL = AST.JLS3;

    class CheckPositionsMatcher extends ASTMatcher {

        public  CheckPositionsMatcher() {
            // include doc tags
            super(true);
        }

        private void checkPositions(Object source, Object destination) {
            assertTrue(source instanceof ASTNode);
            assertTrue(destination instanceof ASTNode);
            int startPosition = ((ASTNode) source).getStartPosition();
            if (startPosition != -1) {
                assertTrue(startPosition == ((ASTNode) destination).getStartPosition());
            }
            int length = ((ASTNode) source).getLength();
            if (length != 0) {
                assertTrue(length == ((ASTNode) destination).getLength());
            }
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(AnnotationTypeDeclaration, Object)
		 * @since 3.0
		 */
        public boolean match(AnnotationTypeDeclaration node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(AnnotationTypeMemberDeclaration, Object)
		 * @since 3.0
		 */
        public boolean match(AnnotationTypeMemberDeclaration node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(AnonymousClassDeclaration, Object)
		 */
        public boolean match(AnonymousClassDeclaration node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(ArrayAccess, Object)
		 */
        public boolean match(ArrayAccess node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(ArrayCreation, Object)
		 */
        public boolean match(ArrayCreation node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(ArrayInitializer, Object)
		 */
        public boolean match(ArrayInitializer node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(ArrayType, Object)
		 */
        public boolean match(ArrayType node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(AssertStatement, Object)
		 */
        public boolean match(AssertStatement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(Assignment, Object)
		 */
        public boolean match(Assignment node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(Block, Object)
		 */
        public boolean match(Block node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(BlockComment, Object)
         * @since 3.0
		 */
        public boolean match(BlockComment node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(BooleanLiteral, Object)
		 */
        public boolean match(BooleanLiteral node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(BreakStatement, Object)
		 */
        public boolean match(BreakStatement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(CastExpression, Object)
		 */
        public boolean match(CastExpression node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(CatchClause, Object)
		 */
        public boolean match(CatchClause node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(CharacterLiteral, Object)
		 */
        public boolean match(CharacterLiteral node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(ClassInstanceCreation, Object)
		 */
        public boolean match(ClassInstanceCreation node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(CompilationUnit, Object)
		 */
        public boolean match(CompilationUnit node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(ConditionalExpression, Object)
		 */
        public boolean match(ConditionalExpression node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(ConstructorInvocation, Object)
		 */
        public boolean match(ConstructorInvocation node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(ContinueStatement, Object)
		 */
        public boolean match(ContinueStatement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(DoStatement, Object)
		 */
        public boolean match(DoStatement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(EmptyStatement, Object)
		 */
        public boolean match(EmptyStatement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(EnhancedForStatement, Object)
		 * @since 3.0
		 */
        public boolean match(EnhancedForStatement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(EnumConstantDeclaration, Object)
		 * @since 3.0
		 */
        public boolean match(EnumConstantDeclaration node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(ExpressionStatement, Object)
		 */
        public boolean match(ExpressionStatement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(FieldAccess, Object)
		 */
        public boolean match(FieldAccess node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(FieldDeclaration, Object)
		 */
        public boolean match(FieldDeclaration node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(ForStatement, Object)
		 */
        public boolean match(ForStatement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(IfStatement, Object)
		 */
        public boolean match(IfStatement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(ImportDeclaration, Object)
		 */
        public boolean match(ImportDeclaration node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(InfixExpression, Object)
		 */
        public boolean match(InfixExpression node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(Initializer, Object)
		 */
        public boolean match(Initializer node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(InstanceofExpression, Object)
		 */
        public boolean match(InstanceofExpression node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(Javadoc, Object)
		 */
        public boolean match(Javadoc node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(LabeledStatement, Object)
		 */
        public boolean match(LabeledStatement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(LineComment, Object)
         * @since 3.0
		 */
        public boolean match(LineComment node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(MarkerAnnotation, Object)
		 * @since 3.0
		 */
        public boolean match(MarkerAnnotation node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(MemberRef, Object)
         * @since 3.0
		 */
        public boolean match(MemberRef node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(MemberValuePair, Object)
		 * @since 3.0
		 */
        public boolean match(MemberValuePair node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(MethodDeclaration, Object)
		 */
        public boolean match(MethodDeclaration node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(MethodInvocation, Object)
		 */
        public boolean match(MethodInvocation node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(MethodRef, Object)
         * @since 3.0
		 */
        public boolean match(MethodRef node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(MethodRefParameter, Object)
         * @since 3.0
		 */
        public boolean match(MethodRefParameter node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(Modifier, Object)
		 * @since 3.0
		 */
        public boolean match(Modifier node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(NormalAnnotation, Object)
		 * @since 3.0
		 */
        public boolean match(NormalAnnotation node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(NullLiteral, Object)
		 */
        public boolean match(NullLiteral node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(NumberLiteral, Object)
		 */
        public boolean match(NumberLiteral node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(PackageDeclaration, Object)
		 */
        public boolean match(PackageDeclaration node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(ParameterizedType, Object)
		 * @since 3.0
		 */
        public boolean match(ParameterizedType node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(ParenthesizedExpression, Object)
		 */
        public boolean match(ParenthesizedExpression node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(PostfixExpression, Object)
		 */
        public boolean match(PostfixExpression node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(PrefixExpression, Object)
		 */
        public boolean match(PrefixExpression node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(PrimitiveType, Object)
		 */
        public boolean match(PrimitiveType node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(QualifiedName, Object)
		 */
        public boolean match(QualifiedName node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(QualifiedType, Object)
		 * @since 3.0
		 */
        public boolean match(QualifiedType node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(ReturnStatement, Object)
		 */
        public boolean match(ReturnStatement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(SimpleName, Object)
		 */
        public boolean match(SimpleName node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(SimpleType, Object)
		 */
        public boolean match(SimpleType node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(SingleMemberAnnotation, Object)
		 * @since 3.0
		 */
        public boolean match(SingleMemberAnnotation node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(SingleVariableDeclaration, Object)
		 */
        public boolean match(SingleVariableDeclaration node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(StringLiteral, Object)
		 */
        public boolean match(StringLiteral node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(SuperConstructorInvocation, Object)
		 */
        public boolean match(SuperConstructorInvocation node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(SuperFieldAccess, Object)
		 */
        public boolean match(SuperFieldAccess node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(SuperMethodInvocation, Object)
		 */
        public boolean match(SuperMethodInvocation node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(SwitchCase, Object)
		 */
        public boolean match(SwitchCase node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(SwitchStatement, Object)
		 */
        public boolean match(SwitchStatement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(SynchronizedStatement, Object)
		 */
        public boolean match(SynchronizedStatement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(TagElement, Object)
         * @since 3.0
		 */
        public boolean match(TagElement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(TextElement, Object)
         * @since 3.0
		 */
        public boolean match(TextElement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(ThisExpression, Object)
		 */
        public boolean match(ThisExpression node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(ThrowStatement, Object)
		 */
        public boolean match(ThrowStatement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(TryStatement, Object)
		 */
        public boolean match(TryStatement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(TypeDeclaration, Object)
		 */
        public boolean match(TypeDeclaration node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(TypeDeclarationStatement, Object)
		 */
        public boolean match(TypeDeclarationStatement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(TypeLiteral, Object)
		 */
        public boolean match(TypeLiteral node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(TypeParameter, Object)
		 * @since 3.0
		 */
        public boolean match(TypeParameter node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(VariableDeclarationExpression, Object)
		 */
        public boolean match(VariableDeclarationExpression node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(VariableDeclarationFragment, Object)
		 */
        public boolean match(VariableDeclarationFragment node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(VariableDeclarationStatement, Object)
		 */
        public boolean match(VariableDeclarationStatement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(WhileStatement, Object)
		 */
        public boolean match(WhileStatement node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }

        /**
		 * @see org.eclipse.jdt.core.dom.ASTMatcher#match(WildcardType, Object)
		 * @since 3.0
		 */
        public boolean match(WildcardType node, Object other) {
            checkPositions(node, other);
            return super.match(node, other);
        }
    }

    /** @deprecated using deprecated code */
    public static Test suite() {
        // TODO (frederic) use buildList + setAstLevel(init) instead...
        junit.framework.TestSuite suite = new junit.framework.TestSuite(ASTTest.class.getName());
        Class c = ASTTest.class;
        Method[] methods = c.getMethods();
        for (int i = 0, max = methods.length; i < max; i++) {
            if (//$NON-NLS-1$
            methods[i].getName().startsWith("test")) {
                suite.addTest(new ASTTest(methods[i].getName(), AST.JLS2));
                suite.addTest(new ASTTest(methods[i].getName(), JLS3_INTERNAL));
                suite.addTest(new ASTTest(methods[i].getName(), AST.JLS4));
                suite.addTest(new ASTTest(methods[i].getName(), AST.JLS8));
            }
        }
        return suite;
    }

    AST ast;

    int API_LEVEL;

    public  ASTTest(String name) {
        super(name.substring(0, name.indexOf(" - JLS")));
        name.indexOf(" - JLS");
        this.API_LEVEL = Integer.parseInt(name.substring(name.indexOf(" - JLS") + 6));
    }

    public  ASTTest(String name, int apiLevel) {
        super(name);
        this.API_LEVEL = apiLevel;
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.ast = AST.newAST(this.API_LEVEL);
    }

    protected void tearDown() throws Exception {
        this.ast = null;
        super.tearDown();
    }

    public String getName() {
        String name = super.getName() + " - JLS" + this.API_LEVEL;
        return name;
    }

    /**
	 * Internal access method to VariableDeclarationFragment#setExtraDimensions for avoiding deprecated warnings.
	 *
	 * @param node
	 * @deprecated
	 */
    private void setExtraDimensions(VariableDeclarationFragment node, int dimensions) {
        node.setExtraDimensions(dimensions);
    }

    /**
	 * Snippets that show how to...
	 * @deprecated using deprecated code
	 */
    public void testExampleSnippets() {
        {
            AST localAst = AST.newAST(this.ast.apiLevel());
            CompilationUnit cu = localAst.newCompilationUnit();
            // package com.example;
            PackageDeclaration pd = localAst.newPackageDeclaration();
            //$NON-NLS-1$ //$NON-NLS-2$
            pd.setName(localAst.newName(new String[] { "com", "example" }));
            cu.setPackage(pd);
            assertTrue(pd.getRoot() == cu);
            // import java.io;*;
            ImportDeclaration im1 = localAst.newImportDeclaration();
            //$NON-NLS-1$ //$NON-NLS-2$
            im1.setName(localAst.newName(new String[] { "java", "io" }));
            im1.setOnDemand(true);
            cu.imports().add(im1);
            assertTrue(im1.getRoot() == cu);
            // import java.util.List;
            ImportDeclaration im2 = localAst.newImportDeclaration();
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            im2.setName(localAst.newName(new String[] { "java", "util", "List" }));
            im2.setOnDemand(false);
            cu.imports().add(im2);
            assertTrue(im2.getRoot() == cu);
            // /** Spec. \n @deprecated Use {@link #foo() bar} instead. */public class MyClass {}
            TypeDeclaration td = localAst.newTypeDeclaration();
            if (this.ast.apiLevel() == AST.JLS2) {
                td.setModifiers(Modifier.PUBLIC);
            } else {
                td.modifiers().add(localAst.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
            }
            td.setInterface(false);
            //$NON-NLS-1$
            td.setName(localAst.newSimpleName("MyClass"));
            {
                Javadoc jd = localAst.newJavadoc();
                TagElement tg0 = localAst.newTagElement();
                jd.tags().add(tg0);
                TextElement tx1 = localAst.newTextElement();
                //$NON-NLS-1$
                tx1.setText(//$NON-NLS-1$
                "Spec.");
                tg0.fragments().add(tx1);
                TagElement tg1 = localAst.newTagElement();
                tg1.setTagName(TagElement.TAG_DEPRECATED);
                jd.tags().add(tg1);
                TextElement tx2 = localAst.newTextElement();
                //$NON-NLS-1$
                tx2.setText(//$NON-NLS-1$
                "Use ");
                tg1.fragments().add(tx2);
                TagElement tg2 = localAst.newTagElement();
                tg2.setTagName(TagElement.TAG_LINK);
                tg1.fragments().add(tg2);
                MethodRef mr1 = localAst.newMethodRef();
                mr1.setName(localAst.newSimpleName("foo"));
                tg2.fragments().add(mr1);
                TextElement tx3 = localAst.newTextElement();
                //$NON-NLS-1$
                tx3.setText(//$NON-NLS-1$
                "bar");
                tg2.fragments().add(tx3);
                TextElement tx4 = localAst.newTextElement();
                //$NON-NLS-1$
                tx2.setText(//$NON-NLS-1$
                " instead.");
                tg1.fragments().add(tx4);
            }
            cu.types().add(td);
            assertTrue(td.getRoot() == cu);
            // private static boolean DEBUG = true;
            VariableDeclarationFragment f1 = localAst.newVariableDeclarationFragment();
            //$NON-NLS-1$
            f1.setName(localAst.newSimpleName("DEBUG"));
            f1.setInitializer(localAst.newBooleanLiteral(true));
            FieldDeclaration fd = localAst.newFieldDeclaration(f1);
            fd.setType(localAst.newPrimitiveType(PrimitiveType.BOOLEAN));
            if (this.ast.apiLevel() == AST.JLS2) {
                fd.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
            } else {
                fd.modifiers().add(localAst.newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD));
                fd.modifiers().add(localAst.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
            }
            td.bodyDeclarations().add(fd);
            assertTrue(fd.getRoot() == cu);
            // public static void main();
            MethodDeclaration md = localAst.newMethodDeclaration();
            if (this.ast.apiLevel() == AST.JLS2) {
                md.setModifiers(Modifier.PUBLIC | Modifier.STATIC);
                md.setReturnType(localAst.newPrimitiveType(PrimitiveType.VOID));
            } else {
                md.modifiers().add(localAst.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
                md.modifiers().add(localAst.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
                md.setReturnType2(localAst.newPrimitiveType(PrimitiveType.VOID));
            }
            md.setConstructor(false);
            //$NON-NLS-1$
            md.setName(localAst.newSimpleName("main"));
            td.bodyDeclarations().add(md);
            assertTrue(md.getRoot() == cu);
            // String[] args
            SingleVariableDeclaration a1 = localAst.newSingleVariableDeclaration();
            a1.setType(localAst.newArrayType(//$NON-NLS-1$
            localAst.newSimpleType(localAst.newSimpleName("String"))));
            //$NON-NLS-1$
            a1.setName(localAst.newSimpleName("args"));
            md.parameters().add(a1);
            assertTrue(a1.getRoot() == cu);
            // {}
            Block b = localAst.newBlock();
            md.setBody(b);
            assertTrue(b.getRoot() == cu);
            // System.out.println("hello world");
            MethodInvocation e = localAst.newMethodInvocation();
            //$NON-NLS-1$ //$NON-NLS-2$
            e.setExpression(localAst.newName(new String[] { "System", "out" }));
            //$NON-NLS-1$
            e.setName(localAst.newSimpleName("println"));
            StringLiteral h = localAst.newStringLiteral();
            //$NON-NLS-1$
            h.setLiteralValue("hello world");
            e.arguments().add(h);
            b.statements().add(localAst.newExpressionStatement(e));
            assertTrue(e.getRoot() == cu);
            assertTrue(h.getRoot() == cu);
            // new String[len];
            ArrayCreation ac1 = localAst.newArrayCreation();
            ac1.setType(localAst.newArrayType(localAst.newSimpleType(//$NON-NLS-1$
            localAst.newSimpleName(//$NON-NLS-1$
            "String"))));
            //$NON-NLS-1$
            ac1.dimensions().add(localAst.newSimpleName("len"));
            b.statements().add(localAst.newExpressionStatement(ac1));
            assertTrue(ac1.getRoot() == cu);
            // new double[7][24][];
            ArrayCreation ac2 = localAst.newArrayCreation();
            ac2.setType(localAst.newArrayType(localAst.newPrimitiveType(PrimitiveType.DOUBLE), 3));
            //$NON-NLS-1$
            ac2.dimensions().add(localAst.newNumberLiteral("7"));
            //$NON-NLS-1$
            ac2.dimensions().add(localAst.newNumberLiteral("24"));
            b.statements().add(localAst.newExpressionStatement(ac2));
            assertTrue(ac2.getRoot() == cu);
            // new int[] {1, 2};
            ArrayCreation ac3 = localAst.newArrayCreation();
            ac3.setType(localAst.newArrayType(localAst.newPrimitiveType(PrimitiveType.INT)));
            ArrayInitializer ai = localAst.newArrayInitializer();
            ac3.setInitializer(ai);
            //$NON-NLS-1$
            ai.expressions().add(localAst.newNumberLiteral("1"));
            //$NON-NLS-1$
            ai.expressions().add(localAst.newNumberLiteral("2"));
            b.statements().add(localAst.newExpressionStatement(ac3));
            assertTrue(ac3.getRoot() == cu);
            assertTrue(ai.getRoot() == cu);
            // new String(10);
            ClassInstanceCreation cr1 = localAst.newClassInstanceCreation();
            if (this.ast.apiLevel() == AST.JLS2) {
                //$NON-NLS-1$
                cr1.setName(//$NON-NLS-1$
                localAst.newSimpleName("String"));
            } else {
                //$NON-NLS-1$
                cr1.setType(//$NON-NLS-1$
                localAst.newSimpleType(localAst.newSimpleName("String")));
            }
            //$NON-NLS-1$
            cr1.arguments().add(localAst.newNumberLiteral("10"));
            b.statements().add(localAst.newExpressionStatement(cr1));
            assertTrue(cr1.getRoot() == cu);
            // new Listener() {public void handleEvent() {} };
            ClassInstanceCreation cr2 = localAst.newClassInstanceCreation();
            AnonymousClassDeclaration ad1 = localAst.newAnonymousClassDeclaration();
            cr2.setAnonymousClassDeclaration(ad1);
            if (this.ast.apiLevel() == AST.JLS2) {
                //$NON-NLS-1$
                cr2.setName(//$NON-NLS-1$
                localAst.newSimpleName("Listener"));
            } else {
                //$NON-NLS-1$
                cr2.setType(//$NON-NLS-1$
                localAst.newSimpleType(localAst.newSimpleName("Listener")));
            }
            MethodDeclaration md0 = localAst.newMethodDeclaration();
            if (this.ast.apiLevel() == AST.JLS2) {
                md0.setModifiers(Modifier.PUBLIC);
            } else {
                md0.modifiers().add(localAst.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
            }
            //$NON-NLS-1$
            md0.setName(localAst.newSimpleName("handleEvent"));
            md0.setBody(localAst.newBlock());
            ad1.bodyDeclarations().add(md0);
            b.statements().add(localAst.newExpressionStatement(cr2));
            assertTrue(cr2.getRoot() == cu);
            assertTrue(md0.getRoot() == cu);
            assertTrue(ad1.getRoot() == cu);
        }
    }

    abstract class Property {

        /**
		 * Indicates whether this property is compulsory, in that every node
		 * must have a value at all times.
		 */
        private boolean compulsory;

        private Class nodeType;

        private String propertyName;

        /**
		 * Creates a new property with the given name.
		 */
         Property(String propertyName, boolean compulsory, Class nodeType) {
            this.propertyName = propertyName;
            this.compulsory = compulsory;
            this.nodeType = nodeType;
        }

        /**
		 * Returns a sample node of a type suitable for storing
		 * in this property.
		 *
		 * @param targetAST the target AST
		 * @param parented <code>true</code> if the sample should be
		 *    parented, and <code>false</code> if unparented
		 * @return a sample node
		 */
        public abstract ASTNode sample(AST targetAST, boolean parented);

        /**
		 * Returns examples of node of types unsuitable for storing
		 * in this property.
		 * <p>
		 * This implementation returns an empty list. Subclasses
		 * should reimplement to specify counter-examples.
		 * </p>
		 *
		 * @param targetAST the target AST
		 * @return a list of counter-example nodes
		 */
        public ASTNode[] counterExamples(AST targetAST) {
            return new ASTNode[] {};
        }

        /**
		 * Returns a sample node of a type suitable for storing
		 * in this property. The sample embeds the node itself.
		 * <p>
		 * For instance, for an Expression-valued property of a given
		 * Statement, this method returns an Expression that embeds
		 * this Statement node (as a descendent).
		 * </p>
		 * <p>
		 * Returns <code>null</code> if such an embedding is impossible.
		 * For instance, for an Name-valued property of a given
		 * Statement, this method returns <code>null</code> because
		 * an Expression cannot be embedded in a Name.
		 * </p>
		 * <p>
		 * This implementation returns <code>null</code>. Subclasses
		 * should reimplement to specify an embedding.
		 * </p>
		 *
		 * @return a sample node that embeds the given node,
		 *    and <code>null</code> if such an embedding is impossible
		 */
        public ASTNode wrap() {
            return null;
        }

        /**
		 * Undoes the effects of a previous <code>wrap</code>.
		 * <p>
		 * This implementation does nothing. Subclasses
		 * should reimplement if they reimplement <code>wrap</code>.
		 * </p>
		 */
        public void unwrap() {
        }

        /**
		 * Returns whether this property is compulsory, in that every node
		 * must have a value at all times.
		 *
		 * @return <code>true</code> if the property is compulsory,
		 *    and <code>false</code> if the property may be null
		 */
        public final boolean isCompulsory() {
            return this.compulsory;
        }

        /**
		 * Returns the value of this property.
		 * <p>
		 * This implementation throws an unchecked exception. Subclasses
		 * should reimplement.
		 * </p>
		 *
		 * @return the property value, or <code>null</code> if no value
		 */
        public ASTNode get() {
            //$NON-NLS-1$
            throw new RuntimeException("get not implemented");
        }

        /**
		 * Sets or clears the value of this property.
		 * <p>
		 * This implementation throws an unchecked exception. Subclasses
		 * should reimplement.
		 * </p>
		 *
		 * @param value the property value, or <code>null</code> if no value
		 */
        public void set(ASTNode value) {
            //$NON-NLS-1$ //$NON-NLS-2$
            throw new RuntimeException("get(" + value + ") not implemented");
        }

        public String toString() {
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            return "Property(" + this.propertyName + ", " + this.compulsory + ", " + this.nodeType + ")";
        }
    }

    /**
	 * Exercises the given property of the given node.
	 *
	 * @param node the node to test
	 * @param prop the property descriptor
	 */
    void genericPropertyTest(ASTNode node, Property prop) {
        ASTNode x1 = prop.sample(node.getAST(), false);
        prop.set(x1);
        assertTrue(prop.get() == x1);
        assertTrue(x1.getParent() == node);
        // check handling of null
        if (prop.isCompulsory()) {
            try {
                prop.set(null);
                assertTrue(false);
            } catch (RuntimeException e) {
            }
        } else {
            long previousCount = node.getAST().modificationCount();
            prop.set(null);
            assertTrue(prop.get() == null);
            assertTrue(node.getAST().modificationCount() > previousCount);
        }
        // check that a child from a different AST is detected
        try {
            AST newAST = AST.newAST(node.getAST().apiLevel());
            prop.set(prop.sample(newAST, false));
            assertTrue(false);
        } catch (RuntimeException e) {
        }
        // check that a child with a parent is detected
        try {
            ASTNode b1 = prop.sample(node.getAST(), true);
            // bogus: already has parent
            prop.set(b1);
            assertTrue(false);
        } catch (RuntimeException e) {
        }
        // check that a cycle is detected
        assertTrue(node.getParent() == null);
        ASTNode s1 = null;
        try {
            s1 = prop.wrap();
            if (s1 != null) {
                // bogus: creates a cycle
                prop.set(s1);
                assertTrue(false);
            }
        } catch (RuntimeException e) {
        } finally {
            if (s1 != null) {
                prop.unwrap();
                assertTrue(node.getParent() == null);
            }
        }
        // check that a child of the wrong type is detected
        ASTNode b1[] = prop.counterExamples(node.getAST());
        for (int i = 0; i < b1.length; i++) {
            try {
                // bogus: wrong type
                prop.set(b1[i]);
                assertTrue(false);
            } catch (RuntimeException e) {
            }
        }
    }

    /**
	 * Exercises the given property of the given node.
	 *
	 * @param node the node to test
	 * @param children the node to test
	 * @param prop the property descriptor
	 */
    void genericPropertyListTest(ASTNode node, List children, Property prop) {
        // wipe the slate clean
        children.clear();
        assertTrue(children.size() == 0);
        // add a child
        ASTNode x1 = prop.sample(node.getAST(), false);
        long previousCount = node.getAST().modificationCount();
        children.add(x1);
        assertTrue(node.getAST().modificationCount() > previousCount);
        assertTrue(children.size() == 1);
        assertTrue(children.get(0) == x1);
        assertTrue(x1.getParent() == node);
        // add a second child
        ASTNode x2 = prop.sample(node.getAST(), false);
        previousCount = node.getAST().modificationCount();
        children.add(x2);
        assertTrue(node.getAST().modificationCount() > previousCount);
        assertTrue(children.size() == 2);
        assertTrue(children.get(0) == x1);
        assertTrue(children.get(1) == x2);
        assertTrue(x1.getParent() == node);
        assertTrue(x2.getParent() == node);
        // remove the first child
        previousCount = node.getAST().modificationCount();
        children.remove(0);
        assertTrue(node.getAST().modificationCount() > previousCount);
        assertTrue(children.size() == 1);
        assertTrue(children.get(0) == x2);
        assertTrue(x1.getParent() == null);
        assertTrue(x2.getParent() == node);
        // remove the remaining child
        previousCount = node.getAST().modificationCount();
        children.remove(x2);
        assertTrue(node.getAST().modificationCount() > previousCount);
        assertTrue(children.size() == 0);
        assertTrue(x1.getParent() == null);
        assertTrue(x2.getParent() == null);
        // check that null is never allowed
        try {
            children.add(null);
            assertTrue(false);
        } catch (RuntimeException e) {
        }
        // check that a child from a different AST is detected
        try {
            AST newAST = AST.newAST(node.getAST().apiLevel());
            children.add(prop.sample(newAST, false));
            assertTrue(false);
        } catch (RuntimeException e) {
        }
        // check that a child with a parent is detected
        try {
            ASTNode b1 = prop.sample(node.getAST(), true);
            // bogus: already has parent
            children.add(b1);
            assertTrue(false);
        } catch (RuntimeException e) {
        }
        // check that a cycle is detected
        assertTrue(node.getParent() == null);
        ASTNode s1 = null;
        try {
            s1 = prop.wrap();
            if (s1 != null) {
                // bogus: creates a cycle
                children.add(s1);
                assertTrue(false);
            }
        } catch (RuntimeException e) {
        } finally {
            if (s1 != null) {
                prop.unwrap();
                assertTrue(node.getParent() == null);
            }
        }
        // check that a child of the wrong type is detected
        ASTNode b1[] = prop.counterExamples(node.getAST());
        for (int i = 0; i < b1.length; i++) {
            try {
                // bogus: wrong type
                children.add(b1[i]);
                assertTrue(false);
            } catch (RuntimeException e) {
            }
        }
    }

    /** @deprecated using deprecated code */
    public void testAST() {
        assertSame(AST.JLS2, 2);
        assertSame(JLS3_INTERNAL, 3);
        // deprecated, but still 2.0
        AST a0 = new AST();
        assertTrue(a0.apiLevel() == AST.JLS2);
        // deprecated, but still 2.0
        AST a1 = new AST(new HashMap());
        assertTrue(a1.apiLevel() == AST.JLS2);
        AST a2 = AST.newAST(AST.JLS2);
        assertTrue(a2.apiLevel() == AST.JLS2);
        AST a3 = AST.newAST(JLS3_INTERNAL);
        assertTrue(a3.apiLevel() == JLS3_INTERNAL);
        // modification count is always non-negative
        assertTrue(this.ast.modificationCount() >= 0);
        // modification count increases for node creations
        long previousCount = this.ast.modificationCount();
        //$NON-NLS-1$
        SimpleName x = this.ast.newSimpleName("first");
        assertTrue(this.ast.modificationCount() > previousCount);
        // modification count does not increase for reading node attributes
        previousCount = this.ast.modificationCount();
        x.getIdentifier();
        x.getParent();
        x.getRoot();
        x.getAST();
        x.getFlags();
        x.getStartPosition();
        x.getLength();
        x.equals(x);
        assertTrue(this.ast.modificationCount() == previousCount);
        // modification count does not increase for reading or writing properties
        previousCount = this.ast.modificationCount();
        //$NON-NLS-1$
        x.getProperty("any");
        // N.B. //$NON-NLS-1$ //$NON-NLS-2$
        x.setProperty("any", "value");
        x.properties();
        assertTrue(this.ast.modificationCount() == previousCount);
        // modification count increases for changing node attributes
        previousCount = this.ast.modificationCount();
        //$NON-NLS-1$
        x.setIdentifier("second");
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        x.setFlags(0);
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        x.setSourceRange(-1, 0);
        assertTrue(this.ast.modificationCount() > previousCount);
    }

    public void testWellKnownBindings() {
        // well known bindings
        String[] wkbs = { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "byte", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "char", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "short", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "int", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "long", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        "boolean", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        "float", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        "double", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        "void", //$NON-NLS-1$
        "java.lang.AssertionError", //$NON-NLS-1$
        "java.lang.Class", //$NON-NLS-1$
        "java.lang.Cloneable", //$NON-NLS-1$
        "java.lang.Error", //$NON-NLS-1$
        "java.lang.Exception", //$NON-NLS-1$
        "java.lang.Object", //$NON-NLS-1$
        "java.lang.RuntimeException", //$NON-NLS-1$
        "java.lang.String", //$NON-NLS-1$
        "java.lang.StringBuffer", //$NON-NLS-1$
        "java.lang.Throwable", //$NON-NLS-1$
        "java.io.Serializable", //$NON-NLS-1$
        "java.lang.Boolean", //$NON-NLS-1$
        "java.lang.Byte", //$NON-NLS-1$
        "java.lang.Character", //$NON-NLS-1$
        "java.lang.Double", //$NON-NLS-1$
        "java.lang.Float", //$NON-NLS-1$
        "java.lang.Integer", //$NON-NLS-1$
        "java.lang.Long", //$NON-NLS-1$
        "java.lang.Short", //$NON-NLS-1$
        "java.lang.Void" };
        // no-so-well-known bindings
        String[] nwkbs = { //$NON-NLS-1$
        "verylong", //$NON-NLS-1$
        "java.lang.Math", //$NON-NLS-1$
        "com.example.MyCode" };
        // none of the well known bindings resolve in a plain AST
        for (int i = 0; i < wkbs.length; i++) {
            assertTrue(this.ast.resolveWellKnownType(wkbs[i]) == null);
        }
        // none of the no so well known bindings resolve either
        for (int i = 0; i < nwkbs.length; i++) {
            assertTrue(this.ast.resolveWellKnownType(nwkbs[i]) == null);
        }
    }

    public void testSimpleName() {
        long previousCount = this.ast.modificationCount();
        //$NON-NLS-1$
        SimpleName x = this.ast.newSimpleName("foo");
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        //$NON-NLS-1$
        assertTrue("foo".equals(x.getIdentifier()));
        //$NON-NLS-1$
        assertTrue("foo".equals(x.getFullyQualifiedName()));
        assertTrue(x.getNodeType() == ASTNode.SIMPLE_NAME);
        assertTrue(x.isDeclaration() == false);
        assertTrue(x.structuralPropertiesForType() == SimpleName.propertyDescriptors(this.ast.apiLevel()));
        // make sure that reading did not change modification count
        assertTrue(this.ast.modificationCount() == previousCount);
        previousCount = this.ast.modificationCount();
        //$NON-NLS-1$
        x.setIdentifier("bar");
        assertTrue(this.ast.modificationCount() > previousCount);
        //$NON-NLS-1$
        assertTrue("bar".equals(x.getIdentifier()));
        //$NON-NLS-1$
        assertTrue("bar".equals(x.getFullyQualifiedName()));
        // check that property cannot be set to null
        try {
            x.setIdentifier(null);
            assertTrue(false);
        } catch (RuntimeException e) {
        }
        // check that property cannot be set to keyword or reserved work
        String[] reserved = new String[] { // literals //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "true", // literals //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "false", // literals //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "null", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "abstract", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "default", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "if", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "private", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "this", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "boolean", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "do", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "implements", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "protected", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "throw", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "break", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "double", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "import", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "public", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "throws", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "byte", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "else", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "instanceof", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "return", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "transient", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "case", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "extends", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "int", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "short", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "try", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "catch", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "final", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "interface", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "static", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "void", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "char", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "finally", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "long", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "strictfp", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "volatile", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "class", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "float", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "native", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "super", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "while", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        "const", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        "for", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        "new", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        "switch", "continue", "goto", "package", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        "synchronized" };
        for (int i = 0; i < reserved.length; i++) {
            try {
                x.setIdentifier(reserved[i]);
                assertTrue(false);
            } catch (RuntimeException e) {
            }
        }
        // check that property cannot be set to keyword or reserved work
        String[] bogus = new String[] { // literals //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "a b", // literals //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "a ", // literals //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        " a", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "a-b", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "a[]", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "a<T>", "", " ", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "a.b" };
        for (int i = 0; i < bogus.length; i++) {
            try {
                x.setIdentifier(bogus[i]);
                assertTrue(false);
            } catch (RuntimeException e) {
            }
        }
        // check that "assert" is not considered a keyword
        // "assert" only became a keyword in J2SE 1.4 and we do *not* want to
        // preclude the AST API from being used to analyze pre-1.4 code
        //$NON-NLS-1$
        x.setIdentifier("assert");
        // check that "enum" is not considered a keyword
        // "enum" only became a keyword in J2SE 1.5 and we do *not* want to
        // preclude the AST API from being used to analyze pre-1.5 code
        //$NON-NLS-1$
        x.setIdentifier("enum");
        // check that isDeclaration works
        //$NON-NLS-1$
        QualifiedName y = this.ast.newQualifiedName(this.ast.newSimpleName("a"), x);
        assertTrue(x.isDeclaration() == false);
        //$NON-NLS-1$
        y.setName(this.ast.newSimpleName("b"));
        assertTrue(x.isDeclaration() == false);
        TypeDeclaration td = this.ast.newTypeDeclaration();
        td.setName(x);
        assertTrue(x.isDeclaration() == true);
        //$NON-NLS-1$
        td.setName(this.ast.newSimpleName("b"));
        assertTrue(x.isDeclaration() == false);
        MethodDeclaration md = this.ast.newMethodDeclaration();
        md.setName(x);
        assertTrue(x.isDeclaration() == true);
        //$NON-NLS-1$
        md.setName(this.ast.newSimpleName("b"));
        assertTrue(x.isDeclaration() == false);
        SingleVariableDeclaration vd = this.ast.newSingleVariableDeclaration();
        vd.setName(x);
        assertTrue(x.isDeclaration() == true);
        //$NON-NLS-1$
        vd.setName(this.ast.newSimpleName("b"));
        assertTrue(x.isDeclaration() == false);
        VariableDeclarationFragment fd = this.ast.newVariableDeclarationFragment();
        fd.setName(x);
        assertTrue(x.isDeclaration() == true);
        //$NON-NLS-1$
        fd.setName(this.ast.newSimpleName("b"));
        assertTrue(x.isDeclaration() == false);
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            AnnotationTypeDeclaration atd = this.ast.newAnnotationTypeDeclaration();
            atd.setName(x);
            assertTrue(x.isDeclaration() == true);
            //$NON-NLS-1$
            atd.setName(this.ast.newSimpleName("b"));
            assertTrue(x.isDeclaration() == false);
        }
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            AnnotationTypeMemberDeclaration atmd = this.ast.newAnnotationTypeMemberDeclaration();
            atmd.setName(x);
            assertTrue(x.isDeclaration() == true);
            //$NON-NLS-1$
            atmd.setName(this.ast.newSimpleName("b"));
            assertTrue(x.isDeclaration() == false);
        }
    }

    public void testQualifiedName() {
        long previousCount = this.ast.modificationCount();
        final QualifiedName x = this.ast.newQualifiedName(//$NON-NLS-1$
        this.ast.newSimpleName("q"), //$NON-NLS-1$
        this.ast.newSimpleName("i"));
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getQualifier().getParent() == x);
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.getName().isDeclaration() == false);
        assertTrue(x.getNodeType() == ASTNode.QUALIFIED_NAME);
        //$NON-NLS-1$
        assertTrue("q.i".equals(x.getFullyQualifiedName()));
        assertTrue(x.structuralPropertiesForType() == QualifiedName.propertyDescriptors(this.ast.apiLevel()));
        // make sure that reading did not change modification count
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new //$NON-NLS-1$
        Property(//$NON-NLS-1$
        "Qualifier", //$NON-NLS-1$
        true, //$NON-NLS-1$
        Name.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                QualifiedName result = targetAst.newQualifiedName(//$NON-NLS-1$
                targetAst.newSimpleName(//$NON-NLS-1$
                "a"), //$NON-NLS-1$
                targetAst.newSimpleName(//$NON-NLS-1$
                "b"));
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                QualifiedName s1 = //$NON-NLS-1$
                ASTTest.this.ast.newQualifiedName(//$NON-NLS-1$
                x, //$NON-NLS-1$
                ASTTest.this.ast.newSimpleName("z"));
                return s1;
            }

            public void unwrap() {
                QualifiedName s1 = (QualifiedName) x.getParent();
                //$NON-NLS-1$
                s1.setQualifier(//$NON-NLS-1$
                ASTTest.this.ast.newSimpleName("z"));
            }

            public ASTNode get() {
                return x.getQualifier();
            }

            public void set(ASTNode value) {
                x.setQualifier((Name) value);
            }
        });
        genericPropertyTest(x, new //$NON-NLS-1$
        Property(//$NON-NLS-1$
        "Name", //$NON-NLS-1$
        true, //$NON-NLS-1$
        SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = //$NON-NLS-1$
                targetAst.newSimpleName(//$NON-NLS-1$
                "foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
        // test fullyQualifiedName on nested names
        Name q0 = this.ast.newName(new String[] { "a", "bb", "ccc", "dddd", "eeeee", "ffffff" });
        //$NON-NLS-1$
        assertTrue("a.bb.ccc.dddd.eeeee.ffffff".equals(q0.getFullyQualifiedName()));
    }

    public void testNameFactories() {
        long previousCount = this.ast.modificationCount();
        //$NON-NLS-1$
        Name x = this.ast.newName("foo");
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        //$NON-NLS-1$
        assertTrue("foo".equals(x.getFullyQualifiedName()));
        assertTrue(x.getNodeType() == ASTNode.SIMPLE_NAME);
        previousCount = this.ast.modificationCount();
        //$NON-NLS-1$
        x = this.ast.newName("foo.bar");
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        //$NON-NLS-1$
        assertTrue("foo.bar".equals(x.getFullyQualifiedName()));
        assertTrue(x.getNodeType() == ASTNode.QUALIFIED_NAME);
        QualifiedName q = (QualifiedName) x;
        //$NON-NLS-1$
        assertTrue("bar".equals(q.getName().getFullyQualifiedName()));
        //$NON-NLS-1$
        assertTrue("foo".equals(q.getQualifier().getFullyQualifiedName()));
        // check that simple and qualified names work
        String[] legal = new String[] { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "a", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "abcdef", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "XYZZY", "a.b", "java.lang.Object", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "a.b.c.d.e" };
        for (int i = 0; i < legal.length; i++) {
            try {
                x = this.ast.newName(legal[i]);
                assertTrue(legal[i].equals(x.getFullyQualifiedName()));
            } catch (RuntimeException e) {
                assertTrue(false);
            }
        }
        // check that property cannot be set to keyword or reserved work
        String[] bogus = new String[] { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        ".", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        ".a", "a.", "a..b", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "..a" };
        for (int i = 0; i < bogus.length; i++) {
            try {
                x = this.ast.newName(bogus[i]);
                assertTrue(false);
            } catch (RuntimeException e) {
            }
        }
    }

    public void testNullLiteral() {
        long previousCount = this.ast.modificationCount();
        NullLiteral x = this.ast.newNullLiteral();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getNodeType() == ASTNode.NULL_LITERAL);
        assertTrue(x.structuralPropertiesForType() == NullLiteral.propertyDescriptors(this.ast.apiLevel()));
        // make sure that reading did not change modification count
        assertTrue(this.ast.modificationCount() == previousCount);
    }

    public void testBooleanLiteral() {
        long previousCount = this.ast.modificationCount();
        BooleanLiteral x = this.ast.newBooleanLiteral(true);
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.booleanValue() == true);
        assertTrue(x.getNodeType() == ASTNode.BOOLEAN_LITERAL);
        assertTrue(x.structuralPropertiesForType() == BooleanLiteral.propertyDescriptors(this.ast.apiLevel()));
        // make sure that reading did not change modification count
        assertTrue(this.ast.modificationCount() == previousCount);
        previousCount = this.ast.modificationCount();
        x.setBooleanValue(false);
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.booleanValue() == false);
        previousCount = this.ast.modificationCount();
        x.setBooleanValue(true);
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.booleanValue() == true);
    }

    public void testStringLiteral() {
        long previousCount = this.ast.modificationCount();
        // check 0-arg factory first
        StringLiteral x = this.ast.newStringLiteral();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        //$NON-NLS-1$
        assertTrue("\"\"".equals(x.getEscapedValue()));
        //$NON-NLS-1$
        assertTrue("".equals(x.getLiteralValue()));
        assertTrue(x.getNodeType() == ASTNode.STRING_LITERAL);
        assertTrue(x.structuralPropertiesForType() == StringLiteral.propertyDescriptors(this.ast.apiLevel()));
        // make sure that reading did not change modification count
        assertTrue(this.ast.modificationCount() == previousCount);
        previousCount = this.ast.modificationCount();
        //$NON-NLS-1$
        x.setEscapedValue("\"bye\"");
        assertTrue(this.ast.modificationCount() > previousCount);
        //$NON-NLS-1$
        assertTrue("\"bye\"".equals(x.getEscapedValue()));
        //$NON-NLS-1$
        assertTrue("bye".equals(x.getLiteralValue()));
        previousCount = this.ast.modificationCount();
        //$NON-NLS-1$
        x.setLiteralValue("hi");
        assertTrue(this.ast.modificationCount() > previousCount);
        //$NON-NLS-1$
        assertTrue("\"hi\"".equals(x.getEscapedValue()));
        //$NON-NLS-1$
        assertTrue("hi".equals(x.getLiteralValue()));
        previousCount = this.ast.modificationCount();
        //$NON-NLS-1$
        x.setLiteralValue("\\012\\015");
        assertTrue(this.ast.modificationCount() > previousCount);
        //$NON-NLS-1$
        assertEquals("different", "\"\\\\012\\\\015\"", x.getEscapedValue());
        //$NON-NLS-1$
        assertTrue("\\012\\015".equals(x.getLiteralValue()));
        previousCount = this.ast.modificationCount();
        //$NON-NLS-1$
        x.setLiteralValue("\012\015");
        assertTrue(this.ast.modificationCount() > previousCount);
        //$NON-NLS-1$
        assertTrue("\n\r".equals(x.getLiteralValue()));
        //$NON-NLS-1$
        assertEquals("different", "\"\\n\\r\"", x.getEscapedValue());
        // check that property cannot be set to null
        try {
            x.setEscapedValue(null);
            assertTrue(false);
        } catch (RuntimeException e) {
        }
        // check that property cannot be set to null
        try {
            x.setLiteralValue(null);
            assertTrue(false);
        } catch (RuntimeException e) {
        }
        // test for 319900
        x.setLiteralValue("'");
        assertEquals("", "\"'\"", x.getEscapedValue());
        assertEquals("", "'", x.getLiteralValue());
        // test for 319900
        x.setEscapedValue("\"'\"");
        assertEquals("", "\"'\"", x.getEscapedValue());
        assertEquals("", "'", x.getLiteralValue());
        // test for bug 442614
        x.setLiteralValue("\0041");
        assertEquals("", "\"\\u00041\"", x.getEscapedValue());
        assertEquals("", "1", x.getLiteralValue());
    }

    public void testStringLiteralUnicode() {
        AST localAst = AST.newAST(this.ast.apiLevel());
        StringLiteral literal = localAst.newStringLiteral();
        //$NON-NLS-1$
        literal.setEscapedValue("\"hello\\u0026\\u0050worl\\u0064\"");
        //$NON-NLS-1$
        assertTrue(literal.getLiteralValue().equals("hello&Pworld"));
        localAst = AST.newAST(this.ast.apiLevel());
        literal = localAst.newStringLiteral();
        //$NON-NLS-1$
        literal.setEscapedValue("\"hello\\nworld\"");
        //$NON-NLS-1$
        assertTrue(literal.getLiteralValue().equals("hello\nworld"));
        localAst = AST.newAST(this.ast.apiLevel());
        literal = localAst.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue("hello\nworld");
        //$NON-NLS-1$
        assertTrue(literal.getLiteralValue().equals("hello\nworld"));
        localAst = AST.newAST(this.ast.apiLevel());
        literal = localAst.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue("\n");
        //$NON-NLS-1$
        assertTrue(literal.getEscapedValue().equals("\"\\n\""));
        //$NON-NLS-1$
        assertTrue(literal.getLiteralValue().equals("\n"));
        localAst = AST.newAST(this.ast.apiLevel());
        literal = localAst.newStringLiteral();
        //$NON-NLS-1$
        literal.setEscapedValue("\"hello\\\"world\"");
        //$NON-NLS-1$
        assertTrue(literal.getLiteralValue().equals("hello\"world"));
        localAst = AST.newAST(this.ast.apiLevel());
        literal = localAst.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue("hello\\u0026world");
        //$NON-NLS-1$
        assertTrue(literal.getLiteralValue().equals("hello\\u0026world"));
        localAst = AST.newAST(this.ast.apiLevel());
        literal = localAst.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue("hello\\u0026world");
        //$NON-NLS-1$
        assertTrue(literal.getEscapedValue().equals("\"hello\\\\u0026world\""));
        localAst = AST.newAST(this.ast.apiLevel());
        literal = localAst.newStringLiteral();
        //$NON-NLS-1$
        literal.setLiteralValue("\\u0001");
        //$NON-NLS-1$
        assertTrue(literal.getEscapedValue().equals("\"\\\\u0001\""));
    }

    public void testCharacterLiteral() {
        long previousCount = this.ast.modificationCount();
        CharacterLiteral x = this.ast.newCharacterLiteral();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        //$NON-NLS-1$
        assertTrue(x.getEscapedValue().startsWith("\'"));
        //$NON-NLS-1$
        assertTrue(x.getEscapedValue().endsWith("\'"));
        assertTrue(x.getNodeType() == ASTNode.CHARACTER_LITERAL);
        assertTrue(x.structuralPropertiesForType() == CharacterLiteral.propertyDescriptors(this.ast.apiLevel()));
        // make sure that reading did not change modification count
        assertTrue(this.ast.modificationCount() == previousCount);
        previousCount = this.ast.modificationCount();
        //$NON-NLS-1$
        x.setEscapedValue("\'z\'");
        assertTrue(this.ast.modificationCount() > previousCount);
        //$NON-NLS-1$
        assertTrue("\'z\'".equals(x.getEscapedValue()));
        assertTrue(x.charValue() == 'z');
        try {
            //$NON-NLS-1$
            x.setEscapedValue("\"z\"");
            assertTrue(false);
        } catch (IllegalArgumentException e) {
        }
        // test other factory method
        previousCount = this.ast.modificationCount();
        CharacterLiteral y = this.ast.newCharacterLiteral();
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(y.getAST() == this.ast);
        assertTrue(y.getParent() == null);
        String v = y.getEscapedValue();
        assertTrue(v.length() >= 3 && v.charAt(0) == '\'' & v.charAt(v.length() - 1) == '\'');
        // check that property cannot be set to null
        try {
            x.setEscapedValue(null);
            assertTrue(false);
        } catch (RuntimeException e) {
        }
        // b, t, n, f, r, ", ', \, 0, 1, 2, 3, 4, 5, 6, or 7
        try {
            //$NON-NLS-1$
            x.setEscapedValue("\'\\b\'");
            //$NON-NLS-1$
            x.setEscapedValue("\'\\t\'");
            //$NON-NLS-1$
            x.setEscapedValue("\'\\n\'");
            //$NON-NLS-1$
            x.setEscapedValue("\'\\f\'");
            //$NON-NLS-1$
            x.setEscapedValue("\'\\\"\'");
            //$NON-NLS-1$
            x.setEscapedValue("\'\\'\'");
            //$NON-NLS-1$
            x.setEscapedValue("\'\\\\\'");
            //$NON-NLS-1$
            x.setEscapedValue("\'\\0\'");
            //$NON-NLS-1$
            x.setEscapedValue("\'\\1\'");
            //$NON-NLS-1$
            x.setEscapedValue("\'\\2\'");
            //$NON-NLS-1$
            x.setEscapedValue("\'\\3\'");
            //$NON-NLS-1$
            x.setEscapedValue("\'\\4\'");
            //$NON-NLS-1$
            x.setEscapedValue("\'\\5\'");
            //$NON-NLS-1$
            x.setEscapedValue("\'\\6\'");
            //$NON-NLS-1$
            x.setEscapedValue("\'\\7\'");
            //$NON-NLS-1$
            x.setEscapedValue("\'\\u0041\'");
            assertTrue(x.charValue() == 'A');
        } catch (IllegalArgumentException e) {
            assertTrue(false);
        }
        x.setCharValue('A');
        //$NON-NLS-1$
        assertTrue(x.getEscapedValue().equals("\'A\'"));
        x.setCharValue('\t');
        //$NON-NLS-1$
        assertTrue(x.getEscapedValue().equals("\'\\t\'"));
        //$NON-NLS-1$
        x.setEscapedValue("\'\\\\\'");
        //$NON-NLS-1$
        assertTrue(x.getEscapedValue().equals("\'\\\\\'"));
        assertTrue(x.charValue() == '\\');
        x.setEscapedValue("\'\\\'\'");
        assertTrue(x.getEscapedValue().equals("\'\\\'\'"));
        assertTrue(x.charValue() == '\'');
        x.setCharValue('\'');
        //$NON-NLS-1$
        assertTrue(x.getEscapedValue().equals("\'\\\'\'"));
        assertTrue(x.charValue() == '\'');
        x.setCharValue('\\');
        assertTrue(x.getEscapedValue().equals("\'\\\\\'"));
        assertTrue(x.charValue() == '\\');
        x.setCharValue('\b');
        assertTrue(x.getEscapedValue().equals("\'\\b\'"));
        x.setCharValue('\n');
        assertTrue(x.getEscapedValue().equals("\'\\n\'"));
        x.setCharValue('\f');
        assertTrue(x.getEscapedValue().equals("\'\\f\'"));
        x.setCharValue('\r');
        assertTrue(x.getEscapedValue().equals("\'\\r\'"));
        x.setCharValue('\"');
        assertTrue(x.getEscapedValue().equals("\'\"\'"));
        x.setCharValue('\0');
        assertTrue(x.getEscapedValue().equals("\'\\u0000\'"));
        x.setCharValue('\1');
        assertTrue(x.getEscapedValue().equals("\'\\u0001\'"));
        x.setCharValue('\2');
        assertTrue(x.getEscapedValue().equals("\'\\u0002\'"));
        x.setCharValue('\3');
        assertTrue(x.getEscapedValue().equals("\'\\u0003\'"));
        x.setCharValue('\4');
        assertTrue(x.getEscapedValue().equals("\'\\u0004\'"));
        x.setCharValue('\5');
        assertTrue(x.getEscapedValue().equals("\'\\u0005\'"));
        x.setCharValue('\6');
        assertTrue(x.getEscapedValue().equals("\'\\u0006\'"));
        x.setCharValue('\7');
        assertTrue(x.getEscapedValue().equals("\'\\u0007\'"));
        x.setCharValue('\33');
        assertTrue(x.getEscapedValue().equals("\'\\u001b\'"));
        x.setCharValue('\41');
        assertTrue(x.getEscapedValue().equals("\'!\'"));
    }

    public void testNumberLiteral() {
        long previousCount = this.ast.modificationCount();
        NumberLiteral x = this.ast.newNumberLiteral("1234");
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue("1234".equals(x.getToken()));
        assertTrue(x.getNodeType() == ASTNode.NUMBER_LITERAL);
        assertTrue(x.structuralPropertiesForType() == NumberLiteral.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        previousCount = this.ast.modificationCount();
        NumberLiteral y = this.ast.newNumberLiteral();
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(y.getAST() == this.ast);
        assertTrue(y.getParent() == null);
        assertTrue("0".equals(y.getToken()));
        final String[] samples = { "0", "1", "1234567890", "0L", "1L", "1234567890L", "0l", "1l", "1234567890l", "077", "0177", "012345670", "077L", "0177L", "012345670L", "077l", "0177l", "012345670l", "0x00", "0x1", "0x0123456789ABCDEF", "0x00L", "0x1L", "0x0123456789ABCDEFL", "0x00l", "0x1l", "0x0123456789ABCDEFl", "1e1f", "2.f", ".3f", "0f", "3.14f", "6.022137e+23f", "1e1", "2.", ".3", "0.0", "3.14", "1e-9d", "1e137" };
        for (int i = 0; i < samples.length; i++) {
            previousCount = this.ast.modificationCount();
            x.setToken(samples[i]);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(samples[i].equals(x.getToken()));
        }
        try {
            x.setToken(null);
            assertTrue(false);
        } catch (RuntimeException e) {
        }
    }

    public void testSimpleType() {
        long previousCount = this.ast.modificationCount();
        final SimpleType x = this.ast.newSimpleType(this.ast.newSimpleName("String"));
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.isSimpleType());
        assertTrue(!x.isArrayType());
        assertTrue(!x.isPrimitiveType());
        assertTrue(!x.isParameterizedType());
        assertTrue(!x.isQualifiedType());
        assertTrue(!x.isWildcardType());
        assertTrue(x.getNodeType() == ASTNode.SIMPLE_TYPE);
        assertTrue(x.structuralPropertiesForType() == SimpleType.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Name", true, Name.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("a");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((Name) value);
            }
        });
    }

    public void testPrimitiveType() {
        long previousCount = this.ast.modificationCount();
        PrimitiveType x = this.ast.newPrimitiveType(PrimitiveType.INT);
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getPrimitiveTypeCode().equals(PrimitiveType.INT));
        assertTrue(!x.isSimpleType());
        assertTrue(!x.isArrayType());
        assertTrue(x.isPrimitiveType());
        assertTrue(!x.isParameterizedType());
        assertTrue(!x.isQualifiedType());
        assertTrue(!x.isWildcardType());
        assertTrue(x.getNodeType() == ASTNode.PRIMITIVE_TYPE);
        assertTrue(x.structuralPropertiesForType() == PrimitiveType.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        assertTrue(PrimitiveType.BYTE.toString().equals("byte"));
        assertTrue(PrimitiveType.INT.toString().equals("int"));
        assertTrue(PrimitiveType.BOOLEAN.toString().equals("boolean"));
        assertTrue(PrimitiveType.CHAR.toString().equals("char"));
        assertTrue(PrimitiveType.SHORT.toString().equals("short"));
        assertTrue(PrimitiveType.LONG.toString().equals("long"));
        assertTrue(PrimitiveType.FLOAT.toString().equals("float"));
        assertTrue(PrimitiveType.DOUBLE.toString().equals("double"));
        assertTrue(PrimitiveType.VOID.toString().equals("void"));
        PrimitiveType.Code[] known = { PrimitiveType.BOOLEAN, PrimitiveType.BYTE, PrimitiveType.CHAR, PrimitiveType.INT, PrimitiveType.SHORT, PrimitiveType.LONG, PrimitiveType.FLOAT, PrimitiveType.DOUBLE, PrimitiveType.VOID };
        for (int i = 0; i < known.length; i++) {
            for (int j = 0; j < known.length; j++) {
                assertTrue(i == j || !known[i].equals(known[j]));
            }
        }
        for (int i = 0; i < known.length; i++) {
            previousCount = this.ast.modificationCount();
            x.setPrimitiveTypeCode(known[i]);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getPrimitiveTypeCode().equals(known[i]));
        }
        try {
            x.setPrimitiveTypeCode(null);
            assertTrue(false);
        } catch (RuntimeException e) {
        }
        for (int i = 0; i < known.length; i++) {
            String name = known[i].toString();
            assertTrue(PrimitiveType.toCode(name).equals(known[i]));
        }
        assertTrue(PrimitiveType.toCode("not-a-type") == null);
    }

    Type getArrayComponentType(ArrayType array) {
        return array.getComponentType();
    }

    void setArrayComponentType(ArrayType array, Type type) {
        array.setComponentType(type);
    }

    public void testArrayType() {
        SimpleName x1 = this.ast.newSimpleName("String");
        SimpleType x2 = this.ast.newSimpleType(x1);
        long previousCount = this.ast.modificationCount();
        final ArrayType x = this.ast.newArrayType(x2);
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        if (this.ast.apiLevel() < AST.JLS8) {
            assertTrue(getArrayComponentType(x).getParent() == x);
        } else {
            assertTrue(x.getElementType().getParent() == x);
        }
        assertTrue(this.ast.modificationCount() == previousCount);
        assertTrue(!x.isSimpleType());
        assertTrue(x.isArrayType());
        assertTrue(!x.isPrimitiveType());
        assertTrue(!x.isParameterizedType());
        assertTrue(!x.isQualifiedType());
        assertTrue(!x.isWildcardType());
        assertTrue(x.getNodeType() == ASTNode.ARRAY_TYPE);
        assertTrue(x.structuralPropertiesForType() == ArrayType.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(x.getDimensions() == 1);
        assertTrue(x.getElementType() == x2);
        if (this.ast.apiLevel() < AST.JLS8) {
            genericPropertyTest(x, new Property("ComponentType", true, Type.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("a"));
                    if (parented) {
                        targetAst.newArrayType(result);
                    }
                    return result;
                }

                public ASTNode wrap() {
                    ArrayType result = ASTTest.this.ast.newArrayType(x);
                    return result;
                }

                public void unwrap() {
                    ArrayType a = (ArrayType) x.getParent();
                    setArrayComponentType(a, ASTTest.this.ast.newPrimitiveType(PrimitiveType.INT));
                }

                public ASTNode get() {
                    return getArrayComponentType(x);
                }

                public void set(ASTNode value) {
                    setArrayComponentType(x, (Type) value);
                }
            });
            setArrayComponentType(x, this.ast.newArrayType(this.ast.newPrimitiveType(PrimitiveType.INT), 4));
            assertTrue(x.getDimensions() == 5);
            assertTrue(x.getElementType().isPrimitiveType());
            final ArrayType x3 = this.ast.newArrayType(x, 2);
            assertTrue(x3.getDimensions() == 7);
        } else {
            genericPropertyTest(x, new Property("ElementType", true, Type.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("a"));
                    if (parented) {
                        targetAst.newArrayType(result);
                    }
                    return result;
                }

                public ASTNode wrap() {
                    ArrayType result = ASTTest.this.ast.newArrayType(x, 5);
                    return result;
                }

                public void unwrap() {
                    ArrayType a = (ArrayType) x.getParent();
                    a.setElementType(ASTTest.this.ast.newPrimitiveType(PrimitiveType.INT));
                }

                public ASTNode get() {
                    return x.getElementType();
                }

                public void set(ASTNode value) {
                    x.setElementType((Type) value);
                }
            });
            x.setElementType(this.ast.newPrimitiveType(PrimitiveType.INT));
            assertTrue(x.getDimensions() == 1);
            assertTrue(x.getElementType().isPrimitiveType());
        }
        try {
            this.ast.newArrayType(null, 2);
        } catch (IllegalArgumentException e) {
        }
        try {
            this.ast.newArrayType(x, 0);
        } catch (IllegalArgumentException e) {
        }
        try {
            this.ast.newArrayType(x, 100000);
        } catch (IllegalArgumentException e) {
        }
    }

    public void testParameterizedType() {
        if (this.ast.apiLevel() == AST.JLS2) {
            try {
                this.ast.newParameterizedType(this.ast.newSimpleType(this.ast.newSimpleName("String")));
                assertTrue(false);
            } catch (UnsupportedOperationException e) {
            }
            return;
        }
        long previousCount = this.ast.modificationCount();
        Type t = this.ast.newSimpleType(this.ast.newSimpleName("String"));
        final ParameterizedType x = this.ast.newParameterizedType(t);
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getType() == t);
        assertTrue(x.getType().getParent() == x);
        assertTrue(!x.isSimpleType());
        assertTrue(!x.isArrayType());
        assertTrue(!x.isPrimitiveType());
        assertTrue(x.isParameterizedType());
        assertTrue(!x.isQualifiedType());
        assertTrue(!x.isWildcardType());
        assertTrue(x.getNodeType() == ASTNode.PARAMETERIZED_TYPE);
        assertTrue(x.typeArguments().size() == 0);
        assertTrue(x.structuralPropertiesForType() == ParameterizedType.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Type", true, Type.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("a"));
                if (parented) {
                    targetAst.newArrayType(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParameterizedType s1 = ASTTest.this.ast.newParameterizedType(x);
                return s1;
            }

            public void unwrap() {
                ParameterizedType s1 = (ParameterizedType) x.getParent();
                s1.setType(ASTTest.this.ast.newSimpleType(ASTTest.this.ast.newSimpleName("z")));
            }

            public ASTNode get() {
                return x.getType();
            }

            public void set(ASTNode value) {
                x.setType((Type) value);
            }
        });
        genericPropertyListTest(x, x.typeArguments(), new Property("Arguments", true, Type.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                PrimitiveType result = targetAst.newPrimitiveType(PrimitiveType.INT);
                if (parented) {
                    targetAst.newArrayType(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParameterizedType s1 = ASTTest.this.ast.newParameterizedType(ASTTest.this.ast.newSimpleType(ASTTest.this.ast.newSimpleName("foo")));
                s1.typeArguments().add(x);
                return s1;
            }

            public void unwrap() {
                ParameterizedType s1 = (ParameterizedType) x.getParent();
                s1.typeArguments().remove(x);
            }
        });
    }

    public void testQualifiedType() {
        if (this.ast.apiLevel() == AST.JLS2) {
            try {
                this.ast.newQualifiedType(this.ast.newSimpleType(this.ast.newSimpleName("q")), this.ast.newSimpleName("i"));
                assertTrue(false);
            } catch (UnsupportedOperationException e) {
            }
            return;
        }
        long previousCount = this.ast.modificationCount();
        final QualifiedType x = this.ast.newQualifiedType(this.ast.newSimpleType(this.ast.newSimpleName("q")), this.ast.newSimpleName("i"));
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getQualifier().getParent() == x);
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.getName().isDeclaration() == false);
        assertTrue(x.getNodeType() == ASTNode.QUALIFIED_TYPE);
        assertTrue(!x.isSimpleType());
        assertTrue(!x.isArrayType());
        assertTrue(!x.isPrimitiveType());
        assertTrue(!x.isParameterizedType());
        assertTrue(x.isQualifiedType());
        assertTrue(!x.isWildcardType());
        assertTrue(x.structuralPropertiesForType() == QualifiedType.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Qualifier", true, Type.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("a"));
                if (parented) {
                    targetAst.newArrayType(result);
                }
                return result;
            }

            public ASTNode wrap() {
                QualifiedType s1 = ASTTest.this.ast.newQualifiedType(x, ASTTest.this.ast.newSimpleName("z"));
                return s1;
            }

            public void unwrap() {
                QualifiedType s1 = (QualifiedType) x.getParent();
                s1.setQualifier(ASTTest.this.ast.newSimpleType(ASTTest.this.ast.newSimpleName("z")));
            }

            public ASTNode get() {
                return x.getQualifier();
            }

            public void set(ASTNode value) {
                x.setQualifier((Type) value);
            }
        });
        genericPropertyTest(x, new Property("Name", true, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
    }

    public void testWildcardType() {
        if (this.ast.apiLevel() == AST.JLS2) {
            try {
                this.ast.newWildcardType();
                assertTrue(false);
            } catch (UnsupportedOperationException e) {
            }
            return;
        }
        long previousCount = this.ast.modificationCount();
        final WildcardType x = this.ast.newWildcardType();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getBound() == null);
        assertTrue(x.isUpperBound() == true);
        assertTrue(x.getNodeType() == ASTNode.WILDCARD_TYPE);
        assertTrue(!x.isSimpleType());
        assertTrue(!x.isArrayType());
        assertTrue(!x.isPrimitiveType());
        assertTrue(!x.isParameterizedType());
        assertTrue(!x.isQualifiedType());
        assertTrue(x.isWildcardType());
        assertTrue(x.structuralPropertiesForType() == WildcardType.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        Type b = this.ast.newPrimitiveType(PrimitiveType.BYTE);
        x.setBound(b);
        x.setUpperBound(false);
        assertTrue(x.isUpperBound() == false);
        x.setUpperBound(true);
        assertTrue(x.isUpperBound() == true);
        x.setBound(null);
        x.setUpperBound(false);
        assertTrue(x.isUpperBound() == false);
        x.setUpperBound(true);
        assertTrue(x.isUpperBound() == true);
        x.setBound(b, false);
        assertTrue(x.getBound() == b);
        assertTrue(x.isUpperBound() == false);
        x.setBound(null, true);
        assertTrue(x.getBound() == null);
        assertTrue(x.isUpperBound() == true);
        x.setBound(b, true);
        assertTrue(x.getBound() == b);
        assertTrue(x.isUpperBound() == true);
        x.setBound(null, false);
        assertTrue(x.getBound() == null);
        assertTrue(x.isUpperBound() == false);
        genericPropertyTest(x, new Property("Bound", false, Type.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("a"));
                if (parented) {
                    targetAst.newArrayType(result);
                }
                return result;
            }

            public ASTNode wrap() {
                WildcardType s1 = ASTTest.this.ast.newWildcardType();
                s1.setBound(x);
                return s1;
            }

            public void unwrap() {
                WildcardType s1 = (WildcardType) x.getParent();
                s1.setBound(null);
            }

            public ASTNode get() {
                return x.getBound();
            }

            public void set(ASTNode value) {
                x.setBound((Type) value);
            }
        });
    }

    public void testPackageDeclaration() {
        long previousCount = this.ast.modificationCount();
        final PackageDeclaration x = this.ast.newPackageDeclaration();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            assertTrue(x.getJavadoc() == null);
            assertTrue(x.annotations().isEmpty());
        }
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.getNodeType() == ASTNode.PACKAGE_DECLARATION);
        assertTrue(x.structuralPropertiesForType() == PackageDeclaration.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            genericPropertyTest(x, new Property("Javadoc", false, Javadoc.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    Javadoc result = targetAst.newJavadoc();
                    if (parented) {
                        targetAst.newInitializer().setJavadoc(result);
                    }
                    return result;
                }

                public ASTNode get() {
                    return x.getJavadoc();
                }

                public void set(ASTNode value) {
                    x.setJavadoc((Javadoc) value);
                }
            });
            genericPropertyListTest(x, x.annotations(), new Property("Annotations", true, Annotation.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    MarkerAnnotation result = targetAst.newMarkerAnnotation();
                    if (parented) {
                        PackageDeclaration pd = targetAst.newPackageDeclaration();
                        pd.annotations().add(result);
                    }
                    return result;
                }
            });
        }
        genericPropertyTest(x, new Property("Name", true, Name.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("a");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((Name) value);
            }
        });
    }

    public void testImportDeclaration() {
        long previousCount = this.ast.modificationCount();
        final ImportDeclaration x = this.ast.newImportDeclaration();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.isOnDemand() == false);
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            assertTrue(x.isStatic() == false);
        }
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.getNodeType() == ASTNode.IMPORT_DECLARATION);
        assertTrue(x.structuralPropertiesForType() == ImportDeclaration.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Name", true, Name.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("a");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((Name) value);
            }
        });
        previousCount = this.ast.modificationCount();
        x.setOnDemand(false);
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.isOnDemand() == false);
        previousCount = this.ast.modificationCount();
        x.setOnDemand(true);
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.isOnDemand() == true);
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            x.setStatic(true);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.isStatic() == true);
        }
    }

    public void testCompilationUnit() {
        long previousCount = this.ast.modificationCount();
        final CompilationUnit x = this.ast.newCompilationUnit();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getPackage() == null);
        assertTrue(x.imports().size() == 0);
        assertTrue(x.types().size() == 0);
        assertTrue(x.getNodeType() == ASTNode.COMPILATION_UNIT);
        assertTrue(x.structuralPropertiesForType() == CompilationUnit.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tClientProperties(x);
        genericPropertyTest(x, new Property("Package", false, PackageDeclaration.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                PackageDeclaration result = targetAst.newPackageDeclaration();
                if (parented) {
                    CompilationUnit cu = targetAst.newCompilationUnit();
                    cu.setPackage(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getPackage();
            }

            public void set(ASTNode value) {
                x.setPackage((PackageDeclaration) value);
            }
        });
        genericPropertyListTest(x, x.imports(), new Property("Imports", true, ImportDeclaration.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                ImportDeclaration result = targetAst.newImportDeclaration();
                if (parented) {
                    CompilationUnit cu = targetAst.newCompilationUnit();
                    cu.imports().add(result);
                }
                return result;
            }
        });
        genericPropertyListTest(x, x.types(), new Property("Types", true, AbstractTypeDeclaration.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                TypeDeclaration result = targetAst.newTypeDeclaration();
                if (parented) {
                    CompilationUnit cu = targetAst.newCompilationUnit();
                    cu.types().add(result);
                }
                return result;
            }
        });
        TypeDeclaration t1 = this.ast.newTypeDeclaration();
        x.types().add(t1);
        assertTrue(t1.isLocalTypeDeclaration() == false);
        assertTrue(t1.isMemberTypeDeclaration() == false);
        assertTrue(t1.isPackageMemberTypeDeclaration() == true);
    }

    public void testCompilationUnitLineNumberTable() {
    }

    public void testTypeDeclaration() {
        long previousCount = this.ast.modificationCount();
        final TypeDeclaration x = this.ast.newTypeDeclaration();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        if (this.ast.apiLevel() == AST.JLS2) {
            assertTrue(x.getModifiers() == Modifier.NONE);
            assertTrue(x.getSuperclass() == null);
            assertTrue(x.superInterfaces().size() == 0);
        } else {
            assertTrue(x.modifiers().size() == 0);
            assertTrue(x.typeParameters().size() == 0);
            assertTrue(x.getSuperclassType() == null);
            assertTrue(x.superInterfaceTypes().size() == 0);
        }
        assertTrue(x.isInterface() == false);
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.getName().isDeclaration() == true);
        assertTrue(x.getJavadoc() == null);
        assertTrue(x.bodyDeclarations().size() == 0);
        assertTrue(x.getNodeType() == ASTNode.TYPE_DECLARATION);
        assertTrue(x.structuralPropertiesForType() == TypeDeclaration.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        previousCount = this.ast.modificationCount();
        x.setInterface(true);
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.isInterface() == true);
        if (this.ast.apiLevel() == AST.JLS2) {
            int legal = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE | Modifier.ABSTRACT | Modifier.STATIC | Modifier.FINAL | Modifier.STRICTFP;
            previousCount = this.ast.modificationCount();
            x.setModifiers(legal);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getModifiers() == legal);
            previousCount = this.ast.modificationCount();
            x.setModifiers(Modifier.NONE);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getModifiers() == Modifier.NONE);
        }
        tJavadocComment(x);
        tModifiers(x);
        genericPropertyTest(x, new Property("Name", true, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            genericPropertyListTest(x, x.typeParameters(), new Property("TypeParameters", true, TypeParameter.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    TypeParameter result = targetAst.newTypeParameter();
                    if (parented) {
                        targetAst.newMethodDeclaration().typeParameters().add(result);
                    }
                    return result;
                }
            });
        }
        if (this.ast.apiLevel() == AST.JLS2) {
            genericPropertyTest(x, new Property("Superclass", false, Name.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    SimpleName result = targetAst.newSimpleName("foo");
                    if (parented) {
                        targetAst.newExpressionStatement(result);
                    }
                    return result;
                }

                public ASTNode get() {
                    return x.getSuperclass();
                }

                public void set(ASTNode value) {
                    x.setSuperclass((Name) value);
                }
            });
        }
        if (this.ast.apiLevel() == AST.JLS2) {
            genericPropertyListTest(x, x.superInterfaces(), new Property("SuperInterfaces", true, Name.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    SimpleName result = targetAst.newSimpleName("foo");
                    if (parented) {
                        targetAst.newExpressionStatement(result);
                    }
                    return result;
                }
            });
        }
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            genericPropertyTest(x, new Property("SuperclassType", false, Type.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("foo"));
                    if (parented) {
                        targetAst.newArrayType(result);
                    }
                    return result;
                }

                public ASTNode get() {
                    return x.getSuperclassType();
                }

                public void set(ASTNode value) {
                    x.setSuperclassType((Type) value);
                }
            });
        }
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            genericPropertyListTest(x, x.superInterfaceTypes(), new Property("SuperInterfaceTypes", true, Type.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("foo"));
                    if (parented) {
                        targetAst.newArrayType(result);
                    }
                    return result;
                }
            });
        }
        genericPropertyListTest(x, x.bodyDeclarations(), new Property("BodyDeclarations", true, BodyDeclaration.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                TypeDeclaration result = targetAst.newTypeDeclaration();
                if (parented) {
                    CompilationUnit cu = targetAst.newCompilationUnit();
                    cu.types().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                TypeDeclaration s1 = x.getAST().newTypeDeclaration();
                s1.bodyDeclarations().add(x);
                return s1;
            }

            public void unwrap() {
                TypeDeclaration s1 = (TypeDeclaration) x.getParent();
                s1.bodyDeclarations().remove(x);
            }
        });
        x.bodyDeclarations().clear();
        FieldDeclaration f1 = this.ast.newFieldDeclaration(this.ast.newVariableDeclarationFragment());
        FieldDeclaration f2 = this.ast.newFieldDeclaration(this.ast.newVariableDeclarationFragment());
        MethodDeclaration m1 = this.ast.newMethodDeclaration();
        MethodDeclaration m2 = this.ast.newMethodDeclaration();
        TypeDeclaration t1 = this.ast.newTypeDeclaration();
        TypeDeclaration t2 = this.ast.newTypeDeclaration();
        EnumConstantDeclaration c1 = null;
        EnumConstantDeclaration c2 = null;
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            c1 = this.ast.newEnumConstantDeclaration();
            c2 = this.ast.newEnumConstantDeclaration();
            x.bodyDeclarations().add(c1);
            x.bodyDeclarations().add(c2);
        }
        x.bodyDeclarations().add(this.ast.newInitializer());
        x.bodyDeclarations().add(f1);
        x.bodyDeclarations().add(this.ast.newInitializer());
        x.bodyDeclarations().add(f2);
        x.bodyDeclarations().add(this.ast.newInitializer());
        x.bodyDeclarations().add(t1);
        x.bodyDeclarations().add(this.ast.newInitializer());
        x.bodyDeclarations().add(m1);
        x.bodyDeclarations().add(this.ast.newInitializer());
        x.bodyDeclarations().add(m2);
        x.bodyDeclarations().add(this.ast.newInitializer());
        x.bodyDeclarations().add(t2);
        x.bodyDeclarations().add(this.ast.newInitializer());
        List fs = Arrays.asList(x.getFields());
        assertTrue(fs.size() == 2);
        assertTrue(fs.contains(f1));
        assertTrue(fs.contains(f2));
        List ms = Arrays.asList(x.getMethods());
        assertTrue(ms.size() == 2);
        assertTrue(ms.contains(m1));
        assertTrue(ms.contains(m2));
        List ts = Arrays.asList(x.getTypes());
        assertTrue(ts.size() == 2);
        assertTrue(ts.contains(t1));
        assertTrue(ts.contains(t2));
        assertTrue(t1.isLocalTypeDeclaration() == false);
        assertTrue(t1.isMemberTypeDeclaration() == true);
        assertTrue(t1.isPackageMemberTypeDeclaration() == false);
    }

    public void testEnumDeclaration() {
        if (this.ast.apiLevel() == AST.JLS2) {
            try {
                this.ast.newEnumDeclaration();
                assertTrue(false);
            } catch (UnsupportedOperationException e) {
            }
            return;
        }
        long previousCount = this.ast.modificationCount();
        final EnumDeclaration x = this.ast.newEnumDeclaration();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.modifiers().size() == 0);
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.getName().isDeclaration() == true);
        assertTrue(x.getJavadoc() == null);
        assertTrue(x.superInterfaceTypes().size() == 0);
        assertTrue(x.enumConstants().size() == 0);
        assertTrue(x.bodyDeclarations().size() == 0);
        assertTrue(x.getNodeType() == ASTNode.ENUM_DECLARATION);
        assertTrue(x.structuralPropertiesForType() == EnumDeclaration.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        previousCount = this.ast.modificationCount();
        tJavadocComment(x);
        tModifiers(x);
        genericPropertyTest(x, new Property("Name", true, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
        genericPropertyListTest(x, x.superInterfaceTypes(), new Property("SuperInterfaceTypes", true, Type.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("foo"));
                if (parented) {
                    targetAst.newArrayType(result);
                }
                return result;
            }
        });
        genericPropertyListTest(x, x.enumConstants(), new Property("EnumConstants", true, EnumConstantDeclaration.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                EnumConstantDeclaration result = targetAst.newEnumConstantDeclaration();
                if (parented) {
                    TypeDeclaration d = targetAst.newTypeDeclaration();
                    d.bodyDeclarations().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                EnumConstantDeclaration s1 = x.getAST().newEnumConstantDeclaration();
                AnonymousClassDeclaration anonymousClassDeclaration = x.getAST().newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(anonymousClassDeclaration);
                anonymousClassDeclaration.bodyDeclarations().add(x);
                return s1;
            }

            public void unwrap() {
                AnonymousClassDeclaration anonymousClassDeclaration = (AnonymousClassDeclaration) x.getParent();
                if (anonymousClassDeclaration != null) {
                    anonymousClassDeclaration.bodyDeclarations().remove(x);
                }
            }
        });
        genericPropertyListTest(x, x.bodyDeclarations(), new Property("BodyDeclarations", true, BodyDeclaration.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                TypeDeclaration result = targetAst.newTypeDeclaration();
                if (parented) {
                    CompilationUnit cu = targetAst.newCompilationUnit();
                    cu.types().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                TypeDeclaration s1 = x.getAST().newTypeDeclaration();
                s1.bodyDeclarations().add(x);
                return s1;
            }

            public void unwrap() {
                TypeDeclaration s1 = (TypeDeclaration) x.getParent();
                s1.bodyDeclarations().remove(x);
            }
        });
        x.bodyDeclarations().clear();
        EnumConstantDeclaration c1 = this.ast.newEnumConstantDeclaration();
        EnumConstantDeclaration c2 = this.ast.newEnumConstantDeclaration();
        FieldDeclaration f1 = this.ast.newFieldDeclaration(this.ast.newVariableDeclarationFragment());
        FieldDeclaration f2 = this.ast.newFieldDeclaration(this.ast.newVariableDeclarationFragment());
        MethodDeclaration m1 = this.ast.newMethodDeclaration();
        MethodDeclaration m2 = this.ast.newMethodDeclaration();
        TypeDeclaration t1 = this.ast.newTypeDeclaration();
        TypeDeclaration t2 = this.ast.newTypeDeclaration();
        x.enumConstants().add(c1);
        x.enumConstants().add(c2);
        x.bodyDeclarations().add(f1);
        x.bodyDeclarations().add(f2);
        x.bodyDeclarations().add(m1);
        x.bodyDeclarations().add(m2);
        x.bodyDeclarations().add(t1);
        x.bodyDeclarations().add(t2);
        assertTrue(t1.isLocalTypeDeclaration() == false);
        assertTrue(t1.isMemberTypeDeclaration() == true);
        assertTrue(t1.isPackageMemberTypeDeclaration() == false);
    }

    public void testEnumConstantDeclaration() {
        if (this.ast.apiLevel() == AST.JLS2) {
            try {
                this.ast.newEnumConstantDeclaration();
                assertTrue(false);
            } catch (UnsupportedOperationException e) {
            }
            return;
        }
        long previousCount = this.ast.modificationCount();
        final EnumConstantDeclaration x = this.ast.newEnumConstantDeclaration();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.getName().isDeclaration() == true);
        assertTrue(x.getJavadoc() == null);
        assertTrue(x.arguments().size() == 0);
        assertTrue(x.getAnonymousClassDeclaration() == null);
        assertTrue(x.modifiers().size() == 0);
        assertTrue(x.getNodeType() == ASTNode.ENUM_CONSTANT_DECLARATION);
        assertTrue(x.structuralPropertiesForType() == EnumConstantDeclaration.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tJavadocComment(x);
        tModifiers(x);
        genericPropertyTest(x, new Property("Name", true, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
        genericPropertyListTest(x, x.arguments(), new Property("Arguments", true, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                AnonymousClassDeclaration s1 = x.getAST().newAnonymousClassDeclaration();
                s1.bodyDeclarations().add(x);
                return s1;
            }

            public void unwrap() {
                AnonymousClassDeclaration s1 = (AnonymousClassDeclaration) x.getParent();
                s1.bodyDeclarations().remove(x);
            }
        });
        genericPropertyTest(x, new Property("AnonymousClassDeclaration", false, AnonymousClassDeclaration.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                AnonymousClassDeclaration result = targetAst.newAnonymousClassDeclaration();
                if (parented) {
                    targetAst.newClassInstanceCreation().setAnonymousClassDeclaration(result);
                }
                return result;
            }

            public ASTNode wrap() {
                AnonymousClassDeclaration s0 = x.getAST().newAnonymousClassDeclaration();
                EnumDeclaration s1 = x.getAST().newEnumDeclaration();
                s0.bodyDeclarations().add(s1);
                s1.bodyDeclarations().add(x);
                return s0;
            }

            public void unwrap() {
                EnumDeclaration s1 = (EnumDeclaration) x.getParent();
                s1.bodyDeclarations().remove(x);
            }

            public ASTNode get() {
                return x.getAnonymousClassDeclaration();
            }

            public void set(ASTNode value) {
                x.setAnonymousClassDeclaration((AnonymousClassDeclaration) value);
            }
        });
        x.setAnonymousClassDeclaration(null);
        AnonymousClassDeclaration w0 = this.ast.newAnonymousClassDeclaration();
        x.setAnonymousClassDeclaration(w0);
        TypeDeclaration w1 = this.ast.newTypeDeclaration();
        w0.bodyDeclarations().add(w1);
        assertTrue(w1.isLocalTypeDeclaration() == false);
        assertTrue(w1.isMemberTypeDeclaration() == true);
        assertTrue(w1.isPackageMemberTypeDeclaration() == false);
    }

    public void testTypeParameter() {
        if (this.ast.apiLevel() == AST.JLS2) {
            try {
                this.ast.newTypeParameter();
                assertTrue(false);
            } catch (UnsupportedOperationException e) {
            }
            return;
        }
        long previousCount = this.ast.modificationCount();
        final TypeParameter x = this.ast.newTypeParameter();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.getNodeType() == ASTNode.TYPE_PARAMETER);
        assertTrue(x.typeBounds().size() == 0);
        assertTrue(x.structuralPropertiesForType() == TypeParameter.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Name", true, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("a");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
        genericPropertyListTest(x, x.typeBounds(), new Property("TypeBounds", true, Type.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                Type result = targetAst.newSimpleType(targetAst.newSimpleName("foo"));
                if (parented) {
                    targetAst.newArrayType(result);
                }
                return result;
            }
        });
    }

    public void testSingleVariableDeclaration() {
        long previousCount = this.ast.modificationCount();
        final SingleVariableDeclaration x = this.ast.newSingleVariableDeclaration();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        if (this.ast.apiLevel() == AST.JLS2) {
            assertTrue(x.getModifiers() == Modifier.NONE);
        } else {
            assertTrue(x.modifiers().size() == 0);
            assertTrue(x.isVarargs() == false);
        }
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.getName().isDeclaration() == true);
        assertTrue(x.getType().getParent() == x);
        assertTrue(x.getExtraDimensions() == 0);
        assertTrue(x.getInitializer() == null);
        assertTrue(x.getNodeType() == ASTNode.SINGLE_VARIABLE_DECLARATION);
        assertTrue(x.structuralPropertiesForType() == SingleVariableDeclaration.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        if (this.ast.apiLevel() == AST.JLS2) {
            int legal = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL | Modifier.TRANSIENT | Modifier.VOLATILE;
            previousCount = this.ast.modificationCount();
            x.setModifiers(legal);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getModifiers() == legal);
            previousCount = this.ast.modificationCount();
            x.setModifiers(Modifier.NONE);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getModifiers() == Modifier.NONE);
        }
        previousCount = this.ast.modificationCount();
        if (this.ast.apiLevel() < AST.JLS8) {
            x.setExtraDimensions(1);
        } else {
            x.extraDimensions().add(this.ast.newDimension());
        }
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.getExtraDimensions() == 1);
        previousCount = this.ast.modificationCount();
        if (this.ast.apiLevel() < AST.JLS8) {
            x.setExtraDimensions(0);
        } else {
            x.extraDimensions().remove(0);
        }
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.getExtraDimensions() == 0);
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            previousCount = this.ast.modificationCount();
            x.setVarargs(true);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.isVarargs() == true);
            previousCount = this.ast.modificationCount();
            x.setVarargs(false);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.isVarargs() == false);
        }
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            genericPropertyListTest(x, x.modifiers(), new Property("Modifiers", true, IExtendedModifier.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    Modifier result = targetAst.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
                    if (parented) {
                        TypeDeclaration pd = targetAst.newTypeDeclaration();
                        pd.modifiers().add(result);
                    }
                    return result;
                }

                public ASTNode wrap() {
                    SingleMemberAnnotation s1 = x.getAST().newSingleMemberAnnotation();
                    ClassInstanceCreation s2 = x.getAST().newClassInstanceCreation();
                    AnonymousClassDeclaration s3 = x.getAST().newAnonymousClassDeclaration();
                    MethodDeclaration s4 = x.getAST().newMethodDeclaration();
                    SingleVariableDeclaration s5 = x.getAST().newSingleVariableDeclaration();
                    s1.setValue(s2);
                    s2.setAnonymousClassDeclaration(s3);
                    s3.bodyDeclarations().add(s4);
                    s4.parameters().add(s5);
                    s5.modifiers().add(x);
                    return s1;
                }

                public void unwrap() {
                    SingleVariableDeclaration s5 = (SingleVariableDeclaration) x.getParent();
                    s5.modifiers().remove(x);
                }
            });
            x.modifiers().clear();
            assertTrue(x.getModifiers() == Modifier.NONE);
            Modifier[] allMods = allModifiers();
            for (int i = 0; i < allMods.length; i++) {
                x.modifiers().add(allMods[i]);
                assertTrue(x.getModifiers() == allMods[i].getKeyword().toFlagValue());
                x.modifiers().remove(allMods[i]);
                assertTrue(x.getModifiers() == Modifier.NONE);
            }
            for (int i = 0; i < allMods.length; i++) {
                x.modifiers().add(allMods[i]);
            }
            int flags = x.getModifiers();
            for (int i = 0; i < allMods.length; i++) {
                assertTrue((flags & allMods[i].getKeyword().toFlagValue()) != 0);
            }
        }
        genericPropertyTest(x, new Property("Name", true, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
        genericPropertyTest(x, new Property("Type", true, Type.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("foo"));
                if (parented) {
                    targetAst.newArrayType(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getType();
            }

            public void set(ASTNode value) {
                x.setType((Type) value);
            }
        });
        if (this.ast.apiLevel() >= AST.JLS8) {
            genericPropertyListTest(x, x.extraDimensions(), new Property("ExtraDimensions", true, Dimension.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    Dimension result = targetAst.newDimension();
                    if (parented) {
                        targetAst.newMethodDeclaration().extraDimensions().add(result);
                    }
                    return result;
                }
            });
        }
        genericPropertyTest(x, new Property("Initializer", false, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                CatchClause s1 = ASTTest.this.ast.newCatchClause();
                s1.setException(x);
                return s1;
            }

            public void unwrap() {
                CatchClause s1 = (CatchClause) x.getParent();
                s1.setException(ASTTest.this.ast.newSingleVariableDeclaration());
            }

            public ASTNode get() {
                return x.getInitializer();
            }

            public void set(ASTNode value) {
                x.setInitializer((Expression) value);
            }
        });
    }

    public void testVariableDeclarationFragment() {
        long previousCount = this.ast.modificationCount();
        final VariableDeclarationFragment x = this.ast.newVariableDeclarationFragment();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.getName().isDeclaration() == true);
        assertTrue(x.getExtraDimensions() == 0);
        assertTrue(x.getInitializer() == null);
        assertTrue(x.getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT);
        assertTrue(x.structuralPropertiesForType() == VariableDeclarationFragment.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        previousCount = this.ast.modificationCount();
        if (this.ast.apiLevel() < AST.JLS8) {
            setExtraDimensions(x, 1);
        } else {
            x.extraDimensions().add(this.ast.newDimension());
        }
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.getExtraDimensions() == 1);
        previousCount = this.ast.modificationCount();
        if (this.ast.apiLevel() < AST.JLS8) {
            setExtraDimensions(x, 0);
        } else {
            x.extraDimensions().remove(0);
        }
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.getExtraDimensions() == 0);
        if (this.ast.apiLevel() < AST.JLS8) {
            try {
                setExtraDimensions(x, -1);
                fail();
            } catch (IllegalArgumentException e) {
            }
        }
        genericPropertyTest(x, new Property("Name", true, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
        if (this.ast.apiLevel() >= AST.JLS8) {
            genericPropertyListTest(x, x.extraDimensions(), new Property("ExtraDimensions", true, Dimension.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    Dimension result = targetAst.newDimension();
                    if (parented) {
                        targetAst.newMethodDeclaration().extraDimensions().add(result);
                    }
                    return result;
                }
            });
        }
        genericPropertyTest(x, new Property("Initializer", false, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                VariableDeclarationExpression s1 = ASTTest.this.ast.newVariableDeclarationExpression(x);
                return s1;
            }

            public void unwrap() {
                VariableDeclarationExpression s1 = (VariableDeclarationExpression) x.getParent();
                s1.fragments().remove(x);
            }

            public ASTNode get() {
                return x.getInitializer();
            }

            public void set(ASTNode value) {
                x.setInitializer((Expression) value);
            }
        });
    }

    public void testMethodDeclaration() {
        long previousCount = this.ast.modificationCount();
        final MethodDeclaration x = this.ast.newMethodDeclaration();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        if (this.ast.apiLevel() == AST.JLS2) {
            assertTrue(x.getModifiers() == Modifier.NONE);
            assertTrue(x.getReturnType().getParent() == x);
            assertTrue(x.getReturnType().isPrimitiveType());
            assertTrue(((PrimitiveType) x.getReturnType()).getPrimitiveTypeCode() == PrimitiveType.VOID);
            try {
                x.typeParameters();
                assertTrue("Should have failed", false);
            } catch (UnsupportedOperationException e) {
            }
            try {
                x.isVarargs();
                assertTrue("Should have failed", false);
            } catch (UnsupportedOperationException e) {
            }
        } else {
            assertTrue(x.modifiers().size() == 0);
            assertTrue(x.typeParameters().size() == 0);
            assertTrue(x.getReturnType2().getParent() == x);
            assertTrue(x.getReturnType2().isPrimitiveType());
            assertTrue(((PrimitiveType) x.getReturnType2()).getPrimitiveTypeCode() == PrimitiveType.VOID);
        }
        assertTrue(x.isConstructor() == false);
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.getName().isDeclaration() == true);
        assertTrue(x.getExtraDimensions() == 0);
        assertTrue(x.getJavadoc() == null);
        assertTrue(x.parameters().size() == 0);
        if (this.ast.apiLevel() < AST.JLS8) {
            assertTrue(x.thrownExceptions().size() == 0);
        } else {
            assertTrue(x.thrownExceptionTypes().size() == 0);
        }
        assertTrue(x.getBody() == null);
        assertTrue(x.getNodeType() == ASTNode.METHOD_DECLARATION);
        assertTrue(x.structuralPropertiesForType() == MethodDeclaration.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        previousCount = this.ast.modificationCount();
        x.setConstructor(true);
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.isConstructor() == true);
        assertTrue(x.getName().isDeclaration() == false);
        previousCount = this.ast.modificationCount();
        x.setConstructor(false);
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.isConstructor() == false);
        if (this.ast.apiLevel() == AST.JLS2) {
            previousCount = this.ast.modificationCount();
            int legal = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE | Modifier.ABSTRACT | Modifier.STATIC | Modifier.FINAL | Modifier.SYNCHRONIZED | Modifier.NATIVE | Modifier.STRICTFP;
            x.setModifiers(legal);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getModifiers() == legal);
            previousCount = this.ast.modificationCount();
            x.setModifiers(Modifier.NONE);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getModifiers() == Modifier.NONE);
        }
        previousCount = this.ast.modificationCount();
        if (this.ast.apiLevel() < AST.JLS8) {
            x.setExtraDimensions(1);
        } else {
            x.extraDimensions().add(this.ast.newDimension());
        }
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.getExtraDimensions() == 1);
        previousCount = this.ast.modificationCount();
        if (this.ast.apiLevel() < AST.JLS8) {
            x.setExtraDimensions(0);
        } else {
            x.extraDimensions().remove(0);
        }
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.getExtraDimensions() == 0);
        tJavadocComment(x);
        tModifiers(x);
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            genericPropertyListTest(x, x.typeParameters(), new Property("TypeParameters", true, TypeParameter.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    TypeParameter result = targetAst.newTypeParameter();
                    if (parented) {
                        targetAst.newMethodDeclaration().typeParameters().add(result);
                    }
                    return result;
                }
            });
        }
        genericPropertyTest(x, new Property("Name", true, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
        if (this.ast.apiLevel() == AST.JLS2) {
            genericPropertyTest(x, new Property("ReturnType", true, Type.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("foo"));
                    if (parented) {
                        targetAst.newArrayType(result);
                    }
                    return result;
                }

                public ASTNode get() {
                    return x.getReturnType();
                }

                public void set(ASTNode value) {
                    x.setReturnType((Type) value);
                }
            });
        }
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            genericPropertyTest(x, new Property("ReturnType2", false, Type.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("foo"));
                    if (parented) {
                        targetAst.newArrayType(result);
                    }
                    return result;
                }

                public ASTNode get() {
                    return x.getReturnType2();
                }

                public void set(ASTNode value) {
                    x.setReturnType2((Type) value);
                }
            });
        }
        if (this.ast.apiLevel() >= AST.JLS8) {
            genericPropertyListTest(x, x.extraDimensions(), new Property("ExtraDimensions", true, Dimension.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    Dimension result = targetAst.newDimension();
                    if (parented) {
                        targetAst.newMethodDeclaration().extraDimensions().add(result);
                    }
                    return result;
                }
            });
        }
        genericPropertyListTest(x, x.parameters(), new Property("Parameters", true, SingleVariableDeclaration.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SingleVariableDeclaration result = targetAst.newSingleVariableDeclaration();
                if (parented) {
                    targetAst.newCatchClause().setException(result);
                }
                return result;
            }

            public ASTNode wrap() {
                SingleVariableDeclaration s1 = ASTTest.this.ast.newSingleVariableDeclaration();
                ClassInstanceCreation s2 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s2.setAnonymousClassDeclaration(a1);
                s1.setInitializer(s2);
                a1.bodyDeclarations().add(x);
                return s1;
            }

            public void unwrap() {
                AnonymousClassDeclaration a1 = (AnonymousClassDeclaration) x.getParent();
                a1.bodyDeclarations().remove(x);
            }
        });
        if (this.ast.apiLevel() < AST.JLS8) {
            genericPropertyListTest(x, x.thrownExceptions(), new Property("ThrownExceptions", true, Name.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    SimpleName result = targetAst.newSimpleName("foo");
                    if (parented) {
                        targetAst.newExpressionStatement(result);
                    }
                    return result;
                }
            });
        } else {
            genericPropertyListTest(x, x.thrownExceptionTypes(), new Property("ThrownExceptionTypes", true, Type.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    Type result = targetAst.newSimpleType(targetAst.newSimpleName("foo"));
                    if (parented) {
                        targetAst.newArrayType(result);
                    }
                    return result;
                }
            });
        }
        genericPropertyTest(x, new Property("Body", false, Block.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                Block result = targetAst.newBlock();
                if (parented) {
                    Block b2 = targetAst.newBlock();
                    b2.statements().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                Block s1 = ASTTest.this.ast.newBlock();
                TypeDeclaration s2 = ASTTest.this.ast.newTypeDeclaration();
                s1.statements().add(ASTTest.this.ast.newTypeDeclarationStatement(s2));
                s2.bodyDeclarations().add(x);
                return s1;
            }

            public void unwrap() {
                TypeDeclaration s2 = (TypeDeclaration) x.getParent();
                s2.bodyDeclarations().remove(x);
            }

            public ASTNode get() {
                return x.getBody();
            }

            public void set(ASTNode value) {
                x.setBody((Block) value);
            }
        });
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            x.parameters().clear();
            assertTrue(!x.isVarargs());
            x.parameters().add(this.ast.newSingleVariableDeclaration());
            assertTrue(!x.isVarargs());
            SingleVariableDeclaration v = this.ast.newSingleVariableDeclaration();
            x.parameters().add(v);
            assertTrue(!x.isVarargs());
            v.setVarargs(true);
            assertTrue(x.isVarargs());
            x.parameters().add(this.ast.newSingleVariableDeclaration());
            assertTrue(!x.isVarargs());
        }
        if (this.ast.apiLevel() < AST.JLS8) {
            try {
                x.setExtraDimensions(-1);
                fail("Should fail");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    public void testInitializer() {
        long previousCount = this.ast.modificationCount();
        final Initializer x = this.ast.newInitializer();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getJavadoc() == null);
        if (this.ast.apiLevel() == AST.JLS2) {
            assertTrue(x.getModifiers() == Modifier.NONE);
        } else {
            assertTrue(x.modifiers().size() == 0);
        }
        assertTrue(x.getBody().getParent() == x);
        assertTrue(x.getBody().statements().size() == 0);
        assertTrue(x.getNodeType() == ASTNode.INITIALIZER);
        assertTrue(x.structuralPropertiesForType() == Initializer.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tJavadocComment(x);
        tModifiers(x);
        if (this.ast.apiLevel() == AST.JLS2) {
            int legal = Modifier.STATIC;
            previousCount = this.ast.modificationCount();
            x.setModifiers(legal);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getModifiers() == legal);
            previousCount = this.ast.modificationCount();
            x.setModifiers(Modifier.NONE);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getModifiers() == Modifier.NONE);
        }
        genericPropertyTest(x, new Property("Body", true, Block.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                Block result = targetAst.newBlock();
                if (parented) {
                    Block b2 = targetAst.newBlock();
                    b2.statements().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                Block s1 = ASTTest.this.ast.newBlock();
                TypeDeclaration s2 = ASTTest.this.ast.newTypeDeclaration();
                s1.statements().add(ASTTest.this.ast.newTypeDeclarationStatement(s2));
                s2.bodyDeclarations().add(x);
                return s1;
            }

            public void unwrap() {
                TypeDeclaration s2 = (TypeDeclaration) x.getParent();
                s2.bodyDeclarations().remove(x);
            }

            public ASTNode get() {
                return x.getBody();
            }

            public void set(ASTNode value) {
                x.setBody((Block) value);
            }
        });
    }

    public void testJavadoc() {
        long previousCount = this.ast.modificationCount();
        final Javadoc x = this.ast.newJavadoc();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        if (this.ast.apiLevel() == AST.JLS2) {
            assertTrue(x.getComment().startsWith("/**"));
            assertTrue(x.getComment().endsWith("*/"));
        }
        assertTrue(x.getNodeType() == ASTNode.JAVADOC);
        assertTrue(!x.isBlockComment());
        assertTrue(!x.isLineComment());
        assertTrue(x.isDocComment());
        assertTrue(x.tags().isEmpty());
        assertTrue(x.getAlternateRoot() == null);
        assertTrue(x.structuralPropertiesForType() == Javadoc.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        assertTrue(TagElement.TAG_AUTHOR.equals("@author"));
        assertTrue(TagElement.TAG_DEPRECATED.equals("@deprecated"));
        assertTrue(TagElement.TAG_DOCROOT.equals("@docRoot"));
        assertTrue(TagElement.TAG_EXCEPTION.equals("@exception"));
        assertTrue(TagElement.TAG_INHERITDOC.equals("@inheritDoc"));
        assertTrue(TagElement.TAG_LINK.equals("@link"));
        assertTrue(TagElement.TAG_LINKPLAIN.equals("@linkplain"));
        assertTrue(TagElement.TAG_PARAM.equals("@param"));
        assertTrue(TagElement.TAG_RETURN.equals("@return"));
        assertTrue(TagElement.TAG_SEE.equals("@see"));
        assertTrue(TagElement.TAG_SERIAL.equals("@serial"));
        assertTrue(TagElement.TAG_SERIALDATA.equals("@serialData"));
        assertTrue(TagElement.TAG_SERIALFIELD.equals("@serialField"));
        assertTrue(TagElement.TAG_SINCE.equals("@since"));
        assertTrue(TagElement.TAG_THROWS.equals("@throws"));
        assertTrue(TagElement.TAG_VALUE.equals("@value"));
        assertTrue(TagElement.TAG_VERSION.equals("@version"));
        if (this.ast.apiLevel() == AST.JLS2) {
            final String[] samples = { "/** Hello there */", "/**\n * Line 1\n * Line 2\n */", "/***/" };
            for (int i = 0; i < samples.length; i++) {
                previousCount = this.ast.modificationCount();
                x.setComment(samples[i]);
                assertTrue(this.ast.modificationCount() > previousCount);
                assertTrue(samples[i].equals(x.getComment()));
            }
            final String[] badSamples = { null, "", "/* */", "/**", "*/" };
            for (int i = 0; i < badSamples.length; i++) {
                try {
                    x.setComment(badSamples[i]);
                    assertTrue(false);
                } catch (RuntimeException e) {
                }
            }
        }
        tAlternateRoot(x);
        genericPropertyListTest(x, x.tags(), new Property("Tags", true, TagElement.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                TagElement result = targetAst.newTagElement();
                if (parented) {
                    Javadoc parent = targetAst.newJavadoc();
                    parent.tags().add(result);
                }
                return result;
            }

            public ASTNode[] counterExamples(AST targetAst) {
                return new ASTNode[] { targetAst.newEmptyStatement(), targetAst.newCompilationUnit(), targetAst.newTypeDeclaration(), targetAst.newJavadoc(), targetAst.newTextElement(), targetAst.newMethodRef() };
            }
        });
    }

    public void testBlockComment() {
        long previousCount = this.ast.modificationCount();
        final BlockComment x = this.ast.newBlockComment();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getNodeType() == ASTNode.BLOCK_COMMENT);
        assertTrue(x.isBlockComment());
        assertTrue(!x.isLineComment());
        assertTrue(!x.isDocComment());
        assertTrue(x.getAlternateRoot() == null);
        assertTrue(x.structuralPropertiesForType() == BlockComment.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tAlternateRoot(x);
    }

    public void testLineComment() {
        long previousCount = this.ast.modificationCount();
        final LineComment x = this.ast.newLineComment();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getNodeType() == ASTNode.LINE_COMMENT);
        assertTrue(!x.isBlockComment());
        assertTrue(x.isLineComment());
        assertTrue(!x.isDocComment());
        assertTrue(x.getAlternateRoot() == null);
        assertTrue(x.structuralPropertiesForType() == LineComment.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tAlternateRoot(x);
    }

    public void testTagElement() {
        long previousCount = this.ast.modificationCount();
        final TagElement x = this.ast.newTagElement();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getNodeType() == ASTNode.TAG_ELEMENT);
        assertTrue(x.getTagName() == null);
        assertTrue(x.fragments().isEmpty());
        assertTrue(x.structuralPropertiesForType() == TagElement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        previousCount = this.ast.modificationCount();
        String s1 = new String("hello");
        x.setTagName(s1);
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.getTagName() == s1);
        previousCount = this.ast.modificationCount();
        String s2 = new String("bye");
        x.setTagName(s2);
        assertTrue(x.getTagName() == s2);
        assertTrue(this.ast.modificationCount() > previousCount);
        x.setTagName(null);
        assertTrue(x.getTagName() == null);
        assertTrue(this.ast.modificationCount() > previousCount);
        genericPropertyListTest(x, x.fragments(), new Property("Fragments", true, TagElement.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                TagElement result = targetAst.newTagElement();
                if (parented) {
                    Javadoc parent = targetAst.newJavadoc();
                    parent.tags().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                TagElement tagElement = ASTTest.this.ast.newTagElement();
                tagElement.fragments().add(x);
                return tagElement;
            }

            public void unwrap() {
                TagElement tagElement = (TagElement) x.getParent();
                tagElement.fragments().remove(x);
            }

            public ASTNode[] counterExamples(AST targetAst) {
                return new ASTNode[] { targetAst.newEmptyStatement(), targetAst.newCompilationUnit(), targetAst.newTypeDeclaration(), targetAst.newJavadoc() };
            }
        });
        genericPropertyListTest(x, x.fragments(), new Property("Fragments", true, Name.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }
        });
        genericPropertyListTest(x, x.fragments(), new Property("Fragments", true, TextElement.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                TextElement result = targetAst.newTextElement();
                if (parented) {
                    TagElement parent = targetAst.newTagElement();
                    parent.fragments().add(result);
                }
                return result;
            }
        });
        genericPropertyListTest(x, x.fragments(), new Property("Fragments", true, MethodRef.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                MethodRef result = targetAst.newMethodRef();
                if (parented) {
                    TagElement parent = targetAst.newTagElement();
                    parent.fragments().add(result);
                }
                return result;
            }
        });
        genericPropertyListTest(x, x.fragments(), new Property("Fragments", true, MemberRef.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                MemberRef result = targetAst.newMemberRef();
                if (parented) {
                    TagElement parent = targetAst.newTagElement();
                    parent.fragments().add(result);
                }
                return result;
            }
        });
    }

    public void testTextElement() {
        long previousCount = this.ast.modificationCount();
        final TextElement x = this.ast.newTextElement();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getNodeType() == ASTNode.TEXT_ELEMENT);
        assertTrue(x.getText().length() == 0);
        assertTrue(x.structuralPropertiesForType() == TextElement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        previousCount = this.ast.modificationCount();
        String s1 = new String("hello");
        x.setText(s1);
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.getText() == s1);
        previousCount = this.ast.modificationCount();
        String s2 = new String("");
        x.setText(s2);
        assertTrue(x.getText() == s2);
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        try {
            x.setText(null);
            assertTrue(false);
        } catch (RuntimeException e) {
        }
        assertTrue(this.ast.modificationCount() == previousCount);
        previousCount = this.ast.modificationCount();
        try {
            x.setText("this would be the */ end of it");
            assertTrue(false);
        } catch (RuntimeException e) {
        }
        assertTrue(this.ast.modificationCount() == previousCount);
    }

    public void testMemberRef() {
        long previousCount = this.ast.modificationCount();
        final MemberRef x = this.ast.newMemberRef();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getNodeType() == ASTNode.MEMBER_REF);
        assertTrue(x.getQualifier() == null);
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.structuralPropertiesForType() == MemberRef.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Qualifier", false, Name.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                QualifiedName result = targetAst.newQualifiedName(targetAst.newSimpleName("a"), targetAst.newSimpleName("b"));
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getQualifier();
            }

            public void set(ASTNode value) {
                x.setQualifier((Name) value);
            }
        });
        genericPropertyTest(x, new Property("Name", true, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
    }

    public void testMethodRef() {
        long previousCount = this.ast.modificationCount();
        final MethodRef x = this.ast.newMethodRef();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getNodeType() == ASTNode.METHOD_REF);
        assertTrue(x.getQualifier() == null);
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.parameters().isEmpty());
        assertTrue(x.structuralPropertiesForType() == MethodRef.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Qualifier", false, Name.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                QualifiedName result = targetAst.newQualifiedName(targetAst.newSimpleName("a"), targetAst.newSimpleName("b"));
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getQualifier();
            }

            public void set(ASTNode value) {
                x.setQualifier((Name) value);
            }
        });
        genericPropertyTest(x, new Property("Name", true, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
        genericPropertyListTest(x, x.parameters(), new Property("Parameters", true, MethodRefParameter.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                MethodRefParameter result = targetAst.newMethodRefParameter();
                if (parented) {
                    MethodRef parent = targetAst.newMethodRef();
                    parent.parameters().add(result);
                }
                return result;
            }
        });
    }

    public void testMethodRefParameter() {
        long previousCount = this.ast.modificationCount();
        final MethodRefParameter x = this.ast.newMethodRefParameter();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getNodeType() == ASTNode.METHOD_REF_PARAMETER);
        assertTrue(x.getType().getParent() == x);
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            assertTrue(x.isVarargs() == false);
        }
        assertTrue(x.getName() == null);
        assertTrue(x.structuralPropertiesForType() == MethodRefParameter.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            previousCount = this.ast.modificationCount();
            x.setVarargs(true);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.isVarargs() == true);
            previousCount = this.ast.modificationCount();
            x.setVarargs(false);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.isVarargs() == false);
        }
        genericPropertyTest(x, new Property("Type", true, Type.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("foo"));
                if (parented) {
                    targetAst.newArrayType(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getType();
            }

            public void set(ASTNode value) {
                x.setType((Type) value);
            }
        });
        genericPropertyTest(x, new Property("Name", false, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
    }

    public void testBlock() {
        long previousCount = this.ast.modificationCount();
        final Block x = this.ast.newBlock();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.statements().size() == 0);
        assertTrue(x.getNodeType() == ASTNode.BLOCK);
        assertTrue(x.structuralPropertiesForType() == Block.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tLeadingComment(x);
        genericPropertyListTest(x, x.statements(), new Property("Statements", true, Statement.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                Block result = targetAst.newBlock();
                if (parented) {
                    Block b2 = targetAst.newBlock();
                    b2.statements().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                Block s1 = ASTTest.this.ast.newBlock();
                s1.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s2 = (Block) x.getParent();
                s2.statements().remove(x);
            }
        });
    }

    public void testMethodInvocation() {
        long previousCount = this.ast.modificationCount();
        final MethodInvocation x = this.ast.newMethodInvocation();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            assertTrue(x.typeArguments().isEmpty());
        }
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.getExpression() == null);
        assertTrue(x.arguments().size() == 0);
        assertTrue(x.getNodeType() == ASTNode.METHOD_INVOCATION);
        assertTrue(x.structuralPropertiesForType() == MethodInvocation.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Expression", false, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("x"));
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            genericPropertyListTest(x, x.typeArguments(), new Property("TypeArguments", true, Type.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    Type result = targetAst.newSimpleType(targetAst.newSimpleName("X"));
                    if (parented) {
                        targetAst.newArrayType(result);
                    }
                    return result;
                }
            });
        }
        genericPropertyTest(x, new Property("Name", true, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
        genericPropertyListTest(x, x.arguments(), new Property("Arguments", true, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("x"));
            }
        });
    }

    public void testExpressionStatement() {
        long previousCount = this.ast.modificationCount();
        SimpleName x1 = this.ast.newSimpleName("foo");
        final ExpressionStatement x = this.ast.newExpressionStatement(x1);
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getExpression() == x1);
        assertTrue(x1.getParent() == x);
        assertTrue(x.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
        assertTrue(x.structuralPropertiesForType() == ExpressionStatement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tLeadingComment(x);
        genericPropertyTest(x, new Property("Expression", true, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(a1);
                MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s2);
                Block s3 = ASTTest.this.ast.newBlock();
                s2.setBody(s3);
                s3.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s3 = (Block) x.getParent();
                s3.statements().remove(x);
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
    }

    public void testVariableDeclarationStatement() {
        VariableDeclarationFragment x1 = this.ast.newVariableDeclarationFragment();
        long previousCount = this.ast.modificationCount();
        final VariableDeclarationStatement x = this.ast.newVariableDeclarationStatement(x1);
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        if (this.ast.apiLevel() == AST.JLS2) {
            assertTrue(x.getModifiers() == Modifier.NONE);
        } else {
            assertTrue(x.modifiers().size() == 0);
        }
        assertTrue(x.getType() != null);
        assertTrue(x.getType().getParent() == x);
        assertTrue(x.fragments().size() == 1);
        assertTrue(x.fragments().get(0) == x1);
        assertTrue(x1.getParent() == x);
        assertTrue(x.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT);
        assertTrue(x.structuralPropertiesForType() == VariableDeclarationStatement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tLeadingComment(x);
        if (this.ast.apiLevel() == AST.JLS2) {
            int legal = Modifier.FINAL;
            previousCount = this.ast.modificationCount();
            x.setModifiers(legal);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getModifiers() == legal);
            previousCount = this.ast.modificationCount();
            x.setModifiers(Modifier.NONE);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getModifiers() == Modifier.NONE);
        }
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            genericPropertyListTest(x, x.modifiers(), new Property("Modifiers", true, IExtendedModifier.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    Modifier result = targetAst.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
                    if (parented) {
                        TypeDeclaration pd = targetAst.newTypeDeclaration();
                        pd.modifiers().add(result);
                    }
                    return result;
                }

                public ASTNode wrap() {
                    SingleMemberAnnotation s1 = x.getAST().newSingleMemberAnnotation();
                    ClassInstanceCreation s2 = x.getAST().newClassInstanceCreation();
                    AnonymousClassDeclaration s3 = x.getAST().newAnonymousClassDeclaration();
                    MethodDeclaration s4 = x.getAST().newMethodDeclaration();
                    Block s5 = x.getAST().newBlock();
                    VariableDeclarationFragment s6 = x.getAST().newVariableDeclarationFragment();
                    VariableDeclarationStatement s7 = x.getAST().newVariableDeclarationStatement(s6);
                    s1.setValue(s2);
                    s2.setAnonymousClassDeclaration(s3);
                    s3.bodyDeclarations().add(s4);
                    s4.setBody(s5);
                    s5.statements().add(s7);
                    s7.modifiers().add(x);
                    return s1;
                }

                public void unwrap() {
                    VariableDeclarationStatement s7 = (VariableDeclarationStatement) x.getParent();
                    s7.modifiers().remove(x);
                }
            });
            x.modifiers().clear();
            assertTrue(x.getModifiers() == Modifier.NONE);
            Modifier[] allMods = allModifiers();
            for (int i = 0; i < allMods.length; i++) {
                x.modifiers().add(allMods[i]);
                assertTrue(x.getModifiers() == allMods[i].getKeyword().toFlagValue());
                x.modifiers().remove(allMods[i]);
                assertTrue(x.getModifiers() == Modifier.NONE);
            }
            for (int i = 0; i < allMods.length; i++) {
                x.modifiers().add(allMods[i]);
            }
            int flags = x.getModifiers();
            for (int i = 0; i < allMods.length; i++) {
                assertTrue((flags & allMods[i].getKeyword().toFlagValue()) != 0);
            }
        }
        genericPropertyTest(x, new Property("Type", true, Type.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("foo"));
                if (parented) {
                    targetAst.newArrayType(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getType();
            }

            public void set(ASTNode value) {
                x.setType((Type) value);
            }
        });
        genericPropertyListTest(x, x.fragments(), new Property("VariableSpecifiers", true, VariableDeclarationFragment.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                VariableDeclarationFragment result = targetAst.newVariableDeclarationFragment();
                if (parented) {
                    targetAst.newVariableDeclarationExpression(result);
                }
                return result;
            }

            public ASTNode wrap() {
                VariableDeclarationFragment s1 = ASTTest.this.ast.newVariableDeclarationFragment();
                ClassInstanceCreation s0 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s0.setAnonymousClassDeclaration(a1);
                s1.setInitializer(s0);
                Initializer s2 = ASTTest.this.ast.newInitializer();
                a1.bodyDeclarations().add(s2);
                s2.getBody().statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s3 = (Block) x.getParent();
                s3.statements().remove(x);
            }
        });
    }

    public void testTypeDeclarationStatement() {
        AbstractTypeDeclaration x1 = this.ast.newTypeDeclaration();
        long previousCount = this.ast.modificationCount();
        final TypeDeclarationStatement x = this.ast.newTypeDeclarationStatement(x1);
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        if (this.ast.apiLevel() == AST.JLS2) {
            assertTrue(x.getTypeDeclaration() == x1);
        } else {
            assertTrue(x.getDeclaration() == x1);
        }
        assertTrue(x1.getParent() == x);
        assertTrue(x.getNodeType() == ASTNode.TYPE_DECLARATION_STATEMENT);
        assertTrue(x.structuralPropertiesForType() == TypeDeclarationStatement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        assertTrue(x1.isLocalTypeDeclaration() == true);
        assertTrue(x1.isMemberTypeDeclaration() == false);
        assertTrue(x1.isPackageMemberTypeDeclaration() == false);
        tLeadingComment(x);
        if (this.ast.apiLevel() == AST.JLS2) {
            genericPropertyTest(x, new Property("TypeDeclaration", true, TypeDeclaration.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    TypeDeclaration result = targetAst.newTypeDeclaration();
                    if (parented) {
                        targetAst.newTypeDeclarationStatement(result);
                    }
                    return result;
                }

                public ASTNode wrap() {
                    TypeDeclaration s1 = ASTTest.this.ast.newTypeDeclaration();
                    MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                    s1.bodyDeclarations().add(s2);
                    Block s3 = ASTTest.this.ast.newBlock();
                    s2.setBody(s3);
                    s3.statements().add(x);
                    return s1;
                }

                public void unwrap() {
                    Block s3 = (Block) x.getParent();
                    s3.statements().remove(x);
                }

                public ASTNode get() {
                    return x.getTypeDeclaration();
                }

                public void set(ASTNode value) {
                    x.setTypeDeclaration((TypeDeclaration) value);
                }
            });
        }
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            genericPropertyTest(x, new Property("Declaration", true, AbstractTypeDeclaration.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    AbstractTypeDeclaration result = targetAst.newTypeDeclaration();
                    if (parented) {
                        targetAst.newTypeDeclarationStatement(result);
                    }
                    return result;
                }

                public ASTNode wrap() {
                    TypeDeclaration s1 = ASTTest.this.ast.newTypeDeclaration();
                    MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                    s1.bodyDeclarations().add(s2);
                    Block s3 = ASTTest.this.ast.newBlock();
                    s2.setBody(s3);
                    s3.statements().add(x);
                    return s1;
                }

                public void unwrap() {
                    Block s3 = (Block) x.getParent();
                    s3.statements().remove(x);
                }

                public ASTNode get() {
                    return x.getDeclaration();
                }

                public void set(ASTNode value) {
                    x.setDeclaration((AbstractTypeDeclaration) value);
                }
            });
        }
    }

    public void testVariableDeclarationExpression() {
        VariableDeclarationFragment x1 = this.ast.newVariableDeclarationFragment();
        long previousCount = this.ast.modificationCount();
        final VariableDeclarationExpression x = this.ast.newVariableDeclarationExpression(x1);
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        if (this.ast.apiLevel() == AST.JLS2) {
            assertTrue(x.getModifiers() == Modifier.NONE);
        } else {
            assertTrue(x.modifiers().size() == 0);
        }
        assertTrue(x.getType() != null);
        assertTrue(x.getType().getParent() == x);
        assertTrue(x.fragments().size() == 1);
        assertTrue(x.fragments().get(0) == x1);
        assertTrue(x1.getParent() == x);
        assertTrue(x.getNodeType() == ASTNode.VARIABLE_DECLARATION_EXPRESSION);
        assertTrue(x.structuralPropertiesForType() == VariableDeclarationExpression.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        if (this.ast.apiLevel() == AST.JLS2) {
            int legal = Modifier.FINAL;
            previousCount = this.ast.modificationCount();
            x.setModifiers(legal);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getModifiers() == legal);
            previousCount = this.ast.modificationCount();
            x.setModifiers(Modifier.NONE);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getModifiers() == Modifier.NONE);
        }
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            genericPropertyListTest(x, x.modifiers(), new Property("Modifiers", true, IExtendedModifier.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    Modifier result = targetAst.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
                    if (parented) {
                        TypeDeclaration pd = targetAst.newTypeDeclaration();
                        pd.modifiers().add(result);
                    }
                    return result;
                }

                public ASTNode wrap() {
                    SingleMemberAnnotation s1 = x.getAST().newSingleMemberAnnotation();
                    s1.setValue(x);
                    return s1;
                }

                public void unwrap() {
                    SingleMemberAnnotation s1 = (SingleMemberAnnotation) x.getParent();
                    s1.setValue(x.getAST().newNullLiteral());
                }
            });
            x.modifiers().clear();
            assertTrue(x.getModifiers() == Modifier.NONE);
            Modifier[] allMods = allModifiers();
            for (int i = 0; i < allMods.length; i++) {
                x.modifiers().add(allMods[i]);
                assertTrue(x.getModifiers() == allMods[i].getKeyword().toFlagValue());
                x.modifiers().remove(allMods[i]);
                assertTrue(x.getModifiers() == Modifier.NONE);
            }
            for (int i = 0; i < allMods.length; i++) {
                x.modifiers().add(allMods[i]);
            }
            int flags = x.getModifiers();
            for (int i = 0; i < allMods.length; i++) {
                assertTrue((flags & allMods[i].getKeyword().toFlagValue()) != 0);
            }
        }
        genericPropertyTest(x, new Property("Type", true, Type.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("foo"));
                if (parented) {
                    targetAst.newArrayType(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getType();
            }

            public void set(ASTNode value) {
                x.setType((Type) value);
            }
        });
        genericPropertyListTest(x, x.fragments(), new Property("VariableSpecifiers", true, VariableDeclarationFragment.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                VariableDeclarationFragment result = targetAst.newVariableDeclarationFragment();
                if (parented) {
                    targetAst.newVariableDeclarationExpression(result);
                }
                return result;
            }

            public ASTNode wrap() {
                VariableDeclarationFragment s1 = ASTTest.this.ast.newVariableDeclarationFragment();
                ClassInstanceCreation s0 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s0.setAnonymousClassDeclaration(a1);
                s1.setInitializer(s0);
                ForStatement s2 = ASTTest.this.ast.newForStatement();
                s2.initializers().add(x);
                Initializer s3 = ASTTest.this.ast.newInitializer();
                a1.bodyDeclarations().add(s3);
                s3.getBody().statements().add(s2);
                return s1;
            }

            public void unwrap() {
                ForStatement s2 = (ForStatement) x.getParent();
                s2.initializers().remove(x);
            }
        });
    }

    public void testFieldDeclaration() {
        VariableDeclarationFragment x1 = this.ast.newVariableDeclarationFragment();
        long previousCount = this.ast.modificationCount();
        final FieldDeclaration x = this.ast.newFieldDeclaration(x1);
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getJavadoc() == null);
        if (this.ast.apiLevel() == AST.JLS2) {
            assertTrue(x.getModifiers() == Modifier.NONE);
        } else {
            assertTrue(x.modifiers().size() == 0);
        }
        assertTrue(x.getType() != null);
        assertTrue(x.getType().getParent() == x);
        assertTrue(x.fragments().size() == 1);
        assertTrue(x.fragments().get(0) == x1);
        assertTrue(x1.getParent() == x);
        assertTrue(x.getNodeType() == ASTNode.FIELD_DECLARATION);
        assertTrue(x.structuralPropertiesForType() == FieldDeclaration.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        if (this.ast.apiLevel() == AST.JLS2) {
            int legal = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL | Modifier.TRANSIENT | Modifier.VOLATILE;
            previousCount = this.ast.modificationCount();
            x.setModifiers(legal);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getModifiers() == legal);
            previousCount = this.ast.modificationCount();
            x.setModifiers(Modifier.NONE);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getModifiers() == Modifier.NONE);
        }
        tJavadocComment(x);
        tModifiers(x);
        genericPropertyTest(x, new Property("Type", true, Type.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("foo"));
                if (parented) {
                    targetAst.newArrayType(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getType();
            }

            public void set(ASTNode value) {
                x.setType((Type) value);
            }
        });
        genericPropertyListTest(x, x.fragments(), new Property("VariableSpecifiers", true, VariableDeclarationFragment.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                VariableDeclarationFragment result = targetAst.newVariableDeclarationFragment();
                if (parented) {
                    targetAst.newVariableDeclarationStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                VariableDeclarationFragment s1 = ASTTest.this.ast.newVariableDeclarationFragment();
                ClassInstanceCreation s2 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s2.setAnonymousClassDeclaration(a1);
                s1.setInitializer(s2);
                a1.bodyDeclarations().add(x);
                return s1;
            }

            public void unwrap() {
                AnonymousClassDeclaration a1 = (AnonymousClassDeclaration) x.getParent();
                a1.bodyDeclarations().remove(x);
            }
        });
    }

    public void testAssignment() {
        long previousCount = this.ast.modificationCount();
        final Assignment x = this.ast.newAssignment();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getOperator() == Assignment.Operator.ASSIGN);
        assertTrue(x.getLeftHandSide().getParent() == x);
        assertTrue(x.getRightHandSide().getParent() == x);
        assertTrue(x.getNodeType() == ASTNode.ASSIGNMENT);
        assertTrue(x.structuralPropertiesForType() == Assignment.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        previousCount = this.ast.modificationCount();
        x.setOperator(Assignment.Operator.PLUS_ASSIGN);
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.getOperator() == Assignment.Operator.PLUS_ASSIGN);
        assertTrue(Assignment.Operator.PLUS_ASSIGN != Assignment.Operator.ASSIGN);
        assertTrue(Assignment.Operator.ASSIGN.toString().equals("="));
        assertTrue(Assignment.Operator.PLUS_ASSIGN.toString().equals("+="));
        assertTrue(Assignment.Operator.MINUS_ASSIGN.toString().equals("-="));
        assertTrue(Assignment.Operator.TIMES_ASSIGN.toString().equals("*="));
        assertTrue(Assignment.Operator.DIVIDE_ASSIGN.toString().equals("/="));
        assertTrue(Assignment.Operator.REMAINDER_ASSIGN.toString().equals("%="));
        assertTrue(Assignment.Operator.LEFT_SHIFT_ASSIGN.toString().equals("<<="));
        assertTrue(Assignment.Operator.RIGHT_SHIFT_SIGNED_ASSIGN.toString().equals(">>="));
        assertTrue(Assignment.Operator.RIGHT_SHIFT_UNSIGNED_ASSIGN.toString().equals(">>>="));
        assertTrue(Assignment.Operator.BIT_AND_ASSIGN.toString().equals("&="));
        assertTrue(Assignment.Operator.BIT_OR_ASSIGN.toString().equals("|="));
        assertTrue(Assignment.Operator.BIT_XOR_ASSIGN.toString().equals("^="));
        Assignment.Operator[] known = { Assignment.Operator.ASSIGN, Assignment.Operator.PLUS_ASSIGN, Assignment.Operator.MINUS_ASSIGN, Assignment.Operator.TIMES_ASSIGN, Assignment.Operator.DIVIDE_ASSIGN, Assignment.Operator.REMAINDER_ASSIGN, Assignment.Operator.LEFT_SHIFT_ASSIGN, Assignment.Operator.RIGHT_SHIFT_SIGNED_ASSIGN, Assignment.Operator.RIGHT_SHIFT_UNSIGNED_ASSIGN, Assignment.Operator.BIT_AND_ASSIGN, Assignment.Operator.BIT_OR_ASSIGN, Assignment.Operator.BIT_XOR_ASSIGN };
        for (int i = 0; i < known.length; i++) {
            for (int j = 0; j < known.length; j++) {
                assertTrue(i == j || !known[i].equals(known[j]));
            }
        }
        for (int i = 0; i < known.length; i++) {
            previousCount = this.ast.modificationCount();
            x.setOperator(known[i]);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getOperator().equals(known[i]));
        }
        try {
            x.setOperator(null);
            assertTrue(false);
        } catch (RuntimeException e) {
        }
        for (int i = 0; i < known.length; i++) {
            String name = known[i].toString();
            assertTrue(Assignment.Operator.toOperator(name).equals(known[i]));
        }
        assertTrue(Assignment.Operator.toOperator("not-an-op") == null);
        genericPropertyTest(x, new Property("LeftHandSide", true, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("x"));
            }

            public ASTNode get() {
                return x.getLeftHandSide();
            }

            public void set(ASTNode value) {
                x.setLeftHandSide((Expression) value);
            }
        });
        genericPropertyTest(x, new Property("RightHandSide", true, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("x"));
            }

            public ASTNode get() {
                return x.getRightHandSide();
            }

            public void set(ASTNode value) {
                x.setRightHandSide((Expression) value);
            }
        });
    }

    public void testBreakStatement() {
        long previousCount = this.ast.modificationCount();
        final BreakStatement x = this.ast.newBreakStatement();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getLabel() == null);
        assertTrue(x.getLeadingComment() == null);
        assertTrue(x.getNodeType() == ASTNode.BREAK_STATEMENT);
        assertTrue(x.structuralPropertiesForType() == BreakStatement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tLeadingComment(x);
        genericPropertyTest(x, new Property("Label", false, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getLabel();
            }

            public void set(ASTNode value) {
                x.setLabel((SimpleName) value);
            }
        });
    }

    public void testContinueStatement() {
        long previousCount = this.ast.modificationCount();
        final ContinueStatement x = this.ast.newContinueStatement();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getLabel() == null);
        assertTrue(x.getLeadingComment() == null);
        assertTrue(x.getNodeType() == ASTNode.CONTINUE_STATEMENT);
        assertTrue(x.structuralPropertiesForType() == ContinueStatement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tLeadingComment(x);
        genericPropertyTest(x, new Property("Label", false, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getLabel();
            }

            public void set(ASTNode value) {
                x.setLabel((SimpleName) value);
            }
        });
    }

    public void testIfStatement() {
        long previousCount = this.ast.modificationCount();
        final IfStatement x = this.ast.newIfStatement();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getLeadingComment() == null);
        assertTrue(x.getExpression().getParent() == x);
        assertTrue(x.getThenStatement().getParent() == x);
        assertTrue(x.getThenStatement() instanceof Block);
        assertTrue(((Block) x.getThenStatement()).statements().isEmpty());
        assertTrue(x.getElseStatement() == null);
        assertTrue(x.getNodeType() == ASTNode.IF_STATEMENT);
        assertTrue(x.structuralPropertiesForType() == IfStatement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tLeadingComment(x);
        genericPropertyTest(x, new Property("Expression", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(a1);
                MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s2);
                Block s3 = ASTTest.this.ast.newBlock();
                s2.setBody(s3);
                s3.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s3 = (Block) x.getParent();
                s3.statements().remove(x);
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
        genericPropertyTest(x, new Property("ThenStatement", true, Statement.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                Block result = targetAst.newBlock();
                if (parented) {
                    Block b2 = targetAst.newBlock();
                    b2.statements().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                Block s1 = ASTTest.this.ast.newBlock();
                s1.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s2 = (Block) x.getParent();
                s2.statements().remove(x);
            }

            public ASTNode get() {
                return x.getThenStatement();
            }

            public void set(ASTNode value) {
                x.setThenStatement((Statement) value);
            }
        });
        genericPropertyTest(x, new Property("ElseStatement", false, Statement.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                Block result = targetAst.newBlock();
                if (parented) {
                    Block b2 = targetAst.newBlock();
                    b2.statements().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                Block s1 = ASTTest.this.ast.newBlock();
                s1.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s2 = (Block) x.getParent();
                s2.statements().remove(x);
            }

            public ASTNode get() {
                return x.getElseStatement();
            }

            public void set(ASTNode value) {
                x.setElseStatement((Statement) value);
            }
        });
    }

    public void testWhileStatement() {
        long previousCount = this.ast.modificationCount();
        final WhileStatement x = this.ast.newWhileStatement();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getLeadingComment() == null);
        assertTrue(x.getExpression().getParent() == x);
        assertTrue(x.getBody().getParent() == x);
        assertTrue(x.getBody() instanceof Block);
        assertTrue(((Block) x.getBody()).statements().isEmpty());
        assertTrue(x.getNodeType() == ASTNode.WHILE_STATEMENT);
        assertTrue(x.structuralPropertiesForType() == WhileStatement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tLeadingComment(x);
        genericPropertyTest(x, new Property("Expression", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(a1);
                MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s2);
                Block s3 = ASTTest.this.ast.newBlock();
                s2.setBody(s3);
                s3.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s3 = (Block) x.getParent();
                s3.statements().remove(x);
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
        genericPropertyTest(x, new Property("Body", true, Statement.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                Block result = targetAst.newBlock();
                if (parented) {
                    Block b2 = targetAst.newBlock();
                    b2.statements().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                Block s1 = ASTTest.this.ast.newBlock();
                s1.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s2 = (Block) x.getParent();
                s2.statements().remove(x);
            }

            public ASTNode get() {
                return x.getBody();
            }

            public void set(ASTNode value) {
                x.setBody((Statement) value);
            }
        });
    }

    public void testDoStatement() {
        long previousCount = this.ast.modificationCount();
        final DoStatement x = this.ast.newDoStatement();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getLeadingComment() == null);
        assertTrue(x.getExpression().getParent() == x);
        assertTrue(x.getBody().getParent() == x);
        assertTrue(x.getBody() instanceof Block);
        assertTrue(((Block) x.getBody()).statements().isEmpty());
        assertTrue(x.getNodeType() == ASTNode.DO_STATEMENT);
        assertTrue(x.structuralPropertiesForType() == DoStatement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tLeadingComment(x);
        genericPropertyTest(x, new Property("Expression", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(a1);
                MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s2);
                Block s3 = ASTTest.this.ast.newBlock();
                s2.setBody(s3);
                s3.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s3 = (Block) x.getParent();
                s3.statements().remove(x);
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
        genericPropertyTest(x, new Property("Body", true, Statement.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                Block result = targetAst.newBlock();
                if (parented) {
                    Block b2 = targetAst.newBlock();
                    b2.statements().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                Block s1 = ASTTest.this.ast.newBlock();
                s1.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s2 = (Block) x.getParent();
                s2.statements().remove(x);
            }

            public ASTNode get() {
                return x.getBody();
            }

            public void set(ASTNode value) {
                x.setBody((Statement) value);
            }
        });
    }

    public void testTryStatement() {
        if (this.ast.apiLevel() <= JLS3_INTERNAL) {
            try {
                final TryStatement x = this.ast.newTryStatement();
                x.resources();
                assertTrue("should not be reached if jls level <= JLS3", false);
            } catch (UnsupportedOperationException e) {
            }
        }
        long previousCount = this.ast.modificationCount();
        final TryStatement x = this.ast.newTryStatement();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getLeadingComment() == null);
        assertTrue(x.getBody().getParent() == x);
        assertTrue((x.getBody()).statements().isEmpty());
        assertTrue(x.getFinally() == null);
        assertTrue(x.catchClauses().size() == 0);
        assertTrue(x.getNodeType() == ASTNode.TRY_STATEMENT);
        assertTrue(x.structuralPropertiesForType() == TryStatement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tLeadingComment(x);
        genericPropertyTest(x, new Property("Body", true, Block.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                Block result = targetAst.newBlock();
                if (parented) {
                    Block b2 = targetAst.newBlock();
                    b2.statements().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                Block s1 = ASTTest.this.ast.newBlock();
                s1.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s2 = (Block) x.getParent();
                s2.statements().remove(x);
            }

            public ASTNode get() {
                return x.getBody();
            }

            public void set(ASTNode value) {
                x.setBody((Block) value);
            }
        });
        genericPropertyListTest(x, x.catchClauses(), new Property("CatchClauses", true, CatchClause.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                CatchClause result = targetAst.newCatchClause();
                if (parented) {
                    TryStatement s1 = targetAst.newTryStatement();
                    s1.catchClauses().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                CatchClause s1 = ASTTest.this.ast.newCatchClause();
                Block s2 = ASTTest.this.ast.newBlock();
                s1.setBody(s2);
                s2.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s2 = (Block) x.getParent();
                s2.statements().remove(x);
            }
        });
        genericPropertyTest(x, new Property("Finally", false, Block.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                Block result = targetAst.newBlock();
                if (parented) {
                    Block b2 = targetAst.newBlock();
                    b2.statements().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                Block s1 = ASTTest.this.ast.newBlock();
                s1.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s2 = (Block) x.getParent();
                s2.statements().remove(x);
            }

            public ASTNode get() {
                return x.getFinally();
            }

            public void set(ASTNode value) {
                x.setFinally((Block) value);
            }
        });
    }

    public void testCatchClause() {
        long previousCount = this.ast.modificationCount();
        final CatchClause x = this.ast.newCatchClause();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getBody().getParent() == x);
        assertTrue(x.getBody().statements().isEmpty());
        assertTrue(x.getException().getParent() == x);
        assertTrue(x.getNodeType() == ASTNode.CATCH_CLAUSE);
        assertTrue(x.structuralPropertiesForType() == CatchClause.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Exception", true, SingleVariableDeclaration.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SingleVariableDeclaration result = targetAst.newSingleVariableDeclaration();
                if (parented) {
                    targetAst.newCatchClause().setException(result);
                }
                return result;
            }

            public ASTNode wrap() {
                SingleVariableDeclaration s1 = ASTTest.this.ast.newSingleVariableDeclaration();
                ClassInstanceCreation s2 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s2.setAnonymousClassDeclaration(a1);
                s1.setInitializer(s2);
                MethodDeclaration s3 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s3);
                Block s4 = ASTTest.this.ast.newBlock();
                s3.setBody(s4);
                TryStatement s5 = ASTTest.this.ast.newTryStatement();
                s4.statements().add(s5);
                s5.catchClauses().add(x);
                return s1;
            }

            public void unwrap() {
                TryStatement s5 = (TryStatement) x.getParent();
                s5.catchClauses().remove(x);
            }

            public ASTNode get() {
                return x.getException();
            }

            public void set(ASTNode value) {
                x.setException((SingleVariableDeclaration) value);
            }
        });
        genericPropertyTest(x, new Property("Body", true, Block.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                Block result = targetAst.newBlock();
                if (parented) {
                    Block b2 = targetAst.newBlock();
                    b2.statements().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                Block s1 = ASTTest.this.ast.newBlock();
                TryStatement s2 = ASTTest.this.ast.newTryStatement();
                s1.statements().add(s2);
                s2.catchClauses().add(x);
                return s1;
            }

            public void unwrap() {
                TryStatement s2 = (TryStatement) x.getParent();
                s2.catchClauses().remove(x);
            }

            public ASTNode get() {
                return x.getBody();
            }

            public void set(ASTNode value) {
                x.setBody((Block) value);
            }
        });
    }

    public void testEmptyStatement() {
        long previousCount = this.ast.modificationCount();
        final EmptyStatement x = this.ast.newEmptyStatement();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getLeadingComment() == null);
        assertTrue(x.getNodeType() == ASTNode.EMPTY_STATEMENT);
        assertTrue(x.structuralPropertiesForType() == EmptyStatement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tLeadingComment(x);
    }

    void tLeadingComment(Statement x) {
        long previousCount = this.ast.modificationCount();
        x.setLeadingComment(null);
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.getLeadingComment() == null);
        previousCount = this.ast.modificationCount();
        x.setLeadingComment("/* X */");
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.getLeadingComment() == "/* X */");
        previousCount = this.ast.modificationCount();
        x.setLeadingComment("/* X\n *Y\n */");
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.getLeadingComment() == "/* X\n *Y\n */");
        previousCount = this.ast.modificationCount();
        x.setLeadingComment("// X\n");
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.getLeadingComment() == "// X\n");
        previousCount = this.ast.modificationCount();
        x.setLeadingComment("// X");
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.getLeadingComment() == "// X");
        try {
            x.setLeadingComment("// X\n extra");
            assertTrue(false);
        } catch (RuntimeException e) {
        }
    }

    void tJavadocComment(final BodyDeclaration x) {
        genericPropertyTest(x, new Property("Javadoc", false, Javadoc.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                Javadoc result = targetAst.newJavadoc();
                if (parented) {
                    targetAst.newInitializer().setJavadoc(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getJavadoc();
            }

            public void set(ASTNode value) {
                x.setJavadoc((Javadoc) value);
            }
        });
    }

    Modifier[] allModifiers() {
        Modifier[] allMods = { this.ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD), this.ast.newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD), this.ast.newModifier(Modifier.ModifierKeyword.PROTECTED_KEYWORD), this.ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD), this.ast.newModifier(Modifier.ModifierKeyword.FINAL_KEYWORD), this.ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD), this.ast.newModifier(Modifier.ModifierKeyword.NATIVE_KEYWORD), this.ast.newModifier(Modifier.ModifierKeyword.SYNCHRONIZED_KEYWORD), this.ast.newModifier(Modifier.ModifierKeyword.TRANSIENT_KEYWORD), this.ast.newModifier(Modifier.ModifierKeyword.VOLATILE_KEYWORD), this.ast.newModifier(Modifier.ModifierKeyword.STRICTFP_KEYWORD), this.ast.newModifier(Modifier.ModifierKeyword.DEFAULT_KEYWORD) };
        return allMods;
    }

    void tModifiers(final BodyDeclaration x) {
        if (this.ast.apiLevel() == AST.JLS2) {
            return;
        }
        genericPropertyListTest(x, x.modifiers(), new Property("Modifiers", true, IExtendedModifier.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                Modifier result = targetAst.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
                if (parented) {
                    TypeDeclaration pd = targetAst.newTypeDeclaration();
                    pd.modifiers().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                SingleMemberAnnotation s1 = x.getAST().newSingleMemberAnnotation();
                ClassInstanceCreation s2 = x.getAST().newClassInstanceCreation();
                AnonymousClassDeclaration s3 = x.getAST().newAnonymousClassDeclaration();
                MethodDeclaration s4 = x.getAST().newMethodDeclaration();
                s1.setValue(s2);
                s2.setAnonymousClassDeclaration(s3);
                s3.bodyDeclarations().add(s4);
                s4.modifiers().add(x);
                return s1;
            }

            public void unwrap() {
                MethodDeclaration s4 = (MethodDeclaration) x.getParent();
                s4.modifiers().remove(x);
            }
        });
        x.modifiers().clear();
        assertTrue(x.getModifiers() == Modifier.NONE);
        Modifier[] allMods = allModifiers();
        for (int i = 0; i < allMods.length; i++) {
            x.modifiers().add(allMods[i]);
            assertTrue(x.getModifiers() == allMods[i].getKeyword().toFlagValue());
            x.modifiers().remove(allMods[i]);
            assertTrue(x.getModifiers() == Modifier.NONE);
        }
        for (int i = 0; i < allMods.length; i++) {
            x.modifiers().add(allMods[i]);
        }
        int flags = x.getModifiers();
        for (int i = 0; i < allMods.length; i++) {
            assertTrue((flags & allMods[i].getKeyword().toFlagValue()) != 0);
        }
    }

    void tAlternateRoot(final Comment x) {
        CompilationUnit cu = this.ast.newCompilationUnit();
        long previousCount = this.ast.modificationCount();
        x.setAlternateRoot(cu);
        assertTrue(this.ast.modificationCount() > previousCount);
        assertTrue(x.getAlternateRoot() == cu);
        previousCount = this.ast.modificationCount();
        x.setAlternateRoot(null);
        assertTrue(x.getAlternateRoot() == null);
        assertTrue(this.ast.modificationCount() > previousCount);
    }

    void tClientProperties(ASTNode x) {
        long previousCount = this.ast.modificationCount();
        assertTrue(x.properties().size() == 0);
        assertTrue(x.getProperty("1") == null);
        x.setProperty("1", null);
        assertTrue(x.getProperty("1") == null);
        assertTrue(x.properties().size() == 0);
        x.setProperty("1", "a1");
        assertTrue(x.getProperty("1") == "a1");
        assertTrue(x.properties().size() == 1);
        Map.Entry[] m = (Map.Entry[]) x.properties().entrySet().toArray(new Map.Entry[1]);
        assertTrue(m[0].getKey() == "1");
        assertTrue(m[0].getValue() == "a1");
        x.setProperty("1", "a2");
        assertTrue(x.getProperty("1") == "a2");
        assertTrue(x.properties().size() == 1);
        m = (Map.Entry[]) x.properties().entrySet().toArray(new Map.Entry[1]);
        assertTrue(m[0].getKey() == "1");
        assertTrue(m[0].getValue() == "a2");
        x.setProperty("1", null);
        assertTrue(x.getProperty("1") == null);
        assertTrue(x.properties().size() == 0);
        x.setProperty("1", "a1");
        x.setProperty("2", "b1");
        x.setProperty("3", "c1");
        assertTrue(x.getProperty("1") == "a1");
        assertTrue(x.getProperty("2") == "b1");
        assertTrue(x.getProperty("3") == "c1");
        assertTrue(x.properties().size() == 3);
        assertTrue(x.properties().get("1") == "a1");
        assertTrue(x.properties().get("2") == "b1");
        assertTrue(x.properties().get("3") == "c1");
        x.setProperty("1", "a2");
        x.setProperty("2", "b2");
        x.setProperty("3", "c2");
        assertTrue(x.getProperty("1") == "a2");
        assertTrue(x.getProperty("2") == "b2");
        assertTrue(x.getProperty("3") == "c2");
        assertTrue(x.properties().size() == 3);
        assertTrue(x.properties().get("1") == "a2");
        assertTrue(x.properties().get("2") == "b2");
        assertTrue(x.properties().get("3") == "c2");
        x.setProperty("2", null);
        assertTrue(x.getProperty("1") == "a2");
        assertTrue(x.getProperty("2") == null);
        assertTrue(x.getProperty("3") == "c2");
        assertTrue(x.properties().size() == 2);
        assertTrue(x.properties().get("1") == "a2");
        assertTrue(x.properties().get("2") == null);
        assertTrue(x.properties().get("3") == "c2");
        x.setProperty("1", null);
        assertTrue(x.getProperty("1") == null);
        assertTrue(x.getProperty("2") == null);
        assertTrue(x.getProperty("3") == "c2");
        assertTrue(x.properties().size() == 1);
        assertTrue(x.properties().get("1") == null);
        assertTrue(x.properties().get("2") == null);
        assertTrue(x.properties().get("3") == "c2");
        assertTrue(this.ast.modificationCount() == previousCount);
    }

    public void testReturnStatement() {
        long previousCount = this.ast.modificationCount();
        final ReturnStatement x = this.ast.newReturnStatement();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getExpression() == null);
        assertTrue(x.getNodeType() == ASTNode.RETURN_STATEMENT);
        assertTrue(x.structuralPropertiesForType() == ReturnStatement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tLeadingComment(x);
        genericPropertyTest(x, new Property("Expression", false, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(a1);
                MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s2);
                Block s3 = ASTTest.this.ast.newBlock();
                s2.setBody(s3);
                s3.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s3 = (Block) x.getParent();
                s3.statements().remove(x);
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
    }

    public void testThrowStatement() {
        long previousCount = this.ast.modificationCount();
        final ThrowStatement x = this.ast.newThrowStatement();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getExpression().getParent() == x);
        assertTrue(x.getLeadingComment() == null);
        assertTrue(x.getNodeType() == ASTNode.THROW_STATEMENT);
        assertTrue(x.structuralPropertiesForType() == ThrowStatement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tLeadingComment(x);
        genericPropertyTest(x, new Property("Expression", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(a1);
                MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s2);
                Block s3 = ASTTest.this.ast.newBlock();
                s2.setBody(s3);
                s3.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s3 = (Block) x.getParent();
                s3.statements().remove(x);
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
    }

    public void testAssertStatement() {
        long previousCount = this.ast.modificationCount();
        final AssertStatement x = this.ast.newAssertStatement();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getExpression().getParent() == x);
        assertTrue(x.getMessage() == null);
        assertTrue(x.getLeadingComment() == null);
        assertTrue(x.getNodeType() == ASTNode.ASSERT_STATEMENT);
        assertTrue(x.structuralPropertiesForType() == AssertStatement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tLeadingComment(x);
        genericPropertyTest(x, new Property("Expression", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(a1);
                MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s2);
                Block s3 = ASTTest.this.ast.newBlock();
                s2.setBody(s3);
                s3.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s3 = (Block) x.getParent();
                s3.statements().remove(x);
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
        genericPropertyTest(x, new Property("Message", false, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(a1);
                MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s2);
                Block s3 = ASTTest.this.ast.newBlock();
                s2.setBody(s3);
                s3.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s3 = (Block) x.getParent();
                s3.statements().remove(x);
            }

            public ASTNode get() {
                return x.getMessage();
            }

            public void set(ASTNode value) {
                x.setMessage((Expression) value);
            }
        });
    }

    public void testSwitchStatement() {
        long previousCount = this.ast.modificationCount();
        final SwitchStatement x = this.ast.newSwitchStatement();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getExpression().getParent() == x);
        assertTrue(x.statements().isEmpty());
        assertTrue(x.getLeadingComment() == null);
        assertTrue(x.getNodeType() == ASTNode.SWITCH_STATEMENT);
        assertTrue(x.structuralPropertiesForType() == SwitchStatement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tLeadingComment(x);
        genericPropertyTest(x, new Property("Expression", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(a1);
                MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s2);
                Block s3 = ASTTest.this.ast.newBlock();
                s2.setBody(s3);
                s3.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s3 = (Block) x.getParent();
                s3.statements().remove(x);
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
        genericPropertyListTest(x, x.statements(), new Property("Statements", true, Statement.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                Block result = targetAst.newBlock();
                if (parented) {
                    Block b2 = targetAst.newBlock();
                    b2.statements().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                Block s1 = ASTTest.this.ast.newBlock();
                s1.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s2 = (Block) x.getParent();
                s2.statements().remove(x);
            }
        });
    }

    public void testSwitchCase() {
        long previousCount = this.ast.modificationCount();
        final SwitchCase x = this.ast.newSwitchCase();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getExpression().getParent() == x);
        assertTrue(x.getLeadingComment() == null);
        assertTrue(!x.isDefault());
        assertTrue(x.getNodeType() == ASTNode.SWITCH_CASE);
        assertTrue(x.structuralPropertiesForType() == SwitchCase.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Expression", false, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(a1);
                MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s2);
                Block s3 = ASTTest.this.ast.newBlock();
                s2.setBody(s3);
                SwitchStatement s4 = ASTTest.this.ast.newSwitchStatement();
                s3.statements().add(s4);
                s4.statements().add(x);
                return s1;
            }

            public void unwrap() {
                SwitchStatement s4 = (SwitchStatement) x.getParent();
                s4.statements().remove(x);
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
    }

    public void testSynchronizedStatement() {
        long previousCount = this.ast.modificationCount();
        final SynchronizedStatement x = this.ast.newSynchronizedStatement();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getExpression().getParent() == x);
        assertTrue(x.getBody().statements().isEmpty());
        assertTrue(x.getLeadingComment() == null);
        assertTrue(x.getNodeType() == ASTNode.SYNCHRONIZED_STATEMENT);
        assertTrue(x.structuralPropertiesForType() == SynchronizedStatement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tLeadingComment(x);
        genericPropertyTest(x, new Property("Expression", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(a1);
                MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s2);
                Block s3 = ASTTest.this.ast.newBlock();
                s2.setBody(s3);
                s3.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s3 = (Block) x.getParent();
                s3.statements().remove(x);
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
        genericPropertyTest(x, new Property("Body", true, Block.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                Block result = targetAst.newBlock();
                if (parented) {
                    Block b2 = targetAst.newBlock();
                    b2.statements().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                Block s1 = ASTTest.this.ast.newBlock();
                s1.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s2 = (Block) x.getParent();
                s2.statements().remove(x);
            }

            public ASTNode get() {
                return x.getBody();
            }

            public void set(ASTNode value) {
                x.setBody((Block) value);
            }
        });
    }

    public void testLabeledStatement() {
        long previousCount = this.ast.modificationCount();
        final LabeledStatement x = this.ast.newLabeledStatement();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getLabel().getParent() == x);
        assertTrue(x.getBody().getParent() == x);
        assertTrue(x.getLeadingComment() == null);
        assertTrue(x.getNodeType() == ASTNode.LABELED_STATEMENT);
        assertTrue(x.structuralPropertiesForType() == LabeledStatement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tLeadingComment(x);
        genericPropertyTest(x, new Property("Label", true, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getLabel();
            }

            public void set(ASTNode value) {
                x.setLabel((SimpleName) value);
            }
        });
        genericPropertyTest(x, new Property("Body", true, Statement.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                Block result = targetAst.newBlock();
                if (parented) {
                    Block b2 = targetAst.newBlock();
                    b2.statements().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                Block s1 = ASTTest.this.ast.newBlock();
                s1.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s2 = (Block) x.getParent();
                s2.statements().remove(x);
            }

            public ASTNode get() {
                return x.getBody();
            }

            public void set(ASTNode value) {
                x.setBody((Statement) value);
            }
        });
    }

    void assignSourceRanges(ASTNode target) {
        final StringBuffer buffer = new StringBuffer();
        final List stack = new ArrayList();
        class PositionAssigner extends ASTVisitor {

             PositionAssigner() {
                super(true);
            }

            public void preVisit(ASTNode node) {
                int start = buffer.length();
                buffer.append("(");
                stack.add(Integer.valueOf(start));
            }

            public void postVisit(ASTNode node) {
                int start = ((Integer) stack.remove(stack.size() - 1)).intValue();
                buffer.append(")");
                int length = buffer.length() - start;
                node.setSourceRange(start, length);
            }
        }
        target.accept(new PositionAssigner());
    }

    public void testClone() {
        ASTNode x = SampleASTs.oneOfEach(this.ast);
        assignSourceRanges(x);
        assertTrue(x.subtreeMatch(new CheckPositionsMatcher(), x));
        ASTNode y = ASTNode.copySubtree(this.ast, x);
        assertTrue(x.subtreeMatch(new CheckPositionsMatcher(), y));
        assertTrue(y.subtreeMatch(new CheckPositionsMatcher(), x));
        AST newAST = AST.newAST(this.ast.apiLevel());
        ASTNode z = ASTNode.copySubtree(newAST, x);
        assertTrue(x.subtreeMatch(new CheckPositionsMatcher(), z));
        assertTrue(z.subtreeMatch(new CheckPositionsMatcher(), x));
    }

    public void testNullResolve() {
        ASTNode x = SampleASTs.oneOfEach(this.ast);
        ASTVisitor v = new ASTVisitor(true) {

            public boolean visit(SimpleName node) {
                assertTrue(node.resolveBinding() == null);
                return true;
            }

            public boolean visit(QualifiedName node) {
                assertTrue(node.resolveBinding() == null);
                return true;
            }

            public boolean visit(SimpleType node) {
                assertTrue(node.resolveBinding() == null);
                return true;
            }

            public boolean visit(ArrayType node) {
                assertTrue(node.resolveBinding() == null);
                return true;
            }

            public boolean visit(ParameterizedType node) {
                assertTrue(node.resolveBinding() == null);
                return true;
            }

            public boolean visit(PrimitiveType node) {
                assertTrue(node.resolveBinding() == null);
                return true;
            }

            public boolean visit(QualifiedType node) {
                assertTrue(node.resolveBinding() == null);
                return true;
            }

            public boolean visit(WildcardType node) {
                assertTrue(node.resolveBinding() == null);
                return true;
            }

            public boolean visit(Assignment node) {
                assertTrue(node.resolveTypeBinding() == null);
                return true;
            }

            public boolean visit(ClassInstanceCreation node) {
                assertTrue(node.resolveConstructorBinding() == null);
                return true;
            }

            public boolean visit(ConstructorInvocation node) {
                assertTrue(node.resolveConstructorBinding() == null);
                return true;
            }

            public boolean visit(SuperConstructorInvocation node) {
                assertTrue(node.resolveConstructorBinding() == null);
                return true;
            }

            public boolean visit(PackageDeclaration node) {
                assertTrue(node.resolveBinding() == null);
                return true;
            }

            public boolean visit(ImportDeclaration node) {
                assertTrue(node.resolveBinding() == null);
                return true;
            }

            public boolean visit(MethodDeclaration node) {
                assertTrue(node.resolveBinding() == null);
                return true;
            }

            public boolean visit(TypeDeclaration node) {
                assertTrue(node.resolveBinding() == null);
                return true;
            }

            public boolean visit(TypeDeclarationStatement node) {
                assertTrue(node.resolveBinding() == null);
                return true;
            }

            public boolean visit(SingleVariableDeclaration node) {
                assertTrue(node.resolveBinding() == null);
                return true;
            }

            public boolean visit(VariableDeclarationFragment node) {
                assertTrue(node.resolveBinding() == null);
                return true;
            }

            public boolean visit(EnumConstantDeclaration node) {
                assertTrue(node.resolveVariable() == null);
                return true;
            }
        };
        x.accept(v);
    }

    public void testForStatement() {
        long previousCount = this.ast.modificationCount();
        final ForStatement x = this.ast.newForStatement();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.initializers().isEmpty());
        assertTrue(x.getExpression() == null);
        assertTrue(x.updaters().isEmpty());
        assertTrue(x.getBody().getParent() == x);
        assertTrue(x.getBody() instanceof Block);
        assertTrue(((Block) x.getBody()).statements().isEmpty());
        assertTrue(x.getLeadingComment() == null);
        assertTrue(x.getNodeType() == ASTNode.FOR_STATEMENT);
        assertTrue(x.structuralPropertiesForType() == ForStatement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tLeadingComment(x);
        genericPropertyListTest(x, x.initializers(), new Property("Initializers", true, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(a1);
                MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s2);
                Block s3 = ASTTest.this.ast.newBlock();
                s2.setBody(s3);
                s3.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s3 = (Block) x.getParent();
                s3.statements().remove(x);
            }
        });
        genericPropertyTest(x, new Property("Expression", false, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(a1);
                MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s2);
                Block s3 = ASTTest.this.ast.newBlock();
                s2.setBody(s3);
                s3.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s3 = (Block) x.getParent();
                s3.statements().remove(x);
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
        genericPropertyListTest(x, x.updaters(), new Property("Updaters", true, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(a1);
                MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s2);
                Block s3 = ASTTest.this.ast.newBlock();
                s2.setBody(s3);
                s3.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s3 = (Block) x.getParent();
                s3.statements().remove(x);
            }
        });
        genericPropertyTest(x, new Property("Body", true, Statement.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                Block result = targetAst.newBlock();
                if (parented) {
                    Block b2 = targetAst.newBlock();
                    b2.statements().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                Block s1 = ASTTest.this.ast.newBlock();
                s1.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s2 = (Block) x.getParent();
                s2.statements().remove(x);
            }

            public ASTNode get() {
                return x.getBody();
            }

            public void set(ASTNode value) {
                x.setBody((Statement) value);
            }
        });
    }

    public void testEnhancedForStatement() {
        if (this.ast.apiLevel() == AST.JLS2) {
            try {
                this.ast.newEnhancedForStatement();
                assertTrue(false);
            } catch (UnsupportedOperationException e) {
            }
            return;
        }
        long previousCount = this.ast.modificationCount();
        final EnhancedForStatement x = this.ast.newEnhancedForStatement();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getParameter() != null);
        assertTrue(x.getParameter().getParent() == x);
        assertTrue(x.getExpression() != null);
        assertTrue(x.getExpression().getParent() == x);
        assertTrue(x.getBody().getParent() == x);
        assertTrue(x.getBody() instanceof Block);
        assertTrue(((Block) x.getBody()).statements().isEmpty());
        assertTrue(x.getLeadingComment() == null);
        assertTrue(x.getNodeType() == ASTNode.ENHANCED_FOR_STATEMENT);
        assertTrue(x.structuralPropertiesForType() == EnhancedForStatement.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tLeadingComment(x);
        genericPropertyTest(x, new Property("Parameter", true, SingleVariableDeclaration.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SingleVariableDeclaration result = targetAst.newSingleVariableDeclaration();
                if (parented) {
                    CatchClause parent = targetAst.newCatchClause();
                    parent.setException(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getParameter();
            }

            public void set(ASTNode value) {
                x.setParameter((SingleVariableDeclaration) value);
            }
        });
        genericPropertyTest(x, new Property("Expression", true, Expression.class) {

            public ASTNode sample(AST target, boolean parented) {
                Expression result = target.newSimpleName("foo");
                if (parented) {
                    target.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(a1);
                MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s2);
                Block s3 = ASTTest.this.ast.newBlock();
                s2.setBody(s3);
                s3.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s3 = (Block) x.getParent();
                s3.statements().remove(x);
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
    }

    public void testConstructorInvocation() {
        long previousCount = this.ast.modificationCount();
        final ConstructorInvocation x = this.ast.newConstructorInvocation();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            assertTrue(x.typeArguments().isEmpty());
        }
        assertTrue(x.arguments().isEmpty());
        assertTrue(x.getNodeType() == ASTNode.CONSTRUCTOR_INVOCATION);
        assertTrue(x.structuralPropertiesForType() == ConstructorInvocation.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            genericPropertyListTest(x, x.typeArguments(), new Property("TypeArguments", true, Type.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    Type result = targetAst.newSimpleType(targetAst.newSimpleName("X"));
                    if (parented) {
                        targetAst.newArrayType(result);
                    }
                    return result;
                }
            });
        }
        genericPropertyListTest(x, x.arguments(), new Property("Arguments", true, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(a1);
                MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s2);
                Block s3 = ASTTest.this.ast.newBlock();
                s2.setBody(s3);
                s3.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s3 = (Block) x.getParent();
                s3.statements().remove(x);
            }
        });
    }

    public void testSuperConstructorInvocation() {
        long previousCount = this.ast.modificationCount();
        final SuperConstructorInvocation x = this.ast.newSuperConstructorInvocation();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getExpression() == null);
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            assertTrue(x.typeArguments().isEmpty());
        }
        assertTrue(x.arguments().isEmpty());
        assertTrue(x.getNodeType() == ASTNode.SUPER_CONSTRUCTOR_INVOCATION);
        assertTrue(x.structuralPropertiesForType() == SuperConstructorInvocation.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Expression", false, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(a1);
                MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s2);
                Block s3 = ASTTest.this.ast.newBlock();
                s2.setBody(s3);
                s3.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s3 = (Block) x.getParent();
                s3.statements().remove(x);
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            genericPropertyListTest(x, x.typeArguments(), new Property("TypeArguments", true, Type.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    Type result = targetAst.newSimpleType(targetAst.newSimpleName("X"));
                    if (parented) {
                        targetAst.newArrayType(result);
                    }
                    return result;
                }
            });
        }
        genericPropertyListTest(x, x.arguments(), new Property("Arguments", true, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = ASTTest.this.ast.newClassInstanceCreation();
                AnonymousClassDeclaration a1 = ASTTest.this.ast.newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(a1);
                MethodDeclaration s2 = ASTTest.this.ast.newMethodDeclaration();
                a1.bodyDeclarations().add(s2);
                Block s3 = ASTTest.this.ast.newBlock();
                s2.setBody(s3);
                s3.statements().add(x);
                return s1;
            }

            public void unwrap() {
                Block s3 = (Block) x.getParent();
                s3.statements().remove(x);
            }
        });
    }

    public void testThisExpression() {
        long previousCount = this.ast.modificationCount();
        final ThisExpression x = this.ast.newThisExpression();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getQualifier() == null);
        assertTrue(x.getNodeType() == ASTNode.THIS_EXPRESSION);
        assertTrue(x.structuralPropertiesForType() == ThisExpression.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Qualifier", false, Name.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                QualifiedName result = targetAst.newQualifiedName(targetAst.newSimpleName("a"), targetAst.newSimpleName("b"));
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getQualifier();
            }

            public void set(ASTNode value) {
                x.setQualifier((Name) value);
            }
        });
    }

    public void testFieldAccess() {
        long previousCount = this.ast.modificationCount();
        final FieldAccess x = this.ast.newFieldAccess();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.getExpression().getParent() == x);
        assertTrue(x.getNodeType() == ASTNode.FIELD_ACCESS);
        assertTrue(x.structuralPropertiesForType() == FieldAccess.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Expression", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("fie"));
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
        genericPropertyTest(x, new Property("Name", true, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
    }

    public void testSuperFieldAccess() {
        long previousCount = this.ast.modificationCount();
        final SuperFieldAccess x = this.ast.newSuperFieldAccess();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.getQualifier() == null);
        assertTrue(x.getNodeType() == ASTNode.SUPER_FIELD_ACCESS);
        assertTrue(x.structuralPropertiesForType() == SuperFieldAccess.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Qualifier", false, Name.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                QualifiedName result = targetAst.newQualifiedName(targetAst.newSimpleName("a"), targetAst.newSimpleName("b"));
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getQualifier();
            }

            public void set(ASTNode value) {
                x.setQualifier((Name) value);
            }
        });
        genericPropertyTest(x, new Property("Name", true, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
    }

    public void testSuperMethodInvocation() {
        long previousCount = this.ast.modificationCount();
        final SuperMethodInvocation x = this.ast.newSuperMethodInvocation();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            assertTrue(x.typeArguments().isEmpty());
        }
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.getQualifier() == null);
        assertTrue(x.arguments().isEmpty());
        assertTrue(x.getNodeType() == ASTNode.SUPER_METHOD_INVOCATION);
        assertTrue(x.structuralPropertiesForType() == SuperMethodInvocation.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Qualifier", false, Name.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                QualifiedName result = targetAst.newQualifiedName(targetAst.newSimpleName("a"), targetAst.newSimpleName("b"));
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getQualifier();
            }

            public void set(ASTNode value) {
                x.setQualifier((Name) value);
            }
        });
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            genericPropertyListTest(x, x.typeArguments(), new Property("TypeArguments", true, Type.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    Type result = targetAst.newSimpleType(targetAst.newSimpleName("X"));
                    if (parented) {
                        targetAst.newArrayType(result);
                    }
                    return result;
                }
            });
        }
        genericPropertyTest(x, new Property("Name", true, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
        genericPropertyListTest(x, x.arguments(), new Property("Arguments", true, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("x"));
            }
        });
    }

    public void testTypeLiteral() {
        long previousCount = this.ast.modificationCount();
        final TypeLiteral x = this.ast.newTypeLiteral();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getType().getParent() == x);
        assertTrue(x.getNodeType() == ASTNode.TYPE_LITERAL);
        assertTrue(x.structuralPropertiesForType() == TypeLiteral.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Type", true, Type.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("a"));
                if (parented) {
                    targetAst.newArrayType(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getType();
            }

            public void set(ASTNode value) {
                x.setType((Type) value);
            }
        });
    }

    public void testCastExpression() {
        long previousCount = this.ast.modificationCount();
        final CastExpression x = this.ast.newCastExpression();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getType().getParent() == x);
        assertTrue(x.getExpression().getParent() == x);
        assertTrue(x.getNodeType() == ASTNode.CAST_EXPRESSION);
        assertTrue(x.structuralPropertiesForType() == CastExpression.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Type", true, Type.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("a"));
                if (parented) {
                    targetAst.newArrayType(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getType();
            }

            public void set(ASTNode value) {
                x.setType((Type) value);
            }
        });
        genericPropertyTest(x, new Property("Expression", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("fie"));
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
    }

    public void testPrefixExpression() {
        long previousCount = this.ast.modificationCount();
        final PrefixExpression x = this.ast.newPrefixExpression();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getOperand().getParent() == x);
        assertTrue(x.getOperator() != null);
        assertTrue(x.getNodeType() == ASTNode.PREFIX_EXPRESSION);
        assertTrue(x.structuralPropertiesForType() == PrefixExpression.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        assertTrue(PrefixExpression.Operator.INCREMENT.toString().equals("++"));
        assertTrue(PrefixExpression.Operator.DECREMENT.toString().equals("--"));
        assertTrue(PrefixExpression.Operator.PLUS.toString().equals("+"));
        assertTrue(PrefixExpression.Operator.MINUS.toString().equals("-"));
        assertTrue(PrefixExpression.Operator.COMPLEMENT.toString().equals("~"));
        assertTrue(PrefixExpression.Operator.NOT.toString().equals("!"));
        PrefixExpression.Operator[] known = { PrefixExpression.Operator.INCREMENT, PrefixExpression.Operator.DECREMENT, PrefixExpression.Operator.PLUS, PrefixExpression.Operator.MINUS, PrefixExpression.Operator.COMPLEMENT, PrefixExpression.Operator.NOT };
        for (int i = 0; i < known.length; i++) {
            for (int j = 0; j < known.length; j++) {
                assertTrue(i == j || !known[i].equals(known[j]));
            }
        }
        for (int i = 0; i < known.length; i++) {
            previousCount = this.ast.modificationCount();
            x.setOperator(known[i]);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getOperator().equals(known[i]));
        }
        try {
            x.setOperator(null);
            assertTrue(false);
        } catch (RuntimeException e) {
        }
        for (int i = 0; i < known.length; i++) {
            String name = known[i].toString();
            assertTrue(PrefixExpression.Operator.toOperator(name).equals(known[i]));
        }
        assertTrue(PrefixExpression.Operator.toOperator("huh") == null);
        genericPropertyTest(x, new Property("Operand", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("fie"));
            }

            public ASTNode get() {
                return x.getOperand();
            }

            public void set(ASTNode value) {
                x.setOperand((Expression) value);
            }
        });
    }

    public void testPostfixExpression() {
        long previousCount = this.ast.modificationCount();
        final PostfixExpression x = this.ast.newPostfixExpression();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getOperand().getParent() == x);
        assertTrue(x.getOperator() != null);
        assertTrue(x.getNodeType() == ASTNode.POSTFIX_EXPRESSION);
        assertTrue(x.structuralPropertiesForType() == PostfixExpression.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        assertTrue(PostfixExpression.Operator.INCREMENT.toString().equals("++"));
        assertTrue(PostfixExpression.Operator.DECREMENT.toString().equals("--"));
        PostfixExpression.Operator[] known = { PostfixExpression.Operator.INCREMENT, PostfixExpression.Operator.DECREMENT };
        for (int i = 0; i < known.length; i++) {
            for (int j = 0; j < known.length; j++) {
                assertTrue(i == j || !known[i].equals(known[j]));
            }
        }
        for (int i = 0; i < known.length; i++) {
            previousCount = this.ast.modificationCount();
            x.setOperator(known[i]);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getOperator().equals(known[i]));
        }
        try {
            x.setOperator(null);
            assertTrue(false);
        } catch (RuntimeException e) {
        }
        for (int i = 0; i < known.length; i++) {
            String name = known[i].toString();
            assertTrue(PostfixExpression.Operator.toOperator(name).equals(known[i]));
        }
        assertTrue(PostfixExpression.Operator.toOperator("huh") == null);
        genericPropertyTest(x, new Property("Operand", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("fie"));
            }

            public ASTNode get() {
                return x.getOperand();
            }

            public void set(ASTNode value) {
                x.setOperand((Expression) value);
            }
        });
    }

    public void testInfixExpression() {
        long previousCount = this.ast.modificationCount();
        final InfixExpression x = this.ast.newInfixExpression();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getLeftOperand().getParent() == x);
        assertTrue(x.getOperator() != null);
        assertTrue(x.getRightOperand().getParent() == x);
        assertTrue(x.extendedOperands().isEmpty());
        assertTrue(x.getNodeType() == ASTNode.INFIX_EXPRESSION);
        assertTrue(x.structuralPropertiesForType() == InfixExpression.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        assertTrue(InfixExpression.Operator.TIMES.toString().equals("*"));
        assertTrue(InfixExpression.Operator.DIVIDE.toString().equals("/"));
        assertTrue(InfixExpression.Operator.REMAINDER.toString().equals("%"));
        assertTrue(InfixExpression.Operator.PLUS.toString().equals("+"));
        assertTrue(InfixExpression.Operator.MINUS.toString().equals("-"));
        assertTrue(InfixExpression.Operator.LEFT_SHIFT.toString().equals("<<"));
        assertTrue(InfixExpression.Operator.RIGHT_SHIFT_SIGNED.toString().equals(">>"));
        assertTrue(InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED.toString().equals(">>>"));
        assertTrue(InfixExpression.Operator.LESS.toString().equals("<"));
        assertTrue(InfixExpression.Operator.GREATER.toString().equals(">"));
        assertTrue(InfixExpression.Operator.LESS_EQUALS.toString().equals("<="));
        assertTrue(InfixExpression.Operator.GREATER_EQUALS.toString().equals(">="));
        assertTrue(InfixExpression.Operator.EQUALS.toString().equals("=="));
        assertTrue(InfixExpression.Operator.NOT_EQUALS.toString().equals("!="));
        assertTrue(InfixExpression.Operator.XOR.toString().equals("^"));
        assertTrue(InfixExpression.Operator.OR.toString().equals("|"));
        assertTrue(InfixExpression.Operator.AND.toString().equals("&"));
        assertTrue(InfixExpression.Operator.CONDITIONAL_OR.toString().equals("||"));
        assertTrue(InfixExpression.Operator.CONDITIONAL_AND.toString().equals("&&"));
        InfixExpression.Operator[] known = { InfixExpression.Operator.TIMES, InfixExpression.Operator.DIVIDE, InfixExpression.Operator.REMAINDER, InfixExpression.Operator.PLUS, InfixExpression.Operator.MINUS, InfixExpression.Operator.LEFT_SHIFT, InfixExpression.Operator.RIGHT_SHIFT_SIGNED, InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED, InfixExpression.Operator.LESS, InfixExpression.Operator.GREATER, InfixExpression.Operator.LESS_EQUALS, InfixExpression.Operator.GREATER_EQUALS, InfixExpression.Operator.EQUALS, InfixExpression.Operator.NOT_EQUALS, InfixExpression.Operator.XOR, InfixExpression.Operator.OR, InfixExpression.Operator.AND, InfixExpression.Operator.CONDITIONAL_OR, InfixExpression.Operator.CONDITIONAL_AND };
        for (int i = 0; i < known.length; i++) {
            for (int j = 0; j < known.length; j++) {
                assertTrue(i == j || !known[i].equals(known[j]));
            }
        }
        for (int i = 0; i < known.length; i++) {
            previousCount = this.ast.modificationCount();
            x.setOperator(known[i]);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getOperator().equals(known[i]));
        }
        try {
            x.setOperator(null);
            assertTrue(false);
        } catch (RuntimeException e) {
        }
        for (int i = 0; i < known.length; i++) {
            String name = known[i].toString();
            assertTrue(InfixExpression.Operator.toOperator(name).equals(known[i]));
        }
        assertTrue(InfixExpression.Operator.toOperator("huh") == null);
        genericPropertyTest(x, new Property("LeftOperand", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("fie"));
            }

            public ASTNode get() {
                return x.getLeftOperand();
            }

            public void set(ASTNode value) {
                x.setLeftOperand((Expression) value);
            }
        });
        genericPropertyTest(x, new Property("RightOperand", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("fie"));
            }

            public ASTNode get() {
                return x.getRightOperand();
            }

            public void set(ASTNode value) {
                x.setRightOperand((Expression) value);
            }
        });
        genericPropertyListTest(x, x.extendedOperands(), new Property("ExtendedOperands", true, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("x"));
            }
        });
    }

    public void testInstanceofExpression() {
        long previousCount = this.ast.modificationCount();
        final InstanceofExpression x = this.ast.newInstanceofExpression();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getLeftOperand().getParent() == x);
        assertTrue(x.getRightOperand().getParent() == x);
        assertTrue(x.getNodeType() == ASTNode.INSTANCEOF_EXPRESSION);
        assertTrue(x.structuralPropertiesForType() == InstanceofExpression.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("LeftOperand", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("fie"));
            }

            public ASTNode get() {
                return x.getLeftOperand();
            }

            public void set(ASTNode value) {
                x.setLeftOperand((Expression) value);
            }
        });
        genericPropertyTest(x, new Property("RightOperand", true, Type.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Type result = localAst.newSimpleType(localAst.newSimpleName("Object"));
                if (parented) {
                    localAst.newArrayType(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("fie"));
            }

            public ASTNode get() {
                return x.getRightOperand();
            }

            public void set(ASTNode value) {
                x.setRightOperand((Type) value);
            }
        });
    }

    public void testConditionalExpression() {
        long previousCount = this.ast.modificationCount();
        final ConditionalExpression x = this.ast.newConditionalExpression();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getExpression().getParent() == x);
        assertTrue(x.getThenExpression().getParent() == x);
        assertTrue(x.getElseExpression().getParent() == x);
        assertTrue(x.getNodeType() == ASTNode.CONDITIONAL_EXPRESSION);
        assertTrue(x.structuralPropertiesForType() == ConditionalExpression.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Expression", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("fie"));
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
        genericPropertyTest(x, new Property("ThenExpression", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("fie"));
            }

            public ASTNode get() {
                return x.getThenExpression();
            }

            public void set(ASTNode value) {
                x.setThenExpression((Expression) value);
            }
        });
        genericPropertyTest(x, new Property("ElseExpression", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("fie"));
            }

            public ASTNode get() {
                return x.getElseExpression();
            }

            public void set(ASTNode value) {
                x.setElseExpression((Expression) value);
            }
        });
    }

    public void testArrayAccess() {
        long previousCount = this.ast.modificationCount();
        final ArrayAccess x = this.ast.newArrayAccess();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getArray().getParent() == x);
        assertTrue(x.getIndex().getParent() == x);
        assertTrue(x.getNodeType() == ASTNode.ARRAY_ACCESS);
        assertTrue(x.structuralPropertiesForType() == ArrayAccess.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Array", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("fie"));
            }

            public ASTNode get() {
                return x.getArray();
            }

            public void set(ASTNode value) {
                x.setArray((Expression) value);
            }
        });
        genericPropertyTest(x, new Property("Index", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("fie"));
            }

            public ASTNode get() {
                return x.getIndex();
            }

            public void set(ASTNode value) {
                x.setIndex((Expression) value);
            }
        });
    }

    public void testArrayInitializer() {
        long previousCount = this.ast.modificationCount();
        final ArrayInitializer x = this.ast.newArrayInitializer();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.expressions().isEmpty());
        assertTrue(x.getNodeType() == ASTNode.ARRAY_INITIALIZER);
        assertTrue(x.structuralPropertiesForType() == ArrayInitializer.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyListTest(x, x.expressions(), new Property("Expressions", true, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("x"));
            }
        });
    }

    public void testClassInstanceCreation() {
        long previousCount = this.ast.modificationCount();
        final ClassInstanceCreation x = this.ast.newClassInstanceCreation();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getExpression() == null);
        if (this.ast.apiLevel() == AST.JLS2) {
            assertTrue(x.getName().getParent() == x);
        } else {
            assertTrue(x.typeArguments().isEmpty());
            assertTrue(x.getType().getParent() == x);
        }
        assertTrue(x.arguments().isEmpty());
        assertTrue(x.getAnonymousClassDeclaration() == null);
        assertTrue(x.getNodeType() == ASTNode.CLASS_INSTANCE_CREATION);
        assertTrue(x.structuralPropertiesForType() == ClassInstanceCreation.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Expression", false, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("x"));
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            genericPropertyListTest(x, x.typeArguments(), new Property("TypeArguments", true, Type.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    Type result = targetAst.newSimpleType(targetAst.newSimpleName("X"));
                    if (parented) {
                        targetAst.newArrayType(result);
                    }
                    return result;
                }
            });
        }
        if (this.ast.apiLevel() == AST.JLS2) {
            genericPropertyTest(x, new Property("Name", true, Name.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    SimpleName result = targetAst.newSimpleName("a");
                    if (parented) {
                        targetAst.newExpressionStatement(result);
                    }
                    return result;
                }

                public ASTNode get() {
                    return x.getName();
                }

                public void set(ASTNode value) {
                    x.setName((Name) value);
                }
            });
        }
        if (this.ast.apiLevel() >= JLS3_INTERNAL) {
            genericPropertyTest(x, new Property("Type", true, Type.class) {

                public ASTNode sample(AST targetAst, boolean parented) {
                    SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("foo"));
                    if (parented) {
                        targetAst.newArrayType(result);
                    }
                    return result;
                }

                public ASTNode get() {
                    return x.getType();
                }

                public void set(ASTNode value) {
                    x.setType((Type) value);
                }
            });
        }
        genericPropertyListTest(x, x.arguments(), new Property("Arguments", true, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("x"));
            }
        });
        genericPropertyTest(x, new Property("AnonymousClassDeclaration", false, AnonymousClassDeclaration.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                AnonymousClassDeclaration result = targetAst.newAnonymousClassDeclaration();
                if (parented) {
                    targetAst.newClassInstanceCreation().setAnonymousClassDeclaration(result);
                }
                return result;
            }

            public ASTNode wrap() {
                AnonymousClassDeclaration s0 = x.getAST().newAnonymousClassDeclaration();
                VariableDeclarationFragment s1 = x.getAST().newVariableDeclarationFragment();
                FieldDeclaration s2 = x.getAST().newFieldDeclaration(s1);
                s0.bodyDeclarations().add(s2);
                s1.setInitializer(x);
                return s0;
            }

            public void unwrap() {
                VariableDeclarationFragment s1 = (VariableDeclarationFragment) x.getParent();
                s1.setInitializer(null);
            }

            public ASTNode get() {
                return x.getAnonymousClassDeclaration();
            }

            public void set(ASTNode value) {
                x.setAnonymousClassDeclaration((AnonymousClassDeclaration) value);
            }
        });
    }

    public void testAnonymousClassDeclaration() {
        long previousCount = this.ast.modificationCount();
        final AnonymousClassDeclaration x = this.ast.newAnonymousClassDeclaration();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.bodyDeclarations().isEmpty());
        assertTrue(x.getNodeType() == ASTNode.ANONYMOUS_CLASS_DECLARATION);
        assertTrue(x.structuralPropertiesForType() == AnonymousClassDeclaration.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyListTest(x, x.bodyDeclarations(), new Property("BodyDeclarations", true, BodyDeclaration.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                TypeDeclaration result = targetAst.newTypeDeclaration();
                if (parented) {
                    CompilationUnit compilationUnit = targetAst.newCompilationUnit();
                    compilationUnit.types().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                VariableDeclarationFragment s0 = x.getAST().newVariableDeclarationFragment();
                FieldDeclaration s1 = x.getAST().newFieldDeclaration(s0);
                ClassInstanceCreation s2 = x.getAST().newClassInstanceCreation();
                s0.setInitializer(s2);
                s2.setAnonymousClassDeclaration(x);
                return s1;
            }

            public void unwrap() {
                ClassInstanceCreation s2 = (ClassInstanceCreation) x.getParent();
                s2.setAnonymousClassDeclaration(null);
            }
        });
        TypeDeclaration t1 = this.ast.newTypeDeclaration();
        x.bodyDeclarations().add(t1);
        assertTrue(t1.isLocalTypeDeclaration() == false);
        assertTrue(t1.isMemberTypeDeclaration() == true);
        assertTrue(t1.isPackageMemberTypeDeclaration() == false);
    }

    public void testArrayCreation() {
        long previousCount = this.ast.modificationCount();
        final ArrayCreation x = this.ast.newArrayCreation();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getType().getParent() == x);
        assertTrue(x.dimensions().isEmpty());
        assertTrue(x.getInitializer() == null);
        assertTrue(x.getNodeType() == ASTNode.ARRAY_CREATION);
        assertTrue(x.structuralPropertiesForType() == ArrayCreation.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Type", true, ArrayType.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                ArrayType result = targetAst.newArrayType(targetAst.newSimpleType(targetAst.newSimpleName("a")));
                if (parented) {
                    targetAst.newArrayType(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getType();
            }

            public void set(ASTNode value) {
                x.setType((ArrayType) value);
            }
        });
        genericPropertyListTest(x, x.dimensions(), new Property("Dimensions", true, Expression.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("x"));
            }
        });
        genericPropertyTest(x, new Property("Initializer", false, ArrayInitializer.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                ArrayInitializer result = targetAst.newArrayInitializer();
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ArrayInitializer s1 = ASTTest.this.ast.newArrayInitializer();
                s1.expressions().add(x);
                return s1;
            }

            public void unwrap() {
                ArrayInitializer s1 = (ArrayInitializer) x.getParent();
                s1.expressions().remove(x);
            }

            public ASTNode get() {
                return x.getInitializer();
            }

            public void set(ASTNode value) {
                x.setInitializer((ArrayInitializer) value);
            }
        });
    }

    public void testParenthesizedExpression() {
        long previousCount = this.ast.modificationCount();
        final ParenthesizedExpression x = this.ast.newParenthesizedExpression();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getExpression().getParent() == x);
        assertTrue(x.getNodeType() == ASTNode.PARENTHESIZED_EXPRESSION);
        assertTrue(x.structuralPropertiesForType() == ParenthesizedExpression.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Expression", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ParenthesizedExpression s1 = ASTTest.this.ast.newParenthesizedExpression();
                s1.setExpression(x);
                return s1;
            }

            public void unwrap() {
                ParenthesizedExpression s1 = (ParenthesizedExpression) x.getParent();
                s1.setExpression(ASTTest.this.ast.newSimpleName("fie"));
            }

            public ASTNode get() {
                return x.getExpression();
            }

            public void set(ASTNode value) {
                x.setExpression((Expression) value);
            }
        });
    }

    public void testAnnotationTypeDeclaration() {
        if (this.ast.apiLevel() == AST.JLS2) {
            try {
                this.ast.newAnnotationTypeDeclaration();
                assertTrue(false);
            } catch (UnsupportedOperationException e) {
            }
            return;
        }
        long previousCount = this.ast.modificationCount();
        final AnnotationTypeDeclaration x = this.ast.newAnnotationTypeDeclaration();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.modifiers().size() == 0);
        assertTrue(x.getJavadoc() == null);
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.getName().isDeclaration() == true);
        assertTrue(x.bodyDeclarations().size() == 0);
        assertTrue(x.getNodeType() == ASTNode.ANNOTATION_TYPE_DECLARATION);
        assertTrue(x.structuralPropertiesForType() == AnnotationTypeDeclaration.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        previousCount = this.ast.modificationCount();
        tJavadocComment(x);
        tModifiers(x);
        genericPropertyTest(x, new Property("Name", true, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
        genericPropertyListTest(x, x.bodyDeclarations(), new Property("BodyDeclarations", true, BodyDeclaration.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                AnnotationTypeMemberDeclaration result = targetAst.newAnnotationTypeMemberDeclaration();
                if (parented) {
                    AnnotationTypeDeclaration atd = targetAst.newAnnotationTypeDeclaration();
                    atd.bodyDeclarations().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                AnnotationTypeMemberDeclaration s1 = x.getAST().newAnnotationTypeMemberDeclaration();
                ClassInstanceCreation s2 = x.getAST().newClassInstanceCreation();
                AnonymousClassDeclaration s3 = x.getAST().newAnonymousClassDeclaration();
                s1.setDefault(s2);
                s2.setAnonymousClassDeclaration(s3);
                s3.bodyDeclarations().add(x);
                return s1;
            }

            public void unwrap() {
                AnonymousClassDeclaration s3 = (AnonymousClassDeclaration) x.getParent();
                s3.bodyDeclarations().remove(x);
            }
        });
        assertTrue(x.isLocalTypeDeclaration() == false);
        assertTrue(x.isMemberTypeDeclaration() == false);
        assertTrue(x.isPackageMemberTypeDeclaration() == false);
        TypeDeclaration t0 = this.ast.newTypeDeclaration();
        AnnotationTypeDeclaration t1 = this.ast.newAnnotationTypeDeclaration();
        t0.bodyDeclarations().add(t1);
        assertTrue(t1.isLocalTypeDeclaration() == false);
        assertTrue(t1.isMemberTypeDeclaration() == true);
        assertTrue(t1.isPackageMemberTypeDeclaration() == false);
        CompilationUnit t2 = this.ast.newCompilationUnit();
        AnnotationTypeDeclaration t3 = this.ast.newAnnotationTypeDeclaration();
        t2.types().add(t3);
        assertTrue(t3.isLocalTypeDeclaration() == false);
        assertTrue(t3.isMemberTypeDeclaration() == false);
        assertTrue(t3.isPackageMemberTypeDeclaration() == true);
    }

    public void testAnnotationTypeMemberDeclaration() {
        if (this.ast.apiLevel() == AST.JLS2) {
            try {
                this.ast.newAnnotationTypeMemberDeclaration();
                assertTrue(false);
            } catch (UnsupportedOperationException e) {
            }
            return;
        }
        long previousCount = this.ast.modificationCount();
        final AnnotationTypeMemberDeclaration x = this.ast.newAnnotationTypeMemberDeclaration();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.modifiers().size() == 0);
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.getName().isDeclaration() == true);
        assertTrue(x.getType().getParent() == x);
        assertTrue(x.getJavadoc() == null);
        assertTrue(x.getDefault() == null);
        assertTrue(x.getNodeType() == ASTNode.ANNOTATION_TYPE_MEMBER_DECLARATION);
        assertTrue(x.structuralPropertiesForType() == AnnotationTypeMemberDeclaration.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tJavadocComment(x);
        tModifiers(x);
        genericPropertyTest(x, new Property("Name", true, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("foo");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
        genericPropertyTest(x, new Property("Type", true, Type.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleType result = targetAst.newSimpleType(targetAst.newSimpleName("foo"));
                if (parented) {
                    targetAst.newArrayType(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getType();
            }

            public void set(ASTNode value) {
                x.setType((Type) value);
            }
        });
        genericPropertyTest(x, new Property("Default", false, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = x.getAST().newClassInstanceCreation();
                AnonymousClassDeclaration s2 = x.getAST().newAnonymousClassDeclaration();
                s1.setAnonymousClassDeclaration(s2);
                s2.bodyDeclarations().add(x);
                return s1;
            }

            public void unwrap() {
                AnonymousClassDeclaration s2 = (AnonymousClassDeclaration) x.getParent();
                s2.bodyDeclarations().remove(x);
            }

            public ASTNode get() {
                return x.getDefault();
            }

            public void set(ASTNode value) {
                x.setDefault((Expression) value);
            }
        });
    }

    public void testNormalAnnotation() {
        if (this.ast.apiLevel() == AST.JLS2) {
            try {
                this.ast.newNormalAnnotation();
                assertTrue(false);
            } catch (UnsupportedOperationException e) {
            }
            return;
        }
        long previousCount = this.ast.modificationCount();
        final NormalAnnotation x = this.ast.newNormalAnnotation();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getTypeName().getParent() == x);
        assertTrue(x.values().size() == 0);
        assertTrue(x.isAnnotation());
        assertTrue(!x.isModifier());
        assertTrue(!x.isMarkerAnnotation());
        assertTrue(x.isNormalAnnotation());
        assertTrue(!x.isSingleMemberAnnotation());
        assertTrue(x.getNodeType() == ASTNode.NORMAL_ANNOTATION);
        assertTrue(x.structuralPropertiesForType() == NormalAnnotation.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tAnnotationName(x);
        genericPropertyListTest(x, x.values(), new Property("Values", true, MemberValuePair.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                MemberValuePair result = targetAst.newMemberValuePair();
                if (parented) {
                    NormalAnnotation ann = targetAst.newNormalAnnotation();
                    ann.values().add(result);
                }
                return result;
            }

            public ASTNode wrap() {
                MemberValuePair s1 = x.getAST().newMemberValuePair();
                ClassInstanceCreation s2 = x.getAST().newClassInstanceCreation();
                AnonymousClassDeclaration s3 = x.getAST().newAnonymousClassDeclaration();
                MethodDeclaration s4 = x.getAST().newMethodDeclaration();
                s1.setValue(s2);
                s2.setAnonymousClassDeclaration(s3);
                s3.bodyDeclarations().add(s4);
                s4.modifiers().add(x);
                return s1;
            }

            public void unwrap() {
                MethodDeclaration s4 = (MethodDeclaration) x.getParent();
                s4.modifiers().remove(x);
            }
        });
    }

    public void testMarkerAnnotation() {
        if (this.ast.apiLevel() == AST.JLS2) {
            try {
                this.ast.newMarkerAnnotation();
                assertTrue(false);
            } catch (UnsupportedOperationException e) {
            }
            return;
        }
        long previousCount = this.ast.modificationCount();
        final MarkerAnnotation x = this.ast.newMarkerAnnotation();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getTypeName().getParent() == x);
        assertTrue(x.isAnnotation());
        assertTrue(!x.isModifier());
        assertTrue(x.isMarkerAnnotation());
        assertTrue(!x.isNormalAnnotation());
        assertTrue(!x.isSingleMemberAnnotation());
        assertTrue(x.getNodeType() == ASTNode.MARKER_ANNOTATION);
        assertTrue(x.structuralPropertiesForType() == MarkerAnnotation.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        tAnnotationName(x);
    }

    public void testSingleMemberAnnotation() {
        if (this.ast.apiLevel() == AST.JLS2) {
            try {
                this.ast.newSingleMemberAnnotation();
                assertTrue(false);
            } catch (UnsupportedOperationException e) {
            }
            return;
        }
        long previousCount = this.ast.modificationCount();
        final SingleMemberAnnotation x = this.ast.newSingleMemberAnnotation();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getTypeName().getParent() == x);
        assertTrue(x.isAnnotation());
        assertTrue(!x.isModifier());
        assertTrue(!x.isMarkerAnnotation());
        assertTrue(!x.isNormalAnnotation());
        assertTrue(x.isSingleMemberAnnotation());
        assertTrue(x.getNodeType() == ASTNode.SINGLE_MEMBER_ANNOTATION);
        assertTrue(this.ast.modificationCount() == previousCount);
        tAnnotationName(x);
        genericPropertyTest(x, new Property("Value", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = x.getAST().newClassInstanceCreation();
                AnonymousClassDeclaration s2 = x.getAST().newAnonymousClassDeclaration();
                MethodDeclaration s3 = x.getAST().newMethodDeclaration();
                s1.setAnonymousClassDeclaration(s2);
                s2.bodyDeclarations().add(s3);
                s3.modifiers().add(x);
                return s1;
            }

            public void unwrap() {
                MethodDeclaration s3 = (MethodDeclaration) x.getParent();
                s3.modifiers().remove(x);
            }

            public ASTNode get() {
                return x.getValue();
            }

            public void set(ASTNode value) {
                x.setValue((Expression) value);
            }
        });
    }

    public void testMemberValuePair() {
        if (this.ast.apiLevel() == AST.JLS2) {
            try {
                this.ast.newMemberValuePair();
                assertTrue(false);
            } catch (UnsupportedOperationException e) {
            }
            return;
        }
        long previousCount = this.ast.modificationCount();
        final MemberValuePair x = this.ast.newMemberValuePair();
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getName().getParent() == x);
        assertTrue(x.getName().isDeclaration() == false);
        assertTrue(x.getNodeType() == ASTNode.MEMBER_VALUE_PAIR);
        assertTrue(x.structuralPropertiesForType() == MemberValuePair.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        genericPropertyTest(x, new Property("Name", true, SimpleName.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("a");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getName();
            }

            public void set(ASTNode value) {
                x.setName((SimpleName) value);
            }
        });
        genericPropertyTest(x, new Property("Value", true, Expression.class) {

            public ASTNode sample(AST localAst, boolean parented) {
                Expression result = localAst.newSimpleName("foo");
                if (parented) {
                    localAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode wrap() {
                ClassInstanceCreation s1 = x.getAST().newClassInstanceCreation();
                AnonymousClassDeclaration s2 = x.getAST().newAnonymousClassDeclaration();
                MethodDeclaration s3 = x.getAST().newMethodDeclaration();
                NormalAnnotation s4 = x.getAST().newNormalAnnotation();
                s1.setAnonymousClassDeclaration(s2);
                s2.bodyDeclarations().add(s3);
                s3.modifiers().add(s4);
                s4.values().add(x);
                return s1;
            }

            public void unwrap() {
                NormalAnnotation s4 = (NormalAnnotation) x.getParent();
                s4.values().remove(x);
            }

            public ASTNode get() {
                return x.getValue();
            }

            public void set(ASTNode value) {
                x.setValue((Expression) value);
            }
        });
    }

    void tAnnotationName(final Annotation x) {
        genericPropertyTest(x, new Property("TypeName", true, Name.class) {

            public ASTNode sample(AST targetAst, boolean parented) {
                SimpleName result = targetAst.newSimpleName("a");
                if (parented) {
                    targetAst.newExpressionStatement(result);
                }
                return result;
            }

            public ASTNode get() {
                return x.getTypeName();
            }

            public void set(ASTNode value) {
                x.setTypeName((Name) value);
            }
        });
    }

    public void testModifiers() {
        assertSame(Modifier.ABSTRACT, 0x0400);
        assertSame(Modifier.FINAL, 0x0010);
        assertSame(Modifier.NATIVE, 0x0100);
        assertSame(Modifier.NONE, 0x0000);
        assertSame(Modifier.PRIVATE, 0x0002);
        assertSame(Modifier.PROTECTED, 0x0004);
        assertSame(Modifier.PUBLIC, 0x0001);
        assertSame(Modifier.STATIC, 0x0008);
        assertSame(Modifier.STRICTFP, 0x0800);
        assertSame(Modifier.SYNCHRONIZED, 0x0020);
        assertSame(Modifier.TRANSIENT, 0x0080);
        assertSame(Modifier.VOLATILE, 0x0040);
        final int[] mods = { Modifier.ABSTRACT, Modifier.FINAL, Modifier.NATIVE, Modifier.PRIVATE, Modifier.PROTECTED, Modifier.PUBLIC, Modifier.STATIC, Modifier.STRICTFP, Modifier.SYNCHRONIZED, Modifier.TRANSIENT, Modifier.VOLATILE };
        for (int i = 0; i < mods.length; i++) {
            int m = mods[i];
            assertTrue(Modifier.isAbstract(m) == (m == Modifier.ABSTRACT));
            assertTrue(Modifier.isFinal(m) == (m == Modifier.FINAL));
            assertTrue(Modifier.isNative(m) == (m == Modifier.NATIVE));
            assertTrue(Modifier.isPrivate(m) == (m == Modifier.PRIVATE));
            assertTrue(Modifier.isProtected(m) == (m == Modifier.PROTECTED));
            assertTrue(Modifier.isPublic(m) == (m == Modifier.PUBLIC));
            assertTrue(Modifier.isStatic(m) == (m == Modifier.STATIC));
            assertTrue(Modifier.isStrictfp(m) == (m == Modifier.STRICTFP));
            assertTrue(Modifier.isSynchronized(m) == (m == Modifier.SYNCHRONIZED));
            assertTrue(Modifier.isTransient(m) == (m == Modifier.TRANSIENT));
            assertTrue(Modifier.isVolatile(m) == (m == Modifier.VOLATILE));
        }
        if (this.ast.apiLevel() == AST.JLS2) {
            try {
                this.ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
                assertTrue(false);
            } catch (UnsupportedOperationException e) {
            }
            try {
                this.ast.newModifiers(Modifier.NONE);
                assertTrue(false);
            } catch (UnsupportedOperationException e) {
            }
            return;
        }
        long previousCount = this.ast.modificationCount();
        final Modifier x = this.ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
        assertTrue(this.ast.modificationCount() > previousCount);
        previousCount = this.ast.modificationCount();
        assertTrue(x.getAST() == this.ast);
        assertTrue(x.getParent() == null);
        assertTrue(x.getKeyword() == Modifier.ModifierKeyword.PUBLIC_KEYWORD);
        assertTrue(x.getNodeType() == ASTNode.MODIFIER);
        assertTrue(x.structuralPropertiesForType() == Modifier.propertyDescriptors(this.ast.apiLevel()));
        assertTrue(this.ast.modificationCount() == previousCount);
        assertTrue(Modifier.ModifierKeyword.PUBLIC_KEYWORD.toString().equals("public"));
        assertTrue(Modifier.ModifierKeyword.PROTECTED_KEYWORD.toString().equals("protected"));
        assertTrue(Modifier.ModifierKeyword.PRIVATE_KEYWORD.toString().equals("private"));
        assertTrue(Modifier.ModifierKeyword.STATIC_KEYWORD.toString().equals("static"));
        assertTrue(Modifier.ModifierKeyword.ABSTRACT_KEYWORD.toString().equals("abstract"));
        assertTrue(Modifier.ModifierKeyword.FINAL_KEYWORD.toString().equals("final"));
        assertTrue(Modifier.ModifierKeyword.SYNCHRONIZED_KEYWORD.toString().equals("synchronized"));
        assertTrue(Modifier.ModifierKeyword.NATIVE_KEYWORD.toString().equals("native"));
        assertTrue(Modifier.ModifierKeyword.TRANSIENT_KEYWORD.toString().equals("transient"));
        assertTrue(Modifier.ModifierKeyword.VOLATILE_KEYWORD.toString().equals("volatile"));
        assertTrue(Modifier.ModifierKeyword.STRICTFP_KEYWORD.toString().equals("strictfp"));
        assertTrue(Modifier.ModifierKeyword.DEFAULT_KEYWORD.toString().equals("default"));
        final Modifier.ModifierKeyword[] known = { Modifier.ModifierKeyword.PUBLIC_KEYWORD, Modifier.ModifierKeyword.PROTECTED_KEYWORD, Modifier.ModifierKeyword.PRIVATE_KEYWORD, Modifier.ModifierKeyword.STATIC_KEYWORD, Modifier.ModifierKeyword.ABSTRACT_KEYWORD, Modifier.ModifierKeyword.FINAL_KEYWORD, Modifier.ModifierKeyword.SYNCHRONIZED_KEYWORD, Modifier.ModifierKeyword.NATIVE_KEYWORD, Modifier.ModifierKeyword.TRANSIENT_KEYWORD, Modifier.ModifierKeyword.VOLATILE_KEYWORD, Modifier.ModifierKeyword.STRICTFP_KEYWORD, Modifier.ModifierKeyword.DEFAULT_KEYWORD };
        for (int i = 0; i < known.length; i++) {
            for (int j = 0; j < known.length; j++) {
                assertTrue(i == j || !known[i].equals(known[j]));
            }
        }
        for (int i = 0; i < known.length; i++) {
            previousCount = this.ast.modificationCount();
            x.setKeyword(known[i]);
            assertTrue(this.ast.modificationCount() > previousCount);
            assertTrue(x.getKeyword().equals(known[i]));
        }
        try {
            x.setKeyword(null);
            assertTrue(false);
        } catch (RuntimeException e) {
        }
        for (int i = 0; i < known.length; i++) {
            String name = known[i].toString();
            assertTrue(Modifier.ModifierKeyword.toKeyword(name).equals(known[i]));
        }
        assertTrue(Modifier.ModifierKeyword.toKeyword("huh") == null);
        for (int i = 0; i < mods.length; i++) {
            int m = mods[i];
            List result = this.ast.newModifiers(m);
            assertEquals(1, result.size());
            Modifier modNode = (Modifier) result.get(0);
            assertEquals(m, modNode.getKeyword().toFlagValue());
        }
        final Modifier.ModifierKeyword[] expectedOrder = { Modifier.ModifierKeyword.PUBLIC_KEYWORD, Modifier.ModifierKeyword.PROTECTED_KEYWORD, Modifier.ModifierKeyword.PRIVATE_KEYWORD, Modifier.ModifierKeyword.ABSTRACT_KEYWORD, Modifier.ModifierKeyword.STATIC_KEYWORD, Modifier.ModifierKeyword.FINAL_KEYWORD, Modifier.ModifierKeyword.SYNCHRONIZED_KEYWORD, Modifier.ModifierKeyword.NATIVE_KEYWORD, Modifier.ModifierKeyword.STRICTFP_KEYWORD, Modifier.ModifierKeyword.TRANSIENT_KEYWORD, Modifier.ModifierKeyword.VOLATILE_KEYWORD };
        int all = 0;
        for (int i = 0; i < mods.length; i++) {
            all |= mods[i];
        }
        List result = this.ast.newModifiers(all);
        assertEquals(expectedOrder.length, result.size());
        for (int i = 0; i < expectedOrder.length; i++) {
            final Modifier modifier = ((Modifier) result.get(i));
            assertEquals(expectedOrder[i], modifier.getKeyword());
            if (modifier.isAbstract()) {
                assertEquals(Modifier.ModifierKeyword.ABSTRACT_KEYWORD, modifier.getKeyword());
            } else if (modifier.isFinal()) {
                assertEquals(Modifier.ModifierKeyword.FINAL_KEYWORD, modifier.getKeyword());
            } else if (modifier.isNative()) {
                assertEquals(Modifier.ModifierKeyword.NATIVE_KEYWORD, modifier.getKeyword());
            } else if (modifier.isPrivate()) {
                assertEquals(Modifier.ModifierKeyword.PRIVATE_KEYWORD, modifier.getKeyword());
            } else if (modifier.isProtected()) {
                assertEquals(Modifier.ModifierKeyword.PROTECTED_KEYWORD, modifier.getKeyword());
            } else if (modifier.isPublic()) {
                assertEquals(Modifier.ModifierKeyword.PUBLIC_KEYWORD, modifier.getKeyword());
            } else if (modifier.isStatic()) {
                assertEquals(Modifier.ModifierKeyword.STATIC_KEYWORD, modifier.getKeyword());
            } else if (modifier.isStrictfp()) {
                assertEquals(Modifier.ModifierKeyword.STRICTFP_KEYWORD, modifier.getKeyword());
            } else if (modifier.isSynchronized()) {
                assertEquals(Modifier.ModifierKeyword.SYNCHRONIZED_KEYWORD, modifier.getKeyword());
            } else if (modifier.isTransient()) {
                assertEquals(Modifier.ModifierKeyword.TRANSIENT_KEYWORD, modifier.getKeyword());
            } else {
                assertEquals(Modifier.ModifierKeyword.VOLATILE_KEYWORD, modifier.getKeyword());
            }
        }
    }

    public void testSubtreeBytes() {
        ASTNode x = SampleASTs.oneOfEach(this.ast);
        final int subtreeBytes = x.subtreeBytes();
        assertTrue(subtreeBytes > 0);
    }

    public void testNodeTypeConstants() throws Exception {
        int[] nodeTypes = { ASTNode.ANONYMOUS_CLASS_DECLARATION, ASTNode.ARRAY_ACCESS, ASTNode.ARRAY_CREATION, ASTNode.ARRAY_INITIALIZER, ASTNode.ARRAY_TYPE, ASTNode.ASSERT_STATEMENT, ASTNode.ASSIGNMENT, ASTNode.BLOCK, ASTNode.BOOLEAN_LITERAL, ASTNode.BREAK_STATEMENT, ASTNode.CAST_EXPRESSION, ASTNode.CATCH_CLAUSE, ASTNode.CHARACTER_LITERAL, ASTNode.CLASS_INSTANCE_CREATION, ASTNode.COMPILATION_UNIT, ASTNode.CONDITIONAL_EXPRESSION, ASTNode.CONSTRUCTOR_INVOCATION, ASTNode.CONTINUE_STATEMENT, ASTNode.DO_STATEMENT, ASTNode.EMPTY_STATEMENT, ASTNode.EXPRESSION_STATEMENT, ASTNode.FIELD_ACCESS, ASTNode.FIELD_DECLARATION, ASTNode.FOR_STATEMENT, ASTNode.IF_STATEMENT, ASTNode.IMPORT_DECLARATION, ASTNode.INFIX_EXPRESSION, ASTNode.INITIALIZER, ASTNode.JAVADOC, ASTNode.LABELED_STATEMENT, ASTNode.METHOD_DECLARATION, ASTNode.METHOD_INVOCATION, ASTNode.NULL_LITERAL, ASTNode.NUMBER_LITERAL, ASTNode.PACKAGE_DECLARATION, ASTNode.PARENTHESIZED_EXPRESSION, ASTNode.POSTFIX_EXPRESSION, ASTNode.PREFIX_EXPRESSION, ASTNode.PRIMITIVE_TYPE, ASTNode.QUALIFIED_NAME, ASTNode.RETURN_STATEMENT, ASTNode.SIMPLE_NAME, ASTNode.SIMPLE_TYPE, ASTNode.SINGLE_VARIABLE_DECLARATION, ASTNode.STRING_LITERAL, ASTNode.SUPER_CONSTRUCTOR_INVOCATION, ASTNode.SUPER_FIELD_ACCESS, ASTNode.SUPER_METHOD_INVOCATION, ASTNode.SWITCH_CASE, ASTNode.SWITCH_STATEMENT, ASTNode.SYNCHRONIZED_STATEMENT, ASTNode.THIS_EXPRESSION, ASTNode.THROW_STATEMENT, ASTNode.TRY_STATEMENT, ASTNode.TYPE_DECLARATION, ASTNode.TYPE_DECLARATION_STATEMENT, ASTNode.TYPE_LITERAL, ASTNode.VARIABLE_DECLARATION_EXPRESSION, ASTNode.VARIABLE_DECLARATION_FRAGMENT, ASTNode.VARIABLE_DECLARATION_STATEMENT, ASTNode.WHILE_STATEMENT, ASTNode.INSTANCEOF_EXPRESSION, ASTNode.LINE_COMMENT, ASTNode.BLOCK_COMMENT, ASTNode.TAG_ELEMENT, ASTNode.TEXT_ELEMENT, ASTNode.MEMBER_REF, ASTNode.METHOD_REF, ASTNode.METHOD_REF_PARAMETER, ASTNode.ENHANCED_FOR_STATEMENT, ASTNode.ENUM_DECLARATION, ASTNode.ENUM_CONSTANT_DECLARATION, ASTNode.TYPE_PARAMETER, ASTNode.PARAMETERIZED_TYPE, ASTNode.QUALIFIED_TYPE, ASTNode.WILDCARD_TYPE, ASTNode.NORMAL_ANNOTATION, ASTNode.MARKER_ANNOTATION, ASTNode.SINGLE_MEMBER_ANNOTATION, ASTNode.MEMBER_VALUE_PAIR, ASTNode.ANNOTATION_TYPE_DECLARATION, ASTNode.ANNOTATION_TYPE_MEMBER_DECLARATION, ASTNode.MODIFIER, ASTNode.UNION_TYPE, ASTNode.DIMENSION, ASTNode.LAMBDA_EXPRESSION, ASTNode.INTERSECTION_TYPE, ASTNode.NAME_QUALIFIED_TYPE, ASTNode.CREATION_REFERENCE, ASTNode.EXPRESSION_METHOD_REFERENCE, ASTNode.SUPER_METHOD_REFERENCE, ASTNode.TYPE_METHOD_REFERENCE };
        for (int i = 0; i < nodeTypes.length; i++) {
            assertSame(i + 1, nodeTypes[i]);
        }
        for (int i = 0; i < nodeTypes.length; i++) {
            int nodeType = nodeTypes[i];
            ASTNode node;
            try {
                node = this.ast.createInstance(nodeType);
            } catch (IllegalArgumentException e) {
                if (this.API_LEVEL < AST.JLS8 && e.getCause() instanceof UnsupportedOperationException) {
                    continue;
                } else {
                    throw new AssertionFailedError("missing node type: " + nodeType);
                }
            }
            assertEquals(nodeType, node.getNodeType());
        }
        Field[] fields = ASTNode.class.getDeclaredFields();
        HashSet declaredNodeTypes = new HashSet();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getType() != int.class)
                continue;
            if (field.getModifiers() != (java.lang.reflect.Modifier.PUBLIC | java.lang.reflect.Modifier.STATIC | java.lang.reflect.Modifier.FINAL))
                continue;
            String name = field.getName();
            if ("MALFORMED".equals(name) || "ORIGINAL".equals(name) || "PROTECT".equals(name) || "RECOVERED".equals(name))
                continue;
            declaredNodeTypes.add(Integer.valueOf(field.getInt(null)));
        }
        for (int i = 0; i < nodeTypes.length; i++) {
            int nodeType = nodeTypes[i];
            assertTrue("node type " + nodeType + " from test is missing in ASTNode", declaredNodeTypes.remove(Integer.valueOf(nodeType)));
            nodeTypes[i] = -1;
        }
        for (int i = 0; i < nodeTypes.length; i++) {
            int nodeType = nodeTypes[i];
            assertEquals("node type " + nodeType + " missing in ASTNode", -1, nodeType);
        }
        assertEquals("node types missing in test", Collections.EMPTY_SET, declaredNodeTypes);
    }
}
