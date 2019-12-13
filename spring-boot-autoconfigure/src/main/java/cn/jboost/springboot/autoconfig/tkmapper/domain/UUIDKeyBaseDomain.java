package cn.jboost.springboot.autoconfig.tkmapper.domain;

import tk.mybatis.mapper.annotation.KeySql;
import tk.mybatis.mapper.code.ORDER;

import javax.persistence.Id;
import java.util.Objects;

/***
 * 用于uuid主键场景
 * @Author ronwxy
 * @Date 2019/6/21 16:46   
 */
public abstract class UUIDKeyBaseDomain extends BaseDomain  {

    @Id
    @KeySql(sql="select replace(uuid(),'-','')", order = ORDER.BEFORE)
    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutoIncrementKeyBaseDomain<?> that = (AutoIncrementKeyBaseDomain<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
