package com.badcode;

import java.sql.*;
import java.util.Scanner;
import java.security.MessageDigest;

public class Main {
    private static final String DATABASE_URL = "jdbc:postgresql://localhost/badcode";
    private static final String DATABASE_USERNAME = "postgres";
    private static final String DATABASE_PASSWORD = "postgres";

    private static final String HASH_ALGORITHM = "MD5";

    private static final int LOGIN = 1;
    private static final int REGISTER = 2;
    private static final int EXIT = 3;

    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the Faulty Code!");
        System.out.println("Try to find some bugs/bad practices in the code and fix them!");

        Connection connection = ConnectToDatabase();
        if (connection == null) {
            System.out.println("Failed to connect to the database!");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("1 - Login");
        System.out.println("2 - Register");
        System.out.println("3 - Exit");

        int choice = 0;
        while (true)
            try {
                choice = Integer.parseInt(scanner.nextLine());
                break;
            } catch (NumberFormatException nfe) {
                System.out.print("Option not available, choose a number from the menu: ");
            }

        switch (choice) {
            case LOGIN:
                Login(connection);
                break;
            case REGISTER:
                Register(connection);
                break;
            case EXIT:
                System.out.println("Goodbye!");
                scanner.close();
                return;
            default:
                System.out.println("Invalid choice!");
                break;
        }

        scanner.close();
    }

    /**
     * @brief Logs the user in
     * 
     * @param connection The connection to the database
     * @param scanner    The scanner to read user input
     * 
     * @return True if the user logged in successfully, false otherwise
     */
    public static boolean Login(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();

        System.out.println("Enter your password: ");
        String password = scanner.nextLine();

        scanner.close();

        String hashed_password = HashPassword(password);
        if (hashed_password == null) {
            System.out.println("Login failed!");
            return false;
        }

        String query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + hashed_password
                + "';";

        try {
            Statement statement = connection.createStatement();
            ResultSet result_set = statement.executeQuery(query);

            boolean user_exists = result_set.next();
            if (user_exists) {
                System.out.println("Login successful!");
                return true;
            } else {
                System.out.println("Login failed!");
                return false;
            }
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            return false;
        }
    }

    /**
     * @brief Registers a new user
     * 
     * @param connection The connection to the database
     * @param scanner    The scanner to read user input
     * 
     * @return True if the user was registered successfully, false otherwise
     */
    public static boolean Register(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();

        System.out.println("Enter your password: ");
        String password = scanner.nextLine();

        scanner.close();
        String hashed_password = HashPassword(password);
        if (hashed_password == null) {
            System.out.println("Registration failed!");
            return false;
        }

        boolean username_exists = CheckIfUsernameExists(connection, username);
        if (username_exists) {
            return false;
        }

        String insert_user_query = "INSERT INTO users (username, password) VALUES ('" + username + "', '"
                + hashed_password
                + "')";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(insert_user_query);
            System.out.println("User registered successfully!");
            return true;
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            return false;
        }
    }

    /**
     * @brief Checks if a username already exists in the database
     * 
     * @param connection The connection to the database
     * @param username   The username to check
     * 
     * @return True if the username exists, false otherwise
     */
    public static boolean CheckIfUsernameExists(Connection connection, String username) {
        String select_username_query = "SELECT * FROM users WHERE username = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(select_username_query);
            statement.setString(1, username);
            ResultSet result_set = statement.executeQuery();

            boolean user_exists = result_set.next();

            if (user_exists) {
                System.out.println("Username already exists!");
                return true;
            } else {
                return false;
            }
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            return false;
        }
    }

    /**
     * @brief Hashes a password using the MD5 algorithm
     * 
     * @param password The password to hash
     * 
     * @return The hashed password if successful, null otherwise
     */
    public static String HashPassword(String password) {

        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuffer hashed_password = new StringBuffer();

            for (int byte_index = 0; byte_index < hash.length; byte_index++) {
                String hex = Integer.toHexString(0xff & hash[byte_index]);
                if (hex.length() == 1)
                    hashed_password.append('0');
                hashed_password.append(hex);
            }

            return hashed_password.toString();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    /**
     * @brief Connects to the database
     * 
     * @return Connection object if successful, null otherwise
     */
    public static Connection ConnectToDatabase() {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
            Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
            return connection;
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }
}