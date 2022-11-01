package fr.bobinho.bcrate.wrapper;

import fr.bobinho.bcrate.api.validate.BValidate;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Wrapper of optional mono-valued attribute
 */
public class OptionalMonoValuedAttribute<T> {

    /**
     * Fields
     */
    private Optional<T> value;

    /**
     * Creates a new optional mono-valued attribute
     *
     * @param value the initial value
     */
    public OptionalMonoValuedAttribute(@Nullable T value) {
        this.value = Optional.ofNullable(value);
    }

    /**
     * Creates a new empty optional mono-valued attribute
     */
    public OptionalMonoValuedAttribute() {
        this(null);
    }

    /**
     * Gets the optional mono-valued attribute
     *
     * @return the optional mono-valued attribute
     */
    public @Nonnull Optional<T> get() {
        return value;
    }

    /**
     * Sets the value of the optional mono-valued attribute
     *
     * @param value the value of the optional mono-valued attribute
     */
    public void set(@Nullable T value) {
        this.value = Optional.ofNullable(value);
    }

    /**
     * Unsets the value of the optional mono-valued attribute
     */
    public void unset() {
        this.value = Optional.empty();
    }

    /**
     * Checks if a value is present in the optional mono-valued attribute
     *
     * @return true if a value is present in the optional mono-valued attribute, false otherwise
     */
    public boolean isPresent() {
        return value.isPresent();
    }

    /**
     * Performs the given action if a value is present in the optional mono-valued attribute
     *
     * @param action the action
     */
    public void ifPresent(@Nonnull Consumer<? super T> action) {
        BValidate.notNull(action);

        value.ifPresent(action);
    }

    /**
     * Performs the given action if a value is present in the optional mono-valued attribute, otherwise performs the empty action
     *
     * @param action      the action
     * @param emptyAction the empty action
     */
    public void ifPresentOrElse(@Nonnull Consumer<? super T> action, @Nonnull Runnable emptyAction) {
        BValidate.notNull(action);
        BValidate.notNull(emptyAction);

        value.ifPresentOrElse(action, emptyAction);
    }

}
