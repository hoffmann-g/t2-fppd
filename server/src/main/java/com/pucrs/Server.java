package com.pucrs;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.pucrs.interfaces.IAtmRemote;
import com.pucrs.interfaces.IBranchRemote;

public class Server extends UnicastRemoteObject implements IAtmRemote, IBranchRemote {

    private static final int PORT = 8080;
    private static final double ERROR_RATE = 0.3;
    private static final long MAX_SLEEP = 4000;

    private static ConcurrentHashMap<Long, Map<String, String>> requestLog = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Long, Boolean> runningTasks = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Long, Double> balance = new ConcurrentHashMap<>();

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN = "\u001B[32m";
    private static final String GRAY = "\u001B[37m";

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    protected Server() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        try {
            clearConsole();

            logMessage("Starting server...", RESET);

            IAtmRemote atmStub = new Server();
            IBranchRemote branchStub = new Server();

            Registry registry = LocateRegistry.createRegistry(PORT);

            registry.bind("atm-server", atmStub);
            registry.bind("branch-server", branchStub);

            logMessage("Server started on port: " + PORT, RESET);
            logMessage("Bindings: " + String.join(", ", registry.list()), RESET);

            logMessage("Mocking accounts...", RESET);
            mockAccounts();
            balance.keySet().forEach(id -> logMessage("Account ID: " + id, RESET));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void mockAccounts() {
        balance.put(5000L, 0.);
        balance.put(8080L, 0.);
    }

    @Override
    public synchronized Map<String, String> getAccountInfo(long requestId, long accountId) throws RemoteException {
        try {
            System.out.println("\n#" + requestId + " - get account info request received.");
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

                    final Double balanceAmount = balance.get(accountId);

                    processedRequest = Map.of(
                            "success", "true",
                            "message", "Account info Account ID: #" + accountId + "Amount: " + balanceAmount);

                    requestLog.put(requestId, processedRequest);

                    System.out.println("#" + requestId + " - Create account request not processed!");
                } else {
                    processedRequest = Map.of(
                            "success", "false",
                            "message", "Account #" + accountId + " not exist!");

                    requestLog.put(requestId, processedRequest);

                    System.out.println("#" + requestId + " - get info account request processed!");
                }

                System.out.println("#" + requestId + " - sending response to client...");

                if (Math.random() < ERROR_RATE) {
                    System.out.println("#" + requestId + " - error while sending response.");
                    return null;
                }

                return processedRequest;

            } else {
                System.out.println("#" + requestId + " - get account info request is repeated");
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
    public synchronized Map<String, String> createAccount(long requestId, long accountId) throws RemoteException {
        try {
            System.out.println("\n#" + requestId + " - create account request received.");
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
                            "success", "false",
                            "message", "Account already exists #" + accountId);

                    requestLog.put(requestId, processedRequest);

                    System.out.println("#" + requestId + " - Create account request not processed!");
                } else {
                    balance.put(accountId, 0.0);
                    processedRequest = Map.of(
                            "success", "true",
                            "message", "Account #" + accountId + " created successfully!");

                    requestLog.put(requestId, processedRequest);

                    System.out.println("#" + requestId + " - create account request processed!");
                }

                System.out.println("#" + requestId + " - sending response to client...");

                if (Math.random() < ERROR_RATE) {
                    System.out.println("#" + requestId + " - error while sending response.");
                    return null;
                }

                return processedRequest;

            } else {
                System.out.println("#" + requestId + " - create account request is repeated");
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
    public Map<String, String> deleteAccount(long requestId, long accountId) throws RemoteException {
        try {
            System.out.println("\n#" + requestId + " - close account request received.");
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

                    balance.remove(accountId);

                    processedRequest = Map.of(
                            "success", "true",
                            "message", "Account exists and were removed #" + accountId);

                    requestLog.put(requestId, processedRequest);

                    System.out.println("#" + requestId + " - Create account request not processed!");
                } else {
                    processedRequest = Map.of(
                            "success", "false",
                            "message", "Account #" + accountId + " don't removed!");

                    requestLog.put(requestId, processedRequest);

                    System.out.println("#" + requestId + " - close account request processed!");
                }

                System.out.println("#" + requestId + " - sending response to client...");

                if (Math.random() < ERROR_RATE) {
                    System.out.println("#" + requestId + " - error while sending response.");
                    return null;
                }

                return processedRequest;

            } else {
                System.out.println("#" + requestId + " - close account request is repeated");
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
    public Map<String, String> deposit(long requestId, long accountId, Double amount) throws RemoteException {
        logMessage("\n#" + requestId + " - deposit request received.");

        if (runningTasks.putIfAbsent(requestId, true) != null) {
            logMessage("\n#" + requestId + " - request already being processed.", GRAY);
            return null;
        }

        return executeRequest(() -> {
            try {
                if (!requestLog.containsKey(requestId)) {
                    
                    logMessage("#" + requestId + " - processing request...");
                    Thread.sleep(15000);

                    if (Math.random() < ERROR_RATE) {
                        logMessage("#" + requestId + " - error while processing request!", RED);
                        return null;
                    }

                    Map<String, String> processedRequest;

                    if (balance.containsKey(accountId)) {
                        balance.put(accountId, balance.get(accountId) + amount);

                        processedRequest = Map.of(
                                "success", "true",
                                "message", "Deposited " + amount + " successfully into account #" + accountId);

                        logMessage("#" + requestId + " - deposit request processed successfully!", GREEN);
                    } else {
                        processedRequest = Map.of(
                                "success", "false",
                                "message", "Account #" + accountId + " not found");

                        logMessage("#" + requestId + " - deposit request could not be processed!", RED);
                    }

                    requestLog.put(requestId, processedRequest);
                    runningTasks.remove(requestId);

                    logMessage("#" + requestId + " - sending response to client...");

                    if (Math.random() < ERROR_RATE) {
                        logMessage("#" + requestId + " - error while sending response.", RED);
                        return null;
                    }

                    return processedRequest;

                } else {
                    logMessage("#" + requestId + " - deposit request is repeated", YELLOW);
                    logMessage("#" + requestId + " - sending response to client again...");

                    if (Math.random() < ERROR_RATE) {
                        logMessage("#" + requestId + " - error while sending response.", RED);
                        return null;
                    }

                    return requestLog.get(requestId);
                }
            } catch (InterruptedException e) {
                return null;
            }
        });
    }

    @Override
    public Map<String, String> withdraw(long requestId, long accountId, Double amount) throws RemoteException {
        logMessage("\n#" + requestId + " - withdraw request received.");
        
        if (runningTasks.putIfAbsent(requestId, true) != null) {
            logMessage("\n#" + requestId + " - request already being processed.", GRAY);
            return null;
        }

        return executeRequest(() -> {
            try {
                if (!requestLog.containsKey(requestId)) {

                    logMessage("#" + requestId + " - processing request...");
                    Thread.sleep((long) (Math.random() * MAX_SLEEP));

                    if (Math.random() < ERROR_RATE) {
                        logMessage("#" + requestId + " - error while processing request!", RED);
                        return null;
                    }

                    Map<String, String> processedRequest;

                    if (balance.containsKey(accountId)) {
                        if (balance.get(accountId) >= amount) {
                            balance.put(accountId, balance.get(accountId) - amount);

                            processedRequest = Map.of(
                                    "success", "true",
                                    "message", "Withdrew " + amount + " successfully from account #" + accountId);

                            logMessage("#" + requestId + " - withdraw request processed successfully!", GREEN);
                        } else {
                            processedRequest = Map.of(
                                    "success", "false",
                                    "message", "Insufficient funds in account #" + accountId);

                            logMessage("#" + requestId + " - withdraw request could not be processed!", RED);
                        }
                    } else {
                        processedRequest = Map.of(
                                "success", "false",
                                "message", "Account #" + accountId + " not found");

                        logMessage("#" + requestId + " - withdraw request could not be processed!", RED);
                    }

                    requestLog.put(requestId, processedRequest);
                    runningTasks.remove(requestId);

                    logMessage("#" + requestId + " - sending response to client...");

                    if (Math.random() < ERROR_RATE) {
                        logMessage("#" + requestId + " - error while sending response.", RED);
                        return null;
                    }

                    return processedRequest;

                } else {
                    logMessage("#" + requestId + " - withdraw request is repeated", YELLOW);
                    logMessage("#" + requestId + " - sending response to client again...");

                    if (Math.random() < ERROR_RATE) {
                        logMessage("#" + requestId + " - error while sending response.", RED);
                        return null;
                    }

                    return requestLog.get(requestId);
                }
            } catch (InterruptedException e) {
                return null;
            }
        });
    }

    @Override
    public Map<String, String> getBalance(long requestId, long accountId) throws RemoteException {
        logMessage("\n#" + requestId + " - get_balance request received.");
        
        if (runningTasks.putIfAbsent(requestId, true) != null) {
            logMessage("\n#" + requestId + " - request already being processed.", GRAY);
            return null;
        }

        return executeRequest(() -> {
            try {
                if (!requestLog.containsKey(requestId)) {

                    logMessage("#" + requestId + " - processing request...");
                    Thread.sleep((long) (Math.random() * MAX_SLEEP));

                    if (Math.random() < ERROR_RATE) {
                        logMessage("#" + requestId + " - error while processing request!", RED);
                        return null;
                    }

                    Map<String, String> processedRequest;

                    if (balance.containsKey(accountId)) {
                        processedRequest = Map.of(
                                "success", "true",
                                "message", "Balance for account #" + accountId + ": $" + balance.get(accountId));

                        logMessage("#" + requestId + " - get_balance request processed successfully!", GREEN);
                    } else {
                        processedRequest = Map.of(
                                "success", "false",
                                "message", "Account #" + accountId + " not found");

                        logMessage("#" + requestId + " - get_balance request could not be processed!", RED);
                    }

                    requestLog.put(requestId, processedRequest);
                    runningTasks.remove(requestId);

                    logMessage("#" + requestId + " - sending response to client...");

                    if (Math.random() < ERROR_RATE) {
                        logMessage("#" + requestId + " - error while sending response.", RED);
                        return null;
                    }

                    return processedRequest;

                } else {
                    logMessage("#" + requestId + " - get_balance request is repeated", YELLOW);
                    logMessage("#" + requestId + " - sending response to client again...");

                    if (Math.random() < ERROR_RATE) {
                        logMessage("#" + requestId + " - error while sending response.", RED);
                        return null;
                    }

                    return requestLog.get(requestId);
                }
            } catch (InterruptedException e) {
                return null;
            }
        });
    }

    private static void clearConsole() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            logMessage("Could not clear the console.", RESET);
        }
    }

    private static void logMessage(String message, String color) {
        System.out.println(color + message + RESET);
    }

    private static void logMessage(String message) {
        System.out.println(message);
    }

    private Map<String, String> executeRequest(RequestHandler handler) throws RemoteException {
        try {
            Future<Map<String, String>> future = executorService.submit(handler::handle);
            return future.get();
        } catch (Exception e) {
            throw new RemoteException("Error executing request", e);
        }
    }

    @FunctionalInterface
    private interface RequestHandler {
        Map<String, String> handle() throws RemoteException;
    }
}