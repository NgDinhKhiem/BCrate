package fr.bobinho.bcrate.util.key.ux;

import fr.bobinho.bcrate.api.item.BItemBuilder;
import fr.bobinho.bcrate.api.menu.BMenu;
import fr.bobinho.bcrate.api.validate.BValidate;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Class representing the key deposit menu
 */
public class KeyDepositMenu extends BMenu {

    private final int depositAmount;

    /**
     * Creates a new key menu
     */
    public KeyDepositMenu(int depositAmount) {
        super(27, "§8Confirm");

        this.depositAmount = depositAmount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openInventory(@Nonnull Player player) {
        BValidate.notNull(player);

        getInventory().clear();

        for (int i = 0; i < 27; i++) {
            setItem(i, new BItemBuilder(Material.GRAY_STAINED_GLASS_PANE).build());
        }

        setItem(12, new BItemBuilder(Material.LIME_WOOL).name("§aConfirm").lore("§7Amount: §6" + depositAmount).build());
        setItem(14, new BItemBuilder(Material.RED_WOOL).name("§cCancel").build());

        player.openInventory(getInventory());
    }

}