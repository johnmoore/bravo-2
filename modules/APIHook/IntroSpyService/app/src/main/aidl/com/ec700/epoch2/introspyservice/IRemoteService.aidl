// IRemoteService.aidl
package com.ec700.epoch2.introspyservice;

// Declare any non-default types here with import statements

interface IRemoteService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    boolean isAllow(int type, String packageName, String dataDir);
}
