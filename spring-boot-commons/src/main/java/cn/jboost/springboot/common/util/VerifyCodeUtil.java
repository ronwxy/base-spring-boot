package cn.jboost.springboot.common.util;

import cn.hutool.captcha.*;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * 验证码工具类
 *
 * @Author ronwxy
 * @Date 2020/5/20 11:58
 * @Version 1.0
 */
public class VerifyCodeUtil {
    public static final int DEFAULT_CODE_COUNT = 4;
    public static final int DEFAULT_DISTURB_NUM = 20;
    public static final int DEFAULT_THICKNESS = 2;

    /**
     * 创建线形干扰的图形验证码
     * @param width
     * @param height
     * @return
     */
    public static VerifyCode generateLineCaptcha(int width, int height) {
        return generateLineCaptcha(width, height, DEFAULT_CODE_COUNT, DEFAULT_DISTURB_NUM);
    }

    /**
     * 创建线形干扰的图形验证码
     * @param width
     * @param height
     * @param codeCount 字符个数
     * @param lineCount 干扰线段数
     * @return
     */
    public static VerifyCode generateLineCaptcha(int width, int height, int codeCount, int lineCount) {
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(width, height, codeCount, lineCount);
        return getVerifyCode(lineCaptcha);
    }

    /**
     * 创建圆形干扰的图形验证码
     * @param width
     * @param height
     * @return
     */
    public static VerifyCode generateCircleCaptcha(int width, int height) {
        return generateCircleCaptcha(width, height, DEFAULT_CODE_COUNT, DEFAULT_DISTURB_NUM);
    }

    /**
     * 创建圆形干扰的图形验证码
     * @param width
     * @param height
     * @param codeCount 字符个数
     * @param circleCount 干扰圆形个数
     * @return
     */
    public static VerifyCode generateCircleCaptcha(int width, int height, int codeCount, int circleCount) {
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(width, height, codeCount, circleCount);
        return getVerifyCode(circleCaptcha);
    }

    /**
     * 创建扭曲干扰的图形验证码
     * @param width
     * @param height
     * @return
     */
    public static VerifyCode generateShearCaptcha(int width, int height) {
        return generateShearCaptcha(width, height, DEFAULT_CODE_COUNT, DEFAULT_THICKNESS);
    }

    /**
     * 创建扭曲干扰的图形验证码
     * @param width
     * @param height
     * @param codeCount 字符个数
     * @param thickness 干扰线宽度
     * @return
     */
    public static VerifyCode generateShearCaptcha(int width, int height, int codeCount, int thickness) {
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(width, height, codeCount, thickness);
        return getVerifyCode(shearCaptcha);
    }

    /**
     * 创建四则运算图形验证码，以线形干扰方式
     * @param width
     * @param height
     * @return
     */
    public static VerifyCode generateMathCaptcha(int width, int height) {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(width, height, DEFAULT_CODE_COUNT, DEFAULT_DISTURB_NUM);
        captcha.setGenerator(new MathGenerator());
        return getVerifyCode(captcha);
    }

    /**
     * 创建随机验证码，用于手机验证码场景
     * @return
     */
    public static VerifyCode generateRandomCode(int codeCount) {
        RandomGenerator randomGenerator = new RandomGenerator("0123456789", codeCount);
        String code = randomGenerator.generate();
        VerifyCode verifyCode = new VerifyCode(null, code, null, LocalDateTimeUtil.getEpochSecond());
        return verifyCode;
    }

    private static VerifyCode getVerifyCode(ICaptcha captcha) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        captcha.write(bos);
        VerifyCode code = new VerifyCode(Base64.encode(bos.toByteArray()), captcha.getCode(), IdUtil.simpleUUID(), null);
        try {
            bos.close();
        } catch (IOException e) {

        }
        return code;
    }

    @Getter
    @Setter
    @ToString(exclude = {"img"})
    @AllArgsConstructor
    public static class VerifyCode implements Serializable {
        private String img;
        @JsonIgnore
        private String code;
        private String uuid;
        private Long created; //手机验证码需要记录创建时间用于请求限制
    }
}
