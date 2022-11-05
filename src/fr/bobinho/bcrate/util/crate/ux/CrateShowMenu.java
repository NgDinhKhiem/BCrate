package fr.bobinho.bcrate.util.crate.ux;

import fr.bobinho.bcrate.api.menu.BMenu;
import fr.bobinho.bcrate.api.notification.BPlaceHolder;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.crate.Crate;
import fr.bobinho.bcrate.util.crate.notification.CrateNotification;
import fr.bobinho.bcrate.wrapper.ReadOnlyMonoValuedAttribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.stream.IntStream;

/**
 * Class representing the crate show menu
 */
public class CrateShowMenu extends BMenu {

    /**
     * Fields
     */
    private final ReadOnlyMonoValuedAttribute<Crate> crate;

    /**
     * Creates a new crate show menu
     *
     * @param crate the crate
     */
    public CrateShowMenu(@NotNull Crate crate) {
        super(crate.size().get().getDimension(), CrateNotification.CRATE_SHOW_MENU_NAME.getNotification(new BPlaceHolder("%name%", crate.name().get())));

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

        IntStream.range(0, getInventory().getSize()).forEach(i -> getInventory().setItem(i, crate.get().color().get().getBackground()));
        crate.get().prizes().get().forEach(prize -> setItem(prize.slot().get(), prize.getBackground(crate.get())));

        player.openInventory(getInventory());
    }

}
