

package cn.yangtengfei;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {


	/*@Bean
	public WebSecurityConfigurerAdapter webSecurityConfigurerAdapter() {
		return new ApplicationSecurity();
	}*/
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

}
