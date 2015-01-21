/*
 * * Copyright (C) 2013-2014 Matt Baxter http://kitteh.org
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.kitteh.irc.client.library;

import org.kitteh.irc.client.library.util.Consumer;
import org.kitteh.irc.client.library.util.QueueProcessingThread;

import java.util.Queue;

final class Listener<Type> {
    private class ListenerThread extends QueueProcessingThread<Type> {
        private final Consumer<Type> consumer;

        private ListenerThread(String clientName, Consumer<Type> consumer) {
            super("Kitteh IRC Client Listener (" + clientName + ")");
            this.consumer = consumer;
        }

        @Override
        protected void processElement(Type element) {
            try {
                this.consumer.accept(element);
            } catch (final Throwable thrown) {
                // NOOP
            }
        }

        @Override
        protected void cleanup(Queue<Type> remainingQueue) {
            while (!remainingQueue.isEmpty()) {
                try {
                    this.consumer.accept(remainingQueue.poll());
                } catch (final Throwable thrown) {
                    // NOOP
                }
            }
        }
    }

    private final ListenerThread thread;

    Listener(String clientName, Consumer<Type> consumer) {
        this.thread = consumer == null ? null : new ListenerThread(clientName, consumer);
    }

    void queue(Type item) {
        if (this.thread != null) {
            this.thread.queue(item);
        }
    }

    void shutdown() {
        if (this.thread != null) {
            this.thread.interrupt();
        }
    }
}