package fr.bobinho.bcrate.util.prize.listener;

import fr.bobinho.bcrate.api.event.BEvent;
import fr.bobinho.bcrate.api.notification.BPlaceHolder;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.crate.notification.CrateNotification;
import fr.bobinho.bcrate.util.prize.Prize;
import fr.bobinho.bcrate.util.prize.PrizeManager;
import fr.bobinho.bcrate.util.prize.notification.PrizeNotification;
import fr.bobinho.bcrate.util.prize.ux.PrizeEditMenu;
import fr.bobinho.bcrate.util.tag.TagManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import javax.annotation.Nonnull;

public class PrizeListener {


    /**
     * Registers crate listeners
     */
    public static void registerEvents() {
        onInteractWithPrizeMenu();
        onChangePrizeTag();
    }

    /**
     * Listens interactions with prize menus
     */
    private static void onInteractWithPrizeMenu() {
        BEvent.registerEvent(InventoryDragEvent.class)
                .filter(event -> event.getInventory().getHolder() instanceof PrizeEditMenu)
                .consume(event -> event.setCancelled(true));

        BEvent.registerEvent(InventoryClickEvent.class)
                .filter(event -> event.getClickedInventory() != null)
                .filter(event -> event.getClickedInventory().getHolder() instanceof PrizeEditMenu)
                .consume(event -> event.setCancelled(true));
    }

    /**
     * Listens change prize tag
     */
    private static void onChangePrizeTag() {
        BEvent.registerEvent(InventoryClickEvent.class)
                .filter(event -> event.getClickedInventory() != null)
                .filter(event -> event.getClickedInventory().getHolder() instanceof PrizeEditMenu)
                .filter(event -> event.getCurrentItem() != null)
                .filter(event -> event.getCurrentItem().getItemMeta() != null)
                .consume(event -> {
                    event.setCancelled(true);

                    if (event.getClick() != ClickType.LEFT) {
                        return;
                    }

                    //Switch tag selection
                    TagManager.get(event.getCurrentItem()).ifPresent(tag -> {
                        Prize prize = ((PrizeEditMenu) event.getClickedInventory().getHolder()).prize().get();

                        PrizeManager.switchTagSelection(prize, tag);
                        event.getClickedInventory().setItem(event.getSlot(), tag.getBackground(prize));
                    });
                });
    }

    /**
     * Asks the prize chance
     *
     * @param player the player
     * @param prize  the prize
     */
    public static void askPrizeChance(@Nonnull Player player, @Nonnull Prize prize) {
        BValidate.notNull(player);
        BValidate.notNull(prize);

        player.closeInventory();
        player.sendMessage(PrizeNotification.PRIZE_ASK_CHANCE.getNotification());

        BEvent.registerEvent(AsyncPlayerChatEvent.class)
                .limit(1)
                .filter(event -> event.getPlayer().equals(player))
                .consume(event -> {
                    event.setCancelled(true);

                    //Checks if the value is valid
                    if (!event.getMessage().matches("^[0-9]\\d*(.[0-9]+)?$")) {
                        player.sendMessage(CrateNotification.UTIL_NOT_A_NUMBER.getNotification(new BPlaceHolder("%number%", event.getMessage())));
                        return;
                    }

                    double chance = Double.parseDouble(event.getMessage());

                    //Checks if the chance is valid
                    if (chance < 1 || chance > 100) {
                        player.sendMessage(PrizeNotification.PRIZE_INVALID_CHANCE.getNotification(new BPlaceHolder("%chance%", String.valueOf(chance))));
                        return;
                    }

                    //Changes chance
                    PrizeManager.changeChance(prize, chance);

                    //Messages
                    player.sendMessage(PrizeNotification.PRIZE_CHANCE_CHANGED.getNotification(new BPlaceHolder("%chance%", String.valueOf(chance))));
                });
    }

}
