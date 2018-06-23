/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.example.collab.ui;

import org.eclipse.ecf.core.user.IUser;

/**
 * @author kgilmer
 */
public class ChatLine {

    private IUser originator;

    private String text;

    private String date;

    private boolean isPrivate = false;

    private boolean isRaw = false;

    private boolean noCRLF = false;

    private Runnable onClick;

    public  ChatLine() {
        this(null);
    }

    public  ChatLine(String text) {
        this(text, null, null, null);
    }

    public  ChatLine(String text, String date) {
        this(text, date, null);
    }

    public  ChatLine(String text, IUser user) {
        this(text, null, user);
    }

    public  ChatLine(String text, String date, IUser user) {
        this(text, date, user, null);
    }

    public  ChatLine(String text, IUser user, Runnable onClick) {
        this(text, null, user, onClick);
    }

    /**
	 * Creates a chat line. If a non-null <code>onClick</code> runnable is
	 * given, this chat line has an associated handler that should be called
	 * when the user clicks on the chat line.
	 * 
	 * @param text
	 * @param date
	 * @param user
	 * @param onClick
	 */
    public  ChatLine(String text, String date, IUser user, Runnable onClick) {
        this.text = text;
        this.date = date;
        this.originator = user;
        this.onClick = onClick;
    }

    /**
	 * @return Returns the originator.
	 */
    public IUser getOriginator() {
        return originator;
    }

    /**
	 * @param originator
	 *            The originator to set.
	 */
    public void setOriginator(IUser originator) {
        this.originator = originator;
    }

    /**
	 * @return Returns the text.
	 */
    public String getText() {
        return text;
    }

    /**
	 * @param text
	 *            The text to set.
	 */
    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /**
	 * @return Returns the isPrivate.
	 */
    public boolean isPrivate() {
        return isPrivate;
    }

    /**
	 * @param isPrivate
	 *            The isPrivate to set.
	 */
    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    /**
	 * @return Returns the isRaw.
	 */
    public boolean isRaw() {
        return isRaw;
    }

    /**
	 * @param isRaw
	 *            The isRaw to set.
	 */
    public void setRaw(boolean isRaw) {
        this.isRaw = isRaw;
    }

    /**
	 * @return Returns the noCRLF.
	 */
    public boolean isNoCRLF() {
        return noCRLF;
    }

    /**
	 * @param noCRLF
	 *            The noCRLF to set.
	 */
    public void setNoCRLF(boolean noCRLF) {
        this.noCRLF = noCRLF;
    }

    /**
	 * 
	 * @return the runnable
	 */
    public Runnable getOnClick() {
        return onClick;
    }

    /**
	 * 
	 * @param onClick
	 */
    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }
}
