package fr.bobinho.bcrate.util.crate.ux;

import fr.bobinho.bcrate.api.item.BItemBuilder;
import fr.bobinho.bcrate.api.menu.BMenu;
import fr.bobinho.bcrate.api.notification.BPlaceHolder;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.crate.Crate;
import fr.bobinho.bcrate.util.crate.notification.CrateNotification;
import fr.bobinho.bcrate.wrapper.ReadOnlyMonoValuedAttribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Class representing the crate structure menu
 */
public class CrateStructureMenu extends BMenu {

    /**
     * Fields
     */
    private final ReadOnlyMonoValuedAttribute<Crate> crate;

    /**
     * Creates a new crate structure menu
     *
     * @param crate the crate
     */
    public CrateStructureMenu(@NotNull Crate crate) {
        super(27, CrateNotification.CRATE_STRUCTURE_MENU_NAME.getNotification(new BPlaceHolder("%name%", crate.name().get())));

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

        getInventory().setItem(10, new BItemBuilder(crate.get().skin().get(0)).name(CrateNotification.CRATE_SKIN_CLOSE.getNotification()).build());

        getInventory().setItem(12, new BItemBuilder(crate.get().skin().get(1)).name(CrateNotification.CRATE_SKIN_OPEN.getNotification()).build());

        getInventory().setItem(14, new BItemBuilder(crate.get().skin().get(2)).name(CrateNotification.CRATE_SKIN_LEFT.getNotification()).build());

        getInventory().setItem(16, new BItemBuilder(crate.get().skin().get(3)).name(CrateNotification.CRATE_SKIN_RIGHT.getNotification()).build());

        player.openInventory(getInventory());
    }

}