package com.mayreh.counter;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class ExactCounter implements Counter {
    private final HashMap<ByteBuffer, Integer> delegate = new HashMap<>();

    public Map<ByteBuffer, Integer> table() {
        return delegate;
    }

    @Override
    public int incrementAndGet(byte[] input, int amount) {
        ByteBuffer key = ByteBuffer.wrap(input);
        int newValue = delegate.getOrDefault(key, 0) + amount;
        delegate.put(key, newValue);
        return newValue;
    }

    @Override
    public int get(byte[] input) {
        return delegate.getOrDefault(ByteBuffer.wrap(input), 0);
    }

    @Override
    public void clear() {
        delegate.clear();
    }
}
