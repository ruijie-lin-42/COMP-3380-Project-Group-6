# Important Note
- This documentation will be continuously improved.
- For now, here are the minimal instructions to get things running.

# Project Structure
```
COMP-3380-Project-Group-6/
│
├── src/  
│   └── Main.java  
│  
├── resources/  
│   ├── queries.xml  
│   ├── schema.sql  
│   ├── insert_data.sql  
│   └── dataset/  
│  
├── libs/  
│   ├── mssql-jdbc-11.2.0.jre11.jar  
│   └── mssql-jdbc-11.2.0.jre18.jar  
│  
├── config/  
│   ├── auth.cfg  
│   └── auth.cfg.example  
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

## Run

- First clone the repository to your own machine.
- Enter the root directory `COMP-3380-Project-Group-6`, run the following:
  - To compile the files, use `make` or `make build`.
  - To run the project, use `make run`.
