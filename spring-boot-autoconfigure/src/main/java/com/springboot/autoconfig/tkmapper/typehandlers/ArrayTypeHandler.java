package com.springboot.autoconfig.tkmapper.typehandlers;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.TypeException;

import java.sql.*;

@MappedJdbcTypes(JdbcType.ARRAY)
public class ArrayTypeHandler extends BaseTypeHandler<Object[]> {

	private static final String TYPE_NAME_VARCHAR = "varchar";
	private static final String TYPE_NAME_INTEGER = "integer";
	private static final String TYPE_NAME_LONG = "bigint";
	private static final String TYPE_NAME_BOOLEAN = "boolean";
	private static final String TYPE_NAME_NUMERIC = "numeric";

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i,
			Object[] parameter, JdbcType jdbcType) throws SQLException {
		if (parameter == null) {
			ps.setNull(i, Types.ARRAY);
		} else {
			String typeName = null;
			if (parameter instanceof String[]) {
				typeName = TYPE_NAME_VARCHAR;
			} else if (parameter instanceof Integer[]) {
				typeName = TYPE_NAME_INTEGER;
			} else if (parameter instanceof Long[]) {
				typeName = TYPE_NAME_LONG;
			} else if (parameter instanceof Boolean[]) {
				typeName = TYPE_NAME_BOOLEAN;
			} else if (parameter instanceof Double[]) {
				typeName = TYPE_NAME_NUMERIC;
			}
			if (typeName == null) {
				throw new TypeException(
						"unsupport parameter type error, type is "
								+ parameter.getClass().getName());
			}
			Connection conn = ps.getConnection();
			Array array = conn.createArrayOf(typeName, parameter);
			ps.setArray(i, array);
		}
	}

	@Override
	public Object[] getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		return getArray(rs.getArray(columnName));
	}

	@Override
	public Object[] getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		return getArray(rs.getArray(columnIndex));
	}

	@Override
	public Object[] getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return getArray(cs.getArray(columnIndex));
	}

	private Object[] getArray(Array array) {
		if (array == null) {
			return null;
		}
		try {
			Object[] value = (Object[]) array.getArray();
			// AbstractJdbc2Array.getArrayImpl 在多次调用后（几次到十几次）会使用binary的方式处理，返回数据库对应的java类型，
			// 如果数据库定义smallint[]类型，Bean中对应是Integer[]类型，则调用几次后getArray()返回Short[]，会产生CastClass异常
			if (value != null && value instanceof Short[]) {
				Integer[] tmp = new Integer[value.length];
				for(int i=0;i<value.length;i++){
					tmp[i] = ((Short)value[i]).intValue();
				}
				value = tmp;
			}
			return value;
		} catch (Exception e) {
		}
		return null;
	}

}
