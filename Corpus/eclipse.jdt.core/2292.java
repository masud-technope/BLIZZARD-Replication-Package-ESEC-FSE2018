/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *        Andy Clement - Contributions for
 *                          Bug 383624 - [1.8][compiler] Revive code generation support for type annotations (from Olivier's work)
 *        Jesper Steen Moller - Contributions for
 *							Bug 406973 - [compiler] Parse MethodParameters attribute
 *******************************************************************************/
package org.eclipse.jdt.core.util;

/**
 * Description of attribute names as described in the JVM specifications.
 *
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IAttributeNamesConstants {

    /**
	 * "Synthetic" attribute.
	 * <p>Note that prior to JDK 1.5, synthetic elements were always marked
	 * using an attribute; with 1.5, synthetic elements can also be marked
	 * using the {@link IModifierConstants#ACC_SYNTHETIC} flag.
	 * </p>
	 * @since 2.0
	 */
    //$NON-NLS-1$
    char[] SYNTHETIC = "Synthetic".toCharArray();

    /**
	 * "ConstantValue" attribute.
	 * @since 2.0
	 */
    //$NON-NLS-1$
    char[] CONSTANT_VALUE = "ConstantValue".toCharArray();

    /**
	 * "LineNumberTable" attribute.
	 * @since 2.0
	 */
    //$NON-NLS-1$
    char[] LINE_NUMBER = "LineNumberTable".toCharArray();

    /**
	 * "LocalVariableTable" attribute.
	 * @since 2.0
	 */
    //$NON-NLS-1$
    char[] LOCAL_VARIABLE = "LocalVariableTable".toCharArray();

    /**
	 * "InnerClasses" attribute.
	 * @since 2.0
	 */
    //$NON-NLS-1$
    char[] INNER_CLASSES = "InnerClasses".toCharArray();

    /**
	 * "Code" attribute.
	 * @since 2.0
	 */
    //$NON-NLS-1$
    char[] CODE = "Code".toCharArray();

    /**
	 * "Exceptions" attribute.
	 * @since 2.0
	 */
    //$NON-NLS-1$
    char[] EXCEPTIONS = "Exceptions".toCharArray();

    /**
	 * "SourceFile" attribute.
	 * @since 2.0
	 */
    //$NON-NLS-1$
    char[] SOURCE = "SourceFile".toCharArray();

    /**
	 * "Deprecated" attribute.
	 * @since 2.0
	 */
    //$NON-NLS-1$
    char[] DEPRECATED = "Deprecated".toCharArray();

    /**
	 * "Signature" attribute (added in J2SE 1.5).
	 * Class file readers which support J2SE 1.5 return
	 * attributes with this name represented by objects
	 * implementing {@link ISignatureAttribute}.
	 * @since 3.0
	 */
    //$NON-NLS-1$
    char[] SIGNATURE = "Signature".toCharArray();

    /**
	 * "EnclosingMethod" attribute (added in J2SE 1.5).
	 * Class file readers which support J2SE 1.5 return
	 * attributes with this name represented by objects
	 * implementing {@link IEnclosingMethodAttribute}.
	 * @since 3.0
	 */
    //$NON-NLS-1$
    char[] ENCLOSING_METHOD = "EnclosingMethod".toCharArray();

    /**
	 * "LocalVariableTypeTable" attribute (added in J2SE 1.5).
	 * @since 3.0
	 */
    //$NON-NLS-1$
    char[] LOCAL_VARIABLE_TYPE_TABLE = "LocalVariableTypeTable".toCharArray();

    /**
	 * "RuntimeVisibleAnnotations" attribute (added in J2SE 1.5).
	 * @since 3.0
	 */
    //$NON-NLS-1$
    char[] RUNTIME_VISIBLE_ANNOTATIONS = "RuntimeVisibleAnnotations".toCharArray();

    /**
	 * "RuntimeInvisibleAnnotations" attribute (added in J2SE 1.5).
	 * @since 3.0
	 */
    //$NON-NLS-1$
    char[] RUNTIME_INVISIBLE_ANNOTATIONS = "RuntimeInvisibleAnnotations".toCharArray();

    /**
	 * "RuntimeVisibleParameterAnnotations" attribute (added in J2SE 1.5).
	 * @since 3.0
	 */
    //$NON-NLS-1$
    char[] RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS = "RuntimeVisibleParameterAnnotations".toCharArray();

    /**
	 * "RuntimeInvisibleParameterAnnotations" attribute (added in J2SE 1.5).
	 * @since 3.0
	 */
    //$NON-NLS-1$
    char[] RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS = "RuntimeInvisibleParameterAnnotations".toCharArray();

    /**
	 * "AnnotationDefault" attribute (added in J2SE 1.5).
	 * @since 3.0
	 */
    //$NON-NLS-1$
    char[] ANNOTATION_DEFAULT = "AnnotationDefault".toCharArray();

    /**
	 * "StackMapTable" attribute (added in J2SE 1.6).
	 * @since 3.2
	 */
    //$NON-NLS-1$
    char[] STACK_MAP_TABLE = "StackMapTable".toCharArray();

    /**
	 * "StackMap" attribute (added in cldc1.0).
	 * @since 3.2
	 */
    //$NON-NLS-1$
    char[] STACK_MAP = "StackMap".toCharArray();

    /**
	 * "RuntimeVisibleTypeAnnotations" attribute (added in jsr 308).
	 * @since 3.10
	 */
    //$NON-NLS-1$
    char[] RUNTIME_VISIBLE_TYPE_ANNOTATIONS = "RuntimeVisibleTypeAnnotations".toCharArray();

    /**
	 * "RuntimeInvisibleTypeAnnotations" attribute (added in jsr 308).
	 * @since 3.10
	 */
    //$NON-NLS-1$
    char[] RUNTIME_INVISIBLE_TYPE_ANNOTATIONS = "RuntimeInvisibleTypeAnnotations".toCharArray();

    /**
	 * "BootstrapMethods" attribute (added in cldc1.0).
	 * @since 3.8
	 */
    //$NON-NLS-1$
    char[] BOOTSTRAP_METHODS = "BootstrapMethods".toCharArray();

    /**
	 * "MethodParameters" attribute (added in jep118).
	 * @since 3.10
	 */
    //$NON-NLS-1$
    char[] METHOD_PARAMETERS = "MethodParameters".toCharArray();
}
