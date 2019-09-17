package com.github.johnsonmoon.commons.util;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by xuyh at 2019/9/17 19:51.
 */
public class PageUtilsTest {
    private List<String> dataList = new ArrayList<>();

    @Before
    public void before() {
        for (int i = 0; i < 1002; i++) {
            dataList.add(i + "-data");
        }
    }

    @Test
    public void test() {
        int pageSize = 10;
        int pages = dataList.size() / pageSize;
        for (int page = 0; page <= pages; page++) {
            List<String> pagedList = PageUtils.getPage(true, page, pageSize, dataList);
            System.out.println("page: " + page + " ---------------------");
            pagedList.forEach(System.out::println);
            System.out.println("\r\n");
        }
    }
}
