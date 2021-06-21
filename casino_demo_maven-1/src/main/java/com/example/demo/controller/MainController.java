package com.example.demo.controller;

import java.text.ParseException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.IContent;
import com.example.demo.service.BetRecordsService;
import com.example.demo.service.UserService;
import com.example.demo.service.RoomService;
import com.example.demo.vo.GameVO;
import com.example.demo.vo.RoomVO;
import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
public class MainController {
	@Autowired
    private UserService userService;
	
	@Autowired
    private BetRecordsService betRecordsService;	

	@RequestMapping(value = { "", "/index.html" },method = RequestMethod.GET)
	public String index(HttpSession session, Model model) throws JsonProcessingException, ParseException {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String loginID = "";
		if (principal instanceof UserDetails) {
			loginID = ((UserDetails) principal).getUsername();
		} else {
			loginID = principal.toString();
		}		
		return "index";
	}
	@RequestMapping(value = { "/room.html" },method = RequestMethod.GET)
	public String room(HttpSession session, Model model,@RequestParam String roomNO) throws JsonProcessingException, ParseException {
		RoomService roomService = RoomService.getInstance();
		RoomVO roomVO = roomService.getRoom(roomNO);
		//model.addAttribute("roomNO", roomNO);
		model.addAttribute("roomVO",roomVO);
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String loginID = "";
		if (principal instanceof UserDetails) {
			loginID = ((UserDetails) principal).getUsername();
		} else {
			loginID = principal.toString();
		}
		//儲存User資料至user map中
		userService.storeUserDataById(loginID);
		
		if(!IContent.ROOM_STATUS_NORMAL.contentEquals(roomVO.getStatus())) {
			return "room_closed";
		}else {
			model.addAttribute("gameVO",roomVO.getLastGame());
			return "room";
		}
	}
	
	@RequestMapping(value = { "/showRecords.html" },method = RequestMethod.GET)
	public String showRecords(HttpSession session, Model model) throws JsonProcessingException, ParseException {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String loginID = "";
		if (principal instanceof UserDetails) {
			loginID = ((UserDetails) principal).getUsername();
		} else {
			loginID = principal.toString();
		}
		model.addAttribute("records", betRecordsService.getBetRecordsByID(loginID));		
		return "showRecords";
	}	
	
	@RequestMapping(value = { "/backend.html" },method = RequestMethod.GET)
	public String backend(HttpSession session, Model model,@RequestParam String roomNO) {
		RoomService roomService = RoomService.getInstance();
		RoomVO roomVO = roomService.getRoom(roomNO);
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String loginID = "";
		if (principal instanceof UserDetails) {
			loginID = ((UserDetails) principal).getUsername();
		} else {
			loginID = principal.toString();
		}		
		//紀錄莊家至map中管理
		userService.storeUserDataById(loginID);		
		GameVO gameVO = (roomVO.getGames()==null || roomVO.getGames().isEmpty())?null:roomVO.getGames().get(roomVO.getGames().size()-1);
		model.addAttribute("roomVO", roomVO);
		model.addAttribute("gameVO", gameVO);
		return "backend";
	}
}
