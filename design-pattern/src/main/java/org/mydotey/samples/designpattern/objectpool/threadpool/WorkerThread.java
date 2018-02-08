package org.mydotey.samples.designpattern.objectpool.threadpool;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.mydotey.samples.designpattern.objectpool.ObjectPool.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author koqizhao
 *
 * Feb 6, 2018
 */
public class WorkerThread extends Thread {

    private static Logger _logger = LoggerFactory.getLogger(WorkerThread.class);

    private volatile Runnable _task;

    private Entry<WorkerThread> _poolEntry;
    private Consumer<WorkerThread> _onTaskComplete;

    private Object _lock = new Object();

    private AtomicBoolean _isStarted = new AtomicBoolean();

    protected WorkerThread(Consumer<WorkerThread> onTaskComplete) {
        Objects.requireNonNull(onTaskComplete, "onTaskComplete is null");
        _onTaskComplete = onTaskComplete;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            synchronized (_lock) {
                if (!_isStarted.get())
                    _isStarted.set(true);

                if (_task != null) {
                    _task = null;

                    try {
                        _onTaskComplete.accept(this);
                    } catch (Exception ex) {
                        _logger.error("_onTaskComplete threw exception: " + this.getId(), ex);
                        break;
                    }
                }

                try {
                    _lock.wait();
                } catch (InterruptedException e) {
                    break;
                }

                try {
                    _task.run();
                } catch (Exception e) {
                    _logger.error("task threw exception: " + this.getId(), e);
                }
            }
        }
    }

    protected void setTask(Runnable task) {
        synchronized (_lock) {
            _task = task;
            _lock.notify();
        }
    }

    protected void setPoolEntry(Entry<WorkerThread> poolEntry) {
        _poolEntry = poolEntry;
    }

    protected Entry<WorkerThread> getPoolEntry() {
        return _poolEntry;
    }

    @Override
    public synchronized void start() {
        super.start();

        while (!_isStarted.get()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

}
