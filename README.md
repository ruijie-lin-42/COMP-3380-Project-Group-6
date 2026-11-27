# Important Note 

One thing we need to be careful about when pushing code to GitHub: we should not commit any personal or sensitive information, 
even if I make the repository to private and only accessible to collaborators.

In particular, we should avoid pushing:
- Database usernames and passwords.
- Personal schema names (our usernames), since they are required as table prefixes in our SQL statements.

Proposed solution:
- Keep the SQL files in the shared repository generic, without personal schema prefixes.
- Each team member can maintain their own local copies of the SQL files with their actual schema names. These local files 
should be excluded using `.gitignore`.
- When preparing the final submission for the professor, we can include the actual schema names in the submitted version if required.
- For database credentials, keep them in a local configuration file (e.g., `auth.cfg`) and commit only a template (e.g., `auth.cfg.examples`).

This way, we can share and collaborate on the project without exposing our personal credentials or usernames in the Git repository.

I understand that this adds a little extra work to our workflow, and I know everyone is already busy with the projects, assignments, or finals. 
However, I think it's important that we avoid exposing our personal usernames and credentials in the shared repository.

If anyone doesn't have enough time to make these changes, that's totally fine. You can just send me your SQL files, and 
I'll update the schema names where necessary and push the changes to GitHub. No need to worry about it!

# COMP 3380 GROUP Project - Movie Analytics

Course group project for COMP 3380 - Databases: Concepts and Usage, Fall 2026, University of Manitoba.

This is Group 6 for A01 section.

A movie database analytics project exploring relationships, trends, and insights across movies, ratings, credits, and other movie-related data.

# Data source: 
*The Movies Dataset* from *Kaggle.com*  
[https://www.kaggle.com/datasets/rounakbanik/the-movies-dataset](https://www.kaggle.com/datasets/rounakbanik/the-movies-dataset)

# Project Structure
```
COMP-3380-Project-Group-6/
│
├── src/
│   ├── dto/
│   ├── exceptions/
│   ├── Main.java
│   ├── QueryLoader.java
│   ├── QueryRepositoriy.java
│   └── QueryUI.java
│  
├── resources/  
│   ├── queries.xml  
│   ├── schema.sql  
│   ├── insert_data.sql  
│   └── dataset/ 
│
├── dataCleaning/
│  
├── libs/  
│   ├── mssql-jdbc-11.2.0.jre11.jar  
│   └── mssql-jdbc-11.2.0.jre18.jar  
│  
├── config/  
│   ├── auth.cfg  
│   └── auth.cfg.examples
│  
├── docs/  
│   └── Project ER Diagram.pdf  
│  
├── bin/  
│    
├── .gitignore  
├── README.md  
└── Makefile  
```

## Project Structure Description
- `src/` contains all the java files.
- `resources/` contains all the sql statements and the dataset. I structured the files this way since I want to separate the responsibilities so that the code have better structures and is easier to understand.
- `dataCleaning` contains all the code we used to clean the data.
- `libs/` contains the dependencies, for now just the jdbc driver for sql server
  - I think we use mssql-jdbc-11.2.0.jre18.jar since our java version is 25, we'll see if we need the other one, if not then delete it should be fine
- `config/` contains all the configurations, for now just username and password for database connection
  - You should be creating your own `auth.cfg` file and follow the example file.
  - `auth.cfg` is contained in `.gitignore`, **NEVER PUSH THAT FILE TO THE REPOSITORY**. We leave the `auth.cfg.example` for showing how to configure username and password.
- `bin/` contains all the compiled codes, won't be pushed to the repo

# How to Run

## Dependencies

Make sure your running environment includes the followings:
- Linux/Unix/WSL. The code is going to be run on Aviary which is a Linux machine so why not. Makes things easier for `Makefile`:)
- Java 25 (since Aviary's java version is 25, other versions not tested).
- make (if you don't have make, see `Makefile` and run the commands line by line).

Make sure you have an available MSSQL (Microsoft SQL Server) database to connect to. Here in the project, for reasons of confidentiality, 
specific database connection url, authentication information, and the specific information required to locate database tables will not be provided.

## Run

- First clone the repository to your own machine.
- Enter the root directory `COMP-3380-Project-Group-6`, run the following:
  - To compile the files, use `make` or `make build`.
  - To run the project, use `make run`.
