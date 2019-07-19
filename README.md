# Fishy Translator

Simple translation tool for i18n resources in JSON format based on [ResourceBundle Editor plugin for Eclipse](https://essiembre.github.io/eclipse-rbe/).

This tool is released under a permissive MIT license.


## Usage


Make sure that you have Java 8 or newer.

Double click fishy.jar to start or run
```
java -jar fishy.jar
```

Select (open) the folder containing the i18n files.

## Build

Download or clone the repository and run maven:
```
./mvnw package
```
The application can be found inside the `target` folder

Alternatively you can run the application directly from maven:
```shell
./mvnw exec:java
```

## License

Code licensed under the [MIT License](LICENSE.txt).



