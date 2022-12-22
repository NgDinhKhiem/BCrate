package fr.bobinho.bcrate.util.crate;

import fr.bobinho.bcrate.BCrateCore;
import fr.bobinho.bcrate.api.entity.BEntity;
import fr.bobinho.bcrate.api.entity.base.BArmorStandEntity;
import fr.bobinho.bcrate.api.entity.type.BArmoredEntity;
import fr.bobinho.bcrate.api.location.BLocation;
import fr.bobinho.bcrate.api.setting.BSetting;
import fr.bobinho.bcrate.api.stream.IndexedStream;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.crate.edit.color.Color;
import fr.bobinho.bcrate.util.crate.edit.size.Size;
import fr.bobinho.bcrate.util.crate.listener.CrateListener;
import fr.bobinho.bcrate.util.crate.type.CrateEW;
import fr.bobinho.bcrate.util.crate.type.CrateNS;
import fr.bobinho.bcrate.util.key.Key;
import fr.bobinho.bcrate.util.key.KeyManager;
import fr.bobinho.bcrate.util.prize.Prize;
import fr.bobinho.bcrate.util.prize.PrizeManager;
import fr.bobinho.bcrate.util.tag.Tag;
import fr.bobinho.bcrate.util.tag.TagManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.awt.geom.IllegalPathStateException;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Manager class for crates
 * TODO use packet instead of real armor stand (lib already done), need interaction
 */
public class CrateManager {

    /**
     * Fields
     */
    private static final HashMap<String, Crate> crates = new HashMap<>();
    private static final BSetting configuration = BCrateCore.getCrateSetting();

    /**
     * Registers the crate manager
     */
    public static void register() {
        load();
        CrateListener.registerEvents();
    }

    /**
     * Unregisters the crate manager
     */
    public static void unregister() {
        save();
    }

    /**
     * Gets a stream of all keys
     *
     * @return a stream of all keys
     */
    public static @Nonnull Stream<Crate> stream() {
        return crates.values().stream();
    }

    /**
     * Gets an IndexedStream of all keys
     *
     * @return an IndexedStream of all keys
     */
    public static @Nonnull IndexedStream<Crate> indexedStream() {
        return IndexedStream.from(crates.values());
    }

    /**
     * Gets an optional crate
     *
     * @param name the name
     * @return an optional crate
     */
    public static @Nonnull Optional<Crate> get(@Nonnull String name) {
        return Optional.ofNullable(crates.get(name));
    }

    /**
     * Checks if the crate is registered
     *
     * @param name the name
     * @return true if the crate is registered, false otherwise
     */
    public static boolean isRegistered(@Nonnull String name) {
        BValidate.notNull(name);

        return get(name).isPresent();
    }

    /**
     * Creates a new crate
     *
     * @param name     the name
     * @param size     the size
     * @param location the location
     * @param color    the color
     * @param key      the key
     */
    public static void create(@Nonnull Player player, @Nonnull String name, @Nonnull Size size, @Nonnull Location location, @Nonnull Color color, @Nonnull Key key, @Nonnull List<ItemStack> skin) {
        BValidate.notNull(player);
        BValidate.notNull(name);
        BValidate.notNull(size);
        BValidate.notNull(location);
        BValidate.notNull(color);
        BValidate.notNull(key);
        BValidate.notNull(skin);

        crates.put(name, player.getFacing() == BlockFace.NORTH || player.getFacing() == BlockFace.SOUTH ?
                new CrateNS(name, size, location, color, key, skin, createStructure(location, true))
                :
                new CrateEW(name, size, location, color, key, skin, createStructure(location, false)));
    }

    /**
     * Deletes a crate
     *
     * @param name the name
     */
    public static void delete(@Nonnull String name) {
        BValidate.notNull(name);

        get(name).ifPresent(crate -> {
            crate.structure().stream().forEach(BEntity::remove);
            crates.remove(crate.name().get());
            crate.animation().stop();
        });
    }

    /**
     * Checks if the crate is empty
     *
     * @param name the name
     * @return true if the crate is empty, false otherwise
     */
    public static boolean isEmpty(@Nonnull String name) {
        BValidate.notNull(name);

        return get(name).map(crate -> crate.prizes().size() == 0).orElse(true);
    }

    /**
     * Resize a crate
     *
     * @param name the name
     * @param size the size
     */
    public static void resize(@Nonnull String name, @Nonnull Size size) {
        BValidate.notNull(name);
        BValidate.notNull(size);

        get(name).ifPresent(crate -> {
            crate.size().set(size);
            crate.prizes().removeAll(crate.prizes().stream()
                    .filter(prize -> prize.slot().get() >= size.getDimension())
                    .toList());
            crate.prizes().resize(size.getDimension());
            crate.prizeMenu().get().resize(size.getDimension());
            crate.showMenu().get().resize(size.getDimension());
        });
    }

    /**
     * Changes the color of the crate
     *
     * @param name  the name
     * @param color the color
     */
    public static void changeColor(@Nonnull String name, @Nonnull Color color) {
        BValidate.notNull(name);
        BValidate.notNull(color);

        get(name).ifPresent(crate -> crate.color().set(color));
    }

    /**
     * Changes the key of the crate
     *
     * @param name the name
     * @param key  the key
     */
    public static void changeKey(@Nonnull String name, @Nonnull Key key) {
        BValidate.notNull(name);
        BValidate.notNull(key);

        get(name).ifPresent(crate -> crate.key().set(key));
    }

    /**
     * Changes the skin of the crate
     *
     * @param name the name
     * @param skin the skin
     * @param slot the slot
     */
    public static void changeSkin(@Nonnull String name, @Nonnull ItemStack skin, int slot) {
        BValidate.notNull(name);
        BValidate.notNull(skin);

        get(name).ifPresent(crate -> {
            crate.skin().set((slot - 10) / 2, skin);

            if (slot == 10) {
                crate.structure().get(0).setEquipment(BArmoredEntity.Equipment.HELMET, skin).render();
            }
        });
    }

    /**
     * Adds a prize to the crate
     *
     * @param item  the item
     * @param crate the crate
     * @param slot  the slot
     */
    public static void addPrize(@Nonnull ItemStack item, @Nonnull Crate crate, int slot) {
        BValidate.notNull(item);
        BValidate.notNull(crate);

        crate.prizes().add(new Prize(item, slot, item.getType() == Material.BARRIER ? 0 : 50));
    }

    /**
     * Removes a prize from the crate
     *
     * @param crate the crate
     * @param slot  the slot
     */
    public static void removePrize(@Nonnull Crate crate, int slot) {
        BValidate.notNull(crate);

        PrizeManager.get(crate, slot).ifPresent(prize -> crate.prizes().remove(prize));
    }

    /**
     * Creates the structure
     *
     * @param location the location
     * @param isNS     the direction
     * @return the structure
     */
    private static @Nonnull List<BArmorStandEntity> createStructure(@Nonnull Location location, boolean isNS) {
        BValidate.notNull(location);

        Point2D.Double[] move = {
                new Point2D.Double(0.0D, 0.0D),
                new Point2D.Double(0.06D, 0.081D),
                new Point2D.Double(0.13D, 0.15D),
                new Point2D.Double(0.22D, 0.21D),
                new Point2D.Double(0.31D, 0.26D),
                new Point2D.Double(0.41D, 0.282D),
                new Point2D.Double(0.512D, 0.288D),
                new Point2D.Double(0.615D, 0.278D),
                new Point2D.Double(0.715D, 0.249D),
                new Point2D.Double(0.805D, 0.201D),
                new Point2D.Double(0.89D, 0.14D),
                new Point2D.Double(0.96D, 0.064D),
                new Point2D.Double(1.015D, -0.02D),
                new Point2D.Double(1.06D, -0.12D)
        };

        return IntStream.range(0, 32).mapToObj(i -> {
            float angle = (float) ((i - 4) * 10 % 140);
            int moveIndex = (Math.max(0, i - 4) % 14);
            double moveX = move[moveIndex].getX();
            double moveY = move[moveIndex].getY();

            Location newLocation = isNS ?
                    location.clone().add(i < 4 ? 0 : (i < 18 ? -moveY : moveY), i < 4 ? 0 : moveX, 0)
                    :
                    location.clone().add(0, i < 4 ? 0 : moveX, i < 4 ? 0 : (i < 18 ? moveY : -moveY));

            if (i == 2 || i == 3) {
                newLocation.setYaw(0.0F);
                newLocation.setYaw(BLocation.degreeToYaw(isNS ? 90.0F : 180.0F));
            }

            return new BArmorStandEntity(newLocation)
                    .setRightArmPose(i == 2 || i == 3 ? -90 : 0, 0, 0)
                    .setLeftArmPose(i == 2 || i == 3 ? -90 : 0, 0, 0)
                    .setHeadPose(isNS ? 0 : (i < 4 ? 0 : (i < 18 ? -angle : angle)), isNS ? 90 : 0, isNS ? (i < 4 ? 0 : (i < 18 ? angle : -angle)) : 0);
        }).toList();
    }

    /**
     * Checks if the entity is from a structure
     *
     * @return true if the entity is from a structure, false otherwise
     */
    public static Optional<Crate> isFromStructure(int id) {

        return crates.values().stream()
                .filter(crate -> crate.structure().stream().anyMatch(armorStand -> armorStand.getId() == id))
                .findFirst();
    }

    /**
     * Opens the crate edit menu
     *
     * @param player the player
     */
    public static void openEditMenu(@Nonnull Player player, @Nonnull String name) {
        BValidate.notNull(player);
        BValidate.notNull(name);

        get(name).ifPresent(crate -> crate.editMenu().get().openInventory(player));
    }

    /**
     * Opens the crate prize menu
     *
     * @param player the player
     */
    public static void openPrizeMenu(@Nonnull Player player, @Nonnull String name) {
        BValidate.notNull(player);
        BValidate.notNull(name);

        get(name).ifPresent(crate -> crate.prizeMenu().get().openInventory(player));
    }

    /**
     * Opens the crate show menu
     *
     * @param player the player
     */
    public static void openShowMenu(@Nonnull Player player, @Nonnull String name) {
        BValidate.notNull(player);
        BValidate.notNull(name);

        get(name).ifPresent(crate -> crate.showMenu().get().openInventory(player));
    }

    /**
     * Opens the crate structure menu
     *
     * @param player the player
     */
    public static void openStructureMenu(@Nonnull Player player, @Nonnull String name) {
        BValidate.notNull(player);
        BValidate.notNull(name);

        get(name).ifPresent(crate -> crate.structureMenu().get().openInventory(player));
    }

    /**
     * Checks if the crate can be played
     *
     * @param name the name
     * @return true if the crate can be played, false otherwise
     */
    public static boolean canPlay(@Nonnull String name) {
        BValidate.notNull(name);

        return get(name).map(crate -> !crate.metadata().has("open") && !crate.metadata().has("waitOpen")).orElse(false);
    }

    /**
     * Plays with the crate
     *
     * @param name the name
     * @return the rewards
     */
    public static @Nonnull List<Prize> play(@Nonnull String name) {
        BValidate.notNull(name);

        Random random = new Random();

        return get(name).map(crate -> IntStream.range(0, 2).mapToObj(i -> {
            double picked = random.nextDouble() * (1.0D + crate.prizes().stream()
                    .mapToDouble(prize -> prize.chance().get())
                    .sum());

            return crate.prizes().stream().filter(new Predicate<>() {
                double sum = 0;

                @Override
                public boolean test(Prize prize) {
                    sum = sum + prize.chance().get();
                    return sum >= picked;
                }

            }).findFirst().orElse(crate.prizes().get(random.nextInt(crate.prizes().size())));

        }).toList()).orElse(Collections.emptyList());
    }

    /**
     * Reloads all crates
     */
    public static void reload() {
        crates.clear();
        configuration.initialize();

        load();
    }

    /**
     * Loads all crates
     */
    public static void load() {
        crates.clear();

        //Loads all crates
        configuration.getKeys().forEach(crate -> {
            Size size = Size.valueOf(configuration.getString(crate + ".size"));
            Location location = BLocation.getAsLocation(configuration.getString(crate + ".location"));
            Color color = Color.valueOf(configuration.getString(crate + ".color"));
            Key key = KeyManager.get(configuration.getString(crate + ".key")).orElseThrow(IllegalPathStateException::new);
            List<ItemStack> skin = configuration.getItemStackList(crate + ".skin");
            String direction = configuration.getString(crate + ".direction");
            List<Prize> prizes = configuration.getConfigurationSection(crate + ".prizes").stream().map(slot -> {
                ItemStack item = configuration.getItemStack(crate + ".prizes." + slot + ".item");
                double chance = configuration.getDouble(crate + ".prizes." + slot + ".chance");
                boolean rare = configuration.getBoolean(crate + ".prizes." + slot + ".rarity");
                List<Tag> tags = configuration.getStringList(crate + ".prizes." + slot + ".tags").stream().map(tag ->
                        TagManager.get(tag).orElseThrow(IllegalPathStateException::new)).collect(Collectors.toList());

                return new Prize(item, Integer.parseInt(slot), chance, rare, tags);
            }).collect(Collectors.toList());

            crates.put(crate, direction.equals("NS") ?
                    new CrateNS(crate, size, prizes, location, color, key, skin, createStructure(location, true))
                    :
                    new CrateEW(crate, size, prizes, location, color, key, skin, createStructure(location, false)));
        });
    }

    /**
     * Saves all crates
     */
    public static void save() {
        configuration.clear();

        //Saves all crates
        crates.values().forEach(crate -> {
            crate.structure().stream().forEach(BArmorStandEntity::remove);
            configuration.set(crate.name().get() + ".size", crate.size().get().name());
            configuration.set(crate.name().get() + ".location", BLocation.getAsString(crate.location().get()));
            configuration.set(crate.name().get() + ".color", crate.color().get().name());
            configuration.set(crate.name().get() + ".key", crate.key().get().name().get());
            configuration.set(crate.name().get() + ".skin", crate.skin().get());
            configuration.set(crate.name().get() + ".direction", crate.direction().get());

            crate.prizes().get().forEach(prize -> {
                configuration.set(crate.name().get() + ".prizes." + prize.slot().get() + ".item", prize.item().get());
                configuration.set(crate.name().get() + ".prizes." + prize.slot().get() + ".chance", prize.chance().get());
                configuration.set(crate.name().get() + ".prizes." + prize.slot().get() + ".rarity", prize.rarity().get());
                configuration.set(crate.name().get() + ".prizes." + prize.slot().get() + ".tags", prize.tags().get().stream().map(tag -> tag.name().get()).toList());
            });
        });

        configuration.save();
    }

}