# Task Tracker CLI

A simple command-line interface application to track and manage your tasks. Task Tracker helps you organize what you need to do, what you're currently working on, and what you've completed.

## Features

- Add, update, and delete tasks
- Mark tasks as "in progress" or "done"
- List all tasks or filter by status
- JSON file storage for task persistence
- Duplicate task prevention
- Simple and intuitive command-line interface

## Requirements

- Java JDK 8 or higher
- No external libraries required

## Installation

1. Clone the repository:
```
git clone https://github.com/adninzxc/task-tracker.git
cd task-tracker
```

2. Compile the Java files:
```
javac -d bin src/Model/*.java src/Json/*.java src/CLI/*.java src/Main.java
```

3. Run the application:
```
java -cp bin Main
```

## Usage

Upon starting the application, you'll see a prompt `task-cli>` where you can enter commands:

### Task Management Commands

- **Add a new task**:
  ```
  add <description>
  ```
  Example: `add Buy groceries`

- **Update a task**:
  ```
  update <id> <new description>
  ```
  Example: `update 1 Buy groceries and cook dinner`

- **Delete a task**:
  ```
  delete <id>
  ```
  Example: `delete 1`

- **Delete all tasks**:
  ```
  delete all
  ```

- **Mark a task as in progress**:
  ```
  mark in-progress <id>
  ```
  Example: `mark in-progress 1`

- **Mark a task as done**:
  ```
  mark done <id>
  ```
  Example: `mark done 1`

### Listing Tasks

- **List all tasks**:
  ```
  list
  ```

- **List tasks by status**:
  ```
  list todo
  list in-progress
  list done
  ```

### Other Commands

- **Help**:
  ```
  help
  ```
  Shows available commands

- **Exit the application**:
  ```
  exit
  ```
  or
  ```
  quit
  ```

## Task Properties

Each task has the following properties:
- **ID**: A unique identifier for the task
- **Description**: A short description of the task
- **Status**: The status of the task (todo, in-progress, done)
- **Created At**: The date and time when the task was created
- **Updated At**: The date and time when the task was last updated

## File Storage

Tasks are automatically saved to a JSON file named `tasks.json` in the current directory. This file is created if it doesn't exist and is updated whenever you make changes to tasks.

## Examples

```
task-cli> add Complete Java project
Task added successfully (ID: 1)

task-cli> add Read design patterns book
Task added successfully (ID: 2)

task-cli> mark in-progress 1
Task marked as in progress

task-cli> list
ID | Description | Status | Created At | Updated At
--------------------------------------------------
1 | Complete Java project | In Progress | Mon Mar 24 10:15:30 CET 2025 | Mon Mar 24 10:16:05 CET 2025
2 | Read design patterns book | Todo | Mon Mar 24 10:15:45 CET 2025 | Mon Mar 24 10:15:45 CET 2025

task-cli> mark done 1
Task marked as done

task-cli> list done
ID | Description | Status | Created At | Updated At
--------------------------------------------------
1 | Complete Java project | Done | Mon Mar 24 10:15:30 CET 2025 | Mon Mar 24 10:16:20 CET 2025
```

## Project Structure

- `src/Model/`: Contains the data models (TaskModel, Status)
- `src/Json/`: Contains the JSON handling utilities
- `src/CLI/`: Contains the task management logic
- `src/Main.java`: Contains the command-line interface and program entry point

## Author

Adnin Sherifi

## Acknowledgments

- Special thanks to https://roadmap.sh/projects/task-tracker.
