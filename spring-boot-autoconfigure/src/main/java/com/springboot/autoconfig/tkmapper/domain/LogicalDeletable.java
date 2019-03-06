package com.springboot.autoconfig.tkmapper.domain;

import java.util.Optional;

/**
 * use to mark {@link BaseDomain},update deleted field rather than physically delete;
 */
public interface LogicalDeletable {
    Boolean getDeleted();

    default boolean isDeleted() {
        return Optional.ofNullable(getDeleted()).orElse(Boolean.FALSE);
    }

    void setDeleted(Boolean deleted);
}
