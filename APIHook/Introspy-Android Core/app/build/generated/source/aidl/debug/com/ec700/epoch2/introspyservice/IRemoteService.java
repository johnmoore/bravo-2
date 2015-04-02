/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\AndroidstudioProjects\\Introspy-Android Core\\app\\src\\main\\aidl\\com\\ec700\\epoch2\\introspyservice\\IRemoteService.aidl
 */
package com.ec700.epoch2.introspyservice;
// Declare any non-default types here with import statements

public interface IRemoteService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.ec700.epoch2.introspyservice.IRemoteService
{
private static final java.lang.String DESCRIPTOR = "com.ec700.epoch2.introspyservice.IRemoteService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.ec700.epoch2.introspyservice.IRemoteService interface,
 * generating a proxy if needed.
 */
public static com.ec700.epoch2.introspyservice.IRemoteService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.ec700.epoch2.introspyservice.IRemoteService))) {
return ((com.ec700.epoch2.introspyservice.IRemoteService)iin);
}
return new com.ec700.epoch2.introspyservice.IRemoteService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_isAllow:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
boolean _result = this.isAllow(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.ec700.epoch2.introspyservice.IRemoteService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
@Override public boolean isAllow(int type, java.lang.String packageName, java.lang.String dataDir) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
_data.writeString(packageName);
_data.writeString(dataDir);
mRemote.transact(Stub.TRANSACTION_isAllow, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_isAllow = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
/**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
public boolean isAllow(int type, java.lang.String packageName, java.lang.String dataDir) throws android.os.RemoteException;
}
