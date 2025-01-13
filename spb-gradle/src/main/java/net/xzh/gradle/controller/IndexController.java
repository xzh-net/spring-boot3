package net.xzh.gradle.controller;

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
		return "这是一个gradle demo。"+ System.currentTimeMillis();
	}

}