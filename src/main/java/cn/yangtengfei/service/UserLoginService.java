package cn.yangtengfei.service;


import cn.yangtengfei.config.UserCache;
import cn.yangtengfei.config.UserProperties;
import cn.yangtengfei.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Service
public class UserLoginService {



    public String  domain="https://kyfw.12306.cn";

    @Autowired
    private HttpHandlerService httpHandlerService;

    @Autowired
    private UserCache userCache;

    @Autowired
    private UserProperties userProperties;

    public String getCaptcha(){
        String result = httpHandlerService.doGet(domain+"/passport/captcha/captcha-image?login_site=E&module=login&rand=sjrand&0.20088273869745750",null);
        return result;
    }

    public void doLogin(){

        String get = httpHandlerService.doGet(domain+"/passport/captcha/captcha-image?login_site=E&module=login&rand=sjrand&0.20088273869745750",null);

        Map<String,String> map = new HashMap<>();
        System.out.print("输入");
        Scanner scan = new Scanner(System.in);
        String read = scan.nextLine();
        System.out.println("输入数据："+read);

        Map<String,String> pictureMap = new HashMap<>();
        pictureMap.put("1","815,514");
        pictureMap.put("2","915,514");
        pictureMap.put("3","1015,514");
        pictureMap.put("4","1115,514");
        pictureMap.put("5","815,605");
        pictureMap.put("6","915,605");
        pictureMap.put("7","1015,605");
        pictureMap.put("8","1115,605");

        String indexArray[] = read.split(",");
        String dataArray = "";
        for(int i=0; i<indexArray.length; i++){
            if(i==indexArray.length-1){
                dataArray+=pictureMap.get(indexArray[i]);
            }else{
                dataArray+=pictureMap.get(indexArray[i])+",";
            }
        }

        String readArray[] = dataArray.split(",");
        StringBuffer stringBuffer = new StringBuffer();
        for(int i=0;i<readArray.length; i++){
            if(i%2==0){
                if(i==readArray.length-1){
                    stringBuffer.append((Integer.parseInt(readArray[i])-780));
                }else{
                    stringBuffer.append((Integer.parseInt(readArray[i])-780) +",");
                }

            }else{
                if(i==readArray.length-1){
                    stringBuffer.append((Integer.parseInt(readArray[i])-477));
                }else{
                    stringBuffer.append((Integer.parseInt(readArray[i])-477)+",");
                }

            }
        }

        map.put("answer",stringBuffer.toString());
        map.put("login_site","e");
        map.put("rand","sjrand");
        String result = httpHandlerService.doPost("https://kyfw.12306.cn/passport/captcha/captcha-check",map,"utf-8");
        System.out.print("结果："+result);

        Map mapTypes = JSON.parseObject(result);
        if("4".equals(mapTypes.get("result_code"))){
            System.out.println("验证成功");


            Map<String,String> loginMap = new HashMap<>();
            loginMap.put("username",userProperties.getName());
            loginMap.put("password",userProperties.getPassword());
            loginMap.put("appid","otn");
            String loginResult = httpHandlerService.doPost("https://kyfw.12306.cn/passport/web/login",loginMap,"utf-8");

            System.out.println("登陆返回结果："+loginResult);
            Map mapResult = JSON.parseObject(loginResult);
            if("0".equals(String.valueOf(mapResult.get("result_code")))){
                String uamtk = String.valueOf(mapResult.get("uamtk"));
                userCache.getCookieMap().put("uamtk",uamtk);
                Map<String,String> secondloginMap = new HashMap<>();
                secondloginMap.put("appid","otn");
                String secondEnureloginResult = httpHandlerService.doPost("https://kyfw.12306.cn/passport/web/auth/uamtk",secondloginMap,"utf-8");
                System.out.println("二次确认结果："+secondEnureloginResult);
            }
            /*System.out.println("========显示cookie的所有值==================");
            Iterator<Map.Entry<String, String>> entries = cookieMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                cookie += entry.getKey() + "=" + entry.getValue() + ";";
            }*/

        }

    }
}
