package com.shsxt.xmjf.server.db.dao;

import com.shsxt.xmjf.api.po.BasUser;
import com.shsxt.xmjf.server.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface BasUserMapper extends BaseMapper<BasUser> {
  
/**
 * @Description :
 * @param phone
 * @Return : com.shsxt.xmjf.api.po.BasUser
 * @Author : pp
 * @Date : 2019/4/12 23:04
 */
    public  BasUser queryBasUserByPhone(@Param("phone") String phone);


}