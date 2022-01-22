package ca.utoronto.utm.mcs;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;

/**
* This Class creates the Server and Handler components, as well as the server context, and starts the server.
* @version 1.0
* @author Armand Sarkezians
 */
public class App{
    //Port number for local host
    static int port = 8080;

    public static void main(String[] args) throws IOException{
        //Creating Server and Handler Components
        ServerComponent serverComponent = DaggerServerComponent.create();
        ReqHandlerComponent reqHandlerComponent = DaggerReqHandlerComponent.create();

        //Building the handler and server
        ReqHandler reqHandler = reqHandlerComponent.buildHandler();
        Server server = serverComponent.buildServer();

        //Creating the server context and starting the server
        server.getHttpSever().createContext("/api/v1/", reqHandler);
        server.getHttpSever().start();

        System.out.printf("Server started on port %d\n", port);

        // This code is used to get the neo4j address, you must use this so that we can mark :)
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("NEO4J_ADDR");
        System.out.println(addr);
    }
}
