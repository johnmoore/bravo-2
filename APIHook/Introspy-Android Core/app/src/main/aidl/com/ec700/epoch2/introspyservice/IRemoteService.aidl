// IRemoteService.aidl
package com.ec700.epoch2.introspyservice;

// Declare my remoteservice with aidl file

interface IRemoteService {
    /**
     * send type, packageName, dataDir out
     * receive the server's result
     */
    boolean isAllow(int type, String packageName, String dataDir);
}
