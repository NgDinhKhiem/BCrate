package fr.bobinho.bcrate.api.event;

import fr.bobinho.bcrate.api.scheduler.BScheduler;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.EventExecutor;
import fr.bobinho.bcrate.BCrateCore;
import fr.bobinho.bcrate.api.validate.BValidate;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Bobinho event library
 */
public final class BEvent<T extends Event> implements Listener, EventExecutor {

    /**
     * Event enum filters
     */
    public enum Filter {
        IGNORE_CANCELLED,

        IGNORE_DISALLOWED_LOGIN,
        IGNORE_DISALLOWED_PRE_LOGIN,

    }

    /**
     * Fields
     */
    private final Class<T> eventClass;
    private EventPriority priority = EventPriority.NORMAL;
    private final Collection<Filter> filters = new ArrayList<>();
    private final Collection<Function<T, Boolean>> functionFilters = new ArrayList<>();
    private int usageLimit;
    private int usage;
    private int expire;
    private TimeUnit expireUnit;
    private int expireTaskId = -1;
    private Consumer<T> consumer;
    private boolean async;
    private boolean isUnregistered;

    /**
     * Creates a new event
     *
     * @param eventClass the event class
     */
    public BEvent(@Nonnull Class<T> eventClass) {
        BValidate.notNull(eventClass);

        this.eventClass = eventClass;
    }

    /**
     * Creates a new event
     *
     * @param eventClass the event class
     * @param priority   the event priority
     */
    public BEvent(@Nonnull Class<T> eventClass, @Nonnull EventPriority priority) {
        BValidate.notNull(eventClass);
        BValidate.notNull(priority);

        this.eventClass = eventClass;
        this.priority = priority;
    }

    /**
     * Registers an event
     *
     * @param eventClass the bukkit event class
     * @param <T>        the bukkit event
     * @return the event builder
     */
    public static @Nonnull <T extends Event> BEvent<T> registerEvent(@Nonnull Class<T> eventClass) {
        BValidate.notNull(eventClass);

        return new BEvent<>(eventClass);
    }

    /**
     * Registers an event
     *
     * @param eventClass the bukkit event class
     * @param priority   the event priority
     * @param <T>        the bukkit event
     * @return the event builder
     */
    public static @Nonnull <T extends Event> BEvent<T> registerEvent(@Nonnull Class<T> eventClass, @Nonnull EventPriority priority) {
        BValidate.notNull(eventClass);
        BValidate.notNull(priority);

        return new BEvent<>(eventClass, priority);
    }

    /**
     * Sets the bukkit event priority
     *
     * @param priority the event priority
     * @return the event builder
     */
    public @Nonnull BEvent<T> priority(@Nonnull EventPriority priority) {
        BValidate.notNull(priority);

        this.priority = priority;

        return this;
    }

    /**
     * Adds filters to the list
     *
     * @param filters the event filters
     * @return the event builder
     */
    public @Nonnull BEvent<T> filter(@Nonnull Filter... filters) {
        BValidate.notNull(filters);

        for (Filter filter : filters) {
            if (!this.filters.contains(filter)) {
                this.filters.add(filter);
            }
        }

        return this;
    }

    /**
     * Adds a new functional event filter to the list
     *
     * @param functionFilter the functional event filter
     * @return the event builder
     */
    public @Nonnull BEvent<T> filter(@Nonnull Function<T, Boolean> functionFilter) {
        BValidate.notNull(functionFilter);

        if (!this.functionFilters.contains(functionFilter)) {
            this.functionFilters.add(functionFilter);
        }

        return this;
    }

    /**
     * Sets the usage limit
     *
     * @param usageLimit the sage limit
     * @return the event builder
     */
    public @Nonnull BEvent<T> limit(int usageLimit) {
        this.usageLimit = usageLimit;

        return this;
    }

    /**
     * Sets the expire duration and time unit
     *
     * @param expire     the expire duration
     * @param expireUnit the expire time unit
     * @return the event builder
     */
    public @Nonnull BEvent<T> expire(int expire, @Nonnull TimeUnit expireUnit) {
        BValidate.notNull(expire);

        this.expire = expire;
        this.expireUnit = expireUnit;

        return this;
    }

    /**
     * Events the consume action
     *
     * @param consumer the root function
     * @return the event builder
     */
    public @Nonnull BEvent<T> consume(@Nonnull Consumer<T> consumer) {
        BValidate.notNull(consumer);

        this.consumer = consumer;

        //Register events
        this.register();

        return this;
    }

    /**
     * Events the consume action
     *
     * @param consumer the root function
     * @return the event builder
     */
    public @Nonnull BEvent<T> consumeAsync(@Nonnull Consumer<T> consumer) {
        BValidate.notNull(consumer);

        this.consumer = consumer;
        this.async = true;

        //Registers event.
        this.register();

        return this;
    }

    /**
     * Registers the vent as a bukkit listener and event executor
     */
    private void register() {

        //Register bukkit listener and executor
        Bukkit.getPluginManager().registerEvent(eventClass, this, priority, this, BCrateCore.getInstance(), false);

        //Expire handler
        if (expireUnit != null) {
            expireTaskId = BScheduler.syncScheduler().after(expire, expireUnit).run(this::unregister);
        }
    }

    /**
     * Registers the event as a bukkit listener and event executor
     */
    public void unregister() {
        //If it is already unregistered, we do not want to use it
        if (isUnregistered) {
            return;
        }

        //Register bukkit listener and executor
        HandlerList.unregisterAll(this);

        //Sets check
        isUnregistered = true;

        //Task control
        if (expireTaskId != 1)
            Bukkit.getScheduler().cancelTask(expireTaskId);
    }

    /**
     * Bukkit event executor execute override
     *
     * @param listener the event listener
     * @param event    the bukkit event
     */
    @Override
    public void execute(@Nonnull Listener listener, @Nonnull Event event) {
        BValidate.notNull(listener);
        BValidate.notNull(event);

        //If it is already unregistered, we do not want to use it
        if (isUnregistered)
            return;

        //If both event class is not same, no need to continue
        if (!event.getClass().isAssignableFrom(eventClass) || !eventClass.isAssignableFrom(event.getClass()))
            return;

        //Checks cancelled
        if (filters.contains(Filter.IGNORE_CANCELLED)
                && event instanceof Cancellable
                && ((Cancellable) event).isCancelled())
            return;

        //Checks disallowed login
        if (filters.contains(Filter.IGNORE_DISALLOWED_LOGIN)
                && event instanceof PlayerLoginEvent
                && ((PlayerLoginEvent) event).getResult() != PlayerLoginEvent.Result.ALLOWED)
            return;

        //Checks disallowed pre login
        if (filters.contains(Filter.IGNORE_DISALLOWED_PRE_LOGIN)
                && event instanceof AsyncPlayerPreLoginEvent
                && ((AsyncPlayerPreLoginEvent) event).getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED)
            return;

        //If any of the functional filters false, no need to continue
        if (functionFilters.stream().anyMatch(filter -> !filter.apply((T) event))) {
            return;
        }

        //Checks limit
        if (usageLimit != 0 && usage++ >= usageLimit) {
            unregister();
            return;
        }

        //Consume event
        if (async) {
            BScheduler.asyncScheduler().run(() -> consumer.accept((T) event));
        } else {
            consumer.accept((T) event);
        }
    }

}
