/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
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
import org.eclipse.pde.api.tools.internal.provisional.RestrictionModifiers;
import org.eclipse.pde.api.tools.internal.provisional.VisibilityModifiers;
import org.eclipse.pde.api.tools.internal.provisional.comparator.ApiComparator;
import org.eclipse.pde.api.tools.internal.provisional.comparator.DeltaProcessor;
import org.eclipse.pde.api.tools.internal.provisional.comparator.IDelta;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiBaseline;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiComponent;

public class InterfaceDeltaTests extends DeltaTestSetup {

    public static Test suite() {
        return new TestSuite(InterfaceDeltaTests.class);
    //		TestSuite suite = new TestSuite(InterfaceDeltaTests.class.getName());
    //		suite.addTest(new InterfaceDeltaTests("test38"));
    //		return suite;
    }

    @Override
    public String getTestRoot() {
        //$NON-NLS-1$
        return "interface";
    }

    public  InterfaceDeltaTests(String name) {
        super(name);
    }

    /**
	 * delete API method
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
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * delete API field
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
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.FIELD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Reorder of members
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
        //$NON-NLS-1$
        assertTrue("Not empty", delta.isEmpty());
        //$NON-NLS-1$
        assertTrue("Different from NO_DELTA", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * Add type parameter
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
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.TYPE_PARAMETERS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add type parameter
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
        assertEquals("Wrong flag", IDelta.TYPE_PARAMETER, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Delete type parameter
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
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.TYPE_PARAMETER, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.TYPE_PARAMETER, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Delete type parameter
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
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.TYPE_PARAMETER, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Reorder of type parameter
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
        assertEquals("Wrong size", 4, allLeavesDeltas.length);
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
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.CLASS_BOUND, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.TYPE_PARAMETER_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[3];
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
	 * Rename of type parameter
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.TYPE_PARAMETER_NAME, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.TYPE_PARAMETER_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
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
	 * Add super interface
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.EXPANDED_SUPERINTERFACES_SET, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Remove super interface
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
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.CONTRACTED_SUPERINTERFACES_SET, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Remove super interface
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
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.CONTRACTED_SUPERINTERFACES_SET, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add super interface
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
        assertEquals("Wrong flag", IDelta.EXPANDED_SUPERINTERFACES_SET, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Addition of a field in an interface that cannot be implemented
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
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Not implement restrictions", RestrictionModifiers.isImplementRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.FIELD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Should be compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Addition of a field in an interface that cannot be extended
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
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Not extend restrictions", RestrictionModifiers.isExtendRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.FIELD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Should not be compatible", !DeltaProcessor.isCompatible(child));
    }

    /**
	 * Addition of a field in an interface that cannot be extended
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
        assertTrue("Not implement restrictions", RestrictionModifiers.isImplementRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.FIELD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Should be compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Addition of a field in an interface that can be implemented
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
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Implement restrictions", !RestrictionModifiers.isImplementRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.FIELD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Move method up in hierarchy with noimplement only
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Not implement restrictions", RestrictionModifiers.isImplementRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Method Add not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_MOVED_UP, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Move method up in hierarchy with noextend only
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Not extend restrictions", RestrictionModifiers.isExtendRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Method Add not compatible", !DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_MOVED_UP, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Move method up in hierarchy with noextend and noimplement
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Not implement restrictions", RestrictionModifiers.isImplementRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Method Add compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_MOVED_UP, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Move method up in hierarchy
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertFalse("Is implement restrictions", RestrictionModifiers.isImplementRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_MOVED_UP, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add a member type
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
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Not implement restrictions", RestrictionModifiers.isImplementRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.TYPE_MEMBER, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add a member types
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
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertFalse("Is implement restrictions", RestrictionModifiers.isImplementRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.TYPE_MEMBER, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add member types
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Not implement restrictions", RestrictionModifiers.isImplementRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.TYPE_MEMBER, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertTrue("Not implement restrictions", RestrictionModifiers.isImplementRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.TYPE_MEMBER, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Remove member types
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
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.TYPE_MEMBER, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add member types
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertFalse("Is implement restrictions", RestrictionModifiers.isImplementRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.TYPE_MEMBER, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Is compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertFalse("Is implement restrictions", RestrictionModifiers.isImplementRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.TYPE_MEMBER, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Move field up in hierarchy
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertFalse("Is implement restrictions", RestrictionModifiers.isImplementRestriction(child.getCurrentRestrictions()));
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.FIELD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.FIELD_MOVED_UP, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Added one parameter to an API method (=> addition and removal of a method)
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=244984
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.SUPER_INTERFACE_WITH_METHODS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.EXPANDED_SUPERINTERFACES_SET, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=244984
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.SUPER_INTERFACE_WITH_METHODS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Method Add not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.EXPANDED_SUPERINTERFACES_SET, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Changed superinterfaces set not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=230189
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.SUPER_INTERFACE_WITH_METHODS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Method Add not compatible", !DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.EXPANDED_SUPERINTERFACES_SET, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Changed superinterfaces set not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=230189
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.SUPER_INTERFACE_WITH_METHODS, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Method Add compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.EXPANDED_SUPERINTERFACES_SET, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Changed superinterfaces set not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=244984
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
        assertEquals("Wrong size", 3, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.TYPE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_COMPONENT_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.EXPANDED_SUPERINTERFACES_SET, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[2];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_MOVED_UP, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=244984
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Method Add compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_MOVED_UP, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=230189
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Method Add compatible", !DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_MOVED_UP, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=230189
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
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Method Add not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.METHOD_MOVED_UP, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    public void test38() {
        //$NON-NLS-1$
        deployBundles("test38");
        IApiBaseline before = getBeforeState();
        IApiBaseline after = getAfterState();
        IApiComponent beforeApiComponent = before.getApiComponent(BUNDLE_NAME);
        //$NON-NLS-1$
        assertNotNull("no api component", beforeApiComponent);
        assertFalse(beforeApiComponent.hasApiDescription());
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
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Added deprecation
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
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Removed deprecation
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
        assertEquals("Wrong element type", IDelta.INTERFACE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Remove @noimplement and add new methods
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
        IDelta delta = ApiComparator.compare(beforeApiComponent, afterApiComponent, before, after, VisibilityModifiers.API, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }
}
