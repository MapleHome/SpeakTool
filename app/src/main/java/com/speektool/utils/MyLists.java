package com.speektool.utils;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by lchli on 2015/12/6.
 */
public class MyLists {

    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<>();
    }

    public static <E> LinkedList<E> newLinkedList() {
        return new LinkedList<>();
    }
}
