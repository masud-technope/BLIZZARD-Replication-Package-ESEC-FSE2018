/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler;

import java.util.Locale;
import org.eclipse.jdt.core.compiler.*;

public interface IProblemFactory {

    IProblem createProblem(char[] originatingFileName, int problemId, String[] problemArguments, // shorter versions of the problemArguments
    String[] messageArguments, int severity, int startPosition, int endPosition, int lineNumber);

    Locale getLocale();

    String getLocalizedMessage(int problemId, String[] messageArguments);
}
