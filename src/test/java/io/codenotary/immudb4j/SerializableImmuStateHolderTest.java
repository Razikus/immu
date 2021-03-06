/*
Copyright 2022 CodeNotary, Inc. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package io.codenotary.immudb4j;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SerializableImmuStateHolderTest {

    @Test(testName = "in-memory state holder")
    public void t1() {

        SerializableImmuStateHolder stateHolder = new SerializableImmuStateHolder();

        ImmuClient immuClient = ImmuClient.newBuilder()
                .withStateHolder(stateHolder)
                .withServerUrl("localhost")
                .withServerPort(3322)
                .build();

        immuClient.login("immudb", "immudb");
        immuClient.useDatabase("defaultdb");

        ImmuState state = immuClient.state();

        Assert.assertNotNull(state);
        // System.out.println(">>> t1 > state: " + state.toString());

        String stateStr = state.toString();
        Assert.assertTrue(stateStr.contains("ImmuState{"));
        Assert.assertTrue(stateStr.contains("txHash(base64)"));
        Assert.assertTrue(stateStr.contains("signature(base64)"));

        immuClient.logout();
    }

}
