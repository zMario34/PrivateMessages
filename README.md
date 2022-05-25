# PrivateMessages
A new bungeecord & velocity plugin for writing private messages!

## Building
We use Maven to handle dependencies & building.

### Requirements
To compile the project, you must have: Java 8 JDK, Git and Maven

#### Compiling from source
```
git clone https://github.com/zMario34/PrivateMessages.git
cd PrivateMessages
mvn clean install
```

You can find the output jars in the `target` directories of every module.

## Contributing
#### Pull Requests
If you make any changes or improvements to the plugin which you think would be beneficial to others, please consider making a pull request.

#### Project Structure
The project is split up into a few separate modules.

* **Common** - The common module contains some code for both bungeecord and velocity.
* **BungeeCord & Velocity** - Modules that use the common module and that support different Minecraft proxies.
