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

import io.codenotary.immudb4j.exceptions.CorruptedDataException;
import io.codenotary.immudb4j.exceptions.VerificationException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class MultidatabaseTest extends ImmuClientIntegrationTest {

    @Test(testName = "Interacting with multiple databases (creating them, setting, and getting, listing)")
    public void t1() throws VerificationException {

        immuClient.login("immudb", "immudb");

        immuClient.createDatabase("db1");
        immuClient.createDatabase("db2");

        immuClient.useDatabase("db1");
        byte[] v0 = new byte[]{0, 1, 2, 3};
        try {
            immuClient.set("k0", v0);
        } catch (CorruptedDataException e) {
            Assert.fail("Failed at set.", e);
        }

        immuClient.useDatabase("db2");

        byte[] v1 = new byte[]{3, 2, 1, 0};
        try {
            immuClient.set("k1", v1);
        } catch (CorruptedDataException e) {
            Assert.fail("Failed at set.", e);
        }

        immuClient.useDatabase("db1");

        byte[] gv0 = null;
        try {
            gv0 = immuClient.get("k0");
        } catch (Exception e) {
            Assert.fail("Failed at get.", e);
        }
        Assert.assertEquals(v0, gv0);

        Entry ev0 = immuClient.verifiedGet("k0");
        Assert.assertNotNull(ev0);
        Assert.assertEquals(ev0.kv.getValue(), v0);

        immuClient.useDatabase("db2");

        byte[] gv1 = null;
        try {
            gv1 = immuClient.get("k1");
        } catch (Exception e) {
            Assert.fail("Failed at get.", e);
        }
        Assert.assertEquals(v1, gv1);

        Entry evgv1 = immuClient.verifiedGet("k1");
        Assert.assertEquals(evgv1.kv.getValue(), v1);

        List<String> dbs = immuClient.databases();
        Assert.assertNotNull(dbs);
        Assert.assertEquals(3, dbs.size(), String.format("Expected 3, but got %d dbs: %s", dbs.size(), dbs));
        Assert.assertTrue(dbs.contains("defaultdb"));
        Assert.assertTrue(dbs.contains("db1"));
        Assert.assertTrue(dbs.contains("db2"));

        immuClient.logout();
    }

}
