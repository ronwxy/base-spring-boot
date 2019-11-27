package cn.jboost.springboot.common.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * 异常工具类
 *
 * @author ray4work@126.com
 * @date 2018/6/5 18:11
 */
public class ExceptionUtil {

    public static <E extends Enum<E> & BaseErrors> void rethrowClientSideException(E exceptionCode, Throwable cause) {
        throw new ClientSideException(exceptionCode, cause);
    }

    public static <E extends Enum<E> & BaseErrors> void rethrowForbiddenException(E exceptionCode, Throwable cause) {
        throw new ForbiddenException(exceptionCode, cause);
    }

    public static <E extends Enum<E> & BaseErrors> void rethrowServerSideException(E exceptionCode, Throwable cause) {
        throw new ServerSideException(exceptionCode, cause);
    }

    public static <E extends Enum<E> & BaseErrors> void rethrowUnauthorizedException(E exceptionCode, Throwable cause) {
        throw new UnauthorizedException(exceptionCode, cause);
    }

    public static <E extends Enum<E> & BaseErrors> void rethrowClientSideException(E exceptionCode) {
        throw new ClientSideException(exceptionCode, null);
    }

    public static <E extends Enum<E> & BaseErrors> void rethrowForbiddenException(E exceptionCode) {
        throw new ForbiddenException(exceptionCode, null);
    }

    public static <E extends Enum<E> & BaseErrors> void rethrowServerSideException(E exceptionCode) {
        throw new ServerSideException(exceptionCode, null);
    }

    public static <E extends Enum<E> & BaseErrors> void rethrowUnauthorizedException(E exceptionCode) {
        throw new UnauthorizedException(exceptionCode, null);
    }

    public static void addStackTrace(Map<String, Object> errorAttributes, Throwable error) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            error.printStackTrace(pw);
            sw.flush();
            errorAttributes.put(ExceptionConstants.ERROR_TRACE_KEY, sw.toString());
        }finally {
            pw.close();
        }
    }

}
