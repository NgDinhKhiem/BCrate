package fr.bobinho.bcrate.util.prize.ux;

import fr.bobinho.bcrate.api.item.BItemBuilder;
import fr.bobinho.bcrate.api.menu.BMenu;
import fr.bobinho.bcrate.api.validate.BValidate;
import fr.bobinho.bcrate.util.prize.Prize;
import fr.bobinho.bcrate.util.prize.notification.PrizeNotification;
import fr.bobinho.bcrate.util.tag.TagManager;
import fr.bobinho.bcrate.wrapper.MonoValuedAttribute;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.stream.IntStream;

/**
 * Class representing the prize skin menu
 */
public class PrizeSkinMenu extends BMenu {

    /**
     * Fields
     */
    private final MonoValuedAttribute<Prize> prize;

    /**
     * Creates a new prize skin menu
     *
     * @param prize the prize
     */
    public PrizeSkinMenu(@NotNull Prize prize) {
        super(27, PrizeNotification.PRIZE_SKIN_MENU_NAME.getNotification());

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

        IntStream.range(0, 27).forEach(i -> setItem(i, new BItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name("").build()));
        setItem(13, prize.get().skin().get());

        player.openInventory(getInventory());
    }

}