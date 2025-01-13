package net.xzh.gradledemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class IndexController {

	/**
	 * 登录页
	 */
	@RequestMapping(value = "/index")
	@ResponseBody
	public String index() {
		return "Hello，admin "+ System.currentTimeMillis();
	}

}