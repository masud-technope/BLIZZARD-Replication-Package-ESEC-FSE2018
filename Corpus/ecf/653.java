package org.eclipse.ecf.examples.internal.eventadmin.app;

public class NonSerializable {

    private final String payload;

    public  NonSerializable(String string) {
        this.payload = string;
    }

    public String getPayload() {
        return payload;
    }
}
