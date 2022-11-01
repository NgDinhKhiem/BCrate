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
    UTIL_NOT_A_NUMBER;

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
        String notification = BColor.color(BCrateCore.getLangSetting().getString(name()));

        for (BPlaceHolder placeHolder : placeholders) {
            notification = notification.replaceAll(placeHolder.getOldValue(), placeHolder.getReplacement());
        }

        return notification;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nonnull List<String> getNotifications(@Nonnull BPlaceHolder... placeholders) {
        List<String> notifications = BCrateCore.getLangSetting().getConfigurationSection(name()).stream().toList();

        for (String notification : notifications) {
            for (BPlaceHolder placeHolder : placeholders) {
                notification = notification.replaceAll(placeHolder.getOldValue(), placeHolder.getReplacement());
            }
        }

        return notifications;
    }

}