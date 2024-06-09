package com.napnap.aop;

import com.napnap.annotation.LoginCheck;
import com.napnap.common.ErrorCode;
import com.napnap.constant.UserConstant;
import com.napnap.entity.User;
import com.napnap.exception.BusinessException;
import com.napnap.service.UserService;
import com.napnap.vo.UserVO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class LoginInterceptor {

    @Resource
    private UserService userService;

    @Around("@annotation(loginCheck)")
    public Object checkLogin(ProceedingJoinPoint joinPoint, LoginCheck loginCheck) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        Object loginUser = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        User user = userService.getById(((UserVO)loginUser).getId());
        if(user == null){
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR);
        }
        return joinPoint.proceed();
    }
}
