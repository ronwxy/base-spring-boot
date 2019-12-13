package cn.jboost.springboot.autoconfig.alimq.producer;

import cn.jboost.springboot.common.jackson.JsonUtil;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public abstract class AbstractLocalTransactionChecker implements LocalTransactionChecker {

    /**
     * 本地反查
     * @param msgMap
     * @return 是否提交消息，true：提交，false：回滚， exception： unknow（继续反查）
     */
    public abstract boolean localTranCheck(Map<String, Object> msgMap);

    @Override
    public TransactionStatus check(Message message) {
        Map<String, Object> msgMap = (Map<String, Object>) JsonUtil.fromJson(message.getBody(), Map.class);
        try {
            boolean isCommit = this.localTranCheck(msgMap);
            if (isCommit) {
                log.info("local check succeed. msgId: {}, msg body: {}", message.getMsgID(), msgMap);
                return TransactionStatus.CommitTransaction;
            } else {
                log.info("local check fail. msgId: {}, msg body: {}", message.getMsgID(), msgMap);
                return TransactionStatus.RollbackTransaction;
            }
        }catch (Exception ex){
            log.error("local check succeed. msgId: {}, msg body: {}", message.getMsgID(), msgMap, ex);
            return TransactionStatus.Unknow;
        }
    }
}
