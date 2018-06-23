public class C {

    Object pipeIn;

    public String foo() {
        if (pipeIn == null)
            // spawn parsing thread
            getReader();
        return "ok";
    }

    public String bar() {
        if (pipeIn == null)
            // spawn parsing thread
            getReader();
        return "ok";
    }

    void getReader() {
    }
}
