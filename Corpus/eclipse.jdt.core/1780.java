/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler;

import java.util.*;

public class ConfigurableOption {

    private String componentName;

    private String optionName;

    private int id;

    private String category;

    private String name;

    private String description;

    private int currentValueIndex;

    private int defaultValueIndex;

    private String[] possibleValues;

    // special value for <possibleValues> indicating that 
    // the <currentValueIndex> is the actual value
    public static final String[] NoDiscreteValue = {};

    /**
 * INTERNAL USE ONLY
 *
 * Initialize an instance of this class according to a specific locale
 *
 * @param loc java.util.Locale
 */
    public  ConfigurableOption(String componentName, String optionName, Locale loc, int currentValueIndex) {
        this.componentName = componentName;
        this.optionName = optionName;
        this.currentValueIndex = currentValueIndex;
        ResourceBundle resource = null;
        try {
            String location = componentName.substring(0, componentName.lastIndexOf('.'));
            //$NON-NLS-1$
            resource = ResourceBundle.getBundle(location + ".options", loc);
        } catch (MissingResourceException e) {
            category = "Missing ressources entries for" + componentName + " options";
            name = "Missing ressources entries for" + componentName + " options";
            description = "Missing ressources entries for" + componentName + " options";
            possibleValues = new String[0];
            id = -1;
        }
        if (resource == null)
            return;
        try {
            //$NON-NLS-1$
            id = Integer.parseInt(resource.getString(optionName + ".number"));
        } catch (MissingResourceException e) {
            id = -1;
        } catch (NumberFormatException e) {
            id = -1;
        }
        try {
            //$NON-NLS-1$
            category = resource.getString(optionName + ".category");
        } catch (MissingResourceException e) {
            category = "Missing ressources entries for" + componentName + " options";
        }
        try {
            //$NON-NLS-1$
            name = resource.getString(optionName + ".name");
        } catch (MissingResourceException e) {
            name = "Missing ressources entries for" + componentName + " options";
        }
        try {
            //$NON-NLS-1$ //$NON-NLS-2$
            StringTokenizer tokenizer = new StringTokenizer(resource.getString(optionName + ".possibleValues"), "|");
            int numberOfValues = Integer.parseInt(tokenizer.nextToken());
            if (numberOfValues == -1) {
                possibleValues = NoDiscreteValue;
            } else {
                possibleValues = new String[numberOfValues];
                int index = 0;
                while (tokenizer.hasMoreTokens()) {
                    possibleValues[index] = tokenizer.nextToken();
                    index++;
                }
            }
        } catch (MissingResourceException e) {
            possibleValues = new String[0];
        } catch (NoSuchElementException e) {
            possibleValues = new String[0];
        } catch (NumberFormatException e) {
            possibleValues = new String[0];
        }
        try {
            //$NON-NLS-1$
            description = resource.getString(optionName + ".description");
        } catch (MissingResourceException e) {
            description = "Missing ressources entries for" + componentName + " options";
        }
    }

    /**
 * Return a String that represents the localized category of the receiver.
 * @return java.lang.String
 */
    public String getCategory() {
        return category;
    }

    /**
 * Return a String that identifies the component owner (typically the qualified
 *	type name of the class which it corresponds to).
 *
 * e.g. "org.eclipse.jdt.internal.compiler.api.Compiler"
 *
 * @return java.lang.String
 */
    public String getComponentName() {
        return componentName;
    }

    /**
 * Answer the index (in possibleValues array) of the current setting for this
 * particular option.
 *
 * In case the set of possibleValues is NoDiscreteValue, then this index is the
 * actual value (e.g. max line lenght set to 80).
 *
 * @return int
 */
    public int getCurrentValueIndex() {
        return currentValueIndex;
    }

    /**
 * Answer the index (in possibleValues array) of the default setting for this
 * particular option.
 *
 * In case the set of possibleValues is NoDiscreteValue, then this index is the
 * actual value (e.g. max line lenght set to 80).
 *
 * @return int
 */
    public int getDefaultValueIndex() {
        return defaultValueIndex;
    }

    /**
 * Return an String that represents the localized description of the receiver.
 *
 * @return java.lang.String
 */
    public String getDescription() {
        return description;
    }

    /**
 * Internal ID which allows the configurable component to identify this particular option.
 *
 * @return int
 */
    public int getID() {
        return id;
    }

    /**
 * Return a String that represents the localized name of the receiver.
 * @return java.lang.String
 */
    public String getName() {
        return name;
    }

    /**
 * Return an array of String that represents the localized possible values of the receiver.
 * @return java.lang.String[]
 */
    public String[] getPossibleValues() {
        return possibleValues;
    }

    /**
 * Change the index (in possibleValues array) of the current setting for this
 * particular option.
 *
 * In case the set of possibleValues is NoDiscreteValue, then this index is the
 * actual value (e.g. max line lenght set to 80).
 */
    public void setValueIndex(int newIndex) {
        currentValueIndex = newIndex;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        //$NON-NLS-1$ 
        buffer.append("Configurable option for ");
        //$NON-NLS-1$ 
        buffer.append(this.componentName).append("\n");
        //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append("- category:			").append(this.category).append("\n");
        //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append("- name:				").append(this.name).append("\n");
        /* display current value */
        //$NON-NLS-1$ 
        buffer.append("- current value:	");
        if (possibleValues == NoDiscreteValue) {
            buffer.append(this.currentValueIndex);
        } else {
            buffer.append(this.possibleValues[this.currentValueIndex]);
        }
        //$NON-NLS-1$ 
        buffer.append("\n");
        /* display possible values */
        if (possibleValues != NoDiscreteValue) {
            //$NON-NLS-1$ 
            buffer.append("- possible values:	[");
            for (int i = 0, max = possibleValues.length; i < max; i++) {
                if (i != 0)
                    //$NON-NLS-1$ 
                    buffer.append(", ");
                buffer.append(possibleValues[i]);
            }
            //$NON-NLS-1$ 
            buffer.append("]\n");
            //$NON-NLS-1$ //$NON-NLS-2$
            buffer.append("- curr. val. index:	").append(currentValueIndex).append("\n");
        }
        //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append("- description:		").append(description).append("\n");
        return buffer.toString();
    }

    /**
	 * Gets the optionName.
	 * @return Returns a String
	 */
    public String getOptionName() {
        return optionName;
    }
}
