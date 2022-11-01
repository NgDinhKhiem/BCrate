package fr.bobinho.bcrate.util.key;

import fr.bobinho.bcrate.BCrateCore;
import fr.bobinho.bcrate.api.color.BColor;
import fr.bobinho.bcrate.api.item.BItemBuilder;
import fr.bobinho.bcrate.api.setting.BSetting;
import fr.bobinho.bcrate.api.stream.IndexedStream;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.key.listener.KeyListener;
import fr.bobinho.bcrate.util.key.ux.KeyMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Manager class for keys
 */
public class KeyManager {

    /**
     * Fields
     */
    private static final HashMap<String, Key> keys = new HashMap<>();
    private static final BSetting configuration = BCrateCore.getKeySetting();
    private static final KeyMenu menu = new KeyMenu();

    /**
     * Registers the key manager
     */
    public static void register() {
        load();
        KeyListener.registerEvents();
    }

    /**
     * Unregisters the key manager
     */
    public static void unregister() {
        save();
    }

    /**
     * Gets a stream of all keys
     *
     * @return a stream of all keys
     */
    public static @Nonnull Stream<Key> stream() {
        return keys.values().stream();
    }

    /**
     * Gets a stream of all keys
     *
     * @return a stream of all keys
     */
    public static @Nonnull IndexedStream<Key> indexedStream() {
        return IndexedStream.from(keys.values());
    }

    /**
     * Gets an optional key
     *
     * @param name the name
     * @return an optional key
     */
    public static @Nonnull Optional<Key> get(@Nonnull String name) {
        BValidate.notNull(name);

        return Optional.ofNullable(keys.get(name));
    }

    /**
     * Gets an optional key
     *
     * @param item the item
     * @return an optional key
     */
    public static @Nonnull Optional<Key> get(@Nonnull ItemStack item) {
        BValidate.notNull(item);

        return keys.entrySet().stream()
                .filter(key -> BColor.color(key.getKey()).equals(item.getItemMeta().getDisplayName()))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    /**
     * Gets the next key
     *
     * @param key the previous key
     * @return the next key
     */
    public static @Nonnull Key getNext(@Nonnull Key key) {
        BValidate.notNull(key);

        return keys.values().toArray(Key[]::new)[(stream().toList().indexOf(key) + 1) % keys.size()];
    }

    /**
     * Checks if the key is registered
     *
     * @param name the name
     * @return true if the key is registered, false otherwise
     */
    public static boolean isRegistered(@Nonnull String name) {
        BValidate.notNull(name);

        return get(name).isPresent();
    }

    /**
     * Creates a new key
     *
     * @param name the name
     * @param item the item
     */
    public static void create(@Nonnull String name, @Nonnull ItemStack item) {
        BValidate.notNull(name);
        BValidate.notNull(item);

        keys.put(name, new Key(name, new BItemBuilder(item).name(BColor.color(name)).build()));
    }

    /**
     * Deletes a key
     *
     * @param name the name
     */
    public static void delete(@Nonnull String name) {
        BValidate.notNull(name);

        get(name).ifPresent(key -> keys.remove(key.name().get()));
    }

    /**
     * Gives the key to the player
     *
     * @param player the player
     * @param name   the name
     */
    public static void give(@Nonnull Player player, @Nonnull String name, int amount) {
        BValidate.notNull(player);
        BValidate.notNull(name);

        get(name).ifPresent(key -> player.getInventory().addItem(new BItemBuilder(key.item().get()).amount(amount).build()));
    }

    /**
     * Opens the key menu
     *
     * @param player the player
     */
    public static void openMenu(@Nonnull Player player) {
        BValidate.notNull(player);

        menu.openInventory(player);
    }

    /**
     * Loads all keys
     */
    public static void load() {

        //Loads all keys
        configuration.getKeys().forEach(key -> {
            ItemStack item = configuration.getItemStack(key + ".item");

            create(key, item);
        });
    }

    /**
     * Saves all keys
     */
    public static void save() {
        configuration.clear();

        //Saves all keys
        keys.values().forEach(key -> configuration.set(key.name().get() + ".item", key.item().get()));

        configuration.save();
    }

}
