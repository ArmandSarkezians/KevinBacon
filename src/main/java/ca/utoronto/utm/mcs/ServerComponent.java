package ca.utoronto.utm.mcs;

import dagger.Component;
import javax.inject.Singleton;

/**
 * This is the interface for the Server class
 */
@Singleton
@Component(modules = ServerModule.class)
public interface ServerComponent {
	public Server buildServer();
}
