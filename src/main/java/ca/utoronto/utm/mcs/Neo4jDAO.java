package ca.utoronto.utm.mcs;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* This class deals with the Neo4j database. It talks to the database and gets/posts all required information
* @version 1.0
* @author Armand Sarkezians
*/
public class Neo4jDAO {
    //Session and Driver variables to create a connection with the database
    private final Session session;
    private final Driver driver;

    //Database URL and authentication, as per A1 requirements
    private final String uriDb = "bolt://localhost:7687";
    private final String username = "neo4j";
    private final String password = "123456";

    /**
    * Constructor, creates the driver for the database using authentication variables posted above, and creates a session
    * based on the driver.
    */
    @Inject
    public Neo4jDAO(Driver driver){
        this.driver = driver;
        this.session = this.driver.session();
    }

    public void cleanDatabase(){
        this.session.run("MATCH (n) DETACH DELETE n");
    }

    /**
    * This method adds an actor to the database if not already there
    * @param name the name of the actor being put in the database
    * @param actorId the id of the actor being put in the database
    * @return    to check if the actor already existed (for 200 or 400 code)
    */
    public Boolean addActor(String name, String actorId){
        // To make sure the actor is not already in the system
        if(getActor(actorId).equals("Not Found")) {
            String query;
            query = "CREATE (a:actor {name:\"%s\", id:\"%s\"})";
            query = String.format(query, name, actorId);
            this.session.run(query);
            return true;
        }
        return false;
    }

    /**
    * This method adds a movie to the database if not already there
    * @param name the name of the movie being put in the database
    * @param movieId the id of the movie being put in the database
    * @return   to check if the movie already existed (for 200 or 400 code)
    */
    public Boolean addMovie(String name, String movieId){
        // To make sure the movie is not already in the system
        if(getMovie(movieId).equals("Not Found")) {

            //Create the text needed to be sent to the database
            String query;
            query = "CREATE (m:movie {name:\"%s\", id:\"%s\"})";
            query = String.format(query, name, movieId);

            //Send the command to the database
            this.session.run(query);

            // Movie was not in database
            return true;
        }
        // Movie was already in database
        return false;
    }

    /**
    * This method adds a relationship between an already existing actor and an already existing movie
    * @param actorId the id of the actor added to the relationship
    * @param movieId the id of the movie added to the relationship
    * @return   to check if the relationship already existed(for 200 or 400 code)
    */
    public Boolean addRelationship(String actorId, String movieId){
        // In the case that the movie or actor does not exist
        if(getMovie(movieId).equals("Not Found") || getActor(actorId).equals("Not Found")){
            return false;
        }

        // Ensure that the relationship didn't previously exist
        if(hasRelationship(actorId, movieId).equals("Not Found") || hasRelationship(actorId, movieId).equals("FALSE")){

            //Create the text needed to be sent to the database
            String query;
            query = "MATCH (a:actor), (m: movie)" +
                    "WHERE a.id=\"%s\" AND m.id=\"%s\"" +
                    "CREATE (a)-[r:ACTED_IN]->(m)";
            query = String.format(query, actorId, movieId);

            // Send query to database
            this.session.run(query);

            //Relationship did not exist and was added
            return true;
        }
        //Relationship already existed and was not added
        return false;
    }

    /**
    * This method gets the information of an actor, given their actorId
    * @param actorId the id of the actor for which information is wanted
    * @return   the information of the actor, if found
    */
    public String getActor(String actorId){
        //Create the text needed to be sent to the database
        String query;
        query = "MATCH (a:actor)" +
                "WHERE a.id=\"%s\"" +
                "RETURN a";
        query = String.format(query, actorId);

        //Sending information to database and taking result
        Result node = this.session.run(query);

        // Checking if node has correct information, if so, storing it in a map and returning it
        if(node.hasNext()){
            Record record = node.next();
            Map<String, Object> actorMap = record.get("a").asMap();
            return actorMap.toString();
        }
        //Actor was not found
        return "Not Found";
    }

    /**
    * This method gets the information of a movie, given their movieId
    * @param movieId the id of the movie for which information is wanted
    * @return   the information of the movie, if found
    */
    public String getMovie(String movieId){
        //Create the text needed to be sent to the database
        String query;
        query = "MATCH (m:movie)" +
                "WHERE m.id=\"%s\"" +
                "RETURN m";
        query = String.format(query, movieId);

        //Sending information to database and taking result
        Result node = this.session.run(query);

        // Checking if node has correct information, if so, storing it in a map and returning it
        if(node.hasNext()){
            Record record = node.next();
            Map<String, Object> movieMap = record.get("m").asMap();
            return movieMap.toString();
        }
        //Movie was not found
        return "Not Found";
    }

    /**
    * This method checks to see if there is a relationship between a movie and an actor
    * @param movieId the id of the movie being checked for a relationship
    * @param actorId the id of the actor being checked for a relationship
    * @return   true or false, depending on whether a relationship exists
    */
    public String hasRelationship(String actorId, String movieId){
        // Create the text needed to be sent to the database
        String query;
        query = "RETURN EXISTS((:actor {id:\"%s\"})-[:ACTED_IN]-(:movie {id:\"%s\"}))";
        query = String.format(query, actorId, movieId);

        // Sending information to database and taking result
        Result node = this.session.run(query);

        // Checking if node has correct information, if so, storing it in a map and returning it
        if(node.hasNext()){
            Record record = node.next();
            Value relationship = record.get
                    ("EXISTS((:actor {id:\"" + actorId +
                            "\"})-[:ACTED_IN]-(:movie {id:\"" + movieId + "\"}))");
            return relationship.toString();
        }
        //Relationship not found
        return "Not Found";
    }

    /**
     * This method determines the Bacon number of any actor
     * @param actorId the actor that needs a connection to kevin bacon
     * @return
     */
    public int computeBaconNumber(String actorId){
        if (actorId.equals("nm0000102")){
            return 0; // Kevin Bacon's actor ID
        }else if(getActor(actorId).equals("Not Found")){
            return -1;
        }
        ArrayList<String> baconPath = computeBaconPath(actorId);
        if(baconPath != null){
            return (int)Math.floor(baconPath.size()/2.0);
        }else{
            return -1;
        }
    }

    /**
     * This method determines the Bacon path of any actor
     * @param actorId
     */
    public ArrayList<String> computeBaconPath(String actorId){
        ArrayList<String> baconPath = new ArrayList<>();

        // In case actor does not exist
        if(getActor(actorId).equals("Not Found")) return null;

        // Incase actorId is Kevin Bacon
        if (actorId.equals("nm0000102")){
            baconPath.add("nm0000102");
            return baconPath;
        }

        String query;
        query = "MATCH (k:actor {id:\"nm0000102\"})," +
                "(a:actor {id:\"%s\"})," +
                "p = shortestPath((k)-[:ACTED_IN*]-(a))" +
                "RETURN p";
        query = String.format(query, actorId);
        Result record = this.session.run(query);
        Record rec = null;

        // Check for edge cases
        if(!record.hasNext()){
            return null;
        }

        rec = record.next();

        // Looping through nodes to get path
        for(Node node : rec.get("p").asPath().nodes()){
            baconPath.add(node.get("id").asString());
        }


        return baconPath;
    }
}
