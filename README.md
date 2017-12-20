
# spring-boot-starter-swagger-registry 

<img src="https://img.shields.io/github/forks/amanganiello90/spring-boot-starter-swagger-registry.svg">&nbsp;
<img src="https://img.shields.io/github/stars/amanganiello90/spring-boot-starter-swagger-registry.svg">&nbsp;<a href="https://github.com/amanganiello90/spring-boot-starter-swagger-registry/issues"><img src="https://img.shields.io/github/issues/amanganiello90/spring-boot-starter-swagger-registry.svg">
</a>&nbsp;<img src="https://img.shields.io/github/license/amanganiello90/spring-boot-starter-swagger-registry.svg">&nbsp;<img src="https://img.shields.io/github/downloads/amanganiello90/spring-boot-starter-swagger-registry/total.svg">&nbsp;

This is spring boot starter that arise a swagger-ui server with spring profile named _swagger-ui_.

* note
Miss only to add typescript templates. You have to write in your application.properties (in this is the example project) the _swagger.yaml_ property and specify the jar yaml dependency (you can add more artifacts with a "," to separate the names in the property value). These yaml files are loaded after starting of the application.


|Please donate whether you wish support us to give more time to app's growth | [![](https://www.paypal.com/en_US/IT/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=XTC895QYD28TC) |
|:------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------|

* build and execution informations
To test the tool, execute a maven install on **swagger-example** project and run the compiled jar with **java -jar swagger-example-1.0-SNAPSHOT.jar --spring.profiles.active=swagger-ui**, or run the main class **SpringBootApplicationRunner** of this in Eclipse with the same option **--spring.profiles.active=swagger-ui**.
Open the browser on _localhost:8081_

### News ###

* Also in development for release
