/***********************************************************************************
 * Copyright (c) 2013. Nickolay Gerilovich. Russia.
 *   Some Rights Reserved.
 ************************************************************************************/

package cn.jboost.springboot.logging;

/**
 * A holder for a pair.
 */
final class ExceptionSeverity {
    private final Severity severity;
    private final boolean stackTrace;

    private ExceptionSeverity(Severity severity, boolean stackTrace) {
        this.severity = severity;
        this.stackTrace = stackTrace;
    }

    public static ExceptionSeverity create(Severity severity, boolean stackTrace) {
        return new ExceptionSeverity(severity, stackTrace);
    }

    public Severity getSeverity() {
        return severity;
    }

    public boolean getStackTrace() {
        return stackTrace;
    }
}
