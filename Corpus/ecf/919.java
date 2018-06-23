/****************************************************************************
 * Copyright (c) 2005, 2010 Jan S. Rellermeyer, Systems Group,
 * Department of Computer Science, ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jan S. Rellermeyer - initial API and implementation
 *    Markus Alexander Kuppe - enhancements and bug fixes
 *
*****************************************************************************/
package ch.ethz.iks.slp.impl.filter;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

/**
 * The RFC1960 LDAP Filter implementation class.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 */
public final class RFC1960Filter implements Filter {

    /**
	 * AND operator.
	 */
    private static final int AND_OPERATOR = 1;

    /**
	 * OR operator.
	 */
    private static final int OR_OPERATOR = 2;

    /**
	 * NOT operator.
	 */
    private static final int NOT_OPERATOR = 3;

    /**
	 * EQUALS (=) operator.
	 */
    private static final int EQUALS = 0;

    /**
	 * PRESENT (=*) operator.
	 */
    private static final int PRESENT = 1;

    /**
	 * APPROX (=~) operator.
	 */
    private static final int APPROX = 2;

    /**
	 * GREATER (>=) operator.
	 */
    private static final int GREATER = 3;

    /**
	 * LESS (<=) operator.
	 */
    private static final int LESS = 4;

    /**
	 * the string presentations of the operators.
	 */
    private static final String[] OP = { "=", "=*", "~=", ">=", "<=" };

    /**
	 * the string class as array.
	 */
    private static final Class[] STRINGCLASS = new Class[] { String.class };

    /**
	 * the empty "null filter" is generated from null filter strings and matches
	 * everything.
	 */
    private static final Filter NULL_FILTER = new Filter() {

        public boolean match(final Dictionary dictionary) {
            return true;
        }

        public String toString() {
            return "";
        }
    };

    // fields
    /**
	 * the operands.
	 */
    private List operands = new ArrayList(1);

    /**
	 * the operator.
	 */
    private int operator;

    /**
	 * create a new filter instance.
	 * 
	 * @param operator
	 *            the operator of the node
	 */
    private  RFC1960Filter(final int operator) {
        this.operator = operator;
    }

    /**
	 * get a filter instance from filter string.
	 * 
	 * @param filterString
	 *            the filter string.
	 * @return a filter instance.
	 * @throws InvalidSyntaxException
	 *             is the string is invalid.
	 */
    public static Filter fromString(final String filterString) {
        if (filterString == null || "".equals(filterString)) {
            return NULL_FILTER;
        }
        Stack stack = new Stack();
        try {
            final int len = filterString.length();
            int last = -1;
            int oper = 0;
            String id = null;
            int comparator = -1;
            final char[] chars = filterString.toCharArray();
            stack.clear();
            for (int i = 0; i < chars.length; i++) {
                switch(chars[i]) {
                    case '(':
                        // lookahead ...
                        final char nextChar = chars[i + 1];
                        if (nextChar == '&') {
                            stack.push(new RFC1960Filter(AND_OPERATOR));
                            continue;
                        } else if (nextChar == '|') {
                            stack.push(new RFC1960Filter(OR_OPERATOR));
                            continue;
                        } else if (nextChar == '!') {
                            stack.push(new RFC1960Filter(NOT_OPERATOR));
                            continue;
                        } else {
                            if (last == -1) {
                                last = i;
                            } else {
                                throw new IllegalStateException("Surplus left paranthesis at: " + filterString.substring(i));
                            }
                        }
                        continue;
                    case ')':
                        if (last == -1) {
                            RFC1960Filter filter = (RFC1960Filter) stack.pop();
                            if (stack.isEmpty()) {
                                return filter;
                            }
                            RFC1960Filter parent = (RFC1960Filter) stack.peek();
                            if (parent.operator == NOT_OPERATOR && !parent.operands.isEmpty()) {
                                throw new IllegalStateException("Unexpected literal: " + filterString.substring(i));
                            }
                            parent.operands.add(filter);
                            if (i == len - 1) {
                                throw new IllegalStateException("Missing right paranthesis at the end.");
                            }
                        } else {
                            if (oper == 0) {
                                throw new IllegalStateException("Missing operator.");
                            }
                            if (stack.isEmpty()) {
                                if (i == len - 1) {
                                    // just a single simple filter
                                    String value = filterString.substring(++oper, len - 1);
                                    if (value.equals("*") && comparator == EQUALS) {
                                        comparator = PRESENT;
                                        value = null;
                                    }
                                    return new RFC1960SimpleFilter(id, comparator, value);
                                } else {
                                    throw new IllegalStateException("Unexpected literal: " + filterString.substring(i));
                                }
                            }
                            // get the parent from stack
                            RFC1960Filter parent = ((RFC1960Filter) stack.peek());
                            String value = filterString.substring(++oper, i);
                            if (value.equals("*") && comparator == EQUALS) {
                                comparator = PRESENT;
                                value = null;
                            }
                            // link current element to parent
                            parent.operands.add(new RFC1960SimpleFilter(id, comparator, value));
                            oper = 0;
                            last = -1;
                            id = null;
                            comparator = -1;
                        }
                        continue;
                    case '~':
                        if (oper == 0 && chars[i + 1] == '=') {
                            id = filterString.substring(last + 1, i).trim();
                            comparator = APPROX;
                            oper = ++i;
                            continue;
                        } else {
                            throw new IllegalStateException("Unexpected character " + chars[i + 1]);
                        }
                    case '>':
                        if (oper == 0 && chars[i + 1] == '=') {
                            id = filterString.substring(last + 1, i).trim();
                            comparator = GREATER;
                            oper = ++i;
                            continue;
                        } else {
                            throw new IllegalStateException("Unexpected character " + chars[i + 1]);
                        }
                    case '<':
                        if (oper == 0 && chars[i + 1] == '=') {
                            id = filterString.substring(last + 1, i).trim();
                            comparator = LESS;
                            oper = ++i;
                            continue;
                        } else {
                            throw new IllegalStateException("Unexpected character " + chars[i + 1]);
                        }
                    case '=':
                        // could also be a "=*" present production.
                        // if this is the case, it is fixed later, because
                        // value=* and value=*key would require a lookahead of at
                        // least two. (the symbol "=*" alone is ambigous).
                        id = filterString.substring(last + 1, i).trim();
                        comparator = EQUALS;
                        oper = i;
                        continue;
                }
            }
            return (RFC1960Filter) stack.pop();
        } catch (EmptyStackException e) {
            throw new IllegalStateException("Filter expression not well-formed.");
        }
    }

    /**
	 * check if the filter matches a dictionary of attributes.
	 * 
	 * @param values
	 *            the attributes.
	 * @return true, if the filter matches, false otherwise.
	 * @see org.osgi.framework.Filter#match(java.util.Dictionary)
	 * @category Filter
	 */
    public boolean match(final Dictionary values) {
        if (operator == AND_OPERATOR) {
            final Filter[] operandArray = (Filter[]) operands.toArray(new Filter[operands.size()]);
            for (int i = 0; i < operandArray.length; i++) {
                if (!operandArray[i].match(values)) {
                    return false;
                }
            }
            return true;
        } else if (operator == OR_OPERATOR) {
            final Filter[] operandArray = (Filter[]) operands.toArray(new Filter[operands.size()]);
            for (int i = 0; i < operandArray.length; i++) {
                if (operandArray[i].match(values)) {
                    return true;
                }
            }
            return false;
        } else if (operator == NOT_OPERATOR) {
            return !(((Filter) operands.get(0)).match(values));
        }
        throw new IllegalStateException("PARSER ERROR");
    }

    /**
	 * get a string representation of the filter.
	 * 
	 * @return the string.
	 * @category Object
	 */
    public String toString() {
        if (operator == NOT_OPERATOR) {
            return "(!" + operands.get(0) + ")";
        }
        final StringBuffer buffer = new StringBuffer(operator == AND_OPERATOR ? "(&" : "(|");
        Filter[] operandArray = (Filter[]) operands.toArray(new Filter[operands.size()]);
        for (int i = 0; i < operandArray.length; i++) {
            buffer.append(operandArray[i]);
        }
        buffer.append(")");
        return buffer.toString();
    }

    /**
	 * check if the filter equals another object.
	 * 
	 * @param obj
	 *            the other object.
	 * @return true if the object is an instance of RFC1960Filter and the
	 *         filters are equal.
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @category Object
	 */
    public boolean equals(final Object obj) {
        if (obj instanceof RFC1960Filter) {
            RFC1960Filter filter = (RFC1960Filter) obj;
            if (operands.size() != filter.operands.size()) {
                return false;
            }
            final Filter[] operandArray = (Filter[]) operands.toArray(new Filter[operands.size()]);
            final Filter[] operandArray2 = (Filter[]) filter.operands.toArray(new Filter[operands.size()]);
            for (int i = 0; i < operandArray.length; i++) {
                if (!operandArray[i].equals(operandArray2[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
	 * get the hash code.
	 * 
	 * @return the hash code.
	 * @category Object
	 */
    public int hashCode() {
        return toString().hashCode();
    }

    /**
	 * A simple filter. That is a filter of the form <tt>key operand value</tt>.
	 * A general filter consists of one or more simple filter literals connected
	 * by boolean operators.
	 * 
	 * @author Jan S. Rellermeyer, IKS, ETH Zurich
	 */
    private static final class RFC1960SimpleFilter implements Filter {

        /**
		 * the id.
		 */
        private final String id;

        /**
		 * the comparator.
		 */
        private final int comparator;

        /**
		 * the value.
		 */
        private final String value;

        /**
		 * create a new filter.
		 * 
		 * @param id
		 *            the key
		 * @param comparator
		 *            the comparator
		 */
        private  RFC1960SimpleFilter(final String id, final int comparator, final String value) {
            this.id = id;
            this.comparator = comparator;
            this.value = value;
        }

        /**
		 * check if the filter matches a dictionary of attributes.
		 * 
		 * @param map
		 *            the attributes.
		 * @return true if the filter matches, false otherwise.
		 * @see org.osgi.framework.Filter#match(java.util.Dictionary)
		 * @category Filter
		 */
        public boolean match(final Dictionary map) {
            Object temp = null;
            // just by chance, try if the case sensitive matching returns a
            // result.
            temp = map.get(id);
            if (temp == null) {
                // no ? Then try lower case.
                temp = map.get(id.toLowerCase());
            }
            if (temp == null) {
                // bad luck, try case insensitive matching of all keys
                for (Enumeration keys = map.keys(); keys.hasMoreElements(); ) {
                    String key = (String) keys.nextElement();
                    if (key.equalsIgnoreCase(id)) {
                        temp = map.get(key);
                        break;
                    }
                }
            }
            if (temp == null) {
                return false;
            }
            // are we just checking for presence ? Then we are done ...
            if (comparator == PRESENT) {
                return true;
            }
            final Object attr = temp;
            try {
                if (attr instanceof String) {
                    return compareString(value, comparator, (String) attr);
                } else if (attr instanceof Number) {
                    // in a primitive typed way
                    return compareNumber(value, comparator, (Number) attr);
                } else if (attr instanceof String[]) {
                    final String[] array = (String[]) attr;
                    if (array.length == 0) {
                        return false;
                    }
                    final String val = comparator == APPROX ? stripWhitespaces(value) : value;
                    for (int i = 0; i < array.length; i++) {
                        if (compareString(val, comparator, array[i])) {
                            return true;
                        }
                    }
                    return false;
                } else if (attr instanceof Boolean) {
                    return ((comparator == EQUALS || comparator == APPROX) && ((Boolean) attr).equals(Boolean.valueOf(value)));
                } else if (attr instanceof Character) {
                    return value.length() == 1 ? compareTyped(new Character(value.charAt(0)), comparator, (Character) attr) : false;
                } else if (attr instanceof Vector) {
                    Vector vec = (Vector) attr;
                    final Object[] obj = new Object[vec.size()];
                    vec.copyInto(obj);
                    return compareArray(value, comparator, obj);
                } else if (attr instanceof Object[]) {
                    return compareArray(value, comparator, (Object[]) attr);
                } else if (attr.getClass().isArray()) {
                    for (int i = 0; i < Array.getLength(attr); i++) {
                        final Object obj = Array.get(attr, i);
                        if (obj instanceof Number && compareNumber(value, comparator, (Number) obj) || (obj instanceof Comparable && compareReflective(value, comparator, (Comparable) obj))) {
                            return true;
                        }
                    }
                    return false;
                } else if (attr instanceof Comparable) {
                    return compareReflective(value, comparator, (Comparable) attr);
                }
                return false;
            } catch (Throwable t) {
                return false;
            }
        }

        /**
		 * compare a string.
		 * 
		 * @param val
		 *            the filter value.
		 * @param comparator
		 *            the comparator.
		 * @param attr
		 *            the attribute.
		 * @return true, iff matches.
		 */
        private static boolean compareString(final String val, final int comparator, final String attr) {
            final String value = comparator == APPROX ? stripWhitespaces(val) : val;
            final String attribute = comparator == APPROX ? stripWhitespaces(attr) : attr;
            switch(comparator) {
                case APPROX:
                case EQUALS:
                    return stringCompare(value.toCharArray(), 0, attribute.toCharArray(), 0) == 0;
                case GREATER:
                    return stringCompare(value.toCharArray(), 0, attribute.toCharArray(), 0) <= 0;
                case LESS:
                    return stringCompare(value.toCharArray(), 0, attribute.toCharArray(), 0) >= 0;
                default:
                    throw new IllegalStateException("Found illegal comparator.");
            }
        }

        /**
		 * compare numbers.
		 * 
		 * @param value
		 *            the filter value.
		 * @param comparator
		 *            the comparator.
		 * @param attr
		 *            the number.
		 * @return true, iff matches.
		 */
        private static boolean compareNumber(final String value, final int comparator, final Number attr) {
            if (attr instanceof Integer) {
                final int intAttr = ((Integer) attr).intValue();
                final int intValue = Integer.parseInt(value);
                switch(comparator) {
                    case GREATER:
                        return intAttr >= intValue;
                    case LESS:
                        return intAttr <= intValue;
                    default:
                        return intAttr == intValue;
                }
            } else if (attr instanceof Long) {
                final long longAttr = ((Long) attr).longValue();
                final long longValue = Long.parseLong(value);
                switch(comparator) {
                    case GREATER:
                        return longAttr >= longValue;
                    case LESS:
                        return longAttr <= longValue;
                    default:
                        return longAttr == longValue;
                }
            } else if (attr instanceof Short) {
                final short shortAttr = ((Short) attr).shortValue();
                final short shortValue = Short.parseShort(value);
                switch(comparator) {
                    case GREATER:
                        return shortAttr >= shortValue;
                    case LESS:
                        return shortAttr <= shortValue;
                    default:
                        return shortAttr == shortValue;
                }
            } else if (attr instanceof Double) {
                final double doubleAttr = ((Double) attr).doubleValue();
                final double doubleValue = Double.parseDouble(value);
                switch(comparator) {
                    case GREATER:
                        return doubleAttr >= doubleValue;
                    case LESS:
                        return doubleAttr <= doubleValue;
                    default:
                        return doubleAttr == doubleValue;
                }
            } else if (attr instanceof Float) {
                final float floatAttr = ((Float) attr).floatValue();
                final float floatValue = Float.parseFloat(value);
                switch(comparator) {
                    case GREATER:
                        return floatAttr >= floatValue;
                    case LESS:
                        return floatAttr <= floatValue;
                    default:
                        return floatAttr == floatValue;
                }
            } else if (attr instanceof Byte) {
                try {
                    return compareTyped(Byte.decode(value), comparator, (Byte) attr);
                } catch (Throwable t) {
                }
            }
            // Comparables.
            return compareReflective(value, comparator, (Comparable) attr);
        }

        /**
		 * compare in a typed way.
		 * 
		 * @param typedVal
		 *            the typed filter value.
		 * @param comparator
		 *            the comparator.
		 * @param attr
		 *            the attribute.
		 * @return true, iff matches.
		 */
        private static boolean compareTyped(final Object typedVal, final int comparator, final Comparable attr) {
            switch(comparator) {
                case EQUALS:
                case APPROX:
                    return attr.equals(typedVal);
                case GREATER:
                    return attr.compareTo(typedVal) >= 0;
                case LESS:
                    return attr.compareTo(typedVal) <= 0;
                default:
                    throw new IllegalStateException("Found illegal comparator.");
            }
        }

        /**
		 * compare arrays.
		 * 
		 * @param value
		 *            the filter value.
		 * @param comparator
		 *            the comparator.
		 * @param array
		 *            the array.
		 * @return true, iff matches.
		 */
        private static boolean compareArray(final String value, final int comparator, final Object[] array) {
            for (int i = 0; i < array.length; i++) {
                final Object obj = array[i];
                if (obj instanceof String) {
                    if (compareString(value, comparator, (String) obj)) {
                        return true;
                    }
                } else if (obj instanceof Number) {
                    if (compareNumber(value, comparator, (Number) obj)) {
                        return true;
                    }
                } else if (obj instanceof Comparable) {
                    if (compareReflective(value, comparator, (Comparable) obj)) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
		 * compare in a generic way by using reflection to create a
		 * corresponding object from the filter values string and compare this
		 * object with the attribute.
		 * 
		 * @param val
		 *            the filter value.
		 * @param comparator
		 *            the comparator.
		 * @param attr
		 *            the attribute.
		 * @return true, iff matches.
		 */
        private static boolean compareReflective(final String val, final int comparator, final Comparable attr) {
            final Class clazz = attr.getClass();
            Object typedVal = null;
            try {
                final Constructor constr = clazz.getConstructor(STRINGCLASS);
                typedVal = constr.newInstance(new Object[] { val });
                return compareTyped(typedVal, comparator, attr);
            } catch (Exception didNotWork) {
                return false;
            }
        }

        /**
		 * strip whitespaces from a string.
		 * 
		 * @param s
		 *            the string.
		 * @return the stripped string.
		 */
        private static String stripWhitespaces(final String s) {
            return s.replace(' ', '\0');
        }

        /**
		 * check, if a value matches a wildcard expression.
		 * 
		 * @param c1
		 *            the value.
		 * @param p1
		 *            the value index.
		 * @param c2
		 *            the attribute.
		 * @param p2
		 *            the attribute index.
		 * @return true, iff matches.
		 */
        private static int stringCompare(final char[] c1, int p1, final char[] c2, int p2) {
            if (p1 == c1.length) {
                return 0;
            }
            final int l1 = c1.length;
            final int l2 = c2.length;
            while (p1 < l1 && p2 < l2) {
                if (c1[p1] == c2[p2]) {
                    p1++;
                    p2++;
                    continue;
                }
                if (c1[p1] > 'A' && c1[p1] < 'Z') {
                    c1[p1] = (char) (c1[p1] + 32);
                }
                if (c2[p2] > 'A' && c2[p2] < 'Z') {
                    c2[p2] = (char) (c2[p2] + 32);
                }
                if (c1[p1] == c2[p2]) {
                    p1++;
                    p2++;
                    continue;
                }
                if (c1[p1] == '*') {
                    p1++;
                    do {
                        if (stringCompare(c1, p1, c2, p2) == 0) {
                            return 0;
                        }
                        p2++;
                    } while (l2 - p2 > -1);
                    return 1;
                } else {
                    if (c1[p1] < c2[p2]) {
                        return -1;
                    } else if (c1[p1] > c2[p2]) {
                        return 1;
                    }
                }
            }
            if (p1 == l1 && p2 == l2 && c1[p1 - 1] == c2[p2 - 1]) {
                return 0;
            }
            if (c1[p1 - 1] == '*' && p1 == l1 && p2 == l2) {
                return 0;
            }
            final int min = l1 < l2 ? l1 : l2;
            return l1 == min ? -1 : 1;
        }

        /**
		 * get a string representation of the SimpleFilter.
		 * 
		 * @return the string.
		 * @category Object
		 */
        public String toString() {
            return "(" + id + OP[comparator] + (value == null ? "" : value) + ")";
        }

        /**
		 * check, if the instance matches another object.
		 * 
		 * @param obj
		 *            the other object.
		 * @return true, iff the other object is an instance of
		 *         RFC1960SimpleFilter and the filter expressions are equal.
		 * @category Object
		 */
        public boolean equals(final Object obj) {
            if (obj instanceof RFC1960SimpleFilter) {
                final RFC1960SimpleFilter filter = (RFC1960SimpleFilter) obj;
                return (comparator == filter.comparator) && id.equals(filter.id) && (value.equals(filter.value));
            }
            return false;
        }

        /**
		 * get the hash code.
		 * 
		 * @return the hash code.
		 * @category Object
		 */
        public int hashCode() {
            return toString().hashCode();
        }
    }
}
