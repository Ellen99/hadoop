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
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestNfsExports {

  private final static String ADDRESS1 = "192.168.0.12";
  private final static String ADDRESS2 = "10.0.0.12";
  private final static String HOSTNAME1 = "a.b.com";
  private final static String HOSTNAME2 = "a.b.org";
  private static boolean IsExceptionThrown;
  private final AccessPrivilege expectedAccessPrivilege;
  private final AccessPrivilege actualAccessPrivilege;
  private final AccessPrivilege actualAccessPrivilege_CacheIsHit;

  private final static long ExpirationPeriodConst = Nfs3Constant.NFS_EXPORTS_CACHE_EXPIRYTIME_MILLIS_DEFAULT * 1000 * 1000;

  private final static int CacheSizeConst = Nfs3Constant.NFS_EXPORTS_CACHE_SIZE_DEFAULT;

  public TestNfsExports(int CacheSize,
                                     long ExpirationPeriod,
                                     String matchHosts,
                                     AccessPrivilege expectedAccessPrivilege,
                                     String addressInitial,
                                     String hostnameInitial,
                                     String addressHitCache,
                                     String hostnameHitCache
  ){
    this.expectedAccessPrivilege = expectedAccessPrivilege;
    IsExceptionThrown = false;
    if(expectedAccessPrivilege==null){
      this.actualAccessPrivilege = null;
      this.actualAccessPrivilege_CacheIsHit = null;
      try {
        new NfsExports(CacheSize, ExpirationPeriod, matchHosts);
        //exception should be thrown
      } catch (IllegalArgumentException e) {
        IsExceptionThrown = true;
      }
    } else{
      NfsExports matcher = new NfsExports(CacheSize, ExpirationPeriod, matchHosts);
      this.actualAccessPrivilege =(addressInitial == null || hostnameInitial == null) ?
              null : matcher.getAccessPrivilege(addressInitial, hostnameInitial);

      this.actualAccessPrivilege_CacheIsHit = (addressHitCache==null || hostnameHitCache==null) ?
              null : matcher.getAccessPrivilege(addressHitCache, hostnameHitCache);
    }
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
            //testWildcardRW
            {CacheSizeConst, ExpirationPeriodConst, "* rw", AccessPrivilege.READ_WRITE, ADDRESS1, HOSTNAME1, null, null},
            //testWildcardRO
            {CacheSizeConst, ExpirationPeriodConst, "* ro", AccessPrivilege.READ_ONLY, ADDRESS1, HOSTNAME1, null, null},
            //testExactAddressRW
            {CacheSizeConst, ExpirationPeriodConst, ADDRESS1 + " rw", AccessPrivilege.READ_WRITE, ADDRESS1, HOSTNAME1, null, null},
            {CacheSizeConst, ExpirationPeriodConst, ADDRESS1 + " rw", AccessPrivilege.NONE, ADDRESS2, HOSTNAME1, null, null},
            //testExactAddressRO
            {CacheSizeConst, ExpirationPeriodConst, ADDRESS1, AccessPrivilege.READ_ONLY, ADDRESS1, HOSTNAME1, null, null},
            {CacheSizeConst, ExpirationPeriodConst, ADDRESS1, AccessPrivilege.NONE, ADDRESS2, HOSTNAME1, null, null},
            //testExactHostRW
            {CacheSizeConst, ExpirationPeriodConst, HOSTNAME1 + " rw", AccessPrivilege.READ_WRITE, ADDRESS1, HOSTNAME1, null, null},
            //testExactHostRO
            {CacheSizeConst, ExpirationPeriodConst, HOSTNAME1, AccessPrivilege.READ_ONLY, ADDRESS1, HOSTNAME1, null, null},
            //testCidrShortRW
            {CacheSizeConst, ExpirationPeriodConst, "192.168.0.0/22 rw", AccessPrivilege.READ_WRITE, ADDRESS1, HOSTNAME1, null, null},
            {CacheSizeConst, ExpirationPeriodConst, "192.168.0.0/22 rw", AccessPrivilege.NONE, ADDRESS2, HOSTNAME1, null, null},
            //testCidrShortRO
            {CacheSizeConst, ExpirationPeriodConst, "192.168.0.0/22", AccessPrivilege.READ_ONLY, ADDRESS1, HOSTNAME1, null, null},
            {CacheSizeConst, ExpirationPeriodConst, "192.168.0.0/22", AccessPrivilege.NONE, ADDRESS2, HOSTNAME1, null, null},
            //testCidrLongRW
            {CacheSizeConst, ExpirationPeriodConst, "192.168.0.0/255.255.252.0 rw", AccessPrivilege.READ_WRITE, ADDRESS1, HOSTNAME1, null, null},
            {CacheSizeConst, ExpirationPeriodConst, "192.168.0.0/255.255.252.0 rw", AccessPrivilege.NONE, ADDRESS2, HOSTNAME1, null, null},
            //testCidrLongRO
            {CacheSizeConst, ExpirationPeriodConst, "192.168.0.0/255.255.252.0", AccessPrivilege.READ_ONLY, ADDRESS1, HOSTNAME1, null, null},
            {CacheSizeConst, ExpirationPeriodConst, "192.168.0.0/255.255.252.0", AccessPrivilege.NONE, ADDRESS2, HOSTNAME1, null, null},
            //testRegexIPRW
            {CacheSizeConst, ExpirationPeriodConst, "192.168.0.[0-9]+ rw", AccessPrivilege.READ_WRITE, ADDRESS1, HOSTNAME1, null, null},
            {CacheSizeConst, ExpirationPeriodConst, "192.168.0.[0-9]+ rw", AccessPrivilege.NONE, ADDRESS2, HOSTNAME1, null, null},
            //testRegexIPRO
            {CacheSizeConst, ExpirationPeriodConst, "192.168.0.[0-9]+", AccessPrivilege.READ_ONLY, ADDRESS1, HOSTNAME1, null, null},
            {CacheSizeConst, ExpirationPeriodConst, "192.168.0.[0-9]+", AccessPrivilege.NONE, ADDRESS2, HOSTNAME1, null, null},
            //testRegexHostRW Hit the cache
            {CacheSizeConst, ExpirationPeriodConst, "[a-z]+.b.com rw", AccessPrivilege.READ_WRITE, ADDRESS1, HOSTNAME1, ADDRESS1, HOSTNAME2},
            //testRegexHostRO Hit the cache
            {CacheSizeConst, ExpirationPeriodConst, "[a-z]+.b.com", AccessPrivilege.READ_ONLY, ADDRESS1, HOSTNAME1, ADDRESS1, HOSTNAME2},
            //testRegexGrouping Hit the cache
            {CacheSizeConst, ExpirationPeriodConst, "192.168.0.(12|34)", AccessPrivilege.READ_ONLY, ADDRESS1, HOSTNAME1, ADDRESS1, HOSTNAME2},
            {CacheSizeConst, ExpirationPeriodConst, "\\w*.a.b.com", AccessPrivilege.READ_ONLY, "1.2.3.4", "web.a.b.com", "1.2.3.4", "email.a.b.org"},
            //Invalid cases
            {CacheSizeConst, ExpirationPeriodConst, "foo#bar", null, null, null, null, null},
            {CacheSizeConst, ExpirationPeriodConst, "foo ro : bar rw", null, null, null, null, null}
    });
  }
  @Test
  public void testNfsAll() {
    Assume.assumeNotNull(expectedAccessPrivilege);
    Assume.assumeTrue(actualAccessPrivilege_CacheIsHit==null);
    Assert.assertEquals(expectedAccessPrivilege, actualAccessPrivilege);
  }
  @Test
  public void testNfsHitCache() {
    Assume.assumeNotNull(actualAccessPrivilege_CacheIsHit);
    Assert.assertEquals(expectedAccessPrivilege,actualAccessPrivilege);
    // initial address will hit the cache
    Assert.assertEquals(expectedAccessPrivilege, actualAccessPrivilege_CacheIsHit);
  }
  @Test
  public void testInvalidCase() {
    Assume.assumeTrue(expectedAccessPrivilege==null);
    Assert.assertTrue(IsExceptionThrown);
  }
}
