package fr.bobinho.bcrate.util.key;

import fr.bobinho.bcrate.BCrateCore;
import fr.bobinho.bcrate.api.color.BColor;
import fr.bobinho.bcrate.api.item.BItemBuilder;
import fr.bobinho.bcrate.api.setting.BSetting;
import fr.bobinho.bcrate.api.stream.IndexedStream;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.crate.CrateManager;
import fr.bobinho.bcrate.util.key.listener.KeyListener;
import fr.bobinho.bcrate.util.key.ux.KeyEditMenu;
import fr.bobinho.bcrate.util.key.ux.KeyShowMenu;
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
    private static final KeyEditMenu editMenu = new KeyEditMenu();
    private static final KeyShowMenu showMenu = new KeyShowMenu();


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

        keys.put(name, new Key(name, new BItemBuilder(item).name(BColor.color(name)).build(), keys.size()));
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
     * Checks if the key is used by a crate
     *
     * @param name the name
     * @return true if the key is used by a crate, false otherwise
     */
    public static boolean isUsed(@Nonnull String name) {
        BValidate.notNull(name);

        return get(name).map(key -> CrateManager.stream().anyMatch(crate -> crate.key().get().equals(key))).orElse(false);
    }

    /**
     * Checks if the keys is full
     *
     * @return true if the keys is full, false otherwise
     */
    public static boolean isFull() {

        return keys.size() >= 54;
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
     * Opens the key edit menu
     *
     * @param player the player
     */
    public static void openEditMenu(@Nonnull Player player) {
        BValidate.notNull(player);

        editMenu.openInventory(player);
    }

    /**
     * Opens the key show menu
     *
     * @param player the player
     */
    public static void openShowMenu(@Nonnull Player player) {
        BValidate.notNull(player);

        showMenu.openInventory(player);
    }

    /**
     * Reloads all keys
     */
    public static void reload() {
        keys.clear();
        configuration.initialize();

        load();
    }

    /**
     * Loads all keys
     */
    public static void load() {

        //Loads all keys
        configuration.getKeys().forEach(key -> {
            ItemStack item = configuration.getItemStack(key + ".item");
            int slot = configuration.getInt(key + ".slot");

            keys.put(key, new Key(key, new BItemBuilder(item).name(BColor.color(key)).build(), slot));
        });
    }

    /**
     * Saves all keys
     */
    public static void save() {
        configuration.clear();

        //Saves all keys
        keys.values().forEach(key -> {
            configuration.set(key.name().get() + ".item", key.item().get());
            configuration.set(key.name().get() + ".slot", key.slot().get());
        });

        configuration.save();
    }

}
