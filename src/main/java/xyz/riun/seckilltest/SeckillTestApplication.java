package xyz.riun.seckilltest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@MapperScan("xyz.riun.seckilltest.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class SeckillTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillTestApplication.class, args);
    }

}
