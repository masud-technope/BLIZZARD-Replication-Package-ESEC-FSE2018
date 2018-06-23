/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jacek Pospychala <jacek.pospychala@pl.ibm.com> - bug 197329, 190851
 *****************************************************************************/
package org.eclipse.ecf.presence.ui.chatroom;

import java.text.SimpleDateFormat;
import java.util.*;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.internal.presence.ui.Messages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;

/**
 * Default implementation of {@link IMessageRenderer}.
 *
 */
public class MessageRenderer implements IMessageRenderer {

    /**
	 * Messages sent by local user
	 */
    //$NON-NLS-1$
    protected static final String SENT_COLOR = "org.eclipse.ecf.presence.ui.sentColor";

    //$NON-NLS-1$
    protected static final String SENT_FONT = "org.eclipse.ecf.presence.ui.sentFont";

    /**
	 * Any received messages
	 */
    //$NON-NLS-1$
    protected static final String RECEIVED_COLOR = "org.eclipse.ecf.presence.ui.receivedColor";

    //$NON-NLS-1$
    protected static final String RECEIVED_FONT = "org.eclipse.ecf.presence.ui.receivedFont";

    /**
	 * System messages, eg. server notifications
	 */
    //$NON-NLS-1$
    protected static final String SYSTEM_COLOR = "org.eclipse.ecf.presence.ui.systemColor";

    //$NON-NLS-1$
    protected static final String SYSTEM_FONT = "org.eclipse.ecf.presence.ui.systemFont";

    /**
	 * The default color used to highlight message when the user's
	 * name is referred to in the chatroom. The default color is red.
	 */
    //$NON-NLS-1$
    protected static final String RECEIVEDHIGHLIGHT_COLOR = "org.eclipse.ecf.presence.ui.receivedHighlightColor";

    //$NON-NLS-1$
    protected static final String RECEIVEDHIGHLIGHT_FONT = "org.eclipse.ecf.presence.ui.receivedHighlightFont";

    /**
	 * Date stamp in message window
	 */
    //$NON-NLS-1$
    protected static final String DATE_COLOR = "org.eclipse.ecf.presence.ui.dateColor";

    //$NON-NLS-1$
    protected static final String DATE_FONT = "org.eclipse.ecf.presence.ui.dateFont";

    protected static final String DEFAULT_TIME_FORMAT = Messages.MessageRenderer_DEFAULT_TIME_FORMAT;

    protected static final String DEFAULT_DATE_FORMAT = Messages.MessageRenderer_DEFAULT_DATE_FORMAT;

    private StringBuffer buffer;

    private List styleRanges = new ArrayList();

    protected boolean nickContained;

    protected boolean isSent;

    protected String message;

    protected String originator;

    private String font;

    private String color;

    public StyleRange[] getStyleRanges() {
        return (StyleRange[]) styleRanges.toArray(new StyleRange[styleRanges.size()]);
    }

    public String render(String msg, String orig, String localUserName) {
        Assert.isNotNull(localUserName);
        styleRanges.clear();
        if (msg == null) {
            return null;
        }
        buffer = new StringBuffer();
        this.message = msg;
        this.originator = orig;
        // check to see if the message has the user's name contained within
        // and make sure that the person referring to the user's name
        // is not the user himself, no highlighting is required in this case
        // as the user is already aware that his name is being referenced
        nickContained = (msg.indexOf(localUserName) != -1) && (!localUserName.equals(orig));
        isSent = (orig != null) && (orig.equals(localUserName));
        if (orig == null) {
            color = SYSTEM_COLOR;
            font = SYSTEM_FONT;
        } else if (isSent) {
            color = SENT_COLOR;
            font = SENT_FONT;
        } else if (nickContained) {
            color = RECEIVEDHIGHLIGHT_COLOR;
            font = RECEIVEDHIGHLIGHT_FONT;
        } else {
            color = RECEIVED_COLOR;
            font = RECEIVED_FONT;
        }
        doRender();
        return buffer.toString();
    }

    protected void doRender() {
        appendDateTime();
        if (originator != null) {
            appendNickname();
        }
        appendMessage();
    }

    protected void appendDateTime() {
        //$NON-NLS-1$
        String msg = NLS.bind(Messages.MessageRenderer_DEFAULT_DATETIME_FORMAT, getCurrentDate(DEFAULT_TIME_FORMAT)) + " ";
        append(msg, DATE_COLOR, null, DATE_FONT);
    }

    protected void appendNickname() {
        //$NON-NLS-1$
        String msg = originator + ": ";
        append(msg, color, null, font);
    }

    protected void appendMessage() {
        append(message, color, null, font);
    }

    protected void append(String msg, String foreground, String background, String font1) {
        if (msg == null) {
            return;
        }
        int start = buffer.length();
        buffer.append(msg);
        if (foreground == null && background == null && font1 == null) {
            return;
        }
        StyleRange styleRange = new StyleRange(start, msg.length(), getColor(foreground), getColor(background));
        styleRange.font = getFont(font1);
        styleRanges.add(styleRange);
    }

    private Color getColor(String name) {
        if (name == null) {
            return null;
        }
        ITheme theme = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme();
        Color c = theme.getColorRegistry().get(name);
        if (c == null) {
            return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
        }
        return c;
    }

    private Font getFont(String name) {
        if (name == null) {
            return null;
        }
        ITheme theme = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme();
        Font f = theme.getFontRegistry().get(name);
        if (f == null) {
            return Display.getDefault().getSystemFont();
        }
        return f;
    }

    protected String getCurrentDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String res = sdf.format(new Date());
        return res;
    }

    protected String getDateTime() {
        StringBuffer buf = new StringBuffer();
        //$NON-NLS-1$
        buf.append(getCurrentDate(DEFAULT_DATE_FORMAT)).append(" ").append(getCurrentDate(DEFAULT_TIME_FORMAT));
        return buf.toString();
    }
}
