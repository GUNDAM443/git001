package com.shsxt.xmjf.server.db.dao;

import com.shsxt.xmjf.api.po.User;
import org.apache.ibatis.annotations.Param;

/**
 * @author ：pp
 * @date ：Created in 2019/4/8 0:21
 */
public interface UserDao {
    public User queryUserByUserId(@Param("userId") Integer id);

}
