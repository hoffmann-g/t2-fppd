package com.pucrs.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface IAtmRemote extends Remote {

    Map<String, String> deposit(long requestId, long accountId, Double amount) throws RemoteException;

    Map<String, String> withdraw(long requestId, long accountId, Double amount) throws RemoteException;

    Map<String, String> getBalance(long requestId, long accountId) throws RemoteException;

}
