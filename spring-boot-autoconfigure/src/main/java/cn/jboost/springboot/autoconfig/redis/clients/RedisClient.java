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

package cn.jboost.springboot.autoconfig.redis.clients;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Declarative configuration for a redis client. Add this annotation to any
 * <code>@Configuration</code> and then inject a {@link RedisClientsFactory} to access the
 * client that is created.
 * copy from the {@code RibbonClient} and redefine the import registrar;
 *
 * @author liubo
 * @author Dave Syer
 */
@Configuration
@Import(RedisClientsConfigurationRegistrar.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisClient {

    /**
     * Synonym for name (the name of the client)
     *
     * @see #name()
     */
    String value() default "";

    /**
     * The name of the redis client, uniquely identifying a set of client resources,
     * including a {@link org.springframework.data.redis.connection.RedisConnectionFactory}
     */
    String name() default "";

    /**
     * A custom <code>@Configuration</code> for the redis client. Can contain override
     * <code>@Bean</code> definition for the pieces that make up the client, for instance
     * {@link org.springframework.data.redis.connection.RedisConnectionFactory}
     *
     * @see RedisClientsConfiguration for the defaults
     */
    Class<?>[] configuration() default {};

}
