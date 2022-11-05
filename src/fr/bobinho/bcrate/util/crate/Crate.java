package fr.bobinho.bcrate.util.crate;

import fr.bobinho.bcrate.api.entity.base.BArmorStandEntity;
import fr.bobinho.bcrate.api.entity.type.BArmoredEntity;
import fr.bobinho.bcrate.api.item.BItemBuilder;
import fr.bobinho.bcrate.api.location.BLocation;
import fr.bobinho.bcrate.api.metadata.BMetadata;
import fr.bobinho.bcrate.api.notification.BPlaceHolder;
import fr.bobinho.bcrate.api.scheduler.BScheduler;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.crate.edit.color.Color;
import fr.bobinho.bcrate.util.crate.edit.size.Size;
import fr.bobinho.bcrate.util.crate.notification.CrateNotification;
import fr.bobinho.bcrate.util.crate.ux.CrateEditMenu;
import fr.bobinho.bcrate.util.crate.ux.CratePrizeMenu;
import fr.bobinho.bcrate.util.crate.ux.CrateShowMenu;
import fr.bobinho.bcrate.util.key.Key;
import fr.bobinho.bcrate.util.player.PlayerManager;
import fr.bobinho.bcrate.util.prize.Prize;
import fr.bobinho.bcrate.util.prize.notification.PrizeNotification;
import fr.bobinho.bcrate.wrapper.MonoValuedAttribute;
import fr.bobinho.bcrate.wrapper.MultiValuedAttribute;
import fr.bobinho.bcrate.wrapper.ReadOnlyMonoValuedAttribute;
import fr.bobinho.bcrate.wrapper.UpperBoundedMultiValuedAttribute;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class representing the crate
 */
public class Crate {

    /**
     * Fields
     */
    private final MonoValuedAttribute<String> name;
    private final MonoValuedAttribute<Size> size;
    private final UpperBoundedMultiValuedAttribute<Prize> prizes;
    private final ReadOnlyMonoValuedAttribute<Location> location;
    private final MonoValuedAttribute<Color> color;
    private final MonoValuedAttribute<Key> key;
    private final ReadOnlyMonoValuedAttribute<CrateEditMenu> editMenu;
    private final ReadOnlyMonoValuedAttribute<CratePrizeMenu> prizeMenu;
    private final ReadOnlyMonoValuedAttribute<CrateShowMenu> showMenu;
    private final MultiValuedAttribute<BArmorStandEntity> structure;
    private final BScheduler animation;
    private final BMetadata metadata;

    /**
     * Creates a new crate
     *
     * @param name     the name
     * @param size     the size
     * @param prizes   the prizes
     * @param location the location
     * @param color    the color
     */
    public Crate(@Nonnull String name, @Nonnull Size size, @Nonnull List<Prize> prizes, @Nonnull Location location, @Nonnull Color color, @Nonnull Key key, @Nonnull List<BArmorStandEntity> structure) {
        BValidate.notNull(name);
        BValidate.notNull(size);
        BValidate.notNull(prizes);
        BValidate.notNull(color);
        BValidate.notNull(key);
        BValidate.notNull(structure);

        this.name = new MonoValuedAttribute<>(name);
        this.size = new MonoValuedAttribute<>(size);
        this.prizes = new UpperBoundedMultiValuedAttribute<>(size.getDimension(), prizes);
        this.location = new ReadOnlyMonoValuedAttribute<>(location);
        this.color = new MonoValuedAttribute<>(color);
        this.key = new MonoValuedAttribute<>(key);
        this.structure = new MultiValuedAttribute<>(structure);
        this.editMenu = new ReadOnlyMonoValuedAttribute<>(new CrateEditMenu(this));
        this.prizeMenu = new ReadOnlyMonoValuedAttribute<>(new CratePrizeMenu(this));
        this.showMenu = new ReadOnlyMonoValuedAttribute<>(new CrateShowMenu(this));
        this.metadata = new BMetadata().add("spine").set("spine:degree", 0.0F);

        this.animation = BScheduler.syncScheduler().every(2);
        this.animation.run(() -> {

            if (metadata.has("restart")) {
                double degree = metadata.getNonNull("restart:degree");

                if (degree <= 1) {
                    List.of(2, 3).forEach(i -> {
                        Location newLocation = this.location.get().add(0.6, 0.5 + -Math.pow((degree - 0.42194) * 2.37, 2) + 1, i == 2 ? 0.4 - degree : -0.4 + degree);
                        newLocation.setPitch(0.0F);
                        newLocation.setYaw(BLocation.degreeToYaw(90.0F));

                        structure.get(i)
                                .teleport(newLocation)
                                .render();
                    });

                    metadata.set("restart:degree", degree + 0.05D);
                } else if (degree > 401) {
                    Player player = metadata.getNonNull("player");
                    List<Prize> items = metadata.getNonNull("prizes");

                    //Messages
                    player.sendMessage(CrateNotification.CRATE_WON.getNotification());
                    for (Prize prize : items) {
                        ItemStack item = prize.item().get();
                        player.sendMessage(CrateNotification.CRATE_PRIZE_INFO.getNotification(
                                new BPlaceHolder("%amount%", String.valueOf(item.getAmount())),
                                new BPlaceHolder("%name%", (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : item.getType().name().replace("_", " ")),
                                new BPlaceHolder("%rare%", prize.rarity().get() ? PrizeNotification.PRIZE_RARE.getNotification() : PrizeNotification.PRIZE_NOT_RARE.getNotification())));

                        if (prize.rarity().get()) {
                            Bukkit.getOnlinePlayers().forEach(receiver -> receiver.sendMessage(CrateNotification.CRATE_PRIZE_INFO_GLOBAL.getNotification(
                                    new BPlaceHolder("%name%", player.getName()),
                                    new BPlaceHolder("%amount%", String.valueOf(item.getAmount())),
                                    new BPlaceHolder("%item%", (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : item.getType().name().replace("_", " ")))));
                        }
                    }

                    //Gives prizes
                    player.getInventory().addItem(items.stream().map(prize -> prize.item().get()).toArray(ItemStack[]::new));
                    metadata.add("close").remove("restart").set("open:degree", 130);
                    List.of(2, 3).forEach(i -> {
                        Location newLocation = this.location.get().add(0.6, 0.5, i == 2 ? 0.4 : -0.4);
                        newLocation.setPitch(0.0F);
                        newLocation.setYaw(BLocation.degreeToYaw(90.0F));

                        structure.get(i).teleport(newLocation).clearEquipments().render();
                        this.location.get().getWorld().spawnParticle(
                                Particle.EXPLOSION_NORMAL,
                                this.location.get().add(0, 1.25, i == 2 ? -0.8 : 0.8),
                                1,
                                0.1,
                                0.1,
                                0.1);
                    });
                } else {
                    List.of(2, 3).forEach(i -> {
                        Location newLocation = this.location.get().add(0.6, 0.376906028 - Math.abs(((degree % 360) - 180) / 600) + 0.3, i == 2 ? -0.6 : 0.6);
                        newLocation.setPitch(0.0F);
                        newLocation.setYaw(BLocation.degreeToYaw(90.0F));

                        structure.get(i)
                                .teleport(newLocation)
                                .render();
                    });

                    metadata.set("restart:degree", degree + 10.0D);
                }
            }

            //Closes animation
            else if (metadata.has("close")) {
                int degree = metadata.getNonNull("open:degree");

                //Restarts and give prizes

                if (degree < 0) {
                    metadata.remove("player").remove("prizes").remove("open").remove("close").remove("open:degree").add("spine");

                    structure.get(0).setEquipment(BArmoredEntity.Equipment.HELMET, new BItemBuilder(Material.DIAMOND_SHOVEL).durability(26).build()).render();
                    List.of(1, 4, 18).forEach(i -> structure.get(i).clearEquipments().render());
                } else {
                    structure.get(Math.min(17, 5 + degree / 10)).clearEquipments().render();
                    structure.get(Math.min(31, 19 + degree / 10)).clearEquipments().render();

                    structure.get(4 + degree / 10).setEquipment(BArmoredEntity.Equipment.HELMET, new BItemBuilder(Material.DIAMOND_SHOVEL).durability(27).build()).render();
                    structure.get(18 + degree / 10).setEquipment(BArmoredEntity.Equipment.HELMET, new BItemBuilder(Material.DIAMOND_SHOVEL).durability(28).build()).render();
                }

                metadata.set("open:degree", degree - 10);
            }

            //Opens animation
            else if (metadata.has("open")) {
                int degree = metadata.getNonNull("open:degree");

                //Restarts and give prizes
                if (degree > 130) {
                    restart();
                } else {
                    Random r = new Random();
                    for (int j = 0; j < 8; j++) {
                        location.getWorld().spawnParticle(
                                Particle.REDSTONE,
                                location.clone().add(0.0D, 2.2D, 0.0D),
                                1,
                                0.1,
                                0.1,
                                0.1,
                                new Particle.DustOptions(org.bukkit.Color.fromBGR(r.nextInt(256), r.nextInt(256), r.nextInt(256)), 2));
                    }

                    structure.get(Math.max(4, 3 + degree / 10)).clearEquipments().render();
                    structure.get(Math.max(18, 17 + degree / 10)).clearEquipments().render();

                    structure.get(4 + degree / 10).setEquipment(BArmoredEntity.Equipment.HELMET, new BItemBuilder(Material.DIAMOND_SHOVEL).durability(27).build()).render();
                    structure.get(18 + degree / 10).setEquipment(BArmoredEntity.Equipment.HELMET, new BItemBuilder(Material.DIAMOND_SHOVEL).durability(28).build()).render();
                }

                metadata.set("open:degree", degree + 10);
            }

            //Spines animation
            else if (metadata.has("spine")) {
                float degree = metadata.getNonNull("spine:degree");

                if (metadata.has("waitOpen")) {

                    //Opens the crate
                    if (degree == 10) {
                        open();
                    }
                }

                if (!metadata.has("open")) {
                    //Gets target location
                    Location newLocation = this.location.get().add(0, -Math.abs((degree - 180) / 360) + 0.5, 0);
                    newLocation.setPitch(0.0F);
                    newLocation.setYaw(BLocation.degreeToYaw(degree));

                    structure.get(0).teleport(newLocation).render();

                    metadata.set("spine:degree", (degree + 10.0F) % 360);
                }
            }
        });
    }

    /**
     * Creates a new crate
     *
     * @param name the name
     * @param size the size
     */
    public Crate(@Nonnull String name, @Nonnull Size size, @Nonnull Location location, @Nonnull Color color, @Nonnull Key key, @Nonnull List<BArmorStandEntity> structure) {
        this(name, size, new ArrayList<>(), location, color, key, structure);
    }

    /**
     * Gets the name wrapper
     *
     * @return the name wrapper
     */
    public @Nonnull MonoValuedAttribute<String> name() {
        return name;
    }

    /**
     * Gets the size wrapper
     *
     * @return the size wrapper
     */
    public @Nonnull MonoValuedAttribute<Size> size() {
        return size;
    }

    /**
     * Gets the prizes wrapper
     *
     * @return the prizes wrapper
     */
    public @Nonnull UpperBoundedMultiValuedAttribute<Prize> prizes() {
        return prizes;
    }

    /**
     * Gets the linked block wrapper
     *
     * @return the linked block wrapper
     */
    public @Nonnull ReadOnlyMonoValuedAttribute<Location> location() {
        return location;
    }

    /**
     * Gets the color wrapper
     *
     * @return the color wrapper
     */
    public @Nonnull MonoValuedAttribute<Color> color() {
        return color;
    }

    /**
     * Gets the key wrapper
     *
     * @return the key wrapper
     */
    public @Nonnull MonoValuedAttribute<Key> key() {
        return key;
    }

    /**
     * Gets the edit menu wrapper
     *
     * @return the edit menu wrapper
     */
    public @Nonnull ReadOnlyMonoValuedAttribute<CrateEditMenu> editMenu() {
        return editMenu;
    }

    /**
     * Gets the prize menu wrapper
     *
     * @return the prize menu wrapper
     */
    public @Nonnull ReadOnlyMonoValuedAttribute<CratePrizeMenu> prizeMenu() {
        return prizeMenu;
    }

    /**
     * Gets the play menu wrapper
     *
     * @return the play menu wrapper
     */
    public @Nonnull ReadOnlyMonoValuedAttribute<CrateShowMenu> showMenu() {
        return showMenu;
    }

    /**
     * Gets the structure wrapper
     *
     * @return the structure wrapper
     */
    public @Nonnull MultiValuedAttribute<BArmorStandEntity> structure() {
        return structure;
    }

    /**
     * Gets the metadata wrapper
     *
     * @return the metadata wrapper
     */
    public BMetadata metadata() {
        return metadata;
    }

    /**
     * Gets the animation wrapper
     *
     * @return the animation wrapper
     */
    public BScheduler animation() {
        return animation;
    }

    /**
     * Launchs the animation to open the crate
     */
    private void open() {
        structure.get(0).clearEquipments().render();
        structure.get(1).setEquipment(BArmoredEntity.Equipment.HELMET, new BItemBuilder(Material.DIAMOND_SHOVEL).durability(29).build()).render();

        metadata.remove("spine").remove("waitOpen").add("open").set("open:degree", 0);
    }

    /**
     * Restarts the crate and give prizes to the last player
     */
    private void restart() {
        metadata.add("restart").set("restart:degree", 0.0D);
        List<Prize> items = metadata.getNonNull("prizes");

        List.of(2, 3).forEach(i -> {
            Location newLocation = location.get().add(0.6, 0.5, i == 2 ? 0.4 : -0.4);
            newLocation.setPitch(0.0F);
            newLocation.setYaw(BLocation.degreeToYaw(90.0F));

            structure.get(i)
                    .setRightArmPose(-90, 0, 0)
                    .setLeftArmPose(-90, 0, 0)
                    .teleport(newLocation)
                    .setEquipment(i == 2 ? BArmoredEntity.Equipment.MAIN_HAND : BArmoredEntity.Equipment.OFF_HAND, items.get(i - 2).item().get()).render();
        });
    }

    /**
     * Launchs the crate's animation and wait for the crate to return to its original position
     *
     * @param player the player
     * @param prizes the prizes
     */
    public void wait(@Nonnull Player player, @Nonnull List<Prize> prizes) {
        BValidate.notNull(player);
        BValidate.notNull(prizes);

        if (!player.getInventory().removeItem(key.get().item().get()).isEmpty()) {
            PlayerManager.removeKey(player.getUniqueId(), key.get(), 1);
        }
        metadata.add("waitOpen").set("prizes", prizes).set("player", player);
    }

}