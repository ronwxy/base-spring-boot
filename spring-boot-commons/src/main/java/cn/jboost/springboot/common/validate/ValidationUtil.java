package cn.jboost.springboot.common.validate;

import cn.jboost.springboot.common.exception.CommonErrorCodeEnum;
import cn.jboost.springboot.common.exception.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * 验证工具类
 */
public class ValidationUtil {

    /**
     * 验证空值
     */
    public static void isNull(Optional optional) {
        if (!optional.isPresent()) {
            ExceptionUtil.rethrowClientSideException(CommonErrorCodeEnum.NOT_EXIST);
        }
    }

    /**
     * 验证是否为邮箱
     *
     * @param string
     * @return
     */
    public static boolean isEmail(String string) {
        if (StringUtils.isEmpty(string)) {
            return false;
        }
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        return string.matches(regEx1);
    }

    /**
     * 验证是否为手机号
     *
     * @param string
     * @return
     */
    public static boolean isPhone(String string) {
        if (StringUtils.isEmpty(string)) {
            return false;
        }
        String regEx1 = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])[0-9]{8}$";
        return string.matches(regEx1);
    }

    /**
     * 验证是否为身份证号(包含15位和18位)
     *
     * @param string
     * @return
     */
    public static boolean isIdentity(String string) {
        if (StringUtils.isEmpty(string)) {
            return false;
        }
        String regEx1 = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$|" +
                "^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}[0-9Xx]$";
        return string.matches(regEx1);
    }

    /**
     * 验证是否为2-10位中文姓名
     *
     * @param string
     * @return
     */
    public static boolean isRealname(String string) {
        if (StringUtils.isEmpty(string)) {
            return false;
        }
        String regEx1 = "^[\\u4e00-\\u9fa5]{2,10}$";
        return string.matches(regEx1);
    }

    /**
     * 验证是否是性别
     *
     * @param string
     * @return
     */
    public static boolean isGender(String string) {
        if (StringUtils.isEmpty(string)) {
            return false;
        }
        String regEx1 = "^[F,M]$";
        return string.matches(regEx1);
    }

    /**
     * 验证是否是字母或数字
     * @param string
     * @return
     */
    public static boolean isLetterOrNumber(String string){
        if (StringUtils.isEmpty(string)) {
            return false;
        }
        String regEx1 = "^[a-zA-Z\\d]+$";
        return string.matches(regEx1);
    }
}
