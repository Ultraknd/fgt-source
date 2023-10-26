package fgt.gameserver.taskmanager;

import fgt.commons.pool.ThreadPool;

import java.util.concurrent.ScheduledFuture;

/**
 * Created by Stanislav on 27.04.2014.
 */
public abstract class ExclusiveTask
{
    private final boolean _returnIfAlreadyRunning;

    private ScheduledFuture<?> _future;
    private boolean _isRunning;
    private Thread _currentThread;

    protected ExclusiveTask(boolean returnIfAlreadyRunning)
    {
        _returnIfAlreadyRunning = returnIfAlreadyRunning;
    }

    protected ExclusiveTask()
    {
        this(false);
    }

    public synchronized boolean isScheduled()
    {
        return _future != null;
    }

    public synchronized final void cancel()
    {
        if (_future != null)
        {
            _future.cancel(false);
            _future = null;
        }
    }

    public synchronized final void schedule(long delay)
    {
        cancel();

        _future = ThreadPool.schedule(_runnable, delay);
    }

    public synchronized final void scheduleAtFixedRate(long delay, long period)
    {
        cancel();

        _future = ThreadPool.scheduleAtFixedRate(_runnable, delay, period);
    }

    private final Runnable _runnable = new Runnable() {
        @Override
        public void run()
        {
            if (tryLock())
            {
                try
                {
                    onElapsed();
                }
                finally
                {
                    unlock();
                }
            }
        }
    };

    protected abstract void onElapsed();

    private synchronized boolean tryLock()
    {
        if (_returnIfAlreadyRunning)
            return !_isRunning;

        _currentThread = Thread.currentThread();

        for (;;)
        {
            try
            {
                notifyAll();

                if (_currentThread != Thread.currentThread())
                    return false;

                if (!_isRunning)
                    return true;

                wait();
            }
            catch (InterruptedException e)
            {
            }
        }
    }

    private synchronized void unlock()
    {
        _isRunning = false;
    }
}


