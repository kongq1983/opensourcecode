/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.rpc.model;

import java.lang.reflect.Method;
/** 服务提供方法模型 */
public class ProviderMethodModel {
    private transient final Method method; // public abstract java.lang.String com.kq.api.IDemoService.getMessage(java.lang.String)
    private final String methodName; //getMessage 方法名
    private final String[] methodArgTypes; //参数类型: java.lang.String
    private final String serviceName; // com.kq.api.IDemoService:1.0.0


    public ProviderMethodModel(Method method, String serviceName) {
        this.method = method;
        this.serviceName = serviceName;
        this.methodName = method.getName();
        this.methodArgTypes = getArgTypes(method);
    }

    public Method getMethod() {
        return method;
    }

    public String getMethodName() {
        return methodName;
    }

    public String[] getMethodArgTypes() {
        return methodArgTypes;
    }

    public String getServiceName() {
        return serviceName;
    }

    private static String[] getArgTypes(Method method) {
        String[] methodArgTypes = new String[0];
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length > 0) {
            methodArgTypes = new String[parameterTypes.length];
            int index = 0;
            for (Class<?> paramType : parameterTypes) {
                methodArgTypes[index++] = paramType.getName();
            }
        }
        return methodArgTypes;
    }
}
