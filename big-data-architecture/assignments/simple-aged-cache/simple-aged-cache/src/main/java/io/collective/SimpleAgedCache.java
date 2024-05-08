package io.collective;

import java.util.HashMap;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class SimpleAgedCache {

    private HashMap<Object, ExpirableEntry> cache;
    private Clock clock;
    private Instant start;

    private class ExpirableEntry {
        private Object value;
        private long retention;

        private ExpirableEntry(Object value, long retention) {
            this.value = value;
            this.retention = retention;
        }

        public Object getValue() {
            return value;
        }

        public long getRetention() {
            return retention;
        }
    }

    public SimpleAgedCache(Clock clock) {
        this.cache = new HashMap<>();
        this.clock = clock;
        this.start = Instant.now(clock);
    }

    public SimpleAgedCache() {
        this.cache = new HashMap<>();
    }

    private void removeExpired() {
        if (clock != null) {
            Instant now = Instant.now(clock);
            for (Object key : cache.keySet()) {
                if ((Duration.between(start, now)).toMillis() > cache.get(key).retention) {
                    cache.remove(key);
                }
            }
        }
    }

    public void put(Object key, Object value, int retentionInMillis) {
        long diff = clock != null ? Duration.between(start, Instant.now(clock)).toMillis() : 0;
        cache.put(
                key,
                new ExpirableEntry(value, retentionInMillis + diff));
    }

    public boolean isEmpty() {
        removeExpired();
        return cache.size() == 0;
    }

    public int size() {
        removeExpired();
        return cache.size();
    }

    public Object get(Object key) {
        removeExpired();
        if (cache.get(key) != null) {
            return cache.get(key).value;
        } else {
            return null;
        }
    }
}