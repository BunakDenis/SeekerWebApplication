package com.example.utils.collections;

import java.util.List;
import java.util.stream.Collectors;

public class CollectionsUtils {

    public static <T> List<T> cloneListWithStream(List<T> originalList) {
        if (originalList == null) {
            return null;
        }
        // Преобразуем список в поток и собираем его обратно в новый список
        return originalList.stream().collect(Collectors.toList());
    }

}
