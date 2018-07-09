/*
 *******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
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
 *   2018-06-19 - Jon Hawkes / IBM Corp
 *      Initial code
 *
 *******************************************************************************/

package org.eclipse.microprofile.servicemesh.servicea;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

@Health
@WebListener
@ApplicationScoped
public class HealthEndpoint implements HealthCheck, ServletContextListener {

  static boolean healthy = true;

  /*
   *  If non-zero, this is how many seconds before we start reporting "DOWN"
   */
  @Inject
  @ConfigProperty(name = "lifetime", defaultValue = "0")
  private int lifetime;

  @Override
  public HealthCheckResponse call() {
    HealthCheckResponse hcr;

    if (healthy) {
      hcr = HealthCheckResponse.named("serviceB")
                                .withData("lifetime", lifetime)
                                .up().build();
    } else {
      hcr = HealthCheckResponse.named("serviceB")
                                .withData("lifetime", lifetime)
                                .down().build();
    }

    System.out.println("Health endpoint called: " + hcr);
    return hcr;
  }

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
  /* Set a timer to go unhealthy after lifetime seconds */
	public void contextInitialized(ServletContextEvent arg0) {
    if (lifetime > 0) {
      Date timeToRun = new Date(System.currentTimeMillis() + lifetime * 1000);
      Timer timer = new Timer();

      timer.schedule(new TimerTask() {
        public void run() {
          healthy = false;
        }
      }, timeToRun);
    }
  }
}
