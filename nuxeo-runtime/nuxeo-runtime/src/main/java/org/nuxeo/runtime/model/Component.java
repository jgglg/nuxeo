/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.runtime.model;

import java.time.Instant;

import org.nuxeo.runtime.service.TimestampedService;

/**
 * A Nuxeo Runtime component.
 * <p>
 * Components are extensible and adaptable objects and they provide methods to respond to component life cycle events.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public interface Component extends Extensible, TimestampedService {

    /**
     * Activates the component.
     * <p>
     * This method is called by the runtime when a component is activated.
     *
     * @param context the runtime context
     */
    void activate(ComponentContext context);

    /**
     * Deactivates the component.
     * <p>
     * This method is called by the runtime when a component is deactivated.
     *
     * @param context the runtime context
     */
    void deactivate(ComponentContext context);

    /**
     * The component notification order for {@link #applicationStarted}.
     * <p>
     * Components are notified in increasing order. Order 1000 is the default order for components that don't care.
     * Order 100 is the repository initialization.
     *
     * @return the order, 1000 by default
     * @since 5.6
     */
    int getApplicationStartedOrder();

    /**
     * Notify the component that Nuxeo Framework finished starting all Nuxeo bundles.
     */
    void applicationStarted(ComponentContext context);

    /**
     * Notify the component that Nuxeo Framework is about to shutdown
     *
     * @param context
     * @param deadline
     *            The instant at which the runtime will be shutdown
     *
     * @since 9.2
     */
    void applicationStopped(ComponentContext context, Instant deadline) throws InterruptedException;

}
