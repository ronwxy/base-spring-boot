package cn.jboost.springboot.autoconfig.baiduocr;

import cn.hutool.json.JSONUtil;
import cn.jboost.springboot.common.exception.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuxk
 * @version V1.0
 * @Title: 百度OCR识别工具类
 * @Description:
 * @date 2019/9/30 14:08
 */
public class BaiduOCRUtil {

    private BaiduOCRUtil() {
    }

    /**
     * 获取通用识别内容、报告单名称
     *
     * @param jsonObject
     * @return
     */
    public static Map<String, Object> getGeneralContent(JSONObject jsonObject, Map<String, Object> params) {
        String json = JSONUtil.toJsonStr(jsonObject);
        OcrGeneralContent ocrGeneralContent = JSONUtil.toBean(json, OcrGeneralContent.class);
        Integer errorCode = ocrGeneralContent.getError_code();
        if (errorCode != null && errorCode != 0) {
            ExceptionUtil.rethrowServerSideException("图片识别失败，错误码:" + errorCode + ",错误原因:" + ocrGeneralContent.getError_msg());
        }
        List<OcrGeneralContent.WordsResultBean> wordsResults = ocrGeneralContent.getWords_result();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        // 文本拼接换行，获取报告单名称
        boolean getReport = false;
        for (OcrGeneralContent.WordsResultBean wordsResult : wordsResults) {
            String words = wordsResult.getWords();
            if (!getReport && StringUtils.isNotEmpty(words) && (words.endsWith("报告") || words.endsWith("报告单"))) {
                params.put("report", words);
                getReport = true;
            }
            sb.append(words);
            if (count != wordsResults.size() - 1) {
                sb.append("\n");
            }
            count++;
        }
        String content = sb.toString();
        if (StringUtils.equals(content, "")) {
            ExceptionUtil.rethrowClientSideException("未识别出文字，请重新上传照片");
        }
        params.put("content", content);
        return params;
    }

    /**
     * 获取自定义识别内容，内容可以直接转成相应对象
     *
     * @param jsonObject
     * @return
     */
    public static String getCustomerContent(JSONObject jsonObject) {
        String json = JSONUtil.toJsonStr(jsonObject);
        OcrCustomerContent ocrCustomerContent = JSONUtil.toBean(json, OcrCustomerContent.class);
        Integer errorCode = ocrCustomerContent.getError_code();
        if (errorCode != null && errorCode != 0) {
            ExceptionUtil.rethrowServerSideException("图片识别失败，错误码:" + errorCode + ",错误原因:" + ocrCustomerContent.getError_msg());
        }
        List<OcrCustomerContent.DataBean.RetBean> ret = ocrCustomerContent.getData().getRet();
        Map<String, Object> params = new HashMap<>(ret.size());
        for (OcrCustomerContent.DataBean.RetBean retBean : ret) {
            String word = retBean.getWord();
            String wordName = retBean.getWord_name();
            params.put(wordName, word);
        }
        return JSONUtil.toJsonStr(params);
    }
}
