package fr.bobinho.bcrate.wrapper;

import fr.bobinho.bcrate.api.validate.BValidate;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Wrapper of multi-valued attribute relation
 */
public class MultiValuedAttributeRelation<K, V> {

    /**
     * Fields
     */
    private final Map<K, V> map;

    /**
     * Creates a new multi-valued attribute relation
     */
    public MultiValuedAttributeRelation(@Nonnull Map<K, V> map) {
        this.map = map;
    }

    /**
     * Creates a new empty multi-valued attribute relation
     */
    public MultiValuedAttributeRelation() {
        map = new HashMap<>();
    }

    /**
     * Creates a new not empty multi-valued attribute relation
     *
     * @param key   the initial wrapper key
     * @param value the initial wrapper value
     */
    public MultiValuedAttributeRelation(@Nonnull K key, @Nonnull V value) {
        BValidate.notNull(key);
        BValidate.notNull(value);

        map = Map.of(key, value);
    }

    /**
     * Puts a new relation to the multi-valued attribute relation
     *
     * @param key   the key
     * @param value the value
     */
    public void put(@Nonnull K key, @Nonnull V value) {
        BValidate.notNull(key);
        BValidate.notNull(value);

        map.put(key, value);
    }

    /**
     * Removes a relation from the multi-valued attribute relation
     *
     * @param key the key
     */
    public void remove(@Nonnull K key) {
        BValidate.notNull(key);

        map.remove(key);
    }

    /**
     * Gets a relation from the multi-valued attribute relation
     *
     * @param key the key of the relation
     */
    public @Nonnull Optional<V> get(@Nonnull K key) {
        BValidate.notNull(key);

        return Optional.ofNullable(map.get(key));
    }

    /**
     * Gets a set containing all relations from the multi-valued attribute relation
     *
     * @return the set containing all relations from the multi-valued attribute relation
     */
    public @Nonnull Set<K> keySet() {
        return map.keySet();
    }

    /**
     * Gets a stream of all relations from the multi-valued attribute relation
     *
     * @return the stream of all relations from the multi-valued attribute relation
     */
    public @Nonnull Stream<Map.Entry<K, V>> stream() {
        return map.entrySet().stream();
    }

}
