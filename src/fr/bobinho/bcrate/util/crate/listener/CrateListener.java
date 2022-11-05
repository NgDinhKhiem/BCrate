package fr.bobinho.bcrate.util.crate.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import fr.bobinho.bcrate.BCrateCore;
import fr.bobinho.bcrate.api.event.BEvent;
import fr.bobinho.bcrate.api.notification.BPlaceHolder;
import fr.bobinho.bcrate.util.crate.Crate;
import fr.bobinho.bcrate.util.crate.CrateManager;
import fr.bobinho.bcrate.util.crate.edit.color.Color;
import fr.bobinho.bcrate.util.crate.edit.size.Size;
import fr.bobinho.bcrate.util.crate.notification.CrateNotification;
import fr.bobinho.bcrate.util.crate.ux.CrateEditMenu;
import fr.bobinho.bcrate.util.crate.ux.CratePrizeMenu;
import fr.bobinho.bcrate.util.crate.ux.CrateShowMenu;
import fr.bobinho.bcrate.util.key.Key;
import fr.bobinho.bcrate.util.key.KeyManager;
import fr.bobinho.bcrate.util.player.PlayerManager;
import fr.bobinho.bcrate.util.player.notification.PlayerNotification;
import fr.bobinho.bcrate.util.prize.Prize;
import fr.bobinho.bcrate.util.prize.PrizeManager;
import fr.bobinho.bcrate.util.prize.listener.PrizeListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Listener class for crates
 * TODO refactor
 */
public class CrateListener {

    /**
     * Registers crate listeners
     */
    public static void registerEvents() {

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(BCrateCore.getInstance(), PacketType.Play.Client.USE_ENTITY) {
            public void onPacketReceiving(PacketEvent event) {
                CrateManager.isFromStructure(event.getPacket().getIntegers().read(0)).ifPresent(crate -> {

                    //Interacts with the crate (left-click)
                    if (event.getPacket().getEnumEntityUseActions().read(0).getAction() == EnumWrappers.EntityUseAction.ATTACK) {
                        //Checks if the player has the key
                        if (!PlayerManager.hasKey(event.getPlayer().getUniqueId(), crate)) {
                            event.getPlayer().sendMessage(PlayerNotification.PLAYER_HAVENT_KEY.getNotification(new BPlaceHolder("%name%", crate.key().get().name().get())));
                            return;
                        }

                        //Checks if the player has the key
                        if (CrateManager.isEmpty(crate.name().get())) {
                            event.getPlayer().sendMessage(CrateNotification.CRATE_IS_EMPTY.getNotification());
                            return;
                        }

                        //Checks if the crate is already used
                        if (!CrateManager.canPlay(crate.name().get())) {
                            event.getPlayer().sendMessage(CrateNotification.CRATE_ALREADY_USED.getNotification());
                            return;
                        }

                        List<Prize> items = CrateManager.play(crate.name().get());


                        //Checks if the player inventory is full
                        if (!PlayerManager.canPlay(event.getPlayer().getUniqueId(), items)) {
                            event.getPlayer().sendMessage(PlayerNotification.PLAYER_INVENTORY_FULL.getNotification());
                            return;
                        }

                        //Launchs the crate
                        crate.wait(event.getPlayer(), items);

                        //Messages
                        event.getPlayer().sendMessage(CrateNotification.CRATE_LAUNCH.getNotification(new BPlaceHolder("%name%", crate.name().get())));
                    }

                    //Interacts with the crate (right-click)
                    else {
                        BCrateCore.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(BCrateCore.getInstance(), () ->
                                CrateManager.openShowMenu(event.getPlayer(), crate.name().get()));
                    }
                });
            }
        });

        BEvent.registerEvent(InventoryDragEvent.class)
                .filter(event -> event.getInventory().getHolder() instanceof CrateShowMenu)
                .consume(event -> event.setCancelled(true));

        BEvent.registerEvent(InventoryDragEvent.class)
                .filter(event -> event.getInventory().getHolder() instanceof CrateEditMenu)
                .consume(event -> event.setCancelled(true));

        BEvent.registerEvent(InventoryDragEvent.class)
                .filter(event -> event.getInventory().getHolder() instanceof CratePrizeMenu)
                .consume(event -> event.setCancelled(true));

        BEvent.registerEvent(InventoryClickEvent.class)
                .filter(event -> event.getClickedInventory() != null)
                .filter(event -> event.getClickedInventory().getHolder() instanceof CrateShowMenu)
                .consume(event -> event.setCancelled(true));

        BEvent.registerEvent(InventoryClickEvent.class)
                .filter(event -> event.getClickedInventory() != null)
                .filter(event -> event.getClickedInventory().getHolder() instanceof CrateEditMenu)
                .consume(event -> event.setCancelled(true));

        BEvent.registerEvent(InventoryClickEvent.class)
                .filter(event -> event.getClickedInventory() != null)
                .filter(event -> event.getClickedInventory().getHolder() instanceof CratePrizeMenu)
                .consume(event -> event.setCancelled(true));

        BEvent.registerEvent(InventoryClickEvent.class)
                .filter(event -> event.getClickedInventory() != null)
                .filter(event -> event.getClickedInventory().getHolder() instanceof CrateEditMenu)
                .filter(event -> event.getCurrentItem() != null)
                .filter(event -> event.getCurrentItem().getItemMeta() != null)
                .consume(event -> {
                    event.setCancelled(true);

                    if (event.getClick() != ClickType.LEFT) {
                        return;
                    }

                    String name = ((CrateEditMenu) event.getClickedInventory().getHolder()).crate().get().name().get();

                    if (event.getSlot() == 10) {
                        Color color = Color.getNext(Color.getColor(event.getCurrentItem()));

                        event.getClickedInventory().setItem(10, color.getBackground());
                        CrateManager.changeColor(name, color);
                    }

                    if (event.getSlot() == 12) {
                        Size size = Size.getNext(Size.getSize(event.getCurrentItem()));

                        event.getClickedInventory().setItem(12, size.getBackground());
                        CrateManager.resize(name, size);
                    }

                    if (event.getSlot() == 14) {
                        Key key = KeyManager.getNext(KeyManager.get(event.getCurrentItem()).get());

                        event.getClickedInventory().setItem(14, key.item().get());
                        CrateManager.changeKey(name, key);
                    }

                    if (event.getSlot() == 16) {
                        CrateManager.openPrizeMenu((Player) event.getWhoClicked(), name);
                    }
                });

        BEvent.registerEvent(InventoryClickEvent.class)
                .filter(event -> event.getInventory().getHolder() instanceof CratePrizeMenu)
                .filter(InventoryClickEvent::isShiftClick)
                .consume(event -> event.setCancelled(true));

        BEvent.registerEvent(InventoryClickEvent.class)
                .filter(event -> event.getClickedInventory() != null)
                .filter(event -> event.getClickedInventory().getHolder() instanceof CratePrizeMenu)
                .consume(event -> {

                    ItemStack curr = event.getCurrentItem();
                    ItemStack curs = event.getCursor();

                    if (event.getClick() == ClickType.MIDDLE) {
                        event.setCancelled(true);
                        if (curr.getType() == Material.BARRIER) {
                            return;
                        }
                        PrizeManager.get(((CratePrizeMenu) event.getClickedInventory().getHolder()).crate().get(), event.getSlot()).ifPresent(prize ->
                                PrizeManager.openEditMenu((Player) event.getWhoClicked(), prize));
                        return;
                    }
                    if (event.getClickedInventory().getType() == InventoryType.PLAYER && !event.getClick().isShiftClick()) {
                        return;
                    }
                    event.setCancelled(true);
                    if (event.getClickedInventory().getType() == InventoryType.PLAYER && event.getClick().isShiftClick()) {
                        return;
                    }
                    if (event.getClick().isShiftClick() && curr != null) {
                        if (curr.getType() == Material.BARRIER) {
                            return;
                        }
                        event.setCancelled(true);
                        if (event.isLeftClick()) {
                            PrizeManager.get(((CratePrizeMenu) event.getClickedInventory().getHolder()).crate().get(), event.getSlot()).ifPresent(prize ->
                                    PrizeListener.askPrizeChance((Player) event.getWhoClicked(), prize));
                        } else {
                            PrizeManager.get(((CratePrizeMenu) event.getClickedInventory().getHolder()).crate().get(), event.getSlot()).ifPresent(prize -> {
                                PrizeManager.changeRarity(prize);
                                event.getClickedInventory().setItem(event.getSlot(), prize.getEditBackground());
                            });
                        }
                    } else if (onTake(event)) {
                        PrizeManager.get(((CratePrizeMenu) event.getClickedInventory().getHolder()).crate().get(), event.getSlot()).ifPresent(prize -> {
                            event.getWhoClicked().setItemOnCursor(prize.item().get());
                            event.setCurrentItem(new ItemStack(Material.AIR));
                        });

                        CrateManager.removePrize(((CratePrizeMenu) event.getClickedInventory().getHolder()).crate().get(), event.getSlot());
                    } else if (onPlace(event)) {
                        CrateManager.addPrize(curs, ((CratePrizeMenu) event.getClickedInventory().getHolder()).crate().get(), event.getSlot());

                        PrizeManager.get(((CratePrizeMenu) event.getClickedInventory().getHolder()).crate().get(), event.getSlot()).ifPresent(prize -> {
                            event.setCurrentItem(prize.getEditBackground());
                            event.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
                        });
                    }
                });
    }

    private static void onPlace() {
        BEvent.registerEvent(InventoryClickEvent.class)
                .filter(event -> event.getClickedInventory() != null)
                .filter(event -> event.getClickedInventory().getHolder() instanceof CratePrizeMenu)
                .filter(event -> event.getCurrentItem() == null && event.getCursor() != null)
                .consume(event -> {
                    event.setCancelled(true);

                    Crate crate = ((CratePrizeMenu) event.getClickedInventory().getHolder()).crate().get();

                    CrateManager.addPrize(event.getCursor(), crate, event.getSlot());
                    PrizeManager.get(((CratePrizeMenu) event.getClickedInventory().getHolder()).crate().get(), event.getSlot()).ifPresent(prize -> {
                        event.setCurrentItem(prize.getEditBackground());
                        event.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
                    });
                });
    }

    private static boolean onPlace(InventoryClickEvent event) {
        return (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) && event.getCursor() != null && event.getCursor().getType() != Material.AIR;
    }

    private static boolean onTake(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCursor() == null) {
            return false;
        }
        return event.getCurrentItem().getType() != Material.AIR && event.getCursor().getType() == Material.AIR;
    }

}