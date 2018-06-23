/*******************************************************************************
 * Copyright (c) 2005, 2007 Remy Suen
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.protocol.msn.internal.encode;

/**
 * The ResponseCommand class processes one line of simple output from the MSN
 * servers. It provides methods to handle the output easier by separating the
 * command and its parameters.
 */
public class ResponseCommand {

    /**
	 * The command of the output, there is a large variety of commands in use
	 * currently and are all three characters long in uppercase.
	 */
    private String cmd;

    /**
	 * The parameters that was sent with the command.
	 */
    private String[] params;

    /**
	 * Creates a new ResponseCommand that will process the given line. The
	 * constructor does not do anything outside of calling
	 * 
	 * @param line
	 *            the line that this should represent
	 */
    public  ResponseCommand(String line) {
        process(line);
    }

    /**
	 * Process the given line. It will store the first three characters as the
	 * command and the rest of the line will be split by a single space and
	 * stored as a String array. If <code>line</code> is null, both the
	 * command and the String array will store null pointers.
	 * 
	 * @param line
	 *            the line to be processed
	 */
    public void process(String line) {
        if (line == null) {
            cmd = null;
            params = null;
        } else {
            cmd = line.substring(0, 3);
            params = StringUtils.splitOnSpace(line.substring(4));
        }
    }

    /**
	 * Returns the command of this line.
	 * 
	 * @return the three character command
	 */
    public String getCommand() {
        return cmd;
    }

    /**
	 * Returns the string literal stored at the given index in params. If the
	 * first parameter is desired, a 0 should be passed.
	 * 
	 * @param index
	 *            the parameter at the given index
	 * @return the desired parameter that is at the given index
	 */
    public String getParam(int index) {
        return params[index];
    }
}
