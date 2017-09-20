package com.example.nuoli.collection;

import com.example.nuoli.stack.Stack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auth liwenya
 * 容器类
 */
public class Vessel {

    //小车启动时需要保存的数据
    public static Map<String,String> map0 = new HashMap<String, String>();
    public static Map<String,String> map1100 = new HashMap<String, String>();
    public static Map<String,String> stationMap = new HashMap<String, String>();
    //自动工位判断保存数据
    public static Stack<String> stack1 = new Stack<String>();
    public static Stack<String> stack2 = new Stack<String>();
    public static Stack<String> stack3 = new Stack<String>();
    public static Stack<String> stack4 = new Stack<String>();
    public static Stack<String> stack5 = new Stack<String>();
    public static Stack<String> stack6 = new Stack<String>();
    public static Stack<String> stack7 = new Stack<String>();
    public static Stack<String> stack8 = new Stack<String>();
    //手动工位判断保存数据
    public static Stack<String> stackMan01 = new Stack<String>();
    public static Stack<String> stackMan02 = new Stack<String>();
    public static Stack<String> stackMan03 = new Stack<String>();
    public static Stack<String> stackMan04 = new Stack<String>();
    public static Stack<String> stackMan05 = new Stack<String>();
    public static Stack<String> stackMan06 = new Stack<String>();
    public static Stack<String> stackMan07 = new Stack<String>();
    public static Stack<String> stackMan08 = new Stack<String>();
    public static Stack<String> stackMan09 = new Stack<String>();
    public static Stack<String> stackMan10 = new Stack<String>();
    public static Stack<String> stackMan11 = new Stack<String>();
    public static Stack<String> stackMan12 = new Stack<String>();
    public static Stack<String> stackMan13 = new Stack<String>();
    public static Stack<String> stackMan14 = new Stack<String>();
    public static Stack<String> stackMan15 = new Stack<String>();
    public static Stack<String> stackMan16 = new Stack<String>();
    public static Stack<String> stackMan17 = new Stack<String>();
    public static Stack<String> stackMan18 = new Stack<String>();
    public static Stack<String> stackMan19 = new Stack<String>();
    public static Stack<String> stackMan20 = new Stack<String>();
    public static Stack<String> stackMan21 = new Stack<String>();
    public static Stack<String> stackMan22 = new Stack<String>();
    public static Stack<String> stackMan23 = new Stack<String>();
    public static Stack<String> stackMan24 = new Stack<String>();
    public static Stack<String> stackMan25 = new Stack<String>();
    public static Stack<String> stackMan26 = new Stack<String>();
    public static Stack<String> stackMan27 = new Stack<String>();
    public static Stack<String> stackMan28 = new Stack<String>();
    public static Stack<String> stackMan29 = new Stack<String>();
    public static Stack<String> stackMan30 = new Stack<String>();
    public static Stack<String> stackMan31 = new Stack<String>();
    public static Stack<String> stackMan32 = new Stack<String>();
    public static Stack<String> stackMan33 = new Stack<String>();
    public static Stack<String> stackMan34 = new Stack<String>();
    public static Stack<String> stackMan35 = new Stack<String>();
    public static Stack<String> stackMan36 = new Stack<String>();
    public static Stack<String> stackMan37 = new Stack<String>();
    public static Stack<String> stackMan38 = new Stack<String>();
    public static Stack<String> stackMan39 = new Stack<String>();
    public static Stack<String> stackMan40 = new Stack<String>();
    public static Stack<String> stackMan41 = new Stack<String>();
    public static Stack<String> stackMan42 = new Stack<String>();
    public static Stack<String> stackMan43 = new Stack<String>();
    public static Stack<String> stackMan44 = new Stack<String>();
    public static Stack<String> stackMan45 = new Stack<String>();
    public static Stack<String> stackMan46 = new Stack<String>();
    public static Stack<String> stackMan47 = new Stack<String>();
    public static Stack<String> stackMan48 = new Stack<String>();
    public static Stack<String> stackMan49 = new Stack<String>();
    public static Stack<String> stackMan50 = new Stack<String>();
    public static Stack<String> stackMan51 = new Stack<String>();
    public static Stack<String> stackMan52 = new Stack<String>();
    public static Stack<String> stackMan53 = new Stack<String>();
    public static Stack<String> stackMan54 = new Stack<String>();
    public static Stack<String> stackMan55 = new Stack<String>();
    public static Stack<String> stackMan56 = new Stack<String>();
    public static Stack<String> stackMan57 = new Stack<String>();
    public static Stack<String> stackMan58 = new Stack<String>();
    public static Stack<String> stackMan59 = new Stack<String>();
    public static Stack<String> stackMan60 = new Stack<String>();

    //保存所有的小车号
    public static Stack<String> stackVehicle = new Stack<String>();

    //清空所有集合
    public static void cleanCollection(){
        clean(stack1);clean(stack2);clean(stack3);clean(stack4);
        clean(stack5);clean(stack6);clean(stack7);clean(stack8);
        clean(stackMan01);clean(stackMan02);clean(stackMan03);
        clean(stackMan04);clean(stackMan05);clean(stackMan06);
        clean(stackMan07);clean(stackMan08);clean(stackMan09);
        clean(stackMan10);clean(stackMan11);clean(stackMan12);
        clean(stackMan13);clean(stackMan14);clean(stackMan15);
        clean(stackMan16);clean(stackMan17);clean(stackMan18);
        clean(stackMan19);clean(stackMan20);clean(stackMan21);
        clean(stackMan22);clean(stackMan23);clean(stackMan24);
        clean(stackMan25);clean(stackMan26);clean(stackMan27);
        clean(stackMan28);clean(stackMan29);clean(stackMan30);
        clean(stackMan31);clean(stackMan32);clean(stackMan33);
        clean(stackMan34);clean(stackMan35);clean(stackMan36);
        clean(stackMan37);clean(stackMan38);clean(stackMan39);
        clean(stackMan40);clean(stackMan41);clean(stackMan42);
        clean(stackMan43);clean(stackMan44);clean(stackMan45);
        clean(stackMan46);clean(stackMan47);clean(stackMan48);
        clean(stackMan49);clean(stackMan50);clean(stackMan51);
        clean(stackMan52);clean(stackMan53);clean(stackMan54);
        clean(stackMan55);clean(stackMan56);clean(stackMan57);
        clean(stackMan58);clean(stackMan59);clean(stackMan60);
//        System.out.println("Vessel.cleanCollection <--- GO ==================================== 清空集合");
    }


    //清空集合方法
    private static Boolean clean(Stack c){
        while(!c.empty()){
            c.pop();
        }
        return c.empty();
    }




}
