package com.mayreh.hash;

import net.openhft.hashing.LongHashFunction;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum HashFamilies implements HashFamily {
    XX3(new Impl(LongHashFunction::xx3)),
    Murmur3(new Impl(LongHashFunction::murmur_3)),
    WY3(new Impl(LongHashFunction::wy_3)),
    ;
    private final Impl impl;

    @Override
    public Hasher get() {
        return impl.get();
    }
}
