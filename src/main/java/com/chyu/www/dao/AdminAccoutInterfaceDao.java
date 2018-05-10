package com.chyu.www.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.chyu.www.domain.AdminAccount;
/**
 * 管理员账号 DAO
 * @author chenjinbao
 *
 */
@Repository("AdminAccoutInterfaceDao")
@Mapper
public interface AdminAccoutInterfaceDao {
	/**
	 * 添加管理员 账号
	 * @param adminAccount
	 * @return
	 */
	int addAdminAccount(AdminAccount adminAccount);
	
	/**
	 * 修改管理员账号
	 * @param adminAccount
	 * @return
	 */
	int modAdminAccount(AdminAccount adminAccount);
	
	/**
	 * 查询 管理员账号
	 * @param username
	 * @return
	 */
	@Select("select * from admin_account where username=#{username}")
	AdminAccount gainAdminAccountByUsername(@Param("username") String username);
	
}
