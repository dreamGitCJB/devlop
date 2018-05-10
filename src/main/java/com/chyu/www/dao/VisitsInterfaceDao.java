package com.chyu.www.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.chyu.www.domain.Visits;
@Repository("VisitsInterfaceDao")
@Mapper
public interface VisitsInterfaceDao {
	
	int addVisitIp(Visits visitIp);
}
