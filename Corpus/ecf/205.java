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
package org.eclipse.ecf.internal.provider.phpbb;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.ecf.bulletinboard.BBException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.internal.bulletinboard.commons.AbstractParser;
import org.eclipse.ecf.internal.bulletinboard.commons.IBBObjectFactory;
import org.eclipse.ecf.internal.bulletinboard.commons.parsing.DefaultPatternDescriptor;
import org.eclipse.ecf.internal.bulletinboard.commons.parsing.IPatternDescriptor;
import org.eclipse.ecf.internal.bulletinboard.commons.util.StringUtil;
import org.eclipse.ecf.internal.provider.phpbb.identity.ThreadMessageID;

/**
 * NB! use new String(Matcher.group(int)) instead of Matcher.group(int)
 * 
 * @author Erkki
 */
public class PHPBBParser extends AbstractParser {

    public  PHPBBParser(Namespace namespace, URL baseURL) {
        super(namespace, baseURL);
    }

    public static final Pattern PAT_PHPBB_SIGNATURE = Pattern.compile("<span class=\"copyright\">(.*)</span>", Pattern.DOTALL);

    public static final String PHPBB_SIGNATURE = "Powered by phpBB";

    public boolean isServiceSupported(final CharSequence seq) {
        final Matcher m = PAT_PHPBB_SIGNATURE.matcher(seq);
        if (m.find()) {
            String copyright = m.group(1);
            copyright = StringUtil.stripHTMLTrim(copyright);
            return copyright.contains(PHPBB_SIGNATURE);
        }
        return false;
    }

    public static final Pattern PAT_FORUM_OR_CATEGORY = Pattern.compile("(?:" + "<span class=\"forumlink\"> <a href=\"viewforum.php\\?f=([0-9]+)(?:.*)\" class=\"forumlink\">(.*)</a><br />" + "(?:\\s*)</span> <span class=\"genmed\">(?s)(.*?)</span>" + ")|(?:" + "<a href=\"index.php\\?c=([0-9]+)(?:.*)\" class=\"cattitle\">(.*)</a>" + ")");

    /**
	 * Parses forum HTML output into a list of forums.
	 * @param seq 
	 * @return map of ID -> Forum associations.
	 */
    public Map<ID, Forum> parseForums(final CharSequence seq) {
        final Map<ID, Forum> forums = new LinkedHashMap<ID, Forum>();
        final Matcher matcher = PAT_FORUM_OR_CATEGORY.matcher(seq);
        Category lastCat = null;
        while (matcher.find()) {
            // Matched forum
            if (matcher.group(2) != null) {
                final String name = StringUtil.stripHTMLTrim(matcher.group(2));
                final String desc = StringUtil.stripHTMLTrim(matcher.group(3));
                if (StringUtil.notEmptyStr(name)) {
                    final ForumFactory ff = new ForumFactory();
                    final String idStr = matcher.group(1);
                    ID id = null;
                    try {
                        id = ff.createBBObjectId(namespace, baseURL, idStr);
                    } catch (final IDCreateException e) {
                        e.printStackTrace();
                    }
                    final Forum forum = (Forum) ff.createBBObject(id, name, null);
                    forum.setDescription(desc);
                    if (lastCat != null) {
                        lastCat.addSubForum(forum);
                        forum.setParent(lastCat);
                    }
                    forums.put(id, forum);
                }
            }
            // Matched category
            if (matcher.group(5) != null) {
                final String name = StringUtil.stripHTMLTrim(matcher.group(5));
                if (StringUtil.notEmptyStr(name)) {
                    final CategoryFactory cf = new CategoryFactory();
                    final String idStr = matcher.group(4);
                    ID id = null;
                    try {
                        id = cf.createBBObjectId(namespace, baseURL, idStr);
                    } catch (final NumberFormatException e) {
                        e.printStackTrace();
                    } catch (final IDCreateException e) {
                        e.printStackTrace();
                    }
                    lastCat = (Category) cf.createBBObject(id, name, null);
                    forums.put(id, lastCat);
                }
            }
        }
        return forums;
    }

    @Override
    public IBBObjectFactory getThreadFactory() {
        return new ThreadFactory();
    }

    @Override
    public IPatternDescriptor getThreadPattern() {
        return DefaultPatternDescriptor.defaultCustom(Pattern.compile("<a href=\"viewtopic.php\\?t=([0-9]+)(?:.*?)\" class=\"topictitle\">(.*)</a>(?:.*?)<span class=\"name\">(.+?)</span>", Pattern.DOTALL), new String[] { "id", "name", "authorInfo" });
    }

    @Override
    public IBBObjectFactory getMemberFactory() {
        return new MemberFactory();
    }

    public Pattern getMemberNamePattern() {
        return Pattern.compile("Viewing profile :: (.*?)</th>");
    }

    @Override
    public IPatternDescriptor getAuthorInfoMemberPattern() {
        return DefaultPatternDescriptor.defaultIdAndName(Pattern.compile("<a href=\"profile.php\\?mode=viewprofile&amp;u=([0-9]+?)\">(.*?)</a>"));
    }

    @Override
    public IBBObjectFactory getGuestFactory() {
        return new GuestFactory();
    }

    /*
	 * 
	 * <table class="forumline" width="100%" cellspacing="1" cellpadding="4"
	 * border="0"> <tr> <th class="thHead" height="25"><b>Information</b></th>
	 * 
	 * </tr> <tr> <td class="row1"><table width="100%" cellspacing="0"
	 * cellpadding="1" border="0"> <tr> <td>&nbsp;</td> </tr> <tr>
	 * <td align="center"><span class="gen">The topic or post you requested
	 * does not exist</span></td>
	 * 
	 * </tr> <tr> <td>&nbsp;</td> </tr> </table></td> </tr> </table>
	 */
    public static final Pattern PAT_MSG_INFORMATION = Pattern.compile("<table class=\"forumline\"(?:.*?)" + "<th class=\"thHead\"(?:.*?)><b>Information</b></th>(?:.*?)" + "<td align=\"center\"><span class=\"gen\">(.*?)</span></td>", Pattern.DOTALL);

    public String parseInformationMessage(CharSequence seq) {
        String msg = null;
        final Matcher m = PAT_MSG_INFORMATION.matcher(seq);
        if (m.find()) {
            msg = "PHPBB: " + m.group(1);
        }
        return msg;
    }

    private BBException createPHPBBException(String msg, CharSequence seq) {
        final String phpBBmsg = parseInformationMessage(seq);
        if (phpBBmsg != null) {
            return new BBException(msg, new PHPBBException(phpBBmsg));
        } else {
            return new BBException(msg);
        }
    }

    public static final Pattern PAT_THEAD_ATTRS = Pattern.compile(// .compile("<title>(?:.*?) :: View topic - (.*?)</title>");
    "<a class=\"maintitle\" href=\"viewtopic.php\\?t=([0-9]+)(?:.*?)\">(.*?)</a>");

    public static final Pattern PAT_THEAD_ATTRS_FORUM = Pattern.compile("<link rel=\"up\" href=\"viewforum.php\\?f=([0-9]+?)\" title=\"(.*?)\" />");

    public static final IPatternDescriptor PD_THREAD_ATTRS = DefaultPatternDescriptor.defaultIdAndName(PAT_THEAD_ATTRS);

    public static final IPatternDescriptor PD_THREAD_ATTRS_FORUM = DefaultPatternDescriptor.defaultIdAndName(PAT_THEAD_ATTRS_FORUM);

    public Thread parseThreadPageForThreadAttributes(CharSequence seq) throws BBException {
        final Thread t = (Thread) genericParser.parseSingleIdName(PD_THREAD_ATTRS, seq, new ThreadFactory());
        if (t != null) {
            final Forum f = (Forum) genericParser.parseSingleIdName(PD_THREAD_ATTRS_FORUM, seq, new ForumFactory());
            t.forum = f;
            return t;
        } else {
            throw createPHPBBException("Failed to parse the thread.", seq);
        }
    }

    public static final Pattern PAT_MSG_TIMESTAMP = Pattern.compile("Posted: (.*?)<span class=\"gen\">&nbsp;</span>");

    public static final Pattern PAT_MSG = Pattern.compile("<tr>(?:.*?)<td width=\"150\" align=\"left\" valign=\"top\" class=\"row(?:[12]{1})\"><span class=\"name\">(.*?)<script language=\"JavaScript\"", Pattern.DOTALL);

    public static final Pattern PAT_MSG_USERID = Pattern.compile("profile.php\\?mode=viewprofile&amp;u=([0-9]+)");

    public static final Pattern PAT_MSG_POSTID_USERNAME = Pattern.compile("<a name=\"([0-9]+)\"></a><b>(.*?)</b></span>");

    public static final Pattern PAT_MSG_TITLE = Pattern.compile("Post subject: (.*?)</span>");

    // <td colspan="2"><span class="postbody">test</span><span
    // class="gensmall"></span></td>
    public static final Pattern PAT_MSG_MESSAGE = Pattern.compile("<td colspan=\"2\"><span class=\"postbody\">(.*?)</span><span class=\"gensmall\"></span></td>", Pattern.DOTALL);

    public String parseMessageId(String msgContent) {
        final Matcher matcher = PAT_MSG_POSTID_USERNAME.matcher(msgContent);
        if (matcher.find()) {
            return new String(matcher.group(1));
        }
        return null;
    }

    public void parseMessage(final CharSequence seq) {
    /*
		 * String username = null; Matcher matcher; // Match date
		 * //message.setTime(parseTimestamp(str)); // Match user id and name
		 * matcher = PAT_MSG_POSTID_USERNAME.matcher(seq); if (matcher.find()) {
		 * username = StringUtil.simpleStripHTML(matcher.group(2));
		 * message.setId(matcher.group(1)); } matcher =
		 * PAT_MSG_USERID.matcher(str); if (matcher.find()) {
		 * message.setAuthor(app.userFor(matcher.group(1), username)); } else {
		 * message.setAuthor(app.userFor(null, username)); } // Match title
		 * matcher = PAT_MSG_TITLE.matcher(seq); if (matcher.find()) {
		 * message.setTitle(matcher.group(1)); } // Match message matcher =
		 * PAT_MSG_MESSAGE.matcher(seq); if (matcher.find()) {
		 * message.setMessage(StringUtil.stripHTMLFullTrim(matcher.group(1))); }
		 */
    }

    public ThreadMessage parseRequestedMessage(final ThreadMessageID id, final CharSequence seq) throws BBException {
        final ThreadMessageFactory tmf = new ThreadMessageFactory();
        // lastRead = -1 the one we want
        ThreadMessageID lastReadId = null;
        try {
            lastReadId = (ThreadMessageID) tmf.createBBObjectId(namespace, baseURL, String.valueOf(id.getLongValue() - 1));
        } catch (final IDCreateException e) {
            e.printStackTrace();
        }
        final List<ThreadMessage> msgs = parseMessages2(seq, lastReadId, true);
        if (msgs.size() > 0) {
            return msgs.get(0);
        }
        return null;
    }

    public List<ThreadMessage> parseMessages2(final CharSequence seq, final ThreadMessageID lastReadId, boolean desc) throws BBException {
        Matcher m;
        ThreadMessage msg;
        final List<ThreadMessage> messages = new ArrayList<ThreadMessage>();
        m = PAT_MSG.matcher(seq);
        while (m.find()) {
            final String msgSrc = m.group(1);
            msg = parseMessage2(msgSrc, lastReadId);
            if (msg != null) {
                if (desc) {
                    messages.add(0, msg);
                } else {
                    messages.add(msg);
                }
            }
        }
        return messages;
    }

    @Override
    public Long parseTimestamp(CharSequence seq) {
        Long l = null;
        final Locale locale = Locale.ENGLISH;
        final String dateFormat = "EEE MMM d, yyyy";
        final String timeFormat = "h:mm aa";
        final String dateTimeSeparator = " ";
        final DateFormat fmtTimestamp = new SimpleDateFormat(dateFormat + dateTimeSeparator + timeFormat, locale);
        final DateFormat fmtTime = new SimpleDateFormat(timeFormat, locale);
        final String timestamp = new StringBuilder(seq).toString();
        /*
		 * timestamp = timestamp.replaceAll("1st", "1"); timestamp =
		 * timestamp.replaceAll("2nd", "2"); timestamp =
		 * timestamp.replaceAll("3rd", "3"); timestamp =
		 * timestamp.replaceAll("th", "");
		 */
        if (timestamp.startsWith("Today") || timestamp.startsWith("Yesterday")) {
            final String[] s = timestamp.split(dateTimeSeparator);
            try {
                final Calendar now = Calendar.getInstance();
                if ("Yesterday".equals(s[0])) {
                    now.add(Calendar.DATE, -1);
                }
                final Date d = fmtTime.parse(s[1]);
                final Calendar then = Calendar.getInstance(fmtTime.getTimeZone());
                then.setTime(d);
                then.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE));
                l = new Long(then.getTimeInMillis());
            } catch (final ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                l = new Long(fmtTimestamp.parse(timestamp).getTime());
            } catch (final ParseException e) {
                e.printStackTrace();
            }
        }
        return l;
    }

    private ThreadMessage parseMessage2(final CharSequence seq, final ThreadMessageID lastReadId) {
        ThreadMessage msg = null;
        Matcher m;
        m = PAT_MSG_POSTID_USERNAME.matcher(seq);
        if (m.find()) {
            final ThreadMessageFactory tmf = new ThreadMessageFactory();
            String idStr = m.group(1);
            ThreadMessageID id = null;
            try {
                id = (ThreadMessageID) tmf.createBBObjectId(namespace, baseURL, idStr);
            } catch (final IDCreateException e1) {
                e1.printStackTrace();
            }
            if (lastReadId == null || id.getLongValue() > lastReadId.getLongValue()) {
                final String uname = new String(m.group(2));
                msg = new ThreadMessage(id, null);
                m = PAT_MSG_TIMESTAMP.matcher(seq);
                if (m.find()) {
                    msg.timePosted = new Date(parseTimestamp(new String(m.group(1))).longValue());
                }
                m = PAT_MSG_TITLE.matcher(seq);
                m.find();
                msg.setNameInternal(new String(m.group(1)));
                m = PAT_MSG_MESSAGE.matcher(seq);
                m.find();
                final String message = StringUtil.stripHTMLFullTrim(m.group(1));
                msg.message = message;
                m = PAT_MEMBER_ID_FROM_LINK.matcher(seq);
                if (m.find()) {
                    final MemberFactory mf = new MemberFactory();
                    idStr = m.group(1);
                    ID id2 = null;
                    try {
                        id2 = mf.createBBObjectId(namespace, baseURL, idStr);
                    } catch (final NumberFormatException e) {
                        e.printStackTrace();
                    } catch (final IDCreateException e) {
                        e.printStackTrace();
                    }
                    msg.author = new Member(id2, uname);
                } else {
                    final GuestFactory gf = new GuestFactory();
                    ID id2 = null;
                    try {
                        id2 = gf.createBBObjectId(namespace, baseURL, null);
                    } catch (final IDCreateException e) {
                        e.printStackTrace();
                    }
                    msg.author = new Member(id2, uname);
                }
            }
        }
        return msg;
    }

    public Map<ID, ThreadMessage> parseMessages(final CharSequence seq, final boolean newOnly) throws BBException {
        Matcher matcher;
        String title;
        ThreadMessage msg;
        final Map<ID, ThreadMessage> messages = new HashMap<ID, ThreadMessage>();
        matcher = PAT_MSG.matcher(seq);
        boolean anyFound = false;
        while (matcher.find()) {
            anyFound = true;
            title = StringUtil.stripHTMLTrim(matcher.group(3));
            if (StringUtil.notEmptyStr(title)) {
                final ThreadMessageFactory tmf = new ThreadMessageFactory();
                final String idStr = matcher.group(1);
                ID id = null;
                try {
                    id = tmf.createBBObjectId(namespace, baseURL, idStr);
                } catch (final NumberFormatException e) {
                    e.printStackTrace();
                } catch (final IDCreateException e) {
                    e.printStackTrace();
                }
                msg = (ThreadMessage) tmf.createBBObject(id, title, null);
                messages.put(id, msg);
            }
        }
        if (!anyFound) {
            throw createPHPBBException("No messages found!", seq);
        }
        return messages;
    }

    public static final Pattern PAT_PAGES = Pattern.compile("<span class=\"nav\">Page <b>([0-9]+)</b> of <b>([0-9]+)</b></span>");

    public int parseNextPage(CharSequence seq) {
        final Matcher m = PAT_PAGES.matcher(seq);
        int next = -1;
        if (m.find()) {
            final int current = Integer.parseInt(m.group(1));
            final int last = Integer.parseInt(m.group(2));
            if (current < last) {
                next = current + 1;
            }
        }
        return next;
    }

    public int parsePrevPage(CharSequence seq) {
        final Matcher m = PAT_PAGES.matcher(seq);
        int prev = -1;
        if (m.find()) {
            final int current = Integer.parseInt(m.group(1));
            if (current > 1) {
                prev = current - 1;
            }
        }
        return prev;
    }

    /*
	 * <tr> <td class="row1" width="20%"><span class="gen">Group name:</span></td>
	 * <td class="row2"><span class="gen"><b>Zerobot</b></span></td>
	 * 
	 * </tr> <tr> <td class="row1" width="20%"><span class="gen">Group
	 * description:</span></td> <td class="row2"><span class="gen">Zerobot
	 * identities</span></td> </tr>
	 */
    public static final Pattern PAT_GROUP = Pattern.compile("<form action=\"groupcp.php\\?g=([0-9]+?)\" method=\"post\">" + "(?:.*?)<tr>" + "(?:.*?)<td class=\"row1\"(?:.*?)><span class=\"gen\">Group name:</span></td>" + "(?:.*?)<td class=\"row2\"(?:.*?)><span class=\"gen\">(.*?)</span></td>" + "(?:.*?)</tr>" + "(?:.*?)<tr>" + "(?:.*?)<td class=\"row1\"(?:.*?)><span class=\"gen\">Group description:</span></td>" + "(?:.*?)<td class=\"row2\"(?:.*?)><span class=\"gen\">(.*?)</span></td>" + "(?:.*?)</tr>" + "(?:.*?)</form>", Pattern.DOTALL);

    public MemberGroup parseMemberGroup(CharSequence seq) {
        final Matcher m = PAT_GROUP.matcher(seq);
        if (m.find()) {
            final MemberGroupFactory mgf = new MemberGroupFactory();
            final String idStr = m.group(1);
            final String name = StringUtil.stripHTMLTrim(m.group(2));
            final String desc = StringUtil.stripHTMLTrim(m.group(3));
            ID id = null;
            try {
                id = mgf.createBBObjectId(namespace, baseURL, idStr);
            } catch (final IDCreateException e) {
                e.printStackTrace();
            }
            final MemberGroup grp = (MemberGroup) mgf.createBBObject(id, name, null);
            grp.setDescription(desc);
            return grp;
        }
        return null;
    }

    /*
	 * <select name="g"><option value="7">Unit Test Group</option><option
	 * value="4">Zerobot</option></select> TODO this didn't work for several
	 * groups, so I split into two patterns
	 */
    public static final Pattern PAT_GROUPS = Pattern.compile("<select name=\"g\">" + "(?:<option value=\"([0-9]+?)\">(.*?)</option>?)" + "</select>");

    public static final Pattern PAT_GROUPS_GROUP = Pattern.compile("<option value=\"([0-9]+?)\">(.*?)</option>");

    @Override
    public IBBObjectFactory getMemberGroupFactory() {
        return new MemberGroupFactory();
    }

    public Pattern getMemberGroupContainerPattern() {
        return Pattern.compile("<select name=\"g\">" + "(?:<option value=\"([0-9]+?)\">(.*?)</option>?)" + "</select>");
    }

    public IPatternDescriptor getMemberGroupPattern() {
        return DefaultPatternDescriptor.defaultIdAndName(Pattern.compile("<option value=\"([0-9]+?)\">(.*?)</option>"));
    }

    @Deprecated
    private static final Pattern PAT_MEMBER_ID_FROM_LINK = Pattern.compile("<a href=\"profile.php\\?mode=viewprofile&amp;u=([0-9]+?)\">");

    public IPatternDescriptor getMemberPattern() {
        return DefaultPatternDescriptor.defaultIdAndName(Pattern.compile("<a href=\"profile.php\\?mode=viewprofile&amp;u=([0-9]+?)\" class=\"gen\">(.*?)</a>"));
    }

    private static final Pattern PAT_TITLE = Pattern.compile("<title>(.*?)</title>");

    public String parseTitle(CharSequence seq) {
        final Matcher m = PAT_TITLE.matcher(seq);
        if (m.find()) {
            final String title = new String(m.group(1));
            return title;
        }
        return null;
    }

    @Override
    public void throwException(final String msg, final CharSequence seq) throws BBException {
        throw createPHPBBException(msg, seq);
    }
}
