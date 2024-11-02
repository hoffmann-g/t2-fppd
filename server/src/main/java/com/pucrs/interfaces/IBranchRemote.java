package com.pucrs.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface IBranchRemote extends Remote {

    Map<String, String> createAccount(long requestId, long accountId) throws RemoteException;

    Map<String, String> deleteAccount(long requestId, long accountId) throws RemoteException;

    Map<String, String> getAccountInfo(long requestId, long accountId) throws RemoteException;

}
