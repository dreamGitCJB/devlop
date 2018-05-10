package com.chyu.www.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chyu.www.Application;
import com.chyu.www.domain.AdminAccount;
import com.chyu.www.service.AdminAccountService;
import com.chyu.www.util.MD5Util;

@Controller
@RequestMapping("/Admin")
public class AdminController {
	
	@Autowired
	AdminAccountService adminAccountService;
	/**
	 * 登陆
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="adminLogin")
	public String showAdminLogin(HttpServletRequest request,HttpServletResponse response,ModelMap model) throws Exception {
		int step = ServletRequestUtils.getIntParameter(request, "step", 1);
		if (step == 1) {
			return "admin_login";
		}
		String username = ServletRequestUtils.getStringParameter(request, "username");
		String password = ServletRequestUtils.getStringParameter(request, "password");
		AdminAccount adminAccount =  adminAccountService.gainAdminAccountByUsername(username);
		if (adminAccount == null) {
			throw new Exception("用户名不存在");
		}
		if (!adminAccount.getPassword().equals(MD5Util.MD5(password))) {
			throw new Exception("密码错误");
		}
		request.getSession().setAttribute(Application.SESSION_NAME, adminAccount);
		//设置超时时间
		request.getSession().setMaxInactiveInterval(Application.SESSION_TIMEOUT);
		model.addAttribute("adminAccount", adminAccount);
		return "admin_main";
	}
}
