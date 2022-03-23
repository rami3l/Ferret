package fr.ferret;

import sun.misc.Unsafe;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;

import static org.awaitility.Awaitility.await;


public class TestUtils {

    private static Unsafe unsafe;
    private static String RESOURCE_FOLDER = "src/test/resources/";

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
        field.setAccessible(true);

        Object fieldBase = unsafe.staticFieldBase(field);
        long fieldOffset = unsafe.staticFieldOffset(field);

        await().until(() -> {
            unsafe.putObject(fieldBase, fieldOffset, value);
            return value.equals(field.get(null));
        });

    }

    public static String toURI(String path) {
        return new File(RESOURCE_FOLDER).toURI() + path;
    }

    public static InputStream getContent(String file) {
        return TestUtils.class.getClassLoader().getResourceAsStream(file);
    }

}
