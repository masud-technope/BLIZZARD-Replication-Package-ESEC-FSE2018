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
package org.eclipse.jdt.internal.core.dom;

import java.util.Iterator;
import java.util.List;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;

/**
 * Internal AST visitor for serializing an AST in a quick and dirty fashion.
 * For various reasons the resulting string is not necessarily legal
 * Java code; and even if it is legal Java code, it is not necessarily the string
 * that corresponds to the given AST. Although useless for most purposes, it's
 * fine for generating debug print strings.
 * <p>
 * Example usage:
 * <code>
 * <pre>
 *    NaiveASTFlattener p = new NaiveASTFlattener();
 *    node.accept(p);
 *    String result = p.getResult();
 * </pre>
 * </code>
 * Call the <code>reset</code> method to clear the previous result before reusing an
 * existing instance.
 * </p>
 *
 * @since 2.0
 */
@SuppressWarnings("rawtypes")
public class NaiveASTFlattener extends ASTVisitor {

    /**
	 * Internal synonym for {@link AST#JLS2}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.4
	 */
    private static final int JLS2 = AST.JLS2;

    /**
	 * Internal synonym for {@link AST#JLS3}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.4
	 */
    private static final int JLS3 = AST.JLS3;

    /**
	 * Internal synonym for {@link AST#JLS4}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.10
	 */
    private static final int JLS4 = AST.JLS4;

    /**
	 * The string buffer into which the serialized representation of the AST is
	 * written.
	 */
    protected StringBuffer buffer;

    private int indent = 0;

    /**
	 * Creates a new AST printer.
	 */
    public  NaiveASTFlattener() {
        this.buffer = new StringBuffer();
    }

    /**
	 * Internal synonym for {@link ClassInstanceCreation#getName()}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.4
	 */
    private Name getName(ClassInstanceCreation node) {
        return node.getName();
    }

    /**
	 * Returns the string accumulated in the visit.
	 *
	 * @return the serialized
	 */
    public String getResult() {
        return this.buffer.toString();
    }

    /**
	 * Internal synonym for {@link MethodDeclaration#getReturnType()}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.4
	 */
    private static Type getReturnType(MethodDeclaration node) {
        return node.getReturnType();
    }

    /**
	 * Internal synonym for {@link TypeDeclaration#getSuperclass()}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.4
	 */
    private static Name getSuperclass(TypeDeclaration node) {
        return node.getSuperclass();
    }

    /**
	 * Internal synonym for {@link TypeDeclarationStatement#getTypeDeclaration()}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.4
	 */
    private static TypeDeclaration getTypeDeclaration(TypeDeclarationStatement node) {
        return node.getTypeDeclaration();
    }

    /**
	 * Internal synonym for {@link MethodDeclaration#thrownExceptions()}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.10
	 */
    private static List thrownExceptions(MethodDeclaration node) {
        return node.thrownExceptions();
    }

    void printIndent() {
        for (int i = 0; i < this.indent; i++) //$NON-NLS-1$
        this.buffer.append("  ");
    }

    /**
	 * Appends the text representation of the given modifier flags, followed by a single space.
	 * Used for JLS2 modifiers.
	 *
	 * @param modifiers the modifier flags
	 */
    void printModifiers(int modifiers) {
        if (Modifier.isPublic(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("public ");
        }
        if (Modifier.isProtected(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("protected ");
        }
        if (Modifier.isPrivate(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("private ");
        }
        if (Modifier.isStatic(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("static ");
        }
        if (Modifier.isAbstract(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("abstract ");
        }
        if (Modifier.isFinal(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("final ");
        }
        if (Modifier.isSynchronized(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("synchronized ");
        }
        if (Modifier.isVolatile(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("volatile ");
        }
        if (Modifier.isNative(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("native ");
        }
        if (Modifier.isStrictfp(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("strictfp ");
        }
        if (Modifier.isTransient(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("transient ");
        }
    }

    /**
	 * Appends the text representation of the given modifier flags, followed by a single space.
	 * Used for 3.0 modifiers and annotations.
	 *
	 * @param ext the list of modifier and annotation nodes
	 * (element type: <code>IExtendedModifiers</code>)
	 */
    void printModifiers(List ext) {
        for (Iterator it = ext.iterator(); it.hasNext(); ) {
            ASTNode p = (ASTNode) it.next();
            p.accept(this);
            //$NON-NLS-1$
            this.buffer.append(" ");
        }
    }

    /**
	 * reference node helper function that is common to all
	 * the difference reference nodes.
	 * 
	 * @param typeArguments list of type arguments 
	 */
    private void visitReferenceTypeArguments(List typeArguments) {
        //$NON-NLS-1$
        this.buffer.append("::");
        if (!typeArguments.isEmpty()) {
            this.buffer.append('<');
            for (Iterator it = typeArguments.iterator(); it.hasNext(); ) {
                Type t = (Type) it.next();
                t.accept(this);
                if (it.hasNext()) {
                    this.buffer.append(',');
                }
            }
            this.buffer.append('>');
        }
    }

    private void visitTypeAnnotations(AnnotatableType node) {
        if (node.getAST().apiLevel() >= AST.JLS8) {
            visitAnnotationsList(node.annotations());
        }
    }

    private void visitAnnotationsList(List annotations) {
        for (Iterator it = annotations.iterator(); it.hasNext(); ) {
            Annotation annotation = (Annotation) it.next();
            annotation.accept(this);
            this.buffer.append(' ');
        }
    }

    /**
	 * Resets this printer so that it can be used again.
	 */
    public void reset() {
        this.buffer.setLength(0);
    }

    /**
	 * Internal synonym for {@link TypeDeclaration#superInterfaces()}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.4
	 */
    private List superInterfaces(TypeDeclaration node) {
        return node.superInterfaces();
    }

    /*
	 * @see ASTVisitor#visit(AnnotationTypeDeclaration)
	 * @since 3.1
	 */
    public boolean visit(AnnotationTypeDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        printIndent();
        printModifiers(node.modifiers());
        //$NON-NLS-1$
        this.buffer.append("@interface ");
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" {");
        for (Iterator it = node.bodyDeclarations().iterator(); it.hasNext(); ) {
            BodyDeclaration d = (BodyDeclaration) it.next();
            d.accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append("}\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(AnnotationTypeMemberDeclaration)
	 * @since 3.1
	 */
    public boolean visit(AnnotationTypeMemberDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        printIndent();
        printModifiers(node.modifiers());
        node.getType().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" ");
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append("()");
        if (node.getDefault() != null) {
            //$NON-NLS-1$
            this.buffer.append(" default ");
            node.getDefault().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(AnonymousClassDeclaration)
	 */
    public boolean visit(AnonymousClassDeclaration node) {
        //$NON-NLS-1$
        this.buffer.append("{\n");
        this.indent++;
        for (Iterator it = node.bodyDeclarations().iterator(); it.hasNext(); ) {
            BodyDeclaration b = (BodyDeclaration) it.next();
            b.accept(this);
        }
        this.indent--;
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("}\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(ArrayAccess)
	 */
    public boolean visit(ArrayAccess node) {
        node.getArray().accept(this);
        //$NON-NLS-1$
        this.buffer.append("[");
        node.getIndex().accept(this);
        //$NON-NLS-1$
        this.buffer.append("]");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(ArrayCreation)
	 */
    public boolean visit(ArrayCreation node) {
        //$NON-NLS-1$
        this.buffer.append("new ");
        ArrayType at = node.getType();
        int dims = at.getDimensions();
        Type elementType = at.getElementType();
        elementType.accept(this);
        for (Iterator it = node.dimensions().iterator(); it.hasNext(); ) {
            //$NON-NLS-1$
            this.buffer.append("[");
            Expression e = (Expression) it.next();
            e.accept(this);
            //$NON-NLS-1$
            this.buffer.append("]");
            dims--;
        }
        // add empty "[]" for each extra array dimension
        for (int i = 0; i < dims; i++) {
            //$NON-NLS-1$
            this.buffer.append("[]");
        }
        if (node.getInitializer() != null) {
            node.getInitializer().accept(this);
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(ArrayInitializer)
	 */
    public boolean visit(ArrayInitializer node) {
        //$NON-NLS-1$
        this.buffer.append("{");
        for (Iterator it = node.expressions().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            e.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append("}");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(ArrayType)
	 */
    public boolean visit(ArrayType node) {
        if (node.getAST().apiLevel() < AST.JLS8) {
            visitComponentType(node);
            //$NON-NLS-1$
            this.buffer.append("[]");
        } else {
            node.getElementType().accept(this);
            List dimensions = node.dimensions();
            int size = dimensions.size();
            for (int i = 0; i < size; i++) {
                Dimension aDimension = (Dimension) dimensions.get(i);
                aDimension.accept(this);
            }
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(AssertStatement)
	 */
    public boolean visit(AssertStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("assert ");
        node.getExpression().accept(this);
        if (node.getMessage() != null) {
            //$NON-NLS-1$
            this.buffer.append(" : ");
            node.getMessage().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(Assignment)
	 */
    public boolean visit(Assignment node) {
        node.getLeftHandSide().accept(this);
        this.buffer.append(node.getOperator().toString());
        node.getRightHandSide().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(Block)
	 */
    public boolean visit(Block node) {
        //$NON-NLS-1$
        this.buffer.append("{\n");
        this.indent++;
        for (Iterator it = node.statements().iterator(); it.hasNext(); ) {
            Statement s = (Statement) it.next();
            s.accept(this);
        }
        this.indent--;
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("}\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(BlockComment)
	 * @since 3.0
	 */
    public boolean visit(BlockComment node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("/* */");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(BooleanLiteral)
	 */
    public boolean visit(BooleanLiteral node) {
        if (node.booleanValue() == true) {
            //$NON-NLS-1$
            this.buffer.append("true");
        } else {
            //$NON-NLS-1$
            this.buffer.append("false");
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(BreakStatement)
	 */
    public boolean visit(BreakStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("break");
        if (node.getLabel() != null) {
            //$NON-NLS-1$
            this.buffer.append(" ");
            node.getLabel().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(CastExpression)
	 */
    public boolean visit(CastExpression node) {
        //$NON-NLS-1$
        this.buffer.append("(");
        node.getType().accept(this);
        //$NON-NLS-1$
        this.buffer.append(")");
        node.getExpression().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(CatchClause)
	 */
    public boolean visit(CatchClause node) {
        //$NON-NLS-1$
        this.buffer.append("catch (");
        node.getException().accept(this);
        //$NON-NLS-1$
        this.buffer.append(") ");
        node.getBody().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(CharacterLiteral)
	 */
    public boolean visit(CharacterLiteral node) {
        this.buffer.append(node.getEscapedValue());
        return false;
    }

    /*
	 * @see ASTVisitor#visit(ClassInstanceCreation)
	 */
    public boolean visit(ClassInstanceCreation node) {
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
            //$NON-NLS-1$
            this.buffer.append(".");
        }
        //$NON-NLS-1$
        this.buffer.append("new ");
        if (node.getAST().apiLevel() == JLS2) {
            getName(node).accept(this);
        }
        if (node.getAST().apiLevel() >= JLS3) {
            if (!node.typeArguments().isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                "<");
                for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
                    Type t = (Type) it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(//$NON-NLS-1$
                        ",");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ">");
            }
            node.getType().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append("(");
        for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            e.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(")");
        if (node.getAnonymousClassDeclaration() != null) {
            node.getAnonymousClassDeclaration().accept(this);
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(CompilationUnit)
	 */
    public boolean visit(CompilationUnit node) {
        if (node.getPackage() != null) {
            node.getPackage().accept(this);
        }
        for (Iterator it = node.imports().iterator(); it.hasNext(); ) {
            ImportDeclaration d = (ImportDeclaration) it.next();
            d.accept(this);
        }
        for (Iterator it = node.types().iterator(); it.hasNext(); ) {
            AbstractTypeDeclaration d = (AbstractTypeDeclaration) it.next();
            d.accept(this);
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(ConditionalExpression)
	 */
    public boolean visit(ConditionalExpression node) {
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" ? ");
        node.getThenExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" : ");
        node.getElseExpression().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(ConstructorInvocation)
	 */
    public boolean visit(ConstructorInvocation node) {
        printIndent();
        if (node.getAST().apiLevel() >= JLS3) {
            if (!node.typeArguments().isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                "<");
                for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
                    Type t = (Type) it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(//$NON-NLS-1$
                        ",");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ">");
            }
        }
        //$NON-NLS-1$
        this.buffer.append("this(");
        for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            e.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(");\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(ContinueStatement)
	 */
    public boolean visit(ContinueStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("continue");
        if (node.getLabel() != null) {
            //$NON-NLS-1$
            this.buffer.append(" ");
            node.getLabel().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(CreationReference)
	 * 
	 * @since 3.10
	 */
    public boolean visit(CreationReference node) {
        node.getType().accept(this);
        visitReferenceTypeArguments(node.typeArguments());
        //$NON-NLS-1$
        this.buffer.append("new");
        return false;
    }

    public boolean visit(Dimension node) {
        List annotations = node.annotations();
        if (annotations.size() > 0)
            this.buffer.append(' ');
        visitAnnotationsList(annotations);
        //$NON-NLS-1$
        this.buffer.append("[]");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(DoStatement)
	 */
    public boolean visit(DoStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("do ");
        node.getBody().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" while (");
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(");\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(EmptyStatement)
	 */
    public boolean visit(EmptyStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(EnhancedForStatement)
	 * @since 3.1
	 */
    public boolean visit(EnhancedForStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("for (");
        node.getParameter().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" : ");
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(") ");
        node.getBody().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(EnumConstantDeclaration)
	 * @since 3.1
	 */
    public boolean visit(EnumConstantDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        printIndent();
        printModifiers(node.modifiers());
        node.getName().accept(this);
        if (!node.arguments().isEmpty()) {
            //$NON-NLS-1$
            this.buffer.append("(");
            for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
                Expression e = (Expression) it.next();
                e.accept(this);
                if (it.hasNext()) {
                    //$NON-NLS-1$
                    this.buffer.append(//$NON-NLS-1$
                    ",");
                }
            }
            //$NON-NLS-1$
            this.buffer.append(")");
        }
        if (node.getAnonymousClassDeclaration() != null) {
            node.getAnonymousClassDeclaration().accept(this);
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(EnumDeclaration)
	 * @since 3.1
	 */
    public boolean visit(EnumDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        printIndent();
        printModifiers(node.modifiers());
        //$NON-NLS-1$
        this.buffer.append("enum ");
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" ");
        if (!node.superInterfaceTypes().isEmpty()) {
            //$NON-NLS-1$
            this.buffer.append("implements ");
            for (Iterator it = node.superInterfaceTypes().iterator(); it.hasNext(); ) {
                Type t = (Type) it.next();
                t.accept(this);
                if (it.hasNext()) {
                    //$NON-NLS-1$
                    this.buffer.append(//$NON-NLS-1$
                    ", ");
                }
            }
            //$NON-NLS-1$
            this.buffer.append(" ");
        }
        //$NON-NLS-1$
        this.buffer.append("{");
        for (Iterator it = node.enumConstants().iterator(); it.hasNext(); ) {
            EnumConstantDeclaration d = (EnumConstantDeclaration) it.next();
            d.accept(this);
            // enum constant declarations do not include punctuation
            if (it.hasNext()) {
                // enum constant declarations are separated by commas
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ", ");
            }
        }
        if (!node.bodyDeclarations().isEmpty()) {
            //$NON-NLS-1$
            this.buffer.append("; ");
            for (Iterator it = node.bodyDeclarations().iterator(); it.hasNext(); ) {
                BodyDeclaration d = (BodyDeclaration) it.next();
                d.accept(this);
            // other body declarations include trailing punctuation
            }
        }
        //$NON-NLS-1$
        this.buffer.append("}\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(ExpressionMethodReference)
	 * 
	 * @since 3.10
	 */
    public boolean visit(ExpressionMethodReference node) {
        node.getExpression().accept(this);
        visitReferenceTypeArguments(node.typeArguments());
        node.getName().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(ExpressionStatement)
	 */
    public boolean visit(ExpressionStatement node) {
        printIndent();
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(FieldAccess)
	 */
    public boolean visit(FieldAccess node) {
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(".");
        node.getName().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(FieldDeclaration)
	 */
    public boolean visit(FieldDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        printIndent();
        if (node.getAST().apiLevel() == JLS2) {
            printModifiers(node.getModifiers());
        }
        if (node.getAST().apiLevel() >= JLS3) {
            printModifiers(node.modifiers());
        }
        node.getType().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" ");
        for (Iterator it = node.fragments().iterator(); it.hasNext(); ) {
            VariableDeclarationFragment f = (VariableDeclarationFragment) it.next();
            f.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ", ");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(ForStatement)
	 */
    public boolean visit(ForStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("for (");
        for (Iterator it = node.initializers().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            e.accept(this);
            //$NON-NLS-1$
            if (it.hasNext())
                this.buffer.append(", ");
        }
        //$NON-NLS-1$
        this.buffer.append("; ");
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append("; ");
        for (Iterator it = node.updaters().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            e.accept(this);
            //$NON-NLS-1$
            if (it.hasNext())
                this.buffer.append(", ");
        }
        //$NON-NLS-1$
        this.buffer.append(") ");
        node.getBody().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(IfStatement)
	 */
    public boolean visit(IfStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("if (");
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(") ");
        node.getThenStatement().accept(this);
        if (node.getElseStatement() != null) {
            //$NON-NLS-1$
            this.buffer.append(" else ");
            node.getElseStatement().accept(this);
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(ImportDeclaration)
	 */
    public boolean visit(ImportDeclaration node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("import ");
        if (node.getAST().apiLevel() >= JLS3) {
            if (node.isStatic()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                "static ");
            }
        }
        node.getName().accept(this);
        if (node.isOnDemand()) {
            //$NON-NLS-1$
            this.buffer.append(".*");
        }
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(InfixExpression)
	 */
    public boolean visit(InfixExpression node) {
        node.getLeftOperand().accept(this);
        // for cases like x= i - -1; or x= i++ + ++i;
        this.buffer.append(' ');
        this.buffer.append(node.getOperator().toString());
        this.buffer.append(' ');
        node.getRightOperand().accept(this);
        final List extendedOperands = node.extendedOperands();
        if (extendedOperands.size() != 0) {
            this.buffer.append(' ');
            for (Iterator it = extendedOperands.iterator(); it.hasNext(); ) {
                this.buffer.append(node.getOperator().toString()).append(' ');
                Expression e = (Expression) it.next();
                e.accept(this);
            }
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(Initializer)
	 */
    public boolean visit(Initializer node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        if (node.getAST().apiLevel() == JLS2) {
            printModifiers(node.getModifiers());
        }
        if (node.getAST().apiLevel() >= JLS3) {
            printModifiers(node.modifiers());
        }
        node.getBody().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(InstanceofExpression)
	 */
    public boolean visit(InstanceofExpression node) {
        node.getLeftOperand().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" instanceof ");
        node.getRightOperand().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(IntersectionType)
	 * @since 3.7
	 */
    public boolean visit(IntersectionType node) {
        for (Iterator it = node.types().iterator(); it.hasNext(); ) {
            Type t = (Type) it.next();
            t.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                " & ");
            }
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(Javadoc)
	 */
    public boolean visit(Javadoc node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("/** ");
        for (Iterator it = node.tags().iterator(); it.hasNext(); ) {
            ASTNode e = (ASTNode) it.next();
            e.accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append("\n */\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(LabeledStatement)
	 */
    public boolean visit(LabeledStatement node) {
        printIndent();
        node.getLabel().accept(this);
        //$NON-NLS-1$
        this.buffer.append(": ");
        node.getBody().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(LambdaExpression)
	 */
    public boolean visit(LambdaExpression node) {
        boolean hasParentheses = node.hasParentheses();
        if (hasParentheses)
            this.buffer.append('(');
        for (Iterator it = node.parameters().iterator(); it.hasNext(); ) {
            VariableDeclaration v = (VariableDeclaration) it.next();
            v.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ",");
            }
        }
        if (hasParentheses)
            this.buffer.append(')');
        //$NON-NLS-1$
        this.buffer.append(" -> ");
        node.getBody().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(LineComment)
	 * @since 3.0
	 */
    public boolean visit(LineComment node) {
        //$NON-NLS-1$
        this.buffer.append("//\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(MarkerAnnotation)
	 * @since 3.1
	 */
    public boolean visit(MarkerAnnotation node) {
        //$NON-NLS-1$
        this.buffer.append("@");
        node.getTypeName().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(MemberRef)
	 * @since 3.0
	 */
    public boolean visit(MemberRef node) {
        if (node.getQualifier() != null) {
            node.getQualifier().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append("#");
        node.getName().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(MemberValuePair)
	 * @since 3.1
	 */
    public boolean visit(MemberValuePair node) {
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append("=");
        node.getValue().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(MethodDeclaration)
	 */
    public boolean visit(MethodDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        printIndent();
        if (node.getAST().apiLevel() == JLS2) {
            printModifiers(node.getModifiers());
        }
        if (node.getAST().apiLevel() >= JLS3) {
            printModifiers(node.modifiers());
            if (!node.typeParameters().isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                "<");
                for (Iterator it = node.typeParameters().iterator(); it.hasNext(); ) {
                    TypeParameter t = (TypeParameter) it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(//$NON-NLS-1$
                        ",");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ">");
            }
        }
        if (!node.isConstructor()) {
            if (node.getAST().apiLevel() == JLS2) {
                getReturnType(node).accept(this);
            } else {
                if (node.getReturnType2() != null) {
                    node.getReturnType2().accept(this);
                } else {
                    // methods really ought to have a return type
                    //$NON-NLS-1$
                    this.buffer.append(//$NON-NLS-1$
                    "void");
                }
            }
            //$NON-NLS-1$
            this.buffer.append(" ");
        }
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append("(");
        if (node.getAST().apiLevel() >= AST.JLS8) {
            Type receiverType = node.getReceiverType();
            if (receiverType != null) {
                receiverType.accept(this);
                this.buffer.append(' ');
                SimpleName qualifier = node.getReceiverQualifier();
                if (qualifier != null) {
                    qualifier.accept(this);
                    this.buffer.append('.');
                }
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                "this");
                if (node.parameters().size() > 0) {
                    this.buffer.append(',');
                }
            }
        }
        for (Iterator it = node.parameters().iterator(); it.hasNext(); ) {
            SingleVariableDeclaration v = (SingleVariableDeclaration) it.next();
            v.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(")");
        int size = node.getExtraDimensions();
        if (node.getAST().apiLevel() >= AST.JLS8) {
            List dimensions = node.extraDimensions();
            for (int i = 0; i < size; i++) {
                visit((Dimension) dimensions.get(i));
            }
        } else {
            for (int i = 0; i < size; i++) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                "[]");
            }
        }
        if (node.getAST().apiLevel() < AST.JLS8) {
            if (!thrownExceptions(node).isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                " throws ");
                for (Iterator it = thrownExceptions(node).iterator(); it.hasNext(); ) {
                    Name n = (Name) it.next();
                    n.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(//$NON-NLS-1$
                        ", ");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                " ");
            }
        } else {
            if (!node.thrownExceptionTypes().isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                " throws ");
                for (Iterator it = node.thrownExceptionTypes().iterator(); it.hasNext(); ) {
                    Type n = (Type) it.next();
                    n.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(//$NON-NLS-1$
                        ", ");
                    }
                }
                //$NON-NLS-1$				
                this.buffer.append(" ");
            }
        }
        if (node.getBody() == null) {
            //$NON-NLS-1$
            this.buffer.append(";\n");
        } else {
            node.getBody().accept(this);
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(MethodInvocation)
	 */
    public boolean visit(MethodInvocation node) {
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
            //$NON-NLS-1$
            this.buffer.append(".");
        }
        if (node.getAST().apiLevel() >= JLS3) {
            if (!node.typeArguments().isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                "<");
                for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
                    Type t = (Type) it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(//$NON-NLS-1$
                        ",");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ">");
            }
        }
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append("(");
        for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            e.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(")");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(MethodRef)
	 * @since 3.0
	 */
    public boolean visit(MethodRef node) {
        if (node.getQualifier() != null) {
            node.getQualifier().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append("#");
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append("(");
        for (Iterator it = node.parameters().iterator(); it.hasNext(); ) {
            MethodRefParameter e = (MethodRefParameter) it.next();
            e.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(")");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(MethodRefParameter)
	 * @since 3.0
	 */
    public boolean visit(MethodRefParameter node) {
        node.getType().accept(this);
        if (node.getAST().apiLevel() >= JLS3) {
            if (node.isVarargs()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                "...");
            }
        }
        if (node.getName() != null) {
            //$NON-NLS-1$
            this.buffer.append(" ");
            node.getName().accept(this);
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(Modifier)
	 * @since 3.1
	 */
    public boolean visit(Modifier node) {
        this.buffer.append(node.getKeyword().toString());
        return false;
    }

    /*
	 * @see ASTVisitor#visit(NameQualifiedType)
	 * @since 3.10
	 */
    public boolean visit(NameQualifiedType node) {
        node.getQualifier().accept(this);
        this.buffer.append('.');
        visitTypeAnnotations(node);
        node.getName().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(NormalAnnotation)
	 * @since 3.1
	 */
    public boolean visit(NormalAnnotation node) {
        //$NON-NLS-1$
        this.buffer.append("@");
        node.getTypeName().accept(this);
        //$NON-NLS-1$
        this.buffer.append("(");
        for (Iterator it = node.values().iterator(); it.hasNext(); ) {
            MemberValuePair p = (MemberValuePair) it.next();
            p.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(")");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(NullLiteral)
	 */
    public boolean visit(NullLiteral node) {
        //$NON-NLS-1$
        this.buffer.append("null");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(NumberLiteral)
	 */
    public boolean visit(NumberLiteral node) {
        this.buffer.append(node.getToken());
        return false;
    }

    /*
	 * @see ASTVisitor#visit(PackageDeclaration)
	 */
    public boolean visit(PackageDeclaration node) {
        if (node.getAST().apiLevel() >= JLS3) {
            if (node.getJavadoc() != null) {
                node.getJavadoc().accept(this);
            }
            for (Iterator it = node.annotations().iterator(); it.hasNext(); ) {
                Annotation p = (Annotation) it.next();
                p.accept(this);
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                " ");
            }
        }
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("package ");
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(ParameterizedType)
	 * @since 3.1
	 */
    public boolean visit(ParameterizedType node) {
        node.getType().accept(this);
        //$NON-NLS-1$
        this.buffer.append("<");
        for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
            Type t = (Type) it.next();
            t.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(">");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(ParenthesizedExpression)
	 */
    public boolean visit(ParenthesizedExpression node) {
        //$NON-NLS-1$
        this.buffer.append("(");
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(")");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(PostfixExpression)
	 */
    public boolean visit(PostfixExpression node) {
        node.getOperand().accept(this);
        this.buffer.append(node.getOperator().toString());
        return false;
    }

    /*
	 * @see ASTVisitor#visit(PrefixExpression)
	 */
    public boolean visit(PrefixExpression node) {
        this.buffer.append(node.getOperator().toString());
        node.getOperand().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(PrimitiveType)
	 */
    public boolean visit(PrimitiveType node) {
        visitTypeAnnotations(node);
        this.buffer.append(node.getPrimitiveTypeCode().toString());
        return false;
    }

    /*
	 * @see ASTVisitor#visit(QualifiedName)
	 */
    public boolean visit(QualifiedName node) {
        node.getQualifier().accept(this);
        //$NON-NLS-1$
        this.buffer.append(".");
        node.getName().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(QualifiedType)
	 * @since 3.1
	 */
    public boolean visit(QualifiedType node) {
        node.getQualifier().accept(this);
        //$NON-NLS-1$
        this.buffer.append(".");
        visitTypeAnnotations(node);
        node.getName().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(ReturnStatement)
	 */
    public boolean visit(ReturnStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("return");
        if (node.getExpression() != null) {
            //$NON-NLS-1$
            this.buffer.append(" ");
            node.getExpression().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(SimpleName)
	 */
    public boolean visit(SimpleName node) {
        this.buffer.append(node.getIdentifier());
        return false;
    }

    /*
	 * @see ASTVisitor#visit(SimpleType)
	 */
    public boolean visit(SimpleType node) {
        visitTypeAnnotations(node);
        node.getName().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(SingleMemberAnnotation)
	 * @since 3.1
	 */
    public boolean visit(SingleMemberAnnotation node) {
        //$NON-NLS-1$
        this.buffer.append("@");
        node.getTypeName().accept(this);
        //$NON-NLS-1$
        this.buffer.append("(");
        node.getValue().accept(this);
        //$NON-NLS-1$
        this.buffer.append(")");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(SingleVariableDeclaration)
	 */
    public boolean visit(SingleVariableDeclaration node) {
        printIndent();
        if (node.getAST().apiLevel() == JLS2) {
            printModifiers(node.getModifiers());
        }
        if (node.getAST().apiLevel() >= JLS3) {
            printModifiers(node.modifiers());
        }
        node.getType().accept(this);
        if (node.getAST().apiLevel() >= JLS3) {
            if (node.isVarargs()) {
                if (node.getAST().apiLevel() >= AST.JLS8) {
                    List annotations = node.varargsAnnotations();
                    if (annotations.size() > 0) {
                        this.buffer.append(' ');
                    }
                    visitAnnotationsList(annotations);
                }
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                "...");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(" ");
        node.getName().accept(this);
        int size = node.getExtraDimensions();
        if (node.getAST().apiLevel() >= AST.JLS8) {
            List dimensions = node.extraDimensions();
            for (int i = 0; i < size; i++) {
                visit((Dimension) dimensions.get(i));
            }
        } else {
            for (int i = 0; i < size; i++) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                "[]");
            }
        }
        if (node.getInitializer() != null) {
            //$NON-NLS-1$
            this.buffer.append("=");
            node.getInitializer().accept(this);
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(StringLiteral)
	 */
    public boolean visit(StringLiteral node) {
        this.buffer.append(node.getEscapedValue());
        return false;
    }

    /*
	 * @see ASTVisitor#visit(SuperConstructorInvocation)
	 */
    public boolean visit(SuperConstructorInvocation node) {
        printIndent();
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
            //$NON-NLS-1$
            this.buffer.append(".");
        }
        if (node.getAST().apiLevel() >= JLS3) {
            if (!node.typeArguments().isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                "<");
                for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
                    Type t = (Type) it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(//$NON-NLS-1$
                        ",");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ">");
            }
        }
        //$NON-NLS-1$
        this.buffer.append("super(");
        for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            e.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(");\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(SuperFieldAccess)
	 */
    public boolean visit(SuperFieldAccess node) {
        if (node.getQualifier() != null) {
            node.getQualifier().accept(this);
            //$NON-NLS-1$
            this.buffer.append(".");
        }
        //$NON-NLS-1$
        this.buffer.append("super.");
        node.getName().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(SuperMethodInvocation)
	 */
    public boolean visit(SuperMethodInvocation node) {
        if (node.getQualifier() != null) {
            node.getQualifier().accept(this);
            //$NON-NLS-1$
            this.buffer.append(".");
        }
        //$NON-NLS-1$
        this.buffer.append("super.");
        if (node.getAST().apiLevel() >= JLS3) {
            if (!node.typeArguments().isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                "<");
                for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
                    Type t = (Type) it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(//$NON-NLS-1$
                        ",");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ">");
            }
        }
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append("(");
        for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            e.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(")");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(SuperMethodReference)
	 * 
	 * @since 3.10
	 */
    public boolean visit(SuperMethodReference node) {
        if (node.getQualifier() != null) {
            node.getQualifier().accept(this);
            this.buffer.append('.');
        }
        //$NON-NLS-1$
        this.buffer.append("super");
        visitReferenceTypeArguments(node.typeArguments());
        node.getName().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(SwitchCase)
	 */
    public boolean visit(SwitchCase node) {
        if (node.isDefault()) {
            //$NON-NLS-1$
            this.buffer.append("default :\n");
        } else {
            //$NON-NLS-1$
            this.buffer.append("case ");
            node.getExpression().accept(this);
            //$NON-NLS-1$
            this.buffer.append(":\n");
        }
        //decremented in visit(SwitchStatement)
        this.indent++;
        return false;
    }

    /*
	 * @see ASTVisitor#visit(SwitchStatement)
	 */
    public boolean visit(SwitchStatement node) {
        //$NON-NLS-1$
        this.buffer.append("switch (");
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(") ");
        //$NON-NLS-1$
        this.buffer.append("{\n");
        this.indent++;
        for (Iterator it = node.statements().iterator(); it.hasNext(); ) {
            Statement s = (Statement) it.next();
            s.accept(this);
            // incremented in visit(SwitchCase)
            this.indent--;
        }
        this.indent--;
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("}\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(SynchronizedStatement)
	 */
    public boolean visit(SynchronizedStatement node) {
        //$NON-NLS-1$
        this.buffer.append("synchronized (");
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(") ");
        node.getBody().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(TagElement)
	 * @since 3.0
	 */
    public boolean visit(TagElement node) {
        if (node.isNested()) {
            // nested tags are always enclosed in braces
            //$NON-NLS-1$
            this.buffer.append("{");
        } else {
            // top-level tags always begin on a new line
            //$NON-NLS-1$
            this.buffer.append("\n * ");
        }
        boolean previousRequiresWhiteSpace = false;
        if (node.getTagName() != null) {
            this.buffer.append(node.getTagName());
            previousRequiresWhiteSpace = true;
        }
        boolean previousRequiresNewLine = false;
        for (Iterator it = node.fragments().iterator(); it.hasNext(); ) {
            ASTNode e = (ASTNode) it.next();
            // Name, MemberRef, MethodRef, and nested TagElement do not include white space.
            // TextElements don't always include whitespace, see <https://bugs.eclipse.org/206518>.
            boolean currentIncludesWhiteSpace = false;
            if (e instanceof TextElement) {
                String text = ((TextElement) e).getText();
                if (text.length() > 0 && ScannerHelper.isWhitespace(text.charAt(0))) {
                    // workaround for https://bugs.eclipse.org/403735
                    currentIncludesWhiteSpace = true;
                }
            }
            if (previousRequiresNewLine && currentIncludesWhiteSpace) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                "\n * ");
            }
            previousRequiresNewLine = currentIncludesWhiteSpace;
            // add space if required to separate
            if (previousRequiresWhiteSpace && !currentIncludesWhiteSpace) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                " ");
            }
            e.accept(this);
            previousRequiresWhiteSpace = !currentIncludesWhiteSpace && !(e instanceof TagElement);
        }
        if (node.isNested()) {
            //$NON-NLS-1$
            this.buffer.append("}");
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(TextElement)
	 * @since 3.0
	 */
    public boolean visit(TextElement node) {
        this.buffer.append(node.getText());
        return false;
    }

    /*
	 * @see ASTVisitor#visit(ThisExpression)
	 */
    public boolean visit(ThisExpression node) {
        if (node.getQualifier() != null) {
            node.getQualifier().accept(this);
            //$NON-NLS-1$
            this.buffer.append(".");
        }
        //$NON-NLS-1$
        this.buffer.append("this");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(ThrowStatement)
	 */
    public boolean visit(ThrowStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("throw ");
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(TryStatement)
	 */
    public boolean visit(TryStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("try ");
        if (node.getAST().apiLevel() >= JLS4) {
            List resources = node.resources();
            if (!resources.isEmpty()) {
                this.buffer.append('(');
                for (Iterator it = resources.iterator(); it.hasNext(); ) {
                    VariableDeclarationExpression variable = (VariableDeclarationExpression) it.next();
                    variable.accept(this);
                    if (it.hasNext()) {
                        this.buffer.append(';');
                    }
                }
                this.buffer.append(')');
            }
        }
        node.getBody().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" ");
        for (Iterator it = node.catchClauses().iterator(); it.hasNext(); ) {
            CatchClause cc = (CatchClause) it.next();
            cc.accept(this);
        }
        if (node.getFinally() != null) {
            //$NON-NLS-1$
            this.buffer.append(" finally ");
            node.getFinally().accept(this);
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(TypeDeclaration)
	 */
    public boolean visit(TypeDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        if (node.getAST().apiLevel() == JLS2) {
            printModifiers(node.getModifiers());
        }
        if (node.getAST().apiLevel() >= JLS3) {
            printModifiers(node.modifiers());
        }
        //$NON-NLS-2$//$NON-NLS-1$
        this.buffer.append(node.isInterface() ? "interface " : "class ");
        node.getName().accept(this);
        if (node.getAST().apiLevel() >= JLS3) {
            if (!node.typeParameters().isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                "<");
                for (Iterator it = node.typeParameters().iterator(); it.hasNext(); ) {
                    TypeParameter t = (TypeParameter) it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(//$NON-NLS-1$
                        ",");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ">");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(" ");
        if (node.getAST().apiLevel() == JLS2) {
            if (getSuperclass(node) != null) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                "extends ");
                getSuperclass(node).accept(this);
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                " ");
            }
            if (!superInterfaces(node).isEmpty()) {
                //$NON-NLS-2$//$NON-NLS-1$
                this.buffer.append(node.isInterface() ? "extends " : "implements ");
                for (Iterator it = superInterfaces(node).iterator(); it.hasNext(); ) {
                    Name n = (Name) it.next();
                    n.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(//$NON-NLS-1$
                        ", ");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                " ");
            }
        }
        if (node.getAST().apiLevel() >= JLS3) {
            if (node.getSuperclassType() != null) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                "extends ");
                node.getSuperclassType().accept(this);
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                " ");
            }
            if (!node.superInterfaceTypes().isEmpty()) {
                //$NON-NLS-2$//$NON-NLS-1$
                this.buffer.append(node.isInterface() ? "extends " : "implements ");
                for (Iterator it = node.superInterfaceTypes().iterator(); it.hasNext(); ) {
                    Type t = (Type) it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(//$NON-NLS-1$
                        ", ");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                " ");
            }
        }
        //$NON-NLS-1$
        this.buffer.append("{\n");
        this.indent++;
        for (Iterator it = node.bodyDeclarations().iterator(); it.hasNext(); ) {
            BodyDeclaration d = (BodyDeclaration) it.next();
            d.accept(this);
        }
        this.indent--;
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("}\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(TypeDeclarationStatement)
	 */
    public boolean visit(TypeDeclarationStatement node) {
        if (node.getAST().apiLevel() == JLS2) {
            getTypeDeclaration(node).accept(this);
        }
        if (node.getAST().apiLevel() >= JLS3) {
            node.getDeclaration().accept(this);
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(TypeLiteral)
	 */
    public boolean visit(TypeLiteral node) {
        node.getType().accept(this);
        //$NON-NLS-1$
        this.buffer.append(".class");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(TypeMethodReference)
	 * 
	 * @since 3.10
	 */
    public boolean visit(TypeMethodReference node) {
        node.getType().accept(this);
        visitReferenceTypeArguments(node.typeArguments());
        node.getName().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(TypeParameter)
	 * @since 3.1
	 */
    public boolean visit(TypeParameter node) {
        if (node.getAST().apiLevel() >= AST.JLS8) {
            printModifiers(node.modifiers());
        }
        node.getName().accept(this);
        if (!node.typeBounds().isEmpty()) {
            //$NON-NLS-1$
            this.buffer.append(" extends ");
            for (Iterator it = node.typeBounds().iterator(); it.hasNext(); ) {
                Type t = (Type) it.next();
                t.accept(this);
                if (it.hasNext()) {
                    //$NON-NLS-1$
                    this.buffer.append(//$NON-NLS-1$
                    " & ");
                }
            }
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(UnionType)
	 * @since 3.7
	 */
    public boolean visit(UnionType node) {
        for (Iterator it = node.types().iterator(); it.hasNext(); ) {
            Type t = (Type) it.next();
            t.accept(this);
            if (it.hasNext()) {
                this.buffer.append('|');
            }
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(VariableDeclarationExpression)
	 */
    public boolean visit(VariableDeclarationExpression node) {
        if (node.getAST().apiLevel() == JLS2) {
            printModifiers(node.getModifiers());
        }
        if (node.getAST().apiLevel() >= JLS3) {
            printModifiers(node.modifiers());
        }
        node.getType().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" ");
        for (Iterator it = node.fragments().iterator(); it.hasNext(); ) {
            VariableDeclarationFragment f = (VariableDeclarationFragment) it.next();
            f.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ", ");
            }
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(VariableDeclarationFragment)
	 */
    public boolean visit(VariableDeclarationFragment node) {
        node.getName().accept(this);
        int size = node.getExtraDimensions();
        if (node.getAST().apiLevel() >= AST.JLS8) {
            List dimensions = node.extraDimensions();
            for (int i = 0; i < size; i++) {
                visit((Dimension) dimensions.get(i));
            }
        } else {
            for (int i = 0; i < size; i++) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                "[]");
            }
        }
        if (node.getInitializer() != null) {
            //$NON-NLS-1$
            this.buffer.append("=");
            node.getInitializer().accept(this);
        }
        return false;
    }

    /*
	 * @see ASTVisitor#visit(VariableDeclarationStatement)
	 */
    public boolean visit(VariableDeclarationStatement node) {
        printIndent();
        if (node.getAST().apiLevel() == JLS2) {
            printModifiers(node.getModifiers());
        }
        if (node.getAST().apiLevel() >= JLS3) {
            printModifiers(node.modifiers());
        }
        node.getType().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" ");
        for (Iterator it = node.fragments().iterator(); it.hasNext(); ) {
            VariableDeclarationFragment f = (VariableDeclarationFragment) it.next();
            f.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                ", ");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(WhileStatement)
	 */
    public boolean visit(WhileStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("while (");
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(") ");
        node.getBody().accept(this);
        return false;
    }

    /*
	 * @see ASTVisitor#visit(WildcardType)
	 * @since 3.1
	 */
    public boolean visit(WildcardType node) {
        visitTypeAnnotations(node);
        //$NON-NLS-1$
        this.buffer.append("?");
        Type bound = node.getBound();
        if (bound != null) {
            if (node.isUpperBound()) {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                " extends ");
            } else {
                //$NON-NLS-1$
                this.buffer.append(//$NON-NLS-1$
                " super ");
            }
            bound.accept(this);
        }
        return false;
    }

    /**
	 * @deprecated
	 */
    private void visitComponentType(ArrayType node) {
        node.getComponentType().accept(this);
    }
}
