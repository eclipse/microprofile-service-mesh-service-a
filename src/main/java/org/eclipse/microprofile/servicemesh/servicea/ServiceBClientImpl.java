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
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

/**
 * A CDI wrapper around the ServiceB Rest Client which allows us to add fault tolerance annotations (Retry and Fallback).
 * If the call to ServiceB fails then first it is retried and then, if it still fails, a fallback method is used instead.
 */
@ApplicationScoped
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
     * By default, serviceB will take somewhere between 100 and 5000ms to perform it's simulated work.
     * The Timeout on this method is set to 2000ms so it may fail.
     * If it fails then the method will be retried a maximum of 5 times.
     * Although the maximum number of retries is set to 5, the CircuitBreaker is set to open if 2 out of the last 4 calls fail (timeout).
     * Once open, by default the CircuitBreaker will stay open for 10000ms, during which time all calls will fail immediately.
     * After 5000ms, if the next two calls are successful then the CircuitBreaker will re-close completely.
     * If all 5 of the retries fail (e.g. if the CircuitBreaker is open) then the fallback method will be called instead.
     * 
     * @param ts
     * @return
     * @throws Exception
     */
    @Retry(maxRetries = 5)
    @Fallback(fallbackMethod = "fallback")
    @CircuitBreaker(failureRatio=0.5, requestVolumeThreshold=4, successThreshold=2, delay=10000, delayUnit=ChronoUnit.MILLIS)
    @Asynchronous
    @Timeout(value=2000, unit=ChronoUnit.MILLIS)
    public Future<ServiceData> call(TracerHeaders ts) throws Exception {
        ++tries;

        String urlString = getURL();
        URL url = new URL(urlString);

        ServiceBClient serviceBClient = RestClientBuilder.newBuilder()
                                             .baseUrl(url)
                                             .build(ServiceBClient.class);


        ServiceData serviceBData = serviceBClient.call(ts.user,ts.xreq,ts.xtraceid,ts.xspanid,ts.xparentspanid,
                                                       ts.xsampled,ts.xflags,ts.xotspan);

        serviceBData.setTries(getTries());

        return CompletableFuture.completedFuture(serviceBData);
    }

    public Future<ServiceData> fallback(TracerHeaders _ts) {

        ServiceData data = new ServiceData();
        data.setSource(this.toString());
        data.setCallCount(1);
        data.setMessage("ServiceBClient fallback @ "+data.getTime()+". ServiceB could not be reached at: "+getURL());
        data.setTries(getTries());
        data.setFallback(true);

        return CompletableFuture.completedFuture(data);
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
