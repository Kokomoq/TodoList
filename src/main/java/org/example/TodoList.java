package org.example;

import java.util.ArrayList;
import java.util.Scanner;

public class TodoList {
    private ArrayList<String> tasks;
    private Scanner scanner;

    public TodoList() {
        tasks = new ArrayList<>();
        scanner = new Scanner(System.in);
    }

    public void displayTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Lista zadań jest pusta.");
        } else {
            System.out.println("Lista zadań:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + ". " + tasks.get(i));
            }
        }
    }

    public void addTask(String task) {
        tasks.add(task);
        System.out.println("Dodano nowe zadanie: " + task);
    }

    public void removeTask(int index) {
        if (index >= 1 && index <= tasks.size()) {
            String removedTask = tasks.remove(index - 1);
            System.out.println("Usunięto zadanie: " + removedTask);
        } else {
            System.out.println("Nieprawidłowy indeks.");
        }
    }
}