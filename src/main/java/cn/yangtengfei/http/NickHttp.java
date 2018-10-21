package cn.yangtengfei.http;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;


public class NickHttp {

    public static void main(String[] args) {
       String result =  HttpUtil.doGet("https://www.nike.com/cn/zh_cn/c/nike-plus/snkrs-app","utf-8");
       //System.out.println(result);
        Document doc = Jsoup.parse(result);
        Element times_content = doc.getElementById("nike-unite");
        String clientId = times_content.attr("data-clientid");
        System.out.println(clientId);

        String url = "https://unite.nike.com/login?appVersion=515" +
                "&experienceVersion=413" +
                "&uxid=com.nike.commerce.nikedotcom.web" +
                "&locale=zh_CN" +
                "&backendEnvironment=identity" +
                //"&browser=Google Inc." +
                "&os=undefined" +
                "&mobile=false" +
                "&native=false" +
                "&visit=1" +
                "&visitor=2011ee7a-5323-40c5-bec5-557972e92a8a";

        Map<String,String> loginMap = new HashMap<String,String>();
        loginMap.put("username","");
        loginMap.put("password","");
        loginMap.put("client_id","HlHa2Cje3ctlaOqnxvgZXNaAs7T9nAuH");
        loginMap.put("ux_id","com.nike.commerce.nikedotcom.web");
        loginMap.put("grant_type","password");
        //{"username":"+8615611300100","password":"FGK1997fgk","client_id":"HlHa2Cje3ctlaOqnxvgZXNaAs7T9nAuH","ux_id":"com.nike.commerce.nikedotcom.web","grant_type":"password"}
       result = HttpUtil.doPost(url,loginMap,"utf-8");
       System.out.println("login result:"+result);
    }
}
