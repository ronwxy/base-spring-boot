package com.springboot.autoconfig.error.handler;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@CrossOrigin
@RestControllerAdvice
public class DefaultWebApplicationExceptionHandler extends BaseWebApplicationExceptionHandler {
}
