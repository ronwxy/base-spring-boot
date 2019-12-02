package cn.jboost.springboot.autoconfig.baiduocr;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yuxk
 * @version V1.0
 * @Title: 通用识别结果
 * @Description:
 * @date 2019/9/30 13:45
 */
@NoArgsConstructor
@Data
public class OcrGeneralContent {

    private Long log_id;
    private Integer words_result_num;
    private Integer error_code;
    private String error_msg;
    private List<WordsResultBean> words_result;

    @NoArgsConstructor
    @Data
    public static class WordsResultBean {
        private String words;
    }
}
