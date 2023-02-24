package xyz.riun.seckilltest.utils;

import java.io.*;

/**
 * @Author：Hanxu
 * @url：https://riun.xyz/
 * @Date：2023/2/23 19:50
 * 向文件中写测试数据
 */
public class Test {
    public static void main(String[] args) throws IOException {
        //向文件中写数据
        FileWriter fileWriter = new FileWriter(new File("E:\\TestFloder\\Seckill\\userId.txt"));
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (int i = 100003; i < 100503; i++) {
            bufferedWriter.write(i + "\n");
        }
        bufferedWriter.close();
        fileWriter.close();
    }
}
