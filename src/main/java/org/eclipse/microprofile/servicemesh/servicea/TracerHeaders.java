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

/**
 * POJO to store the incoming trace data and to forward it on the outgoing call.
 * This is needed as otherwise Istio will see this as two different traces.
 * See https://istio.io/docs/tasks/telemetry/distributed-tracing/#understanding-what-happened
 * for what to do in your appllication and
 * https://medium.com/@pilhuhn/working-on-microprofile-service-mesh-istio-and-kiali-26d6c01b45cc
 * for a more throughout explanation on what is happening if those headers are not
 * propagated.
 *
 * Once MicroProfile specs and implementations have been augmented to automatically
 * propagate these headers, this class and its usages can go away.
 * @author hrupp
 */
public class TracerHeaders {
  String user;
  String xreq;
  String xtraceid;
  String xspanid;
  String xparentspanid;
  String xsampled;
  String xflags;
  String xotspan;

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("TracerHeaders{");
    sb.append("user='").append(user).append('\'');
    sb.append(", xreq='").append(xreq).append('\'');
    sb.append(", xtraceid='").append(xtraceid).append('\'');
    sb.append(", xspanid='").append(xspanid).append('\'');
    sb.append(", xparentspanid='").append(xparentspanid).append('\'');
    sb.append(", xsampled='").append(xsampled).append('\'');
    sb.append(", xflags='").append(xflags).append('\'');
    sb.append(", xotspan='").append(xotspan).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
