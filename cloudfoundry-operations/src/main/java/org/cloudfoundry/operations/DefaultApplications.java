/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.operations;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.SpaceApplicationSummary;
import org.reactivestreams.Publisher;
import reactor.Mono;
import reactor.fn.Function;
import reactor.rx.Stream;

final class DefaultApplications implements Applications {

    private final CloudFoundryClient cloudFoundryClient;

    private final Mono<String> spaceId;

    DefaultApplications(CloudFoundryClient cloudFoundryClient, Mono<String> spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.spaceId = spaceId;
    }

    @Override
    public Publisher<Application> list() {
        return this.spaceId
                .then(requestSpaceSummary(this.cloudFoundryClient))
                .flatMap(extractApplications())
                .map(toApplication());
    }

    private static Function<GetSpaceSummaryResponse, Stream<SpaceApplicationSummary>> extractApplications() {
        return new Function<GetSpaceSummaryResponse, Stream<SpaceApplicationSummary>>() {

            @Override
            public Stream<SpaceApplicationSummary> apply(GetSpaceSummaryResponse getSpaceSummaryResponse) {
                return Stream.fromIterable(getSpaceSummaryResponse.getApplications());
            }

        };
    }

    private static Function<String, Mono<GetSpaceSummaryResponse>> requestSpaceSummary(final CloudFoundryClient cloudFoundryClient) {
        return new Function<String, Mono<GetSpaceSummaryResponse>>() {

            @Override
            public Mono<GetSpaceSummaryResponse> apply(String targetedSpace) {
                GetSpaceSummaryRequest request = GetSpaceSummaryRequest.builder()
                        .id(targetedSpace)
                        .build();

                return cloudFoundryClient.spaces().getSummary(request);
            }

        };
    }

    private static Function<SpaceApplicationSummary, Application> toApplication() {
        return new Function<SpaceApplicationSummary, Application>() {

            @Override
            public Application apply(SpaceApplicationSummary applicationSummary) {
                return Application.builder()
                        .diskQuota(applicationSummary.getDiskQuota())
                        .id(applicationSummary.getId())
                        .instances(applicationSummary.getInstances())
                        .memoryLimit(applicationSummary.getMemory())
                        .name(applicationSummary.getName())
                        .requestedState(applicationSummary.getState())
                        .runningInstances(applicationSummary.getRunningInstances())
                        .urls(applicationSummary.getUrls())
                        .build();
            }

        };
    }

}