package com.example.demo.service;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.UserMapper;
import com.example.demo.vo.UserBean;

@Service
public class UserService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static Map<String, UserBean> users = new HashMap<String, UserBean>();
//	private static MemberService _MEMSERVICE;
	
	@Autowired
    private UserMapper userMapper;	
	
//	private MemberService() {
//		UserBean test1 = new UserBean("MEM00001", "Tester 1", "test1", (float)5000);
//		UserBean test2 = new UserBean("MEM00002", "Tester 2", "test2", (float)5000);
//		UserBean test3 = new UserBean("MEM00003", "Tester 3", "test3", (float)5000);
//		UserBean test4 = new UserBean("MEM00004", "Tester 4", "test4", (float)5000);
//		UserBean test5 = new UserBean("MEM00005", "Tester 5", "test5", (float)5000);
//		UserBean test6 = new UserBean("MEM00006", "Tester 6", "test6", (float)5000);
//		UserBean admin = new UserBean("MEM99999", "Admin", "admin", (float)5000);
//		
//		mems.put("test1", test1);
//		mems.put("test2", test2);
//		mems.put("test3", test3);
//		mems.put("test4", test4);
//		mems.put("test5", test5);
//		mems.put("test6", test6);
//		mems.put("admin", admin);
//	}
	public void storeUserDataById(String loginId) {
		if(null == users.get(loginId)) {
			Map<String,Object> userMap = userMapper.getMemberById(loginId);
			String userName = (String) userMap.get("userName");
			float money = (float) userMap.get("wallet");
			UserBean userBean = new UserBean(loginId, userName, money);
			users.put(loginId, userBean);			
		}
	}
	
//	public void setAdmin(String loginId) {
//		UserBean admin = new UserBean(loginId, "Admin", (float)5000);
//		mems.put(loginId, admin);
//	}	
	
//	public static MemberService getInstance() {
//		if(null==MemberService._MEMSERVICE) {
//			MemberService._MEMSERVICE = new MemberService();
//		}
//		return MemberService._MEMSERVICE;
//	}
	
	public UserBean getUserByLoginID(String loginID) {
		return users.get(loginID);
	}
	
	public Float getMoney(String loginID) {
		return users.get(loginID).getMoney();
	}
	public int countMem() {
		return this.users.size();
	}
	public Float plusMoney(String loginID, Float money) {
		UserBean user = users.get(loginID);
		user.setMoney(user.getMoney()+money);
		return user.getMoney();
	}
}