public class X {

     X(String s) {
    }

    protected void foo() {
        //$NON-NLS-1$
        Main.bind(//$NON-NLS-1$
        "compile.instantTime", new String[] { String.valueOf(this.lineCount), String.valueOf(this.time), String.valueOf(((int) (this.lineCount * 10000.0 / this.time)) / 10.0) });
    }
}
