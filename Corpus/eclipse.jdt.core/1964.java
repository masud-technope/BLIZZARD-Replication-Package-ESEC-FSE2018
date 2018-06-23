/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.problem;

public interface ProblemSeverities {

    // during handling only
    final int Warning = 0;

    // when bit is set: problem is error, if not it is a warning
    final int Error = 1;

    final int AbortCompilation = 2;

    final int AbortCompilationUnit = 4;

    final int AbortType = 8;

    final int AbortMethod = 16;

    // 2r11110
    final int Abort = 30;

    // when bit is set: problem was configurable
    final int Optional = 32;

    final int SecondaryError = 64;

    // when bit is set: problem was either a mandatory error, or an optional+treatOptionalErrorAsFatal
    final int Fatal = 128;

    // during handling only
    final int Ignore = 256;

    // always exposed, even when silent error handling policy is in effect.
    final int InternalError = 512;

    // When bit is set, the unit or project is not flagged.
    final int Info = 1024;

    final int CoreSeverityMASK = Warning | Error | Info | Ignore;
}
