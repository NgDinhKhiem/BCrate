package fr.bobinho.bcrate.util.prize.listener;

import fr.bobinho.bcrate.api.event.BEvent;
import fr.bobinho.bcrate.util.prize.Prize;
import fr.bobinho.bcrate.util.prize.PrizeManager;
import fr.bobinho.bcrate.util.prize.ux.PrizeEditMenu;
import fr.bobinho.bcrate.util.tag.TagManager;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

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

}
