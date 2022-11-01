package fr.bobinho.bcrate.api.packet;

import fr.bobinho.bcrate.api.scheduler.BScheduler;
import fr.bobinho.bcrate.api.validate.BValidate;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Bobinho packet handler library
 */
public final class BPacketHandler {

    /**
     * Fields
     */
    private final UUID id;
    private final ChannelPipeline pipeline;

    /**
     * Creates new packet handler for player
     *
     * @param player the player
     */
    public BPacketHandler(@Nonnull Player player) {
        BValidate.notNull(player);

        this.id = player.getUniqueId();

        //Add new pipeline named "bpacket_handler" after "packet_handler".
        this.pipeline = ((CraftPlayer) player).getHandle().b.a.k.pipeline().addBefore("packet_handler", "bpacket_handler", new ChannelDuplexHandler() {
            @Override
            public void channelRead(@Nonnull ChannelHandlerContext channelHandlerContext, @Nonnull Object message) throws Exception {
                BValidate.notNull(channelHandlerContext);
                BValidate.notNull(message);

                AtomicBoolean status = new AtomicBoolean(false);
                BScheduler.syncScheduler().run(() -> {

                    //Creates packet event.
                    BPacketEvent packetEvent = new BPacketEvent(player, (Packet<?>) message, BPacketEvent.Type.READ);

                    //Calls event
                    Bukkit.getPluginManager().callEvent(packetEvent);

                    //Sets event status
                    status.set(packetEvent.isCancelled());
                });

                //Checks if it is cancelled
                if (status.get()) {
                    return;
                }

                //Passes to default Minecraft packet reader/decoder
                //If you block it, it will not read default one
                super.channelRead(channelHandlerContext, message);
            }

            @Override
            public void write(@Nonnull ChannelHandlerContext channelHandlerContext, @Nonnull Object message, @Nonnull ChannelPromise channelPromise) throws Exception {
                BValidate.notNull(channelHandlerContext);
                BValidate.notNull(message);
                BValidate.notNull(channelPromise);

                AtomicBoolean status = new AtomicBoolean(false);
                BScheduler.syncScheduler().run(() -> {

                    //Creates packet event.
                    BPacketEvent packetEvent = new BPacketEvent(player, (Packet<?>) message, BPacketEvent.Type.WRITE);

                    //Calls event
                    Bukkit.getPluginManager().callEvent(packetEvent);

                    //Sets event status
                    status.set(packetEvent.isCancelled());
                });

                //Checks if it is cancelled
                if (status.get()) {
                    return;
                }

                //Passes to default Minecraft packet writer
                //If you block it, it will not fire/call default one
                super.write(channelHandlerContext, message, channelPromise);
            }
        });
    }

    /**
     * Gets the id
     *
     * @return the id
     */
    public @Nonnull UUID getId() {
        return id;
    }

    /**
     * Deletes the packet handler
     */
    public void delete() {

        //If the pipeline is not initialized, we don't want to use it
        if (this.pipeline != null && this.pipeline.get("bpacket_handler") != null) {

            //Removes "bpacket_handler" pipeline from the player's pipeline.
            this.pipeline.remove("bpacket_handler");
        }
    }

}