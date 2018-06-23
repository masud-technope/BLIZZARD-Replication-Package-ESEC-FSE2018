/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.security;

/**
 * Callback that handles Boolean types
 * 
 */
public class BooleanCallback implements Callback, java.io.Serializable {

    private static final long serialVersionUID = 8660509222691671868L;

    private String prompt;

    private boolean defaultValue;

    private boolean value;

    /**
	 * Construct a <code>BooleanCallback</code> with a prompt.
	 * 
	 * <p>
	 * 
	 * @param prompt
	 *            the prompt used to request the boolean value.
	 * 
	 * @exception IllegalArgumentException
	 *                if <code>prompt</code> is null or if <code>prompt</code>
	 *                has a length of 0.
	 */
    public  BooleanCallback(String prompt) {
        if (prompt == null)
            //$NON-NLS-1$
            throw new IllegalArgumentException("Prompt cannot be null");
        this.prompt = prompt;
    }

    /**
	 * Construct a <code>NameCallback</code> with a prompt and default name.
	 * 
	 * <p>
	 * 
	 * @param prompt
	 *            the prompt used to request the information.
	 *            <p>
	 * 
	 * @param defaultValue
	 *            the value to be used as the default value displayed with the
	 *            prompt.
	 * 
	 * @exception IllegalArgumentException
	 *                if <code>prompt</code> is null.
	 */
    public  BooleanCallback(String prompt, boolean defaultValue) {
        if (prompt == null)
            //$NON-NLS-1$
            throw new IllegalArgumentException("Prompt cannot be null");
        this.prompt = prompt;
        this.defaultValue = defaultValue;
    }

    /**
	 * Get the prompt.
	 * 
	 * @return the prompt value.
	 */
    public String getPrompt() {
        return prompt;
    }

    /**
	 * Get the default value.
	 * 
	 * @return the default value, or null if this <code>BooleanCallback</code> was
	 *         not instantiated with a <code>defaultValue</code>.
	 */
    public boolean getDefaultValue() {
        return defaultValue;
    }

    /**
	 * Set the retrieved name.
	 * 
	 * @param val
	 *            the retrieved value <code>true</code> or <code>false</code>.
	 * 
	 * @see #getValue
	 */
    public void setValue(boolean val) {
        this.value = val;
    }

    /**
	 * Get the retrieved value.
	 * 
	 * @return the retrieved value <code>true</code> or <code>false</code>.
	 * 
	 * @see #setValue
	 */
    public boolean getValue() {
        return value;
    }
}
