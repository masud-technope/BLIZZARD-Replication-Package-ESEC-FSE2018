/*******************************************************************************
 * Copyright (c) 2009, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.internal.search;

import com.ibm.icu.util.ULocale;

/**
 * Contains strings and methods for writing HTML markup
 *
 * @since 1.0.1
 */
public abstract class HTMLConvertor {

    /**
	 * Default file extension for HTML files: <code>.html</code>
	 */
    //$NON-NLS-1$
    public static final String HTML_EXTENSION = ".html";

    /**
	 * Default file extension for XML files: <code>.xml</code>
	 */
    //$NON-NLS-1$
    public static final String XML_EXTENSION = ".xml";

    /**
	 * Standard HTML file prefix
	 */
    //$NON-NLS-1$
    public static final String HTML_HEADER = "<!doctype HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n";

    /**
	 * Meta tag for default HTML content type
	 */
    //$NON-NLS-1$
    public static final String CONTENT_TYPE_META = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n";

    /**
	 * Standard W3C footer image + link
	 */
    //$NON-NLS-1$
    public static final String W3C_FOOTER = "<p>\n\t<a href=\"http://validator.w3.org/check?uri=referer\">\n<img src=\"http://www.w3.org/Icons/valid-html401-blue\" alt=\"Valid HTML 4.01 Transitional\" height=\"31\" width=\"88\"></a>\n</p>\n";

    /**
	 * Opening title tag: <code>&lt;title&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String OPEN_TITLE = "<title>";

    /**
	 * Closing title tag: <code>&lt;/title&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String CLOSE_TITLE = "</title>\n";

    /**
	 * Opening head tag: <code>&lt;head&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String OPEN_HEAD = "<head>\n";

    /**
	 * Closing head tag: <code>&lt;/head&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String CLOSE_HEAD = "</head>\n";

    /**
	 * Opening body tag: <code>&lt;body&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String OPEN_BODY = "<body>\n";

    /**
	 * Closing body tag: <code>&lt;/body&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String CLOSE_BODY = "</body>\n";

    /**
	 * Opening h3 tag: <code>&lt;h3&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String OPEN_H3 = "<h3>";

    /**
	 * Closing h3 tag: <code>&lt;/h3&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String CLOSE_H3 = "</h3>\n";

    /**
	 * Opening html tag: <code>&lt;html&gt;</code>
	 */
    //$NON-NLS-1$ //$NON-NLS-2$
    public static final String OPEN_HTML = !ULocale.getDefault().isRightToLeft() ? "<html>\n" : "<html  dir=\"rtl\">\n";

    /**
	 * Closing html tag: <code>&lt;html&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String CLOSE_HTML = "</html>\n";

    /**
	 * Closing table tag: <code>&lt;/table&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String CLOSE_TABLE = "</table>\n";

    /**
	 * Opening td tag: <code>&lt;td&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String OPEN_TD = "<td>";

    /**
	 * Closing td tag: <code>&lt;/td&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String CLOSE_TD = "</td>\n";

    /**
	 * Opening li tag: <code>&lt;li&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String OPEN_LI = "\t<li>";

    /**
	 * Closing li tag: <code>&lt;/li&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String CLOSE_LI = "</li>\n";

    /**
	 * Opening p tag: <code>&lt;p&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String OPEN_P = "<p>";

    /**
	 * Closing p tag: <code>&lt;/p&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String CLOSE_P = "</p>\n";

    /**
	 * Opening ol tag: <code>&lt;ol&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String OPEN_OL = "<ol>\n";

    /**
	 * Closing ol tag: <code>&lt;/ol&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String CLOSE_OL = "</ol>\n";

    /**
	 * Opening ul tag: <code>&lt;ul&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String OPEN_UL = "<ul>\n";

    /**
	 * Closing ul tag: <code>&lt;/ul&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String CLOSE_UL = "</ul>\n";

    /**
	 * Opening tr tag: <code>&lt;tr&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String OPEN_TR = "<tr>\n";

    /**
	 * Closing tr tag: <code>&lt;/tr&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String CLOSE_TR = "</tr>\n";

    /**
	 * Closing div tag: <code>&lt;/div&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String CLOSE_DIV = "</div>\n";

    /**
	 * Break tag: <code>&lt;br&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String BR = "<br>";

    /**
	 * Closing a tag: <code>&lt;/a&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String CLOSE_A = "</a>\n";

    /**
	 * Opening b tag: <code>&lt;b&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String OPEN_B = "<b>";

    /**
	 * Closing b tag: <code>&lt;/b&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String CLOSE_B = "</b>";

    /**
	 * Closing h4 tag: <code>&lt;/h4&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String CLOSE_H4 = "</h4>\n";

    /**
	 * Opening h4 tag: <code>&lt;h4&gt;</code>
	 */
    //$NON-NLS-1$
    public static final String OPEN_H4 = "<h4>";

    /**
	 * Opens a new <code>&lt;td&gt;</code> with the given width attribute set
	 *
	 * @param width
	 * @return a new open <code>&lt;td&gt;</code> tag
	 */
    public static String openTD(int width) {
        StringBuffer buffer = new StringBuffer();
        //$NON-NLS-1$//$NON-NLS-2$
        buffer.append("<td width=\"").append(width).append("%\">");
        return buffer.toString();
    }
}
