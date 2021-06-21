package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.IContent;
import com.example.demo.service.RoomService;
import com.example.demo.vo.GameVO;
import com.example.demo.vo.RoomVO;


@RestController
@RequestMapping("/api/casino/")
public class CasinoAPIService {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private RoomService roomService2;	
	
	@PutMapping(path = "v1/initRoom/{roomNO}", produces = "application/json;charset=utf-8")
	public Map<String,Object> initRoom(HttpSession session,@PathVariable String roomNO) throws Exception {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String loginID = "";
		if (principal instanceof UserDetails) {
			loginID = ((UserDetails) principal).getUsername();
		} else {
			loginID = principal.toString();
		}
		logger.info("User:{}, do initRoom", loginID);
		
		//創建房間者為莊家
		RoomService roomService = RoomService.getInstance();
		RoomVO roomVO = roomService.getRoom(roomNO);
		roomVO.setAdmID(loginID);		
	
		Map<String, Object> result = new HashMap<String, Object>();
		List<String> message = new ArrayList<String>();
		result.put("RETCODE", "999");
		result.put("RET", false);
		if(roomVO.getAdmID().contentEquals(loginID)) {
			GameVO vo = roomService2.initRoom(roomNO, loginID);
			if(null!=vo) {
				result.put("RETCODE", "000");
				result.put("RET", true);
			}
		}else {
			message.add("授權失敗！");
		}
		
		result.put("MSG", message);
		return result;
	}
	@PutMapping(path = "v1/runGame/{roomNO}", produces = "application/json;charset=utf-8")
	public Map<String,Object> runGame(HttpSession session,@PathVariable String roomNO) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("RETCODE", "999");
		result.put("RET", false);
		RoomService roomService = RoomService.getInstance();
		List<String> errMsg = new ArrayList<String>();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String loginID = "";
		if (principal instanceof UserDetails) {
			loginID = ((UserDetails) principal).getUsername();
		} else {
			loginID = principal.toString();
		}
		logger.info("User:{}, do Drawing", loginID);
		
		if(StringUtils.isBlank(roomNO)) {
			errMsg.add("房間編號空白！");
		}
		
		if(errMsg.isEmpty()) {
			RoomVO roomVO = roomService.getRoom(roomNO);
			if(!roomVO.getAdmID().contentEquals(loginID)) {
				errMsg.add("權限不足，執行者與房間管理者不相同！");
			}else {
				GameVO vo = roomService.doRunning(roomNO);
			}
		}
		if(errMsg.isEmpty()) {
			result.put("RETCODE", "000");
			result.put("RET", true);
		}
		result.put("MSG", errMsg);
		return result;
		
	}
	@PutMapping(path = "v1/startBetting/{roomNO}", produces = "application/json;charset=utf-8")
	public Map<String,Object> startBetting(HttpSession session,@PathVariable String roomNO) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("RETCODE", "999");
		result.put("RET", false);
		RoomService roomService = RoomService.getInstance();
		List<String> errMsg = new ArrayList<String>();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String loginID = "";
		if (principal instanceof UserDetails) {
			loginID = ((UserDetails) principal).getUsername();
		} else {
			loginID = principal.toString();
		}
		logger.info("User:{}, do Drawing", loginID);
		
		if(StringUtils.isBlank(roomNO)) {
			errMsg.add("房間編號空白！");
		}
		
		if(errMsg.isEmpty()) {
			RoomVO roomVO = roomService.getRoom(roomNO);
			if(!roomVO.getAdmID().contentEquals(loginID)) {
				errMsg.add("權限不足，執行者與房間管理者不相同！");
			}else {
				GameVO vo = roomService2.doBetting(roomNO);
			}
		}
		if(errMsg.isEmpty()) {
			result.put("RETCODE", "000");
			result.put("RET", true);
		}
		result.put("MSG", errMsg);
		return result;
		
	}
	@PutMapping(path = "v1/stopBetting/{roomNO}", produces = "application/json;charset=utf-8")
	public Map<String,Object> stopBetting(HttpSession session,@PathVariable String roomNO) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("RETCODE", "999");
		result.put("RET", false);
		RoomService roomService = RoomService.getInstance();
		List<String> errMsg = new ArrayList<String>();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String loginID = "";
		if (principal instanceof UserDetails) {
			loginID = ((UserDetails) principal).getUsername();
		} else {
			loginID = principal.toString();
		}
		logger.info("User:{}, do Drawing", loginID);
		
		if(StringUtils.isBlank(roomNO)) {
			errMsg.add("房間編號空白！");
		}
		
		if(errMsg.isEmpty()) {
			RoomVO roomVO = roomService.getRoom(roomNO);
			if(!roomVO.getAdmID().contentEquals(loginID)) {
				errMsg.add("權限不足，執行者與房間管理者不相同！");
			}else {
				GameVO gameVO = roomService2.stopBetting(roomNO);
			}
			
		}
		if(errMsg.isEmpty()) {
			result.put("RETCODE", "000");
			result.put("RET", true);
		}
		result.put("MSG", errMsg);
		return result;
	}
	@PutMapping(path = "v1/stopDrawing/{roomNO}", produces = "application/json;charset=utf-8")
	public Map<String,Object> stopDrawing(HttpSession session,@PathVariable String roomNO) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("RETCODE", "999");
		result.put("RET", false);
		RoomService roomService = RoomService.getInstance();
		List<String> errMsg = new ArrayList<String>();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String loginID = "";
		if (principal instanceof UserDetails) {
			loginID = ((UserDetails) principal).getUsername();
		} else {
			loginID = principal.toString();
		}
		logger.info("User:{}, do Drawing", loginID);
		
		if(StringUtils.isBlank(roomNO)) {
			errMsg.add("房間編號空白！");
		}
		
		if(errMsg.isEmpty()) {
			RoomVO roomVO = roomService.getRoom(roomNO);
			if(!roomVO.getAdmID().contentEquals(loginID)) {
				errMsg.add("權限不足，執行者與房間管理者不相同！");
			}else {
				GameVO gameVO = roomService2.stopDrawing(roomNO);
			}
			
		}
		if(errMsg.isEmpty()) {
			result.put("RETCODE", "000");
			result.put("RET", true);
		}
		result.put("MSG", errMsg);
		return result;
	}
	@PutMapping(path = "v1/createNewGame/{roomNO}", produces = "application/json;charset=utf-8")
	public Map<String,Object> createNewGame(HttpSession session,@PathVariable String roomNO) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("RETCODE", "999");
		result.put("RET", false);		
		RoomService roomService = RoomService.getInstance();
		List<String> errMsg = new ArrayList<String>();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String loginID = "";
		if (principal instanceof UserDetails) {
			loginID = ((UserDetails) principal).getUsername();
		} else {
			loginID = principal.toString();
		}
		logger.info("User:{}, do Drawing", loginID);
		
		if(StringUtils.isBlank(roomNO)) {
			errMsg.add("房間編號空白！");
		}
		
		
		if(errMsg.isEmpty()) {
			RoomVO roomVO = roomService.getRoom(roomNO);
			if(!roomVO.getAdmID().contentEquals(loginID)) {
				errMsg.add("權限不足，執行者與房間管理者不相同！");
			}else {
				GameVO gameVO = roomService2.doCreateGame(roomNO, loginID);				
			}
			
		}
		if(errMsg.isEmpty()) {
			result.put("RETCODE", "000");
			result.put("RET", true);
		}
		result.put("MSG", errMsg);
		return result;
		
	}
//	@PutMapping(path = "v1/doDrawing/{roomNO}/{winnerPool}", produces = "application/json;charset=utf-8")
//	public Map<String,Object> doDrawing(HttpSession session,@PathVariable String roomNO,@PathVariable String winnerPool) throws Exception {
	@PutMapping(path = "v1/doDrawing/{roomNO}", produces = "application/json;charset=utf-8")
	@ResponseBody
	public Map<String,Object> doDrawing(HttpSession session,@PathVariable String roomNO,@RequestBody LinkedHashMap<String,Integer> resultMap) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("RETCODE", "999");
		result.put("RET", false);		
		RoomService roomService = RoomService.getInstance();
		List<String> errMsg = new ArrayList<String>();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String loginID = "";
		if (principal instanceof UserDetails) {
			loginID = ((UserDetails) principal).getUsername();
		} else {
			loginID = principal.toString();
		}
		logger.info("User:{}, do Drawing", loginID);
		
		if(StringUtils.isBlank(roomNO)) {
			errMsg.add("房間編號空白！");
		}
//		if(StringUtils.isBlank(winnerPool)) {
//			errMsg.add("勝利結果空白！");
//		}
//		
		if(errMsg.isEmpty()) {
			RoomVO roomVO = roomService.getRoom(roomNO);
			if(!roomVO.getAdmID().contentEquals(loginID)) {
				errMsg.add("權限不足，執行者與房間管理者不相同！");
			}else {
				GameVO gameVO = roomService2.doDrawing(roomNO, resultMap, loginID);
				
			}
			
		}
		if(errMsg.isEmpty()) {
			result.put("RETCODE", "000");
			result.put("RET", true);
		}
		result.put("MSG", errMsg);
		return result;
	}
	
	@PutMapping(path = "v1/closeRoom/{roomNO}", produces = "application/json;charset=utf-8")
	public Map<String,Object> closeRoom(HttpSession session,@PathVariable String roomNO) throws Exception {
		RoomService roomService = RoomService.getInstance();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String loginID = "";
		if (principal instanceof UserDetails) {
			loginID = ((UserDetails) principal).getUsername();
		} else {
			loginID = principal.toString();
		}
		logger.info("User:{}, do createNewGame", loginID);
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("RETCODE", "999");
		result.put("RET", false);
		RoomVO roomVO = roomService.doCloseRoom(roomNO);
		if(null!=roomVO) {
			result.put("RETCODE", "000");
			result.put("RET", true);
		}
		result.put("MSG", new ArrayList<String>());
		return result;
	}
}
