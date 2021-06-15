package com.example.demo.service;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.mapper.BetRecordsMapper;

@Service
public class BetRecordsService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
    private BetRecordsMapper betRecordsMapper;
	
    public List<Map<String,Object>> getBetRecordsByID(String loginID) {    	
    	return betRecordsMapper.getBetRecordsByID(loginID);
    }   	
	
}