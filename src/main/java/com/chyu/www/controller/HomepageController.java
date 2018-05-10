package com.chyu.www.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chyu.www.annotation.RequestLimit;
import com.chyu.www.domain.Visits;
import com.chyu.www.service.VisitIpService;
import com.chyu.www.util.DateStyle;
import com.chyu.www.util.DateUtil;

@Controller
@RequestMapping("/HomepageController")
public class HomepageController {
	@Autowired
	VisitIpService visitsService;
	/**
	 * 首页
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestLimit()
	@RequestMapping(params="showHomePage")
	public String showHomePage(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String visit_ip = request.getRemoteHost();
	    String visit_time = DateUtil.DateToString(new Date(), DateStyle.YYYY_MM_DD_HH_MM_SS);
		Visits visits = new Visits();
		visits.setVisit_ip(visit_ip);
		visits.setVisit_time(visit_time);
		visitsService.addVisitId(visits);
	    return "index";
	}
}
