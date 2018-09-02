package cn.yangtengfei.http;
 
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
 
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
 

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
 

 
/**
 * 12306 登录例子
 * 
 * @author wang.situ
 * @blog http://blog.csdn.net/wang_situ
 * @date 2014-12-30
 */
public class LoginApplication {

	/*  定义静态变量接收 登录成功的数据 后续[买票时]会用到  */
	private static String cookieString;
	private static String login_key;
	private static String login_value;

	public static void main(String[] args) throws Exception {
		login("你的帐号", "你的密码");
	}


	/**
	 * 获取 支持 ssl的ThreadSafeClientConnmanager
	 * 12306用的是 https的协议,所以要做修改
	 */
	public static ThreadSafeClientConnManager getClientManager() throws Exception {
		SSLContext ctx = SSLContext.getInstance("TLS");
		X509TrustManager tm = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain,
										   String authType) throws CertificateException {
			}
			@Override
			public void checkServerTrusted(X509Certificate[] chain,
										   String authType) throws CertificateException {
			}
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		ctx.init(null, new TrustManager[]{tm}, null);
		SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("https", 443, ssf));
		ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(registry);

		return mgr;
	}

	/**
	 * 登录逻辑
	 *
	 * @param username 明文帐号
	 * @param password 明文密码
	 **/
	public static DefaultHttpClient login(String username, String password) throws Exception {

		String url = "https://kyfw.12306.cn/otn/login/init";

		//ProtocolSocketFactory fcty = new MySecureProtocolSocketFactory();
		//Protocol.registerProtocol("https", new Protocol("https", fcty, 443));
		/*初始cookie*/
		String cookie = "_jc_save_detail=true; _jc_save_showZtkyts=true;";
		DefaultHttpClient client = new DefaultHttpClient(getClientManager());
		HttpGet get = new HttpGet(url);
		get.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		get.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
		get.addHeader("Connection", "Keep-Alive");
		get.addHeader("Cookie", cookie);
		get.addHeader("Host", "kyfw.12306.cn");
		get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");
		HttpResponse response = client.execute(get);

		String responseText = EntityUtils.toString(response.getEntity(), "GBK");

		get.abort();
		List<Cookie> list = client.getCookieStore().getCookies();

		for (Cookie c : list) {
			cookie += c.getName() + "=" + c.getValue() + ";";
		}
		System.out.println("HttpHeader Cookie:   " + cookie);

		/* ------ 以上逻辑主要是获取cookie   JSESSIONID 值 */


		/* ------ https://kyfw.12306.cn/otn/login/init 返回的数据会动态的引入一个js  这里主要是截取
		 * 这个js很重要,主要是动态生成一个登录的key和value,没有的话，你的就是第三方非法软件
		 *
		 * */
		int rindex = responseText.indexOf("otn/dynamicJs");
		String jsurl = "";
		String kkky = "";
		String vvvy = "1111";
		if (rindex != -1) {

			String rtxt = responseText.substring(rindex, responseText.length());
			int r33 = rtxt.indexOf("\"");
			jsurl = "https://kyfw.12306.cn/" + rtxt.substring(0, r33);
			HttpGet jsget = new HttpGet(jsurl);
			jsget.addHeader("Accept", "*/*");
			jsget.addHeader("Connection", "keep-alive");
			jsget.addHeader("Cookie", cookie);
			jsget.addHeader("Host", "kyfw.12306.cn");
			jsget.addHeader("Referer", "https://kyfw.12306.cn/otn/login/init");
			jsget.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");

			HttpResponse strRes = client.execute(jsget);


			String strScr = EntityUtils.toString(strRes.getEntity(), "GBK");
			int ri1 = strScr.indexOf("var key='");
			strScr = strScr.substring(ri1, strScr.length());
			int ri2 = strScr.indexOf("';");
			kkky = strScr.substring(9, ri2);
			jsget.abort();
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");
			String mygc = "function kkv(){return encode32(bin216(Base32.encrypt('" + vvvy + "', '" + kkky + "')));}function bin216(s){var i,l,o=\"\",n;s+=\"\";b=\"\";for(i=0,l=s.length;i<l;i++){b=s.charCodeAt(i);n=b.toString(16);o+=n.length<2?\"0\"+n:n}return o};var Base32=new function(){var delta=0x9E3779B8;function longArrayToString(data,includeLength){var length=data.length;var n=(length-1)<<2;if(includeLength){var m=data[length-1];if((m<n-3)||(m>n))return null;n=m}for(var i=0;i<length;i++){data[i]=String.fromCharCode(data[i]&0xff,data[i]>>>8&0xff,data[i]>>>16&0xff,data[i]>>>24&0xff)}if(includeLength){return data.join('').substring(0,n)}else{return data.join('')}};function stringToLongArray(string,includeLength){var length=string.length;var result=[];for(var i=0;i<length;i+=4){result[i>>2]=string.charCodeAt(i)|string.charCodeAt(i+1)<<8|string.charCodeAt(i+2)<<16|string.charCodeAt(i+3)<<24}if(includeLength){result[result.length]=length}return result};this.encrypt=function(string,key){if(string==\"\"){return\"\"}var v=stringToLongArray(string,true);var k=stringToLongArray(key,false);if(k.length<4){k.length=4}var n=v.length-1;var z=v[n],y=v[0];var mx,e,p,q=Math.floor(6+52/(n+1)),sum=0;while(0<q--){sum=sum+delta&0xffffffff;e=sum>>>2&3;for(p=0;p<n;p++){y=v[p+1];mx=(z>>>5^y<<2)+(y>>>3^z<<4)^(sum^y)+(k[p&3^e]^z);z=v[p]=v[p]+mx&0xffffffff}y=v[0];mx=(z>>>5^y<<2)+(y>>>3^z<<4)^(sum^y)+(k[p&3^e]^z);z=v[n]=v[n]+mx&0xffffffff}return longArrayToString(v,false)}};var keyStr=\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=\";function encode32(input){input=escape(input);var output=\"\";var chr1,chr2,chr3=\"\";var enc1,enc2,enc3,enc4=\"\";var i=0;do{chr1=input.charCodeAt(i++);chr2=input.charCodeAt(i++);chr3=input.charCodeAt(i++);enc1=chr1>>2;enc2=((chr1&3)<<4)|(chr2>>4);enc3=((chr2&15)<<2)|(chr3>>6);enc4=chr3&63;if(isNaN(chr2)){enc3=enc4=64}else if(isNaN(chr3)){enc4=64}output=output+keyStr.charAt(enc1)+keyStr.charAt(enc2)+keyStr.charAt(enc3)+keyStr.charAt(enc4);chr1=chr2=chr3=\"\";enc1=enc2=enc3=enc4=\"\"}while(i<input.length);return output};";

			engine.eval(mygc);
			if (engine instanceof Invocable) {
				Invocable invoke = (Invocable) engine;
				String gcKeyValue = invoke.invokeFunction("kkv", null).toString();
				vvvy = gcKeyValue;
			}
		}

		/* 获取验证码,下载到本地  */
		String getCodeUrl = "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=login&rand=sjrand&";

		HttpGet get2 = new HttpGet(getCodeUrl);
		get2.addHeader("Accept", "image/webp,*/*;q=0.8");
		get2.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
		get2.addHeader("Connection", "keep-alive");
		get2.addHeader("Cookie", cookie);
		get2.addHeader("Host", "kyfw.12306.cn");
		get2.addHeader("Referer", "https://kyfw.12306.cn/otn/login/init");
		get2.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");

		HttpResponse resp = client.execute(get2);


		/*  写入硬盘 */
		File storeFile = new File("D:\\12306.jpg");
		FileOutputStream output = new FileOutputStream(storeFile);

		// 得到网络资源的字节数组,并写入文件
		HttpEntity entity = resp.getEntity();
		if (entity != null) {
			InputStream instream = entity.getContent();
			try {
				byte b[] = new byte[1024];
				int j = 0;
				while ((j = instream.read(b)) != -1) {
					output.write(b, 0, j);
				}
				/*篇幅关系,这里就直接在这关闭*/
				output.flush();
				output.close();
				get2.abort();

				//查看硬盘中的验证码,控制台输入 
				System.out.print("验证码下载完毕,请输入: ");
				String vcode = new Scanner(System.in).next();

				System.out.println("您输入的验证码是:" + vcode + "  正在校验...");

				String url2 = "https://kyfw.12306.cn/otn/passcodeNew/checkRandCodeAnsyn";
				HttpPost get3 = new HttpPost(url2);
				cookie = cookie + "current_captcha_type=C;";
				get3.addHeader("Accept", "*/*");
				get3.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
				get3.addHeader("Connection", "keep-alive");
				get3.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
				get3.addHeader("Cookie", cookie);
				get3.addHeader("Host", "kyfw.12306.cn");
				get3.addHeader("Origin", "https://kyfw.12306.cn");
				get3.addHeader("Referer", "https://kyfw.12306.cn/otn/login/init");
				get3.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");
				get3.addHeader("X-Requested-With", "XMLHttpRequest");

				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("randCode", vcode));
				params.add(new BasicNameValuePair("rand", "sjrand"));
				params.add(new BasicNameValuePair("randCode_validate", ""));

				get3.setEntity(new UrlEncodedFormEntity(params));

				HttpResponse r3 = client.execute(get3);

				String r3text = EntityUtils.toString(r3.getEntity(), "GBK");

				if (r3text.indexOf("randCodeRight") != -1) {

					client.execute(get3);    //这里校验2次

					System.out.println("验证码校验成功,正在登录...");
					get3.abort();

					/* ************* 登录 开始 ***************** */
					String loginurl = "https://kyfw.12306.cn/otn/login/loginAysnSuggest";

					HttpPost post1 = new HttpPost(loginurl);

					post1.addHeader("Accept", "*/*");
					post1.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
					post1.addHeader("Connection", "keep-alive");
					post1.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
					post1.addHeader("Cookie", cookie);
					post1.addHeader("Host", "kyfw.12306.cn");
					post1.addHeader("Origin", "https://kyfw.12306.cn");
					post1.addHeader("Referer", "https://kyfw.12306.cn/otn/login/init");
					post1.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");
					post1.addHeader("X-Requested-With", "XMLHttpRequest");

					List<BasicNameValuePair> post1params = new ArrayList<BasicNameValuePair>();
					post1params.add(new BasicNameValuePair("loginUserDTO.user_name", username));
					post1params.add(new BasicNameValuePair("userDTO.password", password));
					post1params.add(new BasicNameValuePair("randCode", vcode));
					post1params.add(new BasicNameValuePair("rand", "sjrand"));
					System.out.println(kkky + " - " + vvvy);

					/* 这个东西很重要,这个东西就是 动态的一个表单 */
					post1params.add(new BasicNameValuePair(kkky, vvvy));
					post1params.add(new BasicNameValuePair("myversion", "undefined"));


					post1.setEntity(new UrlEncodedFormEntity(post1params));

					HttpResponse r4 = client.execute(post1);


					String ff = EntityUtils.toString(r4.getEntity(), "UTF-8");
					post1.abort();
					String successResult = "{\"validateMessagesShowId\":\"_validatorMessage\",\"status\":true,\"httpstatus\":200,\"data\":{\"loginCheck\":\"Y\"},\"messages\":[],\"validateMessages\":{}}";
					if (ff.equals(successResult)) {
						System.out.println("登录成功   " + successResult);

						cookieString = cookie;
						login_key = kkky;
						login_value = vvvy;

						return client;
					} else {
						System.out.println("登录失败   " + ff);
					}

				} else {
					System.out.println("验证码校验失败");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("下载图片失败");
			}
		}
		return null;
	}
}
 
