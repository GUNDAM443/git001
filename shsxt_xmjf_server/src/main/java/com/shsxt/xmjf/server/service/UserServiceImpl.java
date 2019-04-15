package com.shsxt.xmjf.server.service;

import com.shsxt.xmjf.api.po.BasUser;
import com.shsxt.xmjf.api.po.User;
import com.shsxt.xmjf.api.service.IUserService;
import com.shsxt.xmjf.server.db.dao.BasUserMapper;
import com.shsxt.xmjf.server.db.dao.UserDao;
import com.sun.xml.bind.v2.model.core.ID;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author ：pp
 * @date ：Created in 2019/4/8 0:20
 */
@Service
public class UserServiceImpl implements IUserService {
    @Resource
    private BasUserMapper basUserMapper;
    @Resource
    private UserDao userDao;
    @Override
    public User queryUserByUserId(Integer userId) {
        return userDao.queryUserByUserId(userId);
    }

    @Override
    public BasUser queryBasUserByPhone(String phone) {
        return basUserMapper.queryBasUserByPhone(phone);
    }

    @Override
    public void saveUser(String phone, String password, String code) {
        /**
         * 1.参数校验
         *      phone:非空  格式合法
         *     password:非空  长度至少6位
         *     code:非空  有效 与缓存中值一致
         * 2.手机号唯一校验
         * 3.表记录初始化
         *   bas_user	用户基本信息
         bas_user_info	用户信息扩展表记录添加
         bas_user_security	用户安全信息表
         bus_account	用户账户表记录信息
         bus_user_integral	用户积分记录
         bus_income_stat	用户收益表记录
         bus_user_stat	用户统计表
         bas_experienced_gold	注册体验金表
         sys_log       系统日志
         4.发送注册成功通知短信
         */

    }

}
