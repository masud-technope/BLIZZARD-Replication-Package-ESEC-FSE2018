/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.internal.remoteservice.apt.java6;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import org.eclipse.ecf.internal.remoteservice.apt.java6.JavaFormatter.AccessSpecifier;
import org.eclipse.ecf.remoteservice.AsyncService;
import org.eclipse.ecf.remoteservice.IAsyncRemoteServiceProxy;

@SupportedAnnotationTypes({ "org.eclipse.ecf.remoteservice.AsyncService" })
public class AsyncAnnotationProcessor extends AbstractProcessor {

    private static final String FUTURE_PACKAGE_IMPORT = org.eclipse.equinox.concurrent.future.IFuture.class.getName();

    private static final String REMOTESERVICE_PACKAGE_IMPORT = org.eclipse.ecf.remoteservice.IAsyncCallback.class.getName();

    public  AsyncAnnotationProcessor() {
        super();
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ProcessingEnvironment env = super.processingEnv;
        Filer filer = env.getFiler();
        Set<TypeElement> annotatedDecls = (Set<TypeElement>) roundEnv.getElementsAnnotatedWith(AsyncService.class);
        for (Iterator<TypeElement> i = annotatedDecls.iterator(); i.hasNext(); ) {
            writeAsyncType(filer, i.next());
        }
        return true;
    }

    private void writeAsyncType(Filer filer, TypeElement te) {
        String qualifiedAsyncName = te.getQualifiedName().toString() + IAsyncRemoteServiceProxy.ASYNC_INTERFACE_SUFFIX;
        String simpleAsyncName = te.getSimpleName().toString() + IAsyncRemoteServiceProxy.ASYNC_INTERFACE_SUFFIX;
        int lastDot = qualifiedAsyncName.lastIndexOf(".");
        String packageAsyncName = null;
        if (lastDot > 0) {
            packageAsyncName = qualifiedAsyncName.substring(0, lastDot);
        }
        JavaFileObject javaFile = null;
        try {
            javaFile = filer.createSourceFile(qualifiedAsyncName, (Element[]) null);
            Writer writer = javaFile.openWriter();
            PrintWriter pw = new PrintWriter(writer);
            JavaFormatter formatter = new JavaFormatter(pw);
            if (packageAsyncName != null) {
                formatter.printPackage(packageAsyncName);
                pw.println();
            }
            formatter.printImport(FUTURE_PACKAGE_IMPORT);
            formatter.printImport(REMOTESERVICE_PACKAGE_IMPORT);
            pw.println();
            formatter.printText("@SuppressWarnings(\"restriction\")");
            formatter.openInterface(AccessSpecifier.PUBLIC, simpleAsyncName, new String[] { IAsyncRemoteServiceProxy.class.getName() });
            pw.println();
            writeAsyncMethods(filer, te, (List<ExecutableElement>) te.getEnclosedElements(), formatter);
            pw.println();
            formatter.closeElement();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeAsyncMethods(Filer filer, TypeElement te, List<ExecutableElement> list, JavaFormatter formatter) {
        for (Iterator<ExecutableElement> i = list.iterator(); i.hasNext(); ) {
            ExecutableElement elem = i.next();
            writeAsyncMethodCallback(elem, formatter);
            writeAsyncMethodFuture(elem, formatter);
        }
    }

    private void writeAsyncMethodCallback(ExecutableElement methodDecl, JavaFormatter formatter) {
        String resultType = getResultTypeForExecutableElement(methodDecl);
        List<VariableElement> methodParams = (List<VariableElement>) methodDecl.getParameters();
        List ptypes = new ArrayList();
        List ps = new ArrayList();
        for (Iterator<VariableElement> i = methodParams.iterator(); i.hasNext(); ) {
            VariableElement m = i.next();
            TypeMirror tm = m.asType();
            ptypes.add(tm.toString());
            ps.add(m.getSimpleName().toString());
        }
        final String asyncCallbackClassname = "IAsyncCallback";
        if (resultType == null) {
            ptypes.add(asyncCallbackClassname + "<Void>");
        } else {
            ptypes.add(asyncCallbackClassname + "<" + resultType + ">");
        }
        ps.add("callback");
        formatter.openMethod(false, AccessSpecifier.PUBLIC, null, methodDecl.getSimpleName() + IAsyncRemoteServiceProxy.ASYNC_METHOD_SUFFIX, (String[]) ptypes.toArray(new String[] {}), (String[]) ps.toArray(new String[] {}), false);
    }

    private String getBoxedTypeNameForTypeMirror(TypeMirror resultType) {
        if (resultType == null)
            return null;
        if (resultType instanceof ArrayType) {
            ArrayType at = (ArrayType) resultType;
            String boxedType = getBoxedTypeNameForTypeMirror(at.getComponentType());
            if (boxedType == null)
                return null;
            return boxedType + "[]";
        }
        TypeKind type = resultType.getKind();
        if (type == null)
            return null;
        if (type.equals(TypeKind.BOOLEAN)) {
            return "Boolean";
        } else if (type.equals(TypeKind.BYTE)) {
            return "Byte";
        } else if (type.equals(TypeKind.CHAR)) {
            return "Character";
        } else if (type.equals(TypeKind.DOUBLE)) {
            return "Double";
        } else if (type.equals(TypeKind.FLOAT)) {
            return "Float";
        } else if (type.equals(TypeKind.INT)) {
            return "Integer";
        } else if (type.equals(TypeKind.LONG)) {
            return "Long";
        } else if (type.equals(TypeKind.SHORT)) {
            return "Short";
        } else if (type.equals(TypeKind.VOID)) {
            return "Void";
        } else if (type.equals(TypeKind.DECLARED)) {
            return resultType.toString();
        }
        return null;
    }

    private String getResultTypeForExecutableElement(ExecutableElement methodDecl) {
        if (methodDecl == null)
            return null;
        TypeMirror returnType = methodDecl.getReturnType();
        if (returnType == null)
            return null;
        return getBoxedTypeNameForTypeMirror(returnType);
    }

    private void writeAsyncMethodFuture(ExecutableElement methodDecl, JavaFormatter formatter) {
        List<VariableElement> methodParams = (List<VariableElement>) methodDecl.getParameters();
        List ptypes = new ArrayList();
        List ps = new ArrayList();
        for (Iterator<VariableElement> i = methodParams.iterator(); i.hasNext(); ) {
            VariableElement m = i.next();
            TypeMirror tm = m.asType();
            ptypes.add(tm.toString());
            ps.add(m.getSimpleName().toString());
        }
        formatter.printText("@SuppressWarnings(\"rawtypes\")");
        formatter.openMethod(false, AccessSpecifier.PUBLIC, "IFuture", methodDecl.getSimpleName() + IAsyncRemoteServiceProxy.ASYNC_METHOD_SUFFIX, (String[]) ptypes.toArray(new String[] {}), (String[]) ps.toArray(new String[] {}), false);
    }
}
