/*
 * Copyright 2015 Async-IO.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.atmosphere.config.managed;

import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.config.service.Singleton;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.FrameworkConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle {@link org.atmosphere.config.service.Singleton},{@link org.atmosphere.config.service.MeteorService} and
 * {@link org.atmosphere.config.service.AtmosphereHandlerService} processing.
 *
 * @author Jeanfrancois Arcand
 */
public class AtmosphereHandlerServiceInterceptor extends ServiceInterceptor {

    private final static Logger logger = LoggerFactory.getLogger(AtmosphereHandlerServiceInterceptor.class);

    protected void mapAnnotatedService(boolean reMap, String path, AtmosphereRequest request, AtmosphereFramework.AtmosphereHandlerWrapper w) {
        synchronized (config.handlers()) {
            if (config.handlers().get(path) == null) {
                // AtmosphereHandlerService
                AtmosphereHandlerService m = w.atmosphereHandler.getClass().getAnnotation(AtmosphereHandlerService.class);
                if (m != null) {
                    try {
                        boolean singleton = w.atmosphereHandler.getClass().getAnnotation(Singleton.class) != null;
                        if (!singleton) {
                            config.framework().addAtmosphereHandler(path, config.framework().newClassInstance(AtmosphereHandler.class, w.atmosphereHandler.getClass()),
                                    config.getBroadcasterFactory().lookup(m.broadcaster(), path, true), w.interceptors);
                        } else {
                            config.framework().addAtmosphereHandler(path, w.atmosphereHandler,
                                    config.getBroadcasterFactory().lookup(m.broadcaster(), path, true), w.interceptors);
                        }
                        request.setAttribute(FrameworkConfig.NEW_MAPPING, "true");
                    } catch (Throwable e) {
                        logger.warn("Unable to create AtmosphereHandler", e);
                    }
                }

            }
        }
    }

    @Override
    public String toString() {
        return "@AtmosphereHandlerService Interceptor";
    }
}
