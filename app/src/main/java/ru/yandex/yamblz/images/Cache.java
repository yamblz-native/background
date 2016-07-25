package ru.yandex.yamblz.images;

import android.support.annotation.Nullable;

/**
 * Simple cache interface
 * @param <K> key
 * @param <V> value associated with the key
 */
public interface Cache<K, V> {
    /**
     * Returns {@link V} object associated with the key
     * @param key the key
     * @return {@link V} object or {@code null} if there wasn't
     */
    @Nullable V get(K key);

    /**
     * Where the cache has an object associated with the key
     * @param key the key
     * @return {@code true} if has
     */
    boolean containsKey(K key);

    /**
     * Removes {@code key} from the cache
     * @param key the key to remove
     * @return the object associated with the key, or {@code null} if there wasn't
     */
    @Nullable V remove(K key);

    /**
     * Puts value to cache
     * @param key the key
     * @param value the value
     * @return whether object was successfully added
     */
    boolean put(K key, V value);
}
