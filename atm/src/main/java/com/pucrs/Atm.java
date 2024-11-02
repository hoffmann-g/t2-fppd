package com.pucrs;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.pucrs.interfaces.IAtmRemote;

import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Atm {
    private static Scanner scanner = new Scanner(System.in);

    private static IAtmRemote bank;

    private static final int MAX_ATTEMPTS = 5;
    private static final int REQUEST_ATTEMPT_SLEEP = 1500;
    private static final int REQUEST_TIMEOUT = 3000;

    public static void main(String[] args) throws InterruptedException {
        while (true) {
            int counter = 5;
            try {
                clearConsole();

                System.out.println("Connecting to server...");

                Registry registry = LocateRegistry.getRegistry("localhost", 8080);

                bank = (IAtmRemote) registry.lookup("atm-server");

                // throw new Exception("Test exception");

                while (true)
                    showMainMenu();

            } catch (NotBoundException e) {
                while (counter > 0) {
                    clearConsole();
                    System.out.println("Error connecting to server: " + e.getMessage());
                    System.out.println("Trying to reconnect in " + counter + " seconds...");
                    Thread.sleep(1000);
                    counter--;
                }
            } catch (Exception e) {
                clearConsole();
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Returning to main menu...");
                Thread.sleep(10000);
            }
        }

    }

    private static void showMainMenu() throws InterruptedException {
        clearConsole();
        System.out.println("Welcome to ATM-CLIENT, press:");
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.println("3. Get Balance");

        int choice = getUserChoice(1, 3);

        switch (choice) {
            case 1:
                handleDeposit();
                break;
            case 2:
                handleWithdraw();
                break;
            case 3:
                handleGetBalance();
                break;
        }
    }

    private static void handleDeposit() throws InterruptedException {
        clearConsole();

        long requestId = java.util.UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;

        System.out.print("Enter Account ID: ");
        long accountId = scanner.nextLong();

        System.out.print("Enter Amount to Deposit: ");
        double amount = getPositiveDouble();

        boolean success = false;
        int attempts = 0;
        while (attempts < MAX_ATTEMPTS) {

            clearConsole();

            try {
                System.out.println("Sending request to server...");
                Map<String, String> res = executeWithTimeout(() -> bank.deposit(requestId, accountId, amount),
                        REQUEST_TIMEOUT);

                if (res == null) {
                    clearConsole();
                    throw new Exception("No response from server.");
                }

                success = true;
                handleResponse(res);
                break;

            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Retrying... (" + (attempts + 1) + "/" + MAX_ATTEMPTS + ")");
                Thread.sleep(REQUEST_ATTEMPT_SLEEP);
                attempts++;
            }
        }

        if (!success) {
            System.out.println("No response from server.");
        }

        if (askToContinue()) {
            handleDeposit();
        }

    }

    private static void handleWithdraw() throws InterruptedException {
        clearConsole();

        long requestId = java.util.UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;

        System.out.print("Enter Account ID: ");
        long accountId = scanner.nextLong();

        System.out.print("Enter Amount to Withdraw: ");
        double amount = getPositiveDouble();

        boolean success = false;
        int attempts = 0;
        while (attempts < MAX_ATTEMPTS) {

            clearConsole();

            try {
                System.out.println("Sending request to server...");
                Map<String, String> res = executeWithTimeout(() -> bank.withdraw(requestId, accountId, amount),
                        REQUEST_TIMEOUT);

                if (res == null) {
                    clearConsole();
                    throw new Exception("No response from server.");
                }

                success = true;
                handleResponse(res);
                break;

            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Retrying... (" + (attempts + 1) + "/" + MAX_ATTEMPTS + ")");
                Thread.sleep(REQUEST_ATTEMPT_SLEEP);
                attempts++;
            }
        }

        if (!success) {
            System.out.println("No response from server.");
        }

        if (askToContinue()) {
            handleDeposit();
        }
    }

    private static void handleGetBalance() throws InterruptedException {
        clearConsole();

        long requestId = java.util.UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;

        System.out.print("Enter Account ID: ");
        long accountId = scanner.nextLong();

        boolean success = false;
        int attempts = 0;
        while (attempts < MAX_ATTEMPTS) {

            clearConsole();

            try {
                System.out.println("Sending request to server...");
                Map<String, String> res = executeWithTimeout(() -> bank.getBalance(requestId, accountId),
                        REQUEST_TIMEOUT);

                if (res == null) {
                    clearConsole();
                    throw new Exception("No response from server.");
                }

                success = true;
                handleResponse(res);
                break;

            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Retrying... (" + (attempts + 1) + "/" + MAX_ATTEMPTS + ")");
                Thread.sleep(REQUEST_ATTEMPT_SLEEP);
                attempts++;
            }
        }

        if (!success) {
            System.out.println("No response from server.");
        }

        if (askToContinue()) {
            handleDeposit();
        }

    }

    private static void handleResponse(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (key.toLowerCase().equals("message")) {
                System.out.println(value);
                return;
            }

        }

        System.out.println();
    }

    private static boolean askToContinue() {
        System.out.println("Do you want to keep performing this operation on this account?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        int choice = getUserChoice(1, 2);
        return choice == 1;
    }

    private static int getUserChoice(int min, int max) {
        int choice = -1;
        do {
            System.out.print("Enter your choice (" + min + "-" + max + "): ");
            while (true) {
                if (!scanner.hasNextInt()) {
                    System.out.print("That's not a valid option! Enter again: ");
                    scanner.next();
                } else {
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    if (choice >= min && choice <= max) {
                        break;
                    } else {
                        System.out.print("That's not a valid option! Enter again: ");
                    }
                }
            }
        } while (choice < min || choice > max);
        return choice;
    }

    private static double getPositiveDouble() {
        double amount;
        do {
            while (!scanner.hasNextDouble()) {
                System.out.print("That's not a valid amount! Enter again: ");
                scanner.next();
            }
            amount = scanner.nextDouble();
            scanner.nextLine();
        } while (amount < 0);
        return amount;
    }

    private static <T> T executeWithTimeout(Callable<T> callable, int timeout) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<T> future = executor.submit(callable);
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            clearConsole();
            throw new Exception("Operation timed out");
        } finally {
            executor.shutdown();
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
