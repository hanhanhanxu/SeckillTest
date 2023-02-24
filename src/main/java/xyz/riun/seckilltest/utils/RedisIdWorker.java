package xyz.riun.seckilltest.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import xyz.riun.seckilltest.constants.RedisConstant;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @Author：Hanxu
 * @url：https://riun.xyz/
 * @Date：2023/2/23 16:09
 * 基于redis的分布式id生成器
 * 最长可用到2090-01-19 03:14:07，每秒并发及每天最多获取id个数 4294967295（42亿）
 */
@Component
public class RedisIdWorker {

    private static final long startTime = 1640995200;
    private static final int COUNT_BITS = 32;
    //private static final int startIncr = 0;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 首位是符号位固定为0 后续31位是秒级时间戳 最后32位是自增数字
     *
     * 时间戳计算方式是当前时间-起始时间，31位二进制位最大可表示2^31-1（ 2147483647）。
     *          如果用2022.1.1 00:00:00（ 1640995200）作为起始时间，最长可用到2090-01-19 03:14:07 （1640995200+2147483647 = 3788478847，秒级时间戳转为时间）
     *
     * 自增数字在redis里从1开始自增，并发获取id时（同一秒内来获取id），前31位秒级时间戳可能相同，因此每秒支持获取4294967295（42亿）个不同的id（2^32-1 redis的自增首个获取到的值是1，因此这32个二进制位不可能全为0）
     *          但由于自增数字只占32个二进制位，所以假设一秒内获取了2^32-1次id，那么今天就无法再获取其他id了，因为再继续自增，32个二进制位存不下，位移时就会丢失数据，导致和之前生成的id重复。
     *          因此每天最多支持获取4294967295（42亿）个不同的id。要想改善这个问题可以增多自增数字占的位数，减少时间戳占的位数。
     *
     * 这里key是 incr:bizKey:yyyy:MM:dd 所以每天都会有一个新的key去做自增，这样可以方便的统计每天获取了多少id，做其他业务上的统计。
     * @param bizKey 业务标识
     * @return 业务内的唯一id
     */
    public long nextId(String bizKey) {
        //当前时间戳 - 起始时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowTime = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowTime - startTime;

        //自增位 incr:voucher:20230223
        String formatTime = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        Long incrNum = stringRedisTemplate.opsForValue().increment(RedisConstant.INCR_PRE_KEY + bizKey + ":" + formatTime);
        //incrNum = startIncr + incrNum;

        //位移
        return (timestamp << COUNT_BITS) | (incrNum);
    }

    public static void main(String[] args) {
        long startTime = LocalDateTime.of(2022, 1, 1, 0, 0, 0).toEpochSecond(ZoneOffset.UTC);
        System.out.println(startTime);
        //1640995200

        long nowTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        System.out.println(nowTime);
        System.out.println(nowTime - startTime);


        long timeMax = startTime + 2147483647;
        System.out.println(timeMax);
        LocalDateTime localDateTime = Instant.ofEpochSecond(timeMax).atZone(ZoneOffset.UTC).toLocalDateTime();
        System.out.println(localDateTime);
    }
}
