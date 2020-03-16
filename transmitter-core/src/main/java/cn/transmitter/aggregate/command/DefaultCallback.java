/*
 * Copyright (c) 2010-2014. Axon Framework
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

package cn.transmitter.aggregate.command;

import cn.transmitter.aggregate.command.message.CommandMessage;
import cn.transmitter.exception.TransmitterException;

/**
 * 异步结果回调函数
 *
 * @param <R> 结果类型
 * @param <C> 命令类型
 * @author cloud
 */
public class DefaultCallback<C, R> implements CommandCallback<C, R> {

    private R result;

    public DefaultCallback() {
    }

    @Override
    public void onSuccess(CommandMessage<? extends C> commandMessage, R executionResult) {
        this.result = executionResult;
    }

    @Override
    public void onFailure(CommandMessage<? extends C> commandMessage, Throwable cause) {
        // 运行时异常直接抛出
        if (cause instanceof RuntimeException) {
            throw (RuntimeException)cause;
        }
        // 非运行时异常调整为运行时异常抛出
        throw asRuntime((Exception)cause);
    }

    public R getResult() {
        return result;
    }

    private RuntimeException asRuntime(Exception e) {
        Throwable failure = e.getCause();
        if (failure instanceof Error) {
            throw (Error)failure;
        } else if (failure instanceof RuntimeException) {
            return (RuntimeException)failure;
        } else {
            return new TransmitterException("An exception occurred while executing a command", failure);
        }
    }
}
