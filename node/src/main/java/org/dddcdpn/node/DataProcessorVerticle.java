package org.dddcdpn.node;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class DataProcessorVerticle extends AbstractVerticle {

    private MessageConsumer<String> textProcessor;

    public void start(){
        final Logger logger = LoggerFactory.getLogger(FileMonitorVerticle.class);
        final String fileDataProcessor = config().getString("fileDataProcessor");
        final String fileWriterTopic = config().getString("fileWriterTopic");
        textProcessor = vertx.eventBus().consumer(fileDataProcessor);
        textProcessor.handler(incoming ->{
            String data = "Processed: " + incoming.body();
            vertx.eventBus().send(fileWriterTopic, data);
        });
    }

    public void stop(){
        if(textProcessor != null){
            textProcessor.unregister();
        }
    }
}
