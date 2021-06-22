package com.example.demo.controller;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.example.demo.IContent;
import com.example.demo.service.UserService;
import com.example.demo.service.RoomService;
import com.example.demo.service.WebSocketSessions;
import com.example.demo.vo.BetBakMessageVO;
import com.example.demo.vo.BetVO;
import com.example.demo.vo.GameVO;
import com.example.demo.vo.MyPrincipal;
import com.example.demo.vo.RespMessageVO;
import com.example.demo.vo.WalletVO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Controller
public class CasinoWSController {
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
	@Autowired
	private WebSocketSessions webSocketSessions;
//	private int rate = 100;
	
	@MessageMapping("/casino/bet")
    public void bet(BetVO betVO,Principal principal) {
		RoomService roomService = RoomService.getInstance();
		UserService memService = new UserService();
		MyPrincipal user = (MyPrincipal)principal;
		String loginID = user.getLoginID();
		String roomNO = betVO.getRoomNO();
		GameVO gameVO = roomService.getRoom(roomNO).getLastGame();
		betVO.setLoginID(user.getLoginID());
		betVO.setMemID(user.getMemID());
		betVO.setBet_time(new Timestamp(System.currentTimeMillis()));
		if(gameVO.getStatus().contentEquals(IContent.GAME_STATUS_BETTING)) {
			roomService.addBet(roomNO, betVO);
			memService.plusMoney(loginID, betVO.getAmount()*-1);
			sendBetMessage(roomNO, gameVO);				
		}else {
			logger.info("遊戲非在壓注狀態，而在{}", gameVO.getStatus());
		}
    }
	
	@MessageMapping("/casino/getStatus")
    public void getRoomStatus(String message,Principal principal) {
		JsonObject jsonObj = new Gson().fromJson(message, JsonObject.class);
		String roomNO = jsonObj.get("roomNO").getAsString();
		RoomService roomService = RoomService.getInstance();
		GameVO gameVO = roomService.getRoom(roomNO).getLastGame();
		sendBetMessage(roomNO, gameVO);		
    }

	
	@MessageMapping("/casino/getMyWalletAmount")
    public void getMyWalletAmount(Principal principal) {
		UserService memService = new UserService(); 
		MyPrincipal user = (MyPrincipal)principal;
		String loginID = user.getLoginID();
		float myWalletAmount = memService.getMoney(loginID);
		RespMessageVO respMsgVO = new RespMessageVO("MY_WALLET_AMOUNT", new WalletVO(String.valueOf(myWalletAmount)));
		simpMessagingTemplate.convertAndSendToUser(loginID, "/topic/getMyWallet", respMsgVO);		
    }
	
	private  synchronized BetBakMessageVO convertBetBak(GameVO gameVO) {
		int totalAmount = 0;
		BetBakMessageVO betBak = new BetBakMessageVO();
		betBak.setRoomNO(gameVO.getRoomNO());
		betBak.setGameStatus(gameVO.getStatus());
		betBak.setLimit_amount(gameVO.getLimit_amount());
		Map<String, List<BetVO>> records = gameVO.getRecords();
		Iterator<String> keyIter = records.keySet().iterator();
		while(keyIter.hasNext()) {
			int amount = 0;
			String poolName = keyIter.next();
			List<BetVO> bets = records.get(poolName);
			for(BetVO bet:bets) {
				amount+=bet.getAmount();
			}
			totalAmount+=amount;
			betBak.putPool(poolName, String.valueOf(amount));
		}
		betBak.setAmount(String.valueOf(totalAmount));
		return betBak;
	}
	
    public void sendBetMessage(String roomNO,GameVO gameVO) {		
		List<String> wsSessIDs = webSocketSessions.getMems(roomNO);	
		RespMessageVO respMsgVO = new RespMessageVO("BET_STATUS", convertBetBak(gameVO));
		for(String sessID:wsSessIDs) {
			simpMessagingTemplate.convertAndSendToUser(sessID, "/topic/roomStatus", respMsgVO);
		}
    }	
}
