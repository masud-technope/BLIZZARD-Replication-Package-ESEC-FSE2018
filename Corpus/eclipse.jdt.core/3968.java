/*******************************************************************************
 * Copyright (c) 2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package targets.model.pc;

public class J {

    // same-named fields in F, G, and H do NOT hide this; unrelated enclosing classes
    int fieldInt;

    // does not hide, and is not hidden by, any of the fields named fieldInt in F, G, H, or this.
    int fieldInt() {
        return 0;
    }

    // does not hide, and is not hidden by, same class in F or H
    public class FChild {
    }

    // does not hide, and is not hidden by, outer class F
    public class F {
    }

    // does not hide, and is not hidden by, F.staticMethod()
    public static void staticMethod() {
    }
}
