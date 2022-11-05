package fr.bobinho.bcrate.util.key.ux;

import fr.bobinho.bcrate.api.item.BItemBuilder;
import fr.bobinho.bcrate.api.menu.BMenu;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.key.KeyManager;
import fr.bobinho.bcrate.util.key.notification.KeyNotification;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Class representing the key edit menu
 */
public class KeyEditMenu extends BMenu {

    /**
     * Creates a new key menu
     */
    public KeyEditMenu() {
        super(Math.max(9, (int) Math.ceil(KeyManager.stream().count() / 9.0D)), KeyNotification.KEY_MENU_NAME.getNotification());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openInventory(@Nonnull Player player) {
        BValidate.notNull(player);

        getInventory().clear();

        KeyManager.stream().forEach(key-> setItem(key.slot().get(), new BItemBuilder(key.item().get()).build()));

        player.openInventory(getInventory());
    }

}