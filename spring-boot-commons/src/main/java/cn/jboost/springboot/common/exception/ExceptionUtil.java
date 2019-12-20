package cn.jboost.springboot.common.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

/**
 * 异常工具类
 *
 * @author ray4work@126.com
 * @date 2018/6/5 18:11
 */
public class ExceptionUtil {

    private ExceptionUtil() {
    }

    public static void rethrowClientSideException(String message, Throwable cause) {
        throw new ClientSideException(message, cause);
    }

    public static void rethrowClientSideException(String message) {
        rethrowClientSideException(message, null);
    }

    public static void rethrowForbiddenException(String message, Throwable cause) {
        throw new ForbiddenException(message, cause);
    }

    public static void rethrowForbiddenException(String message) {
        rethrowForbiddenException(message, null);
    }

    public static void rethrowUnauthorizedException(String message, Throwable cause) {
        throw new UnauthorizedException(message, cause);
    }

    public static void rethrowUnauthorizedException(String message) {
        rethrowUnauthorizedException(message, null);
    }

    public static void rethrowServerSideException(String message, Throwable cause) {
        throw new ServerSideException(message, cause);
    }

    public static void rethrowServerSideException(String message) {
        rethrowServerSideException(message, null);
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

}
