/*******************************************************************************
 * Copyright (c) 2007, 2008 Remy Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *    Markus Kuppe <mkuppe@versant.com> - bug 184036
 *    Nick Boldt <codeslave@ca.ibm.com> - bug 206528, 209410
 *    Dominik Goepel <dominik.goepel@gmx.de> - bug 216644
 ******************************************************************************/
package org.eclipse.ecf.internal.presence.bot.kosmos;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.bot.IChatRoomBotEntry;
import org.eclipse.ecf.presence.bot.IChatRoomMessageHandler;
import org.eclipse.ecf.presence.chatroom.IChatRoomContainer;
import org.eclipse.ecf.presence.chatroom.IChatRoomMessage;
import org.eclipse.ecf.presence.chatroom.IChatRoomMessageSender;
import org.eclipse.ecf.presence.im.IChatID;
import org.eclipse.ecf.presence.im.IChatMessageEvent;
import org.eclipse.ecf.presence.im.IChatMessageSender;
import org.eclipse.osgi.util.NLS;

public class ChatRoomMessageHandler implements IChatRoomMessageHandler {

    //$NON-NLS-1$
    private static final String NEWLINE = System.getProperty("line.separator");

    private static final String LICENSE = //$NON-NLS-1$
    "###############################################################################" + NEWLINE + //$NON-NLS-1$
    "# Copyright (c) 2007, 2008 Remy Suen and others." + NEWLINE + //$NON-NLS-1$
    "# All rights reserved. This program and the accompaning materials" + NEWLINE + //$NON-NLS-1$
    "# are made available under the terms of the Eclipse Public License v1.0" + NEWLINE + //$NON-NLS-1$
    "# which accompanies this distribution, and is available at" + NEWLINE + //$NON-NLS-1$
    "# http://www.eclipse.org/legal/epl-v10.html" + NEWLINE + //$NON-NLS-1$
    "#" + NEWLINE + //$NON-NLS-1$
    "# Contributors:" + NEWLINE + //$NON-NLS-1$
    "#    Remy Suen <remy.suen@gmail.com> - initial API and implementation" + NEWLINE + //$NON-NLS-1$
    "#    Markus Kuppe <mkuppe@versant.com> - bug 184036" + NEWLINE + //$NON-NLS-1$
    " *    Nick Boldt <codeslave@ca.ibm.com> - bug 206528, 209410" + NEWLINE + //$NON-NLS-1$
    " *    Dominik Goepel <dominik.goepel@gmx.de> - bug 216644" + NEWLINE + " *    Nitin Dahyabhai <nitind@us.ibm.com> - bug 308908" + NEWLINE + //$NON-NLS-1$
    "################################################################################";

    //$NON-NLS-1$
    private static final String BUG_DATABASE_PREFIX = "https://bugs.eclipse.org/bugs/show_bug.cgi?id=";

    //$NON-NLS-1$
    private static final String BUG_DATABASE_POSTFIX = "&ctype=xml";

    //$NON-NLS-1$
    private static final String SHORT_DESC_OPEN_TAG = "<short_desc>";

    //$NON-NLS-1$
    private static final String SHORT_DESC_CLOSE_TAG = "</short_desc>";

    //$NON-NLS-1$
    private static final String PRODUCT_OPEN_TAG = "<product>";

    //$NON-NLS-1$
    private static final String PRODUCT_CLOSE_TAG = "</product>";

    //$NON-NLS-1$
    private static final String COMPONENT_OPEN_TAG = "<component>";

    //$NON-NLS-1$
    private static final String COMPONENT_CLOSE_TAG = "</component>";

    //$NON-NLS-1$
    private static final String VERSION_OPEN_TAG = "<version>";

    //$NON-NLS-1$
    private static final String VERSION_CLOSE_TAG = "</version>";

    //$NON-NLS-1$
    private static final String REP_PLATFORM_OPEN_TAG = "<rep_platform>";

    //$NON-NLS-1$
    private static final String REP_PLATFORM_CLOSE_TAG = "</rep_platform>";

    //$NON-NLS-1$
    private static final String OP_SYS_OPEN_TAG = "<op_sys>";

    //$NON-NLS-1$
    private static final String OP_SYS_CLOSE_TAG = "</op_sys>";

    //$NON-NLS-1$
    private static final String BUG_STATUS_OPEN_TAG = "<bug_status>";

    //$NON-NLS-1$
    private static final String BUG_STATUS_CLOSE_TAG = "</bug_status>";

    //$NON-NLS-1$
    private static final String RESOLUTION_OPEN_TAG = "<resolution>";

    //$NON-NLS-1$
    private static final String RESOLUTION_CLOSE_TAG = "</resolution>";

    //$NON-NLS-1$
    private static final String BUG_SEVERITY_OPEN_TAG = "<bug_severity>";

    //$NON-NLS-1$
    private static final String BUG_SEVERITY_CLOSE_TAG = "</bug_severity>";

    //$NON-NLS-1$
    private static final String ASSIGNED_TO_CLOSE_TAG = "</assigned_to>";

    //$NON-NLS-1$
    private static final String BUG_NOT_FOUND_TAG = "<bug error=\"NotFound\">";

    private static final File HTML_FILE_MESSAGES = new File(//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    System.getProperty("user.home") + File.separator + "public_html" + File.separator + "messages.html");

    private static final File HTML_FILE_COMMANDS = new File(//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    System.getProperty("user.home") + File.separator + "public_html" + File.separator + "commands.html");

    //$NON-NLS-1$
    private static final String URL_REGEX = "(http://.+|https://.+|ftp://.+)";

    //$NON-NLS-1$
    private static final String CMD_REGEX = "(~.+)";

    //$NON-NLS-1$
    private static final String BINDING_REGEX = "(\\{[0-9]+\\})";

    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    private static final Pattern CMD_PATTERN = Pattern.compile(CMD_REGEX);

    private static final Pattern BINDING_PATTERN = Pattern.compile(BINDING_REGEX);

    private Map messageSenders;

    private Map newsgroups;

    private Set operators;

    private Properties messages;

    private Properties commands;

    private JavadocAnalyzer analyzer;

    private IContainer container;

    private IChatMessageSender chatMessageSender;

    /**
	 * The name of this bot. May be <code>null</code> if the name could not be retrieved from the container.
	 */
    private String botName;

    private String password;

    private static final String xmlDecode(String string) {
        if (//$NON-NLS-1$
        string == null || string.equals("")) {
            //$NON-NLS-1$
            return "";
        }
        //$NON-NLS-1$
        int index = string.indexOf("&amp;");
        while (index != -1) {
            string = string.substring(0, index) + '&' + string.substring(index + 5);
            //$NON-NLS-1$
            index = string.indexOf("&amp;", index + 1);
        }
        //$NON-NLS-1$
        index = string.indexOf("&quot;");
        while (index != -1) {
            string = string.substring(0, index) + '"' + string.substring(index + 6);
            //$NON-NLS-1$
            index = string.indexOf("&quot;", index + 1);
        }
        //$NON-NLS-1$
        index = string.indexOf("&apos;");
        while (index != -1) {
            string = string.substring(0, index) + '\'' + string.substring(index + 6);
            //$NON-NLS-1$
            index = string.indexOf("&apos;", index + 1);
        }
        //$NON-NLS-1$
        index = string.indexOf("&lt;");
        while (index != -1) {
            string = string.substring(0, index) + '<' + string.substring(index + 4);
            //$NON-NLS-1$
            index = string.indexOf("&lt;", index + 1);
        }
        //$NON-NLS-1$
        index = string.indexOf("&gt;");
        while (index != -1) {
            string = string.substring(0, index) + '>' + string.substring(index + 4);
            //$NON-NLS-1$
            index = string.indexOf("&gt;", index + 1);
        }
        return string;
    }

    public  ChatRoomMessageHandler() {
        messageSenders = new HashMap();
        analyzer = new JavadocAnalyzer();
        try {
            parseOperators();
        } catch (Exception e) {
            operators = Collections.EMPTY_SET;
        }
        try {
            parseMessages();
        } catch (Exception e) {
            messages = new Properties();
        }
        try {
            parseCommands();
        } catch (Exception e) {
            commands = new Properties();
        }
        try {
            writeCommandsToHTML();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            parseNewsgroup();
        } catch (Exception e) {
            newsgroups = Collections.EMPTY_MAP;
        }
    }

    private void parseOperators() throws IOException {
        Properties properties = new Properties();
        InputStream stream = FileLocator.openStream(Activator.getBundle(), new Path("operators.properties"), false);
        properties.load(stream);
        stream.close();
        String operatorString = properties.getProperty("operators");
        String[] operators = operatorString.split(",");
        this.operators = new HashSet(operators.length);
        for (int i = 0; i < operators.length; i++) {
            this.operators.add(operators[i].trim());
        }
    }

    private void parseMessages() throws IOException {
        messages = new Properties();
        InputStream stream = FileLocator.openStream(Activator.getBundle(), new Path("messages.properties"), false);
        messages.load(stream);
        stream.close();
    }

    /*
	 * collect commands from custom.properties -- only want those for which
	 * there's a matching *_Regex key/value pair, eg., EclipseHelp +
	 * EclipseHelp_Regex
	 */
    private void parseCommands() throws IOException {
        commands = new Properties();
        Properties commandsAll = new Properties();
        commandsAll.load(ChatRoomMessageHandler.class.getResourceAsStream(//$NON-NLS-1$
        "custom.properties"));
        Enumeration keys = commandsAll.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            //$NON-NLS-1$
            String keyRegex = key + "_Regex";
            if (commandsAll.keySet().contains(keyRegex)) {
                commands.setProperty(commandsAll.get(keyRegex).toString(), commandsAll.get(key).toString());
            }
        }
    }

    private void parseNewsgroup() throws IOException {
        Properties properties = new Properties();
        properties.load(JavadocAnalyzer.class.getResourceAsStream("newsgroup.txt"));
        newsgroups = new HashMap();
        for (Iterator it = properties.keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            Object value = properties.get(key);
            newsgroups.put(key, value);
            newsgroups.put(value, value);
        }
    }

    private void sendMessage(ID id, String message) {
        try {
            if (container != null) {
                IChatRoomMessageSender sender = (IChatRoomMessageSender) messageSenders.get(id);
                if (sender == null) {
                    chatMessageSender.sendChatMessage(id, message);
                } else {
                    sender.sendMessage(message);
                }
            }
        } catch (ECFException e) {
            e.printStackTrace();
            container.disconnect();
            container = null;
        }
    }

    private void sendBug(ID roomID, String target, String number, String comment) {
        String urlString = BUG_DATABASE_PREFIX + number;
        if (comment != null) {
            //$NON-NLS-1$
            urlString = urlString + "#c" + comment;
        }
        try {
            HttpURLConnection hURL = (HttpURLConnection) new URL(BUG_DATABASE_PREFIX + number + BUG_DATABASE_POSTFIX).openConnection();
            hURL.setAllowUserInteraction(true);
            hURL.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(hURL.getInputStream()));
            StringBuffer buffer = new StringBuffer();
            try {
                if (hURL.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    sendMessage(roomID, (target != null ? target + ": " : "") + NLS.bind(CustomMessages.Bug, number, urlString));
                    return;
                }
                String input = reader.readLine();
                synchronized (buffer) {
                    buffer.append(input);
                    while (input != null && input.indexOf(ASSIGNED_TO_CLOSE_TAG) == -1) {
                        if (input.indexOf(BUG_NOT_FOUND_TAG) != -1) /*
																		 * handle
																		 * case
																		 * where
																		 * bug
																		 * does
																		 * not
																		 * exist,
																		 * eg.
																		 * ~bug1234
																		 */
                        {
                            sendMessage(roomID, (target != null ? target + ": " : "") + NLS.bind(CustomMessages.getString(CustomMessages.Bug_Not_Found), number));
                            return;
                        }
                        input = reader.readLine();
                        buffer.append(input);
                    }
                }
                hURL.disconnect();
            } catch (EOFException e) {
                hURL.disconnect();
                sendMessage(roomID, (target != null ? target + ": " : "") + NLS.bind(CustomMessages.Bug, number, urlString));
                e.printStackTrace();
                return;
            }
            String webPage = buffer.toString();
            int summaryStartIndex = webPage.indexOf(SHORT_DESC_OPEN_TAG);
            int summaryEndIndex = webPage.indexOf(SHORT_DESC_CLOSE_TAG, summaryStartIndex);
            if (summaryStartIndex != -1 & summaryEndIndex != -1) {
                try {
                    String summary = webPage.substring(summaryStartIndex + SHORT_DESC_OPEN_TAG.length(), summaryEndIndex);
                    String product = webPage.substring(webPage.indexOf(PRODUCT_OPEN_TAG) + PRODUCT_OPEN_TAG.length(), webPage.indexOf(PRODUCT_CLOSE_TAG));
                    String component = webPage.substring(webPage.indexOf(COMPONENT_OPEN_TAG) + COMPONENT_OPEN_TAG.length(), webPage.indexOf(COMPONENT_CLOSE_TAG));
                    String version = webPage.substring(webPage.indexOf(VERSION_OPEN_TAG) + VERSION_OPEN_TAG.length(), webPage.indexOf(VERSION_CLOSE_TAG));
                    String platform = webPage.substring(webPage.indexOf(REP_PLATFORM_OPEN_TAG) + REP_PLATFORM_OPEN_TAG.length(), webPage.indexOf(REP_PLATFORM_CLOSE_TAG));
                    String os = webPage.substring(webPage.indexOf(OP_SYS_OPEN_TAG) + OP_SYS_OPEN_TAG.length(), webPage.indexOf(OP_SYS_CLOSE_TAG));
                    String status = webPage.substring(webPage.indexOf(BUG_STATUS_OPEN_TAG) + BUG_STATUS_OPEN_TAG.length(), webPage.indexOf(BUG_STATUS_CLOSE_TAG));
                    String severity = webPage.substring(webPage.indexOf(BUG_SEVERITY_OPEN_TAG) + BUG_SEVERITY_OPEN_TAG.length(), webPage.indexOf(BUG_SEVERITY_CLOSE_TAG));
                    String assignee = webPage.substring(webPage.substring(0, webPage.indexOf(ASSIGNED_TO_CLOSE_TAG)).lastIndexOf('>') + 1, webPage.indexOf(ASSIGNED_TO_CLOSE_TAG));
                    int resolutionStartIndex = webPage.indexOf(RESOLUTION_OPEN_TAG);
                    if (resolutionStartIndex != -1) {
                        String resolution = webPage.substring(resolutionStartIndex + RESOLUTION_OPEN_TAG.length(), webPage.indexOf(RESOLUTION_CLOSE_TAG));
                        sendMessage(roomID, (target != null ? target + ": " : "") + NLS.bind(CustomMessages.getString(CustomMessages.BugContent), new Object[] { number, urlString, product, component, version, platform, os, status, resolution, severity, assignee, xmlDecode(summary) }));
                    } else {
                        sendMessage(roomID, (target != null ? target + ": " : "") + NLS.bind(CustomMessages.getString(CustomMessages.BugContent2), new Object[] { number, urlString, product, component, version, platform, os, status, severity, assignee, xmlDecode(summary) }));
                    }
                } catch (RuntimeException e) {
                    sendMessage(roomID, (target != null ? target + ": " : "") + NLS.bind(CustomMessages.getString(CustomMessages.Bug), new Object[] { number, urlString }));
                }
            } else {
                sendMessage(roomID, (target != null ? target + ": " : "") + NLS.bind(CustomMessages.getString(CustomMessages.Bug), new Object[] { number, urlString }));
            }
        } catch (IOException e) {
            sendMessage(roomID, (target != null ? target + ": " : "") + NLS.bind(CustomMessages.getString(CustomMessages.Bug), new Object[] { number, urlString }));
            e.printStackTrace();
        }
    }

    private void sendNewsgroupSearch(ID roomID, String target, String query) {
        //$NON-NLS-1$
        String[] strings = query.split(" ");
        if (strings.length == 1) {
            sendMessage(roomID, (target != null ? target + ": " : "") + CustomMessages.getString(CustomMessages.NewsgroupSearch_Invalid));
            return;
        }
        for (int i = 0; i < strings.length; i++) {
            try {
                strings[i] = //$NON-NLS-1$
                URLEncoder.encode(//$NON-NLS-1$
                strings[i].trim(), //$NON-NLS-1$
                "UTF-8");
            } catch (UnsupportedEncodingException e) {
                strings[i] = URLEncoder.encode(strings[i].trim());
            }
        }
        /*
		 * support either a lookup in the newsgroups static list, or input of
		 * eclipse.foo.bar as a presumed valid group
		 */
        String newsgroup = strings[0].startsWith("eclipse.") ? "news." + strings[0] : (String) newsgroups.get(strings[0]);
        /*
		 * if newsgroup doesn't start with "eclipse." and lookup fails, we get
		 * back null; help the user when this happens
		 */
        if (newsgroup == null) {
            sendMessage(roomID, (target != null ? target + ": " : "") + CustomMessages.getString(CustomMessages.NewsgroupSearch_InvalidGroup));
            return;
        }
        StringBuffer buffer = new StringBuffer();
        synchronized (buffer) {
            for (int i = 1; i < strings.length; i++) {
                buffer.append(strings[i] + '+');
            }
            buffer.deleteCharAt(buffer.length() - 1);
        }
        sendMessage(roomID, (target != null ? target + ": " : "") + NLS.bind(CustomMessages.getString(CustomMessages.NewsgroupSearch), newsgroup, buffer.toString()));
    }

    private void sendGoogle(ID roomID, String target, String searchString) {
        searchString = searchString.replace(' ', '+');
        sendMessage(roomID, (target != null ? target + ": " : "") + NLS.bind(CustomMessages.getString(CustomMessages.Google), searchString));
    }

    private void sendWiki(ID roomID, String target, String articleName) {
        articleName = articleName.replace(' ', '_');
        sendMessage(roomID, (target != null ? target + ": " : "") + NLS.bind(CustomMessages.getString(CustomMessages.Wiki), articleName));
    }

    private void sendEclipseHelp(ID roomID, String target, String searchString) {
        searchString = searchString.replace(' ', '+');
        sendMessage(roomID, (target != null ? target + ": " : "") + NLS.bind(CustomMessages.getString(CustomMessages.EclipseHelp), searchString));
    }

    private void sendJavaDoc(ID roomID, String target, String parameter) {
        String message = null;
        int index = parameter.indexOf('#');
        if (index == -1) {
            message = analyzer.getJavadocs(parameter);
        } else {
            String className = parameter.substring(0, index);
            parameter = parameter.substring(index + 1);
            index = parameter.indexOf('(');
            if (index == -1) {
                message = className + '#' + parameter + " - " + analyzer.getJavadocs(className, parameter);
            } else {
                String method = parameter.substring(0, index);
                parameter = parameter.substring(index + 1);
                parameter = parameter.substring(0, parameter.indexOf(')'));
                String[] parameters = parameter.split(",");
                for (int i = 0; i < parameters.length; i++) {
                    parameters[i] = parameters[i].trim();
                }
                message = className + '#' + method + " - " + analyzer.getJavadocs(className, method, parameters);
            }
        }
        sendMessage(roomID, (target != null ? target + ": " : "") + message);
    }

    private void sendMessageList(ID roomID, String target) {
        sendMessage(roomID, (target != null ? target + ": " : "") + CustomMessages.getString(CustomMessages.MessageList));
    }

    private void sendSearchPlugins(ID roomID, String target, String searchString) {
        searchString = searchString.replace(' ', '+');
        sendMessage(roomID, (target != null ? target + ": " : "") + NLS.bind(CustomMessages.getString(CustomMessages.SearchPlugins), searchString));
    }

    private void sendCQ(ID roomID, String target, String id, String comment) {
        String suffix = id;
        if (comment != null) {
            suffix += "#c" + comment;
        }
        sendMessage(roomID, (target != null ? target + ": " : "") + NLS.bind(CustomMessages.getString(CustomMessages.CQ), id, suffix));
    }

    private void writeToHTML(File file, String title, Properties properties) throws IOException {
        FileWriter out = new FileWriter(file);
        out.write(//$NON-NLS-1$
        "<html>\n<head><title>" + title + "</title></head>\n<body>\n<table cellspacing=\"2\" cellpadding=\"2\" border=\"0\">\n");
        Set set = properties.keySet();
        String[] propertiesSorted = (String[]) set.toArray(new String[set.size()]);
        Arrays.sort(propertiesSorted);
        for (int i = 0; i < propertiesSorted.length; i++) {
            String output = properties.getProperty(propertiesSorted[i]);
            out.write(formatTableRow(propertiesSorted[i], output));
        }
        //$NON-NLS-1$
        out.write("</table>\n</body></html>\n");
        out.flush();
        try {
            out.close();
        } catch (IOException e) {
        }
    }

    private void writeMessagesToHTML() throws IOException {
        //$NON-NLS-1$
        writeToHTML(HTML_FILE_MESSAGES, "KOS-MOS Messages", messages);
    }

    private void writeCommandsToHTML() throws IOException {
        //$NON-NLS-1$
        writeToHTML(HTML_FILE_COMMANDS, "KOS-MOS Commands", commands);
    }

    private String formatTableRow(String key, String val) {
        return //$NON-NLS-1$
        "<tr valign=\"top\"><td><b>" + //$NON-NLS-1$ //$NON-NLS-2$ 
        key.replaceAll(" ", "&#160;") + "</b></td><td>" + text2html(val) + "</td></tr>\n<tr><td colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" width=\"100%\"/></td></tr>\n\n";
    }

    private String text2html(String val) {
        //$NON-NLS-1$
        StringTokenizer st = new StringTokenizer(val, " )(\"", true);
        StringBuffer sb = new StringBuffer();
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            Matcher patternMatcher = URL_PATTERN.matcher(tok);
            if (patternMatcher.matches()) {
                //$NON-NLS-1$
                sb.append(//$NON-NLS-1$
                "<a href=\"");
                sb.append(patternMatcher.group(1));
                //$NON-NLS-1$
                sb.append(//$NON-NLS-1$
                "\">");
                if (// break long
                patternMatcher.group(1).length() >= // break long
                120) // URLs
                {
                    StringTokenizer st2 = new StringTokenizer(patternMatcher.group(//$NON-NLS-1$
                    1), //$NON-NLS-1$
                    " /&", //$NON-NLS-1$
                    true);
                    StringBuffer sb2 = new StringBuffer();
                    while (st2.hasMoreTokens()) {
                        sb2.append(st2.nextToken());
                        if (sb2.length() >= 100) {
                            //$NON-NLS-1$ 
                            sb2.append(" ");
                        }
                    }
                    sb.append(sb2.toString().replaceAll(BINDING_REGEX, "<b style=\"color:green\">$1</b>"));
                } else {
                    sb.append(patternMatcher.group(1).replaceAll(BINDING_REGEX, "<b style=\"color:green\">$1</b>"));
                }
                //$NON-NLS-1$
                sb.append(//$NON-NLS-1$
                "</a>");
            } else {
                Matcher cmdMatcher = CMD_PATTERN.matcher(tok);
                if (cmdMatcher.matches()) {
                    sb.append("<b style=\"color:red\">");
                    sb.append(cmdMatcher.group(1));
                    //$NON-NLS-1$
                    sb.append("</b>");
                } else {
                    Matcher bindingMatcher = BINDING_PATTERN.matcher(tok);
                    if (bindingMatcher.matches()) {
                        sb.append("<b style=\"color:green\">");
                        sb.append(bindingMatcher.group(1));
                        //$NON-NLS-1$
                        sb.append("</b>");
                    } else {
                        sb.append(tok);
                    }
                }
            }
        }
        return sb.toString();
    }

    private boolean isProcessed(ID roomID, String target, String msg) {
        String reply = (String) messages.get(msg);
        if (reply == null) {
            return false;
        }
        //$NON-NLS-1$
        sendMessage(roomID, (target != null ? target + ": " : "") + reply);
        return true;
    }

    private void learn(ID roomID, String contents) {
        String key = contents.split(" ")[0].toLowerCase();
        try {
            URL url = FileLocator.find(Activator.getBundle(), new Path("messages.properties"), null);
            url = FileLocator.resolve(url);
            String property = messages.getProperty(key);
            if (property == null) {
                OutputStream stream = new FileOutputStream(url.getPath());
                messages.setProperty(key, contents.substring(key.length()).trim());
                messages.store(stream, LICENSE);
                writeMessagesToHTML();
                sendMessage(roomID, NLS.bind(CustomMessages.getString(CustomMessages.Learn_Reply), key));
            } else {
                sendMessage(roomID, NLS.bind(CustomMessages.getString(CustomMessages.Learn_Conflict), key, property));
            }
        } catch (Exception e) {
            sendMessage(roomID, NLS.bind(CustomMessages.getString(CustomMessages.Learn_Failure), key));
        }
    }

    private void update(ID roomID, String contents) {
        String key = contents.split(" ")[0].toLowerCase();
        try {
            URL url = FileLocator.find(Activator.getBundle(), new Path("messages.properties"), null);
            url = FileLocator.resolve(url);
            OutputStream stream = new FileOutputStream(url.getPath());
            messages.setProperty(key, contents.substring(key.length()).trim());
            messages.store(stream, LICENSE);
            writeMessagesToHTML();
            sendMessage(roomID, NLS.bind(CustomMessages.getString(CustomMessages.Learn_Update), key));
        } catch (Exception e) {
            sendMessage(roomID, NLS.bind(CustomMessages.getString(CustomMessages.Learn_Failure), key));
        }
    }

    private void remove(ID roomID, String contents) {
        String key = contents.split(" ")[0].toLowerCase();
        try {
            URL url = FileLocator.find(Activator.getBundle(), new Path("messages.properties"), null);
            url = FileLocator.resolve(url);
            OutputStream stream = new FileOutputStream(url.getPath());
            messages.remove(key);
            messages.store(stream, LICENSE);
            writeMessagesToHTML();
            sendMessage(roomID, NLS.bind(CustomMessages.getString(CustomMessages.Learn_Remove), key));
        } catch (Exception e) {
            sendMessage(roomID, NLS.bind(CustomMessages.getString(CustomMessages.Learn_Failure), key));
        }
    }

    private void send(ID fromID, ID roomID, String target, String msg) {
        /* handle operator-added messages - see messages.properties */
        if (isProcessed(roomID, target, msg)) {
            return;
        }
        /* handle custom commands - see custom.properties */
        Matcher cmdMatcher = null;
        Enumeration keys = commands.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            Pattern pattern = Pattern.compile(key);
            cmdMatcher = pattern.matcher(msg);
            if (cmdMatcher.matches()) {
                break;
            }
        }
        if (cmdMatcher != null && cmdMatcher.matches()) {
            if (//$NON-NLS-1$
            cmdMatcher.group(1).equals("add ")) {
                if (operators.contains(fromID.getName())) {
                    learn(roomID, cmdMatcher.group(2));
                } else {
                    sendMessage(roomID, NLS.bind(CustomMessages.getString(CustomMessages.No_Operation_Privileges), fromID.getName()));
                }
            } else if (//$NON-NLS-1$ //$NON-NLS-2$
            cmdMatcher.group(1).equals("set ") || cmdMatcher.group(1).equals("update ")) {
                if (operators.contains(fromID.getName())) {
                    update(roomID, cmdMatcher.group(2));
                } else {
                    sendMessage(roomID, NLS.bind(CustomMessages.getString(CustomMessages.No_Operation_Privileges), fromID.getName()));
                }
            } else if (//$NON-NLS-1$
            cmdMatcher.group(1).equals("remove ")) {
                if (operators.contains(fromID.getName())) {
                    remove(roomID, cmdMatcher.group(2));
                } else {
                    sendMessage(roomID, NLS.bind(CustomMessages.getString(CustomMessages.No_Operation_Privileges), fromID.getName()));
                }
            } else if (//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            cmdMatcher.group(1).equals("") || cmdMatcher.group(1).equals("bug") || cmdMatcher.group(1).equals("bug ")) {
                msg = cmdMatcher.group(2);
                int index = msg.indexOf('c');
                if (index == -1) {
                    try {
                        // try to parse the string to see that we have a valid
                        // number
                        Integer.parseInt(msg);
                        sendBug(roomID, target, msg, null);
                    } catch (NumberFormatException e) {
                    }
                } else {
                    try {
                        // try to parse the string to see that we have a valid
                        // number
                        Integer.parseInt(msg.substring(0, index));
                        sendBug(roomID, target, msg.substring(0, index), msg.substring(index + 1));
                    } catch (NumberFormatException e) {
                    }
                }
            } else if (//$NON-NLS-1$ //$NON-NLS-2$
            cmdMatcher.group(1).equals("cq") || cmdMatcher.group(1).equals("cq ")) {
                msg = cmdMatcher.group(2);
                int index = msg.indexOf('c');
                if (index == -1) {
                    try {
                        // try to parse the string to see that we have a valid
                        // number
                        Integer.parseInt(msg);
                        sendCQ(roomID, target, msg, null);
                    } catch (NumberFormatException e) {
                    }
                } else {
                    try {
                        // try to parse the string to see that we have a valid
                        // number
                        Integer.parseInt(msg.substring(0, index));
                        sendCQ(roomID, target, msg.substring(0, index), msg.substring(index + 1));
                    } catch (NumberFormatException e) {
                    }
                }
            } else if (//$NON-NLS-1$ //$NON-NLS-2$
            cmdMatcher.group(1).equals("javadoc ") || cmdMatcher.group(1).equals("api ")) {
                sendJavaDoc(roomID, target, cmdMatcher.group(2));
            } else if (//$NON-NLS-1$ //$NON-NLS-2$
            cmdMatcher.group(1).equals("news ") || cmdMatcher.group(1).equals("newsgroup ")) {
                sendNewsgroupSearch(roomID, target, cmdMatcher.group(2));
            } else if (//$NON-NLS-1$
            cmdMatcher.group(1).equals("g ")) {
                sendGoogle(roomID, target, cmdMatcher.group(2));
            } else if (//$NON-NLS-1$
            cmdMatcher.group(1).equals("wiki ")) {
                sendWiki(roomID, target, cmdMatcher.group(2));
            } else if (//$NON-NLS-1$
            cmdMatcher.group(1).equals("eh ")) {
                sendEclipseHelp(roomID, target, cmdMatcher.group(2));
            } else if (//$NON-NLS-1$
            cmdMatcher.group(1).equals("list")) {
                sendMessageList(roomID, target);
            } else if (//$NON-NLS-1$
            cmdMatcher.group(1).equals("searchplugins ")) {
                sendSearchPlugins(roomID, target, cmdMatcher.group(2));
            }
        }
    }

    private String[] parseInput(String msg) {
        if (//$NON-NLS-1$
        msg.startsWith("tell")) {
            msg = msg.substring(5);
            int index = msg.indexOf(' ');
            if (index == -1) {
                return null;
            }
            String user = msg.substring(0, index);
            msg = msg.substring(index + 1);
            index = msg.indexOf(' ');
            if (index == -1) {
                return null;
            }
            String tmp = msg.substring(0, index);
            if (//$NON-NLS-1$
            tmp.equals("about")) {
                msg = msg.substring(index + 1);
            }
            return new String[] { user, msg };
        } else {
            return new String[] { null, msg };
        }
    }

    private void handleMessage(ID fromID, ID roomID, String message) {
        try {
            String[] info = parseInput(message);
            if (info != null) {
                send(fromID, roomID, info[0], info[1]);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void handleRoomMessage(IChatRoomMessage message) {
        ID fromID = message.getFromID();
        String name = fromID.getName();
        if (// skip messages
        name.charAt(0) == '#' || name.equals(botName)) {
            // self
            return;
        }
        String msg = message.getMessage();
        switch(msg.charAt(0)) {
            case '~':
            case '!':
                msg = msg.substring(1).trim();
                int index = msg.indexOf(' ');
                if (index == -1) {
                    handleMessage(fromID, message.getChatRoomID(), msg.toLowerCase());
                } else {
                    handleMessage(fromID, message.getChatRoomID(), msg.substring(0, index).toLowerCase() + msg.substring(index));
                }
                break;
            default:
                String upperCase = msg.toUpperCase();
                if (upperCase.startsWith("KOS-MOS:") || upperCase.startsWith("KOS-MOS,")) {
                    msg = upperCase.substring(8).trim();
                    switch(msg.charAt(0)) {
                        case '~':
                        case '!':
                            msg = msg.substring(1).trim();
                            index = msg.indexOf(' ');
                            if (index == -1) {
                                handleMessage(fromID, message.getChatRoomID(), msg.toLowerCase());
                            } else {
                                handleMessage(fromID, message.getChatRoomID(), msg.substring(0, index).toLowerCase() + msg.substring(index));
                            }
                            break;
                    }
                } else {
                    String[] split = //$NON-NLS-1$
                    msg.split(//$NON-NLS-1$
                    "\\s");
                    for (int i = 0; i < split.length; i++) {
                        if (split[i].length() > 0) {
                            switch(split[i].charAt(0)) {
                                case '~':
                                case '!':
                                    handleMessage(fromID, message.getChatRoomID(), split[i].substring(1).trim().toLowerCase());
                                    break;
                            }
                        }
                    }
                }
        }
    }

    public void init(IChatRoomBotEntry robot) {
    // nothing to do
    }

    public void preChatRoomConnect(IChatRoomContainer roomContainer, ID roomID) {
        // retrieve our name
        ID connectedID = container.getConnectedID();
        botName = connectedID.getName();
        IChatID chatID = (IChatID) connectedID.getAdapter(IChatID.class);
        if (chatID != null) {
            botName = chatID.getUsername();
        }
        messageSenders.put(roomID, roomContainer.getChatRoomMessageSender());
        if (password != null) {
            try {
                sendMessage(IDFactory.getDefault().createStringID("nickserv"), "identify " + password);
            } catch (IDCreateException e) {
            }
        }
    }

    public void preContainerConnect(IContainer container, ID targetID) {
        File file = new File(Platform.getInstanceLocation().getURL().getPath(), "password.properties");
        if (file.exists()) {
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(file));
                password = properties.getProperty("password");
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        }
        this.container = container;
        IChatRoomContainer chatRoomContainer = (IChatRoomContainer) container.getAdapter(IChatRoomContainer.class);
        chatMessageSender = chatRoomContainer.getPrivateMessageSender();
        chatRoomContainer.addMessageListener(new IIMMessageListener() {

            public void handleMessageEvent(IIMMessageEvent e) {
                if (e instanceof IChatMessageEvent) {
                    IChatMessageEvent event = (IChatMessageEvent) e;
                    String msg = event.getChatMessage().getBody();
                    switch(msg.charAt(0)) {
                        case '~':
                        case '!':
                            handleMessage(event.getFromID(), event.getFromID(), msg.substring(1).trim());
                            break;
                        default:
                            handleMessage(event.getFromID(), event.getFromID(), msg.trim());
                            break;
                    }
                }
            }
        });
    }
}
