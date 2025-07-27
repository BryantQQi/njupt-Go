package com.atnjupt.sqyxgo.home.controller;

import java.io.File;
import java.io.IOException;

/**
 * ClassName:FileTest
 * Package: com.atnjupt.sqyxgo.home.controller
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/25 8:55
 * @Version 1.0
 */
public class FileTest {

    public static void main(String[] args) throws IOException {
        File file = new File("test.txt");
        System.out.println(file.exists());
        System.out.println(file.getAbsolutePath());
        System.out.println(file.createNewFile());
        System.out.println(file.getAbsolutePath());
        System.out.println(file.exists());
        System.out.println(file.delete());

    }
}
