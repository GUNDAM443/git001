package com.shsxt.xmjf.web.controller;

import com.shsxt.xmjf.api.constants.XmjfConstant;
import com.shsxt.xmjf.api.exceptions.BusiException;
import com.shsxt.xmjf.api.model.ResultInfo;
import com.shsxt.xmjf.api.po.User;
import com.shsxt.xmjf.api.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class UserController {
    @Resource
    private IUserService userService;

    @GetMapping("user/{userId}")
    @ResponseBody
    public User queryUserByUserId(@PathVariable Integer userId){
        return userService.queryUserByUserId(userId);
    }

    @PostMapping("user/saveUser")
    @ResponseBody
    public ResultInfo saveUser(String phone, String password, String code) {
        ResultInfo resultInfo = new ResultInfo();
        try {
            userService.saveUser(phone, password, code);
            resultInfo.setMsg("注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo.setCode(XmjfConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(XmjfConstant.OPS_FAILED_MSG);
            if (e instanceof BusiException) {
                BusiException be = (BusiException) e;
                resultInfo.setCode(be.getCode());
                resultInfo.setMsg(be.getMsg());
            }
        }
        return resultInfo;
    }
}

