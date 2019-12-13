package cn.jboost.springboot.autoconfig.tkmapper.domain;//package cn.jboost.springboot.parent.domain;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

/**
 * 用于自增主键场景
 * use it to define an entity with an ID named {@code id},and with a default equals and hashcode depends on {@code id}
 *
 * @param <ID>
 */
public abstract class AutoIncrementKeyBaseDomain<ID extends Serializable> extends BaseDomain {
	@Id
	protected ID id;

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
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
