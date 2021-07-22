package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.IContent;
import com.example.demo.VO.MsgVO;
import com.example.demo.service.MsgService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@ServerEndpoint("/websocket/{userID}")
public class WebSocketController {	
	private static ApplicationContext applicationContext;
	 
	public static void setApplicationContext(ApplicationContext context) {
	    applicationContext = context;
	}
	
	private MsgService msgService;
	
	//在線人數
    public static int onlineNumber = 0;
    
	//以userID為key，將WebSocket為對象保存起來
    private static Map<String, WebSocketController> clients = new ConcurrentHashMap<String, WebSocketController>();
    
    //以userID為key，將user的candidate儲存起來
    private static Map<String, ArrayList<Map<String, Object>>> clients_candidates = new ConcurrentHashMap<String, ArrayList<Map<String, Object>>>();
    
    //儲存session
    private Session session;

    //User名稱
    private String username;

    //UserID
    private String userID;
    

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
    public void onOpen(@PathParam("userID") String userID, Session session) {    	
        this.userID = userID;
        this.session = session;        
        try {
            //把自己的資訊加入到map管理
            clients.put(userID, this);
            addOnlineCount();                            
            //查詢個人資訊
            msgService = applicationContext.getBean(MsgService.class);
            Map<String,Object> ownInfoMap = msgService.getOwnInfo(userID);
            this.username = (String) ownInfoMap.get("username");
            
            //查詢未讀訊息count
            List<Map<String, Object>> msg_count_list = msgService.getUnreadCount(userID);
            Map<String, Object> msg_count_map = new HashMap<>();
            msg_count_map.put(IContent.SHOWMSGCOUNT, msg_count_list);
            sendMessageTo(JSON.toJSONString(msg_count_map), userID);              
            
            //查詢給自己的訊息            
//            List<Map<String,Object>> lastMsg_list = msgService.getAllFromLastMessage(userID);
            List<MsgVO> lastMsg_list = msgService.getAllFromLastMessage(userID);
            Map<String, Object> lastMsg_map = new HashMap<>();
            lastMsg_map.put(IContent.SHOWLASTMSG, lastMsg_list);
//            for(int i=0;i<lastMsg_list.size();i++) {
//            	if(null != lastMsg_list.get(i).getMsg_img()) {
//            		ByteBuffer buf = ByteBuffer.wrap(lastMsg_list.get(i).getMsg_img());
//            		sendImgMessageTo(buf,userID);
//            	}else {
//                    Map<String, Object> lastMsg_map = new HashMap<>();
//                    lastMsg_map.put(IContent.SHOWLASTMSG, lastMsg_list.get(i));
//                    sendMessageTo(JSON.toJSONString(lastMsg_map), userID);
//            	}
//            }
            sendMessageTo(JSON.toJSONString(lastMsg_map), userID);
                            
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
        	clients.remove(userID);
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
    @OnMessage(maxMessageSize = 10240000)
    public void onMessage(String message, Session session) {
        try {
        	msgService = applicationContext.getBean(MsgService.class);        	
        	//parse前端傳來的訊息
            JSONObject jsonObject = JSON.parseObject(message);
            String msg_type = jsonObject.getString("msg_type");
            switch(msg_type) {
            	case IContent.SAVE://訊息儲存至DB
                    MsgVO msgVO = new MsgVO();
                    msgVO.setMsg_from(jsonObject.getString("msg_from"));
                    msgVO.setMsg_to(jsonObject.getString("msg_to"));            
                    msgVO.setMsg_status(jsonObject.getInteger("msg_status"));
                    if(null != jsonObject.getString("msg_content")) {
                    	msgVO.setMsg_content(jsonObject.getString("msg_content"));
                    }else {
                    	msgVO.setMsg_img(jsonObject.getString("msg_img"));            
                    }
                                    
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
                                        
                    if(clients.get(msgVO.getMsg_to())!= null) {                    	
                    	msgVO.setMsg_from_user_name(clients.get(msgVO.getMsg_from()).username);
                    	msg_receive_map.put(IContent.RECEIVE_OTHERS_MSG, msgVO);
                    	sendMessageTo(JSON.toJSONString(msg_receive_map), msgVO.getMsg_to());
                    }
                    
                    //將訊息推播至自己
                    Map<String, Object> msg_receive_own_map = new HashMap<>();
                    msg_receive_own_map.put(IContent.RECEIVE_OWN_MSG, true);
                    sendMessageTo(JSON.toJSONString(msg_receive_own_map), msgVO.getMsg_from());
                    
                    //儲存訊息至DB
                    if(null != jsonObject.getString("msg_content")) {
                    	msgService.msgSave(msgVO);
                    }else {            
                    	msgService.msgImgSave(msgVO);      
                    }                    
                    break;
            	case IContent.GETMESSAGE://查詢與對方的聊天紀錄                	
                    String msg_from = jsonObject.getString("msg_from");
                    String msg_to = jsonObject.getString("msg_to");
                    List<MsgVO> msg_record_list = msgService.getConversationRecord(msg_from, msg_to);
                    Map<String, Object> msg_record_map = new HashMap<>();
                    msg_record_map.put(IContent.SHOWRECORD, msg_record_list);                
                    sendMessageTo(JSON.toJSONString(msg_record_map), msg_to);                
                    //令訊息狀態改為已讀
                    msgService.msgUpdateStatus(msg_from, msg_to);
                    break;
            	case IContent.SEARCHKEYWORDCOUNT://關鍵字搜尋筆數    
                	MsgVO msgVO_3 = new MsgVO();            	
                	msgVO_3.setMsg_from(jsonObject.getString("msg_from"));
                	msgVO_3.setMsg_to(jsonObject.getString("msg_to"));
                	msgVO_3.setMsg_content("%" + jsonObject.getString("msg_content") + "%");                        	
                    List<Map<String, Object>> msg_keyword_list = msgService.searchKeywordCount(msgVO_3);
                    Map<String, Object> msg_keyword_map = new HashMap<>();
                    msg_keyword_map.put(IContent.SHOWKEYWORD, msg_keyword_list);                
                    sendMessageTo(JSON.toJSONString(msg_keyword_map), msgVO_3.getMsg_from());
                    break;                    
            	case IContent.BROADCAST://廣播訊息送至在線人員
                    Map<String, Object> msg_broadcast_map = new HashMap<>();
                    MsgVO broadcast_msgVO = new MsgVO();
                    broadcast_msgVO.setMsg_from(jsonObject.getString("msg_from"));
                    broadcast_msgVO.setMsg_from_user_name(clients.get(jsonObject.getString("msg_from")).username);
                    if(null != jsonObject.getString("msg_content")) {
                    	broadcast_msgVO.setMsg_content(jsonObject.getString("msg_content"));
                    }else {            
                    	broadcast_msgVO.setMsg_img(jsonObject.getString("msg_img"));     
                    }                     
                    msg_broadcast_map.put(IContent.BROADCAST, broadcast_msgVO);
            		sendMessageAll(JSON.toJSONString(msg_broadcast_map));
            		break;
            	case IContent.SENDOFFER://從A端送offer至B端
                    Map<String, Object> msg_offer_map = new HashMap<>();
//                    msg_offer_map.put(SENDOFFER, jsonObject.getString("offerSDP"));
                    jsonObject.put("originUserName", clients.get(jsonObject.get("originUser")).username);
                    msg_offer_map.put(IContent.SENDOFFER, jsonObject);
            		sendMessageTo(JSON.toJSONString(msg_offer_map), jsonObject.getString("targetUser"));
            		System.out.println("SENDOFFER");
            		break;            		
            	case IContent.STORECANDIDATE://儲存User的candidate
//                    Map<String, Object> msg_candidate_map = new HashMap<>();
//                    msg_candidate_map.put(IContent.SENDCANDIDATE, jsonObject);
                    if(jsonObject.getBoolean("answerSetting_isReady")) {//當A客戶端setRemoteDescription(answer)設定完成以後，兩端可以直接把candidate傳給對方
                    	ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                    	list.add(jsonObject);
                    	Map<String, Object> msg_candidate_map = new HashMap<>();
                    	msg_candidate_map.put(IContent.SENDCANDIDATE, list);                    	
            			sendMessageTo(JSON.toJSONString(msg_candidate_map), jsonObject.getString("targetUser"));                    	
                    }else{
                        if(null != clients_candidates.get(jsonObject.getString("originUser"))) {
                        	clients_candidates.get(jsonObject.get("originUser")).add(jsonObject);
                        }else {
                        	ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                        	list.add(jsonObject);
                        	clients_candidates.put((String) jsonObject.get("originUser"), list);
                        }                       	
                    }         		
            		System.out.println("SENDCANDIDATE");
            		break;   
            	case IContent.SENDANSWER://B端設定完setRemoteDescription(offer)後，從B端送answer至A端
                    Map<String, Object> msg_answer_map = new HashMap<>();
                    msg_answer_map.put(IContent.SENDANSWER, jsonObject);
            		sendMessageTo(JSON.toJSONString(msg_answer_map), jsonObject.getString("targetUser"));            		
            		System.out.println("SENDANSWER");
            		break;     
            	case IContent.SENDCANDIDATE://分發自己的candidate給要通話的對象
            		ArrayList<Map<String, Object>> origin_cadidate_map_list = clients_candidates.get(jsonObject.get("originUser"));
            		ArrayList<Map<String, Object>> target_cadidate_map_list = clients_candidates.get(jsonObject.get("targetUser"));            		
            		//將targetUser的candidate傳送給originUser
        			if(target_cadidate_map_list.size()>0) {
            			Map<String, Object> targetUser_map = new HashMap<>();
            			targetUser_map.put(IContent.SENDCANDIDATE, target_cadidate_map_list);
            			sendMessageTo(JSON.toJSONString(targetUser_map), (String)jsonObject.get("originUser"));            				
        			}

            		//將originUser的candidate傳送給targetUser
        			if(origin_cadidate_map_list.size() > 0) {
            			Map<String, Object> originUser_map = new HashMap<>();
            			originUser_map.put(IContent.SENDCANDIDATE, origin_cadidate_map_list);
            			sendMessageTo(JSON.toJSONString(originUser_map), (String)jsonObject.get("targetUser"));            				
        			}       
        			
        			//remove candidate資訊
        			clients_candidates.remove(jsonObject.get("originUser"));
        			clients_candidates.remove(jsonObject.get("targetUser"));        			
            		break;
            	case IContent.SENDANSWERISREADY://A端告知B端，setRemoteDescription(answerSDP)處理好了
                    Map<String, Object> msg_answer_is_ready_map = new HashMap<>();
                    msg_answer_is_ready_map.put(IContent.SENDANSWERISREADY, "");            		
            		sendMessageTo(JSON.toJSONString(msg_answer_is_ready_map), jsonObject.getString("targetUser"));
            		break;            		
            	case IContent.REJECT:
            		Map<String, Object> msg_reject_map = new HashMap<>();
            		msg_reject_map.put(IContent.REJECT, jsonObject);
            		sendMessageTo(JSON.toJSONString(msg_reject_map), jsonObject.getString("targetUser"));
            		break;
                default://令訊息狀態改為已讀          	                    
                    msgService.msgUpdateStatus(jsonObject.getString("msg_from"), jsonObject.getString("msg_to")); 
                    break;
            }                               
        } catch (Exception e) {
        	System.out.println(e.getMessage());
//            log.info("發生錯誤);
        }
    }

    //將訊息送至指定用戶
    public void sendMessageTo(String message, String ToUserName) throws IOException {
    	WebSocketController itemTest = clients.get(ToUserName);
		itemTest.session.getAsyncRemote().sendText(message);    	
    }
    
    //將圖片送至指定用戶
    public void sendImgMessageTo(ByteBuffer message, String ToUserName) throws IOException {
    	WebSocketController itemTest = clients.get(ToUserName);
//		itemTest.session.getAsyncRemote().sendText(message);
		itemTest.session.getAsyncRemote().sendBinary(message);
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
