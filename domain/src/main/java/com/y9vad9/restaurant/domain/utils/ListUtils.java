package com.y9vad9.restaurant.domain.utils;

import java.util.ArrayList;
import java.util.List;

public final class ListUtils {
    private ListUtils() {
    }

    public static <T> List<List<T>> makeRows(List<T> list, int elementsPerRow) {
        List<List<T>> rows = new ArrayList<>();
        for (int i = 0; i < list.size(); i += elementsPerRow) {
            int endIndex = Math.min(i + elementsPerRow, list.size());
            rows.add(list.subList(i, endIndex));
        }
        return rows;
    }
}
