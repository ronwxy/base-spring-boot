package cn.jboost.springboot.common.web;

import java.util.Collections;
import java.util.List;

/**
 * paged data dto;
 *
 * @param <T>
 */
public class QueryResultDto<T> {

    public final long totalRecords;

    public final List<T> data;

    public QueryResultDto() {
        this.totalRecords = 0;
        this.data = Collections.emptyList();
    }

    public QueryResultDto(long totalRecords, List<T> data) {
        super();
        this.totalRecords = totalRecords;
        this.data = data;
    }


}
