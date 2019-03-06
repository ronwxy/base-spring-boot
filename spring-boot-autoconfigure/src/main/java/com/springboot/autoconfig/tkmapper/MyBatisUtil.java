package com.springboot.autoconfig.tkmapper;


import com.google.common.base.Strings;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class MyBatisUtil {

	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object o) {
		if (o == null)
			return true;

		if (o instanceof String) {
			return ((String) o).length() == 0;
		} else if (o instanceof Collection) {
			return ((Collection) o).isEmpty();
		} else if (o.getClass().isArray()) {
			return Array.getLength(o) == 0;
		} else if (o instanceof Map) {
			return ((Map) o).isEmpty();
		} else {
			return false;
		}

	}


	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}

	public static boolean jsNotEmpty(Object o) {
		return o != null && !"undefined".equals(o);
	}

	public static boolean isIterable(Object o) {
		if (o == null) {
			return false;
		} else if (o instanceof Collection) {
			return true;
		} else if (o instanceof Map) {
			return true;
		} else return o.getClass().isArray();
	}

	public static boolean isNumeric(Object o) {
		return (o instanceof Number);
	}

	public static boolean isGreaterThanZero(Number n) {
		if (n == null)
			return false;
		return (n.intValue() > 0);
	}

	public static boolean isStringEqual(String obj1, String obj2) {
		boolean result = false;
		if (obj1.equalsIgnoreCase(obj2)) result = true;
		return result;
	}

	public static boolean isNotZero(String obj1) {
		boolean result = true;
		if (obj1.equalsIgnoreCase("0")) result = false;
		return result;
	}

	public static boolean isBothNotNull(String obj1, String obj2) {
		boolean result = false;
		if (null != obj1 && null != obj2) result = true;
		return result;
	}

	public static boolean isFirstNull(String obj1, String obj2) {
		boolean result = false;
		if (null == obj1 && null != obj2) result = true;
		return result;
	}

	public static boolean isSecondNull(String obj1, String obj2) {
		boolean result = false;
		if (null != obj1 && null == obj2) result = true;
		return result;
	}

	public static String matchHead(String criteria) {
		if (!Strings.isNullOrEmpty(criteria)) {
			return criteria + "%";
		}
		return null;
	}

	public static String matchTail(String criteria) {
		if (!Strings.isNullOrEmpty(criteria)) {
			return "%" + criteria;
		}
		return null;
	}

	public static String matchAnywhere(String criteria) {
		if (!Strings.isNullOrEmpty(criteria)) {
			return "%" + criteria + "%";
		}
		return null;
	}
}
