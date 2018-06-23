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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.api.tools.internal.provisional.VisibilityModifiers;
import org.eclipse.pde.api.tools.internal.provisional.comparator.ApiComparator;
import org.eclipse.pde.api.tools.internal.provisional.comparator.DeltaProcessor;
import org.eclipse.pde.api.tools.internal.provisional.comparator.IDelta;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiBaseline;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiComponent;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiTypeRoot;

/**
 * Delta tests for class
 */
public class BundlesDeltaTests extends DeltaTestSetup {

    public static Test suite() {
        return new TestSuite(BundlesDeltaTests.class);
    //		TestSuite suite = new TestSuite(BundlesDeltaTests.class.getName());
    //		suite.addTest(new BundlesDeltaTests("test6"));
    //		return suite;
    }

    public  BundlesDeltaTests(String name) {
        super(name);
    }

    @Override
    public String getTestRoot() {
        //$NON-NLS-1$
        return "bundles";
    }

    /**
	 * Change bundle symbolic name
	 */
    public void test1() {
        //$NON-NLS-1$
        deployBundles("test1");
        IDelta delta = ApiComparator.compare(getBeforeState(), getAfterState(), VisibilityModifiers.ALL_VISIBILITIES, false, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.API_COMPONENT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_BASELINE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Is compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.API_COMPONENT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_BASELINE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Addition of EE
	 */
    public void test2() {
        //$NON-NLS-1$
        deployBundles("test2");
        IDelta delta = ApiComparator.compare(getBeforeState(), getAfterState(), VisibilityModifiers.ALL_VISIBILITIES, false, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.EXECUTION_ENVIRONMENT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_COMPONENT_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Is compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.EXECUTION_ENVIRONMENT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_COMPONENT_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Removing EEs
	 */
    public void test3() {
        //$NON-NLS-1$
        deployBundles("test3");
        IDelta delta = ApiComparator.compare(getBeforeState(), getAfterState(), VisibilityModifiers.ALL_VISIBILITIES, false, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.EXECUTION_ENVIRONMENT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_COMPONENT_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Changing EEs
	 */
    public void test4() {
        //$NON-NLS-1$
        deployBundles("test4");
        IDelta delta = ApiComparator.compare(getBeforeState(), getAfterState(), VisibilityModifiers.ALL_VISIBILITIES, false, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.EXECUTION_ENVIRONMENT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_COMPONENT_ELEMENT_TYPE, child.getElementType());
        String[] arguments = child.getArguments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, arguments.length);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong value", "JRE-1.1", arguments[0]);
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.EXECUTION_ENVIRONMENT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_COMPONENT_ELEMENT_TYPE, child.getElementType());
        arguments = child.getArguments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, arguments.length);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong value", "CDC-1.0/Foundation-1.0", arguments[0]);
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Changing EEs
	 */
    public void test5() {
        //$NON-NLS-1$
        deployBundles("test5");
        IDelta delta = ApiComparator.compare(getBeforeState(), getAfterState(), VisibilityModifiers.ALL_VISIBILITIES, false, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.EXECUTION_ENVIRONMENT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_COMPONENT_ELEMENT_TYPE, child.getElementType());
        String[] arguments = child.getArguments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, arguments.length);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong value", "J2SE-1.4", arguments[0]);
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.EXECUTION_ENVIRONMENT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_COMPONENT_ELEMENT_TYPE, child.getElementType());
        arguments = child.getArguments();
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, arguments.length);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong value", "J2SE-1.5", arguments[0]);
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Test null profile
	 */
    public void test6() {
        //$NON-NLS-1$
        deployBundles("test6");
        try {
            ApiComparator.compare(getBeforeState(), null, VisibilityModifiers.ALL_VISIBILITIES, false, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
    }

    /**
	 * Test null baseline
	 */
    public void test7() {
        //$NON-NLS-1$
        deployBundles("test7");
        try {
            ApiComparator.compare((IApiBaseline) null, getAfterState(), VisibilityModifiers.ALL_VISIBILITIES, false, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
    }

    /**
	 * Test null components
	 */
    public void test8() {
        //$NON-NLS-1$
        deployBundles("test8");
        IApiBaseline beforeState = getBeforeState();
        IApiBaseline afterState = getAfterState();
        //$NON-NLS-1$
        IApiComponent referenceComponent = beforeState.getApiComponent("deltatest1");
        //$NON-NLS-1$
        IApiComponent component = afterState.getApiComponent("deltatest1");
        IDelta delta = ApiComparator.compare(null, component, beforeState, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.API_COMPONENT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_BASELINE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Is compatible", DeltaProcessor.isCompatible(child));
        delta = ApiComparator.compare(referenceComponent, null, beforeState, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.API_COMPONENT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_BASELINE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
        delta = ApiComparator.compare(null, component, beforeState, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.API_COMPONENT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_BASELINE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Is compatible", DeltaProcessor.isCompatible(child));
        delta = ApiComparator.compare(referenceComponent, null, beforeState, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.API_COMPONENT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_BASELINE_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
        try {
            ApiComparator.compare(referenceComponent, component, null, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        try {
            ApiComparator.compare(referenceComponent, component, beforeState, null, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        try {
            ApiComparator.compare(referenceComponent, component, beforeState, null, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        try {
            ApiComparator.compare(referenceComponent, component, null, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        try {
            ApiComparator.compare(referenceComponent, component, null, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        try {
            ApiComparator.compare(referenceComponent, component, beforeState, null, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        IApiTypeRoot classFile = null;
        try {
            //$NON-NLS-1$
            classFile = component.findTypeRoot("Zork");
        } catch (CoreException e) {
            assertTrue("Should not happen", false);
        }
        //$NON-NLS-1$
        assertNull("No class file", classFile);
        try {
            //$NON-NLS-1$
            classFile = component.findTypeRoot("X");
        } catch (CoreException e) {
            assertTrue("Should not happen", false);
        }
        //$NON-NLS-1$
        assertNotNull("No class file", classFile);
        IApiTypeRoot referenceClassFile = null;
        try {
            //$NON-NLS-1$
            referenceClassFile = referenceComponent.findTypeRoot("X");
        } catch (CoreException e) {
            assertTrue("Should not happen", false);
        }
        //$NON-NLS-1$
        assertNotNull("No class file", referenceClassFile);
        try {
            ApiComparator.compare(null, classFile, component, referenceComponent, beforeState, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        try {
            ApiComparator.compare(classFile, (IApiTypeRoot) null, component, referenceComponent, beforeState, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        try {
            ApiComparator.compare(null, component, referenceComponent, null, beforeState, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        try {
            ApiComparator.compare(referenceClassFile, (IApiTypeRoot) null, referenceComponent, null, beforeState, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        delta = ApiComparator.compare(classFile, component, referenceComponent, null, beforeState, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        //$NON-NLS-1$
        assertTrue("Not NO_DELTA", delta == ApiComparator.NO_DELTA);
        try {
            ApiComparator.compare(null, classFile, referenceComponent, component, beforeState, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        try {
            ApiComparator.compare(referenceClassFile, classFile, null, component, beforeState, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        try {
            ApiComparator.compare(referenceClassFile, classFile, referenceComponent, null, beforeState, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        try {
            ApiComparator.compare(referenceClassFile, classFile, referenceComponent, component, null, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        try {
            ApiComparator.compare(referenceClassFile, classFile, referenceComponent, component, beforeState, null, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        delta = ApiComparator.compare(referenceClassFile, classFile, referenceComponent, component, beforeState, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        //$NON-NLS-1$
        assertTrue("Not NO_DELTA", delta == ApiComparator.NO_DELTA);
        delta = ApiComparator.compare(beforeState, afterState, VisibilityModifiers.ALL_VISIBILITIES, true, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        //$NON-NLS-1$
        assertTrue("Not NO_DELTA", delta == ApiComparator.NO_DELTA);
        delta = ApiComparator.compare(beforeState, afterState, VisibilityModifiers.ALL_VISIBILITIES, true, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        //$NON-NLS-1$
        assertTrue("Not NO_DELTA", delta == ApiComparator.NO_DELTA);
        delta = ApiComparator.compare(referenceComponent, component, VisibilityModifiers.ALL_VISIBILITIES, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        //$NON-NLS-1$
        assertTrue("Not NO_DELTA", delta == ApiComparator.NO_DELTA);
        try {
            ApiComparator.compare((IApiComponent) null, beforeState, VisibilityModifiers.ALL_VISIBILITIES, true, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        try {
            ApiComparator.compare(component, null, VisibilityModifiers.ALL_VISIBILITIES, true, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        delta = ApiComparator.compare(component, beforeState, VisibilityModifiers.ALL_VISIBILITIES, true, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        //$NON-NLS-1$
        assertTrue("Not NO_DELTA", delta == ApiComparator.NO_DELTA);
        try {
            ApiComparator.compare(null, null, beforeState, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        try {
            ApiComparator.compare(classFile, component, null, null, beforeState, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        try {
            ApiComparator.compare(classFile, component, referenceComponent, null, null, afterState, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
        try {
            ApiComparator.compare(classFile, component, referenceComponent, null, beforeState, null, VisibilityModifiers.ALL_VISIBILITIES, null);
            //$NON-NLS-1$
            assertFalse("Should not be reached", true);
        } catch (IllegalArgumentException e) {
        }
    }

    /**
	 * Removing EEs
	 */
    public void test9() {
        //$NON-NLS-1$
        deployBundles("test9");
        IDelta delta = ApiComparator.compare(getBeforeState(), getAfterState(), VisibilityModifiers.ALL_VISIBILITIES, false, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 3, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.EXECUTION_ENVIRONMENT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_COMPONENT_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.ADDED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.EXECUTION_ENVIRONMENT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_COMPONENT_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[2];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.EXECUTION_ENVIRONMENT, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_COMPONENT_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Removed api packages - bug 225473
	 */
    public void test10() {
        //$NON-NLS-1$
        deployBundles("test10");
        IDelta delta = ApiComparator.compare(getBeforeState(), getAfterState(), VisibilityModifiers.API, false, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.MAJOR_VERSION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_COMPONENT_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
        child = allLeavesDeltas[1];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.REMOVED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.API_TYPE, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_COMPONENT_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertFalse("Is compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Add type in non API package
	 */
    public void test11() {
        //$NON-NLS-1$
        deployBundles("test11");
        IApiBaseline beforeState = getBeforeState();
        IApiBaseline afterState = getAfterState();
        //$NON-NLS-1$
        IApiComponent apiComponent = afterState.getApiComponent("deltatest1");
        //$NON-NLS-1$
        assertNotNull("No api component", apiComponent);
        //$NON-NLS-1$
        IApiComponent refApiComponent = beforeState.getApiComponent("deltatest1");
        //$NON-NLS-1$
        assertNotNull("No api component", refApiComponent);
        IApiTypeRoot classFile = null;
        try {
            //$NON-NLS-1$
            classFile = apiComponent.findTypeRoot("p.X2");
        } catch (CoreException e) {
        }
        //$NON-NLS-1$
        assertNotNull("No p.X2", classFile);
        IDelta delta = ApiComparator.compare(classFile, refApiComponent, apiComponent, null, beforeState, afterState, VisibilityModifiers.API, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        //$NON-NLS-1$
        assertTrue("Wrong delta", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * Remove internal type in non API package
	 */
    public void test12() {
        //$NON-NLS-1$
        deployBundles("test12");
        IApiBaseline beforeState = getBeforeState();
        IApiBaseline afterState = getAfterState();
        //$NON-NLS-1$
        IApiComponent apiComponent = afterState.getApiComponent("deltatest1");
        //$NON-NLS-1$
        assertNotNull("No api component", apiComponent);
        //$NON-NLS-1$
        IApiComponent refApiComponent = beforeState.getApiComponent("deltatest1");
        //$NON-NLS-1$
        assertNotNull("No api component", refApiComponent);
        IDelta delta = ApiComparator.compare(getBeforeState(), getAfterState(), VisibilityModifiers.API, false, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.MAJOR_VERSION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_COMPONENT_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Change major version
	 */
    public void test13() {
        //$NON-NLS-1$
        deployBundles("test13");
        IApiBaseline beforeState = getBeforeState();
        IApiBaseline afterState = getAfterState();
        //$NON-NLS-1$
        IApiComponent apiComponent = afterState.getApiComponent("deltatest1");
        //$NON-NLS-1$
        assertNotNull("No api component", apiComponent);
        //$NON-NLS-1$
        IApiComponent refApiComponent = beforeState.getApiComponent("deltatest1");
        //$NON-NLS-1$
        assertNotNull("No api component", refApiComponent);
        IDelta delta = ApiComparator.compare(getBeforeState(), getAfterState(), VisibilityModifiers.API, false, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.MAJOR_VERSION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_COMPONENT_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Change minor version
	 */
    public void test14() {
        //$NON-NLS-1$
        deployBundles("test14");
        IApiBaseline beforeState = getBeforeState();
        IApiBaseline afterState = getAfterState();
        //$NON-NLS-1$
        IApiComponent apiComponent = afterState.getApiComponent("deltatest1");
        //$NON-NLS-1$
        assertNotNull("No api component", apiComponent);
        //$NON-NLS-1$
        IApiComponent refApiComponent = beforeState.getApiComponent("deltatest1");
        //$NON-NLS-1$
        assertNotNull("No api component", refApiComponent);
        IDelta delta = ApiComparator.compare(getBeforeState(), getAfterState(), VisibilityModifiers.API, false, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        IDelta[] allLeavesDeltas = collectLeaves(delta);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, allLeavesDeltas.length);
        IDelta child = allLeavesDeltas[0];
        //$NON-NLS-1$
        assertEquals("Wrong kind", IDelta.CHANGED, child.getKind());
        //$NON-NLS-1$
        assertEquals("Wrong flag", IDelta.MINOR_VERSION, child.getFlags());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IDelta.API_COMPONENT_ELEMENT_TYPE, child.getElementType());
        //$NON-NLS-1$
        assertTrue("Not compatible", DeltaProcessor.isCompatible(child));
    }

    /**
	 * Test if diff is returned using org.eclipse.pde.api.tools.internal.provisional.comparator.ApiComparator.compare(IApiComponent, IApiBaseline, int, boolean)
	 */
    public void test15() {
        //$NON-NLS-1$
        deployBundles("test15");
        IApiBaseline beforeState = getBeforeState();
        IApiBaseline afterState = getAfterState();
        //$NON-NLS-1$
        IApiComponent component = afterState.getApiComponent("deltatest");
        IDelta delta = ApiComparator.compare(component, beforeState, VisibilityModifiers.API, false, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        //$NON-NLS-1$
        assertFalse("Equals to NO_DELTA", delta == ApiComparator.NO_DELTA);
    }

    /**
	 * Test if diff is returned using org.eclipse.pde.api.tools.internal.provisional.comparator.ApiComparator.compare(IApiComponent, IApiBaseline, int, boolean)
	 */
    public void test16() {
        //$NON-NLS-1$
        deployBundles("test16");
        IApiBaseline beforeState = getBeforeState();
        IApiBaseline afterState = getAfterState();
        //$NON-NLS-1$
        IApiComponent component = afterState.getApiComponent("deltatest");
        IDelta delta = ApiComparator.compare(component, beforeState, VisibilityModifiers.API, true, null);
        //$NON-NLS-1$
        assertNotNull("No delta", delta);
        //$NON-NLS-1$
        assertFalse("Equals to NO_DELTA", delta == ApiComparator.NO_DELTA);
    }
}
