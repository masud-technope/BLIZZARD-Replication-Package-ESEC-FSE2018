/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.ui.text;

/**
 * Color keys used for syntax highlighting Java
 * code and Javadoc compliant comments.
 * A <code>IColorManager</code> is responsible for mapping
 * concrete colors to these keys.
 * <p>
 * This interface declares static final fields only; it is not intended to be
 * implemented.
 * </p>
 *
 * @see org.eclipse.jdt.ui.text.IColorManager
 * @see org.eclipse.jdt.ui.text.IColorManagerExtension
 *
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IJavaColorConstants {

    /**
	 * Note: This constant is for internal use only. Clients should not use this constant. The
	 * prefix all color constants start with (value <code>"java_"</code>).
	 */
    //$NON-NLS-1$
    String PREFIX = "java_";

    /** The color key for multi-line comments in Java code
	 * (value <code>"java_multi_line_comment"</code>).
	 */
    //$NON-NLS-1$
    String JAVA_MULTI_LINE_COMMENT = "java_multi_line_comment";

    /** The color key for single-line comments in Java code
	 * (value <code>"java_single_line_comment"</code>).
	 */
    //$NON-NLS-1$
    String JAVA_SINGLE_LINE_COMMENT = "java_single_line_comment";

    /** The color key for Java keywords in Java code
	 * (value <code>"java_keyword"</code>).
	 */
    //$NON-NLS-1$
    String JAVA_KEYWORD = "java_keyword";

    /** The color key for string and character literals in Java code
	 * (value <code>"java_string"</code>).
	 */
    //$NON-NLS-1$
    String JAVA_STRING = "java_string";

    /** The color key for method names in Java code
	 * (value <code>"java_method_name"</code>).
	 *
	 * @since 3.0
	 * @deprecated replaced as of 3.1 by an equivalent semantic highlighting, see {@link org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlightings#METHOD}
	 */
    @Deprecated
    String //$NON-NLS-1$
    JAVA_METHOD_NAME = "java_method_name";

    /** The color key for keyword 'return' in Java code
	 * (value <code>"java_keyword_return"</code>).
	 *
	 * @since 3.0
	 */
    //$NON-NLS-1$
    String JAVA_KEYWORD_RETURN = "java_keyword_return";

    /** The color key for operators in Java code
	 * (value <code>"java_operator"</code>).
	 *
	 * @since 3.0
	 */
    //$NON-NLS-1$
    String JAVA_OPERATOR = "java_operator";

    /** The color key for brackets in Java code
	 * (value <code>"java_bracket"</code>).
	 *
	 * @since 3.3
	 */
    //$NON-NLS-1$
    String JAVA_BRACKET = "java_bracket";

    /**
	 * The color key for everything in Java code for which no other color is specified
	 * (value <code>"java_default"</code>).
	 */
    //$NON-NLS-1$
    String JAVA_DEFAULT = "java_default";

    /**
	 * The color key for the Java built-in types such as <code>int</code> and <code>char</code> in Java code
	 * (value <code>"java_type"</code>).
	 *
	 * @deprecated no longer used, replaced by <code>JAVA_KEYWORD</code>
	 */
    @Deprecated
    String //$NON-NLS-1$
    JAVA_TYPE = "java_type";

    /**
	 * The color key for annotations
	 * (value <code>"java_annotation"</code>).
	 *
	 * @since 3.1
	 * @deprecated replaced as of 3.2 by an equivalent semantic highlighting, see {@link org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlightings#ANNOTATION}
	 */
    @Deprecated
    String //$NON-NLS-1$
    JAVA_ANNOTATION = "java_annotation";

    /**
	 * The color key for task tags in java comments
	 * (value <code>"java_comment_task_tag"</code>).
	 *
	 * @since 2.1
	 */
    //$NON-NLS-1$
    String TASK_TAG = "java_comment_task_tag";

    /**
	 * The color key for JavaDoc keywords (<code>@foo</code>) in JavaDoc comments
	 * (value <code>"java_doc_keyword"</code>).
	 */
    //$NON-NLS-1$
    String JAVADOC_KEYWORD = "java_doc_keyword";

    /**
	 * The color key for HTML tags (<code>&lt;foo&gt;</code>) in JavaDoc comments
	 * (value <code>"java_doc_tag"</code>).
	 */
    //$NON-NLS-1$
    String JAVADOC_TAG = "java_doc_tag";

    /**
	 * The color key for JavaDoc links (<code>{foo}</code>) in JavaDoc comments
	 * (value <code>"java_doc_link"</code>).
	 */
    //$NON-NLS-1$
    String JAVADOC_LINK = "java_doc_link";

    /**
	 * The color key for everything in JavaDoc comments for which no other color is specified
	 * (value <code>"java_doc_default"</code>).
	 */
    //$NON-NLS-1$
    String JAVADOC_DEFAULT = "java_doc_default";

    //---------- Properties File Editor ----------
    /**
	 * The color key for keys in a properties file
	 * (value <code>"pf_coloring_key"</code>).
	 *
	 * @since 3.1
	 */
    //$NON-NLS-1$
    String PROPERTIES_FILE_COLORING_KEY = "pf_coloring_key";

    /**
	 * The color key for comments in a properties file
	 * (value <code>"pf_coloring_comment"</code>).
	 *
	 * @since 3.1
	 */
    //$NON-NLS-1$
    String PROPERTIES_FILE_COLORING_COMMENT = "pf_coloring_comment";

    /**
	 * The color key for values in a properties file
	 * (value <code>"pf_coloring_value"</code>).
	 *
	 * @since 3.1
	 */
    //$NON-NLS-1$
    String PROPERTIES_FILE_COLORING_VALUE = "pf_coloring_value";

    /**
	 * The color key for assignment in a properties file.
	 * (value <code>"pf_coloring_assignment"</code>).
	 *
	 * @since 3.1
	 */
    //$NON-NLS-1$
    String PROPERTIES_FILE_COLORING_ASSIGNMENT = "pf_coloring_assignment";

    /**
	 * The color key for arguments in values in a properties file.
	 * (value <code>"pf_coloring_argument"</code>).
	 *
	 * @since 3.1
	 */
    //$NON-NLS-1$
    String PROPERTIES_FILE_COLORING_ARGUMENT = "pf_coloring_argument";
}
