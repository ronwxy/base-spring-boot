package cn.jboost.springboot.autoconfig.alisms;

import cn.jboost.springboot.common.exception.ExceptionUtil;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.text.MessageFormat;

/**
 * @author yuxk
 * @version V1.0
 * @Title:
 * @Description:
 * @date：2019-9-13 0:08
 */
@Slf4j
public class AliSmsManager {

    private AliSmsProperties properties;

    /**
     * 验证码短信模板参数json
     */
    private static final String VERIFY_CODE_PARAM_TEMPLATE = "'{'\"code\":\"{0}\"}";

    public AliSmsManager(AliSmsProperties properties) {
        Assert.notNull(properties, "aliyun sms properties is null.");
        Assert.notNull(properties.getAccessKeyId(), "aliyun sms accessKeyId is not set.");
        Assert.notNull(properties.getAccessKeySecret(), "aliyun sms accessKeySecret is not set.");
        this.properties = properties;
    }

    /**
     * 获取IAcsClient对象
     *
     * @return
     * @throws ClientException
     */
    private IAcsClient initClient() throws ClientException {
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        // 初始化ascClient需要的几个参数
        final String product = "Dysmsapi";// 短信API产品名称
        final String domain = "dysmsapi.aliyuncs.com";// 短信API产品域名
        // 秘钥key和secret
        // 初始化ascClient,暂时不支持多region
        IClientProfile profile = DefaultProfile.getProfile("cn-changsha", properties.getAccessKeyId(), properties.getAccessKeySecret());
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-changsha", product, domain);
        return new DefaultAcsClient(profile);
    }

    /**
     * 发送短信验证码
     *
     * @param phoneNumber
     * @param identify
     */
    public boolean sendVerifyCode(String templateCode, String phoneNumber, String identify) throws ClientException {
        return sendSms(phoneNumber, properties.getSignName(), templateCode, MessageFormat.format(VERIFY_CODE_PARAM_TEMPLATE, identify));
    }

    /**
     * 发送默认模板短信验证码
     *
     * @param phoneNumber
     * @param identify
     */
    public boolean sendVerifyCode( String phoneNumber, String identify) throws ClientException {
        return sendSms(phoneNumber, properties.getSignName(), properties.getTemplateCode(), MessageFormat.format(VERIFY_CODE_PARAM_TEMPLATE, identify));
    }


    /**
     * 短信发送
     *
     * @param phoneNumber 手机号码
     * @param signName    短信签名
     * @param smsTempCode 短信模板code
     * @param tempParam   短信模板参数
     * @return
     * @throws Exception
     */
    public boolean sendSms(String phoneNumber, String signName, String smsTempCode, String tempParam) throws ClientException {
        log.info("send sms, phone:{}, signName:{}, smsTemplateCode:{}, content:{}", phoneNumber, signName, smsTempCode, tempParam);
        IAcsClient acsClient = initClient();
        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为20个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
        request.setPhoneNumbers(phoneNumber);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(smsTempCode);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam(tempParam);
        SendSmsResponse response = acsClient.getAcsResponse(request);
        if (StringUtils.equals("isv.BUSINESS_LIMIT_CONTROL", response.getCode())) {
            ExceptionUtil.rethrowClientSideException("短信已达上限,每天限10条,1小时内不超过5条!");
        } else if (!StringUtils.equals("OK", response.getCode())) {
            ExceptionUtil.rethrowClientSideException(response.getMessage());
        }
        log.info("send sms successfully. phone:{}, content:{}", phoneNumber, tempParam);
        return StringUtils.equals("OK", response.getCode());
    }

}
