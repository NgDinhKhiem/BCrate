package fr.bobinho.bcrate.util.crate.notification;

import fr.bobinho.bcrate.BCrateCore;
import fr.bobinho.bcrate.api.color.BColor;
import fr.bobinho.bcrate.api.notification.BNotification;
import fr.bobinho.bcrate.api.notification.BPlaceHolder;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Enum of crate notifications
 */
public enum CrateNotification implements BNotification {
    CRATE_EDIT_MENU_NAME,
    CRATE_EDIT_MENU_PRIZES,
    CRATE_EDIT_MENU_STRUCTURE,
    CRATE_PRIZE_MENU_NAME,
    CRATE_SHOW_MENU_NAME,
    CRATE_ALREADY_REGISTERED,
    CRATE_NO_KEY,
    CRATE_CREATED,
    CRATE_NOT_REGISTERED,
    CRATE_DELETED,
    CRATE_WON,
    CRATE_PRIZE_INFO,
    CRATE_ALREADY_USED,
    CRATE_IS_EMPTY,
    CRATE_LAUNCH,
    CRATE_RELOADED,
    CRATE_PRIZE_INFO_GLOBAL,
    CRATE_STRUCTURE_MENU_NAME,
    CRATE_SKIN_CLOSE,
    CRATE_SKIN_OPEN,
    CRATE_SKIN_LEFT,
    CRATE_SKIN_RIGHT,
    UTIL_NOT_A_NUMBER,
    UTIL_NOT_ONLINE;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNotification() {
        return BColor.color(BCrateCore.getLangSetting().getString(name()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nonnull String getNotification(@Nonnull BPlaceHolder... placeholders) {
        String notification = BCrateCore.getLangSetting().getString(name());

        for (BPlaceHolder placeHolder : placeholders) {
            notification = notification.replaceAll(placeHolder.getOldValue(), placeHolder.getReplacement());
        }

        return BColor.color(notification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nonnull List<String> getNotifications(@Nonnull BPlaceHolder... placeholders) {
        List<String> notifications = BCrateCore.getLangSetting().getStringList(name()).stream().toList();

        return notifications.stream().map(notification -> {
            for (BPlaceHolder placeHolder : placeholders) {
                notification = notification.replaceAll(placeHolder.getOldValue(), placeHolder.getReplacement());
            }

            return BColor.color(notification);
        }).toList();
    }

}