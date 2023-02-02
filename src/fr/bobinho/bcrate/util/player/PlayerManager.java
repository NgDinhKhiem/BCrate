package fr.bobinho.bcrate.util.player;

import fr.bobinho.bcrate.BCrateCore;
import fr.bobinho.bcrate.api.item.BItemBuilder;
import fr.bobinho.bcrate.api.setting.BSetting;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.crate.Crate;
import fr.bobinho.bcrate.util.key.Key;
import fr.bobinho.bcrate.util.key.KeyManager;
import fr.bobinho.bcrate.util.player.listener.PlayerListener;
import fr.bobinho.bcrate.util.prize.Prize;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Manager class for players
 */
public class PlayerManager {

    /**
     * Fields
     */
    private static final HashMap<UUID, BPlayer> players = new HashMap<>();
    private static final BSetting configuration = BCrateCore.getPlayerSetting();

    /**
     * Registers the player manager
     */
    public static void register() {
        load();
        PlayerListener.registerEvents();
    }

    /**
     * Unregisters the player manager
     */
    public static void unregister() {
        save();
    }

    /**
     * Gets a stream of all players
     *
     * @return a stream of all players
     */
    public static @Nonnull Stream<BPlayer> stream() {
        return players.values().stream();
    }

    /**
     * Gets an optional registered player
     *
     * @param uuid the uuid
     * @return an optional registered player
     */
    public static @Nonnull Optional<BPlayer> get(@Nonnull UUID uuid) {
        BValidate.notNull(uuid);

        return Optional.ofNullable(players.get(uuid));
    }

    /**
     * Checks if the player is registered
     *
     * @param uuid the uuid
     * @return true if the player is registered, false otherwise
     */
    public static boolean isRegistered(@Nonnull UUID uuid) {
        BValidate.notNull(uuid);

        return get(uuid).isPresent();
    }

    /**
     * Creates a new player
     *
     * @param uuid the uuid
     */
    public static void create(@Nonnull UUID uuid) {
        BValidate.notNull(uuid);

        players.put(uuid, new BPlayer(uuid));
        KeyManager.stream().forEach(key -> configuration.isInt(uuid + "." + key.name().get()).ifPresent(amount -> addKey(uuid, key, amount)));
        save();
    }

    /**
     * Deletes a player
     *
     * @param uuid the uuid
     */
    public static void delete(@Nonnull UUID uuid) {
        BValidate.notNull(uuid);

        get(uuid).ifPresent(bPlayer -> players.remove(bPlayer.uuid().get()));
    }

    /**
     * Gets the number of key withdrawable by the player
     *
     * @param uuid the uuid
     * @param key  the key
     * @return the number of key withdrawable by the player
     */
    public static int getKeyNumberWithdrawable(@Nonnull UUID uuid, @Nonnull Key key) {
        BValidate.notNull(uuid);
        BValidate.notNull(key);

        return get(uuid).map(bPlayer -> bPlayer.keys().get(key).orElse(0)).orElse(0);
    }

    /**
     * Gets the number of key depositable by the player
     *
     * @param uuid the uuid
     * @param key  the key
     * @return the number of key depositable by the player
     */
    public static int getKeyNumberDepositable(@Nonnull UUID uuid, @Nonnull Key key) {
        BValidate.notNull(uuid);
        BValidate.notNull(key);

        //Checks if the player have enough key on his inventory
        return Optional.ofNullable(Bukkit.getPlayer(uuid)).map(player ->
                        Arrays.stream(player.getInventory().getContents())
                                .filter(item -> item != null && item.isSimilar(key.item().get()))
                                .mapToInt(ItemStack::getAmount)
                                .sum())
                .orElse(0);
    }

    /**
     * Clones the inventory
     *
     * @param inventory the inventory
     * @return the cloned inventory
     */
    private static Inventory cloneInventory(@Nonnull Inventory inventory) {
        BValidate.notNull(inventory);

        Inventory copy = Bukkit.createInventory(null, 36);
        for (int i = 0; i < 36; i++) {
            copy.setItem(i, inventory.getItem(i) != null ? inventory.getItem(i).clone() : null);
        }

        return copy;
    }

    /**
     * Checks if the player can withdraw key
     *
     * @param uuid   the uuid
     * @param key    the key
     * @param amount the amount
     * @return true if the player can withdraw key, false otherwise
     */
    public static boolean canWithdrawKey(@Nonnull UUID uuid, @Nonnull Key key, int amount) {
        BValidate.notNull(uuid);
        BValidate.notNull(key);

        //Checks if the player inventory with keys will be full
        return Optional.ofNullable(Bukkit.getPlayer(uuid))
                .map(player -> cloneInventory(player.getInventory()).addItem(new BItemBuilder(key.item().get()).amount(amount).build()).isEmpty())
                .orElse(false);
    }

    /**
     * Checks if the player can play
     *
     * @param uuid  the uuid
     * @param items the rewards
     * @return true if the player can play, false otherwise
     */
    public static boolean canPlay(@Nonnull UUID uuid, @Nonnull List<Prize> items) {
        BValidate.notNull(uuid);
        BValidate.notNull(items);

        //Checks if the player inventory with items will be full
        return Optional.ofNullable(Bukkit.getPlayer(uuid))
                .map(player -> cloneInventory(player.getInventory()).addItem(items.stream().map(item -> item.item().get()).toArray(ItemStack[]::new)).isEmpty())
                .orElse(false);
    }

    /**
     * Checks if the player is opening a crate
     *
     * @param uuid the uuid
     * @return true if the player is opening a crate, false otherwise
     */
    public static boolean isOpeningCrate(@Nonnull UUID uuid) {
        BValidate.notNull(uuid);

        return get(uuid).map(bPlayer -> bPlayer.isOpeningCrate().get()).orElse(false);
    }

    public static void openCrate(@Nonnull UUID uuid, boolean isOpening) {
        BValidate.notNull(uuid);

        get(uuid).ifPresent(bPlayer -> bPlayer.isOpeningCrate().set(isOpening));
    }

    /**
     * Checks if the player has the key
     *
     * @param uuid  the uuid
     * @param crate the crate
     * @return true if the player can play, false otherwise
     */
    public static boolean hasKey(@Nonnull UUID uuid, @Nonnull Crate crate) {
        BValidate.notNull(uuid);
        BValidate.notNull(crate);

        //Checks if the player inventory contains the key
        return Optional.ofNullable(Bukkit.getPlayer(uuid)).map(player ->
                (player.getInventory().getItemInMainHand().isSimilar(crate.key().get().item().get()) &&
                        player.getInventory().getItemInMainHand().getAmount() >= crate.key().get().item().get().getAmount()) ||
                        get(uuid).map(bPlayer -> bPlayer.keys().get(crate.key().get()).map(amount -> amount > 0).orElse(false)).orElse(false)
        ).orElse(false);
    }

    public static void removeKey(@Nonnull UUID uuid, @Nonnull Key key, int amount) {
        BValidate.notNull(uuid);
        BValidate.notNull(key);

        get(uuid).ifPresent(bPlayer -> bPlayer.keys().put(key, getKeyNumberWithdrawable(uuid, key) - amount));
        save();
    }

    public static void addKey(@Nonnull UUID uuid, @Nonnull Key key, int amount) {
        BValidate.notNull(uuid);
        BValidate.notNull(key);

        get(uuid).ifPresent(bPlayer -> bPlayer.keys().put(key, getKeyNumberWithdrawable(uuid, key) + amount));
        save();
    }

    public static void withdrawKey(@Nonnull UUID uuid, @Nonnull Key key, int amount) {
        BValidate.notNull(uuid);
        BValidate.notNull(key);

        //Withdraws key
        Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> IntStream.range(0, amount).forEach(i -> player.getInventory().addItem(key.item().get().clone())));
        removeKey(uuid, key, amount);
        save();
    }

    public static void depositKey(@Nonnull UUID uuid, @Nonnull Key key, int amount) {
        BValidate.notNull(uuid);
        BValidate.notNull(key);

        //Deposits key
        Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player ->
                player.getInventory().removeItem(new BItemBuilder(key.item().get()).amount(amount).build()));
        addKey(uuid, key, amount);
        save();
    }

    /**
     * Reloads all players
     */
    public static void reload() {
        players.clear();
        configuration.initialize();

        load();
    }

    /**
     * Loads all players
     */
    public static void load() {

        //Loads all players
        configuration.getKeys().forEach(player -> {
            Map<Key, Integer> keys = configuration.getConfigurationSection(player).stream()
                    .map(key -> KeyManager.get(key).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(key -> key, key -> configuration.getInt(player + "." + key.name().get())));

            players.put(UUID.fromString(player), new BPlayer(UUID.fromString(player), keys));
        });
    }

    /**
     * Saves all players
     */
    public static void save() {
        configuration.clear();

        //Saves all players
        players.values().forEach(bPlayer -> KeyManager.stream().forEach(key ->
                configuration.set(bPlayer.uuid().get() + "." + key.name().get(), getKeyNumberWithdrawable(bPlayer.uuid().get(), key))
        ));

        configuration.save();
    }

}
