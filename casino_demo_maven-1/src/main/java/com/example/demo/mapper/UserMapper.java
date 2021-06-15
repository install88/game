package com.example.demo.mapper;

import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface UserMapper{
    /**
     * User資料相關
     * @return
     */
    //遊戲結算時，更改User錢包餘額至DB。
//    int userWalletUpdate(@Param("loginIdMap") Map<String,Float> loginIdMap);
    int userWalletUpdate(@Param("loginId") String loginId, @Param("money")float money);
    
    Map<String,Object> getMemberById(String loginId);
}
