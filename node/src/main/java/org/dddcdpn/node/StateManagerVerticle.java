package org.dddcdpn.node;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.file.FileSystem;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class StateManagerVerticle extends AbstractVerticle {

    public void start(){
        final Logger logger = LoggerFactory.getLogger(StateManagerVerticle.class);
        final String monitoredFolder = config().getString("dataFolderLocation");
        String currentNodeDetails[] = config().getString("currentNodeDetails").split(":");
        final String host = currentNodeDetails[0];
        final int port = Integer.parseInt(currentNodeDetails[1]);
        final FileSystem fs = vertx.fileSystem();

        vertx.createHttpServer().websocketHandler(serverWebSocket -> {

            if(!serverWebSocket.path().contains("/data")){
                serverWebSocket.reject();
            }
            serverWebSocket.accept();
            serverWebSocket.textMessageHandler(data -> {
                String[] commandDetails = data.split("|");
                String commadType = commandDetails[0];
                String parameter = commandDetails[1];
                if(commadType == "GET"){
                    String file = monitoredFolder + "/" + parameter;

                    fs.exists(file, fileExistenceHandler -> {
                       if(fileExistenceHandler.succeeded()){
                           if(!fileExistenceHandler.result()){
                               serverWebSocket.writeTextMessage(
                                       "No such data : " + parameter);
                           }
                           else {
                               fs.readFile(file, fileReader ->{
                                    if(fileReader.succeeded()){
                                        serverWebSocket.writeTextMessage(fileReader.result().toString());
                                    }
                                    else{
                                        serverWebSocket.writeTextMessage(
                                                "Could not read data for : " + parameter);
                                        logger.error("Could not read data for", fileReader.cause());
                                    }
                               });
                           }
                       }
                       if(fileExistenceHandler.failed()){
                           serverWebSocket.writeTextMessage(
                                   "No such data : " + parameter);
                           logger.error("Could not locate the data", fileExistenceHandler.cause());
                       }
                    });
                }
                else{
                    serverWebSocket.writeTextMessage(
                            "Cannot handle command :" + commadType);
                }
            });
        }).listen(port, host);
    }

    public void stop(){

    }
}
