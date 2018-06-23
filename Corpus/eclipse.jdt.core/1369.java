/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Tom Tromey - patch for readTable(String) as described in http://bugs.eclipse.org/bugs/show_bug.cgi?id=32196
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.parser;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.BindingIds;
import org.eclipse.jdt.internal.compiler.lookup.CompilerModifiers;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.parser.diagnose.DiagnoseParser;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
import org.eclipse.jdt.internal.compiler.util.Util;

public class Parser implements BindingIds, ParserBasicInformation, TerminalTokens, CompilerModifiers, OperatorIds, TypeIds {

    protected static final int THIS_CALL = ExplicitConstructorCall.This;

    protected static final int SUPER_CALL = ExplicitConstructorCall.Super;

    public static char asb[] = null;

    public static char asr[] = null;

    //ast stack
    protected static final int AstStackIncrement = 100;

    public static char base_action[] = null;

    public static final int BracketKinds = 3;

    public static short check_table[] = null;

    public static final int CurlyBracket = 2;

    // TODO remove once testing is done
    private static final boolean DEBUG = false;

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

    private static final String READABLE_NAMES_FILE_NAME = //$NON-NLS-1$
    "org.eclipse.jdt.internal.compiler.parser." + READABLE_NAMES_FILE;

    public static String readableName[] = null;

    public static byte rhs[] = null;

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

    protected int astLengthPtr;

    protected int[] astLengthStack;

    protected int astPtr;

    protected ASTNode[] astStack = new ASTNode[AstStackIncrement];

    public CompilationUnitDeclaration compilationUnit;

    /*the result from parse()*/
    protected RecoveredElement currentElement;

    public int currentToken;

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

    // handle for multiple parsing goals
    public int firstToken;

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

    //the ptr is nestedType
    protected int[] nestedMethod;

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

    //scanner token 
    public Scanner scanner;

    protected int[] stack = new int[StackIncrement];

    protected int stateStackTop;

    protected int synchronizedBlockSourceStart;

    protected int[] variablesCounter;

    public Javadoc javadoc;

    public JavadocParser javadocParser;

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

    private static final void buildFile(String filename, List listToDump) throws java.io.IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (Iterator iterator = listToDump.iterator(); iterator.hasNext(); ) {
            writer.write(String.valueOf(iterator.next()));
        }
        writer.flush();
        writer.close();
        //$NON-NLS-1$
        System.out.println(filename + " creation complete");
    }

    private static final String[] buildFileForName(String filename, String contents) throws java.io.IOException {
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

    private static void buildFileForReadableName(String file, char[] newLhs, char[] newNonTerminalIndex, String[] newName, String[] tokens) throws java.io.IOException {
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
                System.out.println(newName[i] + " has no readable name");
            }
        }
        Collections.sort(entries);
        buildFile(file, entries);
    }

    private static void buildFileForCompliance(String file, int length, String[] tokens) throws java.io.IOException {
        byte[] result = new byte[length * 8];
        for (int i = 0; i < tokens.length; i = i + 3) {
            if (//$NON-NLS-1$
            "2".equals(tokens[i])) {
                int index = Integer.parseInt(tokens[i + 1]);
                String token = tokens[i + 2].trim();
                long compliance = 0;
                if (//$NON-NLS-1$
                "1.4".equals(token)) {
                    compliance = ClassFileConstants.JDK1_4;
                } else if (//$NON-NLS-1$
                "1.5".equals(token)) {
                    compliance = ClassFileConstants.JDK1_5;
                } else if (//$NON-NLS-1$
                "recovery".equals(token)) {
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
        //$NON-NLS-1$
        buildFileForTable(file, result);
    }

    private static final void buildFileForTable(String filename, byte[] bytes) throws java.io.IOException {
        java.io.FileOutputStream stream = new java.io.FileOutputStream(filename);
        stream.write(bytes);
        stream.close();
        //$NON-NLS-1$
        System.out.println(filename + " creation complete");
    }

    private static final void buildFileForTable(String filename, char[] chars) throws java.io.IOException {
        byte[] bytes = new byte[chars.length * 2];
        for (int i = 0; i < chars.length; i++) {
            bytes[2 * i] = (byte) (chars[i] >>> 8);
            bytes[2 * i + 1] = (byte) (chars[i] & 0xFF);
        }
        java.io.FileOutputStream stream = new java.io.FileOutputStream(filename);
        stream.write(bytes);
        stream.close();
        //$NON-NLS-1$
        System.out.println(filename + " creation complete");
    }

    private static final byte[] buildFileOfByteFor(String filename, String tag, String[] tokens) throws java.io.IOException {
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

    private static final char[] buildFileOfIntFor(String filename, String tag, String[] tokens) throws java.io.IOException {
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

    private static final void buildFileOfShortFor(String filename, String tag, String[] tokens) throws java.io.IOException {
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

    public static final void buildFilesFromLPG(String dataFilename, String dataFilename2) throws java.io.IOException {
        //RUN THIS METHOD TO GENERATE PARSER*.RSC FILES
        //build from the lpg javadcl.java files that represents the parser tables
        //lhs check_table asb asr symbol_index
        //[org.eclipse.jdt.internal.compiler.parser.Parser.buildFilesFromLPG("d:/leapfrog/grammar/javadcl.java")]
        char[] contents = new char[] {};
        try {
            contents = Util.getFileCharContent(new File(dataFilename), null);
        } catch (IOException ex) {
            System.out.println(Util.bind("parser.incorrectPath"));
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
        buildFileOfIntFor(prefix + (++i) + ".rsc", "terminal_index", tokens);
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
        contents = new char[] {};
        try {
            contents = Util.getFileCharContent(new File(dataFilename2), null);
        } catch (IOException ex) {
            System.out.println(Util.bind("parser.incorrectPath"));
            return;
        }
        //$NON-NLS-1$
        st = new java.util.StringTokenizer(new String(contents), "\t\n\r=#");
        tokens = new String[st.countTokens()];
        j = 0;
        while (st.hasMoreTokens()) {
            tokens[j++] = st.nextToken();
        }
        //$NON-NLS-1$
        buildFileForCompliance(prefix + (++i) + ".rsc", newRhs.length, tokens);
        //$NON-NLS-1$
        buildFileForReadableName(READABLE_NAMES_FILE + ".properties", newLhs, newNonTerminalIndex, newName, tokens);
        //$NON-NLS-1$
        System.out.println(Util.bind("parser.moveFiles"));
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
        readableName = readReadableNameTable(READABLE_NAMES_FILE_NAME);
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
            //$NON-NLS-1$
            throw new java.io.IOException(Util.bind("parser.missingFile", filename));
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
        ResourceBundle bundle;
        try {
            bundle = ResourceBundle.getBundle(filename, Locale.getDefault());
        } catch (MissingResourceException e) {
            System.out.println("Missing resource : " + filename.replace('.', '/') + ".properties for locale " + Locale.getDefault());
            throw e;
        }
        for (int i = 0; i < NT_OFFSET + 1; i++) {
            result[i] = name[i];
        }
        for (int i = NT_OFFSET; i < name.length; i++) {
            try {
                String n = bundle.getString(name[i]);
                if (n != null && n.length() > 0) {
                    result[i] = n;
                } else {
                    result[i] = name[i];
                }
            } catch (MissingResourceException e) {
                result[i] = name[i];
            }
        }
        return result;
    }

    protected static char[] readTable(String filename) throws java.io.IOException {
        //files are located at Parser.class directory
        InputStream stream = Parser.class.getResourceAsStream(filename);
        if (stream == null) {
            //$NON-NLS-1$
            throw new java.io.IOException(Util.bind("parser.missingFile", filename));
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
        if (length % 2 != 0)
            //$NON-NLS-1$
            throw new java.io.IOException(Util.bind("parser.corruptedFile", filename));
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

    protected static long[] readLongTable(String filename) throws java.io.IOException {
        //files are located at Parser.class directory
        InputStream stream = Parser.class.getResourceAsStream(filename);
        if (stream == null) {
            //$NON-NLS-1$
            throw new java.io.IOException(Util.bind("parser.missingFile", filename));
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
            //$NON-NLS-1$
            throw new java.io.IOException(Util.bind("parser.corruptedFile", filename));
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

    public static int tAction(int state, int sym) {
        return term_action[term_check[base_action[state] + sym] == sym ? base_action[state] + sym : base_action[state]];
    }

    public  Parser(ProblemReporter problemReporter, boolean optimizeStringLiterals) {
        this.problemReporter = problemReporter;
        this.options = problemReporter.options;
        this.optimizeStringLiterals = optimizeStringLiterals;
        this.initializeScanner();
        this.astLengthStack = new int[50];
        this.expressionLengthStack = new int[30];
        this.intStack = new int[50];
        this.identifierStack = new char[30][];
        this.identifierLengthStack = new int[30];
        this.nestedMethod = new int[30];
        this.realBlockStack = new int[30];
        this.identifierPositionStack = new long[30];
        this.variablesCounter = new int[30];
        // javadoc support
        this.javadocParser = new JavadocParser(this);
    }

    /**
 *
 * INTERNAL USE-ONLY
 */
    protected void adjustInterfaceModifiers() {
        this.intStack[this.intPtr - 1] |= AccInterface;
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
            if (// experimenting restart recovery from scratch
            true) {
                this.compilationUnit.currentPackage = null;
                this.compilationUnit.imports = null;
                this.compilationUnit.types = null;
                this.currentToken = 0;
                this.listLength = 0;
                this.listTypeParameterLength = 0;
                this.endPosition = 0;
                this.endStatementPosition = 0;
                return element;
            }
            if (this.compilationUnit.currentPackage != null) {
                this.lastCheckPoint = this.compilationUnit.currentPackage.declarationSourceEnd + 1;
            }
            if (this.compilationUnit.imports != null) {
                this.lastCheckPoint = this.compilationUnit.imports[this.compilationUnit.imports.length - 1].declarationSourceEnd + 1;
            }
        } else {
            if (this.referenceContext instanceof AbstractMethodDeclaration) {
                element = new RecoveredMethod((AbstractMethodDeclaration) this.referenceContext, null, 0, this);
                this.lastCheckPoint = ((AbstractMethodDeclaration) this.referenceContext).bodyStart;
            } else {
                /* Initializer bodies are parsed in the context of the type declaration, we must thus search it inside */
                if (this.referenceContext instanceof TypeDeclaration) {
                    TypeDeclaration type = (TypeDeclaration) this.referenceContext;
                    for (int i = 0; i < type.fields.length; i++) {
                        FieldDeclaration field = type.fields[i];
                        if (field != null && !field.isField() && field.declarationSourceStart <= this.scanner.initialPosition && this.scanner.initialPosition <= field.declarationSourceEnd && this.scanner.eofPosition <= field.declarationSourceEnd + 1) {
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
        }
        return element;
    }

    protected void checkAndSetModifiers(int flag) {
        if (// duplicate modifier
        (this.modifiers & flag) != 0) {
            this.modifiers |= AccAlternateModifierProblem;
        }
        this.modifiers |= flag;
        if (this.modifiersSourceStart < 0)
            this.modifiersSourceStart = this.scanner.startPosition;
    }

    public void checkComment() {
        if (this.currentElement != null && this.scanner.commentPtr >= 0) {
            // discard obsolete comments during recovery
            flushCommentsDefinedPriorTo(this.endStatementPosition);
        }
        int lastComment = this.scanner.commentPtr;
        if (this.modifiersSourceStart >= 0) {
            // eliminate comments located after modifierSourceStart if positionned
            while (lastComment >= 0 && this.scanner.commentStarts[lastComment] > this.modifiersSourceStart) lastComment--;
        }
        if (lastComment >= 0) {
            // consider all remaining leading comments to be part of current declaration
            this.modifiersSourceStart = this.scanner.commentStarts[0];
            // non javadoc comment have negative end positions
            while (lastComment >= 0 && this.scanner.commentStops[lastComment] < 0) lastComment--;
            if (lastComment >= 0 && this.javadocParser != null) {
                if (this.javadocParser.checkDeprecation(this.scanner.commentStarts[lastComment], //stop is one over,
                this.scanner.commentStops[lastComment] - 1)) {
                    checkAndSetModifiers(AccDeprecated);
                }
                // null if check javadoc is not activated 
                this.javadoc = this.javadocParser.docComment;
            }
        }
    }

    protected void checkNonExternalizedStringLiteral() {
        if (this.scanner.wasNonExternalizedStringLiteral) {
            StringLiteral[] literals = this.scanner.nonNLSStrings;
            // added preventive null check see PR 9035
            if (literals != null) {
                for (int i = 0, max = literals.length; i < max; i++) {
                    problemReporter().nonExternalizedStringLiteral(literals[i]);
                }
            }
            this.scanner.wasNonExternalizedStringLiteral = false;
        }
    }

    protected void checkNonNLSAfterBodyEnd(int declarationEnd) {
        if (this.scanner.currentPosition - 1 <= declarationEnd) {
            this.scanner.eofPosition = declarationEnd < Integer.MAX_VALUE ? declarationEnd + 1 : declarationEnd;
            try {
                while (this.scanner.getNextToken() != TokenNameEOF/*empty*/
                ) {
                }
                checkNonExternalizedStringLiteral();
            } catch (InvalidInputException e) {
            }
        }
    }

    protected void classInstanceCreation(boolean alwaysQualified) {
        // ClassInstanceCreationExpression ::= 'new' ClassType '(' ArgumentListopt ')' ClassBodyopt
        // ClassBodyopt produces a null item on the astStak if it produces NO class body
        // An empty class body produces a 0 on the length stack.....
        AllocationExpression alloc;
        int length;
        if (((length = this.astLengthStack[this.astLengthPtr--]) == 1) && (this.astStack[this.astPtr] == null)) {
            //NO ClassBody
            this.astPtr--;
            if (alwaysQualified) {
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
            }
            if (length == 0 && !containsComment(anonymousTypeDeclaration.bodyStart, anonymousTypeDeclaration.bodyEnd)) {
                anonymousTypeDeclaration.bits |= ASTNode.UndocumentedEmptyBlockMASK;
            }
            this.astPtr--;
            this.astLengthPtr--;
            // mark initializers with local type mark if needed
            markInitializersWithLocalType(anonymousTypeDeclaration);
        }
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

    protected void consumeAllocationHeader() {
        if (this.currentElement == null) {
            // should never occur, this consumeRule is only used in recovery mode
            return;
        }
        if (this.currentToken == TokenNameLBRACE) {
            // beginning of an anonymous type
            TypeDeclaration anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
            anonymousType.name = TypeDeclaration.ANONYMOUS_EMPTY_NAME;
            anonymousType.bits |= ASTNode.AnonymousAndLocalMask;
            anonymousType.sourceStart = this.intStack[this.intPtr--];
            // closing parenthesis
            anonymousType.sourceEnd = this.rParenPos;
            QualifiedAllocationExpression alloc = new QualifiedAllocationExpression(anonymousType);
            alloc.type = getTypeReference(0);
            alloc.sourceStart = anonymousType.sourceStart;
            alloc.sourceEnd = anonymousType.sourceEnd;
            anonymousType.allocation = alloc;
            this.lastCheckPoint = anonymousType.bodyStart = this.scanner.currentPosition;
            this.currentElement = this.currentElement.add(anonymousType, 0);
            this.lastIgnoredToken = -1;
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
    // nothing to do
    }

    protected void consumeAnnotationTypeDeclaration() {
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            //there are length declarations
            //dispatch according to the type of the declarations
            dispatchDeclarationInto(length);
        }
        TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
        // mark initializers with local type mark if needed
        markInitializersWithLocalType(typeDecl);
        //convert constructor that do not have the type's name into methods
        typeDecl.checkConstructors(this);
        //always add <clinit> (will be remove at code gen time if empty)
        if (this.scanner.containsAssertKeyword) {
            typeDecl.bits |= ASTNode.AddAssertionMASK;
        }
        typeDecl.addClinit();
        typeDecl.bodyEnd = this.endStatementPosition;
        if (length == 0 && !containsComment(typeDecl.bodyStart, typeDecl.bodyEnd)) {
            typeDecl.bits |= ASTNode.UndocumentedEmptyBlockMASK;
        }
        typeDecl.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeAnnotationTypeDeclarationHeader() {
        // consumeAnnotationTypeDeclarationHeader ::= Modifiers '@' PushModifiers interface Identifier
        // consumeAnnotationTypeDeclarationHeader ::= '@' PushModifiers interface Identifier
        AnnotationTypeDeclaration annotationTypeDeclaration = new AnnotationTypeDeclaration(this.compilationUnit.compilationResult);
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                annotationTypeDeclaration.bits |= ASTNode.IsMemberTypeMASK;
            }
        } else {
            // Record that the block has a declaration for local types
            annotationTypeDeclaration.bits |= ASTNode.IsLocalTypeMASK;
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
        annotationTypeDeclaration.modifiers = this.intStack[this.intPtr--];
        if (annotationTypeDeclaration.modifiersSourceStart >= 0) {
            annotationTypeDeclaration.declarationSourceStart = annotationTypeDeclaration.modifiersSourceStart;
            // remove the position of the '@' token as we have modifiers
            this.intPtr--;
        } else {
            int atPosition = this.intStack[this.intPtr--];
            // remove the position of the '@' token as we don't have modifiers
            annotationTypeDeclaration.declarationSourceStart = atPosition;
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
        if (options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfAnnotationDeclarations(annotationTypeDeclaration);
        }
    }

    protected void consumeAnnotationTypeMemberDeclarationHeader() {
        // AnnotationTypeMemberDeclarationHeader ::= Modifiersopt Type Identifier '(' ')'
        AnnotationTypeMemberDeclaration annotationTypeMemberDeclaration = new AnnotationTypeMemberDeclaration(this.compilationUnit.compilationResult);
        //name
        annotationTypeMemberDeclaration.selector = this.identifierStack[this.identifierPtr];
        long selectorSource = this.identifierPositionStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        //type
        annotationTypeMemberDeclaration.returnType = getTypeReference(this.intStack[this.intPtr--]);
        //modifiers
        annotationTypeMemberDeclaration.declarationSourceStart = this.intStack[this.intPtr--];
        annotationTypeMemberDeclaration.modifiers = this.intStack[this.intPtr--];
        // javadoc
        annotationTypeMemberDeclaration.javadoc = this.javadoc;
        this.javadoc = null;
        // consume annotations
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, annotationTypeMemberDeclaration.annotations = new Annotation[length], 0, length);
        }
        //highlight starts at selector start
        annotationTypeMemberDeclaration.sourceStart = (int) (selectorSource >>> 32);
        annotationTypeMemberDeclaration.sourceEnd = (int) selectorSource;
        annotationTypeMemberDeclaration.bodyStart = this.rParenPos + 1;
        pushOnAstStack(annotationTypeMemberDeclaration);
    }

    protected void consumeAnnotationTypeMemberHeaderExtendedDims() {
        // AnnotationTypeMemberHeaderExtendedDims ::= Dimsopt
        AnnotationTypeMemberDeclaration annotationTypeMemberDeclaration = (AnnotationTypeMemberDeclaration) this.astStack[this.astPtr];
        int extendedDims = this.intStack[this.intPtr--];
        annotationTypeMemberDeclaration.extendedDimensions = extendedDims;
        if (extendedDims != 0) {
            TypeReference returnType = annotationTypeMemberDeclaration.returnType;
            annotationTypeMemberDeclaration.sourceEnd = this.endPosition;
            int dims = returnType.dimensions() + extendedDims;
            int baseType;
            if ((baseType = this.identifierLengthStack[this.identifierLengthPtr + 1]) < 0) {
                //it was a baseType
                int sourceStart = returnType.sourceStart;
                int sourceEnd = returnType.sourceEnd;
                returnType = TypeReference.baseTypeReference(-baseType, dims);
                returnType.sourceStart = sourceStart;
                returnType.sourceEnd = sourceEnd;
                annotationTypeMemberDeclaration.returnType = returnType;
            } else {
                annotationTypeMemberDeclaration.returnType = this.copyDims(annotationTypeMemberDeclaration.returnType, dims);
            }
            if (this.currentToken == TokenNameSEMICOLON) {
                annotationTypeMemberDeclaration.bodyStart = this.endPosition + 1;
            }
        }
    }

    protected void consumeAnnotationTypeMemberDeclaration() {
        // AnnotationTypeMemberDeclaration ::= AnnotationTypeMemberDeclarationHeader AnnotationTypeMemberHeaderExtendedDims DefaultValueopt ';'
        AnnotationTypeMemberDeclaration annotationTypeMemberDeclaration = (AnnotationTypeMemberDeclaration) this.astStack[this.astPtr];
        int length = this.expressionLengthStack[this.expressionLengthPtr--];
        if (length == 1) {
            // we get rid of the position of the default keyword
            intPtr--;
            // we get rid of the position of the default keyword
            intPtr--;
            annotationTypeMemberDeclaration.memberValue = this.expressionStack[this.expressionPtr--];
        }
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

    protected void consumeArgumentList() {
        // ArgumentList ::= ArgumentList ',' Expression
        concatExpressionLists();
    }

    protected void consumeArguments() {
    // Arguments ::= '(' ArgumentListopt ')' 
    // nothing to do, the expression stack is already updated
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
        exp.sourceEnd = this.endPosition;
    }

    protected void consumeArrayCreationExpressionWithInitializer() {
        // ArrayCreationWithArrayInitializer ::= 'new' PrimitiveType DimWithOrWithOutExprs ArrayInitializer
        // ArrayCreationWithArrayInitializer ::= 'new' ClassOrInterfaceType DimWithOrWithOutExprs ArrayInitializer
        int length;
        ArrayAllocationExpression aae = new ArrayAllocationExpression();
        this.expressionLengthPtr--;
        aae.initializer = (ArrayInitializer) this.expressionStack[this.expressionPtr--];
        aae.type = getTypeReference(0);
        length = (this.expressionLengthStack[this.expressionLengthPtr--]);
        this.expressionPtr -= length;
        System.arraycopy(this.expressionStack, this.expressionPtr + 1, aae.dimensions = new Expression[length], 0, length);
        aae.sourceStart = this.intStack[this.intPtr--];
        if (aae.initializer == null) {
            aae.sourceEnd = this.endPosition;
        } else {
            aae.sourceEnd = aae.initializer.sourceEnd;
        }
        pushOnExpressionStack(aae);
    }

    protected void consumeArrayCreationExpressionWithoutInitializer() {
        // ArrayCreationWithoutArrayInitializer ::= 'new' ClassOrInterfaceType DimWithOrWithOutExprs
        // ArrayCreationWithoutArrayInitializer ::= 'new' PrimitiveType DimWithOrWithOutExprs
        int length;
        ArrayAllocationExpression aae = new ArrayAllocationExpression();
        aae.type = getTypeReference(0);
        length = (this.expressionLengthStack[this.expressionLengthPtr--]);
        this.expressionPtr -= length;
        System.arraycopy(this.expressionStack, this.expressionPtr + 1, aae.dimensions = new Expression[length], 0, length);
        aae.sourceStart = this.intStack[this.intPtr--];
        if (aae.initializer == null) {
            aae.sourceEnd = this.endPosition;
        } else {
            aae.sourceEnd = aae.initializer.sourceEnd;
        }
        pushOnExpressionStack(aae);
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
        this.intStack[this.intPtr] += this.identifierLengthStack[this.identifierLengthPtr];
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
        this.expressionStack[this.expressionPtr] = (op != EQUAL) ? new CompoundAssignment(this.expressionStack[this.expressionPtr], this.expressionStack[this.expressionPtr + 1], op, this.scanner.startPosition - 1) : new Assignment(this.expressionStack[this.expressionPtr], this.expressionStack[this.expressionPtr + 1], this.scanner.startPosition - 1);
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
                    if (expr2 instanceof StringLiteral) {
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
                // TODO (olivier) bug 67790 remove once DOMParser is activated
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
                    if (expr2 instanceof StringLiteral) {
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
                // TODO (olivier) bug 67790 remove once DOMParser is activated
                this.intPtr--;
                this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
                break;
            default:
                this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
        }
    }

    protected void consumeBlock() {
        // Block ::= OpenBlock '{' BlockStatementsopt '}'
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
                block.bits |= ASTNode.UndocumentedEmptyBlockMASK;
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

    protected void consumeBlockStatements() {
        // BlockStatements ::= BlockStatements BlockStatement
        concatNodeLists();
    }

    protected void consumeCaseLabel() {
        // SwitchLabel ::= 'case' ConstantExpression ':'
        this.expressionLengthPtr--;
        Expression expression = this.expressionStack[this.expressionPtr--];
        pushOnAstStack(new CaseStatement(expression, expression.sourceEnd, this.intStack[this.intPtr--]));
    }

    protected void consumeCastExpressionLL1() {
        //CastExpression ::= '(' Expression ')' InsideCastExpressionLL1 UnaryExpressionNotPlusMinus
        // Expression is used in order to make the grammar LL1
        //optimize push/pop
        Expression cast, exp;
        this.expressionPtr--;
        this.expressionStack[this.expressionPtr] = cast = new CastExpression(exp = this.expressionStack[this.expressionPtr + 1], getTypeReference(this.expressionStack[this.expressionPtr]));
        this.expressionLengthPtr--;
        updateSourcePosition(cast);
        cast.sourceEnd = exp.sourceEnd;
    }

    protected void consumeCastExpressionWithGenericsArray() {
        // CastExpression ::= PushLPAREN Name TypeArguments Dims PushRPAREN InsideCastExpression UnaryExpressionNotPlusMinus
        Expression exp, cast, castType;
        int end = this.intStack[this.intPtr--];
        int dim = this.intStack[this.intPtr--];
        pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        this.expressionStack[this.expressionPtr] = cast = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType = getTypeReference(dim));
        // TODO (olivier) bug 67790 remove once DOMParser is activated
        intPtr--;
        castType.sourceEnd = end - 1;
        castType.sourceStart = (cast.sourceStart = this.intStack[this.intPtr--]) + 1;
        cast.sourceEnd = exp.sourceEnd;
    }

    protected void consumeCastExpressionWithNameArray() {
        // CastExpression ::= PushLPAREN Name Dims PushRPAREN InsideCastExpression UnaryExpressionNotPlusMinus
        Expression exp, cast, castType;
        int end = this.intStack[this.intPtr--];
        // handle type arguments
        pushOnGenericsLengthStack(0);
        pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        this.expressionStack[this.expressionPtr] = cast = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType = getTypeReference(this.intStack[this.intPtr--]));
        castType.sourceEnd = end - 1;
        castType.sourceStart = (cast.sourceStart = this.intStack[this.intPtr--]) + 1;
        cast.sourceEnd = exp.sourceEnd;
    }

    protected void consumeCastExpressionWithPrimitiveType() {
        // CastExpression ::= PushLPAREN PrimitiveType Dimsopt PushRPAREN InsideCastExpression UnaryExpression
        //this.intStack : posOfLeftParen dim posOfRightParen
        //optimize the push/pop
        Expression exp, cast, castType;
        int end = this.intStack[this.intPtr--];
        this.expressionStack[this.expressionPtr] = cast = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType = getTypeReference(this.intStack[this.intPtr--]));
        castType.sourceEnd = end - 1;
        castType.sourceStart = (cast.sourceStart = this.intStack[this.intPtr--]) + 1;
        cast.sourceEnd = exp.sourceEnd;
    }

    protected ParameterizedQualifiedTypeReference computeQualifiedGenericsFromRightSide(TypeReference rightSide, int dim) {
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
        if (rightSide instanceof ParameterizedSingleTypeReference) {
            ParameterizedSingleTypeReference singleParameterizedTypeReference = (ParameterizedSingleTypeReference) rightSide;
            tokens[nameSize] = singleParameterizedTypeReference.token;
            positions[nameSize] = (((long) singleParameterizedTypeReference.sourceStart) << 32) + singleParameterizedTypeReference.sourceEnd;
            typeArguments[nameSize] = singleParameterizedTypeReference.typeArguments;
        } else if (rightSide instanceof SingleTypeReference) {
            SingleTypeReference singleTypeReference = (SingleTypeReference) rightSide;
            tokens[nameSize] = singleTypeReference.token;
            positions[nameSize] = (((long) singleTypeReference.sourceStart) << 32) + singleTypeReference.sourceEnd;
        } else if (rightSide instanceof ParameterizedQualifiedTypeReference) {
            ParameterizedQualifiedTypeReference parameterizedTypeReference = (ParameterizedQualifiedTypeReference) rightSide;
            TypeReference[][] rightSideTypeArguments = parameterizedTypeReference.typeArguments;
            System.arraycopy(rightSideTypeArguments, 0, typeArguments, nameSize, rightSideTypeArguments.length);
            char[][] rightSideTokens = parameterizedTypeReference.tokens;
            System.arraycopy(rightSideTokens, 0, tokens, nameSize, rightSideTokens.length);
            long[] rightSidePositions = parameterizedTypeReference.sourcePositions;
            System.arraycopy(rightSidePositions, 0, positions, nameSize, rightSidePositions.length);
        } else if (rightSide instanceof QualifiedTypeReference) {
            QualifiedTypeReference qualifiedTypeReference = (QualifiedTypeReference) rightSide;
            char[][] rightSideTokens = qualifiedTypeReference.tokens;
            System.arraycopy(rightSideTokens, 0, tokens, nameSize, rightSideTokens.length);
            long[] rightSidePositions = qualifiedTypeReference.sourcePositions;
            System.arraycopy(rightSidePositions, 0, positions, nameSize, rightSidePositions.length);
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
        return new ParameterizedQualifiedTypeReference(tokens, typeArguments, dim, positions);
    }

    protected void consumeCastExpressionWithQualifiedGenericsArray() {
        // CastExpression ::= PushLPAREN Name OnlyTypeArguments '.' ClassOrInterfaceType Dims PushRPAREN InsideCastExpression UnaryExpressionNotPlusMinus
        Expression exp, cast, castType;
        int end = this.intStack[this.intPtr--];
        int dim = this.intStack[this.intPtr--];
        TypeReference rightSide = getTypeReference(0);
        ParameterizedQualifiedTypeReference qualifiedParameterizedTypeReference = computeQualifiedGenericsFromRightSide(rightSide, dim);
        // TODO (olivier) bug 67790 remove once DOMParser is activated
        intPtr--;
        this.expressionStack[this.expressionPtr] = cast = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType = qualifiedParameterizedTypeReference);
        castType.sourceEnd = end - 1;
        castType.sourceStart = (cast.sourceStart = this.intStack[this.intPtr--]) + 1;
        cast.sourceEnd = exp.sourceEnd;
    }

    protected void consumeCatches() {
        // Catches ::= Catches CatchClause
        optimizedConcatNodeLists();
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

    protected void consumeClassBodyDeclaration() {
        // ClassBodyDeclaration ::= Diet Block
        //push an Initializer
        //optimize the push/pop
        this.nestedMethod[this.nestedType]--;
        Block block = (Block) this.astStack[this.astPtr];
        // clear bit since was diet
        if (this.diet)
            block.bits &= ~ASTNode.UndocumentedEmptyBlockMASK;
        Initializer initializer = new Initializer(block, 0);
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
        this.astStack[this.astPtr] = initializer;
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
        this.endPosition = this.scanner.startPosition - 1;
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
        // mark initializers with local type mark if needed
        markInitializersWithLocalType(typeDecl);
        //convert constructor that do not have the type's name into methods
        boolean hasConstructor = typeDecl.checkConstructors(this);
        //add the default constructor when needed (interface don't have it)
        if (!hasConstructor && !typeDecl.isInterface()) {
            boolean insideFieldInitializer = false;
            if (this.diet) {
                for (int i = this.nestedType; i > 0; i--) {
                    if (this.variablesCounter[i] > 0) {
                        insideFieldInitializer = true;
                        break;
                    }
                }
            }
            typeDecl.createsInternalConstructor(!this.diet || insideFieldInitializer, true);
        }
        //always add <clinit> (will be remove at code gen time if empty)
        if (this.scanner.containsAssertKeyword) {
            typeDecl.bits |= ASTNode.AddAssertionMASK;
        }
        typeDecl.addClinit();
        typeDecl.bodyEnd = this.endStatementPosition;
        if (length == 0 && !containsComment(typeDecl.bodyStart, typeDecl.bodyEnd)) {
            typeDecl.bits |= ASTNode.UndocumentedEmptyBlockMASK;
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
        for (int i = 0, max = typeDecl.superInterfaces.length; i < max; i++) {
            typeDecl.superInterfaces[i].bits |= ASTNode.IsSuperType;
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
                typeDecl.bits |= ASTNode.IsMemberTypeMASK;
            }
        } else {
            // Record that the block has a declaration for local types
            typeDecl.bits |= ASTNode.IsLocalTypeMASK;
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
        if (// is recovering
        this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
        }
    }

    protected void consumeClassInstanceCreationExpression() {
        // ClassInstanceCreationExpression ::= 'new' ClassType '(' ArgumentListopt ')' ClassBodyopt
        classInstanceCreation(false);
    }

    protected void consumeClassInstanceCreationExpressionName() {
        // ClassInstanceCreationExpressionName ::= Name '.'
        pushOnExpressionStack(getUnspecifiedReferenceOptimized());
    }

    protected void consumeClassInstanceCreationExpressionQualified() {
        // ClassInstanceCreationExpression ::= Primary '.' 'new' SimpleName '(' ArgumentListopt ')' ClassBodyopt
        // ClassInstanceCreationExpression ::= ClassInstanceCreationExpressionName 'new' SimpleName '(' ArgumentListopt ')' ClassBodyopt
        classInstanceCreation(true);
        this.expressionLengthPtr--;
        QualifiedAllocationExpression qae = (QualifiedAllocationExpression) this.expressionStack[this.expressionPtr--];
        qae.enclosingInstance = this.expressionStack[this.expressionPtr];
        this.expressionStack[this.expressionPtr] = qae;
        qae.sourceStart = qae.enclosingInstance.sourceStart;
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
            length = this.genericsLengthStack[this.genericsLengthPtr--];
            this.genericsPtr -= length;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, alloc.typeArguments = new TypeReference[length], 0, length);
            // TODO  (olivier) bug 67790 remove once DOMParser is activated
            intPtr--;
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
                anonymousTypeDeclaration.bits |= ASTNode.UndocumentedEmptyBlockMASK;
            }
            this.astPtr--;
            this.astLengthPtr--;
            QualifiedAllocationExpression anonymousTypeDeclarationAllocationExpression = anonymousTypeDeclaration.allocation;
            if (anonymousTypeDeclarationAllocationExpression != null) {
                anonymousTypeDeclarationAllocationExpression.sourceEnd = this.endStatementPosition;
                // handle type arguments
                length = this.genericsLengthStack[this.genericsLengthPtr--];
                this.genericsPtr -= length;
                System.arraycopy(this.genericsStack, this.genericsPtr + 1, anonymousTypeDeclarationAllocationExpression.typeArguments = new TypeReference[length], 0, length);
            }
            // mark initializers with local type mark if needed
            markInitializersWithLocalType(anonymousTypeDeclaration);
        }
        this.expressionLengthPtr--;
        QualifiedAllocationExpression qae = (QualifiedAllocationExpression) this.expressionStack[this.expressionPtr--];
        qae.enclosingInstance = this.expressionStack[this.expressionPtr];
        this.expressionStack[this.expressionPtr] = qae;
        qae.sourceStart = qae.enclosingInstance.sourceStart;
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
            length = this.genericsLengthStack[this.genericsLengthPtr--];
            this.genericsPtr -= length;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, alloc.typeArguments = new TypeReference[length], 0, length);
            // TODO (olivier) bug 67790 remove once DOMParser is activated
            intPtr--;
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
                anonymousTypeDeclaration.bits |= ASTNode.UndocumentedEmptyBlockMASK;
            }
            this.astPtr--;
            this.astLengthPtr--;
            QualifiedAllocationExpression anonymousTypeDeclarationAllocationExpression = anonymousTypeDeclaration.allocation;
            if (anonymousTypeDeclarationAllocationExpression != null) {
                anonymousTypeDeclarationAllocationExpression.sourceEnd = this.endStatementPosition;
                // handle type arguments
                length = this.genericsLengthStack[this.genericsLengthPtr--];
                this.genericsPtr -= length;
                System.arraycopy(this.genericsStack, this.genericsPtr + 1, anonymousTypeDeclarationAllocationExpression.typeArguments = new TypeReference[length], 0, length);
            }
            // mark initializers with local type mark if needed
            markInitializersWithLocalType(anonymousTypeDeclaration);
        }
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
            if (this.astStack[this.astPtr + 1] instanceof ExplicitConstructorCall) {
                //avoid a isSomeThing that would only be used here BUT what is faster between two alternatives ?
                System.arraycopy(this.astStack, this.astPtr + 2, statements = new Statement[length - 1], 0, length - 1);
                constructorCall = (ExplicitConstructorCall) this.astStack[this.astPtr + 1];
            } else //need to add explicitly the super();
            {
                System.arraycopy(this.astStack, this.astPtr + 1, statements = new Statement[length], 0, length);
                constructorCall = SuperReference.implicitSuperConstructorCall();
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
            if (!this.diet || insideFieldInitializer) {
                // add it only in non-diet mode, if diet_bodies, then constructor call will be added elsewhere.
                constructorCall = SuperReference.implicitSuperConstructorCall();
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
        if (!this.diet && (statements == null && constructorCall.isImplicitSuper())) {
            if (!containsComment(cd.bodyStart, this.endPosition)) {
                cd.bits |= ASTNode.UndocumentedEmptyBlockMASK;
            }
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
                method.modifiers |= AccSemicolonBody;
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

    protected void consumeDefaultLabel() {
        // SwitchLabel ::= 'default' ':'
        pushOnAstStack(new CaseStatement(null, this.intStack[this.intPtr--], this.intStack[this.intPtr--]));
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
        // DimWithOrWithOutExpr ::= '[' ']'
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

    protected void consumeEmptyClassMemberDeclaration() {
        // ClassMemberDeclaration ::= ';'
        pushOnAstLengthStack(0);
        problemReporter().superfluousSemicolon(this.endPosition + 1, this.endStatementPosition);
        flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeEmptyDefaultValue() {
        // DefaultValueopt ::= $empty
        pushOnExpressionStackLengthStack(0);
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
    }

    protected void consumeEmptyForUpdateopt() {
        // ForUpdateopt ::= $empty
        pushOnExpressionStackLengthStack(0);
    }

    protected void consumeEmptyInterfaceMemberDeclaration() {
        // InterfaceMemberDeclaration ::= ';'
        pushOnAstLengthStack(0);
    }

    protected void consumeEmptyInterfaceMemberDeclarationsopt() {
        // InterfaceMemberDeclarationsopt ::= $empty
        pushOnAstLengthStack(0);
    }

    protected void consumeEmptyInternalCompilationUnit() {
    // InternalCompilationUnit ::= $empty
    // nothing to do by default
    }

    protected void consumeEmptyMemberValuePairsopt() {
        // MemberValuePairsopt ::= $empty
        pushOnAstLengthStack(0);
    }

    protected void consumeEmptyMemberValueArrayInitializer() {
        // MemberValueArrayInitializer ::= '{' ',' '}'
        // MemberValueArrayInitializer ::= '{' '}'
        arrayInitializer(0);
    }

    protected void consumeEmptyStatement() {
        // EmptyStatement ::= ';'
        if (this.scanner.source[this.endStatementPosition] == ';') {
            pushOnAstStack(new EmptyStatement(this.endStatementPosition, this.endStatementPosition));
        } else {
            // we have a Unicode for the ';' (/u003B)
            pushOnAstStack(new EmptyStatement(this.endStatementPosition - 5, this.endStatementPosition));
        }
    }

    protected void consumeEmptySwitchBlock() {
        // SwitchBlock ::= '{' '}'
        pushOnAstLengthStack(0);
    }

    protected void consumeEmptyTypeDeclaration() {
        // TypeDeclaration ::= ';' 
        pushOnAstLengthStack(0);
        problemReporter().superfluousSemicolon(this.endPosition + 1, this.endStatementPosition);
        flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeEnhancedForStatementHeader(boolean hasModifiers) {
        // EnhancedForStatementHeader ::= 'for' '(' Type PushModifiers Identifier Dimsopt ':' Expression ')'
        // EnhancedForStatementHeader ::= 'for' '(' Modifiers Type PushRealModifiers Identifier Dimsopt ':' Expression ')'
        TypeReference type;
        char[] identifierName = this.identifierStack[this.identifierPtr];
        long namePosition = this.identifierPositionStack[this.identifierPtr];
        LocalDeclaration localDeclaration = createLocalDeclaration(identifierName, (int) (namePosition >>> 32), (int) namePosition);
        localDeclaration.declarationSourceEnd = localDeclaration.declarationEnd;
        int extraDims = this.intStack[this.intPtr--];
        this.identifierPtr--;
        this.identifierLengthPtr--;
        // remove fake modifiers/modifiers start
        int declarationSourceStart = 0;
        int modifiersValue = 0;
        if (hasModifiers) {
            declarationSourceStart = this.intStack[this.intPtr--];
            modifiersValue = this.intStack[this.intPtr--];
        } else {
            this.intPtr -= 2;
        }
        //updates are on the expression stack
        this.expressionLengthPtr--;
        Expression collection = this.expressionStack[this.expressionPtr--];
        // type dimension
        type = getTypeReference(this.intStack[this.intPtr--] + extraDims);
        // consume annotations
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, localDeclaration.annotations = new Annotation[length], 0, length);
        }
        if (hasModifiers) {
            localDeclaration.declarationSourceStart = declarationSourceStart;
            localDeclaration.modifiers = modifiersValue;
        } else {
            localDeclaration.declarationSourceStart = type.sourceStart;
        }
        localDeclaration.type = type;
        ForeachStatement iteratorForStatement = new ForeachStatement(localDeclaration, collection, this.intStack[this.intPtr--]);
        pushOnAstStack(iteratorForStatement);
        if (options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfForeachStatements(localDeclaration, collection);
        }
    }

    protected void consumeEnhancedForStatement() {
        // EnhancedForStatement ::= EnhancedForStatementHeader Statement
        // EnhancedForStatementNoShortIf ::= EnhancedForStatementHeader StatementNoShortIf
        //statements
        this.astLengthPtr--;
        Statement statement = (Statement) this.astStack[this.astPtr--];
        // foreach statement is on the ast stack
        ForeachStatement foreachStatement = (ForeachStatement) this.astStack[this.astPtr];
        foreachStatement.action = statement;
        // remember useful empty statement
        if (statement instanceof EmptyStatement)
            statement.bits |= ASTNode.IsUsefulEmptyStatementMASK;
        foreachStatement.sourceEnd = this.endStatementPosition;
    }

    protected void consumeEnterAnonymousClassBody() {
        // EnterAnonymousClassBody ::= $empty
        TypeReference typeReference = getTypeReference(0);
        QualifiedAllocationExpression alloc;
        TypeDeclaration anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
        anonymousType.name = TypeDeclaration.ANONYMOUS_EMPTY_NAME;
        anonymousType.bits |= ASTNode.AnonymousAndLocalMask;
        alloc = anonymousType.allocation = new QualifiedAllocationExpression(anonymousType);
        markEnclosingMemberWithLocalType();
        pushOnAstStack(anonymousType);
        //the position has been stored explicitly
        alloc.sourceEnd = this.rParenPos;
        int argumentLength;
        if ((argumentLength = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            this.expressionPtr -= argumentLength;
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, alloc.arguments = new Expression[argumentLength], 0, argumentLength);
        }
        alloc.type = typeReference;
        anonymousType.sourceEnd = alloc.sourceEnd;
        //position at the type while it impacts the anonymous declaration
        anonymousType.sourceStart = anonymousType.declarationSourceStart = alloc.type.sourceStart;
        alloc.sourceStart = this.intStack[this.intPtr--];
        pushOnExpressionStack(alloc);
        anonymousType.bodyStart = this.scanner.currentPosition;
        // will be updated when reading super-interfaces
        this.listLength = 0;
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = anonymousType.bodyStart;
            this.currentElement = this.currentElement.add(anonymousType, 0);
            // opening brace already taken into account
            this.currentToken = 0;
            this.lastIgnoredToken = -1;
        }
    }

    protected void consumeEnterAnonymousClassBodySimpleName() {
        // EnterAnonymousClassBody ::= $empty
        pushOnGenericsLengthStack(0);
        pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        TypeReference typeReference = getTypeReference(0);
        QualifiedAllocationExpression alloc;
        TypeDeclaration anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
        anonymousType.name = TypeDeclaration.ANONYMOUS_EMPTY_NAME;
        anonymousType.bits |= ASTNode.AnonymousAndLocalMask;
        alloc = anonymousType.allocation = new QualifiedAllocationExpression(anonymousType);
        markEnclosingMemberWithLocalType();
        pushOnAstStack(anonymousType);
        //the position has been stored explicitly
        alloc.sourceEnd = this.rParenPos;
        int argumentLength;
        if ((argumentLength = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            this.expressionPtr -= argumentLength;
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, alloc.arguments = new Expression[argumentLength], 0, argumentLength);
        }
        alloc.type = typeReference;
        anonymousType.sourceEnd = alloc.sourceEnd;
        //position at the type while it impacts the anonymous declaration
        anonymousType.sourceStart = anonymousType.declarationSourceStart = alloc.type.sourceStart;
        alloc.sourceStart = this.intStack[this.intPtr--];
        pushOnExpressionStack(alloc);
        anonymousType.bodyStart = this.scanner.currentPosition;
        // will be updated when reading super-interfaces
        this.listLength = 0;
        // recovery
        if (this.currentElement != null) {
            this.lastCheckPoint = anonymousType.bodyStart;
            this.currentElement = this.currentElement.add(anonymousType, 0);
            // opening brace already taken into account
            this.currentToken = 0;
            this.lastIgnoredToken = -1;
        }
    }

    protected void consumeEnterCompilationUnit() {
    // EnterCompilationUnit ::= $empty
    // do nothing by default
    }

    protected void consumeEnterVariable() {
        // EnterVariable ::= $empty
        // do nothing by default
        char[] identifierName = this.identifierStack[this.identifierPtr];
        long namePosition = this.identifierPositionStack[this.identifierPtr];
        int extendedDimension = this.intStack[this.intPtr--];
        AbstractVariableDeclaration declaration;
        // create the ast node
        boolean isLocalDeclaration = this.nestedMethod[this.nestedType] != 0;
        if (isLocalDeclaration) {
            // create the local variable declarations
            declaration = this.createLocalDeclaration(identifierName, (int) (namePosition >>> 32), (int) namePosition);
        } else {
            // create the field declaration
            declaration = this.createFieldDeclaration(identifierName, (int) (namePosition >>> 32), (int) namePosition);
        }
        this.identifierPtr--;
        this.identifierLengthPtr--;
        TypeReference type;
        int variableIndex = this.variablesCounter[this.nestedType];
        int typeDim = 0;
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
                type = getTypeReference(typeDim = this.intStack[this.intPtr--]);
                if (declaration.declarationSourceStart == -1) {
                    // this is true if there is no modifiers for the local variable declaration
                    declaration.declarationSourceStart = type.sourceStart;
                }
                pushOnAstStack(type);
            } else {
                // type dimension
                type = getTypeReference(typeDim = this.intStack[this.intPtr--]);
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
                this.javadoc = null;
            }
        } else {
            type = (TypeReference) this.astStack[this.astPtr - variableIndex];
            typeDim = type.dimensions();
            AbstractVariableDeclaration previousVariable = (AbstractVariableDeclaration) this.astStack[this.astPtr];
            declaration.declarationSourceStart = previousVariable.declarationSourceStart;
            declaration.modifiers = previousVariable.modifiers;
        }
        if (extendedDimension == 0) {
            declaration.type = type;
        } else {
            int dimension = typeDim + extendedDimension;
            //on the this.identifierLengthStack there is the information about the type....
            int baseType;
            if ((baseType = this.identifierLengthStack[this.identifierLengthPtr + 1]) < 0) {
                //it was a baseType
                int typeSourceStart = type.sourceStart;
                int typeSourceEnd = type.sourceEnd;
                type = TypeReference.baseTypeReference(-baseType, dimension);
                type.sourceStart = typeSourceStart;
                type.sourceEnd = typeSourceEnd;
                declaration.type = type;
            } else {
                declaration.type = this.copyDims(type, dimension);
            }
        }
        this.variablesCounter[this.nestedType]++;
        pushOnAstStack(declaration);
        // recovery
        if (this.currentElement != null) {
            if (!(this.currentElement instanceof RecoveredType) && (this.currentToken == TokenNameDOT || //|| declaration.modifiers != 0
            (this.scanner.getLineNumber(declaration.type.sourceStart) != this.scanner.getLineNumber((int) (namePosition >>> 32))))) {
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
        EnumConstant enumConstant = new EnumConstant(this.compilationUnit.compilationResult);
        long pos = this.identifierPositionStack[this.identifierPtr];
        int enumConstantEnd = (int) pos;
        enumConstant.sourceEnd = enumConstantEnd;
        final int start = (int) (pos >>> 32);
        enumConstant.sourceStart = start;
        enumConstant.declarationSourceStart = start;
        enumConstant.name = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        // fill arguments if needed
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            this.expressionPtr -= length;
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, enumConstant.arguments = new Expression[length], 0, length);
            enumConstant.declarationSourceEnd = flushCommentsDefinedPriorTo(rParenPos);
            enumConstant.bodyStart = rParenPos;
            enumConstant.bodyEnd = rParenPos;
        } else {
            if (enumConstantEnd <= rParenPos) {
                enumConstant.bodyEnd = rParenPos;
            } else {
                enumConstant.bodyEnd = enumConstantEnd;
            }
        }
        if (this.currentToken == TokenNameLBRACE) {
            enumConstant.bodyStart = this.scanner.currentPosition;
        }
        pushOnAstStack(enumConstant);
    }

    protected void consumeEnumConstantNoClassBody() {
        EnumConstant enumConstant = (EnumConstant) this.astStack[this.astPtr];
        // use to set that this enumConstant has no class body
        enumConstant.modifiers |= CompilerModifiers.AccSemicolonBody;
    }

    protected void consumeEnumConstants() {
        concatNodeLists();
    }

    protected void consumeEnumConstantWithClassBody() {
        dispatchDeclarationInto(this.astLengthStack[this.astLengthPtr--]);
        EnumConstant enumConstant = (EnumConstant) this.astStack[this.astPtr];
        enumConstant.modifiers = 0;
        enumConstant.bodyEnd = this.endPosition;
        enumConstant.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeEnumDeclaration() {
        // EnumDeclaration ::= EnumHeader ClassHeaderImplementsopt EnumBody
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            //there are length declarations
            //dispatch according to the type of the declarations
            dispatchDeclarationIntoEnumDeclaration(length);
        }
        EnumDeclaration enumDeclaration = (EnumDeclaration) this.astStack[this.astPtr];
        // mark initializers with local type mark if needed
        markInitializersWithLocalType(enumDeclaration);
        //convert constructor that do not have the type's name into methods
        boolean hasConstructor = enumDeclaration.checkConstructors(this);
        //add the default constructor when needed (interface don't have it)
        if (!hasConstructor && !enumDeclaration.isInterface()) {
            boolean insideFieldInitializer = false;
            if (this.diet) {
                for (int i = this.nestedType; i > 0; i--) {
                    if (this.variablesCounter[i] > 0) {
                        insideFieldInitializer = true;
                        break;
                    }
                }
            }
            enumDeclaration.createsInternalConstructor(!this.diet || insideFieldInitializer, true);
        }
        //always add <clinit> (will be remove at code gen time if empty)
        if (this.scanner.containsAssertKeyword) {
            enumDeclaration.bits |= ASTNode.AddAssertionMASK;
        }
        enumDeclaration.addClinit();
        enumDeclaration.bodyEnd = this.endStatementPosition;
        if (length == 0 && !containsComment(enumDeclaration.bodyStart, enumDeclaration.bodyEnd)) {
            enumDeclaration.bits |= ASTNode.UndocumentedEmptyBlockMASK;
        }
        enumDeclaration.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeEnumDeclarations() {
    // Do nothing by default
    }

    protected void consumeEnumHeader() {
        // EnumHeader ::= Modifiersopt 'enum' Identifier
        EnumDeclaration enumDeclaration;
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                enumDeclaration = new EnumDeclaration(this.compilationUnit.compilationResult);
            } else {
                enumDeclaration = new EnumDeclaration(this.compilationUnit.compilationResult);
            }
        } else {
            // Record that the block has a declaration for local types
            enumDeclaration = new EnumDeclaration(this.compilationUnit.compilationResult);
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
        enumDeclaration.modifiers = this.intStack[this.intPtr--];
        if (enumDeclaration.modifiersSourceStart >= 0) {
            enumDeclaration.declarationSourceStart = enumDeclaration.modifiersSourceStart;
        }
        // consume annotations
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, enumDeclaration.annotations = new Annotation[length], 0, length);
        }
        if (this.currentToken == TokenNameLBRACE) {
            enumDeclaration.bodyStart = this.scanner.currentPosition;
        }
        pushOnAstStack(enumDeclaration);
        // will be updated when reading super-interfaces
        this.listLength = 0;
        if (options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            //TODO this code will be never run while 'enum' is an identifier in 1.3 scanner 
            this.problemReporter().invalidUsageOfEnumDeclarations(enumDeclaration);
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
        this.recoveryExitFromVariable();
    }

    protected void consumeExitVariableWithoutInitialization() {
        // ExitVariableWithoutInitialization ::= $empty
        // do nothing by default
        AbstractVariableDeclaration variableDecl = (AbstractVariableDeclaration) this.astStack[this.astPtr];
        variableDecl.declarationSourceEnd = variableDecl.declarationEnd;
        this.recoveryExitFromVariable();
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
        ecc.sourceEnd = this.endPosition;
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
        // TODO (olivier) bug 67790 remove once DOMParser is activated
        ecc.typeArgumentsSourceStart = this.intStack[intPtr--];
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
        ecc.sourceEnd = this.endPosition;
    }

    protected void consumeExpressionStatement() {
        // ExpressionStatement ::= StatementExpression ';'
        this.expressionLengthPtr--;
        pushOnAstStack(this.expressionStack[this.expressionPtr--]);
    }

    protected void consumeFieldAccess(boolean isSuperAccess) {
        // FieldAccess ::= Primary '.' 'Identifier'
        // FieldAccess ::= 'super' '.' 'Identifier'
        FieldReference fr = new FieldReference(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--]);
        this.identifierLengthPtr--;
        if (isSuperAccess) {
            //considerates the fieldReference beginning at the 'super' ....	
            fr.sourceStart = this.intStack[this.intPtr--];
            fr.receiver = new SuperReference(fr.sourceStart, this.endPosition);
            pushOnExpressionStack(fr);
        } else {
            //optimize push/pop
            if ((fr.receiver = this.expressionStack[this.expressionPtr]).isThis()) {
                //fieldreference begins at the this
                fr.sourceStart = fr.receiver.sourceStart;
            }
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
    }

    protected void consumeFormalParameter(boolean isVarArgs) {
        // FormalParameter ::= Type VariableDeclaratorId ==> false
        // FormalParameter ::= Modifiers Type VariableDeclaratorId ==> true
        /*
	this.astStack : 
	this.identifierStack : type identifier
	this.intStack : dim dim
	 ==>
	this.astStack : Argument
	this.identifierStack :  
	this.intStack :  
	*/
        this.identifierLengthPtr--;
        char[] identifierName = this.identifierStack[this.identifierPtr];
        long namePositions = this.identifierPositionStack[this.identifierPtr--];
        TypeReference type = getTypeReference(this.intStack[this.intPtr--] + this.intStack[this.intPtr--]);
        int modifierPositions = this.intStack[this.intPtr--];
        this.intPtr--;
        Argument arg = new Argument(identifierName, namePositions, type, this.intStack[this.intPtr + 1] & ~AccDeprecated, // modifiers
        isVarArgs);
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
        if (isVarArgs && options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfVarargs(arg);
        }
    }

    protected void consumeFormalParameterList() {
        // FormalParameterList ::= FormalParameterList ',' FormalParameter
        optimizedConcatNodeLists();
    }

    protected void consumeFormalParameterListopt() {
        // FormalParameterListopt ::= $empty
        pushOnAstLengthStack(0);
    }

    protected void consumeGenericType() {
    // nothing to do
    // Will be consume by a getTypeRefence call
    }

    protected void consumeGenericTypeArrayType() {
    // nothing to do
    // Will be consume by a getTypeRefence call
    }

    protected void consumeGenericTypeNameArrayType() {
        // handle type arguments
        pushOnGenericsLengthStack(0);
    }

    protected void consumeImportDeclaration() {
        // SingleTypeImportDeclaration ::= SingleTypeImportDeclarationName ';'
        ImportReference impt = (ImportReference) this.astStack[this.astPtr];
        // flush annotations defined prior to import statements
        impt.declarationEnd = this.endStatementPosition;
        impt.declarationSourceEnd = this.flushCommentsDefinedPriorTo(impt.declarationSourceEnd);
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
        pushOnExpressionStack(getUnspecifiedReferenceOptimized());
    }

    protected void consumeInsideCastExpressionWithQualifiedGenerics() {
    // InsideCastExpressionWithQualifiedGenerics ::= $empty
    }

    protected void consumeInstanceOfExpression(int op) {
        // RelationalExpression ::= RelationalExpression 'instanceof' ReferenceType
        //optimize the push/pop
        //by construction, no base type may be used in getTypeReference
        Expression exp;
        this.expressionStack[this.expressionPtr] = exp = new InstanceOfExpression(this.expressionStack[this.expressionPtr], getTypeReference(this.intStack[this.intPtr--]), op);
        if (exp.sourceEnd == 0) {
            //array on base type....
            exp.sourceEnd = this.scanner.startPosition - 1;
        }
    //the scanner is on the next token already....
    }

    /**
 * @param op
 */
    protected void consumeInstanceOfExpressionWithName(int op) {
        // RelationalExpression_NotName ::= Name instanceof ReferenceType
        //optimize the push/pop
        //by construction, no base type may be used in getTypeReference
        TypeReference reference = getTypeReference(this.intStack[this.intPtr--]);
        pushOnExpressionStack(getUnspecifiedReferenceOptimized());
        Expression exp;
        this.expressionStack[this.expressionPtr] = exp = new InstanceOfExpression(this.expressionStack[this.expressionPtr], reference, op);
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
        // mark initializers with local type mark if needed
        markInitializersWithLocalType(typeDecl);
        //convert constructor that do not have the type's name into methods
        typeDecl.checkConstructors(this);
        //always add <clinit> (will be remove at code gen time if empty)
        if (this.scanner.containsAssertKeyword) {
            typeDecl.bits |= ASTNode.AddAssertionMASK;
        }
        typeDecl.addClinit();
        typeDecl.bodyEnd = this.endStatementPosition;
        if (length == 0 && !containsComment(typeDecl.bodyStart, typeDecl.bodyEnd)) {
            typeDecl.bits |= ASTNode.UndocumentedEmptyBlockMASK;
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
        for (int i = 0, max = typeDecl.superInterfaces.length; i < max; i++) {
            typeDecl.superInterfaces[i].bits |= ASTNode.IsSuperType;
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
                typeDecl.bits |= ASTNode.IsMemberTypeMASK;
            }
        } else {
            // Record that the block has a declaration for local types
            typeDecl.bits |= ASTNode.IsLocalTypeMASK;
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
    // InternalCompilationUnit ::= PackageDeclaration
    // InternalCompilationUnit ::= PackageDeclaration ImportDeclarations ReduceImports
    // InternalCompilationUnit ::= ImportDeclarations ReduceImports
    }

    protected void consumeInternalCompilationUnitWithTypes() {
        // InternalCompilationUnit ::= PackageDeclaration ImportDeclarations ReduceImports TypeDeclarations
        // InternalCompilationUnit ::= PackageDeclaration TypeDeclarations
        // InternalCompilationUnit ::= TypeDeclarations
        // InternalCompilationUnit ::= ImportDeclarations ReduceImports TypeDeclarations
        // consume type declarations
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.astPtr -= length;
            System.arraycopy(this.astStack, this.astPtr + 1, this.compilationUnit.types = new TypeDeclaration[length], 0, length);
        }
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
        cd.modifiers |= AccSemicolonBody;
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
    }

    protected void consumeLocalVariableDeclarationStatement() {
        // LocalVariableDeclarationStatement ::= LocalVariableDeclaration ';'
        // see blockReal in case of change: duplicated code
        // increment the amount of declared variables for this block
        this.realBlockStack[this.realBlockPtr]++;
        // update source end to include the semi-colon
        int variableDeclaratorsCounter = this.astLengthStack[this.astLengthPtr];
        for (int i = variableDeclaratorsCounter - 1; i >= 0; i--) {
            LocalDeclaration localDeclaration = (LocalDeclaration) this.astStack[this.astPtr - i];
            localDeclaration.declarationSourceEnd = this.endStatementPosition;
            // semi-colon included
            localDeclaration.declarationEnd = this.endStatementPosition;
        }
    }

    protected void consumeMarkerAnnotation() {
        // MarkerAnnotation ::= '@' Name
        MarkerAnnotation markerAnnotation = null;
        int length = this.identifierLengthStack[this.identifierLengthPtr--];
        if (length == 1) {
            markerAnnotation = new MarkerAnnotation(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--], this.intStack[this.intPtr--]);
        } else {
            char[][] tokens = new char[length][];
            this.identifierPtr -= length;
            long[] positions = new long[length];
            System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
            System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
            markerAnnotation = new MarkerAnnotation(tokens, positions, this.intStack[this.intPtr--]);
        }
        int sourceStart = markerAnnotation.sourceStart;
        if (this.modifiersSourceStart < 0) {
            this.modifiersSourceStart = sourceStart;
        } else if (this.modifiersSourceStart > sourceStart) {
            this.modifiersSourceStart = sourceStart;
        }
        markerAnnotation.declarationSourceEnd = markerAnnotation.sourceEnd;
        pushOnExpressionStack(markerAnnotation);
        if (options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfAnnotation(markerAnnotation);
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

    protected void consumeMethodDeclaration(boolean isNotAbstract) {
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
                System.arraycopy(this.astStack, (this.astPtr -= length) + 1, statements = new Statement[length], 0, length);
            }
        }
        // now we know that we have a method declaration at the top of the ast stack
        MethodDeclaration md = (MethodDeclaration) this.astStack[this.astPtr];
        md.statements = statements;
        md.explicitDeclarations = explicitDeclarations;
        // is a body when we reduce the method header
        if (//remember the fact that the method has a semicolon body
        !isNotAbstract) {
            md.modifiers |= AccSemicolonBody;
        } else {
            if (!this.diet && statements == null) {
                if (!containsComment(md.bodyStart, this.endPosition)) {
                    md.bits |= ASTNode.UndocumentedEmptyBlockMASK;
                }
            }
        }
        // store the this.endPosition (position just before the '}') in case there is
        // a trailing comment behind the end of the method
        md.bodyEnd = this.endPosition;
        md.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeMethodHeader() {
        // MethodHeader ::= MethodHeaderName MethodHeaderParameters MethodHeaderExtendedDims ThrowsClauseopt
        // retrieve end position of method declarator
        AbstractMethodDeclaration method = (AbstractMethodDeclaration) this.astStack[this.astPtr];
        if (this.currentToken == TokenNameLBRACE) {
            method.bodyStart = this.scanner.currentPosition;
        }
        // recovery
        if (this.currentElement != null) {
            if (this.currentToken == TokenNameSEMICOLON) {
                method.modifiers |= AccSemicolonBody;
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

    protected void consumeMethodHeaderExtendedDims() {
        // MethodHeaderExtendedDims ::= Dimsopt
        // now we update the returnType of the method
        MethodDeclaration md = (MethodDeclaration) this.astStack[this.astPtr];
        int extendedDims = this.intStack[this.intPtr--];
        if (extendedDims != 0) {
            TypeReference returnType = md.returnType;
            md.sourceEnd = this.endPosition;
            int dims = returnType.dimensions() + extendedDims;
            int baseType;
            if ((baseType = this.identifierLengthStack[this.identifierLengthPtr + 1]) < 0) {
                //it was a baseType
                int sourceStart = returnType.sourceStart;
                int sourceEnd = returnType.sourceEnd;
                returnType = TypeReference.baseTypeReference(-baseType, dims);
                returnType.sourceStart = sourceStart;
                returnType.sourceEnd = sourceEnd;
                md.returnType = returnType;
            } else {
                md.returnType = this.copyDims(md.returnType, dims);
            }
            if (this.currentToken == TokenNameLBRACE) {
                md.bodyStart = this.endPosition + 1;
            }
            // recovery
            if (this.currentElement != null) {
                this.lastCheckPoint = md.bodyStart;
            }
        }
    }

    protected void consumeMethodHeaderName() {
        // MethodHeaderName ::= Modifiersopt Type 'Identifier' '('
        MethodDeclaration md = new MethodDeclaration(this.compilationUnit.compilationResult);
        //name
        md.selector = this.identifierStack[this.identifierPtr];
        long selectorSource = this.identifierPositionStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        //type
        md.returnType = getTypeReference(this.intStack[this.intPtr--]);
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
            (this.scanner.getLineNumber(md.returnType.sourceStart) == this.scanner.getLineNumber(md.sourceStart))) {
                this.lastCheckPoint = md.bodyStart;
                this.currentElement = this.currentElement.add(md, 0);
                this.lastIgnoredToken = -1;
            } else {
                this.lastCheckPoint = md.sourceStart;
                this.restartRecovery = true;
            }
        }
    }

    protected void consumeMethodHeaderNameWithTypeParameters() {
        // MethodHeaderName ::= Modifiersopt TypeParameters Type 'Identifier' '('
        MethodDeclaration md = new MethodDeclaration(this.compilationUnit.compilationResult);
        //name
        md.selector = this.identifierStack[this.identifierPtr];
        long selectorSource = this.identifierPositionStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        //type
        md.returnType = getTypeReference(this.intStack[this.intPtr--]);
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
            if (this.currentElement instanceof RecoveredType || //|| md.modifiers != 0
            (this.scanner.getLineNumber(md.returnType.sourceStart) == this.scanner.getLineNumber(md.sourceStart))) {
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
            System.arraycopy(this.astStack, this.astPtr + 1, md.arguments = new Argument[length], 0, length);
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
        pushOnExpressionStack(m);
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
        // TODO (olivier) bug 67790 remove once DOMParser is activated
        intPtr--;
        m.receiver = getUnspecifiedReference();
        m.sourceStart = m.receiver.sourceStart;
        pushOnExpressionStack(m);
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
        // TODO (olivier) bug 67790 remove once DOMParser is activated
        intPtr--;
        m.receiver = this.expressionStack[this.expressionPtr];
        m.sourceStart = m.receiver.sourceStart;
        m.sourceEnd = this.rParenPos;
        this.expressionStack[this.expressionPtr] = m;
    }

    protected void consumeMethodInvocationSuper() {
        // MethodInvocation ::= 'super' '.' 'Identifier' '(' ArgumentListopt ')'
        MessageSend m = newMessageSend();
        m.sourceStart = this.intStack[this.intPtr--];
        m.sourceEnd = this.rParenPos;
        m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
        m.selector = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        m.receiver = new SuperReference(m.sourceStart, this.endPosition);
        pushOnExpressionStack(m);
    }

    protected void consumeMethodInvocationSuperWithTypeArguments() {
        // MethodInvocation ::= 'super' '.' TypeArguments 'Identifier' '(' ArgumentListopt ')'
        MessageSend m = newMessageSendWithTypeArguments();
        m.sourceStart = this.intStack[this.intPtr--];
        m.sourceEnd = this.rParenPos;
        m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
        m.selector = this.identifierStack[this.identifierPtr--];
        this.identifierLengthPtr--;
        // handle type arguments
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, m.typeArguments = new TypeReference[length], 0, length);
        // TODO (olivier) bug 67790 remove once DOMParser is activated
        intPtr--;
        m.receiver = new SuperReference(m.sourceStart, this.endPosition);
        pushOnExpressionStack(m);
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

    protected void consumeNormalAnnotation() {
        // NormalAnnotation ::= '@' Name '(' MemberValuePairsopt ')'
        NormalAnnotation normalAnnotation = null;
        int length = this.identifierLengthStack[this.identifierLengthPtr--];
        if (length == 1) {
            normalAnnotation = new NormalAnnotation(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--], this.intStack[this.intPtr--]);
        } else {
            char[][] tokens = new char[length][];
            this.identifierPtr -= length;
            long[] positions = new long[length];
            System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
            System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
            normalAnnotation = new NormalAnnotation(tokens, positions, this.intStack[this.intPtr--]);
        }
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            System.arraycopy(this.astStack, (this.astPtr -= length) + 1, normalAnnotation.memberValuePairs = new MemberValuePair[length], 0, length);
        }
        int sourceStart = normalAnnotation.sourceStart;
        if (this.modifiersSourceStart < 0) {
            this.modifiersSourceStart = sourceStart;
        } else if (this.modifiersSourceStart > sourceStart) {
            this.modifiersSourceStart = sourceStart;
        }
        normalAnnotation.declarationSourceEnd = this.rParenPos;
        pushOnExpressionStack(normalAnnotation);
        if (options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfAnnotation(normalAnnotation);
        }
    }

    protected void consumeOneDimLoop() {
        // OneDimLoop ::= '[' ']'
        this.dimensions++;
    }

    protected void consumeOnlySynchronized() {
        // OnlySynchronized ::= 'synchronized'
        pushOnIntStack(this.synchronizedBlockSourceStart);
        resetModifiers();
        this.expressionLengthPtr--;
    }

    protected void consumeOnlyTypeArguments() {
        if (options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            int length = this.genericsLengthStack[this.genericsLengthPtr];
            this.problemReporter().invalidUsageOfTypeArguments((TypeReference) this.genericsStack[this.genericsPtr - length + 1], (TypeReference) this.genericsStack[this.genericsPtr]);
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

    protected void consumePackageDeclaration() {
        // PackageDeclaration ::= 'package' Name ';'
        /* build an ImportRef build from the last name 
	stored in the identifier stack. */
        ImportReference impt = this.compilationUnit.currentPackage;
        // flush comments defined prior to import statements
        impt.declarationEnd = this.endStatementPosition;
        impt.declarationSourceEnd = this.flushCommentsDefinedPriorTo(impt.declarationSourceEnd);
    }

    protected void consumePackageDeclarationName() {
        // PackageDeclarationName ::= 'package' Name
        /* build an ImportRef build from the last name 
	stored in the identifier stack. */
        ImportReference impt;
        int length;
        char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, ++this.identifierPtr, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr--, positions, 0, length);
        impt = new ImportReference(tokens, positions, true, AccDefault);
        this.compilationUnit.currentPackage = impt;
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
            // used to avoid branching back into the regular automaton		
            this.restartRecovery = true;
        }
    }

    protected void consumePackageDeclarationNameWithModifiers() {
        // PackageDeclarationName ::= Modifiers 'package' Name
        /* build an ImportRef build from the last name 
	stored in the identifier stack. */
        ImportReference impt;
        int length;
        char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, ++this.identifierPtr, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr--, positions, 0, length);
        // we don't need the modifiers start
        int packageModifiersSourceStart = this.intStack[this.intPtr--];
        int packageModifiers = this.intStack[this.intPtr--];
        impt = new ImportReference(tokens, positions, true, packageModifiers);
        this.compilationUnit.currentPackage = impt;
        // consume annotations
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, impt.annotations = new Annotation[length], 0, length);
            impt.declarationSourceStart = packageModifiersSourceStart;
            // we don't need the position of the 'package keyword
            intPtr--;
        } else {
            impt.declarationSourceStart = this.intStack[this.intPtr--];
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
        pushOnExpressionStack(new ClassLiteralAccess(this.intStack[this.intPtr--], getTypeReference(this.intStack[this.intPtr--])));
    }

    protected void consumePrimaryNoNewArrayName() {
        // PrimaryNoNewArray ::= Name '.' 'class'
        // remove the class start position
        this.intPtr--;
        // handle type arguments
        pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        pushOnGenericsLengthStack(0);
        TypeReference typeReference = getTypeReference(0);
        pushOnExpressionStack(new ClassLiteralAccess(this.intStack[this.intPtr--], typeReference));
    }

    protected void consumePrimaryNoNewArrayNameSuper() {
        // PrimaryNoNewArray ::= Name '.' 'super'
        // handle type arguments
        pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        pushOnGenericsLengthStack(0);
        TypeReference typeReference = getTypeReference(0);
        pushOnExpressionStack(new QualifiedSuperReference(typeReference, this.intStack[this.intPtr--], this.endPosition));
    }

    protected void consumePrimaryNoNewArrayNameThis() {
        // PrimaryNoNewArray ::= Name '.' 'this'
        // handle type arguments
        pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        // handle type arguments
        pushOnGenericsLengthStack(0);
        TypeReference typeReference = getTypeReference(0);
        pushOnExpressionStack(new QualifiedThisReference(typeReference, this.intStack[this.intPtr--], this.endPosition));
    }

    protected void consumePrimaryNoNewArrayPrimitiveArrayType() {
        // PrimaryNoNewArray ::= PrimitiveType Dims '.' 'class'
        // remove the class start position
        this.intPtr--;
        pushOnExpressionStack(new ClassLiteralAccess(this.intStack[this.intPtr--], getTypeReference(this.intStack[this.intPtr--])));
    }

    protected void consumePrimaryNoNewArrayPrimitiveType() {
        // PrimaryNoNewArray ::= PrimitiveType '.' 'class'
        // remove the class start position
        this.intPtr--;
        pushOnExpressionStack(new ClassLiteralAccess(this.intStack[this.intPtr--], getTypeReference(0)));
    }

    protected void consumePrimaryNoNewArrayThis() {
        // PrimaryNoNewArray ::= 'this'
        pushOnExpressionStack(new ThisReference(this.intStack[this.intPtr--], this.endPosition));
    }

    protected void consumePrimaryNoNewArrayWithName() {
        // PrimaryNoNewArray ::=  PushLPAREN Expression PushRPAREN 
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

    protected void consumePushRealModifiers() {
        // modifiers
        pushOnIntStack(this.modifiers);
        pushOnIntStack(this.modifiersSourceStart);
        resetModifiers();
    }

    protected void consumePushModifiers() {
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

    protected void consumeQualifiedName() {
        // QualifiedName ::= Name '.' SimpleName 
        /*back from the recursive loop of QualifiedName.
	Updates identifier length into the length stack*/
        this.identifierLengthStack[--this.identifierLengthPtr]++;
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

    protected void consumeRestoreDiet() {
        // RestoreDiet ::= $empty
        this.dietInt--;
    }

    protected void consumeRightParen() {
        // PushRPAREN ::= ')'
        pushOnIntStack(this.rParenPos);
    }

    // This method is part of an automatic generation : do NOT edit-modify  
    protected void consumeRule(int act) {
        switch(act) {
            case //$NON-NLS-1$
            26:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Type ::= PrimitiveType");
                }
                consumePrimitiveType();
                break;
            case //$NON-NLS-1$
            40:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceType ::= ClassOrInterfaceType");
                }
                consumeReferenceType();
                break;
            case //$NON-NLS-1$
            44:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassOrInterface ::= Name");
                }
                consumeClassOrInterfaceName();
                break;
            case //$NON-NLS-1$
            45:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassOrInterface ::= GenericType DOT Name");
                }
                consumeClassOrInterface();
                break;
            case //$NON-NLS-1$
            46:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("GenericType ::= ClassOrInterface TypeArguments");
                }
                consumeGenericType();
                break;
            case //$NON-NLS-1$
            47:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayTypeWithTypeArgumentsName ::= GenericType DOT Name");
                }
                consumeArrayTypeWithTypeArgumentsName();
                break;
            case //$NON-NLS-1$
            48:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayType ::= PrimitiveType Dims");
                }
                consumePrimitiveArrayType();
                break;
            case //$NON-NLS-1$
            49:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayType ::= Name Dims");
                }
                consumeNameArrayType();
                break;
            case //$NON-NLS-1$
            50:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayType ::= ArrayTypeWithTypeArgumentsName Dims");
                }
                consumeGenericTypeNameArrayType();
                break;
            case //$NON-NLS-1$
            51:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayType ::= GenericType Dims");
                }
                consumeGenericTypeArrayType();
                break;
            case //$NON-NLS-1$
            56:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("QualifiedName ::= Name DOT SimpleName");
                }
                consumeQualifiedName();
                break;
            case //$NON-NLS-1$
            57:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CompilationUnit ::= EnterCompilationUnit...");
                }
                consumeCompilationUnit();
                break;
            case //$NON-NLS-1$
            58:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InternalCompilationUnit ::= PackageDeclaration");
                }
                consumeInternalCompilationUnit();
                break;
            case //$NON-NLS-1$
            59:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InternalCompilationUnit ::= PackageDeclaration...");
                }
                consumeInternalCompilationUnit();
                break;
            case //$NON-NLS-1$
            60:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InternalCompilationUnit ::= PackageDeclaration...");
                }
                consumeInternalCompilationUnitWithTypes();
                break;
            case //$NON-NLS-1$
            61:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InternalCompilationUnit ::= PackageDeclaration...");
                }
                consumeInternalCompilationUnitWithTypes();
                break;
            case //$NON-NLS-1$
            62:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InternalCompilationUnit ::= ImportDeclarations...");
                }
                consumeInternalCompilationUnit();
                break;
            case //$NON-NLS-1$
            63:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InternalCompilationUnit ::= TypeDeclarations");
                }
                consumeInternalCompilationUnitWithTypes();
                break;
            case //$NON-NLS-1$
            64:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InternalCompilationUnit ::= ImportDeclarations...");
                }
                consumeInternalCompilationUnitWithTypes();
                break;
            case //$NON-NLS-1$
            65:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InternalCompilationUnit ::=");
                }
                consumeEmptyInternalCompilationUnit();
                break;
            case //$NON-NLS-1$
            66:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReduceImports ::=");
                }
                consumeReduceImports();
                break;
            case //$NON-NLS-1$
            67:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnterCompilationUnit ::=");
                }
                consumeEnterCompilationUnit();
                break;
            case //$NON-NLS-1$
            78:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CatchHeader ::= catch LPAREN FormalParameter RPAREN...");
                }
                consumeCatchHeader();
                break;
            case //$NON-NLS-1$
            80:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ImportDeclarations ::= ImportDeclarations...");
                }
                consumeImportDeclarations();
                break;
            case //$NON-NLS-1$
            82:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeDeclarations ::= TypeDeclarations TypeDeclaration");
                }
                consumeTypeDeclarations();
                break;
            case //$NON-NLS-1$
            83:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PackageDeclaration ::= PackageDeclarationName SEMICOLON");
                }
                consumePackageDeclaration();
                break;
            case //$NON-NLS-1$
            84:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PackageDeclarationName ::= Modifiers package...");
                }
                consumePackageDeclarationNameWithModifiers();
                break;
            case //$NON-NLS-1$
            85:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PackageDeclarationName ::= package Name");
                }
                consumePackageDeclarationName();
                break;
            case //$NON-NLS-1$
            90:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SingleTypeImportDeclaration ::=...");
                }
                consumeImportDeclaration();
                break;
            case //$NON-NLS-1$
            91:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SingleTypeImportDeclarationName ::= import Name");
                }
                consumeSingleTypeImportDeclarationName();
                break;
            case //$NON-NLS-1$
            92:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeImportOnDemandDeclaration ::=...");
                }
                consumeImportDeclaration();
                break;
            case //$NON-NLS-1$
            93:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeImportOnDemandDeclarationName ::= import Name DOT...");
                }
                consumeTypeImportOnDemandDeclarationName();
                break;
            case //$NON-NLS-1$
            96:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeDeclaration ::= SEMICOLON");
                }
                consumeEmptyTypeDeclaration();
                break;
            case //$NON-NLS-1$
            100:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Modifiers ::= Modifiers Modifier");
                }
                consumeModifiers2();
                break;
            case //$NON-NLS-1$
            112:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Modifier ::= Annotation");
                }
                consumeAnnotationAsModifier();
                break;
            case //$NON-NLS-1$
            113:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassDeclaration ::= ClassHeader ClassBody");
                }
                consumeClassDeclaration();
                break;
            case //$NON-NLS-1$
            114:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassHeader ::= ClassHeaderName ClassHeaderExtendsopt...");
                }
                consumeClassHeader();
                break;
            case //$NON-NLS-1$
            115:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassHeaderName ::= ClassHeaderName1 TypeParameters");
                }
                consumeTypeHeaderNameWithTypeParameters();
                break;
            case //$NON-NLS-1$
            117:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassHeaderName1 ::= Modifiersopt class Identifier");
                }
                consumeClassHeaderName1();
                break;
            case //$NON-NLS-1$
            118:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassHeaderExtends ::= extends ClassType");
                }
                consumeClassHeaderExtends();
                break;
            case //$NON-NLS-1$
            119:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassHeaderImplements ::= implements InterfaceTypeList");
                }
                consumeClassHeaderImplements();
                break;
            case //$NON-NLS-1$
            121:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceTypeList ::= InterfaceTypeList COMMA...");
                }
                consumeInterfaceTypeList();
                break;
            case //$NON-NLS-1$
            122:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceType ::= ClassOrInterfaceType");
                }
                consumeInterfaceType();
                break;
            case //$NON-NLS-1$
            125:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassBodyDeclarations ::= ClassBodyDeclarations...");
                }
                consumeClassBodyDeclarations();
                break;
            case //$NON-NLS-1$
            129:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassBodyDeclaration ::= Diet NestedMethod Block");
                }
                consumeClassBodyDeclaration();
                break;
            case //$NON-NLS-1$
            130:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Diet ::=");
                }
                consumeDiet();
                break;
            case //$NON-NLS-1$
            131:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Initializer ::= Diet NestedMethod Block");
                }
                consumeClassBodyDeclaration();
                break;
            case //$NON-NLS-1$
            137:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassMemberDeclaration ::= SEMICOLON");
                }
                consumeEmptyClassMemberDeclaration();
                break;
            case //$NON-NLS-1$
            140:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("FieldDeclaration ::= Modifiersopt Type...");
                }
                consumeFieldDeclaration();
                break;
            case //$NON-NLS-1$
            142:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("VariableDeclarators ::= VariableDeclarators COMMA...");
                }
                consumeVariableDeclarators();
                break;
            case //$NON-NLS-1$
            145:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnterVariable ::=");
                }
                consumeEnterVariable();
                break;
            case //$NON-NLS-1$
            146:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExitVariableWithInitialization ::=");
                }
                consumeExitVariableWithInitialization();
                break;
            case //$NON-NLS-1$
            147:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExitVariableWithoutInitialization ::=");
                }
                consumeExitVariableWithoutInitialization();
                break;
            case //$NON-NLS-1$
            148:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ForceNoDiet ::=");
                }
                consumeForceNoDiet();
                break;
            case //$NON-NLS-1$
            149:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RestoreDiet ::=");
                }
                consumeRestoreDiet();
                break;
            case //$NON-NLS-1$
            154:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodDeclaration ::= MethodHeader MethodBody");
                }
                // set to true to consume a method with a body
                consumeMethodDeclaration(true);
                break;
            case //$NON-NLS-1$
            155:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AbstractMethodDeclaration ::= MethodHeader SEMICOLON");
                }
                // set to false to consume a method without body
                consumeMethodDeclaration(false);
                break;
            case //$NON-NLS-1$
            156:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodHeader ::= MethodHeaderName FormalParameterListopt");
                }
                consumeMethodHeader();
                break;
            case //$NON-NLS-1$
            157:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodHeaderName ::= Modifiersopt TypeParameters Type...");
                }
                consumeMethodHeaderNameWithTypeParameters();
                break;
            case //$NON-NLS-1$
            158:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodHeaderName ::= Modifiersopt Type Identifier LPAREN");
                }
                consumeMethodHeaderName();
                break;
            case //$NON-NLS-1$
            159:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodHeaderRightParen ::= RPAREN");
                }
                consumeMethodHeaderRightParen();
                break;
            case //$NON-NLS-1$
            160:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodHeaderExtendedDims ::= Dimsopt");
                }
                consumeMethodHeaderExtendedDims();
                break;
            case //$NON-NLS-1$
            161:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodHeaderThrowsClause ::= throws ClassTypeList");
                }
                consumeMethodHeaderThrowsClause();
                break;
            case //$NON-NLS-1$
            162:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConstructorHeader ::= ConstructorHeaderName...");
                }
                consumeConstructorHeader();
                break;
            case //$NON-NLS-1$
            163:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConstructorHeaderName ::= Modifiersopt TypeParameters...");
                }
                consumeConstructorHeaderNameWithTypeParameters();
                break;
            case //$NON-NLS-1$
            164:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConstructorHeaderName ::= Modifiersopt Identifier LPAREN");
                }
                consumeConstructorHeaderName();
                break;
            case //$NON-NLS-1$
            166:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("FormalParameterList ::= FormalParameterList COMMA...");
                }
                consumeFormalParameterList();
                break;
            case //$NON-NLS-1$
            167:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("FormalParameter ::= Modifiersopt Type...");
                }
                consumeFormalParameter(false);
                break;
            case //$NON-NLS-1$
            168:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("FormalParameter ::= Modifiersopt Type ELLIPSIS...");
                }
                consumeFormalParameter(true);
                break;
            case //$NON-NLS-1$
            170:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassTypeList ::= ClassTypeList COMMA ClassTypeElt");
                }
                consumeClassTypeList();
                break;
            case //$NON-NLS-1$
            171:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassTypeElt ::= ClassType");
                }
                consumeClassTypeElt();
                break;
            case //$NON-NLS-1$
            172:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodBody ::= NestedMethod LBRACE BlockStatementsopt...");
                }
                consumeMethodBody();
                break;
            case //$NON-NLS-1$
            173:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("NestedMethod ::=");
                }
                consumeNestedMethod();
                break;
            case //$NON-NLS-1$
            174:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("StaticInitializer ::= StaticOnly Block");
                }
                consumeStaticInitializer();
                break;
            case //$NON-NLS-1$
            175:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("StaticOnly ::= static");
                }
                consumeStaticOnly();
                break;
            case //$NON-NLS-1$
            176:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConstructorDeclaration ::= ConstructorHeader MethodBody");
                }
                consumeConstructorDeclaration();
                break;
            case //$NON-NLS-1$
            177:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConstructorDeclaration ::= ConstructorHeader SEMICOLON");
                }
                consumeInvalidConstructorDeclaration();
                break;
            case //$NON-NLS-1$
            178:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= this LPAREN...");
                }
                consumeExplicitConstructorInvocation(0, THIS_CALL);
                break;
            case //$NON-NLS-1$
            179:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= OnlyTypeArguments this");
                }
                consumeExplicitConstructorInvocationWithTypeArguments(0, THIS_CALL);
                break;
            case //$NON-NLS-1$
            180:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= super LPAREN...");
                }
                consumeExplicitConstructorInvocation(0, SUPER_CALL);
                break;
            case //$NON-NLS-1$
            181:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= OnlyTypeArguments...");
                }
                consumeExplicitConstructorInvocationWithTypeArguments(0, SUPER_CALL);
                break;
            case //$NON-NLS-1$
            182:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= Primary DOT super...");
                }
                consumeExplicitConstructorInvocation(1, SUPER_CALL);
                break;
            case //$NON-NLS-1$
            183:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= Primary DOT...");
                }
                consumeExplicitConstructorInvocationWithTypeArguments(1, SUPER_CALL);
                break;
            case //$NON-NLS-1$
            184:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= Name DOT super LPAREN");
                }
                consumeExplicitConstructorInvocation(2, SUPER_CALL);
                break;
            case //$NON-NLS-1$
            185:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= Name DOT...");
                }
                consumeExplicitConstructorInvocationWithTypeArguments(2, SUPER_CALL);
                break;
            case //$NON-NLS-1$
            186:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= Primary DOT this...");
                }
                consumeExplicitConstructorInvocation(1, THIS_CALL);
                break;
            case //$NON-NLS-1$
            187:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= Primary DOT...");
                }
                consumeExplicitConstructorInvocationWithTypeArguments(1, THIS_CALL);
                break;
            case //$NON-NLS-1$
            188:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= Name DOT this LPAREN");
                }
                consumeExplicitConstructorInvocation(2, THIS_CALL);
                break;
            case //$NON-NLS-1$
            189:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExplicitConstructorInvocation ::= Name DOT...");
                }
                consumeExplicitConstructorInvocationWithTypeArguments(2, THIS_CALL);
                break;
            case //$NON-NLS-1$
            190:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceDeclaration ::= InterfaceHeader InterfaceBody");
                }
                consumeInterfaceDeclaration();
                break;
            case //$NON-NLS-1$
            191:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceHeader ::= InterfaceHeaderName...");
                }
                consumeInterfaceHeader();
                break;
            case //$NON-NLS-1$
            192:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceHeaderName ::= InterfaceHeaderName1...");
                }
                consumeTypeHeaderNameWithTypeParameters();
                break;
            case //$NON-NLS-1$
            194:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceHeaderName1 ::= Modifiersopt interface...");
                }
                consumeInterfaceHeaderName1();
                break;
            case //$NON-NLS-1$
            196:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceHeaderExtends ::= extends InterfaceTypeList");
                }
                consumeInterfaceHeaderExtends();
                break;
            case //$NON-NLS-1$
            199:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceMemberDeclarations ::=...");
                }
                consumeInterfaceMemberDeclarations();
                break;
            case //$NON-NLS-1$
            200:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceMemberDeclaration ::= SEMICOLON");
                }
                consumeEmptyInterfaceMemberDeclaration();
                break;
            case //$NON-NLS-1$
            203:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceMemberDeclaration ::= InvalidMethodDeclaration");
                }
                ignoreMethodBody();
                break;
            case //$NON-NLS-1$
            204:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InvalidConstructorDeclaration ::= ConstructorHeader...");
                }
                ignoreInvalidConstructorDeclaration(true);
                break;
            case //$NON-NLS-1$
            205:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InvalidConstructorDeclaration ::= ConstructorHeader...");
                }
                ignoreInvalidConstructorDeclaration(false);
                break;
            case //$NON-NLS-1$
            212:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PushLeftBrace ::=");
                }
                consumePushLeftBrace();
                break;
            case //$NON-NLS-1$
            213:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayInitializer ::= LBRACE PushLeftBrace ,opt RBRACE");
                }
                consumeEmptyArrayInitializer();
                break;
            case //$NON-NLS-1$
            214:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayInitializer ::= LBRACE PushLeftBrace...");
                }
                consumeArrayInitializer();
                break;
            case //$NON-NLS-1$
            215:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayInitializer ::= LBRACE PushLeftBrace...");
                }
                consumeArrayInitializer();
                break;
            case //$NON-NLS-1$
            217:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("VariableInitializers ::= VariableInitializers COMMA...");
                }
                consumeVariableInitializers();
                break;
            case //$NON-NLS-1$
            218:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Block ::= OpenBlock LBRACE BlockStatementsopt RBRACE");
                }
                consumeBlock();
                break;
            case //$NON-NLS-1$
            219:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("OpenBlock ::=");
                }
                consumeOpenBlock();
                break;
            case //$NON-NLS-1$
            221:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("BlockStatements ::= BlockStatements BlockStatement");
                }
                consumeBlockStatements();
                break;
            case //$NON-NLS-1$
            225:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("BlockStatement ::= InvalidInterfaceDeclaration");
                }
                ignoreInterfaceDeclaration();
                break;
            case //$NON-NLS-1$
            226:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("LocalVariableDeclarationStatement ::=...");
                }
                consumeLocalVariableDeclarationStatement();
                break;
            case //$NON-NLS-1$
            227:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("LocalVariableDeclaration ::= Type PushModifiers...");
                }
                consumeLocalVariableDeclaration();
                break;
            case //$NON-NLS-1$
            228:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("LocalVariableDeclaration ::= Modifiers Type...");
                }
                consumeLocalVariableDeclaration();
                break;
            case //$NON-NLS-1$
            229:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PushModifiers ::=");
                }
                consumePushModifiers();
                break;
            case //$NON-NLS-1$
            230:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PushRealModifiers ::=");
                }
                consumePushRealModifiers();
                break;
            case //$NON-NLS-1$
            256:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EmptyStatement ::= SEMICOLON");
                }
                consumeEmptyStatement();
                break;
            case //$NON-NLS-1$
            257:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("LabeledStatement ::= Identifier COLON Statement");
                }
                consumeStatementLabel();
                break;
            case //$NON-NLS-1$
            258:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("LabeledStatementNoShortIf ::= Identifier COLON...");
                }
                consumeStatementLabel();
                break;
            case //$NON-NLS-1$
            259:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExpressionStatement ::= StatementExpression SEMICOLON");
                }
                consumeExpressionStatement();
                break;
            case //$NON-NLS-1$
            268:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("IfThenStatement ::= if LPAREN Expression RPAREN...");
                }
                consumeStatementIfNoElse();
                break;
            case //$NON-NLS-1$
            269:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("IfThenElseStatement ::= if LPAREN Expression RPAREN...");
                }
                consumeStatementIfWithElse();
                break;
            case //$NON-NLS-1$
            270:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("IfThenElseStatementNoShortIf ::= if LPAREN Expression...");
                }
                consumeStatementIfWithElse();
                break;
            case //$NON-NLS-1$
            271:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SwitchStatement ::= switch LPAREN Expression RPAREN...");
                }
                consumeStatementSwitch();
                break;
            case //$NON-NLS-1$
            272:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SwitchBlock ::= LBRACE RBRACE");
                }
                consumeEmptySwitchBlock();
                break;
            case //$NON-NLS-1$
            275:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SwitchBlock ::= LBRACE SwitchBlockStatements...");
                }
                consumeSwitchBlock();
                break;
            case //$NON-NLS-1$
            277:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SwitchBlockStatements ::= SwitchBlockStatements...");
                }
                consumeSwitchBlockStatements();
                break;
            case //$NON-NLS-1$
            278:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SwitchBlockStatement ::= SwitchLabels BlockStatements");
                }
                consumeSwitchBlockStatement();
                break;
            case //$NON-NLS-1$
            280:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SwitchLabels ::= SwitchLabels SwitchLabel");
                }
                consumeSwitchLabels();
                break;
            case //$NON-NLS-1$
            281:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SwitchLabel ::= case ConstantExpression COLON");
                }
                consumeCaseLabel();
                break;
            case //$NON-NLS-1$
            282:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SwitchLabel ::= default COLON");
                }
                consumeDefaultLabel();
                break;
            case //$NON-NLS-1$
            283:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WhileStatement ::= while LPAREN Expression RPAREN...");
                }
                consumeStatementWhile();
                break;
            case //$NON-NLS-1$
            284:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WhileStatementNoShortIf ::= while LPAREN Expression...");
                }
                consumeStatementWhile();
                break;
            case //$NON-NLS-1$
            285:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("DoStatement ::= do Statement while LPAREN Expression...");
                }
                consumeStatementDo();
                break;
            case //$NON-NLS-1$
            286:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ForStatement ::= for LPAREN ForInitopt SEMICOLON...");
                }
                consumeStatementFor();
                break;
            case //$NON-NLS-1$
            287:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ForStatementNoShortIf ::= for LPAREN ForInitopt...");
                }
                consumeStatementFor();
                break;
            case //$NON-NLS-1$
            288:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ForInit ::= StatementExpressionList");
                }
                consumeForInit();
                break;
            case //$NON-NLS-1$
            292:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("StatementExpressionList ::= StatementExpressionList...");
                }
                consumeStatementExpressionList();
                break;
            case //$NON-NLS-1$
            293:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssertStatement ::= assert Expression SEMICOLON");
                }
                consumeSimpleAssertStatement();
                break;
            case //$NON-NLS-1$
            294:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssertStatement ::= assert Expression COLON Expression");
                }
                consumeAssertStatement();
                break;
            case //$NON-NLS-1$
            295:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("BreakStatement ::= break SEMICOLON");
                }
                consumeStatementBreak();
                break;
            case //$NON-NLS-1$
            296:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("BreakStatement ::= break Identifier SEMICOLON");
                }
                consumeStatementBreakWithLabel();
                break;
            case //$NON-NLS-1$
            297:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ContinueStatement ::= continue SEMICOLON");
                }
                consumeStatementContinue();
                break;
            case //$NON-NLS-1$
            298:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ContinueStatement ::= continue Identifier SEMICOLON");
                }
                consumeStatementContinueWithLabel();
                break;
            case //$NON-NLS-1$
            299:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReturnStatement ::= return Expressionopt SEMICOLON");
                }
                consumeStatementReturn();
                break;
            case //$NON-NLS-1$
            300:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ThrowStatement ::= throw Expression SEMICOLON");
                }
                consumeStatementThrow();
                break;
            case //$NON-NLS-1$
            301:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SynchronizedStatement ::= OnlySynchronized LPAREN...");
                }
                consumeStatementSynchronized();
                break;
            case //$NON-NLS-1$
            302:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("OnlySynchronized ::= synchronized");
                }
                consumeOnlySynchronized();
                break;
            case //$NON-NLS-1$
            303:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TryStatement ::= try TryBlock Catches");
                }
                consumeStatementTry(false);
                break;
            case //$NON-NLS-1$
            304:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TryStatement ::= try TryBlock Catchesopt Finally");
                }
                consumeStatementTry(true);
                break;
            case //$NON-NLS-1$
            306:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExitTryBlock ::=");
                }
                consumeExitTryBlock();
                break;
            case //$NON-NLS-1$
            308:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Catches ::= Catches CatchClause");
                }
                consumeCatches();
                break;
            case //$NON-NLS-1$
            309:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CatchClause ::= catch LPAREN FormalParameter RPAREN...");
                }
                consumeStatementCatch();
                break;
            case //$NON-NLS-1$
            311:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PushLPAREN ::= LPAREN");
                }
                consumeLeftParen();
                break;
            case //$NON-NLS-1$
            312:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PushRPAREN ::= RPAREN");
                }
                consumeRightParen();
                break;
            case //$NON-NLS-1$
            317:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PrimaryNoNewArray ::= this");
                }
                consumePrimaryNoNewArrayThis();
                break;
            case //$NON-NLS-1$
            318:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PrimaryNoNewArray ::= PushLPAREN Expression_NotName...");
                }
                consumePrimaryNoNewArray();
                break;
            case //$NON-NLS-1$
            319:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PrimaryNoNewArray ::= PushLPAREN Name PushRPAREN");
                }
                consumePrimaryNoNewArrayWithName();
                break;
            case //$NON-NLS-1$
            322:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PrimaryNoNewArray ::= Name DOT this");
                }
                consumePrimaryNoNewArrayNameThis();
                break;
            case //$NON-NLS-1$
            323:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PrimaryNoNewArray ::= Name DOT super");
                }
                consumePrimaryNoNewArrayNameSuper();
                break;
            case //$NON-NLS-1$
            324:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PrimaryNoNewArray ::= Name DOT class");
                }
                consumePrimaryNoNewArrayName();
                break;
            case //$NON-NLS-1$
            325:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PrimaryNoNewArray ::= Name Dims DOT class");
                }
                consumePrimaryNoNewArrayArrayType();
                break;
            case //$NON-NLS-1$
            326:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PrimaryNoNewArray ::= PrimitiveType Dims DOT class");
                }
                consumePrimaryNoNewArrayPrimitiveArrayType();
                break;
            case //$NON-NLS-1$
            327:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PrimaryNoNewArray ::= PrimitiveType DOT class");
                }
                consumePrimaryNoNewArrayPrimitiveType();
                break;
            case //$NON-NLS-1$
            330:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AllocationHeader ::= new ClassType LPAREN...");
                }
                consumeAllocationHeader();
                break;
            case //$NON-NLS-1$
            331:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassInstanceCreationExpression ::= new...");
                }
                consumeClassInstanceCreationExpressionWithTypeArguments();
                break;
            case //$NON-NLS-1$
            332:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassInstanceCreationExpression ::= new ClassType LPAREN");
                }
                consumeClassInstanceCreationExpression();
                break;
            case //$NON-NLS-1$
            333:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassInstanceCreationExpression ::= Primary DOT new...");
                }
                consumeClassInstanceCreationExpressionQualifiedWithTypeArguments();
                break;
            case //$NON-NLS-1$
            334:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassInstanceCreationExpression ::= Primary DOT new...");
                }
                consumeClassInstanceCreationExpressionQualified();
                break;
            case //$NON-NLS-1$
            335:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassInstanceCreationExpression ::=...");
                }
                consumeClassInstanceCreationExpressionQualified();
                break;
            case //$NON-NLS-1$
            336:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassInstanceCreationExpression ::=...");
                }
                consumeClassInstanceCreationExpressionQualifiedWithTypeArguments();
                break;
            case //$NON-NLS-1$
            337:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassInstanceCreationExpressionName ::= Name DOT");
                }
                consumeClassInstanceCreationExpressionName();
                break;
            case //$NON-NLS-1$
            338:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassBodyopt ::=");
                }
                consumeClassBodyopt();
                break;
            case //$NON-NLS-1$
            340:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassBodySimpleNameopt ::=");
                }
                consumeClassBodyopt();
                break;
            case //$NON-NLS-1$
            342:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnterAnonymousClassBodySimpleName ::=");
                }
                consumeEnterAnonymousClassBodySimpleName();
                break;
            case //$NON-NLS-1$
            343:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnterAnonymousClassBody ::=");
                }
                consumeEnterAnonymousClassBody();
                break;
            case //$NON-NLS-1$
            345:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArgumentList ::= ArgumentList COMMA Expression");
                }
                consumeArgumentList();
                break;
            case //$NON-NLS-1$
            346:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayCreationHeader ::= new PrimitiveType...");
                }
                consumeArrayCreationHeader();
                break;
            case //$NON-NLS-1$
            347:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayCreationHeader ::= new ClassOrInterfaceType...");
                }
                consumeArrayCreationHeader();
                break;
            case //$NON-NLS-1$
            348:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayCreationWithoutArrayInitializer ::= new...");
                }
                consumeArrayCreationExpressionWithoutInitializer();
                break;
            case //$NON-NLS-1$
            349:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayCreationWithArrayInitializer ::= new PrimitiveType");
                }
                consumeArrayCreationExpressionWithInitializer();
                break;
            case //$NON-NLS-1$
            350:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayCreationWithoutArrayInitializer ::= new...");
                }
                consumeArrayCreationExpressionWithoutInitializer();
                break;
            case //$NON-NLS-1$
            351:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayCreationWithArrayInitializer ::= new...");
                }
                consumeArrayCreationExpressionWithInitializer();
                break;
            case //$NON-NLS-1$
            353:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("DimWithOrWithOutExprs ::= DimWithOrWithOutExprs...");
                }
                consumeDimWithOrWithOutExprs();
                break;
            case //$NON-NLS-1$
            355:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("DimWithOrWithOutExpr ::= LBRACKET RBRACKET");
                }
                consumeDimWithOrWithOutExpr();
                break;
            case //$NON-NLS-1$
            356:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Dims ::= DimsLoop");
                }
                consumeDims();
                break;
            case //$NON-NLS-1$
            359:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("OneDimLoop ::= LBRACKET RBRACKET");
                }
                consumeOneDimLoop();
                break;
            case //$NON-NLS-1$
            360:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("FieldAccess ::= Primary DOT Identifier");
                }
                consumeFieldAccess(false);
                break;
            case //$NON-NLS-1$
            361:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("FieldAccess ::= super DOT Identifier");
                }
                consumeFieldAccess(true);
                break;
            case //$NON-NLS-1$
            362:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodInvocation ::= Name LPAREN ArgumentListopt RPAREN");
                }
                consumeMethodInvocationName();
                break;
            case //$NON-NLS-1$
            363:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodInvocation ::= Name DOT OnlyTypeArguments...");
                }
                consumeMethodInvocationNameWithTypeArguments();
                break;
            case //$NON-NLS-1$
            364:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodInvocation ::= Primary DOT OnlyTypeArguments...");
                }
                consumeMethodInvocationPrimaryWithTypeArguments();
                break;
            case //$NON-NLS-1$
            365:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodInvocation ::= Primary DOT Identifier LPAREN...");
                }
                consumeMethodInvocationPrimary();
                break;
            case //$NON-NLS-1$
            366:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodInvocation ::= super DOT OnlyTypeArguments...");
                }
                consumeMethodInvocationSuperWithTypeArguments();
                break;
            case //$NON-NLS-1$
            367:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MethodInvocation ::= super DOT Identifier LPAREN...");
                }
                consumeMethodInvocationSuper();
                break;
            case //$NON-NLS-1$
            368:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayAccess ::= Name LBRACKET Expression RBRACKET");
                }
                consumeArrayAccess(true);
                break;
            case //$NON-NLS-1$
            369:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayAccess ::= PrimaryNoNewArray LBRACKET Expression...");
                }
                consumeArrayAccess(false);
                break;
            case //$NON-NLS-1$
            370:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArrayAccess ::= ArrayCreationWithArrayInitializer...");
                }
                consumeArrayAccess(false);
                break;
            case //$NON-NLS-1$
            372:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PostfixExpression ::= Name");
                }
                consumePostfixExpression();
                break;
            case //$NON-NLS-1$
            375:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PostIncrementExpression ::= PostfixExpression PLUS_PLUS");
                }
                consumeUnaryExpression(OperatorIds.PLUS, true);
                break;
            case //$NON-NLS-1$
            376:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PostDecrementExpression ::= PostfixExpression...");
                }
                consumeUnaryExpression(OperatorIds.MINUS, true);
                break;
            case //$NON-NLS-1$
            377:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PushPosition ::=");
                }
                consumePushPosition();
                break;
            case //$NON-NLS-1$
            380:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnaryExpression ::= PLUS PushPosition UnaryExpression");
                }
                consumeUnaryExpression(OperatorIds.PLUS);
                break;
            case //$NON-NLS-1$
            381:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnaryExpression ::= MINUS PushPosition UnaryExpression");
                }
                consumeUnaryExpression(OperatorIds.MINUS);
                break;
            case //$NON-NLS-1$
            383:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PreIncrementExpression ::= PLUS_PLUS PushPosition...");
                }
                consumeUnaryExpression(OperatorIds.PLUS, false);
                break;
            case //$NON-NLS-1$
            384:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("PreDecrementExpression ::= MINUS_MINUS PushPosition...");
                }
                consumeUnaryExpression(OperatorIds.MINUS, false);
                break;
            case //$NON-NLS-1$
            386:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnaryExpressionNotPlusMinus ::= TWIDDLE PushPosition...");
                }
                consumeUnaryExpression(OperatorIds.TWIDDLE);
                break;
            case //$NON-NLS-1$
            387:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnaryExpressionNotPlusMinus ::= NOT PushPosition...");
                }
                consumeUnaryExpression(OperatorIds.NOT);
                break;
            case //$NON-NLS-1$
            389:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CastExpression ::= PushLPAREN PrimitiveType Dimsopt...");
                }
                consumeCastExpressionWithPrimitiveType();
                break;
            case //$NON-NLS-1$
            390:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CastExpression ::= PushLPAREN Name...");
                }
                consumeCastExpressionWithGenericsArray();
                break;
            case //$NON-NLS-1$
            391:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CastExpression ::= PushLPAREN Name...");
                }
                consumeCastExpressionWithQualifiedGenericsArray();
                break;
            case //$NON-NLS-1$
            392:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CastExpression ::= PushLPAREN Name PushRPAREN...");
                }
                consumeCastExpressionLL1();
                break;
            case //$NON-NLS-1$
            393:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("CastExpression ::= PushLPAREN Name Dims PushRPAREN...");
                }
                consumeCastExpressionWithNameArray();
                break;
            case //$NON-NLS-1$
            394:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("OnlyTypeArgumentsForCastExpression ::= OnlyTypeArguments");
                }
                consumeOnlyTypeArgumentsForCastExpression();
                break;
            case //$NON-NLS-1$
            395:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InsideCastExpression ::=");
                }
                consumeInsideCastExpression();
                break;
            case //$NON-NLS-1$
            396:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InsideCastExpressionLL1 ::=");
                }
                consumeInsideCastExpressionLL1();
                break;
            case //$NON-NLS-1$
            397:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InsideCastExpressionWithQualifiedGenerics ::=");
                }
                consumeInsideCastExpressionWithQualifiedGenerics();
                break;
            case //$NON-NLS-1$
            399:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MultiplicativeExpression ::= MultiplicativeExpression...");
                }
                consumeBinaryExpression(OperatorIds.MULTIPLY);
                break;
            case //$NON-NLS-1$
            400:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MultiplicativeExpression ::= MultiplicativeExpression...");
                }
                consumeBinaryExpression(OperatorIds.DIVIDE);
                break;
            case //$NON-NLS-1$
            401:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MultiplicativeExpression ::= MultiplicativeExpression...");
                }
                consumeBinaryExpression(OperatorIds.REMAINDER);
                break;
            case //$NON-NLS-1$
            403:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditiveExpression ::= AdditiveExpression PLUS...");
                }
                consumeBinaryExpression(OperatorIds.PLUS);
                break;
            case //$NON-NLS-1$
            404:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditiveExpression ::= AdditiveExpression MINUS...");
                }
                consumeBinaryExpression(OperatorIds.MINUS);
                break;
            case //$NON-NLS-1$
            406:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ShiftExpression ::= ShiftExpression LEFT_SHIFT...");
                }
                consumeBinaryExpression(OperatorIds.LEFT_SHIFT);
                break;
            case //$NON-NLS-1$
            407:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ShiftExpression ::= ShiftExpression RIGHT_SHIFT...");
                }
                consumeBinaryExpression(OperatorIds.RIGHT_SHIFT);
                break;
            case //$NON-NLS-1$
            408:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ShiftExpression ::= ShiftExpression UNSIGNED_RIGHT_SHIFT");
                }
                consumeBinaryExpression(OperatorIds.UNSIGNED_RIGHT_SHIFT);
                break;
            case //$NON-NLS-1$
            410:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression ::= RelationalExpression LESS...");
                }
                consumeBinaryExpression(OperatorIds.LESS);
                break;
            case //$NON-NLS-1$
            411:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression ::= RelationalExpression GREATER...");
                }
                consumeBinaryExpression(OperatorIds.GREATER);
                break;
            case //$NON-NLS-1$
            412:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression ::= RelationalExpression LESS_EQUAL");
                }
                consumeBinaryExpression(OperatorIds.LESS_EQUAL);
                break;
            case //$NON-NLS-1$
            413:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression ::= RelationalExpression...");
                }
                consumeBinaryExpression(OperatorIds.GREATER_EQUAL);
                break;
            case //$NON-NLS-1$
            415:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InstanceofExpression ::= InstanceofExpression instanceof");
                }
                consumeInstanceOfExpression(OperatorIds.INSTANCEOF);
                break;
            case //$NON-NLS-1$
            417:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EqualityExpression ::= EqualityExpression EQUAL_EQUAL...");
                }
                consumeEqualityExpression(OperatorIds.EQUAL_EQUAL);
                break;
            case //$NON-NLS-1$
            418:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EqualityExpression ::= EqualityExpression NOT_EQUAL...");
                }
                consumeEqualityExpression(OperatorIds.NOT_EQUAL);
                break;
            case //$NON-NLS-1$
            420:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AndExpression ::= AndExpression AND EqualityExpression");
                }
                consumeBinaryExpression(OperatorIds.AND);
                break;
            case //$NON-NLS-1$
            422:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExclusiveOrExpression ::= ExclusiveOrExpression XOR...");
                }
                consumeBinaryExpression(OperatorIds.XOR);
                break;
            case //$NON-NLS-1$
            424:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InclusiveOrExpression ::= InclusiveOrExpression OR...");
                }
                consumeBinaryExpression(OperatorIds.OR);
                break;
            case //$NON-NLS-1$
            426:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConditionalAndExpression ::= ConditionalAndExpression...");
                }
                consumeBinaryExpression(OperatorIds.AND_AND);
                break;
            case //$NON-NLS-1$
            428:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConditionalOrExpression ::= ConditionalOrExpression...");
                }
                consumeBinaryExpression(OperatorIds.OR_OR);
                break;
            case //$NON-NLS-1$
            430:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConditionalExpression ::= ConditionalOrExpression...");
                }
                consumeConditionalExpression(OperatorIds.QUESTIONCOLON);
                break;
            case //$NON-NLS-1$
            433:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Assignment ::= PostfixExpression AssignmentOperator...");
                }
                consumeAssignment();
                break;
            case //$NON-NLS-1$
            435:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Assignment ::= InvalidArrayInitializerAssignement");
                }
                ignoreExpressionAssignment();
                break;
            case //$NON-NLS-1$
            436:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= EQUAL");
                }
                consumeAssignmentOperator(EQUAL);
                break;
            case //$NON-NLS-1$
            437:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= MULTIPLY_EQUAL");
                }
                consumeAssignmentOperator(MULTIPLY);
                break;
            case //$NON-NLS-1$
            438:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= DIVIDE_EQUAL");
                }
                consumeAssignmentOperator(DIVIDE);
                break;
            case //$NON-NLS-1$
            439:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= REMAINDER_EQUAL");
                }
                consumeAssignmentOperator(REMAINDER);
                break;
            case //$NON-NLS-1$
            440:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= PLUS_EQUAL");
                }
                consumeAssignmentOperator(PLUS);
                break;
            case //$NON-NLS-1$
            441:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= MINUS_EQUAL");
                }
                consumeAssignmentOperator(MINUS);
                break;
            case //$NON-NLS-1$
            442:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= LEFT_SHIFT_EQUAL");
                }
                consumeAssignmentOperator(LEFT_SHIFT);
                break;
            case //$NON-NLS-1$
            443:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= RIGHT_SHIFT_EQUAL");
                }
                consumeAssignmentOperator(RIGHT_SHIFT);
                break;
            case //$NON-NLS-1$
            444:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= UNSIGNED_RIGHT_SHIFT_EQUAL");
                }
                consumeAssignmentOperator(UNSIGNED_RIGHT_SHIFT);
                break;
            case //$NON-NLS-1$
            445:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= AND_EQUAL");
                }
                consumeAssignmentOperator(AND);
                break;
            case //$NON-NLS-1$
            446:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= XOR_EQUAL");
                }
                consumeAssignmentOperator(XOR);
                break;
            case //$NON-NLS-1$
            447:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AssignmentOperator ::= OR_EQUAL");
                }
                consumeAssignmentOperator(OR);
                break;
            case //$NON-NLS-1$
            451:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Expressionopt ::=");
                }
                consumeEmptyExpression();
                break;
            case //$NON-NLS-1$
            456:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassBodyDeclarationsopt ::=");
                }
                consumeEmptyClassBodyDeclarationsopt();
                break;
            case //$NON-NLS-1$
            457:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ClassBodyDeclarationsopt ::= NestedType...");
                }
                consumeClassBodyDeclarationsopt();
                break;
            case //$NON-NLS-1$
            458:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Modifiersopt ::=");
                }
                consumeDefaultModifiers();
                break;
            case //$NON-NLS-1$
            459:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Modifiersopt ::= Modifiers");
                }
                consumeModifiers();
                break;
            case //$NON-NLS-1$
            460:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("BlockStatementsopt ::=");
                }
                consumeEmptyBlockStatementsopt();
                break;
            case //$NON-NLS-1$
            462:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Dimsopt ::=");
                }
                consumeEmptyDimsopt();
                break;
            case //$NON-NLS-1$
            464:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ArgumentListopt ::=");
                }
                consumeEmptyArgumentListopt();
                break;
            case //$NON-NLS-1$
            468:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("FormalParameterListopt ::=");
                }
                consumeFormalParameterListopt();
                break;
            case //$NON-NLS-1$
            472:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceMemberDeclarationsopt ::=");
                }
                consumeEmptyInterfaceMemberDeclarationsopt();
                break;
            case //$NON-NLS-1$
            473:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InterfaceMemberDeclarationsopt ::= NestedType...");
                }
                consumeInterfaceMemberDeclarationsopt();
                break;
            case //$NON-NLS-1$
            474:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("NestedType ::=");
                }
                consumeNestedType();
                break;
            case //$NON-NLS-1$
            475:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ForInitopt ::=");
                }
                consumeEmptyForInitopt();
                break;
            case //$NON-NLS-1$
            477:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ForUpdateopt ::=");
                }
                consumeEmptyForUpdateopt();
                break;
            case //$NON-NLS-1$
            481:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Catchesopt ::=");
                }
                consumeEmptyCatchesopt();
                break;
            case //$NON-NLS-1$
            483:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumDeclaration ::= EnumHeader ClassHeaderImplementsopt");
                }
                consumeEnumDeclaration();
                break;
            case //$NON-NLS-1$
            484:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumHeader ::= Modifiersopt enum Identifier");
                }
                consumeEnumHeader();
                break;
            case //$NON-NLS-1$
            485:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumBody ::= LBRACE EnumBodyDeclarationsopt RBRACE");
                }
                consumeEnumBodyNoConstants();
                break;
            case //$NON-NLS-1$
            486:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumBody ::= LBRACE COMMA EnumBodyDeclarationsopt...");
                }
                consumeEnumBodyNoConstants();
                break;
            case //$NON-NLS-1$
            487:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumBody ::= LBRACE EnumConstants COMMA...");
                }
                consumeEnumBodyWithConstants();
                break;
            case //$NON-NLS-1$
            488:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumBody ::= LBRACE EnumConstants...");
                }
                consumeEnumBodyWithConstants();
                break;
            case //$NON-NLS-1$
            490:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumConstants ::= EnumConstants COMMA EnumConstant");
                }
                consumeEnumConstants();
                break;
            case //$NON-NLS-1$
            491:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumConstantHeader ::= Identifier Argumentsopt");
                }
                consumeEnumConstantHeader();
                break;
            case //$NON-NLS-1$
            492:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumConstant ::= EnumConstantHeader ClassBody");
                }
                consumeEnumConstantWithClassBody();
                break;
            case //$NON-NLS-1$
            493:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumConstant ::= EnumConstantHeader");
                }
                consumeEnumConstantNoClassBody();
                break;
            case //$NON-NLS-1$
            494:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Arguments ::= LPAREN ArgumentListopt RPAREN");
                }
                consumeArguments();
                break;
            case //$NON-NLS-1$
            495:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Argumentsopt ::=");
                }
                consumeEmptyArguments();
                break;
            case //$NON-NLS-1$
            497:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumDeclarations ::= SEMICOLON ClassBodyDeclarationsopt");
                }
                consumeEnumDeclarations();
                break;
            case //$NON-NLS-1$
            498:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnumBodyDeclarationsopt ::=");
                }
                consumeEmptyEnumDeclarations();
                break;
            case //$NON-NLS-1$
            500:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnhancedForStatement ::= EnhancedForStatementHeader...");
                }
                consumeEnhancedForStatement();
                break;
            case //$NON-NLS-1$
            501:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnhancedForStatementNoShortIf ::=...");
                }
                consumeEnhancedForStatement();
                break;
            case //$NON-NLS-1$
            502:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnhancedForStatementHeader ::= for LPAREN Type...");
                }
                consumeEnhancedForStatementHeader(false);
                break;
            case //$NON-NLS-1$
            503:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EnhancedForStatementHeader ::= for LPAREN Modifiers Type");
                }
                consumeEnhancedForStatementHeader(true);
                break;
            case //$NON-NLS-1$
            504:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SingleStaticImportDeclaration ::=...");
                }
                consumeImportDeclaration();
                break;
            case //$NON-NLS-1$
            505:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SingleStaticImportDeclarationName ::= import static Name");
                }
                consumeSingleStaticImportDeclarationName();
                break;
            case //$NON-NLS-1$
            506:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("StaticImportOnDemandDeclaration ::=...");
                }
                consumeImportDeclaration();
                break;
            case //$NON-NLS-1$
            507:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("StaticImportOnDemandDeclarationName ::= import static...");
                }
                consumeStaticImportOnDemandDeclarationName();
                break;
            case //$NON-NLS-1$
            508:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeArguments ::= LESS TypeArgumentList1");
                }
                consumeTypeArguments();
                break;
            case //$NON-NLS-1$
            509:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("OnlyTypeArguments ::= LESS TypeArgumentList1");
                }
                consumeOnlyTypeArguments();
                break;
            case //$NON-NLS-1$
            511:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeArgumentList1 ::= TypeArgumentList COMMA...");
                }
                consumeTypeArgumentList1();
                break;
            case //$NON-NLS-1$
            513:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeArgumentList ::= TypeArgumentList COMMA TypeArgument");
                }
                consumeTypeArgumentList();
                break;
            case //$NON-NLS-1$
            514:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeArgument ::= ReferenceType");
                }
                consumeTypeArgument();
                break;
            case //$NON-NLS-1$
            518:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceType1 ::= ReferenceType GREATER");
                }
                consumeReferenceType1();
                break;
            case //$NON-NLS-1$
            519:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceType1 ::= ClassOrInterface LESS...");
                }
                consumeTypeArgumentReferenceType1();
                break;
            case //$NON-NLS-1$
            521:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeArgumentList2 ::= TypeArgumentList COMMA...");
                }
                consumeTypeArgumentList2();
                break;
            case //$NON-NLS-1$
            524:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceType2 ::= ReferenceType RIGHT_SHIFT");
                }
                consumeReferenceType2();
                break;
            case //$NON-NLS-1$
            525:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceType2 ::= ClassOrInterface LESS...");
                }
                consumeTypeArgumentReferenceType2();
                break;
            case //$NON-NLS-1$
            527:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeArgumentList3 ::= TypeArgumentList COMMA...");
                }
                consumeTypeArgumentList3();
                break;
            case //$NON-NLS-1$
            530:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ReferenceType3 ::= ReferenceType UNSIGNED_RIGHT_SHIFT");
                }
                consumeReferenceType3();
                break;
            case //$NON-NLS-1$
            531:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Wildcard ::= QUESTION");
                }
                consumeWildcard();
                break;
            case //$NON-NLS-1$
            532:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Wildcard ::= QUESTION WildcardBounds");
                }
                consumeWildcardWithBounds();
                break;
            case //$NON-NLS-1$
            533:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WildcardBounds ::= extends ReferenceType");
                }
                consumeWildcardBoundsExtends();
                break;
            case //$NON-NLS-1$
            534:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WildcardBounds ::= super ReferenceType");
                }
                consumeWildcardBoundsSuper();
                break;
            case //$NON-NLS-1$
            535:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Wildcard1 ::= QUESTION GREATER");
                }
                consumeWildcard1();
                break;
            case //$NON-NLS-1$
            536:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Wildcard1 ::= QUESTION WildcardBounds1");
                }
                consumeWildcard1WithBounds();
                break;
            case //$NON-NLS-1$
            537:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WildcardBounds1 ::= extends ReferenceType1");
                }
                consumeWildcardBounds1Extends();
                break;
            case //$NON-NLS-1$
            538:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WildcardBounds1 ::= super ReferenceType1");
                }
                consumeWildcardBounds1Super();
                break;
            case //$NON-NLS-1$
            539:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Wildcard2 ::= QUESTION RIGHT_SHIFT");
                }
                consumeWildcard2();
                break;
            case //$NON-NLS-1$
            540:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Wildcard2 ::= QUESTION WildcardBounds2");
                }
                consumeWildcard2WithBounds();
                break;
            case //$NON-NLS-1$
            541:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WildcardBounds2 ::= extends ReferenceType2");
                }
                consumeWildcardBounds2Extends();
                break;
            case //$NON-NLS-1$
            542:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WildcardBounds2 ::= super ReferenceType2");
                }
                consumeWildcardBounds2Super();
                break;
            case //$NON-NLS-1$
            543:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Wildcard3 ::= QUESTION UNSIGNED_RIGHT_SHIFT");
                }
                consumeWildcard3();
                break;
            case //$NON-NLS-1$
            544:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("Wildcard3 ::= QUESTION WildcardBounds3");
                }
                consumeWildcard3WithBounds();
                break;
            case //$NON-NLS-1$
            545:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WildcardBounds3 ::= extends ReferenceType3");
                }
                consumeWildcardBounds3Extends();
                break;
            case //$NON-NLS-1$
            546:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("WildcardBounds3 ::= super ReferenceType3");
                }
                consumeWildcardBounds3Super();
                break;
            case //$NON-NLS-1$
            547:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeParameterHeader ::= Identifier");
                }
                consumeTypeParameterHeader();
                break;
            case //$NON-NLS-1$
            548:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeParameters ::= LESS TypeParameterList1");
                }
                consumeTypeParameters();
                break;
            case //$NON-NLS-1$
            550:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeParameterList ::= TypeParameterList COMMA...");
                }
                consumeTypeParameterList();
                break;
            case //$NON-NLS-1$
            552:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeParameter ::= TypeParameterHeader extends...");
                }
                consumeTypeParameterWithExtends();
                break;
            case //$NON-NLS-1$
            553:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeParameter ::= TypeParameterHeader extends...");
                }
                consumeTypeParameterWithExtendsAndBounds();
                break;
            case //$NON-NLS-1$
            555:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditionalBoundList ::= AdditionalBoundList...");
                }
                consumeAdditionalBoundList();
                break;
            case //$NON-NLS-1$
            556:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditionalBound ::= AND ReferenceType");
                }
                consumeAdditionalBound();
                break;
            case //$NON-NLS-1$
            558:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeParameterList1 ::= TypeParameterList COMMA...");
                }
                consumeTypeParameterList1();
                break;
            case //$NON-NLS-1$
            559:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeParameter1 ::= TypeParameterHeader GREATER");
                }
                consumeTypeParameter1();
                break;
            case //$NON-NLS-1$
            560:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeParameter1 ::= TypeParameterHeader extends...");
                }
                consumeTypeParameter1WithExtends();
                break;
            case //$NON-NLS-1$
            561:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("TypeParameter1 ::= TypeParameterHeader extends...");
                }
                consumeTypeParameter1WithExtendsAndBounds();
                break;
            case //$NON-NLS-1$
            563:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditionalBoundList1 ::= AdditionalBoundList...");
                }
                consumeAdditionalBoundList1();
                break;
            case //$NON-NLS-1$
            564:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditionalBound1 ::= AND ReferenceType1");
                }
                consumeAdditionalBound1();
                break;
            case //$NON-NLS-1$
            570:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnaryExpression_NotName ::= PLUS PushPosition...");
                }
                consumeUnaryExpression(OperatorIds.PLUS);
                break;
            case //$NON-NLS-1$
            571:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnaryExpression_NotName ::= MINUS PushPosition...");
                }
                consumeUnaryExpression(OperatorIds.MINUS);
                break;
            case //$NON-NLS-1$
            574:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnaryExpressionNotPlusMinus_NotName ::= TWIDDLE...");
                }
                consumeUnaryExpression(OperatorIds.TWIDDLE);
                break;
            case //$NON-NLS-1$
            575:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("UnaryExpressionNotPlusMinus_NotName ::= NOT PushPosition");
                }
                consumeUnaryExpression(OperatorIds.NOT);
                break;
            case //$NON-NLS-1$
            578:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MultiplicativeExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.MULTIPLY);
                break;
            case //$NON-NLS-1$
            579:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MultiplicativeExpression_NotName ::= Name MULTIPLY...");
                }
                consumeBinaryExpressionWithName(OperatorIds.MULTIPLY);
                break;
            case //$NON-NLS-1$
            580:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MultiplicativeExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.DIVIDE);
                break;
            case //$NON-NLS-1$
            581:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MultiplicativeExpression_NotName ::= Name DIVIDE...");
                }
                consumeBinaryExpressionWithName(OperatorIds.DIVIDE);
                break;
            case //$NON-NLS-1$
            582:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MultiplicativeExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.REMAINDER);
                break;
            case //$NON-NLS-1$
            583:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MultiplicativeExpression_NotName ::= Name REMAINDER...");
                }
                consumeBinaryExpressionWithName(OperatorIds.REMAINDER);
                break;
            case //$NON-NLS-1$
            585:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditiveExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.PLUS);
                break;
            case //$NON-NLS-1$
            586:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditiveExpression_NotName ::= Name PLUS...");
                }
                consumeBinaryExpressionWithName(OperatorIds.PLUS);
                break;
            case //$NON-NLS-1$
            587:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditiveExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.MINUS);
                break;
            case //$NON-NLS-1$
            588:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AdditiveExpression_NotName ::= Name MINUS...");
                }
                consumeBinaryExpressionWithName(OperatorIds.MINUS);
                break;
            case //$NON-NLS-1$
            590:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ShiftExpression_NotName ::= ShiftExpression_NotName...");
                }
                consumeBinaryExpression(OperatorIds.LEFT_SHIFT);
                break;
            case //$NON-NLS-1$
            591:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ShiftExpression_NotName ::= Name LEFT_SHIFT...");
                }
                consumeBinaryExpressionWithName(OperatorIds.LEFT_SHIFT);
                break;
            case //$NON-NLS-1$
            592:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ShiftExpression_NotName ::= ShiftExpression_NotName...");
                }
                consumeBinaryExpression(OperatorIds.RIGHT_SHIFT);
                break;
            case //$NON-NLS-1$
            593:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ShiftExpression_NotName ::= Name RIGHT_SHIFT...");
                }
                consumeBinaryExpressionWithName(OperatorIds.RIGHT_SHIFT);
                break;
            case //$NON-NLS-1$
            594:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ShiftExpression_NotName ::= ShiftExpression_NotName...");
                }
                consumeBinaryExpression(OperatorIds.UNSIGNED_RIGHT_SHIFT);
                break;
            case //$NON-NLS-1$
            595:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ShiftExpression_NotName ::= Name UNSIGNED_RIGHT_SHIFT...");
                }
                consumeBinaryExpressionWithName(OperatorIds.UNSIGNED_RIGHT_SHIFT);
                break;
            case //$NON-NLS-1$
            597:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression_NotName ::= ShiftExpression_NotName");
                }
                consumeBinaryExpression(OperatorIds.LESS);
                break;
            case //$NON-NLS-1$
            598:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression_NotName ::= Name LESS...");
                }
                consumeBinaryExpressionWithName(OperatorIds.LESS);
                break;
            case //$NON-NLS-1$
            599:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression_NotName ::= ShiftExpression_NotName");
                }
                consumeBinaryExpression(OperatorIds.GREATER);
                break;
            case //$NON-NLS-1$
            600:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression_NotName ::= Name GREATER...");
                }
                consumeBinaryExpressionWithName(OperatorIds.GREATER);
                break;
            case //$NON-NLS-1$
            601:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.LESS_EQUAL);
                break;
            case //$NON-NLS-1$
            602:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression_NotName ::= Name LESS_EQUAL...");
                }
                consumeBinaryExpressionWithName(OperatorIds.LESS_EQUAL);
                break;
            case //$NON-NLS-1$
            603:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.GREATER_EQUAL);
                break;
            case //$NON-NLS-1$
            604:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("RelationalExpression_NotName ::= Name GREATER_EQUAL...");
                }
                consumeBinaryExpressionWithName(OperatorIds.GREATER_EQUAL);
                break;
            case //$NON-NLS-1$
            606:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InstanceofExpression_NotName ::= Name instanceof...");
                }
                consumeInstanceOfExpressionWithName(OperatorIds.INSTANCEOF);
                break;
            case //$NON-NLS-1$
            607:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InstanceofExpression_NotName ::=...");
                }
                consumeInstanceOfExpression(OperatorIds.INSTANCEOF);
                break;
            case //$NON-NLS-1$
            609:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EqualityExpression_NotName ::=...");
                }
                consumeEqualityExpression(OperatorIds.EQUAL_EQUAL);
                break;
            case //$NON-NLS-1$
            610:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EqualityExpression_NotName ::= Name EQUAL_EQUAL...");
                }
                consumeEqualityExpressionWithName(OperatorIds.EQUAL_EQUAL);
                break;
            case //$NON-NLS-1$
            611:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EqualityExpression_NotName ::=...");
                }
                consumeEqualityExpression(OperatorIds.NOT_EQUAL);
                break;
            case //$NON-NLS-1$
            612:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("EqualityExpression_NotName ::= Name NOT_EQUAL...");
                }
                consumeEqualityExpressionWithName(OperatorIds.NOT_EQUAL);
                break;
            case //$NON-NLS-1$
            614:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AndExpression_NotName ::= AndExpression_NotName AND...");
                }
                consumeBinaryExpression(OperatorIds.AND);
                break;
            case //$NON-NLS-1$
            615:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AndExpression_NotName ::= Name AND EqualityExpression");
                }
                consumeBinaryExpressionWithName(OperatorIds.AND);
                break;
            case //$NON-NLS-1$
            617:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExclusiveOrExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.XOR);
                break;
            case //$NON-NLS-1$
            618:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ExclusiveOrExpression_NotName ::= Name XOR AndExpression");
                }
                consumeBinaryExpressionWithName(OperatorIds.XOR);
                break;
            case //$NON-NLS-1$
            620:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InclusiveOrExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.OR);
                break;
            case //$NON-NLS-1$
            621:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("InclusiveOrExpression_NotName ::= Name OR...");
                }
                consumeBinaryExpressionWithName(OperatorIds.OR);
                break;
            case //$NON-NLS-1$
            623:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConditionalAndExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.AND_AND);
                break;
            case //$NON-NLS-1$
            624:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConditionalAndExpression_NotName ::= Name AND_AND...");
                }
                consumeBinaryExpressionWithName(OperatorIds.AND_AND);
                break;
            case //$NON-NLS-1$
            626:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConditionalOrExpression_NotName ::=...");
                }
                consumeBinaryExpression(OperatorIds.OR_OR);
                break;
            case //$NON-NLS-1$
            627:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConditionalOrExpression_NotName ::= Name OR_OR...");
                }
                consumeBinaryExpressionWithName(OperatorIds.OR_OR);
                break;
            case //$NON-NLS-1$
            629:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConditionalExpression_NotName ::=...");
                }
                consumeConditionalExpression(OperatorIds.QUESTIONCOLON);
                break;
            case //$NON-NLS-1$
            630:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("ConditionalExpression_NotName ::= Name QUESTION...");
                }
                consumeConditionalExpressionWithName(OperatorIds.QUESTIONCOLON);
                break;
            case //$NON-NLS-1$
            634:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationTypeDeclarationHeader ::= Modifiers AT...");
                }
                consumeAnnotationTypeDeclarationHeader();
                break;
            case //$NON-NLS-1$
            635:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationTypeDeclarationHeader ::= AT PushModifiers...");
                }
                consumeAnnotationTypeDeclarationHeader();
                break;
            case //$NON-NLS-1$
            636:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationTypeDeclaration ::=...");
                }
                consumeAnnotationTypeDeclaration();
                break;
            case //$NON-NLS-1$
            638:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationTypeMemberDeclarationsopt ::=");
                }
                consumeEmptyAnnotationTypeMemberDeclarationsopt();
                break;
            case //$NON-NLS-1$
            641:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationTypeMemberDeclarations ::=...");
                }
                consumeAnnotationTypeMemberDeclarations();
                break;
            case //$NON-NLS-1$
            642:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationTypeMemberDeclarationHeader ::= Modifiersopt");
                }
                consumeAnnotationTypeMemberDeclarationHeader();
                break;
            case //$NON-NLS-1$
            643:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationTypeMemberHeaderExtendedDims ::= Dimsopt");
                }
                consumeAnnotationTypeMemberHeaderExtendedDims();
                break;
            case //$NON-NLS-1$
            644:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("AnnotationTypeMemberDeclaration ::=...");
                }
                consumeAnnotationTypeMemberDeclaration();
                break;
            case //$NON-NLS-1$
            647:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("DefaultValueopt ::=");
                }
                consumeEmptyDefaultValue();
                break;
            case //$NON-NLS-1$
            653:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("NormalAnnotation ::= AT Name LPAREN MemberValuePairsopt");
                }
                consumeNormalAnnotation();
                break;
            case //$NON-NLS-1$
            654:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MemberValuePairsopt ::=");
                }
                consumeEmptyMemberValuePairsopt();
                break;
            case //$NON-NLS-1$
            657:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MemberValuePairs ::= MemberValuePairs COMMA...");
                }
                consumeMemberValuePairs();
                break;
            case //$NON-NLS-1$
            658:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MemberValuePair ::= SimpleName EQUAL MemberValue");
                }
                consumeMemberValuePair();
                break;
            case //$NON-NLS-1$
            660:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MemberValue ::= Name");
                }
                consumeMemberValueAsName();
                break;
            case //$NON-NLS-1$
            663:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MemberValueArrayInitializer ::= LBRACE PushLeftBrace...");
                }
                consumeMemberValueArrayInitializer();
                break;
            case //$NON-NLS-1$
            664:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MemberValueArrayInitializer ::= LBRACE PushLeftBrace...");
                }
                consumeMemberValueArrayInitializer();
                break;
            case //$NON-NLS-1$
            665:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MemberValueArrayInitializer ::= LBRACE PushLeftBrace...");
                }
                consumeEmptyMemberValueArrayInitializer();
                break;
            case //$NON-NLS-1$
            666:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MemberValueArrayInitializer ::= LBRACE PushLeftBrace...");
                }
                consumeEmptyMemberValueArrayInitializer();
                break;
            case //$NON-NLS-1$
            668:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MemberValues ::= MemberValues COMMA MemberValue");
                }
                consumeMemberValues();
                break;
            case //$NON-NLS-1$
            669:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("MarkerAnnotation ::= AT Name");
                }
                consumeMarkerAnnotation();
                break;
            case //$NON-NLS-1$
            670:
                //$NON-NLS-1$
                if (DEBUG) {
                    System.out.println("SingleMemberAnnotation ::= AT Name LPAREN MemberValue...");
                }
                consumeSingleMemberAnnotation();
                break;
        }
    }

    protected void consumeSimpleAssertStatement() {
        // AssertStatement ::= 'assert' Expression ';'
        this.expressionLengthPtr--;
        pushOnAstStack(new AssertStatement(this.expressionStack[this.expressionPtr--], this.intStack[this.intPtr--]));
    }

    protected void consumeSingleMemberAnnotation() {
        // SingleMemberAnnotation ::= '@' Name '(' MemberValue ')'
        SingleMemberAnnotation singleMemberAnnotation = null;
        int length = this.identifierLengthStack[this.identifierLengthPtr--];
        if (length == 1) {
            singleMemberAnnotation = new SingleMemberAnnotation(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--], this.intStack[this.intPtr--]);
        } else {
            char[][] tokens = new char[length][];
            this.identifierPtr -= length;
            long[] positions = new long[length];
            System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
            System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
            singleMemberAnnotation = new SingleMemberAnnotation(tokens, positions, this.intStack[this.intPtr--]);
        }
        singleMemberAnnotation.memberValue = this.expressionStack[this.expressionPtr--];
        this.expressionLengthPtr--;
        int sourceStart = singleMemberAnnotation.sourceStart;
        if (this.modifiersSourceStart < 0) {
            this.modifiersSourceStart = sourceStart;
        } else if (this.modifiersSourceStart > sourceStart) {
            this.modifiersSourceStart = sourceStart;
        }
        singleMemberAnnotation.declarationSourceEnd = this.rParenPos;
        pushOnExpressionStack(singleMemberAnnotation);
        if (options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfAnnotation(singleMemberAnnotation);
        }
    }

    protected void consumeSingleStaticImportDeclarationName() {
        // SingleTypeImportDeclarationName ::= 'import' 'static' Name
        /* push an ImportRef build from the last name 
	stored in the identifier stack. */
        ImportReference impt;
        int length;
        char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        pushOnAstStack(impt = new ImportReference(tokens, positions, false, AccStatic));
        this.modifiers = AccDefault;
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
        if (this.options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            // convert the static import reference to a non-static importe reference
            impt.modifiers = AccDefault;
            this.problemReporter().invalidUsageOfStaticImports(impt);
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
        pushOnAstStack(impt = new ImportReference(tokens, positions, false, AccDefault));
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
        // break pushs a position on this.intStack in case there is no label
        pushOnAstStack(new BreakStatement(null, this.intStack[this.intPtr--], this.endPosition));
    }

    protected void consumeStatementBreakWithLabel() {
        // BreakStatement ::= 'break' Identifier ';'
        // break pushs a position on this.intStack in case there is no label
        pushOnAstStack(new BreakStatement(this.identifierStack[this.identifierPtr--], this.intStack[this.intPtr--], this.endPosition));
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
        pushOnAstStack(new ContinueStatement(null, this.intStack[this.intPtr--], this.endPosition));
    }

    protected void consumeStatementContinueWithLabel() {
        // ContinueStatement ::= 'continue' Identifier ';'
        // continue pushs a position on this.intStack in case there is no label
        pushOnAstStack(new ContinueStatement(this.identifierStack[this.identifierPtr--], this.intStack[this.intPtr--], this.endPosition));
        this.identifierLengthPtr--;
    }

    protected void consumeStatementDo() {
        // DoStatement ::= 'do' Statement 'while' '(' Expression ')' ';'
        //the 'while' pushes a value on this.intStack that we need to remove
        this.intPtr--;
        Statement statement = (Statement) this.astStack[this.astPtr];
        this.expressionLengthPtr--;
        this.astStack[this.astPtr] = new DoStatement(this.expressionStack[this.expressionPtr--], statement, this.intStack[this.intPtr--], this.endPosition);
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
        Statement stmt = (Statement) this.astStack[this.astPtr];
        this.astStack[this.astPtr] = new LabeledStatement(this.identifierStack[this.identifierPtr], stmt, (int) (this.identifierPositionStack[this.identifierPtr--] >>> 32), this.endStatementPosition);
        this.identifierLengthPtr--;
    }

    protected void consumeStatementReturn() {
        if (this.expressionLengthStack[this.expressionLengthPtr--] != 0) {
            pushOnAstStack(new ReturnStatement(this.expressionStack[this.expressionPtr--], this.intStack[this.intPtr--], this.endPosition));
        } else {
            pushOnAstStack(new ReturnStatement(null, this.intStack[this.intPtr--], this.endPosition));
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
            switchStatement.bits |= ASTNode.UndocumentedEmptyBlockMASK;
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
        resetModifiers();
    }

    protected void consumeStatementThrow() {
        // ThrowStatement ::= 'throw' Expression ';'
        this.expressionLengthPtr--;
        pushOnAstStack(new ThrowStatement(this.expressionStack[this.expressionPtr--], this.intStack[this.intPtr--]));
    }

    protected void consumeStatementTry(boolean withFinally) {
        //TryStatement ::= 'try'  Block Catches
        //TryStatement ::= 'try'  Block Catchesopt Finally
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
        // TypeImportOnDemandDeclarationName ::= 'import' 'static' Name '.' '*'
        /* push an ImportRef build from the last name 
	stored in the identifier stack. */
        ImportReference impt;
        int length;
        char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        pushOnAstStack(impt = new ImportReference(tokens, positions, true, AccStatic));
        this.modifiers = AccDefault;
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
        if (options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            // convert the static import reference to a non-static importe reference
            impt.modifiers = AccDefault;
            this.problemReporter().invalidUsageOfStaticImports(impt);
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
            block.bits &= ~ASTNode.UndocumentedEmptyBlockMASK;
        Initializer initializer = new Initializer(block, AccStatic);
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
        /* remember the last consumed value */
        /* try to minimize the number of build values */
        checkNonExternalizedStringLiteral();
        //System.out.println(this.scanner.toStringAction(type));
        switch(type) {
            case TokenNameIdentifier:
                pushIdentifier();
                if (this.scanner.useAssertAsAnIndentifier && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
                    long positions = this.identifierPositionStack[this.identifierPtr];
                    problemReporter().useAssertAsAnIdentifier((int) (positions >>> 32), (int) positions);
                }
                if (this.scanner.useEnumAsAnIndentifier && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
                    long positions = this.identifierPositionStack[this.identifierPtr];
                    problemReporter().useEnumAsAnIdentifier((int) (positions >>> 32), (int) positions);
                }
                break;
            case TokenNameinterface:
                adjustInterfaceModifiers();
                //'class' is pushing two int (positions) on the stack ==> 'interface' needs to do it too....
                pushOnIntStack(this.scanner.currentPosition - 1);
                pushOnIntStack(this.scanner.startPosition);
                break;
            case TokenNameabstract:
                checkAndSetModifiers(AccAbstract);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNamestrictfp:
                checkAndSetModifiers(AccStrictfp);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNamefinal:
                checkAndSetModifiers(AccFinal);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNamenative:
                checkAndSetModifiers(AccNative);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNameprivate:
                checkAndSetModifiers(AccPrivate);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNameprotected:
                checkAndSetModifiers(AccProtected);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNamepublic:
                checkAndSetModifiers(AccPublic);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNametransient:
                checkAndSetModifiers(AccTransient);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNamevolatile:
                checkAndSetModifiers(AccVolatile);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNamestatic:
                checkAndSetModifiers(AccStatic);
                pushOnExpressionStackLengthStack(0);
                break;
            case TokenNamesynchronized:
                this.synchronizedBlockSourceStart = this.scanner.startPosition;
                checkAndSetModifiers(AccSynchronized);
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
                pushOnExpressionStack(new IntLiteral(this.scanner.getCurrentTokenSource(), this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            case TokenNameLongLiteral:
                pushOnExpressionStack(new LongLiteral(this.scanner.getCurrentTokenSource(), this.scanner.startPosition, this.scanner.currentPosition - 1));
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
                StringLiteral stringLiteral = new StringLiteral(this.scanner.getCurrentTokenSourceString(), this.scanner.startPosition, this.scanner.currentPosition - 1);
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
            case TokenNameassert:
            case TokenNameimport:
            case TokenNamepackage:
            case TokenNamethrow:
            case TokenNamedo:
            case TokenNameif:
            case TokenNamefor:
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
            case TokenNamePLUS:
            case TokenNameMINUS:
            case TokenNameNOT:
            case TokenNameTWIDDLE:
            case TokenNameLBRACE:
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
            case TokenNameAT:
                pushOnIntStack(this.scanner.startPosition);
                break;
            case TokenNameQUESTION:
                pushOnIntStack(this.scanner.startPosition);
                pushOnIntStack(this.scanner.currentPosition - 1);
                break;
            // TODO (olivier) bug 67790 remove once DOMParser is activated
            case TokenNameLESS:
                pushOnIntStack(this.scanner.startPosition);
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
        // TODO (olivier) bug 67790 remove once DOMParser is activated
        intPtr--;
    }

    protected void consumeTypeArgumentReferenceType2() {
        concatGenericsLists();
        pushOnGenericsStack(getTypeReference(0));
        // TODO (olivier) bug 67790 remove once DOMParser is activated
        intPtr--;
    }

    protected void consumeTypeArguments() {
        concatGenericsLists();
        // TODO (olivier) bug 67790 remove once DOMParser is activated
        intPtr--;
        if (options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            int length = this.genericsLengthStack[this.genericsLengthPtr];
            this.problemReporter().invalidUsageOfTypeArguments((TypeReference) this.genericsStack[this.genericsPtr - length + 1], (TypeReference) this.genericsStack[this.genericsPtr]);
        }
    }

    protected void consumeTypeDeclarations() {
        // TypeDeclarations ::= TypeDeclarations TypeDeclaration
        concatNodeLists();
    }

    protected void consumeTypeImportOnDemandDeclarationName() {
        // TypeImportOnDemandDeclarationName ::= 'import' Name '.' '*'
        /* push an ImportRef build from the last name 
	stored in the identifier stack. */
        ImportReference impt;
        int length;
        char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        pushOnAstStack(impt = new ImportReference(tokens, positions, true, AccDefault));
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

    protected void consumeTypeParameterHeader() {
        //TypeParameterHeader ::= Identifier
        TypeParameter typeParameter = new TypeParameter();
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
        superType.bits |= ASTNode.IsSuperType;
        typeParameter.bounds = bounds;
        for (int i = 0, max = bounds.length; i < max; i++) {
            bounds[i].bits |= ASTNode.IsSuperType;
        }
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
        // TODO (olivier) bug 67790 remove once DOMParser is activated
        intPtr--;
        if (options.sourceLevel < ClassFileConstants.JDK1_5 && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            int length = this.genericsLengthStack[this.genericsLengthPtr];
            this.problemReporter().invalidUsageOfTypeParameters((TypeParameter) this.genericsStack[genericsPtr - length + 1], (TypeParameter) this.genericsStack[genericsPtr]);
        }
    }

    protected void consumeTypeParameterWithExtends() {
        //TypeParameter ::= TypeParameterHeader 'extends' ReferenceType
        TypeReference superType = getTypeReference(this.intStack[this.intPtr--]);
        TypeParameter typeParameter = (TypeParameter) this.genericsStack[this.genericsPtr];
        typeParameter.declarationSourceEnd = superType.sourceEnd;
        typeParameter.type = superType;
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
        superType.bits |= ASTNode.IsSuperType;
        typeParameter.bounds = bounds;
        typeParameter.declarationSourceEnd = bounds[additionalBoundsLength - 1].sourceEnd;
        for (int i = 0, max = bounds.length; i < max; i++) {
            bounds[i].bits |= ASTNode.IsSuperType;
        }
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
            if ((exp instanceof IntLiteral) && (((IntLiteral) exp).mayRepresentMIN_VALUE())) {
                r = this.expressionStack[this.expressionPtr] = new IntLiteralMinValue();
            } else {
                if ((exp instanceof LongLiteral) && (((LongLiteral) exp).mayRepresentMIN_VALUE())) {
                    r = this.expressionStack[this.expressionPtr] = new LongLiteralMinValue();
                } else {
                    r = this.expressionStack[this.expressionPtr] = new UnaryExpression(exp, op);
                }
            }
        } else {
            r = this.expressionStack[this.expressionPtr] = new UnaryExpression(exp, op);
        }
        r.sourceStart = this.intStack[this.intPtr--];
        r.sourceEnd = exp.sourceEnd;
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
        pushOnGenericsStack(wildcard);
    }

    protected void consumeWildcard1() {
        final Wildcard wildcard = new Wildcard(Wildcard.UNBOUND);
        wildcard.sourceEnd = this.intStack[this.intPtr--];
        wildcard.sourceStart = this.intStack[this.intPtr--];
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
        this.genericsStack[this.genericsPtr] = wildcard;
    }

    protected void consumeWildcardBounds2Extends() {
        Wildcard wildcard = new Wildcard(Wildcard.EXTENDS);
        wildcard.bound = (TypeReference) this.genericsStack[this.genericsPtr];
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        // remove end position of the '?'
        this.intPtr--;
        wildcard.sourceStart = this.intStack[this.intPtr--];
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
        this.genericsStack[this.genericsPtr] = wildcard;
    }

    protected void consumeWildcardBounds3Extends() {
        Wildcard wildcard = new Wildcard(Wildcard.EXTENDS);
        wildcard.bound = (TypeReference) this.genericsStack[this.genericsPtr];
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        // remove end position of the '?'
        this.intPtr--;
        wildcard.sourceStart = this.intStack[this.intPtr--];
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
        this.genericsStack[this.genericsPtr] = wildcard;
    }

    protected void consumeWildcardBoundsExtends() {
        Wildcard wildcard = new Wildcard(Wildcard.EXTENDS);
        wildcard.bound = getTypeReference(this.intStack[this.intPtr--]);
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        // remove end position of the '?'
        this.intPtr--;
        wildcard.sourceStart = this.intStack[this.intPtr--];
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
        return m;
    }

    protected TypeReference copyDims(TypeReference typeRef, int dim) {
        return typeRef.copyDims(dim);
    }

    protected FieldDeclaration createFieldDeclaration(char[] fieldDeclarationName, int sourceStart, int sourceEnd) {
        return new FieldDeclaration(fieldDeclarationName, sourceStart, sourceEnd);
    }

    protected LocalDeclaration createLocalDeclaration(char[] localDeclarationName, int sourceStart, int sourceEnd) {
        return new LocalDeclaration(localDeclarationName, sourceStart, sourceEnd);
    }

    public CompilationUnitDeclaration dietParse(ICompilationUnit sourceUnit, CompilationResult compilationResult) {
        CompilationUnitDeclaration parsedUnit;
        boolean old = this.diet;
        try {
            this.diet = true;
            parsedUnit = parse(sourceUnit, compilationResult);
        } finally {
            this.diet = old;
        }
        return parsedUnit;
    }

    protected void dispatchDeclarationInto(int length) {
        if (length == 0)
            return;
        //plus one -- see <HERE>
        int[] flag = new int[length + 1];
        int size1 = 0, size2 = 0, size3 = 0, size4 = 0;
        for (int i = length - 1; i >= 0; i--) {
            ASTNode astNode = this.astStack[this.astPtr--];
            if (astNode instanceof AbstractMethodDeclaration) {
                //methods and constructors have been regrouped into one single list
                flag[i] = 3;
                size2++;
            } else if (astNode instanceof EnumDeclaration) {
                flag[i] = 2;
                size4++;
            } else if (astNode instanceof TypeDeclaration) {
                flag[i] = 4;
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
        }
        if (size3 != 0) {
            typeDecl.memberTypes = new TypeDeclaration[size3];
        }
        if (size4 != 0) {
            typeDecl.enums = new EnumDeclaration[size4];
        }
        //arrays fill up
        size1 = size2 = size3 = size4 = 0;
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
                        size4 += (length2 = end - start);
                        System.arraycopy(this.astStack, this.astPtr + start + 1, typeDecl.enums, size4 - length2, length2);
                        break;
                    case 3:
                        size2 += (length2 = end - start);
                        System.arraycopy(this.astStack, this.astPtr + start + 1, typeDecl.methods, size2 - length2, length2);
                        break;
                    case 4:
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
        int size1 = 0, size2 = 0, size3 = 0, size4 = 0;
        for (int i = length - 1; i >= 0; i--) {
            ASTNode astNode = this.astStack[this.astPtr--];
            if (astNode instanceof AbstractMethodDeclaration) {
                //methods and constructors have been regrouped into one single list
                flag[i] = 3;
                size2++;
            } else if (astNode instanceof EnumConstant) {
                // enum constants
                flag[i] = 2;
                size4++;
            } else if (astNode instanceof TypeDeclaration) {
                flag[i] = 4;
                size3++;
            } else if (astNode instanceof FieldDeclaration) {
                flag[i] = 1;
                size1++;
            }
        }
        //arrays creation
        EnumDeclaration enumDeclaration = (EnumDeclaration) this.astStack[this.astPtr];
        if (size1 != 0) {
            enumDeclaration.fields = new FieldDeclaration[size1];
        }
        if (size2 != 0) {
            enumDeclaration.methods = new AbstractMethodDeclaration[size2];
        }
        if (size3 != 0) {
            enumDeclaration.memberTypes = new TypeDeclaration[size3];
        }
        if (size4 != 0) {
            enumDeclaration.enumConstants = new EnumConstant[size4];
        }
        //arrays fill up
        size1 = size2 = size3 = size4 = 0;
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
                        size4 += (length2 = end - start);
                        System.arraycopy(this.astStack, this.astPtr + start + 1, enumDeclaration.enumConstants, size4 - length2, length2);
                        break;
                    case 3:
                        size2 += (length2 = end - start);
                        System.arraycopy(this.astStack, this.astPtr + start + 1, enumDeclaration.methods, size2 - length2, length2);
                        break;
                    case 4:
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
    }

    protected CompilationUnitDeclaration endParse(int act) {
        this.lastAct = act;
        if (this.currentElement != null) {
            this.currentElement.topElement().updateParseTree();
            if (VERBOSE_RECOVERY) {
                //$NON-NLS-1$
                System.out.print(Util.bind("parser.syntaxRecovery"));
                //$NON-NLS-1$
                System.out.println("--------------------------");
                System.out.println(this.compilationUnit);
                //$NON-NLS-1$
                System.out.println("----------------------------------");
            }
        } else {
            if (this.diet & VERBOSE_RECOVERY) {
                //$NON-NLS-1$
                System.out.print(Util.bind("parser.regularParse"));
                //$NON-NLS-1$
                System.out.println("--------------------------");
                System.out.println(this.compilationUnit);
                //$NON-NLS-1$
                System.out.println("----------------------------------");
            }
        }
        persistLineSeparatorPositions();
        for (int i = 0; i < this.scanner.foundTaskCount; i++) {
            problemReporter().task(new String(this.scanner.foundTaskTags[i]), new String(this.scanner.foundTaskMessages[i]), this.scanner.foundTaskPriorities[i] == null ? null : new String(this.scanner.foundTaskPriorities[i]), this.scanner.foundTaskPositions[i][0], this.scanner.foundTaskPositions[i][1]);
        }
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
                if (this.scanner.getLineNumber(position) == this.scanner.getLineNumber(immediateCommentEnd)) {
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
        if (// move valid comment infos, overriding obsolete comment infos
        validCount > 0) {
            System.arraycopy(this.scanner.commentStarts, index + 1, this.scanner.commentStarts, 0, validCount);
            System.arraycopy(this.scanner.commentStops, index + 1, this.scanner.commentStops, 0, validCount);
        }
        this.scanner.commentPtr = validCount - 1;
        return position;
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
        for (int i = 0, max = this.scanner.commentPtr; i <= max; i++) {
            // javadoc only (non javadoc comment have negative end positions.)
            if (this.scanner.commentStops[i] > 0) {
                javadocCount++;
            }
        }
        if (javadocCount == 0)
            return null;
        int[] positions = new int[2 * javadocCount];
        int index = 0;
        for (int i = 0, max = this.scanner.commentPtr; i <= max; i++) {
            // javadoc only (non javadoc comment have negative end positions.)
            if (this.scanner.commentStops[i] > 0) {
                positions[index++] = this.scanner.commentStarts[i];
                //stop is one over			
                positions[index++] = this.scanner.commentStops[i] - 1;
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
        this.scanner.setSource(compilationResult);
        if (this.javadocParser != null && this.javadocParser.checkDocComment) {
            char[] contents = compilationResult.compilationUnit.getContents();
            this.javadocParser.scanner.setSource(contents);
        }
        if (unit.types != null) {
            for (int i = unit.types.length; --i >= 0; ) unit.types[i].parseMethod(this, unit);
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
                if (!(((c1 = Character.getNumericValue(comment[index[0]++])) > 15 || c1 < 0) || ((c2 = Character.getNumericValue(comment[index[0]++])) > 15 || c2 < 0) || ((c3 = Character.getNumericValue(comment[index[0]++])) > 15 || c3 < 0) || ((c4 = Character.getNumericValue(comment[index[0]++])) > 15 || c4 < 0))) {
                    nextCharacter = (char) (((c1 * 16 + c2) * 16 + c3) * 16 + c4);
                }
                break;
        }
        return nextCharacter;
    }

    protected Expression getTypeReference(Expression exp) {
        exp.bits &= ~ASTNode.RestrictiveFlagMASK;
        exp.bits |= TYPE;
        return exp;
    }

    protected TypeReference getTypeReference(int dim) {
        TypeReference ref;
        int length = this.identifierLengthStack[this.identifierLengthPtr--];
        if (length < 0) {
            ref = TypeReference.baseTypeReference(-length, dim);
            ref.sourceStart = this.intStack[this.intPtr--];
            if (dim == 0) {
                ref.sourceEnd = this.intStack[this.intPtr--];
            } else {
                this.intPtr--;
                ref.sourceEnd = this.endPosition;
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
                    ref = new ArrayTypeReference(this.identifierStack[this.identifierPtr], dim, this.identifierPositionStack[this.identifierPtr--]);
                    ref.sourceEnd = this.endPosition;
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
                    ref = new ArrayQualifiedTypeReference(tokens, dim, positions);
                    ref.sourceEnd = this.endPosition;
                }
            }
        }
        return ref;
    }

    protected TypeReference getTypeReferenceForGenericType(int dim, int identifierLength, int numberOfIdentifiers) {
        if (identifierLength == 1 && numberOfIdentifiers == 1) {
            int currentTypeArgumentsLength = this.genericsLengthStack[this.genericsLengthPtr--];
            TypeReference[] typeArguments = new TypeReference[currentTypeArgumentsLength];
            this.genericsPtr -= currentTypeArgumentsLength;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments, 0, currentTypeArgumentsLength);
            return new ParameterizedSingleTypeReference(this.identifierStack[this.identifierPtr], typeArguments, dim, this.identifierPositionStack[this.identifierPtr--]);
        } else {
            TypeReference[][] typeArguments = new TypeReference[numberOfIdentifiers][];
            char[][] tokens = new char[numberOfIdentifiers][];
            long[] positions = new long[numberOfIdentifiers];
            int index = numberOfIdentifiers;
            int currentIdentifiersLength = identifierLength;
            while (index > 0) {
                int currentTypeArgumentsLength = this.genericsLengthStack[this.genericsLengthPtr--];
                if (currentTypeArgumentsLength != 0) {
                    this.genericsPtr -= currentTypeArgumentsLength;
                    System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments[index - 1] = new TypeReference[currentTypeArgumentsLength], 0, currentTypeArgumentsLength);
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
            return new ParameterizedQualifiedTypeReference(tokens, typeArguments, dim, positions);
        }
    }

    protected NameReference getUnspecifiedReference() {
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
        int length;
        NameReference ref;
        if ((length = this.identifierLengthStack[this.identifierLengthPtr--]) == 1) {
            ref = new SingleNameReference(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--]);
            ref.bits &= ~ASTNode.RestrictiveFlagMASK;
            ref.bits |= LOCAL | FIELD;
            return ref;
        }
        char[][] tokens = new char[length][];
        this.identifierPtr -= length;
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        long[] positions = new long[length];
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        ref = new QualifiedNameReference(tokens, positions, (int) (this.identifierPositionStack[this.identifierPtr + 1] >> 32), (int) this.identifierPositionStack[this.identifierPtr + length]);
        ref.bits &= ~ASTNode.RestrictiveFlagMASK;
        ref.bits |= LOCAL | FIELD;
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
        this.scanner.currentLine = null;
    }

    public void goForExpression() {
        this.firstToken = TokenNameREMAINDER;
        this.scanner.recordLineSeparator = true;
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
        this.firstToken = TokenNameUNSIGNED_RIGHT_SHIFT;
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

    protected void ignoreExpressionAssignment() {
        this.intPtr--;
        ArrayInitializer arrayInitializer = (ArrayInitializer) this.expressionStack[this.expressionPtr--];
        this.expressionLengthPtr--;
        problemReporter().arrayConstantsOnlyInArrayInitializers(arrayInitializer.sourceStart, arrayInitializer.sourceEnd);
    }

    protected void ignoreInterfaceDeclaration() {
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            dispatchDeclarationInto(length);
        }
        flushCommentsDefinedPriorTo(this.endStatementPosition);
        TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
        typeDecl.bodyEnd = this.endStatementPosition;
        problemReporter().cannotDeclareLocalInterface(typeDecl.name, typeDecl.sourceStart, typeDecl.sourceEnd);
        markInitializersWithLocalType(typeDecl);
        this.astPtr--;
        pushOnAstLengthStack(-1);
        concatNodeLists();
    }

    protected void ignoreInvalidConstructorDeclaration(boolean hasBody) {
        if (hasBody) {
            this.intPtr--;
        }
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
            constructorDeclaration.modifiers |= AccSemicolonBody;
        }
    }

    protected void ignoreMethodBody() {
        this.intPtr--;
        this.realBlockPtr--;
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.astPtr -= length;
        }
        MethodDeclaration md = (MethodDeclaration) this.astStack[this.astPtr];
        md.bodyEnd = this.endPosition;
        md.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
        problemReporter().abstractMethodNeedingNoBody(md);
    }

    public void initialize() {
        this.astPtr = -1;
        this.astLengthPtr = -1;
        this.expressionPtr = -1;
        this.expressionLengthPtr = -1;
        this.identifierPtr = -1;
        this.identifierLengthPtr = -1;
        this.intPtr = -1;
        this.nestedMethod[this.nestedType = 0] = 0;
        this.variablesCounter[this.nestedType] = 0;
        this.dimensions = 0;
        this.realBlockPtr = -1;
        this.compilationUnit = null;
        this.referenceContext = null;
        this.endStatementPosition = 0;
        int astLength = this.astStack.length;
        if (this.noAstNodes.length < astLength) {
            this.noAstNodes = new ASTNode[astLength];
        }
        System.arraycopy(this.noAstNodes, 0, this.astStack, 0, astLength);
        int expressionLength = this.expressionStack.length;
        if (this.noExpressions.length < expressionLength) {
            this.noExpressions = new Expression[expressionLength];
        }
        System.arraycopy(this.noExpressions, 0, this.expressionStack, 0, expressionLength);
        this.scanner.commentPtr = -1;
        this.scanner.foundTaskCount = 0;
        this.scanner.eofPosition = Integer.MAX_VALUE;
        this.scanner.wasNonExternalizedStringLiteral = false;
        this.scanner.nonNLSStrings = null;
        this.scanner.currentLine = null;
        resetModifiers();
        this.lastCheckPoint = -1;
        this.currentElement = null;
        this.restartRecovery = false;
        this.hasReportedError = false;
        this.recoveredStaticInitializerStart = 0;
        this.lastIgnoredToken = -1;
        this.lastErrorEndPosition = -1;
        this.lastErrorEndPositionBeforeRecovery = -1;
        this.listLength = 0;
        this.listTypeParameterLength = 0;
        this.rBraceStart = 0;
        this.rBraceEnd = 0;
        this.rBraceSuccessorStart = 0;
        this.genericsIdentifiersLengthPtr = -1;
        this.genericsLengthPtr = -1;
        this.genericsPtr = -1;
    }

    public void initializeScanner() {
        this.scanner = new Scanner(false, false, this.options.getSeverity(CompilerOptions.NonExternalizedString) != ProblemSeverities.Ignore, this.options.sourceLevel, this.options.taskTags, this.options.taskPriorites, this.options.isTaskCaseSensitive);
    }

    public void jumpOverMethodBody() {
        if (this.diet && (this.dietInt == 0))
            this.scanner.diet = true;
    }

    protected void markEnclosingMemberWithLocalType() {
        if (this.currentElement != null)
            return;
        for (int i = this.astPtr; i >= 0; i--) {
            ASTNode node = this.astStack[i];
            if (node instanceof AbstractMethodDeclaration || node instanceof FieldDeclaration || node instanceof TypeDeclaration) {
                node.bits |= ASTNode.HasLocalTypeMASK;
                return;
            }
        }
        if (this.referenceContext instanceof AbstractMethodDeclaration || this.referenceContext instanceof TypeDeclaration) {
            ((ASTNode) this.referenceContext).bits |= ASTNode.HasLocalTypeMASK;
        }
    }

    protected void markInitializersWithLocalType(TypeDeclaration type) {
        if (type.fields == null || (type.bits & ASTNode.HasLocalTypeMASK) == 0)
            return;
        for (int i = 0, length = type.fields.length; i < length; i++) {
            FieldDeclaration field = type.fields[i];
            if (field instanceof Initializer) {
                field.bits |= ASTNode.HasLocalTypeMASK;
            }
        }
    }

    protected boolean moveRecoveryCheckpoint() {
        int pos = this.lastCheckPoint;
        this.scanner.startPosition = pos;
        this.scanner.currentPosition = pos;
        this.scanner.diet = false;
        if (this.restartRecovery) {
            this.lastIgnoredToken = -1;
            this.scanner.currentLine = null;
            return true;
        }
        this.lastIgnoredToken = this.nextIgnoredToken;
        this.nextIgnoredToken = -1;
        do {
            try {
                this.nextIgnoredToken = this.scanner.getNextToken();
                if (this.scanner.currentPosition == this.scanner.startPosition) {
                    this.scanner.currentPosition++;
                    this.nextIgnoredToken = -1;
                }
            } catch (InvalidInputException e) {
                pos = this.scanner.currentPosition;
            }
        } while (this.nextIgnoredToken < 0);
        if (this.nextIgnoredToken == TokenNameEOF) {
            if (this.currentToken == TokenNameEOF) {
                this.scanner.currentLine = null;
                return false;
            }
        }
        this.lastCheckPoint = this.scanner.currentPosition;
        this.scanner.startPosition = pos;
        this.scanner.currentPosition = pos;
        this.scanner.commentPtr = -1;
        this.scanner.foundTaskCount = 0;
        this.scanner.currentLine = null;
        return true;
    }

    protected MessageSend newMessageSend() {
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
        this.astLengthStack[--this.astLengthPtr]++;
    }

    protected void parse() {
        if (DEBUG)
            System.out.println("-- ENTER INSIDE PARSE METHOD --");
        boolean isDietParse = this.diet;
        int oldFirstToken = getFirstToken();
        this.hasError = false;
        this.hasReportedError = false;
        int act = START_STATE;
        this.stateStackTop = -1;
        this.currentToken = getFirstToken();
        ProcessTerminals: for (; ; ) {
            int stackLength = this.stack.length;
            if (++this.stateStackTop >= stackLength) {
                System.arraycopy(this.stack, 0, this.stack = new int[stackLength + StackIncrement], 0, stackLength);
            }
            this.stack[this.stateStackTop] = act;
            act = tAction(act, this.currentToken);
            if (act == ERROR_ACTION || this.restartRecovery) {
                int errorPos = this.scanner.currentPosition;
                if (!this.hasReportedError) {
                    this.hasError = true;
                }
                if (resumeOnSyntaxError()) {
                    if (act == ERROR_ACTION)
                        this.lastErrorEndPosition = errorPos;
                    act = START_STATE;
                    this.stateStackTop = -1;
                    this.currentToken = getFirstToken();
                    continue ProcessTerminals;
                }
                act = ERROR_ACTION;
                break ProcessTerminals;
            }
            if (act <= NUM_RULES) {
                this.stateStackTop--;
            } else if (act > ERROR_ACTION) {
                consumeToken(this.currentToken);
                if (this.currentElement != null)
                    this.recoveryTokenCheck();
                try {
                    this.currentToken = this.scanner.getNextToken();
                } catch (InvalidInputException e) {
                    if (!this.hasReportedError) {
                        this.problemReporter().scannerError(this, e.getMessage());
                        this.hasReportedError = true;
                    }
                    this.lastCheckPoint = this.scanner.currentPosition;
                    this.restartRecovery = true;
                }
                act -= ERROR_ACTION;
            } else {
                if (act < ACCEPT_ACTION) {
                    consumeToken(this.currentToken);
                    if (this.currentElement != null)
                        this.recoveryTokenCheck();
                    try {
                        this.currentToken = this.scanner.getNextToken();
                    } catch (InvalidInputException e) {
                        if (!this.hasReportedError) {
                            this.problemReporter().scannerError(this, e.getMessage());
                            this.hasReportedError = true;
                        }
                        this.lastCheckPoint = this.scanner.currentPosition;
                        this.restartRecovery = true;
                    }
                    continue ProcessTerminals;
                }
                break ProcessTerminals;
            }
            ProcessNonTerminals: do {
                consumeRule(act);
                this.stateStackTop -= (rhs[act] - 1);
                act = ntAction(this.stack[this.stateStackTop], lhs[act]);
            } while (act <= NUM_RULES);
        }
        endParse(act);
        if (this.reportSyntaxErrorIsRequired && this.hasError) {
            reportSyntaxErrors(isDietParse, oldFirstToken);
        }
        if (DEBUG)
            System.out.println("-- EXIT FROM PARSE METHOD --");
    }

    public void parse(ConstructorDeclaration cd, CompilationUnitDeclaration unit) {
        parse(cd, unit, false);
    }

    public void parse(ConstructorDeclaration cd, CompilationUnitDeclaration unit, boolean recordLineSeparator) {
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
        }
        checkNonNLSAfterBodyEnd(cd.declarationSourceEnd);
        if (this.lastAct == ERROR_ACTION) {
            initialize();
            return;
        }
        cd.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.astPtr -= length;
            if (this.astStack[this.astPtr + 1] instanceof ExplicitConstructorCall) {
                System.arraycopy(this.astStack, this.astPtr + 2, cd.statements = new Statement[length - 1], 0, length - 1);
                cd.constructorCall = (ExplicitConstructorCall) this.astStack[this.astPtr + 1];
            } else {
                System.arraycopy(this.astStack, this.astPtr + 1, cd.statements = new Statement[length], 0, length);
                cd.constructorCall = SuperReference.implicitSuperConstructorCall();
            }
        } else {
            cd.constructorCall = SuperReference.implicitSuperConstructorCall();
            if (!containsComment(cd.bodyStart, cd.bodyEnd)) {
                cd.bits |= ASTNode.UndocumentedEmptyBlockMASK;
            }
        }
        if (cd.constructorCall.sourceEnd == 0) {
            cd.constructorCall.sourceEnd = cd.sourceEnd;
            cd.constructorCall.sourceStart = cd.sourceStart;
        }
    }

    public void parse(FieldDeclaration field, TypeDeclaration type, CompilationUnitDeclaration unit, char[] initializationSource) {
        initialize();
        goForExpression();
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
            return;
        }
        field.initialization = this.expressionStack[this.expressionPtr];
        if ((type.bits & ASTNode.HasLocalTypeMASK) != 0) {
            field.bits |= ASTNode.HasLocalTypeMASK;
        }
    }

    public CompilationUnitDeclaration parse(ICompilationUnit sourceUnit, CompilationResult compilationResult) {
        return parse(sourceUnit, compilationResult, -1, -1);
    }

    public CompilationUnitDeclaration parse(ICompilationUnit sourceUnit, CompilationResult compilationResult, int start, int end) {
        CompilationUnitDeclaration unit;
        try {
            initialize();
            goForCompilationUnit();
            char[] contents = sourceUnit.getContents();
            this.scanner.setSource(contents);
            if (end != -1)
                this.scanner.resetTo(start, end);
            if (this.javadocParser != null && this.javadocParser.checkDocComment) {
                this.javadocParser.scanner.setSource(contents);
                if (end != -1) {
                    this.javadocParser.scanner.resetTo(start, end);
                }
            }
            this.referenceContext = this.compilationUnit = new CompilationUnitDeclaration(this.problemReporter, compilationResult, this.scanner.source.length);
            parse();
        } finally {
            unit = this.compilationUnit;
            this.compilationUnit = null;
            if (!this.diet)
                unit.bits |= ASTNode.HasAllMethodBodies;
        }
        return unit;
    }

    public void parse(Initializer initializer, TypeDeclaration type, CompilationUnitDeclaration unit) {
        initialize();
        goForBlockStatementsopt();
        this.nestedMethod[this.nestedType]++;
        pushOnRealBlockStack(0);
        this.referenceContext = type;
        this.compilationUnit = unit;
        this.scanner.resetTo(initializer.bodyStart, initializer.bodyEnd);
        try {
            parse();
        } catch (AbortCompilation ex) {
            this.lastAct = ERROR_ACTION;
        } finally {
            this.nestedMethod[this.nestedType]--;
        }
        checkNonNLSAfterBodyEnd(initializer.declarationSourceEnd);
        if (this.lastAct == ERROR_ACTION) {
            return;
        }
        initializer.block.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) > 0) {
            System.arraycopy(this.astStack, (this.astPtr -= length) + 1, initializer.block.statements = new Statement[length], 0, length);
        } else {
            if (!containsComment(initializer.block.sourceStart, initializer.block.sourceEnd)) {
                initializer.block.bits |= ASTNode.UndocumentedEmptyBlockMASK;
            }
        }
        if ((type.bits & ASTNode.HasLocalTypeMASK) != 0) {
            initializer.bits |= ASTNode.HasLocalTypeMASK;
        }
    }

    public void parse(MethodDeclaration md, CompilationUnitDeclaration unit) {
        if (md.isAbstract())
            return;
        if (md.isNative())
            return;
        if ((md.modifiers & AccSemicolonBody) != 0)
            return;
        initialize();
        goForBlockStatementsopt();
        this.nestedMethod[this.nestedType]++;
        pushOnRealBlockStack(0);
        this.referenceContext = md;
        this.compilationUnit = unit;
        this.scanner.resetTo(md.bodyStart, md.bodyEnd);
        try {
            parse();
        } catch (AbortCompilation ex) {
            this.lastAct = ERROR_ACTION;
        } finally {
            this.nestedMethod[this.nestedType]--;
        }
        checkNonNLSAfterBodyEnd(md.declarationSourceEnd);
        if (this.lastAct == ERROR_ACTION) {
            return;
        }
        md.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            System.arraycopy(this.astStack, (this.astPtr -= length) + 1, md.statements = new Statement[length], 0, length);
        } else {
            if (!containsComment(md.bodyStart, md.bodyEnd)) {
                md.bits |= ASTNode.UndocumentedEmptyBlockMASK;
            }
        }
    }

    public ASTNode[] parseClassBodyDeclarations(char[] source, int offset, int length, CompilationUnitDeclaration unit) {
        initialize();
        goForClassBodyDeclarations();
        this.scanner.setSource(source);
        this.scanner.resetTo(offset, offset + length - 1);
        if (this.javadocParser != null && this.javadocParser.checkDocComment) {
            this.javadocParser.scanner.setSource(source);
            this.javadocParser.scanner.resetTo(offset, offset + length - 1);
        }
        this.nestedType = 1;
        this.referenceContext = unit;
        this.compilationUnit = unit;
        try {
            parse();
        } catch (AbortCompilation ex) {
            this.lastAct = ERROR_ACTION;
        }
        if (this.lastAct == ERROR_ACTION) {
            return null;
        }
        int astLength;
        if ((astLength = this.astLengthStack[this.astLengthPtr--]) != 0) {
            ASTNode[] result = new ASTNode[astLength];
            this.astPtr -= astLength;
            System.arraycopy(this.astStack, this.astPtr + 1, result, 0, astLength);
            return result;
        }
        return null;
    }

    public Expression parseExpression(char[] source, int offset, int length, CompilationUnitDeclaration unit) {
        initialize();
        goForExpression();
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

    public void persistLineSeparatorPositions() {
        if (this.scanner.recordLineSeparator) {
            this.compilationUnit.compilationResult.lineSeparatorPositions = this.scanner.getLineEnds();
        }
    }

    public ProblemReporter problemReporter() {
        if (this.scanner.recordLineSeparator) {
            this.compilationUnit.compilationResult.lineSeparatorPositions = this.scanner.getLineEnds();
        }
        this.problemReporter.referenceContext = this.referenceContext;
        return this.problemReporter;
    }

    protected void pushIdentifier() {
        int stackLength = this.identifierStack.length;
        if (++this.identifierPtr >= stackLength) {
            System.arraycopy(this.identifierStack, 0, this.identifierStack = new char[stackLength + 20][], 0, stackLength);
            System.arraycopy(this.identifierPositionStack, 0, this.identifierPositionStack = new long[stackLength + 20], 0, stackLength);
        }
        this.identifierStack[this.identifierPtr] = this.scanner.getCurrentIdentifierSource();
        this.identifierPositionStack[this.identifierPtr] = (((long) this.scanner.startPosition) << 32) + (this.scanner.currentPosition - 1);
        stackLength = this.identifierLengthStack.length;
        if (++this.identifierLengthPtr >= stackLength) {
            System.arraycopy(this.identifierLengthStack, 0, this.identifierLengthStack = new int[stackLength + 10], 0, stackLength);
        }
        this.identifierLengthStack[this.identifierLengthPtr] = 1;
    }

    protected void pushIdentifier(int flag) {
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

    protected void pushOnGenericsStack(ASTNode node) {
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

    public void recoveryExitFromVariable() {
        if (this.currentElement != null && this.currentElement.parent != null) {
            if (this.currentElement instanceof RecoveredLocalVariable) {
                int end = ((RecoveredLocalVariable) this.currentElement).localDeclaration.sourceEnd;
                this.currentElement.updateSourceEndIfNecessary(end);
                this.currentElement = this.currentElement.parent;
            } else if (this.currentElement instanceof RecoveredField && !(this.currentElement instanceof RecoveredInitializer)) {
                int end = ((RecoveredField) this.currentElement).fieldDeclaration.sourceEnd;
                this.currentElement.updateSourceEndIfNecessary(end);
                this.currentElement = this.currentElement.parent;
            }
        }
    }

    public void recoveryTokenCheck() {
        switch(this.currentToken) {
            case TokenNameLBRACE:
                RecoveredElement newElement = null;
                if (!this.ignoreNextOpeningBrace) {
                    newElement = this.currentElement.updateOnOpeningBrace(this.scanner.startPosition - 1, this.scanner.currentPosition - 1);
                }
                this.lastCheckPoint = this.scanner.currentPosition;
                if (newElement != null) {
                    this.restartRecovery = true;
                    this.currentElement = newElement;
                }
                break;
            case TokenNameRBRACE:
                this.rBraceStart = this.scanner.startPosition - 1;
                this.rBraceEnd = this.scanner.currentPosition - 1;
                this.endPosition = this.flushCommentsDefinedPriorTo(this.rBraceEnd);
                newElement = this.currentElement.updateOnClosingBrace(this.scanner.startPosition, this.rBraceEnd);
                this.lastCheckPoint = this.scanner.currentPosition;
                if (newElement != this.currentElement) {
                    this.currentElement = newElement;
                }
                break;
            case TokenNameSEMICOLON:
                this.endStatementPosition = this.scanner.currentPosition - 1;
                this.endPosition = this.scanner.startPosition - 1;
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

    protected void reportSyntaxErrors(boolean isDietParse, int oldFirstToken) {
        if (this.referenceContext instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) this.referenceContext;
            if (methodDeclaration.errorInSignature) {
                return;
            }
        }
        this.compilationUnit.compilationResult.lineSeparatorPositions = this.scanner.getLineEnds();
        this.scanner.recordLineSeparator = false;
        int start = this.scanner.initialPosition;
        int end = this.scanner.eofPosition <= Integer.MAX_VALUE ? this.scanner.eofPosition - 1 : this.scanner.eofPosition;
        if (isDietParse) {
            TypeDeclaration[] types = this.compilationUnit.types;
            int[][] intervalToSkip = org.eclipse.jdt.internal.compiler.parser.diagnose.RangeUtil.computeDietRange(types);
            DiagnoseParser diagnoseParser = new DiagnoseParser(this, oldFirstToken, start, end, intervalToSkip[0], intervalToSkip[1], intervalToSkip[2], this.options);
            diagnoseParser.diagnoseParse();
            reportSyntaxErrorsForSkippedMethod(types);
            this.scanner.resetTo(start, end);
        } else {
            DiagnoseParser diagnoseParser = new DiagnoseParser(this, oldFirstToken, start, end, this.options);
            diagnoseParser.diagnoseParse();
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
                        if (methods[j].errorInSignature) {
                            DiagnoseParser diagnoseParser = new DiagnoseParser(this, TokenNameDIVIDE, method.declarationSourceStart, method.declarationSourceEnd, this.options);
                            diagnoseParser.diagnoseParse();
                        }
                    }
                }
                FieldDeclaration[] fields = types[i].fields;
                if (fields != null) {
                    int length = fields.length;
                    for (int j = 0; j < length; j++) {
                        if (fields[j] instanceof Initializer) {
                            Initializer initializer = (Initializer) fields[j];
                            if (initializer.errorInSignature) {
                                DiagnoseParser diagnoseParser = new DiagnoseParser(this, TokenNameRIGHT_SHIFT, initializer.declarationSourceStart, initializer.declarationSourceEnd, this.options);
                                diagnoseParser.diagnoseParse();
                            }
                        }
                    }
                }
            }
        }
    }

    protected void resetModifiers() {
        this.modifiers = AccDefault;
        this.modifiersSourceStart = -1;
        this.scanner.commentPtr = -1;
    }

    protected void resetStacks() {
        this.astPtr = -1;
        this.astLengthPtr = -1;
        this.expressionPtr = -1;
        this.expressionLengthPtr = -1;
        this.identifierPtr = -1;
        this.identifierLengthPtr = -1;
        this.intPtr = -1;
        this.nestedMethod[this.nestedType = 0] = 0;
        this.variablesCounter[this.nestedType] = 0;
        this.dimensions = 0;
        this.realBlockStack[this.realBlockPtr = 0] = 0;
        this.recoveredStaticInitializerStart = 0;
        this.listLength = 0;
        this.listTypeParameterLength = 0;
        if (this.scanner != null)
            this.scanner.currentLine = null;
        this.genericsIdentifiersLengthPtr = -1;
        this.genericsLengthPtr = -1;
        this.genericsPtr = -1;
    }

    protected boolean resumeAfterRecovery() {
        this.javadoc = null;
        this.resetStacks();
        if (!this.moveRecoveryCheckpoint()) {
            return false;
        }
        if (this.referenceContext instanceof CompilationUnitDeclaration) {
            goForHeaders();
            this.diet = true;
            return true;
        }
        return false;
    }

    protected boolean resumeOnSyntaxError() {
        if (this.currentElement == null) {
            this.currentElement = this.buildInitialRecoveryState();
        }
        if (this.currentElement == null)
            return false;
        if (this.restartRecovery) {
            this.restartRecovery = false;
        }
        this.updateRecoveryState();
        return this.resumeAfterRecovery();
    }

    public String toString() {
        String s = "identifierStack : char[" + (this.identifierPtr + 1) + "][] = {";
        for (int i = 0; i <= this.identifierPtr; i++) {
            s = s + "\"" + String.valueOf(this.identifierStack[i]) + "\",";
        }
        s = s + "}\n";
        s = s + "identifierLengthStack : int[" + (this.identifierLengthPtr + 1) + "] = {";
        for (int i = 0; i <= this.identifierLengthPtr; i++) {
            s = s + this.identifierLengthStack[i] + ",";
        }
        s = s + "}\n";
        s = s + "astLengthStack : int[" + (this.astLengthPtr + 1) + "] = {";
        for (int i = 0; i <= this.astLengthPtr; i++) {
            s = s + this.astLengthStack[i] + ",";
        }
        s = s + "}\n";
        s = s + "astPtr : int = " + String.valueOf(this.astPtr) + "\n";
        s = s + "intStack : int[" + (this.intPtr + 1) + "] = {";
        for (int i = 0; i <= this.intPtr; i++) {
            s = s + this.intStack[i] + ",";
        }
        s = s + "}\n";
        s = s + "expressionLengthStack : int[" + (this.expressionLengthPtr + 1) + "] = {";
        for (int i = 0; i <= this.expressionLengthPtr; i++) {
            s = s + this.expressionLengthStack[i] + ",";
        }
        s = s + "}\n";
        s = s + "expressionPtr : int = " + String.valueOf(this.expressionPtr) + "\n";
        s = s + "genericsIdentifiersLengthStack : int[" + (this.genericsIdentifiersLengthPtr + 1) + "] = {";
        for (int i = 0; i <= this.genericsIdentifiersLengthPtr; i++) {
            s = s + this.genericsIdentifiersLengthStack[i] + ",";
        }
        s = s + "}\n";
        s = s + "genericsLengthStack : int[" + (this.genericsLengthPtr + 1) + "] = {";
        for (int i = 0; i <= this.genericsLengthPtr; i++) {
            s = s + this.genericsLengthStack[i] + ",";
        }
        s = s + "}\n";
        s = s + "genericsPtr : int = " + String.valueOf(this.genericsPtr) + "\n";
        s = s + "\n\n\n----------------Scanner--------------\n" + this.scanner.toString();
        return s;
    }

    protected void updateRecoveryState() {
        this.currentElement.updateFromParserState();
        this.recoveryTokenCheck();
    }

    protected void updateSourceDeclarationParts(int variableDeclaratorsCounter) {
        FieldDeclaration field;
        int endTypeDeclarationPosition = -1 + this.astStack[this.astPtr - variableDeclaratorsCounter + 1].sourceStart;
        for (int i = 0; i < variableDeclaratorsCounter - 1; i++) {
            field = (FieldDeclaration) this.astStack[this.astPtr - i - 1];
            field.endPart1Position = endTypeDeclarationPosition;
            field.endPart2Position = -1 + this.astStack[this.astPtr - i].sourceStart;
        }
        (field = (FieldDeclaration) this.astStack[this.astPtr]).endPart1Position = endTypeDeclarationPosition;
        field.endPart2Position = field.declarationSourceEnd;
    }

    protected void updateSourcePosition(Expression exp) {
        exp.sourceEnd = this.intStack[this.intPtr--];
        exp.sourceStart = this.intStack[this.intPtr--];
    }
}
