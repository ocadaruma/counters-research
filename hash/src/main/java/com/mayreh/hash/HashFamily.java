package com.mayreh.hash;

import java.util.Random;
import java.util.function.LongFunction;

import net.openhft.hashing.LongHashFunction;

/**
 * Hash function family which returns independent hashes for every call
 */
@FunctionalInterface
public interface HashFamily {
    Hasher get();

    class Impl implements HashFamily {
        private final Random random = new Random(0);
        private final LongFunction<LongHashFunction> supplier;

        Impl(LongFunction<LongHashFunction> supplier) {
            this.supplier = supplier;
        }

        @Override
        public Hasher get() {
            LongHashFunction f = supplier.apply(random.nextLong());
            return (input, bitMask) -> (int)(f.hashBytes(input) & bitMask);
        }
    }
}
