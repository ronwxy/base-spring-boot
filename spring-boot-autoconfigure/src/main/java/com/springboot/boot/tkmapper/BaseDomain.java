package com.springboot.boot.tkmapper;


import java.io.Serializable;

/**
 * super class to define a entity in any situation;
 *
 * @param <ID>
 */
public abstract class BaseDomain<ID> implements Serializable {

	private static final long serialVersionUID = -8075827049184773786L;

	public abstract int hashCode();

	public abstract boolean equals(Object obj);
}

