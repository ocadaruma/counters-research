package com.mayreh.counter;

import java.util.Arrays;

import com.mayreh.hash.HashFamilies;
import com.mayreh.hash.Hasher;

/**
 * Count-Min sketch based counter
 */
public class CMCounter implements Counter {
    private static final int MAX_W = 1 << 20;

    private final int[][] sketch;
    private final Hasher[] hashers;
    private final int bitMask;
    private final boolean conservativeUpdate;
    private final int[] hashValueCache;

    public int depth() {
        return sketch.length;
    }

    public int width() {
        return sketch[0].length;
    }

    /**
     * Instantiates the counter which meets the error bounds.
     * @param epsilon target error rate against the total item count
     * @param delta the probability to allow the estimation to be out of error bound
     */
    public CMCounter(double epsilon, double delta, boolean conservativeUpdate) {
        this.conservativeUpdate = conservativeUpdate;

        int d = (int) Math.ceil(Math.log(1 / delta));
        int w0 = (int) Math.ceil(Math.E / epsilon);

        int w = 1;
        while (w < w0) {
            w <<= 1;
            if (w >= MAX_W) {
                break;
            }
        }
        bitMask = w - 1;

        sketch = new int[d][w];
        hashers = new Hasher[d];
        hashValueCache = new int[d];
        HashFamilies family = HashFamilies.XX3;
        for (int i = 0; i < hashers.length; i++) {
            hashers[i] = family.get();
        }
    }

    @Override
    public int incrementAndGet(byte[] input, int amount) {
        int min = Integer.MAX_VALUE;
        if (!conservativeUpdate) {
            for (int i = 0; i < sketch.length; i++) {
                int r = hashers[i].hash(input, bitMask);
                sketch[i][r] += amount;
                min = Math.min(min, sketch[i][r]);
            }
            return min;
        }

        for (int i = 0; i < sketch.length; i++) {
            int r = hashers[i].hash(input, bitMask);
            hashValueCache[i] = r;
            min = Math.min(min, sketch[i][r]);
        }
        int newMin = Integer.MAX_VALUE;
        for (int i = 0; i < sketch.length; i++) {
            int r = hashValueCache[i];
            sketch[i][r] = Math.max(sketch[i][r], min + amount);
            newMin = Math.min(newMin, sketch[i][r]);
        }

        return newMin;
    }

    @Override
    public int get(byte[] input) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < sketch.length; i++) {
            int r = hashers[i].hash(input, bitMask);
            min = Math.min(min, sketch[i][r]);
        }
        return min;
    }

    @Override
    public void clear() {
        for (int[] register : sketch) {
            Arrays.fill(register, 0);
        }
    }
}
