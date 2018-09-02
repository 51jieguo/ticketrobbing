package cn.yangtengfei.control;

import cn.yangtengfei.aop.AnalysisActuator;
import cn.yangtengfei.aop.UserAccess;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FirstController {

    //@AnalysisActuator(note = "获取聊天信息方法")
    @UserAccess(desc = "second")
    @RequestMapping("/first")
    public Object first() {  
        return "first controller";  
    }  
  
    @RequestMapping("/doError")  
    public Object error() {  
        return 1 / 0;  
    }  
}  