package fr.bobinho.bcrate.util.key.notification;

import fr.bobinho.bcrate.BCrateCore;
import fr.bobinho.bcrate.api.color.BColor;
import fr.bobinho.bcrate.api.notification.BNotification;
import fr.bobinho.bcrate.api.notification.BPlaceHolder;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Enum of key notifications
 */
public enum KeyNotification implements BNotification {
    KEY_ASK_WITHDRAW,
    KEY_WITHDRAW,
    KEY_PLAYER_NOT_ENOUGH_TO_WITHDRAW,
    KEY_ASK_DEPOSIT,
    KEY_DEPOSIT,
    KEY_PLAYER_NOT_ENOUGH_TO_DEPOSIT,
    KEY_MENU_NAME,
    KEY_MENU_LORE,
    KEY_ALREADY_REGISTERED,
    KEY_CREATED,
    KEY_NOT_REGISTERED,
    KEY_DELETED;

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