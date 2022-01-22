package ca.utoronto.utm.mcs;

import dagger.Module;
import dagger.Provides;
import io.github.cdimascio.dotenv.Dotenv;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

/**
 * This class creates the module for the ReqHandle class
 * @version 1.0
 * @author Armand Sarkezians
 */
@Module
public class ReqHandlerModule {
    private final String user = "neo4j";
    private final String password = "123456";

    @Provides
    public Neo4jDAO provideNeo4jDAO() {
        Dotenv env = Dotenv.load();
        String uri = env.get("NEO4J_ADDR");
        uri = "bolt://" + uri + ":7687";

        Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
        return new Neo4jDAO(driver);
    }
}
