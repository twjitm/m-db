package com.mdb.manager;

import com.mdb.entity.MongoTask;
import com.mdb.thread.ThreadManager;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

class MongoSyncManager {

    private final BlockingQueue<MongoTask<Document>> taskQueue = new ArrayBlockingQueue<>(1024, true);
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<WriteModel<Document>>> pool = new ConcurrentHashMap<>(1024);
    private boolean stop;
    private final boolean async;
    private final MongoCollectionManager collectionManager;
    private final int DEFAULT_INITIAL_DELAY = 1;
    private final int DEFAULT_PERIOD = 5;

    public MongoSyncManager(MongoCollectionManager collectionManager) {
        this.stop = false;
        this.async = false;
        this.collectionManager = collectionManager;
        this.init(false);
    }

    public MongoSyncManager(boolean async, MongoCollectionManager collectionManager) {
        this.stop = false;
        this.async = async;
        this.collectionManager = collectionManager;
        this.init(async);
    }

    public void shutdown() {
        this.stop = true;
        ThreadManager.getInstance().shutdown();
    }

    public void init(boolean async) {
        if (!async) {
            return;
        }
        ThreadManager.getInstance().executeGeneral(() -> {
            while (!stop) {
                try {
                    MongoTask<Document> task = taskQueue.take();
                    CopyOnWriteArrayList<WriteModel<Document>> list = pool.get(task.getKey());
                    if (list == null) {
                        list = new CopyOnWriteArrayList<>();
                    }
                    list.add(task.getModel());
                    pool.put(task.getKey(), list);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        ThreadManager.getInstance().scheduleGeneralAtFixedRate(this::execute, DEFAULT_INITIAL_DELAY, DEFAULT_PERIOD);
    }

    public boolean put(MongoTask<Document> commend) {
        if (commend == null) {
            return false;
        }
        if (commend.getModel() == null) {
            return false;
        }
        if (!async) {
            return false;
        }
        try {
            this.taskQueue.put(commend);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void execute() {
        if (pool.isEmpty()) {
            return;
        }
        pool.forEach((k, v) -> collectionManager.getCollection(k).bulkWrite(v));
        pool.clear();
    }
}
