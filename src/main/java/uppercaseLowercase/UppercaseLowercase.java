package uppercaseLowercase;

import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class UppercaseLowercase {
    private static Scanner sc = new Scanner(System.in);
    private static Connection mysqlConnection;
    private static Connection postgresConnection;
    private static ArrayList<String> lowercaseWords = new ArrayList<>();
    private static ArrayList<String> uppercaseWords = new ArrayList<>();
    private static int choirs = 0;

    public static void main(String[] args) {
        System.out.println("please enter the sentence");
        String sentence = sc.nextLine();
        splitWords(sentence);
        createDatabases();
        insertWordsIntoMySQL();
        insertWordsIntoPostgreSQL();
        while (true) {
            printMenu();
            try {
                choirs = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException e) {
                sc.nextLine();
                continue;
            }
            switch (choirs) {
                case 1:
                    displayInfoFromMySQL();
                    break;
                case 2:
                    displayInfoFromPostgreSQL();
                    break;
                case 3:
                    System.out.println("Exiting the system.");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void displayInfoFromMySQL() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql-container", "mysql:latest", "mysql");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM lowercase");

            System.out.println("Information from MySQL database:");
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                System.out.print( name + " ");
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void displayInfoFromPostgreSQL() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM uppercase");

            System.out.println("Information from PostgreSQL database:");
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                System.out.print( name + " ");
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void splitWords(String sentence) {
        String[] words = sentence.split("\\s+");
        for (String word : words) {
            if (Character.isLowerCase(word.charAt(0))) {
                lowercaseWords.add(word);
            } else if (Character.isUpperCase(word.charAt(0))) {
                uppercaseWords.add(word);
            }
        }
    }

    public static void createDatabases() {
        try {
            mysqlConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql-container", "mysql:latest", "mysql");
            postgresConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");

            mysqlConnection.setAutoCommit(false);
            Statement mysqlStatement = mysqlConnection.createStatement();

            postgresConnection.setAutoCommit(false);
            Statement postgresStatement = postgresConnection.createStatement();

            try {
                mysqlStatement.executeUpdate("CREATE TABLE IF NOT EXISTS lowercase (id INT PRIMARY KEY, name VARCHAR(255))");
                postgresStatement.executeUpdate("CREATE TABLE IF NOT EXISTS uppercase (id SERIAL PRIMARY KEY, name VARCHAR(255))");

                mysqlConnection.commit();
                postgresConnection.commit();

                System.out.println("Tables created successfully.");
            } catch (SQLException e) {
                mysqlConnection.rollback();
                postgresConnection.rollback();
                e.printStackTrace();
            } finally {
                mysqlStatement.close();
                postgresStatement.close();

                mysqlConnection.close();
                postgresConnection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void insertWordsIntoMySQL() {
        try {
            mysqlConnection.setAutoCommit(false);
            PreparedStatement preparedStatement = mysqlConnection.prepareStatement("INSERT INTO lowercase(word) VALUES (?)");
            for (String word : lowercaseWords) {
                preparedStatement.setString(1, word);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            mysqlConnection.commit();
        } catch (SQLException e) {
            try {
                mysqlConnection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static void insertWordsIntoPostgreSQL() {
        try {
            postgresConnection.setAutoCommit(false);
            PreparedStatement preparedStatement = postgresConnection.prepareStatement("INSERT INTO uppercase(word) VALUES (?)");
            for (String word : lowercaseWords) {
                preparedStatement.setString(1, word);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            postgresConnection.commit();
        } catch (SQLException e) {
            try {
                postgresConnection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }



private static void printMenu() {
    StringBuilder sb = new StringBuilder();
    sb.append("\n1. Display information from the lowercase MySQL database.\n" +
              "2. Display information from the uppercase PostgreSQL database.\n" +
              "3. Exiting the system. ");
    System.out.println(sb);
}

}