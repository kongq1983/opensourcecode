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
package org.apache.dubbo.registry.support;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.integration.RegistryDirectory;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

/**
 * @date 2017/11/23
 */
public class ConsumerInvokerWrapper<T> implements Invoker {
    private Invoker<T> invoker;
    private URL originUrl;
    private URL registryUrl;
    private URL consumerUrl;
    private RegistryDirectory registryDirectory;

    public ConsumerInvokerWrapper(Invoker<T> invoker, URL registryUrl, URL consumerUrl, RegistryDirectory registryDirectory) {
        this.invoker = invoker; // interface com.kq.api.IDemoService -> zookeeper://192.168.5.217:2181/org.apache.dubbo.registry.RegistryService?anyhost=true&application=consumer-of-helloworld-app&check=false&deprecated=false&dubbo=2.0.2&dynamic=true&generic=false&interface=com.kq.api.IDemoService&lazy=false&methods=getMessage&pid=9228&register=true&register.ip=192.168.6.170&release=2.7.2&remote.application=hello-world-app&revision=1.0.0&side=consumer&sticky=false&timeout=1000&timestamp=1563880571249&version=1.0.0
        this.originUrl = URL.valueOf(invoker.getUrl().toFullString()); // zookeeper://192.168.5.217:2181/org.apache.dubbo.registry.RegistryService?anyhost=true&application=consumer-of-helloworld-app&check=false&deprecated=false&dubbo=2.0.2&dynamic=true&generic=false&interface=com.kq.api.IDemoService&lazy=false&methods=getMessage&pid=9228&register=true&register.ip=192.168.6.170&release=2.7.2&remote.application=hello-world-app&revision=1.0.0&side=consumer&sticky=false&timeout=1000&timestamp=1563880571249&version=1.0.0
        this.registryUrl = URL.valueOf(registryUrl.toFullString()); // zookeeper://192.168.5.217:2181/org.apache.dubbo.registry.RegistryService?application=consumer-of-helloworld-app&dubbo=2.0.2&pid=9228&refer=application%3Dconsumer-of-helloworld-app%26dubbo%3D2.0.2%26interface%3Dcom.kq.api.IDemoService%26lazy%3Dfalse%26methods%3DgetMessage%26pid%3D9228%26register.ip%3D192.168.6.170%26release%3D2.7.2%26revision%3D1.0.0%26side%3Dconsumer%26sticky%3Dfalse%26timeout%3D1000%26timestamp%3D1563880571249%26version%3D1.0.0&release=2.7.2&timestamp=1563880888058
        this.consumerUrl = consumerUrl; // consumer://192.168.6.170/com.kq.api.IDemoService?application=consumer-of-helloworld-app&dubbo=2.0.2&interface=com.kq.api.IDemoService&lazy=false&methods=getMessage&pid=9228&release=2.7.2&revision=1.0.0&side=consumer&sticky=false&timeout=1000&timestamp=1563880571249&version=1.0.0
        this.registryDirectory = registryDirectory;
    }

    @Override
    public Class<T> getInterface() {
        return invoker.getInterface();
    }

    @Override
    public URL getUrl() {
        return invoker.getUrl();
    }

    @Override
    public boolean isAvailable() {
        return invoker.isAvailable();
    }

    @Override
    public Result invoke(Invocation invocation) throws RpcException {
        return invoker.invoke(invocation);
    }

    @Override
    public void destroy() {
        invoker.destroy();
    }

    public URL getOriginUrl() {
        return originUrl;
    }

    public URL getRegistryUrl() {
        return registryUrl;
    }

    public Invoker<T> getInvoker() {
        return invoker;
    }

    public URL getConsumerUrl() {
        return consumerUrl;
    }

    public RegistryDirectory getRegistryDirectory() {
        return registryDirectory;
    }
}
