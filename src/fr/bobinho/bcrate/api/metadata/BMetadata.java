package fr.bobinho.bcrate.api.metadata;

import fr.bobinho.bcrate.api.scheduler.BScheduler;
import fr.bobinho.bcrate.api.validate.BValidate;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Bobinho metadata library
 */
public final class BMetadata {

    /**
     * Fields
     */
    private final ConcurrentHashMap<Object, Object> metadata = new ConcurrentHashMap<>();

    /**
     * Checks if the metadata is registered or not
     *
     * @param key the key
     * @return true if the metadata is registered or not, false otherwise
     */
    public boolean has(@Nonnull Object key) {
        BValidate.notNull(key);

        return metadata.get(key) != null;
    }

    /**
     * Gets all keys
     *
     * @return all keys
     */
    public @Nonnull Collection<Object> keys() {
        return metadata.keySet();
    }

    /**
     * Gets all values
     *
     * @return all values
     */
    public @Nonnull Collection<Object> values() {
        return metadata.values();
    }

    /**
     * Gets the metadata
     *
     * @param <T> the value type
     * @param key the key
     * @return the value
     */
    public @Nonnull <T> Optional<T> get(@Nonnull Object key) {
        BValidate.notNull(key);

        return (Optional<T>) get(key, Object.class);
    }

    /**
     * Gets the metadata
     *
     * @param <T>        the value type
     * @param key        the key
     * @param valueClass the value class
     * @return the value
     */
    public @Nonnull <T> Optional<T> get(@Nonnull Object key, @Nonnull Class<T> valueClass) {
        BValidate.notNull(key);
        BValidate.notNull(valueClass);

        return Optional.ofNullable((T) metadata.get(key));
    }

    /**
     * Gets the metadata
     *
     * @param <T> the value type
     * @param key the key
     * @return the value
     */
    public @Nonnull <T> T getNonNull(@Nonnull Object key) {
        BValidate.notNull(key);

        return (T) getNonNull(key, Object.class);
    }

    /**
     * Gets the metadata
     *
     * @param <T>        the value type
     * @param key        the key
     * @param valueClass the value class
     * @return the value
     */
    public @Nonnull <T> T getNonNull(@Nonnull Object key, @Nonnull Class<T> valueClass) {
        BValidate.notNull(key);
        BValidate.notNull(valueClass);

        return (T) metadata.get(key);
    }

    /**
     * Gets the metadata
     *
     * @param <T>          the value type
     * @param key          the key
     * @param defaultValue the default value
     * @return the value
     */
    public @Nonnull <T> T get(@Nonnull Object key, @Nonnull T defaultValue) {
        BValidate.notNull(key);
        BValidate.notNull(defaultValue);

        return (T) metadata.getOrDefault(key, defaultValue);
    }

    /**
     * Sets the metadata
     *
     * @param key   the key
     * @param value the value
     * @return the metadata
     */
    public @Nonnull BMetadata set(@Nonnull Object key, @Nonnull Object value) {
        BValidate.notNull(key);
        BValidate.notNull(value);

        //Checks expire
        checkExpire(key);

        //Sets metadata
        metadata.put(key, value);

        return this;
    }

    /**
     * Sets the metadata
     *
     * @param key      the key
     * @param value    the value
     * @param unit     the time unit
     * @param duration the duration
     * @return the metadata
     */
    public @Nonnull BMetadata set(@Nonnull Object key, @Nonnull Object value, @Nonnull TimeUnit unit, int duration) {
        BValidate.notNull(key);
        BValidate.notNull(value);
        BValidate.notNull(unit);

        //Checks expire
        checkExpire(key);

        //Sets the metadata
        metadata.put(key, value);
        metadata.put(key + ":expire", BScheduler.syncScheduler().after(duration, unit).run(() -> {
            metadata.remove(key);
            metadata.remove(key + ":expire");
        }));

        return this;
    }

    /**
     * Sets the metadata
     *
     * @param key           the key
     * @param value         the value
     * @param unit          the time unit
     * @param duration      the duration
     * @param expireHandler the expiration handler
     * @return the metadata
     */
    public @Nonnull BMetadata set(@Nonnull Object key, @Nonnull Object value, @Nonnull TimeUnit unit, int duration, @Nonnull Consumer<BMetadata> expireHandler) {
        BValidate.notNull(key);
        BValidate.notNull(value);
        BValidate.notNull(unit);
        BValidate.notNull(expireHandler);

        //Checks expire
        checkExpire(key);

        //Sets the metadata
        metadata.put(key, value);
        metadata.put(key + ":expire", BScheduler.syncScheduler().after(duration, unit).run(() -> {
            metadata.remove(key);
            metadata.remove(key + ":expire");
            expireHandler.accept(this);
        }));

        return this;
    }

    /**
     * Adds the metadata key
     *
     * @param key the key
     * @return the metadata
     */
    public @Nonnull BMetadata add(@Nonnull Object key) {
        BValidate.notNull(key);

        //Checks expire
        checkExpire(key);

        //Adds metadata
        metadata.put(key, 0);

        return this;
    }

    /**
     * Adds the metadata
     *
     * @param key      the key
     * @param unit     the time unit
     * @param duration the duration
     * @return the metadata
     */
    public @Nonnull BMetadata add(@Nonnull Object key, @Nonnull TimeUnit unit, int duration) {
        BValidate.notNull(key);
        BValidate.notNull(unit);

        return set(key, 0, unit, duration);
    }

    /**
     * Adds the metadata
     *
     * @param key           the key
     * @param unit          the time unit
     * @param duration      the duration
     * @param expireHandler the expire handler
     * @return the metadata
     */
    public @Nonnull BMetadata add(@Nonnull Object key, @Nonnull TimeUnit unit, int duration, @Nonnull Consumer<BMetadata> expireHandler) {
        BValidate.notNull(key);
        BValidate.notNull(unit);
        BValidate.notNull(expireHandler);

        return set(key, 0, unit, duration, expireHandler);
    }

    /**
     * Removes metadata.
     *
     * @param key Key.
     * @return Appa metadata.
     */
    public @Nonnull BMetadata remove(@Nonnull Object key) {
        BValidate.notNull(key);

        //Checks expire
        if (!checkExpire(key)) {
            metadata.remove(key);
        }

        return this;
    }


    /*
    MISC
     */

    /**
     * Checks expire
     *
     * @param key the key
     */
    private boolean checkExpire(@Nonnull Object key) {
        BValidate.notNull(key);

        //If the key expire is not set, no need to continue
        if (this.get(key + ":expire").isEmpty())
            return false;

        //Gets bukkit task id
        int taskId = getNonNull(key + ":expire");

        //Removes metadata
        metadata.remove(key);
        metadata.remove(key + ":expire");

        //Cancels scheduler
        Bukkit.getScheduler().cancelTask(taskId);

        return true;
    }

    /**
     * Resets the metadata
     */
    public void reset() {

        //Expires metadata keys
        metadata.keySet().stream()
                .filter(key -> key.getClass() == String.class && ((String) key).endsWith(":expire"))
                .forEach(key -> Bukkit.getScheduler().cancelTask(this.getNonNull(key)));

        //Clears metadata
        metadata.clear();
    }

}