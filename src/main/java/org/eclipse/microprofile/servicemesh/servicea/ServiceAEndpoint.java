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
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Make ServiceA available as a jaxrs endpoint which produces json. jsonp is
 * used to automatically convert the response from serviceA into json.
 */
@RequestScoped
@Path("/")
public class ServiceAEndpoint {

    @Inject
    private ServiceA serviceA;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ServiceData callServiceA(@HeaderParam("end-user") String user,
                                @HeaderParam("x-request-id") String xreq,
                                @HeaderParam("x-b3-traceid") String xtraceid,
                                @HeaderParam("x-b3-spanid") String xspanid,
                                @HeaderParam("x-b3-parentspanid") String xparentspanid,
                                @HeaderParam("x-b3-sampled") String xsampled,
                                @HeaderParam("x-b3-flags") String xflags,
                                @HeaderParam("x-ot-span-context") String xotspan,
                                @HeaderParam("user-agent") String userAgent
                            ) throws Exception {

        TracerHeaders ts = new TracerHeaders();
        ts.user = user;
        ts.xreq = xreq;
        ts.xtraceid = xtraceid;
        ts.xspanid = xspanid;
        ts.xparentspanid = xparentspanid;
        ts.xsampled = xsampled;
        ts.xflags = xflags;
        ts.xotspan = xotspan;


        ServiceData data = serviceA.call(ts, userAgent);

        return data;
    }
}
