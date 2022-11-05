package fr.bobinho.bcrate.api.renderer;

import fr.bobinho.bcrate.api.location.BLocation;
import fr.bobinho.bcrate.api.validate.BValidate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Bobinho renderer library
 */
public final class BRenderer {

    /**
     * Fields
     */
    private List<UUID> viewers = new ArrayList<>();
    private List<UUID> blacklist = new ArrayList<>();
    private Location location;
    private int radius = -1;
    private final Consumer<List<Player>> showConsumer;
    private final Consumer<List<Player>> hideConsumer;
    private Consumer<BRenderer> emptyConsumer;
    private Consumer<BRenderer> deleteConsumer;
    private Consumer<BRenderer> updateConsumer;
    private final List<UUID> shownViewers = new ArrayList<>();

    /**
     * Creates a new renderer
     *
     * @param viewers      Viewers.
     * @param location     Location.
     * @param showConsumer Show consumer.
     * @param hideConsumer Hide consumer.
     */
    public BRenderer(@Nonnull List<UUID> viewers, @Nonnull Location location, @Nonnull Consumer<List<Player>> showConsumer, @Nonnull Consumer<List<Player>> hideConsumer) {
        BValidate.notNull(viewers);
        BValidate.notNull(location);
        BValidate.notNull(showConsumer);
        BValidate.notNull(hideConsumer);

        this.viewers = viewers;
        this.location = location;
        this.showConsumer = showConsumer;
        this.hideConsumer = hideConsumer;
    }

    /**
     * Creates a new renderer
     *
     * @param location     Location.
     * @param showConsumer Show consumer.
     * @param hideConsumer Hide consumer.
     */
    public BRenderer(@Nonnull Location location, @Nonnull Consumer<List<Player>> showConsumer, @Nonnull Consumer<List<Player>> hideConsumer) {
        BValidate.notNull(location);
        BValidate.notNull(showConsumer);
        BValidate.notNull(hideConsumer);

        this.location = location;
        this.showConsumer = showConsumer;
        this.hideConsumer = hideConsumer;
    }

    /**
     * Gets all shown viewers as players
     *
     * @return all shown viewers as players
     */
    public @Nonnull List<Player> getShownViewersAsPlayer() {
        return shownViewers.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Gets all shown viewers
     *
     * @return all shown viewers
     */
    public @Nonnull List<UUID> getShownViewers() {
        return shownViewers;
    }

    /**
     * Removes player from shown viewers
     *
     * @param uuid the uuid
     */
    public void removeShownViewers(@Nonnull UUID uuid) {
        BValidate.notNull(uuid);

        shownViewers.remove(uuid);
    }

    /**
     * Gets all viewers as players.
     *
     * @return all viewers as players.
     */
    public @Nonnull List<Player> getViewersAsPlayer() {
        return viewers.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Gets all viewers
     *
     * @return all viewers
     */
    public @Nonnull List<UUID> getViewers() {
        return viewers;
    }

    /**
     * Sets the viewers
     *
     * @param viewers the viewers
     */
    public void setViewers(@Nonnull List<UUID> viewers) {
        BValidate.notNull(viewers);

        this.hideConsumer.accept(getViewersAsPlayer());
        this.shownViewers.clear();
        this.viewers = viewers;
    }

    /**
     * Adds the viewer to the list
     *
     * @param player the player
     */
    public void addViewer(@Nonnull Player player) {
        BValidate.notNull(player);

        if (!viewers.contains(player.getUniqueId())) {
            viewers.add(player.getUniqueId());
        }
    }

    /**
     * Adds the viewer to the list
     *
     * @param uuid the uuid
     */
    public void addViewer(@Nonnull UUID uuid) {
        BValidate.notNull(uuid);

        if (!viewers.contains(uuid)) {
            viewers.add(uuid);
        }
    }

    /**
     * Removes the viewer from the list
     *
     * @param player the player
     */
    public void removeViewer(@Nonnull Player player) {
        BValidate.notNull(player);

        viewers.remove(player.getUniqueId());
    }

    /**
     * Removes the viewer from the list
     *
     * @param uuid the uuid
     */
    public void removeViewer(@Nonnull UUID uuid) {
        BValidate.notNull(uuid);

        viewers.remove(uuid);
    }

    /**
     * Gets the blacklist
     *
     * @return the blacklist
     */
    public @Nonnull List<UUID> getBlacklist() {
        return blacklist;
    }

    /**
     * Sets the blacklist
     *
     * @param blacklist the blacklist
     */
    public void setBlacklist(@Nonnull List<UUID> blacklist) {
        BValidate.notNull(blacklist);

        this.blacklist = blacklist;
    }

    /**
     * Adds the player to the blacklist
     *
     * @param player the player
     */
    public void addBlacklist(@Nonnull Player player) {
        BValidate.notNull(player);

        if (!blacklist.contains(player.getUniqueId())) {
            blacklist.add(player.getUniqueId());
        }
    }

    /**
     * Adds the player to the blacklist
     *
     * @param uuid the uuid
     */
    public void addBlacklist(@Nonnull UUID uuid) {
        BValidate.notNull(uuid);

        if (!blacklist.contains(uuid)) {
            blacklist.add(uuid);
        }
    }

    /**
     * Removes the player from the blacklist
     *
     * @param player the player
     */
    public void removeBlacklist(@Nonnull Player player) {
        BValidate.notNull(player);

        blacklist.remove(player.getUniqueId());
    }

    /**
     * Removes a player from the blacklist
     *
     * @param uuid the uuid
     */
    public void removeBlacklist(@Nonnull UUID uuid) {
        BValidate.notNull(uuid);

        blacklist.remove(uuid);
    }

    /**
     * Gets the location
     *
     * @return the location.
     */
    public @Nonnull Location getLocation() {
        return location;
    }

    /**
     * Sets the location
     *
     * @param location the location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Gets the view radius
     *
     * @return the view radius
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Sets the view radius
     *
     * @param radius the radius
     * @return the renderer
     */
    public @Nonnull BRenderer setRadius(int radius) {
        this.radius = radius;

        return this;
    }

    /**
     * Sets the empty consumer
     *
     * @param emptyConsumer the empty consumer
     * @return the renderer
     */
    public @Nonnull BRenderer setEmptyConsumer(@Nonnull Consumer<BRenderer> emptyConsumer) {
        BValidate.notNull(emptyConsumer);

        this.emptyConsumer = emptyConsumer;

        return this;
    }

    /**
     * Sets the update consumer
     *
     * @param updateConsumer the update consumer
     * @return the renderer
     */
    public @Nonnull BRenderer setUpdateConsumer(@Nonnull Consumer<BRenderer> updateConsumer) {
        BValidate.notNull(updateConsumer);

        this.updateConsumer = updateConsumer;

        return this;
    }

    /**
     * Sets the delete consumer
     *
     * @param deleteConsumer Delete consumer
     * @return the renderer
     */
    public @Nonnull BRenderer setDeleteConsumer(@Nonnull Consumer<BRenderer> deleteConsumer) {
        BValidate.notNull(deleteConsumer);

        this.deleteConsumer = deleteConsumer;

        return this;
    }

    /**
     * Checks if player can see the target location
     *
     * @param targetLocation the target location
     * @return true if player can see the target location, false otherwise
     */
    private boolean canSee(@Nonnull Location targetLocation) {
        BValidate.notNull(targetLocation);

        //If radius is configured, use it instead of default one
        if (radius > 0) {
            return BLocation.measure(location, targetLocation) <= radius;
        }

        //Use default configuration
        return BLocation.canSee(location, targetLocation);
    }

    /**
     * Renders the objects
     */
    public void render() {

        //Handle update
        if (updateConsumer != null) {
            updateConsumer.accept(this);
        }

        //Checks shown viewers
        if (!shownViewers.isEmpty()) {

            Collection<UUID> notAbleToSeeViewers = shownViewers.stream()
                    .filter(uuid -> {
                        Player player = Bukkit.getPlayer(uuid);
                        return player == null || !canSee(player.getLocation());
                    }).toList();

            //If viewers are empty, no need to continue
            if (!notAbleToSeeViewers.isEmpty()) {

                //Removes not able to see viewers
                shownViewers.removeAll(notAbleToSeeViewers);

                //Declares target viewers
                List<Player> targetViewers = notAbleToSeeViewers.stream()
                        .map(Bukkit::getPlayer)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                //Hide consumer
                hideConsumer.accept(targetViewers);
            }
        }

        //Shows viewers
        if (!viewers.isEmpty()) {

            //Stream base target viewers
            List<Player> targetViewersStream = viewers.stream()
                    .filter(uuid -> !shownViewers.contains(uuid))
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull).toList();

            //If all viewers are offline, call empty consumer.
            if (getViewersAsPlayer().isEmpty()) {

                if (emptyConsumer != null) {
                    emptyConsumer.accept(this);
                }
                return;
            }

            //If there is no target, no need to continue
            if (targetViewersStream.isEmpty()) {
                return;
            }

            //Filters target viewers
            List<Player> targetViewers = targetViewersStream.stream()
                    .filter(player -> !blacklist.contains(player.getUniqueId()))
                    .filter(player -> canSee(player.getLocation()))
                    .collect(Collectors.toList());

            //If viewers are empty, no need to continue
            if (targetViewers.isEmpty()) {
                return;
            }

            //Clear shown viewers and add all new target viewers
            targetViewers.forEach(player -> shownViewers.add(player.getUniqueId()));

            //Show consumer
            showConsumer.accept(targetViewers);
            return;
        }

        //Declare target viewers
        List<Player> targetViewers = Bukkit.getOnlinePlayers().stream()
                .filter(player -> !blacklist.contains(player.getUniqueId()))
                .filter(player -> !shownViewers.contains(player.getUniqueId()))
                .filter(player -> canSee(player.getLocation()))
                .collect(Collectors.toList());

        //If viewers are empty, no need to continue
        if (targetViewers.isEmpty()) {
            return;
        }

        //Clear shown viewers and add all new target viewers
        targetViewers.forEach(player -> shownViewers.add(player.getUniqueId()));

        //Show consumer
        showConsumer.accept(targetViewers);
    }

    /**
     * Deletes
     */
    public void delete() {
        if (deleteConsumer != null) {
            deleteConsumer.accept(this);
        }
    }

}