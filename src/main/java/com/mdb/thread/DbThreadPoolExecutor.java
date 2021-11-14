package com.mdb.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DbThreadPoolExecutor {

    final String name;
    final int core;
    final ThreadPoolExecutor[] threads;

    public DbThreadPoolExecutor(String name, int core) {
        this.name = name;
        this.core = core;
        threads = new ThreadPoolExecutor[this.core];
        ThreadFactory threadFactory = new NamedThreadFactory(this.name);
        for (int i = 0; i < this.core; i++) {

            ThreadPoolExecutor t = new ThreadPoolExecutor(1, 1,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    threadFactory);

            this.threads[i] = t;
        }
    }

    public void execute(Runnable r, int index) {
        this.threads[index % this.core].execute(r);
    }

    public static class NamedThreadFactory implements ThreadFactory {
        private final String _name;
        private final AtomicInteger _threadNumber = new AtomicInteger(1);
        private final ThreadGroup _group;

        public NamedThreadFactory(String name) {
            _name = name;
            _group = new ThreadGroup(_name);
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(_group, r, _name + "-" + _threadNumber.getAndIncrement());
            return t;
        }

        public ThreadGroup getGroup() {
            return _group;
        }
    }
}
