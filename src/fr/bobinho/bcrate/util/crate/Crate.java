package fr.bobinho.bcrate.util.crate;

import fr.bobinho.bcrate.api.entity.BEntity;
import fr.bobinho.bcrate.api.entity.base.BArmorStandEntity;
import fr.bobinho.bcrate.api.entity.type.BArmoredEntity;
import fr.bobinho.bcrate.api.metadata.BMetadata;
import fr.bobinho.bcrate.api.scheduler.BScheduler;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.crate.edit.color.Color;
import fr.bobinho.bcrate.util.crate.edit.size.Size;
import fr.bobinho.bcrate.util.crate.ux.CrateEditMenu;
import fr.bobinho.bcrate.util.crate.ux.CratePrizeMenu;
import fr.bobinho.bcrate.util.crate.ux.CrateShowMenu;
import fr.bobinho.bcrate.util.crate.ux.CrateStructureMenu;
import fr.bobinho.bcrate.util.key.Key;
import fr.bobinho.bcrate.util.player.PlayerManager;
import fr.bobinho.bcrate.util.prize.Prize;
import fr.bobinho.bcrate.wrapper.MonoValuedAttribute;
import fr.bobinho.bcrate.wrapper.MultiValuedAttribute;
import fr.bobinho.bcrate.wrapper.ReadOnlyMonoValuedAttribute;
import fr.bobinho.bcrate.wrapper.UpperBoundedMultiValuedAttribute;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the crate
 */
public abstract class Crate {

    /**
     * Fields
     */
    private final MonoValuedAttribute<String> name;
    private final MonoValuedAttribute<Size> size;
    private final UpperBoundedMultiValuedAttribute<Prize> prizes;
    private final ReadOnlyMonoValuedAttribute<Location> location;
    private final MonoValuedAttribute<Color> color;
    private final MonoValuedAttribute<Key> key;
    private final MultiValuedAttribute<ItemStack> skin;
    private final ReadOnlyMonoValuedAttribute<String> direction;
    private final ReadOnlyMonoValuedAttribute<CrateEditMenu> editMenu;
    private final ReadOnlyMonoValuedAttribute<CratePrizeMenu> prizeMenu;
    private final ReadOnlyMonoValuedAttribute<CrateShowMenu> showMenu;
    private final ReadOnlyMonoValuedAttribute<CrateStructureMenu> structureMenu;
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
    public Crate(@Nonnull String name, @Nonnull Size size, @Nonnull List<Prize> prizes, @Nonnull Location location, @Nonnull Color color, @Nonnull Key key, @Nonnull List<ItemStack> skin, @Nonnull String direction, @Nonnull List<BArmorStandEntity> structure) {
        BValidate.notNull(name);
        BValidate.notNull(size);
        BValidate.notNull(prizes);
        BValidate.notNull(color);
        BValidate.notNull(key);
        BValidate.notNull(direction);
        BValidate.notNull(structure);

        this.name = new MonoValuedAttribute<>(name);
        this.size = new MonoValuedAttribute<>(size);
        this.prizes = new UpperBoundedMultiValuedAttribute<>(size.getDimension(), prizes);
        this.location = new ReadOnlyMonoValuedAttribute<>(location);
        this.color = new MonoValuedAttribute<>(color);
        this.key = new MonoValuedAttribute<>(key);
        this.skin = new MultiValuedAttribute<>(skin);
        this.direction = new ReadOnlyMonoValuedAttribute<>(direction);
        this.structure = new MultiValuedAttribute<>(structure);
        this.editMenu = new ReadOnlyMonoValuedAttribute<>(new CrateEditMenu(this));
        this.prizeMenu = new ReadOnlyMonoValuedAttribute<>(new CratePrizeMenu(this));
        this.showMenu = new ReadOnlyMonoValuedAttribute<>(new CrateShowMenu(this));
        this.structureMenu = new ReadOnlyMonoValuedAttribute<>(new CrateStructureMenu(this));
        this.metadata = new BMetadata().add("spine").set("spine:degree", 0.0F);
        this.animation = BScheduler.syncScheduler().every(2);

        this.run();
    }

    /**
     * Creates a new crate
     *
     * @param name the name
     * @param size the size
     */
    public Crate(@Nonnull String name, @Nonnull Size size, @Nonnull Location location, @Nonnull Color color, @Nonnull Key key, @Nonnull List<ItemStack> skin, @Nonnull String direction, @Nonnull List<BArmorStandEntity> structure) {
        this(name, size, new ArrayList<>(), location, color, key, skin, direction, structure);
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
     * Gets the skin wrapper
     *
     * @return the skin wrapper
     */
    public @Nonnull MultiValuedAttribute<ItemStack> skin() {
        return skin;
    }

    public @Nonnull ReadOnlyMonoValuedAttribute<String> direction() {
        return direction;
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
     * Gets the structure menu wrapper
     *
     * @return the structure menu wrapper
     */
    public @Nonnull ReadOnlyMonoValuedAttribute<CrateStructureMenu> structureMenu() {
        return structureMenu;
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
    protected void open() {
        structure.get(0).clearEquipments().render();
        structure.get(1).setEquipment(BArmoredEntity.Equipment.HELMET, skin().get(1)).render();

        metadata.remove("spine").remove("waitOpen").add("open").set("open:degree", 0);

        location.get().getWorld().playSound(location.get(), Sound.ENTITY_FIREWORK_ROCKET_SHOOT, 1, 2);
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

        PlayerManager.openCrate(player.getUniqueId(), true);

        boolean finded = false;
        for (int i = player.getInventory().getSize() - 1; i >= 0 && !finded; i--) {
            if (player.getInventory().getItem(i) != null && player.getInventory().getItem(i).isSimilar(key.get().item().get())) {
                ItemStack current = player.getInventory().getItem(i).clone();
                finded = true;
                if (current.getAmount() > 1) {
                    current.setAmount(current.getAmount() - 1);
                    player.getInventory().setItem(i, current);
                } else {
                    player.getInventory().setItem(i, null);
                }
            }
        }

        if (!finded) {
            PlayerManager.removeKey(player.getUniqueId(), key.get(), 1);
        }

        structure.get().forEach(BEntity::render);
        metadata.add("waitOpen").set("prizes", prizes).set("player", player);
    }

    /**
     * Restarts the crate and give prizes to the last player
     */
    protected abstract void restart();

    /**
     * Runs the crate
     */
    protected abstract void run();
}