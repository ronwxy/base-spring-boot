package cn.jboost.springboot.autoconfig.aoplog.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * @Author ronwxy
 * @Date 2020/5/28 16:32
 * @Version 1.0
 */
public class LogUtil {

    private static final String CALL = "call: ";
    private static final String RETURN = "return: ";
    private static final String THROW = "throw: ";

    private static final String NULL_VALUE = "NIL";
    private static final String ENUMERATION_START = "{";
    private static final String ENUMERATION_SEPARATOR = ",";
    private static final String ENUMERATION_END = "}";
    private static final String SIZE_START = "..<size=";
    private static final String SIZE_END = ">..";

    /**
     * 调用日志消息体
     * @param method
     * @param names
     * @param args
     * @return
     */
    public static String callMessage(String method, String[] names, Object[] args, int depthThreshold) {
        if (args.length == 0) {
            return CALL + method + "()";
        }

        StringBuilder buff = new StringBuilder(CALL).append(method).append('(');
        if (args.length > 1) {
            buff.append(args.length).append(" arguments: ");
        }
        if (names == null) {
            for (int i = 0; i < args.length; i++) {
                    buff.append(parse(args[i], depthThreshold));
                    buff.append(", ");
            }
        } else {
            for (int i = 0; i < names.length; i++) {
                buff.append(names[i]).append("=").append(parse(args[i], depthThreshold));
                buff.append(", ");
            }
        }
        buff.replace(buff.length()-2, buff.length(), ")");
        return buff.toString();
    }

    /**
     * 返回日志消息体
     * @param method
     * @param argCount
     * @param result
     * @return
     */
    public static String returnMessage(String method, int argCount, Object result, int depthThreshold) {
        StringBuilder buff = new StringBuilder(RETURN).append(method).append("(");
        if (argCount == 0) {
            buff.append("):" ).append(parse(result, depthThreshold));
        } else {
            buff.append(argCount).append(" arguments):").append(parse(result, depthThreshold));
        }
        return buff.toString();
    }

    /**
     * 异常日志消息体
     * @param method
     * @param argCount
     * @param e
     * @param stackTrace
     * @return
     */
    public static String throwMessage(String method, int argCount, Throwable e, boolean stackTrace) {
        StringBuilder buff = new StringBuilder(THROW).append(method).append("(");
        if (argCount == 0) {
            buff.append("):").append(e.getClass());
        } else {
            buff.append(argCount).append(" arguments):").append(e.getClass());
        }
        if (e.getMessage() != null) {
            buff.append("[").append(e.getMessage()).append("]");
        }

        if(stackTrace) {
            buff.append(", stack trace: ").append(extractStackTrace(e));
        }
        return buff.toString();
    }

    public static String extractStackTrace(Throwable error) {
        if (Objects.isNull(error)) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            error.printStackTrace(pw);
            sw.flush();
            return sw.toString();
        } finally {
            pw.close();
        }
    }

    /**
     * 参数或返回值解析
     * @param value
     * @param depthThreshold
     * @return
     */
    public static String parse(Object value, int depthThreshold) {
        if (value == null) {
            return NULL_VALUE;
        }
        Class<?> clazz = value.getClass();

        if (value instanceof Collection<?>) {
            return parseCollection((Collection<?>) value, depthThreshold);
        } else if (value instanceof Map<?, ?>) {
            return parseMap((Map<?, ?>) value, depthThreshold);
        } else if (clazz.isArray()) {
            return parseArray(value, depthThreshold);
        } else {
            //Bean需实现toString()方法，否则可能得不到预期的输出
            return value.toString();
        }
    }

    /**
     * 解析collection
     * @param value
     * @param depthThreshold
     * @return
     */
    private static String parseCollection(Collection<?> value, int depthThreshold) {
        StringBuilder buffer = new StringBuilder(ENUMERATION_START);
        if (!value.isEmpty()) {
            Iterator<?> iterator = value.iterator();
            for (int i = 0;; i++) {
                if (i == depthThreshold) {
                    buffer.append(SIZE_START).append(value.size()).append(SIZE_END);
                    break;
                }
                Object item = iterator.next();
                if (item == null) {
                    buffer.append(NULL_VALUE);
                } else {
                    buffer.append(parse(item, depthThreshold));
                }
                if (!iterator.hasNext()) {
                    break;
                }
                buffer.append(ENUMERATION_SEPARATOR);
            }
        }
        buffer.append(ENUMERATION_END);
        return buffer.toString();
    }

    /**
     * 解析map
     * @param value
     * @param depthThreshold
     * @return
     */
    private static String parseMap(Map<?, ?> value, int depthThreshold) {
        StringBuilder buffer = new StringBuilder(ENUMERATION_START);
        if (!value.isEmpty()) {
            Iterator<? extends Map.Entry> iterator = value.entrySet().iterator();
            for (int i = 0;; i++) {
                if (i == depthThreshold) {
                    buffer.append(SIZE_START).append(value.size()).append(SIZE_END);
                    break;
                }

                Map.Entry item = iterator.next(); // iterator shall never return null
                buffer.append(item.getKey()).append(":").append(parse(item.getValue(), depthThreshold));

                if (!iterator.hasNext()) {
                    break;
                }
                buffer.append(ENUMERATION_SEPARATOR);
            }
        }
        buffer.append(ENUMERATION_END);
        return  buffer.toString();
    }

    /**
     * 解析数组
     * @param value
     * @param depthThreshold
     * @return
     */
    private static String parseArray(Object value, int depthThreshold) {
        StringBuilder buffer = new StringBuilder(ENUMERATION_START);
        int maxIndex = Array.getLength(value) - 1;
        if (maxIndex != -1) {
            for (int i = 0;; i++) {
                if (i == depthThreshold) {
                    buffer.append(SIZE_START).append(maxIndex + 1).append(SIZE_END);
                    break;
                }
                Object item = Array.get(value, i);
                if (item == null) {
                    buffer.append(NULL_VALUE);
                } else {
                    buffer.append(parse(item, depthThreshold));
                }
                if (i == maxIndex) {
                    break;
                }
                buffer.append(ENUMERATION_SEPARATOR);
            }
        }
        buffer.append(ENUMERATION_END);
        return buffer.toString();
    }

}
