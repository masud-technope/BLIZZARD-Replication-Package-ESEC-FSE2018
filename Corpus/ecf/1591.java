/****************************************************************************
 * Copyright (c) 2005, 2010 Jan S. Rellermeyer, Systems Group,
 * Department of Computer Science, ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Markus Alexander Kuppe - initial API and implementation
 *
*****************************************************************************/
package ch.ethz.iks.slp.impl.attr.gen;

/**
 * Changed, do not overwrite added!!!
 */
public class ParserException extends Exception {

    private static final long serialVersionUID = -3319122582148082535L;

    private Rule rule;

    public  ParserException(String message) {
        super(message);
    }

    public  ParserException(String string, Rule aRule) {
        super(string);
        rule = aRule;
    }

    /**
	 * @return the rule
	 */
    public Rule getRule() {
        return rule;
    }
}
