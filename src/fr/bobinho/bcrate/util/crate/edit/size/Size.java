package fr.bobinho.bcrate.util.crate.edit.size;

import fr.bobinho.bcrate.api.item.BItemBuilder;
import fr.bobinho.bcrate.api.notification.BPlaceHolder;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.crate.edit.size.notification.SizeNotification;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * Class representing the size of crate menu
 */
public enum Size {
    SIZE_1(Material.WHITE_WOOL, 9),
    SIZE_2(Material.BLUE_WOOL, 18),
    SIZE_3(Material.GREEN_WOOL, 27),
    SIZE_4(Material.RED_WOOL, 36),
    SIZE_5(Material.YELLOW_WOOL, 45),
    SIZE_6(Material.PINK_WOOL, 54);

    /**
     * Fields
     */
    private final Material material;
    private final int dimension;

    /**
     * Creates a new size
     *
     * @param material  the material
     * @param dimension the dimension
     */
    Size(@Nonnull Material material, int dimension) {
        BValidate.notNull(material);

        this.material = material;
        this.dimension = dimension;
    }

    /**
     * Gets the material
     *
     * @return the material
     */
    public @Nonnull Material getMaterial() {
        return material;
    }

    /**
     * Gets the dimension
     *
     * @return the dimension
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * Gets the background
     *
     * @return the background
     */
    public @Nonnull ItemStack getBackground() {
        return new BItemBuilder(material)
                .name(SizeNotification.SIZE_ITEM_NAME.getNotification(new BPlaceHolder("%size%", String.valueOf(dimension))))
                .build();
    }

    /**
     * Gets the size from an ItemStack
     *
     * @param item the item
     * @return the size
     */
    public static @Nonnull Size getSize(@Nonnull ItemStack item) {
        BValidate.notNull(item);

        return Arrays.stream(values()).filter(color -> color.getMaterial() == item.getType()).findFirst().orElse(SIZE_1);
    }

    /**
     * Gets the next size
     *
     * @param size the previous size
     * @return the next size
     */
    public static @Nonnull Size getNext(@Nonnull Size size) {
        BValidate.notNull(size);

        return values()[(Arrays.stream(values()).toList().indexOf(size) + 1) % values().length];
    }

}
