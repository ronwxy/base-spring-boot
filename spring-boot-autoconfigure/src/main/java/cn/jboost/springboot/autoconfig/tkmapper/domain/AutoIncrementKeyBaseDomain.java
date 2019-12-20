package cn.jboost.springboot.autoconfig.tkmapper.domain;//package cn.jboost.springboot.parent.domain;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

/**
 * 用于自增主键场景
 * use it to define an entity with an ID named {@code id},and with a default equals and hashcode depends on {@code id}
 *
 * @param <>
 */
public abstract class AutoIncrementKeyBaseDomain extends BaseDomain {
	@Id
	protected Serializable id;

	public Serializable  getId() {
		return id;
	}

	public void setId(Serializable id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AutoIncrementKeyBaseDomain that = (AutoIncrementKeyBaseDomain) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
