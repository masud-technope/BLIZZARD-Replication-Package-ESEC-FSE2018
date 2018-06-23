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

public interface Visitor {

    public void visit(Rule rule);

    public Object visit_attr_list(Parser.attr_list rule);

    public Object visit_attribute(Parser.attribute rule);

    public Object visit_attr_val_list(Parser.attr_val_list rule);

    public Object visit_attr_tag(Parser.attr_tag rule);

    public Object visit_attr_val(Parser.attr_val rule);

    public Object visit_intval(Parser.intval rule);

    public Object visit_strval(Parser.strval rule);

    public Object visit_boolval(Parser.boolval rule);

    public Object visit_opaque(Parser.opaque rule);

    public Object visit_safe_val(Parser.safe_val rule);

    public Object visit_safe_tag(Parser.safe_tag rule);

    public Object visit_escape_val(Parser.escape_val rule);

    public Object visit_DIGIT(Parser.DIGIT rule);

    public Object visit_HEXDIG(Parser.HEXDIG rule);

    public Object visit_StringValue(Parser.StringValue value);

    public Object visit_NumericValue(Parser.NumericValue value);
}
/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
