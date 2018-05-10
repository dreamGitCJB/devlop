package com.chyu.www.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.chyu.www.dao.AdminAccoutInterfaceDao;
import com.chyu.www.domain.AdminAccount;

@Service
public class AdminAccountService {
	private static String KEY_ADMIN_ACCOUNT="admin_accounts";
	private static String HASH_KEY_ADMIN_ACCOUNT = "admin_account_%s";
	private String hashKey(String hashkey,String username) {
		return  String.format(hashkey, username);
	}
	
	private static String TASKQUEUENAME="adminAccountTask";
	
	@Autowired
	AsyncRedisToDbService asyncRedisToDbService;
	
	@Autowired
	AdminAccoutInterfaceDao adminAccoutInterfaceDao;
	
	@Autowired
	RedisTemplate redisTemplate;
	/**
	 * 添加账号
	 * @param adminAccount
	 */
	public void addAdminAccount(AdminAccount adminAccount) {
		int result = asyncRedisToDbService.asyncAddTask(TASKQUEUENAME, "AdminAccoutInterfaceDao", "addAdminAccount", adminAccount);
		asyncRedisToDbService.asyncSubmit(TASKQUEUENAME);
		if (result > 0) {
			redisTemplate.opsForHash().put(KEY_ADMIN_ACCOUNT, hashKey(HASH_KEY_ADMIN_ACCOUNT, adminAccount.getUsername()), adminAccount);
		}
	}
	
	/**
	 * 修改账号
	 * @param adminAccount
	 */
	public void modAdminAccount(AdminAccount adminAccount) {
		int result = asyncRedisToDbService.asyncAddTask(TASKQUEUENAME, "AdminAccoutInterfaceDao", "modAdminAccount", adminAccount);
		asyncRedisToDbService.asyncSubmit(TASKQUEUENAME);
		if (result > 0) {
			redisTemplate.opsForHash().put(KEY_ADMIN_ACCOUNT, hashKey(HASH_KEY_ADMIN_ACCOUNT, adminAccount.getUsername()), adminAccount);
		}
	}
	/**
	 * 根据 username 获取管理员
	 * @param username
	 * @return
	 */
	public AdminAccount gainAdminAccountByUsername(String username) {
		Object value = redisTemplate.opsForHash().get(KEY_ADMIN_ACCOUNT, hashKey(HASH_KEY_ADMIN_ACCOUNT, username));
		AdminAccount adminAccount = (AdminAccount)value;
		if (value == null) {
			adminAccount = adminAccoutInterfaceDao.gainAdminAccountByUsername(username);
			if (adminAccount != null) {
				redisTemplate.opsForHash().put(KEY_ADMIN_ACCOUNT, hashKey(HASH_KEY_ADMIN_ACCOUNT, adminAccount.getUsername()), adminAccount);
			}
		}
		return adminAccount;
	}
}
