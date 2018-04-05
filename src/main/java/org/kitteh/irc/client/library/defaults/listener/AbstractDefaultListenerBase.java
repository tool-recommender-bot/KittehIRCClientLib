package org.kitteh.irc.client.library.defaults.listener;

import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.event.helper.ClientEvent;
import org.kitteh.irc.client.library.event.helper.ClientReceiveServerMessageEvent;
import org.kitteh.irc.client.library.exception.KittehServerMessageException;
import org.kitteh.irc.client.library.feature.ActorTracker;
import org.kitteh.irc.client.library.feature.EventManager;
import org.kitteh.irc.client.library.util.ToStringer;

import javax.annotation.Nonnull;

/**
 * A base for listening to server message events.
 */
public class AbstractDefaultListenerBase {
    private final Client.WithManagement client;

    /**
     * Constructs the listener.
     *
     * @param client client
     */
    public AbstractDefaultListenerBase(@Nonnull Client.WithManagement client) {
        this.client = client;
    }

    @Nonnull
    @Override
    public String toString() {
        return new ToStringer(this).toString();
    }

    @Nonnull
    protected Client.WithManagement getClient() {
        return this.client;
    }

    /**
     * Fires an event. Convenience method.
     *
     * @param event event to fire
     * @see EventManager#callEvent(Object)
     */
    protected void fire(@Nonnull ClientEvent event) {
        this.client.getEventManager().callEvent(event);
    }

    /**
     * Fires an exception in processing a server message event.
     *
     * @param event event causing trouble
     * @param reason reason for the trouble
     */
    protected void trackException(@Nonnull ClientReceiveServerMessageEvent event, @Nonnull String reason) {
        this.client.getExceptionListener().queue(new KittehServerMessageException(event.getServerMessage(), reason));
    }

    /**
     * Gets the actor tracker. Convenience method.
     *
     * @return actor tracker
     * @see Client.WithManagement#getActorTracker()
     */
    @Nonnull
    protected ActorTracker getTracker() {
        return this.client.getActorTracker();
    }
}