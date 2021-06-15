package com.example.demo.mapper;

import com.example.demo.vo.GameVO;

public interface GameRecordsMapper{
    /**
     * 遊戲紀錄相關
     * @return
     */
    //初始化房間時，新增遊戲紀錄至DB。
    int gameRecordsSave(GameVO gameVO);
    
    //開始下注時，修改遊戲紀錄的開始下注時間至DB。
    int gameRecordsUpdateStartBettingTime(GameVO gameVO);
    
    //結束下注時，修改遊戲紀錄的結束下注時間至DB。
    int gameRecordsUpdateEndBettingTime(GameVO gameVO);
    
    //遊戲結束時，修改遊戲紀錄的遊戲結束時間至DB。
    int gameRecordsUpdateEndTime(GameVO gameVO);    
    
        
    //User登入時點開好友清單，只顯示其好友們最新一筆資料(像line一樣)
//    List<Map<String,Object>> getAllFromLastMessage(String msg_to);  
        
    //點開對象聊天室後，查詢與該對象的聊天紀錄
//    List<Map<String,Object>> getConversationRecord(String msg_from, String msg_to);
    
    //將該好友訊息狀態從未讀改成已讀

    
    
    //取得未讀訊息筆數
//    List<Map<String,Object>> getUnreadCount(String msg_to);

    //取得關鍵字搜尋筆數
//    List<Map<String,Object>> searchKeyword(MsgVO msgVO);
        
    //將未讀訊息修改成已讀狀態
//    int msgUpdateStatus(String msg_from,String msg_to);
 
}
