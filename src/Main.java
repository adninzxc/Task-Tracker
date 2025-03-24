import CLI.TaskManager;
import Model.Status;
import Model.TaskModel;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        taskManager.loadTasksFromFile();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to TaskTracker! If you need help with the commands, type 'help'");
        System.out.print("task-cli> ");

        while (true) {
            String input = scanner.nextLine().trim();
            String[] parts = input.split("\\s+", 2);

            if (parts.length == 0 || parts[0].isEmpty()) {
                System.out.print("task-cli> ");
                continue;
            }

            String command = parts[0];
            String arguments = parts.length > 1 ? parts[1] : "";

            try {
                if (command.equals("mark") && arguments.startsWith("in-progress")) {
                    String idPart = arguments.substring("in-progress".length()).trim();
                    try {
                        int id = Integer.parseInt(idPart);
                        TaskModel task = taskManager.markInProgress(id);
                        if (task != null) {
                            System.out.println("Task marked as in progress");
                        } else {
                            System.out.println("Error: Task not found");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid ID format");
                    }
                    System.out.print("task-cli> ");
                    continue;
                } else if (command.equals("mark") && arguments.startsWith("done")) {
                    String idPart = arguments.substring("done".length()).trim();
                    try {
                        int id = Integer.parseInt(idPart);
                        TaskModel task = taskManager.markDone(id);
                        if (task != null) {
                            System.out.println("Task marked as done");
                        } else {
                            System.out.println("Error: Task not found");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid ID format");
                    }
                    System.out.print("task-cli> ");
                    continue;
                }

                switch (command) {
                    case "help":
                        commandLineHelp();
                        break;
                    case "add":
                        if (!arguments.isEmpty()) {
                            TaskModel newTask = taskManager.addTask(arguments);
                            if (newTask != null) {
                                System.out.println("Task added successfully (ID: " + newTask.getId() + ")");
                            } else {
                                System.out.println("Error: A task with this description already exists. Please use a different description.");
                            }
                        } else {
                            System.out.println("Error: Description is required");
                        }
                        break;
                    case "update":
                        String[] updateParts = arguments.split("\\s+", 2);
                        if (updateParts.length == 2) {
                            try {
                                int id = Integer.parseInt(updateParts[0]);
                                String newDescription = updateParts[1];

                                boolean isDuplicate = false;
                                for (TaskModel task : taskManager.getAllTasks()) {
                                    if (task.getDescription().equalsIgnoreCase(newDescription) && task.getId() != id) {
                                        isDuplicate = true;
                                        break;
                                    }
                                }

                                if (isDuplicate) {
                                    System.out.println("Error: Another task with this description already exists. Please use a different description.");
                                } else {
                                    TaskModel updatedTask = taskManager.updateTask(id, newDescription);
                                    if (updatedTask != null) {
                                        System.out.println("Task updated successfully");
                                    } else {
                                        System.out.println("Error: Task not found");
                                    }
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Error: Invalid ID format");
                            }
                        } else {
                            System.out.println("Error: Both ID and new description are required");
                        }
                        break;
                    case "delete":
                        if (arguments.trim().equalsIgnoreCase("all")) {
                            int count = taskManager.clearAllTasks();
                            if (count > 0) {
                                System.out.println("All tasks cleared successfully. Deleted " + count + " task(s).");
                            } else {
                                System.out.println("No tasks to delete.");
                            }
                        } else {
                            try {
                                int id = Integer.parseInt(arguments.trim());
                                boolean deleted = taskManager.deleteTask(id);
                                if (deleted) {
                                    System.out.println("Task deleted successfully");
                                } else {
                                    System.out.println("Error: Task not found");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Error: Invalid ID format. Use a numeric ID or 'all' to clear all tasks.");
                            }
                        }
                        break;
                    case "list":
                        if (arguments.isEmpty()) {
                            displayTasks(taskManager.getAllTasks());
                        } else if (arguments.equals("done")) {
                            displayTasks(taskManager.getTasksByStatus(Status.DONE));
                        } else if (arguments.equals("todo")) {
                            displayTasks(taskManager.getTasksByStatus(Status.TODO));
                        } else if (arguments.equals("in-progress")) {
                            displayTasks(taskManager.getTasksByStatus(Status.INPROGRESS));
                        } else {
                            System.out.println("Error: Unknown list option");
                        }
                        break;
                    case "exit":
                    case "quit":
                        System.out.println("Goodbye!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Unknown command. Type 'help' for available commands");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            System.out.print("task-cli> ");
        }
    }

    private static void commandLineHelp() {
        System.out.println("TaskTracker command lines:");
        System.out.println("- add <description>                  : Add a new task");
        System.out.println("- update <id> <new description>      : Update an existing task");
        System.out.println("- delete <id>                        : Delete a task");
        System.out.println("- delete all                         : Delete all tasks");
        System.out.println("- mark in-progress <id>              : Mark a task as 'in progress'");
        System.out.println("- mark done <id>                     : Mark a task as 'done'");
        System.out.println("- list                               : List all tasks");
        System.out.println("- list done                          : List all tasks that are 'done'");
        System.out.println("- list todo                          : List all tasks that are 'todo'");
        System.out.println("- list in-progress                   : List all tasks that are 'in progress'");
        System.out.println("- exit/quit                          : Exit the program");
    }

    private static void displayTasks(List<TaskModel> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("No tasks found");
            return;
        }

        System.out.println("ID | Description | Status | Created At | Updated At");
        System.out.println("--------------------------------------------------");
        for (TaskModel task : tasks) {
            System.out.printf("%d | %s | %s | %s | %s%n",
                    task.getId(),
                    task.getDescription(),
                    formatStatus(task.getStatus()),
                    task.getCreatedAt(),
                    task.getUpdatedAt());
        }
    }

    private static String formatStatus(Status status) {
        switch (status) {
            case TODO:
                return "Todo";
            case INPROGRESS:
                return "In Progress";
            case DONE:
                return "Done";
            default:
                return status.toString();
        }
    }
}