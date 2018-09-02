package cn.yangtengfei.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HtmlController {
	@GetMapping("/")
	public String html() {
		return "/index.html";
	}
}
