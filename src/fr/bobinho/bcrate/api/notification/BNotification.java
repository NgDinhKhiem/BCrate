package fr.bobinho.bcrate.api.notification;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Bobinho notification library
 */
public interface BNotification {

    /**
     * Gets the text of the notification
     *
     * @return the text of the notification
     */
    String getNotification();

    /**
     * Gets the text of the notification after modification via placeholders
     *
     * @param placeholders all placeholders
     * @return the text of the notification after modification via placeholders
     */
    String getNotification(@Nonnull BPlaceHolder... placeholders);

    /**
     * Gets the text list of the notification after modification via placeholders
     *
     * @param placeholders all placeholders
     * @return the text of the notification after modification via placeholders
     */
    List<String> getNotifications(@Nonnull BPlaceHolder... placeholders);

}
