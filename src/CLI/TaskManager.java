package CLI;

import Json.JsonHandler;
import Model.Status;
import Model.TaskModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private final Map<Integer, TaskModel> tasks = new HashMap<>();
    private int nextId = 1;
    private static final String TASKS_FILE = "tasks.json";

    public void loadTasksFromFile() {
        try {
            File file = new File(TASKS_FILE);
            if (!file.exists()) {
                return;
            }

            StringBuilder jsonContent = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonContent.append(line);
                }
            }

            if (jsonContent.length() == 0) {
                return;
            }

            Map<String, Object> data = JsonHandler.parseJsonObject(jsonContent.toString());
            if (data.containsKey("nextId")) {
                nextId = ((Number) data.get("nextId")).intValue();
            }

            if (data.containsKey("tasks")) {
                List<Object> tasksList = (List<Object>) data.get("tasks");
                for (Object taskObj : tasksList) {
                    Map<String, Object> taskMap = (Map<String, Object>) taskObj;
                    int id = ((Number) taskMap.get("id")).intValue();
                    String description = (String) taskMap.get("description");
                    Status status = Status.valueOf((String) taskMap.get("status"));
                    Date createdAt = new Date(Long.parseLong((String) taskMap.get("createdAt")));
                    Date updatedAt = new Date(Long.parseLong((String) taskMap.get("updatedAt")));

                    TaskModel task = new TaskModel(id, description, status, createdAt, updatedAt);
                    tasks.put(id, task);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading tasks from file: " + e.getMessage());
        }
    }

    public void saveTasksToFile() {
        try {
            String json = tasksToJson();
            File file = new File(TASKS_FILE);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json);
            }
        } catch (IOException e) {
            System.err.println("Error saving tasks to file: " + e.getMessage());
        }
    }

    public List<TaskModel> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public TaskModel addTask(String description) {
        for (TaskModel existingTask : tasks.values()) {
            if (existingTask.getDescription().equalsIgnoreCase(description.trim())) {
                return null;
            }
        }

        Date now = new Date();
        TaskModel newTask = new TaskModel(nextId, description, Status.TODO, now, now);
        tasks.put(nextId, newTask);
        nextId++;
        saveTasksToFile();
        return newTask;
    }

    public TaskModel updateTask(int id, String description) {
        if (tasks.containsKey(id)) {
            TaskModel task = tasks.get(id);
            task.setDescription(description);
            task.setUpdatedAt(new Date());
            saveTasksToFile();
            return task;
        }
        return null;
    }

    public boolean deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            saveTasksToFile();
            return true;
        }
        return false;
    }

    public int clearAllTasks() {
        int count = tasks.size();
        tasks.clear();
        saveTasksToFile();
        return count;
    }

    public TaskModel markInProgress(int id) {
        if (tasks.containsKey(id)) {
            TaskModel task = tasks.get(id);
            task.setStatus(Status.INPROGRESS);
            task.setUpdatedAt(new Date());
            saveTasksToFile();
            return task;
        }
        return null;
    }

    public TaskModel markDone(int id) {
        if (tasks.containsKey(id)) {
            TaskModel task = tasks.get(id);
            task.setStatus(Status.DONE);
            task.setUpdatedAt(new Date());
            saveTasksToFile();
            return task;
        }
        return null;
    }

    public List<TaskModel> getTasksByStatus(Status status) {
        List<TaskModel> filteredTasks = new ArrayList<>();
        for (TaskModel task : tasks.values()) {
            if (task.getStatus() == status) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    private String taskModelToJson(TaskModel task) {
        return "{" +
                "\"id\":" + task.getId() + "," +
                "\"description\":" + JsonHandler.toJsonString(task.getDescription()) + "," +
                "\"status\":" + JsonHandler.toJsonString(task.getStatus().toString()) + "," +
                "\"createdAt\":" + JsonHandler.toJsonString(String.valueOf(task.getCreatedAt().getTime())) + "," +
                "\"updatedAt\":" + JsonHandler.toJsonString(String.valueOf(task.getUpdatedAt().getTime())) +
                "}";
    }

    private String tasksToJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"nextId\":").append(nextId).append(",");
        json.append("\"tasks\":[");

        boolean first = true;
        for (TaskModel task : tasks.values()) {
            if (!first) {
                json.append(",");
            }
            json.append(taskModelToJson(task));
            first = false;
        }

        json.append("]");
        json.append("}");
        return json.toString();
    }
}
