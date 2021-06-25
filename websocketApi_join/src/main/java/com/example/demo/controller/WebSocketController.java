package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.VO.MsgVO;
import com.example.demo.service.MsgService;
import org.apache.tomcat.util.http.parser.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.ContextLoader;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@ServerEndpoint("/websocket/{username}")
public class WebSocketController {	
	private static ApplicationContext applicationContext;
	 
	public static void setApplicationContext(ApplicationContext context) {
	    applicationContext = context;
	}
	
	private MsgService msgService;
	private final String SAVE = "save";
	private final String GETMESSAGE = "getMessage";
	private final String SEARCHKEYWORD = "searchKeyword";
	private final String SHOWLASTMSG = "showLastMsg";
	private final String SHOWMSGCOUNT = "showMsgCount";
	private final String RECEIVE_OTHERS_MSG = "receive_others_msg";
	private final String RECEIVE_OWN_MSG = "receive_own_msg";
	private final String SHOWRECORD = "showRecord";
	private final String SHOWKEYWORD = "showKeyword";
	private final String BROADCAST = "broadcast";
	private final String SENDOFFER = "sendOffer";
	private final String SENDANSWER = "sendAnswer";
	private final String STORECANDIDATE = "storeCandidate";
	private final String SENDCANDIDATE = "sendCandidate";
	
	
	
	/**
     * 在線人數
     */
    public static int onlineNumber = 0;
    /**
     * 以User姓名为key，將WebSocket為對象保存起来
     */
    private static Map<String, WebSocketController> clients = new ConcurrentHashMap<String, WebSocketController>();
    
    private static Map<String, ArrayList<Map<String, Object>>> clients_candidates = new ConcurrentHashMap<String, ArrayList<Map<String, Object>>>();
    /**
     * 儲存session
     */
    private Session session;
    /**
     * User名稱
     */
    private String username;

    /**
     * OnOpen 表示有瀏覽器連接過來的時候調用
     * OnClose 表示有瀏覽器發出關閉請求的時候被調用
     * OnMessage 表示有瀏覽器發消息的時候被調用
     * OnError 表示有錯誤發生，比如網路斷掉
     */

    /**
     * 建立連線
     *
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam("username") String username, Session session) {    	
        this.username = username;
        this.session = session;        
        try {
            //把自己的資訊加入到map管理
            clients.put(username, this);
            addOnlineCount();    
            //查詢給自己的訊息
            msgService = applicationContext.getBean(MsgService.class);
            List<Map<String,Object>> lastMsg_list = msgService.getAllFromLastMessage(username);
            Map<String, Object> lastMsg_map = new HashMap<>();
            lastMsg_map.put(SHOWLASTMSG, lastMsg_list);
            sendMessageTo(JSON.toJSONString(lastMsg_map), username);
            //查詢未讀訊息count
            List<Map<String, Object>> msg_count_list = msgService.getUnreadCount(username);
            Map<String, Object> msg_count_map = new HashMap<>();
            msg_count_map.put(SHOWMSGCOUNT, msg_count_list);
            sendMessageTo(JSON.toJSONString(msg_count_map), username);                              
        } catch (IOException e) {
        	//上線的時候發生了錯誤
        	System.out.println(e.getMessage());
        }


    }

    
    /**
     * Server發生錯誤
     */    
    @OnError
    public void onError(Session session, Throwable error) {    	
    	System.out.println(error.getMessage());
    }

    /**
     * 連接關閉
     */
    @OnClose
    public void onClose() {
        try {
        	clients.remove(username);
        	subOnlineCount();            
        } catch (Exception e) {
        	//下線發生錯誤
        	System.out.println(e.getMessage());
        }
    }

    /**
     * 收到客户端的消息
     *
     * @param message 消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
        	msgService = applicationContext.getBean(MsgService.class);        	
        	//parse前端傳來的訊息
            JSONObject jsonObject = JSON.parseObject(message);
            String msg_type = jsonObject.getString("msg_type");
            switch(msg_type) {
            	case SAVE://訊息儲存至DB
                    MsgVO msgVO = new MsgVO();
                    msgVO.setMsg_from(jsonObject.getString("msg_from"));
                    msgVO.setMsg_to(jsonObject.getString("msg_to"));
                    msgVO.setMsg_content(jsonObject.getString("msg_content"));            
                    msgVO.setMsg_status(jsonObject.getInteger("msg_status"));
                    msgService.saveMsg(msgVO);                
                    //將訊息推撥至對方
                    Map<String, Object> msg_receive_map = new HashMap<>();
                    //呼叫getMemberInfoById
//                  RestTemplate restTemplate = new RestTemplate();
//                  String url = "http://192.168.50.93:9082/api/v1/org/getMember/" + msgVO.getFrom();
//                  HttpHeaders headers = new HttpHeaders();
//                  HttpEntity<String> entity = new HttpEntity<String>(headers);
//                  String strbody = restTemplate.exchange(url, HttpMethod.GET, entity,String.class).getBody();
//                  JSONObject jsonObject2 = JSON.parseObject(strbody);
//                  JSONObject usernameTest = jsonObject2.getJSONObject("RET");                    
                    
                    msg_receive_map.put(RECEIVE_OTHERS_MSG, msgVO);
                    if(clients.get(msgVO.getMsg_to())!= null) {
                    	sendMessageTo(JSON.toJSONString(msg_receive_map), msgVO.getMsg_to());
                    }
                    //將訊息推播至自己
                    Map<String, Object> msg_receive_own_map = new HashMap<>();
                    msg_receive_own_map.put(RECEIVE_OWN_MSG, msgVO);                    
                    sendMessageTo(JSON.toJSONString(msg_receive_own_map), msgVO.getMsg_from());
                    break;
            	case GETMESSAGE://查詢與對方的聊天紀錄                	
                    String msg_from = jsonObject.getString("msg_from");
                    String msg_to = jsonObject.getString("msg_to");
                    List<Map<String,Object>> msg_record_list = msgService.getConversationRecord(msg_from, msg_to);
                    Map<String, Object> msg_record_map = new HashMap<>();
                    msg_record_map.put(SHOWRECORD, msg_record_list);                
                    sendMessageTo(JSON.toJSONString(msg_record_map), msg_to);                
                    //令訊息狀態改為已讀
                    msgService.msgUpdateStatus(msg_from, msg_to);
                    break;
            	case SEARCHKEYWORD://關鍵字搜尋    
                	MsgVO msgVO_3 = new MsgVO();            	
                	msgVO_3.setMsg_from(jsonObject.getString("msg_from"));
                	msgVO_3.setMsg_to(jsonObject.getString("msg_to"));
                	msgVO_3.setMsg_content("%" + jsonObject.getString("msg_content") + "%");                        	
                    List<Map<String, Object>> msg_keyword_list = msgService.searchKeyword(msgVO_3);
                    Map<String, Object> msg_keyword_map = new HashMap<>();
                    msg_keyword_map.put(SHOWKEYWORD, msg_keyword_list);                
                    sendMessageTo(JSON.toJSONString(msg_keyword_map), msgVO_3.getMsg_from());
                    break;                    
            	case BROADCAST://廣播訊息送至在線人員
                    Map<String, Object> msg_broadcast_map = new HashMap<>();
                    msg_broadcast_map.put(BROADCAST, jsonObject.getString("msg_content"));
            		sendMessageAll(JSON.toJSONString(msg_broadcast_map));
            		break;
            	case SENDOFFER:
                    Map<String, Object> msg_offer_map = new HashMap<>();
//                    msg_offer_map.put(SENDOFFER, jsonObject.getString("offerSDP"));
                    msg_offer_map.put(SENDOFFER, jsonObject);
            		sendMessageTo(JSON.toJSONString(msg_offer_map), jsonObject.getString("targetUser"));
            		System.out.println("SENDOFFER");
            		break;            		
            	case STORECANDIDATE:
                    Map<String, Object> msg_candidate_map = new HashMap<>();
                    msg_candidate_map.put(SENDCANDIDATE, jsonObject);
                    if(null != clients_candidates.get(jsonObject.getString("originUser"))) {
//                    	clients_candidates.get((jsonObject.getString("originUser")).
                    	clients_candidates.get(jsonObject.get("originUser")).add(msg_candidate_map);
                    }else {
                    	ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                    	list.add(msg_candidate_map);
                    	clients_candidates.put((String) jsonObject.get("originUser"), list);
                    }            		
            		System.out.println("SENDCANDIDATE");
            		break;   
            	case SENDANSWER:
                    Map<String, Object> msg_answer_map = new HashMap<>();
                    msg_answer_map.put(SENDANSWER, jsonObject);
            		sendMessageTo(JSON.toJSONString(msg_answer_map), jsonObject.getString("targetUser"));            		
            		System.out.println("SENDANSWER");
            		break;     
            	case SENDCANDIDATE:
            		ArrayList<Map<String, Object>> origin_cadidate_map_list = clients_candidates.get(jsonObject.get("originUser"));
            		String targetUser = null;
            		for(Map<String, Object> origin_map:origin_cadidate_map_list) {
            			JSONObject origin_json = (JSONObject) origin_map.get(SENDCANDIDATE);
            			sendMessageTo(JSON.toJSONString(origin_map), origin_json.getString("targetUser"));
            			targetUser = origin_json.getString("targetUser");
            			//刪除target_cadidate
//            			try {
//                			ArrayList<Map<String, Object>> target_cadidate_map_list = clients_candidates.get(origin_json.getString("targetUser"));
//                			for(Map<String, Object> target_map:target_cadidate_map_list) {
//                				JSONObject target_json = (JSONObject) target_map.get(SENDCANDIDATE);
//                				sendMessageTo(JSON.toJSONString(target_map), target_json.getString("targetUser"));
////                				target_cadidate_map_list.remove(target_map);
//                			}            				
//            			}catch(Exception e) {
//            				System.out.println(e.getMessage());
//            			}

            		
            			//刪除origin_cadidate
//            			origin_cadidate_map_list.remove(origin_map);
            		}
            		ArrayList<Map<String, Object>> target_cadidate_map_list = clients_candidates.get(targetUser);
        			for(Map<String, Object> target_map:target_cadidate_map_list) {
	    				JSONObject target_json = (JSONObject) target_map.get(SENDCANDIDATE);
	    				sendMessageTo(JSON.toJSONString(target_map), target_json.getString("targetUser"));
	//    				target_cadidate_map_list.remove(target_map);
        			}              		
//                    Map<String, Object> msg_answer_map = new HashMap<>();
//                    msg_answer_map.put(SENDANSWER, jsonObject);
//            		sendMessageTo(JSON.toJSONString(msg_answer_map), jsonObject.getString("targetUser"));            		
//            		System.out.println("SENDANSWER");
            		break;                 		
                default://令訊息狀態改為已讀          	                    
                    msgService.msgUpdateStatus(jsonObject.getString("msg_from"), jsonObject.getString("msg_to")); 
                    break;
            }                               
        } catch (Exception e) {
//            log.info("發生錯誤);
        }
    }

    
    //將訊息送至指定用戶
    public void sendMessageTo(String message, String ToUserName) throws IOException {
    	WebSocketController itemTest = clients.get(ToUserName);
		itemTest.session.getAsyncRemote().sendText(message);    	
    }
    
    
    //廣播給在線上的所有User
    public void sendMessageAll(String message) throws IOException {
        for (WebSocketController item : clients.values()) {
            item.session.getAsyncRemote().sendText(message);
        }
    }

    //取得在線人數
    public static synchronized int getOnlineCount() {
        return onlineNumber;
    }
    
    //用戶連線時，在線人數++    
    public static synchronized void addOnlineCount() {
    	WebSocketController.onlineNumber++;
    }
    
    //用戶離線時，在線人數--    
    public static synchronized void subOnlineCount() {
    	WebSocketController.onlineNumber--;
    }    

}
