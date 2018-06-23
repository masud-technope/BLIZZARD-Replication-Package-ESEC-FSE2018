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
package ch.ethz.iks.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SmartConstants {

    /**
	 * the positive list contains class names of classes that are
	 * string-serializable.
	 */
    static List positiveList = new ArrayList(Arrays.asList(new Object[] { //$NON-NLS-1$ //$NON-NLS-2$
    "java.lang.Integer", //$NON-NLS-1$ //$NON-NLS-2$
    "java.lang.Boolean", "java.lang.Long", "java.lang.Short", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
    "java.lang.Byte" }));

    static HashSet blackList = new HashSet();

    static {
        //$NON-NLS-1$
        blackList.add("org.osgi.framework.ServiceReference");
        //$NON-NLS-1$
        blackList.add("org.osgi.framework.ServiceRegistration");
    }

    static final HashMap idToClass = new HashMap();

    static final HashMap classToId = new HashMap();

    static {
        //$NON-NLS-1$
        idToClass.put("I", Integer.class);
        //$NON-NLS-1$
        classToId.put(Integer.class.getName(), "I");
        //$NON-NLS-1$
        idToClass.put("Z", Boolean.class);
        //$NON-NLS-1$
        classToId.put(Boolean.class.getName(), "Z");
        //$NON-NLS-1$
        idToClass.put("J", Long.class);
        //$NON-NLS-1$
        classToId.put(Long.class.getName(), "J");
        //$NON-NLS-1$
        idToClass.put("S", Short.class);
        //$NON-NLS-1$
        classToId.put(Short.class.getName(), "S");
        //$NON-NLS-1$
        idToClass.put("B", Byte.class);
        //$NON-NLS-1$
        classToId.put(Byte.class.getName(), "B");
        //$NON-NLS-1$
        idToClass.put("C", Character.class);
        //$NON-NLS-1$
        classToId.put(Character.class.getName(), "C");
        //$NON-NLS-1$
        idToClass.put("D", Double.class);
        //$NON-NLS-1$
        classToId.put(Double.class.getName(), "D");
        //$NON-NLS-1$
        idToClass.put("F", Float.class);
        //$NON-NLS-1$
        classToId.put(Float.class.getName(), "F");
    }
}
