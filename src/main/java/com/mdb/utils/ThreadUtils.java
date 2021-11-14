package com.mdb.utils;


import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.*;


public class ThreadUtils {

    private static final class RunnableWrapper implements Runnable {
        private final Runnable _r;

        public RunnableWrapper(final Runnable r) {
            _r = r;
        }

        @Override
        public final void run() {
            try {
                _r.run();
            } catch (final Throwable e) {
                final Thread t = Thread.currentThread();
                final UncaughtExceptionHandler h = t.getUncaughtExceptionHandler();
                if (h != null) {
                    h.uncaughtException(t, e);
                }
            }
        }
    }

    protected ScheduledThreadPoolExecutor _generalScheduledThreadPool;
    protected ScheduledThreadPoolExecutor _eventScheduledThreadPool;


    private final ThreadPoolExecutor _generalThreadPool;//thread no-limit

    private boolean _shutdown;

    public static ThreadUtils getInstance() {
        return SingletonHolder._instance;
    }


    protected ThreadUtils() {
        _generalThreadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        scheduleGeneralAtFixedRate(new PurgeTask(), 10, 5, TimeUnit.MINUTES);
    }


    /**
     * Schedules a general task to be executed after the given delay.INIT
     *
     * @param task  the task to execute
     * @param delay the delay in the given time unit
     * @param unit  the time unit of the delay parameter
     * @return a ScheduledFuture representing pending completion of the task,
     * and whose get() method will throw an exception upon cancellation
     */
    public ScheduledFuture<?> scheduleGeneral(Runnable task, long delay, TimeUnit unit) {
        try {
            return _generalScheduledThreadPool.schedule(new RunnableWrapper(task), delay, unit);
        } catch (RejectedExecutionException e) {
            return null; /* shutdown, ignore */
        }
    }

    /**
     * Schedules a general task to be executed after the given delay.
     *
     * @param task  the task to execute
     * @param delay the delay in milliseconds
     * @return a ScheduledFuture representing pending completion of the task,
     * and whose get() method will throw an exception upon cancellation
     */
    public ScheduledFuture<?> scheduleGeneral(Runnable task, long delay) {
        return scheduleGeneral(task, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Schedules a general task to be executed at fixed rate.
     *
     * @param task         the task to execute
     * @param initialDelay the initial delay in the given time unit
     * @param period       the period between executions in the given time unit
     * @param unit         the time unit of the initialDelay and period parameters
     * @return a ScheduledFuture representing pending completion of the task,
     * and whose get() method will throw an exception upon cancellation
     */
    public ScheduledFuture<?> scheduleGeneralAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        try {
            return _generalScheduledThreadPool.scheduleAtFixedRate(new RunnableWrapper(task), initialDelay, period,
                    unit);
        } catch (RejectedExecutionException e) {
            return null; /* shutdown, ignore */
        }
    }

    /**
     * Schedules a event task to be executed after the given delay.
     *
     * @param task  the task to execute
     * @param delay the delay in the given time unit
     * @param unit  the time unit of the delay parameter
     * @return a ScheduledFuture representing pending completion of the task,
     * and whose get() method will throw an exception upon cancellation
     */
    public ScheduledFuture<?> scheduleEvent(Runnable task, long delay, TimeUnit unit) {
        try {
            return _eventScheduledThreadPool.schedule(new RunnableWrapper(task), delay, unit);
        } catch (RejectedExecutionException e) {
            return null; /* shutdown, ignore */
        }
    }

    /**
     * Schedules a event task to be executed after the given delay.
     *
     * @param task  the task to execute
     * @param delay the delay in milliseconds
     * @return a ScheduledFuture representing pending completion of the task,
     * and whose get() method will throw an exception upon cancellation
     */
    public ScheduledFuture<?> scheduleEvent(Runnable task, long delay) {
        return scheduleEvent(task, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Schedules a event task to be executed at fixed rate.
     *
     * @param task         the task to execute
     * @param initialDelay the initial delay in the given time unit
     * @param period       the period between executions in the given time unit
     * @param unit         the time unit of the initialDelay and period parameters
     * @return a ScheduledFuture representing pending completion of the task,
     * and whose get() method will throw an exception upon cancellation
     */
    public ScheduledFuture<?> scheduleEventAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        try {
            return _eventScheduledThreadPool.scheduleAtFixedRate(new RunnableWrapper(task), initialDelay, period, unit);
        } catch (RejectedExecutionException e) {
            return null; /* shutdown, ignore */
        }
    }

    /**
     * Schedules a general task to be executed at fixed rate.
     *
     * @param task         the task to execute
     * @param initialDelay the initial delay in milliseconds
     * @param period       the period between executions in milliseconds
     * @return a ScheduledFuture representing pending completion of the task,
     * and whose get() method will throw an exception upon cancellation
     */
    public ScheduledFuture<?> scheduleGeneralAtFixedRate(Runnable task, long initialDelay, long period) {
        return scheduleGeneralAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
    }


    /**
     * Executes a general task sometime in future in another thread.
     *
     * @param task the task to execute
     */
    public void executeGeneral(Runnable task) {
        try {
            _generalThreadPool.execute(new RunnableWrapper(task));
        } catch (RejectedExecutionException e) {
            /* shutdown, ignore */
        }
    }

    public void shutdown() {
        _shutdown = true;
        try {

            _generalScheduledThreadPool.awaitTermination(1, TimeUnit.SECONDS);
            _generalThreadPool.awaitTermination(1, TimeUnit.SECONDS);

            _generalScheduledThreadPool.shutdown();
            _generalThreadPool.shutdown();

        } catch (InterruptedException e) {
        }
    }

    public boolean isShutdown() {
        return _shutdown;
    }

    public void purge() {
        _generalScheduledThreadPool.purge();
        _eventScheduledThreadPool.purge();
        _generalThreadPool.purge();
    }


    protected class PurgeTask implements Runnable {
        @Override
        public void run() {
            _generalScheduledThreadPool.purge();
            _eventScheduledThreadPool.purge();
        }
    }

    private static class SingletonHolder {
        protected static final ThreadUtils _instance = new ThreadUtils();
    }
}