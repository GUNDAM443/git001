package com.shsxt.xmjf.api.service;

import com.shsxt.xmjf.api.po.BasUser;
import com.shsxt.xmjf.api.po.User;

/**
 * @author ：pp
 * @date ：Created in 2019/4/7 23:44
 */
public interface IUserService {
    public User queryUserByUserId(Integer userId);
    public BasUser queryBasUserByPhone(String phone);
    /**
     * @Description :添加用户的方法
     * @param phone
     * @param password
     * @param code
     * @Return : void
     * @Author : pp
     * @Date : 2019/4/15 20:23
     */
    public void saveUser(String phone ,String password ,String code);

}
