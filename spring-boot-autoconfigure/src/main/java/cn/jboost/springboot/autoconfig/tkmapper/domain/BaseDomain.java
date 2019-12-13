package cn.jboost.springboot.autoconfig.tkmapper.domain;


import java.io.Serializable;

/**
 * super class to define a entity
 *
 */
public abstract class BaseDomain implements Serializable {

	private static final long serialVersionUID = -8075827049184773786L;

	public abstract int hashCode();

	public abstract boolean equals(Object obj);
}

