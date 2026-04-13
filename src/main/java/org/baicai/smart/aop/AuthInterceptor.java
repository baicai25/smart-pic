package org.baicai.smart.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.baicai.smart.annotation.AuthCheck;
import org.baicai.smart.exception.BusinessException;
import org.baicai.smart.exception.ErrorCode;
import org.baicai.smart.model.entity.User;
import org.baicai.smart.model.enums.UserRoleEnum;
import org.baicai.smart.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        //获取当前登录用户的信息
        User loginUser = userService.getLoginUser(request);
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        //如果不需要权限(vip,admin...),直接放行
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }
        //以下必须要有权限才能通过,没有就报错
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        if (userRoleEnum == null) {
            //应该不会发生
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "怎么会进到这里面呢");
        }
        //要求必须是管理员权限,没有就报错
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //通过校验后放行
        return joinPoint.proceed();
    }
}
