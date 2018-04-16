package org.dddcdpn.node;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        DeploymentOptions options = new DeploymentOptions();
        options.setConfig(config());

        FileMonitorVerticle fileMon = new FileMonitorVerticle();
        DataProcessorVerticle dataProcessorVerticle = new DataProcessorVerticle();
        FileWriterVerticle fileWriterVerticle = new FileWriterVerticle();
        StateManagerVerticle stateManagerVerticle = new StateManagerVerticle();
        StateRecieverVerticle stateRecieverVerticle = new StateRecieverVerticle();
        vertx.deployVerticle(fileWriterVerticle, options,stringAsyncResult ->
        {
            if(stringAsyncResult.succeeded())
                System.out.println("Deployed ..." + stringAsyncResult.result());
            else
                System.out.println("Failed ..." + stringAsyncResult.result());
        });

        vertx.deployVerticle(dataProcessorVerticle, options, stringAsyncResult ->
        {
            if(stringAsyncResult.succeeded())
                System.out.println("Deployed ..." + stringAsyncResult.result());
            else
                System.out.println("Failed ..." + stringAsyncResult.result());
        });
        vertx.deployVerticle(stateManagerVerticle, options, stringAsyncResult ->
        {
            if(stringAsyncResult.succeeded())
                System.out.println("Deployed ..." + stringAsyncResult.result());
            else
                System.out.println("Failed ..." + stringAsyncResult.result());
        });
        vertx.deployVerticle(stateRecieverVerticle, options, stringAsyncResult ->
        {
            if(stringAsyncResult.succeeded())
                System.out.println("Deployed ..." + stringAsyncResult.result());
            else
                System.out.println("Failed ..." + stringAsyncResult.result());
        });
        vertx.deployVerticle(fileMon, options,stringAsyncResult ->
        {
            if(stringAsyncResult.succeeded())
                System.out.println("Deployed ..." + stringAsyncResult.result());
            else
                System.out.println("Failed ..." + stringAsyncResult.result());
        });
    }
}
