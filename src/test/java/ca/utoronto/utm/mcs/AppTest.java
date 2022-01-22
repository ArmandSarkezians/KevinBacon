package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


/**
 * This class is for texting the endpoints
 * @version 1.0
 * @author Armand Sarkezians
 */
public class AppTest {

    // The URL used for testing
    final static String API_URL = "http://localhost:8080/api/v1";
    private static Neo4jDAO neo4j;

    /**
     * This method sends a request to the URL and returns the status code
     * @param endpoint
     * @param method
     * @param reqBody
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody)
            throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @BeforeAll
    public static void setup(){
        try{
            ServerComponent serverComponent = DaggerServerComponent.create();
            ReqHandlerComponent reqHandlerComponent = DaggerReqHandlerComponent.create();

            //Building the handler and server
            ReqHandler reqHandler = reqHandlerComponent.buildHandler();
            Server server = serverComponent.buildServer();

            //Creating the server context and starting the server
            server.getHttpSever().createContext("/api/v1/", reqHandler);
            server.getHttpSever().start();

            neo4j = reqHandler.getNeo4j();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @AfterEach
    public void cleanup(){ neo4j.cleanDatabase();}

    /**
     * This method tests the addActor in a passing environment
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void addActorPass() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject().put("name", "Denzel Washington")
                .put("actorId", "nm1001213");
        HttpResponse<String> confirmRes = sendRequest("/addActor", "POST", confirmReq.toString());
        assertEquals(200, confirmRes.statusCode());
    }

    /**
     * This method tests addActor in a failing environment
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void addActorFail() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject().put("name", "Denzel Washington");
        HttpResponse<String> confirmRes = sendRequest("/addActor", "POST", confirmReq.toString());
        assertEquals(400, confirmRes.statusCode());
    }

    /**
     * This method tests addMovie in a passing environment
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void addMoviePass() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject().put("name", "Parasite").put("movieId", "nm7001453");
        HttpResponse<String> confirmRes = sendRequest("/addMovie", "POST", confirmReq.toString());
        assertEquals(200, confirmRes.statusCode());
    }

    /**
     * This method tests addMovie in a failing environment
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void addMovieFail() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject().put("name", "Parasite");
        HttpResponse<String> confirmRes = sendRequest("/addMovie", "POST", confirmReq.toString());
        assertEquals(400, confirmRes.statusCode());
    }

    /**
     * This method tests addRelationship in a passing environment
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void addRelationshipPass() throws JSONException, IOException, InterruptedException {
        // Adding actor, movie, and the relationship to the database
        neo4j.addActor("Denzel Washington", "nm1001213");
        neo4j.addMovie("Parasite", "nm7001453");

        JSONObject confirmReq = new JSONObject().put("actorId", "nm1001213").put("movieId", "nm7001453");
        HttpResponse<String> confirmRes = sendRequest("/addRelationship",
                "POST", confirmReq.toString());
        assertEquals(200, confirmRes.statusCode());
    }

    /**
     * This method tests addRelationship in a failing environment
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void addRelationshipFail() throws JSONException, IOException, InterruptedException {
        // Adding actor, movie, and relationship to the database
        neo4j.addActor("Denzel Washington", "nm1001213");
        neo4j.addMovie("Parasite", "nm7001453");

        JSONObject confirmReq = new JSONObject()
                .put("actorId", "1")
                .put("movieId", "2");
        HttpResponse<String> confirmRes = sendRequest("/addRelationship",
                "POST", confirmReq.toString());
        assertEquals(400, confirmRes.statusCode());
    }


    /**
     * This method tests getActor in a passing environment
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void getActorPass() throws JSONException, IOException, InterruptedException{
        // Adding actor to the database
        neo4j.addActor("Denzel Washington", "nm1001213");

        JSONObject confirmReq = new JSONObject()
                .put("actorId", "nm1001213");
        HttpResponse<String> confirmRes = sendRequest("/getActor", "GET", confirmReq.toString());
        assertEquals(200, confirmRes.statusCode());
    }

    /**
     * This method tests getActor in a failing environment
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void getActorFail() throws JSONException, IOException, InterruptedException{
        // Adding actor to the database
        neo4j.addActor("Denzel Washington", "nm1001213");

        JSONObject confirmReq = new JSONObject()
                .put("actorId", "1");
        HttpResponse<String> confirmRes = sendRequest("/getActor", "GET", confirmReq.toString());
        assertEquals(404, confirmRes.statusCode());
    }

    /**
     * This method tests getMovie in a passing environment
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void getMoviePass() throws JSONException, IOException, InterruptedException{
        // Adding movie to the database
        neo4j.addMovie("Parasite", "nm7001453");

        JSONObject confirmReq = new JSONObject()
                .put("movieId", "nm7001453");
        HttpResponse<String> confirmRes = sendRequest("/getMovie", "GET", confirmReq.toString());
        assertEquals(200, confirmRes.statusCode());
    }

    /**
     * This method tests getMovie in a failing environment
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void getMovieFail() throws JSONException, IOException, InterruptedException{
        // Adding movie to the database
        neo4j.addMovie("Parasite", "nm7001453");

        JSONObject confirmReq = new JSONObject()
                .put("movieId", "1");
        HttpResponse<String> confirmRes = sendRequest("/getMovie", "GET", confirmReq.toString());
        assertEquals(404, confirmRes.statusCode());
    }

    /**
     * This method tests hasRelationship in a passing environment
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void hasRelationshipPass() throws JSONException, IOException, InterruptedException{
        // Adding actor, movie, and relationship to the database
        neo4j.addActor("Denzel Washington", "nm1001213");
        neo4j.addMovie("Parasite", "nm7001453");
        neo4j.addRelationship("nm1001213", "nm7001453");


        JSONObject confirmReq = new JSONObject()
                .put("actorId", "nm1001213")
                .put("movieId", "nm7001453");
        HttpResponse<String> confirmRes = sendRequest("/hasRelationship", "GET", confirmReq.toString());
        assertEquals(200, confirmRes.statusCode());
    }

    /**
     * This method tests hasRelationship in a failing environment
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void hasRelationshipFail() throws JSONException, IOException, InterruptedException{
        // Adding actor, movie, and relationship to the database
        neo4j.addActor("Denzel Washington", "nm1001213");
        neo4j.addMovie("Parasite", "nm7001453");

        JSONObject confirmReq = new JSONObject()
                .put("actorId", "nm1001213")
                .put("movieId", "nm7001453");
        HttpResponse<String> confirmRes = sendRequest("/hasRelationship", "GET", confirmReq.toString());
        assertEquals(404, confirmRes.statusCode());
    }


    /**
     * This method tests computerBaconNumber in a passing environment
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void computeBaconNumberPass() throws JSONException, IOException, InterruptedException{
        // Adding actor, movie, and relationship to the database
        neo4j.addActor("Denzel Washington", "nm1001213");
        neo4j.addActor("Kevin Bacon", "nm0000102");
        neo4j.addActor("Chris Evans", "nm1873921");
        neo4j.addMovie("Parasite", "nm7001453");
        neo4j.addMovie("Captain America", "nm7001454");
        neo4j.addRelationship("nm0000102", "nm7001453");
        neo4j.addRelationship("nm1001213", "nm7001453");
        neo4j.addRelationship("nm1001213", "nm7001454");
        neo4j.addRelationship("nm1873921", "nm7001454");

        JSONObject confirmReq = new JSONObject()
                .put("actorId", "nm1873921");
        HttpResponse<String> confirmRes = sendRequest("/computeBaconNumber",
                "GET", confirmReq.toString());
        assertEquals(200, confirmRes.statusCode());
    }

    /**
     * This method tests computeBaconNumber in a failing environment
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void computeBaconNumberFail() throws JSONException, IOException, InterruptedException{
        // Adding actor, movie, and relationship to the database
        neo4j.addActor("Denzel Washington", "nm1001213");
        neo4j.addActor("Kevin Bacon", "nm0000102");
        neo4j.addActor("Chris Evans", "nm1873921");
        neo4j.addMovie("Parasite", "nm7001453");
        neo4j.addMovie("Captain America", "nm7001454");
        neo4j.addRelationship("nm0000102", "nm7001453");
        neo4j.addRelationship("nm1001213", "nm7001453");
        neo4j.addRelationship("nm1001213", "nm7001454");
        neo4j.addRelationship("nm1873921", "nm7001454");

        JSONObject confirmReq = new JSONObject()
                .put("actorId", "nm1893921");
        HttpResponse<String> confirmRes = sendRequest("/computeBaconNumber", "GET", confirmReq.toString());
        assertEquals(404, confirmRes.statusCode());
    }

    /**
     * This method tests computeBaconPath in a passing envrionment
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void computeBaconPathPass() throws JSONException, IOException, InterruptedException{
        // Adding actor, movie, and relationship to the database
        neo4j.addActor("Denzel Washington", "nm1001213");
        neo4j.addActor("Kevin Bacon", "nm0000102");
        neo4j.addActor("Chris Evans", "nm1873921");
        neo4j.addMovie("Parasite", "nm7001453");
        neo4j.addMovie("Captain America", "nm7001454");
        neo4j.addRelationship("nm0000102", "nm7001453");
        neo4j.addRelationship("nm1001213", "nm7001453");
        neo4j.addRelationship("nm1001213", "nm7001454");
        neo4j.addRelationship("nm1873921", "nm7001454");

        JSONObject confirmReq = new JSONObject()
                .put("actorId", "nm1873921");
        HttpResponse<String> confirmRes = sendRequest("/computeBaconPath",
                "GET", confirmReq.toString());
        assertEquals(200, confirmRes.statusCode());
    }

    /**
     * This method tests computeBaconPath in a failing environment
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void computeBaconPathFail() throws JSONException, IOException, InterruptedException{
        // Adding actor, movie, and relationship to the database
        neo4j.addActor("Denzel Washington", "nm1001213");
        neo4j.addActor("Kevin Bacon", "nm0000102");
        neo4j.addActor("Chris Evans", "nm1873921");
        neo4j.addMovie("Parasite", "nm7001453");
        neo4j.addMovie("Captain America", "nm7001454");
        neo4j.addRelationship("nm0000102", "nm7001453");
        neo4j.addRelationship("nm1001213", "nm7001453");
        neo4j.addRelationship("nm1001213", "nm7001454");

        // No relationship between Chris Evans and any movie

        JSONObject confirmReq = new JSONObject()
                .put("actorId", "nm1873921");
        HttpResponse<String> confirmRes = sendRequest("/computeBaconPath",
                "GET", confirmReq.toString());
        assertEquals(404, confirmRes.statusCode());
    }

}
