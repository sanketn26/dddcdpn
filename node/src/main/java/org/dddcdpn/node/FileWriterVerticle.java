package org.dddcdpn.node;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.commons.codec.digest.DigestUtils;

public class FileWriterVerticle extends AbstractVerticle {

    private MessageConsumer<String> fileConsumer;

    public void start(){
        final String dataFolderLocation = config().getString("dataFolderLocation");
        final String fileWriterTopic = config().getString("fileWriterTopic");
        final String publishDataTopic = config().getString("publishDataTopic");
        final String currentNodeDetails = config().getString("currentNodeDetails");
        final Logger logger = LoggerFactory.getLogger(FileWriterVerticle.class);

        fileConsumer = vertx.eventBus().consumer(fileWriterTopic);
        fileConsumer.handler(message ->{
            // Handle Data Writing
            String dataId = DigestUtils.sha256Hex(message.body());
            FileSystem fs = vertx.fileSystem();
            String filePath = dataFolderLocation + "/" + dataId;
            fs.exists(filePath, existsChecker -> {
                if(existsChecker.succeeded()){
                    if(existsChecker.result()) {
                        fs.delete(filePath, deleteHandler -> {
                            if(deleteHandler.succeeded()){
                                logger.info("Removed File: " + filePath);
                                writeFile(publishDataTopic,
                                        currentNodeDetails,
                                        logger,
                                        message,
                                        dataId,
                                        fs,
                                        filePath);

                            }
                        });
                    }
                    else{
                        writeFile(publishDataTopic,
                                currentNodeDetails,
                                logger,
                                message,
                                dataId,
                                fs,
                                filePath);
                    }
                }
            });


        });
    }

    private void writeFile(String publishDataTopic, String currentNodeDetails, Logger logger, Message<String> message, String dataId, FileSystem fs, String filePath) {
        fs.writeFile(filePath, Buffer.buffer(message.body()), writer->{
            if(writer.succeeded()){
                logger.info("File Written to: " + filePath);
                vertx.eventBus().publish(publishDataTopic, ""+ dataId + "#" + currentNodeDetails);
            }
            else{
                logger.error("File cannot be Written to: " + filePath, writer.cause());
            }
        });
    }

    public void stop(){
        if(fileConsumer != null){
            fileConsumer.unregister();
        }
    }
}

