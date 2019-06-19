package cn.jboost.springboot.autoconfig.alimq.producer;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import cn.jboost.springboot.common.jackson.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class AbstractLocalTransactionChecker implements LocalTransactionChecker {

    private final static Logger LOG = LoggerFactory.getLogger(AbstractLocalTransactionChecker.class);

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
                LOG.info("local check succeed. msgId: {}, msg body: {}", message.getMsgID(), msgMap);
                return TransactionStatus.CommitTransaction;
            } else {
                LOG.info("local check fail. msgId: {}, msg body: {}", message.getMsgID(), msgMap);
                return TransactionStatus.RollbackTransaction;
            }
        }catch (Exception ex){
            LOG.error("local check succeed. msgId: {}, msg body: {}", message.getMsgID(), msgMap, ex);
            return TransactionStatus.Unknow;
        }
    }
}
