# transfer-app
Home Task for the Backend Engineer role

## Build

You can build project using maven

    mvn clean package


## Start
After building you can start the application using terminal

    java -jar transfer-app/target/transfer-app.jar

or you can start the application programmatically

    
    TransferApp transferApp = new TransferApp(cfg);
    transferApp.start();
    
You can check that the application has been started successfully sending GET request to URL

   
    http://localhost:8080/healthcheck

## Configuration

If you start the application using terminal you can set next System Properties
* transfer.app.server.port
* transfer.app.server.contextPath
* transfer.app.server.executor-pool-size
* transfer.app.wait-transaction-ms

example:
    
    java -Dtransfer.app.server.port=8085 -jar transfer-app/target/transfer-app.jar 

or if you start the application programmatically you can use class `TransferAppConfig`

    TransferAppConfig cfg = new TransferAppConfig()
        .serverPort(8085)
        .serverExecutorPoolSize(2)
        .httpContextPath("/transfer-app");
        
    TransferApp transferApp = new TransferApp(cfg);
    transferApp.start();
    
## Integration tests

    You can find an integration test in module transfer-app in classes `ru.uskov.dmitry.transferapp.TransferTest` and `ru.uskov.dmitry.transferapp.AccountManagementTest`

## Comment
I haven't described rest api in this file because I am sure it is not necessary in Test Home task. You can understand how this api works after you have watched classes 
`ru.uskov.dmitry.transferapp.rest.controller.AccountManagementController` and `ru.uskov.dmitry.transferapp.rest.controller.TransferController`.
    
P.S. as you can see this project contains two modules. The first is transfer-app. It is rest-api implementation. And the second one is http-rest-server. 
It is http-server implementation, which makes creating REST-services easier. I am not allowed to use big frameworks, such as Spring, so I have made this mini-framework myself