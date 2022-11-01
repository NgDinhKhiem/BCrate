package fr.bobinho.bcrate.api.scheduler;

import fr.bobinho.bcrate.BCrateCore;
import fr.bobinho.bcrate.api.validate.BValidate;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Bobinho scheduler library
 */
public final class BScheduler {

    /**
     * Fields
     */
    private final Type type;
    private int delay;
    private TimeUnit delayType;
    private int repeatingDelay;
    private TimeUnit repeatingDelayType;
    private Runnable cachedRunnable;
    private int bukkitTaskId = -1;
    private boolean shouldWait = false;
    /**
     * Creates scheduler
     *
     * @param type the type
     */
    public BScheduler(@Nonnull Type type) {
        BValidate.notNull(type);

        //Root construction
        this.type = type;
    }

    /**
     * Creates sync scheduler builder.
     *
     * @return the sync scheduler builder.
     */
    public @Nonnull
    static BScheduler syncScheduler() {
        return new BScheduler(BScheduler.Type.SYNC);
    }

    /**
     * Creates async scheduler builder
     *
     * @return the async scheduler builder
     */
    public @Nonnull
    static BScheduler asyncScheduler() {
        return new BScheduler(BScheduler.Type.ASYNC);
    }

    /**
     * Gets scheduler delay
     *
     * @return the scheduler delay
     */
    public long getDelay() {
        return delayType == null ? 0 : Math.max(delayType.toMillis(delay) / 50, 0);
    }

    /**
     * Gets scheduler repeating delay
     *
     * @return the scheduler repeating delay
     */
    public long getRepeatingDelay() {
        return repeatingDelayType == null ? 0 : Math.max(repeatingDelayType.toMillis(repeatingDelay) / 50, 0);
    }

    /**
     * Runs scheduler after declared time
     *
     * @param delay the scheduler after delay
     * @return the scheduler builder
     */
    public @Nonnull BScheduler after(int delay) {
        this.delay = delay * 50;
        this.delayType = TimeUnit.MILLISECONDS;

        return this;
    }

    /**
     * Runs scheduler after declared time
     *
     * @param delay     the scheduler after delay
     * @param delayType the scheduler after delay type
     * @return the scheduler builder
     */
    public @Nonnull BScheduler after(int delay, @Nonnull TimeUnit delayType) {
        BValidate.notNull(delayType);

        this.delay = delay;
        this.delayType = delayType;

        return this;
    }

    /**
     * Runs scheduler every declared time
     *
     * @param repeatingDelay the scheduler repeating time
     * @return the scheduler builder
     */
    public @Nonnull BScheduler every(int repeatingDelay) {
        this.repeatingDelay = repeatingDelay * 50;
        this.repeatingDelayType = TimeUnit.MILLISECONDS;

        return this;
    }

    /**
     * Runs scheduler every declared time
     *
     * @param repeatingDelay     the scheduler repeating time
     * @param repeatingDelayType the scheduler repeating time type
     * @return the scheduler builder
     */
    public @Nonnull BScheduler every(int repeatingDelay, @Nonnull TimeUnit repeatingDelayType) {
        BValidate.notNull(repeatingDelayType);

        this.repeatingDelay = repeatingDelay;
        this.repeatingDelayType = repeatingDelayType;

        return this;
    }

    /**
     * Gets cached runnable
     *
     * @return the runnable
     */
    public Runnable getCachedRunnable() {
        return cachedRunnable;
    }

    /**
     * Sets cached runnable
     *
     * @param cachedRunnable the runnable
     * @return the scheduler builder
     */
    public @Nonnull BScheduler setCachedRunnable(@Nonnull Runnable cachedRunnable) {
        BValidate.notNull(cachedRunnable);

        this.cachedRunnable = cachedRunnable;

        return this;
    }

    /**
     * Waits task to complete
     *
     * @return the scheduler builder
     */
    public @Nonnull BScheduler block() {
        shouldWait = true;

        return this;
    }

    /**
     * Runs cached scheduler
     *
     * @return the bukkit task id
     */
    public int runCached() {
        return run(cachedRunnable);
    }

    /**
     * If there is an ongoing task, it will stop it
     */
    public void stop() {
        if (bukkitTaskId == -1) {
            return;
        }

        Bukkit.getScheduler().cancelTask(bukkitTaskId);
    }

    /**
     * Runs configured scheduler
     *
     * @param runnable the runnable
     * @return the bukkit task id.
     */
    public synchronized int run(@Nonnull Runnable runnable) throws IllegalArgumentException {
        BValidate.notNull(runnable);

        if (type == Type.SYNC) {
            if (getRepeatingDelay() != 0) {
                bukkitTaskId = Bukkit.getScheduler().runTaskTimer(BCrateCore.getInstance(), runnable, getDelay(), getRepeatingDelay()).getTaskId();

            } else if (getDelay() != 0) {
                bukkitTaskId = Bukkit.getScheduler().runTaskLater(BCrateCore.getInstance(), runnable, getDelay()).getTaskId();

            } else {
                bukkitTaskId = Bukkit.getScheduler().runTask(BCrateCore.getInstance(), runnable).getTaskId();
            }

        } else {
            if (getRepeatingDelay() != 0) {
                bukkitTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(BCrateCore.getInstance(), runnable, getDelay(), getRepeatingDelay()).getTaskId();
            } else if (getDelay() != 0) {
                bukkitTaskId = Bukkit.getScheduler().runTaskLaterAsynchronously(BCrateCore.getInstance(), runnable, getDelay()).getTaskId();
            } else {
                bukkitTaskId = Bukkit.getScheduler().runTaskAsynchronously(BCrateCore.getInstance(), runnable).getTaskId();
            }
        }

        //Waits task to complete
        while (shouldWait) {
            if (!Bukkit.getScheduler().isCurrentlyRunning(bukkitTaskId) && !Bukkit.getScheduler().isQueued(bukkitTaskId)) {
                break;
            }
        }

        return bukkitTaskId;
    }

    /**
     * Runs configured scheduler
     *
     * @param task the bukkit task
     */
    public synchronized void run(@Nonnull Consumer<BukkitTask> task) throws IllegalArgumentException {
        BValidate.notNull(task);

        if (type == Type.SYNC) {
            if (getRepeatingDelay() != 0) {
                Bukkit.getScheduler().runTaskTimer(BCrateCore.getInstance(), task, getDelay(), getRepeatingDelay());
            } else if (getDelay() != 0) {
                Bukkit.getScheduler().runTaskLater(BCrateCore.getInstance(), task, getDelay());
            } else {
                Bukkit.getScheduler().runTask(BCrateCore.getInstance(), task);
            }

        } else {
            if (getRepeatingDelay() != 0) {
                Bukkit.getScheduler().runTaskTimerAsynchronously(BCrateCore.getInstance(), task, getDelay(), getRepeatingDelay());
            } else if (getDelay() != 0) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(BCrateCore.getInstance(), task, getDelay());
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(BCrateCore.getInstance(), task);
            }
        }
    }

    /**
     * Scheduler types
     */
    private enum Type {
        SYNC,
        ASYNC
    }

}
