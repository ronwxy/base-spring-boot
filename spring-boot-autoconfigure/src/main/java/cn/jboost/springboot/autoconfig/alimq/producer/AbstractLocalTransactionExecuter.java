package cn.jboost.springboot.autoconfig.alimq.producer;

import cn.jboost.springboot.common.jackson.JsonUtil;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public abstract class AbstractLocalTransactionExecuter implements LocalTransactionExecuter {
    /**
     * 执行本地事务
     *
     * @param arg
     * @return 是否提交消息， true:提交， false：回滚， exception：回滚，并抛出异常
     */
    public abstract boolean localTranExecute(Object arg);

    @Override
    public TransactionStatus execute(Message message, Object arg) {

        /** 消息 ID 和 crc32id 主要是用来防止消息重复
         * 如果业务本身是幂等的，可以忽略，否则需要利用 msgId 或 crc32Id 来做幂等
         * 如果要求消息绝对不重复，推荐做法是对消息体 body 使用 crc32或 md5来防止重复消息
         */
        Map<String, Object> msgMap = (Map<String, Object>) JsonUtil.fromJson(message.getBody(), Map.class);
        try {
            boolean isCommit = this.localTranExecute(arg);
            if (isCommit) {
                // 本地事务成功则提交消息
                log.info("local transaction succeed. msgId: {}, msg body: {}", message.getMsgID(), msgMap);
                return TransactionStatus.CommitTransaction;
            } else {
                // 本地事务失败则回滚消息
                log.warn("local transaction fail. msgId: {}, msg body: {}", message.getMsgID(), msgMap);
                return TransactionStatus.RollbackTransaction;
            }
        } catch (Exception e) {
            log.error("local transaction execute error. msgId: {}, msg body: {} ", message.getMsgID(), msgMap);
            //本地事务抛出异常则回滚消息
            return TransactionStatus.RollbackTransaction;

        }
    }
}
