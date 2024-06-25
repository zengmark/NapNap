package com.napnap.controller;

import com.napnap.utils.SseEmitterUtil;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
@Controller
@RequestMapping("/sse")
@Api(tags = "消息推送服务管理")
public class SseController {

    @GetMapping("/connect")
//    @LoginCheck
    public SseEmitter connect(HttpServletRequest request){
//        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
//        Long userId = userVO.getId();
        return SseEmitterUtil.connect(String.valueOf(1));
    }

    @GetMapping("/connect/{userId}")
    public SseEmitter connect(@PathVariable String userId) {
        return SseEmitterUtil.connect(userId);
    }

    @PostMapping("/send/{userId}")
    public void sendMessage(@PathVariable String userId, @RequestParam String message) {
        SseEmitterUtil.sendMessage(userId, message);
    }
}
