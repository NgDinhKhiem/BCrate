package fr.bobinho.bcrate.api.packet;

import fr.bobinho.bcrate.api.validate.BValidate;
import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Bobinho packet event library
 */
public class BPacketEvent extends Event implements Cancellable {

    /**
     * Packet type
     */
    public enum Type {
        READ,
        WRITE
    }

    /**
     * Fields
     */
    private static final HandlerList handlerList = new HandlerList();
    private final Player player;
    private final Packet<?> packet;
    private final Type type;
    private boolean cancelled;

    /**
     * Creates a new packet event
     *
     * @param player the player
     * @param packet the packet
     * @param type   the type
     */
    public BPacketEvent(@Nonnull Player player, @Nonnull Packet<?> packet, @Nonnull Type type) {
        BValidate.notNull(player);
        BValidate.notNull(packet);
        BValidate.notNull(type);

        this.player = player;
        this.packet = packet;
        this.type = type;
    }

    /**
     * Gets the player
     *
     * @return the player
     */
    public @Nonnull Player getPlayer() {
        return player;
    }

    /**
     * Gets the packet
     *
     * @param <T> the packet type
     * @return the packet
     */
    public @Nonnull <T extends Packet<?>> T getPacket() {
        return (T) packet;
    }

    /**
     * Gets the packet
     *
     * @param packetClass the packet class
     * @param <T>         the packet type
     * @return the packet
     */
    public @Nonnull <T extends Packet<?>> T getPacket(@Nonnull Class<T> packetClass) {
        BValidate.notNull(packetClass);

        return (T) packet;
    }

    /**
     * Gets the packet type
     *
     * @return the packet type
     */
    public @Nonnull Type getType() {
        return type;
    }

    /**
     * Checks if the event is cancelled
     *
     * @return true if the event is cancelled, false otherwise
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the event cancel status
     *
     * @param cancelled the event cancel status
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Gets the event handler list
     *
     * @return the event handler list
     */
    public @Nonnull HandlerList getHandlers() {
        return handlerList;
    }

    /**
     * Gets the event handler list
     *
     * @return tge event handler list
     */
    public static @Nonnull HandlerList getHandlerList() {
        return handlerList;
    }
}
