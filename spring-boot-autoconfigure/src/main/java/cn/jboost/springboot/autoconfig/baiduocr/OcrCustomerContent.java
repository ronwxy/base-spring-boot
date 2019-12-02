package cn.jboost.springboot.autoconfig.baiduocr;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yuxk
 * @version V1.0
 * @Title: 模板识别结果
 * @Description:
 * @date 2019/9/30 13:46
 */
@NoArgsConstructor
@Data
public class OcrCustomerContent {

    private String error_msg;
    private DataBean data;
    private Integer error_code;

    @NoArgsConstructor
    @Data
    public static class DataBean {
        private String templateSign;
        private String templateName;
        private Integer scores;
        private boolean isStructured;
        private String logId;
        private Integer clockwiseAngle;
        private List<RetBean> ret;

        @NoArgsConstructor
        @Data
        public static class RetBean {
            private ProbabilityBean probability;
            private LocationBean location;
            private String word_name;
            private String word;

            @NoArgsConstructor
            @Data
            public static class ProbabilityBean {
                private Double average;
                private Double min;
                private Integer variance;
            }

            @NoArgsConstructor
            @Data
            public static class LocationBean {
                private Integer top;
                private Integer left;
                private Integer width;
                private Integer height;
            }
        }
    }
}
