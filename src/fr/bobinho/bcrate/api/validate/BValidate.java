package fr.bobinho.bcrate.api.validate;

import java.util.Objects;

/**
 * Bobinho validate library
 */
public final class BValidate {

    /**
     * Unitilizable constructor (utility class)
     */
    private BValidate() {}

    /**
     * Validates the not null object status
     */
    public static void notNull(Object object) {
        if (Objects.isNull(object)) {
            throw new NullPointerException("This object cannot be null!");
        }
    }

    /**
     * Validates the truth expression status
     */
    public static void isTrue(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException("This expression cannot be false!");
        }
    }

    /**
     * Validates the falsehood expression status
     */
    public static void isFalse(boolean expression) {
        if (expression) {
            throw new IllegalArgumentException("This expression cannot be true!");
        }
    }

}
