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
import com.example.demo.service.MemberService;
import com.example.demo.service.RoomService;
import com.example.demo.vo.GameVO;
import com.example.demo.vo.RoomVO;
import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
public class MainController {
	@Autowired
    private MemberService memberService;		

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
		
		//
		memberService.getMemberById(loginID);
		
		if(!IContent.ROOM_STATUS_NORMAL.contentEquals(roomVO.getStatus())) {
			return "room_closed";
		}else {
			return "room";
		}
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
		
		//
		memberService.setAdmin(loginID);
		
		GameVO gameVO = (roomVO.getGames()==null || roomVO.getGames().isEmpty())?null:roomVO.getGames().get(roomVO.getGames().size()-1);
		model.addAttribute("roomVO", roomVO);
		model.addAttribute("gameVO", gameVO);
		return "backend";
	}
}
