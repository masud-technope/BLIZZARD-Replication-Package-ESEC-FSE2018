package ch.ethz.iks.test.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import ch.ethz.iks.r_osgi.types.BoxedPrimitive;
import ch.ethz.iks.util.SmartSerializer;
import junit.framework.TestCase;

public class SmartSerializerTestCase extends TestCase {

    public void setUp() throws InterruptedException {
    }

    public void testSmartSerializer() throws Exception {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(bout);
        // some test objects ...
        final String testString = "test";
        final Object[] testArray = { "test1", "test2", "test3" };
        final String[] testStringArray = { "test1", "test2", "test3", "test4" };
        final byte[] testBytes = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        final Integer testInteger = new Integer(10);
        final byte[] bigByteArray = new byte[1024];
        final BoxedPrimitive testBp = new BoxedPrimitive(100L);
        final float[] floats = new float[1000];
        final Dictionary dict = new Hashtable();
        dict.put("test1", new Integer(10));
        dict.put("test2", "value");
        // serialize
        SmartSerializer.serialize(testString, out);
        SmartSerializer.serialize(testArray, out);
        SmartSerializer.serialize(testStringArray, out);
        SmartSerializer.serialize(testBytes, out);
        SmartSerializer.serialize(testInteger, out);
        SmartSerializer.serialize(bigByteArray, out);
        SmartSerializer.serialize(testBp, out);
        SmartSerializer.serialize(floats, out);
        SmartSerializer.serialize(dict, out);
        out.flush();
        final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
        assertEquals(testString, SmartSerializer.deserialize(in));
        assertArrayEquals(testArray, (Object[]) SmartSerializer.deserialize(in));
        assertArrayEquals(testStringArray, (String[]) SmartSerializer.deserialize(in));
        assertByteArrayEquals(testBytes, (byte[]) SmartSerializer.deserialize(in));
        assertEquals(testInteger, SmartSerializer.deserialize(in));
        assertByteArrayEquals(bigByteArray, (byte[]) SmartSerializer.deserialize(in));
        assertEquals(testBp.getBoxed(), SmartSerializer.deserialize(in));
        assertFloatArrayEquals(floats, (float[]) SmartSerializer.deserialize(in));
        assertEquals(dict, (Dictionary) SmartSerializer.deserialize(in));
    }

    private void assertArrayEquals(Object[] a1, Object[] a2) {
        assertEquals(a1.length, a2.length);
        for (int i = 0; i < a1.length; i++) {
            assertEquals(a1[i], a2[i]);
        }
    }

    private void assertByteArrayEquals(byte[] b1, byte[] b2) {
        assertEquals(b1.length, b2.length);
        for (int i = 0; i < b1.length; i++) {
            assertEquals(b1[i], b2[i]);
        }
    }

    private void assertFloatArrayEquals(float[] f1, float[] f2) {
        assertEquals(f1.length, f2.length);
        for (int i = 0; i < f1.length; i++) {
            assertEquals(f1[i], f2[i], 0);
        }
    }
}
