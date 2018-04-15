package org.dddcdpn.node;

import io.vertx.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        FileMonitorVerticle fileMon = new FileMonitorVerticle();
        DataProcessorVerticle dataProcessorVerticle = new DataProcessorVerticle();
        FileWriterVerticle fileWriterVerticle = new FileWriterVerticle();
        StateManagerVerticle stateManagerVerticle = new StateManagerVerticle();
        StateRecieverVerticle stateRecieverVerticle = new StateRecieverVerticle();
        vertx.deployVerticle(fileWriterVerticle, stringAsyncResult ->
        {
            if(stringAsyncResult.succeeded())
                System.out.println("Deployed ..." + stringAsyncResult.result());
            else
                System.out.println("Failed ..." + stringAsyncResult.result());
        });

        vertx.deployVerticle(dataProcessorVerticle, stringAsyncResult ->
        {
            if(stringAsyncResult.succeeded())
                System.out.println("Deployed ..." + stringAsyncResult.result());
            else
                System.out.println("Failed ..." + stringAsyncResult.result());
        });
        vertx.deployVerticle(stateManagerVerticle, stringAsyncResult ->
        {
            if(stringAsyncResult.succeeded())
                System.out.println("Deployed ..." + stringAsyncResult.result());
            else
                System.out.println("Failed ..." + stringAsyncResult.result());
        });
        vertx.deployVerticle(stateRecieverVerticle, stringAsyncResult ->
        {
            if(stringAsyncResult.succeeded())
                System.out.println("Deployed ..." + stringAsyncResult.result());
            else
                System.out.println("Failed ..." + stringAsyncResult.result());
        });
        vertx.deployVerticle(fileMon, stringAsyncResult ->
        {
            if(stringAsyncResult.succeeded())
                System.out.println("Deployed ..." + stringAsyncResult.result());
            else
                System.out.println("Failed ..." + stringAsyncResult.result());
        });
    }
}
