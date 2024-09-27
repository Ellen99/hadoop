/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.registry.integration;

import org.apache.hadoop.registry.RegistryTestHelper;
import org.apache.hadoop.registry.client.types.yarn.PersistencePolicies;
import org.apache.hadoop.registry.client.types.RegistryPathStatus;
import org.apache.hadoop.registry.client.types.ServiceRecord;
import org.apache.hadoop.registry.server.integration.SelectByYarnPersistence;
import org.apache.hadoop.registry.server.services.RegistryAdminService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestYarnPolicySelector extends RegistryTestHelper {


  private ServiceRecord record = createRecord("1",
      PersistencePolicies.APPLICATION, "one",
      null);
  private RegistryPathStatus status = new RegistryPathStatus("/", 0, 0, 1);

  private final boolean outcome;
  private final String id;
  private final String policy;

  public TestYarnPolicySelector(boolean outcome, String id, String policy){
    this.outcome = outcome;
    this.id = id;
    this.policy = policy;
  }
  public void assertSelected(boolean outcome,
      RegistryAdminService.NodeSelector selector) {
    boolean select = selector.shouldSelect("/", status, record);
    assertEquals(selector.toString(), outcome, select);
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
            {false, "1", PersistencePolicies.CONTAINER},
            {true, "1", PersistencePolicies.APPLICATION},
            {false, "2", PersistencePolicies.APPLICATION}
    });
  }

  @Test
  public void testByAll() throws Throwable {
    assertSelected(outcome,
            new SelectByYarnPersistence(id,policy));
  }
}