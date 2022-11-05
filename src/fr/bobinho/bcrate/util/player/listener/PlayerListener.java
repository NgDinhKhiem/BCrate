package fr.bobinho.bcrate.util.player.listener;

import fr.bobinho.bcrate.api.event.BEvent;
import fr.bobinho.bcrate.util.crate.CrateManager;
import fr.bobinho.bcrate.util.player.PlayerManager;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener class for players
 */
public class PlayerListener {

    /**
     * Registers player listeners
     */
    public static void registerEvents() {
        onJoin();
        onQuit();
    }

    /**
     * Listens player join
     */
    private static void onJoin() {
        BEvent.registerEvent(PlayerJoinEvent.class)
                .filter(event -> !PlayerManager.isRegistered(event.getPlayer().getUniqueId()))
                .consume(event -> PlayerManager.create(event.getPlayer().getUniqueId()));
    }

    /**
     * Listens player quit
     */
    private static void onQuit() {
        BEvent.registerEvent(PlayerQuitEvent.class)
                .filter(event -> PlayerManager.isRegistered(event.getPlayer().getUniqueId()))
                .consume(event -> {
                    PlayerManager.save();
                    PlayerManager.delete(event.getPlayer().getUniqueId());
                    CrateManager.stream().forEach(crate -> crate.structure().stream()
                                    .forEach(structure -> structure.getRenderer().removeShownViewers(event.getPlayer().getUniqueId())));
                });
    }

}
