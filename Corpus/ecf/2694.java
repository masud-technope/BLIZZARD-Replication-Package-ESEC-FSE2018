package org.eclipse.ecf.internal.bulletinboard.commons;

import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.ecf.bulletinboard.BBException;
import org.eclipse.ecf.bulletinboard.IBBObject;
import org.eclipse.ecf.bulletinboard.IMember;
import org.eclipse.ecf.bulletinboard.IMemberGroup;
import org.eclipse.ecf.bulletinboard.IThread;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.internal.bulletinboard.commons.parsing.GenericParser;
import org.eclipse.ecf.internal.bulletinboard.commons.parsing.IPatternDescriptor;
import org.eclipse.ecf.internal.bulletinboard.commons.util.StringUtil;

public abstract class AbstractParser {

    protected Namespace namespace;

    protected URL baseURL;

    protected GenericParser genericParser;

    protected  AbstractParser(Namespace namespace, URL baseURL) {
        this.namespace = namespace;
        this.baseURL = baseURL;
        this.genericParser = new GenericParser(namespace, baseURL);
    }

    public abstract void throwException(String msg, CharSequence seq) throws BBException;

    public abstract Long parseTimestamp(CharSequence seq);

    public abstract IPatternDescriptor getThreadPattern();

    public abstract IBBObjectFactory getThreadFactory();

    public abstract IPatternDescriptor getAuthorInfoMemberPattern();

    public Map<ID, IThread> parseThreads(final CharSequence seq) {
        IPatternDescriptor pattern = getThreadPattern();
        IBBObjectFactory factory = getThreadFactory();
        Matcher m = pattern.getPattern().matcher(seq);
        Map<ID, IThread> threads = new LinkedHashMap<ID, IThread>();
        while (m.find()) {
            Map<String, Object> values = pattern.getValueMap(m);
            ID id = null;
            try {
                id = factory.createBBObjectId(namespace, baseURL, (String) values.get(IPatternDescriptor.ID_PARAM));
            } catch (IDCreateException e) {
                e.printStackTrace();
            }
            String name = StringUtil.stripHTMLTrim((String) values.get(IPatternDescriptor.NAME_PARAM));
            String authorInfo = (String) values.get("authorInfo");
            IBBObject member = genericParser.parseSingleIdName(getAuthorInfoMemberPattern(), authorInfo, getMemberFactory());
            if (member != null) {
                values.put("author", member);
            } else {
                IBBObjectFactory gf = getGuestFactory();
                String guestName = StringUtil.stripHTMLTrim(authorInfo);
                ID guestID = null;
                try {
                    guestID = gf.createBBObjectId(namespace, baseURL, guestName);
                } catch (IDCreateException e) {
                    e.printStackTrace();
                }
                values.put("author", getGuestFactory().createBBObject(guestID, guestName, null));
            }
            IThread obj = (IThread) factory.createBBObject(id, new String(name), values);
            threads.put(id, obj);
        }
        return threads;
    }

    public abstract Pattern getMemberNamePattern();

    public abstract IBBObjectFactory getMemberFactory();

    public abstract IBBObjectFactory getGuestFactory();

    public IMember parseMemberPageForName(final CharSequence seq, ID memberID) {
        Matcher m = getMemberNamePattern().matcher(seq);
        if (m.find()) {
            return (IMember) getMemberFactory().createBBObject(memberID, new String(m.group(1)), null);
        }
        return null;
    }

    public abstract IPatternDescriptor getMemberPattern();

    public Map<ID, IMember> parseMembers(final CharSequence seq) {
        // TODO is stripHTMLTrim needed? GenericParser doesn't do it
        Map<ID, IBBObject> objects = genericParser.parseMultiIdName(getMemberPattern(), seq, getMemberFactory(), true);
        Map<ID, IMember> members = new LinkedHashMap<ID, IMember>(objects.size());
        for (IBBObject obj : objects.values()) {
            members.put(obj.getID(), (IMember) obj);
        }
        return members;
    }

    public abstract IBBObjectFactory getMemberGroupFactory();

    public abstract Pattern getMemberGroupContainerPattern();

    public abstract IPatternDescriptor getMemberGroupPattern();

    public Map<ID, IMemberGroup> parseMemberGroups(final CharSequence seq) throws BBException {
        Matcher m = getMemberGroupContainerPattern().matcher(seq);
        if (m.find()) {
            // TODO is stripHTMLTrim needed? GenericParser doesn't do it
            Map<ID, IBBObject> objects = genericParser.parseMultiIdName(getMemberGroupPattern(), m.group(0), getMemberGroupFactory(), true);
            Map<ID, IMemberGroup> members = new LinkedHashMap<ID, IMemberGroup>(objects.size());
            for (IBBObject obj : objects.values()) {
                members.put(obj.getID(), (IMemberGroup) obj);
            }
            return members;
        } else {
            throwException("Could not find member groups.", seq);
        }
        return Collections.emptyMap();
    }
}
