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

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.operations.domains.CreateDomainRequest;
import org.junit.Test;

public final class DomainsTest extends AbstractIntegrationTest {

    @Test
    public void create() {
        this.cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain("test.domain")
                .organization(organizationName)
                .build())
            .subscribe(testSubscriber());
    }

    @Test
    public void createInvalidDomain() {
        this.cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain("invalid-domain")
                .organization(this.organizationName)
                .build())
            .after()
            .subscribe(testSubscriber()
                .assertError(CloudFoundryException.class));
    }

}
