package com.springboot.autoconfig.tkmapper.domain;

import java.util.Date;

/**
 * use to mark {@link BaseDomain},that the domain entity can record the create time,update time,and the operator;
 */
public interface Auditable {
    Date getCreateTime();

    void setCreateTime(Date date);

    Date getUpdateTime();

    void setUpdateTime(Date date);

    Long getOperatorId();

    void setOperatorId(Long operatorId);

}
