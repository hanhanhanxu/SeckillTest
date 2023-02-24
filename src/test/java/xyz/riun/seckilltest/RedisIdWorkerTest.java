package xyz.riun.seckilltest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.riun.seckilltest.utils.RedisIdWorker;

/**
 * @Author：Hanxu
 * @url：https://riun.xyz/
 * @Date：2023/2/23 16:52
 * redisIdWorker Test
 */
@SpringBootTest
public class RedisIdWorkerTest {
    @Autowired
    private RedisIdWorker redisIdWorker;

    @Test
    void test() {
        long voucher = redisIdWorker.nextId("voucher");
        System.out.println(voucher);
        //77692322694823939
        System.out.println(Long.toBinaryString(voucher));
        //00000001 00010100 00000100 11000001 10000000 00000000 00000000 00000011

        //1000000的二进制是                              00001111 01000010 01000000
    }

}
