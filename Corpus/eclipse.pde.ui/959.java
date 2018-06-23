/*******************************************************************************
 * Copyright (c) 2000, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jacek Pospychala <jacek.pospychala@pl.ibm.com> - bugs 202583, 207061
 *     Jacek Pospychala <jacek.pospychala@pl.ibm.com> - bugs 207312, 100715
 *     Jacek Pospychala <jacek.pospychala@pl.ibm.com> - bugs 207344
 *******************************************************************************/
package org.eclipse.ui.internal.views.log;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IMemento;

class LogReader {

    private static final int SESSION_STATE = 10;

    public static final long MAX_FILE_LENGTH = 1024 * 1024;

    private static final int ONE_MEGA_BYTE_IN_BYTES = 1024 * 1024;

    private static final int ENTRY_STATE = 20;

    private static final int SUBENTRY_STATE = 30;

    private static final int MESSAGE_STATE = 40;

    private static final int STACK_STATE = 50;

    private static final int TEXT_STATE = 60;

    private static final int UNKNOWN_STATE = 70;

    public static LogSession parseLogFile(File file, long maxLogTailSizeInMegaByte, List entries, IMemento memento) {
        if (!file.exists())
            return null;
        if (//$NON-NLS-1$
        memento.getString(LogView.P_USE_LIMIT).equals("true") && memento.getInteger(LogView.P_LOG_LIMIT).intValue() == 0)
            return null;
        ArrayList parents = new ArrayList();
        LogEntry current = null;
        LogSession session = null;
        int writerState = UNKNOWN_STATE;
        StringWriter swriter = null;
        PrintWriter writer = null;
        int state = UNKNOWN_STATE;
        LogSession currentSession = null;
        BufferedReader reader = null;
        try {
            long maxTailSizeInBytes = maxLogTailSizeInMegaByte > 0 ? maxLogTailSizeInMegaByte * ONE_MEGA_BYTE_IN_BYTES : ONE_MEGA_BYTE_IN_BYTES;
            //$NON-NLS-1$
            reader = new BufferedReader(new InputStreamReader(new TailInputStream(file, maxTailSizeInBytes), "UTF-8"));
            for (; ; ) {
                String line0 = reader.readLine();
                if (line0 == null)
                    break;
                String line = line0.trim();
                if (line.startsWith(LogSession.SESSION)) {
                    state = SESSION_STATE;
                } else if (//$NON-NLS-1$
                line.startsWith("!ENTRY")) {
                    state = ENTRY_STATE;
                } else if (//$NON-NLS-1$
                line.startsWith("!SUBENTRY")) {
                    state = SUBENTRY_STATE;
                } else if (//$NON-NLS-1$
                line.startsWith("!MESSAGE")) {
                    state = MESSAGE_STATE;
                } else if (//$NON-NLS-1$
                line.startsWith("!STACK")) {
                    state = STACK_STATE;
                } else
                    state = TEXT_STATE;
                if (state == TEXT_STATE) {
                    if (writer != null) {
                        if (swriter.getBuffer().length() > 0)
                            writer.println();
                        writer.print(line0);
                    }
                    continue;
                }
                if (writer != null) {
                    setData(current, session, writerState, swriter);
                    writerState = UNKNOWN_STATE;
                    swriter = null;
                    writer.close();
                    writer = null;
                }
                if (state == STACK_STATE) {
                    swriter = new StringWriter();
                    writer = new PrintWriter(swriter, true);
                    writerState = STACK_STATE;
                } else if (state == SESSION_STATE) {
                    session = new LogSession();
                    session.processLogLine(line);
                    swriter = new StringWriter();
                    writer = new PrintWriter(swriter, true);
                    writerState = SESSION_STATE;
                    currentSession = updateCurrentSession(currentSession, session);
                    // if current session is most recent and not showing all sessions
                    if (currentSession.equals(session) && !//$NON-NLS-1$
                    memento.getString(LogView.P_SHOW_ALL_SESSIONS).equals(//$NON-NLS-1$
                    "true"))
                        entries.clear();
                } else if (state == ENTRY_STATE) {
                    if (// create fake session if there was no any
                    currentSession == null) {
                        currentSession = new LogSession();
                    }
                    try {
                        LogEntry entry = new LogEntry();
                        entry.setSession(currentSession);
                        entry.processEntry(line);
                        setNewParent(parents, entry, 0);
                        current = entry;
                        addEntry(current, entries, memento);
                    } catch (ParseException pe) {
                    }
                } else if (state == SUBENTRY_STATE) {
                    if (parents.size() > 0) {
                        try {
                            LogEntry entry = new LogEntry();
                            entry.setSession(session);
                            int depth = entry.processSubEntry(line);
                            setNewParent(parents, entry, depth);
                            current = entry;
                            LogEntry parent = (LogEntry) parents.get(depth - 1);
                            parent.addChild(entry);
                        } catch (ParseException pe) {
                        }
                    }
                } else if (state == MESSAGE_STATE) {
                    swriter = new StringWriter();
                    writer = new PrintWriter(swriter, true);
                    //$NON-NLS-1$
                    String //$NON-NLS-1$
                    message = "";
                    if (line.length() > 8)
                        message = line.substring(9);
                    if (current != null)
                        current.setMessage(message);
                    writerState = MESSAGE_STATE;
                }
            }
            if (swriter != null && current != null && writerState == STACK_STATE) {
                writerState = UNKNOWN_STATE;
                current.setStack(swriter.toString());
            }
        } catch (FileNotFoundException // do nothing
        e) {
        } catch (IOException // do nothing
        e) {
        } finally {
            if (file.length() > maxLogTailSizeInMegaByte && entries.size() == 0) {
                LogEntry entry = new LogEntry(new Status(IStatus.WARNING, Activator.PLUGIN_ID, NLS.bind(Messages.LogReader_warn_noEntryWithinMaxLogTailSize, new Long(maxLogTailSizeInMegaByte))));
                entry.setSession(currentSession == null ? new LogSession() : currentSession);
                entries.add(entry);
            }
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException // do nothing
            e1) {
            }
            if (writer != null) {
                setData(current, session, writerState, swriter);
                writer.close();
            }
        }
        return currentSession;
    }

    public static LogSession parseLogFile(File file, List entries, IMemento memento) {
        return parseLogFile(file, ONE_MEGA_BYTE_IN_BYTES, entries, memento);
    }

    /**
	 * Assigns data from writer to appropriate field of current Log Entry or Session,
	 * depending on writer state.
	 */
    private static void setData(LogEntry current, LogSession session, int writerState, StringWriter swriter) {
        if (writerState == STACK_STATE && current != null) {
            current.setStack(swriter.toString());
        } else if (writerState == SESSION_STATE && session != null) {
            session.setSessionData(swriter.toString());
        } else if (writerState == MESSAGE_STATE && current != null) {
            StringBuffer sb = new StringBuffer(current.getMessage());
            String continuation = swriter.toString();
            if (continuation.length() > 0)
                //$NON-NLS-1$
                sb.append(System.getProperty("line.separator")).append(//$NON-NLS-1$
                continuation);
            current.setMessage(sb.toString());
        }
    }

    /**
	 * Updates the {@link currentSession} to be the one that is not null or has most recent date.
	 * @param session
	 */
    private static LogSession updateCurrentSession(LogSession currentSession, LogSession session) {
        if (currentSession == null) {
            return session;
        }
        Date currentDate = currentSession.getDate();
        Date sessionDate = session.getDate();
        if (currentDate == null && sessionDate != null)
            return session;
        else if (currentDate != null && sessionDate == null)
            return session;
        else if (currentDate != null && sessionDate != null && sessionDate.after(currentDate))
            return session;
        return currentSession;
    }

    /**
	 * Adds entry to the list if it's not filtered. Removes entries exceeding the count limit.
	 *
	 * @param entry
	 * @param entries
	 * @param memento
	 */
    private static void addEntry(LogEntry entry, List entries, IMemento memento) {
        if (isLogged(entry, memento)) {
            entries.add(entry);
            if (//$NON-NLS-1$
            memento.getString(LogView.P_USE_LIMIT).equals("true")) {
                int limit = memento.getInteger(LogView.P_LOG_LIMIT).intValue();
                if (entries.size() > limit) {
                    entries.remove(0);
                }
            }
        }
    }

    /**
	 * Returns whether given entry is logged (true) or filtered (false).
	 *
	 * @param entry
	 * @param memento
	 * @return is entry logged or filtered
	 */
    public static boolean isLogged(LogEntry entry, IMemento memento) {
        int severity = entry.getSeverity();
        switch(severity) {
            case IStatus.INFO:
                return //$NON-NLS-1$
                memento.getString(LogView.P_LOG_INFO).equals(//$NON-NLS-1$
                "true");
            case IStatus.WARNING:
                return //$NON-NLS-1$
                memento.getString(LogView.P_LOG_WARNING).equals(//$NON-NLS-1$
                "true");
            case IStatus.ERROR:
                return //$NON-NLS-1$
                memento.getString(LogView.P_LOG_ERROR).equals(//$NON-NLS-1$
                "true");
            case IStatus.OK:
                return //$NON-NLS-1$
                memento.getString(LogView.P_LOG_OK).equals(//$NON-NLS-1$
                "true");
        }
        return false;
    }

    private static void setNewParent(ArrayList parents, LogEntry entry, int depth) {
        if (depth + 1 > parents.size())
            parents.add(entry);
        else
            parents.set(depth, entry);
    }
}
