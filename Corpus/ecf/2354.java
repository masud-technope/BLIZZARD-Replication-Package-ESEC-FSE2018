/*******************************************************************************
 * Copyright (c) 2005, 2006 Erkki Lindpere and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erkki Lindpere - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.provider.vbulletin;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.ecf.bulletinboard.BBException;
import org.eclipse.ecf.bulletinboard.IBBObject;
import org.eclipse.ecf.bulletinboard.IMember;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.internal.bulletinboard.commons.AbstractParser;
import org.eclipse.ecf.internal.bulletinboard.commons.IBBObjectFactory;
import org.eclipse.ecf.internal.bulletinboard.commons.parsing.DefaultPatternDescriptor;
import org.eclipse.ecf.internal.bulletinboard.commons.parsing.IPatternDescriptor;
import org.eclipse.ecf.internal.bulletinboard.commons.util.StringUtil;
import org.eclipse.ecf.internal.provider.vbulletin.ThreadBrowser2.SkippedStatus;
import org.eclipse.ecf.internal.provider.vbulletin.identity.ThreadMessageID;
import org.eclipse.ecf.internal.provider.vbulletin.internal.ForumFactory;
import org.eclipse.ecf.internal.provider.vbulletin.internal.GuestFactory;
import org.eclipse.ecf.internal.provider.vbulletin.internal.MemberFactory;
import org.eclipse.ecf.internal.provider.vbulletin.internal.MemberGroupFactory;
import org.eclipse.ecf.internal.provider.vbulletin.internal.ThreadMessageFactory;
import org.eclipse.ecf.internal.provider.vbulletin.internal.VBException;

public class VBParser extends AbstractParser {

    public  VBParser(Namespace namespace, URL baseURL) {
        super(namespace, baseURL);
    }

    private static final Pattern PAT_TITLE = Pattern.compile("<title>(.*?)</title>");

    public String parseTitle(CharSequence seq) {
        Matcher m = PAT_TITLE.matcher(seq);
        if (m.find()) {
            return new String(m.group(1));
        }
        return null;
    }

    @Override
    public IBBObjectFactory getMemberFactory() {
        return new MemberFactory();
    }

    public Pattern getMemberNamePattern() {
        return Pattern.compile("- View Profile: (.*?)</title>");
    }

    public static final Pattern PAT_FORUM = Pattern.compile("<a href=\"forumdisplay.php?(?:.*?)f=([0-9]+)\">(.*?)</a>");

    public Map<ID, Forum> parseForums(final CharSequence seq) {
        Map<ID, Forum> forums = new LinkedHashMap<ID, Forum>();
        Matcher matcher = PAT_FORUM.matcher(seq);
        while (matcher.find()) {
            String name = StringUtil.stripHTMLTrim(matcher.group(2));
            // String desc = StringUtil.stripHTMLTrim(matcher.group(3));
            if (StringUtil.notEmptyStr(name)) {
                ForumFactory ff = new ForumFactory();
                String idStr = matcher.group(1);
                ID id = null;
                try {
                    id = ff.createBBObjectId(namespace, baseURL, idStr);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (IDCreateException e) {
                    e.printStackTrace();
                }
                Forum forum = (Forum) ff.createBBObject(id, name, null);
                // forum.setDescription(desc);
                forums.put(id, forum);
            }
        }
        return forums;
    }

    @Override
    public IPatternDescriptor getThreadPattern() {
        return DefaultPatternDescriptor.defaultCustom(Pattern.compile("<a href=\"showthread.php?(?:.*?)t=([0-9]+?)\" id=\"thread_title_(?:[0-9]+?)\">(.*?)</a>(?:.*?)<div class=\"smallfont\">(.*?)</div>", Pattern.DOTALL), new String[] { "id", "name", "authorInfo" });
    }

    @Override
    public IBBObjectFactory getThreadFactory() {
        return new ThreadFactory();
    }

    public static final Pattern PAT_MSG_INFORMATION = Pattern.compile("<div class=\"panel\">(?:.*?)<blockquote>(.*?)</blockquote>(?:.*?)</td>", Pattern.DOTALL);

    public String parseInformationMessage(CharSequence seq) {
        String msg = null;
        Matcher m = PAT_MSG_INFORMATION.matcher(seq);
        if (m.find()) {
            msg = "vBulletin: " + m.group(1);
        }
        return msg;
    }

    public static final Pattern PAT_MSG_POST_ERROR = Pattern.compile("<!--POSTERROR do not remove this comment-->(.*?)<!--/POSTERROR do not remove this comment-->", Pattern.DOTALL);

    public String parsePostErrorMessage(CharSequence seq) {
        String msg = null;
        Matcher m = PAT_MSG_POST_ERROR.matcher(seq);
        if (m.find()) {
            msg = "vBulletin: " + m.group(1);
        }
        return msg;
    }

    protected BBException createVBException(String msg, CharSequence seq) {
        String vbmsg = parseInformationMessage(seq);
        if (vbmsg == null) {
            vbmsg = parsePostErrorMessage(seq);
        }
        if (vbmsg != null) {
            return new BBException(msg, new VBException(new String(StringUtil.stripHTMLTrim(vbmsg))));
        } else {
            return new BBException(msg);
        }
    }

    public static final Pattern PAT_THEAD_ATTRS = Pattern.compile("<td class=\"navbar\"(?:.*?)><a href=\"/showthread.php\\?t=([0-9]+)(?:.*?)\">(.*?)</td>", Pattern.DOTALL);

    public static final Pattern PAT_THEAD_ATTRS_FORUM = Pattern.compile("<span class=\"navbar\">&gt; <a href=\"forumdisplay.php\\?f=([0-9]+?)\">(.*?)</a></span>");

    public static final IPatternDescriptor PD_THREAD_ATTRS = DefaultPatternDescriptor.defaultIdAndName(PAT_THEAD_ATTRS);

    public static final IPatternDescriptor PD_THREAD_ATTRS_FORUM = DefaultPatternDescriptor.defaultIdAndName(PAT_THEAD_ATTRS_FORUM);

    public Thread parseThreadPageForThreadAttributes(CharSequence seq) throws BBException {
        Thread t = (Thread) genericParser.parseSingleIdName(PD_THREAD_ATTRS, seq, new ThreadFactory());
        if (t != null) {
            Map<ID, IBBObject> forums = genericParser.parseMultiIdName(PD_THREAD_ATTRS_FORUM, seq, new ForumFactory(), true);
            Forum prev = null;
            Forum f = null;
            for (IBBObject obj : forums.values()) {
                f = (Forum) obj;
                if (prev != null) {
                    prev.subforums.add(f);
                }
                f.setParent(prev);
                prev = f;
            }
            t.forum = f;
            return t;
        } else {
            throw new BBException("Failed to parse the thread.");
        }
    }

    public static final Pattern PAT_MSG = Pattern.compile("<!-- post #([0-9]+) -->(.*)<!-- / post #\\1 -->", Pattern.DOTALL);

    public List<ThreadMessage> parseMessages2(final CharSequence seq, final ID lastReadId, boolean desc, SkippedStatus skipped) throws BBException {
        Matcher m;
        ThreadMessage msg;
        List<ThreadMessage> messages = new ArrayList<ThreadMessage>();
        m = PAT_MSG.matcher(seq);
        while (m.find()) {
            ThreadMessageFactory tmf = new ThreadMessageFactory();
            ThreadMessageID id = null;
            try {
                id = (ThreadMessageID) tmf.createBBObjectId(namespace, baseURL, m.group(1));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IDCreateException e) {
                e.printStackTrace();
            }
            if (lastReadId == null || id.getLongValue() > ((ThreadMessageID) lastReadId).getLongValue()) {
                String msgSrc = m.group(2);
                msg = parseMessage2(id, msgSrc);
                if (msg != null) {
                    if (desc) {
                        messages.add(0, msg);
                    } else {
                        messages.add(msg);
                    }
                }
            } else {
                skipped.messagesSkipped = true;
            }
        }
        return messages;
    }

    public static final Pattern PAT_MSG_USER = Pattern.compile("<a class=\"bigusername\" href=\"member.php?(.*)u=([0-9]+)\">(.*)</a>");

    public static final Pattern PAT_MSG_TITLE = Pattern.compile("<!-- icon and title -->(.*)<!-- / icon and title -->", Pattern.DOTALL);

    public static final Pattern PAT_MSG_MESSAGE = Pattern.compile("<!-- message -->(.*)<!-- / message -->", Pattern.DOTALL);

    public static final Pattern PAT_MSG_TIMESTAMP = Pattern.compile("<!-- status icon and date -->(.*)<!-- / status icon and date -->", Pattern.DOTALL);

    @Override
    public Long parseTimestamp(CharSequence seq) {
        Long l = null;
        final Locale locale = Locale.ENGLISH;
        final String dateFormat = "MM-dd-yyyy";
        final String timeFormat = "hh:mm aa";
        final String dateTimeSeparator = ", ";
        final DateFormat fmtTimestamp = new SimpleDateFormat(dateFormat + dateTimeSeparator + timeFormat, locale);
        final DateFormat fmtTime = new SimpleDateFormat(timeFormat, locale);
        Matcher matcher;
        matcher = PAT_MSG_TIMESTAMP.matcher(seq);
        if (matcher.find()) {
            String timestamp = StringUtil.stripHTMLFullTrim(matcher.group(1));
            timestamp = timestamp.replaceAll("1st", "1");
            timestamp = timestamp.replaceAll("2nd", "2");
            timestamp = timestamp.replaceAll("3rd", "3");
            timestamp = timestamp.replaceAll("th", "");
            if (timestamp.startsWith("Today") || timestamp.startsWith("Yesterday")) {
                String[] s = timestamp.split(dateTimeSeparator);
                try {
                    Calendar now = Calendar.getInstance(fmtTime.getTimeZone());
                    if ("Yesterday".equals(s[0])) {
                        now.add(Calendar.DATE, -1);
                    }
                    Date d = fmtTime.parse(s[1]);
                    Calendar then = Calendar.getInstance(fmtTime.getTimeZone());
                    then.setTime(d);
                    then.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE));
                    l = new Long(then.getTimeInMillis());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    l = new Long(fmtTimestamp.parse(timestamp).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return l;
    }

    private ThreadMessage parseMessage2(final ID id, final CharSequence seq) {
        ThreadMessage msg = null;
        ThreadMessageFactory tmf = new ThreadMessageFactory();
        msg = (ThreadMessage) tmf.createBBObject(id, null, null);
        Matcher m;
        String uname;
        Long l = parseTimestamp(seq);
        if (l != null) {
            msg.timePosted = new Date(l);
        }
        m = Pattern.compile("<div id=\"postmenu_" + ((ThreadMessageID) id).getLongValue() + "\">(.*?)</div>", Pattern.DOTALL).matcher(seq);
        if (m.find()) {
            String userInfoStr = m.group(1);
            m = PAT_MSG_USER.matcher(userInfoStr);
            if (m.find()) {
                MemberFactory mf = new MemberFactory();
                uname = new String(StringUtil.simpleStripHTML(m.group(3)));
                ID uid = null;
                try {
                    uid = mf.createBBObjectId(namespace, baseURL, m.group(2));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (IDCreateException e) {
                    e.printStackTrace();
                }
                msg.author = (IMember) mf.createBBObject(uid, uname, null);
            } else {
                // Didn't find a registered author, so the userinfo should
                // contain only the username.
                msg.author = new Member(new String(userInfoStr.trim()));
            }
        }
        m = Pattern.compile("#<a href=\"showpost.php\\?p=" + ((ThreadMessageID) id).getLongValue() + "(?:.*?)><strong>([0-9]+)</strong></a>").matcher(seq);
        m.find();
        msg.number = Integer.parseInt(m.group(1));
        m = PAT_MSG_TITLE.matcher(seq);
        m.find();
        msg.setNameInternal(new String(StringUtil.stripHTMLTrim(m.group(1))));
        m = PAT_MSG_MESSAGE.matcher(seq);
        m.find();
        String message = StringUtil.stripHTMLFullTrim(m.group(1));
        msg.message = message;
        return msg;
    }

    public static final Pattern PAT_PAGES = Pattern.compile("<td class=\"vbmenu_control\"(?:.*?)>Page ([0-9]+) of ([0-9]+)</td>");

    public int parseNextPage(CharSequence seq) {
        Matcher m = PAT_PAGES.matcher(seq);
        int next = -1;
        if (m.find()) {
            int current = Integer.parseInt(m.group(1));
            int last = Integer.parseInt(m.group(2));
            if (current < last) {
                next = current + 1;
            }
        }
        return next;
    }

    public int parsePrevPage(CharSequence seq) {
        Matcher m = PAT_PAGES.matcher(seq);
        int prev = -1;
        if (m.find()) {
            int current = Integer.parseInt(m.group(1));
            if (current > 1) {
                prev = current - 1;
            }
        }
        return prev;
    }

    @Override
    public IPatternDescriptor getMemberPattern() {
        return DefaultPatternDescriptor.defaultIdAndName(Pattern.compile("<a href=\"member.php\\?u=([0-9]+?)\">(.*?)</a>"));
    }

    @Override
    public IPatternDescriptor getAuthorInfoMemberPattern() {
        return DefaultPatternDescriptor.defaultIdAndName(Pattern.compile("<span(?:.*?)onclick=\"window.open('member.php\\?u=([0-9]+?)', '_self')\">(.*?)</span>"));
    }

    @Override
    public Pattern getMemberGroupContainerPattern() {
        return Pattern.compile("<form action=\"profile.php\\?do=joingroup\" method=\"post\">(.*?)</form>", Pattern.DOTALL);
    }

    @Override
    public IBBObjectFactory getMemberGroupFactory() {
        return new MemberGroupFactory();
    }

    @Override
    public IPatternDescriptor getMemberGroupPattern() {
        return DefaultPatternDescriptor.reverseIdAndName(Pattern.compile("<tr>(?:.*?)<td class=\"alt(?:[12]{1})\">(.*?)<div class=\"smallfont\">(?:.*?)</div>(?:.*?)<label for=\"rb_join_([0-9]+?)\">(?:.*?)</tr>", Pattern.DOTALL));
    }

    @Override
    public void throwException(final String msg, final CharSequence seq) throws BBException {
        throw createVBException(msg, seq);
    }

    @Override
    public IBBObjectFactory getGuestFactory() {
        return new GuestFactory();
    }
}
