package com.springboot.boot.tkmapper;

import com.springboot.common.web.QueryResultDto;

import java.util.Collections;
import java.util.List;

/**
 * paged data dto;
 *
 * @param <T>
 */
public class QueryResult<T> {


    public final long totalRecords;

    public final List<T> data;

    public QueryResult(long totalRecords, List<T> data) {
        super();
        this.totalRecords = totalRecords;
        this.data = data;
    }

    public QueryResult() {
        this.totalRecords = 0;
        this.data = Collections.emptyList();
    }

    public QueryResultDto<T> toQueryResultDto() {
        return new QueryResultDto<>(totalRecords, data);
    }


}
