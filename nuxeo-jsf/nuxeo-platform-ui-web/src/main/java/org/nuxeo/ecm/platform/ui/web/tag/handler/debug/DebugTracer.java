/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
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
 *
 * Contributors:
 *     Anahide Tchertchian
 */
package org.nuxeo.ecm.platform.ui.web.tag.handler.debug;

import org.apache.commons.logging.Log;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.services.config.ConfigurationService;

/**
 * @since TODO
 */
public class DebugTracer {

    public static final String TRACE_PROP = "nuxeo.jsf.faceletHandler.traceLag";

    public static void trace(Log log, long start, String id) {
        ConfigurationService cs = Framework.getService(ConfigurationService.class);
        long lagValue = Long.valueOf(cs.getProperty(TRACE_PROP, "-1"));
        trace(log, start, id, lagValue);
    }

    public static void traceMillis(Log log, long start, String id) {
        trace(log, start, id, 0);
    }

    public static void trace(Log log, long start, String id, long maxLag) {
        if (maxLag >= 0) {
            long end = System.currentTimeMillis();
            long lag = end - start;
            if (lag > maxLag) {
                log.error(id + " took: " + lag + " ms.");
            }
        }
    }

}