/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.springboot.boot.redis;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Convenience annotation that allows user to combine multiple <code>@@RedisClient</code>
 * annotations on a single class (including in Java 7).
 * <p>
 * copy from the {@code RibbonClients} and redefine the import registrar;
 *
 * @author liubo
 * @author Dave Syer
 */
@Configuration
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(RedisClientConfigurationRegistrar.class)
public @interface RedisClients {

    RedisClient[] value() default {};

    Class<?>[] defaultConfiguration() default {};

}
