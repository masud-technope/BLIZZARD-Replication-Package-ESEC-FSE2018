package org.eclipse.ecf.core.sharedobject.util;

import java.util.Map;

/**
 * @since 2.4
 */
public class PropertiesUtil {

    @SuppressWarnings("unchecked")
    public static final <T> T getProperty(Class<T> type, Map<String, ?> properties, String name, T def) {
        if (properties == null)
            return def;
        if (name == null)
            return def;
        Object o = properties.get(name);
        if (o == null)
            return def;
        if (type.isInstance(o))
            return (T) o;
        return def;
    }

    public static final String getPropertyString(Map<String, ?> properties, String name, String def) {
        return getProperty(String.class, properties, name, def);
    }

    public static final String getPropertyString(Map<String, ?> properties, String name) {
        return getPropertyString(properties, name, null);
    }

    public static final float getPropertyFloat(Map<String, ?> properties, String name, float def) {
        return getProperty(Float.class, properties, name, def);
    }

    public static final float getPropertyFloat(Map<String, ?> properties, String name) {
        return getPropertyFloat(properties, name, 0.0f);
    }

    public static final double getPropertyDouble(Map<String, ?> properties, String name, double def) {
        return getProperty(Double.class, properties, name, def);
    }

    public static final double getPropertyDouble(Map<String, ?> properties, String name) {
        return getPropertyDouble(properties, name, 0.0d);
    }

    public static final int getPropertyInteger(Map<String, ?> properties, String name, int def) {
        return getProperty(Integer.class, properties, name, def);
    }

    public static final int getPropertyInteger(Map<String, ?> properties, String name) {
        return getPropertyInteger(properties, name, 0);
    }

    public static final long getPropertyLong(Map<String, ?> properties, String name, long def) {
        return getProperty(Long.class, properties, name, def);
    }

    public static final long getPropertyLong(Map<String, ?> properties, String name) {
        return getPropertyLong(properties, name, 0l);
    }

    public static final boolean getPropertyBoolean(Map<String, ?> properties, String name, boolean def) {
        return getProperty(Boolean.class, properties, name, def);
    }

    public static final boolean getPropertyBoolean(Map<String, ?> properties, String name) {
        return getPropertyBoolean(properties, name, false);
    }

    public static final byte[] getPropertyBytes(Map<String, ?> properties, String name, byte[] def) {
        if (properties == null)
            return def;
        if (name == null)
            return def;
        Object o = properties.get(name);
        if (o instanceof byte[])
            return (byte[]) o;
        return def;
    }

    public static final byte[] getPropertyBytes(Map<String, ?> properties, String name) {
        return getPropertyBytes(properties, name, null);
    }
}
