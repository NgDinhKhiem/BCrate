package fr.bobinho.bcrate.util.key.ux;

import fr.bobinho.bcrate.api.item.BItemBuilder;
import fr.bobinho.bcrate.api.menu.BMenu;
import fr.bobinho.bcrate.api.notification.BPlaceHolder;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.key.KeyManager;
import fr.bobinho.bcrate.util.key.notification.KeyNotification;
import fr.bobinho.bcrate.util.player.PlayerManager;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Class representing the key menu
 */
public class KeyMenu extends BMenu {

    /**
     * Creates a new key menu
     */
    public KeyMenu() {
        super(Math.max(9, (int) Math.ceil(KeyManager.stream().count() / 9.0D)), KeyNotification.KEY_MENU_NAME.getNotification());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openInventory(@Nonnull Player player) {
        BValidate.notNull(player);

        getInventory().clear();

        KeyManager.indexedStream().forEach((key, i) -> setItem(i,
                new BItemBuilder(key.item().get())
                        .setLore(List.of(KeyNotification.KEY_MENU_LORE.getNotification(
                                new BPlaceHolder("%amount%", String.valueOf(PlayerManager.getKeyNumberWithdrawable(player.getUniqueId(), key))))))
                        .build()));

        player.openInventory(getInventory());
    }

}
