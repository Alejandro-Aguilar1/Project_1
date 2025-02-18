
## Running the Project
There are a few prerequisite tools to run our project. While we will be including both a macOS .dmg file and a Windows executable, if you wish to build the project itself, we will outline what we did to do so.

The development of our project was done primarily in IntelliJ Ultimate, where we had access to Gradle 8.12 and Oracle JDK 23. Gradle acts as the build service, fetching dependencies to be used for compiling the project. The Oracle JDK provides the primary mechanism for running the project through the Java Virtual Machine, as well as some libraries from Java that we used. Another tool, which is highly recommended, is having the Kotlin Compiler installed. Though you should not need to compile our Kotlin project from the command, if you wished to do so, the Kotlin compiler would be the tool to accomplish this.

Again, we highly recommend running the project through IntelliJ Ultimate so the Gradle build process will be automated. If you wished to run Gradle from the command line, you should ensure to *sync* Gradle dependencies, then run "gradle run" to have Gradle build the project.

If you have any issues getting the project running, please let us know.

To create an executable of this program run this command in the root directory of the project.
```bash
/.gradlew package
```

Gael Mota, Alejandro Aguilar.