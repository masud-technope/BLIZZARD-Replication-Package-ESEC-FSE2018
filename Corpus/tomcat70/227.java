/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tomcat.util.buf;

import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;

public class TestAscii {

    @Test
    public void testParseLong1() {
        // Long.MAX_VALUE
        String value = "9223372036854775807";
        byte[] bytes = value.getBytes();
        long result = Ascii.parseLong(bytes, 0, bytes.length);
        Assert.assertEquals(value, String.valueOf(result));
    }

    @Test(expected = NumberFormatException.class)
    public void testParseLong2() {
        // Long.MAX_VALUE + 1
        byte[] bytes = "9223372036854775808".getBytes();
        long result = Ascii.parseLong(bytes, 0, bytes.length);
        Assert.fail("NumberFormatException expected, got: " + result);
    }

    @Test(expected = NumberFormatException.class)
    public void testParseLong3() {
        // Long.MAX_VALUE + 3
        byte[] bytes = "9223372036854775810".getBytes();
        long result = Ascii.parseLong(bytes, 0, bytes.length);
        Assert.fail("NumberFormatException expected, got: " + result);
    }

    @Test(expected = NumberFormatException.class)
    public void testParseLong4() {
        BigInteger x = BigInteger.valueOf(5000000000L).shiftLeft(32);
        byte[] bytes = String.valueOf(x).getBytes();
        long result = Ascii.parseLong(bytes, 0, bytes.length);
        Assert.fail("NumberFormatException expected, got: " + result);
    }

    @Test
    public void testParseLong5() {
        // Long.MAX_VALUE - 1
        String value = "9223372036854775806";
        byte[] bytes = value.getBytes();
        long result = Ascii.parseLong(bytes, 0, bytes.length);
        Assert.assertEquals(value, String.valueOf(result));
    }
}
