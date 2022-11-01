package fr.bobinho.bcrate.util.key;

import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.wrapper.ReadOnlyMonoValuedAttribute;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * Class representing the key
 */
public class Key {

    /**
     * Fields
     */
    private final ReadOnlyMonoValuedAttribute<String> name;
    private final ReadOnlyMonoValuedAttribute<ItemStack> item;

    /**
     * Creates a new key
     *
     * @param name the name
     * @param item the item
     */
    public Key(@Nonnull String name, @Nonnull ItemStack item) {
        BValidate.notNull(name);
        BValidate.notNull(item);

        this.name = new ReadOnlyMonoValuedAttribute<>(name);
        this.item = new ReadOnlyMonoValuedAttribute<>(item);
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
     * Gets the item wrapper
     *
     * @return the item wrapper
     */
    public @Nonnull ReadOnlyMonoValuedAttribute<ItemStack> item() {
        return item;
    }

}
