package com.github.johnsonmoon.commons;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by xuyh at 2019/8/28 11:22.
 */
public class PageUtils {
    /**
     * Get page from a list.
     *
     * @param needPage whether needed to be paged
     * @param current  current page, start from 0
     * @param pageSize page size
     * @param sources  source list to be paged
     * @return paged list
     */
    public static <T> List<T> getPage(Boolean needPage, Integer current, Integer pageSize, List<T> sources) {
        if (sources == null || sources.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> pagedTargets = new ArrayList<>();
        if (needPage) {
            Integer page = current;
            if (page < 0) {
                page = 0;
            }
            Integer offset = page * pageSize;
            if (offset <= sources.size() - 1) {
                for (int index = offset, count = 0; index < sources.size() && count < pageSize; index++, count++) {
                    pagedTargets.add(sources.get(index));
                }
            }
        } else {
            pagedTargets.addAll(sources);
        }
        return pagedTargets;
    }
}
