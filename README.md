# Exchange Order Matching Engine
## Description
**Matching Engine** (<code>matchingEngine-1.0.jar</code>) is a Java application serving as financial exchange order matching engine. The algorithm is based on the price/time priority.

## Requirements and Dependencies
Matching Engine requires Java 14.

It is a Maven project (see pom.xml, file Apache Maven 3.6.3 is recommended) and depends on:
* Spring 5.2.7.RELEASE https://spring.io/projects/spring-framework
* Spring Boot 2.3.1.RELEASE https://spring.io/projects/spring-boot
* QuickFIX/J 2.2.0 https://github.com/quickfix-j/quickfixj
* Groovy 1.8.9

All dependencies are downloaded from Internet when you run <code>mvn clean install</code>.

## Building
Since project is a Spring Boot project, <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html">Spring Boot Maven</a> build plugin is used to build the artifact.

To build Simple FIX Client run <code>mvn clean install</code> or <code>mvn package</code> or use <code>package.bat</code> in the project folder.

Resulting simplefixclient-&lt;version&gt;.jar JAR file will be created in <code>target</code> sub-folder.

This JAR file is a Spring Boot jumbo-JAR that contains all dependencies required to run the application.

## Running
Start the application using:

<code>jar -jar target/simplefixclient-&lt;version&gt;.jar Scenario1</code>

or use <code>mvn spring-boot:run</code> command

or use <code>start.bat</code> or <code>start.sh</code> script.

## Eclipse
Before opening project in Eclipse, run <code>mvn eclipse:eclipse</code> task in the project's folder. Maven will create Eclipse specific project files. Then import the project into your workspace as a Maven project.

To run project in Eclipse, use Maven task <code>spring-boot:run</code>. 

## Testing
For testing you can use <a href="https://www.quickfixj.org/usermanual/2.1.0/usage/examples.html">quickfixj-examples-executor application</a> that is distributed as a part of QuickFIX/J distribution package. 

Or <a href="https://github.com/alexkachanov/simpleFIXExecutor">Simple FIX Executor</a> can be used which is basically a Spring Boot wrapper around quickfixj-examples-executor application. 

Simple FIX Client will use FIX 4.2 protocol and will try to access port 9878 at localhost (see. simplefixclient.cfg).

Sample Groovy DSL scenario "Scenario1.groovy" is located in <code>scenarios</code> sub-folder. 

You can pass any scenario as a startup parameter (without .groovy extension).
