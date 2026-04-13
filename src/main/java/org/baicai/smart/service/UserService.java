package org.baicai.smart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.baicai.smart.model.dto.user.UserQueryRequest;
import org.baicai.smart.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.baicai.smart.model.vo.LoginUserVo;
import org.baicai.smart.model.vo.UserVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 孟宜辉
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2026-03-23 10:23:09
 */
public interface UserService extends IService<User> {

    //用户注册
    long userRegister(String userAccount, String password, String checkPassword);

    //用户登录
    LoginUserVo userLogin(String userAccount, String password, HttpServletRequest request);

    //用户密码加密
    String getEncryptPassword(String password);

    User getLoginUser(HttpServletRequest request);

    //获得脱敏后的当前登录用户信息
    LoginUserVo getLoginUserVO(User user);

    //获得脱敏后用户信息
    UserVo getUserVO(User user);

    //获得脱敏后用户信息列表
    List<UserVo> getUserVOList(List<User> userList);

    //用户登出
    boolean userLogout(HttpServletRequest request);

    QueryWrapper<User> queryWrapper(UserQueryRequest userQueryRequest);

    boolean isAdmin(User user);

}
