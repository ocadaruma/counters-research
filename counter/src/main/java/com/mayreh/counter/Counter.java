package com.mayreh.counter;

/**
 * Interface for counting key
 */
public interface Counter {
    int incrementAndGet(byte[] input, int amount);

    int get(byte[] input);

    void clear();
}
