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
 *******************************************************************************/
package org.eclipse.microprofile.servicemesh.servicea;

import java.net.URL;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

/**
 * A CDI wrapper around the ServiceB Rest Client which allows us to add fault tolerance annotations (Retry and Fallback).
 * If the call to ServiceB fails then first it is retried and then, if it still fails, a fallback method is used instead.
 */
@Dependent
public class ServiceBClientImpl {

    @Inject
    @ConfigProperty(name="serviceB_host", defaultValue="localhost")
    String serviceB_host;

    @Inject
    @ConfigProperty(name="serviceB_http_port", defaultValue="8080")
    String serviceB_http_port;

    @Inject
    @ConfigProperty(name="serviceB_context_root", defaultValue="/mp-servicemesh-sample/serviceB")
    String serviceB_context_root;

    private int tries;

    /**
     * The default values on serviceB are that it will fail 20% of the time and when it does fail, it will take 500ms and then it will stay failed for 2000ms
     * If it does not fail then serviceB simulates some work for 100ms.
     * 
     * If serviceB fails then we retry at most 5 times. However, since those retries should be in quick succession (no delay), the CircuitBreaker
     * should open after just one retry.
     * 
     * @param ts
     * @return
     * @throws Exception
     */
    @Retry(maxRetries = 5)
    @Fallback(fallbackMethod = "fallback")
    @CircuitBreaker(failureRatio=0.5, requestVolumeThreshold=4, successThreshold=2)
    public ServiceData call(TracerHeaders ts) throws Exception {
        ++tries;

        String urlString = getURL();
        URL url = new URL(urlString);

        ServiceBClient serviceBClient = RestClientBuilder.newBuilder()
                                             .baseUrl(url)
                                             .build(ServiceBClient.class);


        ServiceData serviceBData = serviceBClient.call(ts.user,ts.xreq,ts.xtraceid,ts.xspanid,ts.xparentspanid,
                                                       ts.xsampled,ts.xflags,ts.xotspan);

        serviceBData.setTries(getTries());

        return serviceBData;
    }

    public ServiceData fallback(TracerHeaders _ts) {

        ServiceData data = new ServiceData();
        data.setSource(this.toString());
        data.setCallCount(1);
        data.setMessage("ServiceBClient fallback @ "+data.getTime()+". ServiceB could not be reached at: "+getURL());
        data.setTries(getTries());
        data.setFallback(true);

        return data;
    }

    int getTries() {
        return tries;
    }

    private String getURL() {
        String slash = "/";
        if(serviceB_context_root.startsWith(slash)) {
            slash = "";
        }
        return "http://" + serviceB_host + ":" + serviceB_http_port + slash + serviceB_context_root;
    }

}
