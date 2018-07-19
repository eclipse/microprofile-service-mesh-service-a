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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * A CDI wrapper around the ServiceB Rest Client which allows us to add fault tolerance annotations (Retry).
 * In future versions, hopefully this won't be needed.
 */
@Dependent
public class ServiceBClientImpl {

    @Inject
    @RestClient
    ServiceBClient serviceBClient;

    private int tries;

    @Retry(maxRetries = 2)
    public String callServiceB() throws Exception {
        ++tries;

        String serviceBData = serviceBClient.call();

        return serviceBData;
    }

    public int getTries() {
        return tries;
    }

}
