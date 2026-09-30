package org.baicai.smart.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.baicai.smart.constant.UserConstant;
import org.baicai.smart.exception.BusinessException;
import org.baicai.smart.exception.ErrorCode;
import org.baicai.smart.manager.auth.StpKit;
import org.baicai.smart.model.dto.user.UserQueryRequest;
import org.baicai.smart.model.entity.User;
import org.baicai.smart.model.enums.UserRoleEnum;
import org.baicai.smart.model.vo.LoginUserVo;
import org.baicai.smart.model.vo.UserVo;
import org.baicai.smart.service.UserService;
import org.baicai.smart.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 孟宜辉
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2026-03-23 10:23:09
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override //用户注册
    public long userRegister(String userAccount, String password, String checkPassword) {
        //1.校验参数是否为空或异常
        if (StrUtil.hasBlank(userAccount, password, checkPassword)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "参数为空?0");
        }
        if (userAccount.length() < 4 || userAccount.length() > 16) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户过长或过短!1");
        }
        if (password.length() < 4 || password.length() > 16) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过长或过短!2");
        }
        if (!password.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不同3");
        }
        //2.检查数据库是否已存在该用户
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("userAccount", userAccount);
        long count = this.baseMapper.selectCount(qw);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复!4");
        }
        //3.密码需要加密,例如盐
        String encryptPassword = getEncryptPassword(password);
        //4.注册该用户,将数据插入到数据库中
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("游客UUID");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "账号注册失败,可能是系统错误");

        }
        return user.getId();
    }


    @Override //用户登录
    public LoginUserVo userLogin(String userAccount, String password, HttpServletRequest request) {
        //1.校验
        if (StrUtil.hasBlank(userAccount, password)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "参数为空0");
        }
        if (userAccount.length() < 4 || userAccount.length() > 16) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账户不存在");
        }
        if (password.length() < 4 || password.length() > 16) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码错误");
        }
        //2.对用户传递的密码进行加密
        String encryptPassword = getEncryptPassword(password);
        //3.查询该用户是否存在  不存在报错
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("userAccount", userAccount);
        qw.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(qw);
        if (user == null) {
            log.info("用户登录失败,账号或密码错误了");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        //4.保存用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);

        // 记录用户登录态到 Sa-token，便于空间鉴权时使用，注意保证该用户信息与 SpringSession 中的信息过期时间一致
        StpKit.SPACE.login(user.getId());
        StpKit.SPACE.getSession().set(UserConstant.USER_LOGIN_STATE, user);

//        LoginUserVo loginUserVo = new LoginUserVo();
//        BeanUtil.copyProperties(user, loginUserVo);
        return getLoginUserVO(user);
    }


    @Override
    public String getEncryptPassword(String userPassword) {
        //加密后,还要加盐,混淆密码
        final String YAN = "baicai";
        return DigestUtils.md5DigestAsHex((YAN + userPassword).getBytes());
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        //判断是否已登录
        User currentUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //从数据库在查一下,防止用户修改后,缓存(当前信息)不一致
        Long userId = currentUser.getId();
        currentUser = this.getById(userId);
        return currentUser;
    }

    @Override
    public LoginUserVo getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVo loginUserVo = new LoginUserVo();
        BeanUtil.copyProperties(user, loginUserVo);
        return loginUserVo;
    }

    @Override
    public UserVo getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(user, userVo);
        return userVo;
    }

    @Override
    public List<UserVo> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }

        return userList.stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        //判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }
        // 登出,移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        StpKit.SPACE.logout();
        return true;
    }

    @Override
    public QueryWrapper<User> queryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            return new QueryWrapper(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        int current = userQueryRequest.getCurrent();
        int pageSize = userQueryRequest.getPageSize();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);

        return queryWrapper;
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }
}



