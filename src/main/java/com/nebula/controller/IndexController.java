package com.nebula.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by hzq on 2017/7/28.
 */
@Controller
public class IndexController {

	private static Logger logger = LoggerFactory.getLogger(IndexController.class);

	@RequestMapping({"", "/", "/index"})
	public String thymeleafIndex(Model model){
		model.addAttribute("index", "thymeleaf首页");
		return "index";
	}

}
