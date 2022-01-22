package ca.utoronto.utm.mcs;

import dagger.Module;
import dagger.Provides;

/**
 * This class creates the module for the Server class
 * @version 1.0
 * @author Armand Sarkezians
 */
@Module
public class ServerModule {

    /**
     * This method creates a new Server object
     * @return the new Server object
     */
    @Provides
    public Server provideServer(){
        return new Server();
    }
}
