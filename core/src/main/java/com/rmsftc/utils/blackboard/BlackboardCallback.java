package com.rmsftc.utils.blackboard;

/**
 * Callback invoked when a {@link Blackboard} key's value is set.
 *
 * @param <T> the type of value stored under the key
 */
@FunctionalInterface
public interface BlackboardCallback<T> {
    void onChange(String key, T value);
}
