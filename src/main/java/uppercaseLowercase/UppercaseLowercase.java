package uppercaseLowercase;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class UppercaseLowercase {
    private static Scanner sc = new Scanner(System.in);
    private static final Connection CONNECTION = DatabaseConnection.getInstance().getConnection();
    private static int choirs = 0;

    public static void main(String[] args) {
        System.out.println("pleas enter the sentence");
        String sentence = sc.nextLine();
        uppercaseLowercase(sentence);
        while (true) {
            printManu();
            try {
                choirs = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException e) {
                sc.nextLine();
                continue;
            }
            switch (choirs) {
                case 1:
                    firstDatabase();
                    break;
                case 2:
                    secondDatabase();
                    break;
                case 3:
                    System.out.println("Exiting the system.");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }


        }
    }
    private static void printManu() {
        StringBuilder sb = new StringBuilder();
        sb.append("1. Display information from the first database.\n" +
                  "2. Display information from the second database.\n" +
                  "3. Exiting the system. ");
        System.out.println(sb);
    }
    public static void uppercaseLowercase(String colum) {
        String[] array = colum.trim().split(" ");
        int size = 0;
        for (String str : array) {
            if (checkUppercase(str))
                size++;
        }
        String[] uppercase = new String[size];
        String[] lowercase = new String[array.length - size];
        int i = 0, j = 0;
        for (String word : array) {
            if (checkUppercase(word)) {
                uppercase[i] = word;
                i++;
            } else {
                lowercase[j] = word;
                j++;
            }
        }
        insertFirst(uppercase);
        insertSecond(lowercase);
    }
    public static void creatSecondDB() {
        try (Statement statement = CONNECTION.createStatement()) {
            statement.executeUpdate(
                    """
                             CREATE TABLE IF NOT EXISTS second(
                              id SERIAL PRIMARY KEY ,
                              info text
                             );
                            """
            );
            System.out.println("successful");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public static void insertSecond(String[] lowercase) {
        creatSecondDB();
        StringBuilder sb = new StringBuilder();
        for (String word : lowercase) {
            sb.append(word).append(" ");
        }
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(
                "INSERT INTO second(info) VALUES (?)"
        )) {
            preparedStatement.setString(1, sb.toString());
            preparedStatement.execute();
            System.out.println("successful");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public static void creatFirstDB() {
        try (Statement statement = CONNECTION.createStatement()) {
            statement.executeUpdate(
                    """
                             CREATE TABLE IF NOT EXISTS first(
                              id SERIAL PRIMARY KEY ,
                              info text
                             );
                            """
            );
            System.out.println("successful");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public static void insertFirst(String[] uppercase) {
        creatFirstDB();
        StringBuilder sb = new StringBuilder();
        for (String word : uppercase) {
            sb.append(word).append(" ");
        }
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(
                "INSERT INTO first(info) VALUES (?)"
        )) {
            preparedStatement.setString(1, sb.toString());
            preparedStatement.execute();
            System.out.println("successful");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public static Boolean checkUppercase(String word) {
        char c = word.charAt(0);
        return c > 64 && c < 91 ? true : false;
    }
    private static void firstDatabase() {
        String sql = "SELECT * FROM first";
        try (Statement statement = CONNECTION.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String string = resultSet.getString("info");
                System.out.print(string + " ");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static void secondDatabase() {
        String sql = "SELECT * FROM second";
        try (Statement statement = CONNECTION.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String string = resultSet.getString("info");
                System.out.print(string + " ");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
