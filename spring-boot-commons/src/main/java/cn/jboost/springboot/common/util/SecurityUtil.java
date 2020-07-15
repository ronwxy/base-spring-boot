package cn.jboost.springboot.common.util;


import cn.hutool.json.JSONObject;
import cn.jboost.springboot.common.exception.CommonErrorCodeEnum;
import cn.jboost.springboot.common.exception.ExceptionUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 安全工具类，获取登录用户信息
 */
public class SecurityUtil {

    public static UserDetails getUserDetails() {
        UserDetails userDetails = null;
        try {
            userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            ExceptionUtil.rethrowUnauthorizedException(CommonErrorCodeEnum.TOKEN_EXPIRED.getMessage(), e);
        }
        return userDetails;
    }

    /**
     * 获取系统用户名称
     * @return 系统用户名称
     */
    public static String getUsername(){
        Object obj = getUserDetails();
        JSONObject json = new JSONObject(obj);
        return json.get("username", String.class);
    }

    /**
     * 获取系统用户id
     * @return 系统用户id
     */
    public static Long getUserId(){
        Object obj = getUserDetails();
        JSONObject json = new JSONObject(obj);
        return json.get("id", Long.class);
    }
}
