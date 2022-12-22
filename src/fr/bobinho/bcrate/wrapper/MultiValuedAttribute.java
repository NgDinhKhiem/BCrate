package fr.bobinho.bcrate.wrapper;

import fr.bobinho.bcrate.api.validate.BValidate;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Wrapper of multi-valued attribute
 */
public class MultiValuedAttribute<T> {

    /**
     * Fields
     */
    private final List<T> values;

    /**
     * Creates a new multi-valued attribute
     *
     * @param values the initial values
     */
    public MultiValuedAttribute(@Nonnull List<T> values) {
        this.values = new ArrayList<>(values);
    }

    /**
     * Creates a new empty multi-valued attribute
     */
    public MultiValuedAttribute() {
        this(new ArrayList<>());
    }

    /**
     * Gets a copy of the multi-valued attribute
     *
     * @return the copy of the multi-valued attribute
     */
    public @Nonnull List<T> get() {
        return List.copyOf(values);
    }

    /**
     * Gets a stream of the multi-valued attribute
     *
     * @return the stream of the multi-valued attribute
     */
    public @Nonnull Stream<T> stream() {
        return values.stream();
    }

    /**
     * Gets the value at index i of the multi-valued attribute
     *
     * @param i the index of the value
     * @return the value at index i of the multi-valued attribute
     */
    public @Nonnull T get(int i) {
        return values.get(i);
    }

    /**
     * Adds a new value to the multi-valued attribute
     *
     * @param value the value
     */
    public void add(@Nonnull T value) {
        BValidate.notNull(value);

        values.add(value);
    }

    /**
     * Sets the index value of the multi-valued attribute to value
     *
     * @param index the index
     * @param value the value
     */
    public void set(int index, @Nonnull T value) {
        BValidate.notNull(value);

        values.set(index, value);
    }

    /**
     * Removes a value from the multi-valued attribute
     *
     * @param value the value
     */
    public void remove(@Nonnull T value) {
        BValidate.notNull(value);

        values.remove(value);
    }

    /**
     * Removes values from the multi-valued attribute
     *
     * @param values the values
     */
    public void removeAll(@Nonnull List<T> values) {
        BValidate.notNull(values);

        this.values.removeAll(values);
    }

    /**
     * Removes index-th value from the multi-valued attribute
     *
     * @param index the index
     */
    public void remove(int index) {
        values.remove(index);
    }

    /**
     * Clears the multi-valued attribute
     */
    public void clear() {
        values.clear();
    }

    /**
     * Gets the list size
     *
     * @return the list size
     */
    public int size() {
        return values.size();
    }

    /**
     * Checks if the multi-valued attribute contains an element
     *
     * @param element the element
     * @return true if the multi-valued attribute contains an element, false otherwise
     */
    public boolean contains(@Nonnull T element) {
        BValidate.notNull(element);

        return values.contains(element);
    }

}