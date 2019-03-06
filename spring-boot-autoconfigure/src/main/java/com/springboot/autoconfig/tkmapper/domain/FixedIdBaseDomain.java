package com.springboot.autoconfig.tkmapper.domain;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

/**
 * use it to define a table with an ID named {@code id},and with a default equals and hashcode depends on {@code id};
 *
 * @param <ID>
 */
public abstract class FixedIdBaseDomain<ID> extends BaseDomain<ID> implements Serializable {
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
		FixedIdBaseDomain<?> that = (FixedIdBaseDomain<?>) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
