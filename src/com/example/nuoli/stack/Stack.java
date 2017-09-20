package com.example.nuoli.stack;

import java.util.*;

/**
 * 堆处理类
 */
public class Stack<T>{

    private LinkedList<T> storage = new LinkedList<T>();

    public void push(T v){
        storage.addFirst(v);
    }

    public T pop(){
        return storage.removeLast();
    }

    public Boolean empty(){
        return storage.isEmpty();
    }

    public Boolean contains(T n){
        return storage.contains(n);
    }

    public Integer length(){
        return storage.size();
    }

    public String toString(){
        return storage.toString();
    }

}
