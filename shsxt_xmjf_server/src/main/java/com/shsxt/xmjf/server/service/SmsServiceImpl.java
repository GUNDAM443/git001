package com.shsxt.xmjf.server.service;

import com.alibaba.fastjson.JSONException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.github.pagehelper.Constant;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.shsxt.xmjf.api.constants.XmjfConstant;
import com.shsxt.xmjf.api.service.ISmsService;
import com.shsxt.xmjf.api.service.IUserService;
import com.shsxt.xmjf.api.utils.AssertUtil;
import com.shsxt.xmjf.api.utils.PhoneUtil;
import com.shsxt.xmjf.api.utils.RandomCodesUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements ISmsService {

    @Resource
    private IUserService userService;


    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource(name="redisTemplate")
    private ValueOperations<String,Object> valueOperations;

    @Override
    public void sendSms(String phone, Integer type) {

        checkParams(phone,type);
        String code= RandomCodesUtils.createRandom(true,4);
        if(type== XmjfConstant.SMS_LOGIN_TYPE){
            doSendSms(phone,type,code,XmjfConstant.XMJF_LOGIN_TEMPLATEID);
        }else if(type== XmjfConstant.SMS_REGISTER_TYPE){
            AssertUtil.isTrue(null != (userService.queryBasUserByPhone(phone)),"该手机号已注册!");
            doSendSms(phone,type,code,XmjfConstant.XMJF_REGISTER_TEMPLATEID);
            valueOperations.set("phone::"+ phone+"::templateCode::"+ XmjfConstant.XMJF_REGISTER_TEMPLATEID,code,180, TimeUnit.SECONDS);
        }else {
            System.out.println("------");
        }

    }

    private void checkParams(String phone, Integer type) {
        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号不能为空!");
        AssertUtil.isTrue(!(PhoneUtil.checkPhone(phone)),"手机号格式非法!");
        AssertUtil.isTrue(null==type,"短信类型不能为空!");
        AssertUtil.isTrue(!(type== XmjfConstant.SMS_LOGIN_TYPE||type== XmjfConstant.SMS_REGISTER_TYPE),"短信类型不合法!");
    }


    private void doSendSms(String phone, Integer type, String code,Integer templateCode) {

        String reStr = ""; //定义返回值
        // 短信应用SDK AppID  // 1400开头
        int appid = XmjfConstant.TENCENT_APPID;
        // 短信应用SDK AppKey
        String appkey = XmjfConstant.TENCENT_APPKEY;
        /*// 短信模板ID，需要在短信应用中申请
        int templateId =XmjfConstant.XMJF_REGISTER_TEMPLATEID;*/
        // 签名，使用的是`签名内容`，而不是`签名ID`
        String smsSign = XmjfConstant.XMJF_SMSSIGN;
        try {
            //参数，一定要对应短信模板中的参数顺序和个数，
            String[] params = {code,"1"};
            //创建ssender对象
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            //发送
            SmsSingleSenderResult result = ssender.sendWithParam("86", phone, templateCode, params, smsSign, "", "");
            // 签名参数未提供或者为空时，会使用默认签名发送短信
            System.out.println(result.toString());
            if (result.result == 0) {
                reStr = "success";
            } else {
                reStr = "error";
            }
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
        } catch (Exception e) {
            // 网络IO错误
            e.printStackTrace();
        }
       /* return reStr;*/

        /*try {
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");
            final String product = XmjfConstant.SMS_PRODUCT;
            final String domain = XmjfConstant.SMS_DOMAIN;
            final String accessKeyId = XmjfConstant.SMS_AK;
            final String accessKeySecret = XmjfConstant.SMS_SK;
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
                    accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            SendSmsRequest request = new SendSmsRequest();
            request.setMethod(MethodType.POST);
            request.setPhoneNumbers(phone);
            request.setSignName(XmjfConstant.SMS_SIGN);
            request.setTemplateCode(templateCode);
            request.setTemplateParam("{\"code\":\""+code+"\"}");
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                System.out.println("短信发送成功。。。");
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }*/
    }

}
