package ca.utoronto.utm.mcs;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

/**
 * This class handles the requests given to the server, depending on their condition (GET/POST)
 * @version 1.0
 * @author Armand Sarkezians
 */
public class ReqHandler implements HttpHandler {
    public Neo4jDAO dao;

    /**
     * Constructor, creates the DAO for the Neo4j database
     */
    @Inject
    public ReqHandler(Neo4jDAO dao){
        this.dao = dao;
    }

    public Neo4jDAO getNeo4j(){
        return dao;
    }

    /**
     * This method handles the distribution of requests depending on their method(GET/POST)
     * @param exchange is the information given by the sender
     */
    @Override
    public void handle(HttpExchange exchange) {
        try{
            switch (exchange.getRequestMethod()) {
                case "GET":
                    this.handleGet(exchange);
                    break;
                case "PUT":
                    this.handlePut(exchange);
                    break;
                default:
                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This method handles the GET case, and distributes the cases to the Neo4jDAO class, which sends the request
     * to the database
     * @param exchange is the information given by the sender
     * @throws IOException
     * @throws JSONException
     */
    public void handleGet(HttpExchange exchange) throws IOException, JSONException{
        URI apiURI = exchange.getRequestURI();
        ArrayList<String> baconPath = new ArrayList<>();

        // Converting InputStream to String
        String body = Utils.convert(exchange.getRequestBody());
        String res = "";
        int baconNumber = -1;
        try{
            JSONObject filteredText = new JSONObject(body);

            // Checks to see if the requesting body has proper information
            if(filteredText.has("actorId") && !filteredText.getString("actorId").equals("") // hasRelationship
                    && filteredText.has("movieId") && !filteredText.getString("movieId").equals("")
                    && apiURI.toString().equals("/api/v1/hasRelationship")) {
                res = this.dao.hasRelationship(filteredText.getString("actorId"),
                        filteredText.getString("movieId"));
            }else if(filteredText.has("actorId") && !filteredText.getString("actorId").equals("") // getActor
                    && apiURI.toString().equals("/api/v1/getActor")) {
                res = this.dao.getActor(filteredText.getString("actorId"));
            }else if(filteredText.has("movieId") && !filteredText.getString("movieId").equals("") // getMovie
                    && apiURI.toString().equals("/api/v1/getMovie")) {
                res = this.dao.getMovie(filteredText.getString("movieId"));
            }else if(filteredText.has("actorId") && !filteredText.getString("actorId").equals("") // computeBacon#
                    && apiURI.toString().equals("/api/v1/computeBaconNumber")){
                baconNumber = this.dao.computeBaconNumber(filteredText.getString("actorId"));
            }else if(filteredText.has("actorId") && !filteredText.getString("actorId").equals("") // computeBaconP
                    && apiURI.toString().equals("/api/v1/computeBaconPath")) {
                baconPath = this.dao.computeBaconPath(filteredText.getString("actorId"));
            }else{
                // If information is incorrectly given, 400 error is sent
                exchange.sendResponseHeaders(400, -1);
                return;
            }


            if(res.equals("Not Found") || res.equals("FALSE")){ // If the movie or actor is not found in the database
                exchange.sendResponseHeaders(404, -1);
                return;
            }else if(apiURI.toString().equals("/api/v1/computeBaconNumber") && baconNumber >= 0){
                res = String.valueOf(baconNumber);
                exchange.sendResponseHeaders(200, res.length());
                OutputStream os = exchange.getResponseBody();
                os.write(res.getBytes());
                os.close();
            }else if (apiURI.toString().equals("/api/v1/computeBaconNumber") && baconNumber == -1){
                exchange.sendResponseHeaders(404, -1);
            }else if(apiURI.toString().equals("/api/v1/computeBaconPath") && baconPath != null){
                res = baconPath.toString();
                exchange.sendResponseHeaders(200, res.length());
                OutputStream os = exchange.getResponseBody();
                os.write(res.getBytes());
                os.close();
            }else if (apiURI.toString().equals("/api/v1/computeBaconPath")){
                exchange.sendResponseHeaders(404, -1);
                return;
            }else{
                exchange.sendResponseHeaders(200, res.length());
                OutputStream os = exchange.getResponseBody();
                os.write(res.getBytes());
                os.close();
            }

        }catch(Exception e){
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
            e.printStackTrace();
        }
    }

    /**
     * This method handles the POST case, and distributes the cases to the Neo4jDAO class, which sends the request
     * to the database
     * @param exchange is the information given by the sender
     * @throws IOException
     * @throws JSONException
     */
    public void handlePut(HttpExchange exchange) throws IOException, JSONException{
        URI apiURI = exchange.getRequestURI();
        // Converting InputStream to String
        String body = Utils.convert(exchange.getRequestBody());
        try{
            JSONObject filteredText = new JSONObject(body);

            // The response is to determine if the actor or movie already existed in the database
            Boolean response = true;

            // Checks to see if the requesting body has proper information
            if(filteredText.has("actorId") && !filteredText.getString("actorId").equals("") // addRelationship
                    && filteredText.has("movieId") && !filteredText.getString("movieId").equals("")
                    && apiURI.toString().equals("/api/v1/addRelationship")){
                response = this.dao.addRelationship(filteredText.getString("actorId"),
                        filteredText.getString("movieId"));
            }else if(filteredText.has("actorId") && !filteredText.getString("actorId").equals("") // addActor
                    && filteredText.has("name") && !filteredText.getString("name").equals("")
                    && apiURI.toString().equals("/api/v1/addActor")){
                response = this.dao.addActor(filteredText.getString("name"), filteredText.getString("actorId"));
            }else if(filteredText.has("movieId") && !filteredText.getString("movieId").equals("") // addMovie
                    &&filteredText.has("name") && !filteredText.getString("name").equals("")
                    && apiURI.toString().equals("/api/v1/addMovie")){
                response = this.dao.addMovie(filteredText.getString("name"),
                        filteredText.getString("movieId"));
            }else{
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            if(response){
                // If the actor/movie was not in the database
                exchange.sendResponseHeaders(200, -1);
            }else{
                // If the actor/movie was already in the database OR relationship existed OR relationship could not
                // be made (actorId or movieId was wrong)
                exchange.sendResponseHeaders(400, -1);
            }

        }catch(Exception e){
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }
}