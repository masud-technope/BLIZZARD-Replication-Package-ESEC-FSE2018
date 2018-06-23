/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Tom Tromey - patch for readTable(String) as described in http://bugs.eclipse.org/bugs/show_bug.cgi?id=32196
 *     Stephan Herrmann - Contributions for 
 *								bug 366003 - CCE in ASTNode.resolveAnnotations(ASTNode.java:639)
 *								bug 374605 - Unreasonable warning for enum-based switch statements
 *								bug 393719 - [compiler] inconsistent warnings on iteration variables
 *								bug 382353 - [1.8][compiler] Implementation property modifiers should be accepted on default methods.
 *								bug 383973 - [1.8][compiler] syntax recovery in the presence of default methods
 *								bug 401035 - [1.8] A few tests have started failing recently
 *     Jesper S Moller - Contributions for
 *							bug 382701 - [1.8][compiler] Implement semantic analysis of Lambda expressions & Reference expression
 *							bug 399695 - [1.8][compiler] [1.8][compiler] migrate parser to other syntax for default methods
 *							bug 384567 - [1.5][compiler] Compiler accepts illegal modifiers on package declaration
 *									bug 393192 - Incomplete type hierarchy with > 10 annotations
 *        Andy Clement - Contributions for
 *                          Bug 383624 - [1.8][compiler] Revive code generation support for type annotations (from Olivier's work)
 *                          Bug 409250 - [1.8][compiler] Various loose ends in 308 code generation
 *                          Bug 415821 - [1.8][compiler] CLASS_EXTENDS target type annotation missing for anonymous classes
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.parser;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.AND_AND_Expression;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.AssertStatement;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.CharLiteral;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.CombinedBinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ContinueStatement;
import org.eclipse.jdt.internal.compiler.ast.DoStatement;
import org.eclipse.jdt.internal.compiler.ast.DoubleLiteral;
import org.eclipse.jdt.internal.compiler.ast.EmptyStatement;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.FloatLiteral;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.ast.IntersectionCastTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LongLiteral;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.OR_OR_Expression;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.PostfixExpression;
import org.eclipse.jdt.internal.compiler.ast.PrefixExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedSuperReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedThisReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Receiver;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.ast.UnionTypeReference;
import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.ExtraCompilerModifiers;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.parser.diagnose.DiagnoseParser;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.util.Util;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Parser implements TerminalTokens, ParserBasicInformation, ConflictedParser, OperatorIds, TypeIds {

    protected static final int THIS_CALL = ExplicitConstructorCall.This;

    protected static final int SUPER_CALL = ExplicitConstructorCall.Super;

    //$NON-NLS-1$
    public static final char[] FALL_THROUGH_TAG = "$FALL-THROUGH$".toCharArray();

    //$NON-NLS-1$
    public static final char[] CASES_OMITTED_TAG = "$CASES-OMITTED$".toCharArray();

    public static char asb[] = null;

    public static char asr[] = null;

    //ast stack
    protected static final int AstStackIncrement = 100;

    public static char base_action[] = null;

    public static final int BracketKinds = 3;

    public static short check_table[] = null;

    public static final int CurlyBracket = 2;

    private static final boolean DEBUG = false;

    private static final boolean DEBUG_AUTOMATON = false;

    //$NON-NLS-1$
    private static final String EOF_TOKEN = "$eof";

    //$NON-NLS-1$
    private static final String ERROR_TOKEN = "$error";

    //expression stack
    protected static final int ExpressionStackIncrement = 100;

    protected static final int GenericsStackIncrement = 10;

    //$NON-NLS-1$
    private static final String FILEPREFIX = "parser";

    public static char in_symb[] = null;

    //$NON-NLS-1$
    private static final String INVALID_CHARACTER = "Invalid Character";

    public static char lhs[] = null;

    public static String name[] = null;

    public static char nasb[] = null;

    public static char nasr[] = null;

    public static char non_terminal_index[] = null;

    //$NON-NLS-1$
    private static final String READABLE_NAMES_FILE = "readableNames";

    public static String readableName[] = null;

    public static byte rhs[] = null;

    public static int[] reverse_index = null;

    public static char[] recovery_templates_index = null;

    public static char[] recovery_templates = null;

    public static char[] statements_recovery_filter = null;

    public static long rules_compliance[] = null;

    public static final int RoundBracket = 0;

    public static byte scope_la[] = null;

    public static char scope_lhs[] = null;

    public static char scope_prefix[] = null;

    public static char scope_rhs[] = null;

    public static char scope_state[] = null;

    public static char scope_state_set[] = null;

    public static char scope_suffix[] = null;

    public static final int SquareBracket = 1;

    //internal data for the automat
    protected static final int StackIncrement = 255;

    public static char term_action[] = null;

    public static byte term_check[] = null;

    public static char terminal_index[] = null;

    //$NON-NLS-1$
    private static final String UNEXPECTED_EOF = "Unexpected End Of File";

    public static boolean VERBOSE_RECOVERY = false;

    private static enum LocalTypeKind implements  {

        LOCAL() {
        }
        , METHOD_REFERENCE() {
        }
        , LAMBDA() {
        }
        ;
    }

    // resumeOnSyntaxError codes:
    // halt and throw up hands.
    protected static final int HALT = 0;

    // stacks adjusted, alternate goal from check point.
    protected static final int RESTART = 1;

    // stacks untouched, just continue from where left off.
    protected static final int RESUME = 2;

    public Scanner scanner;

    public int currentToken;

    static {
        try {
            initTables();
        } catch (java.io.IOException ex) {
            throw new ExceptionInInitializerError(ex.getMessage());
        }
    }

    public static int asi(int state) {
        return asb[original_state(state)];
    }

    public static final short base_check(int i) {
        return check_table[i - (NUM_RULES + 1)];
    }

    private static final void buildFile(String filename, List listToDump) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filename));
            for (Iterator iterator = listToDump.iterator(); iterator.hasNext(); ) {
                writer.write(String.valueOf(iterator.next()));
            }
            writer.flush();
        } catch (IOException e) {
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e1) {
                }
            }
        }
        //$NON-NLS-1$
        System.out.println(filename + " creation complete");
    }

    private static void buildFileForCompliance(String file, int length, String[] tokens) {
        byte[] result = new byte[length * 8];
        for (int i = 0; i < tokens.length; i = i + 3) {
            if (//$NON-NLS-1$
            "2".equals(tokens[i])) {
                int index = Integer.parseInt(tokens[i + 1]);
                String token = tokens[i + 2].trim();
                long compliance = 0;
                if (//$NON-NLS-1$
                "1.4".equals(//$NON-NLS-1$
                token)) {
                    compliance = ClassFileConstants.JDK1_4;
                } else if (//$NON-NLS-1$
                "1.5".equals(//$NON-NLS-1$
                token)) {
                    compliance = ClassFileConstants.JDK1_5;
                } else if (//$NON-NLS-1$
                "1.6".equals(//$NON-NLS-1$
                token)) {
                    compliance = ClassFileConstants.JDK1_6;
                } else if (//$NON-NLS-1$
                "1.7".equals(//$NON-NLS-1$
                token)) {
                    compliance = ClassFileConstants.JDK1_7;
                } else if (//$NON-NLS-1$
                "1.8".equals(//$NON-NLS-1$
                token)) {
                    compliance = ClassFileConstants.JDK1_8;
                } else if (//$NON-NLS-1$
                "recovery".equals(//$NON-NLS-1$
                token)) {
                    compliance = ClassFileConstants.JDK_DEFERRED;
                }
                int j = index * 8;
                result[j] = (byte) (compliance >>> 56);
                result[j + 1] = (byte) (compliance >>> 48);
                result[j + 2] = (byte) (compliance >>> 40);
                result[j + 3] = (byte) (compliance >>> 32);
                result[j + 4] = (byte) (compliance >>> 24);
                result[j + 5] = (byte) (compliance >>> 16);
                result[j + 6] = (byte) (compliance >>> 8);
                result[j + 7] = (byte) (compliance);
            }
        }
        buildFileForTable(file, result);
    }

    private static final String[] buildFileForName(String filename, String contents) {
        String[] result = new String[contents.length()];
        result[0] = null;
        int resultCount = 1;
        StringBuffer buffer = new StringBuffer();
        //$NON-NLS-1$
        int start = contents.indexOf("name[]");
        start = contents.indexOf('\"', start);
        //$NON-NLS-1$
        int end = contents.indexOf("};", start);
        contents = contents.substring(start, end);
        boolean addLineSeparator = false;
        int tokenStart = -1;
        StringBuffer currentToken = new StringBuffer();
        for (int i = 0; i < contents.length(); i++) {
            char c = contents.charAt(i);
            if (c == '\"') {
                if (tokenStart == -1) {
                    tokenStart = i + 1;
                } else {
                    if (addLineSeparator) {
                        buffer.append('\n');
                        result[resultCount++] = currentToken.toString();
                        currentToken = new StringBuffer();
                    }
                    String token = contents.substring(tokenStart, i);
                    if (token.equals(ERROR_TOKEN)) {
                        token = INVALID_CHARACTER;
                    } else if (token.equals(EOF_TOKEN)) {
                        token = UNEXPECTED_EOF;
                    }
                    buffer.append(token);
                    currentToken.append(token);
                    addLineSeparator = true;
                    tokenStart = -1;
                }
            }
            if (tokenStart == -1 && c == '+') {
                addLineSeparator = false;
            }
        }
        if (currentToken.length() > 0) {
            result[resultCount++] = currentToken.toString();
        }
        buildFileForTable(filename, buffer.toString().toCharArray());
        System.arraycopy(result, 0, result = new String[resultCount], 0, resultCount);
        return result;
    }

    private static void buildFileForReadableName(String file, char[] newLhs, char[] newNonTerminalIndex, String[] newName, String[] tokens) {
        ArrayList entries = new ArrayList();
        boolean[] alreadyAdded = new boolean[newName.length];
        for (int i = 0; i < tokens.length; i = i + 3) {
            if (//$NON-NLS-1$
            "1".equals(tokens[i])) {
                int index = newNonTerminalIndex[newLhs[Integer.parseInt(tokens[i + 1])]];
                StringBuffer buffer = new StringBuffer();
                if (!alreadyAdded[index]) {
                    alreadyAdded[index] = true;
                    buffer.append(newName[index]);
                    buffer.append('=');
                    buffer.append(tokens[i + 2].trim());
                    buffer.append('\n');
                    entries.add(String.valueOf(buffer));
                }
            }
        }
        int i = 1;
        while (!INVALID_CHARACTER.equals(newName[i])) i++;
        i++;
        for (; i < alreadyAdded.length; i++) {
            if (!alreadyAdded[i]) {
                //$NON-NLS-1$
                System.out.println(//$NON-NLS-1$
                newName[i] + " has no readable name");
            }
        }
        Collections.sort(entries);
        buildFile(file, entries);
    }

    private static final void buildFileForTable(String filename, byte[] bytes) {
        java.io.FileOutputStream stream = null;
        try {
            stream = new java.io.FileOutputStream(filename);
            stream.write(bytes);
        } catch (IOException e) {
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
        //$NON-NLS-1$
        System.out.println(filename + " creation complete");
    }

    private static final void buildFileForTable(String filename, char[] chars) {
        byte[] bytes = new byte[chars.length * 2];
        for (int i = 0; i < chars.length; i++) {
            bytes[2 * i] = (byte) (chars[i] >>> 8);
            bytes[2 * i + 1] = (byte) (chars[i] & 0xFF);
        }
        java.io.FileOutputStream stream = null;
        try {
            stream = new java.io.FileOutputStream(filename);
            stream.write(bytes);
        } catch (IOException e) {
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
        //$NON-NLS-1$
        System.out.println(filename + " creation complete");
    }

    private static final byte[] buildFileOfByteFor(String filename, String tag, String[] tokens) {
        //transform the String tokens into chars before dumping then into file
        int i = 0;
        //read upto the tag
        while (!tokens[i++].equals(tag)) /*empty*/
        {
        }
        //read upto the }
        //can't be bigger
        byte[] bytes = new byte[tokens.length];
        int ic = 0;
        String token;
        while (//$NON-NLS-1$
        !(token = tokens[i++]).equals("}")) {
            int c = Integer.parseInt(token);
            bytes[ic++] = (byte) c;
        }
        //resize
        System.arraycopy(bytes, 0, bytes = new byte[ic], 0, ic);
        buildFileForTable(filename, bytes);
        return bytes;
    }

    private static final char[] buildFileOfIntFor(String filename, String tag, String[] tokens) {
        //transform the String tokens into chars before dumping then into file
        int i = 0;
        //read upto the tag
        while (!tokens[i++].equals(tag)) /*empty*/
        {
        }
        //read upto the }
        //can't be bigger
        char[] chars = new char[tokens.length];
        int ic = 0;
        String token;
        while (//$NON-NLS-1$
        !(token = tokens[i++]).equals("}")) {
            int c = Integer.parseInt(token);
            chars[ic++] = (char) c;
        }
        //resize
        System.arraycopy(chars, 0, chars = new char[ic], 0, ic);
        buildFileForTable(filename, chars);
        return chars;
    }

    private static final void buildFileOfShortFor(String filename, String tag, String[] tokens) {
        //transform the String tokens into chars before dumping then into file
        int i = 0;
        //read upto the tag
        while (!tokens[i++].equals(tag)) /*empty*/
        {
        }
        //read upto the }
        //can't be bigger
        char[] chars = new char[tokens.length];
        int ic = 0;
        String token;
        while (//$NON-NLS-1$
        !(token = tokens[i++]).equals("}")) {
            int c = Integer.parseInt(token);
            chars[ic++] = (char) (c + 32768);
        }
        //resize
        System.arraycopy(chars, 0, chars = new char[ic], 0, ic);
        buildFileForTable(filename, chars);
    }

    private static void buildFilesForRecoveryTemplates(String indexFilename, String templatesFilename, char[] newTerminalIndex, char[] newNonTerminalIndex, String[] newName, char[] newLhs, String[] tokens) {
        int[] newReverse = computeReverseTable(newTerminalIndex, newNonTerminalIndex, newName);
        char[] newRecoveyTemplatesIndex = new char[newNonTerminalIndex.length];
        char[] newRecoveyTemplates = new char[newNonTerminalIndex.length];
        int newRecoveyTemplatesPtr = 0;
        for (int i = 0; i < tokens.length; i = i + 3) {
            if (//$NON-NLS-1$
            "3".equals(tokens[i])) {
                int length = newRecoveyTemplates.length;
                if (length == newRecoveyTemplatesPtr + 1) {
                    System.arraycopy(newRecoveyTemplates, 0, newRecoveyTemplates = new char[length * 2], 0, length);
                }
                newRecoveyTemplates[newRecoveyTemplatesPtr++] = 0;
                int index = newLhs[Integer.parseInt(tokens[i + 1])];
                newRecoveyTemplatesIndex[index] = (char) newRecoveyTemplatesPtr;
                String token = tokens[i + 2].trim();
                //$NON-NLS-1$
                java.util.StringTokenizer st = new java.util.StringTokenizer(token, " ");
                String[] terminalNames = new String[st.countTokens()];
                int t = 0;
                while (st.hasMoreTokens()) {
                    terminalNames[t++] = st.nextToken();
                }
                for (int j = 0; j < terminalNames.length; j++) {
                    int symbol = getSymbol(terminalNames[j], newName, newReverse);
                    if (symbol > -1) {
                        length = newRecoveyTemplates.length;
                        if (length == newRecoveyTemplatesPtr + 1) {
                            System.arraycopy(newRecoveyTemplates, 0, newRecoveyTemplates = new char[length * 2], 0, length);
                        }
                        newRecoveyTemplates[newRecoveyTemplatesPtr++] = (char) symbol;
                    }
                }
            }
        }
        newRecoveyTemplates[newRecoveyTemplatesPtr++] = 0;
        System.arraycopy(newRecoveyTemplates, 0, newRecoveyTemplates = new char[newRecoveyTemplatesPtr], 0, newRecoveyTemplatesPtr);
        buildFileForTable(indexFilename, newRecoveyTemplatesIndex);
        buildFileForTable(templatesFilename, newRecoveyTemplates);
    }

    private static void buildFilesForStatementsRecoveryFilter(String filename, char[] newNonTerminalIndex, char[] newLhs, String[] tokens) {
        char[] newStatementsRecoveryFilter = new char[newNonTerminalIndex.length];
        for (int i = 0; i < tokens.length; i = i + 3) {
            if (//$NON-NLS-1$
            "4".equals(tokens[i])) {
                int index = newLhs[Integer.parseInt(tokens[i + 1])];
                newStatementsRecoveryFilter[index] = 1;
            }
        }
        buildFileForTable(filename, newStatementsRecoveryFilter);
    }

    public static final void buildFilesFromLPG(String dataFilename, String dataFilename2) {
        //RUN THIS METHOD TO GENERATE PARSER*.RSC FILES
        //build from the lpg javadcl.java files that represents the parser tables
        //lhs check_table asb asr symbol_index
        //[org.eclipse.jdt.internal.compiler.parser.Parser.buildFilesFromLPG("d:/leapfrog/grammar/javadcl.java")]
        char[] contents = CharOperation.NO_CHAR;
        try {
            contents = Util.getFileCharContent(new File(dataFilename), null);
        } catch (IOException ex) {
            System.out.println(Messages.parser_incorrectPath);
            return;
        }
        java.util.StringTokenizer st = //$NON-NLS-1$
        new java.util.StringTokenizer(new String(contents), " \t\n\r[]={,;");
        String[] tokens = new String[st.countTokens()];
        int j = 0;
        while (st.hasMoreTokens()) {
            tokens[j++] = st.nextToken();
        }
        final String prefix = FILEPREFIX;
        int i = 0;
        //$NON-NLS-1$ //$NON-NLS-2$
        char[] newLhs = buildFileOfIntFor(prefix + (++i) + ".rsc", "lhs", tokens);
        //$NON-NLS-2$ //$NON-NLS-1$
        buildFileOfShortFor(prefix + (++i) + ".rsc", "check_table", tokens);
        //$NON-NLS-2$ //$NON-NLS-1$
        buildFileOfIntFor(prefix + (++i) + ".rsc", "asb", tokens);
        //$NON-NLS-2$ //$NON-NLS-1$
        buildFileOfIntFor(prefix + (++i) + ".rsc", "asr", tokens);
        //$NON-NLS-2$ //$NON-NLS-1$
        buildFileOfIntFor(prefix + (++i) + ".rsc", "nasb", tokens);
        //$NON-NLS-2$ //$NON-NLS-1$
        buildFileOfIntFor(prefix + (++i) + ".rsc", "nasr", tokens);
        //$NON-NLS-2$ //$NON-NLS-1$
        char[] newTerminalIndex = buildFileOfIntFor(prefix + (++i) + ".rsc", "terminal_index", tokens);
        //$NON-NLS-1$ //$NON-NLS-2$
        char[] newNonTerminalIndex = buildFileOfIntFor(prefix + (++i) + ".rsc", "non_terminal_index", tokens);
        //$NON-NLS-2$ //$NON-NLS-1$
        buildFileOfIntFor(prefix + (++i) + ".rsc", "term_action", tokens);
        //$NON-NLS-2$ //$NON-NLS-1$
        buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_prefix", tokens);
        //$NON-NLS-2$ //$NON-NLS-1$
        buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_suffix", tokens);
        //$NON-NLS-2$ //$NON-NLS-1$
        buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_lhs", tokens);
        //$NON-NLS-2$ //$NON-NLS-1$
        buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_state_set", tokens);
        //$NON-NLS-2$ //$NON-NLS-1$
        buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_rhs", tokens);
        //$NON-NLS-2$ //$NON-NLS-1$
        buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_state", tokens);
        //$NON-NLS-2$ //$NON-NLS-1$
        buildFileOfIntFor(prefix + (++i) + ".rsc", "in_symb", tokens);
        //$NON-NLS-2$ //$NON-NLS-1$
        byte[] newRhs = buildFileOfByteFor(prefix + (++i) + ".rsc", "rhs", tokens);
        //$NON-NLS-2$ //$NON-NLS-1$
        buildFileOfByteFor(prefix + (++i) + ".rsc", "term_check", tokens);
        //$NON-NLS-2$ //$NON-NLS-1$
        buildFileOfByteFor(prefix + (++i) + ".rsc", "scope_la", tokens);
        //$NON-NLS-1$
        String[] newName = buildFileForName(prefix + (++i) + ".rsc", new String(contents));
        contents = CharOperation.NO_CHAR;
        try {
            contents = Util.getFileCharContent(new File(dataFilename2), null);
        } catch (IOException ex) {
            System.out.println(Messages.parser_incorrectPath);
            return;
        }
        //$NON-NLS-1$
        st = new java.util.StringTokenizer(new String(contents), "\t\n\r#");
        tokens = new String[st.countTokens()];
        j = 0;
        while (st.hasMoreTokens()) {
            tokens[j++] = st.nextToken();
        }
        //$NON-NLS-1$
        buildFileForCompliance(prefix + (++i) + ".rsc", newRhs.length, tokens);
        //$NON-NLS-1$
        buildFileForReadableName(READABLE_NAMES_FILE + ".props", newLhs, newNonTerminalIndex, newName, tokens);
        buildFilesForRecoveryTemplates(//$NON-NLS-1$
        prefix + (++i) + //$NON-NLS-1$
        ".rsc", //$NON-NLS-1$
        prefix + (++i) + //$NON-NLS-1$
        ".rsc", newTerminalIndex, newNonTerminalIndex, newName, newLhs, tokens);
        buildFilesForStatementsRecoveryFilter(//$NON-NLS-1$
        prefix + (++i) + //$NON-NLS-1$
        ".rsc", newNonTerminalIndex, newLhs, tokens);
        System.out.println(Messages.parser_moveFiles);
    }

    protected static int[] computeReverseTable(char[] newTerminalIndex, char[] newNonTerminalIndex, String[] newName) {
        int[] newReverseTable = new int[newName.length];
        for (int j = 0; j < newName.length; j++) {
            found: {
                for (int k = 0; k < newTerminalIndex.length; k++) {
                    if (newTerminalIndex[k] == j) {
                        newReverseTable[j] = k;
                        break found;
                    }
                }
                for (int k = 0; k < newNonTerminalIndex.length; k++) {
                    if (newNonTerminalIndex[k] == j) {
                        newReverseTable[j] = -k;
                        break found;
                    }
                }
            }
        }
        return newReverseTable;
    }

    private static int getSymbol(String terminalName, String[] newName, int[] newReverse) {
        for (int j = 0; j < newName.length; j++) {
            if (terminalName.equals(newName[j])) {
                return newReverse[j];
            }
        }
        return -1;
    }

    public static int in_symbol(int state) {
        return in_symb[original_state(state)];
    }

    public static final void initTables() throws java.io.IOException {
        final String prefix = FILEPREFIX;
        int i = 0;
        //$NON-NLS-1$
        lhs = readTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        char[] chars = readTable(prefix + (++i) + ".rsc");
        check_table = new short[chars.length];
        for (int c = chars.length; c-- > 0; ) {
            check_table[c] = (short) (chars[c] - 32768);
        }
        //$NON-NLS-1$
        asb = readTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        asr = readTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        nasb = readTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        nasr = readTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        terminal_index = readTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        non_terminal_index = readTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        term_action = readTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        scope_prefix = readTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        scope_suffix = readTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        scope_lhs = readTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        scope_state_set = readTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        scope_rhs = readTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        scope_state = readTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        in_symb = readTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        rhs = readByteTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        term_check = readByteTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        scope_la = readByteTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        name = readNameTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        rules_compliance = readLongTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        readableName = readReadableNameTable(READABLE_NAMES_FILE + ".props");
        reverse_index = computeReverseTable(terminal_index, non_terminal_index, name);
        //$NON-NLS-1$
        recovery_templates_index = readTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        recovery_templates = readTable(prefix + (++i) + ".rsc");
        //$NON-NLS-1$
        statements_recovery_filter = readTable(prefix + (++i) + ".rsc");
        base_action = lhs;
    }

    public static int nasi(int state) {
        return nasb[original_state(state)];
    }

    public static int ntAction(int state, int sym) {
        return base_action[state + sym];
    }

    protected static int original_state(int state) {
        return -base_check(state);
    }

    protected static byte[] readByteTable(String filename) throws java.io.IOException {
        //files are located at Parser.class directory
        InputStream stream = Parser.class.getResourceAsStream(filename);
        if (stream == null) {
            throw new java.io.IOException(Messages.bind(Messages.parser_missingFile, filename));
        }
        byte[] bytes = null;
        try {
            stream = new BufferedInputStream(stream);
            bytes = Util.getInputStreamAsByteArray(stream, -1);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
        return bytes;
    }

    protected static long[] readLongTable(String filename) throws java.io.IOException {
        //files are located at Parser.class directory
        InputStream stream = Parser.class.getResourceAsStream(filename);
        if (stream == null) {
            throw new java.io.IOException(Messages.bind(Messages.parser_missingFile, filename));
        }
        byte[] bytes = null;
        try {
            stream = new BufferedInputStream(stream);
            bytes = Util.getInputStreamAsByteArray(stream, -1);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
        //minimal integrity check (even size expected)
        int length = bytes.length;
        if (length % 8 != 0)
            throw new java.io.IOException(Messages.bind(Messages.parser_corruptedFile, filename));
        // convert bytes into longs
        long[] longs = new long[length / 8];
        int i = 0;
        int longIndex = 0;
        while (true) {
            longs[longIndex++] = (((long) (bytes[i++] & 0xFF)) << 56) + (((long) (bytes[i++] & 0xFF)) << 48) + (((long) (bytes[i++] & 0xFF)) << 40) + (((long) (bytes[i++] & 0xFF)) << 32) + (((long) (bytes[i++] & 0xFF)) << 24) + (((long) (bytes[i++] & 0xFF)) << 16) + (((long) (bytes[i++] & 0xFF)) << 8) + (bytes[i++] & 0xFF);
            if (i == length)
                break;
        }
        return longs;
    }

    protected static String[] readNameTable(String filename) throws java.io.IOException {
        char[] contents = readTable(filename);
        char[][] nameAsChar = CharOperation.splitOn('\n', contents);
        String[] result = new String[nameAsChar.length + 1];
        result[0] = null;
        for (int i = 0; i < nameAsChar.length; i++) {
            result[i + 1] = new String(nameAsChar[i]);
        }
        return result;
    }

    protected static String[] readReadableNameTable(String filename) {
        String[] result = new String[name.length];
        InputStream is = Parser.class.getResourceAsStream(filename);
        Properties props = new Properties();
        try {
            props.load(is);
        } catch (IOException e) {
            result = name;
            return result;
        }
        for (int i = 0; i < NT_OFFSET + 1; i++) {
            result[i] = name[i];
        }
        for (int i = NT_OFFSET; i < name.length; i++) {
            String n = props.getProperty(name[i]);
            if (n != null && n.length() > 0) {
                result[i] = n;
            } else {
                result[i] = name[i];
            }
        }
        return result;
    }

    protected static char[] readTable(String filename) throws java.io.IOException {
        //files are located at Parser.class directory
        InputStream stream = Parser.class.getResourceAsStream(filename);
        if (stream == null) {
            throw new java.io.IOException(Messages.bind(Messages.parser_missingFile, filename));
        }
        byte[] bytes = null;
        try {
            stream = new BufferedInputStream(stream);
            bytes = Util.getInputStreamAsByteArray(stream, -1);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
        //minimal integrity check (even size expected)
        int length = bytes.length;
        if ((length & 1) != 0)
            throw new java.io.IOException(Messages.bind(Messages.parser_corruptedFile, filename));
        // convert bytes into chars
        char[] chars = new char[length / 2];
        int i = 0;
        int charIndex = 0;
        while (true) {
            chars[charIndex++] = (char) (((bytes[i++] & 0xFF) << 8) + (bytes[i++] & 0xFF));
            if (i == length)
                break;
        }
        return chars;
    }

    public static int tAction(int state, int sym) {
        return term_action[term_check[base_action[state] + sym] == sym ? base_action[state] + sym : base_action[state]];
    }

    protected int astLengthPtr;

    protected int[] astLengthStack;

    protected int astPtr;

    protected ASTNode[] astStack = new ASTNode[AstStackIncrement];

    public CompilationUnitDeclaration compilationUnit;

    /*the result from parse()*/
    protected RecoveredElement currentElement;

    //tells the scanner to jump over some parts of the code/expressions like method bodies
    protected boolean diet = false;

    // if > 0 force the none-diet-parsing mode (even if diet if requested) [field parsing with anonymous inner classes...]
    protected int dietInt = 0;

    //accurate only when used ! (the start position is pushed into intStack while the end the current one)
    protected int endPosition;

    protected int endStatementPosition;

    protected int expressionLengthPtr;

    protected int[] expressionLengthStack;

    protected int expressionPtr;

    protected Expression[] expressionStack = new Expression[ExpressionStackIncrement];

    protected int rBracketPosition;

    // handle for multiple parsing goals
    public int firstToken;

    /* jsr308 -- Type annotation management, we now maintain type annotations in a separate stack
	   as otherwise they get interspersed with other expressions and some of the code is not prepared
	   to handle such interleaving and will look ugly if changed. 
	   
	   See consumeArrayCreationExpressionWithoutInitializer for example. 

	   Where SE8 annotations occur in a place SE5 annotations are legal, the SE8 annotations end up in
	   the expression stack as we have no way of distinguishing between the two.
	*/
    protected int typeAnnotationPtr;

    protected int typeAnnotationLengthPtr;

    protected Annotation[] typeAnnotationStack = new Annotation[TypeAnnotationStackIncrement];

    protected int[] typeAnnotationLengthStack;

    // annotation stack
    protected static final int TypeAnnotationStackIncrement = 100;

    // generics management
    protected int genericsIdentifiersLengthPtr;

    protected int[] genericsIdentifiersLengthStack = new int[GenericsStackIncrement];

    protected int genericsLengthPtr;

    protected int[] genericsLengthStack = new int[GenericsStackIncrement];

    protected int genericsPtr;

    protected ASTNode[] genericsStack = new ASTNode[GenericsStackIncrement];

    protected boolean hasError;

    protected boolean hasReportedError;

    //identifiers stacks
    protected int identifierLengthPtr;

    protected int[] identifierLengthStack;

    protected long[] identifierPositionStack;

    protected int identifierPtr;

    protected char[][] identifierStack;

    protected boolean ignoreNextOpeningBrace;

    protected boolean ignoreNextClosingBrace;

    //positions , dimensions , .... (int stacks)
    protected int intPtr;

    protected int[] intStack;

    //handle for multiple parsing goals
    public int lastAct;

    //error recovery management
    protected int lastCheckPoint;

    protected int lastErrorEndPosition;

    protected int lastErrorEndPositionBeforeRecovery = -1;

    protected int lastIgnoredToken, nextIgnoredToken;

    // for recovering some incomplete list (interfaces, throws or parameters)
    protected int listLength;

    // for recovering some incomplete list (type parameters)
    protected int listTypeParameterLength;

    //accurate only when used !
    protected int lParenPos, rParenPos;

    protected int modifiers;

    protected int modifiersSourceStart;

    protected int colonColonStart = -1;

    //the ptr is nestedType
    protected int[] nestedMethod;

    protected int forStartPosition = 0;

    protected int nestedType, dimensions;

    ASTNode[] noAstNodes = new ASTNode[AstStackIncrement];

    Expression[] noExpressions = new Expression[ExpressionStackIncrement];

    //modifiers dimensions nestedType etc.......
    protected boolean optimizeStringLiterals = true;

    protected CompilerOptions options;

    protected ProblemReporter problemReporter;

    //accurate only when used !
    protected int rBraceStart, rBraceEnd, rBraceSuccessorStart;

    protected int realBlockPtr;

    protected int[] realBlockStack;

    protected int recoveredStaticInitializerStart;

    public ReferenceContext referenceContext;

    public boolean reportOnlyOneSyntaxError = false;

    public boolean reportSyntaxErrorIsRequired = true;

    protected boolean restartRecovery;

    protected boolean annotationRecoveryActivated = true;

    protected int lastPosistion;

    // statement recovery
    public boolean methodRecoveryActivated = false;

    protected boolean statementRecoveryActivated = false;

    protected TypeDeclaration[] recoveredTypes;

    protected int recoveredTypePtr;

    protected int nextTypeStart;

    protected TypeDeclaration pendingRecoveredType;

    public RecoveryScanner recoveryScanner;

    protected int[] stack = new int[StackIncrement];

    protected int stateStackTop;

    protected int synchronizedBlockSourceStart;

    protected int[] variablesCounter;

    protected boolean checkExternalizeStrings;

    protected boolean recordStringLiterals;

    // javadoc
    public Javadoc javadoc;

    public JavadocParser javadocParser;

    // used for recovery
    protected int lastJavadocEnd;

    public org.eclipse.jdt.internal.compiler.ReadManager readManager;

    protected int valueLambdaNestDepth = -1;

    private int stateStackLengthStack[] = new int[0];

    protected boolean parsingJava8Plus;

    protected int unstackedAct = ERROR_ACTION;

    private boolean haltOnSyntaxError = false;

    private boolean tolerateDefaultClassMethods = false;

    private boolean processingLambdaParameterList = false;

    private boolean expectTypeAnnotation = false;

    private boolean reparsingLambdaExpression = false;

    public  Parser() {
    // Caveat Emptor: For inheritance purposes and then only in very special needs. Only minimal state is initialized !
    }

    public  Parser(ProblemReporter problemReporter, boolean optimizeStringLiterals) {
        this.problemReporter = problemReporter;
        this.options = problemReporter.options;
        this.optimizeStringLiterals = optimizeStringLiterals;
        initializeScanner();
        this.parsingJava8Plus = this.options.sourceLevel >= ClassFileConstants.JDK1_8;
        this.astLengthStack = new int[50];
        this.expressionLengthStack = new int[30];
        this.typeAnnotationLengthStack = new int[30];
        this.intStack = new int[50];
        this.identifierStack = new char[30][];
        this.identifierLengthStack = new int[30];
        this.nestedMethod = new int[30];
        this.realBlockStack = new int[30];
        this.identifierPositionStack = new long[30];
        this.variablesCounter = new int[30];
        // javadoc support
        this.javadocParser = createJavadocParser();
    }

    protected void annotationRecoveryCheckPoint(int start, int end) {
        if (this.lastCheckPoint < end) {
            this.lastCheckPoint = end + 1;
        }
    }

    public void arrayInitializer(int length) {
        //length is the size of the array Initializer
        //expressionPtr points on the last elt of the arrayInitializer,
        // in other words, it has not been decremented yet.
        ArrayInitializer ai = new ArrayInitializer();
        if (length != 0) {
            this.expressionPtr -= length;
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, ai.expressions = new Expression[length], 0, length);
        }
        pushOnExpressionStack(ai);
        //positionning
        ai.sourceEnd = this.endStatementPosition;
        ai.sourceStart = this.intStack[this.intPtr--];
    }

    protected void blockReal() {
        // See consumeLocalVariableDeclarationStatement in case of change: duplicated code
        // increment the amount of declared variables for this block
        this.realBlockStack[this.realBlockPtr]++;
    }

    /*
 * Build initial recovery state.
 * Recovery state is inferred from the current state of the parser (reduced node stack).
 */
    public RecoveredElement buildInitialRecoveryState() {
        /* initialize recovery by retrieving available reduced nodes
	 * also rebuild bracket balance
	 */
        this.lastCheckPoint = 0;
        this.lastErrorEndPositionBeforeRecovery = this.scanner.currentPosition;
        RecoveredElement element = null;
        if (this.referenceContext instanceof CompilationUnitDeclaration) {
            element = new RecoveredUnit(this.compilationUnit, 0, this);
            /* ignore current stack state, since restarting from the beginnning
		   since could not trust simple brace count */
            // restart recovery from scratch
            this.compilationUnit.currentPackage = null;
            this.compilationUnit.imports = null;
            this.compilationUnit.types = null;
            this.currentToken = 0;
            this.listLength = 0;
            this.listTypeParameterLength = 0;
            this.endPosition = 0;
            this.endStatementPosition = 0;
            return element;
        } else {
            if (this.referenceContext instanceof AbstractMethodDeclaration) {
                element = new RecoveredMethod((AbstractMethodDeclaration) this.referenceContext, null, 0, this);
                this.lastCheckPoint = ((AbstractMethodDeclaration) this.referenceContext).bodyStart;
                if (this.statementRecoveryActivated) {
                    element = element.add(new Block(0), 0);
                }
            } else {
                /* Initializer bodies are parsed in the context of the type declaration, we must thus search it inside */
                if (this.referenceContext instanceof TypeDeclaration) {
                    TypeDeclaration type = (TypeDeclaration) this.referenceContext;
                    FieldDeclaration[] fieldDeclarations = type.fields;
                    int length = fieldDeclarations == null ? 0 : fieldDeclarations.length;
                    for (int i = 0; i < length; i++) {
                        FieldDeclaration field = fieldDeclarations[i];
                        if (field != null && field.getKind() == AbstractVariableDeclaration.INITIALIZER && ((Initializer) field).block != null && field.declarationSourceStart <= this.scanner.initialPosition && this.scanner.initialPosition <= field.declarationSourceEnd && this.scanner.eofPosition <= field.declarationSourceEnd + 1) {
                            element = new RecoveredInitializer(field, null, 1, this);
                            this.lastCheckPoint = field.declarationSourceStart;
                            break;
                        }
                    }
                }
            }
        }
        if (element == null)
            return element;
        for (int i = 0; i <= this.astPtr; i++) {
            ASTNode node = this.astStack[i];
            if (node instanceof AbstractMethodDeclaration) {
                AbstractMethodDeclaration method = (AbstractMethodDeclaration) node;
                if (method.declarationSourceEnd == 0) {
                    element = element.add(method, 0);
                    this.lastCheckPoint = method.bodyStart;
                } else {
                    element = element.add(method, 0);
                    this.lastCheckPoint = method.declarationSourceEnd + 1;
                }
                continue;
            }
            if (node instanceof Initializer) {
                Initializer initializer = (Initializer) node;
                // ignore initializer with no block
                if (initializer.block == null)
                    continue;
                if (initializer.declarationSourceEnd == 0) {
                    element = element.add(initializer, 1);
                    this.lastCheckPoint = initializer.sourceStart;
                } else {
                    element = element.add(initializer, 0);
                    this.lastCheckPoint = initializer.declarationSourceEnd + 1;
                }
                continue;
            }
            if (node instanceof FieldDeclaration) {
                FieldDeclaration field = (FieldDeclaration) node;
                if (field.declarationSourceEnd == 0) {
                    element = element.add(field, 0);
                    if (field.initialization == null) {
                        this.lastCheckPoint = field.sourceEnd + 1;
                    } else {
                        this.lastCheckPoint = field.initialization.sourceEnd + 1;
                    }
                } else {
                    element = element.add(field, 0);
                    this.lastCheckPoint = field.declarationSourceEnd + 1;
                }
                continue;
            }
            if (node instanceof TypeDeclaration) {
                TypeDeclaration type = (TypeDeclaration) node;
                if ((type.modifiers & ClassFileConstants.AccEnum) != 0) {
                    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=340691
                    continue;
                }
                if (type.declarationSourceEnd == 0) {
                    element = element.add(type, 0);
                    this.lastCheckPoint = type.bodyStart;
                } else {
                    element = element.add(type, 0);
                    this.lastCheckPoint = type.declarationSourceEnd + 1;
                }
                continue;
            }
            if (node instanceof ImportReference) {
                ImportReference importRef = (ImportReference) node;
                element = element.add(importRef, 0);
                this.lastCheckPoint = importRef.declarationSourceEnd + 1;
            }
            if (this.statementRecoveryActivated) {
                if (node instanceof Block) {
                    Block block = (Block) node;
                    element = element.add(block, 0);
                    this.lastCheckPoint = block.sourceEnd + 1;
                } else if (node instanceof LocalDeclaration) {
                    LocalDeclaration statement = (LocalDeclaration) node;
                    element = element.add(statement, 0);
                    this.lastCheckPoint = statement.sourceEnd + 1;
                } else if (node instanceof Expression) {
                    if (node instanceof Assignment || node instanceof PrefixExpression || node instanceof PostfixExpression || node instanceof MessageSend || node instanceof AllocationExpression) {
                        // recover only specific expressions
                        Expression statement = (Expression) node;
                        element = element.add(statement, 0);
                        if (statement.statementEnd != -1) {
                            this.lastCheckPoint = statement.statementEnd + 1;
                        } else {
                            this.lastCheckPoint = statement.sourceEnd + 1;
                        }
                    }
                } else if (node instanceof Statement) {
                    Statement statement = (Statement) node;
                    element = element.add(statement, 0);
                    this.lastCheckPoint = statement.sourceEnd + 1;
                }
            }
        }
        if (this.statementRecoveryActivated) {
            if (this.pendingRecoveredType != null && this.scanner.startPosition - 1 <= this.pendingRecoveredType.declarationSourceEnd) {
                // Add the pending type to the AST if this type isn't already added in the AST.
                element = element.add(this.pendingRecoveredType, 0);
                this.lastCheckPoint = this.pendingRecoveredType.declarationSourceEnd + 1;
                this.pendingRecoveredType = null;
            }
        }
        return element;
    }

    protected void checkAndSetModifiers(int flag) {
        if (// duplicate modifier
        (this.modifiers & flag) != 0) {
            this.modifiers |= ExtraCompilerModifiers.AccAlternateModifierProblem;
        }
        this.modifiers |= flag;
        if (this.modifiersSourceStart < 0)
            this.modifiersSourceStart = this.scanner.startPosition;
        if (this.currentElement != null) {
            this.currentElement.addModifier(flag, this.modifiersSourceStart);
        }
    }

    public void checkComment() {
        // discard obsolete comments while inside methods or fields initializer (see bug 74369)
        if (!(this.diet && this.dietInt == 0) && this.scanner.commentPtr >= 0) {
            flushCommentsDefinedPriorTo(this.endStatementPosition);
        }
        int lastComment = this.scanner.commentPtr;
        if (this.modifiersSourceStart >= 0) {
            // eliminate comments located after modifierSourceStart if positioned
            while (lastComment >= 0) {
                int commentSourceStart = this.scanner.commentStarts[lastComment];
                if (commentSourceStart < 0)
                    commentSourceStart = -commentSourceStart;
                if (commentSourceStart <= this.modifiersSourceStart)
                    break;
                lastComment--;
            }
        }
        if (lastComment >= 0) {
            // consider all remaining leading comments to be part of current declaration
            int lastCommentStart = this.scanner.commentStarts[0];
            if (lastCommentStart < 0)
                lastCommentStart = -lastCommentStart;
            if (// if there is no 'for' in-between.
            this.forStartPosition != 0 || this.forStartPosition < lastCommentStart) {
                this.modifiersSourceStart = lastCommentStart;
            }
            // non javadoc comment have negative end positions
            while (lastComment >= 0 && this.scanner.commentStops[lastComment] < 0) lastComment--;
            if (lastComment >= 0 && this.javadocParser != null) {
                //stop is one over,
                int commentEnd = this.scanner.commentStops[lastComment] - 1;
                // do not report problem before last parsed comment while recovering code...
                if (this.javadocParser.shouldReportProblems) {
                    this.javadocParser.reportProblems = this.currentElement == null || commentEnd > this.lastJavadocEnd;
                } else {
                    this.javadocParser.reportProblems = false;
                }
                if (this.javadocParser.checkDeprecation(lastComment)) {
                    checkAndSetModifiers(ClassFileConstants.AccDeprecated);
                }
                // null if check javadoc is not activated
                this.javadoc = this.javadocParser.docComment;
                if (this.currentElement == null)
                    this.lastJavadocEnd = commentEnd;
            }
        }
    }

    protected void checkNonNLSAfterBodyEnd(int declarationEnd) {
        if (this.scanner.currentPosition - 1 <= declarationEnd) {
            this.scanner.eofPosition = declarationEnd < Integer.MAX_VALUE ? declarationEnd + 1 : declarationEnd;
            try {
                while (this.scanner.getNextToken() != TokenNameEOF/*empty*/
                ) {
                }
            } catch (InvalidInputException e) {
            }
        }
    }

    protected void classInstanceCreation(boolean isQualified) {
        // ClassInstanceCreationExpression ::= 'new' ClassType '(' ArgumentListopt ')' ClassBodyopt
        // ClassBodyopt produces a null item on the astStak if it produces NO class body
        // An empty class body produces a 0 on the length stack.....
        AllocationExpression alloc;
        int length;
        if (((length = this.astLengthStack[this.astLengthPtr--]) == 1) && (this.astStack[this.astPtr] == null)) {
            //NO ClassBody
            this.astPtr--;
            if (isQualified) {
                alloc = new QualifiedAllocationExpression();
            } else {
                alloc = new AllocationExpression();
            }
            //the position has been stored explicitly
            alloc.sourceEnd = this.endPosition;
            if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                this.expressionPtr -= length;
                System.arraycopy(this.expressionStack, this.expressionPtr + 1, alloc.arguments = new Expression[length], 0, length);
            }
            alloc.type = getTypeReference(0);
            checkForDiamond(alloc.type);
            //the default constructor with the correct number of argument
            //will be created and added by the TC (see createsInternalConstructorWithBinding)
            alloc.sourceStart = this.intStack[this.intPtr--];
            pushOnExpressionStack(alloc);
        } else {
            dispatchDeclarationInto(length);
            TypeDeclaration anonymousTypeDeclaration = (TypeDeclaration) this.astStack[this.astPtr];
            anonymousTypeDeclaration.declarationSourceEnd = this.endStatementPosition;
            anonymousTypeDeclaration.bodyEnd = this.endStatementPosition;
            if (anonymousTypeDeclaration.allocation != null) {
                anonymousTypeDeclaration.allocation.sourceEnd = this.endStatementPosition;
                checkForDiamond(anonymousTypeDeclaration.allocation.type);
            }
            if (length == 0 && !containsComment(anonymousTypeDeclaration.bodyStart, anonymousTypeDeclaration.bodyEnd)) {
                anonymousTypeDeclaration.bits |= ASTNode.UndocumentedEmptyBlock;
            }
            this.astPtr--;
            this.astLengthPtr--;
        }
    }

    protected void checkForDiamond(TypeReference allocType) {
        if (allocType instanceof ParameterizedSingleTypeReference) {
            ParameterizedSingleTypeReference type = (ParameterizedSingleTypeReference) allocType;
            if (type.typeArguments == TypeReference.NO_TYPE_ARGUMENTS) {
                if (this.options.sourceLevel < ClassFileConstants.JDK1_7) {
                    problemReporter().diamondNotBelow17(allocType);
                }
                if (// https://bugs.eclipse.org/bugs/show_bug.cgi?id=351965
                this.options.sourceLevel > ClassFileConstants.JDK1_4) {
                    type.bits |= ASTNode.IsDiamond;
                }
            // else don't even bother to recognize this as <>
            }
        } else if (allocType instanceof ParameterizedQualifiedTypeReference) {
            ParameterizedQualifiedTypeReference type = (ParameterizedQualifiedTypeReference) allocType;
            if (// Don't care for X<>.Y<> and X<>.Y<String>
            type.typeArguments[type.typeArguments.length - 1] == TypeReference.NO_TYPE_ARGUMENTS) {
                if (this.options.sourceLevel < ClassFileConstants.JDK1_7) {
                    problemReporter().diamondNotBelow17(allocType, type.typeArguments.length - 1);
                }
                if (// https://bugs.eclipse.org/bugs/show_bug.cgi?id=351965
                this.options.sourceLevel > ClassFileConstants.JDK1_4) {
                    type.bits |= ASTNode.IsDiamond;
                }
            // else don't even bother to recognize this as <>
            }
        }
    }

    protected ParameterizedQualifiedTypeReference computeQualifiedGenericsFromRightSide(TypeReference rightSide, int dim, Annotation[][] annotationsOnDimensions) {
        int nameSize = this.identifierLengthStack[this.identifierLengthPtr];
        int tokensSize = nameSize;
        if (rightSide instanceof ParameterizedSingleTypeReference) {
            tokensSize++;
        } else if (rightSide instanceof SingleTypeReference) {
            tokensSize++;
        } else if (rightSide instanceof ParameterizedQualifiedTypeReference) {
            tokensSize += ((QualifiedTypeReference) rightSide).tokens.length;
        } else if (rightSide instanceof QualifiedTypeReference) {
            tokensSize += ((QualifiedTypeReference) rightSide).tokens.length;
        }
        TypeReference[][] typeArguments = new TypeReference[tokensSize][];
        char[][] tokens = new char[tokensSize][];
        long[] positions = new long[tokensSize];
        Annotation[][] typeAnnotations = null;
        if (rightSide instanceof ParameterizedSingleTypeReference) {
            ParameterizedSingleTypeReference singleParameterizedTypeReference = (ParameterizedSingleTypeReference) rightSide;
            tokens[nameSize] = singleParameterizedTypeReference.token;
            positions[nameSize] = (((long) singleParameterizedTypeReference.sourceStart) << 32) + singleParameterizedTypeReference.sourceEnd;
            typeArguments[nameSize] = singleParameterizedTypeReference.typeArguments;
            if (singleParameterizedTypeReference.annotations != null) {
                typeAnnotations = new Annotation[tokensSize][];
                typeAnnotations[nameSize] = singleParameterizedTypeReference.annotations[0];
            }
        } else if (rightSide instanceof SingleTypeReference) {
            SingleTypeReference singleTypeReference = (SingleTypeReference) rightSide;
            tokens[nameSize] = singleTypeReference.token;
            positions[nameSize] = (((long) singleTypeReference.sourceStart) << 32) + singleTypeReference.sourceEnd;
            if (singleTypeReference.annotations != null) {
                typeAnnotations = new Annotation[tokensSize][];
                typeAnnotations[nameSize] = singleTypeReference.annotations[0];
            }
        } else if (rightSide instanceof ParameterizedQualifiedTypeReference) {
            ParameterizedQualifiedTypeReference parameterizedTypeReference = (ParameterizedQualifiedTypeReference) rightSide;
            TypeReference[][] rightSideTypeArguments = parameterizedTypeReference.typeArguments;
            System.arraycopy(rightSideTypeArguments, 0, typeArguments, nameSize, rightSideTypeArguments.length);
            char[][] rightSideTokens = parameterizedTypeReference.tokens;
            System.arraycopy(rightSideTokens, 0, tokens, nameSize, rightSideTokens.length);
            long[] rightSidePositions = parameterizedTypeReference.sourcePositions;
            System.arraycopy(rightSidePositions, 0, positions, nameSize, rightSidePositions.length);
            Annotation[][] rightSideAnnotations = parameterizedTypeReference.annotations;
            if (rightSideAnnotations != null) {
                typeAnnotations = new Annotation[tokensSize][];
                System.arraycopy(rightSideAnnotations, 0, typeAnnotations, nameSize, rightSideAnnotations.length);
            }
        } else if (rightSide instanceof QualifiedTypeReference) {
            QualifiedTypeReference qualifiedTypeReference = (QualifiedTypeReference) rightSide;
            char[][] rightSideTokens = qualifiedTypeReference.tokens;
            System.arraycopy(rightSideTokens, 0, tokens, nameSize, rightSideTokens.length);
            long[] rightSidePositions = qualifiedTypeReference.sourcePositions;
            System.arraycopy(rightSidePositions, 0, positions, nameSize, rightSidePositions.length);
            Annotation[][] rightSideAnnotations = qualifiedTypeReference.annotations;
            if (rightSideAnnotations != null) {
                typeAnnotations = new Annotation[tokensSize][];
                System.arraycopy(rightSideAnnotations, 0, typeAnnotations, nameSize, rightSideAnnotations.length);
            }
        }
        int currentTypeArgumentsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        TypeReference[] currentTypeArguments = new TypeReference[currentTypeArgumentsLength];
        this.genericsPtr -= currentTypeArgumentsLength;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, currentTypeArguments, 0, currentTypeArgumentsLength);
        if (nameSize == 1) {
            tokens[0] = this.identifierStack[this.identifierPtr];
            positions[0] = this.identifierPositionStack[this.identifierPtr--];
            typeArguments[0] = currentTypeArguments;
        } else {
            this.identifierPtr -= nameSize;
            System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, nameSize);
            System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, nameSize);
            typeArguments[nameSize - 1] = currentTypeArguments;
        }
        this.identifierLengthPtr--;
        ParameterizedQualifiedTypeReference typeRef = new ParameterizedQualifiedTypeReference(tokens, typeArguments, dim, annotationsOnDimensions, positions);
        while (nameSize > 0) {
            int length;
            if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
                if (typeAnnotations == null)
                    typeAnnotations = new Annotation[tokensSize][];
                System.arraycopy(this.typeAnnotationStack, (this.typeAnnotationPtr -= length) + 1, typeAnnotations[nameSize - 1] = new Annotation[length], 0, length);
                if (nameSize == 1) {
                    typeRef.sourceStart = typeAnnotations[0][0].sourceStart;
                }
            }
            nameSize--;
        }
        if ((typeRef.annotations = typeAnnotations) != null) {
            typeRef.bits |= ASTNode.HasTypeAnnotations;
        }
        return typeRef;
    }

    protected void concatExpressionLists() {
        this.expressionLengthStack[--this.expressionLengthPtr]++;
    }

    protected void concatGenericsLists() {
        this.genericsLengthStack[this.genericsLengthPtr - 1] += this.genericsLengthStack[this.genericsLengthPtr--];
    }

    protected void concatNodeLists() {
        /*
	 * This is a case where you have two sublists into the this.astStack that you want
	 * to merge in one list. There is no action required on the this.astStack. The only
	 * thing you need to do is merge the two lengths specified on the astStackLength.
	 * The top two length are for example:
	 * ... p   n
	 * and you want to result in a list like:
	 * ... n+p
	 * This means that the p could be equals to 0 in case there is no astNode pushed
	 * on the this.astStack.
	 * Look at the InterfaceMemberDeclarations for an example.
	 */
        this.astLengthStack[this.astLengthPtr - 1] += this.astLengthStack[this.astLengthPtr--];
    }

    protected void consumeAdditionalBound() {
        pushOnGenericsStack(getTypeReference(this.intStack[this.intPtr--]));
    }

    protected void consumeAdditionalBound1() {
    // nothing to be done.
    // The reference type1 is consumed by consumeReferenceType1 method.
    }

    protected void consumeAdditionalBoundList() {
        concatGenericsLists();
    }

    protected void consumeAdditionalBoundList1() {
        concatGenericsLists();
    }

    protected boolean isIndirectlyInsideLambdaExpression() {
        return false;
    }

    protected void consumeAllocationHeader() {
        if (this.currentElement == null) {
            // should never occur, this consumeRule is only used in recovery mode
            return;
        }
        if (this.currentToken == TokenNameLBRACE) {
            // beginning of an anonymous type
            TypeDeclaration anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
            anonymousType.name = CharOperation.NO_CHAR;
            anonymousType.bits |= (ASTNode.IsAnonymousType | ASTNode.IsLocalType);
            anonymousType.sourceStart = this.intStack[this.intPtr--];
            anonymousType.declarationSourceStart = anonymousType.sourceStart;
            // closing parenthesis
            anonymousType.sourceEnd = this.rParenPos;
            QualifiedAllocationExpression alloc = new QualifiedAllocationExpression(anonymousType);
            alloc.type = getTypeReference(0);
            alloc.sourceStart = anonymousType.sourceStart;
            alloc.sourceEnd = anonymousType.sourceEnd;
            this.lastCheckPoint = anonymousType.bodyStart = this.scanner.currentPosition;
            this.currentElement = this.currentElement.add(anonymousType, 0);
            this.lastIgnoredToken = -1;
            if (isIndirectlyInsideLambdaExpression())
                this.ignoreNextOpeningBrace = true;
            else
                // opening brace already taken into account
                this.currentToken = 0;
            return;
        }
        // force to restart at this exact position
        this.lastCheckPoint = this.scanner.startPosition;
        // request to restart from here on
        this.restartRecovery = true;
    }

    protected void consumeAnnotationAsModifier() {
        Expression expression = this.expressionStack[this.expressionPtr];
        int sourceStart = expression.sourceStart;
        if (this.modifiersSourceStart < 0) {
            this.modifiersSourceStart = sourceStart;
        }
    }

    protected void consumeAnnotationName() {
        if (this.currentElement != null && !this.expectTypeAnnotation) {
            int start = this.intStack[this.intPtr];
            int end = (int) (this.identifierPositionStack[this.identifierPtr] & 0x00000000FFFFFFFFL);
            annotationRecoveryCheckPoint(start, end);
            if (this.annotationRecoveryActivated) {
                this.currentElement = this.currentElement.addAnnotationName(this.identifierPtr, this.identifierLengthPtr, start, 0);
            }
        }
        this.recordStringLiterals = false;
        this.expectTypeAnnotation = false;
    }

    protected void consumeAnnotationTypeDeclaration() {
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            //there are length declarations
            //dispatch according to the type of the declarations
            dispatchDeclarationInto(length);
        }
        TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
        //convert constructor that do not have the type's name into methods
        typeDecl.checkConstructors(this);
        //always add <clinit> (will be remove at code gen time if empty)
        if (this.scanner.containsAssertKeyword) {
            typeDecl.bits |= ASTNode.ContainsAssertion;
        }
        typeDecl.addClinit();
        typeDecl.bodyEnd = this.endStatementPosition;
        if (length == 0 && !containsComment(typeDecl.bodyStart, typeDecl.bodyEnd)) {
            typeDecl.bits |= ASTNode.UndocumentedEmptyBlock;
        }
        typeDecl.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeAnnotationTypeDeclarationHeader() {
        TypeDeclaration annotationTypeDeclaration = (TypeDeclaration) this.astStack[this.astPtr];
        if (this.currentToken == TokenNameLBRACE) {
            annotationTypeDeclaration.bodyStart = this.scanner.currentPosition;
        }
        if (this.currentElement != null) {
            // used to avoid branching back into the regular automaton
            this.restartRecovery = true;
        }
        // flush the comments related to the annotation type header
        this.scanner.commentPtr = -1;
    }

    protected void consumeAnnotationTypeDeclarationHeaderName() {
        // consumeAnnotationTypeDeclarationHeader ::= Modifiers '@' PushModifiers interface Identifier
        // consumeAnnotationTypeDeclarationHeader ::= '@' PushModifiers interface Identifier
        TypeDeclaration annotationTypeDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                annotationTypeDeclaration.bits |= ASTNode.IsMemberType;
            }
        } else {
            // Record that the block has a declaration for local types
            annotationTypeDeclaration.bits |= ASTNode.IsLocalType;
            markEnclosingMemberWithLocalType();
            blockReal();
        }
        //highlight the name of the type
        long pos = this.identifierPositionStack[this.identifierPtr];
        annotationTypeDeclaration.sourceEnd = (int) pos;
        annotationTypeDeclaration.sourceStart = (int) (pos >>> 32);
        annotationTypeDeclaration.name = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        //compute the declaration source too
        // 'interface' push two int positions: the beginning of the class token and its end.
        // we want to keep the beginning position but get rid of the end position
        // it is only used for the ClassLiteralAccess positions.
        // remove the start position of the interface token
        this.intPtr--;
        // remove the end position of the interface token
        this.intPtr--;
        annotationTypeDeclaration.modifiersSourceStart = this.intStack[this.intPtr--];
        annotationTypeDeclaration.modifiers = this.intStack[this.intPtr--] | ClassFileConstants.AccAnnotation | ClassFileConstants.AccInterface;
        if (annotationTypeDeclaration.modifiersSourceStart >= 0) {
            annotationTypeDeclaration.declarationSourceStart = annotationTypeDeclaration.modifiersSourceStart;
            // remove the position of the '@' token as we have modifiers
            this.intPtr--;
        } else {
            int atPosition = this.intStack[this.intPtr--];
            // remove the position of the '@' token as we don't have modifiers
            annotationTypeDeclaration.declarationSourceStart = atPosition;
        }
        // Store secondary info
        if ((annotationTypeDeclaration.bits & ASTNode.IsMemberType) == 0 && (annotationTypeDeclaration.bits & ASTNode.IsLocalType) == 0) {
            if (this.compilationUnit != null && !CharOperation.equals(annotationTypeDeclaration.name, this.compilationUnit.getMainTypeName())) {
                annotationTypeDeclaration.bits |= ASTNode.IsSecondaryType;
            }
        }
        // consume annotations
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, annotationTypeDeclaration.annotations = new Annotation[length], 0, length);
        }
        annotationTypeDeclaration.bodyStart = annotationTypeDeclaration.sourceEnd + 1;
        // javadoc
        annotationTypeDeclaration.javadoc = this.javadoc;
        this.javadoc = null;
        pushOnAstStack(annotationTypeDeclaration);
        if (!this.statementRecoveryActivated && this.options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            problemReporter().invalidUsageOfAnnotationDeclarations(annotationTypeDeclaration);
        }
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = annotationTypeDeclaration.bodyStart;
            this.currentElement = this.currentElement.add(annotationTypeDeclaration, 0);
            this.lastIgnoredToken = -1;
        }
    }

    protected void consumeAnnotationTypeDeclarationHeaderNameWithTypeParameters() {
        // consumeAnnotationTypeDeclarationHeader ::= Modifiers '@' PushModifiers interface Identifier TypeParameters
        // consumeAnnotationTypeDeclarationHeader ::= '@' PushModifiers interface Identifier TypeParameters
        TypeDeclaration annotationTypeDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
        // consume type parameters
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, annotationTypeDeclaration.typeParameters = new TypeParameter[length], 0, length);
        problemReporter().invalidUsageOfTypeParametersForAnnotationDeclaration(annotationTypeDeclaration);
        annotationTypeDeclaration.bodyStart = annotationTypeDeclaration.typeParameters[length - 1].declarationSourceEnd + 1;
        //	annotationTypeDeclaration.typeParameters = null;
        this.listTypeParameterLength = 0;
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                annotationTypeDeclaration.bits |= ASTNode.IsMemberType;
            }
        } else {
            // Record that the block has a declaration for local types
            annotationTypeDeclaration.bits |= ASTNode.IsLocalType;
            markEnclosingMemberWithLocalType();
            blockReal();
        }
        //highlight the name of the type
        long pos = this.identifierPositionStack[this.identifierPtr];
        annotationTypeDeclaration.sourceEnd = (int) pos;
        annotationTypeDeclaration.sourceStart = (int) (pos >>> 32);
        annotationTypeDeclaration.name = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        //compute the declaration source too
        // 'interface' push two int positions: the beginning of the class token and its end.
        // we want to keep the beginning position but get rid of the end position
        // it is only used for the ClassLiteralAccess positions.
        // remove the start position of the interface token
        this.intPtr--;
        // remove the end position of the interface token
        this.intPtr--;
        annotationTypeDeclaration.modifiersSourceStart = this.intStack[this.intPtr--];
        annotationTypeDeclaration.modifiers = this.intStack[this.intPtr--] | ClassFileConstants.AccAnnotation | ClassFileConstants.AccInterface;
        if (annotationTypeDeclaration.modifiersSourceStart >= 0) {
            annotationTypeDeclaration.declarationSourceStart = annotationTypeDeclaration.modifiersSourceStart;
            // remove the position of the '@' token as we have modifiers
            this.intPtr--;
        } else {
            int atPosition = this.intStack[this.intPtr--];
            // remove the position of the '@' token as we don't have modifiers
            annotationTypeDeclaration.declarationSourceStart = atPosition;
        }
        // Store secondary info
        if ((annotationTypeDeclaration.bits & ASTNode.IsMemberType) == 0 && (annotationTypeDeclaration.bits & ASTNode.IsLocalType) == 0) {
            if (this.compilationUnit != null && !CharOperation.equals(annotationTypeDeclaration.name, this.compilationUnit.getMainTypeName())) {
                annotationTypeDeclaration.bits |= ASTNode.IsSecondaryType;
            }
        }
        // consume annotations
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, annotationTypeDeclaration.annotations = new Annotation[length], 0, length);
        }
        // javadoc
        annotationTypeDeclaration.javadoc = this.javadoc;
        this.javadoc = null;
        pushOnAstStack(annotationTypeDeclaration);
        if (!this.statementRecoveryActivated && this.options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            problemReporter().invalidUsageOfAnnotationDeclarations(annotationTypeDeclaration);
        }
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = annotationTypeDeclaration.bodyStart;
            this.currentElement = this.currentElement.add(annotationTypeDeclaration, 0);
            this.lastIgnoredToken = -1;
        }
    }

    protected void consumeAnnotationTypeMemberDeclaration() {
        // AnnotationTypeMemberDeclaration ::= AnnotationTypeMemberDeclarationHeader AnnotationTypeMemberHeaderExtendedDims DefaultValueopt ';'
        AnnotationMethodDeclaration annotationTypeMemberDeclaration = (AnnotationMethodDeclaration) this.astStack[this.astPtr];
        annotationTypeMemberDeclaration.modifiers |= ExtraCompilerModifiers.AccSemicolonBody;
        // store the this.endPosition (position just before the '}') in case there is
        // a trailing comment behind the end of the method
        int declarationEndPosition = flushCommentsDefinedPriorTo(this.endStatementPosition);
        annotationTypeMemberDeclaration.bodyStart = this.endStatementPosition;
        annotationTypeMemberDeclaration.bodyEnd = declarationEndPosition;
        annotationTypeMemberDeclaration.declarationSourceEnd = declarationEndPosition;
    }

    protected void consumeAnnotationTypeMemberDeclarations() {
        // AnnotationTypeMemberDeclarations ::= AnnotationTypeMemberDeclarations AnnotationTypeMemberDeclaration
        concatNodeLists();
    }

    protected void consumeAnnotationTypeMemberDeclarationsopt() {
        this.nestedType--;
    }

    protected void consumeArgumentList() {
        // ArgumentList ::= ArgumentList ',' Expression
        concatExpressionLists();
    }

    protected void consumeArguments() {
        // Arguments ::= '(' ArgumentListopt ')'
        // nothing to do, the expression stack is already updated
        pushOnIntStack(this.rParenPos);
    }

    protected void consumeArrayAccess(boolean unspecifiedReference) {
        // ArrayAccess ::= Name '[' Expression ']' ==> true
        // ArrayAccess ::= PrimaryNoNewArray '[' Expression ']' ==> false
        //optimize push/pop
        Expression exp;
        if (unspecifiedReference) {
            exp = this.expressionStack[this.expressionPtr] = new ArrayReference(getUnspecifiedReferenceOptimized(), this.expressionStack[this.expressionPtr]);
        } else {
            this.expressionPtr--;
            this.expressionLengthPtr--;
            exp = this.expressionStack[this.expressionPtr] = new ArrayReference(this.expressionStack[this.expressionPtr], this.expressionStack[this.expressionPtr + 1]);
        }
        exp.sourceEnd = this.endStatementPosition;
    }

    protected void consumeArrayCreationExpressionWithInitializer() {
        // ArrayCreationWithArrayInitializer ::= 'new' PrimitiveType DimWithOrWithOutExprs ArrayInitializer
        // ArrayCreationWithArrayInitializer ::= 'new' ClassOrInterfaceType DimWithOrWithOutExprs ArrayInitializer
        int length;
        ArrayAllocationExpression arrayAllocation = new ArrayAllocationExpression();
        this.expressionLengthPtr--;
        arrayAllocation.initializer = (ArrayInitializer) this.expressionStack[this.expressionPtr--];
        length = (this.expressionLengthStack[this.expressionLengthPtr--]);
        this.expressionPtr -= length;
        System.arraycopy(this.expressionStack, this.expressionPtr + 1, arrayAllocation.dimensions = new Expression[length], 0, length);
        Annotation[][] annotationsOnDimensions = getAnnotationsOnDimensions(length);
        arrayAllocation.annotationsOnDimensions = annotationsOnDimensions;
        arrayAllocation.type = getTypeReference(0);
        // no need to worry about raw type usage
        arrayAllocation.type.bits |= ASTNode.IgnoreRawTypeCheck;
        if (annotationsOnDimensions != null) {
            arrayAllocation.bits |= ASTNode.HasTypeAnnotations;
            arrayAllocation.type.bits |= ASTNode.HasTypeAnnotations;
        }
        arrayAllocation.sourceStart = this.intStack[this.intPtr--];
        if (arrayAllocation.initializer == null) {
            arrayAllocation.sourceEnd = this.endStatementPosition;
        } else {
            arrayAllocation.sourceEnd = arrayAllocation.initializer.sourceEnd;
        }
        pushOnExpressionStack(arrayAllocation);
    }

    protected void consumeArrayCreationExpressionWithoutInitializer() {
        // ArrayCreationWithoutArrayInitializer ::= 'new' ClassOrInterfaceType DimWithOrWithOutExprs
        // ArrayCreationWithoutArrayInitializer ::= 'new' PrimitiveType DimWithOrWithOutExprs
        int length;
        ArrayAllocationExpression arrayAllocation = new ArrayAllocationExpression();
        length = (this.expressionLengthStack[this.expressionLengthPtr--]);
        this.expressionPtr -= length;
        System.arraycopy(this.expressionStack, this.expressionPtr + 1, arrayAllocation.dimensions = new Expression[length], 0, length);
        Annotation[][] annotationsOnDimensions = getAnnotationsOnDimensions(length);
        arrayAllocation.annotationsOnDimensions = annotationsOnDimensions;
        arrayAllocation.type = getTypeReference(0);
        // no need to worry about raw type usage
        arrayAllocation.type.bits |= ASTNode.IgnoreRawTypeCheck;
        if (annotationsOnDimensions != null) {
            arrayAllocation.bits |= ASTNode.HasTypeAnnotations;
            arrayAllocation.type.bits |= ASTNode.HasTypeAnnotations;
        }
        arrayAllocation.sourceStart = this.intStack[this.intPtr--];
        if (arrayAllocation.initializer == null) {
            arrayAllocation.sourceEnd = this.endStatementPosition;
        } else {
            arrayAllocation.sourceEnd = arrayAllocation.initializer.sourceEnd;
        }
        pushOnExpressionStack(arrayAllocation);
    }

    protected void consumeArrayCreationHeader() {
    // nothing to do
    }

    protected void consumeArrayInitializer() {
        // ArrayInitializer ::= '{' VariableInitializers '}'
        // ArrayInitializer ::= '{' VariableInitializers , '}'
        arrayInitializer(this.expressionLengthStack[this.expressionLengthPtr--]);
    }

    protected void consumeArrayTypeWithTypeArgumentsName() {
        this.genericsIdentifiersLengthStack[this.genericsIdentifiersLengthPtr] += this.identifierLengthStack[this.identifierLengthPtr];
        // handle type arguments
        pushOnGenericsLengthStack(0);
    }

    protected void consumeAssertStatement() {
        // AssertStatement ::= 'assert' Expression ':' Expression ';'
        this.expressionLengthPtr -= 2;
        pushOnAstStack(new AssertStatement(this.expressionStack[this.expressionPtr--], this.expressionStack[this.expressionPtr--], this.intStack[this.intPtr--]));
    }

    protected void consumeAssignment() {
        // Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
        //optimize the push/pop
        //<--the encoded operator
        int op = this.intStack[this.intPtr--];
        this.expressionPtr--;
        this.expressionLengthPtr--;
        Expression expression = this.expressionStack[this.expressionPtr + 1];
        this.expressionStack[this.expressionPtr] = (op != EQUAL) ? new CompoundAssignment(this.expressionStack[this.expressionPtr], expression, op, expression.sourceEnd) : new Assignment(this.expressionStack[this.expressionPtr], expression, expression.sourceEnd);
        if (this.pendingRecoveredType != null) {
            // The assignment must be replace by the anonymous type.
            if (this.pendingRecoveredType.allocation != null && this.scanner.startPosition - 1 <= this.pendingRecoveredType.declarationSourceEnd) {
                this.expressionStack[this.expressionPtr] = this.pendingRecoveredType.allocation;
                this.pendingRecoveredType = null;
                return;
            }
            this.pendingRecoveredType = null;
        }
    }

    protected void consumeAssignmentOperator(int pos) {
        // AssignmentOperator ::= '='
        // AssignmentOperator ::= '*='
        // AssignmentOperator ::= '/='
        // AssignmentOperator ::= '%='
        // AssignmentOperator ::= '+='
        // AssignmentOperator ::= '-='
        // AssignmentOperator ::= '<<='
        // AssignmentOperator ::= '>>='
        // AssignmentOperator ::= '>>>='
        // AssignmentOperator ::= '&='
        // AssignmentOperator ::= '^='
        // AssignmentOperator ::= '|='
        pushOnIntStack(pos);
    }

    protected void consumeBinaryExpression(int op) {
        // MultiplicativeExpression ::= MultiplicativeExpression '*' UnaryExpression
        // MultiplicativeExpression ::= MultiplicativeExpression '/' UnaryExpression
        // MultiplicativeExpression ::= MultiplicativeExpression '%' UnaryExpression
        // AdditiveExpression ::= AdditiveExpression '+' MultiplicativeExpression
        // AdditiveExpression ::= AdditiveExpression '-' MultiplicativeExpression
        // ShiftExpression ::= ShiftExpression '<<'  AdditiveExpression
        // ShiftExpression ::= ShiftExpression '>>'  AdditiveExpression
        // ShiftExpression ::= ShiftExpression '>>>' AdditiveExpression
        // RelationalExpression ::= RelationalExpression '<'  ShiftExpression
        // RelationalExpression ::= RelationalExpression '>'  ShiftExpression
        // RelationalExpression ::= RelationalExpression '<=' ShiftExpression
        // RelationalExpression ::= RelationalExpression '>=' ShiftExpression
        // AndExpression ::= AndExpression '&' EqualityExpression
        // ExclusiveOrExpression ::= ExclusiveOrExpression '^' AndExpression
        // InclusiveOrExpression ::= InclusiveOrExpression '|' ExclusiveOrExpression
        // ConditionalAndExpression ::= ConditionalAndExpression '&&' InclusiveOrExpression
        // ConditionalOrExpression ::= ConditionalOrExpression '||' ConditionalAndExpression
        //optimize the push/pop
        this.expressionPtr--;
        this.expressionLengthPtr--;
        Expression expr1 = this.expressionStack[this.expressionPtr];
        Expression expr2 = this.expressionStack[this.expressionPtr + 1];
        switch(op) {
            case OR_OR:
                this.expressionStack[this.expressionPtr] = new OR_OR_Expression(expr1, expr2, op);
                break;
            case AND_AND:
                this.expressionStack[this.expressionPtr] = new AND_AND_Expression(expr1, expr2, op);
                break;
            case PLUS:
                // look for "string1" + "string2"
                if (this.optimizeStringLiterals) {
                    if (expr1 instanceof StringLiteral) {
                        if (((expr1.bits & ASTNode.ParenthesizedMASK) >> ASTNode.ParenthesizedSHIFT) == 0) {
                            if (// string+char
                            expr2 instanceof // string+char
                            CharLiteral) {
                                this.expressionStack[this.expressionPtr] = ((StringLiteral) expr1).extendWith((CharLiteral) expr2);
                            } else if (//string+string
                            expr2 instanceof //string+string
                            StringLiteral) {
                                this.expressionStack[this.expressionPtr] = ((StringLiteral) expr1).extendWith((StringLiteral) expr2);
                            } else {
                                this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, PLUS);
                            }
                        } else {
                            this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, PLUS);
                        }
                    } else if (expr1 instanceof CombinedBinaryExpression) {
                        CombinedBinaryExpression cursor;
                        // full-fledged references table
                        if ((cursor = (CombinedBinaryExpression) expr1).arity < cursor.arityMax) {
                            cursor.left = new BinaryExpression(cursor);
                            cursor.arity++;
                        } else {
                            cursor.left = new CombinedBinaryExpression(cursor);
                            cursor.arity = 0;
                            cursor.tuneArityMax();
                        }
                        cursor.right = expr2;
                        cursor.sourceEnd = expr2.sourceEnd;
                        this.expressionStack[this.expressionPtr] = cursor;
                    // BE_INSTRUMENTATION: neutralized in the released code
                    //					cursor.depthTracker = ((BinaryExpression)cursor.left).
                    //						depthTracker + 1;
                    } else if (expr1 instanceof BinaryExpression && // expressions)
                    ((expr1.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT) == OperatorIds.PLUS) {
                        this.expressionStack[this.expressionPtr] = new CombinedBinaryExpression(expr1, expr2, PLUS, 1);
                    } else {
                        this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, PLUS);
                    }
                } else if (expr1 instanceof StringLiteral) {
                    if (expr2 instanceof StringLiteral && ((expr1.bits & ASTNode.ParenthesizedMASK) >> ASTNode.ParenthesizedSHIFT) == 0) {
                        // string + string
                        this.expressionStack[this.expressionPtr] = ((StringLiteral) expr1).extendsWith((StringLiteral) expr2);
                    } else {
                        // single out the a + b case
                        this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, PLUS);
                    }
                } else if (expr1 instanceof CombinedBinaryExpression) {
                    CombinedBinaryExpression cursor;
                    // shift cursor; create BE/CBE as needed
                    if ((cursor = (CombinedBinaryExpression) expr1).arity < cursor.arityMax) {
                        cursor.left = new BinaryExpression(cursor);
                        // clear the bits on cursor
                        cursor.bits &= ~ASTNode.ParenthesizedMASK;
                        cursor.arity++;
                    } else {
                        cursor.left = new CombinedBinaryExpression(cursor);
                        // clear the bits on cursor
                        cursor.bits &= ~ASTNode.ParenthesizedMASK;
                        cursor.arity = 0;
                        cursor.tuneArityMax();
                    }
                    cursor.right = expr2;
                    cursor.sourceEnd = expr2.sourceEnd;
                    // BE_INSTRUMENTATION: neutralized in the released code
                    //					cursor.depthTracker = ((BinaryExpression)cursor.left).
                    //						depthTracker + 1;
                    this.expressionStack[this.expressionPtr] = cursor;
                } else if (expr1 instanceof BinaryExpression && ((expr1.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT) == OperatorIds.PLUS) {
                    // single out the a + b case
                    this.expressionStack[this.expressionPtr] = new CombinedBinaryExpression(expr1, expr2, PLUS, 1);
                } else {
                    this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, PLUS);
                }
                break;
            case LESS:
            case MULTIPLY:
                // star end position or starting position of angle bracket
                this.intPtr--;
                this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
                break;
            default:
                this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
        }
    }

    /**
 * @param op binary operator
 */
    protected void consumeBinaryExpressionWithName(int op) {
        pushOnExpressionStack(getUnspecifiedReferenceOptimized());
        this.expressionPtr--;
        this.expressionLengthPtr--;
        /*
	if (op == OR_OR) {
		this.expressionStack[this.expressionPtr] =
			new OR_OR_Expression(
				this.expressionStack[this.expressionPtr + 1],
				this.expressionStack[this.expressionPtr],
				op);
	} else {
		if (op == AND_AND) {
			this.expressionStack[this.expressionPtr] =
				new AND_AND_Expression(
					this.expressionStack[this.expressionPtr + 1],
					this.expressionStack[this.expressionPtr],
					op);
		} else {
			// look for "string1" + "string2"
			if ((op == PLUS) && this.optimizeStringLiterals) {
				Expression expr1, expr2;
				expr1 = this.expressionStack[this.expressionPtr + 1];
				expr2 = this.expressionStack[this.expressionPtr];
				if (expr1 instanceof StringLiteral) {
					if (expr2 instanceof CharLiteral) { // string+char
						this.expressionStack[this.expressionPtr] =
							((StringLiteral) expr1).extendWith((CharLiteral) expr2);
					} else if (expr2 instanceof StringLiteral) { //string+string
						this.expressionStack[this.expressionPtr] =
							((StringLiteral) expr1).extendWith((StringLiteral) expr2);
					} else {
						this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, PLUS);
					}
				} else {
					this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, PLUS);
				}
			} else {
				this.expressionStack[this.expressionPtr] =
					new BinaryExpression(
						this.expressionStack[this.expressionPtr + 1],
						this.expressionStack[this.expressionPtr],
						op);
			}
		}
	}
	*/
        Expression expr1 = this.expressionStack[this.expressionPtr + 1];
        Expression expr2 = this.expressionStack[this.expressionPtr];
        //       IndexedBinaryExpression-s here since expr1 always holds a name
        switch(op) {
            case OR_OR:
                this.expressionStack[this.expressionPtr] = new OR_OR_Expression(expr1, expr2, op);
                break;
            case AND_AND:
                this.expressionStack[this.expressionPtr] = new AND_AND_Expression(expr1, expr2, op);
                break;
            case PLUS:
                // look for "string1" + "string2"
                if (this.optimizeStringLiterals) {
                    if (expr1 instanceof StringLiteral && ((expr1.bits & ASTNode.ParenthesizedMASK) >> ASTNode.ParenthesizedSHIFT) == 0) {
                        if (// string+char
                        expr2 instanceof // string+char
                        CharLiteral) {
                            this.expressionStack[this.expressionPtr] = ((StringLiteral) expr1).extendWith((CharLiteral) expr2);
                        } else if (//string+string
                        expr2 instanceof //string+string
                        StringLiteral) {
                            this.expressionStack[this.expressionPtr] = ((StringLiteral) expr1).extendWith((StringLiteral) expr2);
                        } else {
                            this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, PLUS);
                        }
                    } else {
                        this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, PLUS);
                    }
                } else if (expr1 instanceof StringLiteral) {
                    if (expr2 instanceof StringLiteral && ((expr1.bits & ASTNode.ParenthesizedMASK) >> ASTNode.ParenthesizedSHIFT) == 0) {
                        // string + string
                        this.expressionStack[this.expressionPtr] = ((StringLiteral) expr1).extendsWith((StringLiteral) expr2);
                    } else {
                        this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
                    }
                } else {
                    this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
                }
                break;
            case LESS:
            case MULTIPLY:
                // star end position or starting position of angle bracket
                this.intPtr--;
                this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
                break;
            default:
                this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
        }
    }

    protected void consumeBlock() {
        // Block ::= OpenBlock '{' BlockStatementsopt '}'
        // LambdaBody ::= NestedType NestedMethod  '{' BlockStatementsopt '}'
        // simpler action for empty blocks
        int statementsLength = this.astLengthStack[this.astLengthPtr--];
        Block block;
        if (// empty block
        statementsLength == 0) {
            block = new Block(0);
            block.sourceStart = this.intStack[this.intPtr--];
            block.sourceEnd = this.endStatementPosition;
            // check whether this block at least contains some comment in it
            if (!containsComment(block.sourceStart, block.sourceEnd)) {
                block.bits |= ASTNode.UndocumentedEmptyBlock;
            }
            // still need to pop the block variable counter
            this.realBlockPtr--;
        } else {
            block = new Block(this.realBlockStack[this.realBlockPtr--]);
            this.astPtr -= statementsLength;
            System.arraycopy(this.astStack, this.astPtr + 1, block.statements = new Statement[statementsLength], 0, statementsLength);
            block.sourceStart = this.intStack[this.intPtr--];
            block.sourceEnd = this.endStatementPosition;
        }
        pushOnAstStack(block);
    }

    protected void consumeBlockStatement() {
    // for assist parsers.
    }

    protected void consumeBlockStatements() {
        // BlockStatements ::= BlockStatements BlockStatement
        concatNodeLists();
    }

    protected void consumeCaseLabel() {
        // SwitchLabel ::= 'case' ConstantExpression ':'
        this.expressionLengthPtr--;
        Expression expression = this.expressionStack[this.expressionPtr--];
        CaseStatement caseStatement = new CaseStatement(expression, expression.sourceEnd, this.intStack[this.intPtr--]);
        // Look for $fall-through$ tag in leading comment for case statement
        if (hasLeadingTagComment(FALL_THROUGH_TAG, caseStatement.sourceStart)) {
            caseStatement.bits |= ASTNode.DocumentedFallthrough;
        }
        pushOnAstStack(caseStatement);
    }

    protected void consumeCastExpressionLL1() {
        //CastExpression ::= '(' Name ')' InsideCastExpressionLL1 UnaryExpressionNotPlusMinus
        //optimize push/pop
        Expression cast;
        Expression exp;
        this.expressionPtr--;
        this.expressionStack[this.expressionPtr] = cast = new CastExpression(exp = this.expressionStack[this.expressionPtr + 1], (TypeReference) this.expressionStack[this.expressionPtr]);
        this.expressionLengthPtr--;
        updateSourcePosition(cast);
        cast.sourceEnd = exp.sourceEnd;
    }

    public IntersectionCastTypeReference createIntersectionCastTypeReference(TypeReference[] typeReferences) {
        if (this.options.sourceLevel < ClassFileConstants.JDK1_8) {
            problemReporter().intersectionCastNotBelow18(typeReferences);
        }
        return new IntersectionCastTypeReference(typeReferences);
    }

    protected void consumeCastExpressionLL1WithBounds() {
        //CastExpression ::= '(' Name AdditionalBoundsList ')' UnaryExpressionNotPlusMinus
        Expression cast;
        Expression exp;
        int length;
        exp = this.expressionStack[this.expressionPtr--];
        this.expressionLengthPtr--;
        TypeReference[] bounds = new TypeReference[length = this.expressionLengthStack[this.expressionLengthPtr]];
        System.arraycopy(this.expressionStack, this.expressionPtr -= (length - 1), bounds, 0, length);
        this.expressionStack[this.expressionPtr] = cast = new CastExpression(exp, createIntersectionCastTypeReference(bounds));
        this.expressionLengthStack[this.expressionLengthPtr] = 1;
        updateSourcePosition(cast);
        cast.sourceEnd = exp.sourceEnd;
    }

    protected void consumeCastExpressionWithGenericsArray() {
        // CastExpression ::= PushLPAREN Name TypeArguments Dimsopt AdditionalBoundsListOpt PushRPAREN InsideCastExpression UnaryExpressionNotPlusMinus
        TypeReference[] bounds = null;
        int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        if (additionalBoundsLength > 0) {
            bounds = new TypeReference[additionalBoundsLength + 1];
            this.genericsPtr -= additionalBoundsLength;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 1, additionalBoundsLength);
        }
        Expression exp;
        Expression cast;
        TypeReference castType;
        int end = this.intStack[this.intPtr--];
        int dim = this.intStack[this.intPtr--];
        pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        if (additionalBoundsLength > 0) {
            bounds[0] = getTypeReference(dim);
            castType = createIntersectionCastTypeReference(bounds);
        } else {
            castType = getTypeReference(dim);
        }
        this.expressionStack[this.expressionPtr] = cast = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType);
        // pop position of '<'
        this.intPtr--;
        castType.sourceEnd = end - 1;
        castType.sourceStart = (cast.sourceStart = this.intStack[this.intPtr--]) + 1;
        cast.sourceEnd = exp.sourceEnd;
    }

    protected void consumeCastExpressionWithNameArray() {
        // CastExpression ::= PushLPAREN Name Dims AdditionalBoundsListOpt PushRPAREN InsideCastExpression UnaryExpressionNotPlusMinus
        Expression exp;
        Expression cast;
        TypeReference castType;
        int end = this.intStack[this.intPtr--];
        TypeReference[] bounds = null;
        int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        if (additionalBoundsLength > 0) {
            bounds = new TypeReference[additionalBoundsLength + 1];
            this.genericsPtr -= additionalBoundsLength;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 1, additionalBoundsLength);
        }
        // handle type arguments
        pushOnGenericsLengthStack(0);
        pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        if (additionalBoundsLength > 0) {
            bounds[0] = getTypeReference(this.intStack[this.intPtr--]);
            castType = createIntersectionCastTypeReference(bounds);
        } else {
            castType = getTypeReference(this.intStack[this.intPtr--]);
        }
        this.expressionStack[this.expressionPtr] = cast = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType);
        castType.sourceEnd = end - 1;
        castType.sourceStart = (cast.sourceStart = this.intStack[this.intPtr--]) + 1;
        cast.sourceEnd = exp.sourceEnd;
    }

    protected void consumeCastExpressionWithPrimitiveType() {
        // CastExpression ::= PushLPAREN PrimitiveType Dimsopt AdditionalBoundsListOpt PushRPAREN InsideCastExpression UnaryExpression
        //this.intStack : posOfLeftParen dim posOfRightParen
        TypeReference[] bounds = null;
        int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        if (additionalBoundsLength > 0) {
            bounds = new TypeReference[additionalBoundsLength + 1];
            this.genericsPtr -= additionalBoundsLength;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 1, additionalBoundsLength);
        }
        //optimize the push/pop
        Expression exp;
        Expression cast;
        TypeReference castType;
        int end = this.intStack[this.intPtr--];
        if (additionalBoundsLength > 0) {
            bounds[0] = getTypeReference(this.intStack[this.intPtr--]);
            castType = createIntersectionCastTypeReference(bounds);
        } else {
            castType = getTypeReference(this.intStack[this.intPtr--]);
        }
        this.expressionStack[this.expressionPtr] = cast = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType);
        castType.sourceEnd = end - 1;
        castType.sourceStart = (cast.sourceStart = this.intStack[this.intPtr--]) + 1;
        cast.sourceEnd = exp.sourceEnd;
    }

    protected void consumeCastExpressionWithQualifiedGenericsArray() {
        // CastExpression ::= PushLPAREN Name OnlyTypeArguments '.' ClassOrInterfaceType Dimsopt AdditionalBoundsOpt PushRPAREN InsideCastExpression UnaryExpressionNotPlusMinus
        TypeReference[] bounds = null;
        int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        if (additionalBoundsLength > 0) {
            bounds = new TypeReference[additionalBoundsLength + 1];
            this.genericsPtr -= additionalBoundsLength;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 1, additionalBoundsLength);
        }
        Expression exp;
        Expression cast;
        TypeReference castType;
        int end = this.intStack[this.intPtr--];
        int dim = this.intStack[this.intPtr--];
        Annotation[][] annotationsOnDimensions = dim == 0 ? null : getAnnotationsOnDimensions(dim);
        TypeReference rightSide = getTypeReference(0);
        castType = computeQualifiedGenericsFromRightSide(rightSide, dim, annotationsOnDimensions);
        if (additionalBoundsLength > 0) {
            bounds[0] = castType;
            castType = createIntersectionCastTypeReference(bounds);
        }
        this.intPtr--;
        this.expressionStack[this.expressionPtr] = cast = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType);
        castType.sourceEnd = end - 1;
        castType.sourceStart = (cast.sourceStart = this.intStack[this.intPtr--]) + 1;
        cast.sourceEnd = exp.sourceEnd;
    }

    protected void consumeCatches() {
        // Catches ::= Catches CatchClause
        optimizedConcatNodeLists();
    }

    protected void consumeCatchFormalParameter() {
        // CatchFormalParameter ::= Modifiersopt CatchType VariableDeclaratorId
        this.identifierLengthPtr--;
        char[] identifierName = this.identifierStack[this.identifierPtr];
        long namePositions = this.identifierPositionStack[this.identifierPtr--];
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=348369
        int extendedDimensions = this.intStack[this.intPtr--];
        TypeReference type = (TypeReference) this.astStack[this.astPtr--];
        if (extendedDimensions > 0) {
            type = augmentTypeWithAdditionalDimensions(type, extendedDimensions, null, false);
            type.sourceEnd = this.endPosition;
            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=391092
            if (type instanceof UnionTypeReference) {
                this.problemReporter().illegalArrayOfUnionType(identifierName, type);
            }
        }
        this.astLengthPtr--;
        int modifierPositions = this.intStack[this.intPtr--];
        this.intPtr--;
        Argument arg = new Argument(identifierName, namePositions, type, // modifiers
        this.intStack[this.intPtr + 1] & ~ClassFileConstants.AccDeprecated);
        arg.bits &= ~ASTNode.IsArgument;
        arg.declarationSourceStart = modifierPositions;
        // consume annotations
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, arg.annotations = new Annotation[length], 0, length);
        }
        pushOnAstStack(arg);
        /* if incomplete method header, this.listLength counter will not have been reset,
		indicating that some arguments are available on the stack */
        this.listLength++;
    }

    protected void consumeCatchHeader() {
        if (this.currentElement == null) {
            // should never occur, this consumeRule is only used in recovery mode
            return;
        }
        // current element should be a block due to the presence of the opening brace
        if (!(this.currentElement instanceof RecoveredBlock)) {
            if (!(this.currentElement instanceof RecoveredMethod)) {
                return;
            }
            RecoveredMethod rMethod = (RecoveredMethod) this.currentElement;
            if (!(rMethod.methodBody == null && rMethod.bracketBalance > 0)) {
                return;
            }
        }
        Argument arg = (Argument) this.astStack[this.astPtr--];
        // convert argument to local variable
        LocalDeclaration localDeclaration = new LocalDeclaration(arg.name, arg.sourceStart, arg.sourceEnd);
        localDeclaration.type = arg.type;
        localDeclaration.declarationSourceStart = arg.declarationSourceStart;
        localDeclaration.declarationSourceEnd = arg.declarationSourceEnd;
        this.currentElement = this.currentElement.add(localDeclaration, 0);
        // force to restart at this exact position
        this.lastCheckPoint = this.scanner.startPosition;
        // request to restart from here on
        this.restartRecovery = true;
        this.lastIgnoredToken = -1;
    }

    protected void consumeCatchType() {
        // CatchType ::= UnionType
        int length = this.astLengthStack[this.astLengthPtr--];
        if (length != 1) {
            TypeReference[] typeReferences;
            System.arraycopy(this.astStack, (this.astPtr -= length) + 1, (typeReferences = new TypeReference[length]), 0, length);
            UnionTypeReference typeReference = new UnionTypeReference(typeReferences);
            pushOnAstStack(typeReference);
            if (this.options.sourceLevel < ClassFileConstants.JDK1_7) {
                problemReporter().multiCatchNotBelow17(typeReference);
            }
        } else {
            // push back the type reference
            pushOnAstLengthStack(1);
        }
    }

    protected void consumeClassBodyDeclaration() {
        // ClassBodyDeclaration ::= Diet NestedMethod CreateInitializer Block
        //push an Initializer
        //optimize the push/pop
        this.nestedMethod[this.nestedType]--;
        Block block = (Block) this.astStack[this.astPtr--];
        this.astLengthPtr--;
        // clear bit since was diet
        if (this.diet)
            block.bits &= ~ASTNode.UndocumentedEmptyBlock;
        Initializer initializer = (Initializer) this.astStack[this.astPtr];
        initializer.declarationSourceStart = initializer.sourceStart = block.sourceStart;
        initializer.block = block;
        // pop sourcestart left on the stack by consumeNestedMethod.
        this.intPtr--;
        initializer.bodyStart = this.intStack[this.intPtr--];
        // pop the block variable counter left on the stack by consumeNestedMethod
        this.realBlockPtr--;
        int javadocCommentStart = this.intStack[this.intPtr--];
        if (javadocCommentStart != -1) {
            initializer.declarationSourceStart = javadocCommentStart;
            initializer.javadoc = this.javadoc;
            this.javadoc = null;
        }
        initializer.bodyEnd = this.endPosition;
        initializer.sourceEnd = this.endStatementPosition;
        initializer.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeClassBodyDeclarations() {
        // ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
        concatNodeLists();
    }

    protected void consumeClassBodyDeclarationsopt() {
        // ClassBodyDeclarationsopt ::= NestedType ClassBodyDeclarations
        this.nestedType--;
    }

    protected void consumeClassBodyopt() {
        // ClassBodyopt ::= $empty
        pushOnAstStack(null);
        this.endPosition = this.rParenPos;
    }

    protected void consumeClassDeclaration() {
        // ClassDeclaration ::= ClassHeader ClassBody
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            //there are length declarations
            //dispatch according to the type of the declarations
            dispatchDeclarationInto(length);
        }
        TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
        //convert constructor that do not have the type's name into methods
        boolean hasConstructor = typeDecl.checkConstructors(this);
        //add the default constructor when needed (interface don't have it)
        if (!hasConstructor) {
            switch(TypeDeclaration.kind(typeDecl.modifiers)) {
                case TypeDeclaration.CLASS_DECL:
                case TypeDeclaration.ENUM_DECL:
                    boolean insideFieldInitializer = false;
                    if (this.diet) {
                        for (int i = this.nestedType; i > 0; i--) {
                            if (this.variablesCounter[i] > 0) {
                                insideFieldInitializer = true;
                                break;
                            }
                        }
                    }
                    typeDecl.createDefaultConstructor(!(this.diet && this.dietInt == 0) || insideFieldInitializer, true);
            }
        }
        //always add <clinit> (will be remove at code gen time if empty)
        if (this.scanner.containsAssertKeyword) {
            typeDecl.bits |= ASTNode.ContainsAssertion;
        }
        typeDecl.addClinit();
        typeDecl.bodyEnd = this.endStatementPosition;
        if (length == 0 && !containsComment(typeDecl.bodyStart, typeDecl.bodyEnd)) {
            typeDecl.bits |= ASTNode.UndocumentedEmptyBlock;
        }
        typeDecl.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeClassHeader() {
        // ClassHeader ::= ClassHeaderName ClassHeaderExtendsopt ClassHeaderImplementsopt
        TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
        if (this.currentToken == TokenNameLBRACE) {
            typeDecl.bodyStart = this.scanner.currentPosition;
        }
        if (this.currentElement != null) {
            // used to avoid branching back into the regular automaton
            this.restartRecovery = true;
        }
        // flush the comments related to the class header
        this.scanner.commentPtr = -1;
    }

    protected void consumeClassHeaderExtends() {
        // ClassHeaderExtends ::= 'extends' ClassType
        //superclass
        TypeReference superClass = getTypeReference(0);
        // There is a class declaration on the top of stack
        TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
        typeDecl.bits |= (superClass.bits & ASTNode.HasTypeAnnotations);
        typeDecl.superclass = superClass;
        superClass.bits |= ASTNode.IsSuperType;
        typeDecl.bodyStart = typeDecl.superclass.sourceEnd + 1;
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
        }
    }

    protected void consumeClassHeaderImplements() {
        // ClassHeaderImplements ::= 'implements' InterfaceTypeList
        int length = this.astLengthStack[this.astLengthPtr--];
        //super interfaces
        this.astPtr -= length;
        // There is a class declaration on the top of stack
        TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
        System.arraycopy(this.astStack, this.astPtr + 1, typeDecl.superInterfaces = new TypeReference[length], 0, length);
        TypeReference[] superinterfaces = typeDecl.superInterfaces;
        for (int i = 0, max = superinterfaces.length; i < max; i++) {
            TypeReference typeReference = superinterfaces[i];
            typeDecl.bits |= (typeReference.bits & ASTNode.HasTypeAnnotations);
            typeReference.bits |= ASTNode.IsSuperType;
        }
        typeDecl.bodyStart = typeDecl.superInterfaces[length - 1].sourceEnd + 1;
        // reset after having read super-interfaces
        this.listLength = 0;
        // recovery
        if (// is recovering
        this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
        }
    }

    protected void consumeClassHeaderName1() {
        // ClassHeaderName1 ::= Modifiersopt 'class' 'Identifier'
        TypeDeclaration typeDecl = new TypeDeclaration(this.compilationUnit.compilationResult);
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                typeDecl.bits |= ASTNode.IsMemberType;
            }
        } else {
            // Record that the block has a declaration for local types
            typeDecl.bits |= ASTNode.IsLocalType;
            markEnclosingMemberWithLocalType();
            blockReal();
        }
        //highlight the name of the type
        long pos = this.identifierPositionStack[this.identifierPtr];
        typeDecl.sourceEnd = (int) pos;
        typeDecl.sourceStart = (int) (pos >>> 32);
        typeDecl.name = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        //compute the declaration source too
        // 'class' and 'interface' push two int positions: the beginning of the class token and its end.
        // we want to keep the beginning position but get rid of the end position
        // it is only used for the ClassLiteralAccess positions.
        typeDecl.declarationSourceStart = this.intStack[this.intPtr--];
        // remove the end position of the class token
        this.intPtr--;
        typeDecl.modifiersSourceStart = this.intStack[this.intPtr--];
        typeDecl.modifiers = this.intStack[this.intPtr--];
        if (typeDecl.modifiersSourceStart >= 0) {
            typeDecl.declarationSourceStart = typeDecl.modifiersSourceStart;
        }
        // Store secondary info
        if ((typeDecl.bits & ASTNode.IsMemberType) == 0 && (typeDecl.bits & ASTNode.IsLocalType) == 0) {
            if (this.compilationUnit != null && !CharOperation.equals(typeDecl.name, this.compilationUnit.getMainTypeName())) {
                typeDecl.bits |= ASTNode.IsSecondaryType;
            }
        }
        // consume annotations
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, typeDecl.annotations = new Annotation[length], 0, length);
        }
        typeDecl.bodyStart = typeDecl.sourceEnd + 1;
        pushOnAstStack(typeDecl);
        // will be updated when reading super-interfaces
        this.listLength = 0;
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
            this.currentElement = this.currentElement.add(typeDecl, 0);
            this.lastIgnoredToken = -1;
        }
        // javadoc
        typeDecl.javadoc = this.javadoc;
        this.javadoc = null;
    }

    protected void consumeClassInstanceCreationExpression() {
        // ClassInstanceCreationExpression ::= 'new' ClassType '(' ArgumentListopt ')' ClassBodyopt
        classInstanceCreation(false);
        consumeInvocationExpression();
    }

    protected void consumeClassInstanceCreationExpressionName() {
        // ClassInstanceCreationExpressionName ::= Name '.'
        pushOnExpressionStack(getUnspecifiedReferenceOptimized());
    }

    protected void consumeClassInstanceCreationExpressionQualified() {
        // ClassInstanceCreationExpression ::= Primary '.' 'new' SimpleName '(' ArgumentListopt ')' ClassBodyopt
        // ClassInstanceCreationExpression ::= ClassInstanceCreationExpressionName 'new' SimpleName '(' ArgumentListopt ')' ClassBodyopt
        classInstanceCreation(true);
        QualifiedAllocationExpression qae = (QualifiedAllocationExpression) this.expressionStack[this.expressionPtr];
        if (qae.anonymousType == null) {
            this.expressionLengthPtr--;
            this.expressionPtr--;
            qae.enclosingInstance = this.expressionStack[this.expressionPtr];
            this.expressionStack[this.expressionPtr] = qae;
        }
        qae.sourceStart = qae.enclosingInstance.sourceStart;
        consumeInvocationExpression();
    }

    protected void consumeClassInstanceCreationExpressionQualifiedWithTypeArguments() {
        // ClassInstanceCreationExpression ::= Primary '.' 'new' TypeArguments SimpleName '(' ArgumentListopt ')' ClassBodyopt
        // ClassInstanceCreationExpression ::= ClassInstanceCreationExpressionName 'new' TypeArguments SimpleName '(' ArgumentListopt ')' ClassBodyopt
        QualifiedAllocationExpression alloc;
        int length;
        if (((length = this.astLengthStack[this.astLengthPtr--]) == 1) && (this.astStack[this.astPtr] == null)) {
            //NO ClassBody
            this.astPtr--;
            alloc = new QualifiedAllocationExpression();
            //the position has been stored explicitly
            alloc.sourceEnd = this.endPosition;
            if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                this.expressionPtr -= length;
                System.arraycopy(this.expressionStack, this.expressionPtr + 1, alloc.arguments = new Expression[length], 0, length);
            }
            alloc.type = getTypeReference(0);
            checkForDiamond(alloc.type);
            length = this.genericsLengthStack[this.genericsLengthPtr--];
            this.genericsPtr -= length;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, alloc.typeArguments = new TypeReference[length], 0, length);
            this.intPtr--;
            //the default constructor with the correct number of argument
            //will be created and added by the TC (see createsInternalConstructorWithBinding)
            alloc.sourceStart = this.intStack[this.intPtr--];
            pushOnExpressionStack(alloc);
        } else {
            dispatchDeclarationInto(length);
            TypeDeclaration anonymousTypeDeclaration = (TypeDeclaration) this.astStack[this.astPtr];
            anonymousTypeDeclaration.declarationSourceEnd = this.endStatementPosition;
            anonymousTypeDeclaration.bodyEnd = this.endStatementPosition;
            if (length == 0 && !containsComment(anonymousTypeDeclaration.bodyStart, anonymousTypeDeclaration.bodyEnd)) {
                anonymousTypeDeclaration.bits |= ASTNode.UndocumentedEmptyBlock;
            }
            this.astPtr--;
            this.astLengthPtr--;
            QualifiedAllocationExpression allocationExpression = anonymousTypeDeclaration.allocation;
            if (allocationExpression != null) {
                allocationExpression.sourceEnd = this.endStatementPosition;
                // handle type arguments
                length = this.genericsLengthStack[this.genericsLengthPtr--];
                this.genericsPtr -= length;
                System.arraycopy(this.genericsStack, this.genericsPtr + 1, allocationExpression.typeArguments = new TypeReference[length], 0, length);
                allocationExpression.sourceStart = this.intStack[this.intPtr--];
                checkForDiamond(allocationExpression.type);
            }
        }
        QualifiedAllocationExpression qae = (QualifiedAllocationExpression) this.expressionStack[this.expressionPtr];
        if (qae.anonymousType == null) {
            this.expressionLengthPtr--;
            this.expressionPtr--;
            qae.enclosingInstance = this.expressionStack[this.expressionPtr];
            this.expressionStack[this.expressionPtr] = qae;
        }
        qae.sourceStart = qae.enclosingInstance.sourceStart;
        consumeInvocationExpression();
    }

    protected void consumeClassInstanceCreationExpressionWithTypeArguments() {
        // ClassInstanceCreationExpression ::= 'new' TypeArguments ClassType '(' ArgumentListopt ')' ClassBodyopt
        AllocationExpression alloc;
        int length;
        if (((length = this.astLengthStack[this.astLengthPtr--]) == 1) && (this.astStack[this.astPtr] == null)) {
            //NO ClassBody
            this.astPtr--;
            alloc = new AllocationExpression();
            //the position has been stored explicitly
            alloc.sourceEnd = this.endPosition;
            if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                this.expressionPtr -= length;
                System.arraycopy(this.expressionStack, this.expressionPtr + 1, alloc.arguments = new Expression[length], 0, length);
            }
            alloc.type = getTypeReference(0);
            checkForDiamond(alloc.type);
            length = this.genericsLengthStack[this.genericsLengthPtr--];
            this.genericsPtr -= length;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, alloc.typeArguments = new TypeReference[length], 0, length);
            this.intPtr--;
            //the default constructor with the correct number of argument
            //will be created and added by the TC (see createsInternalConstructorWithBinding)
            alloc.sourceStart = this.intStack[this.intPtr--];
            pushOnExpressionStack(alloc);
        } else {
            dispatchDeclarationInto(length);
            TypeDeclaration anonymousTypeDeclaration = (TypeDeclaration) this.astStack[this.astPtr];
            anonymousTypeDeclaration.declarationSourceEnd = this.endStatementPosition;
            anonymousTypeDeclaration.bodyEnd = this.endStatementPosition;
            if (length == 0 && !containsComment(anonymousTypeDeclaration.bodyStart, anonymousTypeDeclaration.bodyEnd)) {
                anonymousTypeDeclaration.bits |= ASTNode.UndocumentedEmptyBlock;
            }
            this.astPtr--;
            this.astLengthPtr--;
            QualifiedAllocationExpression allocationExpression = anonymousTypeDeclaration.allocation;
            if (allocationExpression != null) {
                allocationExpression.sourceEnd = this.endStatementPosition;
                // handle type arguments
                length = this.genericsLengthStack[this.genericsLengthPtr--];
                this.genericsPtr -= length;
                System.arraycopy(this.genericsStack, this.genericsPtr + 1, allocationExpression.typeArguments = new TypeReference[length], 0, length);
                allocationExpression.sourceStart = this.intStack[this.intPtr--];
                checkForDiamond(allocationExpression.type);
            }
        }
        consumeInvocationExpression();
    }

    protected void consumeClassOrInterface() {
        this.genericsIdentifiersLengthStack[this.genericsIdentifiersLengthPtr] += this.identifierLengthStack[this.identifierLengthPtr];
        // handle type arguments
        pushOnGenericsLengthStack(0);
    }

    protected void consumeClassOrInterfaceName() {
        pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        // handle type arguments
        pushOnGenericsLengthStack(0);
    }

    protected void consumeClassTypeElt() {
        // ClassTypeElt ::= ClassType
        pushOnAstStack(getTypeReference(0));
        /* if incomplete thrown exception list, this.listLength counter will not have been reset,
		indicating that some items are available on the stack */
        this.listLength++;
    }

    protected void consumeClassTypeList() {
        // ClassTypeList ::= ClassTypeList ',' ClassTypeElt
        optimizedConcatNodeLists();
    }

    protected void consumeCompilationUnit() {
    // CompilationUnit ::= EnterCompilationUnit InternalCompilationUnit
    // do nothing by default
    }

    protected void consumeConditionalExpression(int op) {
        // ConditionalExpression ::= ConditionalOrExpression '?' Expression ':' ConditionalExpression
        //optimize the push/pop
        //consume position of the question mark
        this.intPtr -= 2;
        this.expressionPtr -= 2;
        this.expressionLengthPtr -= 2;
        this.expressionStack[this.expressionPtr] = new ConditionalExpression(this.expressionStack[this.expressionPtr], this.expressionStack[this.expressionPtr + 1], this.expressionStack[this.expressionPtr + 2]);
    }

    /**
 * @param op
 */
    protected void consumeConditionalExpressionWithName(int op) {
        // ConditionalExpression ::= Name '?' Expression ':' ConditionalExpression
        //consume position of the question mark
        this.intPtr -= 2;
        pushOnExpressionStack(getUnspecifiedReferenceOptimized());
        this.expressionPtr -= 2;
        this.expressionLengthPtr -= 2;
        this.expressionStack[this.expressionPtr] = new ConditionalExpression(this.expressionStack[this.expressionPtr + 2], this.expressionStack[this.expressionPtr], this.expressionStack[this.expressionPtr + 1]);
    }

    protected void consumeConstructorBlockStatements() {
        // ConstructorBody ::= NestedMethod '{' ExplicitConstructorInvocation BlockStatements '}'
        // explictly add the first statement into the list of statements
        concatNodeLists();
    }

    protected void consumeConstructorBody() {
        // ConstructorBody ::= NestedMethod  '{' BlockStatementsopt '}'
        // ConstructorBody ::= NestedMethod  '{' ExplicitConstructorInvocation '}'
        this.nestedMethod[this.nestedType]--;
    }

    protected void consumeConstructorDeclaration() {
        // ConstructorDeclaration ::= ConstructorHeader ConstructorBody
        /*
	this.astStack : MethodDeclaration statements
	this.identifierStack : name
	 ==>
	this.astStack : MethodDeclaration
	this.identifierStack :
	*/
        //must provide a default constructor call when needed
        int length;
        // pop the position of the {  (body of the method) pushed in block decl
        this.intPtr--;
        this.intPtr--;
        //statements
        this.realBlockPtr--;
        ExplicitConstructorCall constructorCall = null;
        Statement[] statements = null;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.astPtr -= length;
            if (!this.options.ignoreMethodBodies) {
                if (this.astStack[this.astPtr + 1] instanceof ExplicitConstructorCall) {
                    //avoid a isSomeThing that would only be used here BUT what is faster between two alternatives ?
                    System.arraycopy(this.astStack, this.astPtr + 2, statements = new Statement[length - 1], 0, length - 1);
                    constructorCall = (ExplicitConstructorCall) this.astStack[this.astPtr + 1];
                } else //need to add explicitly the super();
                {
                    System.arraycopy(this.astStack, this.astPtr + 1, statements = new Statement[length], 0, length);
                    constructorCall = SuperReference.implicitSuperConstructorCall();
                }
            }
        } else {
            boolean insideFieldInitializer = false;
            if (this.diet) {
                for (int i = this.nestedType; i > 0; i--) {
                    if (this.variablesCounter[i] > 0) {
                        insideFieldInitializer = true;
                        break;
                    }
                }
            }
            if (!this.options.ignoreMethodBodies) {
                if (!this.diet || insideFieldInitializer) {
                    // add it only in non-diet mode, if diet_bodies, then constructor call will be added elsewhere.
                    constructorCall = SuperReference.implicitSuperConstructorCall();
                }
            }
        }
        // now we know that the top of stack is a constructorDeclaration
        ConstructorDeclaration cd = (ConstructorDeclaration) this.astStack[this.astPtr];
        cd.constructorCall = constructorCall;
        cd.statements = statements;
        //highlight of the implicit call on the method name
        if (constructorCall != null && cd.constructorCall.sourceEnd == 0) {
            cd.constructorCall.sourceEnd = cd.sourceEnd;
            cd.constructorCall.sourceStart = cd.sourceStart;
        }
        if (!(this.diet && this.dietInt == 0) && statements == null && (constructorCall == null || constructorCall.isImplicitSuper()) && !containsComment(cd.bodyStart, this.endPosition)) {
            cd.bits |= ASTNode.UndocumentedEmptyBlock;
        }
        //watch for } that could be given as a unicode ! ( u007D is '}' )
        // store the this.endPosition (position just before the '}') in case there is
        // a trailing comment behind the end of the method
        cd.bodyEnd = this.endPosition;
        cd.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeConstructorHeader() {
        // ConstructorHeader ::= ConstructorHeaderName MethodHeaderParameters MethodHeaderThrowsClauseopt
        AbstractMethodDeclaration method = (AbstractMethodDeclaration) this.astStack[this.astPtr];
        if (this.currentToken == TokenNameLBRACE) {
            method.bodyStart = this.scanner.currentPosition;
        }
        // recovery
        if (this.currentElement != null) {
            if (// for invalid constructors
            this.currentToken == TokenNameSEMICOLON) {
                method.modifiers |= ExtraCompilerModifiers.AccSemicolonBody;
                method.declarationSourceEnd = this.scanner.currentPosition - 1;
                method.bodyEnd = this.scanner.currentPosition - 1;
                if (this.currentElement.parseTree() == method && this.currentElement.parent != null) {
                    this.currentElement = this.currentElement.parent;
                }
            }
            // used to avoid branching back into the regular automaton
            this.restartRecovery = true;
        }
    }

    protected void consumeConstructorHeaderName() {
        /* recovering - might be an empty message send */
        if (this.currentElement != null) {
            if (// was an allocation expression
            this.lastIgnoredToken == TokenNamenew) {
                // force to restart at this exact position
                this.lastCheckPoint = this.scanner.startPosition;
                this.restartRecovery = true;
                return;
            }
        }
        // ConstructorHeaderName ::=  Modifiersopt 'Identifier' '('
        ConstructorDeclaration cd = new ConstructorDeclaration(this.compilationUnit.compilationResult);
        //name -- this is not really revelant but we do .....
        cd.selector = this.identifierStack[this.identifierPtr];
        long selectorSource = this.identifierPositionStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        //modifiers
        cd.declarationSourceStart = this.intStack[this.intPtr--];
        cd.modifiers = this.intStack[this.intPtr--];
        // consume annotations
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, cd.annotations = new Annotation[length], 0, length);
        }
        // javadoc
        cd.javadoc = this.javadoc;
        this.javadoc = null;
        //highlight starts at the selector starts
        cd.sourceStart = (int) (selectorSource >>> 32);
        pushOnAstStack(cd);
        cd.sourceEnd = this.lParenPos;
        cd.bodyStart = this.lParenPos + 1;
        // initialize this.listLength before reading parameters/throws
        this.listLength = 0;
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = cd.bodyStart;
            if ((this.currentElement instanceof RecoveredType && this.lastIgnoredToken != TokenNameDOT) || cd.modifiers != 0) {
                this.currentElement = this.currentElement.add(cd, 0);
                this.lastIgnoredToken = -1;
            }
        }
    }

    protected void consumeConstructorHeaderNameWithTypeParameters() {
        /* recovering - might be an empty message send */
        if (this.currentElement != null) {
            if (// was an allocation expression
            this.lastIgnoredToken == TokenNamenew) {
                // force to restart at this exact position
                this.lastCheckPoint = this.scanner.startPosition;
                this.restartRecovery = true;
                return;
            }
        }
        // ConstructorHeaderName ::=  Modifiersopt TypeParameters 'Identifier' '('
        ConstructorDeclaration cd = new ConstructorDeclaration(this.compilationUnit.compilationResult);
        //name -- this is not really revelant but we do .....
        cd.selector = this.identifierStack[this.identifierPtr];
        long selectorSource = this.identifierPositionStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        // consume type parameters
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, cd.typeParameters = new TypeParameter[length], 0, length);
        //modifiers
        cd.declarationSourceStart = this.intStack[this.intPtr--];
        cd.modifiers = this.intStack[this.intPtr--];
        // consume annotations
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, cd.annotations = new Annotation[length], 0, length);
        }
        // javadoc
        cd.javadoc = this.javadoc;
        this.javadoc = null;
        //highlight starts at the selector starts
        cd.sourceStart = (int) (selectorSource >>> 32);
        pushOnAstStack(cd);
        cd.sourceEnd = this.lParenPos;
        cd.bodyStart = this.lParenPos + 1;
        // initialize this.listLength before reading parameters/throws
        this.listLength = 0;
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = cd.bodyStart;
            if ((this.currentElement instanceof RecoveredType && this.lastIgnoredToken != TokenNameDOT) || cd.modifiers != 0) {
                this.currentElement = this.currentElement.add(cd, 0);
                this.lastIgnoredToken = -1;
            }
        }
    }

    protected void consumeCreateInitializer() {
        pushOnAstStack(new Initializer(null, 0));
    }

    protected void consumeDefaultLabel() {
        // SwitchLabel ::= 'default' ':'
        CaseStatement defaultStatement = new CaseStatement(null, this.intStack[this.intPtr--], this.intStack[this.intPtr--]);
        // Look for $fall-through$ and $CASES-OMITTED$ tags in leading comment for case statement
        if (hasLeadingTagComment(FALL_THROUGH_TAG, defaultStatement.sourceStart)) {
            defaultStatement.bits |= ASTNode.DocumentedFallthrough;
        }
        if (hasLeadingTagComment(CASES_OMITTED_TAG, defaultStatement.sourceStart)) {
            defaultStatement.bits |= ASTNode.DocumentedCasesOmitted;
        }
        pushOnAstStack(defaultStatement);
    }

    protected void consumeDefaultModifiers() {
        // might update modifiers with AccDeprecated
        checkComment();
        // modifiers
        pushOnIntStack(this.modifiers);
        pushOnIntStack(this.modifiersSourceStart >= 0 ? this.modifiersSourceStart : this.scanner.startPosition);
        resetModifiers();
        // no annotation
        pushOnExpressionStackLengthStack(0);
    }

    protected void consumeDiet() {
        // Diet ::= $empty
        checkComment();
        // push the start position of a javadoc comment if there is one
        pushOnIntStack(this.modifiersSourceStart);
        resetModifiers();
        jumpOverMethodBody();
    }

    protected void consumeDims() {
        // Dims ::= DimsLoop
        pushOnIntStack(this.dimensions);
        this.dimensions = 0;
    }

    protected void consumeDimWithOrWithOutExpr() {
        // DimWithOrWithOutExpr ::= TypeAnnotationsopt '[' ']'
        // DimWithOrWithOutExpr ::= TypeAnnotationsopt '[' Expression ']'
        pushOnExpressionStack(null);
        if (this.currentElement != null && this.currentToken == TokenNameLBRACE) {
            this.ignoreNextOpeningBrace = true;
            this.currentElement.bracketBalance++;
        }
    }

    protected void consumeDimWithOrWithOutExprs() {
        // DimWithOrWithOutExprs ::= DimWithOrWithOutExprs DimWithOrWithOutExpr
        concatExpressionLists();
    }

    protected void consumeUnionType() {
        // UnionType ::= UnionType '|' Type
        pushOnAstStack(getTypeReference(this.intStack[this.intPtr--]));
        optimizedConcatNodeLists();
    }

    protected void consumeUnionTypeAsClassType() {
        // UnionType ::= Type
        pushOnAstStack(getTypeReference(this.intStack[this.intPtr--]));
    }

    protected void consumeEmptyAnnotationTypeMemberDeclarationsopt() {
        // AnnotationTypeMemberDeclarationsopt ::= $empty
        pushOnAstLengthStack(0);
    }

    protected void consumeEmptyArgumentListopt() {
        // ArgumentListopt ::= $empty
        pushOnExpressionStackLengthStack(0);
    }

    protected void consumeEmptyArguments() {
        // Argumentsopt ::= $empty
        final FieldDeclaration fieldDeclaration = (FieldDeclaration) this.astStack[this.astPtr];
        pushOnIntStack(fieldDeclaration.sourceEnd);
        pushOnExpressionStackLengthStack(0);
    }

    protected void consumeEmptyArrayInitializer() {
        // ArrayInitializer ::= '{' ,opt '}'
        arrayInitializer(0);
    }

    protected void consumeEmptyArrayInitializeropt() {
        // ArrayInitializeropt ::= $empty
        pushOnExpressionStackLengthStack(0);
    }

    protected void consumeEmptyBlockStatementsopt() {
        // BlockStatementsopt ::= $empty
        pushOnAstLengthStack(0);
    }

    protected void consumeEmptyCatchesopt() {
        // Catchesopt ::= $empty
        pushOnAstLengthStack(0);
    }

    protected void consumeEmptyClassBodyDeclarationsopt() {
        // ClassBodyDeclarationsopt ::= $empty
        pushOnAstLengthStack(0);
    }

    protected void consumeEmptyDimsopt() {
        // Dimsopt ::= $empty
        pushOnIntStack(0);
    }

    protected void consumeEmptyEnumDeclarations() {
        // EnumBodyDeclarationsopt ::= $empty
        pushOnAstLengthStack(0);
    }

    protected void consumeEmptyExpression() {
        // Expressionopt ::= $empty
        pushOnExpressionStackLengthStack(0);
    }

    protected void consumeEmptyForInitopt() {
        // ForInitopt ::= $empty
        pushOnAstLengthStack(0);
        this.forStartPosition = 0;
    }

    protected void consumeEmptyForUpdateopt() {
        // ForUpdateopt ::= $empty
        pushOnExpressionStackLengthStack(0);
    }

    protected void consumeEmptyInterfaceMemberDeclarationsopt() {
        // InterfaceMemberDeclarationsopt ::= $empty
        pushOnAstLengthStack(0);
    }

    protected void consumeEmptyInternalCompilationUnit() {
        // nothing to do by default
        if (this.compilationUnit.isPackageInfo()) {
            this.compilationUnit.types = new TypeDeclaration[1];
            this.compilationUnit.createPackageInfoType();
        }
    }

    protected void consumeEmptyMemberValueArrayInitializer() {
        // MemberValueArrayInitializer ::= '{' ',' '}'
        // MemberValueArrayInitializer ::= '{' '}'
        arrayInitializer(0);
    }

    protected void consumeEmptyMemberValuePairsopt() {
        // MemberValuePairsopt ::= $empty
        pushOnAstLengthStack(0);
    }

    protected void consumeEmptyMethodHeaderDefaultValue() {
        // DefaultValueopt ::= $empty
        AbstractMethodDeclaration method = (AbstractMethodDeclaration) this.astStack[this.astPtr];
        if (//'method' can be a MethodDeclaration when recovery is started
        method.isAnnotationMethod()) {
            pushOnExpressionStackLengthStack(0);
        }
        this.recordStringLiterals = true;
    }

    protected void consumeEmptyStatement() {
        // EmptyStatement ::= ';'
        char[] source = this.scanner.source;
        if (source[this.endStatementPosition] == ';') {
            pushOnAstStack(new EmptyStatement(this.endStatementPosition, this.endStatementPosition));
        } else {
            if (source.length > 5) {
                int c1 = 0, c2 = 0, c3 = 0, c4 = 0;
                int pos = this.endStatementPosition - 4;
                while (source[pos] == 'u') {
                    pos--;
                }
                if (source[pos] == '\\' && !((c1 = ScannerHelper.getHexadecimalValue(source[this.endStatementPosition - 3])) > 15 || c1 < 0 || (c2 = ScannerHelper.getHexadecimalValue(source[this.endStatementPosition - 2])) > 15 || c2 < 0 || (c3 = ScannerHelper.getHexadecimalValue(source[this.endStatementPosition - 1])) > 15 || c3 < 0 || (c4 = ScannerHelper.getHexadecimalValue(source[this.endStatementPosition])) > 15 || c4 < 0) && ((char) (((c1 * 16 + c2) * 16 + c3) * 16 + c4)) == ';') {
                    pushOnAstStack(new EmptyStatement(pos, this.endStatementPosition));
                    return;
                }
            }
            pushOnAstStack(new EmptyStatement(this.endPosition + 1, this.endStatementPosition));
        }
    }

    protected void consumeEmptySwitchBlock() {
        pushOnAstLengthStack(0);
    }

    protected void consumeEmptyTypeDeclaration() {
        pushOnAstLengthStack(0);
        if (!this.statementRecoveryActivated)
            problemReporter().superfluousSemicolon(this.endPosition + 1, this.endStatementPosition);
        flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeEnhancedForStatement() {
        this.astLengthPtr--;
        Statement statement = (Statement) this.astStack[this.astPtr--];
        ForeachStatement foreachStatement = (ForeachStatement) this.astStack[this.astPtr];
        foreachStatement.action = statement;
        if (statement instanceof EmptyStatement)
            statement.bits |= ASTNode.IsUsefulEmptyStatement;
        foreachStatement.sourceEnd = this.endStatementPosition;
    }

    protected void consumeEnhancedForStatementHeader() {
        final ForeachStatement statement = (ForeachStatement) this.astStack[this.astPtr];
        this.expressionLengthPtr--;
        final Expression collection = this.expressionStack[this.expressionPtr--];
        statement.collection = collection;
        statement.elementVariable.declarationSourceEnd = collection.sourceEnd;
        statement.elementVariable.declarationEnd = collection.sourceEnd;
        statement.sourceEnd = this.rParenPos;
        if (!this.statementRecoveryActivated && this.options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            problemReporter().invalidUsageOfForeachStatements(statement.elementVariable, collection);
        }
    }

    protected void consumeEnhancedForStatementHeaderInit(boolean hasModifiers) {
        TypeReference type;
        char[] identifierName = this.identifierStack[this.identifierPtr];
        long namePosition = this.identifierPositionStack[this.identifierPtr];
        LocalDeclaration localDeclaration = createLocalDeclaration(identifierName, (int) (namePosition >>> 32), (int) namePosition);
        localDeclaration.declarationSourceEnd = localDeclaration.declarationEnd;
        localDeclaration.bits |= ASTNode.IsForeachElementVariable;
        int extraDims = this.intStack[this.intPtr--];
        Annotation[][] annotationsOnExtendedDimensions = extraDims == 0 ? null : getAnnotationsOnDimensions(extraDims);
        this.identifierPtr--;
        this.identifierLengthPtr--;
        int declarationSourceStart = 0;
        int modifiersValue = 0;
        if (hasModifiers) {
            declarationSourceStart = this.intStack[this.intPtr--];
            modifiersValue = this.intStack[this.intPtr--];
        } else {
            this.intPtr -= 2;
        }
        type = getTypeReference(this.intStack[this.intPtr--]);
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, localDeclaration.annotations = new Annotation[length], 0, length);
            localDeclaration.bits |= ASTNode.HasTypeAnnotations;
        }
        if (extraDims != 0) {
            type = augmentTypeWithAdditionalDimensions(type, extraDims, annotationsOnExtendedDimensions, false);
        }
        if (hasModifiers) {
            localDeclaration.declarationSourceStart = declarationSourceStart;
            localDeclaration.modifiers = modifiersValue;
        } else {
            localDeclaration.declarationSourceStart = type.sourceStart;
        }
        localDeclaration.type = type;
        localDeclaration.bits |= (type.bits & ASTNode.HasTypeAnnotations);
        ForeachStatement iteratorForStatement = new ForeachStatement(localDeclaration, this.intStack[this.intPtr--]);
        pushOnAstStack(iteratorForStatement);
        iteratorForStatement.sourceEnd = localDeclaration.declarationSourceEnd;
        this.forStartPosition = 0;
    }

    protected void consumeEnterAnonymousClassBody(boolean qualified) {
        TypeReference typeReference = getTypeReference(0);
        TypeDeclaration anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
        anonymousType.name = CharOperation.NO_CHAR;
        anonymousType.bits |= (ASTNode.IsAnonymousType | ASTNode.IsLocalType);
        anonymousType.bits |= (typeReference.bits & ASTNode.HasTypeAnnotations);
        QualifiedAllocationExpression alloc = new QualifiedAllocationExpression(anonymousType);
        markEnclosingMemberWithLocalType();
        pushOnAstStack(anonymousType);
        alloc.sourceEnd = this.rParenPos;
        int argumentLength;
        if ((argumentLength = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            this.expressionPtr -= argumentLength;
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, alloc.arguments = new Expression[argumentLength], 0, argumentLength);
        }
        if (qualified) {
            this.expressionLengthPtr--;
            alloc.enclosingInstance = this.expressionStack[this.expressionPtr--];
        }
        alloc.type = typeReference;
        anonymousType.sourceEnd = alloc.sourceEnd;
        anonymousType.sourceStart = anonymousType.declarationSourceStart = alloc.type.sourceStart;
        alloc.sourceStart = this.intStack[this.intPtr--];
        pushOnExpressionStack(alloc);
        anonymousType.bodyStart = this.scanner.currentPosition;
        this.listLength = 0;
        this.scanner.commentPtr = -1;
        if (this.currentElement != null) {
            this.lastCheckPoint = anonymousType.bodyStart;
            this.currentElement = this.currentElement.add(anonymousType, 0);
            if (!(this.currentElement instanceof RecoveredAnnotation)) {
                if (isIndirectlyInsideLambdaExpression())
                    this.ignoreNextOpeningBrace = true;
                else
                    this.currentToken = 0;
            } else {
                this.ignoreNextOpeningBrace = true;
                this.currentElement.bracketBalance++;
            }
            this.lastIgnoredToken = -1;
        }
    }

    protected void consumeEnterCompilationUnit() {
    }

    protected void consumeEnterMemberValue() {
        if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
            RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation) this.currentElement;
            recoveredAnnotation.hasPendingMemberValueName = true;
        }
    }

    protected void consumeEnterMemberValueArrayInitializer() {
        if (this.currentElement != null) {
            this.ignoreNextOpeningBrace = true;
            this.currentElement.bracketBalance++;
        }
    }

    protected void consumeEnterVariable() {
        char[] identifierName = this.identifierStack[this.identifierPtr];
        long namePosition = this.identifierPositionStack[this.identifierPtr];
        int extendedDimensions = this.intStack[this.intPtr--];
        Annotation[][] annotationsOnExtendedDimensions = extendedDimensions == 0 ? null : getAnnotationsOnDimensions(extendedDimensions);
        AbstractVariableDeclaration declaration;
        // create the ast node
        boolean isLocalDeclaration = this.nestedMethod[this.nestedType] != 0;
        if (isLocalDeclaration) {
            // create the local variable declarations
            declaration = createLocalDeclaration(identifierName, (int) (namePosition >>> 32), (int) namePosition);
        } else {
            // create the field declaration
            declaration = createFieldDeclaration(identifierName, (int) (namePosition >>> 32), (int) namePosition);
        }
        this.identifierPtr--;
        this.identifierLengthPtr--;
        TypeReference type;
        int variableIndex = this.variablesCounter[this.nestedType];
        if (variableIndex == 0) {
            // first variable of the declaration (FieldDeclaration or LocalDeclaration)
            if (isLocalDeclaration) {
                declaration.declarationSourceStart = this.intStack[this.intPtr--];
                declaration.modifiers = this.intStack[this.intPtr--];
                // consume annotations
                int length;
                if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                    System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, declaration.annotations = new Annotation[length], 0, length);
                }
                // type dimension
                type = getTypeReference(this.intStack[this.intPtr--]);
                if (declaration.declarationSourceStart == -1) {
                    // this is true if there is no modifiers for the local variable declaration
                    declaration.declarationSourceStart = type.sourceStart;
                }
                pushOnAstStack(type);
            } else {
                // type dimension
                type = getTypeReference(this.intStack[this.intPtr--]);
                pushOnAstStack(type);
                declaration.declarationSourceStart = this.intStack[this.intPtr--];
                declaration.modifiers = this.intStack[this.intPtr--];
                // consume annotations
                int length;
                if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                    System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, declaration.annotations = new Annotation[length], 0, length);
                }
                // Store javadoc only on first declaration as it is the same for all ones
                FieldDeclaration fieldDeclaration = (FieldDeclaration) declaration;
                fieldDeclaration.javadoc = this.javadoc;
            }
            this.javadoc = null;
        } else {
            type = (TypeReference) this.astStack[this.astPtr - variableIndex];
            AbstractVariableDeclaration previousVariable = (AbstractVariableDeclaration) this.astStack[this.astPtr];
            declaration.declarationSourceStart = previousVariable.declarationSourceStart;
            declaration.modifiers = previousVariable.modifiers;
            final Annotation[] annotations = previousVariable.annotations;
            if (annotations != null) {
                final int annotationsLength = annotations.length;
                System.arraycopy(annotations, 0, declaration.annotations = new Annotation[annotationsLength], 0, annotationsLength);
            }
        }
        declaration.type = extendedDimensions == 0 ? type : augmentTypeWithAdditionalDimensions(type, extendedDimensions, annotationsOnExtendedDimensions, false);
        declaration.bits |= (type.bits & ASTNode.HasTypeAnnotations);
        this.variablesCounter[this.nestedType]++;
        pushOnAstStack(declaration);
        // recovery
        if (this.currentElement != null) {
            if (!(this.currentElement instanceof RecoveredType) && (this.currentToken == TokenNameDOT || //|| declaration.modifiers != 0
            (Util.getLineNumber(declaration.type.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr) != Util.getLineNumber((int) (namePosition >>> 32), this.scanner.lineEnds, 0, this.scanner.linePtr)))) {
                this.lastCheckPoint = (int) (namePosition >>> 32);
                this.restartRecovery = true;
                return;
            }
            if (isLocalDeclaration) {
                LocalDeclaration localDecl = (LocalDeclaration) this.astStack[this.astPtr];
                this.lastCheckPoint = localDecl.sourceEnd + 1;
                this.currentElement = this.currentElement.add(localDecl, 0);
            } else {
                FieldDeclaration fieldDecl = (FieldDeclaration) this.astStack[this.astPtr];
                this.lastCheckPoint = fieldDecl.sourceEnd + 1;
                this.currentElement = this.currentElement.add(fieldDecl, 0);
            }
            this.lastIgnoredToken = -1;
        }
    }

    protected void consumeEnumBodyNoConstants() {
    // nothing to do
    // The 0 on the astLengthStack has been pushed by EnumBodyDeclarationsopt
    }

    protected void consumeEnumBodyWithConstants() {
        // merge the constants values with the class body
        concatNodeLists();
    }

    protected void consumeEnumConstantHeader() {
        FieldDeclaration enumConstant = (FieldDeclaration) this.astStack[this.astPtr];
        boolean foundOpeningBrace = this.currentToken == TokenNameLBRACE;
        if (foundOpeningBrace) {
            // qualified allocation expression
            TypeDeclaration anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
            anonymousType.name = CharOperation.NO_CHAR;
            anonymousType.bits |= (ASTNode.IsAnonymousType | ASTNode.IsLocalType);
            final int start = this.scanner.startPosition;
            anonymousType.declarationSourceStart = start;
            anonymousType.sourceStart = start;
            // closing parenthesis
            anonymousType.sourceEnd = start;
            anonymousType.modifiers = 0;
            anonymousType.bodyStart = this.scanner.currentPosition;
            markEnclosingMemberWithLocalType();
            consumeNestedType();
            this.variablesCounter[this.nestedType]++;
            pushOnAstStack(anonymousType);
            QualifiedAllocationExpression allocationExpression = new QualifiedAllocationExpression(anonymousType);
            allocationExpression.enumConstant = enumConstant;
            // fill arguments if needed
            int length;
            if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                this.expressionPtr -= length;
                System.arraycopy(this.expressionStack, this.expressionPtr + 1, allocationExpression.arguments = new Expression[length], 0, length);
            }
            enumConstant.initialization = allocationExpression;
        } else {
            AllocationExpression allocationExpression = new AllocationExpression();
            allocationExpression.enumConstant = enumConstant;
            // fill arguments if needed
            int length;
            if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                this.expressionPtr -= length;
                System.arraycopy(this.expressionStack, this.expressionPtr + 1, allocationExpression.arguments = new Expression[length], 0, length);
            }
            enumConstant.initialization = allocationExpression;
        }
        // initialize the starting position of the allocation expression
        enumConstant.initialization.sourceStart = enumConstant.declarationSourceStart;
        // recovery
        if (this.currentElement != null) {
            if (foundOpeningBrace) {
                TypeDeclaration anonymousType = (TypeDeclaration) this.astStack[this.astPtr];
                this.currentElement = this.currentElement.add(anonymousType, 0);
                this.lastCheckPoint = anonymousType.bodyStart;
                this.lastIgnoredToken = -1;
                if (isIndirectlyInsideLambdaExpression())
                    this.ignoreNextOpeningBrace = true;
                else
                    // opening brace already taken into account
                    this.currentToken = 0;
            } else {
                if (this.currentToken == TokenNameSEMICOLON) {
                    RecoveredType currentType = currentRecoveryType();
                    if (currentType != null) {
                        currentType.insideEnumConstantPart = false;
                    }
                }
                // force to restart at this exact position
                this.lastCheckPoint = this.scanner.startPosition;
                this.lastIgnoredToken = -1;
                this.restartRecovery = true;
            }
        }
    }

    protected void consumeEnumConstantHeaderName() {
        if (this.currentElement != null) {
            if (!(this.currentElement instanceof RecoveredType || (this.currentElement instanceof RecoveredField && ((RecoveredField) this.currentElement).fieldDeclaration.type == null)) || (this.lastIgnoredToken == TokenNameDOT)) {
                this.lastCheckPoint = this.scanner.startPosition;
                this.restartRecovery = true;
                return;
            }
        }
        long namePosition = this.identifierPositionStack[this.identifierPtr];
        char[] constantName = this.identifierStack[this.identifierPtr];
        final int sourceEnd = (int) namePosition;
        FieldDeclaration enumConstant = createFieldDeclaration(constantName, (int) (namePosition >>> 32), sourceEnd);
        this.identifierPtr--;
        this.identifierLengthPtr--;
        enumConstant.modifiersSourceStart = this.intStack[this.intPtr--];
        enumConstant.modifiers = this.intStack[this.intPtr--];
        enumConstant.declarationSourceStart = enumConstant.modifiersSourceStart;
        // consume annotations
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, enumConstant.annotations = new Annotation[length], 0, length);
            enumConstant.bits |= ASTNode.HasTypeAnnotations;
        }
        pushOnAstStack(enumConstant);
        if (this.currentElement != null) {
            this.lastCheckPoint = enumConstant.sourceEnd + 1;
            this.currentElement = this.currentElement.add(enumConstant, 0);
        }
        // javadoc
        enumConstant.javadoc = this.javadoc;
        this.javadoc = null;
    }

    protected void consumeEnumConstantNoClassBody() {
        // set declarationEnd and declarationSourceEnd
        int endOfEnumConstant = this.intStack[this.intPtr--];
        final FieldDeclaration fieldDeclaration = (FieldDeclaration) this.astStack[this.astPtr];
        fieldDeclaration.declarationEnd = endOfEnumConstant;
        fieldDeclaration.declarationSourceEnd = endOfEnumConstant;
        // initialize the starting position of the allocation expression
        ASTNode initialization = fieldDeclaration.initialization;
        if (initialization != null) {
            initialization.sourceEnd = endOfEnumConstant;
        }
    }

    protected void consumeEnumConstants() {
        concatNodeLists();
    }

    protected void consumeEnumConstantWithClassBody() {
        dispatchDeclarationInto(this.astLengthStack[this.astLengthPtr--]);
        // pop type
        TypeDeclaration anonymousType = (TypeDeclaration) this.astStack[this.astPtr--];
        this.astLengthPtr--;
        anonymousType.bodyEnd = this.endPosition;
        anonymousType.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
        final FieldDeclaration fieldDeclaration = ((FieldDeclaration) this.astStack[this.astPtr]);
        fieldDeclaration.declarationEnd = this.endStatementPosition;
        int declarationSourceEnd = anonymousType.declarationSourceEnd;
        fieldDeclaration.declarationSourceEnd = declarationSourceEnd;
        // remove end position of the arguments
        this.intPtr--;
        this.variablesCounter[this.nestedType] = 0;
        this.nestedType--;
        ASTNode initialization = fieldDeclaration.initialization;
        if (initialization != null) {
            initialization.sourceEnd = declarationSourceEnd;
        }
    }

    protected void consumeEnumDeclaration() {
        // EnumDeclaration ::= EnumHeader ClassHeaderImplementsopt EnumBody
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            //there are length declarations
            //dispatch according to the type of the declarations
            dispatchDeclarationIntoEnumDeclaration(length);
        }
        TypeDeclaration enumDeclaration = (TypeDeclaration) this.astStack[this.astPtr];
        //convert constructor that do not have the type's name into methods
        boolean hasConstructor = enumDeclaration.checkConstructors(this);
        //add the default constructor when needed
        if (!hasConstructor) {
            boolean insideFieldInitializer = false;
            if (this.diet) {
                for (int i = this.nestedType; i > 0; i--) {
                    if (this.variablesCounter[i] > 0) {
                        insideFieldInitializer = true;
                        break;
                    }
                }
            }
            enumDeclaration.createDefaultConstructor(!this.diet || insideFieldInitializer, true);
        }
        //always add <clinit> (will be remove at code gen time if empty)
        if (this.scanner.containsAssertKeyword) {
            enumDeclaration.bits |= ASTNode.ContainsAssertion;
        }
        enumDeclaration.addClinit();
        enumDeclaration.bodyEnd = this.endStatementPosition;
        if (length == 0 && !containsComment(enumDeclaration.bodyStart, enumDeclaration.bodyEnd)) {
            enumDeclaration.bits |= ASTNode.UndocumentedEmptyBlock;
        }
        enumDeclaration.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeEnumDeclarations() {
    // Do nothing by default
    }

    protected void consumeEnumHeader() {
        TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
        if (this.currentToken == TokenNameLBRACE) {
            typeDecl.bodyStart = this.scanner.currentPosition;
        }
        if (this.currentElement != null) {
            // used to avoid branching back into the regular automaton
            this.restartRecovery = true;
        }
        // flush the comments related to the enum header
        this.scanner.commentPtr = -1;
    }

    protected void consumeEnumHeaderName() {
        // EnumHeaderName ::= Modifiersopt 'enum' Identifier
        TypeDeclaration enumDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                enumDeclaration.bits |= ASTNode.IsMemberType;
            }
        } else {
            // Record that the block has a declaration for local types
            //		markEnclosingMemberWithLocalType();
            blockReal();
        }
        //highlight the name of the type
        long pos = this.identifierPositionStack[this.identifierPtr];
        enumDeclaration.sourceEnd = (int) pos;
        enumDeclaration.sourceStart = (int) (pos >>> 32);
        enumDeclaration.name = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        //compute the declaration source too
        // 'class' and 'interface' push two int positions: the beginning of the class token and its end.
        // we want to keep the beginning position but get rid of the end position
        // it is only used for the ClassLiteralAccess positions.
        enumDeclaration.declarationSourceStart = this.intStack[this.intPtr--];
        // remove the end position of the class token
        this.intPtr--;
        enumDeclaration.modifiersSourceStart = this.intStack[this.intPtr--];
        enumDeclaration.modifiers = this.intStack[this.intPtr--] | ClassFileConstants.AccEnum;
        if (enumDeclaration.modifiersSourceStart >= 0) {
            enumDeclaration.declarationSourceStart = enumDeclaration.modifiersSourceStart;
        }
        // Store secondary info
        if ((enumDeclaration.bits & ASTNode.IsMemberType) == 0 && (enumDeclaration.bits & ASTNode.IsLocalType) == 0) {
            if (this.compilationUnit != null && !CharOperation.equals(enumDeclaration.name, this.compilationUnit.getMainTypeName())) {
                enumDeclaration.bits |= ASTNode.IsSecondaryType;
            }
        }
        // consume annotations
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, enumDeclaration.annotations = new Annotation[length], 0, length);
        }
        //	if (this.currentToken == TokenNameLBRACE) {
        //		enumDeclaration.bodyStart = this.scanner.currentPosition;
        //	}
        enumDeclaration.bodyStart = enumDeclaration.sourceEnd + 1;
        pushOnAstStack(enumDeclaration);
        // will be updated when reading super-interfaces
        this.listLength = 0;
        if (!this.statementRecoveryActivated && this.options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            //TODO this code will be never run while 'enum' is an identifier in 1.3 scanner
            problemReporter().invalidUsageOfEnumDeclarations(enumDeclaration);
        }
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = enumDeclaration.bodyStart;
            this.currentElement = this.currentElement.add(enumDeclaration, 0);
            this.lastIgnoredToken = -1;
        }
        // javadoc
        enumDeclaration.javadoc = this.javadoc;
        this.javadoc = null;
    }

    protected void consumeEnumHeaderNameWithTypeParameters() {
        // EnumHeaderNameWithTypeParameters ::= Modifiersopt 'enum' Identifier TypeParameters
        TypeDeclaration enumDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
        // consume type parameters
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, enumDeclaration.typeParameters = new TypeParameter[length], 0, length);
        problemReporter().invalidUsageOfTypeParametersForEnumDeclaration(enumDeclaration);
        enumDeclaration.bodyStart = enumDeclaration.typeParameters[length - 1].declarationSourceEnd + 1;
        //	enumDeclaration.typeParameters = null;
        this.listTypeParameterLength = 0;
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                enumDeclaration.bits |= ASTNode.IsMemberType;
            }
        } else {
            // Record that the block has a declaration for local types
            //		markEnclosingMemberWithLocalType();
            blockReal();
        }
        //highlight the name of the type
        long pos = this.identifierPositionStack[this.identifierPtr];
        enumDeclaration.sourceEnd = (int) pos;
        enumDeclaration.sourceStart = (int) (pos >>> 32);
        enumDeclaration.name = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        //compute the declaration source too
        // 'class' and 'interface' push two int positions: the beginning of the class token and its end.
        // we want to keep the beginning position but get rid of the end position
        // it is only used for the ClassLiteralAccess positions.
        enumDeclaration.declarationSourceStart = this.intStack[this.intPtr--];
        // remove the end position of the class token
        this.intPtr--;
        enumDeclaration.modifiersSourceStart = this.intStack[this.intPtr--];
        enumDeclaration.modifiers = this.intStack[this.intPtr--] | ClassFileConstants.AccEnum;
        if (enumDeclaration.modifiersSourceStart >= 0) {
            enumDeclaration.declarationSourceStart = enumDeclaration.modifiersSourceStart;
        }
        // Store secondary info
        if ((enumDeclaration.bits & ASTNode.IsMemberType) == 0 && (enumDeclaration.bits & ASTNode.IsLocalType) == 0) {
            if (this.compilationUnit != null && !CharOperation.equals(enumDeclaration.name, this.compilationUnit.getMainTypeName())) {
                enumDeclaration.bits |= ASTNode.IsSecondaryType;
            }
        }
        // consume annotations
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, enumDeclaration.annotations = new Annotation[length], 0, length);
        }
        //	if (this.currentToken == TokenNameLBRACE) {
        //		enumDeclaration.bodyStart = this.scanner.currentPosition;
        //	}
        enumDeclaration.bodyStart = enumDeclaration.sourceEnd + 1;
        pushOnAstStack(enumDeclaration);
        // will be updated when reading super-interfaces
        this.listLength = 0;
        if (!this.statementRecoveryActivated && this.options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            //TODO this code will be never run while 'enum' is an identifier in 1.3 scanner
            problemReporter().invalidUsageOfEnumDeclarations(enumDeclaration);
        }
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = enumDeclaration.bodyStart;
            this.currentElement = this.currentElement.add(enumDeclaration, 0);
            this.lastIgnoredToken = -1;
        }
        // javadoc
        enumDeclaration.javadoc = this.javadoc;
        this.javadoc = null;
    }

    protected void consumeEqualityExpression(int op) {
        // EqualityExpression ::= EqualityExpression '==' RelationalExpression
        // EqualityExpression ::= EqualityExpression '!=' RelationalExpression
        //optimize the push/pop
        this.expressionPtr--;
        this.expressionLengthPtr--;
        this.expressionStack[this.expressionPtr] = new EqualExpression(this.expressionStack[this.expressionPtr], this.expressionStack[this.expressionPtr + 1], op);
    }

    /*
 * @param op
 */
    protected void consumeEqualityExpressionWithName(int op) {
        // EqualityExpression ::= Name '==' RelationalExpression
        // EqualityExpression ::= Name '!=' RelationalExpression
        pushOnExpressionStack(getUnspecifiedReferenceOptimized());
        this.expressionPtr--;
        this.expressionLengthPtr--;
        this.expressionStack[this.expressionPtr] = new EqualExpression(this.expressionStack[this.expressionPtr + 1], this.expressionStack[this.expressionPtr], op);
    }

    protected void consumeExitMemberValue() {
        // ExitMemberValue ::= $empty
        if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
            RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation) this.currentElement;
            recoveredAnnotation.hasPendingMemberValueName = false;
            recoveredAnnotation.memberValuPairEqualEnd = -1;
        }
    }

    protected void consumeExitTryBlock() {
        //ExitTryBlock ::= $empty
        if (this.currentElement != null) {
            this.restartRecovery = true;
        }
    }

    protected void consumeExitVariableWithInitialization() {
        // ExitVariableWithInitialization ::= $empty
        // do nothing by default
        this.expressionLengthPtr--;
        AbstractVariableDeclaration variableDecl = (AbstractVariableDeclaration) this.astStack[this.astPtr];
        variableDecl.initialization = this.expressionStack[this.expressionPtr--];
        // we need to update the declarationSourceEnd of the local variable declaration to the
        // source end position of the initialization expression
        variableDecl.declarationSourceEnd = variableDecl.initialization.sourceEnd;
        variableDecl.declarationEnd = variableDecl.initialization.sourceEnd;
        recoveryExitFromVariable();
    }

    protected void consumeExitVariableWithoutInitialization() {
        // ExitVariableWithoutInitialization ::= $empty
        // do nothing by default
        AbstractVariableDeclaration variableDecl = (AbstractVariableDeclaration) this.astStack[this.astPtr];
        variableDecl.declarationSourceEnd = variableDecl.declarationEnd;
        if (this.currentElement != null && this.currentElement instanceof RecoveredField) {
            if (this.endStatementPosition > variableDecl.sourceEnd) {
                this.currentElement.updateSourceEndIfNecessary(this.endStatementPosition);
            }
        }
        recoveryExitFromVariable();
    }

    protected void consumeExplicitConstructorInvocation(int flag, int recFlag) {
        /* flag allows to distinguish 3 cases :
	(0) :
	ExplicitConstructorInvocation ::= 'this' '(' ArgumentListopt ')' ';'
	ExplicitConstructorInvocation ::= 'super' '(' ArgumentListopt ')' ';'
	(1) :
	ExplicitConstructorInvocation ::= Primary '.' 'super' '(' ArgumentListopt ')' ';'
	ExplicitConstructorInvocation ::= Primary '.' 'this' '(' ArgumentListopt ')' ';'
	(2) :
	ExplicitConstructorInvocation ::= Name '.' 'super' '(' ArgumentListopt ')' ';'
	ExplicitConstructorInvocation ::= Name '.' 'this' '(' ArgumentListopt ')' ';'
	*/
        int startPosition = this.intStack[this.intPtr--];
        ExplicitConstructorCall ecc = new ExplicitConstructorCall(recFlag);
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            this.expressionPtr -= length;
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, ecc.arguments = new Expression[length], 0, length);
        }
        switch(flag) {
            case 0:
                ecc.sourceStart = startPosition;
                break;
            case 1:
                this.expressionLengthPtr--;
                ecc.sourceStart = (ecc.qualification = this.expressionStack[this.expressionPtr--]).sourceStart;
                break;
            case 2:
                ecc.sourceStart = (ecc.qualification = getUnspecifiedReferenceOptimized()).sourceStart;
                break;
        }
        pushOnAstStack(ecc);
        ecc.sourceEnd = this.endStatementPosition;
    }

    protected void consumeExplicitConstructorInvocationWithTypeArguments(int flag, int recFlag) {
        /* flag allows to distinguish 3 cases :
	(0) :
	ExplicitConstructorInvocation ::= TypeArguments 'this' '(' ArgumentListopt ')' ';'
	ExplicitConstructorInvocation ::= TypeArguments 'super' '(' ArgumentListopt ')' ';'
	(1) :
	ExplicitConstructorInvocation ::= Primary '.' TypeArguments 'super' '(' ArgumentListopt ')' ';'
	ExplicitConstructorInvocation ::= Primary '.' TypeArguments 'this' '(' ArgumentListopt ')' ';'
	(2) :
	ExplicitConstructorInvocation ::= Name '.' TypeArguments 'super' '(' ArgumentListopt ')' ';'
	ExplicitConstructorInvocation ::= Name '.' TypeArguments 'this' '(' ArgumentListopt ')' ';'
	*/
        int startPosition = this.intStack[this.intPtr--];
        ExplicitConstructorCall ecc = new ExplicitConstructorCall(recFlag);
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            this.expressionPtr -= length;
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, ecc.arguments = new Expression[length], 0, length);
        }
        length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, ecc.typeArguments = new TypeReference[length], 0, length);
        ecc.typeArgumentsSourceStart = this.intStack[this.intPtr--];
        switch(flag) {
            case 0:
                ecc.sourceStart = startPosition;
                break;
            case 1:
                this.expressionLengthPtr--;
                ecc.sourceStart = (ecc.qualification = this.expressionStack[this.expressionPtr--]).sourceStart;
                break;
            case 2:
                ecc.sourceStart = (ecc.qualification = getUnspecifiedReferenceOptimized()).sourceStart;
                break;
        }
        pushOnAstStack(ecc);
        ecc.sourceEnd = this.endStatementPosition;
    }

    protected void consumeExpressionStatement() {
        // ExpressionStatement ::= StatementExpression ';'
        this.expressionLengthPtr--;
        Expression expression = this.expressionStack[this.expressionPtr--];
        expression.statementEnd = this.endStatementPosition;
        expression.bits |= ASTNode.InsideExpressionStatement;
        pushOnAstStack(expression);
    }

    protected void consumeFieldAccess(boolean isSuperAccess) {
        // FieldAccess ::= Primary '.' 'Identifier'
        // FieldAccess ::= 'super' '.' 'Identifier'
        FieldReference fr = new FieldReference(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--]);
        this.identifierLengthPtr--;
        if (isSuperAccess) {
            //considers the fieldReference beginning at the 'super' ....
            fr.sourceStart = this.intStack[this.intPtr--];
            fr.receiver = new SuperReference(fr.sourceStart, this.endPosition);
            pushOnExpressionStack(fr);
        } else {
            //optimize push/pop
            fr.receiver = this.expressionStack[this.expressionPtr];
            //field reference begins at the receiver
            fr.sourceStart = fr.receiver.sourceStart;
            this.expressionStack[this.expressionPtr] = fr;
        }
    }

    protected void consumeFieldDeclaration() {
        // See consumeLocalVariableDeclarationDefaultModifier() in case of change: duplicated code
        // FieldDeclaration ::= Modifiersopt Type VariableDeclarators ';'
        /*
	this.astStack :
	this.expressionStack: Expression Expression ...... Expression
	this.identifierStack : type  identifier identifier ...... identifier
	this.intStack : typeDim      dim        dim               dim
	 ==>
	this.astStack : FieldDeclaration FieldDeclaration ...... FieldDeclaration
	this.expressionStack :
	this.identifierStack :
	this.intStack :

	*/
        int variableDeclaratorsCounter = this.astLengthStack[this.astLengthPtr];
        for (int i = variableDeclaratorsCounter - 1; i >= 0; i--) {
            FieldDeclaration fieldDeclaration = (FieldDeclaration) this.astStack[this.astPtr - i];
            fieldDeclaration.declarationSourceEnd = this.endStatementPosition;
            // semi-colon included
            fieldDeclaration.declarationEnd = this.endStatementPosition;
        }
        updateSourceDeclarationParts(variableDeclaratorsCounter);
        int endPos = flushCommentsDefinedPriorTo(this.endStatementPosition);
        if (endPos != this.endStatementPosition) {
            for (int i = 0; i < variableDeclaratorsCounter; i++) {
                FieldDeclaration fieldDeclaration = (FieldDeclaration) this.astStack[this.astPtr - i];
                fieldDeclaration.declarationSourceEnd = endPos;
            }
        }
        // update the this.astStack, this.astPtr and this.astLengthStack
        int startIndex = this.astPtr - this.variablesCounter[this.nestedType] + 1;
        System.arraycopy(this.astStack, startIndex, this.astStack, startIndex - 1, variableDeclaratorsCounter);
        // remove the type reference
        this.astPtr--;
        this.astLengthStack[--this.astLengthPtr] = variableDeclaratorsCounter;
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = endPos + 1;
            if (this.currentElement.parent != null && this.currentElement instanceof RecoveredField) {
                if (!(this.currentElement instanceof RecoveredInitializer)) {
                    this.currentElement = this.currentElement.parent;
                }
            }
            this.restartRecovery = true;
        }
        this.variablesCounter[this.nestedType] = 0;
    }

    protected void consumeForceNoDiet() {
        // ForceNoDiet ::= $empty
        this.dietInt++;
    }

    protected void consumeForInit() {
        // ForInit ::= StatementExpressionList
        pushOnAstLengthStack(-1);
        this.forStartPosition = 0;
    }

    protected void consumeFormalParameter(boolean isVarArgs) {
        // FormalParameter ::= Modifiersopt Type VariableDeclaratorIdOrThis
        // FormalParameter ::= Modifiersopt Type PushZeroTypeAnnotations '...' VariableDeclaratorIdOrThis
        // FormalParameter ::= Modifiersopt Type @308... TypeAnnotations '...' VariableDeclaratorIdOrThis
        /*
	this.astStack :
	this.identifierStack : type identifier
	this.intStack : dim dim 1||0  // 1 => normal parameter, 0 => this parameter
	 ==>
	this.astStack : Argument
	this.identifierStack :
	this.intStack :
	*/
        NameReference qualifyingNameReference = null;
        // flag pushed in consumeExplicitThisParameter -> 0, consumeVariableDeclaratorIdParameter -> 1
        boolean isReceiver = this.intStack[this.intPtr--] == 0;
        if (isReceiver) {
            qualifyingNameReference = (NameReference) this.expressionStack[this.expressionPtr--];
            this.expressionLengthPtr--;
        }
        this.identifierLengthPtr--;
        char[] identifierName = this.identifierStack[this.identifierPtr];
        long namePositions = this.identifierPositionStack[this.identifierPtr--];
        int extendedDimensions = this.intStack[this.intPtr--];
        Annotation[][] annotationsOnExtendedDimensions = extendedDimensions == 0 ? null : getAnnotationsOnDimensions(extendedDimensions);
        Annotation[] varArgsAnnotations = null;
        int endOfEllipsis = 0;
        int length;
        if (isVarArgs) {
            endOfEllipsis = this.intStack[this.intPtr--];
            if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
                System.arraycopy(this.typeAnnotationStack, (this.typeAnnotationPtr -= length) + 1, varArgsAnnotations = new Annotation[length], 0, length);
            }
        }
        int firstDimensions = this.intStack[this.intPtr--];
        TypeReference type = getTypeReference(firstDimensions);
        if (isVarArgs || extendedDimensions != 0) {
            if (isVarArgs) {
                type = augmentTypeWithAdditionalDimensions(type, 1, varArgsAnnotations != null ? new Annotation[][] { varArgsAnnotations } : null, true);
            }
            if (extendedDimensions != 0) {
                type = augmentTypeWithAdditionalDimensions(type, extendedDimensions, annotationsOnExtendedDimensions, false);
            }
            type.sourceEnd = type.isParameterizedTypeReference() ? this.endStatementPosition : this.endPosition;
        }
        if (isVarArgs) {
            if (extendedDimensions == 0) {
                type.sourceEnd = endOfEllipsis;
            }
            // set isVarArgs
            type.bits |= ASTNode.IsVarArgs;
        }
        int modifierPositions = this.intStack[this.intPtr--];
        Argument arg;
        if (isReceiver) {
            arg = new Receiver(identifierName, namePositions, type, qualifyingNameReference, this.intStack[this.intPtr--] & ~ClassFileConstants.AccDeprecated);
        } else {
            arg = new Argument(identifierName, namePositions, type, // modifiers
            this.intStack[this.intPtr--] & ~ClassFileConstants.AccDeprecated);
        }
        arg.declarationSourceStart = modifierPositions;
        arg.bits |= (type.bits & ASTNode.HasTypeAnnotations);
        // consume annotations
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, arg.annotations = new Annotation[length], 0, length);
            arg.bits |= ASTNode.HasTypeAnnotations;
            RecoveredType currentRecoveryType = this.currentRecoveryType();
            if (currentRecoveryType != null)
                currentRecoveryType.annotationsConsumed(arg.annotations);
        }
        pushOnAstStack(arg);
        /* if incomplete method header, this.listLength counter will not have been reset,
		indicating that some arguments are available on the stack */
        this.listLength++;
        if (isVarArgs) {
            if (!this.statementRecoveryActivated && this.options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
                problemReporter().invalidUsageOfVarargs(arg);
            } else if (!this.statementRecoveryActivated && extendedDimensions > 0) {
                problemReporter().illegalExtendedDimensions(arg);
            }
        }
    }

    protected Annotation[][] getAnnotationsOnDimensions(int dimensionsCount) {
        Annotation[][] dimensionsAnnotations = null;
        if (dimensionsCount > 0) {
            for (int i = 0; i < dimensionsCount; i++) {
                Annotation[] annotations = null;
                int length;
                if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
                    System.arraycopy(this.typeAnnotationStack, (this.typeAnnotationPtr -= length) + 1, annotations = new Annotation[length], 0, length);
                    if (dimensionsAnnotations == null) {
                        dimensionsAnnotations = new Annotation[dimensionsCount][];
                    }
                    dimensionsAnnotations[dimensionsCount - i - 1] = annotations;
                }
            }
        }
        return dimensionsAnnotations;
    }

    protected void consumeFormalParameterList() {
        // FormalParameterList ::= FormalParameterList ',' FormalParameter
        // TypeElidedFormalParameterList ::= TypeElidedFormalParameterList ',' TypeElidedFormalParameter
        optimizedConcatNodeLists();
    }

    protected void consumeFormalParameterListopt() {
        // FormalParameterListopt ::= $empty
        pushOnAstLengthStack(0);
    }

    protected void consumeGenericType() {
    // GenericType ::= ClassOrInterface TypeArguments
    // nothing to do
    // Will be consume by a getTypeReference call
    }

    protected void consumeGenericTypeArrayType() {
    // nothing to do
    // Will be consume by a getTypeReference call
    }

    protected void consumeGenericTypeNameArrayType() {
    // nothing to do
    // Will be consume by a getTypeReference call
    }

    protected void consumeGenericTypeWithDiamond() {
        // GenericType ::= ClassOrInterface '<' '>'
        // zero type arguments == <>
        pushOnGenericsLengthStack(-1);
        concatGenericsLists();
        // pop the null dimension pushed in by consumeReferenceType, as we have no type between <>, getTypeReference won't kick in 
        this.intPtr--;
    }

    protected void consumeImportDeclaration() {
        // SingleTypeImportDeclaration ::= SingleTypeImportDeclarationName ';'
        ImportReference impt = (ImportReference) this.astStack[this.astPtr];
        // flush annotations defined prior to import statements
        impt.declarationEnd = this.endStatementPosition;
        impt.declarationSourceEnd = flushCommentsDefinedPriorTo(impt.declarationSourceEnd);
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.add(impt, 0);
            this.lastIgnoredToken = -1;
            this.restartRecovery = true;
        // used to avoid branching back into the regular automaton
        }
    }

    protected void consumeImportDeclarations() {
        // ImportDeclarations ::= ImportDeclarations ImportDeclaration
        optimizedConcatNodeLists();
    }

    protected void consumeInsideCastExpression() {
    // InsideCastExpression ::= $empty
    }

    protected void consumeInsideCastExpressionLL1() {
        // InsideCastExpressionLL1 ::= $empty
        // handle type arguments
        pushOnGenericsLengthStack(0);
        pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        pushOnExpressionStack(getTypeReference(0));
    }

    protected void consumeInsideCastExpressionLL1WithBounds() {
        // InsideCastExpressionLL1WithBounds ::= $empty
        int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        TypeReference[] bounds = new TypeReference[additionalBoundsLength + 1];
        this.genericsPtr -= additionalBoundsLength;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 1, additionalBoundsLength);
        // handle type arguments
        pushOnGenericsLengthStack(0);
        pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        bounds[0] = getTypeReference(0);
        for (int i = 0; i <= additionalBoundsLength; i++) {
            pushOnExpressionStack(bounds[i]);
            if (i > 0)
                this.expressionLengthStack[--this.expressionLengthPtr]++;
        }
    }

    protected void consumeInsideCastExpressionWithQualifiedGenerics() {
    // InsideCastExpressionWithQualifiedGenerics ::= $empty
    }

    protected void consumeInstanceOfExpression() {
        // RelationalExpression ::= RelationalExpression 'instanceof' ReferenceType
        //optimize the push/pop
        //by construction, no base type may be used in getTypeReference
        Expression exp;
        this.expressionStack[this.expressionPtr] = exp = new InstanceOfExpression(this.expressionStack[this.expressionPtr], getTypeReference(this.intStack[this.intPtr--]));
        if (exp.sourceEnd == 0) {
            //array on base type....
            exp.sourceEnd = this.scanner.startPosition - 1;
        }
    //the scanner is on the next token already....
    }

    protected void consumeInstanceOfExpressionWithName() {
        // RelationalExpression_NotName ::= Name instanceof ReferenceType
        //optimize the push/pop
        //by construction, no base type may be used in getTypeReference
        TypeReference reference = getTypeReference(this.intStack[this.intPtr--]);
        pushOnExpressionStack(getUnspecifiedReferenceOptimized());
        Expression exp;
        this.expressionStack[this.expressionPtr] = exp = new InstanceOfExpression(this.expressionStack[this.expressionPtr], reference);
        if (exp.sourceEnd == 0) {
            //array on base type....
            exp.sourceEnd = this.scanner.startPosition - 1;
        }
    //the scanner is on the next token already....
    }

    protected void consumeInterfaceDeclaration() {
        // see consumeClassDeclaration in case of changes: duplicated code
        // InterfaceDeclaration ::= InterfaceHeader InterfaceBody
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            //there are length declarations
            //dispatch.....according to the type of the declarations
            dispatchDeclarationInto(length);
        }
        TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
        //convert constructor that do not have the type's name into methods
        typeDecl.checkConstructors(this);
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=212713, 
        // reject initializers that have been tolerated by the grammar.
        FieldDeclaration[] fields = typeDecl.fields;
        int fieldCount = fields == null ? 0 : fields.length;
        for (int i = 0; i < fieldCount; i++) {
            FieldDeclaration field = fields[i];
            if (field instanceof Initializer) {
                problemReporter().interfaceCannotHaveInitializers(typeDecl.name, field);
            }
        }
        //always add <clinit> (will be remove at code gen time if empty)
        if (this.scanner.containsAssertKeyword) {
            typeDecl.bits |= ASTNode.ContainsAssertion;
        }
        typeDecl.addClinit();
        typeDecl.bodyEnd = this.endStatementPosition;
        if (length == 0 && !containsComment(typeDecl.bodyStart, typeDecl.bodyEnd)) {
            typeDecl.bits |= ASTNode.UndocumentedEmptyBlock;
        }
        typeDecl.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeInterfaceHeader() {
        // InterfaceHeader ::= InterfaceHeaderName InterfaceHeaderExtendsopt
        TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
        if (this.currentToken == TokenNameLBRACE) {
            typeDecl.bodyStart = this.scanner.currentPosition;
        }
        if (this.currentElement != null) {
            // used to avoid branching back into the regular automaton
            this.restartRecovery = true;
        }
        // flush the comments related to the interface header
        this.scanner.commentPtr = -1;
    }

    protected void consumeInterfaceHeaderExtends() {
        // InterfaceHeaderExtends ::= 'extends' InterfaceTypeList
        int length = this.astLengthStack[this.astLengthPtr--];
        //super interfaces
        this.astPtr -= length;
        TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
        System.arraycopy(this.astStack, this.astPtr + 1, typeDecl.superInterfaces = new TypeReference[length], 0, length);
        TypeReference[] superinterfaces = typeDecl.superInterfaces;
        for (int i = 0, max = superinterfaces.length; i < max; i++) {
            TypeReference typeReference = superinterfaces[i];
            typeDecl.bits |= (typeReference.bits & ASTNode.HasTypeAnnotations);
            typeReference.bits |= ASTNode.IsSuperType;
        }
        typeDecl.bodyStart = typeDecl.superInterfaces[length - 1].sourceEnd + 1;
        // reset after having read super-interfaces
        this.listLength = 0;
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
        }
    }

    protected void consumeInterfaceHeaderName1() {
        // InterfaceHeaderName ::= Modifiersopt 'interface' 'Identifier'
        TypeDeclaration typeDecl = new TypeDeclaration(this.compilationUnit.compilationResult);
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                typeDecl.bits |= ASTNode.IsMemberType;
            }
        } else {
            // Record that the block has a declaration for local types
            typeDecl.bits |= ASTNode.IsLocalType;
            markEnclosingMemberWithLocalType();
            blockReal();
        }
        //highlight the name of the type
        long pos = this.identifierPositionStack[this.identifierPtr];
        typeDecl.sourceEnd = (int) pos;
        typeDecl.sourceStart = (int) (pos >>> 32);
        typeDecl.name = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        //compute the declaration source too
        // 'class' and 'interface' push two int positions: the beginning of the class token and its end.
        // we want to keep the beginning position but get rid of the end position
        // it is only used for the ClassLiteralAccess positions.
        typeDecl.declarationSourceStart = this.intStack[this.intPtr--];
        // remove the end position of the class token
        this.intPtr--;
        typeDecl.modifiersSourceStart = this.intStack[this.intPtr--];
        typeDecl.modifiers = this.intStack[this.intPtr--] | ClassFileConstants.AccInterface;
        if (typeDecl.modifiersSourceStart >= 0) {
            typeDecl.declarationSourceStart = typeDecl.modifiersSourceStart;
        }
        // Store secondary info
        if ((typeDecl.bits & ASTNode.IsMemberType) == 0 && (typeDecl.bits & ASTNode.IsLocalType) == 0) {
            if (this.compilationUnit != null && !CharOperation.equals(typeDecl.name, this.compilationUnit.getMainTypeName())) {
                typeDecl.bits |= ASTNode.IsSecondaryType;
            }
        }
        // consume annotations
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, typeDecl.annotations = new Annotation[length], 0, length);
        }
        typeDecl.bodyStart = typeDecl.sourceEnd + 1;
        pushOnAstStack(typeDecl);
        // will be updated when reading super-interfaces
        this.listLength = 0;
        // recovery
        if (// is recovering
        this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
            this.currentElement = this.currentElement.add(typeDecl, 0);
            this.lastIgnoredToken = -1;
        }
        // javadoc
        typeDecl.javadoc = this.javadoc;
        this.javadoc = null;
    }

    protected void consumeInterfaceMemberDeclarations() {
        // InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
        concatNodeLists();
    }

    protected void consumeInterfaceMemberDeclarationsopt() {
        // InterfaceMemberDeclarationsopt ::= NestedType InterfaceMemberDeclarations
        this.nestedType--;
    }

    protected void consumeInterfaceType() {
        // InterfaceType ::= ClassOrInterfaceType
        pushOnAstStack(getTypeReference(0));
        /* if incomplete type header, this.listLength counter will not have been reset,
		indicating that some interfaces are available on the stack */
        this.listLength++;
    }

    protected void consumeInterfaceTypeList() {
        // InterfaceTypeList ::= InterfaceTypeList ',' InterfaceType
        optimizedConcatNodeLists();
    }

    protected void consumeInternalCompilationUnit() {
        // InternalCompilationUnit ::= ImportDeclarations ReduceImports
        if (this.compilationUnit.isPackageInfo()) {
            this.compilationUnit.types = new TypeDeclaration[1];
            this.compilationUnit.createPackageInfoType();
        }
    }

    protected void consumeInternalCompilationUnitWithTypes() {
        // InternalCompilationUnit ::= PackageDeclaration ImportDeclarations ReduceImports TypeDeclarations
        // InternalCompilationUnit ::= PackageDeclaration TypeDeclarations
        // InternalCompilationUnit ::= TypeDeclarations
        // InternalCompilationUnit ::= ImportDeclarations ReduceImports TypeDeclarations
        // consume type declarations
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            if (this.compilationUnit.isPackageInfo()) {
                this.compilationUnit.types = new TypeDeclaration[length + 1];
                this.astPtr -= length;
                System.arraycopy(this.astStack, this.astPtr + 1, this.compilationUnit.types, 1, length);
                this.compilationUnit.createPackageInfoType();
            } else {
                this.compilationUnit.types = new TypeDeclaration[length];
                this.astPtr -= length;
                System.arraycopy(this.astStack, this.astPtr + 1, this.compilationUnit.types, 0, length);
            }
        }
    }

    protected void consumeInvalidAnnotationTypeDeclaration() {
        // BlockStatement ::= AnnotationTypeDeclaration
        TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
        if (!this.statementRecoveryActivated)
            problemReporter().illegalLocalTypeDeclaration(typeDecl);
        // remove the ast node created in interface header
        this.astPtr--;
        pushOnAstLengthStack(-1);
        concatNodeLists();
    }

    protected void consumeInvalidConstructorDeclaration() {
        // ConstructorDeclaration ::= ConstructorHeader ';'
        // now we know that the top of stack is a constructorDeclaration
        ConstructorDeclaration cd = (ConstructorDeclaration) this.astStack[this.astPtr];
        // position just before the trailing semi-colon
        cd.bodyEnd = this.endPosition;
        cd.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
        // report the problem and continue the parsing - narrowing the problem onto the method
        // remember semi-colon body
        cd.modifiers |= ExtraCompilerModifiers.AccSemicolonBody;
    }

    protected void consumeInvalidConstructorDeclaration(boolean hasBody) {
        /*
	this.astStack : modifiers arguments throws statements
	this.identifierStack : name
	 ==>
	this.astStack : MethodDeclaration
	this.identifierStack :
	*/
        if (hasBody) {
            // pop the position of the {  (body of the method) pushed in block decl
            this.intPtr--;
        }
        //statements
        if (hasBody) {
            this.realBlockPtr--;
        }
        int length;
        if (hasBody && ((length = this.astLengthStack[this.astLengthPtr--]) != 0)) {
            this.astPtr -= length;
        }
        ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration) this.astStack[this.astPtr];
        constructorDeclaration.bodyEnd = this.endStatementPosition;
        constructorDeclaration.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
        if (!hasBody) {
            constructorDeclaration.modifiers |= ExtraCompilerModifiers.AccSemicolonBody;
        }
    }

    protected void consumeInvalidEnumDeclaration() {
        // BlockStatement ::= EnumDeclaration
        TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
        if (!this.statementRecoveryActivated)
            problemReporter().illegalLocalTypeDeclaration(typeDecl);
        // remove the ast node created in interface header
        this.astPtr--;
        pushOnAstLengthStack(-1);
        concatNodeLists();
    }

    protected void consumeInvalidInterfaceDeclaration() {
        // BlockStatement ::= InvalidInterfaceDeclaration
        //InterfaceDeclaration ::= Modifiersopt 'interface' 'Identifier' ExtendsInterfacesopt InterfaceHeader InterfaceBody
        TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
        if (!this.statementRecoveryActivated)
            problemReporter().illegalLocalTypeDeclaration(typeDecl);
        // remove the ast node created in interface header
        this.astPtr--;
        pushOnAstLengthStack(-1);
        concatNodeLists();
    }

    protected void consumeInterfaceMethodDeclaration(boolean hasSemicolonBody) {
        // InterfaceMemberDeclaration ::= DefaultMethodHeader MethodBody
        // InterfaceMemberDeclaration ::= MethodHeader MethodBody
        // -- the next rule is illegal but allows to give a more canonical error message from inside consumeInterfaceMethodDeclaration(): 
        // InterfaceMemberDeclaration ::= DefaultMethodHeader ';'
        /*
	this.astStack : modifiers arguments throws statements
	this.identifierStack : type name
	this.intStack : dim dim dim
	 ==>
	this.astStack : MethodDeclaration
	this.identifierStack :
	this.intStack :
	*/
        int explicitDeclarations = 0;
        Statement[] statements = null;
        if (!hasSemicolonBody) {
            // pop the position of the {  (body of the method) pushed in block decl
            this.intPtr--;
            this.intPtr--;
            explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
            //statements
            int length;
            if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
                if (this.options.ignoreMethodBodies) {
                    this.astPtr -= length;
                } else {
                    System.arraycopy(this.astStack, (this.astPtr -= length) + 1, statements = new Statement[length], 0, length);
                }
            }
        }
        //watch for } that could be given as a unicode ! ( u007D is '}' )
        MethodDeclaration md = (MethodDeclaration) this.astStack[this.astPtr];
        md.statements = statements;
        md.explicitDeclarations = explicitDeclarations;
        md.bodyEnd = this.endPosition;
        md.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
        boolean isDefault = (md.modifiers & ExtraCompilerModifiers.AccDefaultMethod) != 0;
        boolean isStatic = (md.modifiers & ClassFileConstants.AccStatic) != 0;
        boolean bodyAllowed = isDefault || isStatic;
        if (this.parsingJava8Plus) {
            if (bodyAllowed && hasSemicolonBody) {
                // avoid complaints regarding undocumented empty body
                md.modifiers |= ExtraCompilerModifiers.AccSemicolonBody;
            }
        } else {
            if (isDefault)
                problemReporter().defaultMethodsNotBelow18(md);
            if (isStatic)
                problemReporter().staticInterfaceMethodsNotBelow18(md);
        }
        if (!bodyAllowed && !this.statementRecoveryActivated && !hasSemicolonBody) {
            problemReporter().abstractMethodNeedingNoBody(md);
        }
    }

    protected void consumeLabel() {
    // Do nothing
    }

    protected void consumeLeftParen() {
        // PushLPAREN ::= '('
        pushOnIntStack(this.lParenPos);
    }

    protected void consumeLocalVariableDeclaration() {
        // LocalVariableDeclaration ::= Modifiers Type VariableDeclarators ';'
        /*
	this.astStack :
	this.expressionStack: Expression Expression ...... Expression
	this.identifierStack : type  identifier identifier ...... identifier
	this.intStack : typeDim      dim        dim               dim
	 ==>
	this.astStack : FieldDeclaration FieldDeclaration ...... FieldDeclaration
	this.expressionStack :
	this.identifierStack :
	this.intStack :

	*/
        int variableDeclaratorsCounter = this.astLengthStack[this.astLengthPtr];
        // update the this.astStack, this.astPtr and this.astLengthStack
        int startIndex = this.astPtr - this.variablesCounter[this.nestedType] + 1;
        System.arraycopy(this.astStack, startIndex, this.astStack, startIndex - 1, variableDeclaratorsCounter);
        // remove the type reference
        this.astPtr--;
        this.astLengthStack[--this.astLengthPtr] = variableDeclaratorsCounter;
        this.variablesCounter[this.nestedType] = 0;
        this.forStartPosition = 0;
    }

    protected void consumeLocalVariableDeclarationStatement() {
        int variableDeclaratorsCounter = this.astLengthStack[this.astLengthPtr];
        if (variableDeclaratorsCounter == 1) {
            LocalDeclaration localDeclaration = (LocalDeclaration) this.astStack[this.astPtr];
            if (localDeclaration.isRecoveredFromLoneIdentifier()) {
                // https://bugs.eclipse.org/bugs/show_bug.cgi?id=430336, [1.8][compiler] Bad syntax error recovery: Lonely identifier should be variable name, not type
                // Mutate foo $missing; into foo = $missing$; 
                Expression left;
                if (localDeclaration.type instanceof QualifiedTypeReference) {
                    QualifiedTypeReference qtr = (QualifiedTypeReference) localDeclaration.type;
                    left = new QualifiedNameReference(qtr.tokens, qtr.sourcePositions, 0, 0);
                } else {
                    left = new SingleNameReference(localDeclaration.type.getLastToken(), 0L);
                }
                left.sourceStart = localDeclaration.type.sourceStart;
                left.sourceEnd = localDeclaration.type.sourceEnd;
                Expression right = new SingleNameReference(localDeclaration.name, 0L);
                right.sourceStart = localDeclaration.sourceStart;
                right.sourceEnd = localDeclaration.sourceEnd;
                Assignment assignment = new Assignment(left, right, 0);
                int end = this.endStatementPosition;
                assignment.sourceEnd = (end == localDeclaration.sourceEnd) ? ++end : end;
                assignment.statementEnd = end;
                this.astStack[this.astPtr] = assignment;
                // also massage recovery scanner data.
                if (this.recoveryScanner != null) {
                    RecoveryScannerData data = this.recoveryScanner.getData();
                    int position = data.insertedTokensPtr;
                    while (position > 0) {
                        if (data.insertedTokensPosition[position] != data.insertedTokensPosition[position - 1])
                            break;
                        position--;
                    }
                    if (position >= 0)
                        this.recoveryScanner.insertTokenAhead(TerminalTokens.TokenNameEQUAL, position);
                }
                if (this.currentElement != null) {
                    this.lastCheckPoint = assignment.sourceEnd + 1;
                    this.currentElement = this.currentElement.add(assignment, 0);
                }
                return;
            }
        }
        // LocalVariableDeclarationStatement ::= LocalVariableDeclaration ';'
        // see blockReal in case of change: duplicated code
        // increment the amount of declared variables for this block
        this.realBlockStack[this.realBlockPtr]++;
        // update source end to include the semi-colon
        for (int i = variableDeclaratorsCounter - 1; i >= 0; i--) {
            LocalDeclaration localDeclaration = (LocalDeclaration) this.astStack[this.astPtr - i];
            localDeclaration.declarationSourceEnd = this.endStatementPosition;
            // semi-colon included
            localDeclaration.declarationEnd = this.endStatementPosition;
        }
    }

    protected void consumeMarkerAnnotation(boolean isTypeAnnotation) {
        // MarkerAnnotation ::= AnnotationName
        // MarkerTypeAnnotation ::= TypeAnnotationName
        MarkerAnnotation markerAnnotation = null;
        int oldIndex = this.identifierPtr;
        TypeReference typeReference = getAnnotationType();
        markerAnnotation = new MarkerAnnotation(typeReference, this.intStack[this.intPtr--]);
        markerAnnotation.declarationSourceEnd = markerAnnotation.sourceEnd;
        if (isTypeAnnotation) {
            pushOnTypeAnnotationStack(markerAnnotation);
        } else {
            pushOnExpressionStack(markerAnnotation);
        }
        if (!this.statementRecoveryActivated && this.options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            problemReporter().invalidUsageOfAnnotation(markerAnnotation);
        }
        this.recordStringLiterals = true;
        if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
            this.currentElement = ((RecoveredAnnotation) this.currentElement).addAnnotation(markerAnnotation, oldIndex);
        }
    }

    protected void consumeMemberValueArrayInitializer() {
        // MemberValueArrayInitializer ::= '{' MemberValues ',' '}'
        // MemberValueArrayInitializer ::= '{' MemberValues '}'
        arrayInitializer(this.expressionLengthStack[this.expressionLengthPtr--]);
    }

    protected void consumeMemberValueAsName() {
        pushOnExpressionStack(getUnspecifiedReferenceOptimized());
    }

    protected void consumeMemberValuePair() {
        // MemberValuePair ::= SimpleName '=' MemberValue
        char[] simpleName = this.identifierStack[this.identifierPtr];
        long position = this.identifierPositionStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        int end = (int) position;
        int start = (int) (position >>> 32);
        Expression value = this.expressionStack[this.expressionPtr--];
        this.expressionLengthPtr--;
        MemberValuePair memberValuePair = new MemberValuePair(simpleName, start, end, value);
        pushOnAstStack(memberValuePair);
        if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
            RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation) this.currentElement;
            recoveredAnnotation.setKind(RecoveredAnnotation.NORMAL);
        }
    }

    protected void consumeMemberValuePairs() {
        // MemberValuePairs ::= MemberValuePairs ',' MemberValuePair
        concatNodeLists();
    }

    protected void consumeMemberValues() {
        // MemberValues ::= MemberValues ',' MemberValue
        concatExpressionLists();
    }

    protected void consumeMethodBody() {
        // MethodBody ::= NestedMethod '{' BlockStatementsopt '}'
        this.nestedMethod[this.nestedType]--;
    }

    protected void consumeMethodDeclaration(boolean isNotAbstract, boolean isDefaultMethod) {
        // MethodDeclaration ::= MethodHeader MethodBody
        // AbstractMethodDeclaration ::= MethodHeader ';'
        /*
	this.astStack : modifiers arguments throws statements
	this.identifierStack : type name
	this.intStack : dim dim dim
	 ==>
	this.astStack : MethodDeclaration
	this.identifierStack :
	this.intStack :
	*/
        int length;
        if (isNotAbstract) {
            // pop the position of the {  (body of the method) pushed in block decl
            this.intPtr--;
            this.intPtr--;
        }
        int explicitDeclarations = 0;
        Statement[] statements = null;
        if (isNotAbstract) {
            //statements
            explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
            if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
                if (this.options.ignoreMethodBodies) {
                    this.astPtr -= length;
                } else {
                    System.arraycopy(this.astStack, (this.astPtr -= length) + 1, statements = new Statement[length], 0, length);
                }
            }
        }
        // now we know that we have a method declaration at the top of the ast stack
        MethodDeclaration md = (MethodDeclaration) this.astStack[this.astPtr];
        md.statements = statements;
        md.explicitDeclarations = explicitDeclarations;
        // is a body when we reduce the method header
        if (//remember the fact that the method has a semicolon body
        !isNotAbstract) {
            md.modifiers |= ExtraCompilerModifiers.AccSemicolonBody;
        } else if (!(this.diet && this.dietInt == 0) && statements == null && !containsComment(md.bodyStart, this.endPosition)) {
            md.bits |= ASTNode.UndocumentedEmptyBlock;
        }
        // store the this.endPosition (position just before the '}') in case there is
        // a trailing comment behind the end of the method
        md.bodyEnd = this.endPosition;
        md.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
        if (isDefaultMethod && !this.tolerateDefaultClassMethods) {
            if (this.options.sourceLevel >= ClassFileConstants.JDK1_8) {
                problemReporter().defaultModifierIllegallySpecified(md.sourceStart, md.sourceEnd);
            } else {
                problemReporter().illegalModifierForMethod(md);
            }
        }
    }

    protected void consumeMethodHeader() {
        // MethodHeader ::= MethodHeaderName MethodHeaderParameters MethodHeaderExtendedDims ThrowsClauseopt
        // AnnotationMethodHeader ::= AnnotationMethodHeaderName FormalParameterListopt MethodHeaderRightParen MethodHeaderExtendedDims AnnotationMethodHeaderDefaultValueopt
        // RecoveryMethodHeader ::= RecoveryMethodHeaderName FormalParameterListopt MethodHeaderRightParen MethodHeaderExtendedDims AnnotationMethodHeaderDefaultValueopt
        // RecoveryMethodHeader ::= RecoveryMethodHeaderName FormalParameterListopt MethodHeaderRightParen MethodHeaderExtendedDims MethodHeaderThrowsClause
        // retrieve end position of method declarator
        AbstractMethodDeclaration method = (AbstractMethodDeclaration) this.astStack[this.astPtr];
        if (this.currentToken == TokenNameLBRACE) {
            method.bodyStart = this.scanner.currentPosition;
        }
        // recovery
        if (this.currentElement != null) {
            //		} else
            if (this.currentToken == TokenNameSEMICOLON) /*&& !method.isAnnotationMethod()*/
            {
                method.modifiers |= ExtraCompilerModifiers.AccSemicolonBody;
                method.declarationSourceEnd = this.scanner.currentPosition - 1;
                method.bodyEnd = this.scanner.currentPosition - 1;
                if (this.currentElement.parseTree() == method && this.currentElement.parent != null) {
                    this.currentElement = this.currentElement.parent;
                }
            } else if (this.currentToken == TokenNameLBRACE) {
                if (this.currentElement instanceof RecoveredMethod && ((RecoveredMethod) this.currentElement).methodDeclaration != method) {
                    this.ignoreNextOpeningBrace = true;
                    this.currentElement.bracketBalance++;
                }
            }
            // used to avoid branching back into the regular automaton
            this.restartRecovery = true;
        }
    }

    protected void consumeMethodHeaderDefaultValue() {
        // MethodHeaderDefaultValue ::= DefaultValue
        MethodDeclaration md = (MethodDeclaration) this.astStack[this.astPtr];
        int length = this.expressionLengthStack[this.expressionLengthPtr--];
        if (length == 1) {
            // we get rid of the position of the default keyword
            this.intPtr--;
            // we get rid of the position of the default keyword
            this.intPtr--;
            if (md.isAnnotationMethod()) {
                ((AnnotationMethodDeclaration) md).defaultValue = this.expressionStack[this.expressionPtr];
                md.modifiers |= ClassFileConstants.AccAnnotationDefault;
            }
            this.expressionPtr--;
            this.recordStringLiterals = true;
        }
        if (this.currentElement != null) {
            if (md.isAnnotationMethod()) {
                this.currentElement.updateSourceEndIfNecessary(((AnnotationMethodDeclaration) md).defaultValue.sourceEnd);
            }
        }
    }

    protected void consumeMethodHeaderExtendedDims() {
        // MethodHeaderExtendedDims ::= Dimsopt
        // now we update the returnType of the method
        MethodDeclaration md = (MethodDeclaration) this.astStack[this.astPtr];
        int extendedDimensions = this.intStack[this.intPtr--];
        if (md.isAnnotationMethod()) {
            ((AnnotationMethodDeclaration) md).extendedDimensions = extendedDimensions;
        }
        if (extendedDimensions != 0) {
            md.sourceEnd = this.endPosition;
            md.returnType = augmentTypeWithAdditionalDimensions(md.returnType, extendedDimensions, getAnnotationsOnDimensions(extendedDimensions), false);
            md.bits |= (md.returnType.bits & ASTNode.HasTypeAnnotations);
            if (this.currentToken == TokenNameLBRACE) {
                md.bodyStart = this.endPosition + 1;
            }
            // recovery
            if (this.currentElement != null) {
                this.lastCheckPoint = md.bodyStart;
            }
        }
    }

    protected void consumeMethodHeaderName(boolean isAnnotationMethod) {
        // MethodHeaderName ::= Modifiersopt Type 'Identifier' '('
        // AnnotationMethodHeaderName ::= Modifiersopt Type 'Identifier' '('
        // RecoveryMethodHeaderName ::= Modifiersopt Type 'Identifier' '('
        MethodDeclaration md = null;
        if (isAnnotationMethod) {
            md = new AnnotationMethodDeclaration(this.compilationUnit.compilationResult);
            this.recordStringLiterals = false;
        } else {
            md = new MethodDeclaration(this.compilationUnit.compilationResult);
        }
        //name
        md.selector = this.identifierStack[this.identifierPtr];
        long selectorSource = this.identifierPositionStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        //type
        md.returnType = getTypeReference(this.intStack[this.intPtr--]);
        md.bits |= (md.returnType.bits & ASTNode.HasTypeAnnotations);
        //modifiers
        md.declarationSourceStart = this.intStack[this.intPtr--];
        md.modifiers = this.intStack[this.intPtr--];
        // consume annotations
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, md.annotations = new Annotation[length], 0, length);
        }
        // javadoc
        md.javadoc = this.javadoc;
        this.javadoc = null;
        //highlight starts at selector start
        md.sourceStart = (int) (selectorSource >>> 32);
        pushOnAstStack(md);
        md.sourceEnd = this.lParenPos;
        md.bodyStart = this.lParenPos + 1;
        // initialize this.listLength before reading parameters/throws
        this.listLength = 0;
        // recovery
        if (this.currentElement != null) {
            if (this.currentElement instanceof RecoveredType || //|| md.modifiers != 0
            (Util.getLineNumber(md.returnType.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr) == Util.getLineNumber(md.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr))) {
                this.lastCheckPoint = md.bodyStart;
                this.currentElement = this.currentElement.add(md, 0);
                this.lastIgnoredToken = -1;
            } else {
                this.lastCheckPoint = md.sourceStart;
                this.restartRecovery = true;
            }
        }
    }

    protected void consumeMethodHeaderNameWithTypeParameters(boolean isAnnotationMethod) {
        // MethodHeaderName ::= Modifiersopt TypeParameters Type 'Identifier' '('
        // AnnotationMethodHeaderName ::= Modifiersopt TypeParameters Type 'Identifier' '('
        // RecoveryMethodHeaderName ::= Modifiersopt TypeParameters Type 'Identifier' '('
        MethodDeclaration md = null;
        if (isAnnotationMethod) {
            md = new AnnotationMethodDeclaration(this.compilationUnit.compilationResult);
            this.recordStringLiterals = false;
        } else {
            md = new MethodDeclaration(this.compilationUnit.compilationResult);
        }
        //name
        md.selector = this.identifierStack[this.identifierPtr];
        long selectorSource = this.identifierPositionStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        //type
        TypeReference returnType = getTypeReference(this.intStack[this.intPtr--]);
        if (isAnnotationMethod)
            rejectIllegalLeadingTypeAnnotations(returnType);
        md.returnType = returnType;
        md.bits |= (returnType.bits & ASTNode.HasTypeAnnotations);
        // consume type parameters
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, md.typeParameters = new TypeParameter[length], 0, length);
        //modifiers
        md.declarationSourceStart = this.intStack[this.intPtr--];
        md.modifiers = this.intStack[this.intPtr--];
        // consume annotations
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, md.annotations = new Annotation[length], 0, length);
        }
        // javadoc
        md.javadoc = this.javadoc;
        this.javadoc = null;
        //highlight starts at selector start
        md.sourceStart = (int) (selectorSource >>> 32);
        pushOnAstStack(md);
        md.sourceEnd = this.lParenPos;
        md.bodyStart = this.lParenPos + 1;
        // initialize this.listLength before reading parameters/throws
        this.listLength = 0;
        // recovery
        if (this.currentElement != null) {
            boolean isType;
            if ((isType = this.currentElement instanceof RecoveredType) || //|| md.modifiers != 0
            (Util.getLineNumber(md.returnType.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr) == Util.getLineNumber(md.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr))) {
                if (isType) {
                    ((RecoveredType) this.currentElement).pendingTypeParameters = null;
                }
                this.lastCheckPoint = md.bodyStart;
                this.currentElement = this.currentElement.add(md, 0);
                this.lastIgnoredToken = -1;
            } else {
                this.lastCheckPoint = md.sourceStart;
                this.restartRecovery = true;
            }
        }
    }

    protected void consumeMethodHeaderRightParen() {
        // MethodHeaderParameters ::= FormalParameterListopt ')'
        int length = this.astLengthStack[this.astLengthPtr--];
        this.astPtr -= length;
        AbstractMethodDeclaration md = (AbstractMethodDeclaration) this.astStack[this.astPtr];
        md.sourceEnd = this.rParenPos;
        //arguments
        if (length != 0) {
            Argument arg = (Argument) this.astStack[this.astPtr + 1];
            if (arg.isReceiver()) {
                md.receiver = (Receiver) arg;
                if (length > 1) {
                    System.arraycopy(this.astStack, this.astPtr + 2, md.arguments = new Argument[length - 1], 0, length - 1);
                }
                // Receiver annotations can only be type annotations; move to the type
                Annotation[] annotations = arg.annotations;
                if (annotations != null && annotations.length > 0) {
                    // The code assumes that receiver.type.annotations[0] will be null/empty
                    TypeReference type = arg.type;
                    if (type.annotations == null) {
                        type.bits |= ASTNode.HasTypeAnnotations;
                        type.annotations = new Annotation[type.getAnnotatableLevels()][];
                        md.bits |= ASTNode.HasTypeAnnotations;
                    }
                    type.annotations[0] = annotations;
                    int annotationSourceStart = annotations[0].sourceStart;
                    if (type.sourceStart > annotationSourceStart)
                        type.sourceStart = annotationSourceStart;
                    arg.annotations = null;
                }
                md.bits |= (arg.type.bits & ASTNode.HasTypeAnnotations);
            } else {
                System.arraycopy(this.astStack, this.astPtr + 1, md.arguments = new Argument[length], 0, length);
                for (int i = 0, max = md.arguments.length; i < max; i++) {
                    if ((md.arguments[i].bits & ASTNode.HasTypeAnnotations) != 0) {
                        md.bits |= ASTNode.HasTypeAnnotations;
                        break;
                    }
                }
            }
        }
        md.bodyStart = this.rParenPos + 1;
        // reset this.listLength after having read all parameters
        this.listLength = 0;
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = md.bodyStart;
            if (this.currentElement.parseTree() == md)
                return;
            // might not have been attached yet - in some constructor scenarii
            if (md.isConstructor()) {
                if ((length != 0) || (this.currentToken == TokenNameLBRACE) || (this.currentToken == TokenNamethrows)) {
                    this.currentElement = this.currentElement.add(md, 0);
                    this.lastIgnoredToken = -1;
                }
            }
        }
    }

    protected void consumeMethodHeaderThrowsClause() {
        // MethodHeaderThrowsClause ::= 'throws' ClassTypeList
        int length = this.astLengthStack[this.astLengthPtr--];
        this.astPtr -= length;
        AbstractMethodDeclaration md = (AbstractMethodDeclaration) this.astStack[this.astPtr];
        System.arraycopy(this.astStack, this.astPtr + 1, md.thrownExceptions = new TypeReference[length], 0, length);
        md.sourceEnd = md.thrownExceptions[length - 1].sourceEnd;
        md.bodyStart = md.thrownExceptions[length - 1].sourceEnd + 1;
        // reset this.listLength after having read all thrown exceptions
        this.listLength = 0;
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = md.bodyStart;
        }
    }

    protected void consumeInvocationExpression() {
    // Trap all forms of invocation expressions. Note: Explicit constructor calls are not expressions. Top of expression stack has the MessageSend or AllocationExpression.
    }

    protected void consumeMethodInvocationName() {
        // MethodInvocation ::= Name '(' ArgumentListopt ')'
        // when the name is only an identifier...we have a message send to "this" (implicit)
        MessageSend m = newMessageSend();
        m.sourceEnd = this.rParenPos;
        m.sourceStart = (int) ((m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr]) >>> 32);
        m.selector = this.identifierStack[this.identifierPtr--];
        if (this.identifierLengthStack[this.identifierLengthPtr] == 1) {
            m.receiver = ThisReference.implicitThis();
            this.identifierLengthPtr--;
        } else {
            this.identifierLengthStack[this.identifierLengthPtr]--;
            m.receiver = getUnspecifiedReference();
            m.sourceStart = m.receiver.sourceStart;
        }
        int length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--];
        Annotation[] typeAnnotations;
        if (length != 0) {
            System.arraycopy(this.typeAnnotationStack, (this.typeAnnotationPtr -= length) + 1, typeAnnotations = new Annotation[length], 0, length);
            problemReporter().misplacedTypeAnnotations(typeAnnotations[0], typeAnnotations[typeAnnotations.length - 1]);
        }
        pushOnExpressionStack(m);
        consumeInvocationExpression();
    }

    protected void consumeMethodInvocationNameWithTypeArguments() {
        // MethodInvocation ::= Name '.' TypeArguments 'Identifier' '(' ArgumentListopt ')'
        // when the name is only an identifier...we have a message send to "this" (implicit)
        MessageSend m = newMessageSendWithTypeArguments();
        m.sourceEnd = this.rParenPos;
        m.sourceStart = (int) ((m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr]) >>> 32);
        m.selector = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        // handle type arguments
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, m.typeArguments = new TypeReference[length], 0, length);
        // consume position of '<'
        this.intPtr--;
        m.receiver = getUnspecifiedReference();
        m.sourceStart = m.receiver.sourceStart;
        pushOnExpressionStack(m);
        consumeInvocationExpression();
    }

    protected void consumeMethodInvocationPrimary() {
        //optimize the push/pop
        //MethodInvocation ::= Primary '.' 'Identifier' '(' ArgumentListopt ')'
        MessageSend m = newMessageSend();
        m.sourceStart = (int) ((m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr]) >>> 32);
        m.selector = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        m.receiver = this.expressionStack[this.expressionPtr];
        m.sourceStart = m.receiver.sourceStart;
        m.sourceEnd = this.rParenPos;
        this.expressionStack[this.expressionPtr] = m;
        consumeInvocationExpression();
    }

    protected void consumeMethodInvocationPrimaryWithTypeArguments() {
        //optimize the push/pop
        //MethodInvocation ::= Primary '.' TypeArguments 'Identifier' '(' ArgumentListopt ')'
        MessageSend m = newMessageSendWithTypeArguments();
        m.sourceStart = (int) ((m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr]) >>> 32);
        m.selector = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        // handle type arguments
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, m.typeArguments = new TypeReference[length], 0, length);
        // consume position of '<'
        this.intPtr--;
        m.receiver = this.expressionStack[this.expressionPtr];
        m.sourceStart = m.receiver.sourceStart;
        m.sourceEnd = this.rParenPos;
        this.expressionStack[this.expressionPtr] = m;
        consumeInvocationExpression();
    }

    protected void consumeMethodInvocationSuper() {
        // MethodInvocation ::= 'super' '.' 'Identifier' '(' ArgumentListopt ')'
        MessageSend m = newMessageSend();
        // start position of the super keyword
        m.sourceStart = this.intStack[this.intPtr--];
        m.sourceEnd = this.rParenPos;
        m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
        m.selector = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        m.receiver = new SuperReference(m.sourceStart, this.endPosition);
        pushOnExpressionStack(m);
        consumeInvocationExpression();
    }

    protected void consumeMethodInvocationSuperWithTypeArguments() {
        // MethodInvocation ::= 'super' '.' TypeArguments 'Identifier' '(' ArgumentListopt ')'
        MessageSend m = newMessageSendWithTypeArguments();
        // start position of the typeArguments
        this.intPtr--;
        m.sourceEnd = this.rParenPos;
        m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
        m.selector = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        // handle type arguments
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, m.typeArguments = new TypeReference[length], 0, length);
        // start position of the super keyword
        m.sourceStart = this.intStack[this.intPtr--];
        m.receiver = new SuperReference(m.sourceStart, this.endPosition);
        pushOnExpressionStack(m);
        consumeInvocationExpression();
    }

    protected void consumeModifiers() {
        int savedModifiersSourceStart = this.modifiersSourceStart;
        // might update modifiers with AccDeprecated
        checkComment();
        // modifiers
        pushOnIntStack(this.modifiers);
        if (this.modifiersSourceStart >= savedModifiersSourceStart) {
            this.modifiersSourceStart = savedModifiersSourceStart;
        }
        pushOnIntStack(this.modifiersSourceStart);
        resetModifiers();
    }

    protected void consumeModifiers2() {
        this.expressionLengthStack[this.expressionLengthPtr - 1] += this.expressionLengthStack[this.expressionLengthPtr--];
    }

    protected void consumeMultipleResources() {
        // Resources ::= Resources ';' Resource
        concatNodeLists();
    }

    protected void consumeTypeAnnotation() {
        if (!this.statementRecoveryActivated && this.options.sourceLevel < ClassFileConstants.JDK1_8 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            Annotation annotation = this.typeAnnotationStack[this.typeAnnotationPtr];
            problemReporter().invalidUsageOfTypeAnnotations(annotation);
        }
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=417660
        this.dimensions = this.intStack[this.intPtr--];
    }

    protected void consumeOneMoreTypeAnnotation() {
        // TypeAnnotations ::= TypeAnnotations TypeAnnotation
        this.typeAnnotationLengthStack[--this.typeAnnotationLengthPtr]++;
    }

    protected void consumeNameArrayType() {
        // handle type arguments
        pushOnGenericsLengthStack(0);
        pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
    }

    protected void consumeNestedMethod() {
        // NestedMethod ::= $empty
        jumpOverMethodBody();
        this.nestedMethod[this.nestedType]++;
        pushOnIntStack(this.scanner.currentPosition);
        consumeOpenBlock();
    }

    protected void consumeNestedType() {
        // NestedType ::= $empty
        int length = this.nestedMethod.length;
        if (++this.nestedType >= length) {
            System.arraycopy(this.nestedMethod, 0, this.nestedMethod = new int[length + 30], 0, length);
            // increase the size of the variablesCounter as well. It has to be consistent with the size of the nestedMethod collection
            System.arraycopy(this.variablesCounter, 0, this.variablesCounter = new int[length + 30], 0, length);
        }
        this.nestedMethod[this.nestedType] = 0;
        this.variablesCounter[this.nestedType] = 0;
    }

    protected void consumeNormalAnnotation(boolean isTypeAnnotation) {
        // NormalTypeAnnotation ::= TypeAnnotationName '(' MemberValuePairsopt ')'
        // NormalAnnotation ::= AnnotationName '(' MemberValuePairsopt ')'
        NormalAnnotation normalAnnotation = null;
        int oldIndex = this.identifierPtr;
        TypeReference typeReference = getAnnotationType();
        normalAnnotation = new NormalAnnotation(typeReference, this.intStack[this.intPtr--]);
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            System.arraycopy(this.astStack, (this.astPtr -= length) + 1, normalAnnotation.memberValuePairs = new MemberValuePair[length], 0, length);
        }
        normalAnnotation.declarationSourceEnd = this.rParenPos;
        if (isTypeAnnotation) {
            pushOnTypeAnnotationStack(normalAnnotation);
        } else {
            pushOnExpressionStack(normalAnnotation);
        }
        if (this.currentElement != null) {
            annotationRecoveryCheckPoint(normalAnnotation.sourceStart, normalAnnotation.declarationSourceEnd);
            if (this.currentElement instanceof RecoveredAnnotation) {
                this.currentElement = ((RecoveredAnnotation) this.currentElement).addAnnotation(normalAnnotation, oldIndex);
            }
        }
        if (!this.statementRecoveryActivated && this.options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            problemReporter().invalidUsageOfAnnotation(normalAnnotation);
        }
        this.recordStringLiterals = true;
    }

    protected void consumeOneDimLoop(boolean isAnnotated) {
        // OneDimLoop ::= '[' ']'
        // OneDimLoop ::= TypeAnnotations '[' ']'
        this.dimensions++;
        if (!isAnnotated) {
            // signal no annotations for the current dimension.
            pushOnTypeAnnotationLengthStack(0);
        }
    }

    protected void consumeOnlySynchronized() {
        // OnlySynchronized ::= 'synchronized'
        pushOnIntStack(this.synchronizedBlockSourceStart);
        resetModifiers();
        this.expressionLengthPtr--;
    }

    protected void consumeOnlyTypeArguments() {
        if (!this.statementRecoveryActivated && this.options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            int length = this.genericsLengthStack[this.genericsLengthPtr];
            problemReporter().invalidUsageOfTypeArguments((TypeReference) this.genericsStack[this.genericsPtr - length + 1], (TypeReference) this.genericsStack[this.genericsPtr]);
        }
    }

    protected void consumeOnlyTypeArgumentsForCastExpression() {
    // OnlyTypeArgumentsForCastExpression ::= OnlyTypeArguments
    }

    protected void consumeOpenBlock() {
        // OpenBlock ::= $empty
        pushOnIntStack(this.scanner.startPosition);
        int stackLength = this.realBlockStack.length;
        if (++this.realBlockPtr >= stackLength) {
            System.arraycopy(this.realBlockStack, 0, this.realBlockStack = new int[stackLength + StackIncrement], 0, stackLength);
        }
        this.realBlockStack[this.realBlockPtr] = 0;
    }

    protected void consumePackageComment() {
        // get possible comment for syntax since 1.5
        if (this.options.sourceLevel >= ClassFileConstants.JDK1_5) {
            checkComment();
            resetModifiers();
        }
    }

    protected void consumePackageDeclaration() {
        // PackageDeclaration ::= 'package' Name ';'
        /* build an ImportRef build from the last name
	stored in the identifier stack. */
        ImportReference impt = this.compilationUnit.currentPackage;
        this.compilationUnit.javadoc = this.javadoc;
        this.javadoc = null;
        // flush comments defined prior to import statements
        impt.declarationEnd = this.endStatementPosition;
        impt.declarationSourceEnd = flushCommentsDefinedPriorTo(impt.declarationSourceEnd);
    }

    protected void consumePackageDeclarationName() {
        // PackageDeclarationName ::= PackageComment 'package' Name RejectTypeAnnotations
        /* build an ImportRef build from the last name
	stored in the identifier stack. */
        ImportReference impt;
        int length;
        char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, ++this.identifierPtr, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr--, positions, 0, length);
        impt = new ImportReference(tokens, positions, false, ClassFileConstants.AccDefault);
        this.compilationUnit.currentPackage = impt;
        if (this.currentToken == TokenNameSEMICOLON) {
            impt.declarationSourceEnd = this.scanner.currentPosition - 1;
        } else {
            impt.declarationSourceEnd = impt.sourceEnd;
        }
        impt.declarationEnd = impt.declarationSourceEnd;
        //this.endPosition is just before the ;
        impt.declarationSourceStart = this.intStack[this.intPtr--];
        // get possible comment source start
        if (this.javadoc != null) {
            impt.declarationSourceStart = this.javadoc.sourceStart;
        }
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            // used to avoid branching back into the regular automaton
            this.restartRecovery = true;
        }
    }

    protected void consumePackageDeclarationNameWithModifiers() {
        // PackageDeclarationName ::= Modifiers 'package' PushRealModifiers Name RejectTypeAnnotations
        /* build an ImportRef build from the last name
	stored in the identifier stack. */
        ImportReference impt;
        int length;
        char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, ++this.identifierPtr, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr--, positions, 0, length);
        int packageModifiersSourceStart = this.intStack[this.intPtr--];
        // Unless there were any
        int packageModifiersSourceEnd = packageModifiersSourceStart;
        int packageModifiers = this.intStack[this.intPtr--];
        impt = new ImportReference(tokens, positions, false, packageModifiers);
        this.compilationUnit.currentPackage = impt;
        // consume annotations
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, impt.annotations = new Annotation[length], 0, length);
            impt.declarationSourceStart = packageModifiersSourceStart;
            // we don't need the position of the 'package keyword
            packageModifiersSourceEnd = this.intStack[this.intPtr--] - 2;
        } else {
            impt.declarationSourceStart = this.intStack[this.intPtr--];
            packageModifiersSourceEnd = impt.declarationSourceStart - 2;
            // get possible comment source start
            if (this.javadoc != null) {
                impt.declarationSourceStart = this.javadoc.sourceStart;
            }
        }
        if (packageModifiers != 0) {
            problemReporter().illegalModifiers(packageModifiersSourceStart, packageModifiersSourceEnd);
        }
        if (this.currentToken == TokenNameSEMICOLON) {
            impt.declarationSourceEnd = this.scanner.currentPosition - 1;
        } else {
            impt.declarationSourceEnd = impt.sourceEnd;
        }
        impt.declarationEnd = impt.declarationSourceEnd;
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            // used to avoid branching back into the regular automaton
            this.restartRecovery = true;
        }
    }

    protected void consumePostfixExpression() {
        // PostfixExpression ::= Name
        pushOnExpressionStack(getUnspecifiedReferenceOptimized());
    }

    protected void consumePrimaryNoNewArray() {
        // PrimaryNoNewArray ::=  PushLPAREN Expression PushRPAREN
        final Expression parenthesizedExpression = this.expressionStack[this.expressionPtr];
        updateSourcePosition(parenthesizedExpression);
        int numberOfParenthesis = (parenthesizedExpression.bits & ASTNode.ParenthesizedMASK) >> ASTNode.ParenthesizedSHIFT;
        parenthesizedExpression.bits &= ~ASTNode.ParenthesizedMASK;
        parenthesizedExpression.bits |= (numberOfParenthesis + 1) << ASTNode.ParenthesizedSHIFT;
    }

    protected void consumePrimaryNoNewArrayArrayType() {
        // PrimaryNoNewArray ::= Name Dims '.' 'class'
        // remove the class start position
        this.intPtr--;
        pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        pushOnGenericsLengthStack(0);
        ClassLiteralAccess cla;
        pushOnExpressionStack(cla = new ClassLiteralAccess(this.intStack[this.intPtr--], getTypeReference(this.intStack[this.intPtr--])));
        // javac correctly rejects annotations on dimensions here.
        rejectIllegalTypeAnnotations(cla.type);
    }

    protected void consumePrimaryNoNewArrayName() {
        // PrimaryNoNewArray ::= Name '.' 'class'
        // remove the class start position
        this.intPtr--;
        // handle type arguments
        pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        pushOnGenericsLengthStack(0);
        TypeReference typeReference = getTypeReference(0);
        rejectIllegalTypeAnnotations(typeReference);
        pushOnExpressionStack(new ClassLiteralAccess(this.intStack[this.intPtr--], typeReference));
    }

    protected void rejectIllegalLeadingTypeAnnotations(TypeReference typeReference) {
        // Reject misplaced annotations prefixed to a type reference; Used when the grammar is permissive enough to allow them in the first place.
        Annotation[][] annotations = typeReference.annotations;
        if (annotations != null && annotations[0] != null) {
            problemReporter().misplacedTypeAnnotations(annotations[0][0], annotations[0][annotations[0].length - 1]);
            // don't complain further.
            annotations[0] = null;
        }
    }

    private void rejectIllegalTypeAnnotations(TypeReference typeReference) {
        rejectIllegalTypeAnnotations(typeReference, false);
    }

    private void rejectIllegalTypeAnnotations(TypeReference typeReference, boolean tolerateAnnotationsOnDimensions) {
        // Reject misplaced annotations on type reference; Used when grammar is permissive enough to allow them in the first place.
        Annotation[][] annotations = typeReference.annotations;
        Annotation[] misplacedAnnotations;
        for (int i = 0, length = annotations == null ? 0 : annotations.length; i < length; i++) {
            misplacedAnnotations = annotations[i];
            if (misplacedAnnotations != null) {
                problemReporter().misplacedTypeAnnotations(misplacedAnnotations[0], misplacedAnnotations[misplacedAnnotations.length - 1]);
            }
        }
        annotations = typeReference.getAnnotationsOnDimensions(true);
        boolean tolerated = false;
        for (int i = 0, length = annotations == null ? 0 : annotations.length; i < length; i++) {
            misplacedAnnotations = annotations[i];
            if (misplacedAnnotations != null) {
                if (tolerateAnnotationsOnDimensions) {
                    problemReporter().toleratedMisplacedTypeAnnotations(misplacedAnnotations[0], misplacedAnnotations[misplacedAnnotations.length - 1]);
                    tolerated = true;
                } else
                    problemReporter().misplacedTypeAnnotations(misplacedAnnotations[0], misplacedAnnotations[misplacedAnnotations.length - 1]);
            }
        }
        if (!tolerated) {
            typeReference.annotations = null;
            typeReference.setAnnotationsOnDimensions(null);
            typeReference.bits &= ~ASTNode.HasTypeAnnotations;
        }
    }

    protected void consumeQualifiedSuperReceiver() {
        // QualifiedSuperReceiver ::= Name '.' 'super'
        // handle type arguments
        pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        pushOnGenericsLengthStack(0);
        // javac does not accept annotations here anywhere ...
        TypeReference typeReference = getTypeReference(0);
        rejectIllegalTypeAnnotations(typeReference);
        pushOnExpressionStack(new QualifiedSuperReference(typeReference, this.intStack[this.intPtr--], this.endPosition));
    }

    protected void consumePrimaryNoNewArrayNameThis() {
        // PrimaryNoNewArray ::= Name '.' 'this'
        // handle type arguments
        pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        // handle type arguments
        pushOnGenericsLengthStack(0);
        // javac does not accept annotations here anywhere ...
        TypeReference typeReference = getTypeReference(0);
        rejectIllegalTypeAnnotations(typeReference);
        pushOnExpressionStack(new QualifiedThisReference(typeReference, this.intStack[this.intPtr--], this.endPosition));
    }

    protected void consumePrimaryNoNewArrayPrimitiveArrayType() {
        // PrimaryNoNewArray ::= PrimitiveType Dims '.' 'class'
        // remove the class start position
        this.intPtr--;
        ClassLiteralAccess cla;
        pushOnExpressionStack(cla = new ClassLiteralAccess(this.intStack[this.intPtr--], getTypeReference(this.intStack[this.intPtr--])));
        rejectIllegalTypeAnnotations(cla.type, true);
    }

    protected void consumePrimaryNoNewArrayPrimitiveType() {
        // PrimaryNoNewArray ::= PrimitiveType '.' 'class'
        // remove the class start position
        this.intPtr--;
        ClassLiteralAccess cla;
        pushOnExpressionStack(cla = new ClassLiteralAccess(this.intStack[this.intPtr--], getTypeReference(0)));
        rejectIllegalTypeAnnotations(cla.type);
    }

    protected void consumePrimaryNoNewArrayThis() {
        // PrimaryNoNewArray ::= 'this'
        pushOnExpressionStack(new ThisReference(this.intStack[this.intPtr--], this.endPosition));
    }

    protected void consumePrimaryNoNewArrayWithName() {
        // PrimaryNoNewArray ::=  PushLPAREN Name PushRPAREN
        pushOnExpressionStack(getUnspecifiedReferenceOptimized());
        final Expression parenthesizedExpression = this.expressionStack[this.expressionPtr];
        updateSourcePosition(parenthesizedExpression);
        int numberOfParenthesis = (parenthesizedExpression.bits & ASTNode.ParenthesizedMASK) >> ASTNode.ParenthesizedSHIFT;
        parenthesizedExpression.bits &= ~ASTNode.ParenthesizedMASK;
        parenthesizedExpression.bits |= (numberOfParenthesis + 1) << ASTNode.ParenthesizedSHIFT;
    }

    protected void consumePrimitiveArrayType() {
    // nothing to do
    // Will be consume by a getTypeRefence call
    }

    protected void consumePrimitiveType() {
        // Type ::= PrimitiveType
        pushOnIntStack(0);
    }

    protected void consumePushLeftBrace() {
        // modifiers
        pushOnIntStack(this.endPosition);
    }

    protected void consumePushModifiers() {
        // modifiers
        pushOnIntStack(this.modifiers);
        pushOnIntStack(this.modifiersSourceStart);
        resetModifiers();
        pushOnExpressionStackLengthStack(0);
    }

    protected void consumePushCombineModifiers() {
        // ModifiersWithDefault ::= Modifiersopt 'default' Modifiersopt'
        // int stack on entry : ... Modifiers, ModifiersSourceStart, defaultSourceStart, defaultSourceEnd, Modifiers', Modifiers'SourceStart <<--- intPtr
        // int stack on exit : ... combinedModifiers, combinedModifiersSourceStart <<--- intPtr
        // pop modifiers'SourceStart, real location is with earlier block
        this.intPtr--;
        // pop modifiers
        int newModifiers = this.intStack[this.intPtr--] | ExtraCompilerModifiers.AccDefaultMethod;
        // pop location of 'default' keyword
        this.intPtr -= 2;
        if (// duplicate modifier(s) ?
        (this.intStack[this.intPtr - 1] & newModifiers) != 0) {
            newModifiers |= ExtraCompilerModifiers.AccAlternateModifierProblem;
        }
        // merge them in place
        this.intStack[this.intPtr - 1] |= newModifiers;
        // Also fix number of annotations-modifiers:
        this.expressionLengthStack[this.expressionLengthPtr - 1] += this.expressionLengthStack[this.expressionLengthPtr--];
        if (this.currentElement != null) {
            this.currentElement.addModifier(newModifiers, this.intStack[this.intPtr]);
        }
    }

    protected void consumePushModifiersForHeader() {
        // might update modifiers with AccDeprecated
        checkComment();
        // modifiers
        pushOnIntStack(this.modifiers);
        pushOnIntStack(this.modifiersSourceStart);
        resetModifiers();
        pushOnExpressionStackLengthStack(0);
    }

    protected void consumePushPosition() {
        // for source managment purpose
        // PushPosition ::= $empty
        pushOnIntStack(this.endPosition);
    }

    protected void consumePushRealModifiers() {
        // might update modifiers with AccDeprecated
        checkComment();
        // modifiers
        pushOnIntStack(this.modifiers);
        pushOnIntStack(this.modifiersSourceStart);
        resetModifiers();
    }

    protected void consumeQualifiedName(boolean qualifiedNameIsAnnotated) {
        // QualifiedName ::= Name '.' SimpleName
        // QualifiedName ::= Name '.' TypeAnnotations SimpleName 
        /*back from the recursive loop of QualifiedName.
	Updates identifier length into the length stack*/
        this.identifierLengthStack[--this.identifierLengthPtr]++;
        if (!qualifiedNameIsAnnotated) {
            pushOnTypeAnnotationLengthStack(0);
        }
    }

    protected void consumeUnannotatableQualifiedName() {
        // UnannotatableName ::= UnannotatableName '.' SimpleName
        this.identifierLengthStack[--this.identifierLengthPtr]++;
    }

    protected void consumeRecoveryMethodHeaderName() {
        // this method is call only inside recovery
        boolean isAnnotationMethod = false;
        if (this.currentElement instanceof RecoveredType) {
            isAnnotationMethod = (((RecoveredType) this.currentElement).typeDeclaration.modifiers & ClassFileConstants.AccAnnotation) != 0;
        } else {
            RecoveredType recoveredType = this.currentElement.enclosingType();
            if (recoveredType != null) {
                isAnnotationMethod = (recoveredType.typeDeclaration.modifiers & ClassFileConstants.AccAnnotation) != 0;
            }
        }
        consumeMethodHeaderName(isAnnotationMethod);
    }

    protected void consumeRecoveryMethodHeaderNameWithTypeParameters() {
        // this method is call only inside recovery
        boolean isAnnotationMethod = false;
        if (this.currentElement instanceof RecoveredType) {
            isAnnotationMethod = (((RecoveredType) this.currentElement).typeDeclaration.modifiers & ClassFileConstants.AccAnnotation) != 0;
        } else {
            RecoveredType recoveredType = this.currentElement.enclosingType();
            if (recoveredType != null) {
                isAnnotationMethod = (recoveredType.typeDeclaration.modifiers & ClassFileConstants.AccAnnotation) != 0;
            }
        }
        consumeMethodHeaderNameWithTypeParameters(isAnnotationMethod);
    }

    protected void consumeReduceImports() {
        // Consume imports
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.astPtr -= length;
            System.arraycopy(this.astStack, this.astPtr + 1, this.compilationUnit.imports = new ImportReference[length], 0, length);
        }
    }

    protected void consumeReferenceType() {
        // handle array type
        pushOnIntStack(0);
    }

    protected void consumeReferenceType1() {
        pushOnGenericsStack(getTypeReference(this.intStack[this.intPtr--]));
    }

    protected void consumeReferenceType2() {
        pushOnGenericsStack(getTypeReference(this.intStack[this.intPtr--]));
    }

    protected void consumeReferenceType3() {
        pushOnGenericsStack(getTypeReference(this.intStack[this.intPtr--]));
    }

    protected void consumeResourceAsLocalVariableDeclaration() {
        // Resource ::= Type PushModifiers VariableDeclaratorId EnterVariable '=' ForceNoDiet VariableInitializer RestoreDiet ExitVariableWithInitialization
        // Resource ::= Modifiers Type PushRealModifiers VariableDeclaratorId EnterVariable '=' ForceNoDiet VariableInitializer RestoreDiet ExitVariableWithInitialization
        consumeLocalVariableDeclaration();
    }

    protected void consumeResourceSpecification() {
    // ResourceSpecification ::= '(' Resources ')'
    }

    protected void consumeResourceOptionalTrailingSemiColon(boolean punctuated) {
        // TrailingSemiColon ::= ';'
        LocalDeclaration localDeclaration = (LocalDeclaration) this.astStack[this.astPtr];
        if (punctuated) {
            localDeclaration.declarationSourceEnd = this.endStatementPosition;
        }
    }

    protected void consumeRestoreDiet() {
        // RestoreDiet ::= $empty
        this.dietInt--;
    }

    protected void consumeRightParen() {
        // PushRPAREN ::= ')'
        pushOnIntStack(this.rParenPos);
    }

    protected void consumeNonTypeUseName() {
        // We can get here with type annotation stack empty, because completion parser manipulates the identifier stacks even without rule reduction. See completionIdentifierCheck
        for (int i = this.identifierLengthStack[this.identifierLengthPtr]; i > 0 && this.typeAnnotationLengthPtr >= 0; --i) {
            int length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--];
            Annotation[] typeAnnotations;
            if (length != 0) {
                System.arraycopy(this.typeAnnotationStack, (this.typeAnnotationPtr -= length) + 1, typeAnnotations = new Annotation[length], 0, length);
                problemReporter().misplacedTypeAnnotations(typeAnnotations[0], typeAnnotations[typeAnnotations.length - 1]);
            }
        }
    }

    protected void consumeZeroTypeAnnotations() {
        // PushZeroTypeAnnotations ::= $empty
        // Name ::= SimpleName
        // TypeAnnotationsopt ::= $empty
        // signal absence of @308 annotations.
        pushOnTypeAnnotationLengthStack(0);
    }

    // This method is part of an automatic generation : do NOT edit-modify
    protected void consumeRule(int act) {
        switch(act) {
            case //$NON-NLS-1$
            35:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Type ::= PrimitiveType");
                }
                consumePrimitiveType();
                break;
            case //$NON-NLS-1$
            49:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceType ::= ClassOrInterfaceType");
                }
                consumeReferenceType();
                break;
            case //$NON-NLS-1$
            53:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassOrInterface ::= Name");
                }
                consumeClassOrInterfaceName();
                break;
            case //$NON-NLS-1$
            54:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassOrInterface ::= GenericType DOT Name");
                }
                consumeClassOrInterface();
                break;
            case //$NON-NLS-1$
            55:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("GenericType ::= ClassOrInterface TypeArguments");
                }
                consumeGenericType();
                break;
            case //$NON-NLS-1$
            56:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("GenericType ::= ClassOrInterface LESS GREATER");
                }
                consumeGenericTypeWithDiamond();
                break;
            case //$NON-NLS-1$
            57:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayTypeWithTypeArgumentsName ::= GenericType DOT Name");
                }
                consumeArrayTypeWithTypeArgumentsName();
                break;
            case //$NON-NLS-1$
            58:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayType ::= PrimitiveType Dims");
                }
                consumePrimitiveArrayType();
                break;
            case //$NON-NLS-1$
            59:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayType ::= Name Dims");
                }
                consumeNameArrayType();
                break;
            case //$NON-NLS-1$
            60:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayType ::= ArrayTypeWithTypeArgumentsName Dims");
                }
                consumeGenericTypeNameArrayType();
                break;
            case //$NON-NLS-1$
            61:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayType ::= GenericType Dims");
                }
                consumeGenericTypeArrayType();
                break;
            case //$NON-NLS-1$
            63:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Name ::= SimpleName");
                }
                consumeZeroTypeAnnotations();
                break;
            case //$NON-NLS-1$
            68:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnannotatableName ::= UnannotatableName DOT SimpleName");
                }
                consumeUnannotatableQualifiedName();
                break;
            case //$NON-NLS-1$
            69:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("QualifiedName ::= Name DOT SimpleName");
                }
                consumeQualifiedName(false);
                break;
            case //$NON-NLS-1$
            70:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("QualifiedName ::= Name DOT TypeAnnotations SimpleName");
                }
                consumeQualifiedName(true);
                break;
            case //$NON-NLS-1$
            71:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeAnnotationsopt ::=");
                }
                consumeZeroTypeAnnotations();
                break;
            case //$NON-NLS-1$
            75:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeAnnotations0 ::= TypeAnnotations0 TypeAnnotation");
                }
                consumeOneMoreTypeAnnotation();
                break;
            case //$NON-NLS-1$
            76:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeAnnotation ::= NormalTypeAnnotation");
                }
                consumeTypeAnnotation();
                break;
            case //$NON-NLS-1$
            77:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeAnnotation ::= MarkerTypeAnnotation");
                }
                consumeTypeAnnotation();
                break;
            case //$NON-NLS-1$
            78:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeAnnotation ::= SingleMemberTypeAnnotation");
                }
                consumeTypeAnnotation();
                break;
            case //$NON-NLS-1$
            79:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeAnnotationName ::= AT308 UnannotatableName");
                }
                consumeAnnotationName();
                break;
            case //$NON-NLS-1$
            80:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("NormalTypeAnnotation ::= TypeAnnotationName LPAREN...");
                }
                consumeNormalAnnotation(true);
                break;
            case //$NON-NLS-1$
            81:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MarkerTypeAnnotation ::= TypeAnnotationName");
                }
                consumeMarkerAnnotation(true);
                break;
            case //$NON-NLS-1$
            82:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SingleMemberTypeAnnotation ::= TypeAnnotationName LPAREN");
                }
                consumeSingleMemberAnnotation(true);
                break;
            case //$NON-NLS-1$
            83:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RejectTypeAnnotations ::=");
                }
                consumeNonTypeUseName();
                break;
            case //$NON-NLS-1$
            84:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PushZeroTypeAnnotations ::=");
                }
                consumeZeroTypeAnnotations();
                break;
            case //$NON-NLS-1$
            85:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("VariableDeclaratorIdOrThis ::= this");
                }
                consumeExplicitThisParameter(false);
                break;
            case //$NON-NLS-1$
            86:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("VariableDeclaratorIdOrThis ::= UnannotatableName DOT this");
                }
                consumeExplicitThisParameter(true);
                break;
            case //$NON-NLS-1$
            87:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("VariableDeclaratorIdOrThis ::= VariableDeclaratorId");
                }
                consumeVariableDeclaratorIdParameter();
                break;
            case //$NON-NLS-1$
            88:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CompilationUnit ::= EnterCompilationUnit...");
                }
                consumeCompilationUnit();
                break;
            case //$NON-NLS-1$
            89:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InternalCompilationUnit ::= PackageDeclaration");
                }
                consumeInternalCompilationUnit();
                break;
            case //$NON-NLS-1$
            90:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InternalCompilationUnit ::= PackageDeclaration...");
                }
                consumeInternalCompilationUnit();
                break;
            case //$NON-NLS-1$
            91:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InternalCompilationUnit ::= PackageDeclaration...");
                }
                consumeInternalCompilationUnitWithTypes();
                break;
            case //$NON-NLS-1$
            92:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InternalCompilationUnit ::= PackageDeclaration...");
                }
                consumeInternalCompilationUnitWithTypes();
                break;
            case //$NON-NLS-1$
            93:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InternalCompilationUnit ::= ImportDeclarations...");
                }
                consumeInternalCompilationUnit();
                break;
            case //$NON-NLS-1$
            94:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InternalCompilationUnit ::= TypeDeclarations");
                }
                consumeInternalCompilationUnitWithTypes();
                break;
            case //$NON-NLS-1$
            95:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InternalCompilationUnit ::= ImportDeclarations...");
                }
                consumeInternalCompilationUnitWithTypes();
                break;
            case //$NON-NLS-1$
            96:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InternalCompilationUnit ::=");
                }
                consumeEmptyInternalCompilationUnit();
                break;
            case //$NON-NLS-1$
            97:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReduceImports ::=");
                }
                consumeReduceImports();
                break;
            case //$NON-NLS-1$
            98:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnterCompilationUnit ::=");
                }
                consumeEnterCompilationUnit();
                break;
            case //$NON-NLS-1$
            114:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CatchHeader ::= catch LPAREN CatchFormalParameter RPAREN");
                }
                consumeCatchHeader();
                break;
            case //$NON-NLS-1$
            116:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ImportDeclarations ::= ImportDeclarations...");
                }
                consumeImportDeclarations();
                break;
            case //$NON-NLS-1$
            118:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeDeclarations ::= TypeDeclarations TypeDeclaration");
                }
                consumeTypeDeclarations();
                break;
            case //$NON-NLS-1$
            119:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PackageDeclaration ::= PackageDeclarationName SEMICOLON");
                }
                consumePackageDeclaration();
                break;
            case //$NON-NLS-1$
            120:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PackageDeclarationName ::= Modifiers package...");
                }
                consumePackageDeclarationNameWithModifiers();
                break;
            case //$NON-NLS-1$
            121:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PackageDeclarationName ::= PackageComment package Name");
                }
                consumePackageDeclarationName();
                break;
            case //$NON-NLS-1$
            122:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PackageComment ::=");
                }
                consumePackageComment();
                break;
            case //$NON-NLS-1$
            127:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SingleTypeImportDeclaration ::=...");
                }
                consumeImportDeclaration();
                break;
            case //$NON-NLS-1$
            128:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SingleTypeImportDeclarationName ::= import Name...");
                }
                consumeSingleTypeImportDeclarationName();
                break;
            case //$NON-NLS-1$
            129:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeImportOnDemandDeclaration ::=...");
                }
                consumeImportDeclaration();
                break;
            case //$NON-NLS-1$
            130:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeImportOnDemandDeclarationName ::= import Name DOT...");
                }
                consumeTypeImportOnDemandDeclarationName();
                break;
            case //$NON-NLS-1$
            133:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeDeclaration ::= SEMICOLON");
                }
                consumeEmptyTypeDeclaration();
                break;
            case //$NON-NLS-1$
            137:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Modifiers ::= Modifiers Modifier");
                }
                consumeModifiers2();
                break;
            case //$NON-NLS-1$
            149:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Modifier ::= Annotation");
                }
                consumeAnnotationAsModifier();
                break;
            case //$NON-NLS-1$
            150:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassDeclaration ::= ClassHeader ClassBody");
                }
                consumeClassDeclaration();
                break;
            case //$NON-NLS-1$
            151:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassHeader ::= ClassHeaderName ClassHeaderExtendsopt...");
                }
                consumeClassHeader();
                break;
            case //$NON-NLS-1$
            152:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassHeaderName ::= ClassHeaderName1 TypeParameters");
                }
                consumeTypeHeaderNameWithTypeParameters();
                break;
            case //$NON-NLS-1$
            154:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassHeaderName1 ::= Modifiersopt class Identifier");
                }
                consumeClassHeaderName1();
                break;
            case //$NON-NLS-1$
            155:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassHeaderExtends ::= extends ClassType");
                }
                consumeClassHeaderExtends();
                break;
            case //$NON-NLS-1$
            156:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassHeaderImplements ::= implements InterfaceTypeList");
                }
                consumeClassHeaderImplements();
                break;
            case //$NON-NLS-1$
            158:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceTypeList ::= InterfaceTypeList COMMA...");
                }
                consumeInterfaceTypeList();
                break;
            case //$NON-NLS-1$
            159:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceType ::= ClassOrInterfaceType");
                }
                consumeInterfaceType();
                break;
            case //$NON-NLS-1$
            162:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassBodyDeclarations ::= ClassBodyDeclarations...");
                }
                consumeClassBodyDeclarations();
                break;
            case //$NON-NLS-1$
            166:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassBodyDeclaration ::= Diet NestedMethod...");
                }
                consumeClassBodyDeclaration();
                break;
            case //$NON-NLS-1$
            167:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Diet ::=");
                }
                consumeDiet();
                break;
            case //$NON-NLS-1$
            168:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Initializer ::= Diet NestedMethod CreateInitializer...");
                }
                consumeClassBodyDeclaration();
                break;
            case //$NON-NLS-1$
            169:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CreateInitializer ::=");
                }
                consumeCreateInitializer();
                break;
            case //$NON-NLS-1$
            176:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassMemberDeclaration ::= SEMICOLON");
                }
                consumeEmptyTypeDeclaration();
                break;
            case //$NON-NLS-1$
            179:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("FieldDeclaration ::= Modifiersopt Type...");
                }
                consumeFieldDeclaration();
                break;
            case //$NON-NLS-1$
            181:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("VariableDeclarators ::= VariableDeclarators COMMA...");
                }
                consumeVariableDeclarators();
                break;
            case //$NON-NLS-1$
            184:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnterVariable ::=");
                }
                consumeEnterVariable();
                break;
            case //$NON-NLS-1$
            185:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExitVariableWithInitialization ::=");
                }
                consumeExitVariableWithInitialization();
                break;
            case //$NON-NLS-1$
            186:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExitVariableWithoutInitialization ::=");
                }
                consumeExitVariableWithoutInitialization();
                break;
            case //$NON-NLS-1$
            187:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ForceNoDiet ::=");
                }
                consumeForceNoDiet();
                break;
            case //$NON-NLS-1$
            188:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RestoreDiet ::=");
                }
                consumeRestoreDiet();
                break;
            case //$NON-NLS-1$
            193:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodDeclaration ::= MethodHeader MethodBody");
                }
                // set to true to consume a method with a body
                consumeMethodDeclaration(true, false);
                break;
            case //$NON-NLS-1$
            194:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodDeclaration ::= DefaultMethodHeader MethodBody");
                }
                // set to true to consume a method with a body
                consumeMethodDeclaration(true, true);
                break;
            case //$NON-NLS-1$
            195:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AbstractMethodDeclaration ::= MethodHeader SEMICOLON");
                }
                // set to false to consume a method without body
                consumeMethodDeclaration(false, false);
                break;
            case //$NON-NLS-1$
            196:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodHeader ::= MethodHeaderName FormalParameterListopt");
                }
                consumeMethodHeader();
                break;
            case //$NON-NLS-1$
            197:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("DefaultMethodHeader ::= DefaultMethodHeaderName...");
                }
                consumeMethodHeader();
                break;
            case //$NON-NLS-1$
            198:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodHeaderName ::= Modifiersopt TypeParameters Type...");
                }
                consumeMethodHeaderNameWithTypeParameters(false);
                break;
            case //$NON-NLS-1$
            199:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodHeaderName ::= Modifiersopt Type Identifier LPAREN");
                }
                consumeMethodHeaderName(false);
                break;
            case //$NON-NLS-1$
            200:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("DefaultMethodHeaderName ::= ModifiersWithDefault...");
                }
                consumeMethodHeaderNameWithTypeParameters(false);
                break;
            case //$NON-NLS-1$
            201:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("DefaultMethodHeaderName ::= ModifiersWithDefault Type...");
                }
                consumeMethodHeaderName(false);
                break;
            case //$NON-NLS-1$
            202:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ModifiersWithDefault ::= Modifiersopt default...");
                }
                consumePushCombineModifiers();
                break;
            case //$NON-NLS-1$
            203:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodHeaderRightParen ::= RPAREN");
                }
                consumeMethodHeaderRightParen();
                break;
            case //$NON-NLS-1$
            204:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodHeaderExtendedDims ::= Dimsopt");
                }
                consumeMethodHeaderExtendedDims();
                break;
            case //$NON-NLS-1$
            205:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodHeaderThrowsClause ::= throws ClassTypeList");
                }
                consumeMethodHeaderThrowsClause();
                break;
            case //$NON-NLS-1$
            206:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConstructorHeader ::= ConstructorHeaderName...");
                }
                consumeConstructorHeader();
                break;
            case //$NON-NLS-1$
            207:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConstructorHeaderName ::= Modifiersopt TypeParameters...");
                }
                consumeConstructorHeaderNameWithTypeParameters();
                break;
            case //$NON-NLS-1$
            208:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConstructorHeaderName ::= Modifiersopt Identifier LPAREN");
                }
                consumeConstructorHeaderName();
                break;
            case //$NON-NLS-1$
            210:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("FormalParameterList ::= FormalParameterList COMMA...");
                }
                consumeFormalParameterList();
                break;
            case //$NON-NLS-1$
            211:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("FormalParameter ::= Modifiersopt Type...");
                }
                consumeFormalParameter(false);
                break;
            case //$NON-NLS-1$
            212:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("FormalParameter ::= Modifiersopt Type...");
                }
                consumeFormalParameter(true);
                break;
            case //$NON-NLS-1$
            213:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("FormalParameter ::= Modifiersopt Type AT308DOTDOTDOT...");
                }
                consumeFormalParameter(true);
                break;
            case //$NON-NLS-1$
            214:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CatchFormalParameter ::= Modifiersopt CatchType...");
                }
                consumeCatchFormalParameter();
                break;
            case //$NON-NLS-1$
            215:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CatchType ::= UnionType");
                }
                consumeCatchType();
                break;
            case //$NON-NLS-1$
            216:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnionType ::= Type");
                }
                consumeUnionTypeAsClassType();
                break;
            case //$NON-NLS-1$
            217:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnionType ::= UnionType OR Type");
                }
                consumeUnionType();
                break;
            case //$NON-NLS-1$
            219:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassTypeList ::= ClassTypeList COMMA ClassTypeElt");
                }
                consumeClassTypeList();
                break;
            case //$NON-NLS-1$
            220:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassTypeElt ::= ClassType");
                }
                consumeClassTypeElt();
                break;
            case //$NON-NLS-1$
            221:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodBody ::= NestedMethod LBRACE BlockStatementsopt...");
                }
                consumeMethodBody();
                break;
            case //$NON-NLS-1$
            222:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("NestedMethod ::=");
                }
                consumeNestedMethod();
                break;
            case //$NON-NLS-1$
            223:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("StaticInitializer ::= StaticOnly Block");
                }
                consumeStaticInitializer();
                break;
            case //$NON-NLS-1$
            224:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("StaticOnly ::= static");
                }
                consumeStaticOnly();
                break;
            case //$NON-NLS-1$
            225:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConstructorDeclaration ::= ConstructorHeader MethodBody");
                }
                consumeConstructorDeclaration();
                break;
            case //$NON-NLS-1$
            226:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConstructorDeclaration ::= ConstructorHeader SEMICOLON");
                }
                consumeInvalidConstructorDeclaration();
                break;
            case //$NON-NLS-1$
            227:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= this LPAREN...");
                }
                consumeExplicitConstructorInvocation(0, THIS_CALL);
                break;
            case //$NON-NLS-1$
            228:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= OnlyTypeArguments this");
                }
                consumeExplicitConstructorInvocationWithTypeArguments(0, THIS_CALL);
                break;
            case //$NON-NLS-1$
            229:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= super LPAREN...");
                }
                consumeExplicitConstructorInvocation(0, SUPER_CALL);
                break;
            case //$NON-NLS-1$
            230:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= OnlyTypeArguments...");
                }
                consumeExplicitConstructorInvocationWithTypeArguments(0, SUPER_CALL);
                break;
            case //$NON-NLS-1$
            231:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= Primary DOT super...");
                }
                consumeExplicitConstructorInvocation(1, SUPER_CALL);
                break;
            case //$NON-NLS-1$
            232:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= Primary DOT...");
                }
                consumeExplicitConstructorInvocationWithTypeArguments(1, SUPER_CALL);
                break;
            case //$NON-NLS-1$
            233:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= Name DOT super LPAREN");
                }
                consumeExplicitConstructorInvocation(2, SUPER_CALL);
                break;
            case //$NON-NLS-1$
            234:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= Name DOT...");
                }
                consumeExplicitConstructorInvocationWithTypeArguments(2, SUPER_CALL);
                break;
            case //$NON-NLS-1$
            235:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= Primary DOT this...");
                }
                consumeExplicitConstructorInvocation(1, THIS_CALL);
                break;
            case //$NON-NLS-1$
            236:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= Primary DOT...");
                }
                consumeExplicitConstructorInvocationWithTypeArguments(1, THIS_CALL);
                break;
            case //$NON-NLS-1$
            237:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= Name DOT this LPAREN");
                }
                consumeExplicitConstructorInvocation(2, THIS_CALL);
                break;
            case //$NON-NLS-1$
            238:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= Name DOT...");
                }
                consumeExplicitConstructorInvocationWithTypeArguments(2, THIS_CALL);
                break;
            case //$NON-NLS-1$
            239:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceDeclaration ::= InterfaceHeader InterfaceBody");
                }
                consumeInterfaceDeclaration();
                break;
            case //$NON-NLS-1$
            240:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceHeader ::= InterfaceHeaderName...");
                }
                consumeInterfaceHeader();
                break;
            case //$NON-NLS-1$
            241:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceHeaderName ::= InterfaceHeaderName1...");
                }
                consumeTypeHeaderNameWithTypeParameters();
                break;
            case //$NON-NLS-1$
            243:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceHeaderName1 ::= Modifiersopt interface...");
                }
                consumeInterfaceHeaderName1();
                break;
            case //$NON-NLS-1$
            244:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceHeaderExtends ::= extends InterfaceTypeList");
                }
                consumeInterfaceHeaderExtends();
                break;
            case //$NON-NLS-1$
            247:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceMemberDeclarations ::=...");
                }
                consumeInterfaceMemberDeclarations();
                break;
            case //$NON-NLS-1$
            248:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceMemberDeclaration ::= SEMICOLON");
                }
                consumeEmptyTypeDeclaration();
                break;
            case //$NON-NLS-1$
            250:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceMemberDeclaration ::= DefaultMethodHeader...");
                }
                consumeInterfaceMethodDeclaration(false);
                break;
            case //$NON-NLS-1$
            251:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceMemberDeclaration ::= MethodHeader MethodBody");
                }
                consumeInterfaceMethodDeclaration(false);
                break;
            case //$NON-NLS-1$
            252:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceMemberDeclaration ::= DefaultMethodHeader...");
                }
                consumeInterfaceMethodDeclaration(true);
                break;
            case //$NON-NLS-1$
            253:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InvalidConstructorDeclaration ::= ConstructorHeader...");
                }
                consumeInvalidConstructorDeclaration(true);
                break;
            case //$NON-NLS-1$
            254:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InvalidConstructorDeclaration ::= ConstructorHeader...");
                }
                consumeInvalidConstructorDeclaration(false);
                break;
            case //$NON-NLS-1$
            265:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PushLeftBrace ::=");
                }
                consumePushLeftBrace();
                break;
            case //$NON-NLS-1$
            266:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayInitializer ::= LBRACE PushLeftBrace ,opt RBRACE");
                }
                consumeEmptyArrayInitializer();
                break;
            case //$NON-NLS-1$
            267:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayInitializer ::= LBRACE PushLeftBrace...");
                }
                consumeArrayInitializer();
                break;
            case //$NON-NLS-1$
            268:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayInitializer ::= LBRACE PushLeftBrace...");
                }
                consumeArrayInitializer();
                break;
            case //$NON-NLS-1$
            270:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("VariableInitializers ::= VariableInitializers COMMA...");
                }
                consumeVariableInitializers();
                break;
            case //$NON-NLS-1$
            271:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Block ::= OpenBlock LBRACE BlockStatementsopt RBRACE");
                }
                consumeBlock();
                break;
            case //$NON-NLS-1$
            272:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("OpenBlock ::=");
                }
                consumeOpenBlock();
                break;
            case //$NON-NLS-1$
            273:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("BlockStatements ::= BlockStatement");
                }
                consumeBlockStatement();
                break;
            case //$NON-NLS-1$
            274:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("BlockStatements ::= BlockStatements BlockStatement");
                }
                consumeBlockStatements();
                break;
            case //$NON-NLS-1$
            281:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("BlockStatement ::= InterfaceDeclaration");
                }
                consumeInvalidInterfaceDeclaration();
                break;
            case //$NON-NLS-1$
            282:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("BlockStatement ::= AnnotationTypeDeclaration");
                }
                consumeInvalidAnnotationTypeDeclaration();
                break;
            case //$NON-NLS-1$
            283:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("BlockStatement ::= EnumDeclaration");
                }
                consumeInvalidEnumDeclaration();
                break;
            case //$NON-NLS-1$
            284:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("LocalVariableDeclarationStatement ::=...");
                }
                consumeLocalVariableDeclarationStatement();
                break;
            case //$NON-NLS-1$
            285:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("LocalVariableDeclaration ::= Type PushModifiers...");
                }
                consumeLocalVariableDeclaration();
                break;
            case //$NON-NLS-1$
            286:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("LocalVariableDeclaration ::= Modifiers Type...");
                }
                consumeLocalVariableDeclaration();
                break;
            case //$NON-NLS-1$
            287:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PushModifiers ::=");
                }
                consumePushModifiers();
                break;
            case //$NON-NLS-1$
            288:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PushModifiersForHeader ::=");
                }
                consumePushModifiersForHeader();
                break;
            case //$NON-NLS-1$
            289:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PushRealModifiers ::=");
                }
                consumePushRealModifiers();
                break;
            case //$NON-NLS-1$
            316:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EmptyStatement ::= SEMICOLON");
                }
                consumeEmptyStatement();
                break;
            case //$NON-NLS-1$
            317:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("LabeledStatement ::= Label COLON Statement");
                }
                consumeStatementLabel();
                break;
            case //$NON-NLS-1$
            318:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("LabeledStatementNoShortIf ::= Label COLON...");
                }
                consumeStatementLabel();
                break;
            case //$NON-NLS-1$
            319:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Label ::= Identifier");
                }
                consumeLabel();
                break;
            case //$NON-NLS-1$
            320:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExpressionStatement ::= StatementExpression SEMICOLON");
                }
                consumeExpressionStatement();
                break;
            case //$NON-NLS-1$
            329:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("IfThenStatement ::= if LPAREN Expression RPAREN...");
                }
                consumeStatementIfNoElse();
                break;
            case //$NON-NLS-1$
            330:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("IfThenElseStatement ::= if LPAREN Expression RPAREN...");
                }
                consumeStatementIfWithElse();
                break;
            case //$NON-NLS-1$
            331:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("IfThenElseStatementNoShortIf ::= if LPAREN Expression...");
                }
                consumeStatementIfWithElse();
                break;
            case //$NON-NLS-1$
            332:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SwitchStatement ::= switch LPAREN Expression RPAREN...");
                }
                consumeStatementSwitch();
                break;
            case //$NON-NLS-1$
            333:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SwitchBlock ::= LBRACE RBRACE");
                }
                consumeEmptySwitchBlock();
                break;
            case //$NON-NLS-1$
            336:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SwitchBlock ::= LBRACE SwitchBlockStatements...");
                }
                consumeSwitchBlock();
                break;
            case //$NON-NLS-1$
            338:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SwitchBlockStatements ::= SwitchBlockStatements...");
                }
                consumeSwitchBlockStatements();
                break;
            case //$NON-NLS-1$
            339:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SwitchBlockStatement ::= SwitchLabels BlockStatements");
                }
                consumeSwitchBlockStatement();
                break;
            case //$NON-NLS-1$
            341:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SwitchLabels ::= SwitchLabels SwitchLabel");
                }
                consumeSwitchLabels();
                break;
            case //$NON-NLS-1$
            342:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SwitchLabel ::= case ConstantExpression COLON");
                }
                consumeCaseLabel();
                break;
            case //$NON-NLS-1$
            343:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SwitchLabel ::= default COLON");
                }
                consumeDefaultLabel();
                break;
            case //$NON-NLS-1$
            344:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WhileStatement ::= while LPAREN Expression RPAREN...");
                }
                consumeStatementWhile();
                break;
            case //$NON-NLS-1$
            345:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WhileStatementNoShortIf ::= while LPAREN Expression...");
                }
                consumeStatementWhile();
                break;
            case //$NON-NLS-1$
            346:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("DoStatement ::= do Statement while LPAREN Expression...");
                }
                consumeStatementDo();
                break;
            case //$NON-NLS-1$
            347:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ForStatement ::= for LPAREN ForInitopt SEMICOLON...");
                }
                consumeStatementFor();
                break;
            case //$NON-NLS-1$
            348:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ForStatementNoShortIf ::= for LPAREN ForInitopt...");
                }
                consumeStatementFor();
                break;
            case //$NON-NLS-1$
            349:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ForInit ::= StatementExpressionList");
                }
                consumeForInit();
                break;
            case //$NON-NLS-1$
            353:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("StatementExpressionList ::= StatementExpressionList...");
                }
                consumeStatementExpressionList();
                break;
            case //$NON-NLS-1$
            354:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssertStatement ::= assert Expression SEMICOLON");
                }
                consumeSimpleAssertStatement();
                break;
            case //$NON-NLS-1$
            355:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssertStatement ::= assert Expression COLON Expression");
                }
                consumeAssertStatement();
                break;
            case //$NON-NLS-1$
            356:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("BreakStatement ::= break SEMICOLON");
                }
                consumeStatementBreak();
                break;
            case //$NON-NLS-1$
            357:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("BreakStatement ::= break Identifier SEMICOLON");
                }
                consumeStatementBreakWithLabel();
                break;
            case //$NON-NLS-1$
            358:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ContinueStatement ::= continue SEMICOLON");
                }
                consumeStatementContinue();
                break;
            case //$NON-NLS-1$
            359:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ContinueStatement ::= continue Identifier SEMICOLON");
                }
                consumeStatementContinueWithLabel();
                break;
            case //$NON-NLS-1$
            360:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReturnStatement ::= return Expressionopt SEMICOLON");
                }
                consumeStatementReturn();
                break;
            case //$NON-NLS-1$
            361:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ThrowStatement ::= throw Expression SEMICOLON");
                }
                consumeStatementThrow();
                break;
            case //$NON-NLS-1$
            362:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SynchronizedStatement ::= OnlySynchronized LPAREN...");
                }
                consumeStatementSynchronized();
                break;
            case //$NON-NLS-1$
            363:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("OnlySynchronized ::= synchronized");
                }
                consumeOnlySynchronized();
                break;
            case //$NON-NLS-1$
            364:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TryStatement ::= try TryBlock Catches");
                }
                consumeStatementTry(false, false);
                break;
            case //$NON-NLS-1$
            365:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TryStatement ::= try TryBlock Catchesopt Finally");
                }
                consumeStatementTry(true, false);
                break;
            case //$NON-NLS-1$
            366:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TryStatementWithResources ::= try ResourceSpecification");
                }
                consumeStatementTry(false, true);
                break;
            case //$NON-NLS-1$
            367:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TryStatementWithResources ::= try ResourceSpecification");
                }
                consumeStatementTry(true, true);
                break;
            case //$NON-NLS-1$
            368:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ResourceSpecification ::= LPAREN Resources ;opt RPAREN");
                }
                consumeResourceSpecification();
                break;
            case //$NON-NLS-1$
            369:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println(";opt ::=");
                }
                consumeResourceOptionalTrailingSemiColon(false);
                break;
            case //$NON-NLS-1$
            370:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println(";opt ::= SEMICOLON");
                }
                consumeResourceOptionalTrailingSemiColon(true);
                break;
            case //$NON-NLS-1$
            371:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Resources ::= Resource");
                }
                consumeSingleResource();
                break;
            case //$NON-NLS-1$
            372:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Resources ::= Resources TrailingSemiColon Resource");
                }
                consumeMultipleResources();
                break;
            case //$NON-NLS-1$
            373:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TrailingSemiColon ::= SEMICOLON");
                }
                consumeResourceOptionalTrailingSemiColon(true);
                break;
            case //$NON-NLS-1$
            374:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Resource ::= Type PushModifiers VariableDeclaratorId...");
                }
                consumeResourceAsLocalVariableDeclaration();
                break;
            case //$NON-NLS-1$
            375:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Resource ::= Modifiers Type PushRealModifiers...");
                }
                consumeResourceAsLocalVariableDeclaration();
                break;
            case //$NON-NLS-1$
            377:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExitTryBlock ::=");
                }
                consumeExitTryBlock();
                break;
            case //$NON-NLS-1$
            379:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Catches ::= Catches CatchClause");
                }
                consumeCatches();
                break;
            case //$NON-NLS-1$
            380:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CatchClause ::= catch LPAREN CatchFormalParameter RPAREN");
                }
                consumeStatementCatch();
                break;
            case //$NON-NLS-1$
            382:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PushLPAREN ::= LPAREN");
                }
                consumeLeftParen();
                break;
            case //$NON-NLS-1$
            383:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PushRPAREN ::= RPAREN");
                }
                consumeRightParen();
                break;
            case //$NON-NLS-1$
            388:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PrimaryNoNewArray ::= this");
                }
                consumePrimaryNoNewArrayThis();
                break;
            case //$NON-NLS-1$
            389:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PrimaryNoNewArray ::= PushLPAREN Expression_NotName...");
                }
                consumePrimaryNoNewArray();
                break;
            case //$NON-NLS-1$
            390:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PrimaryNoNewArray ::= PushLPAREN Name PushRPAREN");
                }
                consumePrimaryNoNewArrayWithName();
                break;
            case //$NON-NLS-1$
            393:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PrimaryNoNewArray ::= Name DOT this");
                }
                consumePrimaryNoNewArrayNameThis();
                break;
            case //$NON-NLS-1$
            394:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("QualifiedSuperReceiver ::= Name DOT super");
                }
                consumeQualifiedSuperReceiver();
                break;
            case //$NON-NLS-1$
            395:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PrimaryNoNewArray ::= Name DOT class");
                }
                consumePrimaryNoNewArrayName();
                break;
            case //$NON-NLS-1$
            396:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PrimaryNoNewArray ::= Name Dims DOT class");
                }
                consumePrimaryNoNewArrayArrayType();
                break;
            case //$NON-NLS-1$
            397:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PrimaryNoNewArray ::= PrimitiveType Dims DOT class");
                }
                consumePrimaryNoNewArrayPrimitiveArrayType();
                break;
            case //$NON-NLS-1$
            398:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PrimaryNoNewArray ::= PrimitiveType DOT class");
                }
                consumePrimaryNoNewArrayPrimitiveType();
                break;
            case //$NON-NLS-1$
            404:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceExpressionTypeArgumentsAndTrunk0 ::=...");
                }
                consumeReferenceExpressionTypeArgumentsAndTrunk(false);
                break;
            case //$NON-NLS-1$
            405:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceExpressionTypeArgumentsAndTrunk0 ::=...");
                }
                consumeReferenceExpressionTypeArgumentsAndTrunk(true);
                break;
            case //$NON-NLS-1$
            406:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceExpression ::= PrimitiveType Dims COLON_COLON");
                }
                consumeReferenceExpressionTypeForm(true);
                break;
            case //$NON-NLS-1$
            407:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceExpression ::= Name Dimsopt COLON_COLON...");
                }
                consumeReferenceExpressionTypeForm(false);
                break;
            case //$NON-NLS-1$
            408:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceExpression ::= Name BeginTypeArguments...");
                }
                consumeReferenceExpressionGenericTypeForm();
                break;
            case //$NON-NLS-1$
            409:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceExpression ::= Primary COLON_COLON...");
                }
                consumeReferenceExpressionPrimaryForm();
                break;
            case //$NON-NLS-1$
            410:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceExpression ::= QualifiedSuperReceiver...");
                }
                consumeReferenceExpressionPrimaryForm();
                break;
            case //$NON-NLS-1$
            411:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceExpression ::= super COLON_COLON...");
                }
                consumeReferenceExpressionSuperForm();
                break;
            case //$NON-NLS-1$
            412:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("NonWildTypeArgumentsopt ::=");
                }
                consumeEmptyTypeArguments();
                break;
            case //$NON-NLS-1$
            414:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("IdentifierOrNew ::= Identifier");
                }
                consumeIdentifierOrNew(false);
                break;
            case //$NON-NLS-1$
            415:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("IdentifierOrNew ::= new");
                }
                consumeIdentifierOrNew(true);
                break;
            case //$NON-NLS-1$
            416:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("LambdaExpression ::= LambdaParameters ARROW LambdaBody");
                }
                consumeLambdaExpression();
                break;
            case //$NON-NLS-1$
            417:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("NestedLambda ::=");
                }
                consumeNestedLambda();
                break;
            case //$NON-NLS-1$
            418:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("LambdaParameters ::= Identifier NestedLambda");
                }
                consumeTypeElidedLambdaParameter(false);
                break;
            case //$NON-NLS-1$
            424:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeElidedFormalParameterList ::=...");
                }
                consumeFormalParameterList();
                break;
            case //$NON-NLS-1$
            425:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeElidedFormalParameter ::= Modifiersopt Identifier");
                }
                consumeTypeElidedLambdaParameter(true);
                break;
            case //$NON-NLS-1$
            428:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ElidedLeftBraceAndReturn ::=");
                }
                consumeElidedLeftBraceAndReturn();
                break;
            case //$NON-NLS-1$
            429:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AllocationHeader ::= new ClassType LPAREN...");
                }
                consumeAllocationHeader();
                break;
            case //$NON-NLS-1$
            430:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassInstanceCreationExpression ::= new...");
                }
                consumeClassInstanceCreationExpressionWithTypeArguments();
                break;
            case //$NON-NLS-1$
            431:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassInstanceCreationExpression ::= new ClassType...");
                }
                consumeClassInstanceCreationExpression();
                break;
            case //$NON-NLS-1$
            432:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassInstanceCreationExpression ::= Primary DOT new...");
                }
                consumeClassInstanceCreationExpressionQualifiedWithTypeArguments();
                break;
            case //$NON-NLS-1$
            433:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassInstanceCreationExpression ::= Primary DOT new...");
                }
                consumeClassInstanceCreationExpressionQualified();
                break;
            case //$NON-NLS-1$
            434:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassInstanceCreationExpression ::=...");
                }
                consumeClassInstanceCreationExpressionQualified();
                break;
            case //$NON-NLS-1$
            435:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassInstanceCreationExpression ::=...");
                }
                consumeClassInstanceCreationExpressionQualifiedWithTypeArguments();
                break;
            case //$NON-NLS-1$
            436:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnterInstanceCreationArgumentList ::=");
                }
                consumeEnterInstanceCreationArgumentList();
                break;
            case //$NON-NLS-1$
            437:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassInstanceCreationExpressionName ::= Name DOT new");
                }
                consumeClassInstanceCreationExpressionName();
                break;
            case //$NON-NLS-1$
            438:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnqualifiedClassBodyopt ::=");
                }
                consumeClassBodyopt();
                break;
            case //$NON-NLS-1$
            440:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnqualifiedEnterAnonymousClassBody ::=");
                }
                consumeEnterAnonymousClassBody(false);
                break;
            case //$NON-NLS-1$
            441:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("QualifiedClassBodyopt ::=");
                }
                consumeClassBodyopt();
                break;
            case //$NON-NLS-1$
            443:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("QualifiedEnterAnonymousClassBody ::=");
                }
                consumeEnterAnonymousClassBody(true);
                break;
            case //$NON-NLS-1$
            445:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArgumentList ::= ArgumentList COMMA Expression");
                }
                consumeArgumentList();
                break;
            case //$NON-NLS-1$
            446:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayCreationHeader ::= new PrimitiveType...");
                }
                consumeArrayCreationHeader();
                break;
            case //$NON-NLS-1$
            447:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayCreationHeader ::= new ClassOrInterfaceType...");
                }
                consumeArrayCreationHeader();
                break;
            case //$NON-NLS-1$
            448:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayCreationWithoutArrayInitializer ::= new...");
                }
                consumeArrayCreationExpressionWithoutInitializer();
                break;
            case //$NON-NLS-1$
            449:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayCreationWithArrayInitializer ::= new PrimitiveType");
                }
                consumeArrayCreationExpressionWithInitializer();
                break;
            case //$NON-NLS-1$
            450:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayCreationWithoutArrayInitializer ::= new...");
                }
                consumeArrayCreationExpressionWithoutInitializer();
                break;
            case //$NON-NLS-1$
            451:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayCreationWithArrayInitializer ::= new...");
                }
                consumeArrayCreationExpressionWithInitializer();
                break;
            case //$NON-NLS-1$
            453:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("DimWithOrWithOutExprs ::= DimWithOrWithOutExprs...");
                }
                consumeDimWithOrWithOutExprs();
                break;
            case //$NON-NLS-1$
            455:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("DimWithOrWithOutExpr ::= TypeAnnotationsopt LBRACKET...");
                }
                consumeDimWithOrWithOutExpr();
                break;
            case //$NON-NLS-1$
            456:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Dims ::= DimsLoop");
                }
                consumeDims();
                break;
            case //$NON-NLS-1$
            459:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("OneDimLoop ::= LBRACKET RBRACKET");
                }
                consumeOneDimLoop(false);
                break;
            case //$NON-NLS-1$
            460:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("OneDimLoop ::= TypeAnnotations LBRACKET RBRACKET");
                }
                consumeOneDimLoop(true);
                break;
            case //$NON-NLS-1$
            461:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("FieldAccess ::= Primary DOT Identifier");
                }
                consumeFieldAccess(false);
                break;
            case //$NON-NLS-1$
            462:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("FieldAccess ::= super DOT Identifier");
                }
                consumeFieldAccess(true);
                break;
            case //$NON-NLS-1$
            463:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("FieldAccess ::= QualifiedSuperReceiver DOT Identifier");
                }
                consumeFieldAccess(false);
                break;
            case //$NON-NLS-1$
            464:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodInvocation ::= Name LPAREN ArgumentListopt RPAREN");
                }
                consumeMethodInvocationName();
                break;
            case //$NON-NLS-1$
            465:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodInvocation ::= Name DOT OnlyTypeArguments...");
                }
                consumeMethodInvocationNameWithTypeArguments();
                break;
            case //$NON-NLS-1$
            466:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodInvocation ::= Primary DOT OnlyTypeArguments...");
                }
                consumeMethodInvocationPrimaryWithTypeArguments();
                break;
            case //$NON-NLS-1$
            467:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodInvocation ::= Primary DOT Identifier LPAREN...");
                }
                consumeMethodInvocationPrimary();
                break;
            case //$NON-NLS-1$
            468:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodInvocation ::= QualifiedSuperReceiver DOT...");
                }
                consumeMethodInvocationPrimary();
                break;
            case //$NON-NLS-1$
            469:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodInvocation ::= QualifiedSuperReceiver DOT...");
                }
                consumeMethodInvocationPrimaryWithTypeArguments();
                break;
            case //$NON-NLS-1$
            470:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodInvocation ::= super DOT OnlyTypeArguments...");
                }
                consumeMethodInvocationSuperWithTypeArguments();
                break;
            case //$NON-NLS-1$
            471:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodInvocation ::= super DOT Identifier LPAREN...");
                }
                consumeMethodInvocationSuper();
                break;
            case //$NON-NLS-1$
            472:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayAccess ::= Name LBRACKET Expression RBRACKET");
                }
                consumeArrayAccess(true);
                break;
            case //$NON-NLS-1$
            473:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayAccess ::= PrimaryNoNewArray LBRACKET Expression...");
                }
                consumeArrayAccess(false);
                break;
            case //$NON-NLS-1$
            474:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayAccess ::= ArrayCreationWithArrayInitializer...");
                }
                consumeArrayAccess(false);
                break;
            case //$NON-NLS-1$
            476:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PostfixExpression ::= Name");
                }
                consumePostfixExpression();
                break;
            case //$NON-NLS-1$
            479:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PostIncrementExpression ::= PostfixExpression PLUS_PLUS");
                }
                consumeUnaryExpression(OperatorIds.PLUS, true);
                break;
            case //$NON-NLS-1$
            480:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PostDecrementExpression ::= PostfixExpression...");
                }
                consumeUnaryExpression(OperatorIds.MINUS, true);
                break;
            case //$NON-NLS-1$
            481:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PushPosition ::=");
                }
                consumePushPosition();
                break;
            case //$NON-NLS-1$
            484:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnaryExpression ::= PLUS PushPosition UnaryExpression");
                }
                consumeUnaryExpression(OperatorIds.PLUS);
                break;
            case //$NON-NLS-1$
            485:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnaryExpression ::= MINUS PushPosition UnaryExpression");
                }
                consumeUnaryExpression(OperatorIds.MINUS);
                break;
            case //$NON-NLS-1$
            487:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PreIncrementExpression ::= PLUS_PLUS PushPosition...");
                }
                consumeUnaryExpression(OperatorIds.PLUS, false);
                break;
            case //$NON-NLS-1$
            488:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PreDecrementExpression ::= MINUS_MINUS PushPosition...");
                }
                consumeUnaryExpression(OperatorIds.MINUS, false);
                break;
            case //$NON-NLS-1$
            490:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnaryExpressionNotPlusMinus ::= TWIDDLE PushPosition...");
                }
                consumeUnaryExpression(OperatorIds.TWIDDLE);
                break;
            case //$NON-NLS-1$
            491:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnaryExpressionNotPlusMinus ::= NOT PushPosition...");
                }
                consumeUnaryExpression(OperatorIds.NOT);
                break;
            case //$NON-NLS-1$
            493:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CastExpression ::= PushLPAREN PrimitiveType Dimsopt...");
                }
                consumeCastExpressionWithPrimitiveType();
                break;
            case //$NON-NLS-1$
            494:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CastExpression ::= PushLPAREN Name...");
                }
                consumeCastExpressionWithGenericsArray();
                break;
            case //$NON-NLS-1$
            495:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CastExpression ::= PushLPAREN Name...");
                }
                consumeCastExpressionWithQualifiedGenericsArray();
                break;
            case //$NON-NLS-1$
            496:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CastExpression ::= PushLPAREN Name PushRPAREN...");
                }
                consumeCastExpressionLL1();
                break;
            case //$NON-NLS-1$
            497:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CastExpression ::= BeginIntersectionCast PushLPAREN...");
                }
                consumeCastExpressionLL1WithBounds();
                break;
            case //$NON-NLS-1$
            498:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CastExpression ::= PushLPAREN Name Dims...");
                }
                consumeCastExpressionWithNameArray();
                break;
            case //$NON-NLS-1$
            499:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditionalBoundsListOpt ::=");
                }
                consumeZeroAdditionalBounds();
                break;
            case //$NON-NLS-1$
            503:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("OnlyTypeArgumentsForCastExpression ::= OnlyTypeArguments");
                }
                consumeOnlyTypeArgumentsForCastExpression();
                break;
            case //$NON-NLS-1$
            504:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InsideCastExpression ::=");
                }
                consumeInsideCastExpression();
                break;
            case //$NON-NLS-1$
            505:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InsideCastExpressionLL1 ::=");
                }
                consumeInsideCastExpressionLL1();
                break;
            case //$NON-NLS-1$
            506:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InsideCastExpressionLL1WithBounds ::=");
                }
                consumeInsideCastExpressionLL1WithBounds();
                break;
            case //$NON-NLS-1$
            507:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InsideCastExpressionWithQualifiedGenerics ::=");
                }
                consumeInsideCastExpressionWithQualifiedGenerics();
                break;
            case //$NON-NLS-1$
            509:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MultiplicativeExpression ::= MultiplicativeExpression...");
                }
                consumeBinaryExpression(OperatorIds.MULTIPLY);
                break;
            case //$NON-NLS-1$
            510:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MultiplicativeExpression ::= MultiplicativeExpression...");
                }
                consumeBinaryExpression(OperatorIds.DIVIDE);
                break;
            case //$NON-NLS-1$
            511:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MultiplicativeExpression ::= MultiplicativeExpression...");
                }
                consumeBinaryExpression(OperatorIds.REMAINDER);
                break;
            case //$NON-NLS-1$
            513:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditiveExpression ::= AdditiveExpression PLUS...");
                }
                consumeBinaryExpression(OperatorIds.PLUS);
                break;
            case //$NON-NLS-1$
            514:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditiveExpression ::= AdditiveExpression MINUS...");
                }
                consumeBinaryExpression(OperatorIds.MINUS);
                break;
            case //$NON-NLS-1$
            516:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ShiftExpression ::= ShiftExpression LEFT_SHIFT...");
                }
                consumeBinaryExpression(OperatorIds.LEFT_SHIFT);
                break;
            case //$NON-NLS-1$
            517:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ShiftExpression ::= ShiftExpression RIGHT_SHIFT...");
                }
                consumeBinaryExpression(OperatorIds.RIGHT_SHIFT);
                break;
            case //$NON-NLS-1$
            518:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ShiftExpression ::= ShiftExpression UNSIGNED_RIGHT_SHIFT");
                }
                consumeBinaryExpression(OperatorIds.UNSIGNED_RIGHT_SHIFT);
                break;
            case //$NON-NLS-1$
            520:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression ::= RelationalExpression LESS...");
                }
                consumeBinaryExpression(OperatorIds.LESS);
                break;
            case //$NON-NLS-1$
            521:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression ::= RelationalExpression GREATER...");
                }
                consumeBinaryExpression(OperatorIds.GREATER);
                break;
            case //$NON-NLS-1$
            522:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression ::= RelationalExpression LESS_EQUAL");
                }
                consumeBinaryExpression(OperatorIds.LESS_EQUAL);
                break;
            case //$NON-NLS-1$
            523:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression ::= RelationalExpression...");
                }
                consumeBinaryExpression(OperatorIds.GREATER_EQUAL);
                break;
            case //$NON-NLS-1$
            525:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InstanceofExpression ::= InstanceofExpression instanceof");
                }
                consumeInstanceOfExpression();
                break;
            case //$NON-NLS-1$
            527:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EqualityExpression ::= EqualityExpression EQUAL_EQUAL...");
                }
                consumeEqualityExpression(OperatorIds.EQUAL_EQUAL);
                break;
            case //$NON-NLS-1$
            528:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EqualityExpression ::= EqualityExpression NOT_EQUAL...");
                }
                consumeEqualityExpression(OperatorIds.NOT_EQUAL);
                break;
            case //$NON-NLS-1$
            530:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AndExpression ::= AndExpression AND EqualityExpression");
                }
                consumeBinaryExpression(OperatorIds.AND);
                break;
            case //$NON-NLS-1$
            532:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExclusiveOrExpression ::= ExclusiveOrExpression XOR...");
                }
                consumeBinaryExpression(OperatorIds.XOR);
                break;
            case //$NON-NLS-1$
            534:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InclusiveOrExpression ::= InclusiveOrExpression OR...");
                }
                consumeBinaryExpression(OperatorIds.OR);
                break;
            case //$NON-NLS-1$
            536:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConditionalAndExpression ::= ConditionalAndExpression...");
                }
                consumeBinaryExpression(OperatorIds.AND_AND);
                break;
            case //$NON-NLS-1$
            538:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConditionalOrExpression ::= ConditionalOrExpression...");
                }
                consumeBinaryExpression(OperatorIds.OR_OR);
                break;
            case //$NON-NLS-1$
            540:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConditionalExpression ::= ConditionalOrExpression...");
                }
                consumeConditionalExpression(OperatorIds.QUESTIONCOLON);
                break;
            case //$NON-NLS-1$
            543:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Assignment ::= PostfixExpression AssignmentOperator...");
                }
                consumeAssignment();
                break;
            case //$NON-NLS-1$
            545:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Assignment ::= InvalidArrayInitializerAssignement");
                }
                ignoreExpressionAssignment();
                break;
            case //$NON-NLS-1$
            546:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= EQUAL");
                }
                consumeAssignmentOperator(EQUAL);
                break;
            case //$NON-NLS-1$
            547:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= MULTIPLY_EQUAL");
                }
                consumeAssignmentOperator(MULTIPLY);
                break;
            case //$NON-NLS-1$
            548:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= DIVIDE_EQUAL");
                }
                consumeAssignmentOperator(DIVIDE);
                break;
            case //$NON-NLS-1$
            549:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= REMAINDER_EQUAL");
                }
                consumeAssignmentOperator(REMAINDER);
                break;
            case //$NON-NLS-1$
            550:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= PLUS_EQUAL");
                }
                consumeAssignmentOperator(PLUS);
                break;
            case //$NON-NLS-1$
            551:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= MINUS_EQUAL");
                }
                consumeAssignmentOperator(MINUS);
                break;
            case //$NON-NLS-1$
            552:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= LEFT_SHIFT_EQUAL");
                }
                consumeAssignmentOperator(LEFT_SHIFT);
                break;
            case //$NON-NLS-1$
            553:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= RIGHT_SHIFT_EQUAL");
                }
                consumeAssignmentOperator(RIGHT_SHIFT);
                break;
            case //$NON-NLS-1$
            554:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= UNSIGNED_RIGHT_SHIFT_EQUAL");
                }
                consumeAssignmentOperator(UNSIGNED_RIGHT_SHIFT);
                break;
            case //$NON-NLS-1$
            555:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= AND_EQUAL");
                }
                consumeAssignmentOperator(AND);
                break;
            case //$NON-NLS-1$
            556:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= XOR_EQUAL");
                }
                consumeAssignmentOperator(XOR);
                break;
            case //$NON-NLS-1$
            557:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= OR_EQUAL");
                }
                consumeAssignmentOperator(OR);
                break;
            case //$NON-NLS-1$
            558:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Expression ::= AssignmentExpression");
                }
                consumeExpression();
                break;
            case //$NON-NLS-1$
            561:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Expressionopt ::=");
                }
                consumeEmptyExpression();
                break;
            case //$NON-NLS-1$
            566:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassBodyDeclarationsopt ::=");
                }
                consumeEmptyClassBodyDeclarationsopt();
                break;
            case //$NON-NLS-1$
            567:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassBodyDeclarationsopt ::= NestedType...");
                }
                consumeClassBodyDeclarationsopt();
                break;
            case //$NON-NLS-1$
            568:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Modifiersopt ::=");
                }
                consumeDefaultModifiers();
                break;
            case //$NON-NLS-1$
            569:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Modifiersopt ::= Modifiers");
                }
                consumeModifiers();
                break;
            case //$NON-NLS-1$
            570:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("BlockStatementsopt ::=");
                }
                consumeEmptyBlockStatementsopt();
                break;
            case //$NON-NLS-1$
            572:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Dimsopt ::=");
                }
                consumeEmptyDimsopt();
                break;
            case //$NON-NLS-1$
            574:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArgumentListopt ::=");
                }
                consumeEmptyArgumentListopt();
                break;
            case //$NON-NLS-1$
            578:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("FormalParameterListopt ::=");
                }
                consumeFormalParameterListopt();
                break;
            case //$NON-NLS-1$
            582:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceMemberDeclarationsopt ::=");
                }
                consumeEmptyInterfaceMemberDeclarationsopt();
                break;
            case //$NON-NLS-1$
            583:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceMemberDeclarationsopt ::= NestedType...");
                }
                consumeInterfaceMemberDeclarationsopt();
                break;
            case //$NON-NLS-1$
            584:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("NestedType ::=");
                }
                consumeNestedType();
                break;
            case //$NON-NLS-1$
            585:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ForInitopt ::=");
                }
                consumeEmptyForInitopt();
                break;
            case //$NON-NLS-1$
            587:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ForUpdateopt ::=");
                }
                consumeEmptyForUpdateopt();
                break;
            case //$NON-NLS-1$
            591:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Catchesopt ::=");
                }
                consumeEmptyCatchesopt();
                break;
            case //$NON-NLS-1$
            593:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumDeclaration ::= EnumHeader EnumBody");
                }
                consumeEnumDeclaration();
                break;
            case //$NON-NLS-1$
            594:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumHeader ::= EnumHeaderName ClassHeaderImplementsopt");
                }
                consumeEnumHeader();
                break;
            case //$NON-NLS-1$
            595:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumHeaderName ::= Modifiersopt enum Identifier");
                }
                consumeEnumHeaderName();
                break;
            case //$NON-NLS-1$
            596:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumHeaderName ::= Modifiersopt enum Identifier...");
                }
                consumeEnumHeaderNameWithTypeParameters();
                break;
            case //$NON-NLS-1$
            597:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumBody ::= LBRACE EnumBodyDeclarationsopt RBRACE");
                }
                consumeEnumBodyNoConstants();
                break;
            case //$NON-NLS-1$
            598:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumBody ::= LBRACE COMMA EnumBodyDeclarationsopt...");
                }
                consumeEnumBodyNoConstants();
                break;
            case //$NON-NLS-1$
            599:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumBody ::= LBRACE EnumConstants COMMA...");
                }
                consumeEnumBodyWithConstants();
                break;
            case //$NON-NLS-1$
            600:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumBody ::= LBRACE EnumConstants...");
                }
                consumeEnumBodyWithConstants();
                break;
            case //$NON-NLS-1$
            602:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumConstants ::= EnumConstants COMMA EnumConstant");
                }
                consumeEnumConstants();
                break;
            case //$NON-NLS-1$
            603:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumConstantHeaderName ::= Modifiersopt Identifier");
                }
                consumeEnumConstantHeaderName();
                break;
            case //$NON-NLS-1$
            604:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumConstantHeader ::= EnumConstantHeaderName...");
                }
                consumeEnumConstantHeader();
                break;
            case //$NON-NLS-1$
            605:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumConstant ::= EnumConstantHeader ForceNoDiet...");
                }
                consumeEnumConstantWithClassBody();
                break;
            case //$NON-NLS-1$
            606:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumConstant ::= EnumConstantHeader");
                }
                consumeEnumConstantNoClassBody();
                break;
            case //$NON-NLS-1$
            607:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Arguments ::= LPAREN ArgumentListopt RPAREN");
                }
                consumeArguments();
                break;
            case //$NON-NLS-1$
            608:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Argumentsopt ::=");
                }
                consumeEmptyArguments();
                break;
            case //$NON-NLS-1$
            610:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumDeclarations ::= SEMICOLON ClassBodyDeclarationsopt");
                }
                consumeEnumDeclarations();
                break;
            case //$NON-NLS-1$
            611:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumBodyDeclarationsopt ::=");
                }
                consumeEmptyEnumDeclarations();
                break;
            case //$NON-NLS-1$
            613:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnhancedForStatement ::= EnhancedForStatementHeader...");
                }
                consumeEnhancedForStatement();
                break;
            case //$NON-NLS-1$
            614:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnhancedForStatementNoShortIf ::=...");
                }
                consumeEnhancedForStatement();
                break;
            case //$NON-NLS-1$
            615:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnhancedForStatementHeaderInit ::= for LPAREN Type...");
                }
                consumeEnhancedForStatementHeaderInit(false);
                break;
            case //$NON-NLS-1$
            616:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnhancedForStatementHeaderInit ::= for LPAREN Modifiers");
                }
                consumeEnhancedForStatementHeaderInit(true);
                break;
            case //$NON-NLS-1$
            617:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnhancedForStatementHeader ::=...");
                }
                consumeEnhancedForStatementHeader();
                break;
            case //$NON-NLS-1$
            618:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SingleStaticImportDeclaration ::=...");
                }
                consumeImportDeclaration();
                break;
            case //$NON-NLS-1$
            619:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SingleStaticImportDeclarationName ::= import static Name");
                }
                consumeSingleStaticImportDeclarationName();
                break;
            case //$NON-NLS-1$
            620:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("StaticImportOnDemandDeclaration ::=...");
                }
                consumeImportDeclaration();
                break;
            case //$NON-NLS-1$
            621:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("StaticImportOnDemandDeclarationName ::= import static...");
                }
                consumeStaticImportOnDemandDeclarationName();
                break;
            case //$NON-NLS-1$
            622:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeArguments ::= LESS TypeArgumentList1");
                }
                consumeTypeArguments();
                break;
            case //$NON-NLS-1$
            623:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("OnlyTypeArguments ::= LESS TypeArgumentList1");
                }
                consumeOnlyTypeArguments();
                break;
            case //$NON-NLS-1$
            625:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeArgumentList1 ::= TypeArgumentList COMMA...");
                }
                consumeTypeArgumentList1();
                break;
            case //$NON-NLS-1$
            627:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeArgumentList ::= TypeArgumentList COMMA TypeArgument");
                }
                consumeTypeArgumentList();
                break;
            case //$NON-NLS-1$
            628:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeArgument ::= ReferenceType");
                }
                consumeTypeArgument();
                break;
            case //$NON-NLS-1$
            632:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceType1 ::= ReferenceType GREATER");
                }
                consumeReferenceType1();
                break;
            case //$NON-NLS-1$
            633:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceType1 ::= ClassOrInterface LESS...");
                }
                consumeTypeArgumentReferenceType1();
                break;
            case //$NON-NLS-1$
            635:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeArgumentList2 ::= TypeArgumentList COMMA...");
                }
                consumeTypeArgumentList2();
                break;
            case //$NON-NLS-1$
            638:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceType2 ::= ReferenceType RIGHT_SHIFT");
                }
                consumeReferenceType2();
                break;
            case //$NON-NLS-1$
            639:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceType2 ::= ClassOrInterface LESS...");
                }
                consumeTypeArgumentReferenceType2();
                break;
            case //$NON-NLS-1$
            641:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeArgumentList3 ::= TypeArgumentList COMMA...");
                }
                consumeTypeArgumentList3();
                break;
            case //$NON-NLS-1$
            644:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceType3 ::= ReferenceType UNSIGNED_RIGHT_SHIFT");
                }
                consumeReferenceType3();
                break;
            case //$NON-NLS-1$
            645:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Wildcard ::= TypeAnnotationsopt QUESTION");
                }
                consumeWildcard();
                break;
            case //$NON-NLS-1$
            646:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Wildcard ::= TypeAnnotationsopt QUESTION WildcardBounds");
                }
                consumeWildcardWithBounds();
                break;
            case //$NON-NLS-1$
            647:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WildcardBounds ::= extends ReferenceType");
                }
                consumeWildcardBoundsExtends();
                break;
            case //$NON-NLS-1$
            648:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WildcardBounds ::= super ReferenceType");
                }
                consumeWildcardBoundsSuper();
                break;
            case //$NON-NLS-1$
            649:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Wildcard1 ::= TypeAnnotationsopt QUESTION GREATER");
                }
                consumeWildcard1();
                break;
            case //$NON-NLS-1$
            650:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Wildcard1 ::= TypeAnnotationsopt QUESTION...");
                }
                consumeWildcard1WithBounds();
                break;
            case //$NON-NLS-1$
            651:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WildcardBounds1 ::= extends ReferenceType1");
                }
                consumeWildcardBounds1Extends();
                break;
            case //$NON-NLS-1$
            652:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WildcardBounds1 ::= super ReferenceType1");
                }
                consumeWildcardBounds1Super();
                break;
            case //$NON-NLS-1$
            653:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Wildcard2 ::= TypeAnnotationsopt QUESTION RIGHT_SHIFT");
                }
                consumeWildcard2();
                break;
            case //$NON-NLS-1$
            654:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Wildcard2 ::= TypeAnnotationsopt QUESTION...");
                }
                consumeWildcard2WithBounds();
                break;
            case //$NON-NLS-1$
            655:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WildcardBounds2 ::= extends ReferenceType2");
                }
                consumeWildcardBounds2Extends();
                break;
            case //$NON-NLS-1$
            656:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WildcardBounds2 ::= super ReferenceType2");
                }
                consumeWildcardBounds2Super();
                break;
            case //$NON-NLS-1$
            657:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Wildcard3 ::= TypeAnnotationsopt QUESTION...");
                }
                consumeWildcard3();
                break;
            case //$NON-NLS-1$
            658:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Wildcard3 ::= TypeAnnotationsopt QUESTION...");
                }
                consumeWildcard3WithBounds();
                break;
            case //$NON-NLS-1$
            659:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WildcardBounds3 ::= extends ReferenceType3");
                }
                consumeWildcardBounds3Extends();
                break;
            case //$NON-NLS-1$
            660:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WildcardBounds3 ::= super ReferenceType3");
                }
                consumeWildcardBounds3Super();
                break;
            case //$NON-NLS-1$
            661:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeParameterHeader ::= TypeAnnotationsopt Identifier");
                }
                consumeTypeParameterHeader();
                break;
            case //$NON-NLS-1$
            662:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeParameters ::= LESS TypeParameterList1");
                }
                consumeTypeParameters();
                break;
            case //$NON-NLS-1$
            664:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeParameterList ::= TypeParameterList COMMA...");
                }
                consumeTypeParameterList();
                break;
            case //$NON-NLS-1$
            666:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeParameter ::= TypeParameterHeader extends...");
                }
                consumeTypeParameterWithExtends();
                break;
            case //$NON-NLS-1$
            667:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeParameter ::= TypeParameterHeader extends...");
                }
                consumeTypeParameterWithExtendsAndBounds();
                break;
            case //$NON-NLS-1$
            669:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditionalBoundList ::= AdditionalBoundList...");
                }
                consumeAdditionalBoundList();
                break;
            case //$NON-NLS-1$
            670:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditionalBound ::= AND ReferenceType");
                }
                consumeAdditionalBound();
                break;
            case //$NON-NLS-1$
            672:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeParameterList1 ::= TypeParameterList COMMA...");
                }
                consumeTypeParameterList1();
                break;
            case //$NON-NLS-1$
            673:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeParameter1 ::= TypeParameterHeader GREATER");
                }
                consumeTypeParameter1();
                break;
            case //$NON-NLS-1$
            674:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeParameter1 ::= TypeParameterHeader extends...");
                }
                consumeTypeParameter1WithExtends();
                break;
            case //$NON-NLS-1$
            675:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeParameter1 ::= TypeParameterHeader extends...");
                }
                consumeTypeParameter1WithExtendsAndBounds();
                break;
            case //$NON-NLS-1$
            677:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditionalBoundList1 ::= AdditionalBoundList...");
                }
                consumeAdditionalBoundList1();
                break;
            case //$NON-NLS-1$
            678:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditionalBound1 ::= AND ReferenceType1");
                }
                consumeAdditionalBound1();
                break;
            case //$NON-NLS-1$
            684:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnaryExpression_NotName ::= PLUS PushPosition...");
                }
                consumeUnaryExpression(OperatorIds.PLUS);
                break;
            case //$NON-NLS-1$
            685:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnaryExpression_NotName ::= MINUS PushPosition...");
                }
                consumeUnaryExpression(OperatorIds.MINUS);
                break;
            case //$NON-NLS-1$
            688:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnaryExpressionNotPlusMinus_NotName ::= TWIDDLE...");
                }
                consumeUnaryExpression(OperatorIds.TWIDDLE);
                break;
            case //$NON-NLS-1$
            689:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnaryExpressionNotPlusMinus_NotName ::= NOT PushPosition");
                }
                consumeUnaryExpression(OperatorIds.NOT);
                break;
            case //$NON-NLS-1$
            692:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MultiplicativeExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.MULTIPLY);
                break;
            case //$NON-NLS-1$
            693:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MultiplicativeExpression_NotName ::= Name MULTIPLY...");
                }
                consumeBinaryExpressionWithName(OperatorIds.MULTIPLY);
                break;
            case //$NON-NLS-1$
            694:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MultiplicativeExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.DIVIDE);
                break;
            case //$NON-NLS-1$
            695:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MultiplicativeExpression_NotName ::= Name DIVIDE...");
                }
                consumeBinaryExpressionWithName(OperatorIds.DIVIDE);
                break;
            case //$NON-NLS-1$
            696:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MultiplicativeExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.REMAINDER);
                break;
            case //$NON-NLS-1$
            697:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MultiplicativeExpression_NotName ::= Name REMAINDER...");
                }
                consumeBinaryExpressionWithName(OperatorIds.REMAINDER);
                break;
            case //$NON-NLS-1$
            699:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditiveExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.PLUS);
                break;
            case //$NON-NLS-1$
            700:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditiveExpression_NotName ::= Name PLUS...");
                }
                consumeBinaryExpressionWithName(OperatorIds.PLUS);
                break;
            case //$NON-NLS-1$
            701:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditiveExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.MINUS);
                break;
            case //$NON-NLS-1$
            702:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditiveExpression_NotName ::= Name MINUS...");
                }
                consumeBinaryExpressionWithName(OperatorIds.MINUS);
                break;
            case //$NON-NLS-1$
            704:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ShiftExpression_NotName ::= ShiftExpression_NotName...");
                }
                consumeBinaryExpression(OperatorIds.LEFT_SHIFT);
                break;
            case //$NON-NLS-1$
            705:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ShiftExpression_NotName ::= Name LEFT_SHIFT...");
                }
                consumeBinaryExpressionWithName(OperatorIds.LEFT_SHIFT);
                break;
            case //$NON-NLS-1$
            706:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ShiftExpression_NotName ::= ShiftExpression_NotName...");
                }
                consumeBinaryExpression(OperatorIds.RIGHT_SHIFT);
                break;
            case //$NON-NLS-1$
            707:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ShiftExpression_NotName ::= Name RIGHT_SHIFT...");
                }
                consumeBinaryExpressionWithName(OperatorIds.RIGHT_SHIFT);
                break;
            case //$NON-NLS-1$
            708:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ShiftExpression_NotName ::= ShiftExpression_NotName...");
                }
                consumeBinaryExpression(OperatorIds.UNSIGNED_RIGHT_SHIFT);
                break;
            case //$NON-NLS-1$
            709:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ShiftExpression_NotName ::= Name UNSIGNED_RIGHT_SHIFT...");
                }
                consumeBinaryExpressionWithName(OperatorIds.UNSIGNED_RIGHT_SHIFT);
                break;
            case //$NON-NLS-1$
            711:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression_NotName ::= ShiftExpression_NotName");
                }
                consumeBinaryExpression(OperatorIds.LESS);
                break;
            case //$NON-NLS-1$
            712:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression_NotName ::= Name LESS...");
                }
                consumeBinaryExpressionWithName(OperatorIds.LESS);
                break;
            case //$NON-NLS-1$
            713:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression_NotName ::= ShiftExpression_NotName");
                }
                consumeBinaryExpression(OperatorIds.GREATER);
                break;
            case //$NON-NLS-1$
            714:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression_NotName ::= Name GREATER...");
                }
                consumeBinaryExpressionWithName(OperatorIds.GREATER);
                break;
            case //$NON-NLS-1$
            715:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.LESS_EQUAL);
                break;
            case //$NON-NLS-1$
            716:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression_NotName ::= Name LESS_EQUAL...");
                }
                consumeBinaryExpressionWithName(OperatorIds.LESS_EQUAL);
                break;
            case //$NON-NLS-1$
            717:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.GREATER_EQUAL);
                break;
            case //$NON-NLS-1$
            718:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression_NotName ::= Name GREATER_EQUAL...");
                }
                consumeBinaryExpressionWithName(OperatorIds.GREATER_EQUAL);
                break;
            case //$NON-NLS-1$
            720:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InstanceofExpression_NotName ::= Name instanceof...");
                }
                consumeInstanceOfExpressionWithName();
                break;
            case //$NON-NLS-1$
            721:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InstanceofExpression_NotName ::=...");
                }
                consumeInstanceOfExpression();
                break;
            case //$NON-NLS-1$
            723:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EqualityExpression_NotName ::=...");
                }
                consumeEqualityExpression(OperatorIds.EQUAL_EQUAL);
                break;
            case //$NON-NLS-1$
            724:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EqualityExpression_NotName ::= Name EQUAL_EQUAL...");
                }
                consumeEqualityExpressionWithName(OperatorIds.EQUAL_EQUAL);
                break;
            case //$NON-NLS-1$
            725:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EqualityExpression_NotName ::=...");
                }
                consumeEqualityExpression(OperatorIds.NOT_EQUAL);
                break;
            case //$NON-NLS-1$
            726:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EqualityExpression_NotName ::= Name NOT_EQUAL...");
                }
                consumeEqualityExpressionWithName(OperatorIds.NOT_EQUAL);
                break;
            case //$NON-NLS-1$
            728:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AndExpression_NotName ::= AndExpression_NotName AND...");
                }
                consumeBinaryExpression(OperatorIds.AND);
                break;
            case //$NON-NLS-1$
            729:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AndExpression_NotName ::= Name AND EqualityExpression");
                }
                consumeBinaryExpressionWithName(OperatorIds.AND);
                break;
            case //$NON-NLS-1$
            731:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExclusiveOrExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.XOR);
                break;
            case //$NON-NLS-1$
            732:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExclusiveOrExpression_NotName ::= Name XOR AndExpression");
                }
                consumeBinaryExpressionWithName(OperatorIds.XOR);
                break;
            case //$NON-NLS-1$
            734:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InclusiveOrExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.OR);
                break;
            case //$NON-NLS-1$
            735:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InclusiveOrExpression_NotName ::= Name OR...");
                }
                consumeBinaryExpressionWithName(OperatorIds.OR);
                break;
            case //$NON-NLS-1$
            737:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConditionalAndExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.AND_AND);
                break;
            case //$NON-NLS-1$
            738:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConditionalAndExpression_NotName ::= Name AND_AND...");
                }
                consumeBinaryExpressionWithName(OperatorIds.AND_AND);
                break;
            case //$NON-NLS-1$
            740:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConditionalOrExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.OR_OR);
                break;
            case //$NON-NLS-1$
            741:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConditionalOrExpression_NotName ::= Name OR_OR...");
                }
                consumeBinaryExpressionWithName(OperatorIds.OR_OR);
                break;
            case //$NON-NLS-1$
            743:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConditionalExpression_NotName ::=...");
                }
                consumeConditionalExpression(OperatorIds.QUESTIONCOLON);
                break;
            case //$NON-NLS-1$
            744:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConditionalExpression_NotName ::= Name QUESTION...");
                }
                consumeConditionalExpressionWithName(OperatorIds.QUESTIONCOLON);
                break;
            case //$NON-NLS-1$
            748:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationTypeDeclarationHeaderName ::= Modifiers AT...");
                }
                consumeAnnotationTypeDeclarationHeaderName();
                break;
            case //$NON-NLS-1$
            749:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationTypeDeclarationHeaderName ::= Modifiers AT...");
                }
                consumeAnnotationTypeDeclarationHeaderNameWithTypeParameters();
                break;
            case //$NON-NLS-1$
            750:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationTypeDeclarationHeaderName ::= AT...");
                }
                consumeAnnotationTypeDeclarationHeaderNameWithTypeParameters();
                break;
            case //$NON-NLS-1$
            751:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationTypeDeclarationHeaderName ::= AT...");
                }
                consumeAnnotationTypeDeclarationHeaderName();
                break;
            case //$NON-NLS-1$
            752:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationTypeDeclarationHeader ::=...");
                }
                consumeAnnotationTypeDeclarationHeader();
                break;
            case //$NON-NLS-1$
            753:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationTypeDeclaration ::=...");
                }
                consumeAnnotationTypeDeclaration();
                break;
            case //$NON-NLS-1$
            755:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationTypeMemberDeclarationsopt ::=");
                }
                consumeEmptyAnnotationTypeMemberDeclarationsopt();
                break;
            case //$NON-NLS-1$
            756:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationTypeMemberDeclarationsopt ::= NestedType...");
                }
                consumeAnnotationTypeMemberDeclarationsopt();
                break;
            case //$NON-NLS-1$
            758:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationTypeMemberDeclarations ::=...");
                }
                consumeAnnotationTypeMemberDeclarations();
                break;
            case //$NON-NLS-1$
            759:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationMethodHeaderName ::= Modifiersopt...");
                }
                consumeMethodHeaderNameWithTypeParameters(true);
                break;
            case //$NON-NLS-1$
            760:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationMethodHeaderName ::= Modifiersopt Type...");
                }
                consumeMethodHeaderName(true);
                break;
            case //$NON-NLS-1$
            761:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationMethodHeaderDefaultValueopt ::=");
                }
                consumeEmptyMethodHeaderDefaultValue();
                break;
            case //$NON-NLS-1$
            762:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationMethodHeaderDefaultValueopt ::= DefaultValue");
                }
                consumeMethodHeaderDefaultValue();
                break;
            case //$NON-NLS-1$
            763:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationMethodHeader ::= AnnotationMethodHeaderName...");
                }
                consumeMethodHeader();
                break;
            case //$NON-NLS-1$
            764:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationTypeMemberDeclaration ::=...");
                }
                consumeAnnotationTypeMemberDeclaration();
                break;
            case //$NON-NLS-1$
            772:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationName ::= AT UnannotatableName");
                }
                consumeAnnotationName();
                break;
            case //$NON-NLS-1$
            773:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("NormalAnnotation ::= AnnotationName LPAREN...");
                }
                consumeNormalAnnotation(false);
                break;
            case //$NON-NLS-1$
            774:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MemberValuePairsopt ::=");
                }
                consumeEmptyMemberValuePairsopt();
                break;
            case //$NON-NLS-1$
            777:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MemberValuePairs ::= MemberValuePairs COMMA...");
                }
                consumeMemberValuePairs();
                break;
            case //$NON-NLS-1$
            778:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MemberValuePair ::= SimpleName EQUAL EnterMemberValue...");
                }
                consumeMemberValuePair();
                break;
            case //$NON-NLS-1$
            779:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnterMemberValue ::=");
                }
                consumeEnterMemberValue();
                break;
            case //$NON-NLS-1$
            780:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExitMemberValue ::=");
                }
                consumeExitMemberValue();
                break;
            case //$NON-NLS-1$
            782:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MemberValue ::= Name");
                }
                consumeMemberValueAsName();
                break;
            case //$NON-NLS-1$
            785:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MemberValueArrayInitializer ::=...");
                }
                consumeMemberValueArrayInitializer();
                break;
            case //$NON-NLS-1$
            786:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MemberValueArrayInitializer ::=...");
                }
                consumeMemberValueArrayInitializer();
                break;
            case //$NON-NLS-1$
            787:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MemberValueArrayInitializer ::=...");
                }
                consumeEmptyMemberValueArrayInitializer();
                break;
            case //$NON-NLS-1$
            788:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MemberValueArrayInitializer ::=...");
                }
                consumeEmptyMemberValueArrayInitializer();
                break;
            case //$NON-NLS-1$
            789:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnterMemberValueArrayInitializer ::=");
                }
                consumeEnterMemberValueArrayInitializer();
                break;
            case //$NON-NLS-1$
            791:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MemberValues ::= MemberValues COMMA MemberValue");
                }
                consumeMemberValues();
                break;
            case //$NON-NLS-1$
            792:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MarkerAnnotation ::= AnnotationName");
                }
                consumeMarkerAnnotation(false);
                break;
            case //$NON-NLS-1$
            793:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SingleMemberAnnotationMemberValue ::= MemberValue");
                }
                consumeSingleMemberAnnotationMemberValue();
                break;
            case //$NON-NLS-1$
            794:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SingleMemberAnnotation ::= AnnotationName LPAREN...");
                }
                consumeSingleMemberAnnotation(false);
                break;
            case //$NON-NLS-1$
            795:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RecoveryMethodHeaderName ::= Modifiersopt TypeParameters");
                }
                consumeRecoveryMethodHeaderNameWithTypeParameters();
                break;
            case //$NON-NLS-1$
            796:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RecoveryMethodHeaderName ::= Modifiersopt Type...");
                }
                consumeRecoveryMethodHeaderName();
                break;
            case //$NON-NLS-1$
            797:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RecoveryMethodHeaderName ::= ModifiersWithDefault...");
                }
                consumeRecoveryMethodHeaderNameWithTypeParameters();
                break;
            case //$NON-NLS-1$
            798:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RecoveryMethodHeaderName ::= ModifiersWithDefault Type");
                }
                consumeRecoveryMethodHeaderName();
                break;
            case //$NON-NLS-1$
            799:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RecoveryMethodHeader ::= RecoveryMethodHeaderName...");
                }
                consumeMethodHeader();
                break;
            case //$NON-NLS-1$
            800:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RecoveryMethodHeader ::= RecoveryMethodHeaderName...");
                }
                consumeMethodHeader();
                break;
        }
    }

    protected void consumeVariableDeclaratorIdParameter() {
        // signal "normal" variable declarator id parameter.
        pushOnIntStack(1);
    }

    protected void consumeExplicitThisParameter(boolean isQualified) {
        // VariableDeclaratorIdOrThis ::= 'this'
        // VariableDeclaratorIdOrThis ::= UnannotatableName '.' 'this'
        // VariableDeclaratorIdOrThis ::= VariableDeclaratorId
        NameReference qualifyingNameReference = null;
        if (isQualified) {
            // By construction the qualified name is unannotated here, so we should not meddle with the type annotation stack
            qualifyingNameReference = getUnspecifiedReference(false);
        }
        pushOnExpressionStack(qualifyingNameReference);
        int thisStart = this.intStack[this.intPtr--];
        pushIdentifier(ConstantPool.This, (((long) thisStart << 32)) + (thisStart + 3));
        // extended dimensions ...
        pushOnIntStack(0);
        // signal explicit this
        pushOnIntStack(0);
    }

    protected boolean isAssistParser() {
        return false;
    }

    protected void consumeNestedLambda() {
        // NestedLambda ::= $empty - we get here just after the type+parenthesis elided singleton parameter or just before the '(' of the parameter list. 
        consumeNestedType();
        this.nestedMethod[this.nestedType]++;
        LambdaExpression lambda = new LambdaExpression(this.compilationUnit.compilationResult, isAssistParser());
        pushOnAstStack(lambda);
        this.processingLambdaParameterList = true;
    }

    protected void consumeLambdaHeader() {
        // LambdaHeader ::= LambdaParameters '->'  Synthetic/fake production with a synthetic non-terminal. Body not seen yet.
        int arrowPosition = this.scanner.currentPosition - 1;
        Argument[] arguments = null;
        int length = this.astLengthStack[this.astLengthPtr--];
        this.astPtr -= length;
        //arguments
        if (length != 0) {
            System.arraycopy(this.astStack, this.astPtr + 1, arguments = new Argument[length], 0, length);
        }
        for (int i = 0; i < length; i++) {
            final Argument argument = arguments[i];
            if (argument.isReceiver()) {
                problemReporter().illegalThis(argument);
            }
            if (argument.name.length == 1 && argument.name[0] == '_')
                // true == lambdaParameter
                problemReporter().illegalUseOfUnderscoreAsAnIdentifier(argument.sourceStart, argument.sourceEnd, true);
        }
        LambdaExpression lexp = (LambdaExpression) this.astStack[this.astPtr];
        lexp.setArguments(arguments);
        lexp.setArrowPosition(arrowPosition);
        // ')' position or identifier position.
        lexp.sourceEnd = this.intStack[this.intPtr--];
        // '(' position or identifier position.
        lexp.sourceStart = this.intStack[this.intPtr--];
        lexp.hasParentheses = (this.scanner.getSource()[lexp.sourceStart] == '(');
        // not necessary really.
        this.listLength -= arguments == null ? 0 : arguments.length;
        this.processingLambdaParameterList = false;
        if (this.currentElement != null) {
            // we don't want the typed formal parameters to be processed by recovery.
            this.lastCheckPoint = arrowPosition + 1;
            this.currentElement.lambdaNestLevel++;
        }
    }

    protected void consumeLambdaExpression() {
        // LambdaExpression ::= LambdaHeader LambdaBody
        this.nestedType--;
        // pop length for LambdaBody (always 1)
        this.astLengthPtr--;
        Statement body = (Statement) this.astStack[this.astPtr--];
        if (body instanceof Block) {
            if (this.options.ignoreMethodBodies) {
                Statement oldBody = body;
                body = new Block(0);
                body.sourceStart = oldBody.sourceStart;
                body.sourceEnd = oldBody.sourceEnd;
            }
        }
        LambdaExpression lexp = (LambdaExpression) this.astStack[this.astPtr--];
        this.astLengthPtr--;
        lexp.setBody(body);
        lexp.sourceEnd = body.sourceEnd;
        if (body instanceof Expression) {
            Expression expression = (Expression) body;
            expression.statementEnd = body.sourceEnd;
        }
        if (!this.parsingJava8Plus) {
            problemReporter().lambdaExpressionsNotBelow18(lexp);
        }
        pushOnExpressionStack(lexp);
        if (this.currentElement != null) {
            this.lastCheckPoint = body.sourceEnd + 1;
            this.currentElement.lambdaNestLevel--;
        }
        this.referenceContext.compilationResult().hasFunctionalTypes = true;
        markEnclosingMemberWithLocalOrFunctionalType(LocalTypeKind.LAMBDA);
        if (lexp.compilationResult.getCompilationUnit() == null) {
            // unit built out of model. Stash a textual representation of lambda to enable LE.copy().
            int length = lexp.sourceEnd - lexp.sourceStart + 1;
            System.arraycopy(this.scanner.getSource(), lexp.sourceStart, lexp.text = new char[length], 0, length);
        }
    }

    protected Argument typeElidedArgument() {
        this.identifierLengthPtr--;
        char[] identifierName = this.identifierStack[this.identifierPtr];
        long namePositions = this.identifierPositionStack[this.identifierPtr--];
        Argument arg = new Argument(identifierName, namePositions, // elided type
        null, ClassFileConstants.AccDefault, true);
        arg.declarationSourceStart = (int) (namePositions >>> 32);
        return arg;
    }

    protected void consumeTypeElidedLambdaParameter(boolean parenthesized) {
        // LambdaParameters ::= Identifier
        // TypeElidedFormalParameter ::= Modifiersopt Identifier
        int modifier = ClassFileConstants.AccDefault;
        int annotationLength = 0;
        int modifiersStart = 0;
        if (// The grammar is permissive enough to allow optional modifiers for the parenthesized version, they should be rejected if present. 
        parenthesized) {
            modifiersStart = this.intStack[this.intPtr--];
            modifier = this.intStack[this.intPtr--];
            // pop annotations
            annotationLength = this.expressionLengthStack[this.expressionLengthPtr--];
            this.expressionPtr -= annotationLength;
        }
        Argument arg = typeElidedArgument();
        if (modifier != ClassFileConstants.AccDefault || annotationLength != 0) {
            problemReporter().illegalModifiersForElidedType(arg);
            arg.declarationSourceStart = modifiersStart;
        }
        if (// in the absence of '(' and ')', record positions.
        !parenthesized) {
            pushOnIntStack(arg.declarationSourceStart);
            pushOnIntStack(arg.declarationSourceEnd);
        }
        pushOnAstStack(arg);
        // not relevant really.
        this.listLength++;
    }

    protected void consumeElidedLeftBraceAndReturn() {
        /* ElidedLeftBraceAndReturn ::= $empty
	   Alert ! Sleight of hand - Part I : Record stack depth now that we are at the state with the kernel item
	   ElidedLeftBraceAndReturn .Expression ElidedSemicolonAndRightBrace
	*/
        int stackLength = this.stateStackLengthStack.length;
        if (++this.valueLambdaNestDepth >= stackLength) {
            System.arraycopy(this.stateStackLengthStack, 0, this.stateStackLengthStack = new int[stackLength + 4], 0, stackLength);
        }
        this.stateStackLengthStack[this.valueLambdaNestDepth] = this.stateStackTop;
    }

    protected void consumeExpression() {
        /* Expression ::= AssignmentExpression
	   Alert ! Sleight of hand - Part II: See if we are at the state with the item: "ElidedLeftBraceAndReturn Expression .ElidedSemicolonAndRightBrace"
       If so, push back the current token into the lexer stream, materialize the synthetic terminal marker symbol, switch and continue.
    */
        if (this.valueLambdaNestDepth >= 0 && this.stateStackLengthStack[this.valueLambdaNestDepth] == this.stateStackTop - 1) {
            this.valueLambdaNestDepth--;
            this.scanner.ungetToken(this.currentToken);
            // conjure a rabbit out of the hat ...
            this.currentToken = TokenNameElidedSemicolonAndRightBrace;
            Expression exp = this.expressionStack[this.expressionPtr--];
            this.expressionLengthPtr--;
            pushOnAstStack(exp);
        }
    }

    protected void consumeIdentifierOrNew(boolean newForm) {
        // IdentifierOrNew ::= 'new'
        if (newForm) {
            int newStart = this.intStack[this.intPtr--];
            pushIdentifier(ConstantPool.Init, (((long) newStart << 32)) + (newStart + 2));
        }
    }

    protected void consumeEmptyTypeArguments() {
        // NonWildTypeArgumentsopt ::= $empty
        // signal absence of type arguments.
        pushOnGenericsLengthStack(0);
    }

    public ReferenceExpression newReferenceExpression() {
        return new ReferenceExpression();
    }

    protected void consumeReferenceExpressionTypeForm(boolean isPrimitive) {
        // actually Name or Type form.
        // ReferenceExpression ::= PrimitiveType Dims '::' NonWildTypeArgumentsopt IdentifierOrNew
        // ReferenceExpression ::= Name Dimsopt '::' NonWildTypeArgumentsopt IdentifierOrNew
        ReferenceExpression referenceExpression = newReferenceExpression();
        TypeReference[] typeArguments = null;
        char[] selector;
        int sourceEnd;
        sourceEnd = (int) this.identifierPositionStack[this.identifierPtr];
        referenceExpression.nameSourceStart = (int) (this.identifierPositionStack[this.identifierPtr] >>> 32);
        selector = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        if (length > 0) {
            this.genericsPtr -= length;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments = new TypeReference[length], 0, length);
            // pop type arguments source start.
            this.intPtr--;
        }
        int dimension = this.intStack[this.intPtr--];
        boolean typeAnnotatedName = false;
        for (int i = this.identifierLengthStack[this.identifierLengthPtr], j = 0; i > 0 && this.typeAnnotationLengthPtr >= 0; --i, j++) {
            length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr - j];
            if (length != 0) {
                typeAnnotatedName = true;
                break;
            }
        }
        if (dimension > 0 || typeAnnotatedName) {
            if (!isPrimitive) {
                pushOnGenericsLengthStack(0);
                pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
            }
            referenceExpression.initialize(this.compilationUnit.compilationResult, getTypeReference(dimension), typeArguments, selector, sourceEnd);
        } else {
            referenceExpression.initialize(this.compilationUnit.compilationResult, getUnspecifiedReference(), typeArguments, selector, sourceEnd);
        }
        consumeReferenceExpression(referenceExpression);
    }

    protected void consumeReferenceExpressionPrimaryForm() {
        // ReferenceExpression ::= Primary '::' NonWildTypeArgumentsopt Identifier
        ReferenceExpression referenceExpression = newReferenceExpression();
        TypeReference[] typeArguments = null;
        char[] selector;
        int sourceEnd;
        sourceEnd = (int) this.identifierPositionStack[this.identifierPtr];
        referenceExpression.nameSourceStart = (int) (this.identifierPositionStack[this.identifierPtr] >>> 32);
        selector = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        if (length > 0) {
            this.genericsPtr -= length;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments = new TypeReference[length], 0, length);
            // pop type arguments source start.
            this.intPtr--;
        }
        Expression primary = this.expressionStack[this.expressionPtr--];
        this.expressionLengthPtr--;
        referenceExpression.initialize(this.compilationUnit.compilationResult, primary, typeArguments, selector, sourceEnd);
        consumeReferenceExpression(referenceExpression);
    }

    protected void consumeReferenceExpressionSuperForm() {
        // ReferenceExpression ::= 'super' '::' NonWildTypeArgumentsopt Identifier
        ReferenceExpression referenceExpression = newReferenceExpression();
        TypeReference[] typeArguments = null;
        char[] selector;
        int sourceEnd;
        sourceEnd = (int) this.identifierPositionStack[this.identifierPtr];
        referenceExpression.nameSourceStart = (int) (this.identifierPositionStack[this.identifierPtr] >>> 32);
        selector = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        if (length > 0) {
            this.genericsPtr -= length;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments = new TypeReference[length], 0, length);
            // pop type arguments source start.
            this.intPtr--;
        }
        SuperReference superReference = new SuperReference(this.intStack[this.intPtr--], this.endPosition);
        referenceExpression.initialize(this.compilationUnit.compilationResult, superReference, typeArguments, selector, sourceEnd);
        consumeReferenceExpression(referenceExpression);
    }

    protected void consumeReferenceExpression(ReferenceExpression referenceExpression) {
        pushOnExpressionStack(referenceExpression);
        if (!this.parsingJava8Plus) {
            problemReporter().referenceExpressionsNotBelow18(referenceExpression);
        }
        if (referenceExpression.compilationResult.getCompilationUnit() == null) {
            // unit built out of model. Stash a textual representation to enable RE.copy().
            int length = referenceExpression.sourceEnd - referenceExpression.sourceStart + 1;
            System.arraycopy(this.scanner.getSource(), referenceExpression.sourceStart, referenceExpression.text = new char[length], 0, length);
        }
        this.referenceContext.compilationResult().hasFunctionalTypes = true;
        markEnclosingMemberWithLocalOrFunctionalType(LocalTypeKind.METHOD_REFERENCE);
    }

    protected void consumeReferenceExpressionTypeArgumentsAndTrunk(boolean qualified) {
        // ReferenceExpressionTypeArgumentsAndTrunk ::= OnlyTypeArguments Dimsopt ==> qualified == false
        // ReferenceExpressionTypeArgumentsAndTrunk ::= OnlyTypeArguments '.' ClassOrInterfaceType Dimsopt ==> qualified == true
        pushOnIntStack(qualified ? 1 : 0);
        // mark position of :: as the end of type
        pushOnIntStack(this.scanner.startPosition - 1);
    }

    protected void consumeReferenceExpressionGenericTypeForm() {
        // ReferenceExpression ::= Name BeginTypeArguments ReferenceExpressionTypeArgumentsAndTrunk '::' NonWildTypeArgumentsopt IdentifierOrNew
        ReferenceExpression referenceExpression = newReferenceExpression();
        TypeReference type;
        TypeReference[] typeArguments = null;
        char[] selector;
        int sourceEnd;
        sourceEnd = (int) this.identifierPositionStack[this.identifierPtr];
        referenceExpression.nameSourceStart = (int) (this.identifierPositionStack[this.identifierPtr] >>> 32);
        selector = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        if (length > 0) {
            this.genericsPtr -= length;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments = new TypeReference[length], 0, length);
            // pop type arguments source start.
            this.intPtr--;
        }
        int typeSourceEnd = this.intStack[this.intPtr--];
        boolean qualified = this.intStack[this.intPtr--] != 0;
        int dims = this.intStack[this.intPtr--];
        if (qualified) {
            Annotation[][] annotationsOnDimensions = dims == 0 ? null : getAnnotationsOnDimensions(dims);
            TypeReference rightSide = getTypeReference(0);
            type = computeQualifiedGenericsFromRightSide(rightSide, dims, annotationsOnDimensions);
        } else {
            pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
            type = getTypeReference(dims);
        }
        // pop '<' position
        this.intPtr--;
        type.sourceEnd = typeSourceEnd;
        referenceExpression.initialize(this.compilationUnit.compilationResult, type, typeArguments, selector, sourceEnd);
        consumeReferenceExpression(referenceExpression);
    }

    protected void consumeEnterInstanceCreationArgumentList() {
        return;
    }

    protected void consumeSimpleAssertStatement() {
        // AssertStatement ::= 'assert' Expression ';'
        this.expressionLengthPtr--;
        pushOnAstStack(new AssertStatement(this.expressionStack[this.expressionPtr--], this.intStack[this.intPtr--]));
    }

    protected void consumeSingleMemberAnnotation(boolean isTypeAnnotation) {
        // SingleMemberTypeAnnotation ::= TypeAnnotationName '(' SingleMemberAnnotationMemberValue ')'
        // SingleMemberAnnotation ::= AnnotationName '(' SingleMemberAnnotationMemberValue ')'
        SingleMemberAnnotation singleMemberAnnotation = null;
        int oldIndex = this.identifierPtr;
        TypeReference typeReference = getAnnotationType();
        singleMemberAnnotation = new SingleMemberAnnotation(typeReference, this.intStack[this.intPtr--]);
        singleMemberAnnotation.memberValue = this.expressionStack[this.expressionPtr--];
        this.expressionLengthPtr--;
        singleMemberAnnotation.declarationSourceEnd = this.rParenPos;
        if (isTypeAnnotation) {
            pushOnTypeAnnotationStack(singleMemberAnnotation);
        } else {
            pushOnExpressionStack(singleMemberAnnotation);
        }
        if (this.currentElement != null) {
            annotationRecoveryCheckPoint(singleMemberAnnotation.sourceStart, singleMemberAnnotation.declarationSourceEnd);
            if (this.currentElement instanceof RecoveredAnnotation) {
                this.currentElement = ((RecoveredAnnotation) this.currentElement).addAnnotation(singleMemberAnnotation, oldIndex);
            }
        }
        if (!this.statementRecoveryActivated && this.options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            problemReporter().invalidUsageOfAnnotation(singleMemberAnnotation);
        }
        this.recordStringLiterals = true;
    }

    protected void consumeSingleMemberAnnotationMemberValue() {
        // this rule is used for syntax recovery only
        if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
            RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation) this.currentElement;
            recoveredAnnotation.setKind(RecoveredAnnotation.SINGLE_MEMBER);
        }
    }

    protected void consumeSingleResource() {
    // Resources ::= Resource
    }

    protected void consumeSingleStaticImportDeclarationName() {
        // SingleTypeImportDeclarationName ::= 'import' Name RejectTypeAnnotations
        /* push an ImportRef build from the last name
	stored in the identifier stack. */
        ImportReference impt;
        int length;
        char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        pushOnAstStack(impt = new ImportReference(tokens, positions, false, ClassFileConstants.AccStatic));
        this.modifiers = ClassFileConstants.AccDefault;
        // <-- see comment into modifiersFlag(int)
        this.modifiersSourceStart = -1;
        if (this.currentToken == TokenNameSEMICOLON) {
            impt.declarationSourceEnd = this.scanner.currentPosition - 1;
        } else {
            impt.declarationSourceEnd = impt.sourceEnd;
        }
        impt.declarationEnd = impt.declarationSourceEnd;
        //this.endPosition is just before the ;
        impt.declarationSourceStart = this.intStack[this.intPtr--];
        if (!this.statementRecoveryActivated && this.options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            // convert the static import reference to a non-static importe reference
            impt.modifiers = ClassFileConstants.AccDefault;
            problemReporter().invalidUsageOfStaticImports(impt);
        }
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.add(impt, 0);
            this.lastIgnoredToken = -1;
            // used to avoid branching back into the regular automaton
            this.restartRecovery = true;
        }
    }

    protected void consumeSingleTypeImportDeclarationName() {
        // SingleTypeImportDeclarationName ::= 'import' Name
        /* push an ImportRef build from the last name
	stored in the identifier stack. */
        ImportReference impt;
        int length;
        char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        pushOnAstStack(impt = new ImportReference(tokens, positions, false, ClassFileConstants.AccDefault));
        if (this.currentToken == TokenNameSEMICOLON) {
            impt.declarationSourceEnd = this.scanner.currentPosition - 1;
        } else {
            impt.declarationSourceEnd = impt.sourceEnd;
        }
        impt.declarationEnd = impt.declarationSourceEnd;
        //this.endPosition is just before the ;
        impt.declarationSourceStart = this.intStack[this.intPtr--];
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.add(impt, 0);
            this.lastIgnoredToken = -1;
            // used to avoid branching back into the regular automaton
            this.restartRecovery = true;
        }
    }

    protected void consumeStatementBreak() {
        // BreakStatement ::= 'break' ';'
        // break pushes a position on this.intStack in case there is no label
        pushOnAstStack(new BreakStatement(null, this.intStack[this.intPtr--], this.endStatementPosition));
        if (this.pendingRecoveredType != null) {
            // The break statement must be replace by the local type.
            if (this.pendingRecoveredType.allocation == null && this.endPosition <= this.pendingRecoveredType.declarationSourceEnd) {
                this.astStack[this.astPtr] = this.pendingRecoveredType;
                this.pendingRecoveredType = null;
                return;
            }
            this.pendingRecoveredType = null;
        }
    }

    protected void consumeStatementBreakWithLabel() {
        // BreakStatement ::= 'break' Identifier ';'
        // break pushs a position on this.intStack in case there is no label
        pushOnAstStack(new BreakStatement(this.identifierStack[this.identifierPtr--], this.intStack[this.intPtr--], this.endStatementPosition));
        this.identifierLengthPtr--;
    }

    protected void consumeStatementCatch() {
        // CatchClause ::= 'catch' '(' FormalParameter ')'    Block
        //catch are stored directly into the Try
        //has they always comes two by two....
        //we remove one entry from the astlengthPtr.
        //The construction of the try statement must
        //then fetch the catches using  2*i and 2*i + 1
        this.astLengthPtr--;
        // reset formalParameter counter (incremented for catch variable)
        this.listLength = 0;
    }

    protected void consumeStatementContinue() {
        // ContinueStatement ::= 'continue' ';'
        // continue pushs a position on this.intStack in case there is no label
        pushOnAstStack(new ContinueStatement(null, this.intStack[this.intPtr--], this.endStatementPosition));
    }

    protected void consumeStatementContinueWithLabel() {
        // ContinueStatement ::= 'continue' Identifier ';'
        // continue pushs a position on this.intStack in case there is no label
        pushOnAstStack(new ContinueStatement(this.identifierStack[this.identifierPtr--], this.intStack[this.intPtr--], this.endStatementPosition));
        this.identifierLengthPtr--;
    }

    protected void consumeStatementDo() {
        // DoStatement ::= 'do' Statement 'while' '(' Expression ')' ';'
        //the 'while' pushes a value on this.intStack that we need to remove
        this.intPtr--;
        Statement statement = (Statement) this.astStack[this.astPtr];
        this.expressionLengthPtr--;
        this.astStack[this.astPtr] = new DoStatement(this.expressionStack[this.expressionPtr--], statement, this.intStack[this.intPtr--], this.endStatementPosition);
    }

    protected void consumeStatementExpressionList() {
        // StatementExpressionList ::= StatementExpressionList ',' StatementExpression
        concatExpressionLists();
    }

    protected void consumeStatementFor() {
        // ForStatement ::= 'for' '(' ForInitopt ';' Expressionopt ';' ForUpdateopt ')' Statement
        // ForStatementNoShortIf ::= 'for' '(' ForInitopt ';' Expressionopt ';' ForUpdateopt ')' StatementNoShortIf
        int length;
        Expression cond = null;
        Statement[] inits, updates;
        boolean scope = true;
        //statements
        this.astLengthPtr--;
        Statement statement = (Statement) this.astStack[this.astPtr--];
        //updates are on the expresion stack
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) == 0) {
            updates = null;
        } else {
            this.expressionPtr -= length;
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, updates = new Statement[length], 0, length);
        }
        if (this.expressionLengthStack[this.expressionLengthPtr--] != 0)
            cond = this.expressionStack[this.expressionPtr--];
        //inits may be on two different stacks
        if ((length = this.astLengthStack[this.astLengthPtr--]) == 0) {
            inits = null;
            scope = false;
        } else {
            if (//on this.expressionStack
            length == -1) {
                scope = false;
                length = this.expressionLengthStack[this.expressionLengthPtr--];
                this.expressionPtr -= length;
                System.arraycopy(this.expressionStack, this.expressionPtr + 1, inits = new Statement[length], 0, length);
            } else //on this.astStack
            {
                this.astPtr -= length;
                System.arraycopy(this.astStack, this.astPtr + 1, inits = new Statement[length], 0, length);
            }
        }
        pushOnAstStack(new ForStatement(inits, cond, updates, statement, scope, this.intStack[this.intPtr--], this.endStatementPosition));
    }

    protected void consumeStatementIfNoElse() {
        // IfThenStatement ::=  'if' '(' Expression ')' Statement
        //optimize the push/pop
        this.expressionLengthPtr--;
        Statement thenStatement = (Statement) this.astStack[this.astPtr];
        this.astStack[this.astPtr] = new IfStatement(this.expressionStack[this.expressionPtr--], thenStatement, this.intStack[this.intPtr--], this.endStatementPosition);
    }

    protected void consumeStatementIfWithElse() {
        // IfThenElseStatement ::=  'if' '(' Expression ')' StatementNoShortIf 'else' Statement
        // IfThenElseStatementNoShortIf ::=  'if' '(' Expression ')' StatementNoShortIf 'else' StatementNoShortIf
        this.expressionLengthPtr--;
        // optimized {..., Then, Else } ==> {..., If }
        this.astLengthPtr--;
        //optimize the push/pop
        this.astStack[--this.astPtr] = new IfStatement(this.expressionStack[this.expressionPtr--], (Statement) this.astStack[this.astPtr], (Statement) this.astStack[this.astPtr + 1], this.intStack[this.intPtr--], this.endStatementPosition);
    }

    protected void consumeStatementLabel() {
        // LabeledStatement ::= 'Identifier' ':' Statement
        // LabeledStatementNoShortIf ::= 'Identifier' ':' StatementNoShortIf
        //optimize push/pop
        Statement statement = (Statement) this.astStack[this.astPtr];
        this.astStack[this.astPtr] = new LabeledStatement(this.identifierStack[this.identifierPtr], statement, this.identifierPositionStack[this.identifierPtr--], this.endStatementPosition);
        this.identifierLengthPtr--;
    }

    protected void consumeStatementReturn() {
        if (this.expressionLengthStack[this.expressionLengthPtr--] != 0) {
            pushOnAstStack(new ReturnStatement(this.expressionStack[this.expressionPtr--], this.intStack[this.intPtr--], this.endStatementPosition));
        } else {
            pushOnAstStack(new ReturnStatement(null, this.intStack[this.intPtr--], this.endStatementPosition));
        }
    }

    protected void consumeStatementSwitch() {
        // SwitchStatement ::= 'switch' OpenBlock '(' Expression ')' SwitchBlock
        //OpenBlock just makes the semantic action blockStart()
        //the block is inlined but a scope need to be created
        //if some declaration occurs.
        int length;
        SwitchStatement switchStatement = new SwitchStatement();
        this.expressionLengthPtr--;
        switchStatement.expression = this.expressionStack[this.expressionPtr--];
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.astPtr -= length;
            System.arraycopy(this.astStack, this.astPtr + 1, switchStatement.statements = new Statement[length], 0, length);
        }
        switchStatement.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
        pushOnAstStack(switchStatement);
        switchStatement.blockStart = this.intStack[this.intPtr--];
        switchStatement.sourceStart = this.intStack[this.intPtr--];
        switchStatement.sourceEnd = this.endStatementPosition;
        if (length == 0 && !containsComment(switchStatement.blockStart, switchStatement.sourceEnd)) {
            switchStatement.bits |= ASTNode.UndocumentedEmptyBlock;
        }
    }

    protected void consumeStatementSynchronized() {
        if (this.astLengthStack[this.astLengthPtr] == 0) {
            this.astLengthStack[this.astLengthPtr] = 1;
            this.expressionLengthPtr--;
            this.astStack[++this.astPtr] = new SynchronizedStatement(this.expressionStack[this.expressionPtr--], null, this.intStack[this.intPtr--], this.endStatementPosition);
        } else {
            this.expressionLengthPtr--;
            this.astStack[this.astPtr] = new SynchronizedStatement(this.expressionStack[this.expressionPtr--], (Block) this.astStack[this.astPtr], this.intStack[this.intPtr--], this.endStatementPosition);
        }
        this.modifiers = ClassFileConstants.AccDefault;
        // <-- see comment into modifiersFlag(int)
        this.modifiersSourceStart = -1;
    }

    protected void consumeStatementThrow() {
        // ThrowStatement ::= 'throw' Expression ';'
        this.expressionLengthPtr--;
        pushOnAstStack(new ThrowStatement(this.expressionStack[this.expressionPtr--], this.intStack[this.intPtr--], this.endStatementPosition));
    }

    protected void consumeStatementTry(boolean withFinally, boolean hasResources) {
        // TryStatement ::= 'try'  Block Catches
        // TryStatement ::= 'try'  Block Catchesopt Finally
        // TryStatementWithResources ::= 'try' ResourceSpecification TryBlock Catchesopt
        // TryStatementWithResources ::= 'try' ResourceSpecification TryBlock Catchesopt Finally
        int length;
        TryStatement tryStmt = new TryStatement();
        //finally
        if (withFinally) {
            this.astLengthPtr--;
            tryStmt.finallyBlock = (Block) this.astStack[this.astPtr--];
        }
        //catches are handle by two <argument-block> [see statementCatch]
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            if (length == 1) {
                tryStmt.catchBlocks = new Block[] { (Block) this.astStack[this.astPtr--] };
                tryStmt.catchArguments = new Argument[] { (Argument) this.astStack[this.astPtr--] };
            } else {
                Block[] bks = (tryStmt.catchBlocks = new Block[length]);
                Argument[] args = (tryStmt.catchArguments = new Argument[length]);
                while (length-- > 0) {
                    bks[length] = (Block) this.astStack[this.astPtr--];
                    args[length] = (Argument) this.astStack[this.astPtr--];
                }
            }
        }
        //try
        this.astLengthPtr--;
        tryStmt.tryBlock = (Block) this.astStack[this.astPtr--];
        if (hasResources) {
            // get the resources
            length = this.astLengthStack[this.astLengthPtr--];
            LocalDeclaration[] resources = new LocalDeclaration[length];
            System.arraycopy(this.astStack, (this.astPtr -= length) + 1, resources, 0, length);
            tryStmt.resources = resources;
            if (this.options.sourceLevel < ClassFileConstants.JDK1_7) {
                problemReporter().autoManagedResourcesNotBelow17(resources);
            }
        }
        //positions
        tryStmt.sourceEnd = this.endStatementPosition;
        tryStmt.sourceStart = this.intStack[this.intPtr--];
        pushOnAstStack(tryStmt);
    }

    protected void consumeStatementWhile() {
        // WhileStatement ::= 'while' '(' Expression ')' Statement
        // WhileStatementNoShortIf ::= 'while' '(' Expression ')' StatementNoShortIf
        this.expressionLengthPtr--;
        Statement statement = (Statement) this.astStack[this.astPtr];
        this.astStack[this.astPtr] = new WhileStatement(this.expressionStack[this.expressionPtr--], statement, this.intStack[this.intPtr--], this.endStatementPosition);
    }

    protected void consumeStaticImportOnDemandDeclarationName() {
        // StaticImportOnDemandDeclarationName ::= 'import' 'static' Name '.' RejectTypeAnnotations '*'
        /* push an ImportRef build from the last name
	stored in the identifier stack. */
        ImportReference impt;
        int length;
        char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        pushOnAstStack(impt = new ImportReference(tokens, positions, true, ClassFileConstants.AccStatic));
        // star end position
        impt.trailingStarPosition = this.intStack[this.intPtr--];
        this.modifiers = ClassFileConstants.AccDefault;
        // <-- see comment into modifiersFlag(int)
        this.modifiersSourceStart = -1;
        if (this.currentToken == TokenNameSEMICOLON) {
            impt.declarationSourceEnd = this.scanner.currentPosition - 1;
        } else {
            impt.declarationSourceEnd = impt.sourceEnd;
        }
        impt.declarationEnd = impt.declarationSourceEnd;
        //this.endPosition is just before the ;
        impt.declarationSourceStart = this.intStack[this.intPtr--];
        if (!this.statementRecoveryActivated && this.options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            // convert the static import reference to a non-static importe reference
            impt.modifiers = ClassFileConstants.AccDefault;
            problemReporter().invalidUsageOfStaticImports(impt);
        }
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.add(impt, 0);
            this.lastIgnoredToken = -1;
            // used to avoid branching back into the regular automaton
            this.restartRecovery = true;
        }
    }

    protected void consumeStaticInitializer() {
        // StaticInitializer ::=  StaticOnly Block
        //push an Initializer
        //optimize the push/pop
        Block block = (Block) this.astStack[this.astPtr];
        // clear bit set since was diet
        if (this.diet)
            block.bits &= ~ASTNode.UndocumentedEmptyBlock;
        Initializer initializer = new Initializer(block, ClassFileConstants.AccStatic);
        this.astStack[this.astPtr] = initializer;
        initializer.sourceEnd = this.endStatementPosition;
        initializer.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
        this.nestedMethod[this.nestedType]--;
        initializer.declarationSourceStart = this.intStack[this.intPtr--];
        initializer.bodyStart = this.intStack[this.intPtr--];
        initializer.bodyEnd = this.endPosition;
        // doc comment
        initializer.javadoc = this.javadoc;
        this.javadoc = null;
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = initializer.declarationSourceEnd;
            this.currentElement = this.currentElement.add(initializer, 0);
            this.lastIgnoredToken = -1;
        }
    }

    protected void consumeStaticOnly() {
        // StaticOnly ::= 'static'
        int savedModifiersSourceStart = this.modifiersSourceStart;
        // might update declaration source start
        checkComment();
        if (this.modifiersSourceStart >= savedModifiersSourceStart) {
            this.modifiersSourceStart = savedModifiersSourceStart;
        }
        pushOnIntStack(this.scanner.currentPosition);
        pushOnIntStack(this.modifiersSourceStart >= 0 ? this.modifiersSourceStart : this.scanner.startPosition);
        jumpOverMethodBody();
        this.nestedMethod[this.nestedType]++;
        resetModifiers();
        // remove the 0 pushed in consumeToken() for the static modifier
        this.expressionLengthPtr--;
        // recovery
        if (this.currentElement != null) {
            // remember start position only for static initializers
            this.recoveredStaticInitializerStart = this.intStack[this.intPtr];
        }
    }

    protected void consumeSwitchBlock() {
        // SwitchBlock ::= '{' SwitchBlockStatements SwitchLabels '}'
        concatNodeLists();
    }

    protected void consumeSwitchBlockStatement() {
        // SwitchBlockStatement ::= SwitchLabels BlockStatements
        concatNodeLists();
    }

    protected void consumeSwitchBlockStatements() {
        // SwitchBlockStatements ::= SwitchBlockStatements SwitchBlockStatement
        concatNodeLists();
    }

    protected void consumeSwitchLabels() {
        // SwitchLabels ::= SwitchLabels SwitchLabel
        optimizedConcatNodeLists();
    }

    protected void consumeToken(int type) {
        //System.out.println(this.scanner.toStringAction(type));
        switch(type) {
            case TokenNameARROW:
                consumeLambdaHeader();
                break;
            case TokenNameCOLON_COLON:
                this.colonColonStart = this.scanner.currentPosition - 2;
                break;
            case TokenNameBeginLambda:
                flushCommentsDefinedPriorTo(this.scanner.currentPosition);
                break;
            case TokenNameIdentifier:
                pushIdentifier();
                if (this.scanner.useAssertAsAnIndentifier && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
                    long positions = this.identifierPositionStack[this.identifierPtr];
                    if (!this.statementRecoveryActivated)
                        problemReporter().useAssertAsAnIdentifier((int) (positions >>> 32), (int) positions);
                }
                if (this.scanner.useEnumAsAnIndentifier && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
                    long positions = this.identifierPositionStack[this.identifierPtr];
                    if (!this.statementRecoveryActivated)
                        problemReporter().useEnumAsAnIdentifier((int) (positions >>> 32), (int) positions);
                }
                break;
            case TokenNameinterface:
                //'class' is pushing two int (positions) on the stack ==> 'interface' needs to do it too....
                pushOnIntStack(this.scanner.currentPosition - 1);
                pushOnIntStack(this.scanner.startPosition);
                break;
            case TokenNameabstract:
                checkAndSetModifiers(ClassFileConstants.AccAbstract);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNamestrictfp:
                checkAndSetModifiers(ClassFileConstants.AccStrictfp);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNamefinal:
                checkAndSetModifiers(ClassFileConstants.AccFinal);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNamenative:
                checkAndSetModifiers(ClassFileConstants.AccNative);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNameprivate:
                checkAndSetModifiers(ClassFileConstants.AccPrivate);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNameprotected:
                checkAndSetModifiers(ClassFileConstants.AccProtected);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNamepublic:
                checkAndSetModifiers(ClassFileConstants.AccPublic);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNametransient:
                checkAndSetModifiers(ClassFileConstants.AccTransient);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNamevolatile:
                checkAndSetModifiers(ClassFileConstants.AccVolatile);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNamestatic:
                checkAndSetModifiers(ClassFileConstants.AccStatic);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNamesynchronized:
                this.synchronizedBlockSourceStart = this.scanner.startPosition;
                checkAndSetModifiers(ClassFileConstants.AccSynchronized);
                pushOnExpressionStackLengthStack(0);
                break;
            //==============================
            case TokenNamevoid:
                pushIdentifier(-T_void);
                pushOnIntStack(this.scanner.currentPosition - 1);
                pushOnIntStack(this.scanner.startPosition);
                break;
            //regular type parsing that generates a dimension on this.intStack
            case TokenNameboolean:
                pushIdentifier(-T_boolean);
                pushOnIntStack(this.scanner.currentPosition - 1);
                pushOnIntStack(this.scanner.startPosition);
                break;
            case TokenNamebyte:
                pushIdentifier(-T_byte);
                pushOnIntStack(this.scanner.currentPosition - 1);
                pushOnIntStack(this.scanner.startPosition);
                break;
            case TokenNamechar:
                pushIdentifier(-T_char);
                pushOnIntStack(this.scanner.currentPosition - 1);
                pushOnIntStack(this.scanner.startPosition);
                break;
            case TokenNamedouble:
                pushIdentifier(-T_double);
                pushOnIntStack(this.scanner.currentPosition - 1);
                pushOnIntStack(this.scanner.startPosition);
                break;
            case TokenNamefloat:
                pushIdentifier(-T_float);
                pushOnIntStack(this.scanner.currentPosition - 1);
                pushOnIntStack(this.scanner.startPosition);
                break;
            case TokenNameint:
                pushIdentifier(-T_int);
                pushOnIntStack(this.scanner.currentPosition - 1);
                pushOnIntStack(this.scanner.startPosition);
                break;
            case TokenNamelong:
                pushIdentifier(-T_long);
                pushOnIntStack(this.scanner.currentPosition - 1);
                pushOnIntStack(this.scanner.startPosition);
                break;
            case TokenNameshort:
                pushIdentifier(-T_short);
                pushOnIntStack(this.scanner.currentPosition - 1);
                pushOnIntStack(this.scanner.startPosition);
                break;
            //==============================
            case TokenNameIntegerLiteral:
                pushOnExpressionStack(IntLiteral.buildIntLiteral(this.scanner.getCurrentTokenSource(), this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            case TokenNameLongLiteral:
                pushOnExpressionStack(LongLiteral.buildLongLiteral(this.scanner.getCurrentTokenSource(), this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            case TokenNameFloatingPointLiteral:
                pushOnExpressionStack(new FloatLiteral(this.scanner.getCurrentTokenSource(), this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            case TokenNameDoubleLiteral:
                pushOnExpressionStack(new DoubleLiteral(this.scanner.getCurrentTokenSource(), this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            case TokenNameCharacterLiteral:
                pushOnExpressionStack(new CharLiteral(this.scanner.getCurrentTokenSource(), this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            case TokenNameStringLiteral:
                StringLiteral stringLiteral;
                if (this.recordStringLiterals && !this.reparsingLambdaExpression && this.checkExternalizeStrings && this.lastPosistion < this.scanner.currentPosition && !this.statementRecoveryActivated) {
                    stringLiteral = createStringLiteral(this.scanner.getCurrentTokenSourceString(), this.scanner.startPosition, this.scanner.currentPosition - 1, Util.getLineNumber(this.scanner.startPosition, this.scanner.lineEnds, 0, this.scanner.linePtr));
                    this.compilationUnit.recordStringLiteral(stringLiteral, this.currentElement != null);
                } else {
                    stringLiteral = createStringLiteral(this.scanner.getCurrentTokenSourceString(), this.scanner.startPosition, this.scanner.currentPosition - 1, 0);
                }
                pushOnExpressionStack(stringLiteral);
                break;
            case TokenNamefalse:
                pushOnExpressionStack(new FalseLiteral(this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            case TokenNametrue:
                pushOnExpressionStack(new TrueLiteral(this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            case TokenNamenull:
                pushOnExpressionStack(new NullLiteral(this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            //============================
            case TokenNamesuper:
            case TokenNamethis:
                this.endPosition = this.scanner.currentPosition - 1;
                pushOnIntStack(this.scanner.startPosition);
                break;
            case TokenNamefor:
                this.forStartPosition = this.scanner.startPosition;
            //$FALL-THROUGH$
            case TokenNameassert:
            case TokenNameimport:
            case TokenNamepackage:
            case TokenNamethrow:
            case TokenNamedo:
            case TokenNameif:
            case TokenNameswitch:
            case TokenNametry:
            case TokenNamewhile:
            case TokenNamebreak:
            case TokenNamecontinue:
            case TokenNamereturn:
            case TokenNamecase:
                pushOnIntStack(this.scanner.startPosition);
                break;
            case TokenNamenew:
                // https://bugs.eclipse.org/bugs/show_bug.cgi?id=40954
                resetModifiers();
                pushOnIntStack(this.scanner.startPosition);
                break;
            case TokenNameclass:
                pushOnIntStack(this.scanner.currentPosition - 1);
                pushOnIntStack(this.scanner.startPosition);
                break;
            case TokenNameenum:
                pushOnIntStack(this.scanner.currentPosition - 1);
                pushOnIntStack(this.scanner.startPosition);
                break;
            case TokenNamedefault:
                pushOnIntStack(this.scanner.startPosition);
                pushOnIntStack(this.scanner.currentPosition - 1);
                break;
            //let extra semantic action decide when to push
            case TokenNameRBRACKET:
                this.rBracketPosition = this.scanner.startPosition;
                this.endPosition = this.scanner.startPosition;
                this.endStatementPosition = this.scanner.currentPosition - 1;
                break;
            case TokenNameLBRACE:
                this.endStatementPosition = this.scanner.currentPosition - 1;
            //$FALL-THROUGH$
            case TokenNamePLUS:
            case TokenNameMINUS:
            case TokenNameNOT:
            case TokenNameTWIDDLE:
                this.endPosition = this.scanner.startPosition;
                break;
            case TokenNamePLUS_PLUS:
            case TokenNameMINUS_MINUS:
                this.endPosition = this.scanner.startPosition;
                this.endStatementPosition = this.scanner.currentPosition - 1;
                break;
            case TokenNameRBRACE:
            case TokenNameSEMICOLON:
                this.endStatementPosition = this.scanner.currentPosition - 1;
                this.endPosition = this.scanner.startPosition - 1;
                //the item is not part of the potential futur expression/statement
                break;
            case TokenNameRPAREN:
                // in order to handle ( expression) ////// (cast)expression///// foo(x)
                // position of the end of right parenthesis (in case of unicode \u0029) lex00101
                this.rParenPos = this.scanner.currentPosition - 1;
                break;
            case TokenNameLPAREN:
                this.lParenPos = this.scanner.startPosition;
                break;
            case TokenNameAT308:
                this.expectTypeAnnotation = true;
                // https://bugs.eclipse.org/bugs/show_bug.cgi?id=417660: Stack the dimensions, they get unstacked in consumeTypeAnnotation.
                pushOnIntStack(this.dimensions);
                this.dimensions = 0;
            //$FALL-THROUGH$
            case TokenNameAT:
                pushOnIntStack(this.scanner.startPosition);
                break;
            case TokenNameQUESTION:
                pushOnIntStack(this.scanner.startPosition);
                pushOnIntStack(this.scanner.currentPosition - 1);
                break;
            case TokenNameLESS:
                pushOnIntStack(this.scanner.startPosition);
                break;
            case TokenNameELLIPSIS:
                pushOnIntStack(this.scanner.currentPosition - 1);
                break;
            case TokenNameEQUAL:
                if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
                    RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation) this.currentElement;
                    if (recoveredAnnotation.memberValuPairEqualEnd == -1) {
                        recoveredAnnotation.memberValuPairEqualEnd = this.scanner.currentPosition - 1;
                    }
                }
                break;
            case TokenNameMULTIPLY:
                // star end position
                pushOnIntStack(this.scanner.currentPosition - 1);
                break;
        }
    }

    protected void consumeTypeArgument() {
        pushOnGenericsStack(getTypeReference(this.intStack[this.intPtr--]));
    }

    protected void consumeTypeArgumentList() {
        concatGenericsLists();
    }

    protected void consumeTypeArgumentList1() {
        concatGenericsLists();
    }

    protected void consumeTypeArgumentList2() {
        concatGenericsLists();
    }

    protected void consumeTypeArgumentList3() {
        concatGenericsLists();
    }

    protected void consumeTypeArgumentReferenceType1() {
        concatGenericsLists();
        pushOnGenericsStack(getTypeReference(0));
        // pop '<' position.
        this.intPtr--;
    }

    protected void consumeTypeArgumentReferenceType2() {
        concatGenericsLists();
        pushOnGenericsStack(getTypeReference(0));
        this.intPtr--;
    }

    protected void consumeTypeArguments() {
        concatGenericsLists();
        this.intPtr--;
        if (!this.statementRecoveryActivated && this.options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            int length = this.genericsLengthStack[this.genericsLengthPtr];
            problemReporter().invalidUsageOfTypeArguments((TypeReference) this.genericsStack[this.genericsPtr - length + 1], (TypeReference) this.genericsStack[this.genericsPtr]);
        }
    }

    protected void consumeTypeDeclarations() {
        // TypeDeclarations ::= TypeDeclarations TypeDeclaration
        concatNodeLists();
    }

    protected void consumeTypeHeaderNameWithTypeParameters() {
        // ClassHeaderName ::= ClassHeaderName1 TypeParameters
        // InterfaceHeaderName ::= InterfaceHeaderName1 TypeParameters
        TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
        // consume type parameters
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeDecl.typeParameters = new TypeParameter[length], 0, length);
        typeDecl.bodyStart = typeDecl.typeParameters[length - 1].declarationSourceEnd + 1;
        this.listTypeParameterLength = 0;
        if (this.currentElement != null) {
            // is recovering
            if (this.currentElement instanceof RecoveredType) {
                RecoveredType recoveredType = (RecoveredType) this.currentElement;
                recoveredType.pendingTypeParameters = null;
                this.lastCheckPoint = typeDecl.bodyStart;
            } else {
                this.lastCheckPoint = typeDecl.bodyStart;
                this.currentElement = this.currentElement.add(typeDecl, 0);
                this.lastIgnoredToken = -1;
            }
        }
    }

    protected void consumeTypeImportOnDemandDeclarationName() {
        // TypeImportOnDemandDeclarationName ::= 'import' Name '.' RejectTypeAnnotations '*'
        /* push an ImportRef build from the last name
	stored in the identifier stack. */
        ImportReference impt;
        int length;
        char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        pushOnAstStack(impt = new ImportReference(tokens, positions, true, ClassFileConstants.AccDefault));
        // star end position
        impt.trailingStarPosition = this.intStack[this.intPtr--];
        if (this.currentToken == TokenNameSEMICOLON) {
            impt.declarationSourceEnd = this.scanner.currentPosition - 1;
        } else {
            impt.declarationSourceEnd = impt.sourceEnd;
        }
        impt.declarationEnd = impt.declarationSourceEnd;
        //this.endPosition is just before the ;
        impt.declarationSourceStart = this.intStack[this.intPtr--];
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.add(impt, 0);
            this.lastIgnoredToken = -1;
            // used to avoid branching back into the regular automaton
            this.restartRecovery = true;
        }
    }

    protected void consumeTypeParameter1() {
    // nothing to do
    }

    protected void consumeTypeParameter1WithExtends() {
        //TypeParameter1 ::= TypeParameterHeader 'extends' ReferenceType1
        TypeReference superType = (TypeReference) this.genericsStack[this.genericsPtr--];
        this.genericsLengthPtr--;
        TypeParameter typeParameter = (TypeParameter) this.genericsStack[this.genericsPtr];
        typeParameter.declarationSourceEnd = superType.sourceEnd;
        typeParameter.type = superType;
        superType.bits |= ASTNode.IsSuperType;
        typeParameter.bits |= (superType.bits & ASTNode.HasTypeAnnotations);
        this.genericsStack[this.genericsPtr] = typeParameter;
    }

    protected void consumeTypeParameter1WithExtendsAndBounds() {
        //TypeParameter1 ::= TypeParameterHeader 'extends' ReferenceType AdditionalBoundList1
        int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        TypeReference[] bounds = new TypeReference[additionalBoundsLength];
        this.genericsPtr -= additionalBoundsLength;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 0, additionalBoundsLength);
        TypeReference superType = getTypeReference(this.intStack[this.intPtr--]);
        TypeParameter typeParameter = (TypeParameter) this.genericsStack[this.genericsPtr];
        typeParameter.declarationSourceEnd = bounds[additionalBoundsLength - 1].sourceEnd;
        typeParameter.type = superType;
        typeParameter.bits |= (superType.bits & ASTNode.HasTypeAnnotations);
        superType.bits |= ASTNode.IsSuperType;
        typeParameter.bounds = bounds;
        for (int i = 0, max = bounds.length; i < max; i++) {
            TypeReference bound = bounds[i];
            bound.bits |= ASTNode.IsSuperType;
            typeParameter.bits |= (bound.bits & ASTNode.HasTypeAnnotations);
        }
    }

    protected void consumeTypeParameterHeader() {
        //TypeParameterHeader ::= TypeAnnotationsopt Identifier
        TypeParameter typeParameter = new TypeParameter();
        int length;
        if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
            System.arraycopy(this.typeAnnotationStack, (this.typeAnnotationPtr -= length) + 1, typeParameter.annotations = new Annotation[length], 0, length);
            typeParameter.bits |= ASTNode.HasTypeAnnotations;
        }
        long pos = this.identifierPositionStack[this.identifierPtr];
        final int end = (int) pos;
        typeParameter.declarationSourceEnd = end;
        typeParameter.sourceEnd = end;
        final int start = (int) (pos >>> 32);
        typeParameter.declarationSourceStart = start;
        typeParameter.sourceStart = start;
        typeParameter.name = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        pushOnGenericsStack(typeParameter);
        this.listTypeParameterLength++;
    }

    protected void consumeTypeParameterList() {
        //TypeParameterList ::= TypeParameterList ',' TypeParameter
        concatGenericsLists();
    }

    protected void consumeTypeParameterList1() {
        //TypeParameterList1 ::= TypeParameterList ',' TypeParameter1
        concatGenericsLists();
    }

    protected void consumeTypeParameters() {
        int startPos = this.intStack[this.intPtr--];
        if (this.currentElement != null) {
            if (this.currentElement instanceof RecoveredType) {
                RecoveredType recoveredType = (RecoveredType) this.currentElement;
                int length = this.genericsLengthStack[this.genericsLengthPtr];
                TypeParameter[] typeParameters = new TypeParameter[length];
                System.arraycopy(this.genericsStack, this.genericsPtr - length + 1, typeParameters, 0, length);
                recoveredType.add(typeParameters, startPos);
            }
        }
        if (!this.statementRecoveryActivated && this.options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            int length = this.genericsLengthStack[this.genericsLengthPtr];
            problemReporter().invalidUsageOfTypeParameters((TypeParameter) this.genericsStack[this.genericsPtr - length + 1], (TypeParameter) this.genericsStack[this.genericsPtr]);
        }
    }

    protected void consumeTypeParameterWithExtends() {
        //TypeParameter ::= TypeParameterHeader 'extends' ReferenceType
        TypeReference superType = getTypeReference(this.intStack[this.intPtr--]);
        TypeParameter typeParameter = (TypeParameter) this.genericsStack[this.genericsPtr];
        typeParameter.declarationSourceEnd = superType.sourceEnd;
        typeParameter.type = superType;
        typeParameter.bits |= (superType.bits & ASTNode.HasTypeAnnotations);
        superType.bits |= ASTNode.IsSuperType;
    }

    protected void consumeTypeParameterWithExtendsAndBounds() {
        //TypeParameter ::= TypeParameterHeader 'extends' ReferenceType AdditionalBoundList
        int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        TypeReference[] bounds = new TypeReference[additionalBoundsLength];
        this.genericsPtr -= additionalBoundsLength;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 0, additionalBoundsLength);
        TypeReference superType = getTypeReference(this.intStack[this.intPtr--]);
        TypeParameter typeParameter = (TypeParameter) this.genericsStack[this.genericsPtr];
        typeParameter.type = superType;
        typeParameter.bits |= (superType.bits & ASTNode.HasTypeAnnotations);
        superType.bits |= ASTNode.IsSuperType;
        typeParameter.bounds = bounds;
        typeParameter.declarationSourceEnd = bounds[additionalBoundsLength - 1].sourceEnd;
        for (int i = 0, max = bounds.length; i < max; i++) {
            TypeReference bound = bounds[i];
            bound.bits |= ASTNode.IsSuperType;
            typeParameter.bits |= (bound.bits & ASTNode.HasTypeAnnotations);
        }
    }

    protected void consumeZeroAdditionalBounds() {
        if (// Signal zero additional bounds - do this only when the cast type is fully seen (i.e not in error path)
        this.currentToken == TokenNameRPAREN)
            // Not all stacks are adjusted - this is not meant to be popped by getTypeReference
            pushOnGenericsLengthStack(0);
    }

    protected void consumeUnaryExpression(int op) {
        // UnaryExpression ::= '+' PushPosition UnaryExpression
        // UnaryExpression ::= '-' PushPosition UnaryExpression
        // UnaryExpressionNotPlusMinus ::= '~' PushPosition UnaryExpression
        // UnaryExpressionNotPlusMinus ::= '!' PushPosition UnaryExpression
        //optimize the push/pop
        //handle manually the -2147483648 while it is not a real
        //computation of an - and 2147483648 (notice that 2147483648
        //is Integer.MAX_VALUE+1.....)
        //Same for -9223372036854775808L ............
        //this.intStack have the position of the operator
        Expression r, exp = this.expressionStack[this.expressionPtr];
        if (op == MINUS) {
            if (exp instanceof IntLiteral) {
                IntLiteral intLiteral = (IntLiteral) exp;
                IntLiteral convertToMinValue = intLiteral.convertToMinValue();
                if (convertToMinValue == intLiteral) {
                    // not a min value literal so we convert it to an unary expression
                    r = new UnaryExpression(exp, op);
                } else {
                    r = convertToMinValue;
                }
            } else if (exp instanceof LongLiteral) {
                LongLiteral longLiteral = (LongLiteral) exp;
                LongLiteral convertToMinValue = longLiteral.convertToMinValue();
                if (convertToMinValue == longLiteral) {
                    // not a min value literal so we convert it to an unary expression
                    r = new UnaryExpression(exp, op);
                } else {
                    r = convertToMinValue;
                }
            } else {
                r = new UnaryExpression(exp, op);
            }
        } else {
            r = new UnaryExpression(exp, op);
        }
        r.sourceStart = this.intStack[this.intPtr--];
        r.sourceEnd = exp.sourceEnd;
        this.expressionStack[this.expressionPtr] = r;
    }

    protected void consumeUnaryExpression(int op, boolean post) {
        // PreIncrementExpression ::= '++' PushPosition UnaryExpression
        // PreDecrementExpression ::= '--' PushPosition UnaryExpression
        // ++ and -- operators
        //optimize the push/pop
        //this.intStack has the position of the operator when prefix
        Expression leftHandSide = this.expressionStack[this.expressionPtr];
        if (leftHandSide instanceof Reference) {
            // ++foo()++ is unvalid
            if (post) {
                this.expressionStack[this.expressionPtr] = new PostfixExpression(leftHandSide, IntLiteral.One, op, this.endStatementPosition);
            } else {
                this.expressionStack[this.expressionPtr] = new PrefixExpression(leftHandSide, IntLiteral.One, op, this.intStack[this.intPtr--]);
            }
        } else {
            //the ++ or the -- is NOT taken into account if code gen proceeds
            if (!post) {
                this.intPtr--;
            }
            if (!this.statementRecoveryActivated)
                problemReporter().invalidUnaryExpression(leftHandSide);
        }
    }

    protected void consumeVariableDeclarators() {
        // VariableDeclarators ::= VariableDeclarators ',' VariableDeclarator
        optimizedConcatNodeLists();
    }

    protected void consumeVariableInitializers() {
        // VariableInitializers ::= VariableInitializers ',' VariableInitializer
        concatExpressionLists();
    }

    protected void consumeWildcard() {
        final Wildcard wildcard = new Wildcard(Wildcard.UNBOUND);
        wildcard.sourceEnd = this.intStack[this.intPtr--];
        wildcard.sourceStart = this.intStack[this.intPtr--];
        annotateTypeReference(wildcard);
        pushOnGenericsStack(wildcard);
    }

    protected void consumeWildcard1() {
        final Wildcard wildcard = new Wildcard(Wildcard.UNBOUND);
        wildcard.sourceEnd = this.intStack[this.intPtr--];
        wildcard.sourceStart = this.intStack[this.intPtr--];
        annotateTypeReference(wildcard);
        pushOnGenericsStack(wildcard);
    }

    protected void consumeWildcard1WithBounds() {
    // Nothing to do
    // The wildcard is created by the consumeWildcardBounds1Extends or by consumeWildcardBounds1Super
    }

    protected void consumeWildcard2() {
        final Wildcard wildcard = new Wildcard(Wildcard.UNBOUND);
        wildcard.sourceEnd = this.intStack[this.intPtr--];
        wildcard.sourceStart = this.intStack[this.intPtr--];
        annotateTypeReference(wildcard);
        pushOnGenericsStack(wildcard);
    }

    protected void consumeWildcard2WithBounds() {
    // Nothing to do
    // The wildcard is created by the consumeWildcardBounds2Extends or by consumeWildcardBounds2Super
    }

    protected void consumeWildcard3() {
        final Wildcard wildcard = new Wildcard(Wildcard.UNBOUND);
        wildcard.sourceEnd = this.intStack[this.intPtr--];
        wildcard.sourceStart = this.intStack[this.intPtr--];
        annotateTypeReference(wildcard);
        pushOnGenericsStack(wildcard);
    }

    protected void consumeWildcard3WithBounds() {
    // Nothing to do
    // The wildcard is created by the consumeWildcardBounds3Extends or by consumeWildcardBounds3Super
    }

    protected void consumeWildcardBounds1Extends() {
        Wildcard wildcard = new Wildcard(Wildcard.EXTENDS);
        wildcard.bound = (TypeReference) this.genericsStack[this.genericsPtr];
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        // remove end position of the '?'
        this.intPtr--;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        annotateTypeReference(wildcard);
        this.genericsStack[this.genericsPtr] = wildcard;
    }

    protected void consumeWildcardBounds1Super() {
        Wildcard wildcard = new Wildcard(Wildcard.SUPER);
        wildcard.bound = (TypeReference) this.genericsStack[this.genericsPtr];
        // remove the starting position of the super keyword
        this.intPtr--;
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        // remove end position of the '?'
        this.intPtr--;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        annotateTypeReference(wildcard);
        this.genericsStack[this.genericsPtr] = wildcard;
    }

    protected void consumeWildcardBounds2Extends() {
        Wildcard wildcard = new Wildcard(Wildcard.EXTENDS);
        wildcard.bound = (TypeReference) this.genericsStack[this.genericsPtr];
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        // remove end position of the '?'
        this.intPtr--;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        annotateTypeReference(wildcard);
        this.genericsStack[this.genericsPtr] = wildcard;
    }

    protected void consumeWildcardBounds2Super() {
        Wildcard wildcard = new Wildcard(Wildcard.SUPER);
        wildcard.bound = (TypeReference) this.genericsStack[this.genericsPtr];
        // remove the starting position of the super keyword
        this.intPtr--;
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        // remove end position of the '?'
        this.intPtr--;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        annotateTypeReference(wildcard);
        this.genericsStack[this.genericsPtr] = wildcard;
    }

    protected void consumeWildcardBounds3Extends() {
        Wildcard wildcard = new Wildcard(Wildcard.EXTENDS);
        wildcard.bound = (TypeReference) this.genericsStack[this.genericsPtr];
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        // remove end position of the '?'
        this.intPtr--;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        annotateTypeReference(wildcard);
        this.genericsStack[this.genericsPtr] = wildcard;
    }

    protected void consumeWildcardBounds3Super() {
        Wildcard wildcard = new Wildcard(Wildcard.SUPER);
        wildcard.bound = (TypeReference) this.genericsStack[this.genericsPtr];
        // remove the starting position of the super keyword
        this.intPtr--;
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        // remove end position of the '?'
        this.intPtr--;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        annotateTypeReference(wildcard);
        this.genericsStack[this.genericsPtr] = wildcard;
    }

    protected void consumeWildcardBoundsExtends() {
        Wildcard wildcard = new Wildcard(Wildcard.EXTENDS);
        wildcard.bound = getTypeReference(this.intStack[this.intPtr--]);
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        // remove end position of the '?'
        this.intPtr--;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        annotateTypeReference(wildcard);
        pushOnGenericsStack(wildcard);
    }

    protected void consumeWildcardBoundsSuper() {
        Wildcard wildcard = new Wildcard(Wildcard.SUPER);
        wildcard.bound = getTypeReference(this.intStack[this.intPtr--]);
        // remove the starting position of the super keyword
        this.intPtr--;
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        // remove end position of the '?'
        this.intPtr--;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        annotateTypeReference(wildcard);
        pushOnGenericsStack(wildcard);
    }

    protected void consumeWildcardWithBounds() {
    // Nothing to do
    // The wildcard is created by the consumeWildcardBoundsExtends or by consumeWildcardBoundsSuper
    }

    /**
 * Given the current comment stack, answer whether some comment is available in a certain exclusive range
 *
 * @param sourceStart int
 * @param sourceEnd int
 * @return boolean
 */
    public boolean containsComment(int sourceStart, int sourceEnd) {
        int iComment = this.scanner.commentPtr;
        for (; iComment >= 0; iComment--) {
            int commentStart = this.scanner.commentStarts[iComment];
            if (commentStart < 0)
                commentStart = -commentStart;
            // ignore comments before start
            if (commentStart < sourceStart)
                continue;
            // ignore comments after end
            if (commentStart > sourceEnd)
                continue;
            return true;
        }
        return false;
    }

    public MethodDeclaration convertToMethodDeclaration(ConstructorDeclaration c, CompilationResult compilationResult) {
        MethodDeclaration m = new MethodDeclaration(compilationResult);
        m.typeParameters = c.typeParameters;
        m.sourceStart = c.sourceStart;
        m.sourceEnd = c.sourceEnd;
        m.bodyStart = c.bodyStart;
        m.bodyEnd = c.bodyEnd;
        m.declarationSourceEnd = c.declarationSourceEnd;
        m.declarationSourceStart = c.declarationSourceStart;
        m.selector = c.selector;
        m.statements = c.statements;
        m.modifiers = c.modifiers;
        m.annotations = c.annotations;
        m.arguments = c.arguments;
        m.thrownExceptions = c.thrownExceptions;
        m.explicitDeclarations = c.explicitDeclarations;
        m.returnType = null;
        m.javadoc = c.javadoc;
        m.bits = c.bits;
        return m;
    }

    protected TypeReference augmentTypeWithAdditionalDimensions(TypeReference typeReference, int additionalDimensions, Annotation[][] additionalAnnotations, boolean isVarargs) {
        return typeReference.augmentTypeWithAdditionalDimensions(additionalDimensions, additionalAnnotations, isVarargs);
    }

    protected FieldDeclaration createFieldDeclaration(char[] fieldDeclarationName, int sourceStart, int sourceEnd) {
        return new FieldDeclaration(fieldDeclarationName, sourceStart, sourceEnd);
    }

    protected JavadocParser createJavadocParser() {
        return new JavadocParser(this);
    }

    protected LocalDeclaration createLocalDeclaration(char[] localDeclarationName, int sourceStart, int sourceEnd) {
        return new LocalDeclaration(localDeclarationName, sourceStart, sourceEnd);
    }

    protected StringLiteral createStringLiteral(char[] token, int start, int end, int lineNumber) {
        return new StringLiteral(token, start, end, lineNumber);
    }

    protected RecoveredType currentRecoveryType() {
        if (this.currentElement != null) {
            if (this.currentElement instanceof RecoveredType) {
                return (RecoveredType) this.currentElement;
            } else {
                return this.currentElement.enclosingType();
            }
        }
        return null;
    }

    public CompilationUnitDeclaration dietParse(ICompilationUnit sourceUnit, CompilationResult compilationResult) {
        CompilationUnitDeclaration parsedUnit;
        boolean old = this.diet;
        int oldInt = this.dietInt;
        try {
            this.dietInt = 0;
            this.diet = true;
            parsedUnit = parse(sourceUnit, compilationResult);
        } finally {
            this.diet = old;
            this.dietInt = oldInt;
        }
        return parsedUnit;
    }

    protected void dispatchDeclarationInto(int length) {
        if (length == 0)
            return;
        //plus one -- see <HERE>
        int[] flag = new int[length + 1];
        int size1 = 0, size2 = 0, size3 = 0;
        boolean hasAbstractMethods = false;
        for (int i = length - 1; i >= 0; i--) {
            ASTNode astNode = this.astStack[this.astPtr--];
            if (astNode instanceof AbstractMethodDeclaration) {
                //methods and constructors have been regrouped into one single list
                flag[i] = 2;
                size2++;
                if (((AbstractMethodDeclaration) astNode).isAbstract()) {
                    hasAbstractMethods = true;
                }
            } else if (astNode instanceof TypeDeclaration) {
                flag[i] = 3;
                size3++;
            } else {
                //field
                flag[i] = 1;
                size1++;
            }
        }
        //arrays creation
        TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
        if (size1 != 0) {
            typeDecl.fields = new FieldDeclaration[size1];
        }
        if (size2 != 0) {
            typeDecl.methods = new AbstractMethodDeclaration[size2];
            if (hasAbstractMethods)
                typeDecl.bits |= ASTNode.HasAbstractMethods;
        }
        if (size3 != 0) {
            typeDecl.memberTypes = new TypeDeclaration[size3];
        }
        //arrays fill up
        size1 = size2 = size3 = 0;
        int flagI = flag[0], start = 0;
        int length2;
        for (//<HERE> the plus one allows to
        int end = 0; //<HERE> the plus one allows to
        end <= length; //<HERE> the plus one allows to
        end++) {
            if (//treat the last element as a ended flag.....
            flagI != flag[end]) //array copy
            {
                switch(flagI) {
                    case 1:
                        size1 += (length2 = end - start);
                        System.arraycopy(this.astStack, this.astPtr + start + 1, typeDecl.fields, size1 - length2, length2);
                        break;
                    case 2:
                        size2 += (length2 = end - start);
                        System.arraycopy(this.astStack, this.astPtr + start + 1, typeDecl.methods, size2 - length2, length2);
                        break;
                    case 3:
                        size3 += (length2 = end - start);
                        System.arraycopy(this.astStack, this.astPtr + start + 1, typeDecl.memberTypes, size3 - length2, length2);
                        break;
                }
                flagI = flag[start = end];
            }
        }
        if (typeDecl.memberTypes != null) {
            for (int i = typeDecl.memberTypes.length - 1; i >= 0; i--) {
                typeDecl.memberTypes[i].enclosingType = typeDecl;
            }
        }
    }

    protected void dispatchDeclarationIntoEnumDeclaration(int length) {
        if (length == 0)
            return;
        //plus one -- see <HERE>
        int[] flag = new int[length + 1];
        int size1 = 0, size2 = 0, size3 = 0;
        TypeDeclaration enumDeclaration = (TypeDeclaration) this.astStack[this.astPtr - length];
        boolean hasAbstractMethods = false;
        int enumConstantsCounter = 0;
        for (int i = length - 1; i >= 0; i--) {
            ASTNode astNode = this.astStack[this.astPtr--];
            if (astNode instanceof AbstractMethodDeclaration) {
                //methods and constructors have been regrouped into one single list
                flag[i] = 2;
                size2++;
                if (((AbstractMethodDeclaration) astNode).isAbstract()) {
                    hasAbstractMethods = true;
                }
            } else if (astNode instanceof TypeDeclaration) {
                flag[i] = 3;
                size3++;
            } else if (astNode instanceof FieldDeclaration) {
                flag[i] = 1;
                size1++;
                if (((FieldDeclaration) astNode).getKind() == AbstractVariableDeclaration.ENUM_CONSTANT) {
                    enumConstantsCounter++;
                }
            }
        }
        //arrays creation
        if (size1 != 0) {
            enumDeclaration.fields = new FieldDeclaration[size1];
        }
        if (size2 != 0) {
            enumDeclaration.methods = new AbstractMethodDeclaration[size2];
            if (hasAbstractMethods)
                enumDeclaration.bits |= ASTNode.HasAbstractMethods;
        }
        if (size3 != 0) {
            enumDeclaration.memberTypes = new TypeDeclaration[size3];
        }
        //arrays fill up
        size1 = size2 = size3 = 0;
        int flagI = flag[0], start = 0;
        int length2;
        for (//<HERE> the plus one allows to
        int end = 0; //<HERE> the plus one allows to
        end <= length; //<HERE> the plus one allows to
        end++) {
            if (//treat the last element as a ended flag.....
            flagI != flag[end]) //array copy
            {
                switch(flagI) {
                    case 1:
                        size1 += (length2 = end - start);
                        System.arraycopy(this.astStack, this.astPtr + start + 1, enumDeclaration.fields, size1 - length2, length2);
                        break;
                    case 2:
                        size2 += (length2 = end - start);
                        System.arraycopy(this.astStack, this.astPtr + start + 1, enumDeclaration.methods, size2 - length2, length2);
                        break;
                    case 3:
                        size3 += (length2 = end - start);
                        System.arraycopy(this.astStack, this.astPtr + start + 1, enumDeclaration.memberTypes, size3 - length2, length2);
                        break;
                }
                flagI = flag[start = end];
            }
        }
        if (enumDeclaration.memberTypes != null) {
            for (int i = enumDeclaration.memberTypes.length - 1; i >= 0; i--) {
                enumDeclaration.memberTypes[i].enclosingType = enumDeclaration;
            }
        }
        enumDeclaration.enumConstantsCounter = enumConstantsCounter;
    }

    protected CompilationUnitDeclaration endParse(int act) {
        this.lastAct = act;
        if (this.statementRecoveryActivated) {
            RecoveredElement recoveredElement = buildInitialRecoveryState();
            if (recoveredElement != null) {
                recoveredElement.topElement().updateParseTree();
            }
            if (this.hasError)
                resetStacks();
        } else if (this.currentElement != null) {
            if (VERBOSE_RECOVERY) {
                System.out.print(Messages.parser_syntaxRecovery);
                //$NON-NLS-1$
                System.out.println("--------------------------");
                System.out.println(this.compilationUnit);
                //$NON-NLS-1$
                System.out.println("----------------------------------");
            }
            this.currentElement.topElement().updateParseTree();
        } else {
            if (this.diet & VERBOSE_RECOVERY) {
                System.out.print(Messages.parser_regularParse);
                //$NON-NLS-1$
                System.out.println("--------------------------");
                System.out.println(this.compilationUnit);
                //$NON-NLS-1$
                System.out.println("----------------------------------");
            }
        }
        persistLineSeparatorPositions();
        for (int i = 0; i < this.scanner.foundTaskCount; i++) {
            if (!this.statementRecoveryActivated)
                problemReporter().task(new String(this.scanner.foundTaskTags[i]), new String(this.scanner.foundTaskMessages[i]), this.scanner.foundTaskPriorities[i] == null ? null : new String(this.scanner.foundTaskPriorities[i]), this.scanner.foundTaskPositions[i][0], this.scanner.foundTaskPositions[i][1]);
        }
        this.javadoc = null;
        return this.compilationUnit;
    }

    /*
 * Flush comments defined prior to a given positions.
 *
 * Note: comments are stacked in syntactical order
 *
 * Either answer given <position>, or the end position of a comment line
 * immediately following the <position> (same line)
 *
 * e.g.
 * void foo(){
 * } // end of method foo
 */
    public int flushCommentsDefinedPriorTo(int position) {
        int lastCommentIndex = this.scanner.commentPtr;
        // no comment
        if (lastCommentIndex < 0)
            return position;
        // compute the index of the first obsolete comment
        int index = lastCommentIndex;
        int validCount = 0;
        while (index >= 0) {
            int commentEnd = this.scanner.commentStops[index];
            // negative end position for non-javadoc comments
            if (commentEnd < 0)
                commentEnd = -commentEnd;
            if (commentEnd <= position) {
                break;
            }
            index--;
            validCount++;
        }
        // flush this comment and shift <position> to the comment end.
        if (validCount > 0) {
            //non-javadoc comment end positions are negative
            int immediateCommentEnd = -this.scanner.commentStops[index + 1];
            if (// only tolerating non-javadoc comments
            immediateCommentEnd > 0) {
                // is there any line break until the end of the immediate comment ? (thus only tolerating line comment)
                // comment end in one char too far
                immediateCommentEnd--;
                if (Util.getLineNumber(position, this.scanner.lineEnds, 0, this.scanner.linePtr) == Util.getLineNumber(immediateCommentEnd, this.scanner.lineEnds, 0, this.scanner.linePtr)) {
                    position = immediateCommentEnd;
                    // flush this comment
                    validCount--;
                    index++;
                }
            }
        }
        // no obsolete comment
        if (index < 0)
            return position;
        switch(validCount) {
            case 0:
                // do nothing
                break;
            // move valid comment infos, overriding obsolete comment infos
            case 2:
                this.scanner.commentStarts[0] = this.scanner.commentStarts[index + 1];
                this.scanner.commentStops[0] = this.scanner.commentStops[index + 1];
                this.scanner.commentTagStarts[0] = this.scanner.commentTagStarts[index + 1];
                this.scanner.commentStarts[1] = this.scanner.commentStarts[index + 2];
                this.scanner.commentStops[1] = this.scanner.commentStops[index + 2];
                this.scanner.commentTagStarts[1] = this.scanner.commentTagStarts[index + 2];
                break;
            case 1:
                this.scanner.commentStarts[0] = this.scanner.commentStarts[index + 1];
                this.scanner.commentStops[0] = this.scanner.commentStops[index + 1];
                this.scanner.commentTagStarts[0] = this.scanner.commentTagStarts[index + 1];
                break;
            default:
                System.arraycopy(this.scanner.commentStarts, index + 1, this.scanner.commentStarts, 0, validCount);
                System.arraycopy(this.scanner.commentStops, index + 1, this.scanner.commentStops, 0, validCount);
                System.arraycopy(this.scanner.commentTagStarts, index + 1, this.scanner.commentTagStarts, 0, validCount);
        }
        this.scanner.commentPtr = validCount - 1;
        return position;
    }

    protected TypeReference getAnnotationType() {
        int length = this.identifierLengthStack[this.identifierLengthPtr--];
        if (length == 1) {
            return new SingleTypeReference(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--]);
        } else {
            char[][] tokens = new char[length][];
            this.identifierPtr -= length;
            long[] positions = new long[length];
            System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
            System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
            return new QualifiedTypeReference(tokens, positions);
        }
    }

    public int getFirstToken() {
        return this.firstToken;
    }

    /*
 * Answer back an array of sourceStart/sourceEnd positions of the available JavaDoc comments.
 * The array is a flattened structure: 2*n entries with consecutives start and end positions.
 *
 * If no JavaDoc is available, then null is answered instead of an empty array.
 *
 * e.g. { 10, 20, 25, 45 }  --> javadoc1 from 10 to 20, javadoc2 from 25 to 45
 */
    public int[] getJavaDocPositions() {
        int javadocCount = 0;
        int max = this.scanner.commentPtr;
        for (int i = 0; i <= max; i++) {
            // javadoc only (non javadoc comment have negative start and/or end positions.)
            if (this.scanner.commentStarts[i] >= 0 && this.scanner.commentStops[i] > 0) {
                javadocCount++;
            }
        }
        if (javadocCount == 0)
            return null;
        int[] positions = new int[2 * javadocCount];
        int index = 0;
        for (int i = 0; i <= max; i++) {
            // javadoc only (non javadoc comment have negative start and/or end positions.)
            int commentStart = this.scanner.commentStarts[i];
            if (commentStart >= 0) {
                int commentStop = this.scanner.commentStops[i];
                if (commentStop > 0) {
                    positions[index++] = commentStart;
                    //stop is one over
                    positions[index++] = commentStop - 1;
                }
            }
        }
        return positions;
    }

    public void getMethodBodies(CompilationUnitDeclaration unit) {
        if (unit == null)
            return;
        if (unit.ignoreMethodBodies) {
            unit.ignoreFurtherInvestigation = true;
            return;
        // if initial diet parse did not work, no need to dig into method bodies.
        }
        if ((unit.bits & ASTNode.HasAllMethodBodies) != 0)
            //work already done ...
            return;
        // save existing values to restore them at the end of the parsing process
        // see bug 47079 for more details
        int[] oldLineEnds = this.scanner.lineEnds;
        int oldLinePtr = this.scanner.linePtr;
        //real parse of the method....
        CompilationResult compilationResult = unit.compilationResult;
        char[] contents = this.readManager != null ? this.readManager.getContents(compilationResult.compilationUnit) : compilationResult.compilationUnit.getContents();
        this.scanner.setSource(contents, compilationResult);
        if (this.javadocParser != null && this.javadocParser.checkDocComment) {
            this.javadocParser.scanner.setSource(contents);
        }
        if (unit.types != null) {
            for (int i = 0, length = unit.types.length; i < length; i++) unit.types[i].parseMethods(this, unit);
        }
        // tag unit has having read bodies
        unit.bits |= ASTNode.HasAllMethodBodies;
        // this is done to prevent any side effects on the compilation unit result
        // line separator positions array.
        this.scanner.lineEnds = oldLineEnds;
        this.scanner.linePtr = oldLinePtr;
    }

    protected char getNextCharacter(char[] comment, int[] index) {
        char nextCharacter = comment[index[0]++];
        switch(nextCharacter) {
            case '\\':
                int c1, c2, c3, c4;
                index[0]++;
                while (comment[index[0]] == 'u') index[0]++;
                if (!(((c1 = ScannerHelper.getHexadecimalValue(comment[index[0]++])) > 15 || c1 < 0) || ((c2 = ScannerHelper.getHexadecimalValue(comment[index[0]++])) > 15 || c2 < 0) || ((c3 = ScannerHelper.getHexadecimalValue(comment[index[0]++])) > 15 || c3 < 0) || ((c4 = ScannerHelper.getHexadecimalValue(comment[index[0]++])) > 15 || c4 < 0))) {
                    nextCharacter = (char) (((c1 * 16 + c2) * 16 + c3) * 16 + c4);
                }
                break;
        }
        return nextCharacter;
    }

    protected Expression getTypeReference(Expression exp) {
        exp.bits &= ~ASTNode.RestrictiveFlagMASK;
        exp.bits |= Binding.TYPE;
        return exp;
    }

    protected void annotateTypeReference(Wildcard ref) {
        int length;
        if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
            if (ref.annotations == null)
                ref.annotations = new Annotation[ref.getAnnotatableLevels()][];
            System.arraycopy(this.typeAnnotationStack, (this.typeAnnotationPtr -= length) + 1, ref.annotations[0] = new Annotation[length], 0, length);
            if (ref.sourceStart > ref.annotations[0][0].sourceStart) {
                ref.sourceStart = ref.annotations[0][0].sourceStart;
            }
            ref.bits |= ASTNode.HasTypeAnnotations;
        }
        if (ref.bound != null) {
            ref.bits |= (ref.bound.bits & ASTNode.HasTypeAnnotations);
        }
    }

    protected TypeReference getTypeReference(int dim) {
        TypeReference ref;
        Annotation[][] annotationsOnDimensions = null;
        int length = this.identifierLengthStack[this.identifierLengthPtr--];
        if (length < 0) {
            if (dim > 0) {
                annotationsOnDimensions = getAnnotationsOnDimensions(dim);
            }
            ref = TypeReference.baseTypeReference(-length, dim, annotationsOnDimensions);
            ref.sourceStart = this.intStack[this.intPtr--];
            if (dim == 0) {
                ref.sourceEnd = this.intStack[this.intPtr--];
            } else {
                this.intPtr--;
                ref.sourceEnd = this.rBracketPosition;
            }
        } else {
            int numberOfIdentifiers = this.genericsIdentifiersLengthStack[this.genericsIdentifiersLengthPtr--];
            if (length != numberOfIdentifiers || this.genericsLengthStack[this.genericsLengthPtr] != 0) {
                ref = getTypeReferenceForGenericType(dim, length, numberOfIdentifiers);
            } else if (length == 1) {
                this.genericsLengthPtr--;
                if (dim == 0) {
                    ref = new SingleTypeReference(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--]);
                } else {
                    annotationsOnDimensions = getAnnotationsOnDimensions(dim);
                    ref = new ArrayTypeReference(this.identifierStack[this.identifierPtr], dim, annotationsOnDimensions, this.identifierPositionStack[this.identifierPtr--]);
                    ref.sourceEnd = this.endPosition;
                    if (annotationsOnDimensions != null) {
                        ref.bits |= ASTNode.HasTypeAnnotations;
                    }
                }
            } else {
                this.genericsLengthPtr--;
                char[][] tokens = new char[length][];
                this.identifierPtr -= length;
                long[] positions = new long[length];
                System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
                System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
                if (dim == 0) {
                    ref = new QualifiedTypeReference(tokens, positions);
                } else {
                    annotationsOnDimensions = getAnnotationsOnDimensions(dim);
                    ref = new ArrayQualifiedTypeReference(tokens, dim, annotationsOnDimensions, positions);
                    ref.sourceEnd = this.endPosition;
                    if (annotationsOnDimensions != null) {
                        ref.bits |= ASTNode.HasTypeAnnotations;
                    }
                }
            }
        }
        int levels = ref.getAnnotatableLevels();
        for (int i = levels - 1; i >= 0; i--) {
            if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
                if (ref.annotations == null)
                    ref.annotations = new Annotation[levels][];
                System.arraycopy(this.typeAnnotationStack, (this.typeAnnotationPtr -= length) + 1, ref.annotations[i] = new Annotation[length], 0, length);
                if (i == 0) {
                    ref.sourceStart = ref.annotations[0][0].sourceStart;
                }
                ref.bits |= ASTNode.HasTypeAnnotations;
            }
        }
        return ref;
    }

    protected TypeReference getTypeReferenceForGenericType(int dim, int identifierLength, int numberOfIdentifiers) {
        Annotation[][] annotationsOnDimensions = dim == 0 ? null : getAnnotationsOnDimensions(dim);
        if (identifierLength == 1 && numberOfIdentifiers == 1) {
            int currentTypeArgumentsLength = this.genericsLengthStack[this.genericsLengthPtr--];
            TypeReference[] typeArguments = null;
            if (currentTypeArgumentsLength < 0) {
                typeArguments = TypeReference.NO_TYPE_ARGUMENTS;
            } else {
                typeArguments = new TypeReference[currentTypeArgumentsLength];
                this.genericsPtr -= currentTypeArgumentsLength;
                System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments, 0, currentTypeArgumentsLength);
            }
            ParameterizedSingleTypeReference parameterizedSingleTypeReference = new ParameterizedSingleTypeReference(this.identifierStack[this.identifierPtr], typeArguments, dim, annotationsOnDimensions, this.identifierPositionStack[this.identifierPtr--]);
            if (dim != 0) {
                parameterizedSingleTypeReference.sourceEnd = this.endStatementPosition;
            }
            return parameterizedSingleTypeReference;
        } else {
            TypeReference[][] typeArguments = new TypeReference[numberOfIdentifiers][];
            char[][] tokens = new char[numberOfIdentifiers][];
            long[] positions = new long[numberOfIdentifiers];
            int index = numberOfIdentifiers;
            int currentIdentifiersLength = identifierLength;
            while (index > 0) {
                int currentTypeArgumentsLength = this.genericsLengthStack[this.genericsLengthPtr--];
                if (currentTypeArgumentsLength > 0) {
                    this.genericsPtr -= currentTypeArgumentsLength;
                    System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments[index - 1] = new TypeReference[currentTypeArgumentsLength], 0, currentTypeArgumentsLength);
                } else if (currentTypeArgumentsLength < 0) {
                    typeArguments[index - 1] = TypeReference.NO_TYPE_ARGUMENTS;
                }
                switch(currentIdentifiersLength) {
                    case 1:
                        tokens[index - 1] = this.identifierStack[this.identifierPtr];
                        positions[index - 1] = this.identifierPositionStack[this.identifierPtr--];
                        break;
                    default:
                        this.identifierPtr -= currentIdentifiersLength;
                        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, index - currentIdentifiersLength, currentIdentifiersLength);
                        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, index - currentIdentifiersLength, currentIdentifiersLength);
                }
                index -= currentIdentifiersLength;
                if (index > 0) {
                    currentIdentifiersLength = this.identifierLengthStack[this.identifierLengthPtr--];
                }
            }
            ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference = new ParameterizedQualifiedTypeReference(tokens, typeArguments, dim, annotationsOnDimensions, positions);
            if (dim != 0) {
                parameterizedQualifiedTypeReference.sourceEnd = this.endStatementPosition;
            }
            return parameterizedQualifiedTypeReference;
        }
    }

    protected NameReference getUnspecifiedReference() {
        return getUnspecifiedReference(true);
    }

    protected NameReference getUnspecifiedReference(boolean rejectTypeAnnotations) {
        if (rejectTypeAnnotations) {
            consumeNonTypeUseName();
        }
        int length;
        NameReference ref;
        if ((length = this.identifierLengthStack[this.identifierLengthPtr--]) == 1)
            ref = new SingleNameReference(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--]);
        else {
            char[][] tokens = new char[length][];
            this.identifierPtr -= length;
            System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
            long[] positions = new long[length];
            System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
            ref = new QualifiedNameReference(tokens, positions, (int) (this.identifierPositionStack[this.identifierPtr + 1] >> 32), (int) this.identifierPositionStack[this.identifierPtr + length]);
        }
        return ref;
    }

    protected NameReference getUnspecifiedReferenceOptimized() {
        consumeNonTypeUseName();
        int length;
        NameReference ref;
        if ((length = this.identifierLengthStack[this.identifierLengthPtr--]) == 1) {
            ref = new SingleNameReference(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--]);
            ref.bits &= ~ASTNode.RestrictiveFlagMASK;
            ref.bits |= Binding.LOCAL | Binding.FIELD;
            return ref;
        }
        char[][] tokens = new char[length][];
        this.identifierPtr -= length;
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        long[] positions = new long[length];
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        ref = new QualifiedNameReference(tokens, positions, (int) (this.identifierPositionStack[this.identifierPtr + 1] >> 32), (int) this.identifierPositionStack[this.identifierPtr + length]);
        ref.bits &= ~ASTNode.RestrictiveFlagMASK;
        ref.bits |= Binding.LOCAL | Binding.FIELD;
        return ref;
    }

    public void goForBlockStatementsopt() {
        this.firstToken = TokenNameTWIDDLE;
        this.scanner.recordLineSeparator = false;
    }

    public void goForBlockStatementsOrCatchHeader() {
        this.firstToken = TokenNameMULTIPLY;
        this.scanner.recordLineSeparator = false;
    }

    public void goForClassBodyDeclarations() {
        this.firstToken = TokenNameAND;
        this.scanner.recordLineSeparator = true;
    }

    public void goForCompilationUnit() {
        this.firstToken = TokenNamePLUS_PLUS;
        this.scanner.foundTaskCount = 0;
        this.scanner.recordLineSeparator = true;
    }

    public void goForExpression(boolean recordLineSeparator) {
        this.firstToken = TokenNameREMAINDER;
        this.scanner.recordLineSeparator = recordLineSeparator;
    }

    public void goForFieldDeclaration() {
        this.firstToken = TokenNameAND_AND;
        this.scanner.recordLineSeparator = true;
    }

    public void goForGenericMethodDeclaration() {
        this.firstToken = TokenNameDIVIDE;
        this.scanner.recordLineSeparator = true;
    }

    public void goForHeaders() {
        RecoveredType currentType = currentRecoveryType();
        if (currentType != null && currentType.insideEnumConstantPart) {
            this.firstToken = TokenNameNOT;
        } else {
            this.firstToken = TokenNameUNSIGNED_RIGHT_SHIFT;
        }
        this.scanner.recordLineSeparator = true;
    }

    public void goForImportDeclaration() {
        this.firstToken = TokenNameOR_OR;
        this.scanner.recordLineSeparator = true;
    }

    public void goForInitializer() {
        this.firstToken = TokenNameRIGHT_SHIFT;
        this.scanner.recordLineSeparator = false;
    }

    public void goForMemberValue() {
        this.firstToken = TokenNameOR_OR;
        this.scanner.recordLineSeparator = true;
    }

    public void goForMethodBody() {
        this.firstToken = TokenNameMINUS_MINUS;
        this.scanner.recordLineSeparator = false;
    }

    public void goForPackageDeclaration() {
        this.firstToken = TokenNameQUESTION;
        this.scanner.recordLineSeparator = true;
    }

    public void goForTypeDeclaration() {
        this.firstToken = TokenNamePLUS;
        this.scanner.recordLineSeparator = true;
    }

    public boolean hasLeadingTagComment(char[] commentPrefixTag, int rangeEnd) {
        int iComment = this.scanner.commentPtr;
        if (iComment < 0)
            return false;
        int iStatement = this.astLengthPtr;
        if (iStatement < 0 || this.astLengthStack[iStatement] <= 1)
            return false;
        ASTNode lastNode = this.astStack[this.astPtr];
        int rangeStart = lastNode.sourceEnd;
        previousComment: for (; iComment >= 0; iComment--) {
            int commentStart = this.scanner.commentStarts[iComment];
            if (commentStart < 0)
                commentStart = -commentStart;
            if (commentStart < rangeStart)
                return false;
            if (commentStart > rangeEnd)
                continue previousComment;
            char[] source = this.scanner.source;
            int charPos = commentStart + 2;
            for (; charPos < rangeEnd; charPos++) {
                char c = source[charPos];
                if (c >= ScannerHelper.MAX_OBVIOUS || (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & ScannerHelper.C_JLS_SPACE) == 0) {
                    break;
                }
            }
            for (int iTag = 0, length = commentPrefixTag.length; iTag < length; iTag++, charPos++) {
                if (charPos >= rangeEnd || source[charPos] != commentPrefixTag[iTag]) {
                    if (iTag == 0) {
                        return false;
                    } else {
                        // accept as tag comment -> skip it and keep searching backwards
                        continue previousComment;
                    }
                }
            }
            return true;
        }
        return false;
    }

    protected void ignoreNextClosingBrace() {
        this.ignoreNextClosingBrace = true;
    }

    protected void ignoreExpressionAssignment() {
        // Assignment ::= InvalidArrayInitializerAssignement
        // encoded operator would be: this.intStack[this.intPtr]
        this.intPtr--;
        ArrayInitializer arrayInitializer = (ArrayInitializer) this.expressionStack[this.expressionPtr--];
        this.expressionLengthPtr--;
        // report a syntax error and abort parsing
        if (!this.statementRecoveryActivated)
            problemReporter().arrayConstantsOnlyInArrayInitializers(arrayInitializer.sourceStart, arrayInitializer.sourceEnd);
    }

    public void initialize() {
        this.initialize(false);
    }

    public void initialize(boolean parsingCompilationUnit) {
        //positioning the parser for a new compilation unit
        //avoiding stack reallocation and all that....
        this.javadoc = null;
        this.astPtr = -1;
        this.astLengthPtr = -1;
        this.expressionPtr = -1;
        this.expressionLengthPtr = -1;
        this.typeAnnotationLengthPtr = -1;
        this.typeAnnotationPtr = -1;
        this.identifierPtr = -1;
        this.identifierLengthPtr = -1;
        this.intPtr = -1;
        // need to reset for further reuse
        this.nestedMethod[this.nestedType = 0] = 0;
        this.variablesCounter[this.nestedType] = 0;
        this.dimensions = 0;
        this.realBlockPtr = -1;
        this.compilationUnit = null;
        this.referenceContext = null;
        this.endStatementPosition = 0;
        this.valueLambdaNestDepth = -1;
        //remove objects from stack too, while the same parser/compiler couple is
        //re-used between two compilations ....
        int astLength = this.astStack.length;
        if (this.noAstNodes.length < astLength) {
            this.noAstNodes = new ASTNode[astLength];
        //System.out.println("Resized AST stacks : "+ astLength);
        }
        System.arraycopy(this.noAstNodes, 0, this.astStack, 0, astLength);
        int expressionLength = this.expressionStack.length;
        if (this.noExpressions.length < expressionLength) {
            this.noExpressions = new Expression[expressionLength];
        //System.out.println("Resized EXPR stacks : "+ expressionLength);
        }
        System.arraycopy(this.noExpressions, 0, this.expressionStack, 0, expressionLength);
        // reset this.scanner state
        this.scanner.commentPtr = -1;
        this.scanner.foundTaskCount = 0;
        this.scanner.eofPosition = Integer.MAX_VALUE;
        this.recordStringLiterals = true;
        final boolean checkNLS = this.options.getSeverity(CompilerOptions.NonExternalizedString) != ProblemSeverities.Ignore;
        this.checkExternalizeStrings = checkNLS;
        this.scanner.checkNonExternalizedStringLiterals = parsingCompilationUnit && checkNLS;
        this.scanner.checkUninternedIdentityComparison = parsingCompilationUnit && this.options.complainOnUninternedIdentityComparison;
        this.scanner.lastPosition = -1;
        resetModifiers();
        // recovery
        this.lastCheckPoint = -1;
        this.currentElement = null;
        this.restartRecovery = false;
        this.hasReportedError = false;
        this.recoveredStaticInitializerStart = 0;
        this.lastIgnoredToken = -1;
        this.lastErrorEndPosition = -1;
        this.lastErrorEndPositionBeforeRecovery = -1;
        this.lastJavadocEnd = -1;
        this.listLength = 0;
        this.listTypeParameterLength = 0;
        this.lastPosistion = -1;
        this.rBraceStart = 0;
        this.rBraceEnd = 0;
        this.rBraceSuccessorStart = 0;
        this.rBracketPosition = 0;
        this.genericsIdentifiersLengthPtr = -1;
        this.genericsLengthPtr = -1;
        this.genericsPtr = -1;
    }

    public void initializeScanner() {
        this.scanner = new Scanner(/*comment*/
        false, /*whitespace*/
        false, /* will be set in initialize(boolean) */
        false, this.options.sourceLevel, /*sourceLevel*/
        this.options.complianceLevel, /*complianceLevel*/
        this.options.taskTags, /*taskTags*/
        this.options.taskPriorities, /*taskPriorities*/
        this.options.isTaskCaseSensitive);
    }

    public void jumpOverMethodBody() {
        if (this.diet && (this.dietInt == 0))
            this.scanner.diet = true;
    }

    private void jumpOverType() {
        if (this.recoveredTypes != null && this.nextTypeStart > -1 && this.nextTypeStart < this.scanner.currentPosition) {
            if (DEBUG_AUTOMATON) {
                //$NON-NLS-1$
                System.out.println("Jump         -");
            }
            TypeDeclaration typeDeclaration = this.recoveredTypes[this.recoveredTypePtr];
            boolean isAnonymous = typeDeclaration.allocation != null;
            this.scanner.startPosition = typeDeclaration.declarationSourceEnd + 1;
            this.scanner.currentPosition = typeDeclaration.declarationSourceEnd + 1;
            // quit jumping over method bodies
            this.scanner.diet = false;
            if (!isAnonymous) {
                ((RecoveryScanner) this.scanner).setPendingTokens(new int[] { TokenNameSEMICOLON, TokenNamebreak });
            } else {
                ((RecoveryScanner) this.scanner).setPendingTokens(new int[] { TokenNameIdentifier, TokenNameEQUAL, TokenNameIdentifier });
            }
            this.pendingRecoveredType = typeDeclaration;
            try {
                this.currentToken = this.scanner.getNextToken();
            } catch (InvalidInputException e) {
            }
            if (++this.recoveredTypePtr < this.recoveredTypes.length) {
                TypeDeclaration nextTypeDeclaration = this.recoveredTypes[this.recoveredTypePtr];
                this.nextTypeStart = nextTypeDeclaration.allocation == null ? nextTypeDeclaration.declarationSourceStart : nextTypeDeclaration.allocation.sourceStart;
            } else {
                this.nextTypeStart = Integer.MAX_VALUE;
            }
        }
    }

    protected void markEnclosingMemberWithLocalType() {
        // this is already done in the recovery code
        if (this.currentElement != null)
            return;
        markEnclosingMemberWithLocalOrFunctionalType(LocalTypeKind.LOCAL);
    }

    protected void markEnclosingMemberWithLocalOrFunctionalType(LocalTypeKind context) {
        for (int i = this.astPtr; i >= 0; i--) {
            ASTNode node = this.astStack[i];
            if (node instanceof AbstractMethodDeclaration || node instanceof FieldDeclaration || (// mark type for now: all initializers will be marked when added to this type
            node instanceof TypeDeclaration && // and enclosing type must not be closed (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=147485)
            ((TypeDeclaration) node).declarationSourceEnd == 0)) {
                switch(context) {
                    case METHOD_REFERENCE:
                        node.bits |= ASTNode.HasFunctionalInterfaceTypes;
                        break;
                    case LAMBDA:
                        node.bits |= ASTNode.HasFunctionalInterfaceTypes;
                    //$FALL-THROUGH$
                    case LOCAL:
                        node.bits |= ASTNode.HasLocalType;
                }
                return;
            }
        }
        // default to reference context (case of parse method body)
        if (this.referenceContext instanceof AbstractMethodDeclaration || this.referenceContext instanceof TypeDeclaration) {
            ASTNode node = (ASTNode) this.referenceContext;
            switch(context) {
                case METHOD_REFERENCE:
                    node.bits |= ASTNode.HasFunctionalInterfaceTypes;
                    break;
                case LAMBDA:
                    node.bits |= ASTNode.HasFunctionalInterfaceTypes;
                //$FALL-THROUGH$
                case LOCAL:
                    node.bits |= ASTNode.HasLocalType;
            }
        }
    }

    /*
 * Move checkpoint location (current implementation is moving it by one token)
 *
 * Answers true if successfully moved checkpoint (in other words, it did not attempt to move it
 * beyond end of file).
 */
    protected boolean moveRecoveryCheckpoint() {
        int pos = this.lastCheckPoint;
        /* reset this.scanner, and move checkpoint by one token */
        this.scanner.startPosition = pos;
        this.scanner.currentPosition = pos;
        // quit jumping over method bodies
        this.scanner.diet = false;
        /* if about to restart, then no need to shift token */
        if (this.restartRecovery) {
            this.lastIgnoredToken = -1;
            this.scanner.insideRecovery = true;
            return true;
        }
        /* protect against shifting on an invalid token */
        this.lastIgnoredToken = this.nextIgnoredToken;
        this.nextIgnoredToken = -1;
        do {
            try {
                // stay clear of the voodoo in the present method
                this.scanner.lookBack[0] = this.scanner.lookBack[1] = TokenNameNotAToken;
                this.nextIgnoredToken = this.scanner.getNextToken();
                if (this.scanner.currentPosition == this.scanner.startPosition) {
                    // on fake completion identifier
                    this.scanner.currentPosition++;
                    this.nextIgnoredToken = -1;
                }
            } catch (InvalidInputException e) {
                pos = this.scanner.currentPosition;
            } finally {
                // steer clear of the voodoo in the present method
                this.scanner.lookBack[0] = this.scanner.lookBack[1] = TokenNameNotAToken;
            }
        } while (this.nextIgnoredToken < 0);
        if (// no more recovery after this point
        this.nextIgnoredToken == TokenNameEOF) {
            if (// already tried one iteration on EOF
            this.currentToken == TokenNameEOF) {
                return false;
            }
        }
        this.lastCheckPoint = this.scanner.currentPosition;
        /* reset this.scanner again to previous checkpoint location*/
        this.scanner.startPosition = pos;
        this.scanner.currentPosition = pos;
        this.scanner.commentPtr = -1;
        this.scanner.foundTaskCount = 0;
        return true;
    /*
 	The following implementation moves the checkpoint location by one line:

	int pos = this.lastCheckPoint;
	// reset this.scanner, and move checkpoint by one token
	this.scanner.startPosition = pos;
	this.scanner.currentPosition = pos;
	this.scanner.diet = false; // quit jumping over method bodies

	// if about to restart, then no need to shift token
	if (this.restartRecovery){
		this.lastIgnoredToken = -1;
		return true;
	}

	// protect against shifting on an invalid token
	this.lastIgnoredToken = this.nextIgnoredToken;
	this.nextIgnoredToken = -1;

	boolean wasTokenizingWhiteSpace = this.scanner.tokenizeWhiteSpace;
	this.scanner.tokenizeWhiteSpace = true;
	checkpointMove:
		do {
			try {
				this.nextIgnoredToken = this.scanner.getNextToken();
				switch(this.nextIgnoredToken){
					case Scanner.TokenNameWHITESPACE :
						if(this.scanner.getLineNumber(this.scanner.startPosition)
							== this.scanner.getLineNumber(this.scanner.currentPosition)){
							this.nextIgnoredToken = -1;
							}
						break;
					case TokenNameSEMICOLON :
					case TokenNameLBRACE :
					case TokenNameRBRACE :
						break;
					case TokenNameIdentifier :
						if(this.scanner.currentPosition == this.scanner.startPosition){
							this.scanner.currentPosition++; // on fake completion identifier
						}
					default:
						this.nextIgnoredToken = -1;
						break;
					case TokenNameEOF :
						break checkpointMove;
				}
			} catch(InvalidInputException e){
				pos = this.scanner.currentPosition;
			}
		} while (this.nextIgnoredToken < 0);
	this.scanner.tokenizeWhiteSpace = wasTokenizingWhiteSpace;

	if (this.nextIgnoredToken == TokenNameEOF) { // no more recovery after this point
		if (this.currentToken == TokenNameEOF) { // already tried one iteration on EOF
			return false;
		}
	}
	this.lastCheckPoint = this.scanner.currentPosition;

	// reset this.scanner again to previous checkpoint location
	this.scanner.startPosition = pos;
	this.scanner.currentPosition = pos;
	this.scanner.commentPtr = -1;

	return true;
*/
    }

    protected MessageSend newMessageSend() {
        // '(' ArgumentListopt ')'
        // the arguments are on the expression stack
        MessageSend m = new MessageSend();
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            this.expressionPtr -= length;
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, m.arguments = new Expression[length], 0, length);
        }
        return m;
    }

    protected MessageSend newMessageSendWithTypeArguments() {
        MessageSend m = new MessageSend();
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            this.expressionPtr -= length;
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, m.arguments = new Expression[length], 0, length);
        }
        return m;
    }

    protected void optimizedConcatNodeLists() {
        /*back from a recursive loop. Virtualy group the
	astNode into an array using this.astLengthStack*/
        /*
	 * This is a case where you have two sublists into the this.astStack that you want
	 * to merge in one list. There is no action required on the this.astStack. The only
	 * thing you need to do is merge the two lengths specified on the astStackLength.
	 * The top two length are for example:
	 * ... p   n
	 * and you want to result in a list like:
	 * ... n+p
	 * This means that the p could be equals to 0 in case there is no astNode pushed
	 * on the this.astStack.
	 * Look at the InterfaceMemberDeclarations for an example.
	 * This case optimizes the fact that p == 1.
	 */
        this.astLengthStack[--this.astLengthPtr]++;
    }

    public boolean atConflictScenario(int token) {
        /* Answer true if the parser is at a configuration where the scanner must look ahead and help disambiguate between (a) '<' as an operator and '<' as the
	   start of <type argument> and (b) the use of '(' in '(' expression ')' and '( type ')' and '(' lambda formal parameters ')'. (c) whether the token @
	   begins a Java SE5 style declaration annotation or if it begins a SE8 style type annotation. When requested thus, the scanner helps by fabricating 
	   synthetic tokens and injecting them into the stream ahead of the tokens that trigger conflicts in the absence of these artificial tokens. These 
	   manufactured token help transform the grammar into LALR(1) by splitting the states so that they have unambigious prefixes.
	   
	   We do this by claiming to the automaton that the next token seen is the (suitable) synthetic token and observing the response of the state machine. 
	   Error signals we are NOT at a conflict site, while shift or shift/reduce signals that we are. Accept is impossible, while there may be intermediate
	   reductions that are called for -- It is axiomatic of the push down automaton that corresponds to the LALR grammar that it will never shift on invalid
	   input.
	   
	   Obviously, the dry runs should not alter the parser state in any way or otherwise cause side effects. Proof by argument that this is the case:
	   
	       - The only pieces of state needed to answer the question are: this.stack, this.stateStackTop and the last action variable `act`. None of the various
	         and sundry stacks used in the AST constructions process are touched here.
	       - As we reduce, we DON'T call the semantic action functions i.e the consume* method calls are skipped.
	       - Lexer stream is left untouched.
	       - this.stateStackTop and the last action variable `act` of the automaton are readily cloned, these being primitives and changes are to the replicas.
	       - We never remove elements from the state stack here (or elsewhere for that matter). Pops are implemented by mere adjustments of the stack pointer.
	       - During this algorithm, either the stack pointer monotonically decreases or stays fixed. (The only way for the stack pointer to increase would call
	         for a shift or a shift/reduce at which point the algorithm is ready to terminate already.) This means that we don't have to replicate the stack. 
	         Pushes can be mimiced by writing to a local stackTopState variable, leaving the original stack untouched.
	         
	    Though this code looks complex, we should exit early in most situations.     
	 */
        if (// automaton is not running.
        this.unstackedAct == ERROR_ACTION) {
            return false;
        }
        if (token != TokenNameAT) {
            token = token == TokenNameLPAREN ? TokenNameBeginLambda : TokenNameBeginTypeArguments;
        }
        return automatonWillShift(token, this.unstackedAct);
    }

    /*main loop of the automat
When a rule is reduced, the method consumeRule(int) is called with the number
of the consumed rule. When a terminal is consumed, the method consumeToken(int) is
called in order to remember (when needed) the consumed token */
    // (int)asr[asi(act)]
    // name[symbol_index[currentKind]]
    protected void parse() {
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println("-- ENTER INSIDE PARSE METHOD --");
        if (DEBUG_AUTOMATON) {
            //$NON-NLS-1$
            System.out.println("- Start --------------------------------");
        }
        boolean isDietParse = this.diet;
        int oldFirstToken = getFirstToken();
        this.hasError = false;
        this.hasReportedError = false;
        int act = START_STATE;
        this.unstackedAct = ERROR_ACTION;
        this.stateStackTop = -1;
        this.currentToken = getFirstToken();
        try {
            this.scanner.setActiveParser(this);
            ProcessTerminals: for (; ; ) {
                int stackLength = this.stack.length;
                if (++this.stateStackTop >= stackLength) {
                    System.arraycopy(this.stack, 0, this.stack = new int[stackLength + StackIncrement], 0, stackLength);
                }
                this.stack[this.stateStackTop] = act;
                this.unstackedAct = act = tAction(act, this.currentToken);
                if (act == ERROR_ACTION || this.restartRecovery) {
                    if (DEBUG_AUTOMATON) {
                        if (this.restartRecovery) {
                            System.out.println("Restart      - ");
                        } else {
                            System.out.println("Error        - ");
                        }
                    }
                    int errorPos = this.scanner.currentPosition - 1;
                    if (!this.hasReportedError) {
                        this.hasError = true;
                    }
                    int previousToken = this.currentToken;
                    switch(resumeOnSyntaxError()) {
                        case HALT:
                            act = ERROR_ACTION;
                            break ProcessTerminals;
                        case RESTART:
                            if (act == ERROR_ACTION && previousToken != 0)
                                this.lastErrorEndPosition = errorPos;
                            act = START_STATE;
                            this.stateStackTop = -1;
                            this.currentToken = getFirstToken();
                            continue ProcessTerminals;
                        case RESUME:
                            if (act == ERROR_ACTION) {
                                act = this.stack[this.stateStackTop--];
                                continue ProcessTerminals;
                            } else {
                            // FALL THROUGH.	
                            }
                    }
                }
                if (act <= NUM_RULES) {
                    this.stateStackTop--;
                    if (DEBUG_AUTOMATON) {
                        //$NON-NLS-1$
                        System.out.print(//$NON-NLS-1$
                        "Reduce       - ");
                    }
                } else if (act > ERROR_ACTION) /* shift-reduce */
                {
                    consumeToken(this.currentToken);
                    if (this.currentElement != null) {
                        boolean oldValue = this.recordStringLiterals;
                        this.recordStringLiterals = false;
                        recoveryTokenCheck();
                        this.recordStringLiterals = oldValue;
                    }
                    try {
                        this.currentToken = this.scanner.getNextToken();
                    } catch (InvalidInputException e) {
                        if (!this.hasReportedError) {
                            problemReporter().scannerError(this, e.getMessage());
                            this.hasReportedError = true;
                        }
                        this.lastCheckPoint = this.scanner.currentPosition;
                        this.currentToken = 0;
                        this.restartRecovery = true;
                    }
                    if (this.statementRecoveryActivated) {
                        jumpOverType();
                    }
                    this.unstackedAct = act -= ERROR_ACTION;
                    if (DEBUG_AUTOMATON) {
                        //$NON-NLS-1$  //$NON-NLS-2$
                        System.out.print("Shift/Reduce - (" + name[terminal_index[this.currentToken]] + ") ");
                    }
                } else {
                    if (act < ACCEPT_ACTION) /* shift */
                    {
                        consumeToken(this.currentToken);
                        if (this.currentElement != null) {
                            boolean oldValue = this.recordStringLiterals;
                            this.recordStringLiterals = false;
                            recoveryTokenCheck();
                            this.recordStringLiterals = oldValue;
                        }
                        try {
                            this.currentToken = this.scanner.getNextToken();
                        } catch (InvalidInputException e) {
                            if (!this.hasReportedError) {
                                problemReporter().scannerError(this, e.getMessage());
                                this.hasReportedError = true;
                            }
                            this.lastCheckPoint = this.scanner.currentPosition;
                            this.currentToken = 0;
                            this.restartRecovery = true;
                        }
                        if (this.statementRecoveryActivated) {
                            jumpOverType();
                        }
                        if (DEBUG_AUTOMATON) {
                            //$NON-NLS-1$  //$NON-NLS-2$
                            System.out.println("Shift        - (" + name[terminal_index[this.currentToken]] + ")");
                        }
                        continue ProcessTerminals;
                    }
                    break ProcessTerminals;
                }
                /* reduce */
                do {
                    if (DEBUG_AUTOMATON) {
                        System.out.println(name[non_terminal_index[lhs[act]]]);
                    }
                    this.stateStackTop -= (rhs[act] - 1);
                    this.unstackedAct = ntAction(this.stack[this.stateStackTop], lhs[act]);
                    consumeRule(act);
                    act = this.unstackedAct;
                    if (DEBUG_AUTOMATON) {
                        if (act <= NUM_RULES) {
                            System.out.print("             - ");
                        }
                    }
                } while (act <= NUM_RULES);
                if (DEBUG_AUTOMATON) {
                    //$NON-NLS-1$
                    System.out.println("----------------------------------------");
                }
            }
        } finally {
            this.unstackedAct = ERROR_ACTION;
            this.scanner.setActiveParser(null);
        }
        if (DEBUG_AUTOMATON) {
            //$NON-NLS-1$
            System.out.println("- End ----------------------------------");
        }
        endParse(act);
        // record all nls tags in the corresponding compilation unit
        final NLSTag[] tags = this.scanner.getNLSTags();
        if (tags != null) {
            this.compilationUnit.nlsTags = tags;
        }
        this.scanner.checkNonExternalizedStringLiterals = false;
        if (this.scanner.checkUninternedIdentityComparison) {
            this.compilationUnit.validIdentityComparisonLines = this.scanner.getIdentityComparisonLines();
            this.scanner.checkUninternedIdentityComparison = false;
        }
        if (this.reportSyntaxErrorIsRequired && this.hasError && !this.statementRecoveryActivated) {
            if (!this.options.performStatementsRecovery) {
                reportSyntaxErrors(isDietParse, oldFirstToken);
            } else {
                RecoveryScannerData data = this.referenceContext.compilationResult().recoveryScannerData;
                if (this.recoveryScanner == null) {
                    this.recoveryScanner = new RecoveryScanner(this.scanner, data);
                } else {
                    this.recoveryScanner.setData(data);
                }
                this.recoveryScanner.setSource(this.scanner.source);
                this.recoveryScanner.lineEnds = this.scanner.lineEnds;
                this.recoveryScanner.linePtr = this.scanner.linePtr;
                reportSyntaxErrors(isDietParse, oldFirstToken);
                if (data == null) {
                    this.referenceContext.compilationResult().recoveryScannerData = this.recoveryScanner.getData();
                }
                if (this.methodRecoveryActivated && this.options.performStatementsRecovery) {
                    this.methodRecoveryActivated = false;
                    recoverStatements();
                    this.methodRecoveryActivated = true;
                    this.lastAct = ERROR_ACTION;
                }
            }
        }
        // Null this so we won't escalate problems needlessly (bug 393192)
        this.problemReporter.referenceContext = null;
        //$NON-NLS-1$
        if (DEBUG)
            System.out.println("-- EXIT FROM PARSE METHOD --");
    }

    public void parse(ConstructorDeclaration cd, CompilationUnitDeclaration unit, boolean recordLineSeparator) {
        //only parse the method body of cd
        //fill out its statements
        //convert bugs into parse error
        boolean oldMethodRecoveryActivated = this.methodRecoveryActivated;
        if (this.options.performMethodsFullRecovery) {
            this.methodRecoveryActivated = true;
            // we should not relocate bodyStart if there is a block within the statements
            this.ignoreNextOpeningBrace = true;
        }
        initialize();
        goForBlockStatementsopt();
        if (recordLineSeparator) {
            this.scanner.recordLineSeparator = true;
        }
        this.nestedMethod[this.nestedType]++;
        pushOnRealBlockStack(0);
        this.referenceContext = cd;
        this.compilationUnit = unit;
        this.scanner.resetTo(cd.bodyStart, cd.bodyEnd);
        try {
            parse();
        } catch (AbortCompilation ex) {
            this.lastAct = ERROR_ACTION;
        } finally {
            this.nestedMethod[this.nestedType]--;
            if (this.options.performStatementsRecovery) {
                this.methodRecoveryActivated = oldMethodRecoveryActivated;
            }
        }
        checkNonNLSAfterBodyEnd(cd.declarationSourceEnd);
        if (this.lastAct == ERROR_ACTION) {
            cd.bits |= ASTNode.HasSyntaxErrors;
            initialize();
            return;
        }
        //statements
        cd.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
        int length;
        if (this.astLengthPtr > -1 && (length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.astPtr -= length;
            if (!this.options.ignoreMethodBodies) {
                if (this.astStack[this.astPtr + 1] instanceof ExplicitConstructorCall) //avoid a isSomeThing that would only be used here BUT what is faster between two alternatives ?
                {
                    System.arraycopy(this.astStack, this.astPtr + 2, cd.statements = new Statement[length - 1], 0, length - 1);
                    cd.constructorCall = (ExplicitConstructorCall) this.astStack[this.astPtr + 1];
                } else //need to add explicitly the super();
                {
                    System.arraycopy(this.astStack, this.astPtr + 1, cd.statements = new Statement[length], 0, length);
                    cd.constructorCall = SuperReference.implicitSuperConstructorCall();
                }
            }
        } else {
            if (!this.options.ignoreMethodBodies) {
                cd.constructorCall = SuperReference.implicitSuperConstructorCall();
            }
            if (!containsComment(cd.bodyStart, cd.bodyEnd)) {
                cd.bits |= ASTNode.UndocumentedEmptyBlock;
            }
        }
        ExplicitConstructorCall explicitConstructorCall = cd.constructorCall;
        if (explicitConstructorCall != null && explicitConstructorCall.sourceEnd == 0) {
            explicitConstructorCall.sourceEnd = cd.sourceEnd;
            explicitConstructorCall.sourceStart = cd.sourceStart;
        }
    }

    // A P I
    public void parse(FieldDeclaration field, TypeDeclaration type, CompilationUnitDeclaration unit, char[] initializationSource) {
        //only parse the initializationSource of the given field
        //convert bugs into parse error
        initialize();
        goForExpression(true);
        this.nestedMethod[this.nestedType]++;
        this.referenceContext = type;
        this.compilationUnit = unit;
        this.scanner.setSource(initializationSource);
        this.scanner.resetTo(0, initializationSource.length - 1);
        try {
            parse();
        } catch (AbortCompilation ex) {
            this.lastAct = ERROR_ACTION;
        } finally {
            this.nestedMethod[this.nestedType]--;
        }
        if (this.lastAct == ERROR_ACTION) {
            field.bits |= ASTNode.HasSyntaxErrors;
            return;
        }
        field.initialization = this.expressionStack[this.expressionPtr];
        // mark field with local type if one was found during parsing
        if ((type.bits & ASTNode.HasLocalType) != 0) {
            field.bits |= ASTNode.HasLocalType;
        }
    }

    // A P I
    public CompilationUnitDeclaration parse(ICompilationUnit sourceUnit, CompilationResult compilationResult) {
        return parse(sourceUnit, compilationResult, -1, /*parse without reseting the scanner*/
        -1);
    }

    // A P I
    public CompilationUnitDeclaration parse(ICompilationUnit sourceUnit, CompilationResult compilationResult, int start, int end) {
        // parses a compilation unit and manages error handling (even bugs....)
        CompilationUnitDeclaration unit;
        try {
            /* automaton initialization */
            initialize(true);
            goForCompilationUnit();
            /* unit creation */
            this.referenceContext = this.compilationUnit = new CompilationUnitDeclaration(this.problemReporter, compilationResult, 0);
            /* scanners initialization */
            char[] contents;
            try {
                contents = this.readManager != null ? this.readManager.getContents(sourceUnit) : sourceUnit.getContents();
            } catch (AbortCompilationUnit abortException) {
                problemReporter().cannotReadSource(this.compilationUnit, abortException, this.options.verbose);
                contents = CharOperation.NO_CHAR;
            }
            this.scanner.setSource(contents);
            this.compilationUnit.sourceEnd = this.scanner.source.length - 1;
            if (end != -1)
                this.scanner.resetTo(start, end);
            if (this.javadocParser != null && this.javadocParser.checkDocComment) {
                this.javadocParser.scanner.setSource(contents);
                if (end != -1) {
                    this.javadocParser.scanner.resetTo(start, end);
                }
            }
            /* run automaton */
            parse();
        } finally {
            unit = this.compilationUnit;
            // reset parser
            this.compilationUnit = null;
            // tag unit has having read bodies
            if (!this.diet)
                unit.bits |= ASTNode.HasAllMethodBodies;
        }
        return unit;
    }

    // A P I
    public void parse(Initializer initializer, TypeDeclaration type, CompilationUnitDeclaration unit) {
        //only parse the method body of md
        //fill out method statements
        //convert bugs into parse error
        boolean oldMethodRecoveryActivated = this.methodRecoveryActivated;
        if (this.options.performMethodsFullRecovery) {
            this.methodRecoveryActivated = true;
        }
        initialize();
        goForBlockStatementsopt();
        this.nestedMethod[this.nestedType]++;
        pushOnRealBlockStack(0);
        this.referenceContext = type;
        this.compilationUnit = unit;
        // just on the beginning {
        this.scanner.resetTo(initializer.bodyStart, initializer.bodyEnd);
        try {
            parse();
        } catch (AbortCompilation ex) {
            this.lastAct = ERROR_ACTION;
        } finally {
            this.nestedMethod[this.nestedType]--;
            if (this.options.performStatementsRecovery) {
                this.methodRecoveryActivated = oldMethodRecoveryActivated;
            }
        }
        checkNonNLSAfterBodyEnd(initializer.declarationSourceEnd);
        if (this.lastAct == ERROR_ACTION) {
            initializer.bits |= ASTNode.HasSyntaxErrors;
            return;
        }
        //refill statements
        initializer.block.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
        int length;
        if (this.astLengthPtr > -1 && (length = this.astLengthStack[this.astLengthPtr--]) > 0) {
            System.arraycopy(this.astStack, (this.astPtr -= length) + 1, initializer.block.statements = new Statement[length], 0, length);
        } else {
            // check whether this block at least contains some comment in it
            if (!containsComment(initializer.block.sourceStart, initializer.block.sourceEnd)) {
                initializer.block.bits |= ASTNode.UndocumentedEmptyBlock;
            }
        }
        // mark initializer with local type if one was found during parsing
        if ((type.bits & ASTNode.HasLocalType) != 0) {
            initializer.bits |= ASTNode.HasLocalType;
        }
    }

    // A P I
    public void parse(MethodDeclaration md, CompilationUnitDeclaration unit) {
        if (md.isAbstract())
            return;
        if (md.isNative())
            return;
        if ((md.modifiers & ExtraCompilerModifiers.AccSemicolonBody) != 0)
            return;
        boolean oldMethodRecoveryActivated = this.methodRecoveryActivated;
        if (this.options.performMethodsFullRecovery) {
            // we should not relocate bodyStart if there is a block within the statements
            this.ignoreNextOpeningBrace = true;
            this.methodRecoveryActivated = true;
            this.rParenPos = md.sourceEnd;
        }
        initialize();
        goForBlockStatementsopt();
        this.nestedMethod[this.nestedType]++;
        pushOnRealBlockStack(0);
        this.referenceContext = md;
        this.compilationUnit = unit;
        this.scanner.resetTo(md.bodyStart, md.bodyEnd);
        // reset the scanner to parser from { down to }
        try {
            parse();
        } catch (AbortCompilation ex) {
            this.lastAct = ERROR_ACTION;
        } finally {
            this.nestedMethod[this.nestedType]--;
            if (this.options.performStatementsRecovery) {
                this.methodRecoveryActivated = oldMethodRecoveryActivated;
            }
        }
        checkNonNLSAfterBodyEnd(md.declarationSourceEnd);
        if (this.lastAct == ERROR_ACTION) {
            md.bits |= ASTNode.HasSyntaxErrors;
            return;
        }
        //refill statements
        md.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
        int length;
        if (this.astLengthPtr > -1 && (length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            if (this.options.ignoreMethodBodies) {
                // ignore statements
                this.astPtr -= length;
            } else {
                System.arraycopy(this.astStack, (this.astPtr -= length) + 1, md.statements = new Statement[length], 0, length);
            }
        } else {
            if (!containsComment(md.bodyStart, md.bodyEnd)) {
                md.bits |= ASTNode.UndocumentedEmptyBlock;
            }
        }
    }

    public ASTNode[] parseClassBodyDeclarations(char[] source, int offset, int length, CompilationUnitDeclaration unit) {
        boolean oldDiet = this.diet;
        int oldInt = this.dietInt;
        boolean oldTolerateDefaultClassMethods = this.tolerateDefaultClassMethods;
        /* automaton initialization */
        initialize();
        goForClassBodyDeclarations();
        /* scanner initialization */
        this.scanner.setSource(source);
        this.scanner.resetTo(offset, offset + length - 1);
        if (this.javadocParser != null && this.javadocParser.checkDocComment) {
            this.javadocParser.scanner.setSource(source);
            this.javadocParser.scanner.resetTo(offset, offset + length - 1);
        }
        /* type declaration should be parsed as member type declaration */
        this.nestedType = 1;
        /* unit creation */
        TypeDeclaration referenceContextTypeDeclaration = new TypeDeclaration(unit.compilationResult);
        referenceContextTypeDeclaration.name = Util.EMPTY_STRING.toCharArray();
        referenceContextTypeDeclaration.fields = new FieldDeclaration[0];
        this.compilationUnit = unit;
        unit.types = new TypeDeclaration[1];
        unit.types[0] = referenceContextTypeDeclaration;
        this.referenceContext = unit;
        /* run automaton */
        try {
            this.diet = true;
            this.dietInt = 0;
            this.tolerateDefaultClassMethods = this.parsingJava8Plus;
            parse();
        } catch (AbortCompilation ex) {
            this.lastAct = ERROR_ACTION;
        } finally {
            this.diet = oldDiet;
            this.dietInt = oldInt;
            this.tolerateDefaultClassMethods = oldTolerateDefaultClassMethods;
        }
        ASTNode[] result = null;
        if (this.lastAct == ERROR_ACTION) {
            if (!this.options.performMethodsFullRecovery && !this.options.performStatementsRecovery) {
                return null;
            }
            // collect all body declaration inside the compilation unit except the default constructor
            final List bodyDeclarations = new ArrayList();
            ASTVisitor visitor = new ASTVisitor() {

                public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
                    if (!methodDeclaration.isDefaultConstructor()) {
                        bodyDeclarations.add(methodDeclaration);
                    }
                    return false;
                }

                public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope) {
                    bodyDeclarations.add(fieldDeclaration);
                    return false;
                }

                public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope scope) {
                    bodyDeclarations.add(memberTypeDeclaration);
                    return false;
                }
            };
            unit.ignoreFurtherInvestigation = false;
            unit.traverse(visitor, unit.scope);
            unit.ignoreFurtherInvestigation = true;
            result = (ASTNode[]) bodyDeclarations.toArray(new ASTNode[bodyDeclarations.size()]);
        } else {
            int astLength;
            if (this.astLengthPtr > -1 && (astLength = this.astLengthStack[this.astLengthPtr--]) != 0) {
                result = new ASTNode[astLength];
                this.astPtr -= astLength;
                System.arraycopy(this.astStack, this.astPtr + 1, result, 0, astLength);
            } else {
                // empty class body declaration (like ';' see https://bugs.eclipse.org/bugs/show_bug.cgi?id=280079).
                result = new ASTNode[0];
            }
        }
        boolean containsInitializers = false;
        TypeDeclaration typeDeclaration = null;
        for (int i = 0, max = result.length; i < max; i++) {
            // parse each class body declaration
            ASTNode node = result[i];
            if (node instanceof TypeDeclaration) {
                ((TypeDeclaration) node).parseMethods(this, unit);
            } else if (node instanceof AbstractMethodDeclaration) {
                ((AbstractMethodDeclaration) node).parseStatements(this, unit);
            } else if (node instanceof FieldDeclaration) {
                FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
                switch(fieldDeclaration.getKind()) {
                    case AbstractVariableDeclaration.INITIALIZER:
                        containsInitializers = true;
                        if (typeDeclaration == null) {
                            typeDeclaration = referenceContextTypeDeclaration;
                        }
                        if (typeDeclaration.fields == null) {
                            typeDeclaration.fields = new FieldDeclaration[1];
                            typeDeclaration.fields[0] = fieldDeclaration;
                        } else {
                            int length2 = typeDeclaration.fields.length;
                            FieldDeclaration[] temp = new FieldDeclaration[length2 + 1];
                            System.arraycopy(typeDeclaration.fields, 0, temp, 0, length2);
                            temp[length2] = fieldDeclaration;
                            typeDeclaration.fields = temp;
                        }
                        break;
                }
            }
            if (((node.bits & ASTNode.HasSyntaxErrors) != 0) && (!this.options.performMethodsFullRecovery && !this.options.performStatementsRecovery)) {
                return null;
            }
        }
        if (containsInitializers) {
            FieldDeclaration[] fieldDeclarations = typeDeclaration.fields;
            for (int i = 0, max = fieldDeclarations.length; i < max; i++) {
                Initializer initializer = (Initializer) fieldDeclarations[i];
                initializer.parseStatements(this, typeDeclaration, unit);
                if (((initializer.bits & ASTNode.HasSyntaxErrors) != 0) && (!this.options.performMethodsFullRecovery && !this.options.performStatementsRecovery)) {
                    return null;
                }
            }
        }
        return result;
    }

    public Expression parseLambdaExpression(char[] source, int offset, int length, CompilationUnitDeclaration unit, boolean recordLineSeparators) {
        // unexposed/unshared object, no threading concerns.
        this.haltOnSyntaxError = true;
        this.reparsingLambdaExpression = true;
        return parseExpression(source, offset, length, unit, recordLineSeparators);
    }

    public Expression parseExpression(char[] source, int offset, int length, CompilationUnitDeclaration unit, boolean recordLineSeparators) {
        initialize();
        goForExpression(recordLineSeparators);
        this.nestedMethod[this.nestedType]++;
        this.referenceContext = unit;
        this.compilationUnit = unit;
        this.scanner.setSource(source);
        this.scanner.resetTo(offset, offset + length - 1);
        try {
            parse();
        } catch (AbortCompilation ex) {
            this.lastAct = ERROR_ACTION;
        } finally {
            this.nestedMethod[this.nestedType]--;
        }
        if (this.lastAct == ERROR_ACTION) {
            return null;
        }
        return this.expressionStack[this.expressionPtr];
    }

    public Expression parseMemberValue(char[] source, int offset, int length, CompilationUnitDeclaration unit) {
        initialize();
        goForMemberValue();
        this.nestedMethod[this.nestedType]++;
        this.referenceContext = unit;
        this.compilationUnit = unit;
        this.scanner.setSource(source);
        this.scanner.resetTo(offset, offset + length - 1);
        try {
            parse();
        } catch (AbortCompilation ex) {
            this.lastAct = ERROR_ACTION;
        } finally {
            this.nestedMethod[this.nestedType]--;
        }
        if (this.lastAct == ERROR_ACTION) {
            return null;
        }
        return this.expressionStack[this.expressionPtr];
    }

    public void parseStatements(ReferenceContext rc, int start, int end, TypeDeclaration[] types, CompilationUnitDeclaration unit) {
        boolean oldStatementRecoveryEnabled = this.statementRecoveryActivated;
        this.statementRecoveryActivated = true;
        initialize();
        goForBlockStatementsopt();
        this.nestedMethod[this.nestedType]++;
        pushOnRealBlockStack(0);
        pushOnAstLengthStack(0);
        this.referenceContext = rc;
        this.compilationUnit = unit;
        this.pendingRecoveredType = null;
        if (types != null && types.length > 0) {
            this.recoveredTypes = types;
            this.recoveredTypePtr = 0;
            this.nextTypeStart = this.recoveredTypes[0].allocation == null ? this.recoveredTypes[0].declarationSourceStart : this.recoveredTypes[0].allocation.sourceStart;
        } else {
            this.recoveredTypes = null;
            this.recoveredTypePtr = -1;
            this.nextTypeStart = -1;
        }
        this.scanner.resetTo(start, end);
        // reset the scanner to parser from { down to }
        this.lastCheckPoint = this.scanner.initialPosition;
        this.stateStackTop = -1;
        try {
            parse();
        } catch (AbortCompilation ex) {
            this.lastAct = ERROR_ACTION;
        } finally {
            this.nestedMethod[this.nestedType]--;
            this.recoveredTypes = null;
            this.statementRecoveryActivated = oldStatementRecoveryEnabled;
        }
        checkNonNLSAfterBodyEnd(end);
    }

    public void persistLineSeparatorPositions() {
        if (this.scanner.recordLineSeparator) {
            this.compilationUnit.compilationResult.lineSeparatorPositions = this.scanner.getLineEnds();
        }
    }

    /*
 * Prepares the state of the parser to go for BlockStatements.
 */
    protected void prepareForBlockStatements() {
        this.nestedMethod[this.nestedType = 0] = 1;
        this.variablesCounter[this.nestedType] = 0;
        this.realBlockStack[this.realBlockPtr = 1] = 0;
    }

    /**
 * Returns this parser's problem reporter initialized with its reference context.
 * Also it is assumed that a problem is going to be reported, so initializes
 * the compilation result's line positions.
 *
 * @return ProblemReporter
 */
    public ProblemReporter problemReporter() {
        if (this.scanner.recordLineSeparator) {
            this.compilationUnit.compilationResult.lineSeparatorPositions = this.scanner.getLineEnds();
        }
        this.problemReporter.referenceContext = this.referenceContext;
        return this.problemReporter;
    }

    protected void pushIdentifier(char[] identifier, long position) {
        int stackLength = this.identifierStack.length;
        if (++this.identifierPtr >= stackLength) {
            System.arraycopy(this.identifierStack, 0, this.identifierStack = new char[stackLength + 20][], 0, stackLength);
            System.arraycopy(this.identifierPositionStack, 0, this.identifierPositionStack = new long[stackLength + 20], 0, stackLength);
        }
        this.identifierStack[this.identifierPtr] = identifier;
        this.identifierPositionStack[this.identifierPtr] = position;
        stackLength = this.identifierLengthStack.length;
        if (++this.identifierLengthPtr >= stackLength) {
            System.arraycopy(this.identifierLengthStack, 0, this.identifierLengthStack = new int[stackLength + 10], 0, stackLength);
        }
        this.identifierLengthStack[this.identifierLengthPtr] = 1;
        if (this.parsingJava8Plus && identifier.length == 1 && identifier[0] == '_' && !this.processingLambdaParameterList) {
            problemReporter().illegalUseOfUnderscoreAsAnIdentifier((int) (position >>> 32), (int) position, /* not a lambda parameter */
            false);
        }
    }

    protected void pushIdentifier() {
        /*push the consumeToken on the identifier stack.
	Increase the total number of identifier in the stack.
	identifierPtr points on the next top */
        pushIdentifier(this.scanner.getCurrentIdentifierSource(), (((long) this.scanner.startPosition) << 32) + (this.scanner.currentPosition - 1));
    }

    protected void pushIdentifier(int flag) {
        /*push a special flag on the stack :
	-zero stands for optional Name
	-negative number for direct ref to base types.
	identifierLengthPtr points on the top */
        int stackLength = this.identifierLengthStack.length;
        if (++this.identifierLengthPtr >= stackLength) {
            System.arraycopy(this.identifierLengthStack, 0, this.identifierLengthStack = new int[stackLength + 10], 0, stackLength);
        }
        this.identifierLengthStack[this.identifierLengthPtr] = flag;
    }

    protected void pushOnAstLengthStack(int pos) {
        int stackLength = this.astLengthStack.length;
        if (++this.astLengthPtr >= stackLength) {
            System.arraycopy(this.astLengthStack, 0, this.astLengthStack = new int[stackLength + StackIncrement], 0, stackLength);
        }
        this.astLengthStack[this.astLengthPtr] = pos;
    }

    protected void pushOnAstStack(ASTNode node) {
        /*add a new obj on top of the ast stack
	astPtr points on the top*/
        int stackLength = this.astStack.length;
        if (++this.astPtr >= stackLength) {
            System.arraycopy(this.astStack, 0, this.astStack = new ASTNode[stackLength + AstStackIncrement], 0, stackLength);
            this.astPtr = stackLength;
        }
        this.astStack[this.astPtr] = node;
        stackLength = this.astLengthStack.length;
        if (++this.astLengthPtr >= stackLength) {
            System.arraycopy(this.astLengthStack, 0, this.astLengthStack = new int[stackLength + AstStackIncrement], 0, stackLength);
        }
        this.astLengthStack[this.astLengthPtr] = 1;
    }

    protected void pushOnTypeAnnotationStack(Annotation annotation) {
        int stackLength = this.typeAnnotationStack.length;
        if (++this.typeAnnotationPtr >= stackLength) {
            System.arraycopy(this.typeAnnotationStack, 0, this.typeAnnotationStack = new Annotation[stackLength + TypeAnnotationStackIncrement], 0, stackLength);
        }
        this.typeAnnotationStack[this.typeAnnotationPtr] = annotation;
        stackLength = this.typeAnnotationLengthStack.length;
        if (++this.typeAnnotationLengthPtr >= stackLength) {
            System.arraycopy(this.typeAnnotationLengthStack, 0, this.typeAnnotationLengthStack = new int[stackLength + TypeAnnotationStackIncrement], 0, stackLength);
        }
        this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr] = 1;
    }

    protected void pushOnTypeAnnotationLengthStack(int pos) {
        int stackLength = this.typeAnnotationLengthStack.length;
        if (++this.typeAnnotationLengthPtr >= stackLength) {
            System.arraycopy(this.typeAnnotationLengthStack, 0, this.typeAnnotationLengthStack = new int[stackLength + TypeAnnotationStackIncrement], 0, stackLength);
        }
        this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr] = pos;
    }

    protected void pushOnExpressionStack(Expression expr) {
        int stackLength = this.expressionStack.length;
        if (++this.expressionPtr >= stackLength) {
            System.arraycopy(this.expressionStack, 0, this.expressionStack = new Expression[stackLength + ExpressionStackIncrement], 0, stackLength);
        }
        this.expressionStack[this.expressionPtr] = expr;
        stackLength = this.expressionLengthStack.length;
        if (++this.expressionLengthPtr >= stackLength) {
            System.arraycopy(this.expressionLengthStack, 0, this.expressionLengthStack = new int[stackLength + ExpressionStackIncrement], 0, stackLength);
        }
        this.expressionLengthStack[this.expressionLengthPtr] = 1;
    }

    protected void pushOnExpressionStackLengthStack(int pos) {
        int stackLength = this.expressionLengthStack.length;
        if (++this.expressionLengthPtr >= stackLength) {
            System.arraycopy(this.expressionLengthStack, 0, this.expressionLengthStack = new int[stackLength + StackIncrement], 0, stackLength);
        }
        this.expressionLengthStack[this.expressionLengthPtr] = pos;
    }

    protected void pushOnGenericsIdentifiersLengthStack(int pos) {
        int stackLength = this.genericsIdentifiersLengthStack.length;
        if (++this.genericsIdentifiersLengthPtr >= stackLength) {
            System.arraycopy(this.genericsIdentifiersLengthStack, 0, this.genericsIdentifiersLengthStack = new int[stackLength + GenericsStackIncrement], 0, stackLength);
        }
        this.genericsIdentifiersLengthStack[this.genericsIdentifiersLengthPtr] = pos;
    }

    protected void pushOnGenericsLengthStack(int pos) {
        int stackLength = this.genericsLengthStack.length;
        if (++this.genericsLengthPtr >= stackLength) {
            System.arraycopy(this.genericsLengthStack, 0, this.genericsLengthStack = new int[stackLength + GenericsStackIncrement], 0, stackLength);
        }
        this.genericsLengthStack[this.genericsLengthPtr] = pos;
    }

    protected void pushOnGenericsStack(ASTNode node) {
        /*add a new obj on top of the generics stack
	genericsPtr points on the top*/
        int stackLength = this.genericsStack.length;
        if (++this.genericsPtr >= stackLength) {
            System.arraycopy(this.genericsStack, 0, this.genericsStack = new ASTNode[stackLength + GenericsStackIncrement], 0, stackLength);
        }
        this.genericsStack[this.genericsPtr] = node;
        stackLength = this.genericsLengthStack.length;
        if (++this.genericsLengthPtr >= stackLength) {
            System.arraycopy(this.genericsLengthStack, 0, this.genericsLengthStack = new int[stackLength + GenericsStackIncrement], 0, stackLength);
        }
        this.genericsLengthStack[this.genericsLengthPtr] = 1;
    }

    protected void pushOnIntStack(int pos) {
        int stackLength = this.intStack.length;
        if (++this.intPtr >= stackLength) {
            System.arraycopy(this.intStack, 0, this.intStack = new int[stackLength + StackIncrement], 0, stackLength);
        }
        this.intStack[this.intPtr] = pos;
    }

    protected void pushOnRealBlockStack(int i) {
        int stackLength = this.realBlockStack.length;
        if (++this.realBlockPtr >= stackLength) {
            System.arraycopy(this.realBlockStack, 0, this.realBlockStack = new int[stackLength + StackIncrement], 0, stackLength);
        }
        this.realBlockStack[this.realBlockPtr] = i;
    }

    protected void recoverStatements() {
        class MethodVisitor extends ASTVisitor {

            public ASTVisitor typeVisitor;

            // used only for initializer
            TypeDeclaration enclosingType;

            TypeDeclaration[] types = new TypeDeclaration[0];

            int typePtr = -1;

            public void endVisit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
                endVisitMethod(constructorDeclaration, scope);
            }

            public void endVisit(Initializer initializer, MethodScope scope) {
                if (initializer.block == null)
                    return;
                TypeDeclaration[] foundTypes = null;
                int length = 0;
                if (this.typePtr > -1) {
                    length = this.typePtr + 1;
                    foundTypes = new TypeDeclaration[length];
                    System.arraycopy(this.types, 0, foundTypes, 0, length);
                }
                ReferenceContext oldContext = Parser.this.referenceContext;
                Parser.this.recoveryScanner.resetTo(initializer.bodyStart, initializer.bodyEnd);
                Scanner oldScanner = Parser.this.scanner;
                Parser.this.scanner = Parser.this.recoveryScanner;
                parseStatements(this.enclosingType, initializer.bodyStart, initializer.bodyEnd, foundTypes, Parser.this.compilationUnit);
                Parser.this.scanner = oldScanner;
                Parser.this.referenceContext = oldContext;
                for (int i = 0; i < length; i++) {
                    foundTypes[i].traverse(this.typeVisitor, scope);
                }
            }

            public void endVisit(MethodDeclaration methodDeclaration, ClassScope scope) {
                endVisitMethod(methodDeclaration, scope);
            }

            private void endVisitMethod(AbstractMethodDeclaration methodDeclaration, ClassScope scope) {
                TypeDeclaration[] foundTypes = null;
                int length = 0;
                if (this.typePtr > -1) {
                    length = this.typePtr + 1;
                    foundTypes = new TypeDeclaration[length];
                    System.arraycopy(this.types, 0, foundTypes, 0, length);
                }
                ReferenceContext oldContext = Parser.this.referenceContext;
                Parser.this.recoveryScanner.resetTo(methodDeclaration.bodyStart, methodDeclaration.bodyEnd);
                Scanner oldScanner = Parser.this.scanner;
                Parser.this.scanner = Parser.this.recoveryScanner;
                parseStatements(methodDeclaration, methodDeclaration.bodyStart, methodDeclaration.bodyEnd, foundTypes, Parser.this.compilationUnit);
                Parser.this.scanner = oldScanner;
                Parser.this.referenceContext = oldContext;
                for (int i = 0; i < length; i++) {
                    foundTypes[i].traverse(this.typeVisitor, scope);
                }
            }

            public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
                this.typePtr = -1;
                return true;
            }

            public boolean visit(Initializer initializer, MethodScope scope) {
                this.typePtr = -1;
                if (initializer.block == null)
                    return false;
                return true;
            }

            public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
                this.typePtr = -1;
                return true;
            }

            private boolean visit(TypeDeclaration typeDeclaration) {
                if (this.types.length <= ++this.typePtr) {
                    int length = this.typePtr;
                    System.arraycopy(this.types, 0, this.types = new TypeDeclaration[length * 2 + 1], 0, length);
                }
                this.types[this.typePtr] = typeDeclaration;
                return false;
            }

            public boolean visit(TypeDeclaration typeDeclaration, BlockScope scope) {
                return this.visit(typeDeclaration);
            }

            public boolean visit(TypeDeclaration typeDeclaration, ClassScope scope) {
                return this.visit(typeDeclaration);
            }
        }
        class TypeVisitor extends ASTVisitor {

            public MethodVisitor methodVisitor;

            TypeDeclaration[] types = new TypeDeclaration[0];

            int typePtr = -1;

            public void endVisit(TypeDeclaration typeDeclaration, BlockScope scope) {
                endVisitType();
            }

            public void endVisit(TypeDeclaration typeDeclaration, ClassScope scope) {
                endVisitType();
            }

            private void endVisitType() {
                this.typePtr--;
            }

            public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
                if (constructorDeclaration.isDefaultConstructor())
                    return false;
                constructorDeclaration.traverse(this.methodVisitor, scope);
                return false;
            }

            public boolean visit(Initializer initializer, MethodScope scope) {
                if (initializer.block == null)
                    return false;
                this.methodVisitor.enclosingType = this.types[this.typePtr];
                initializer.traverse(this.methodVisitor, scope);
                return false;
            }

            public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
                methodDeclaration.traverse(this.methodVisitor, scope);
                return false;
            }

            private boolean visit(TypeDeclaration typeDeclaration) {
                if (this.types.length <= ++this.typePtr) {
                    int length = this.typePtr;
                    System.arraycopy(this.types, 0, this.types = new TypeDeclaration[length * 2 + 1], 0, length);
                }
                this.types[this.typePtr] = typeDeclaration;
                return true;
            }

            public boolean visit(TypeDeclaration typeDeclaration, BlockScope scope) {
                return this.visit(typeDeclaration);
            }

            public boolean visit(TypeDeclaration typeDeclaration, ClassScope scope) {
                return this.visit(typeDeclaration);
            }
        }
        MethodVisitor methodVisitor = new MethodVisitor();
        TypeVisitor typeVisitor = new TypeVisitor();
        methodVisitor.typeVisitor = typeVisitor;
        typeVisitor.methodVisitor = methodVisitor;
        if (this.referenceContext instanceof AbstractMethodDeclaration) {
            ((AbstractMethodDeclaration) this.referenceContext).traverse(methodVisitor, (ClassScope) null);
        } else if (this.referenceContext instanceof TypeDeclaration) {
            TypeDeclaration typeContext = (TypeDeclaration) this.referenceContext;
            int length = typeContext.fields.length;
            for (int i = 0; i < length; i++) {
                final FieldDeclaration fieldDeclaration = typeContext.fields[i];
                switch(fieldDeclaration.getKind()) {
                    case AbstractVariableDeclaration.INITIALIZER:
                        Initializer initializer = (Initializer) fieldDeclaration;
                        if (initializer.block == null)
                            break;
                        methodVisitor.enclosingType = typeContext;
                        initializer.traverse(methodVisitor, (MethodScope) null);
                        break;
                }
            }
        }
    }

    public void recoveryExitFromVariable() {
        if (this.currentElement != null && this.currentElement.parent != null) {
            if (this.currentElement instanceof RecoveredLocalVariable) {
                int end = ((RecoveredLocalVariable) this.currentElement).localDeclaration.sourceEnd;
                this.currentElement.updateSourceEndIfNecessary(end);
                this.currentElement = this.currentElement.parent;
            } else if (this.currentElement instanceof RecoveredField && !(this.currentElement instanceof RecoveredInitializer)) {
                // https://bugs.eclipse.org/bugs/show_bug.cgi?id=292087 
                if (this.currentElement.bracketBalance <= 0) {
                    int end = ((RecoveredField) this.currentElement).fieldDeclaration.sourceEnd;
                    this.currentElement.updateSourceEndIfNecessary(end);
                    this.currentElement = this.currentElement.parent;
                }
            }
        }
    }

    /* Token check performed on every token shift once having entered
 * recovery mode.
 */
    public void recoveryTokenCheck() {
        switch(this.currentToken) {
            case TokenNameStringLiteral:
                if (this.recordStringLiterals && this.checkExternalizeStrings && this.lastPosistion < this.scanner.currentPosition && !this.statementRecoveryActivated) {
                    StringLiteral stringLiteral = createStringLiteral(this.scanner.getCurrentTokenSourceString(), this.scanner.startPosition, this.scanner.currentPosition - 1, Util.getLineNumber(this.scanner.startPosition, this.scanner.lineEnds, 0, this.scanner.linePtr));
                    this.compilationUnit.recordStringLiteral(stringLiteral, this.currentElement != null);
                }
                break;
            case TokenNameLBRACE:
                RecoveredElement newElement = null;
                if (!this.ignoreNextOpeningBrace) {
                    newElement = this.currentElement.updateOnOpeningBrace(this.scanner.startPosition - 1, this.scanner.currentPosition - 1);
                }
                this.lastCheckPoint = this.scanner.currentPosition;
                if (// null means nothing happened
                newElement != null) {
                    // opening brace detected
                    this.restartRecovery = true;
                    this.currentElement = newElement;
                }
                break;
            case TokenNameRBRACE:
                if (this.ignoreNextClosingBrace) {
                    this.ignoreNextClosingBrace = false;
                    break;
                }
                this.rBraceStart = this.scanner.startPosition - 1;
                this.rBraceEnd = this.scanner.currentPosition - 1;
                this.endPosition = flushCommentsDefinedPriorTo(this.rBraceEnd);
                newElement = this.currentElement.updateOnClosingBrace(this.scanner.startPosition, this.rBraceEnd);
                this.lastCheckPoint = this.scanner.currentPosition;
                if (newElement != this.currentElement) {
                    this.currentElement = newElement;
                //				if (newElement instanceof RecoveredField && this.dietInt <= 0) {
                //					if (((RecoveredField)newElement).fieldDeclaration.type == null) { // enum constant
                //						this.isInsideEnumConstantPart = true; // restore status
                //					}
                //				}
                }
                break;
            case TokenNameSEMICOLON:
                this.endStatementPosition = this.scanner.currentPosition - 1;
                this.endPosition = this.scanner.startPosition - 1;
                RecoveredType currentType = currentRecoveryType();
                if (currentType != null) {
                    currentType.insideEnumConstantPart = false;
                }
            //$FALL-THROUGH$
            default:
                {
                    if (this.rBraceEnd > this.rBraceSuccessorStart && this.scanner.currentPosition != this.scanner.startPosition) {
                        this.rBraceSuccessorStart = this.scanner.startPosition;
                    }
                    break;
                }
        }
        this.ignoreNextOpeningBrace = false;
    }

    // A P I
    protected void reportSyntaxErrors(boolean isDietParse, int oldFirstToken) {
        if (this.referenceContext instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) this.referenceContext;
            if ((methodDeclaration.bits & ASTNode.ErrorInSignature) != 0) {
                return;
            }
        }
        this.compilationUnit.compilationResult.lineSeparatorPositions = this.scanner.getLineEnds();
        this.scanner.recordLineSeparator = false;
        int start = this.scanner.initialPosition;
        int end = this.scanner.eofPosition == Integer.MAX_VALUE ? this.scanner.eofPosition : this.scanner.eofPosition - 1;
        if (isDietParse) {
            TypeDeclaration[] types = this.compilationUnit.types;
            int[][] intervalToSkip = org.eclipse.jdt.internal.compiler.parser.diagnose.RangeUtil.computeDietRange(types);
            DiagnoseParser diagnoseParser = new DiagnoseParser(this, oldFirstToken, start, end, intervalToSkip[0], intervalToSkip[1], intervalToSkip[2], this.options);
            diagnoseParser.diagnoseParse(false);
            reportSyntaxErrorsForSkippedMethod(types);
            this.scanner.resetTo(start, end);
        } else {
            DiagnoseParser diagnoseParser = new DiagnoseParser(this, oldFirstToken, start, end, this.options);
            diagnoseParser.diagnoseParse(this.options.performStatementsRecovery);
        }
    }

    private void reportSyntaxErrorsForSkippedMethod(TypeDeclaration[] types) {
        if (types != null) {
            for (int i = 0; i < types.length; i++) {
                TypeDeclaration[] memberTypes = types[i].memberTypes;
                if (memberTypes != null) {
                    reportSyntaxErrorsForSkippedMethod(memberTypes);
                }
                AbstractMethodDeclaration[] methods = types[i].methods;
                if (methods != null) {
                    for (int j = 0; j < methods.length; j++) {
                        AbstractMethodDeclaration method = methods[j];
                        if ((method.bits & ASTNode.ErrorInSignature) != 0) {
                            if (method.isAnnotationMethod()) {
                                DiagnoseParser diagnoseParser = new DiagnoseParser(this, TokenNameQUESTION, method.declarationSourceStart, method.declarationSourceEnd, this.options);
                                diagnoseParser.diagnoseParse(this.options.performStatementsRecovery);
                            } else {
                                DiagnoseParser diagnoseParser = new DiagnoseParser(this, TokenNameDIVIDE, method.declarationSourceStart, method.declarationSourceEnd, this.options);
                                diagnoseParser.diagnoseParse(this.options.performStatementsRecovery);
                            }
                        }
                    }
                }
                FieldDeclaration[] fields = types[i].fields;
                if (fields != null) {
                    int length = fields.length;
                    for (int j = 0; j < length; j++) {
                        if (fields[j] instanceof Initializer) {
                            Initializer initializer = (Initializer) fields[j];
                            if ((initializer.bits & ASTNode.ErrorInSignature) != 0) {
                                DiagnoseParser diagnoseParser = new DiagnoseParser(this, TokenNameRIGHT_SHIFT, initializer.declarationSourceStart, initializer.declarationSourceEnd, this.options);
                                diagnoseParser.diagnoseParse(this.options.performStatementsRecovery);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
 * Reset modifiers buffer and comment stack. Should be call only for nodes that claim both.
 */
    protected void resetModifiers() {
        this.modifiers = ClassFileConstants.AccDefault;
        // <-- see comment into modifiersFlag(int)
        this.modifiersSourceStart = -1;
        this.scanner.commentPtr = -1;
    }

    /*
 * Reset context so as to resume to regular parse loop
 */
    protected void resetStacks() {
        this.astPtr = -1;
        this.astLengthPtr = -1;
        this.expressionPtr = -1;
        this.expressionLengthPtr = -1;
        this.typeAnnotationLengthPtr = -1;
        this.typeAnnotationPtr = -1;
        this.identifierPtr = -1;
        this.identifierLengthPtr = -1;
        this.intPtr = -1;
        // need to reset for further reuse
        this.nestedMethod[this.nestedType = 0] = 0;
        this.variablesCounter[this.nestedType] = 0;
        this.dimensions = 0;
        this.realBlockStack[this.realBlockPtr = 0] = 0;
        this.recoveredStaticInitializerStart = 0;
        this.listLength = 0;
        this.listTypeParameterLength = 0;
        this.genericsIdentifiersLengthPtr = -1;
        this.genericsLengthPtr = -1;
        this.genericsPtr = -1;
        this.valueLambdaNestDepth = -1;
    }

    /*
 * Reset context so as to resume to regular parse loop
 * If unable to reset for resuming, answers false.
 *
 * Move checkpoint location, reset internal stacks and
 * decide which grammar goal is activated.
 */
    protected int resumeAfterRecovery() {
        if (!this.methodRecoveryActivated && !this.statementRecoveryActivated) {
            // reset internal stacks
            resetStacks();
            resetModifiers();
            /* attempt to move checkpoint location */
            if (!moveRecoveryCheckpoint()) {
                return HALT;
            }
            // only look for headers
            if (this.referenceContext instanceof CompilationUnitDeclaration) {
                goForHeaders();
                // passed this point, will not consider method bodies
                this.diet = true;
                this.dietInt = 0;
                return RESTART;
            }
            // does not know how to restart
            return HALT;
        } else if (!this.statementRecoveryActivated) {
            // reset internal stacks
            resetStacks();
            resetModifiers();
            /* attempt to move checkpoint location */
            if (!moveRecoveryCheckpoint()) {
                return HALT;
            }
            // only look for headers
            goForHeaders();
            return RESTART;
        } else {
            return HALT;
        }
    }

    protected int resumeOnSyntaxError() {
        if (this.haltOnSyntaxError)
            return HALT;
        /* request recovery initialization */
        if (this.currentElement == null) {
            // Reset javadoc before restart parsing after recovery
            this.javadoc = null;
            // do not investigate deeper in statement recovery
            if (this.statementRecoveryActivated)
                return HALT;
            // build some recovered elements
            this.currentElement = buildInitialRecoveryState();
        }
        /* do not investigate deeper in recovery when no recovered element */
        if (this.currentElement == null)
            return HALT;
        /* manual forced recovery restart - after headers */
        if (this.restartRecovery) {
            this.restartRecovery = false;
        }
        /* update recovery state with current error state of the parser */
        updateRecoveryState();
        if (getFirstToken() == TokenNameAND) {
            if (this.referenceContext instanceof CompilationUnitDeclaration) {
                TypeDeclaration typeDeclaration = new TypeDeclaration(this.referenceContext.compilationResult());
                typeDeclaration.name = Util.EMPTY_STRING.toCharArray();
                this.currentElement = this.currentElement.add(typeDeclaration, 0);
            }
        }
        if (this.lastPosistion < this.scanner.currentPosition) {
            this.lastPosistion = this.scanner.currentPosition;
            this.scanner.lastPosition = this.scanner.currentPosition;
        }
        /* attempt to reset state in order to resume to parse loop */
        return resumeAfterRecovery();
    }

    public void setMethodsFullRecovery(boolean enabled) {
        this.options.performMethodsFullRecovery = enabled;
    }

    public void setStatementsRecovery(boolean enabled) {
        if (enabled)
            this.options.performMethodsFullRecovery = true;
        this.options.performStatementsRecovery = enabled;
    }

    public String toString() {
        //$NON-NLS-1$ //$NON-NLS-2$
        String s = "lastCheckpoint : int = " + String.valueOf(this.lastCheckPoint) + "\n";
        //$NON-NLS-1$ //$NON-NLS-2$
        s = s + "identifierStack : char[" + (this.identifierPtr + 1) + "][] = {";
        for (int i = 0; i <= this.identifierPtr; i++) {
            //$NON-NLS-1$ //$NON-NLS-2$
            s = s + "\"" + String.valueOf(this.identifierStack[i]) + "\",";
        }
        //$NON-NLS-1$
        s = s + "}\n";
        //$NON-NLS-1$ //$NON-NLS-2$
        s = s + "identifierLengthStack : int[" + (this.identifierLengthPtr + 1) + "] = {";
        for (int i = 0; i <= this.identifierLengthPtr; i++) {
            //$NON-NLS-1$
            s = s + this.identifierLengthStack[i] + ",";
        }
        //$NON-NLS-1$
        s = s + "}\n";
        //$NON-NLS-1$ //$NON-NLS-2$
        s = s + "astLengthStack : int[" + (this.astLengthPtr + 1) + "] = {";
        for (int i = 0; i <= this.astLengthPtr; i++) {
            //$NON-NLS-1$
            s = s + this.astLengthStack[i] + ",";
        }
        //$NON-NLS-1$
        s = s + "}\n";
        //$NON-NLS-1$ //$NON-NLS-2$
        s = s + "astPtr : int = " + String.valueOf(this.astPtr) + "\n";
        //$NON-NLS-1$ //$NON-NLS-2$
        s = s + "intStack : int[" + (this.intPtr + 1) + "] = {";
        for (int i = 0; i <= this.intPtr; i++) {
            //$NON-NLS-1$
            s = s + this.intStack[i] + ",";
        }
        //$NON-NLS-1$
        s = s + "}\n";
        //$NON-NLS-1$ //$NON-NLS-2$
        s = s + "expressionLengthStack : int[" + (this.expressionLengthPtr + 1) + "] = {";
        for (int i = 0; i <= this.expressionLengthPtr; i++) {
            //$NON-NLS-1$
            s = s + this.expressionLengthStack[i] + ",";
        }
        //$NON-NLS-1$
        s = s + "}\n";
        //$NON-NLS-1$ //$NON-NLS-2$
        s = s + "expressionPtr : int = " + String.valueOf(this.expressionPtr) + "\n";
        //$NON-NLS-1$ //$NON-NLS-2$
        s = s + "genericsIdentifiersLengthStack : int[" + (this.genericsIdentifiersLengthPtr + 1) + "] = {";
        for (int i = 0; i <= this.genericsIdentifiersLengthPtr; i++) {
            //$NON-NLS-1$
            s = s + this.genericsIdentifiersLengthStack[i] + ",";
        }
        //$NON-NLS-1$
        s = s + "}\n";
        //$NON-NLS-1$ //$NON-NLS-2$
        s = s + "genericsLengthStack : int[" + (this.genericsLengthPtr + 1) + "] = {";
        for (int i = 0; i <= this.genericsLengthPtr; i++) {
            //$NON-NLS-1$
            s = s + this.genericsLengthStack[i] + ",";
        }
        //$NON-NLS-1$
        s = s + "}\n";
        //$NON-NLS-1$ //$NON-NLS-2$
        s = s + "genericsPtr : int = " + String.valueOf(this.genericsPtr) + "\n";
        //$NON-NLS-1$
        s = s + "\n\n\n----------------Scanner--------------\n" + this.scanner.toString();
        return s;
    }

    /*
 * Update recovery state based on current parser/scanner state
 */
    protected void updateRecoveryState() {
        /* expose parser state to recovery state */
        this.currentElement.updateFromParserState();
        /* check and update recovered state based on current token,
		this action is also performed when shifting token after recovery
		got activated once.
	*/
        recoveryTokenCheck();
    }

    protected void updateSourceDeclarationParts(int variableDeclaratorsCounter) {
        //fields is a definition of fields that are grouped together like in
        //public int[] a, b[], c
        //which results into 3 fields.
        FieldDeclaration field;
        int endTypeDeclarationPosition = -1 + this.astStack[this.astPtr - variableDeclaratorsCounter + 1].sourceStart;
        for (int i = 0; i < variableDeclaratorsCounter - 1; i++) {
            //last one is special(see below)
            field = (FieldDeclaration) this.astStack[this.astPtr - i - 1];
            field.endPart1Position = endTypeDeclarationPosition;
            field.endPart2Position = -1 + this.astStack[this.astPtr - i].sourceStart;
        }
        //last one
        (field = (FieldDeclaration) this.astStack[this.astPtr]).endPart1Position = endTypeDeclarationPosition;
        field.endPart2Position = field.declarationSourceEnd;
    }

    protected void updateSourcePosition(Expression exp) {
        //update the source Position of the expression
        //this.intStack : int int
        //-->
        //this.intStack :
        exp.sourceEnd = this.intStack[this.intPtr--];
        exp.sourceStart = this.intStack[this.intPtr--];
    }

    public void copyState(Parser from) {
        Parser parser = from;
        // Stack pointers.
        this.stateStackTop = parser.stateStackTop;
        this.unstackedAct = parser.unstackedAct;
        this.identifierPtr = parser.identifierPtr;
        this.identifierLengthPtr = parser.identifierLengthPtr;
        this.astPtr = parser.astPtr;
        this.astLengthPtr = parser.astLengthPtr;
        this.expressionPtr = parser.expressionPtr;
        this.expressionLengthPtr = parser.expressionLengthPtr;
        this.genericsPtr = parser.genericsPtr;
        this.genericsLengthPtr = parser.genericsLengthPtr;
        this.genericsIdentifiersLengthPtr = parser.genericsIdentifiersLengthPtr;
        this.typeAnnotationPtr = parser.typeAnnotationPtr;
        this.typeAnnotationLengthPtr = parser.typeAnnotationLengthPtr;
        this.intPtr = parser.intPtr;
        this.nestedType = parser.nestedType;
        this.realBlockPtr = parser.realBlockPtr;
        this.valueLambdaNestDepth = parser.valueLambdaNestDepth;
        // Stacks.
        int length;
        System.arraycopy(parser.stack, 0, this.stack = new int[length = parser.stack.length], 0, length);
        System.arraycopy(parser.identifierStack, 0, this.identifierStack = new char[length = parser.identifierStack.length][], 0, length);
        System.arraycopy(parser.identifierLengthStack, 0, this.identifierLengthStack = new int[length = parser.identifierLengthStack.length], 0, length);
        System.arraycopy(parser.identifierPositionStack, 0, this.identifierPositionStack = new long[length = parser.identifierPositionStack.length], 0, length);
        System.arraycopy(parser.astStack, 0, this.astStack = new ASTNode[length = parser.astStack.length], 0, length);
        System.arraycopy(parser.astLengthStack, 0, this.astLengthStack = new int[length = parser.astLengthStack.length], 0, length);
        System.arraycopy(parser.expressionStack, 0, this.expressionStack = new Expression[length = parser.expressionStack.length], 0, length);
        System.arraycopy(parser.expressionLengthStack, 0, this.expressionLengthStack = new int[length = parser.expressionLengthStack.length], 0, length);
        System.arraycopy(parser.genericsStack, 0, this.genericsStack = new ASTNode[length = parser.genericsStack.length], 0, length);
        System.arraycopy(parser.genericsLengthStack, 0, this.genericsLengthStack = new int[length = parser.genericsLengthStack.length], 0, length);
        System.arraycopy(parser.genericsIdentifiersLengthStack, 0, this.genericsIdentifiersLengthStack = new int[length = parser.genericsIdentifiersLengthStack.length], 0, length);
        System.arraycopy(parser.typeAnnotationStack, 0, this.typeAnnotationStack = new Annotation[length = parser.typeAnnotationStack.length], 0, length);
        System.arraycopy(parser.typeAnnotationLengthStack, 0, this.typeAnnotationLengthStack = new int[length = parser.typeAnnotationLengthStack.length], 0, length);
        System.arraycopy(parser.intStack, 0, this.intStack = new int[length = parser.intStack.length], 0, length);
        System.arraycopy(parser.nestedMethod, 0, this.nestedMethod = new int[length = parser.nestedMethod.length], 0, length);
        System.arraycopy(parser.realBlockStack, 0, this.realBlockStack = new int[length = parser.realBlockStack.length], 0, length);
        System.arraycopy(parser.stateStackLengthStack, 0, this.stateStackLengthStack = new int[length = parser.stateStackLengthStack.length], 0, length);
        System.arraycopy(parser.variablesCounter, 0, this.variablesCounter = new int[length = parser.variablesCounter.length], 0, length);
        System.arraycopy(parser.stack, 0, this.stack = new int[length = parser.stack.length], 0, length);
        System.arraycopy(parser.stack, 0, this.stack = new int[length = parser.stack.length], 0, length);
        System.arraycopy(parser.stack, 0, this.stack = new int[length = parser.stack.length], 0, length);
        // Loose variables.
        this.listLength = parser.listLength;
        this.listTypeParameterLength = parser.listTypeParameterLength;
        this.dimensions = parser.dimensions;
        this.recoveredStaticInitializerStart = parser.recoveredStaticInitializerStart;
    // Parser.resetStacks is not clearing the modifiers, but AssistParser.resumeAfterRecovery is - why ? (the former doesn't)
    // this.modifiers = parser.modifiers;
    // this.modifiersSourceStart = parser.modifiersSourceStart;
    }

    public int automatonState() {
        return this.stack[this.stateStackTop];
    }

    public boolean automatonWillShift(int token, int lastAction) {
        // local copy of stack pointer
        int stackTop = this.stateStackTop;
        // single cell non write through "alternate stack" - the automaton's stack pointer either stays fixed during this manoeuvre or monotonically decreases.
        int stackTopState = this.stack[stackTop];
        int highWaterMark = stackTop;
        // A rotated version of the automaton - cf. parse()'s for(;;)
        if (// in recovery mode, we could take a detour to here, with a pending reduce action.
        lastAction <= NUM_RULES) {
            stackTop--;
            lastAction += ERROR_ACTION;
        }
        for (; ; ) {
            if (lastAction > ERROR_ACTION) {
                lastAction -= ERROR_ACTION;
                /* reduce */
                do {
                    stackTop -= rhs[lastAction] - 1;
                    if (stackTop < highWaterMark) {
                        stackTopState = this.stack[highWaterMark = stackTop];
                    }
                    // else stackTopState is upto date already.
                    lastAction = ntAction(stackTopState, lhs[lastAction]);
                } while (lastAction <= NUM_RULES);
            }
            highWaterMark = ++stackTop;
            // "push"
            stackTopState = lastAction;
            // can be looked up from a precomputed cache.
            lastAction = tAction(lastAction, token);
            if (lastAction <= NUM_RULES) {
                stackTop--;
                lastAction += ERROR_ACTION;
                continue;
            }
            // Error => false, Shift, Shift/Reduce => true, Accept => impossible. 
            return lastAction != ERROR_ACTION;
        }
    }
}
