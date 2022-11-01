package fr.bobinho.bcrate.util.crate.edit.color;

import fr.bobinho.bcrate.api.item.BItemBuilder;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.crate.edit.color.notification.ColorNotification;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * Class representing the background color of crate menu
 */
public enum Color {
    BLACK(Material.BLACK_STAINED_GLASS_PANE, ColorNotification.COLOR_BLACK.getNotification()),
    BLUE(Material.BLUE_STAINED_GLASS_PANE, ColorNotification.COLOR_BLUE.getNotification()),
    BROWN(Material.BROWN_STAINED_GLASS_PANE, ColorNotification.COLOR_BROWN.getNotification()),
    CYAN(Material.CYAN_STAINED_GLASS_PANE, ColorNotification.COLOR_CYAN.getNotification()),
    GRAY(Material.GRAY_STAINED_GLASS_PANE, ColorNotification.COLOR_GRAY.getNotification()),
    GREEN(Material.GREEN_STAINED_GLASS_PANE, ColorNotification.COLOR_GREEN.getNotification()),
    LIGHT_BLUE(Material.LIGHT_BLUE_STAINED_GLASS_PANE, ColorNotification.COLOR_LIGHT_BLUE.getNotification()),
    LIGHT_GRAY(Material.LIGHT_GRAY_STAINED_GLASS_PANE, ColorNotification.COLOR_LIGHT_GRAY.getNotification()),
    LIME(Material.LIME_STAINED_GLASS_PANE, ColorNotification.COLOR_LIME.getNotification()),
    MAGENTA(Material.MAGENTA_STAINED_GLASS_PANE, ColorNotification.COLOR_MAGENTA.getNotification()),
    ORANGE(Material.ORANGE_STAINED_GLASS_PANE, ColorNotification.COLOR_ORANGE.getNotification()),
    PINK(Material.PINK_STAINED_GLASS_PANE, ColorNotification.COLOR_PINK.getNotification()),
    PURPLE(Material.PURPLE_STAINED_GLASS_PANE, ColorNotification.COLOR_PURPLE.getNotification()),
    RED(Material.RED_STAINED_GLASS_PANE, ColorNotification.COLOR_RED.getNotification()),
    WHITE(Material.WHITE_STAINED_GLASS_PANE, ColorNotification.COLOR_WHITE.getNotification()),
    YELLOW(Material.YELLOW_STAINED_GLASS_PANE, ColorNotification.COLOR_YELLOW.getNotification());

    /**
     * Fields
     */
    private final Material material;
    private final String name;

    /**
     * Creates a new color
     *
     * @param material the material
     * @param name     the name
     */
    Color(@Nonnull Material material, @Nonnull String name) {
        BValidate.notNull(material);
        BValidate.notNull(name);

        this.material = material;
        this.name = name;
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
     * Gets the name
     *
     * @return the name
     */
    public @Nonnull String getName() {
        return name;
    }

    /**
     * Gets the background
     *
     * @return the background
     */
    public @Nonnull ItemStack getBackground() {
        return new BItemBuilder(material).name(name).build();
    }

    /**
     * Gets the color from an ItemStack
     *
     * @param item the item
     * @return the color
     */
    public static @Nonnull Color getColor(@Nonnull ItemStack item) {
        BValidate.notNull(item);

        return Arrays.stream(values()).filter(color -> color.getMaterial() == item.getType()).findFirst().orElse(BLACK);
    }

    /**
     * Gets the next color
     *
     * @param color the previous color
     * @return the next color
     */
    public static @Nonnull Color getNext(@Nonnull Color color) {
        BValidate.notNull(color);

        return values()[(Arrays.stream(values()).toList().indexOf(color) + 1) % values().length];
    }

}
