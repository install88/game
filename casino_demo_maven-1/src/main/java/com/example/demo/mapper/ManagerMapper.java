package com.example.demo.mapper;

import org.apache.ibatis.annotations.Param;

public interface ManagerMapper{
    /**
     * Manager資料相關
     * @return
     */
    //遊戲結算時，更改User錢包餘額至DB。
    int managerWalletUpdate(@Param("admin") String admin, @Param("money")float money);    
}
