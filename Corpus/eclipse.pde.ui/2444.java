/*******************************************************************************
 * Copyright (c) 2007, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.comparator.tests;

import org.eclipse.jdt.core.Flags;
import org.eclipse.pde.api.tools.internal.provisional.RestrictionModifiers;
import org.eclipse.pde.api.tools.internal.provisional.VisibilityModifiers;
import org.eclipse.pde.api.tools.internal.provisional.comparator.ApiComparator;
import org.eclipse.pde.api.tools.internal.provisional.comparator.DeltaProcessor;
import org.eclipse.pde.api.tools.internal.provisional.comparator.IDelta;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiBaseline;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiComponent;
import org.eclipse.pde.api.tools.internal.util.Util;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Delta tests for method
 */
public class MethodDeltaTests extends DeltaTestSetup {

    public static Test suite() {
        return new TestSuite(MethodDeltaTests.class);
    //		TestSuite suite = new TestSuite(MethodDeltaTests.class.getName());
    //		suite.addTest(new MethodDeltaTests("test126"));
    //		return suite;
    }

    public  MethodDeltaTests(String name) {
        super(name);
    }

    @Override
    public String getTestRoot() {
        //$NON-NLS-1$
        return "method";
    }

    /**
	 * Change method body
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
        //$NON-NLS-1$
        assertTrue("Not empty", delta.isEmpty());
        //$NON-NLS-1$
        assertTrue("Different from NO_DELTA", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * rename method parameter
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
        //$NON-NLS-1$
        assertTrue("Not empty", delta.isEmpty());
        //$NON-NLS-1$
        assertTrue("Different from NO_DELTA", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * Change method name
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
        assertTrue("Extend restrictions", !RestrictionModifiers.isExtendRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add formal parameter
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
        assertTrue("Extend restrictions", !RestrictionModifiers.isExtendRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Delete formal parameter
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Extend restrictions", !RestrictionModifiers.isExtendRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Change type of formal parameter
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
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Extend restrictions", !RestrictionModifiers.isExtendRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Change result type
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Extend restrictions", !RestrictionModifiers.isExtendRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add checked exception
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
        assertEquals("Wrong flag", IDelta.CHECKED_EXCEPTION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add unchecked exception
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
        assertEquals("Wrong flag", IDelta.UNCHECKED_EXCEPTION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Remove checked exception
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
        assertEquals("Wrong flag", IDelta.CHECKED_EXCEPTION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Remove unchecked exception
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
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.UNCHECKED_EXCEPTION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Reorder list of thrown exceptions
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
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        //$NON-NLS-1$
        assertTrue("Not empty", delta.isEmpty());
        //$NON-NLS-1$
        assertTrue("Different from NO_DELTA", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * Decrease visibility
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
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DECREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Decrease visibility
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
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DECREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Decrease visibility
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
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DECREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Decrease visibility
	 */
    public void test16() {
        //$NON-NLS-1$
        deployBundles("test16");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DECREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Decrease visibility
	 */
    public void test17() {
        //$NON-NLS-1$
        deployBundles("test17");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DECREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Increase visibility
	 */
    public void test18() {
        //$NON-NLS-1$
        deployBundles("test18");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.INCREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Increase visibility
	 */
    public void test19() {
        //$NON-NLS-1$
        deployBundles("test19");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.INCREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Increase visibility
	 */
    public void test20() {
        //$NON-NLS-1$
        deployBundles("test20");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.INCREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Abstract to non-abstract
	 */
    public void test21() {
        //$NON-NLS-1$
        deployBundles("test21");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ABSTRACT_TO_NON_ABSTRACT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * non-abstract to abstract
	 */
    public void test22() {
        //$NON-NLS-1$
        deployBundles("test22");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.NON_ABSTRACT_TO_ABSTRACT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * final to non-final
	 */
    public void test23() {
        //$NON-NLS-1$
        deployBundles("test23");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.FINAL_TO_NON_FINAL, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * non-final to final
	 */
    public void test24() {
        //$NON-NLS-1$
        deployBundles("test24");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Extend restrictions", !RestrictionModifiers.isExtendRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.NON_FINAL_TO_FINAL, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * static to non-static
	 */
    public void test25() {
        //$NON-NLS-1$
        deployBundles("test25");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.STATIC_TO_NON_STATIC, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * non-static to static
	 */
    public void test26() {
        //$NON-NLS-1$
        deployBundles("test26");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Is visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertTrue("Was visible", Util.isVisible(child.getOldModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.NON_STATIC_TO_STATIC, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * native to non-native
	 */
    public void test27() {
        //$NON-NLS-1$
        deployBundles("test27");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.NATIVE_TO_NON_NATIVE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * non-native to native
	 */
    public void test28() {
        //$NON-NLS-1$
        deployBundles("test28");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.NON_NATIVE_TO_NATIVE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * synchronized to non-synchronized
	 */
    public void test29() {
        //$NON-NLS-1$
        deployBundles("test29");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.SYNCHRONIZED_TO_NON_SYNCHRONIZED, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * non-synchronized to synchronized
	 */
    public void test30() {
        //$NON-NLS-1$
        deployBundles("test30");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.NON_SYNCHRONIZED_TO_SYNCHRONIZED, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add new type parameter
	 */
    public void test31() {
        //$NON-NLS-1$
        deployBundles("test31");
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
        assertEquals("Wrong flag", IDelta.TYPE_PARAMETERS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add new type parameter to a method that did not have a type parameter but
	 * that had a generic signature.
	 */
    public void test31a() {
        //$NON-NLS-1$
        deployBundles("test31a");
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
        assertEquals("Wrong flag", IDelta.TYPE_PARAMETERS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        //$NON-NLS-1$
        assertEquals("foo<U:Ljava/lang/Number;>(TU;Ljava/util/List<TU;>;)V", child.getKey());
    }

    /**
	 * Add another type parameter
	 */
    public void test32() {
        //$NON-NLS-1$
        deployBundles("test32");
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
        assertEquals("Wrong flag", IDelta.TYPE_PARAMETER, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
        //$NON-NLS-1$
        assertEquals("foo<U:Ljava/lang/Object;V:Ljava/lang/Object;>(TU;)V", child.getKey());
    }

    /**
	 * Delete type parameters
	 */
    public void test33() {
        //$NON-NLS-1$
        deployBundles("test33");
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
        assertEquals("Wrong flag", IDelta.TYPE_PARAMETER, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Delete type parameter
	 */
    public void test34() {
        //$NON-NLS-1$
        deployBundles("test34");
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
        assertEquals("Wrong flag", IDelta.TYPE_PARAMETER, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Rename type parameter
	 */
    public void test35() {
        //$NON-NLS-1$
        deployBundles("test35");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.TYPE_PARAMETER_NAME, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.TYPE_PARAMETER_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Reorder type parameters + changed class bound and interface bound
	 */
    public void test36() {
        //$NON-NLS-1$
        deployBundles("test36");
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
        assertEquals("Wrong size", 6, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.CLASS_BOUND, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.TYPE_PARAMETER_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.INTERFACE_BOUND, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.TYPE_PARAMETER_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[2];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.TYPE_PARAMETER_NAME, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.TYPE_PARAMETER_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[3];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.TYPE_PARAMETER_NAME, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.TYPE_PARAMETER_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[4];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.CLASS_BOUND, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.TYPE_PARAMETER_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[5];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.INTERFACE_BOUND, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.TYPE_PARAMETER_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Change last parameter from array to varargs
	 */
    public void test37() {
        //$NON-NLS-1$
        deployBundles("test37");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ARRAY_TO_VARARGS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Change last parameter from varargs to array
	 */
    public void test38() {
        //$NON-NLS-1$
        deployBundles("test38");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.VARARGS_TO_ARRAY, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Removed unchecked exception
	 */
    public void test39() {
        //$NON-NLS-1$
        deployBundles("test39");
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
        assertEquals("Wrong flag", IDelta.UNCHECKED_EXCEPTION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Removed checked exception
	 */
    public void test40() {
        //$NON-NLS-1$
        deployBundles("test40");
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
        assertEquals("Wrong flag", IDelta.CHECKED_EXCEPTION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add checked exception
	 */
    public void test41() {
        //$NON-NLS-1$
        deployBundles("test41");
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
        assertEquals("Wrong flag", IDelta.CHECKED_EXCEPTION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Added unchecked exception
	 */
    public void test42() {
        //$NON-NLS-1$
        deployBundles("test42");
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
        assertEquals("Wrong flag", IDelta.UNCHECKED_EXCEPTION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Added default value
	 */
    public void test43() {
        //$NON-NLS-1$
        deployBundles("test43");
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
        assertEquals("Wrong flag", IDelta.ANNOTATION_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Removed default value
	 */
    public void test44() {
        //$NON-NLS-1$
        deployBundles("test44");
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
        assertEquals("Wrong flag", IDelta.ANNOTATION_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Changed default value
	 */
    public void test45() {
        //$NON-NLS-1$
        deployBundles("test45");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ANNOTATION_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * non-final to final
	 */
    public void test46() {
        //$NON-NLS-1$
        deployBundles("test46");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.NON_FINAL_TO_FINAL, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * change return type of a package visible method
	 */
    public void test47() {
        //$NON-NLS-1$
        deployBundles("test47");
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
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertFalse("Is visible", Util.isVisible(child.getOldModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add checked exception
	 */
    public void test48() {
        //$NON-NLS-1$
        deployBundles("test48");
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
        assertEquals("Wrong flag", IDelta.CHECKED_EXCEPTION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Added abstract method
	 */
    public void test49() {
        //$NON-NLS-1$
        deployBundles("test49");
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
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Changes in a non-visible type should not report delta when only API is requested
	 */
    public void test50() {
        //$NON-NLS-1$
        deployBundles("test50");
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
        assertTrue("No NO_DELTA", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * Changes in a visible type should report delta when only API is requested
	 */
    public void test51() {
        //$NON-NLS-1$
        deployBundles("test51");
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
        assertTrue("Extend restrictions", !RestrictionModifiers.isExtendRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Changes in a visible type should report delta when only API is requested
	 * with extend restriction
	 */
    public void test52() {
        //$NON-NLS-1$
        deployBundles("test52");
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
        assertTrue("No extend restrictions", RestrictionModifiers.isExtendRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Changes in a visible type should report delta when only API is requested
	 */
    public void test53() {
        //$NON-NLS-1$
        deployBundles("test53");
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
        assertFalse("Should not be NO_DELTA", delta == ApiComparator.NO_DELTA);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Extend restrictions", !RestrictionModifiers.isExtendRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add static to a private method
	 */
    public void test54() {
        //$NON-NLS-1$
        deployBundles("test54");
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
        assertFalse("Should not be NO_DELTA", delta == ApiComparator.NO_DELTA);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertFalse("Is visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.NON_STATIC_TO_STATIC, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=226128
	 */
    public void test55() {
        //$NON-NLS-1$
        deployBundles("test55");
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
        assertFalse("Should not be NO_DELTA", delta == ApiComparator.NO_DELTA);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.OVERRIDEN_METHOD, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=226128
	 */
    public void test56() {
        //$NON-NLS-1$
        deployBundles("test56");
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
        assertFalse("Should not be NO_DELTA", delta == ApiComparator.NO_DELTA);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        //$NON-NLS-1$
        assertTrue("Not private", Flags.isPrivate(child.getNewModifiers()));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=228209
	 */
    public void test57() {
        //$NON-NLS-1$
        deployBundles("test57");
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
        assertFalse("Should not be NO_DELTA", delta == ApiComparator.NO_DELTA);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.OVERRIDEN_METHOD, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=228209
	 */
    public void test58() {
        //$NON-NLS-1$
        deployBundles("test58");
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
        assertFalse("Should not be NO_DELTA", delta == ApiComparator.NO_DELTA);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.OVERRIDEN_METHOD, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=228075
	 */
    public void test59() {
        //$NON-NLS-1$
        deployBundles("test59");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.NON_FINAL_TO_FINAL, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=228075
	 */
    public void test60() {
        //$NON-NLS-1$
        deployBundles("test60");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.NON_FINAL_TO_FINAL, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=228209
	 */
    public void test61() {
        //$NON-NLS-1$
        deployBundles("test61");
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
        assertFalse("Should not be NO_DELTA", delta == ApiComparator.NO_DELTA);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.OVERRIDEN_METHOD, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=228209
	 */
    public void test62() {
        //$NON-NLS-1$
        deployBundles("test62");
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
        assertFalse("Should not be NO_DELTA", delta == ApiComparator.NO_DELTA);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.OVERRIDEN_METHOD, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Remove @noreference on an existing method
	 */
    public void test63() {
        //$NON-NLS-1$
        deployBundles("test63");
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
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add @noreference on an existing method
	 */
    public void test64() {
        //$NON-NLS-1$
        deployBundles("test64");
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
        assertEquals("Wrong flag", IDelta.API_METHOD, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getOldModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add @noreference on an new method
	 */
    public void test65() {
        //$NON-NLS-1$
        deployBundles("test65");
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
        assertTrue("Should be NO_DELTA", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * Add @noreference on an new method
	 */
    public void test66() {
        //$NON-NLS-1$
        deployBundles("test66");
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
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Remove @noreference on an existing constructor
	 */
    public void test67() {
        //$NON-NLS-1$
        deployBundles("test67");
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
        assertEquals("Wrong flag", IDelta.CONSTRUCTOR, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add @noreference on an existing constructor
	 */
    public void test68() {
        //$NON-NLS-1$
        deployBundles("test68");
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
        assertEquals("Wrong flag", IDelta.API_CONSTRUCTOR, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getOldModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add @noreference on an new constructor
	 */
    public void test69() {
        //$NON-NLS-1$
        deployBundles("test69");
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
        assertTrue("Should be NO_DELTA", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * Add @noreference on an new constructor
	 */
    public void test70() {
        //$NON-NLS-1$
        deployBundles("test70");
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
        assertEquals("Wrong flag", IDelta.CONSTRUCTOR, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Remove @noreference on an existing annotation method
	 */
    public void test71() {
        //$NON-NLS-1$
        deployBundles("test71");
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
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add @noreference on an existing annotation method
	 */
    public void test72() {
        //$NON-NLS-1$
        deployBundles("test72");
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
        assertEquals("Wrong flag", IDelta.API_METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getOldModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add @noreference on an new annotation method
	 */
    public void test73() {
        //$NON-NLS-1$
        deployBundles("test73");
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
        assertTrue("Should be NO_DELTA", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * Add @noreference on an new annotation method
	 */
    public void test74() {
        //$NON-NLS-1$
        deployBundles("test74");
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
        assertEquals("Wrong flag", IDelta.METHOD_WITH_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Remove @noreference on an existing annotation method
	 */
    public void test75() {
        //$NON-NLS-1$
        deployBundles("test75");
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
        assertEquals("Wrong flag", IDelta.METHOD_WITHOUT_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add @noreference on an existing annotation method
	 */
    public void test76() {
        //$NON-NLS-1$
        deployBundles("test76");
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
        assertEquals("Wrong flag", IDelta.API_METHOD_WITHOUT_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getOldModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add @noreference on an new annotation method
	 */
    public void test77() {
        //$NON-NLS-1$
        deployBundles("test77");
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
        assertTrue("Should be NO_DELTA", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * Add @noreference on an new annotation method
	 */
    public void test78() {
        //$NON-NLS-1$
        deployBundles("test78");
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
        assertEquals("Wrong flag", IDelta.METHOD_WITHOUT_DEFAULT_VALUE, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.ANNOTATION_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * 229688
	 */
    public void test79() {
        //$NON-NLS-1$
        deployBundles("test79");
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
        assertEquals("Wrong size", 3, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.SUPERCLASS, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getOldModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[2];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.TYPE, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getOldModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_COMPONENT_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_MOVED_DOWN, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * 244673
	 */
    public void test80() {
        //$NON-NLS-1$
        deployBundles("test80");
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
        assertTrue("Different from NO_DELTA", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * 244673
	 */
    public void test81() {
        //$NON-NLS-1$
        deployBundles("test81");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.INCREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertTrue("Not @noreferece restriction", RestrictionModifiers.isReferenceRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * 244941
	 */
    public void test82() {
        //$NON-NLS-1$
        deployBundles("test82");
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
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertTrue("Not protected", Flags.isProtected(child.getNewModifiers()));
        //$NON-NLS-1$
        assertTrue("Not @extend restriction", RestrictionModifiers.isExtendRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=244995
	 */
    public void test83() {
        //$NON-NLS-1$
        deployBundles("test83");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DECREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertFalse("Is visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=244995
	 */
    public void test84() {
        //$NON-NLS-1$
        deployBundles("test84");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DECREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertFalse("Is visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CONSTRUCTOR_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=245166
	 */
    public void test85() {
        //$NON-NLS-1$
        deployBundles("test85");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DECREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertFalse("Is visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertTrue("Not @reference restriction", RestrictionModifiers.isReferenceRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CONSTRUCTOR_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=245166
	 */
    public void test86() {
        //$NON-NLS-1$
        deployBundles("test86");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DECREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertFalse("Is visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertTrue("Not @reference restriction", RestrictionModifiers.isReferenceRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CONSTRUCTOR_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=245166
	 */
    public void test87() {
        //$NON-NLS-1$
        deployBundles("test87");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DECREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertFalse("Is visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertTrue("Not @reference restriction", RestrictionModifiers.isReferenceRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CONSTRUCTOR_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=245166
	 */
    public void test88() {
        //$NON-NLS-1$
        deployBundles("test88");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DECREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertFalse("Is visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertTrue("Not @reference restriction", RestrictionModifiers.isReferenceRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CONSTRUCTOR_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=245166
	 */
    public void test89() {
        //$NON-NLS-1$
        deployBundles("test89");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.NON_STATIC_TO_STATIC, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertTrue("Not @reference restriction", RestrictionModifiers.isReferenceRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=245166
	 */
    public void test90() {
        //$NON-NLS-1$
        deployBundles("test90");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.STATIC_TO_NON_STATIC, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertTrue("Not @reference restriction", RestrictionModifiers.isReferenceRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=245166
	 */
    public void test91() {
        //$NON-NLS-1$
        deployBundles("test91");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DECREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertFalse("Is visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertTrue("Not @reference restriction", RestrictionModifiers.isReferenceRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=245166
	 */
    public void test92() {
        //$NON-NLS-1$
        deployBundles("test92");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DECREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertFalse("Is visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertTrue("Not @reference restriction", RestrictionModifiers.isReferenceRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=245166
	 */
    public void test93() {
        //$NON-NLS-1$
        deployBundles("test93");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.DECREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertFalse("Is visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertTrue("Not @reference restriction", RestrictionModifiers.isReferenceRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=244620
	 */
    public void test94() {
        //$NON-NLS-1$
        deployBundles("test94");
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
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=244620
	 */
    public void test95() {
        //$NON-NLS-1$
        deployBundles("test95");
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
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=244620
	 */
    public void test96() {
        //$NON-NLS-1$
        deployBundles("test96");
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
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=244620
	 */
    public void test97() {
        //$NON-NLS-1$
        deployBundles("test97");
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
        assertTrue("Not no delta", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=244620
	 */
    public void test98() {
        //$NON-NLS-1$
        deployBundles("test98");
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
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246767
	 */
    public void test99() {
        //$NON-NLS-1$
        deployBundles("test99");
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
        assertEquals("Wrong flag", IDelta.RESTRICTIONS, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertTrue("Not @override restriction", RestrictionModifiers.isOverrideRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246767
	 */
    public void test100() {
        //$NON-NLS-1$
        deployBundles("test100");
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
        assertTrue("Should be NO_DELTA", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246767
	 */
    public void test101() {
        //$NON-NLS-1$
        deployBundles("test101");
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
        assertTrue("Should be NO_DELTA", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246767
	 */
    public void test102() {
        //$NON-NLS-1$
        deployBundles("test102");
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
        assertTrue("Should be NO_DELTA", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246767
	 */
    public void test103() {
        //$NON-NLS-1$
        deployBundles("test103");
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
        assertTrue("Should be NO_DELTA", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=246767
	 */
    public void test104() {
        //$NON-NLS-1$
        deployBundles("test104");
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
        assertEquals("Wrong flag", IDelta.RESTRICTIONS, child.getFlags());
        //$NON-NLS-1$
        assertFalse("Is visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertTrue("Not @override restriction", RestrictionModifiers.isOverrideRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=228209
	 */
    public void test105() {
        //$NON-NLS-1$
        deployBundles("test105");
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
        assertFalse("Should not be NO_DELTA", delta == ApiComparator.NO_DELTA);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.OVERRIDEN_METHOD, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=242598
	 */
    public void test106() {
        //$NON-NLS-1$
        deployBundles("test106");
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
        assertFalse("Should not be NO_DELTA", delta == ApiComparator.NO_DELTA);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.NON_FINAL_TO_FINAL, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=247632
	 */
    public void test107() {
        //$NON-NLS-1$
        deployBundles("test107");
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
        assertTrue("Should be NO_DELTA", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=247952
	 */
    public void test108() {
        //$NON-NLS-1$
        deployBundles("test108");
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
        assertEquals("Wrong flag", IDelta.NON_FINAL_TO_FINAL, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=251513
	 */
    public void test109() {
        //$NON-NLS-1$
        deployBundles("test109");
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
        assertTrue("Different from NO_DELTA", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=261176
	 */
    public void test110() {
        //$NON-NLS-1$
        deployBundles("test110");
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CLASS_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=267545
	 */
    public void test111() {
        //$NON-NLS-1$
        deployBundles("test111");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        IApiComponent afterApiComponent = after.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", afterApiComponent);
        IDelta delta = ApiComparator.compare(before, after, VisibilityModifiers.API, true, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.INCREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.NON_FINAL_TO_FINAL, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Changed Map to Map&lt;String, String&gt;
	 */
    public void test112() {
        //$NON-NLS-1$
        deployBundles("test112");
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
        assertEquals("Wrong flag", IDelta.TYPE_ARGUMENTS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Changed Map to Map&lt;String, String&gt;
	 */
    public void test113() {
        //$NON-NLS-1$
        deployBundles("test113");
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
        assertEquals("Wrong flag", IDelta.TYPE_ARGUMENTS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CONSTRUCTOR_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add type parameters (constructor)
	 */
    public void test114() {
        //$NON-NLS-1$
        deployBundles("test114");
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
        assertEquals("Wrong flag", IDelta.TYPE_PARAMETERS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CONSTRUCTOR_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add checked exception (constructor)
	 */
    public void test115() {
        //$NON-NLS-1$
        deployBundles("test115");
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
        assertEquals("Wrong flag", IDelta.CHECKED_EXCEPTION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CONSTRUCTOR_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add unchecked exception (constructor)
	 */
    public void test116() {
        //$NON-NLS-1$
        deployBundles("test116");
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
        assertEquals("Wrong flag", IDelta.UNCHECKED_EXCEPTION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CONSTRUCTOR_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Change last parameter from array to varargs (constructor)
	 */
    public void test117() {
        //$NON-NLS-1$
        deployBundles("test117");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.ARRAY_TO_VARARGS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CONSTRUCTOR_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Removed unchecked exception (constructor)
	 */
    public void test118() {
        //$NON-NLS-1$
        deployBundles("test118");
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
        assertTrue("Is visible", !Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.UNCHECKED_EXCEPTION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CONSTRUCTOR_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Removed checked exception (constructor)
	 */
    public void test119() {
        //$NON-NLS-1$
        deployBundles("test119");
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
        assertTrue("Is visible", !Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.CHECKED_EXCEPTION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CONSTRUCTOR_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Increased access (constructor)
	 */
    public void test120() {
        //$NON-NLS-1$
        deployBundles("test120");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Is visible", !Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.INCREASE_ACCESS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.CONSTRUCTOR_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Non-final to final for @nooverride method
	 */
    public void test121() {
        //$NON-NLS-1$
        deployBundles("test121");
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Is visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.NON_FINAL_TO_FINAL, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Is compatible", DeltaProcessor.isCompatible(child));
    }

    public void test122() {
        //$NON-NLS-1$
        deployBundles("test122");
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
        assertEquals("Wrong size", 3, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_MOVED_DOWN, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getNewModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.CONTRACTED_SUPERINTERFACES_SET, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getOldModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[2];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.TYPE, child.getFlags());
        //$NON-NLS-1$
        assertTrue("Not visible", Util.isVisible(child.getOldModifiers()));
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_COMPONENT_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Added deprecation
	 */
    public void test123() {
        //$NON-NLS-1$
        deployBundles("test123");
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
        assertEquals("Wrong flag", IDelta.DEPRECATION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Removed deprecation
	 */
    public void test124() {
        //$NON-NLS-1$
        deployBundles("test124");
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
        assertEquals("Wrong flag", IDelta.DEPRECATION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.METHOD_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Added public method into protected member interface inside a class tagged as noextend
	 */
    public void test125() {
        //$NON-NLS-1$
        deployBundles("test125");
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
        assertTrue("Not no delta", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * Added public method into protected member interface inside a class tagged as noextend
	 */
    public void test126() {
        //$NON-NLS-1$
        deployBundles("test126");
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
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        //$NON-NLS-1$
        assertTrue("Not noextend", RestrictionModifiers.isExtendRestriction(child.getCurrentRestrictions()));
    }
}
