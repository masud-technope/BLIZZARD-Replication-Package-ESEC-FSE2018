/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.comparator.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.pde.api.tools.internal.provisional.VisibilityModifiers;
import org.eclipse.pde.api.tools.internal.provisional.comparator.ApiComparator;
import org.eclipse.pde.api.tools.internal.provisional.comparator.DeltaProcessor;
import org.eclipse.pde.api.tools.internal.provisional.comparator.IDelta;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiBaseline;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiComponent;
import org.eclipse.pde.api.tools.internal.util.Util;

/**
 * Delta tests for enum
 */
public class EnumDeltaTests extends DeltaTestSetup {

    public static Test suite() {
        return new TestSuite(EnumDeltaTests.class);
    //		TestSuite suite = new TestSuite(EnumDeltaTests.class.getName());
    //		suite.addTest(new EnumDeltaTests("test13"));
    //		return suite;
    }

    public  EnumDeltaTests(String name) {
        super(name);
    }

    @Override
    public String getTestRoot() {
        //$NON-NLS-1$
        return "enum";
    }

    /**
	 * delete enum constant
	 */
    public void test1() {
        //$NON-NLS-1$
        deployBundles("test1");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ENUM_CONSTANT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ENUM_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * rename enum constant = remove + add
	 */
    public void test2() {
        //$NON-NLS-1$
        deployBundles("test2");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ENUM_CONSTANT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ENUM_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ENUM_CONSTANT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ENUM_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add enum constant arguments
	 */
    public void test3() {
        //$NON-NLS-1$
        deployBundles("test3");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Is visible", !Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.CONSTRUCTOR, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ENUM_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertFalse("Is visible", Util.isVisible(child.getOldModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.CONSTRUCTOR, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ENUM_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Change enum constant arguments
	 */
    public void test4() {
        //$NON-NLS-1$
        deployBundles("test4");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Is visible", !Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.CONSTRUCTOR, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ENUM_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertFalse("Is visible", Util.isVisible(child.getOldModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.CONSTRUCTOR, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ENUM_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add new enum constant
	 */
    public void test5() {
        //$NON-NLS-1$
        deployBundles("test5");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ENUM_CONSTANT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ENUM_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add new enum constant
	 */
    public void test6() {
        //$NON-NLS-1$
        deployBundles("test6");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        //$NON-NLS-1$
        assertTrue("Not empty", delta.isEmpty());
        //$NON-NLS-1$
        assertTrue("Different from NO_DELTA", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * Added non visible method
	 */
    public void test7() {
        //$NON-NLS-1$
        deployBundles("test7");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Is visible", !Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ENUM_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Added non visible method
	 */
    public void test8() {
        //$NON-NLS-1$
        deployBundles("test8");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Is visible", !Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ENUM_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Added @noreference to an existing enum constant
	 */
    public void test9() {
        //$NON-NLS-1$
        deployBundles("test9");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.API, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getOldModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.API_ENUM_CONSTANT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ENUM_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Added @noreference to a new enum constant
	 */
    public void test10() {
        //$NON-NLS-1$
        deployBundles("test10");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.API, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        //$NON-NLS-1$
        assertTrue("Wrong delta", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * Added @noreference to a new enum constant
	 */
    public void test11() {
        //$NON-NLS-1$
        deployBundles("test11");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ENUM_CONSTANT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ENUM_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Removed @noreference to a new enum constant
	 */
    public void test12() {
        //$NON-NLS-1$
        deployBundles("test12");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.API, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ENUM_CONSTANT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ENUM_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Decrease access for an enum type
	 */
    public void test13() {
        //$NON-NLS-1$
        deployBundles("test13");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.API, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertFalse("Is visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DECREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ENUM_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Added deprecation
	 */
    public void test14() {
        //$NON-NLS-1$
        deployBundles("test14");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.API, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DEPRECATION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ENUM_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Removed deprecation
	 */
    public void test15() {
        //$NON-NLS-1$
        deployBundles("test15");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.API, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DEPRECATION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ENUM_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }
}
