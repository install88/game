package com.example.demo.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.example.demo.vo.BetVO;

public interface BetRecordsMapper{
    /**
     * 賭注紀錄相關
     * @return
     */
    //初始化房間時，新增遊戲紀錄至DB。
    int betRecordsSave(@Param("gameId")Integer id,@Param("betMap") Map<String,List<BetVO>> betMap);
    
    List<Map<String,Object>> getBetRecordsByID(@Param("loginID") String loginID);
}
