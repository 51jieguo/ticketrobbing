package cn.yangtengfei.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "12306")
public class UserProperties {

    private String name;

    private String password;
}
