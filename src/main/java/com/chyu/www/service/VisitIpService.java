package com.chyu.www.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chyu.www.domain.Visits;
/**
 * 
 * @author chenjinbao
 *
 */
@Service
public class VisitIpService {
	private static String TASKQUEUENAME = "visitIpTask";
	
	@Autowired
	AsyncRedisToDbService asyncRedisToDbService;
	
	public int addVisitId(Visits visitIp) {
		int	result = asyncRedisToDbService.asyncAddTask(TASKQUEUENAME, "VisitIpInterfaceDao", "addVisitIp",
				visitIp);
		asyncRedisToDbService.asyncSubmit(TASKQUEUENAME);
		return result;
		
	}
}
