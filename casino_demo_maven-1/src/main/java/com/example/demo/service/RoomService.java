package com.example.demo.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.IContent;
import com.example.demo.mapper.BetRecordsMapper;
import com.example.demo.mapper.GameRecordsMapper;
import com.example.demo.mapper.ManagerMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.vo.BetVO;
import com.example.demo.vo.GameVO;
import com.example.demo.vo.RoomVO;
import com.example.demo.vo.UserBean;

@Service
public class RoomService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Map<String,RoomVO> rooms ;
	private static RoomService _ROOM;
	
	@Autowired
    private GameRecordsMapper gameRecordsMapper;
	
	@Autowired
    private BetRecordsMapper betRecordsMapper;	

	@Autowired
    private UserMapper userMapper;
	
	@Autowired
    private ManagerMapper managerMapper;	
	
	public static RoomService getInstance() {
		if(null==RoomService._ROOM) {
			RoomService._ROOM = new RoomService();
			_ROOM.rooms = new HashMap<String,RoomVO>();
			RoomVO room1 = new RoomVO();
//			room1.setAdmID("admin");
			room1.setNo("ROOM1");
			room1.setId("1");
			room1.setOwnerID("admin");	
			room1.setStatus(IContent.ROOM_STATUS_INIT);
			_ROOM.rooms.put("ROOM1", room1);
			RoomVO room2 = new RoomVO();
//			room2.setAdmID("admin");
			room2.setNo("ROOM2");
			room2.setId("2");
			room2.setOwnerID("admin");	
			room2.setStatus(IContent.ROOM_STATUS_INIT);
			_ROOM.rooms.put("ROOM2", room2);
			RoomVO room3 = new RoomVO();
//			room3.setAdmID("admin");
			room3.setNo("ROOM3");
			room3.setId("3");
			room3.setOwnerID("admin");
			room3.setStatus(IContent.ROOM_STATUS_INIT);
			_ROOM.rooms.put("ROOM3", room3);			
		}
		return RoomService._ROOM;
	}
	public RoomVO getRoom(String roomNO) {
		return this.rooms.get(roomNO);
	}
	public GameVO initRoom(String roomNO) {		
		_ROOM.rooms.get(roomNO).setGames(new ArrayList<GameVO>());
		_ROOM.rooms.get(roomNO).setStatus(IContent.ROOM_STATUS_NORMAL);
		GameVO  game = doCreateGame(roomNO);
		return game;
	}
	public void closeRoom(String roomNO) {
		_ROOM.rooms.get(roomNO).setStatus(IContent.ROOM_STATUS_CLOSE);
	}
	public GameVO doCreateGame(String roomNO) {
		List<GameVO> gameList = _ROOM.rooms.get(roomNO).getGames();
		GameVO game = new GameVO();
//		game.setId(gameList.size()+1);	
		game.setRoom_id(Integer.valueOf(roomNO.substring(roomNO.length()-1)));
		gameRecordsMapper.gameRecordsSave(game);						
		game.setRoomNO(roomNO);
		game.setStatus(IContent.GAME_STATUS_INIT);
		Map<String,List<BetVO>> table =new HashMap<String,List<BetVO>>(); 
		table.put("A", new ArrayList<BetVO>());
		table.put("B", new ArrayList<BetVO>());
		table.put("C", new ArrayList<BetVO>());
		table.put("D", new ArrayList<BetVO>());
		game.setRecords(table);
		game.setLimit_amount(3000);
		_ROOM.rooms.get(roomNO).addGame(game);
		return game;
	}
	public GameVO doRunning(String roomNO) {
		RoomVO roomVO = _ROOM.rooms.get(roomNO);
		GameVO gameVO = roomVO.getGames().get(roomVO.getGames().size()-1);
		if(IContent.ROOM_STATUS_NORMAL.contentEquals(roomVO.getStatus())) {
			if(IContent.GAME_STATUS_INIT.contentEquals(gameVO.getStatus())) {
				gameVO.setStatus(IContent.GAME_STATUS_RUNNING);
			}
		}
		return gameVO;
	}
	public GameVO doBetting(String roomNO) {
		RoomVO roomVO = _ROOM.rooms.get(roomNO);
		GameVO gameVO = roomVO.getGames().get(roomVO.getGames().size()-1);
		gameVO.setBet_start_time(new Timestamp(System.currentTimeMillis()));
		gameRecordsMapper.gameRecordsUpdateStartBettingTime(gameVO);		
		if(IContent.ROOM_STATUS_NORMAL.contentEquals(roomVO.getStatus())) {
			if(IContent.GAME_STATUS_RUNNING.contentEquals(gameVO.getStatus())) {
				gameVO.setStatus(IContent.GAME_STATUS_BETTING);
			}
		}
		return gameVO;
	}
	public GameVO stopBetting(String roomNO) {
		RoomVO roomVO = _ROOM.rooms.get(roomNO);
		GameVO gameVO = roomVO.getGames().get(roomVO.getGames().size()-1);
		gameVO.setBet_end_time(new Timestamp(System.currentTimeMillis()));
		gameRecordsMapper.gameRecordsUpdateEndBettingTime(gameVO);
		if(IContent.ROOM_STATUS_NORMAL.contentEquals(roomVO.getStatus())) {
			if(IContent.GAME_STATUS_BETTING.contentEquals(gameVO.getStatus())) {
				gameVO.setStatus(IContent.GAME_STATUS_DRAWING);
			}
		}
		return gameVO;
	}
	public GameVO doDrawing(String roomNO,LinkedHashMap<String,Integer> resultPool, String loginID) {
		UserService memService = new UserService(); 
		RoomVO roomVO = _ROOM.rooms.get(roomNO);
		GameVO gameVO = roomVO.getGames().get(roomVO.getGames().size()-1);
		if(IContent.ROOM_STATUS_NORMAL.contentEquals(roomVO.getStatus())) {
			if(IContent.GAME_STATUS_DRAWING.contentEquals(gameVO.getStatus())) {
				if(gameVO.hasBetRecords()) {
					HashSet<String> loginIdSet = new HashSet<String>();
					float banker_get_money = 0;
//					Integer limit_amount = gameVO.getLimit_amount();
					for(String key : resultPool.keySet()) {
						if(resultPool.get(key)==0) {//莊贏
							for(BetVO betVO:gameVO.getRecords().get(key)) {
								//莊家贏的錢
								banker_get_money += betVO.getAmount();								
								//閒家下注紀錄賺的錢
								betVO.setGet_money(0);
								betVO.setResult(0);
								loginIdSet.add(betVO.getLoginID());								
							}
						}else if(resultPool.get(key)==1) {//閒贏
							for(BetVO betVO:gameVO.getRecords().get(key)) {
								//莊家贏的錢
								banker_get_money += (betVO.getAmount() * -1);										
								//閒家下注紀錄賺的錢
								betVO.setGet_money(betVO.getAmount()* (float)1.95);
								betVO.setResult(1);
								memService.plusMoney(betVO.getLoginID(), betVO.getAmount() *(float)1.95);
								loginIdSet.add(betVO.getLoginID());
							}							
						}else {
							for(BetVO betVO:gameVO.getRecords().get(key)) {//平手
								betVO.setGet_money(betVO.getAmount());
								betVO.setResult(2);
								memService.plusMoney(betVO.getLoginID(), betVO.getAmount());
								loginIdSet.add(betVO.getLoginID());
							}							
						}
					}									
					//新增User的下注紀錄
					betRecordsMapper.betRecordsSave(gameVO.getId(), gameVO.getRecords());
					
					//更新莊家錢包
					userMapper.bankerWalletUpdate(loginID, banker_get_money);
					memService.plusMoney(loginID, banker_get_money);
					
					//場主更新錢包(還沒寫)
//					managerMapper.managerWalletUpdate(loginID, banker_get_money);
					
					//更新User的錢包
					HashMap<String,Float> loginIdMap = new HashMap();
					for (String loginId : loginIdSet) {
						userMapper.userWalletUpdate(loginId, memService.getMoney(loginId));

					}					
				}								
//				List<BetVO> winnerBets = getLastBets(roomNO, winnerPool);
//				for(BetVO bet : winnerBets) {
//					//TODO:不同的遊戲應有不同的賠率
//					memService.plusMoney(bet.getLoginID(), bet.getAmount()*2*100);
//				}
			}
		}
		return gameVO;
	}
	
	public GameVO stopDrawing(String roomNO) {
		RoomVO roomVO = _ROOM.rooms.get(roomNO);
		GameVO gameVO = roomVO.getGames().get(roomVO.getGames().size()-1);
		gameVO.setEnd_time(new Timestamp(System.currentTimeMillis()));
		gameRecordsMapper.gameRecordsUpdateEndTime(gameVO);		
		if(IContent.ROOM_STATUS_NORMAL.contentEquals(roomVO.getStatus())) {
			if(IContent.GAME_STATUS_DRAWING.contentEquals(gameVO.getStatus())) {
				gameVO.setStatus(IContent.GAME_STATUS_CLOSE);
			}
		}
		return gameVO;
	}
	
	public RoomVO doCloseRoom(String roomNO) {
		RoomVO roomVO = _ROOM.rooms.get(roomNO);
		roomVO.setStatus(IContent.ROOM_STATUS_INIT);
		return roomVO;
	}
	public GameVO doCloseGame(String roomNO,String winnerResult) {
		List<GameVO> gameList = _ROOM.rooms.get(roomNO).getGames();
		UserService memService = new UserService(); 
		GameVO vo = gameList.get(gameList.size()-1);
		vo.setResult(winnerResult);
		List<BetVO> bets = vo.getRecords().get(winnerResult);
		for(BetVO bet : bets) {
			String loginID = bet.getLoginID();
			memService.plusMoney(loginID, bet.getAmount()*2);
			logger.info("RoomNO:{},GameID:{},User:{},Bet:{},Get:{}",bet.getRoomNO(),gameList.size()-1 ,bet.getMemID(),bet.getAmount(),bet.getAmount()*2);
		}
		return vo;
	}
	public Float doBet(String loginID,String roomNO, BetVO betVO) {
		UserService memService = new UserService(); 
		UserBean mem = memService.getUserByLoginID(loginID);
		RoomVO roomVO = _ROOM.rooms.get(roomNO);
		if(IContent.ROOM_STATUS_NORMAL.contentEquals(roomVO.getStatus())) {
			List<GameVO> gameList = roomVO.getGames();
			GameVO vo = gameList.get(gameList.size()-1);
			if(IContent.GAME_STATUS_RUNNING.contentEquals(vo.getStatus())) {
				mem.setMoney(memService.plusMoney(loginID, 0-betVO.getAmount()));
				vo.addBet(betVO);
				return mem.getMoney();
			}
		}
		return mem.getMoney();
	}

	public List<BetVO> getBets(String roomNO, Integer gameID, String poolName){
		RoomVO roomVO = _ROOM.rooms.get(roomNO);
		List<GameVO> gameList = roomVO.getGames();
		if(IContent.ROOM_STATUS_NORMAL.contentEquals(roomVO.getStatus()) && gameID<=(gameList.size()-1)) {
			GameVO game = gameList.get(gameID);
			return game.getRecords().get(poolName);
		}
		return new ArrayList<BetVO>();
	}
	
	public List<BetVO> getLastBets(String roomNO, String poolName){
		RoomVO roomVO = _ROOM.rooms.get(roomNO);
		if(IContent.ROOM_STATUS_NORMAL.contentEquals(roomVO.getStatus())) {
			List<GameVO> gameList = roomVO.getGames();
			GameVO game = gameList.get(gameList.size()-1);
			return game.getRecords().get(poolName);
		}
		return new ArrayList<BetVO>();
	}
	public synchronized void addBet(String roomNO, BetVO betVO) {
		RoomVO roomVO = _ROOM.rooms.get(roomNO);
		if(IContent.ROOM_STATUS_NORMAL.contentEquals(roomVO.getStatus())) {
			List<GameVO> gameList = roomVO.getGames();
			GameVO game = gameList.get(gameList.size()-1);
			game.getRecords().get(betVO.getTarget()).add(betVO);
		}
	}
	
}
