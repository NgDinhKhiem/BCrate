package fr.bobinho.bcrate.util.tag.notification;

import fr.bobinho.bcrate.BCrateCore;
import fr.bobinho.bcrate.api.color.BColor;
import fr.bobinho.bcrate.api.notification.BNotification;
import fr.bobinho.bcrate.api.notification.BPlaceHolder;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Enum of tag notifications
 */
public enum TagNotification implements BNotification {
    TAG_SELECTED,
    TAG_NOT_SELECTED,
    TAG_ALREADY_REGISTERED,
    TAG_CREATED,
    TAG_NOT_REGISTERED,
    TAG_DELETED;

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