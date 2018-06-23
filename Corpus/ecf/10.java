/* Copyright (c) 2006-2009 Jan S. Rellermeyer
 * Systems Group,
 * Department of Computer Science, ETH Zurich.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of ETH Zurich nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.iks.r_osgi.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import ch.ethz.iks.r_osgi.messages.DeliverServiceMessage;
import ch.ethz.iks.util.StringUtils;

/**
 * <p>
 * The code analyzer takes a service interface together with additionally
 * registered parts (like smart proxies) and determines the injection set by
 * static code analysis.
 * </p>
 * <p>
 * The idea is to inject all types that are referenced by the service interface
 * so that the proxy becomes self-contained. However, at the current state, only
 * own types are injected. Imported types are not, since they are considered to
 * belong to the required environment.
 * <p>
 * <p>
 * For the moment, the code analysis does not cover accessed resources from the
 * bundle. In later revisions, it is likely that this will be introduced.
 * </p>
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.6
 */
final class CodeAnalyzer extends ClassVisitor {

    /**
	 * the class loader of the service provider bundle.
	 */
    private final ClassLoader loader;

    /**
	 * the closure list.
	 */
    private final ArrayList closure = new ArrayList();

    /**
	 * the set of already visited types.
	 */
    final HashSet visited = new HashSet();

    /**
	 * the injection set.
	 */
    private final HashMap injections = new HashMap();

    /**
	 * import map.
	 */
    private final HashMap importsMap;

    /**
	 * export map.
	 */
    private final HashMap exportsMap;

    /**
	 * imports of the proxy. Is generated during code analyis.
	 */
    private final HashSet proxyImports = new HashSet(0);

    /**
	 * exports of the proxy. Is generated during code analysis.
	 */
    private final HashSet proxyExports = new HashSet(0);

    /**
	 * the method analyzer.
	 */
    private final MethodVisitor methodVisitor = new MethodAnalyzer();

    private String currentClass;

    /**
	 * create a new code analyzer instance.
	 * 
	 * @param loader
	 *            the class loader of the service bundle.
	 * @param imports
	 *            the imports of the service bundle.
	 * @param exports
	 *            the exports of the service bundle.
	 */
     CodeAnalyzer(final ClassLoader loader, final String imports, final String exports) {
        super(Opcodes.ASM5);
        this.loader = loader;
        if (imports != null) {
            //$NON-NLS-1$
            final String[] tokens = StringUtils.stringToArray(imports, ",");
            importsMap = new HashMap(tokens.length);
            for (int i = 0; i < tokens.length; i++) {
                final int pos = //$NON-NLS-1$
                tokens[i].indexOf(//$NON-NLS-1$
                ";");
                if (pos > -1) {
                    importsMap.put(tokens[i].substring(0, pos), tokens[i].substring(pos + 1, tokens[i].length()));
                } else {
                    importsMap.put(tokens[i], null);
                }
            }
        } else {
            importsMap = new HashMap(0);
        }
        if (exports != null) {
            //$NON-NLS-1$
            final String[] tokens = StringUtils.stringToArray(exports, ",");
            exportsMap = new HashMap(tokens.length);
            for (int i = 0; i < tokens.length; i++) {
                final int pos = //$NON-NLS-1$
                tokens[i].indexOf(//$NON-NLS-1$
                ";");
                if (pos > -1) {
                    exportsMap.put(tokens[i].substring(0, pos), tokens[i].substring(pos + 1, tokens[i].length()));
                } else {
                    exportsMap.put(tokens[i], null);
                }
            }
        } else {
            exportsMap = new HashMap(0);
        }
    }

    /**
	 * analyze the service.
	 * 
	 * @param iface
	 *            the service interface class name.
	 * @param smartProxy
	 *            the smart proxy class name or <code>null</code>
	 * @param explicitInjections
	 *            the injection array or <code>null</code>
	 * @param presentation
	 *            the presentation class name or <code>null</code>
	 * @return the <code>DeliverServiceMessage</code> that contains all
	 *         information required to build a proxy on the client side. This
	 *         message is generic, can be cached and initialized for a concrete
	 *         requesting peer by calling the <code>init</code> method.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
    DeliverServiceMessage analyze(final String[] ifaces, final String smartProxy, final String[] explicitInjections, final String presentation) throws ClassNotFoundException, IOException {
        closure.addAll(Arrays.asList(ifaces));
        if (smartProxy != null) {
            closure.add(smartProxy);
        }
        if (presentation != null) {
            closure.add(presentation);
        }
        if (explicitInjections != null) {
            closure.addAll(Arrays.asList(explicitInjections));
        }
        while (!closure.isEmpty()) {
            visit((String) closure.remove(0));
        }
        for (int i = 0; i < ifaces.length; i++) {
            proxyImports.add(packageOf(ifaces[i]));
            proxyExports.add(packageOf(ifaces[i]));
        }
        // remove the obvious imports to save network bandwidth
        //$NON-NLS-1$
        proxyImports.remove("org.osgi.framework");
        //$NON-NLS-1$
        proxyImports.remove("ch.ethz.iks.r_osgi");
        //$NON-NLS-1$
        proxyImports.remove("ch.ethz.iks.r_osgi.types");
        //$NON-NLS-1$
        proxyImports.remove("ch.ethz.iks.r_osgi.channels");
        final StringBuffer importDeclaration = new StringBuffer();
        final StringBuffer exportDeclaration = new StringBuffer();
        final String[] pi = (String[]) proxyImports.toArray(new String[proxyImports.size()]);
        for (int i = 0; i < pi.length; i++) {
            importDeclaration.append(pi[i]);
            final Object v = importsMap.get(pi[i]);
            if (v != null) {
                //$NON-NLS-1$
                importDeclaration.append(//$NON-NLS-1$
                "; ");
                importDeclaration.append(v);
            }
            if (i < pi.length - 1) {
                //$NON-NLS-1$
                importDeclaration.append(//$NON-NLS-1$
                ", ");
            }
        }
        final String[] pe = (String[]) proxyExports.toArray(new String[proxyExports.size()]);
        for (int i = 0; i < pe.length; i++) {
            exportDeclaration.append(pe[i]);
            final Object v = exportsMap.get(pe[i]);
            if (v != null) {
                //$NON-NLS-1$
                exportDeclaration.append(//$NON-NLS-1$
                "; ");
                exportDeclaration.append(v);
            }
            if (i < pe.length - 1) {
                //$NON-NLS-1$
                exportDeclaration.append(//$NON-NLS-1$
                ", ");
            }
        }
        final DeliverServiceMessage message = new DeliverServiceMessage();
        message.setInterfaceNames(ifaces);
        message.setSmartProxyName(smartProxy);
        message.setInjections((HashMap) injections.clone());
        message.setImports(importDeclaration.toString());
        message.setExports(exportDeclaration.toString());
        visited.clear();
        injections.clear();
        closure.clear();
        return message;
    }

    /**
	 * visit a class.
	 * 
	 * @param className
	 *            the class name.
	 * @throws ClassNotFoundException
	 *             if the class is unknown.
	 * @throws IOException
	 *             if the classes bytecode cannot be found and accessed.
	 */
    private void visit(final String className) throws ClassNotFoundException {
        currentClass = className.replace('.', '/');
        // remove array indicators
        if (currentClass.startsWith("[L")) {
            currentClass = currentClass.substring(2);
        } else if (currentClass.startsWith("L")) {
            currentClass = currentClass.substring(1);
        }
        //$NON-NLS-1$
        final String classFile = currentClass + ".class";
        final String pkg = packageOf(className);
        if (importsMap.containsKey(pkg) || exportsMap.containsKey(pkg)) {
            proxyExports.add(pkg);
        }
        try {
            final ClassReader reader = new ClassReader(loader.getResourceAsStream(classFile));
            injections.put(classFile, reader.b);
            if (exportsMap.containsKey(pkg)) {
                proxyExports.add(pkg);
            }
            reader.accept(this, ClassReader.SKIP_DEBUG + ClassReader.SKIP_FRAMES);
        } catch (final IOException ioe) {
            throw new ClassNotFoundException(className);
        }
    }

    /**
	 * get the package of a class.
	 * 
	 * @param cls
	 *            the class.
	 * @return the package name.
	 */
    private String packageOf(final String cls) {
        //$NON-NLS-1$
        final int p = cls.lastIndexOf(".");
        //$NON-NLS-1$
        return p > -1 ? cls.substring(0, p).trim() : "";
    }

    /**
	 * visit a type.
	 * 
	 * @param t
	 *            the type.
	 */
    void visitType(final Type t) {
        if (t.getSort() < Type.ARRAY) {
            visited.add(t.getClassName());
            return;
        }
        if (t.getSort() == Type.ARRAY) {
            visitType(t.getElementType());
            return;
        }
        if (//$NON-NLS-1$
        "null".equals(t.getClassName())) {
            return;
        }
        final String className = t.getClassName();
        final String iClassName = className.replace('.', '/');
        if (visited.contains(iClassName)) {
            return;
        }
        // do not inject java* classes
        if (//$NON-NLS-1$
        className.startsWith("java")) {
            visited.add(iClassName);
            return;
        }
        final String pkg = packageOf(className);
        if (importsMap.containsKey(pkg)) {
            visited.add(iClassName);
            proxyImports.add(pkg);
            return;
        }
        // do not inject osgi classes
        if (//$NON-NLS-1$
        className.startsWith("org.osgi")) {
            visited.add(iClassName);
            return;
        }
        visited.add(iClassName);
        closure.add(className);
    }

    /**
	 * 
	 * @see org.objectweb.asm.ClassVisitor#visit(int, int, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String[])
	 */
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        if (superName != null && !visited.contains(superName)) {
            visitType(Type.getType('L' + superName + ';'));
        }
        for (int i = 0; i < interfaces.length; i++) {
            if (!visited.contains(interfaces[i])) {
                visitType(Type.getType('L' + interfaces[i] + ';'));
            }
        }
    }

    /**
	 * 
	 * @see org.objectweb.asm.ClassVisitor#visitAnnotation(java.lang.String,
	 *      boolean)
	 */
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        return null;
    }

    /**
	 * 
	 * @see org.objectweb.asm.ClassVisitor#visitAttribute(org.objectweb.asm.Attribute)
	 */
    public void visitAttribute(final Attribute attr) {
    }

    /**
	 * 
	 * @see org.objectweb.asm.ClassVisitor#visitEnd()
	 */
    public void visitEnd() {
    }

    /**
	 * 
	 * @see org.objectweb.asm.ClassVisitor#visitField(int, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.Object)
	 */
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        if (!visited.contains(desc)) {
            visitType(Type.getType(desc));
        }
        return null;
    }

    /**
	 * 
	 * @see org.objectweb.asm.ClassVisitor#visitInnerClass(java.lang.String,
	 *      java.lang.String, java.lang.String, int)
	 */
    public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
        if (!name.equals(currentClass) && !visited.contains(currentClass)) {
            closure.add(name.replace('/', '.'));
        }
    }

    /**
	 * 
	 * @see org.objectweb.asm.ClassVisitor#visitMethod(int, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String[])
	 */
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final Type[] methodTypes = Type.getArgumentTypes(desc);
        for (int i = 0; i < methodTypes.length; i++) {
            if (!visited.contains(methodTypes[i].getClassName().replace('.', '/'))) {
                visitType(methodTypes[i]);
            }
        }
        final Type returnType = Type.getReturnType(desc);
        if (!visited.contains(returnType.getClassName().replace('.', '/'))) {
            visitType(returnType);
        }
        if (exceptions != null) {
            for (int i = 0; i < exceptions.length; i++) {
                if (!visited.contains(exceptions[i])) {
                    visitType(Type.getType('L' + exceptions[i] + ';'));
                }
            }
        }
        if ((access & Opcodes.ACC_ABSTRACT) == 0) {
            return methodVisitor;
        } else {
            return null;
        }
    }

    /**
	 * 
	 * @see org.objectweb.asm.ClassVisitor#visitOuterClass(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
    public void visitOuterClass(final String owner, final String name, final String desc) {
    }

    /**
	 * 
	 * @see org.objectweb.asm.ClassVisitor#visitSource(java.lang.String,
	 *      java.lang.String)
	 */
    public void visitSource(final String source, final String debug) {
    }

    /**
	 * the method analyzer.
	 * 
	 * @author Jan S. Rellermeyer, ETH Zurich
	 */
    final class MethodAnalyzer extends MethodVisitor {

        protected  MethodAnalyzer() {
            super(Opcodes.ASM5);
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitAnnotation(java.lang.String,
		 *      boolean)
		 */
        public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
            if (!visited.contains(desc)) {
                //$NON-NLS-1$ //$NON-NLS-2$
                visitType(Type.getType("L" + desc + ";"));
            }
            return null;
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitAnnotationDefault()
		 */
        public AnnotationVisitor visitAnnotationDefault() {
            return null;
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitAttribute(org.objectweb.asm.Attribute)
		 */
        public void visitAttribute(final Attribute attr) {
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitCode()
		 */
        public void visitCode() {
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitEnd()
		 */
        public void visitEnd() {
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitFieldInsn(int,
		 *      java.lang.String, java.lang.String, java.lang.String)
		 */
        public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
            if (!visited.contains(owner)) {
                //$NON-NLS-1$ //$NON-NLS-2$
                visitType(Type.getType("L" + owner + ";"));
            }
            if (!visited.contains(desc)) {
                //$NON-NLS-1$ //$NON-NLS-2$
                visitType(Type.getType(desc));
            }
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitIincInsn(int, int)
		 */
        public void visitIincInsn(final int var, final int increment) {
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitInsn(int)
		 */
        public void visitInsn(final int opcode) {
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitIntInsn(int, int)
		 */
        public void visitIntInsn(final int opcode, final int operand) {
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitJumpInsn(int,
		 *      org.objectweb.asm.Label)
		 */
        public void visitJumpInsn(final int opcode, final Label label) {
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitLabel(org.objectweb.asm.Label)
		 */
        public void visitLabel(final Label label) {
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitLdcInsn(java.lang.Object)
		 */
        public void visitLdcInsn(final Object cst) {
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitLineNumber(int,
		 *      org.objectweb.asm.Label)
		 */
        public void visitLineNumber(final int line, final Label start) {
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitLocalVariable(java.lang.String,
		 *      java.lang.String, java.lang.String, org.objectweb.asm.Label,
		 *      org.objectweb.asm.Label, int)
		 */
        public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
            if (!visited.contains(desc)) {
                //$NON-NLS-1$ //$NON-NLS-2$
                visitType(Type.getType("L" + desc + ";"));
            }
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitLookupSwitchInsn(org.objectweb.asm.Label,
		 *      int[], org.objectweb.asm.Label[])
		 */
        public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitMaxs(int, int)
		 */
        public void visitMaxs(final int maxStack, final int maxLocals) {
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitMethodInsn(int,
		 *      java.lang.String, java.lang.String, java.lang.String)
		 */
        public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
            if (!visited.contains(owner)) {
                //$NON-NLS-1$ //$NON-NLS-2$
                visitType(Type.getType("L" + owner + ";"));
            }
        }

        /**
		 * @see org.objectweb.asm.MethodVisitor#visitMethodInsn(int,
		 *      java.lang.String, java.lang.String, java.lang.String, boolean)
		 */
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (!visited.contains(owner)) {
                //$NON-NLS-1$ //$NON-NLS-2$
                visitType(Type.getType("L" + owner + ";"));
            }
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitMultiANewArrayInsn(java.lang.String,
		 *      int)
		 */
        public void visitMultiANewArrayInsn(final String desc, final int dims) {
            if (!visited.contains(desc)) {
                //$NON-NLS-1$ //$NON-NLS-2$
                visitType(Type.getType("L" + desc + ";"));
            }
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitParameterAnnotation(int,
		 *      java.lang.String, boolean)
		 */
        public AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc, final boolean visible) {
            if (!visited.contains(desc)) {
                //$NON-NLS-1$ //$NON-NLS-2$
                visitType(Type.getType("L" + desc + ";"));
            }
            return null;
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitTableSwitchInsn(int, int,
		 *      org.objectweb.asm.Label, org.objectweb.asm.Label[])
		 */
        public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label[] labels) {
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitTryCatchBlock(org.objectweb.asm.Label,
		 *      org.objectweb.asm.Label, org.objectweb.asm.Label,
		 *      java.lang.String)
		 */
        public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {
            if (!visited.contains(type)) {
                //$NON-NLS-1$ //$NON-NLS-2$
                visitType(Type.getType("L" + type + ";"));
            }
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitTypeInsn(int,
		 *      java.lang.String)
		 */
        public void visitTypeInsn(final int opcode, final String desc) {
            if (!visited.contains(desc)) {
                //$NON-NLS-1$ //$NON-NLS-2$
                visitType(Type.getType("L" + desc + ";"));
            }
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitVarInsn(int, int)
		 */
        public void visitVarInsn(final int opcode, final int var) {
        }

        /**
		 * 
		 * @see org.objectweb.asm.MethodVisitor#visitFrame(int, int,
		 *      java.lang.Object[], int, java.lang.Object[])
		 */
        public void visitFrame(final int arg0, final int arg1, final Object[] arg2, final int arg3, final Object[] arg4) {
        }
    }
}
