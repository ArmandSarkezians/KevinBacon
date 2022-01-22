package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;

import javax.inject.Inject;
import java.net.InetSocketAddress;

/**
 * This class creates the server for which information will be sent
 * @version 1.0
 * @author Armand Sarkezians
 */
public class Server {
    // The HTTPServer which will host the database
    public HttpServer server;

    // The port on which the server will be created
    static int port = 8080;

    /**
     * Constructor, this method creates the server
     */
    @Inject
    public Server(){
        try{
            this.server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Getter, this method is a getter for the server
     * @return returns the server
     */
    public HttpServer getHttpSever(){
        return server;
    }
}
