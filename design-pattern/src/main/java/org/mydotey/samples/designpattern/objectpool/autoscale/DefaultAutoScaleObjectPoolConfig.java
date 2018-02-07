package org.mydotey.samples.designpattern.objectpool.autoscale;

import java.util.concurrent.TimeUnit;

import org.mydotey.samples.designpattern.objectpool.DefaultObjectPoolConfig;

/**
 * @author koqizhao
 *
 * Feb 5, 2018
 */
public class DefaultAutoScaleObjectPoolConfig<T> extends DefaultObjectPoolConfig<T>
        implements AutoScaleObjectPoolConfig<T> {

    private long _objectTtl;
    private long _maxIdleTime;
    private StaleChecker<T> _staleChecker;
    private long _checkInterval;
    private int _scaleFactor;

    protected DefaultAutoScaleObjectPoolConfig() {

    }

    @Override
    public long getObjectTtl() {
        return _objectTtl;
    }

    @Override
    public long getMaxIdleTime() {
        return _maxIdleTime;
    }

    @Override
    public StaleChecker<T> getStaleChecker() {
        return _staleChecker;
    }

    @Override
    public long getCheckInterval() {
        return _checkInterval;
    }

    @Override
    public int getScaleFactor() {
        return _scaleFactor;
    }

    public static class Builder<T> extends AbstractBuilder<T, AutoScaleObjectPoolConfig.Builder<T>>
            implements AutoScaleObjectPoolConfig.Builder<T> {

    }

    @SuppressWarnings("unchecked")
    protected static abstract class AbstractBuilder<T, B extends AutoScaleObjectPoolConfig.AbstractBuilder<T, B>>
            extends DefaultObjectPoolConfig.AbstractBuilder<T, B>
            implements AutoScaleObjectPoolConfig.AbstractBuilder<T, B> {

        protected AbstractBuilder() {
            getPoolConfig()._objectTtl = Long.MAX_VALUE;
            getPoolConfig()._maxIdleTime = Long.MAX_VALUE;
            getPoolConfig()._staleChecker = StaleChecker.DEFAULT;
            getPoolConfig()._checkInterval = TimeUnit.SECONDS.toMillis(10);
            getPoolConfig()._scaleFactor = 1;
        }

        @Override
        protected DefaultAutoScaleObjectPoolConfig<T> newPoolConfig() {
            return new DefaultAutoScaleObjectPoolConfig<T>();
        }

        @Override
        protected DefaultAutoScaleObjectPoolConfig<T> getPoolConfig() {
            return (DefaultAutoScaleObjectPoolConfig<T>) super.getPoolConfig();
        }

        @Override
        public B setObjectTtl(long objectTtl) {
            getPoolConfig()._objectTtl = objectTtl;
            return (B) this;
        }

        @Override
        public B setMaxIdleTime(long maxIdleTime) {
            getPoolConfig()._maxIdleTime = maxIdleTime;
            return (B) this;
        }

        @Override
        public B setStaleChecker(StaleChecker<T> staleChecker) {
            getPoolConfig()._staleChecker = staleChecker;
            return (B) this;
        }

        @Override
        public B setCheckInterval(long checkInterval) {
            getPoolConfig()._checkInterval = checkInterval;
            return (B) this;
        }

        @Override
        public B setScaleFactor(int scaleFactor) {
            getPoolConfig()._scaleFactor = scaleFactor;
            return (B) this;
        }

        @Override
        public AutoScaleObjectPoolConfig<T> build() {
            if (getPoolConfig()._objectTtl <= 0)
                throw new IllegalStateException("objectTtl is invalid: " + getPoolConfig()._objectTtl);

            if (getPoolConfig()._maxIdleTime <= 0)
                throw new IllegalStateException("maxIdleTime is invalid: " + getPoolConfig()._maxIdleTime);

            if (getPoolConfig()._staleChecker == null)
                throw new IllegalStateException("staleChecker is null.");

            if (getPoolConfig()._checkInterval <= 0)
                throw new IllegalStateException("checkInterval is invalid: " + getPoolConfig()._checkInterval);

            if (getPoolConfig()._scaleFactor <= 0)
                throw new IllegalStateException("invalid scaleFactor: " + getPoolConfig()._scaleFactor);

            if (getPoolConfig()._scaleFactor > getPoolConfig().getMaxSize() - getPoolConfig().getMinSize())
                throw new IllegalStateException("too large scaleFactor: " + getPoolConfig()._scaleFactor);

            return (DefaultAutoScaleObjectPoolConfig<T>) super.build();
        }

    }

}
