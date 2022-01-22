package ca.utoronto.utm.mcs;

import dagger.Component;
import javax.inject.Singleton;

/**
 * This is the interface for the Server class
 */
@Singleton
@Component(modules = ReqHandlerModule.class)
public interface ReqHandlerComponent {
    public ReqHandler buildHandler();
}
