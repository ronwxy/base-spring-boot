package cn.jboost.springboot.parent.domain;


import java.io.Serializable;

/**
 * super class to define a entity in any situation;
 *
 * @param <ID> 主键类型
 */
public abstract class BaseDomain<ID> implements Serializable {

	private static final long serialVersionUID = -8075827049184773786L;

	public abstract int hashCode();

	public abstract boolean equals(Object obj);
}

