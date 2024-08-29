package club.scoder.maa.fish.configuration;

import cn.hutool.extra.spring.SpringUtil;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpClientConfig {

    public static OkHttpClient get() {
        return SpringUtil.getBean("okHttpClient");
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(200, 50, TimeUnit.MINUTES))
                .build();
    }

}