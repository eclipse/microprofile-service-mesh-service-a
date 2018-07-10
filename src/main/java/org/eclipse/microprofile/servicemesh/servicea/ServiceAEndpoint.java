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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;

@RequestScoped
@Path("serviceA")
public class ServiceAEndpoint {

    @Inject
    @ConfigProperty(name = "svcBHost", defaultValue = "localhost")
    private String serviceBHost;

    @Inject
    @ConfigProperty(name = "svcBPort", defaultValue = "8080")
    private String serviceBPort;

    StringBuilder url;
    static int callCount;
    int tries;

    @GET
    @Retry
    @Fallback(fallbackMethod="serviceAFallback")
    @Produces(MediaType.TEXT_PLAIN)
    public String callServiceB() throws Exception {

      ++callCount;
      ++tries;

      url = new StringBuilder();
      url.append("http://")
          .append(serviceBHost)
          .append(":")
          .append(serviceBPort)
          .append("/mp-servicemesh-sample/serviceB");

      return "Hello from serviceA (" + this + ")\n" + callService(url);
    }

    public String serviceAFallback() {

        return "Hello from serviceAFallback at " + new Date() + " (ServiceA call count: " + callCount + ")\nCompletely failed to call " + url + " after " + tries + " tries";
    }

    private String callService(StringBuilder url) throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append("Calling service at: ")
            .append(url)
            .append(" (ServiceA call count: " + callCount + ", tries: " + tries)
            .append(")");

        System.out.println(sb.toString());

        sb.append("\n");

        String result = null;
        
        try {
          result = ClientBuilder.newClient()
                            .target(url.toString())
                            .request(MediaType.TEXT_PLAIN)
                            .get(String.class);
        } catch (Exception e) {
          System.out.println("Caught exception");
          e.printStackTrace();
          throw e;
        }

        return sb.append(result).toString();
    }
}
