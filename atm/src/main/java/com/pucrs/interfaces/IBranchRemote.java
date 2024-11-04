package com.pucrs.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface IBranchRemote extends Remote {

    Map<String, String> getAccountInfo(String accountNumber) throws RemoteException;

    Map<String, String> createAccount(String accountNumber, String password) throws RemoteException;

    Map<String, String> deleteAccount(String accountNumber, String password) throws RemoteException;

}
