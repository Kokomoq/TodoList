package org.example;

import java.util.ArrayList;
import java.util.Scanner;
import static org.example.TodoList.*;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> tasks = readTasksFromDatabase();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("Todo List");
            System.out.println("1. Dodaj zadanie");
            System.out.println("2. Wyświetl zadania");
            System.out.println("3. Zapisz i wyjdź");
            System.out.print("Wybierz opcję: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    scanner.nextLine();
                    System.out.print("Wprowadź zadanie: ");
                    String task = scanner.nextLine();
                    tasks.add(task);
                    break;
                case 2:
                    System.out.println("Lista zadań:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + ". " + tasks.get(i));
                    }
                    break;
                case 3:
                    saveTasksToDatabase(tasks);
                    System.out.println("Zadania zostały zapisane.");
                    break;
                default:
                    System.out.println("Nieprawidłowy wybór.");
            }

        } while (choice != 3);
    }
}
