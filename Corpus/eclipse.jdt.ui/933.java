/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.ui.text;

/**
 * Definition of Java partitioning and its partitions.
 *
 * @since 3.1
 */
public interface IJavaPartitions {

    /**
	 * The identifier of the Java partitioning.
	 */
    //$NON-NLS-1$
    String JAVA_PARTITIONING = "___java_partitioning";

    /**
	 * The identifier of the single-line (JLS2: EndOfLineComment) end comment partition content type.
	 */
    //$NON-NLS-1$
    String JAVA_SINGLE_LINE_COMMENT = "__java_singleline_comment";

    /**
	 * The identifier multi-line (JLS2: TraditionalComment) comment partition content type.
	 */
    //$NON-NLS-1$
    String JAVA_MULTI_LINE_COMMENT = "__java_multiline_comment";

    /**
	 * The identifier of the Javadoc (JLS2: DocumentationComment) partition content type.
	 */
    //$NON-NLS-1$
    String JAVA_DOC = "__java_javadoc";

    /**
	 * The identifier of the Java string partition content type.
	 */
    //$NON-NLS-1$
    String JAVA_STRING = "__java_string";

    /**
	 * The identifier of the Java character partition content type.
	 */
    //$NON-NLS-1$
    String JAVA_CHARACTER = "__java_character";
}
