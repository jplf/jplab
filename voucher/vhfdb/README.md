### VHF db program

#### Gradle

This program is built with Gradle. This tool is chosen since it is much more powerful
and easier to use than `maven`.

##### Some tips

Start slowly, read the doc from the beginning, try some *hello world* content,
avoid the `init` plugin which generates a ton of unknown files.

Check out the command line [options](https://docs.gradle.org/current/userguide/gradle_command_line.html)
and give a try to the gui version. The `-m` allows to start a dry run.

Play with properties, figure out where to define them and how to use them.

Code some simple tasks. The `<<` operator is simply an alias for doLast.

The repository is a place where dependencies can be found. A good choice is
[jcenter](https://bintray.com/bintray/jcenter). Google with some strings to
find out the best package needed to compile the app.

##### Used plugins

To compile java code just use the [java plugin](https://docs.gradle.org/current/userguide/java_plugin.html)
and organize the directories as expected by the default behaviour of the plugin: the code should be in
`src/main/java`, a source file will have a path like `src/main/java/my/package/code/Code.java`.

To verify that the coding standard is respected use the
[checkstyle plugin](https://docs.gradle.org/current/userguide/checkstyle_plugin.html).
The rules and exceptions are described in the `config/checkstyle` directory.
To check the code run `gradle check`

To package a web application apply the [war plugin](https://docs.gradle.org/current/userguide/war_plugin.html).
The webapp specific content has to be in the directory `src/main/webapp`. Hopefully the contents of the `classes` directory in `java` and
`webapp` are merged in the `war`.

In the development phase it is possible to create a soft link to the `build/libs/myapp.war` into the tomcat `webapps` directory.
Each time the code is modified and `gradle` is run the application deployed by tomcat is updated.


