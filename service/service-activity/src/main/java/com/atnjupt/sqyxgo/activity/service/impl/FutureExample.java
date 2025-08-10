package com.atnjupt.sqyxgo.activity.service.impl;

/**
 * ClassName:TEst
 * Package: com.atnjupt.sqyxgo.activity.service.impl
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/29 10:03
 * @Version 1.0
 */
import java.util.concurrent.*;

public class FutureExample {
    public static void main(String[] args) throws Exception {
        Long result = 1L;
        for (Long i = 1L; i <= 20;i++){
            result *= i;

        }
        System.out.println(result);
    }
}

