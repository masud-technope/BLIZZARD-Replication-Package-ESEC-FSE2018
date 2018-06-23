/*******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.ecf.internal.remoteservice.apt.java6;

import java.io.PrintWriter;

/**
 * A class to help with generating nicely-formatted Java source files.
 * This class does not handle all possible situations; the caller should
 * expect to mingle calls to this class with calls directly to the
 * underlying PrintWriter.
 * <p>
 * This class does not have any validation intelligence, and does not
 * attempt to protect the caller from generating bogus code.
 */
public class JavaFormatter {

    public enum AccessSpecifier implements  {

        DEFAULT() {
        }
        , PRIVATE() {
        }
        , PROTECTED() {
        }
        , PUBLIC() {
        }
        ;

        public String getKeyword() {
            switch(this) {
                default:
                    return "";
                case PRIVATE:
                    return "private";
                case PROTECTED:
                    return "protected";
                case PUBLIC:
                    return "public";
            }
        }
    }

    protected final PrintWriter _out;

    protected final String _indent;

    protected int _indentLevel = 0;

    /**
	 * Silly example showing typical use of this class.  
	 * This method can be deleted if desired.
	 */
    public static void main(String[] args) {
        PrintWriter pw = new PrintWriter(System.out);
        JavaFormatter f = new JavaFormatter(pw);
        f.printCommentHeader("JavaFormatter");
        f.printPackage("p");
        pw.println();
        f.printImport("q.A");
        f.printImport("q.B");
        pw.println();
        f.openClass(true, AccessSpecifier.PUBLIC, "Test", null, new String[] { "A", "B" });
        f.printField(AccessSpecifier.PROTECTED, true, true, "MSG", "\"Hello \"");
        f.openMethod(false, AccessSpecifier.DEFAULT, "String", "getHelloMsg", new String[] { "String", "int" }, new String[] { "guest", "count" }, true);
        f.printText("StringBuilder sb = new StringBuilder();");
        f.printText("sb.append(MSG);");
        f.printText("for (int i = 0; i < count; ++i) {");
        f.increaseIndent();
        f.printText("sb.append(guest).append(' ');");
        f.decreaseIndent();
        f.printText("}");
        f.printText("return sb.toString();");
        f.closeElement();
        f.openMethod(true, AccessSpecifier.PUBLIC, "int", "count", null, null, false);
        f.closeElement();
        pw.close();
    }

    /**
	 * Construct a Java source helper with a customized indentation string
	 * @param out the PrintWriter the output will be sent to
	 * @param indentString the string defining a single indent, e.g., <code>"\t"</code> or <code>"   "</code>.
	 * Can be empty or null.
	 */
    public  JavaFormatter(PrintWriter out, String indentString) {
        _indent = indentString == null ? "" : indentString;
        _out = out;
    }

    /**
	 * Construct a Java source helper that uses a default tab indentation
	 * @param out the PrintWriter the output will be sent to
	 */
    public  JavaFormatter(PrintWriter out) {
        _indent = "\t";
        _out = out;
    }

    /**
	 * Print a header message indicating that this file was generated.
	 * Note: avoid the temptation to add a timestamp; doing so will cause the
	 * file contents to change every time its parent is edited, which can have
	 * serious performance consequences.
	 * @param author the qualified name of the processor class generating the file,
	 * or null if that is not applicable
	 */
    public void printCommentHeader(String author) {
        StringBuilder sb = new StringBuilder();
        sb.append("// This file was generated");
        if (author != null) {
            sb.append(" by ");
            sb.append(author);
        }
        sb.append(". Do not edit.");
        _out.println(sb.toString());
    }

    /**
	 * Print the package statement.
	 * @param pkg the package name, e.g., "org.example.test"
	 */
    public void printPackage(String pkg) {
        assert pkg != null && pkg.length() > 0;
        _out.println("package " + pkg + ";");
    }

    /**
	 * Print an import statement.
	 * @param imprt the class or group to import, e.g., "org.xyz.Foo" or "org.xyz.*"
	 */
    public void printImport(String imprt) {
        assert imprt != null && imprt.length() > 0;
        _out.println("import " + imprt + ";");
    }

    /**
	 * Generate the first line of a class declaration.  This works for top-level or nested classes.
	 * @param isAbstract if true, class will be marked as abstract
	 * @param accessSpecifier the access specifier of this class, e.g., AccessSpecifier.PUBLIC
	 * @param name the simple name of this class
	 * @param extendName the simple name of a class that this class extends, or null if there is none
	 * @param interfaces an array of simple names of interfaces that this class implements, or null if there are none
	 */
    public void openClass(boolean isAbstract, AccessSpecifier as, String name, String extendName, String[] interfaces) {
        assert name != null && name.length() > 0;
        printIndent();
        StringBuilder sb = new StringBuilder();
        sb.append(as.getKeyword());
        if (sb.length() > 0)
            sb.append(' ');
        if (isAbstract) {
            sb.append("abstract ");
        }
        sb.append("class ");
        sb.append(name);
        if (extendName != null && extendName.length() > 0) {
            sb.append(" extends ");
            sb.append(extendName);
        }
        if (interfaces != null) {
            for (int i = 0; i < interfaces.length; ++i) {
                String intfc = interfaces[i];
                if (intfc.length() == 0)
                    continue;
                if (i == 0) {
                    sb.append(" implements ");
                } else {
                    sb.append(", ");
                }
                sb.append(intfc);
            }
        }
        sb.append(" {");
        _out.println(sb.toString());
        increaseIndent();
    }

    /**
	 * Generate the last line of a class, interface, or method declaration.  
	 * There must be one call to this method for every call to openClass() or
	 * openInterface().  There must also be one call to this method for every
	 * call to openMethod() for methods that have bodies.
	 */
    public void closeElement() {
        decreaseIndent();
        printIndent();
        _out.println("}");
    }

    /**
	 * Generate the first line of an interface declaration.  This works for top-level or nested classes.
	 * @param accessSpecifier the access specifier of this interface, e.g., AccessSpecifier.PUBLIC
	 * @param name the simple name of this interface
	 * @param extendNames an array of simple names of interfaces that this interface extends, or null if there are none
	 */
    public void openInterface(AccessSpecifier as, String name, String[] extendNames) {
        assert name != null && name.length() > 0;
        StringBuilder sb = new StringBuilder();
        sb.append(as.getKeyword());
        if (sb.length() > 0)
            sb.append(' ');
        sb.append("interface ");
        sb.append(name);
        if (extendNames != null && extendNames.length > 0) {
            sb.append(" extends ");
            for (int i = 0; i < extendNames.length; ++i) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(extendNames[i]);
            }
            sb.append(' ');
        }
        sb.append('{');
        printIndent();
        _out.println(sb.toString());
        increaseIndent();
    }

    /**
	 * Generate the first line of a method declaration.
	 * @param isAbstract true if this method is abstract.  Should be <code>false</code> for interfaces.
	 * @param accessSpecifier e.g., AccessSpecifier.PUBLIC
	 * @param returnType the return type of the method.  If null, assumed to be "void".
	 * @param name the name of the method
	 * @param paramTypes the simple names of the parameter types.  Must be the same length as <code>argNames</code>.
	 * @param params the names of the parameters.  Must be same length as <code>types</code>.
	 * @param hasBody true if this method has a body, i.e., is in a class rather than an interface and is not abstract.
	 * Ignored if the method is abstract.  
	 */
    public void openMethod(boolean isAbstract, AccessSpecifier as, String returnType, String name, String[] paramTypes, String[] params, boolean hasBody) {
        assert name != null && name.length() > 0;
        assert (paramTypes == null && params == null) || (paramTypes.length == params.length);
        if (isAbstract) {
            hasBody = false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(as.getKeyword());
        if (sb.length() > 0)
            sb.append(' ');
        if (isAbstract) {
            sb.append("abstract ");
        }
        if (returnType == null || returnType.length() == 0) {
            sb.append("void");
        } else {
            sb.append(returnType);
        }
        sb.append(' ');
        sb.append(name);
        sb.append('(');
        if (paramTypes != null) {
            for (int i = 0; i < paramTypes.length; ++i) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(paramTypes[i]);
                sb.append(' ');
                sb.append(params[i]);
            }
        }
        sb.append(')');
        if (hasBody) {
            sb.append(" {");
        } else {
            sb.append(';');
        }
        printIndent();
        _out.println(sb.toString());
        if (hasBody) {
            increaseIndent();
        }
    }

    /**
	 * Generate a field declaration
	 * @param as the access specifier (e.g., AccessSpecifier.PRIVATE)
	 * @param isStatic true if this is a static field
	 * @param isFinal true if this field is final
	 * @param name the simple name of the field
	 * @param initializer the initializer, not including '=', e.g. <code>"foo"</code>.
	 * Note that if this is a String initializer the quotes must be explicitly included.
	 * for a String field; or null if none is needed.
	 */
    public void printField(AccessSpecifier as, boolean isStatic, boolean isFinal, String name, String initializer) {
        assert name != null && name.length() > 0;
        StringBuilder sb = new StringBuilder();
        sb.append(as.getKeyword());
        if (sb.length() > 0)
            sb.append(' ');
        if (isStatic) {
            sb.append("static ");
        }
        if (isFinal) {
            sb.append("final ");
        }
        sb.append(name);
        if (initializer != null && initializer.length() > 0) {
            sb.append(" = ");
            sb.append(initializer);
        }
        sb.append(';');
        printIndent();
        _out.println(sb.toString());
    }

    /**
	 * Print an arbitrary line of text, prefixed by the proper indentation.
	 * A newline character will be appended to the printed text.
	 */
    public void printText(String text) {
        assert text != null && text.length() > 0;
        printIndent();
        _out.println(text);
    }

    /**
	 * Increase the indentation level.  This is done automatically when methods and types are opened,
	 * but may be done explicitly, e.g. when opening a block of code within a method body.  Every call
	 * to this method must be matched with a call to decreaseIndent().
	 */
    public void increaseIndent() {
        ++_indentLevel;
    }

    /**
	 * Decrease the indentation level.  This is done automatically when closing a method or type, but
	 * should be done explicitly whenever increaseIndent() has been called, e.g. to close a block of
	 * code in a method body.  Every call to this method must be matched with a call to increaseIndent().
	 */
    public void decreaseIndent() {
        --_indentLevel;
        assert _indentLevel >= 0;
    }

    /**
	 * Print the proper amount of indentation.
	 */
    protected void printIndent() {
        for (int i = 0; i < _indentLevel; ++i) {
            _out.print(_indent);
        }
    }
}
