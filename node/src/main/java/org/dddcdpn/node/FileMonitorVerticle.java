package org.dddcdpn.node;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.file.FileSystem;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class FileMonitorVerticle extends AbstractVerticle {

    private long timerId = -1;

    public void start() {
        final Logger logger = LoggerFactory.getLogger(FileMonitorVerticle.class);
        final String monitoredFolder = config().getString("monitoredFileLocation");
        final String fileDataProcessor = config().getString("fileDataProcessor");
        final FileSystem fs = vertx.fileSystem();
        timerId = vertx.setPeriodic(1000, id -> {
            logger.info("Reading contents of the Directory " + monitoredFolder);
            fs.readDir(monitoredFolder, listAsyncResult -> {
                if(listAsyncResult.succeeded()){
                    for (String filePath: listAsyncResult.result()) {
                        fs.readFile(filePath, handler -> {
                            if (handler.succeeded()) {
                                // Need to handle forwarding the message to the
                                // the processing verticle
                                vertx.eventBus().send(fileDataProcessor, handler.result().toString());
                            } else {
                                logger.error(
                                    "Could not read file",
                                    handler.cause());
                            }
                        });
                        fs.delete(filePath, handler -> {
                            if (handler.succeeded()) {
                                logger.info(
                                    "Removed the file successfully");
                            } else {
                                logger.error(
                                    "Could not delete file",
                                    handler.cause());
                            }
                        });
                    }
                }
                else {
                    logger.error("Failed to read contents of directory",
                        listAsyncResult.cause());
                }
            });
        });
    }

    // Optional - called when verticle is undeployed
    public void stop() {
        final Logger logger = LoggerFactory.getLogger(FileMonitorVerticle.class);
        logger.info("Stopping the verticle");
        if(timerId != -1) {
            logger.info("Cancelling the Timer");
            vertx.cancelTimer(timerId);
            timerId = -1;
        }
        logger.info("Stopped the verticle");
    }


}
