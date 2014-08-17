package net.digitaltsunami.tmeter.test;

public class EnumUtils {
    /**
     * Utility to complete code coverage of bytecode generated methods for enum
     * classes.
     * <p>
     * Code below found on stackoverflow at
     * http://stackoverflow.com/questions/4512358/emma-coverage-on-enum-types
     * <p>
     * Note: This code only exercises these methods and performs no validation.
     * 
     * @param enumClass
     *            class to exercise.
     */
    public static void superficialEnumCodeCoverage(Class<? extends Enum<?>> enumClass) {
        try {
            for (Object o : (Object[]) enumClass.getMethod("values").invoke(null)) {
                enumClass.getMethod("valueOf", String.class).invoke(null, o.toString());
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
