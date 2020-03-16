/*
 * Copyright (c) 2010-2016. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.transmitter.annotation;

import java.lang.annotation.*;

/**
 * 命令处理器注解
 * 事件处理分为聚合内处理和聚合外处理
 * 聚合内处理固定是同步处理
 * 聚合外处理默认异步，可选择同步处理，但同步处理异常会被框架捕获，不会抛给聚合
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface EventHandler {

    /**
     * 事件是否异步处理，默认异步，这个参数只在聚合外生效，聚合内事件只能是同步
     *
     * @return 是否异步处理事件
     */
    boolean async() default true;

}
