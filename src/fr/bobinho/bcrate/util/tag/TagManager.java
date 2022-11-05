package fr.bobinho.bcrate.util.tag;

import fr.bobinho.bcrate.BCrateCore;
import fr.bobinho.bcrate.api.color.BColor;
import fr.bobinho.bcrate.api.setting.BSetting;
import fr.bobinho.bcrate.api.stream.IndexedStream;
import fr.bobinho.bcrate.api.validate.BValidate;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Manager class for tags
 */
public class TagManager {

    /**
     * Fields
     */
    private static final HashMap<String, Tag> tags = new HashMap<>();
    private static final BSetting configuration = BCrateCore.getTagSetting();

    /**
     * Registers the tag manager
     */
    public static void register() {
        load();
    }

    /**
     * Unregisters the tag manager
     */
    public static void unregister() {
        save();
    }

    /**
     * Gets a stream of all tags
     *
     * @return a stream of all tags
     */
    public static @Nonnull Stream<Tag> stream() {
        return tags.values().stream();
    }

    /**
     * Gets a stream of all tags
     *
     * @return a stream of all tags
     */
    public static @Nonnull IndexedStream<Tag> indexedStream() {
        return IndexedStream.from(tags.values());
    }

    /**
     * Gets an optional tag
     *
     * @param name the name
     * @return an optional tag
     */
    public static @Nonnull Optional<Tag> get(@Nonnull String name) {
        BValidate.notNull(name);

        return Optional.ofNullable(tags.get(name));
    }

    /**
     * Gets an optional tag
     *
     * @param item the item
     * @return an optional tag
     */
    public static @Nonnull Optional<Tag> get(@Nonnull ItemStack item) {
        BValidate.notNull(item);

        return tags.entrySet().stream()
                .filter(tag -> BColor.color(tag.getKey()).equals(item.getItemMeta().getDisplayName()))
                .map(Map.Entry::getValue)
                .findFirst();
    }


    /**
     * Checks if the tag is registered
     *
     * @param name the name
     * @return true if the tag is registered, false otherwise
     */
    public static boolean isRegistered(@Nonnull String name) {
        BValidate.notNull(name);

        return get(name).isPresent();
    }

    /**
     * Creates a new tag
     *
     * @param name        the name
     * @param description the description
     */
    public static void create(@Nonnull String name, @Nonnull String description) {
        BValidate.notNull(name);
        BValidate.notNull(description);

        tags.put(name, new Tag(name, BColor.color(description)));
    }

    /**
     * Deletes a tag
     *
     * @param name the name
     */
    public static void delete(@Nonnull String name) {
        BValidate.notNull(name);

        get(name).ifPresent(tag -> tags.remove(tag.name().get()));
    }

    /**
     * Reloads all tags
     */
    public static void reload() {
        tags.clear();
        configuration.initialize();

        load();
    }

    /**
     * Loads all tags
     */
    public static void load() {

        //Loads all tags
        configuration.getKeys().forEach(tag -> {
            String description = configuration.getString(tag + ".description");

            create(tag, description);
        });
    }

    /**
     * Saves all tags
     */
    public static void save() {
        configuration.clear();

        //Saves all tags
        tags.values().forEach(tag -> configuration.set(tag.name().get() + ".description", tag.description().get()));

        configuration.save();
    }

}
