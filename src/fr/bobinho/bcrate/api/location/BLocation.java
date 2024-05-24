package fr.bobinho.bcrate.api.location;

import fr.bobinho.bcrate.api.validate.BValidate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Bobinho location library
 */
public final class BLocation {

    /**
     * Unitilizable constructor (utility class)
     */
    private BLocation() {
    }

    /**
     * Serializes a location
     *
     * @param location the location
     * @return the serialized location
     */
    public static @Nonnull String getAsString(@Nonnull Location location) {
        BValidate.notNull(location);

        return Objects.requireNonNull(location.getWorld()).getName() + ":" +
                location.getX() + ":" +
                location.getY() + ":" +
                location.getZ() + ":" +
                location.getYaw() + ":" +
                location.getPitch();
    }

    /**
     * Deserializes a location
     *
     * @param locationString the location string
     * @return the deserialized location
     */
    public static @Nonnull Location getAsLocation(@Nonnull String locationString) {
        BValidate.notNull(locationString);

        String[] locationInformations = locationString.split(":");
        return new Location(
                Bukkit.getWorld(locationInformations[0]),
                Double.parseDouble(locationInformations[1]),
                Double.parseDouble(locationInformations[2]),
                Double.parseDouble(locationInformations[3]),
                Float.parseFloat(locationInformations[4]),
                Float.parseFloat(locationInformations[5])
        );
    }

    /**
     * Checks if the tested 1D coordinate is between the two others
     *
     * @param coordinates1 the first 1D coordinate
     * @param coordinates2 the second 1D coordinate
     * @param tested       the tested 1D coordinate
     * @return if the tested 1D coordinate is between the two others
     */
    public static boolean isBetweenTwo1DPoint(double coordinates1, double coordinates2, double tested) {
        return (tested >= coordinates1 && tested <= coordinates2) || (tested <= coordinates1 && tested >= coordinates2);
    }

    /**
     * Checks if the tested 2D coordinate is between the two others
     *
     * @param location1 the first 2D coordinate
     * @param location2 the second 2D coordinate
     * @param tested    the tested 0D coordinate
     * @return if the tested 2D coordinate is between the two others
     */
    public static boolean isBetweenTwo2DPoint(@Nonnull Location location1, @Nonnull Location location2, @Nonnull Location tested) {
        BValidate.notNull(location1);
        BValidate.notNull(location2);
        BValidate.notNull(tested);

        return Objects.equals(location1.getWorld(), tested.getWorld()) && isBetweenTwo1DPoint(location1.getX(), location2.getX(), tested.getX()) && isBetweenTwo1DPoint(location1.getZ(), location2.getZ(), tested.getZ());
    }

    /**
     * Measures the distance between two location
     *
     * @param location1 the first location
     * @param location2 the second location
     * @return the distance between two location
     */
    public static double measure(@Nonnull Location location1, @Nonnull Location location2) {
        BValidate.notNull(location1);
        BValidate.notNull(location2);

        //If one of the two location is in different world, return max integer value
        if (!Objects.requireNonNull(location1.getWorld()).getUID().equals(Objects.requireNonNull(location2.getWorld()).getUID()))
            return Integer.MAX_VALUE;

        //Calculates and returns
        return location1.distance(location2);
    }

    /**
     * Checks if the second location is observable from the first location
     *
     * @param location1 the first location
     * @param location2 the second location
     * @return true if the second location is observable from the first location, false otherwise
     */
    public static boolean canSee(@Nonnull Location location1, @Nonnull Location location2) {
        BValidate.notNull(location1);
        BValidate.notNull(location2);

        //Measure distance with player tracking range from the spigot config
        return measure(location1, location2) <= ((CraftWorld) Objects.requireNonNull(location1.getWorld())).getHandle().spigotConfig.playerTrackingRange / 1.5;
    }

    /**
     * Converts degree to yaw
     *
     * @param angle the angle
     * @return the converted yaw
     */
    public static float degreeToYaw(double angle) {
        //Reduces the angle
        angle = angle % 360;

        //Forces it to be the positive remainder, so that 0 <= angle < 360
        angle = (angle + 360) % 360;

        //Forces into the minimum absolute value residue class, so that -180 < angle <= 180
        if (angle > 180)
            angle -= 360;

        return (float) angle;
    }

    /**
     * Gets the location without orientation
     *
     * @param location the location
     * @return the location without orientation
     */
    public static Location getPosition(@Nonnull Location location) {
        BValidate.notNull(location);

        return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
    }

    /**
     * Gets the middle from two locations
     *
     * @param location1 the first location
     * @param location2 the second location
     * @return the middle from two locations
     */
    public static Location getMiddle(@Nonnull Location location1, @Nonnull Location location2) {
        BValidate.notNull(location1);
        BValidate.notNull(location2);

        return new Location(location1.getWorld(), (location1.getX() + location2.getX()) / 2, (location1.getY() + location2.getY()) / 2, (location1.getZ() + location2.getZ()) / 2, 0.0F, 0.0F);
    }

}
