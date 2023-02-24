package xyz.riun.seckilltest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @Author：Hanxu
 * @url：https://riun.xyz/
 * @Date：2023/2/23 16:52
 * stringRedisTemplate Test
 */
@SpringBootTest
class SeckillTestApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class User {
        private String name;
        private int age;
    }

    @Test
    void contextLoads() throws JsonProcessingException {
        User user = new User("lisi", 18);
        String json = mapper.writeValueAsString(user);
        stringRedisTemplate.opsForValue().set("u1", json);

        String val = stringRedisTemplate.opsForValue().get("u1");
        //注意：反序列化时，User必须有空的构造方法
        User user1 = mapper.readValue(val, User.class);
        System.out.println(user1);
        //SeckillTestApplicationTests.User(name=lisi, age=18)
    }

}
