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
    public void msgSave(MsgVO msgVO) {
    	msgMapper.msgSave(msgVO);
    }
	
	@Transactional
    public void msgImgSave(MsgVO msgVO) {
    	msgMapper.msgImgSave(msgVO);
    }	
    
//    public List<Map<String,Object>> getAllFromLastMessage(String msg_to) {
//    	return msgMapper.getAllFromLastMessage(msg_to);
//    }    
    public List<MsgVO> getAllFromLastMessage(String msg_to) {
    	return msgMapper.getAllFromLastMessage(msg_to);
    }        

//    public List<Map<String,Object>> getConversationRecord(String msg_from, String msg_to) {
//    	return msgMapper.getConversationRecord(msg_from, msg_to);
//    }
    
    public List<MsgVO> getConversationRecord(String msg_from, String msg_to) {
    	return msgMapper.getConversationRecord(msg_from, msg_to);
    }   
           
    public List<Map<String,Object>> getUnreadCount(String msg_to) {
    	return msgMapper.getUnreadCount(msg_to);
    }     
    
    
    public List<Map<String,Object>> searchKeyword(MsgVO msgVO) {
    	return msgMapper.searchKeyword(msgVO);
    }    
    
    public Map<String,Object> getOwnInfo(String userID) {
    	return msgMapper.getOwnInfo(userID);
    }        
    
    @Transactional
    public void msgUpdateStatus(String msg_from, String msg_to) {
    	msgMapper.msgUpdateStatus(msg_from, msg_to);
    }    
}
