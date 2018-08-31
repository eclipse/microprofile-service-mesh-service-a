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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Metric;

/**
 * ServiceA tries to call ServiceB (via a Rest Client) and then wrap the response up in a data bean to be returned.
 * It keeps track of how many times it has been called through an ApplicationScoped CallCounter.
 */
@RequestScoped
public class ServiceA {

    @Inject
    ServiceBClientImpl serviceBClient;

    @Inject
    @Metric(name="callCounter")
    Counter callCounter;

    @Counted(name="callCounter", monotonic=true)
    public ServiceData call(TracerHeaders ts) throws Exception {

        long callCount = callCounter.getCount();

        ServiceData serviceBData = serviceBClient.call(ts);

        ServiceData data = new ServiceData();
        data.setSource(this.toString());
        data.setMessage("Hello from serviceA @ "+data.getTime());
        data.setData(serviceBData);
        data.setCallCount(callCount);
        data.setTries(1);

        return data;
    }

}
