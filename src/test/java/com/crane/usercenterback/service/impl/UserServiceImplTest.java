package com.crane.usercenterback.service.impl;

import cn.hutool.core.lang.Pair;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.crane.usercenterback.service.UserService;
import com.crane.usercenterback.utils.AlgorithmUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserService userService;


    @Test
    public void test() {
        String a = "[吉林市, 大同市, 莆田市]";
        JSON parse = JSONUtil.parseArray(a);
        System.out.println(parse);
    }

    @Test
    public void testTreeMap() {

        //key作为匹配值，value作为userid不就行了？
        Map<Long, Long> map = new TreeMap<>(new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                return o2.compareTo(o1);
            }
        });


        map.put(99L,0L);
        map.put(98L,0L);
        map.put(93L,0L);
        map.put(911L,0L);
        map.put(123L,0L);
        map.put(213L,0L);
        map.put(2323L,0L);

        System.out.println(map.toString());
    }

    @Test
    public void testMatch(){
//
//        String a = "[西汉市, 海口市, 包原市,桂林市]";
//        String b = "[西汉市, 海口市, 包原市]";
//        List<String> lista = JSONUtil.parseArray(a).toList(String.class);
//        List<String> listb = JSONUtil.parseArray(b).toList(String.class);
//        System.out.println(AlgorithmUtil.minDistance(lista, listb));


//        PriorityQueue<Pair<Integer, Integer>> pq = new PriorityQueue<>((o1, o2) -> o2.getValue() - o1.getValue());
//        pq.add(new Pair<>(1,1));
//        pq.add(new Pair<>(5,1));
//        pq.add(new Pair<>(10,1));
//        pq.add(new Pair<>(4,0));
//        pq.add(new Pair<>(6,1));
//        pq.add(new Pair<>(199,1));
//        System.out.println(pq.toString());

        // 创建优先级队列
        PriorityQueue<Integer> numbers = new PriorityQueue<>();
        numbers.add(4);
        numbers.add(2);
        numbers.add(1);
        System.out.println("PriorityQueue: " + numbers);

        //使用 peek() 方法
        int number = numbers.peek();
        System.out.println("访问元素: " + numbers.poll());
        System.out.println("访问元素: " + numbers.poll());

    }

}