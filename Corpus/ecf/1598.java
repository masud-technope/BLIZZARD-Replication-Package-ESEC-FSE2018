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

import java.util.ArrayList;

public abstract class Rule {

    public final String spelling;

    public final ArrayList rules;

    protected  Rule(String spelling, ArrayList rules) {
        this.spelling = spelling;
        this.rules = rules;
    }

    public  Rule(Rule rule) {
        this(rule.spelling, rule.rules);
    }

    public String toString() {
        return spelling;
    }

    public boolean equals(Object object) {
        return object instanceof Rule && spelling.equals(((Rule) object).spelling);
    }

    public int hashCode() {
        return spelling.hashCode();
    }

    public int compareTo(Rule rule) {
        return spelling.compareTo(rule.spelling);
    }

    public abstract Object visit(Visitor visitor);
}
/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
