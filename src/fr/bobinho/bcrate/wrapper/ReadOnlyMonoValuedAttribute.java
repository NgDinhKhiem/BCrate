package fr.bobinho.bcrate.wrapper;

import fr.bobinho.bcrate.api.validate.BValidate;

import javax.annotation.Nonnull;

/**
 * Wrapper of read only mono-valued attribute
 */
public class ReadOnlyMonoValuedAttribute<T> {

    /**
     * Fields
     */
    protected T value;

    /**
     * Creates a new not empty read only mono-valued attribute
     *
     * @param value the initial wrapper value
     */
    public ReadOnlyMonoValuedAttribute(@Nonnull T value) {
        BValidate.notNull(value);

        this.value = value;
    }

    /**
     * Gets the wrapper value
     *
     * @return the wrapper value
     */
    public @Nonnull T get() {
        if (value instanceof Cloneable) {
            try {
                return (T) value.getClass().getMethod("clone").invoke(value, new Object[0]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return value;
    }

}
