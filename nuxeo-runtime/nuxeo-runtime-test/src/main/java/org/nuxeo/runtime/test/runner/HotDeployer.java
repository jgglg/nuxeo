/*
 * (C) Copyright 2006-2016 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     bstefanescu
 */
package org.nuxeo.runtime.test.runner;

import org.nuxeo.runtime.api.Framework;

/**
 * A dynamic component deployer which enable tests to deploy new contributions
 * after the test was started (i.e. from inside the test method)
 *
 * The deployer is reloading all the components and reinject the test members.
 *
 * @author bogdan
 */
public class HotDeployer {

	/**
	 * Deploy actions are usually used by features to customize the deployment of the runtime feature (which is using the DeaultDeployAction)
	 * @author bogdan
	 *
	 */
	public static abstract class ActionHandler {
		protected ActionHandler next;
		/**
		 * Can wrap another deploy action with custom code. Should call the next action using
		 * <code>next.deploy(contribs)</code>
		 * @param action
		 * @param args
		 * @throws Exception
		 */
		public abstract void exec(String action, String ... args) throws Exception;
	}

	/**
	 * This action has no next action and will deploy the contributions and then reload the component manager
	 * @author bogdan
	 *
	 */
	class DefaultDeployHandler extends ActionHandler {

		@Override
		public void exec(String action, String ... args) throws Exception {
		    if ("deploy".equals(action)) {
		        deploy(args);
		    } else if ("restart".equals(action)) {
		        restart();
		    } else if ("reset".equals(action)) {
		        reset();
		    }
		}

		public void deploy(String ...contribs) throws Exception {
			if (contribs != null && contribs.length > 0) {
				for (String contrib : contribs) {
					int i = contrib.indexOf(':');
					if (i > -1) {
						String bundleId = contrib.substring(0, i);
						if (bundleId.startsWith("@")) {
							bundleId = bundleId.substring(1);
							harness.deployTestContrib(bundleId, contrib.substring(i+1));
						} else {
						    harness.deployContrib(bundleId, contrib.substring(i+1));
						}
					} else {
						harness.deployBundle(contrib);
					}
				}
			}
			// use false to prevent removing the local test method deployments
			Framework.getRuntime().getComponentManager().refresh(false);
		}

		public void restart() throws Exception {
		    Framework.getRuntime().getComponentManager().restart(false);
		}

		public void reset() throws Exception {
		    Framework.getRuntime().getComponentManager().restart(true);
		}
	}

	protected FeaturesRunner runner;
	protected RuntimeHarness harness;
	protected ActionHandler head;

	public HotDeployer(FeaturesRunner runner, RuntimeHarness harness) {
		this.runner = runner;
		this.harness = harness;
		this.head = new DefaultDeployHandler();
	}

	/**
	 * Add a custom deploy action that wraps the default action.
	 * You should call next.deploy(..) in your action code to call the next action
	 * @param action
	 * @return
	 */
	public HotDeployer addHandler(ActionHandler action) {
		action.next = this.head;
		this.head = action;
		return this;
	}

	/**
	 * Deploy the given list of contributions. The format is [@]bundleId[:componentPath].
	 * If no component path is specified then the bundle identified by the bundleId part will be deployed.
	 * If a componentPath is given {@link RuntimeHarness#deployContrib(String,String)} will be used to deploy the contribution.
	 * If bundleId:componentPath expression is prefixed by a '@' character then {@link RuntimeHarness#deployTestContrib(String,String)} will be used to deploy the contribution	 *
	 * @param contribs
	 * @throws Exception
	 */
	public void deploy(String ... contribs) throws Exception {
		this.head.exec("deploy", contribs);
		reinject();
	}

	/**
	 * Restart the components and preserve the current registry state.
	 * @param reset
	 * @throws Exception
	 */
	public void restart() throws Exception {
	    this.head.exec("restart");
	    reinject();
	}

	/**
	 * Restart the components and revert to the initial registry snapshot if any.
	 * @throws Exception
	 */
	public void reset() throws Exception {
	    this.head.exec("reset");
	    reinject();
	}

	public void reinject() {
		runner.getInjector().injectMembers(runner.getTargetTestInstance());
	}


}