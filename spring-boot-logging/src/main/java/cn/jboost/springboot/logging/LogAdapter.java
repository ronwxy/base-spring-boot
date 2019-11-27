/***********************************************************************************
 * Copyright (c) 2013. Nickolay Gerilovich. Russia.
 *   Some Rights Reserved.
 ************************************************************************************/

package cn.jboost.springboot.logging;

import org.slf4j.Logger;

/**
 * Declares access to the logger and log message creation.
 */
interface LogAdapter {
    Logger getLog(Class clazz);

    Logger getLog(String name);

    String toMessage(String method, Object[] args, ArgumentDescriptor argumentDescriptor);

    String toMessage(String method, int argCount, Object result);

    String toMessage(String method, int argCount, Exception e, boolean stackTrace);

}
