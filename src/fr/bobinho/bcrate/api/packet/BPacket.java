package fr.bobinho.bcrate.api.packet;

import fr.bobinho.bcrate.BCrateCore;
import fr.bobinho.bcrate.api.event.BEvent;
import fr.bobinho.bcrate.api.validate.BValidate;
import net.minecraft.network.protocol.Packet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Bobinho packet library
 */
public final class BPacket {

    /**
     * Fields
     */
    private static final LinkedHashMap<UUID, BPacketHandler> packetHandlers = new LinkedHashMap<>();

    /**
     * Main construction
     */
    static {
        //Register join listener
        BEvent.registerEvent(PlayerJoinEvent.class).consume(event -> createHandler(event.getPlayer()));

        //Register quit listener
        BEvent.registerEvent(PlayerQuitEvent.class).consume(event -> removeHandler(event.getPlayer()));
    }

    /**
     * Gets the packet handler by player
     *
     * @param player the player
     * @return the packet handler
     */
    public static @Nonnull Optional<BPacketHandler> getHandler(@Nonnull Player player) {
        BValidate.notNull(player);

        return Optional.ofNullable(packetHandlers.get(player));
    }

    /**
     * Gets the packet handler by player
     *
     * @param uuid the uuid
     * @return the packet handler
     */
    public static @Nonnull Optional<BPacketHandler> getHandler(@Nonnull UUID uuid) {
        BValidate.notNull(uuid);

        return Optional.ofNullable(packetHandlers.get(uuid));
    }


    /**
     * Adds the packet handler to the list
     *
     * @param handler the packet handler
     */
    public static void addHandler(@Nonnull BPacketHandler handler) {
        BValidate.notNull(handler);

        getHandler(handler.getId()).ifPresent(BPacketHandler::delete);
        packetHandlers.put(handler.getId(), handler);
    }

    /**
     * Removes the packet handler from the list
     *
     * @param handler the packet handler
     */
    public static void removeHandler(@Nonnull BPacketHandler handler) {
        BValidate.notNull(handler);

        getHandler(handler.getId()).ifPresent(BPacketHandler::delete);
    }

    /**
     * Removes the packet handler from the list
     *
     * @param player the player
     */
    public static void removeHandler(@Nonnull Player player) {
        BValidate.notNull(player);

        getHandler(player.getUniqueId()).ifPresent(BPacketHandler::delete);
    }

    /**
     * Removes the packet handler from the list
     *
     * @param uuid the player
     */
    public static void removeHandler(@Nonnull UUID uuid) {
        BValidate.notNull(uuid);

        getHandler(uuid).ifPresent(BPacketHandler::delete);
    }

    /**
     * Created the packet handler for player
     *
     * @param player the player
     * @return the packet handler
     */
    public static BPacketHandler createHandler(@Nonnull Player player) {
        BValidate.notNull(player);

        //Created new the packet handler
        BPacketHandler packetHandler = new BPacketHandler(player);

        //Adds new created the packet handler to the list
        addHandler(packetHandler);

        //Return new created the packet handler
        return packetHandler;
    }

    /**
     * Sends the packet to the players
     *
     * @param packet  the packet
     * @param players the players
     */
    public static void send(@Nonnull Packet<?> packet, @Nonnull Player... players) {
        BValidate.notNull(packet);
        BValidate.notNull(players);

        //Sends packet
        Arrays.stream(players)
                .filter(player -> player != null && player.isOnline())
                .forEach(player -> ((CraftPlayer) player).getHandle().b.sendPacket(packet));
    }

    /**
     * Sends the packet to the player
     *
     * @param packet  the packet
     * @param players the uuids
     */
    public static void send(@Nonnull Packet<?> packet, @Nonnull UUID... players) {
        BValidate.notNull(packet);
        BValidate.notNull(players);

        //Sends packet
        Arrays.stream(players)
                .map(Bukkit::getPlayer)
                .filter(player -> player != null && player.isOnline())
                .forEach(player -> ((CraftPlayer) player).getHandle().b.sendPacket(packet));
    }

    /**
     * Sends the packet to the player
     *
     * @param packet  the packet
     * @param players the players
     */
    public static void send(@Nonnull Packet<?> packet, @Nonnull List<Player> players) {
        BValidate.notNull(packet);
        BValidate.notNull(players);

        //Sends packet.
        players.stream()
                .filter(player -> player != null && player.isOnline())
                .forEach(player -> ((CraftPlayer) player).getHandle().b.sendPacket(packet));
    }

    /**
     * Sends the packet to the location
     *
     * @param packet   the packet
     * @param location the location
     * @param radius   the radius
     */
    public static void send(@Nonnull Packet<?> packet, @Nonnull Location location, int radius) {
        BValidate.notNull(packet);
        BValidate.notNull(location);

        //Sends packet.
        Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, radius, radius, radius).stream()
                .filter(entity -> entity instanceof Player)
                .forEach(player -> ((CraftPlayer) player).getHandle().b.sendPacket(packet));
    }

    /**
     * Gets the value from an object
     *
     * @param instance the instance
     * @param name     the name
     * @return Value.
     */
    @Nonnull
    public static Object getValue(@Nonnull Object instance, @Nonnull String name) {
        BValidate.notNull(instance);
        BValidate.notNull(name);

        try {
            //Gets field
            Field field = instance.getClass().getDeclaredField(name);
            boolean flag = field.isAccessible();

            //Allows accessible
            if (!flag) {
                field.setAccessible(true);
            }

            //Gets the value
            Object value = field.get(instance);

            //Disallows accessible
            if (!flag) {
                field.setAccessible(false);
            }

            return value;
        } catch (Exception exception) {
            BCrateCore.getBLogger().error("Couldn't get value from the object!", exception);
        }

        return instance;
    }

}
