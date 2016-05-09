/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.azurecompute.arm.config;
import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.handlers.AzureComputeErrorHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.implicit.OnlyLocationOrFirstRegionOptionallyMatchingRegionId;

import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.providers.ProviderMetadata;

import com.google.inject.Scopes;
import com.google.inject.Provides;
import javax.inject.Named;

@ConfiguresHttpApi
public class AzureComputeHttpApiModule extends HttpApiModule<AzureComputeApi> {

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(AzureComputeErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(AzureComputeErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(AzureComputeErrorHandler.class);
   }

   @Override
   protected void installLocations() {
      super.installLocations();
      bind(ImplicitLocationSupplier.class).
              to(OnlyLocationOrFirstRegionOptionallyMatchingRegionId.class).
              in(Scopes.SINGLETON);
   }

   @Override
   protected void configure() {
      install(new AzureComputeParserModule());
      super.configure();
      bind(OAuthScopes.class).toInstance(OAuthScopes.NoScopes.create());
   }
   @Provides
   @Named("subscriptionId")
   protected String provideSubscriptionId(ProviderMetadata providerMetadata) {
      String subscriptionid = null;
      String endpoint = null;
      endpoint = providerMetadata.getEndpoint();
      if (endpoint != null) {
         subscriptionid = endpoint.substring(endpoint.lastIndexOf("/") + 1);
      }
      return subscriptionid;
   }
}
