package cn.yangtengfei.control;


import cn.yangtengfei.config.UserProperties;
import cn.yangtengfei.service.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.dc.pr.PRError;

@RestController
public class GrabController {

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private UserProperties userProperties;


    @RequestMapping("/doGrab")
    public String doGrab() {
        userLoginService.doLogin();
        return "213123";
    }

    @RequestMapping("/getCaptcha")
    public String getCaptcha() {
        System.out.print(userProperties.getName());
        System.out.print(userProperties.getPassword());
        String result = userLoginService.getCaptcha();
        return result;
    }
}
