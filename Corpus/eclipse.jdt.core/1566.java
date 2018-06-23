package junit.runner;

/**
 * An interface to define how a test suite should be loaded.
 */
public interface TestSuiteLoader {

    public abstract Class load(String suiteClassName) throws ClassNotFoundException;

    public abstract Class reload(Class aClass) throws ClassNotFoundException;
}
