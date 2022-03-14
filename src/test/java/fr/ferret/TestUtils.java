package fr.ferret;

import sun.misc.Unsafe;

import java.lang.reflect.Field;


public class TestUtils {

    private static Unsafe unsafe;

    static {
        try {
            final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static <T> void setFinalStatic(Class<T> c, String fieldName, Object value)
        throws Exception {

        var field = c.getDeclaredField(fieldName);

        Object fieldBase = unsafe.staticFieldBase(field);
        long fieldOffset = unsafe.staticFieldOffset(field);

        unsafe.putObject(fieldBase, fieldOffset, value);

    }

}
