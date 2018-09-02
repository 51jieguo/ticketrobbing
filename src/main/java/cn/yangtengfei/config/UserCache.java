package cn.yangtengfei.config;


import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class UserCache {

    public Map<String,String> cookieMap = new HashMap<>();
}
