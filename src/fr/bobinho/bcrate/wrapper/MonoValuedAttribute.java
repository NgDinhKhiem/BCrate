package fr.bobinho.bcrate.wrapper;

import fr.bobinho.bcrate.api.validate.BValidate;

import javax.annotation.Nonnull;

/**
 * Wrapper of mono-valued attribute
 */
public class MonoValuedAttribute<T> extends ReadOnlyMonoValuedAttribute<T> {

    /**
     * Creates a new not empty mono-valued attribute
     *
     * @param value the initial wrapper value
     */
    public MonoValuedAttribute(@Nonnull T value) {
        super(value);
    }

    /**
     * Sets the new wrapper value
     *
     * @param value the new wrapper value
     */
    public void set(@Nonnull T value) {
        BValidate.notNull(value);

        this.value = value;
    }

}
