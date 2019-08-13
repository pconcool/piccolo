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
package io.github.ukuz.piccolo.core;

import io.github.ukuz.piccolo.api.PiccoloContext;
import io.github.ukuz.piccolo.api.cache.CacheManager;
import io.github.ukuz.piccolo.api.common.Monitor;
import io.github.ukuz.piccolo.api.common.threadpool.ExecutorFactory;
import io.github.ukuz.piccolo.api.config.Environment;
import io.github.ukuz.piccolo.api.config.Properties;
import io.github.ukuz.piccolo.api.mq.MQClient;
import io.github.ukuz.piccolo.api.service.discovery.ServiceDiscovery;
import io.github.ukuz.piccolo.api.service.registry.ServiceRegistry;
import io.github.ukuz.piccolo.api.spi.Spi;
import io.github.ukuz.piccolo.api.spi.SpiLoader;
import io.github.ukuz.piccolo.common.event.EventBus;
import io.github.ukuz.piccolo.core.router.RouterCenter;
import io.github.ukuz.piccolo.core.server.ConnectServer;
import io.github.ukuz.piccolo.core.server.GatewayServer;
import io.github.ukuz.piccolo.core.session.ReusableSessionManager;
import io.github.ukuz.piccolo.core.threadpool.ServerExecutorFactory;
import io.github.ukuz.piccolo.registry.zookeeper.ZKServiceRegistryAndDiscovery;

/**
 * @author ukuz90
 */
public class PiccoloServer implements PiccoloContext {

    private final Environment environment;
    private final GatewayServer gatewayServer;
    private final ConnectServer connectServer;
    private final ReusableSessionManager reusableSessionManager;
    private final CacheManager cacheManager;
    private final ExecutorFactory executorFactory;
    private final MQClient mqClient;
    private final ZKServiceRegistryAndDiscovery srd;

    private final RouterCenter routerCenter;

    public PiccoloServer() {
        //initialize config
        environment = SpiLoader.getLoader(Environment.class).getExtension();
        environment.scanAllProperties();
        environment.load("piccolo-server.properties");

        //initialize eventBus
        executorFactory = new ServerExecutorFactory();
        EventBus.create(executorFactory.create(ExecutorFactory.EVENT_BUS, environment));

        srd = (ZKServiceRegistryAndDiscovery) SpiLoader.getLoader(ServiceRegistry.class).getExtension("zk");

        mqClient = SpiLoader.getLoader(MQClient.class).getExtension();

        reusableSessionManager = new ReusableSessionManager(this);

        cacheManager = SpiLoader.getLoader(CacheManager.class).getExtension();

        routerCenter = new RouterCenter(this);

        gatewayServer = new GatewayServer(this);
        connectServer = new ConnectServer(this);
    }

    @Override
    public Monitor getMonitor() {
        return null;
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return srd;
    }

    @Override
    public ServiceDiscovery getServiceDiscovery() {
        return srd;
    }

    @Override
    public CacheManager getCacheManager() {
        return cacheManager;
    }

    @Override
    public MQClient getMQClient() {
        return mqClient;
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public <T extends Properties> T getProperties(Class<T> clazz) {
        return environment.getProperties(clazz);
    }

    @Override
    public ExecutorFactory getExecutorFactory() {
        return executorFactory;
    }

    public GatewayServer getGatewayServer() {
        return gatewayServer;
    }

    public ConnectServer getConnectServer() {
        return connectServer;
    }

    public ReusableSessionManager getReusableSessionManager() {
        return reusableSessionManager;
    }

    public RouterCenter getRouterCenter() {
        return routerCenter;
    }
}
