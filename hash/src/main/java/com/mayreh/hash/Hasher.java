package com.mayreh.hash;

/**
 * Interface for hashing byte array into integer.
 */
@FunctionalInterface
public interface Hasher {
    int hash(byte[] input, int bitMask);
}
