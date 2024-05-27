package org.example.task;

import java.sql.*;
import java.util.ArrayList;

public class TaskService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/todo_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin";
    static ArrayList<String> readTasksFromDatabase() {
        ArrayList<String> tasks = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            String sql = "SELECT task FROM tasks";
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()) {
                tasks.add(resultSet.getString("task"));
                }
                } catch (SQLException e) {
                e.printStackTrace();
                }
                return tasks;
            }

    static void saveTasksToDatabase(ArrayList<String> tasks) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("TRUNCATE TABLE tasks");
            for(String task : tasks) {
                statement.executeUpdate("INSERT INTO tasks (task) VALUES ('" + task + "')");
            }
            }  catch (SQLException e) {
            e.printStackTrace();
        }
    }
}