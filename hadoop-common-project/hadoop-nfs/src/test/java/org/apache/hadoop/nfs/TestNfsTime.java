/**
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
package org.apache.hadoop.nfs;

import org.apache.hadoop.oncrpc.XDR;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestNfsTime {
  private final NfsTime nfstime;
  private final int expectedSeconds;
  private final int expectedNanoSeconds;
  public TestNfsTime(int milliseconds, int expectedSeconds, int expectedNanoSeconds){
    this.nfstime = new NfsTime(milliseconds);
    this.expectedSeconds = expectedSeconds;
    this.expectedNanoSeconds = expectedNanoSeconds;
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
            {1001, 1, 1000000},
            {3002, 3, 2000000},
            {2001, 2, 1000000}
    });
  }
  @Test
  public void testConstructor() {
    Assert.assertEquals(expectedSeconds, nfstime.getSeconds());
    Assert.assertEquals(expectedNanoSeconds, nfstime.getNseconds());
  }

  @Test
  public void testSerializeDeserialize() {
    // Serialize NfsTime
    XDR xdr = new XDR();
    nfstime.serialize(xdr);

    // Deserialize it back
    NfsTime t2 = NfsTime.deserialize(xdr.asReadOnlyWrap());

    // Ensure the NfsTimes are equal
    Assert.assertEquals(nfstime, t2);
  }
}
