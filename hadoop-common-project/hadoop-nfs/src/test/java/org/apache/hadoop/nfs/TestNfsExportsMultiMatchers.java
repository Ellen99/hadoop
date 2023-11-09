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

import org.apache.hadoop.nfs.nfs3.Nfs3Constant;
import org.junit.Assert;
import org.junit.Test;

public class TestNfsExportsMultiMatchers {
  private static final int CacheSize = Nfs3Constant.NFS_EXPORTS_CACHE_SIZE_DEFAULT;
  private static final long NanosPerMillis = 1000000;
  @Test
  public void testMultiMatchers() throws Exception {
    String hostname2 = "a.b.org";
    String address1 = "192.168.0.12";
    String hostname1 = "a.b.com";
    String address2 = "10.0.0.12";
    long shortExpirationPeriod = 1 * 1000 * 1000 * 1000; // 1s

    NfsExports matcher = new NfsExports(CacheSize, shortExpirationPeriod,
            "192.168.0.[0-9]+;[a-z]+.b.com rw");
    Assert.assertEquals(AccessPrivilege.READ_ONLY,
            matcher.getAccessPrivilege(address1, hostname2));
    Assert.assertEquals(AccessPrivilege.READ_ONLY,
            matcher.getAccessPrivilege(address1, address1));
    Assert.assertEquals(AccessPrivilege.READ_ONLY,
            matcher.getAccessPrivilege(address1, hostname1));
    Assert.assertEquals(AccessPrivilege.READ_WRITE,
            matcher.getAccessPrivilege(address2, hostname1));
    // address2 will hit the cache
    Assert.assertEquals(AccessPrivilege.READ_WRITE,
            matcher.getAccessPrivilege(address2, hostname2));

    Thread.sleep(1000);
    // no cache for address2 now
    AccessPrivilege ap;
    long startNanos = System.nanoTime();
    do {
      ap = matcher.getAccessPrivilege(address2, address2);
      if (ap == AccessPrivilege.NONE) {
        break;
      }
      Thread.sleep(500);
    } while ((System.nanoTime() - startNanos) / NanosPerMillis < 5000);
    Assert.assertEquals(AccessPrivilege.NONE, ap);
  }
}
