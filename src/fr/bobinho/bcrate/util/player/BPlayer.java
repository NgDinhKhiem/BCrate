package fr.bobinho.bcrate.util.player;

import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.key.Key;
import fr.bobinho.bcrate.wrapper.MultiValuedAttributeRelation;
import fr.bobinho.bcrate.wrapper.ReadOnlyMonoValuedAttribute;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class representing the player
 */
public class BPlayer {

    /**
     * Fields
     */
    private final ReadOnlyMonoValuedAttribute<UUID> uuid;
    private final MultiValuedAttributeRelation<Key, Integer> keys;

    /**
     * Creates a new player
     *
     * @param uuid the uuid
     * @param keys the keys
     */
    public BPlayer(@Nonnull UUID uuid, @Nonnull Map<Key, Integer> keys) {
        BValidate.notNull(uuid);
        BValidate.notNull(keys);

        this.uuid = new ReadOnlyMonoValuedAttribute<>(uuid);
        this.keys = new MultiValuedAttributeRelation<>(keys);
    }

    /**
     * Creates a new player
     *
     * @param uuid the uuid
     */
    public BPlayer(@Nonnull UUID uuid) {
        this(uuid, new HashMap<>());
    }

    /**
     * Gets the uuid wrapper
     *
     * @return the uuid wrapper
     */
    public @Nonnull ReadOnlyMonoValuedAttribute<UUID> uuid() {
        return uuid;
    }

    /**
     * Gets the keys wrapper
     *
     * @return the keys wrapper
     */
    public @Nonnull MultiValuedAttributeRelation<Key, Integer> keys() {
        return keys;
    }

}
