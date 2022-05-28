To run Modular Choreographies [Maven](https://maven.apache.org/install.html) needs to be installed as well as [Choral](https://www.choral-lang.org/install.html).

To run the code after repo is cloned:
First run the command
```console
~$ mvn clean install
```

Then the code is run using
```console
~$ java -jar --enable-preview target/Master_Parser-1.0-SNAPSHOT-jar-with-dependencies.jar "{path of input file}" "{path of where to store output files}"
```
