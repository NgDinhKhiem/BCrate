package fr.bobinho.bcrate.util.crate.ux;

import fr.bobinho.bcrate.api.color.BColor;
import fr.bobinho.bcrate.api.item.BItemBuilder;
import fr.bobinho.bcrate.api.menu.BMenu;
import fr.bobinho.bcrate.api.notification.BPlaceHolder;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.crate.Crate;
import fr.bobinho.bcrate.util.crate.edit.size.notification.SizeNotification;
import fr.bobinho.bcrate.util.crate.notification.CrateNotification;
import fr.bobinho.bcrate.wrapper.ReadOnlyMonoValuedAttribute;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Class representing the crate edit menu
 */
public class CrateEditMenu extends BMenu {

    /**
     * Fields
     */
    private final ReadOnlyMonoValuedAttribute<Crate> crate;

    /**
     * Creates a new crate edit menu
     *
     * @param crate the crate
     */
    public CrateEditMenu(@NotNull Crate crate) {
        super(27, CrateNotification.CRATE_EDIT_MENU_NAME.getNotification(new BPlaceHolder("%name%", crate.name().get())));

        this.crate = new ReadOnlyMonoValuedAttribute<>(crate);
    }

    /**
     * Gets the crate wrapper
     *
     * @return the crate wrapper
     */
    public @Nonnull ReadOnlyMonoValuedAttribute<Crate> crate() {
        return crate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openInventory(@NotNull Player player) {
        BValidate.notNull(player);

        getInventory().clear();

        getInventory().setItem(10, crate.get().color().get().getBackground());

        getInventory().setItem(12, crate.get().size().get().getBackground());

        getInventory().setItem(14, crate.get().key().get().item().get());

        getInventory().setItem(16, new BItemBuilder(Material.DIAMOND).name(CrateNotification.CRATE_EDIT_MENU_PRIZES.getNotification()).build());

        player.openInventory(getInventory());
    }

}
