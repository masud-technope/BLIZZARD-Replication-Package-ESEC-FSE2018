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
package org.eclipse.jdt.core.tests.dom;

import java.lang.reflect.Method;
import junit.framework.Test;
import org.eclipse.jdt.core.dom.*;

@SuppressWarnings("rawtypes")
public class ASTVisitorTest extends org.eclipse.jdt.core.tests.junit.extension.TestCase {

    /** @deprecated using deprecated code */
    public static Test suite() {
        // TODO (frederic) use buildList + setAstLevel(init) instead...
        junit.framework.TestSuite suite = new junit.framework.TestSuite(ASTVisitorTest.class.getName());
        Class c = ASTVisitorTest.class;
        Method[] methods = c.getMethods();
        for (int i = 0, max = methods.length; i < max; i++) {
            if (//$NON-NLS-1$
            methods[i].getName().startsWith("test")) {
                suite.addTest(new ASTVisitorTest(methods[i].getName(), AST.JLS2));
                suite.addTest(new ASTVisitorTest(methods[i].getName(), AST.JLS3));
                suite.addTest(new ASTVisitorTest(methods[i].getName(), AST.JLS4));
                suite.addTest(new ASTVisitorTest(methods[i].getName(), AST.JLS8));
            }
        }
        return suite;
    }

    AST ast;

    SimpleName N1;

    String N1S;

    SimpleName N2;

    String N2S;

    SimpleName N3;

    String N3S;

    SimpleName N4;

    String N4S;

    Expression E1;

    String E1S;

    Expression E2;

    String E2S;

    Type T1;

    String T1S;

    Type T2;

    String T2S;

    ParameterizedType PT1;

    String PT1S;

    Statement S1;

    String S1S;

    Statement S2;

    Block B1;

    String B1S;

    String S2S;

    SingleVariableDeclaration V1;

    String V1S;

    SingleVariableDeclaration V2;

    String V2S;

    VariableDeclarationFragment W1;

    String W1S;

    VariableDeclarationFragment W2;

    String W2S;

    FieldDeclaration FD1;

    String FD1S;

    FieldDeclaration FD2;

    String FD2S;

    PackageDeclaration PD1;

    String PD1S;

    ImportDeclaration ID1;

    String ID1S;

    ImportDeclaration ID2;

    String ID2S;

    TypeDeclaration TD1;

    String TD1S;

    TypeDeclaration TD2;

    String TD2S;

    Javadoc JD1;

    String JD1S;

    TagElement TAG1;

    String TAG1S;

    TextElement TEXT1;

    String TEXT1S;

    MemberRef MBREF1;

    String MBREF1S;

    MethodRef MTHREF1;

    String MTHREF1S;

    MethodRefParameter MPARM1;

    String MPARM1S;

    LineComment LC1;

    String LC1S;

    BlockComment BC1;

    String BC1S;

    AnonymousClassDeclaration ACD1;

    String ACD1S;

    TypeParameter TP1;

    String TP1S;

    TypeParameter TP2;

    String TP2S;

    MemberValuePair MVP1;

    String MVP1S;

    MemberValuePair MVP2;

    String MVP2S;

    Modifier MOD1;

    String MOD1S;

    Modifier MOD2;

    String MOD2S;

    Annotation ANO1;

    String ANO1S;

    Annotation ANO2;

    String ANO2S;

    EnumConstantDeclaration EC1;

    String EC1S;

    EnumConstantDeclaration EC2;

    String EC2S;

    Type T3;

    String T3S;

    Type T4;

    String T4S;

    final StringBuffer b = new StringBuffer();

    int API_LEVEL;

    public  ASTVisitorTest(String name, int apiLevel) {
        super(name);
        this.API_LEVEL = apiLevel;
    }

    public  ASTVisitorTest(String name) {
        super(name.substring(0, name.indexOf(" - JLS")));
        name.indexOf(" - JLS");
        this.API_LEVEL = Integer.parseInt(name.substring(name.indexOf(" - JLS") + 6));
    }

    /** @deprecated using deprecated code */
    public String getName() {
        String name = super.getName() + " - JLS" + this.API_LEVEL;
        return name;
    }

    /**
	 * @deprecated (not really - just suppressing the warnings
	 * that come from testing Javadoc.getComment())
	 *
	 */
    protected void setUp() throws Exception {
        super.setUp();
        this.ast = AST.newAST(this.API_LEVEL);
        //$NON-NLS-1$
        this.N1 = this.ast.newSimpleName("N");
        //$NON-NLS-1$
        this.N1S = "[(nSNNnS)]";
        //$NON-NLS-1$
        this.N2 = this.ast.newSimpleName("M");
        //$NON-NLS-1$
        this.N2S = "[(nSMMnS)]";
        //$NON-NLS-1$
        this.N3 = this.ast.newSimpleName("O");
        //$NON-NLS-1$
        this.N3S = "[(nSOOnS)]";
        //$NON-NLS-1$
        this.N4 = this.ast.newSimpleName("P");
        //$NON-NLS-1$
        this.N4S = "[(nSPPnS)]";
        //$NON-NLS-1$
        this.E1 = this.ast.newSimpleName("X");
        //$NON-NLS-1$
        this.E1S = "[(nSXXnS)]";
        //$NON-NLS-1$
        this.E2 = this.ast.newSimpleName("Y");
        //$NON-NLS-1$
        this.E2S = "[(nSYYnS)]";
        //$NON-NLS-1$
        this.T1 = this.ast.newSimpleType(this.ast.newSimpleName("Z"));
        //$NON-NLS-1$
        this.T1S = "[(tS[(nSZZnS)]tS)]";
        //$NON-NLS-1$
        this.T2 = this.ast.newSimpleType(this.ast.newSimpleName("X"));
        //$NON-NLS-1$
        this.T2S = "[(tS[(nSXXnS)]tS)]";
        this.S1 = this.ast.newContinueStatement();
        //$NON-NLS-1$
        this.S1S = "[(sCNsCN)]";
        this.S2 = this.ast.newBreakStatement();
        //$NON-NLS-1$
        this.S2S = "[(sBRsBR)]";
        this.B1 = this.ast.newBlock();
        //$NON-NLS-1$
        this.B1S = "[(sBsB)]";
        this.V1 = this.ast.newSingleVariableDeclaration();
        this.V1.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
        //$NON-NLS-1$
        this.V1.setName(this.ast.newSimpleName("a"));
        //$NON-NLS-1$
        this.V1S = "[(VD[(tPintinttP)][(nSaanS)]VD)]";
        this.V2 = this.ast.newSingleVariableDeclaration();
        this.V2.setType(this.ast.newPrimitiveType(PrimitiveType.BYTE));
        //$NON-NLS-1$
        this.V2.setName(this.ast.newSimpleName("b"));
        //$NON-NLS-1$
        this.V2S = "[(VD[(tPbytebytetP)][(nSbbnS)]VD)]";
        this.W1 = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        this.W1.setName(this.ast.newSimpleName("a"));
        //$NON-NLS-1$
        this.W1S = "[(VS[(nSaanS)]VS)]";
        this.W2 = this.ast.newVariableDeclarationFragment();
        //$NON-NLS-1$
        this.W2.setName(this.ast.newSimpleName("b"));
        //$NON-NLS-1$
        this.W2S = "[(VS[(nSbbnS)]VS)]";
        {
            VariableDeclarationFragment temp = this.ast.newVariableDeclarationFragment();
            //$NON-NLS-1$
            temp.setName(this.ast.newSimpleName("f"));
            this.FD1 = this.ast.newFieldDeclaration(temp);
            this.FD1.setType(this.ast.newPrimitiveType(PrimitiveType.INT));
            //$NON-NLS-1$
            this.FD1S = "[(FD[(tPintinttP)][(VS[(nSffnS)]VS)]FD)]";
        }
        {
            VariableDeclarationFragment temp = this.ast.newVariableDeclarationFragment();
            //$NON-NLS-1$
            temp.setName(this.ast.newSimpleName("g"));
            this.FD2 = this.ast.newFieldDeclaration(temp);
            this.FD2.setType(this.ast.newPrimitiveType(PrimitiveType.CHAR));
            //$NON-NLS-1$
            this.FD2S = "[(FD[(tPcharchartP)][(VS[(nSggnS)]VS)]FD)]";
        }
        this.PD1 = this.ast.newPackageDeclaration();
        //$NON-NLS-1$
        this.PD1.setName(this.ast.newSimpleName("p"));
        //$NON-NLS-1$
        this.PD1S = "[(PD[(nSppnS)]PD)]";
        this.ID1 = this.ast.newImportDeclaration();
        //$NON-NLS-1$
        this.ID1.setName(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        this.ID1S = "[(ID[(nSiinS)]ID)]";
        this.ID2 = this.ast.newImportDeclaration();
        //$NON-NLS-1$
        this.ID2.setName(this.ast.newSimpleName("j"));
        //$NON-NLS-1$
        this.ID2S = "[(ID[(nSjjnS)]ID)]";
        this.TD1 = this.ast.newTypeDeclaration();
        //$NON-NLS-1$
        this.TD1.setName(this.ast.newSimpleName("c"));
        //$NON-NLS-1$
        this.TD1S = "[(TD[(nSccnS)]TD)]";
        this.TD2 = this.ast.newTypeDeclaration();
        //$NON-NLS-1$
        this.TD2.setName(this.ast.newSimpleName("d"));
        //$NON-NLS-1$
        this.TD2S = "[(TD[(nSddnS)]TD)]";
        this.ACD1 = this.ast.newAnonymousClassDeclaration();
        //$NON-NLS-1$
        this.ACD1S = "[(ACDACD)]";
        this.JD1 = this.ast.newJavadoc();
        //$NON-NLS-1$
        this.JD1S = "[(JDJD)]";
        this.LC1 = this.ast.newLineComment();
        //$NON-NLS-1$
        this.LC1S = "[(//*//)]";
        this.BC1 = this.ast.newBlockComment();
        //$NON-NLS-1$
        this.BC1S = "[(/**/)]";
        this.TAG1 = this.ast.newTagElement();
        //$NON-NLS-1$
        this.TAG1.setTagName("@foo");
        //$NON-NLS-1$
        this.TAG1S = "[(TG@foo@fooTG)]";
        this.TEXT1 = this.ast.newTextElement();
        //$NON-NLS-1$
        this.TEXT1.setText("foo");
        //$NON-NLS-1$
        this.TEXT1S = "[(TXfoofooTX)]";
        this.MBREF1 = this.ast.newMemberRef();
        //$NON-NLS-1$
        this.MBREF1.setName(this.ast.newSimpleName("p"));
        //$NON-NLS-1$
        this.MBREF1S = "[(MBREF[(nSppnS)]MBREF)]";
        this.MTHREF1 = this.ast.newMethodRef();
        //$NON-NLS-1$
        this.MTHREF1.setName(this.ast.newSimpleName("p"));
        //$NON-NLS-1$
        this.MTHREF1S = "[(MTHREF[(nSppnS)]MTHREF)]";
        this.MPARM1 = this.ast.newMethodRefParameter();
        this.MPARM1.setType(this.ast.newPrimitiveType(PrimitiveType.CHAR));
        //$NON-NLS-1$
        this.MPARM1S = "[(MPARM[(tPcharchartP)]MPARM)]";
        if (this.ast.apiLevel() >= AST.JLS3) {
            //$NON-NLS-1$
            this.PT1 = this.ast.newParameterizedType(this.ast.newSimpleType(this.ast.newSimpleName("Z")));
            //$NON-NLS-1$
            this.PT1S = "[(tM[(tS[(nSZZnS)]tS)]tM)]";
            this.TP1 = this.ast.newTypeParameter();
            //$NON-NLS-1$
            this.TP1.setName(this.ast.newSimpleName("x"));
            //$NON-NLS-1$
            this.TP1S = "[(tTP[(nSxxnS)]tTP)]";
            this.TP2 = this.ast.newTypeParameter();
            //$NON-NLS-1$
            this.TP2.setName(this.ast.newSimpleName("y"));
            //$NON-NLS-1$
            this.TP2S = "[(tTP[(nSyynS)]tTP)]";
            this.MVP1 = this.ast.newMemberValuePair();
            //$NON-NLS-1$
            this.MVP1.setName(this.ast.newSimpleName("x"));
            //$NON-NLS-1$
            this.MVP1.setValue(this.ast.newSimpleName("y"));
            //$NON-NLS-1$
            this.MVP1S = "[(@MVP[(nSxxnS)][(nSyynS)]@MVP)]";
            this.MVP2 = this.ast.newMemberValuePair();
            //$NON-NLS-1$
            this.MVP2.setName(this.ast.newSimpleName("a"));
            //$NON-NLS-1$
            this.MVP2.setValue(this.ast.newSimpleName("b"));
            //$NON-NLS-1$
            this.MVP2S = "[(@MVP[(nSaanS)][(nSbbnS)]@MVP)]";
            this.MOD1 = this.ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
            //$NON-NLS-1$
            this.MOD1S = "[(MODpublicpublicMOD)]";
            this.MOD2 = this.ast.newModifier(Modifier.ModifierKeyword.FINAL_KEYWORD);
            //$NON-NLS-1$
            this.MOD2S = "[(MODfinalfinalMOD)]";
            this.ANO1 = this.ast.newMarkerAnnotation();
            //$NON-NLS-1$
            this.ANO1.setTypeName(this.ast.newSimpleName("a"));
            //$NON-NLS-1$
            this.ANO1S = "[(@MAN[(nSaanS)]@MAN)]";
            this.ANO2 = this.ast.newNormalAnnotation();
            //$NON-NLS-1$
            this.ANO2.setTypeName(this.ast.newSimpleName("b"));
            //$NON-NLS-1$
            this.ANO2S = "[(@NAN[(nSbbnS)]@NAN)]";
            this.EC1 = this.ast.newEnumConstantDeclaration();
            //$NON-NLS-1$
            this.EC1.setName(this.ast.newSimpleName("c"));
            //$NON-NLS-1$
            this.EC1S = "[(ECD[(nSccnS)]ECD)]";
            this.EC2 = this.ast.newEnumConstantDeclaration();
            //$NON-NLS-1$
            this.EC2.setName(this.ast.newSimpleName("d"));
            //$NON-NLS-1$
            this.EC2S = "[(ECD[(nSddnS)]ECD)]";
        }
        if (this.ast.apiLevel() >= AST.JLS8) {
            //$NON-NLS-1$
            this.T3 = this.ast.newSimpleType(this.ast.newSimpleName("W"));
            //$NON-NLS-1$
            this.T3S = "[(tS[(nSWWnS)]tS)]";
            //$NON-NLS-1$
            this.T4 = this.ast.newSimpleType(this.ast.newSimpleName("X"));
            //$NON-NLS-1$
            this.T4S = "[(tS[(nSXXnS)]tS)]";
        }
    }

    protected void tearDown() throws Exception {
        this.ast = null;
        super.tearDown();
    }

    class TestVisitor extends ASTVisitor {

        boolean visitTheKids = true;

        boolean visitDocTags;

         TestVisitor() {
            this(false);
        }

         TestVisitor(boolean visitDocTags) {
            super(visitDocTags);
            this.visitDocTags = visitDocTags;
        }

        public boolean isVisitingChildren() {
            return this.visitTheKids;
        }

        public void setVisitingChildren(boolean visitChildren) {
            this.visitTheKids = visitChildren;
        }

        // NAMES
        public boolean visit(SimpleName node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(nS");
            ASTVisitorTest.this.b.append(node.getIdentifier());
            return isVisitingChildren();
        }

        public void endVisit(SimpleName node) {
            ASTVisitorTest.this.b.append(node.getIdentifier());
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("nS)");
        }

        public boolean visit(QualifiedName node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(nQ");
            return isVisitingChildren();
        }

        public void endVisit(QualifiedName node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("nQ)");
        }

        // TYPES
        public boolean visit(SimpleType node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(tS");
            return isVisitingChildren();
        }

        public void endVisit(SimpleType node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("tS)");
        }

        public boolean visit(ArrayType node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(tA");
            return isVisitingChildren();
        }

        public void endVisit(ArrayType node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("tA)");
        }

        public boolean visit(PrimitiveType node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(tP");
            ASTVisitorTest.this.b.append(node.getPrimitiveTypeCode().toString());
            return isVisitingChildren();
        }

        public void endVisit(PrimitiveType node) {
            ASTVisitorTest.this.b.append(node.getPrimitiveTypeCode().toString());
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("tP)");
        }

        public boolean visit(NameQualifiedType node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(tPQ");
            return isVisitingChildren();
        }

        public void endVisit(NameQualifiedType node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("tPQ)");
        }

        public boolean visit(ParameterizedType node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(tM");
            return isVisitingChildren();
        }

        public void endVisit(ParameterizedType node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("tM)");
        }

        public boolean visit(QualifiedType node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(tQ");
            return isVisitingChildren();
        }

        public void endVisit(QualifiedType node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("tQ)");
        }

        public boolean visit(UnionType node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(tU");
            return isVisitingChildren();
        }

        public void endVisit(UnionType node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("tU)");
        }

        public boolean visit(WildcardType node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(tW");
            return isVisitingChildren();
        }

        public void endVisit(WildcardType node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("tW)");
        }

        // EXPRESSIONS and STATEMENTS
        public boolean visit(ArrayAccess node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eAA");
            return isVisitingChildren();
        }

        public void endVisit(ArrayAccess node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eAA)");
        }

        public boolean visit(ArrayCreation node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eAC");
            return isVisitingChildren();
        }

        public void endVisit(ArrayCreation node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eAC)");
        }

        public boolean visit(ArrayInitializer node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eAI");
            return isVisitingChildren();
        }

        public void endVisit(ArrayInitializer node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eAI)");
        }

        public boolean visit(AssertStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sAS");
            return isVisitingChildren();
        }

        public void endVisit(AssertStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sAS)");
        }

        public boolean visit(Assignment node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(");
            ASTVisitorTest.this.b.append(node.getOperator().toString());
            return isVisitingChildren();
        }

        public void endVisit(Assignment node) {
            ASTVisitorTest.this.b.append(node.getOperator().toString());
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append(")");
        }

        public boolean visit(Block node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sB");
            return isVisitingChildren();
        }

        public void endVisit(Block node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sB)");
        }

        public boolean visit(BooleanLiteral node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eBL");
            //$NON-NLS-1$ //$NON-NLS-2$
            ASTVisitorTest.this.b.append(node.booleanValue() ? "true" : "false");
            return isVisitingChildren();
        }

        public void endVisit(BooleanLiteral node) {
            //$NON-NLS-1$ //$NON-NLS-2$
            ASTVisitorTest.this.b.append(node.booleanValue() ? "true" : "false");
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eBL)");
        }

        public boolean visit(BreakStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sBR");
            return isVisitingChildren();
        }

        public void endVisit(BreakStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sBR)");
        }

        public boolean visit(CastExpression node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eCS");
            return isVisitingChildren();
        }

        public void endVisit(CastExpression node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eCS)");
        }

        public boolean visit(CatchClause node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(cc");
            return isVisitingChildren();
        }

        public void endVisit(CatchClause node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("cc)");
        }

        public boolean visit(CharacterLiteral node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eCL");
            ASTVisitorTest.this.b.append(node.getEscapedValue());
            return isVisitingChildren();
        }

        public void endVisit(CharacterLiteral node) {
            ASTVisitorTest.this.b.append(node.getEscapedValue());
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eCL)");
        }

        public boolean visit(ClassInstanceCreation node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eCI");
            return isVisitingChildren();
        }

        public void endVisit(ClassInstanceCreation node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eCI)");
        }

        public boolean visit(AnonymousClassDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(ACD");
            return isVisitingChildren();
        }

        public void endVisit(AnonymousClassDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("ACD)");
        }

        public boolean visit(CompilationUnit node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(CU");
            return isVisitingChildren();
        }

        public void endVisit(CompilationUnit node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("CU)");
        }

        public boolean visit(ConditionalExpression node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eCO");
            return isVisitingChildren();
        }

        public void endVisit(ConditionalExpression node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eCO)");
        }

        public boolean visit(ConstructorInvocation node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sCI");
            return isVisitingChildren();
        }

        public void endVisit(ConstructorInvocation node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sCI)");
        }

        public boolean visit(ContinueStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sCN");
            return isVisitingChildren();
        }

        public void endVisit(ContinueStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sCN)");
        }

        public boolean visit(DoStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sDO");
            return isVisitingChildren();
        }

        public void endVisit(DoStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sDO)");
        }

        public boolean visit(EmptyStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sEM");
            return isVisitingChildren();
        }

        public void endVisit(EmptyStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sEM)");
        }

        public boolean visit(EnhancedForStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sEFR");
            return isVisitingChildren();
        }

        public void endVisit(EnhancedForStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sEFR)");
        }

        public boolean visit(EnumConstantDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(ECD");
            return isVisitingChildren();
        }

        public void endVisit(EnumConstantDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("ECD)");
        }

        public boolean visit(EnumDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(ED");
            return isVisitingChildren();
        }

        public void endVisit(EnumDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("ED)");
        }

        public boolean visit(ExpressionStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sEX");
            return isVisitingChildren();
        }

        public void endVisit(ExpressionStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sEX)");
        }

        public boolean visit(FieldAccess node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eFA");
            return isVisitingChildren();
        }

        public void endVisit(FieldAccess node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eFA)");
        }

        public boolean visit(FieldDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(FD");
            return isVisitingChildren();
        }

        public void endVisit(FieldDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("FD)");
        }

        public boolean visit(ForStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sFR");
            return isVisitingChildren();
        }

        public void endVisit(ForStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sFR)");
        }

        public boolean visit(IfStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sIF");
            return isVisitingChildren();
        }

        public void endVisit(IfStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sIF)");
        }

        public boolean visit(ImportDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(ID");
            return isVisitingChildren();
        }

        public void endVisit(ImportDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("ID)");
        }

        public boolean visit(InfixExpression node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eIN");
            ASTVisitorTest.this.b.append(node.getOperator().toString());
            return isVisitingChildren();
        }

        public void endVisit(InfixExpression node) {
            ASTVisitorTest.this.b.append(node.getOperator().toString());
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eIN)");
        }

        public boolean visit(InstanceofExpression node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eIO");
            return isVisitingChildren();
        }

        public void endVisit(InstanceofExpression node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eIO)");
        }

        public boolean visit(Initializer node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(IN");
            return isVisitingChildren();
        }

        public void endVisit(Initializer node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("IN)");
        }

        /**
		 * @deprecated (not really - just suppressing the warnings
		 * that come from testing Javadoc.getComment())
		 *
		 */
        public boolean visit(Javadoc node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(JD");
            // verify that children of Javadoc nodes are visited only if requested
            if (this.visitDocTags) {
                assertTrue(super.visit(node) == true);
            } else {
                assertTrue(super.visit(node) == false);
            }
            return isVisitingChildren() && super.visit(node);
        }

        /**
		 * @deprecated (not really - just suppressing the warnings
		 * that come from testing Javadoc.getComment())
		 *
		 */
        public void endVisit(Javadoc node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("JD)");
        }

        public boolean visit(BlockComment node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(/*");
            return isVisitingChildren();
        }

        public void endVisit(BlockComment node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("*/)");
        }

        public boolean visit(CreationReference node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eCR");
            return isVisitingChildren();
        }

        public void endVisit(CreationReference node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eCR)");
        }

        public boolean visit(ExpressionMethodReference node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eEMR");
            return isVisitingChildren();
        }

        public void endVisit(ExpressionMethodReference node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eEMR)");
        }

        public boolean visit(LineComment node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(//");
            return isVisitingChildren();
        }

        public void endVisit(LineComment node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("//)");
        }

        public boolean visit(TagElement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(TG");
            ASTVisitorTest.this.b.append(node.getTagName());
            return isVisitingChildren();
        }

        public void endVisit(TagElement node) {
            ASTVisitorTest.this.b.append(node.getTagName());
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("TG)");
        }

        public boolean visit(TextElement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(TX");
            ASTVisitorTest.this.b.append(node.getText());
            return isVisitingChildren();
        }

        public void endVisit(TextElement node) {
            ASTVisitorTest.this.b.append(node.getText());
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("TX)");
        }

        public boolean visit(MemberRef node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(MBREF");
            return isVisitingChildren();
        }

        public void endVisit(MemberRef node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("MBREF)");
        }

        public boolean visit(MethodRef node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(MTHREF");
            return isVisitingChildren();
        }

        public void endVisit(MethodRef node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("MTHREF)");
        }

        public boolean visit(MethodRefParameter node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(MPARM");
            return isVisitingChildren();
        }

        public void endVisit(MethodRefParameter node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("MPARM)");
        }

        public boolean visit(LabeledStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sLA");
            return isVisitingChildren();
        }

        public void endVisit(LabeledStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sLA)");
        }

        public boolean visit(MethodDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(MD");
            return isVisitingChildren();
        }

        public void endVisit(MethodDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("MD)");
        }

        public boolean visit(MethodInvocation node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eMI");
            return isVisitingChildren();
        }

        public void endVisit(MethodInvocation node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eMI)");
        }

        public boolean visit(NullLiteral node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eNL");
            return isVisitingChildren();
        }

        public void endVisit(NullLiteral node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eNL)");
        }

        public boolean visit(NumberLiteral node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eNU");
            ASTVisitorTest.this.b.append(node.getToken());
            return isVisitingChildren();
        }

        public void endVisit(NumberLiteral node) {
            ASTVisitorTest.this.b.append(node.getToken());
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eNU)");
        }

        public boolean visit(PackageDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(PD");
            return isVisitingChildren();
        }

        public void endVisit(PackageDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("PD)");
        }

        public boolean visit(ParenthesizedExpression node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(ePA");
            return isVisitingChildren();
        }

        public void endVisit(ParenthesizedExpression node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("ePA)");
        }

        public boolean visit(PostfixExpression node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(ePO");
            ASTVisitorTest.this.b.append(node.getOperator().toString());
            return isVisitingChildren();
        }

        public void endVisit(PostfixExpression node) {
            ASTVisitorTest.this.b.append(node.getOperator().toString());
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("ePO)");
        }

        public boolean visit(PrefixExpression node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(ePR");
            ASTVisitorTest.this.b.append(node.getOperator().toString());
            return isVisitingChildren();
        }

        public void endVisit(PrefixExpression node) {
            ASTVisitorTest.this.b.append(node.getOperator().toString());
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("ePR)");
        }

        public boolean visit(ReturnStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sRT");
            return isVisitingChildren();
        }

        public void endVisit(ReturnStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sRT)");
        }

        public boolean visit(SingleVariableDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(VD");
            return isVisitingChildren();
        }

        public void endVisit(SingleVariableDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("VD)");
        }

        public boolean visit(StringLiteral node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eSL");
            ASTVisitorTest.this.b.append(node.getLiteralValue());
            return isVisitingChildren();
        }

        public void endVisit(StringLiteral node) {
            ASTVisitorTest.this.b.append(node.getLiteralValue());
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eSL)");
        }

        public boolean visit(SuperConstructorInvocation node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sSC");
            return isVisitingChildren();
        }

        public void endVisit(SuperConstructorInvocation node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sSC)");
        }

        public boolean visit(SuperFieldAccess node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eSF");
            return isVisitingChildren();
        }

        public void endVisit(SuperFieldAccess node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eSF)");
        }

        public boolean visit(SuperMethodInvocation node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eSM");
            return isVisitingChildren();
        }

        public void endVisit(SuperMethodInvocation node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eSM)");
        }

        public boolean visit(SuperMethodReference node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eSMR");
            return isVisitingChildren();
        }

        public void endVisit(SuperMethodReference node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eSMR)");
        }

        public boolean visit(SwitchCase node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sSC");
            return isVisitingChildren();
        }

        public void endVisit(SwitchCase node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sSC)");
        }

        public boolean visit(SwitchStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sSW");
            return isVisitingChildren();
        }

        public void endVisit(SwitchStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sSW)");
        }

        public boolean visit(SynchronizedStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sSY");
            return isVisitingChildren();
        }

        public void endVisit(SynchronizedStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sSY)");
        }

        public boolean visit(ThisExpression node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eTH");
            return isVisitingChildren();
        }

        public void endVisit(ThisExpression node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eTH)");
        }

        public boolean visit(ThrowStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sTR");
            return isVisitingChildren();
        }

        public void endVisit(ThrowStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sTR)");
        }

        public boolean visit(TryStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sTY");
            return isVisitingChildren();
        }

        public void endVisit(TryStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sTY)");
        }

        public boolean visit(TypeDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(TD");
            return isVisitingChildren();
        }

        public void endVisit(TypeDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("TD)");
        }

        public boolean visit(TypeDeclarationStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sTD");
            return isVisitingChildren();
        }

        public void endVisit(TypeDeclarationStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sTD)");
        }

        public boolean visit(TypeLiteral node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eTL");
            return isVisitingChildren();
        }

        public void endVisit(TypeLiteral node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eTL)");
        }

        public boolean visit(TypeMethodReference node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eTMR");
            return isVisitingChildren();
        }

        public void endVisit(TypeMethodReference node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eTMR)");
        }

        public boolean visit(TypeParameter node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(tTP");
            return isVisitingChildren();
        }

        public void endVisit(TypeParameter node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("tTP)");
        }

        public boolean visit(VariableDeclarationExpression node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(eVD");
            return isVisitingChildren();
        }

        public void endVisit(VariableDeclarationExpression node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("eVD)");
        }

        public boolean visit(VariableDeclarationFragment node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(VS");
            return isVisitingChildren();
        }

        public void endVisit(VariableDeclarationFragment node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("VS)");
        }

        public boolean visit(VariableDeclarationStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sVD");
            return isVisitingChildren();
        }

        public void endVisit(VariableDeclarationStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sVD)");
        }

        public boolean visit(WhileStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(sWH");
            return isVisitingChildren();
        }

        public void endVisit(WhileStatement node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("sWH)");
        }

        public boolean visit(AnnotationTypeDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(@TD");
            return isVisitingChildren();
        }

        public void endVisit(AnnotationTypeDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("@TD)");
        }

        public boolean visit(AnnotationTypeMemberDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(@MD");
            return isVisitingChildren();
        }

        public void endVisit(AnnotationTypeMemberDeclaration node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("@MD)");
        }

        public boolean visit(NormalAnnotation node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(@NAN");
            return isVisitingChildren();
        }

        public void endVisit(NormalAnnotation node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("@NAN)");
        }

        public boolean visit(MarkerAnnotation node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(@MAN");
            return isVisitingChildren();
        }

        public void endVisit(MarkerAnnotation node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("@MAN)");
        }

        public boolean visit(SingleMemberAnnotation node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(@SMAN");
            return isVisitingChildren();
        }

        public void endVisit(SingleMemberAnnotation node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("@SMAN)");
        }

        public boolean visit(MemberValuePair node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(@MVP");
            return isVisitingChildren();
        }

        public void endVisit(MemberValuePair node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("@MVP)");
        }

        public boolean visit(Modifier node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(MOD");
            ASTVisitorTest.this.b.append(node.getKeyword().toString());
            return isVisitingChildren();
        }

        public void endVisit(Modifier node) {
            ASTVisitorTest.this.b.append(node.getKeyword().toString());
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("MOD)");
        }

        public boolean visit(Dimension node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("(@ED");
            return isVisitingChildren();
        }

        public void endVisit(Dimension node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("@ED)");
        }

        public void preVisit(ASTNode node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("[");
        }

        public void postVisit(ASTNode node) {
            //$NON-NLS-1$
            ASTVisitorTest.this.b.append("]");
        }
    }

    // NAMES
    public void testSimpleName() {
        //$NON-NLS-1$
        Name x1 = this.ast.newName(new String[] { "Z" });
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$
        assertTrue("[(nSZZnS)]".equals(result));
    }

    public void testQualifiedName() {
        //$NON-NLS-1$ //$NON-NLS-2$
        Name x1 = this.ast.newName(new String[] { "X", "Y" });
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$
        assertTrue("[(nQ[(nSXXnS)][(nSYYnS)]nQ)]".equals(result));
    }

    // TYPES
    public void testPrimitiveType() {
        Type x1 = this.ast.newPrimitiveType(PrimitiveType.CHAR);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$
        assertTrue("[(tPcharchartP)]".equals(result));
    }

    public void testSimpleType() {
        //$NON-NLS-1$
        Type x1 = this.ast.newSimpleType(this.ast.newName(new String[] { "Z" }));
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$
        assertTrue("[(tS[(nSZZnS)]tS)]".equals(result));
    }

    public void testArrayType() {
        Type x0 = this.ast.newPrimitiveType(PrimitiveType.CHAR);
        Type x1 = this.ast.newArrayType(x0);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        String expected = this.ast.apiLevel() < AST.JLS8 ? "[(tA[(tPcharchartP)]tA)]" : "[(tA[(tPcharchartP)][(@ED@ED)]tA)]";
        //$NON-NLS-1$
        assertTrue(expected.equals(result));
    }

    /** @deprecated using deprecated code */
    public void testNameQualifiedType() {
        if (this.ast.apiLevel() < AST.JLS8) {
            return;
        }
        QualifiedName q = this.ast.newQualifiedName(this.N2, this.N3);
        NameQualifiedType x1 = this.ast.newNameQualifiedType(q, this.N1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(tPQ" + "[(nQ" + this.N2S + this.N3S + "nQ)]" + this.N1S + "tPQ)]"));
    }

    /** @deprecated using deprecated code */
    public void testParameterizedType() {
        if (this.ast.apiLevel() == AST.JLS2) {
            return;
        }
        ParameterizedType x1 = this.ast.newParameterizedType(this.T1);
        x1.typeArguments().add(this.T2);
        x1.typeArguments().add(this.PT1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(tM" + this.T1S + this.T2S + this.PT1S + "tM)]"));
    }

    /** @deprecated using deprecated code */
    public void testQualifiedType() {
        if (this.ast.apiLevel() == AST.JLS2) {
            return;
        }
        QualifiedType x1 = this.ast.newQualifiedType(this.T1, this.N1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(tQ" + this.T1S + this.N1S + "tQ)]"));
    }

    /** @deprecated using deprecated code */
    public void testWildcardType() {
        if (this.ast.apiLevel() == AST.JLS2) {
            return;
        }
        WildcardType x1 = this.ast.newWildcardType();
        x1.setBound(this.T1, true);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(tW" + this.T1S + "tW)]"));
    }

    /** @deprecated using deprecated code */
    public void testUnionType() {
        if (this.ast.apiLevel() <= AST.JLS4) {
            return;
        }
        UnionType x1 = this.ast.newUnionType();
        x1.types().add(this.T1);
        x1.types().add(this.T2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(tU" + this.T1S + this.T2S + "tU)]"));
    }

    // EXPRESSIONS and STATEMENTS
    public void testArrayAccess() {
        ArrayAccess x1 = this.ast.newArrayAccess();
        x1.setArray(this.E1);
        x1.setIndex(this.E2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(eAA" + this.E1S + this.E2S + "eAA)]"));
    }

    public void testArrayCreation() {
        ArrayCreation x1 = this.ast.newArrayCreation();
        x1.setType(this.ast.newArrayType(this.T1));
        x1.dimensions().add(this.E1);
        x1.dimensions().add(this.E2);
        x1.setInitializer(this.ast.newArrayInitializer());
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        String dim = this.ast.apiLevel() < AST.JLS8 ? "" : "[(@ED@ED)]";
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertTrue(result.equals("[(eAC" + "[(tA" + this.T1S + dim + "tA)]" + this.E1S + this.E2S + "[(eAIeAI)]eAC)]"));
    }

    public void testArrayInitializer() {
        ArrayInitializer x1 = this.ast.newArrayInitializer();
        x1.expressions().add(this.E1);
        x1.expressions().add(this.E2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(eAI" + this.E1S + this.E2S + "eAI)]"));
    }

    public void testAssertStatement() {
        AssertStatement x1 = this.ast.newAssertStatement();
        x1.setExpression(this.E1);
        x1.setMessage(this.E2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(sAS" + this.E1S + this.E2S + "sAS)]"));
    }

    public void testAssignment() {
        Assignment x1 = this.ast.newAssignment();
        x1.setLeftHandSide(this.E1);
        x1.setRightHandSide(this.E2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(=" + this.E1S + this.E2S + "=)]"));
    }

    public void testBlock() {
        Block x1 = this.ast.newBlock();
        x1.statements().add(this.S1);
        x1.statements().add(this.S2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(sB" + this.S1S + this.S2S + "sB)]"));
        // check that visiting children can be cut off
        v1.setVisitingChildren(false);
        this.b.setLength(0);
        x1.accept(v1);
        result = this.b.toString();
        //$NON-NLS-1$
        assertTrue(result.equals("[(sBsB)]"));
    }

    public void testBlockComment() {
        BlockComment x1 = this.ast.newBlockComment();
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$
        assertTrue("[(/**/)]".equals(result));
    }

    public void testBooleanLiteral() {
        BooleanLiteral x1 = this.ast.newBooleanLiteral(true);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$
        assertTrue(result.equals("[(eBLtruetrueeBL)]"));
    }

    public void testBreakStatement() {
        BreakStatement x1 = this.ast.newBreakStatement();
        x1.setLabel(this.N1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(sBR" + this.N1S + "sBR)]"));
    }

    public void testCastExpression() {
        CastExpression x1 = this.ast.newCastExpression();
        x1.setType(this.T1);
        x1.setExpression(this.E1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(eCS" + this.T1S + this.E1S + "eCS)]"));
    }

    public void testCatchClause() {
        CatchClause x1 = this.ast.newCatchClause();
        x1.setException(this.V1);
        x1.setBody(this.B1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(cc" + this.V1S + this.B1S + "cc)]"));
    }

    public void testCharacterLiteral() {
        CharacterLiteral x1 = this.ast.newCharacterLiteral();
        x1.setCharValue('q');
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$
        assertTrue(result.equals("[(eCL'q''q'eCL)]"));
    }

    /** @deprecated using deprecated code */
    public void testClassInstanceCreation() {
        ClassInstanceCreation x1 = this.ast.newClassInstanceCreation();
        x1.setExpression(this.E1);
        if (this.ast.apiLevel() == AST.JLS2) {
            x1.setName(this.N1);
        } else {
            x1.typeArguments().add(this.PT1);
            x1.setType(this.T1);
        }
        x1.setAnonymousClassDeclaration(this.ACD1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        if (this.ast.apiLevel() == AST.JLS2) {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(eCI" + this.E1S + this.N1S + this.ACD1S + "eCI)]"));
        } else {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(eCI" + this.E1S + this.PT1S + this.T1S + this.ACD1S + "eCI)]"));
        }
    }

    public void testAnonymousClassDeclaration() {
        AnonymousClassDeclaration x1 = this.ast.newAnonymousClassDeclaration();
        x1.bodyDeclarations().add(this.FD1);
        x1.bodyDeclarations().add(this.FD2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(ACD" + this.FD1S + this.FD2S + "ACD)]"));
    }

    public void testCompilationUnit() {
        CompilationUnit x1 = this.ast.newCompilationUnit();
        x1.setPackage(this.PD1);
        x1.imports().add(this.ID1);
        x1.imports().add(this.ID2);
        x1.types().add(this.TD1);
        x1.types().add(this.TD2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(CU" + this.PD1S + this.ID1S + this.ID2S + this.TD1S + this.TD2S + "CU)]"));
        // check that visiting children can be cut off
        v1.setVisitingChildren(false);
        this.b.setLength(0);
        x1.accept(v1);
        result = this.b.toString();
        //$NON-NLS-1$
        assertTrue(result.equals("[(CUCU)]"));
    }

    public void testConditionalExpression() {
        ConditionalExpression x1 = this.ast.newConditionalExpression();
        x1.setExpression(this.E1);
        x1.setThenExpression(this.E2);
        x1.setElseExpression(this.N1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(eCO" + this.E1S + this.E2S + this.N1S + "eCO)]"));
    }

    /** @deprecated using deprecated code */
    public void testConstructorInvocation() {
        ConstructorInvocation x1 = this.ast.newConstructorInvocation();
        if (this.ast.apiLevel() >= AST.JLS3) {
            x1.typeArguments().add(this.PT1);
        }
        x1.arguments().add(this.E1);
        x1.arguments().add(this.E2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        if (this.ast.apiLevel() == AST.JLS2) {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(sCI" + this.E1S + this.E2S + "sCI)]"));
        } else {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(sCI" + this.PT1S + this.E1S + this.E2S + "sCI)]"));
        }
    }

    public void testContinueStatement() {
        ContinueStatement x1 = this.ast.newContinueStatement();
        x1.setLabel(this.N1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(sCN" + this.N1S + "sCN)]"));
    }

    public void testCreationReference() {
        if (this.ast.apiLevel() < AST.JLS8)
            return;
        CreationReference x1 = this.ast.newCreationReference();
        x1.setType(this.T1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(eCR" + this.T1S + "eCR)]"));
    }

    public void testDoStatement() {
        DoStatement x1 = this.ast.newDoStatement();
        x1.setExpression(this.E1);
        x1.setBody(this.S1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(sDO" + this.S1S + this.E1S + "sDO)]"));
    }

    public void testEmptyStatement() {
        EmptyStatement x1 = this.ast.newEmptyStatement();
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$
        assertTrue(result.equals("[(sEMsEM)]"));
    }

    /** @deprecated Only to suppress warnings for refs to bodyDeclarations. */
    // TODO (jeem) - remove deprecation after 3.1 M4
    public void testEnumConstantDeclaration() {
        if (this.ast.apiLevel() == AST.JLS2) {
            return;
        }
        EnumConstantDeclaration x1 = this.ast.newEnumConstantDeclaration();
        x1.setJavadoc(this.JD1);
        x1.modifiers().add(this.MOD1);
        x1.modifiers().add(this.MOD2);
        x1.setName(this.N1);
        x1.arguments().add(this.E1);
        x1.arguments().add(this.E2);
        x1.setAnonymousClassDeclaration(this.ACD1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(ECD" + this.JD1S + this.MOD1S + this.MOD2S + this.N1S + this.E1S + this.E2S + this.ACD1S + "ECD)]"));
    }

    /** @deprecated using deprecated code */
    public void testEnumDeclaration() {
        if (this.ast.apiLevel() == AST.JLS2) {
            return;
        }
        EnumDeclaration x1 = this.ast.newEnumDeclaration();
        x1.setJavadoc(this.JD1);
        x1.modifiers().add(this.MOD1);
        x1.modifiers().add(this.MOD2);
        x1.setName(this.N1);
        x1.superInterfaceTypes().add(this.T1);
        x1.superInterfaceTypes().add(this.T2);
        x1.enumConstants().add(this.EC1);
        x1.enumConstants().add(this.EC2);
        x1.bodyDeclarations().add(this.FD1);
        x1.bodyDeclarations().add(this.FD2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(ED" + this.JD1S + this.MOD1S + this.MOD2S + this.N1S + this.T1S + this.T2S + this.EC1S + this.EC2S + this.FD1S + this.FD2S + "ED)]"));
    }

    public void testExpressionMethodReference() {
        if (this.ast.apiLevel() < AST.JLS8)
            return;
        ExpressionMethodReference x1 = this.ast.newExpressionMethodReference();
        x1.setExpression(this.E1);
        x1.setName(this.N1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(eEMR" + this.E1S + this.N1S + "eEMR)]"));
    }

    public void testExpressionStatement() {
        ExpressionStatement x1 = this.ast.newExpressionStatement(this.E1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(sEX" + this.E1S + "sEX)]"));
    }

    public void testExtraDimension() {
        if (this.ast.apiLevel() < AST.JLS8) {
            return;
        }
        Dimension x1 = this.ast.newDimension();
        x1.annotations().add(this.ANO1);
        x1.annotations().add(this.ANO2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("[(@ED" + this.ANO1S + this.ANO2S + "@ED)]", result);
    }

    public void testFieldAccess() {
        FieldAccess x1 = this.ast.newFieldAccess();
        x1.setExpression(this.E1);
        x1.setName(this.N1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(eFA" + this.E1S + this.N1S + "eFA)]"));
    }

    /** @deprecated using deprecated code */
    public void testFieldDeclaration() {
        FieldDeclaration x1 = this.ast.newFieldDeclaration(this.W1);
        x1.setJavadoc(this.JD1);
        if (this.ast.apiLevel() >= AST.JLS3) {
            x1.modifiers().add(this.MOD1);
            x1.modifiers().add(this.MOD2);
        }
        x1.setType(this.T1);
        x1.fragments().add(this.W2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        if (this.ast.apiLevel() == AST.JLS2) {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(FD" + this.JD1S + this.T1S + this.W1S + this.W2S + "FD)]"));
        } else {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(FD" + this.JD1S + this.MOD1S + this.MOD2S + this.T1S + this.W1S + this.W2S + "FD)]"));
        }
    }

    public void testForStatement() {
        ForStatement x1 = this.ast.newForStatement();
        x1.initializers().add(this.E1);
        x1.initializers().add(this.E2);
        x1.setExpression(this.N1);
        x1.updaters().add(this.N2);
        x1.updaters().add(this.N3);
        x1.setBody(this.S1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(sFR" + this.E1S + this.E2S + this.N1S + this.N2S + this.N3S + this.S1S + "sFR)]"));
    }

    /** @deprecated using deprecated code */
    public void testEnhancedForStatement() {
        if (this.ast.apiLevel() == AST.JLS2) {
            return;
        }
        EnhancedForStatement x1 = this.ast.newEnhancedForStatement();
        x1.setParameter(this.V1);
        x1.setExpression(this.E1);
        x1.setBody(this.S1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(sEFR" + this.V1S + this.E1S + this.S1S + "sEFR)]"));
    }

    public void testIfStatement() {
        IfStatement x1 = this.ast.newIfStatement();
        x1.setExpression(this.E1);
        x1.setThenStatement(this.S1);
        x1.setElseStatement(this.S2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(sIF" + this.E1S + this.S1S + this.S2S + "sIF)]"));
    }

    public void testImportDeclaration() {
        ImportDeclaration x1 = this.ast.newImportDeclaration();
        x1.setName(this.N1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(ID" + this.N1S + "ID)]"));
    }

    public void testInfixExpression() {
        InfixExpression x1 = this.ast.newInfixExpression();
        x1.setOperator(InfixExpression.Operator.PLUS);
        x1.setLeftOperand(this.E1);
        x1.setRightOperand(this.E2);
        x1.extendedOperands().add(this.N1);
        x1.extendedOperands().add(this.N2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(eIN+" + this.E1S + this.E2S + this.N1S + this.N2S + "+eIN)]"));
    }

    public void testInstanceofExpression() {
        InstanceofExpression x1 = this.ast.newInstanceofExpression();
        x1.setLeftOperand(this.E1);
        x1.setRightOperand(this.T1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(eIO" + this.E1S + this.T1S + "eIO)]"));
    }

    /** @deprecated using deprecated code */
    public void testInitializer() {
        Initializer x1 = this.ast.newInitializer();
        x1.setJavadoc(this.JD1);
        if (this.ast.apiLevel() >= AST.JLS3) {
            x1.modifiers().add(this.MOD1);
            x1.modifiers().add(this.MOD2);
        }
        x1.setBody(this.B1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        if (this.ast.apiLevel() == AST.JLS2) {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(IN" + this.JD1S + this.B1S + "IN)]"));
        } else {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(IN" + this.JD1S + this.MOD1S + this.MOD2S + this.B1S + "IN)]"));
        }
    }

    /**
	 * @deprecated (not really - just suppressing the warnings
	 * that come from testing Javadoc.getComment())
	 *
	 */
    public void testJavadoc() {
        Javadoc x1 = this.ast.newJavadoc();
        x1.tags().add(this.TAG1);
        // ASTVisitor() does not visit doc tags
        {
            TestVisitor v1 = new TestVisitor();
            this.b.setLength(0);
            x1.accept(v1);
            String result = this.b.toString();
            //$NON-NLS-1$
            assertTrue(("[(JDJD)]").equals(result));
        }
        // ASTVisitor(false) does not visit doc tags
        {
            TestVisitor v1 = new TestVisitor(false);
            this.b.setLength(0);
            x1.accept(v1);
            String result = this.b.toString();
            //$NON-NLS-1$
            assertTrue(("[(JDJD)]").equals(result));
        }
        // ASTVisitor(true) does visit doc tags
        {
            TestVisitor v1 = new TestVisitor(true);
            this.b.setLength(0);
            x1.accept(v1);
            String result = this.b.toString();
            //$NON-NLS-1$
            assertTrue(("[(JD" + this.TAG1S + "JD)]").equals(result));
        }
    }

    public void testLabeledStatement() {
        LabeledStatement x1 = this.ast.newLabeledStatement();
        x1.setLabel(this.N1);
        x1.setBody(this.S1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(sLA" + this.N1S + this.S1S + "sLA)]"));
    }

    public void testLineComment() {
        LineComment x1 = this.ast.newLineComment();
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$
        assertTrue("[(////)]".equals(result));
    }

    public void testMemberRef() {
        MemberRef x1 = this.ast.newMemberRef();
        x1.setQualifier(this.N1);
        x1.setName(this.N2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(MBREF" + this.N1S + this.N2S + "MBREF)]"));
    }

    /** @deprecated using deprecated code */
    public void testMethodDeclaration() {
        MethodDeclaration x1 = this.ast.newMethodDeclaration();
        x1.setJavadoc(this.JD1);
        if (this.ast.apiLevel() == AST.JLS2) {
            x1.setReturnType(this.T1);
        } else {
            x1.modifiers().add(this.MOD1);
            x1.modifiers().add(this.MOD2);
            x1.typeParameters().add(this.TP1);
            x1.setReturnType2(this.T1);
        }
        x1.setName(this.N1);
        x1.parameters().add(this.V1);
        x1.parameters().add(this.V2);
        if (this.ast.apiLevel() < AST.JLS8) {
            x1.thrownExceptions().add(this.N2);
            x1.thrownExceptions().add(this.N3);
        } else {
            x1.thrownExceptionTypes().add(this.T3);
            x1.thrownExceptionTypes().add(this.T4);
        }
        x1.setBody(this.B1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        if (this.ast.apiLevel() == AST.JLS2) {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertEquals("[(MD" + this.JD1S + this.T1S + this.N1S + this.V1S + this.V2S + this.N2S + this.N3S + this.B1S + "MD)]", result);
        } else if (this.ast.apiLevel() < AST.JLS8) {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertEquals("[(MD" + this.JD1S + this.MOD1S + this.MOD2S + this.TP1S + this.T1S + this.N1S + this.V1S + this.V2S + this.N2S + this.N3S + this.B1S + "MD)]", result);
        } else {
            //$NON-NLS-1$ //$NON-NLS-2$			
            assertEquals("[(MD" + this.JD1S + this.MOD1S + this.MOD2S + this.TP1S + this.T1S + this.N1S + this.V1S + this.V2S + this.T3S + this.T4S + this.B1S + "MD)]", result);
        }
    }

    /** @deprecated using deprecated code */
    public void testMethodInvocation() {
        MethodInvocation x1 = this.ast.newMethodInvocation();
        x1.setExpression(this.N1);
        if (this.ast.apiLevel() >= AST.JLS3) {
            x1.typeArguments().add(this.PT1);
        }
        x1.setName(this.N2);
        x1.arguments().add(this.E1);
        x1.arguments().add(this.E2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        if (this.ast.apiLevel() == AST.JLS2) {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(eMI" + this.N1S + this.N2S + this.E1S + this.E2S + "eMI)]"));
        } else {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(eMI" + this.N1S + this.PT1S + this.N2S + this.E1S + this.E2S + "eMI)]"));
        }
    }

    public void testMethodRef() {
        MethodRef x1 = this.ast.newMethodRef();
        x1.setQualifier(this.N1);
        x1.setName(this.N2);
        x1.parameters().add(this.MPARM1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(MTHREF" + this.N1S + this.N2S + this.MPARM1S + "MTHREF)]"));
    }

    public void testMethodRefParameter() {
        MethodRefParameter x1 = this.ast.newMethodRefParameter();
        x1.setType(this.T1);
        x1.setName(this.N1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(MPARM" + this.T1S + this.N1S + "MPARM)]"));
    }

    /** @deprecated using deprecated code */
    public void testModifier() {
        if (this.ast.apiLevel() == AST.JLS2) {
            return;
        }
        Modifier x1 = this.ast.newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(MODprivateprivateMOD)]"));
    }

    /** @deprecated using deprecated code */
    public void testNormalAnnotation() {
        if (this.ast.apiLevel() == AST.JLS2) {
            return;
        }
        NormalAnnotation x1 = this.ast.newNormalAnnotation();
        x1.setTypeName(this.N1);
        x1.values().add(this.MVP1);
        x1.values().add(this.MVP2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(@NAN" + this.N1S + this.MVP1S + this.MVP2S + "@NAN)]"));
    }

    /** @deprecated using deprecated code */
    public void testMemberValuePair() {
        if (this.ast.apiLevel() == AST.JLS2) {
            return;
        }
        MemberValuePair x1 = this.ast.newMemberValuePair();
        x1.setName(this.N1);
        x1.setValue(this.E1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(@MVP" + this.N1S + this.E1S + "@MVP)]"));
    }

    /** @deprecated using deprecated code */
    public void testMarkerAnnotation() {
        if (this.ast.apiLevel() == AST.JLS2) {
            return;
        }
        MarkerAnnotation x1 = this.ast.newMarkerAnnotation();
        x1.setTypeName(this.N1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(@MAN" + this.N1S + "@MAN)]"));
    }

    /** @deprecated using deprecated code */
    public void testSingleMemberAnnotation() {
        if (this.ast.apiLevel() == AST.JLS2) {
            return;
        }
        SingleMemberAnnotation x1 = this.ast.newSingleMemberAnnotation();
        x1.setTypeName(this.N1);
        x1.setValue(this.E1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(@SMAN" + this.N1S + this.E1S + "@SMAN)]"));
    }

    /** @deprecated using deprecated code */
    public void testAnnotationTypeDeclaration() {
        if (this.ast.apiLevel() == AST.JLS2) {
            return;
        }
        AnnotationTypeDeclaration x1 = this.ast.newAnnotationTypeDeclaration();
        x1.setJavadoc(this.JD1);
        x1.modifiers().add(this.MOD1);
        x1.modifiers().add(this.MOD2);
        x1.setName(this.N1);
        x1.bodyDeclarations().add(this.FD1);
        x1.bodyDeclarations().add(this.FD2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertTrue(result.equals("[(@TD" + this.JD1S + this.MOD1S + this.MOD2S + this.N1S + this.FD1S + this.FD2S + "@TD)]"));
    }

    /** @deprecated using deprecated code */
    public void testAnnotationTypeMemberDeclaration() {
        if (this.ast.apiLevel() == AST.JLS2) {
            return;
        }
        AnnotationTypeMemberDeclaration x1 = this.ast.newAnnotationTypeMemberDeclaration();
        x1.setJavadoc(this.JD1);
        x1.modifiers().add(this.MOD1);
        x1.modifiers().add(this.MOD2);
        x1.setType(this.T1);
        x1.setName(this.N1);
        x1.setDefault(this.E1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(@MD" + this.JD1S + this.MOD1S + this.MOD2S + this.T1S + this.N1S + this.E1S + "@MD)]"));
    }

    public void testNullLiteral() {
        NullLiteral x1 = this.ast.newNullLiteral();
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$
        assertTrue(result.equals("[(eNLeNL)]"));
    }

    public void testNumberLiteral() {
        //$NON-NLS-1$
        NumberLiteral x1 = this.ast.newNumberLiteral("1.0");
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$
        assertTrue(result.equals("[(eNU1.01.0eNU)]"));
    }

    /** @deprecated using deprecated code */
    public void testPackageDeclaration() {
        PackageDeclaration x1 = this.ast.newPackageDeclaration();
        if (this.ast.apiLevel() >= AST.JLS3) {
            x1.setJavadoc(this.JD1);
            x1.annotations().add(this.ANO1);
            x1.annotations().add(this.ANO2);
        }
        x1.setName(this.N1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        if (this.ast.apiLevel() == AST.JLS2) {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(PD" + this.N1S + "PD)]"));
        } else {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(PD" + this.JD1S + this.ANO1S + this.ANO2S + this.N1S + "PD)]"));
        }
    }

    public void testParenthesizedExpression() {
        ParenthesizedExpression x1 = this.ast.newParenthesizedExpression();
        x1.setExpression(this.E1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(ePA" + this.E1S + "ePA)]"));
    }

    public void testPostfixExpression() {
        PostfixExpression x1 = this.ast.newPostfixExpression();
        x1.setOperand(this.E1);
        x1.setOperator(PostfixExpression.Operator.INCREMENT);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(ePO++" + this.E1S + "++ePO)]"));
    }

    public void testPrefixExpression() {
        PrefixExpression x1 = this.ast.newPrefixExpression();
        x1.setOperand(this.E1);
        x1.setOperator(PrefixExpression.Operator.INCREMENT);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(ePR++" + this.E1S + "++ePR)]"));
    }

    public void testReturnStatement() {
        ReturnStatement x1 = this.ast.newReturnStatement();
        x1.setExpression(this.E1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(sRT" + this.E1S + "sRT)]"));
    }

    public void testStringLiteral() {
        StringLiteral x1 = this.ast.newStringLiteral();
        //$NON-NLS-1$
        x1.setLiteralValue("H");
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$
        assertTrue(result.equals("[(eSLHHeSL)]"));
    }

    /** @deprecated using deprecated code */
    public void testSuperConstructorInvocation() {
        SuperConstructorInvocation x1 = this.ast.newSuperConstructorInvocation();
        x1.setExpression(this.N1);
        if (this.ast.apiLevel() >= AST.JLS3) {
            x1.typeArguments().add(this.PT1);
        }
        x1.arguments().add(this.E1);
        x1.arguments().add(this.E2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        if (this.ast.apiLevel() == AST.JLS2) {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(sSC" + this.N1S + this.E1S + this.E2S + "sSC)]"));
        } else {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(sSC" + this.N1S + this.PT1S + this.E1S + this.E2S + "sSC)]"));
        }
    }

    public void testSuperFieldAccess() {
        SuperFieldAccess x1 = this.ast.newSuperFieldAccess();
        x1.setQualifier(this.N1);
        x1.setName(this.N2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(eSF" + this.N1S + this.N2S + "eSF)]"));
    }

    /** @deprecated using deprecated code */
    public void testSuperMethodInvocation() {
        SuperMethodInvocation x1 = this.ast.newSuperMethodInvocation();
        x1.setQualifier(this.N1);
        if (this.ast.apiLevel() >= AST.JLS3) {
            x1.typeArguments().add(this.PT1);
        }
        x1.setName(this.N2);
        x1.arguments().add(this.E1);
        x1.arguments().add(this.E2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        if (this.ast.apiLevel() == AST.JLS2) {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(eSM" + this.N1S + this.N2S + this.E1S + this.E2S + "eSM)]"));
        } else {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(eSM" + this.N1S + this.PT1S + this.N2S + this.E1S + this.E2S + "eSM)]"));
        }
    }

    public void testSuperMethodReference() {
        if (this.ast.apiLevel() < AST.JLS8) {
            return;
        }
        SuperMethodReference x1 = this.ast.newSuperMethodReference();
        x1.setQualifier(this.N1);
        x1.setName(this.N2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(eSMR" + this.N1S + this.N2S + "eSMR)]"));
    }

    public void testSwitchCase() {
        SwitchCase x1 = this.ast.newSwitchCase();
        x1.setExpression(this.E1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(sSC" + this.E1S + "sSC)]"));
    }

    public void testSwitchStatement() {
        SwitchStatement x1 = this.ast.newSwitchStatement();
        x1.setExpression(this.E1);
        x1.statements().add(this.S1);
        x1.statements().add(this.S2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(sSW" + this.E1S + this.S1S + this.S2S + "sSW)]"));
    }

    public void testSynchronizedStatement() {
        SynchronizedStatement x1 = this.ast.newSynchronizedStatement();
        x1.setExpression(this.E1);
        x1.setBody(this.B1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(sSY" + this.E1S + this.B1S + "sSY)]"));
    }

    public void testTagElement() {
        TagElement x1 = this.ast.newTagElement();
        //$NON-NLS-1$
        x1.setTagName("x");
        x1.fragments().add(this.TAG1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(("[(TGx" + this.TAG1S + "xTG)]").equals(result));
    }

    public void testTextElement() {
        TextElement x1 = this.ast.newTextElement();
        //$NON-NLS-1$
        x1.setText("x");
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$
        assertTrue("[(TXxxTX)]".equals(result));
    }

    public void testThisExpression() {
        ThisExpression x1 = this.ast.newThisExpression();
        x1.setQualifier(this.N1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(eTH" + this.N1S + "eTH)]"));
    }

    public void testThrowStatement() {
        ThrowStatement x1 = this.ast.newThrowStatement();
        x1.setExpression(this.E1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(sTR" + this.E1S + "sTR)]"));
    }

    /** @deprecated using deprecated code */
    public void testTryStatement() {
        TryStatement x1 = this.ast.newTryStatement();
        int level = this.ast.apiLevel();
        if (level >= AST.JLS4) {
            VariableDeclarationExpression vde1 = this.ast.newVariableDeclarationExpression(this.W1);
            vde1.setType(this.T1);
            x1.resources().add(vde1);
            VariableDeclarationExpression vde2 = this.ast.newVariableDeclarationExpression(this.W2);
            vde2.setType(this.T2);
            x1.resources().add(vde2);
        }
        x1.setBody(this.B1);
        CatchClause c1 = this.ast.newCatchClause();
        c1.setException(this.V1);
        c1.setBody(this.ast.newBlock());
        x1.catchClauses().add(c1);
        CatchClause c2 = this.ast.newCatchClause();
        c2.setException(this.V2);
        c2.setBody(this.ast.newBlock());
        x1.catchClauses().add(c2);
        x1.setFinally(this.ast.newBlock());
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        assertEquals("[(sTY" + (level >= AST.JLS4 ? "[(eVD" + this.T1S + this.W1S + "eVD)]" + "[(eVD" + this.T2S + this.W2S + "eVD)]" : "") + this.B1S + "[(cc" + this.V1S + "[(sBsB)]" + "cc)]" + "[(cc" + this.V2S + "[(sBsB)]" + "cc)]" + "[(sBsB)]" + "sTY)]", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
        result);
    }

    /** @deprecated using deprecated code */
    public void testTypeDeclaration() {
        TypeDeclaration x1 = this.ast.newTypeDeclaration();
        x1.setJavadoc(this.JD1);
        x1.setName(this.N1);
        if (this.ast.apiLevel() == AST.JLS2) {
            x1.setSuperclass(this.N2);
            x1.superInterfaces().add(this.N3);
            x1.superInterfaces().add(this.N4);
        } else {
            x1.modifiers().add(this.MOD1);
            x1.modifiers().add(this.MOD2);
            x1.typeParameters().add(this.TP1);
            x1.setSuperclassType(this.PT1);
            x1.superInterfaceTypes().add(this.T1);
            //$NON-NLS-1$
            x1.superInterfaceTypes().add(this.T2);
        }
        x1.bodyDeclarations().add(this.FD1);
        x1.bodyDeclarations().add(this.FD2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        if (this.ast.apiLevel() == AST.JLS2) {
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            assertTrue(result.equals("[(TD" + this.JD1S + this.N1S + this.N2S + this.N3S + this.N4S + this.FD1S + this.FD2S + "TD)]"));
        } else {
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            assertTrue(result.equals("[(TD" + this.JD1S + this.MOD1S + this.MOD2S + this.N1S + this.TP1S + this.PT1S + this.T1S + this.T2S + this.FD1S + this.FD2S + "TD)]"));
        }
    }

    public void testTypeDeclarationStatement() {
        TypeDeclarationStatement x1 = this.ast.newTypeDeclarationStatement(this.TD1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(sTD" + this.TD1S + "sTD)]"));
    }

    public void testTypeLiteral() {
        TypeLiteral x1 = this.ast.newTypeLiteral();
        x1.setType(this.T1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(eTL" + this.T1S + "eTL)]"));
    }

    public void testTypeMethodReference() {
        if (this.ast.apiLevel() < AST.JLS8)
            return;
        TypeMethodReference x1 = this.ast.newTypeMethodReference();
        x1.setType(this.T1);
        x1.setName(this.N1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(eTMR" + this.T1S + this.N1S + "eTMR)]"));
    }

    /** @deprecated using deprecated code */
    public void testSingleVariableDeclaration() {
        SingleVariableDeclaration x1 = this.ast.newSingleVariableDeclaration();
        if (this.ast.apiLevel() >= AST.JLS3) {
            x1.modifiers().add(this.MOD1);
            x1.modifiers().add(this.MOD2);
        }
        x1.setType(this.T1);
        x1.setName(this.N1);
        x1.setInitializer(this.E1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        if (this.ast.apiLevel() == AST.JLS2) {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(VD" + this.T1S + this.N1S + this.E1S + "VD)]"));
        } else {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(VD" + this.MOD1S + this.MOD2S + this.T1S + this.N1S + this.E1S + "VD)]"));
        }
    }

    public void testVariableDeclarationFragment() {
        VariableDeclarationFragment x1 = this.ast.newVariableDeclarationFragment();
        x1.setName(this.N1);
        x1.setInitializer(this.E1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(VS" + this.N1S + this.E1S + "VS)]"));
    }

    /** @deprecated using deprecated code */
    public void testVariableDeclarationExpression() {
        VariableDeclarationExpression x1 = this.ast.newVariableDeclarationExpression(this.W1);
        if (this.ast.apiLevel() >= AST.JLS3) {
            x1.modifiers().add(this.MOD1);
            x1.modifiers().add(this.MOD2);
        }
        x1.setType(this.T1);
        x1.fragments().add(this.W2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        if (this.ast.apiLevel() == AST.JLS2) {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(eVD" + this.T1S + this.W1S + this.W2S + "eVD)]"));
        } else {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(eVD" + this.MOD1S + this.MOD2S + this.T1S + this.W1S + this.W2S + "eVD)]"));
        }
    }

    /** @deprecated using deprecated code */
    public void testVariableDeclarationStatement() {
        VariableDeclarationStatement x1 = this.ast.newVariableDeclarationStatement(this.W1);
        if (this.ast.apiLevel() >= AST.JLS3) {
            x1.modifiers().add(this.MOD1);
            x1.modifiers().add(this.MOD2);
        }
        x1.setType(this.T1);
        x1.fragments().add(this.W2);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        if (this.ast.apiLevel() == AST.JLS2) {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(sVD" + this.T1S + this.W1S + this.W2S + "sVD)]"));
        } else {
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue(result.equals("[(sVD" + this.MOD1S + this.MOD2S + this.T1S + this.W1S + this.W2S + "sVD)]"));
        }
    }

    public void testWhileStatement() {
        WhileStatement x1 = this.ast.newWhileStatement();
        x1.setExpression(this.E1);
        x1.setBody(this.S1);
        TestVisitor v1 = new TestVisitor();
        this.b.setLength(0);
        x1.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(result.equals("[(sWH" + this.E1S + this.S1S + "sWH)]"));
    }

    public void testPrePost() {
        //$NON-NLS-1$
        SimpleName n1 = this.ast.newSimpleName("a");
        //$NON-NLS-1$
        SimpleName n2 = this.ast.newSimpleName("b");
        QualifiedName q = this.ast.newQualifiedName(n1, n2);
        TestVisitor v1 = new TestVisitor() {

            public void preVisit(ASTNode node) {
                //$NON-NLS-1$
                ASTVisitorTest.this.b.append(//$NON-NLS-1$
                "[");
                switch(node.getNodeType()) {
                    case ASTNode.QUALIFIED_NAME:
                        //$NON-NLS-1$
                        ASTVisitorTest.this.b.append(//$NON-NLS-1$
                        "q");
                        break;
                    case ASTNode.SIMPLE_NAME:
                        ASTVisitorTest.this.b.append(((SimpleName) node).getIdentifier());
                        break;
                }
            }

            public void postVisit(ASTNode node) {
                switch(node.getNodeType()) {
                    case ASTNode.QUALIFIED_NAME:
                        //$NON-NLS-1$
                        ASTVisitorTest.this.b.append(//$NON-NLS-1$
                        "q");
                        break;
                    case ASTNode.SIMPLE_NAME:
                        ASTVisitorTest.this.b.append(((SimpleName) node).getIdentifier());
                        break;
                }
                //$NON-NLS-1$
                ASTVisitorTest.this.b.append(//$NON-NLS-1$
                "]");
            }
        };
        this.b.setLength(0);
        q.accept(v1);
        String result = this.b.toString();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertTrue(result.equals("[q(nQ" + "[a(nSaanS)a]" + "[b(nSbbnS)b]" + "nQ)q]"));
    }

    public void testTraverseAndModify() {
        final TypeDeclaration typeDeclaration = this.ast.newTypeDeclaration();
        typeDeclaration.setName(this.N1);
        MethodDeclaration methodDeclaration = this.ast.newMethodDeclaration();
        //$NON-NLS-1$
        methodDeclaration.setName(this.ast.newSimpleName("M1"));
        typeDeclaration.bodyDeclarations().add(0, methodDeclaration);
        final MethodDeclaration methodDeclaration2 = this.ast.newMethodDeclaration();
        //$NON-NLS-1$
        methodDeclaration2.setName(this.ast.newSimpleName("M2"));
        typeDeclaration.bodyDeclarations().add(1, methodDeclaration2);
        MethodDeclaration methodDeclaration3 = this.ast.newMethodDeclaration();
        //$NON-NLS-1$
        methodDeclaration3.setName(this.ast.newSimpleName("M3"));
        typeDeclaration.bodyDeclarations().add(2, methodDeclaration3);
        // insert a new before the current node during a traverse
        TestVisitor v1 = new TestVisitor() {

            public boolean visit(MethodDeclaration node) {
                if (node == methodDeclaration2) {
                    MethodDeclaration methodDeclaration4 = ASTVisitorTest.this.ast.newMethodDeclaration();
                    methodDeclaration4.setName(//$NON-NLS-1$
                    ASTVisitorTest.this.ast.newSimpleName(//$NON-NLS-1$
                    "M4"));
                    typeDeclaration.bodyDeclarations().add(0, methodDeclaration4);
                }
                return super.visit(node);
            }
        };
        this.b.setLength(0);
        typeDeclaration.accept(v1);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong output", "[(TD[(nSNNnS)][(MD[(tPvoidvoidtP)][(nSM1M1nS)]MD)][(MD[(tPvoidvoidtP)][(nSM2M2nS)]MD)][(MD[(tPvoidvoidtP)][(nSM3M3nS)]MD)]TD)]", this.b.toString());
    }

    public void testTraverseAndModify_2() {
        final TypeDeclaration typeDeclaration = this.ast.newTypeDeclaration();
        typeDeclaration.setName(this.N1);
        MethodDeclaration methodDeclaration = this.ast.newMethodDeclaration();
        //$NON-NLS-1$
        methodDeclaration.setName(this.ast.newSimpleName("M1"));
        typeDeclaration.bodyDeclarations().add(0, methodDeclaration);
        final MethodDeclaration methodDeclaration2 = this.ast.newMethodDeclaration();
        //$NON-NLS-1$
        methodDeclaration2.setName(this.ast.newSimpleName("M2"));
        typeDeclaration.bodyDeclarations().add(1, methodDeclaration2);
        MethodDeclaration methodDeclaration3 = this.ast.newMethodDeclaration();
        //$NON-NLS-1$
        methodDeclaration3.setName(this.ast.newSimpleName("M3"));
        typeDeclaration.bodyDeclarations().add(2, methodDeclaration3);
        // insert a new after the current node during a traverse
        TestVisitor v1 = new TestVisitor() {

            public boolean visit(MethodDeclaration node) {
                if (node == methodDeclaration2) {
                    MethodDeclaration methodDeclaration4 = ASTVisitorTest.this.ast.newMethodDeclaration();
                    methodDeclaration4.setName(//$NON-NLS-1$
                    ASTVisitorTest.this.ast.newSimpleName(//$NON-NLS-1$
                    "M4"));
                    typeDeclaration.bodyDeclarations().add(3, methodDeclaration4);
                }
                return super.visit(node);
            }
        };
        this.b.setLength(0);
        typeDeclaration.accept(v1);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong output", "[(TD[(nSNNnS)][(MD[(tPvoidvoidtP)][(nSM1M1nS)]MD)][(MD[(tPvoidvoidtP)][(nSM2M2nS)]MD)][(MD[(tPvoidvoidtP)][(nSM3M3nS)]MD)][(MD[(tPvoidvoidtP)][(nSM4M4nS)]MD)]TD)]", this.b.toString());
    }

    public void testTraverseAndModify_3() {
        final InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newNumberLiteral("10"));
        infixExpression.setOperator(InfixExpression.Operator.PLUS);
        // insert a new after the current node during a traverse
        TestVisitor v1 = new TestVisitor() {

            public boolean visit(SimpleName node) {
                //$NON-NLS-1$
                infixExpression.setRightOperand(//$NON-NLS-1$
                ASTVisitorTest.this.ast.newNumberLiteral("22"));
                return super.visit(node);
            }
        };
        this.b.setLength(0);
        infixExpression.accept(v1);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong output", "[(eIN+[(nSiinS)][(eNU2222eNU)]+eIN)]", this.b.toString());
    }

    public void testTraverseAndModify_4() {
        final InfixExpression infixExpression = this.ast.newInfixExpression();
        //$NON-NLS-1$
        infixExpression.setLeftOperand(this.ast.newSimpleName("i"));
        //$NON-NLS-1$
        infixExpression.setRightOperand(this.ast.newNumberLiteral("10"));
        infixExpression.setOperator(InfixExpression.Operator.PLUS);
        // insert a new before the current node during a traverse
        TestVisitor v1 = new TestVisitor() {

            public boolean visit(NumberLiteral node) {
                //$NON-NLS-1$
                infixExpression.setLeftOperand(//$NON-NLS-1$
                ASTVisitorTest.this.ast.newSimpleName("j"));
                return super.visit(node);
            }
        };
        this.b.setLength(0);
        infixExpression.accept(v1);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong output", "[(eIN+[(nSiinS)][(eNU1010eNU)]+eIN)]", this.b.toString());
    }
}
