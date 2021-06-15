package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.VO.MsgVO;
import com.example.demo.mapper.MsgMapper;

@Service
public class MsgService {	

	@Autowired
    private MsgMapper msgMapper;
    
	@Transactional
    public void saveMsg(MsgVO msgVO) {
    	msgMapper.msgSave(msgVO);
    }
    
    public List<Map<String,Object>> getAllFromLastMessage(String msg_to) {
    	return msgMapper.getAllFromLastMessage(msg_to);
    }    
    
    public List<Map<String,Object>> getConversationRecord(String msg_from, String msg_to) {
    	return msgMapper.getConversationRecord(msg_from, msg_to);
    }   
    
    public List<Map<String,Object>> getUnreadCount(String msg_to) {
    	return msgMapper.getUnreadCount(msg_to);
    }     
    
    
    public List<Map<String,Object>> searchKeyword(MsgVO msgVO) {
    	return msgMapper.searchKeyword(msgVO);
    }       
    
    @Transactional
    public void msgUpdateStatus(String msg_from, String msg_to) {
    	msgMapper.msgUpdateStatus(msg_from, msg_to);
    }    
}
