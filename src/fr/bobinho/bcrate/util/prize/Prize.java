package fr.bobinho.bcrate.util.prize;

import fr.bobinho.bcrate.api.item.BItemBuilder;
import fr.bobinho.bcrate.api.notification.BPlaceHolder;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.crate.Crate;
import fr.bobinho.bcrate.util.prize.notification.PrizeNotification;
import fr.bobinho.bcrate.util.prize.ux.PrizeEditMenu;
import fr.bobinho.bcrate.util.tag.Tag;
import fr.bobinho.bcrate.wrapper.MonoValuedAttribute;
import fr.bobinho.bcrate.wrapper.MultiValuedAttribute;
import fr.bobinho.bcrate.wrapper.ReadOnlyMonoValuedAttribute;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class representing the prize
 */
public class Prize {

    /**
     * Fields
     */
    private final MonoValuedAttribute<ItemStack> item;
    private final MonoValuedAttribute<Integer> slot;
    private final MonoValuedAttribute<Double> chance;
    private final MultiValuedAttribute<Tag> tags;
    private final ReadOnlyMonoValuedAttribute<PrizeEditMenu> editMenu;


    /**
     * Creates a new prize
     *
     * @param item   the item
     * @param slot   the slot
     * @param chance the chance
     * @param tag    the tag
     */
    public Prize(@Nonnull ItemStack item, int slot, double chance, @Nonnull List<Tag> tag) {
        BValidate.notNull(item);
        BValidate.notNull(tag);

        this.item = new MonoValuedAttribute<>(item);
        this.slot = new MonoValuedAttribute<>(slot);
        this.chance = new MonoValuedAttribute<>(chance);
        this.tags = new MultiValuedAttribute<>(tag);
        this.editMenu = new ReadOnlyMonoValuedAttribute<>(new PrizeEditMenu(this));
    }

    /**
     * Creates a new prize without tag
     *
     * @param item   the item
     * @param slot   the slot
     * @param chance the chance
     */
    public Prize(@Nonnull ItemStack item, int slot, double chance) {
        this(item, slot, chance, new ArrayList<>());
    }

    /**
     * Gets the item wrapper
     *
     * @return the item wrapper
     */
    public @Nonnull MonoValuedAttribute<ItemStack> item() {
        return item;
    }

    /**
     * Gets the slot wrapper
     *
     * @return the slot wrapper
     */
    public @Nonnull MonoValuedAttribute<Integer> slot() {
        return slot;
    }

    /**
     * Gets the chance wrapper
     *
     * @return the chance wrapper
     */
    public @Nonnull MonoValuedAttribute<Double> chance() {
        return chance;
    }

    /**
     * Gets the tag wrapper
     *
     * @return the tag wrapper
     */
    public @Nonnull MultiValuedAttribute<Tag> tags() {
        return tags;
    }

    /**
     * Gets the edit menu wrapper
     *
     * @return the edit menu wrapper
     */
    public @Nonnull ReadOnlyMonoValuedAttribute<PrizeEditMenu> editMenu() {
        return editMenu;
    }

    /**
     * Gets the prize edit background
     *
     * @return the prize edit background
     */
    public @Nonnull ItemStack getEditBackground() {
        return new BItemBuilder(item.get())
                .lore(tags.get().stream().map(tag -> tag.description().get()).toList())
                .lore(List.of("", PrizeNotification.PRIZE_CHANCE.getNotification(new BPlaceHolder("%chance%", String.valueOf(chance.get())))))
                .build();
    }

    /**
     * Gets the prize background
     *
     * @param crate the crate
     * @return the prize background
     */
    public @Nonnull ItemStack getBackground(@Nonnull Crate crate) {
        boolean isBarrier = item.get().getType() == Material.BARRIER;

        //Gets the item background (replaces barrier by color)
        return (isBarrier ?
                new BItemBuilder(crate.color().get().getBackground())
                        .name(" ")
                        .setLore(Collections.emptyList())
                :
                new BItemBuilder(item().get())
                        .lore(tags.get().stream().map(tag -> tag.description().get()).toList())
                        .lore(List.of("", PrizeNotification.PRIZE_CHANCE.getNotification(new BPlaceHolder("%chance%", String.valueOf(chance.get()))))))
                .build();
    }
}
