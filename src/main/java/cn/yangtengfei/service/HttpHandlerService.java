package cn.yangtengfei.service;


import cn.yangtengfei.config.UserCache;
import cn.yangtengfei.http.SSLClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class HttpHandlerService {


    @Autowired
    private UserCache userCache;

    public  String doGet(String url,String charset){
        if(null == charset){
            charset = "utf-8";
        }
        HttpClient httpClient = null;
        HttpGet httpGet= null;
        String result = null;
        try {
            httpClient = new SSLClient();
            httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){

                    CookieStore cookieStore = ((SSLClient) httpClient).getCookieStore();
                    setCookIntoMap(cookieStore.getCookies());
                    //System.out.print(resEntity.getContentType().getValue());
                    if("image/jpeg".equals(resEntity.getContentType().getValue())){
                        InputStream in = resEntity.getContent();
                        String path = "D:\\temp\\images";
                        String fileName= UUID.randomUUID()+".jpg";
                        savePicToDisk(in,path,fileName);
                        return fileName;
                    }else{
                        result = EntityUtils.toString(resEntity,charset);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public  String doPost(String url,Map<String,String> map,String charset){
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try{
            httpClient = new SSLClient();
            httpPost = new HttpPost(url);
            //装填参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            if(map!=null){
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            //设置参数到请求对象中
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, charset));

            System.out.println("请求地址："+url);
            System.out.println("请求参数："+nvps.toString());

            String cookie = getCookie();
            System.out.println("post cookie:"+cookie);
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            httpPost.setHeader("Cookie", cookie);
            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                CookieStore cookieStore = ((SSLClient) httpClient).getCookieStore();

                setCookIntoMap(cookieStore.getCookies());

                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,charset);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }


    private void setCookIntoMap(List<Cookie> cookies){
        for (Cookie c : cookies) {
            userCache.getCookieMap().put(c.getName(),c.getValue());
        }
    }

    private String getCookie(){
        Iterator<Map.Entry<String, String>> entries = userCache.getCookieMap().entrySet().iterator();
        String cookie ="";
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            cookie += entry.getKey() + "=" + entry.getValue() + ";";
        }
        return cookie;
    }

    private static void savePicToDisk(InputStream in, String dirPath,
                                      String filePath) {

        try {
            File dir = new File(dirPath);
            if (dir == null || !dir.exists()) {
                dir.mkdirs();
            }

            //文件真实路径
            String realPath = dirPath.concat(File.separator+filePath);
            File file = new File(realPath);
            if (file == null || !file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = in.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
