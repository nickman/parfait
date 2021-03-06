package com.custardsource.parfait.timing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public interface ThreadValue<T> {
    T get();

    boolean canRetrieveAcrossThreads();

    T getForThread(Thread thread);

    Map<Thread, T> asMap();

    public static class ThreadLocalMap<T> implements ThreadValue<T> {
        protected final ThreadLocal<? extends T> threadLocal;

        public ThreadLocalMap(ThreadLocal<? extends T> threadLocal) {
            this.threadLocal = threadLocal;
        }

        @Override
        public final boolean canRetrieveAcrossThreads() {
            return false;
        }

        @Override
        public final T get() {
            return threadLocal.get();
        }

        @Override
        public final T getForThread(Thread thread) {
            if (thread == Thread.currentThread()) {
                return threadLocal.get();
            }
            return null;
        }

        @Override
        public final Map<Thread, T> asMap() {
            return Collections.emptyMap();
        }
    }

    public static class WeakReferenceThreadMap<T> implements ThreadValue<T> {
        protected final Map<Thread, T> map = new MapMaker().weakKeys().makeComputingMap(
                new Function<Thread, T>() {
                    @Override
                    public T apply(Thread from) {
                        return initialValue();
                    }
                });

        protected T initialValue() {
            return null;
        }

        @Override
        public final boolean canRetrieveAcrossThreads() {
            return true;
        }

        @Override
        public final T get() {
            return map.get(Thread.currentThread());
        }

        @Override
        public final T getForThread(Thread thread) {
            return map.get(thread);
        }

        @Override
        public final Map<Thread, T> asMap() {
            return new HashMap<Thread, T>(map);
        }
    }
}
