/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Philippe Ombredanne <pombredanne@nexb.com> - https://bugs.eclipse.org/bugs/show_bug.cgi?id=150989
 *     Anton Leherbauer (Wind River Systems) - [misc] Allow custom token for WhitespaceRule - https://bugs.eclipse.org/bugs/show_bug.cgi?id=251224
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.text.java;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaColorConstants;
import org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlightings;
import org.eclipse.jdt.internal.ui.text.AbstractJavaScanner;
import org.eclipse.jdt.internal.ui.text.CombinedWordRule;
import org.eclipse.jdt.internal.ui.text.ISourceVersionDependent;
import org.eclipse.jdt.internal.ui.text.JavaWhitespaceDetector;
import org.eclipse.jdt.internal.ui.text.JavaWordDetector;

/**
 * A Java code scanner.
 */
public final class JavaCodeScanner extends AbstractJavaScanner {

    /**
	 * Rule to detect java operators.
	 *
	 * @since 3.0
	 */
    private static final class OperatorRule implements IRule {

        /** Java operators */
        private final char[] JAVA_OPERATORS = { ';', '.', '=', '/', '\\', '+', '-', '*', '<', '>', ':', '?', '!', ',', '|', '&', '^', '%', '~' };

        private final IToken fToken;

        public  OperatorRule(IToken token) {
            fToken = token;
        }

        public boolean isOperator(char character) {
            for (int index = 0; index < JAVA_OPERATORS.length; index++) {
                if (JAVA_OPERATORS[index] == character)
                    return true;
            }
            return false;
        }

        @Override
        public IToken evaluate(ICharacterScanner scanner) {
            int character = scanner.read();
            if (isOperator((char) character)) {
                do {
                    character = scanner.read();
                } while (isOperator((char) character));
                scanner.unread();
                return fToken;
            } else {
                scanner.unread();
                return Token.UNDEFINED;
            }
        }
    }

    private static final class BracketRule implements IRule {

        private final char[] JAVA_BRACKETS = { '(', ')', '{', '}', '[', ']' };

        private final IToken fToken;

        public  BracketRule(IToken token) {
            fToken = token;
        }

        public boolean isBracket(char character) {
            for (int index = 0; index < JAVA_BRACKETS.length; index++) {
                if (JAVA_BRACKETS[index] == character)
                    return true;
            }
            return false;
        }

        @Override
        public IToken evaluate(ICharacterScanner scanner) {
            int character = scanner.read();
            if (isBracket((char) character)) {
                do {
                    character = scanner.read();
                } while (isBracket((char) character));
                scanner.unread();
                return fToken;
            } else {
                scanner.unread();
                return Token.UNDEFINED;
            }
        }
    }

    private static class VersionedWordMatcher extends CombinedWordRule.WordMatcher implements ISourceVersionDependent {

        private final IToken fDefaultToken;

        private final String fVersion;

        private boolean fIsVersionMatch;

        public  VersionedWordMatcher(IToken defaultToken, String version, String currentVersion) {
            fDefaultToken = defaultToken;
            fVersion = version;
            setSourceVersion(currentVersion);
        }

        @Override
        public void setSourceVersion(String version) {
            fIsVersionMatch = fVersion.compareTo(version) <= 0;
        }

        @Override
        public IToken evaluate(ICharacterScanner scanner, CombinedWordRule.CharacterBuffer word) {
            IToken token = super.evaluate(scanner, word);
            if (fIsVersionMatch || token.isUndefined())
                return token;
            return fDefaultToken;
        }
    }

    private static class AnnotationRule implements IRule, ISourceVersionDependent {

        private static final class ResettableScanner implements ICharacterScanner {

            private final ICharacterScanner fDelegate;

            private int fReadCount;

            public  ResettableScanner(final ICharacterScanner scanner) {
                Assert.isNotNull(scanner);
                fDelegate = scanner;
                mark();
            }

            @Override
            public int getColumn() {
                return fDelegate.getColumn();
            }

            @Override
            public char[][] getLegalLineDelimiters() {
                return fDelegate.getLegalLineDelimiters();
            }

            @Override
            public int read() {
                int ch = fDelegate.read();
                if (ch != ICharacterScanner.EOF)
                    fReadCount++;
                return ch;
            }

            @Override
            public void unread() {
                if (fReadCount > 0)
                    fReadCount--;
                fDelegate.unread();
            }

            public void mark() {
                fReadCount = 0;
            }

            public void reset() {
                while (fReadCount > 0) unread();
                while (fReadCount < 0) read();
            }
        }

        private final IWhitespaceDetector fWhitespaceDetector = new JavaWhitespaceDetector();

        private final IWordDetector fWordDetector = new JavaWordDetector();

        private final IToken fInterfaceToken;

        private final IToken fAtToken;

        private final String fVersion;

        private boolean fIsVersionMatch;

        public  AnnotationRule(IToken interfaceToken, Token atToken, String version, String currentVersion) {
            fInterfaceToken = interfaceToken;
            fAtToken = atToken;
            fVersion = version;
            setSourceVersion(currentVersion);
        }

        @Override
        public IToken evaluate(ICharacterScanner scanner) {
            if (!fIsVersionMatch)
                return Token.UNDEFINED;
            ResettableScanner resettable = new ResettableScanner(scanner);
            if (resettable.read() == '@')
                return readAnnotation(resettable);
            resettable.reset();
            return Token.UNDEFINED;
        }

        private IToken readAnnotation(ResettableScanner scanner) {
            scanner.mark();
            skipWhitespace(scanner);
            if (readInterface(scanner)) {
                return fInterfaceToken;
            } else {
                scanner.reset();
                return fAtToken;
            }
        }

        private boolean readInterface(ICharacterScanner scanner) {
            int ch = scanner.read();
            int i = 0;
            while (i < INTERFACE.length() && INTERFACE.charAt(i) == ch) {
                i++;
                ch = scanner.read();
            }
            if (i < INTERFACE.length())
                return false;
            if (fWordDetector.isWordPart((char) ch))
                return false;
            if (ch != ICharacterScanner.EOF)
                scanner.unread();
            return true;
        }

        private boolean skipWhitespace(ICharacterScanner scanner) {
            while (fWhitespaceDetector.isWhitespace((char) scanner.read())) {
            }
            scanner.unread();
            return true;
        }

        @Override
        public void setSourceVersion(String version) {
            fIsVersionMatch = fVersion.compareTo(version) <= 0;
        }
    }

    private static final String SOURCE_VERSION = JavaCore.COMPILER_SOURCE;

    static String[] fgKeywords = { "abstract", "break", "case", "catch", "class", "const", "continue", "default", "do", "else", "extends", "final", "finally", "for", "goto", "if", "implements", "import", "instanceof", "interface", "native", "new", "package", "private", "protected", "public", "static", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "volatile", "while" };

    private static final String INTERFACE = "interface";

    private static final String RETURN = "return";

    private static String[] fgJava14Keywords = { "assert" };

    private static String[] fgJava15Keywords = { "enum" };

    private static String[] fgTypes = { "void", "boolean", "char", "byte", "short", "strictfp", "int", "long", "float", "double" };

    private static String[] fgConstants = { "false", "null", "true" };

    private static final String ANNOTATION_BASE_KEY = PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + SemanticHighlightings.ANNOTATION;

    private static final String ANNOTATION_COLOR_KEY = ANNOTATION_BASE_KEY + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_COLOR_SUFFIX;

    private static String[] fgTokenProperties = { IJavaColorConstants.JAVA_KEYWORD, IJavaColorConstants.JAVA_STRING, IJavaColorConstants.JAVA_DEFAULT, IJavaColorConstants.JAVA_KEYWORD_RETURN, IJavaColorConstants.JAVA_OPERATOR, IJavaColorConstants.JAVA_BRACKET, ANNOTATION_COLOR_KEY };

    private List<ISourceVersionDependent> fVersionDependentRules = new ArrayList(3);

    public  JavaCodeScanner(IColorManager manager, IPreferenceStore store) {
        super(manager, store);
        initialize();
    }

    @Override
    protected String[] getTokenProperties() {
        return fgTokenProperties;
    }

    @Override
    protected List<IRule> createRules() {
        List<IRule> rules = new ArrayList();
        Token token = getToken(IJavaColorConstants.JAVA_STRING);
        rules.add(new SingleLineRule("'", "'", token, '\\'));
        Token defaultToken = getToken(IJavaColorConstants.JAVA_DEFAULT);
        rules.add(new WhitespaceRule(new JavaWhitespaceDetector(), defaultToken));
        String version = getPreferenceStore().getString(SOURCE_VERSION);
        token = getToken(ANNOTATION_COLOR_KEY);
        AnnotationRule atInterfaceRule = new AnnotationRule(getToken(IJavaColorConstants.JAVA_KEYWORD), token, JavaCore.VERSION_1_5, version);
        rules.add(atInterfaceRule);
        fVersionDependentRules.add(atInterfaceRule);
        JavaWordDetector wordDetector = new JavaWordDetector();
        CombinedWordRule combinedWordRule = new CombinedWordRule(wordDetector, defaultToken);
        VersionedWordMatcher j14Matcher = new VersionedWordMatcher(defaultToken, JavaCore.VERSION_1_4, version);
        token = getToken(IJavaColorConstants.JAVA_KEYWORD);
        for (int i = 0; i < fgJava14Keywords.length; i++) j14Matcher.addWord(fgJava14Keywords[i], token);
        combinedWordRule.addWordMatcher(j14Matcher);
        fVersionDependentRules.add(j14Matcher);
        VersionedWordMatcher j15Matcher = new VersionedWordMatcher(defaultToken, JavaCore.VERSION_1_5, version);
        token = getToken(IJavaColorConstants.JAVA_KEYWORD);
        for (int i = 0; i < fgJava15Keywords.length; i++) j15Matcher.addWord(fgJava15Keywords[i], token);
        combinedWordRule.addWordMatcher(j15Matcher);
        fVersionDependentRules.add(j15Matcher);
        token = getToken(IJavaColorConstants.JAVA_OPERATOR);
        rules.add(new OperatorRule(token));
        token = getToken(IJavaColorConstants.JAVA_BRACKET);
        rules.add(new BracketRule(token));
        CombinedWordRule.WordMatcher returnWordRule = new CombinedWordRule.WordMatcher();
        token = getToken(IJavaColorConstants.JAVA_KEYWORD_RETURN);
        returnWordRule.addWord(RETURN, token);
        combinedWordRule.addWordMatcher(returnWordRule);
        CombinedWordRule.WordMatcher wordRule = new CombinedWordRule.WordMatcher();
        token = getToken(IJavaColorConstants.JAVA_KEYWORD);
        for (int i = 0; i < fgKeywords.length; i++) wordRule.addWord(fgKeywords[i], token);
        for (int i = 0; i < fgTypes.length; i++) wordRule.addWord(fgTypes[i], token);
        for (int i = 0; i < fgConstants.length; i++) wordRule.addWord(fgConstants[i], token);
        combinedWordRule.addWordMatcher(wordRule);
        rules.add(combinedWordRule);
        setDefaultReturnToken(defaultToken);
        return rules;
    }

    @Override
    protected String getBoldKey(String colorKey) {
        if ((ANNOTATION_COLOR_KEY).equals(colorKey))
            return ANNOTATION_BASE_KEY + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_BOLD_SUFFIX;
        return super.getBoldKey(colorKey);
    }

    @Override
    protected String getItalicKey(String colorKey) {
        if ((ANNOTATION_COLOR_KEY).equals(colorKey))
            return ANNOTATION_BASE_KEY + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_ITALIC_SUFFIX;
        return super.getItalicKey(colorKey);
    }

    @Override
    protected String getStrikethroughKey(String colorKey) {
        if ((ANNOTATION_COLOR_KEY).equals(colorKey))
            return ANNOTATION_BASE_KEY + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_STRIKETHROUGH_SUFFIX;
        return super.getStrikethroughKey(colorKey);
    }

    @Override
    protected String getUnderlineKey(String colorKey) {
        if ((ANNOTATION_COLOR_KEY).equals(colorKey))
            return ANNOTATION_BASE_KEY + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_UNDERLINE_SUFFIX;
        return super.getUnderlineKey(colorKey);
    }

    @Override
    public boolean affectsBehavior(PropertyChangeEvent event) {
        return event.getProperty().equals(SOURCE_VERSION) || super.affectsBehavior(event);
    }

    @Override
    public void adaptToPreferenceChange(PropertyChangeEvent event) {
        if (event.getProperty().equals(SOURCE_VERSION)) {
            Object value = event.getNewValue();
            if (value instanceof String) {
                String s = (String) value;
                for (Iterator<ISourceVersionDependent> it = fVersionDependentRules.iterator(); it.hasNext(); ) {
                    ISourceVersionDependent dependent = it.next();
                    dependent.setSourceVersion(s);
                }
            }
        } else if (super.affectsBehavior(event)) {
            super.adaptToPreferenceChange(event);
        }
    }
}
