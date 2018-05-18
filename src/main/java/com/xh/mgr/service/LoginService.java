package com.xh.mgr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.xh.mgr.util.AESUtil;
import com.xh.mgr.util.Md5Util;

@Service
public class LoginService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	static Logger logger = LoggerFactory.getLogger("LoginService");
	public boolean checkUser(String userName,String userPwd,String key){
//		String md5Pwd = Md5Util.generatePassword(userPwd);
		int i = jdbcTemplate.queryForInt(AESUtil.getInstance().decrypt("2WGSl+Q6d8KHmH8jpvHtXqEOSorZRhuCUnsKI8V4OIUEJ9aFwd6slq1hfNxuWY4E31PjSzX1aCz3CcXQDpMce+f0CYZAocDbZwSNK7WAG0+w6vaGnZ718F6lq6rfcWWY",key), userName,userPwd);
		return i>0;
	}
	public String getUserRole(String userName){
		String userRole = "";
		userRole = jdbcTemplate.queryForInt("select max(user_role) from user_login where user_name = ?", userName)+"";
		return userRole;
	}
	public static void main(String[] args) {
		String md5Pwd = Md5Util.generatePassword("admin");
		System.out.println(md5Pwd);
//		select count(1) from user_login where user_name = ? and user_password = ? and is_delete = 0 "
	}
}
