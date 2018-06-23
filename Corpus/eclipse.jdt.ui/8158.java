/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <Lars.Vogel@vogella.com> - Bug 487901, 488432
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.template.java;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.jface.text.templates.TemplateVariableResolver;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.JavaElementLabels;

/**
 * Compilation unit context type.
 */
public abstract class CompilationUnitContextType extends TemplateContextType {

    protected static class ReturnType extends TemplateVariableResolver {

        public  ReturnType() {
            //$NON-NLS-1$
            super("return_type", JavaTemplateMessages.CompilationUnitContextType_variable_description_return_type);
        }

        @Override
        protected String resolve(TemplateContext context) {
            IJavaElement element = ((CompilationUnitContext) context).findEnclosingElement(IJavaElement.METHOD);
            if (element == null)
                return null;
            try {
                return Signature.toString(((IMethod) element).getReturnType());
            } catch (JavaModelException e) {
                return null;
            }
        }
    }

    protected static class File extends TemplateVariableResolver {

        public  File() {
            //$NON-NLS-1$
            super("file", JavaTemplateMessages.CompilationUnitContextType_variable_description_file);
        }

        @Override
        protected String resolve(TemplateContext context) {
            ICompilationUnit unit = ((CompilationUnitContext) context).getCompilationUnit();
            return (unit == null) ? null : unit.getElementName();
        }

        /*
		 * @see org.eclipse.jface.text.templates.TemplateVariableResolver#isUnambiguous(org.eclipse.jface.text.templates.TemplateContext)
		 */
        @Override
        protected boolean isUnambiguous(TemplateContext context) {
            return resolve(context) != null;
        }
    }

    protected static class PrimaryTypeName extends TemplateVariableResolver {

        public  PrimaryTypeName() {
            //$NON-NLS-1$
            super("primary_type_name", JavaTemplateMessages.CompilationUnitContextType_variable_description_primary_type_name);
        }

        @Override
        protected String resolve(TemplateContext context) {
            ICompilationUnit unit = ((CompilationUnitContext) context).getCompilationUnit();
            if (unit == null)
                return null;
            return JavaCore.removeJavaLikeExtension(unit.getElementName());
        }

        /*
		 * @see org.eclipse.jface.text.templates.TemplateVariableResolver#isUnambiguous(org.eclipse.jface.text.templates.TemplateContext)
		 */
        @Override
        protected boolean isUnambiguous(TemplateContext context) {
            return resolve(context) != null;
        }
    }

    protected static class EnclosingJavaElement extends TemplateVariableResolver {

        protected final int fElementType;

        public  EnclosingJavaElement(String name, String description, int elementType) {
            super(name, description);
            fElementType = elementType;
        }

        @Override
        protected String resolve(TemplateContext context) {
            IJavaElement element = ((CompilationUnitContext) context).findEnclosingElement(fElementType);
            if (element instanceof IType)
                return JavaElementLabels.getElementLabel(element, JavaElementLabels.T_CONTAINER_QUALIFIED);
            return (element == null) ? null : element.getElementName();
        }

        /*
		 * @see org.eclipse.jface.text.templates.TemplateVariableResolver#isUnambiguous(org.eclipse.jface.text.templates.TemplateContext)
		 */
        @Override
        protected boolean isUnambiguous(TemplateContext context) {
            return resolve(context) != null;
        }
    }

    protected static class Method extends EnclosingJavaElement {

        public  Method() {
            //$NON-NLS-1$
            super("enclosing_method", JavaTemplateMessages.CompilationUnitContextType_variable_description_enclosing_method, IJavaElement.METHOD);
        }
    }

    protected static class Type extends EnclosingJavaElement {

        public  Type() {
            //$NON-NLS-1$
            super("enclosing_type", JavaTemplateMessages.CompilationUnitContextType_variable_description_enclosing_type, IJavaElement.TYPE);
        }
    }

    protected static class Package extends EnclosingJavaElement {

        public  Package() {
            //$NON-NLS-1$
            super("enclosing_package", JavaTemplateMessages.CompilationUnitContextType_variable_description_enclosing_package, IJavaElement.PACKAGE_FRAGMENT);
        }
    }

    protected static class Project extends EnclosingJavaElement {

        public  Project() {
            //$NON-NLS-1$
            super("enclosing_project", JavaTemplateMessages.CompilationUnitContextType_variable_description_enclosing_project, IJavaElement.JAVA_PROJECT);
        }
    }

    protected static class Arguments extends TemplateVariableResolver {

        public  Arguments() {
            //$NON-NLS-1$
            super("enclosing_method_arguments", JavaTemplateMessages.CompilationUnitContextType_variable_description_enclosing_method_arguments);
        }

        @Override
        protected String resolve(TemplateContext context) {
            IJavaElement element = ((CompilationUnitContext) context).findEnclosingElement(IJavaElement.METHOD);
            if (element == null)
                return null;
            IMethod method = (IMethod) element;
            try {
                String[] arguments = method.getParameterNames();
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < arguments.length; i++) {
                    if (i > 0)
                        //$NON-NLS-1$
                        buffer.append(", ");
                    buffer.append(arguments[i]);
                }
                return buffer.toString();
            } catch (JavaModelException e) {
                return null;
            }
        }
    }

    /*
	 * @see ContextType#ContextType(String)
	 */
    public  CompilationUnitContextType(String id) {
        super(id);
    }

    public  CompilationUnitContextType() {
    }

    public abstract CompilationUnitContext createContext(IDocument document, int completionPosition, int length, ICompilationUnit compilationUnit);

    public abstract CompilationUnitContext createContext(IDocument document, Position completionPosition, ICompilationUnit compilationUnit);

    @Override
    protected void validateVariables(TemplateVariable[] variables) throws TemplateException {
        // check for multiple cursor variables
        for (int i = 0; i < variables.length; i++) {
            TemplateVariable var = variables[i];
            if (var.getType().equals(GlobalTemplateVariables.Cursor.NAME)) {
                if (var.getOffsets().length > 1) {
                    throw new TemplateException(JavaTemplateMessages.ContextType_error_multiple_cursor_variables);
                }
            }
        }
    }
}
