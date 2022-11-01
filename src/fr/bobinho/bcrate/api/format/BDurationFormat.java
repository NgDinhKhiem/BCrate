package fr.bobinho.bcrate.api.format;

import javax.annotation.Nonnull;

/**
 * Bobinho duration format library
 */
public final class BDurationFormat {

    /**
     * Unitilizable constructor (utility class)
     */
    private BDurationFormat() {
    }

    /**
     * Gets a duration string in minutes - seconds format
     *
     * @param durationInSecond the duration in second to format
     * @return the formatted string in minutes - seconds format
     */
    public static @Nonnull String getAsMinuteSecondFormat(long durationInSecond) {
        return ((int) durationInSecond / 60) + ":" + (durationInSecond % 60 < 10 ? "0" : "") + (durationInSecond % 60);
    }

    /**
     * Gets a duration string in seconds format
     *
     * @param durationInSecond the duration in second to format
     * @return the formatted string in seconds format
     */
    public static @Nonnull String getAsSecondFormat(long durationInSecond) {
        return durationInSecond + "s ";
    }

}
