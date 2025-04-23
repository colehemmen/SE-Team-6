## Run Instructions
- [`Virtual Machine Setup`](#virtual-machine-setup)
- [`Running the Program`](#running-the-program)

## Virtual Machine Setup
Move the project to the VM. Either drag / drop the project from your local machine, or clone the repository.

Install the required packages:
```bat
sudo apt update
sudo apt install default-jdk openjdk-17-jdk postgresql postgresql-client libpostgresql-jdbc-java libpostgresql-jdbc-java-doc
```

Update the enviornment variables:
```env
JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
CLASSPATH=".:/usr/share/java/posgresql-jdbc4.jar"
```

Double check that Java was installed to the location above. If not, reinstall in the correct location.
Resize the screen to 1920x1080. Otherwise the game screen will not fit within the VM screen.

## Running the Program
Navigate to the project's directory, and execute the following to compile the program:
```bat
javac -d out -sourcepath src -cp lib/jlayer-1.0.1.jar src/Main.java src/**/*.java
cp -r src/images out/
```

To run the program, execute the following:
```bat
java -cp "out:lib/jlayer-1.0.1.jar:/usr/share/java/postgresql-jdbc4.jar" Main
```

## Project Members:
| GITHUB USERNAME  |    REAL NAME     |
| ---------------- | ---------------- |
| colehemmen       | Cole Hemmen      |
| The-Crimson-V    | Jacob Wise       |
| strikeriv        | Matthew Craddock |
| porter-Marcus    | Marcus Porter    |
| jpink8           | Jake Pinkerton   |
