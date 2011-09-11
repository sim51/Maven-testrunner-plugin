/**
 *  This file is part of LogiSima (http://www.logisima.com).
 *
 *  maven-testrunner-plugin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  maven-testrunner-plugin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with maven-testrunner-plugin. If not, see <http://www.gnu.org/licenses/>.
 *  
 *  @author Benoît Simard
 *  @See https://github.com/sim51/maven-testrunner-plugin
 */
package com.logisima.selenium.server;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class NettyServer extends Thread {

    private Integer         port;
    private Channel         channel;
    private ServerBootstrap bootstrap;
    private File            documentRoot;
    private URL             baseApplicationUrl;
    private File            testSourceDirectory;
    private File            outputDirectory;

    public NettyServer(Integer port, String documentRoot, URL baseApplicationUrl, String testSourceDirectory,
            String outputDirectory) {
        super();
        this.port = port;
        this.documentRoot = new File(documentRoot);
        this.baseApplicationUrl = baseApplicationUrl;
        this.testSourceDirectory = new File(testSourceDirectory);
        this.outputDirectory = new File(outputDirectory);
    }

    public void run() {
        // create the boostrap server
        this.bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool(), Runtime.getRuntime().availableProcessors() * 2 + 1));

        // Set up the event pipeline factory.
        this.bootstrap.setPipelineFactory(new HttpServerPipelineFactory(documentRoot, baseApplicationUrl,
                testSourceDirectory, outputDirectory));

        // set options
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.setOption("child.reuseAddress", true);
        bootstrap.setOption("child.connectTimeoutMillis", 100);
        bootstrap.setOption("readWriteFair", true);

        // Bind and start to accept incoming connections.
        this.channel = this.bootstrap.bind(new InetSocketAddress(port));
    }

}