public class A {

    public void launch() {
        try {
            if ((javaProject == null) || !javaProject.exists()) {
                //$NON-NLS-1$
                abort(PDEPlugin.getResourceString("JUnitLaunchConfiguration.error.invalidproject"), null, IJavaLaunchConfigurationConstants.ERR_NOT_A_JAVA_PROJECT);
            }
        } catch (CoreException e) {
        }
    }
}
