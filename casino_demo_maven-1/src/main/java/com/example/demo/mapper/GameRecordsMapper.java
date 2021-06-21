package com.example.demo.mapper;

import org.apache.ibatis.annotations.Param;

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
    
    //結束下注時，修改遊戲紀錄的結束下注時間至DB。
    int gameRecordsUpdateBankerGetMoney(@Param("gameID")Integer gameID, @Param("banker_get_money")Float banker_get_money);    
    
    //遊戲結束時，修改遊戲紀錄的遊戲結束時間至DB。
    int gameRecordsUpdateEndTime(GameVO gameVO);    
}
