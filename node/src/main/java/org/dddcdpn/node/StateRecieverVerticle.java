package org.dddcdpn.node;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.commons.codec.digest.DigestUtils;

public class StateRecieverVerticle extends AbstractVerticle {

    private MessageConsumer<String> newStateConsumer;

    public void start(){
        final Logger logger = LoggerFactory.getLogger(StateRecieverVerticle.class);
        final String dataFolderLocation = config().getString("dataFolderLocation");
        final String publishDataTopic = config().getString("publishDataTopic");
        final String currentNodeDetails = config().getString("currentNodeDetails");
        newStateConsumer = vertx.eventBus().consumer(publishDataTopic);
        newStateConsumer.handler(stateHandler -> {
            logger.info("Received Data " + stateHandler.body());
            String[] data = stateHandler.body().split("#");
            String dataId = data[0];
            logger.info("Data Id " + dataId);
            logger.info("Comparing " + currentNodeDetails + " with " + data[1]);
            if(currentNodeDetails.equals(data[1]))
                return;
            String[] nodeDetails = data[1].split(":");
            HttpClientOptions options = new HttpClientOptions().setDefaultHost(nodeDetails[0]);
            options.setDefaultPort(Integer.parseInt(nodeDetails[1]));
            vertx.createHttpClient(options).websocket("/data", webSocket -> {
                webSocket.textMessageHandler(content -> {
                    logger.info("Received Content :" + dataId);
                    // To act on recieved data from websocket server
                    String fileName = DigestUtils.sha256Hex(content);
                    FileSystem fs = vertx.fileSystem();
                    String filePath = dataFolderLocation + "/" + fileName;
                    fs.writeFile(filePath, Buffer.buffer(content),writeHandler ->{
                        if(writeHandler.succeeded()){
                            logger.info("New State updated at :" + filePath);
                        }
                        else{
                            logger.error("Could not write new state at: " + filePath,
                                    writeHandler.cause());
                        }
                    });
                });
                logger.info("Requesting for Data :" + dataId);
                webSocket.writeTextMessage("GET#"+ dataId);
            });
        });
    }

    public void stop(){
        if(newStateConsumer != null) {
            newStateConsumer.unregister();
        }
    }

}
