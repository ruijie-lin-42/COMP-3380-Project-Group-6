JDBC = libs/mssql-jdbc-11.2.0.jre18.jar
BIN = bin
SRC = src/*.java
MAIN = Main

.PHONY: all build run clean

all: build

build:
	mkdir -p $(BIN)
	javac -cp "$(JDBC)" -d $(BIN) $(SRC)

run: build
	java -cp "$(BIN):$(JDBC)" $(MAIN)

clean:
	rm -rf $(BIN)
