/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.presence.bot;

import java.util.List;
import org.eclipse.ecf.presence.bot.IIMBotEntry;

public class IMBotEntry implements IIMBotEntry {

    private String id;

    private String name;

    private String containerFactoryName;

    private String connectID;

    private String password;

    private List commands;

    public  IMBotEntry(String id, String name, String containerFactoryName, String connectID, String password, List commands) {
        this.id = id;
        this.name = name;
        this.containerFactoryName = containerFactoryName;
        this.connectID = connectID;
        this.password = password;
        this.commands = commands;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.bot.IIMBotEntry#getCommands()
	 */
    public List getCommands() {
        return commands;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.bot.IIMBotEntry#getId()
	 */
    public String getId() {
        return id;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.bot.IIMBotEntry#getName()
	 */
    public String getName() {
        return name;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.bot.IIMBotEntry#getConnectID()
	 */
    public String getConnectID() {
        return connectID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.bot.IIMBotEntry#getContainerFactoryName()
	 */
    public String getContainerFactoryName() {
        return containerFactoryName;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.bot.IIMBotEntry#getPassword()
	 */
    public String getPassword() {
        return password;
    }
}
