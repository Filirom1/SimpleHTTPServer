/**
 *  Copyright 2006-2012 Michael Vorburger (http://www.vorburger.ch)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/*******************************************************************************
 * Copyright (c) 2006-2012 Michael Vorburger (http://www.vorburger.ch).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.simpleHTTPServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A Server that allows multiple concurrent Worker threads.
 * This implementation is based on the JDK 5.0 java.util.concurrent.* package.
 * @author vorburger
 */
class ServerMultiThreadedWorkers extends ServerSingleThreadedWorker {

    private final ExecutorService pool;

    /**
     * Constructor
     *
     * @param port TCP/IP port number that the server will listen on
     * @param timeout Timeout in miliseconds that the server will wait for a client to send the full request
     * @param threads Maximum number of threads
     */
    public ServerMultiThreadedWorkers(int port, int timeout, int threads, RequestHandlerFactory requestHandlerFactory) {
        super(port, timeout, requestHandlerFactory);
        pool = Executors.newFixedThreadPool(threads);
    }

    @Override
    protected void handle(RequestHandler handler) {
        assert handler != null;
        pool.execute(handler);
    }

    @Override
    public void terminate() {
        System.out.println("Is stopping HTTP Server");
        super.terminate();
        pool.shutdownNow();
        try {
            pool.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // Fine, whatever; main thing is that we stopped.
        }
        assert pool.isTerminated();
        assert pool.isShutdown();
    }
}
