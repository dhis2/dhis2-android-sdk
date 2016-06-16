package org.hisp.dhis.client.sdk.core.common;

public final class KeyValue<T, K> {
    private final T key;
    private final K value;

    public KeyValue(T key, K value) {
        this.key = key;
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public K getValue() {
        return value;
    }
}
