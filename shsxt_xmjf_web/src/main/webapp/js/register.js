$(function () {
    $(".validImg").click(function () {
        //这边是给前端相应图片  不是相应json 所以ajax不合适
        //点击更换验证码  new date 是防止图片缓存
        $(this).attr("src", ctx + "/image?time" + new Date());
    })

    $("#clickMes").click(function () {
        //获取输入的手机号和验证码 判断非空
        var phone = $("#phone").val();
        var imageCode = $("#code").val();
        if (isEmpty(phone)) {
            alert("手机号不能为空!");
            return;
        }

        if (isEmpty(imageCode)) {
            alert("请输入图片验证码!");
            return;
        }
        //ajax发送数据到后台
        $.ajax({
            type: "post",
            url: ctx + "/sendSms",
            data: {
                phone: phone,
                type: 2,
                imageCode: imageCode
            },
            dataType: "json",
            success: function (data) {
                if (data.code == 200) {

                    var _this = $("#clickMes");
                    var time = 60;//设置倒计时时间60s
                    var obj = setInterval(function () {//setinterval是定时器
                        if (time >= 2) {
                            time = time - 1;//时间减1s
                            _this.attr("disabled", true);//设置按钮禁用
                            _this.val("" + time + "s");//设置显示按钮文字
                            _this.css("background", "grey");//设置背景颜色
                        } else {//倒计时结束
                            clearInterval(obj);//关闭定时器,防止显示负数时间
                            _this.removeAttr("disabled");//启用按钮
                            _this.val("获取验证码");//设置显示按钮文字
                            _this.css("background", "#fcb22f");//背景颜色重新恢复
                        }
                    }, 1000);//定时器时间间隔为1s

                } else {
                    alert(data.msg);
                    $(".validImg").attr("src", ctx + "/image?time=" + new Date());//请求不成功时  更换图片验证码
                }
            }
        });

    })

    /*点击注册按钮时校验*/
    $("#register").click(function () {
        var phone=$("#phone").val();
        var code=$("#verification").val();
        var password=$("#password").val();
        if(isEmpty(phone)){
            layer.tips("请输入手机号码!","#phone");
            return;
        }
        if(isEmpty(code)){
            layer.tips("请输入手机短信验证码!","#verification");
            return;
        }

        if(isEmpty(password)){
            layer.tips("请输入密码!","#password");
            return;
        }

        $.ajax({
            type:"post",
            url:ctx+"/user/saveUser",
            data:{
                phone:phone,
                code:code,
                password:password
            },
            dataType:"json",
            success:function (data) {
                if(data.code==200){
                    layer.msg('注册成功');
                    setTimeout(function () {
                        window.location.href=ctx+"/login";
                    },2000)
                }else{
                    layer.tips(data.msg,"#register");
                }
            }
        })




    })

});