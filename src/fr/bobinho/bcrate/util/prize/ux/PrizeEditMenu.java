package fr.bobinho.bcrate.util.prize.ux;

import fr.bobinho.bcrate.api.menu.BMenu;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.prize.Prize;
import fr.bobinho.bcrate.util.prize.notification.PrizeNotification;
import fr.bobinho.bcrate.util.tag.TagManager;
import fr.bobinho.bcrate.wrapper.MonoValuedAttribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Class representing the prize edit menu
 */
public class PrizeEditMenu extends BMenu {

    /**
     * Fields
     */
    private final MonoValuedAttribute<Prize> prize;

    /**
     * Creates a new prize edit menu
     *
     * @param prize the prize
     */
    public PrizeEditMenu(@NotNull Prize prize) {
        super(Math.max(9, (int) Math.ceil(TagManager.stream().count() / 9.0D)), PrizeNotification.PRIZE_MENU_NAME.getNotification());

        this.prize = new MonoValuedAttribute<>(prize);
    }

    /**
     * Gets the prize wrapper
     *
     * @return the prize wrapper
     */
    public @Nonnull MonoValuedAttribute<Prize> prize() {
        return prize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openInventory(@NotNull Player player) {
        BValidate.notNull(player);

        getInventory().clear();

        TagManager.indexedStream().forEach((tag, i) -> setItem(i, tag.getBackground(prize.get())));

        player.openInventory(getInventory());
    }

}
