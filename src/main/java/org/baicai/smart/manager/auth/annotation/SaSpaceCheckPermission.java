package org.baicai.smart.manager.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 空间权限认证：必须具有指定权限才能进入该方法（支持匿名访问）
 * <p> 通过自定义 AOP 实现，不依赖 SaInterceptor 的 @SaCheckPermission
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SaSpaceCheckPermission {

    /**
     * 需要校验的权限码（支持多个，AND 逻辑）
     *
     * @return 权限码数组
     */
    String[] value() default {};

    /**
     * 是否允许匿名访问（不登录也可以访问）
     * <p>true = 未登录时跳过鉴权，已登录时正常校验权限</p>
     * <p>false = 必须登录且具有权限</p>
     *
     * @return 是否允许匿名
     */
    boolean allowAnonymous() default false;

}