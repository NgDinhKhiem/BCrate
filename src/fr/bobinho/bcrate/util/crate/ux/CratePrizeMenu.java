package fr.bobinho.bcrate.util.crate.ux;

import fr.bobinho.bcrate.api.menu.BMenu;
import fr.bobinho.bcrate.api.notification.BPlaceHolder;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.crate.Crate;
import fr.bobinho.bcrate.util.crate.notification.CrateNotification;
import fr.bobinho.bcrate.wrapper.ReadOnlyMonoValuedAttribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Class representing the crate prize menu
 */
public class CratePrizeMenu extends BMenu {

    /**
     * Fields
     */
    private final ReadOnlyMonoValuedAttribute<Crate> crate;

    /**
     * Creates a new crate prize menu
     *
     * @param crate the crate
     */
    public CratePrizeMenu(@NotNull Crate crate) {
        super(crate.size().get().getDimension(), CrateNotification.CRATE_PRIZE_MENU_NAME.getNotification(new BPlaceHolder("%name%", crate.name().get())));

        this.crate = new ReadOnlyMonoValuedAttribute<>(crate);
    }

    /**
     * Gets the crate wrapper
     *
     * @return the crate wrapper
     */
    public ReadOnlyMonoValuedAttribute<Crate> crate() {
        return crate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openInventory(@NotNull Player player) {
        BValidate.notNull(player);

        getInventory().clear();

        crate.get().prizes().get().forEach(prize -> setItem(prize.slot().get(), prize.getEditBackground()));

        player.openInventory(getInventory());
    }

}
