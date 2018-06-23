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

import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Iterator;
import java.util.Properties;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;

public class Parser {

    public static void main(String[] args) {
        Properties arguments = new Properties();
        String error = "";
        boolean ok = args.length > 0;
        if (ok) {
            arguments.setProperty("Trace", "Off");
            arguments.setProperty("Rule", "attr-list");
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-trace"))
                    arguments.setProperty("Trace", "On");
                else if (args[i].equals("-visitor"))
                    arguments.setProperty("Visitor", args[++i]);
                else if (args[i].equals("-file"))
                    arguments.setProperty("File", args[++i]);
                else if (args[i].equals("-string"))
                    arguments.setProperty("String", args[++i]);
                else if (args[i].equals("-rule"))
                    arguments.setProperty("Rule", args[++i]);
                else {
                    error = "unknown argument: " + args[i];
                    ok = false;
                }
            }
        }
        if (ok) {
            if (arguments.getProperty("File") == null && arguments.getProperty("String") == null) {
                error = "insufficient arguments: -file or -string required";
                ok = false;
            }
        }
        if (!ok) {
            System.out.println("error: " + error);
            System.out.println("usage: Parser [-rule rulename] [-trace] <-file file | -string string> [-visitor visitor]");
        } else {
            try {
                Parser parser = new Parser();
                Rule rule = null;
                parser.traceOff();
                if (arguments.getProperty("Trace").equals("On"))
                    parser.traceOn();
                if (arguments.getProperty("File") != null)
                    rule = parser.parse(arguments.getProperty("Rule"), new File(arguments.getProperty("File")));
                else if (arguments.getProperty("String") != null)
                    rule = parser.parse(arguments.getProperty("Rule"), arguments.getProperty("String"));
                if (arguments.getProperty("Visitor") != null) {
                    Visitor visitor = (Visitor) Class.forName(arguments.getProperty("Visitor")).newInstance();
                    visitor.visit(rule);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("argument error: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("io error: " + e.getMessage());
            } catch (ParserException e) {
                System.out.println("parser error: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println("visitor error: class not found - " + e.getMessage());
            } catch (IllegalAccessException e) {
                System.out.println("visitor error: illegal access - " + e.getMessage());
            } catch (InstantiationException e) {
                System.out.println("visitor error: instantiation failure - " + e.getMessage());
            }
        }
    }

    /* ---------------------------------------------------------------------------
   * public parsers
   * ---------------------------------------------------------------------------
   */
    public Rule parse(String rulename, String string) throws IllegalArgumentException, ParserException {
        if (rulename == null)
            throw new IllegalArgumentException("null rulename");
        if (string == null)
            throw new IllegalArgumentException("null string");
        return decode(rulename, string);
    }

    public Rule parse(String rulename, InputStream in) throws IllegalArgumentException, IOException, ParserException {
        if (rulename == null)
            throw new IllegalArgumentException("null rulename");
        if (in == null)
            throw new IllegalArgumentException("null input stream");
        int ch = 0;
        StringBuffer out = new StringBuffer();
        while ((ch = in.read()) != -1) out.append((char) ch);
        return decode(rulename, out.toString());
    }

    public Rule parse(String rulename, File file) throws IllegalArgumentException, IOException, ParserException {
        if (rulename == null)
            throw new IllegalArgumentException("null rulename");
        if (file == null)
            throw new IllegalArgumentException("null file");
        BufferedReader in = new BufferedReader(new FileReader(file));
        int ch = 0;
        StringBuffer out = new StringBuffer();
        while ((ch = in.read()) != -1) out.append((char) ch);
        in.close();
        return decode(rulename, out.toString());
    }

    /* ---------------------------------------------------------------------------
   * private data
   * ---------------------------------------------------------------------------
   */
    private String text;

    private int index = 0;

    private boolean trace = false;

    private int level = 0;

    private int error = -1;

    private Stack callStack = new Stack();

    private Stack errorStack = new Stack();

    private static final String newline = System.getProperty("line.separator", "\n");

    /* ---------------------------------------------------------------------------
   * private trace
   * ---------------------------------------------------------------------------
   */
    private void traceOn() {
        trace = true;
    }

    private void traceOff() {
        trace = false;
    }

    private void push(String function) {
        callStack.push(function);
        if (trace) {
            System.out.println("-> " + ++level + ": " + function + "()");
            System.out.println(index + ": " + text.substring(index, index + 10 > text.length() ? text.length() : index + 10).replaceAll("[^\\p{Print}]", " "));
        }
    }

    private void push(String function, String regex) {
        callStack.push(function);
        if (trace) {
            System.out.println("-> " + ++level + ": " + function + "(" + regex + ")");
            System.out.println(index + ": " + text.substring(index, index + 10 > text.length() ? text.length() : index + 10).replaceAll("[^\\p{Print}]", " "));
        }
    }

    private void push(String function, String spelling, String regex) {
        callStack.push(function);
        if (trace) {
            System.out.println("-> " + ++level + ": " + function + "(" + spelling + ", " + regex + ")");
            System.out.println(index + ": " + text.substring(index, index + 10 > text.length() ? text.length() : index + 10).replaceAll("[^\\p{Print}]", " "));
        }
    }

    private void pop(String function, boolean result, int length) {
        callStack.pop();
        if (trace) {
            System.out.println("<- " + level-- + ": " + function + "(" + (result ? "true," : "false,") + length + ")");
        }
        if (!result) {
            if (index > error) {
                error = index;
                errorStack = new Stack();
                errorStack.addAll(callStack);
            }
        } else {
            if (index > error)
                error = -1;
        }
    }

    /* ---------------------------------------------------------------------------
   * private decoders
   * ---------------------------------------------------------------------------
   */
    private Rule decode(String rulename, String text) throws ParserException {
        this.text = text;
        Rule rule = null;
        if (rulename.equalsIgnoreCase("attr-list"))
            rule = decode_attr_list();
        else if (rulename.equalsIgnoreCase("attribute"))
            rule = decode_attribute();
        else if (rulename.equalsIgnoreCase("attr-val-list"))
            rule = decode_attr_val_list();
        else if (rulename.equalsIgnoreCase("attr-tag"))
            rule = decode_attr_tag();
        else if (rulename.equalsIgnoreCase("attr-val"))
            rule = decode_attr_val();
        else if (rulename.equalsIgnoreCase("intval"))
            rule = decode_intval();
        else if (rulename.equalsIgnoreCase("strval"))
            rule = decode_strval();
        else if (rulename.equalsIgnoreCase("boolval"))
            rule = decode_boolval();
        else if (rulename.equalsIgnoreCase("opaque"))
            rule = decode_opaque();
        else if (rulename.equalsIgnoreCase("safe-val"))
            rule = decode_safe_val();
        else if (rulename.equalsIgnoreCase("safe-tag"))
            rule = decode_safe_tag();
        else if (rulename.equalsIgnoreCase("escape-val"))
            rule = decode_escape_val();
        else if (rulename.equalsIgnoreCase("DIGIT"))
            rule = decode_DIGIT();
        else if (rulename.equalsIgnoreCase("HEXDIG"))
            rule = decode_HEXDIG();
        else
            throw new IllegalArgumentException("unknown rule");
        if (rule == null) {
            String marker = "                              ";
            StringBuffer errorBuffer = new StringBuffer();
            int start = (error < 30) ? 0 : error - 30;
            int end = (text.length() < error + 30) ? text.length() : error + 30;
            errorBuffer.append("rule \"" + (String) errorStack.peek() + "\" failed" + newline);
            errorBuffer.append(text.substring(start, end).replaceAll("[^\\p{Print}]", " ") + newline);
            errorBuffer.append(marker.substring(0, error < 30 ? error : 30) + "^" + newline);
            errorBuffer.append("rule stack:");
            for (Iterator i = errorStack.iterator(); i.hasNext(); ) errorBuffer.append(newline + "  " + (String) i.next());
            throw new ParserException(errorBuffer.toString());
        }
        if (text.length() > index) {
            String marker = "                              ";
            StringBuffer errorBuffer = new StringBuffer();
            int start = (index < 30) ? 0 : index - 30;
            int end = (text.length() < index + 30) ? text.length() : index + 30;
            errorBuffer.append("extra data found" + newline);
            errorBuffer.append(text.substring(start, end).replaceAll("[^\\p{Print}]", " ") + newline);
            errorBuffer.append(marker.substring(0, index < 30 ? index : 30) + "^" + newline);
            throw new ParserException(errorBuffer.toString(), rule);
        }
        return rule;
    }

    private attr_list decode_attr_list() {
        push("attr-list");
        boolean decoded = true;
        int s0 = index;
        ArrayList e0 = new ArrayList();
        Rule rule;
        decoded = false;
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_attribute();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_StringValue(",");
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_attr_list();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_attribute();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        rule = null;
        if (decoded)
            rule = new attr_list(text.substring(s0, index), e0);
        else
            index = s0;
        pop("attr-list", decoded, index - s0);
        return (attr_list) rule;
    }

    private attribute decode_attribute() {
        push("attribute");
        boolean decoded = true;
        int s0 = index;
        ArrayList e0 = new ArrayList();
        Rule rule;
        decoded = false;
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_StringValue("(");
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_attr_tag();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_StringValue("=");
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_attr_val_list();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_StringValue(")");
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_attr_tag();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        rule = null;
        if (decoded)
            rule = new attribute(text.substring(s0, index), e0);
        else
            index = s0;
        pop("attribute", decoded, index - s0);
        return (attribute) rule;
    }

    private attr_val_list decode_attr_val_list() {
        push("attr-val-list");
        boolean decoded = true;
        int s0 = index;
        ArrayList e0 = new ArrayList();
        Rule rule;
        decoded = false;
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_attr_val();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_StringValue(",");
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_attr_val_list();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_attr_val();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        rule = null;
        if (decoded)
            rule = new attr_val_list(text.substring(s0, index), e0);
        else
            index = s0;
        pop("attr-val-list", decoded, index - s0);
        return (attr_val_list) rule;
    }

    private attr_tag decode_attr_tag() {
        push("attr-tag");
        boolean decoded = true;
        int s0 = index;
        ArrayList e0 = new ArrayList();
        Rule rule;
        decoded = false;
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_safe_tag();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    while (f1) {
                        rule = decode_safe_tag();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 >= 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        rule = null;
        if (decoded)
            rule = new attr_tag(text.substring(s0, index), e0);
        else
            index = s0;
        pop("attr-tag", decoded, index - s0);
        return (attr_tag) rule;
    }

    private attr_val decode_attr_val() {
        push("attr-val");
        boolean decoded = true;
        int s0 = index;
        ArrayList e0 = new ArrayList();
        Rule rule;
        decoded = false;
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_intval();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_strval();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_boolval();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_opaque();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        rule = null;
        if (decoded)
            rule = new attr_val(text.substring(s0, index), e0);
        else
            index = s0;
        pop("attr-val", decoded, index - s0);
        return (attr_val) rule;
    }

    private intval decode_intval() {
        push("intval");
        boolean decoded = true;
        int s0 = index;
        ArrayList e0 = new ArrayList();
        Rule rule;
        decoded = false;
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_StringValue("-");
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_DIGIT();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    while (f1) {
                        rule = decode_DIGIT();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 >= 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        rule = null;
        if (decoded)
            rule = new intval(text.substring(s0, index), e0);
        else
            index = s0;
        pop("intval", decoded, index - s0);
        return (intval) rule;
    }

    private strval decode_strval() {
        push("strval");
        boolean decoded = true;
        int s0 = index;
        ArrayList e0 = new ArrayList();
        Rule rule;
        decoded = false;
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_safe_val();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    while (f1) {
                        rule = decode_safe_val();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 >= 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        rule = null;
        if (decoded)
            rule = new strval(text.substring(s0, index), e0);
        else
            index = s0;
        pop("strval", decoded, index - s0);
        return (strval) rule;
    }

    private boolval decode_boolval() {
        push("boolval");
        boolean decoded = true;
        int s0 = index;
        ArrayList e0 = new ArrayList();
        Rule rule;
        decoded = false;
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_StringValue("true");
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_StringValue("false");
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        rule = null;
        if (decoded)
            rule = new boolval(text.substring(s0, index), e0);
        else
            index = s0;
        pop("boolval", decoded, index - s0);
        return (boolval) rule;
    }

    private opaque decode_opaque() {
        push("opaque");
        boolean decoded = true;
        int s0 = index;
        ArrayList e0 = new ArrayList();
        Rule rule;
        decoded = false;
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_StringValue("\\FF");
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_escape_val();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    while (f1) {
                        rule = decode_escape_val();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 >= 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        rule = null;
        if (decoded)
            rule = new opaque(text.substring(s0, index), e0);
        else
            index = s0;
        pop("opaque", decoded, index - s0);
        return (opaque) rule;
    }

    private safe_val decode_safe_val() {
        push("safe-val");
        boolean decoded = true;
        int s0 = index;
        ArrayList e0 = new ArrayList();
        Rule rule;
        decoded = false;
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_NumericValue("%x20", "[\\x20]", 1);
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_NumericValue("%x22-27", "[\\x22-\\x27]", 1);
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_NumericValue("%x2A-2B", "[\\x2A-\\x2B]", 1);
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_NumericValue("%x2D-3B", "[\\x2D-\\x3B]", 1);
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_NumericValue("%x3F-5B", "[\\x3F-\\x5B]", 1);
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_NumericValue("%x5D-7D", "[\\x5D-\\x7D]", 1);
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        rule = null;
        if (decoded)
            rule = new safe_val(text.substring(s0, index), e0);
        else
            index = s0;
        pop("safe-val", decoded, index - s0);
        return (safe_val) rule;
    }

    private safe_tag decode_safe_tag() {
        push("safe-tag");
        boolean decoded = true;
        int s0 = index;
        ArrayList e0 = new ArrayList();
        Rule rule;
        decoded = false;
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_NumericValue("%x20", "[\\x20]", 1);
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_NumericValue("%x22-27", "[\\x22-\\x27]", 1);
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_NumericValue("%x2B", "[\\x2B]", 1);
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_NumericValue("%x2D-3B", "[\\x2D-\\x3B]", 1);
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_NumericValue("%x3F-5B", "[\\x3F-\\x5B]", 1);
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_NumericValue("%x5D-5E", "[\\x5D-\\x5E]", 1);
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_NumericValue("%x60-7D", "[\\x60-\\x7D]", 1);
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        rule = null;
        if (decoded)
            rule = new safe_tag(text.substring(s0, index), e0);
        else
            index = s0;
        pop("safe-tag", decoded, index - s0);
        return (safe_tag) rule;
    }

    private escape_val decode_escape_val() {
        push("escape-val");
        boolean decoded = true;
        int s0 = index;
        ArrayList e0 = new ArrayList();
        Rule rule;
        decoded = false;
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_StringValue("\\");
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_HEXDIG();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_HEXDIG();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        rule = null;
        if (decoded)
            rule = new escape_val(text.substring(s0, index), e0);
        else
            index = s0;
        pop("escape-val", decoded, index - s0);
        return (escape_val) rule;
    }

    private DIGIT decode_DIGIT() {
        push("DIGIT");
        boolean decoded = true;
        int s0 = index;
        ArrayList e0 = new ArrayList();
        Rule rule;
        decoded = false;
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_NumericValue("%x30-39", "[\\x30-\\x39]", 1);
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        rule = null;
        if (decoded)
            rule = new DIGIT(text.substring(s0, index), e0);
        else
            index = s0;
        pop("DIGIT", decoded, index - s0);
        return (DIGIT) rule;
    }

    private HEXDIG decode_HEXDIG() {
        push("HEXDIG");
        boolean decoded = true;
        int s0 = index;
        ArrayList e0 = new ArrayList();
        Rule rule;
        decoded = false;
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_DIGIT();
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_StringValue("A");
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_StringValue("B");
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_StringValue("C");
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_StringValue("D");
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_StringValue("E");
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        if (!decoded) {
            {
                ArrayList e1 = new ArrayList();
                int s1 = index;
                decoded = true;
                if (decoded) {
                    boolean f1 = true;
                    int c1 = 0;
                    for (int i1 = 0; i1 < 1 && f1; i1++) {
                        rule = decode_StringValue("F");
                        if ((f1 = rule != null)) {
                            e1.add(rule);
                            c1++;
                        }
                    }
                    decoded = c1 == 1;
                }
                if (decoded)
                    e0.addAll(e1);
                else
                    index = s1;
            }
        }
        rule = null;
        if (decoded)
            rule = new HEXDIG(text.substring(s0, index), e0);
        else
            index = s0;
        pop("HEXDIG", decoded, index - s0);
        return (HEXDIG) rule;
    }

    public class StringValue extends Rule {

        public  StringValue(String spelling, ArrayList rules) {
            super(spelling, rules);
        }

        public Object visit(Visitor visitor) {
            return visitor.visit_StringValue(this);
        }
    }

    private StringValue decode_StringValue(String regex) {
        push("*StringValue", regex);
        boolean decoded = true;
        int start = index;
        StringValue stringValue = null;
        try {
            String value = text.substring(index, index + regex.length());
            if ((decoded = value.equalsIgnoreCase(regex))) {
                index += regex.length();
                stringValue = new StringValue(value, null);
            }
        } catch (IndexOutOfBoundsException e) {
            decoded = false;
        }
        pop("*StringValue", decoded, index - start);
        return stringValue;
    }

    public class NumericValue extends Rule {

        public  NumericValue(String spelling, ArrayList rules) {
            super(spelling, rules);
        }

        public Object visit(Visitor visitor) {
            return visitor.visit_NumericValue(this);
        }
    }

    private NumericValue decode_NumericValue(String spelling, String regex, int length) {
        push("*NumericValue", spelling, regex);
        boolean decoded = true;
        int start = index;
        NumericValue numericValue = null;
        try {
            String value = text.substring(index, index + length);
            if ((decoded = Pattern.matches(regex, value))) {
                index += length;
                numericValue = new NumericValue(value, null);
            }
        } catch (IndexOutOfBoundsException e) {
            decoded = false;
        }
        pop("*NumericValue", decoded, index - start);
        return numericValue;
    }

    public static final class attr_list extends Rule {

        private  attr_list(String spelling, ArrayList rules) {
            super(spelling, rules);
        }

        public  attr_list(attr_list rule) {
            super(rule.spelling, rule.rules);
        }

        public Object visit(Visitor visitor) {
            return visitor.visit_attr_list(this);
        }
    }

    public static final class attribute extends Rule {

        private  attribute(String spelling, ArrayList rules) {
            super(spelling, rules);
        }

        public  attribute(attribute rule) {
            super(rule.spelling, rule.rules);
        }

        public Object visit(Visitor visitor) {
            return visitor.visit_attribute(this);
        }
    }

    public static final class attr_val_list extends Rule {

        private  attr_val_list(String spelling, ArrayList rules) {
            super(spelling, rules);
        }

        public  attr_val_list(attr_val_list rule) {
            super(rule.spelling, rule.rules);
        }

        public Object visit(Visitor visitor) {
            return visitor.visit_attr_val_list(this);
        }
    }

    public static final class attr_tag extends Rule {

        private  attr_tag(String spelling, ArrayList rules) {
            super(spelling, rules);
        }

        public  attr_tag(attr_tag rule) {
            super(rule.spelling, rule.rules);
        }

        public Object visit(Visitor visitor) {
            return visitor.visit_attr_tag(this);
        }
    }

    public static final class attr_val extends Rule {

        private  attr_val(String spelling, ArrayList rules) {
            super(spelling, rules);
        }

        public  attr_val(attr_val rule) {
            super(rule.spelling, rule.rules);
        }

        public Object visit(Visitor visitor) {
            return visitor.visit_attr_val(this);
        }
    }

    public static final class intval extends Rule {

        private  intval(String spelling, ArrayList rules) {
            super(spelling, rules);
        }

        public  intval(intval rule) {
            super(rule.spelling, rule.rules);
        }

        public Object visit(Visitor visitor) {
            return visitor.visit_intval(this);
        }
    }

    public static final class strval extends Rule {

        private  strval(String spelling, ArrayList rules) {
            super(spelling, rules);
        }

        public  strval(strval rule) {
            super(rule.spelling, rule.rules);
        }

        public Object visit(Visitor visitor) {
            return visitor.visit_strval(this);
        }
    }

    public static final class boolval extends Rule {

        private  boolval(String spelling, ArrayList rules) {
            super(spelling, rules);
        }

        public  boolval(boolval rule) {
            super(rule.spelling, rule.rules);
        }

        public Object visit(Visitor visitor) {
            return visitor.visit_boolval(this);
        }
    }

    public static final class opaque extends Rule {

        private  opaque(String spelling, ArrayList rules) {
            super(spelling, rules);
        }

        public  opaque(opaque rule) {
            super(rule.spelling, rule.rules);
        }

        public Object visit(Visitor visitor) {
            return visitor.visit_opaque(this);
        }
    }

    public static final class safe_val extends Rule {

        private  safe_val(String spelling, ArrayList rules) {
            super(spelling, rules);
        }

        public  safe_val(safe_val rule) {
            super(rule.spelling, rule.rules);
        }

        public Object visit(Visitor visitor) {
            return visitor.visit_safe_val(this);
        }
    }

    public static final class safe_tag extends Rule {

        private  safe_tag(String spelling, ArrayList rules) {
            super(spelling, rules);
        }

        public  safe_tag(safe_tag rule) {
            super(rule.spelling, rule.rules);
        }

        public Object visit(Visitor visitor) {
            return visitor.visit_safe_tag(this);
        }
    }

    public static final class escape_val extends Rule {

        private  escape_val(String spelling, ArrayList rules) {
            super(spelling, rules);
        }

        public  escape_val(escape_val rule) {
            super(rule.spelling, rule.rules);
        }

        public Object visit(Visitor visitor) {
            return visitor.visit_escape_val(this);
        }
    }

    public static final class DIGIT extends Rule {

        private  DIGIT(String spelling, ArrayList rules) {
            super(spelling, rules);
        }

        public  DIGIT(DIGIT rule) {
            super(rule.spelling, rule.rules);
        }

        public Object visit(Visitor visitor) {
            return visitor.visit_DIGIT(this);
        }
    }

    public static final class HEXDIG extends Rule {

        private  HEXDIG(String spelling, ArrayList rules) {
            super(spelling, rules);
        }

        public  HEXDIG(HEXDIG rule) {
            super(rule.spelling, rule.rules);
        }

        public Object visit(Visitor visitor) {
            return visitor.visit_HEXDIG(this);
        }
    }
}
