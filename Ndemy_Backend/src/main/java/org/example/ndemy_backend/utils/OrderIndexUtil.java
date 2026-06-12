package org.example.ndemy_backend.utils;

import java.util.function.Supplier;

public class OrderIndexUtil {
    public static int resolveOrderIndex(Integer requested, Supplier<Integer> fallback) {
        return requested != null ? requested : fallback.get();
    }
}
