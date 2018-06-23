public class X {

    void foo() {
        if (JavaModelManager.CP_RESOLVE_VERBOSE) {
            System.out.println(//$NON-NLS-1$ //$NON-NLS-2$
            "CPContainer SET  - setting container: [" + containerPath + "] for projects: {" + (org.eclipse.jdt.internal.compiler.util.Util.toString(affectedProjects, new org.eclipse.jdt.internal.compiler.util.Util.Displayable() {

                public String displayString(Object o) {
                    return ((IJavaProject) o).getElementName();
                }
            })) + "} with values: " + (org.eclipse.jdt.internal.compiler.util.Util.toString(respectiveContainers, new org.eclipse.jdt.internal.compiler.util.Util.Displayable() {

                public String displayString(Object o) {
                    return ((IClasspathContainer) o).getDescription();
                }
            })));
        }
    }
}
