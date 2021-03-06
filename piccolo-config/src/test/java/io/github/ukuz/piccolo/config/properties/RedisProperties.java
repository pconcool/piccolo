/*
 * Copyright 2019 ukuz90
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
package io.github.ukuz.piccolo.config.properties;

import io.github.ukuz.piccolo.api.config.ConfigurationProperties;
import io.github.ukuz.piccolo.api.config.Properties;
import lombok.Data;

/**
 * @author ukuz90
 */
@ConfigurationProperties(prefix = "piccolo.redis")
@Data
public class RedisProperties implements Properties {

    private String mode;
    private int maxConnNum;
    private boolean enabled;
    private long timeBetweenEvictionRunsMillis;
    private short prop1;
    private float prop2;
    private double prop3;
    private byte prop4;

    private JedisPoolConfig jedisPoolConfig;

    @Data
    class JedisPoolConfig implements Properties {

        boolean testWhileIdle;


    }

}
