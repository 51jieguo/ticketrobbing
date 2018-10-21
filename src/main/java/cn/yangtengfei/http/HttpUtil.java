package cn.yangtengfei.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.file.attribute.AclEntry;
import java.util.*;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


public class HttpUtil {

    public static String cookie = "";
    public static Map<String,String> cookieMap = new HashMap<>();
    public static void main(String[] args) throws InterruptedException {
        String get = doGet("https://kyfw.12306.cn/passport/captcha/captcha-image?login_site=E&module=login&rand=sjrand&0.20088273869745750",null);

        //answer=118%2C37%2C258%2C110&login_site=E&rand=sjrand
//POST https://kyfw.12306.cn/passport/captcha/captcha-check HTTP/1.1
        Map<String,String> map = new HashMap<>();

        System.out.println("cookie:"+cookie);
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
        /*stringBuffer.append((Integer.parseInt(readArray[0])-780) +",");
        stringBuffer.append((Integer.parseInt(readArray[1])-477));
        if(readArray.length>2){
            stringBuffer.append(",");
            stringBuffer.append(Integer.parseInt(readArray[2])-780+",");
            stringBuffer.append(Integer.parseInt(readArray[3])-477);
        }*/

        map.put("answer",stringBuffer.toString());
        map.put("login_site","e");
        map.put("rand","sjrand");
        String result = doPost("https://kyfw.12306.cn/passport/captcha/captcha-check",map,"utf-8");
        System.out.println("cookie:"+cookie);
        System.out.print("结果："+result);

        Map mapTypes = JSON.parseObject(result);
        if("4".equals(mapTypes.get("result_code"))){
            System.out.println("验证成功");


            Map<String,String> loginMap = new HashMap<>();
            loginMap.put("username","759620299@qq.com");
            loginMap.put("password","longfei19880");
            loginMap.put("appid","otn");
            String loginResult = doPost("https://kyfw.12306.cn/passport/web/login",loginMap,"utf-8");

            System.out.println("登陆返回结果："+loginResult);
            Map mapResult = JSON.parseObject(loginResult);
            if("0".equals(String.valueOf(mapResult.get("result_code")))){
                String uamtk = String.valueOf(mapResult.get("uamtk"));
                cookieMap.put("uamtk",uamtk);
                Map<String,String> secondloginMap = new HashMap<>();
                secondloginMap.put("appid","otn");
                String secondEnureloginResult = doPost("https://kyfw.12306.cn/passport/web/auth/uamtk",secondloginMap,"utf-8");
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

    public static String doGet(String url,String charset){
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
                    List<Cookie> cookies = cookieStore.getCookies();

                    for (Cookie c : cookies) {
                        //cookie += c.getName() + "=" + c.getValue() + ";";
                        cookieMap.put(c.getName(),c.getValue());
                    }
                    cookie = "";
                    Iterator<Map.Entry<String, String>> entries = cookieMap.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry<String, String> entry = entries.next();
                        //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                        cookie += entry.getKey() + "=" + entry.getValue() + ";";
                    }


                    //System.out.println("HttpHeader Cookie:   " + cookie);

                    for (int i = 0; i < cookies.size(); i++) {
                        //遍历Cookies
                        System.out.println(cookies.get(i));
                        if (cookies.get(i).getName().equals("JSESSIONID")) {
                            String JSESSIONID = cookies.get(i).getValue();
                            System.out.print("JSESSIONID="+JSESSIONID);
                        }
                    }
                    //System.out.print(resEntity.getContentType().getValue());
                    if("image/jpeg".equals(resEntity.getContentType().getValue())){
                        InputStream in = resEntity.getContent();
                        String path = "D:\\temp";
                        String fileName= "my.jpg";
                        savePicToDisk(in,path,fileName);
                        return path +File.separator+ fileName;
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

    public static String doPost(String url,Map<String,String> map,String charset){
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try{
            httpClient = new SSLClient();
            httpPost = new HttpPost(url);
            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            /*Iterator iterator = map.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String,String> elem = (Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));
            }
            if(list.size() > 0){
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);
                httpPost.setEntity(entity);
            }*/

            //装填参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            if(map!=null){
                for (Entry<String, String> entry : map.entrySet()) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            //设置参数到请求对象中
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, charset));

            System.out.println("请求地址："+url);
            System.out.println("请求参数："+nvps.toString());

            cookie = "";
            Iterator<Map.Entry<String, String>> entries = cookieMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                cookie += entry.getKey() + "=" + entry.getValue() + ";";
            }


            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            httpPost.setHeader("Cookie", cookie);
            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                CookieStore cookieStore = ((SSLClient) httpClient).getCookieStore();

                List<Cookie> cookies = cookieStore.getCookies();

                for (Cookie c : cookies) {
                    //cookie += c.getName() + "=" + c.getValue() + ";";
                    cookieMap.put(c.getName(),c.getValue());
                }



                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,charset);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
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
