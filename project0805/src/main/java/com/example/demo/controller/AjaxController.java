package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.example.demo.IContent;
import com.example.demo.VO.MsgVO;
import com.example.demo.service.MsgService;

@RestController
@RequestMapping("/member/")
public class AjaxController {

    @Autowired
    private MsgService msgService;	
    
    @PostMapping("getMemberPhoto")
    public MsgVO getMemberPhoto(@RequestParam String msg_from) {  
    	MsgVO msgVO = msgService.getMemeberPhoto(msg_from);
        return msgService.getMemeberPhoto(msg_from);
    }   
    
    @PostMapping("searchKeyword")
    public List<Map<String, Object>> searchKeyword(@RequestParam String keyword,@RequestParam String userID) {
    	MsgVO msgVO = new MsgVO();            	
    	msgVO.setMsg_from(userID);
    	msgVO.setMsg_content("%" + keyword + "%");             
    	
        List<Map<String, Object>> msg_keyword_list = msgService.searchKeywordCount(msgVO);
//        Map<String, Object> msg_keyword_map = new HashMap<>();
//        msg_keyword_map.put(IContent.SHOWKEYWORD, msg_keyword_list);                
//        sendMessageTo(JSON.toJSONString(msg_keyword_map), msgVO_3.getMsg_from());    	
//    	System.out.print(keyword);
        return msg_keyword_list;
    }      
}
