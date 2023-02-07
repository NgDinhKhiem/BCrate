package fr.bobinho.bcrate.util.player.listener;

import fr.bobinho.bcrate.api.entity.BEntity;
import fr.bobinho.bcrate.api.event.BEvent;
import fr.bobinho.bcrate.util.crate.CrateManager;
import fr.bobinho.bcrate.util.player.PlayerManager;
import fr.bobinho.bcrate.util.prize.Prize;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Listener class for players
 */
public class PlayerListener {

    /**
     * Fields
     */
    private static final Map<UUID, List<Prize>> unrecoveredPrizes = new HashMap<>();

    /**
     * Registers player listeners
     */
    public static void registerEvents() {
        onJoin();
        onQuit();
        onPick();
    }

    /**
     * Listens player join
     */
    private static void onJoin() {
        BEvent.registerEvent(PlayerJoinEvent.class)
                .filter(event -> !PlayerManager.isRegistered(event.getPlayer().getUniqueId()))
                .consume(event -> {
                    Optional.ofNullable(unrecoveredPrizes.get(event.getPlayer().getUniqueId())).ifPresent(prizes -> {
                        event.getPlayer().getInventory().addItem(prizes.stream().map(prize -> prize.item().get()).toArray(ItemStack[]::new));
                        unrecoveredPrizes.remove(event.getPlayer().getUniqueId());
                    });
                    PlayerManager.create(event.getPlayer().getUniqueId());
                    CrateManager.stream().forEach(crate -> crate.structure().stream().forEach(BEntity::render));
                });
    }

    /**
     * Listens player quit
     */
    private static void onQuit() {
        BEvent.registerEvent(PlayerQuitEvent.class)
                .filter(event -> PlayerManager.isRegistered(event.getPlayer().getUniqueId()))
                .consume(event -> {
                    CrateManager.stream()
                            .filter(crate -> crate.metadata().has("open") || crate.metadata().has("waitOpen") && crate.metadata().getNonNull("player").equals(event.getPlayer()))
                            .findFirst()
                            .ifPresent(crate -> unrecoveredPrizes.put(event.getPlayer().getUniqueId(), crate.metadata().getNonNull("prizes")));
                    CrateManager.stream().forEach(crate -> crate.structure().stream()
                            .forEach(structure -> structure.getRenderer().removeShownViewers(event.getPlayer().getUniqueId())));
                });
    }

    /**
     * Listens player pick
     */
    private static void onPick() {
        BEvent.registerEvent(PlayerAttemptPickupItemEvent.class)
                .filter(event -> PlayerManager.isOpeningCrate(event.getPlayer().getUniqueId()))
                .consume(event -> event.setCancelled(true));
    }

}
