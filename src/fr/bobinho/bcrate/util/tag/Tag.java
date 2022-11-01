package fr.bobinho.bcrate.util.tag;

import fr.bobinho.bcrate.api.color.BColor;
import fr.bobinho.bcrate.api.item.BItemBuilder;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.prize.Prize;
import fr.bobinho.bcrate.util.tag.notification.TagNotification;
import fr.bobinho.bcrate.wrapper.ReadOnlyMonoValuedAttribute;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Class representing the tag
 */
public class Tag {

    /**
     * Fields
     */
    private final ReadOnlyMonoValuedAttribute<String> name;
    private final ReadOnlyMonoValuedAttribute<String> description;

    /**
     * Creates a new tag
     *
     * @param name        the name
     * @param description the description
     */
    public Tag(@Nonnull String name, @Nonnull String description) {
        BValidate.notNull(name);
        BValidate.notNull(description);

        this.name = new ReadOnlyMonoValuedAttribute<>(name);
        this.description = new ReadOnlyMonoValuedAttribute<>(description);
    }

    /**
     * Gets the name wrapper
     *
     * @return the name wrapper
     */
    public @Nonnull ReadOnlyMonoValuedAttribute<String> name() {
        return name;
    }

    /**
     * Gets the description wrapper
     *
     * @return the description wrapper
     */
    public @Nonnull ReadOnlyMonoValuedAttribute<String> description() {
        return description;
    }

    /**
     * Gets the tag background
     *
     * @param prize the prize
     * @return the tag background
     */
    public @Nonnull ItemStack getBackground(@Nonnull Prize prize) {
        BValidate.notNull(prize);

        return new BItemBuilder(Material.NAME_TAG)
                .name(BColor.color(name.get()))
                .setLore(List.of(description.get(), ""))
                .lore(prize.tags().contains(this) ? TagNotification.TAG_SELECTED.getNotification() : TagNotification.TAG_NOT_SELECTED.getNotification())
                .build();
    }

}
