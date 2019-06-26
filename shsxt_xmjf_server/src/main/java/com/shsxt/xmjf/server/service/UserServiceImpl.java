package com.shsxt.xmjf.server.service;

import com.shsxt.xmjf.api.constants.XmjfConstant;
import com.shsxt.xmjf.api.enums.UserType;
import com.shsxt.xmjf.api.po.*;
import com.shsxt.xmjf.api.service.ISmsService;
import com.shsxt.xmjf.api.service.IUserService;
import com.shsxt.xmjf.api.utils.*;
import com.shsxt.xmjf.server.db.dao.*;
import com.sun.xml.bind.v2.model.core.ID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

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
    @Resource
    private BasUserInfoMapper basUserInfoMapper;
    @Resource
    private BasUserSecurityMapper basUserSecurityMapper;
    @Resource
    private BusAccountMapper busAccountMapper;
    @Resource
    private  BusUserIntegralMapper busUserIntegralMapper;

    @Resource
    private  BusIncomeStatMapper busIncomeStatMapper;

    @Resource
    private  BusUserStatMapper busUserStatMapper;

    @Resource
    private  BasExperiencedGoldMapper basExperiencedGoldMapper;

    @Resource
    private ISmsService smsService;



    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource(name="redisTemplate")
    private ValueOperations<String,Object> valueOperations;
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
        checkParams(phone,password,code);
        int userId= initBasUser(phone,password);//添加返回主键,因为后面几张表也要用用户id
        initBasUserInfo(userId,phone);
        initBasUserSecurity(userId);
        initBusAccount(userId);
        initBasUserIntegral(userId);
        initBusIncomeStat(userId);
        initBusUserStat(userId);
        initBasExperiencedGold(userId);
        System.out.println("==========准备发通知注册成功===========");
        // 通知短信发送
        smsService.sendSms(phone,XmjfConstant.SMS_REGISTER_SUCCESS_NOTIFY_TYPE);
        System.out.println("===================================================");

    }

    private void initBasExperiencedGold(int userId) {
        BasExperiencedGold basExperiencedGold=new BasExperiencedGold();
        basExperiencedGold.setAddtime(new Date());
        basExperiencedGold.setAmount(BigDecimal.valueOf(2888L));
        Date time=new Date(System.currentTimeMillis()+1000*24*60*60*15);
        basExperiencedGold.setExpiredTime(time);
        basExperiencedGold.setGoldName("注册体验金");
        basExperiencedGold.setStatus(2);// 未使用
        basExperiencedGold.setUsefulLife(15);
        basExperiencedGold.setUserId(userId);
        basExperiencedGold.setWay("用户注册");
        AssertUtil.isTrue(basExperiencedGoldMapper.insert(basExperiencedGold)<1,XmjfConstant.OPS_FAILED_MSG);
    }

    private void initBusUserStat(int userId) {
        BusUserStat busUserStat=new BusUserStat();
        busUserStat.setInvestAmount(BigDecimal.ZERO);
        busUserStat.setInvestCount(0);
        busUserStat.setRechargeAmount(BigDecimal.ZERO);
        busUserStat.setRechargeCount(0);
        busUserStat.setUserId(userId);
        busUserStat.setCashAmount(BigDecimal.ZERO);
        busUserStat.setCashCount(0);
        busUserStat.setCouponAmount(BigDecimal.ZERO);
        busUserStat.setCouponCount(0);
        busUserStat.setInvestLaveAmount(BigDecimal.ZERO);
        AssertUtil.isTrue(busUserStatMapper.insert(busUserStat)<1,XmjfConstant.OPS_FAILED_MSG);
    }

    private void initBusIncomeStat(int userId) {
        BusIncomeStat busIncomeStat=new BusIncomeStat();
        busIncomeStat.setUserId(userId);
        busIncomeStat.setWaitIncome(BigDecimal.ZERO);
        busIncomeStat.setTotalIncome(BigDecimal.ZERO);
        busIncomeStat.setEarnedIncome(BigDecimal.ZERO);
        AssertUtil.isTrue(busIncomeStatMapper.insert(busIncomeStat)<1,XmjfConstant.OPS_FAILED_MSG);
    }

    private void initBasUserIntegral(int userId) {
        BusUserIntegral busUserIntegral=new BusUserIntegral();
        busUserIntegral.setUserId(userId);
        busUserIntegral.setUsable(0);
        busUserIntegral.setTotal(0);
        AssertUtil.isTrue(busUserIntegralMapper.insert(busUserIntegral)<1,XmjfConstant.OPS_FAILED_MSG);
    }

    private void initBusAccount(int userId) {
        BusAccount busAccount=new BusAccount();
        busAccount.setUserId(userId);
        busAccount.setWait(BigDecimal.ZERO);//也可以用BigDecimal.valueof(0)
        busAccount.setFrozen(BigDecimal.ZERO);
        busAccount.setTotal(BigDecimal.ZERO);
        busAccount.setCash(BigDecimal.ZERO);
        busAccount.setUsable(BigDecimal.ZERO);
        busAccount.setRepay(BigDecimal.ZERO);
        AssertUtil.isTrue(busAccountMapper.insert(busAccount)<1,XmjfConstant.OPS_FAILED_MSG);
    }

    private void initBasUserSecurity(int userId) {
        BasUserSecurity basUserSecurity=new BasUserSecurity();
        basUserSecurity.setUserId(userId);
        basUserSecurity.setPhoneStatus(1);
        AssertUtil.isTrue(basUserSecurityMapper.insert(basUserSecurity)<1,XmjfConstant.OPS_FAILED_MSG);
    }

    private void initBasUserInfo(int userId, String phone) {
        BasUserInfo basUserInfo=new BasUserInfo();
        /**
         * 邀请码
         *    系统时间毫秒数
         *    phone
         *    用户id-不安全 加密
         *       1
         *       100000
         *       1000000
         *       1000000000
         *    抽奖-奖池中号码提前生成 数据库  redis-队列
         *    双11   1-300000/s   雪花算法
         */
        basUserInfo.setInviteCode(phone);
        basUserInfo.setCustomerType(0);
        basUserInfo.setUserId(userId);
        AssertUtil.isTrue(basUserInfoMapper.insert(basUserInfo)<1,XmjfConstant.OPS_FAILED_MSG);
    }

    private int initBasUser(String phone, String password) {
        BasUser basUser=new BasUser();
        /**
         * 用户名唯一
         *    系统时间毫秒
         *    手机号
         *    UUID-时空唯一  32位
         *    ...
         */
        basUser.setUsername("XMJF_"+System.currentTimeMillis()+"");
        basUser.setStatus(1);
        basUser.setType(UserType.INVEST.getType());//枚举类
        basUser.setAddtime(new Date());
        basUser.setTime(new Date());
        basUser.setReferer("PC");
        String salt= RandomCodesUtils.createRandom(false,6);
        basUser.setSalt(salt);
        basUser.setPassword(MD5.toMD5(password+salt));//密码加盐值 用md5加密
        basUser.setMobile(phone);
        basUser.setAddip(IpUtils.get());//若使用nginx 那么获取的ip不准确
        AssertUtil.isTrue(basUserMapper.insert(basUser)<1,XmjfConstant.OPS_FAILED_MSG);
        return basUser.getId();//返回主键
    }

    private void checkParams(String phone, String password, String code) {
        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号不能为空!");
        AssertUtil.isTrue(!(PhoneUtil.checkPhone(phone)),"手机号格式非法!");
        AssertUtil.isTrue(null!=queryBasUserByPhone(phone),"该手机号已注册!");
        AssertUtil.isTrue(StringUtils.isBlank(password),"密码不能为空!");
        AssertUtil.isTrue(password.length()<6,"密码长度至少6位!");
        AssertUtil.isTrue(StringUtils.isBlank(code),"短信验证码不能为空!");
        String key="phone::"+ phone+"::templateCode::"+ XmjfConstant.XMJF_REGISTER_TEMPLATEID;
        AssertUtil.isTrue(!(redisTemplate.hasKey(key)),"短信验证码已过期!");
        String redisCode= (String) valueOperations.get(key);
        AssertUtil.isTrue(!(redisCode.equals(code)),"短信验证码不正确!");

    }

}
