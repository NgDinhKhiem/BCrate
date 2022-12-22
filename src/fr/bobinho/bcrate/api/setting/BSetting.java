package fr.bobinho.bcrate.api.setting;

import fr.bobinho.bcrate.BCrateCore;
import fr.bobinho.bcrate.api.validate.BValidate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.awt.geom.IllegalPathStateException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

/**
 * Bobinho setting library
 */
public final class BSetting {

    /**
     * Fields
     */
    private final String fileName;

    private YamlConfiguration configuration;

    /**
     * Creates a new setting
     *
     * @param fileName the setting file name
     */
    public BSetting(@Nonnull String fileName) {
        BValidate.notNull(fileName);

        this.fileName = fileName;
        initialize();
    }

    /**
     * Gets the file name
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Initializes settings file
     */
    public void initialize() {
        File file = new File(BCrateCore.getInstance().getDataFolder(), getFileName() + ".yml");

        if (!file.exists()) {
            try {
                Files.createDirectories(file.getParentFile().toPath());
                InputStream input = BSetting.class.getResourceAsStream("/" + getFileName() + ".yml");
                if (input != null) {
                    Files.copy(input, file.toPath());
                } else {
                    Files.createFile(file.toPath());
                }
            } catch (Exception e) {
                BCrateCore.getBLogger().error("Could not load the " + getFileName() + ".yml file!", e);
                return;
            }
        }

        configuration = YamlConfiguration.loadConfiguration(file);

        BCrateCore.getBLogger().info("Successfully loaded " + getFileName() + " data.");
    }

    /**
     * Gets a set containing all keys in this section
     *
     * @return the set containing all keys in this section
     */
    public @Nonnull Set<String> getKeys() {
        return configuration.getKeys(false);
    }

    /**
     * Gets the requested int by path
     *
     * @param path the path
     * @return the requested int by path
     */
    public int getInt(@Nonnull String path) {
        BValidate.notNull(path);

        if (!configuration.isInt(path)) {
            throw new IllegalPathStateException();
        }

        return configuration.getInt(path);
    }

    /**
     * Checks if the path is associated with an int and gets the optional int
     *
     * @param path the path
     * @return the optional int
     */
    public @Nonnull Optional<Integer> isInt(@Nonnull String path) {
        BValidate.notNull(path);

        return Optional.ofNullable(configuration.isInt(path) ? configuration.getInt(path) : null);
    }

    /**
     * Gets the requested double by path
     *
     * @param path the path
     * @return the requested double by path
     */
    public double getDouble(@Nonnull String path) {
        BValidate.notNull(path);

        if (!configuration.isDouble(path)) {
            throw new IllegalPathStateException();
        }

        return configuration.getDouble(path);
    }

    /**
     * Checks if the path is associated with a double and gets the optional double
     *
     * @param path the path
     * @return the optional double
     */
    public @Nonnull Optional<Double> isDouble(@Nonnull String path) {
        BValidate.notNull(path);

        return Optional.ofNullable(configuration.isDouble(path) ? configuration.getDouble(path) : null);
    }

    /**
     * Gets the requested boolean by path
     *
     * @param path the path
     * @return the requested boolean by path
     */
    public boolean getBoolean(@Nonnull String path) {
        BValidate.notNull(path);

        if (!configuration.isBoolean(path)) {
            throw new IllegalPathStateException();
        }

        return configuration.getBoolean(path);
    }

    /**
     * Gets the requested String by path
     *
     * @param path the path
     * @return the requested String by path
     */
    public @Nonnull String getString(@Nonnull String path) {
        BValidate.notNull(path);

        return Optional.ofNullable(configuration.getString(path)).orElseThrow(IllegalPathStateException::new);
    }

    /**
     * Gets the requested String list by path
     *
     * @param path the path
     * @return the requested String list by path
     */
    public @Nonnull List<String> getStringList(@Nonnull String path) {
        BValidate.notNull(path);

        return configuration.getStringList(path);
    }

    /**
     * Gets the requested ItemStack by path
     *
     * @param path the path
     * @return the requested ItemStack by path
     */
    public @Nonnull ItemStack getItemStack(@Nonnull String path) {
        BValidate.notNull(path);

        return Optional.ofNullable(configuration.getItemStack(path)).orElseThrow(IllegalPathStateException::new);
    }

    /**
     * Gets the requested ItemStack list by path
     *
     * @param path the path
     * @return the requested ItemStack list by path
     */
    public @Nonnull List<ItemStack> getItemStackList(@Nonnull String path) {
        BValidate.notNull(path);

        return new ArrayList<>((Collection<? extends ItemStack>) Optional.ofNullable(configuration.getList(path)).orElse(Collections.emptyList()));
    }

    /**
     * Gets the requested ItemStack by path
     *
     * @param path the path
     * @return the requested ItemStack by path
     */
    public @Nonnull Set<String> getConfigurationSection(@Nonnull String path) {
        BValidate.notNull(path);

        return Optional.ofNullable(configuration.getConfigurationSection(path))
                .map(configurationSection -> configurationSection.getKeys(false))
                .orElse(Collections.emptySet());
    }

    /**
     * Sets the specified path to the given value
     *
     * @param path  the path
     * @param value the value
     */
    public void set(@Nonnull String path, @Nonnull Object value) {
        BValidate.notNull(path);
        BValidate.notNull(value);

        configuration.set(path, value);
    }

    /**
     * Clears configuration
     */
    public void clear() {
        for (String key : getKeys()) {
            configuration.set(key, null);
        }
    }

    /**
     * Saves configuration
     */
    public void save() {
        try {
            configuration.save(BCrateCore.getInstance().getDataFolder() + "/" + getFileName() + ".yml");
        } catch (IOException e) {
            BCrateCore.getBLogger().error("Could not save the " + getFileName() + ".yml file!", e);
        }
    }

}
