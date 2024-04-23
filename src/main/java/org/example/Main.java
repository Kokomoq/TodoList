package org.example;

import org.example.TodoList;

import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        TodoList todoList = new TodoList();
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        while (choice != 4) {
            System.out.println("\nMenu:");
            System.out.println("1. Wyświetl wszystkie pozycje z listy");
            System.out.println("2. Dodaj nową pozycję");
            System.out.println("3. Usuń pozycję z listy");
            System.out.println("4. Wyjdź");

            System.out.print("Wybierz opcję: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    todoList.displayTasks();
                    break;
                case 2:
                    System.out.print("Podaj nową pozycję: ");
                    scanner.nextLine(); // Clear the input buffer
                    String newTask = scanner.nextLine();
                    todoList.addTask(newTask);
                    break;
                case 3:
                    System.out.print("Podaj numer pozycji do usunięcia: ");
                    int indexToRemove = scanner.nextInt();
                    todoList.removeTask(indexToRemove);
                    break;
                case 4:
                    System.out.println("Dziękujemy. Do widzenia!");
                    break;
                default:
                    System.out.println("Nieprawidłowa opcja. Wybierz ponownie.");
            }
        }

        scanner.close();
    }
}