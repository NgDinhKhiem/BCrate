package fr.bobinho.bcrate.util.crate.type;

import fr.bobinho.bcrate.api.entity.base.BArmorStandEntity;
import fr.bobinho.bcrate.api.entity.type.BArmoredEntity;
import fr.bobinho.bcrate.api.location.BLocation;
import fr.bobinho.bcrate.api.notification.BPlaceHolder;
import fr.bobinho.bcrate.util.crate.Crate;
import fr.bobinho.bcrate.util.crate.edit.color.Color;
import fr.bobinho.bcrate.util.crate.edit.size.Size;
import fr.bobinho.bcrate.util.crate.notification.CrateNotification;
import fr.bobinho.bcrate.util.key.Key;
import fr.bobinho.bcrate.util.prize.Prize;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class representing the crate EW
 */
public class CrateEW extends Crate {

    /**
     * Creates a new crate
     *
     * @param name     the name
     * @param size     the size
     * @param prizes   the prizes
     * @param location the location
     * @param color    the color
     */
    public CrateEW(@Nonnull String name, @Nonnull Size size, @Nonnull List<Prize> prizes, @Nonnull Location location, @Nonnull Color color, @Nonnull Key key, @Nonnull List<ItemStack> skin, @Nonnull List<BArmorStandEntity> structure) {
        super(name, size, prizes, location, color, key, skin, "EW", structure);
    }

    /**
     * Creates a new crate
     *
     * @param name the name
     * @param size the size
     */
    public CrateEW(@Nonnull String name, @Nonnull Size size, @Nonnull Location location, @Nonnull Color color, @Nonnull Key key, @Nonnull List<ItemStack> skin, @Nonnull List<BArmorStandEntity> structure) {
        this(name, size, new ArrayList<>(), location, color, key, skin, structure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void restart() {
        metadata().add("restart").set("restart:degree", 0.0D);
        List<Prize> items = metadata().getNonNull("prizes");

        List.of(2, 3).forEach(i -> {
            Location newLocation = location().get().add(0, 0.5, 0);
            newLocation.setPitch(0.0F);
            newLocation.setYaw(BLocation.degreeToYaw(270.0F));

            structure().get(i)
                    .setRightArmPose(-90, 0, 0)
                    .setLeftArmPose(-90, 0, 0)
                    .teleport(newLocation)
                    .setEquipment(BArmoredEntity.Equipment.HELMET, items.get(i - 2).skin().get()).render();
        });

        location().get().getWorld().playSound(location().get(), Sound.ENTITY_EVOKER_CAST_SPELL, 1, 2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void run() {
        structure().get(0).setEquipment(BArmoredEntity.Equipment.HELMET, skin().get(0)).render();
        List.of(2, 3).forEach(i -> {
            Location newLocation = location().get().add(0, 0.5, 0);
            newLocation.setPitch(0.0F);
            newLocation.setYaw(BLocation.degreeToYaw(270.0F));

            structure().get(i)
                    .setRightArmPose(-90, 0, 0)
                    .setLeftArmPose(-90, 0, 0)
                    .teleport(newLocation)
                    .render();
        });
        animation().run(() -> {

            if (metadata().has("restart")) {
                double degree = metadata().getNonNull("restart:degree");

                if (degree <= 1.5) {
                    List.of(2, 3).forEach(i -> {
                        Location newLocation = location().get().add(0, 0.5 - Math.pow((degree - 0.6329113) * 1.58, 2) + 1, i == 2 ? -degree : degree);
                        newLocation.setPitch(0.0F);
                        newLocation.setYaw(BLocation.degreeToYaw(270.0F));

                        structure().get(i)
                                .teleport(newLocation)
                                .render();
                    });

                    metadata().set("restart:degree", degree + 0.05D);
                } else if (degree > 401) {
                    Player player = metadata().getNonNull("player");
                    List<Prize> items = metadata().getNonNull("prizes");

                    //Messages
                    player.sendMessage(CrateNotification.CRATE_WON.getNotification());
                    for (Prize prize : items) {
                        ItemStack item = prize.item().get();
                        player.sendMessage(CrateNotification.CRATE_PRIZE_INFO.getNotification(
                                new BPlaceHolder("%amount%", String.valueOf(item.getAmount())),
                                new BPlaceHolder("%name%", (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : item.getType().name().replace("_", " "))));

                        if (prize.rarity().get()) {
                            Bukkit.getOnlinePlayers().forEach(receiver -> receiver.sendMessage(CrateNotification.CRATE_PRIZE_INFO_GLOBAL.getNotification(
                                    new BPlaceHolder("%name%", player.getName()),
                                    new BPlaceHolder("%amount%", String.valueOf(item.getAmount())),
                                    new BPlaceHolder("%item%", (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : item.getType().name().replace("_", " ")))));
                        }
                    }

                    //Gives prizes
                    player.getInventory().addItem(items.stream().map(prize -> prize.item().get()).toArray(ItemStack[]::new));
                    metadata().add("close").remove("restart").set("open:degree", 130);
                    List.of(2, 3).forEach(i -> {
                        Location newLocation = location().get().add(0, 0.5, 0);
                        newLocation.setPitch(0.0F);
                        newLocation.setYaw(BLocation.degreeToYaw(270.0F));

                        structure().get(i).teleport(newLocation).clearEquipments().render();
                        location().get().getWorld().spawnParticle(
                                Particle.REDSTONE,
                                location().get().add(0, 1.5, i == 2 ? -1.5 : 1.5),
                                10,
                                0.2,
                                0.2,
                                0.2,
                                new Particle.DustOptions(org.bukkit.Color.WHITE, 2));

                        location().get().getWorld().playSound(location().get(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 2);
                    });
                } else {
                    List.of(2, 3).forEach(i -> {
                        Location newLocation = location().get().add(0, 0.37690040004 - Math.abs(((degree % 360) - 180) / 600) + 0.3, i == 2 ? -1.5 : 1.5);
                        newLocation.setPitch(0.0F);
                        newLocation.setYaw(BLocation.degreeToYaw(270.0F));

                        structure().get(i)
                                .teleport(newLocation)
                                .render();
                    });

                    metadata().set("restart:degree", degree + 10.0D);
                }
            }

            //Closes animation
            else if (metadata().has("close")) {
                int degree = metadata().getNonNull("open:degree");

                //Restarts and give prizes

                if (degree < 0) {
                    metadata().remove("player").remove("prizes").remove("open").remove("close").remove("open:degree").add("spine");

                    structure().get(0).setEquipment(BArmoredEntity.Equipment.HELMET, skin().get(0)).render();
                    List.of(1, 4, 18).forEach(i -> structure().get(i).clearEquipments().render());
                } else {
                    structure().get(Math.min(17, 5 + degree / 10)).clearEquipments().render();
                    structure().get(Math.min(31, 19 + degree / 10)).clearEquipments().render();

                    structure().get(4 + degree / 10).setEquipment(BArmoredEntity.Equipment.HELMET, skin().get(2)).render();
                    structure().get(18 + degree / 10).setEquipment(BArmoredEntity.Equipment.HELMET, skin().get(3)).render();
                }

                metadata().set("open:degree", degree - 10);
            }

            //Opens animation
            else if (metadata().has("open")) {
                int degree = metadata().getNonNull("open:degree");

                //Restarts and give prizes
                if (degree > 130) {
                    restart();
                } else {
                    Random r = new Random();
                    for (int j = 0; j < 8; j++) {
                        location().get().getWorld().spawnParticle(
                                Particle.REDSTONE,
                                location().get().add(0.0D, 2.3D, 0.0D),
                                1,
                                0.2,
                                0.2,
                                0.2,
                                new Particle.DustOptions(org.bukkit.Color.fromBGR(r.nextInt(256), r.nextInt(256), r.nextInt(256)), 2));
                    }

                    structure().get(Math.max(4, 3 + degree / 10)).clearEquipments().render();
                    structure().get(Math.max(18, 17 + degree / 10)).clearEquipments().render();

                    structure().get(4 + degree / 10).setEquipment(BArmoredEntity.Equipment.HELMET, skin().get(2)).render();
                    structure().get(18 + degree / 10).setEquipment(BArmoredEntity.Equipment.HELMET, skin().get(3)).render();
                }

                metadata().set("open:degree", degree + 10);
            }

            //Spines animation
            else if (metadata().has("spine")) {
                float degree = metadata().getNonNull("spine:degree");

                if (metadata().has("waitOpen")) {

                    //Opens the crate
                    if (degree == 10) {
                        open();
                    }
                }

                if (!metadata().has("open")) {
                    //Gets target location
                    Location newLocation = location().get().add(0, -Math.abs((degree - 180) / 360) + 0.5, 0);
                    newLocation.setPitch(0.0F);
                    newLocation.setYaw(BLocation.degreeToYaw(degree));

                    structure().get(0).teleport(newLocation).render();

                    metadata().set("spine:degree", (degree + 10.0F) % 360);
                }
            }
        });
    }

}