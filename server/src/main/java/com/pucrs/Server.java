package com.pucrs;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.pucrs.interfaces.IAtmRemote;
import com.pucrs.interfaces.IBranchRemote;

public class Server extends UnicastRemoteObject implements IAtmRemote, IBranchRemote {

    private static final int PORT = 8080;
    private static final double ERROR_RATE = 0.3;
    private static final long MAX_SLEEP = 4000;

    private static Map<Long, Map<String, String>> requestLog = new ConcurrentHashMap<>();
    private static Map<Long, Double> balance = new ConcurrentHashMap<>();

    protected Server() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        try {
            clearConsole();

            System.out.println("Starting server...");

            IAtmRemote atmStub = new Server();
            IBranchRemote branchStub = new Server();

            Registry registry = LocateRegistry.createRegistry(PORT);

            registry.bind("atm-server", atmStub);
            registry.bind("branch-server", branchStub);

            System.out.println("Server started on port: " + PORT);
            System.out.println("Bindings: " + String.join(", ", registry.list()));

            System.out.println("Mocking accounts... " );
            mockAccounts();
            balance.keySet().forEach(id -> System.out.println("Account ID: " + id));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void mockAccounts(){
        balance.put(5000L, 0.);
        balance.put(8080L, 0.);
    }

    @Override
    public synchronized Map<String, String> getAccountInfo(long requestId, long accountId) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public synchronized Map<String, String> createAccount(long requestId, long accountId) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, String> deleteAccount(long requestId, long accountId) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public synchronized Map<String, String> deposit(long requestId, long accountId, Double amount)
            throws RemoteException {
        try {
            System.out.println("\n#" + requestId + " - deposit request received.");
            if (!requestLog.containsKey(requestId)) {

                System.out.println("#" + requestId + " - processing request...");
                Thread.sleep((long) (Math.random() * MAX_SLEEP));

                // Simulate an error
                if (Math.random() < ERROR_RATE) {
                    System.out.println("#" + requestId + " - error while processing request!");
                    return null;
                }

                Map<String, String> processedRequest;

                if (balance.containsKey(accountId)) {
                    balance.put(accountId, balance.get(accountId) + amount);

                    processedRequest = Map.of(
                            "success", "true",
                            "message", "Deposited " + amount + " successfully into account #" + accountId);

                    requestLog.put(requestId, processedRequest);

                    System.out.println("#" + requestId + " - deposit request proccessed successfully!");
                } else {
                    processedRequest = Map.of(
                            "success", "false",
                            "message", "Account #" + accountId + " not found");

                    requestLog.put(requestId, processedRequest);

                    System.out.println("#" + requestId + " - deposit request could not be proccessed!");
                }

                

                System.out.println("#" + requestId + " - sending response to client...");

                if (Math.random() < ERROR_RATE) {
                    System.out.println("#" + requestId + " - error while sending response.");
                    return null;
                }

                return processedRequest;

            } else {
                System.out.println("#" + requestId + " - deposit request is repeated");
                System.out.println("#" + requestId + " - sending response to client again...");

                if (Math.random() < ERROR_RATE) {
                    System.out.println("#" + requestId + " - error while sending response.");
                    return null;
                }

                return requestLog.get(requestId);
            }
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public synchronized Map<String, String> withdraw(long requestId, long accountId, Double amount)
            throws RemoteException {
        try {
            System.out.println("\n#" + requestId + " - withdraw request received.");
            if (!requestLog.containsKey(requestId)) {

                System.out.println("#" + requestId + " - processing request...");
                Thread.sleep((long) (Math.random() * MAX_SLEEP));

                // Simulate an error
                if (Math.random() < ERROR_RATE) {
                    System.out.println("#" + requestId + " - error while processing request!");
                    return null;
                }

                Map<String, String> processedRequest;

                if (balance.containsKey(accountId)) {
                    if (balance.get(accountId) >= amount) {
                        balance.put(accountId, balance.get(accountId) - amount);

                        processedRequest = Map.of(
                                "success", "true",
                                "message", "Withdrew " + amount + " successfully from account #" + accountId);

                        requestLog.put(requestId, processedRequest);

                    } else {
                        processedRequest = Map.of(
                                "success", "false",
                                "message", "Insufficient funds in account #" + accountId);

                        requestLog.put(requestId, processedRequest);
                    }

                    System.out.println("#" + requestId + " - withdraw request proccessed successfully!");
                } else {
                    processedRequest = Map.of(
                            "success", "false",
                            "message", "Account #" + accountId + " not found");

                    requestLog.put(requestId, processedRequest);

                    System.out.println("#" + requestId + " - withdraw request could not be proccessed!");
                }

                System.out.println("#" + requestId + " - sending response to client...");

                if (Math.random() < ERROR_RATE) {
                    System.out.println("#" + requestId + " - error while sending response.");
                    return null;
                }

                return processedRequest;

            } else {
                System.out.println("#" + requestId + " - withdraw request is repeated");
                System.out.println("#" + requestId + " - sending response to client again...");

                if (Math.random() < ERROR_RATE) {
                    System.out.println("#" + requestId + " - error while sending response.");
                    return null;
                }

                return requestLog.get(requestId);
            }
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public synchronized Map<String, String> getBalance(long requestId, long accountId) throws RemoteException {
        try {
            System.out.println("\n#" + requestId + " - get_balance request received.");
            if (!requestLog.containsKey(requestId)) {

                System.out.println("#" + requestId + " - processing request...");
                Thread.sleep((long) (Math.random() * MAX_SLEEP));

                // Simulate an error
                if (Math.random() < ERROR_RATE) {
                    System.out.println("#" + requestId + " - error while processing request!");
                    return null;
                }

                Map<String, String> processedRequest;

                if (balance.containsKey(accountId)) {
                    processedRequest = Map.of(
                        "success", "true",
                        "message", "Balance for account #" + accountId + ": $" + balance.get(accountId));

                    requestLog.put(requestId, processedRequest);

                    System.out.println("#" + requestId + " - get_balance request proccessed successfully!");
                } else {
                    processedRequest = Map.of(
                            "success", "false",
                            "message", "Account #" + accountId + " not found");

                    requestLog.put(requestId, processedRequest);

                    System.out.println("#" + requestId + " - get_balance request could not be proccessed!");
                }

                System.out.println("#" + requestId + " - sending response to client...");

                if (Math.random() < ERROR_RATE) {
                    System.out.println("#" + requestId + " - error while sending response.");
                    return null;
                }

                return processedRequest;

            } else {
                System.out.println("#" + requestId + " - get_balance request is repeated");
                System.out.println("#" + requestId + " - sending response to client again...");

                if (Math.random() < ERROR_RATE) {
                    System.out.println("#" + requestId + " - error while sending response.");
                    return null;
                }

                return requestLog.get(requestId);
            }
        } catch (InterruptedException e) {
            return null;
        }
    }

    private static void clearConsole() {
        // Clear console for Windows and Unix-based systems
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            System.out.println("Could not clear the console.");
        }
    }

}