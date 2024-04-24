package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class TodoList {
    static ArrayList<String> readTasksFromFile() {
        ArrayList<String> tasks = new ArrayList<>();
        try {
            File file = new File("TaskList.txt");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader("TaskList.txt"));
                String line;
                while ((line = reader.readLine()) != null) {
                    tasks.add(line);
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    static void saveTasksToFile(ArrayList<String> tasks) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("TaskList.txt"));
            for (String task : tasks) {
                writer.write(task);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}