package com.yeahmobi.lab;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerStartup {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerStartup.class);

    private static CommandLine parseArguments(String[] args) {
        Options options = new Options();

        Option portOption = new Option("p", "port", true, "Port to listen");
        options.addOption(portOption);

        Option backlogOption = new Option("b", "backlog", true, "backlog number");
        options.addOption(backlogOption);

        CommandLineParser parser = new DefaultParser();
        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            LOGGER.error("Failed to parse command line arguments");
            return null;
        }
    }

    public static void main(String[] args) {

        CommandLine commandLine = parseArguments(args);
        if (null == commandLine) {
            return;
        }

        ServerConfig serverConfig = new ServerConfig();

        if (commandLine.hasOption('p')) {
            int port = Integer.parseInt(commandLine.getOptionValue('p'));
            serverConfig.setPort(port);
        } else {
            serverConfig.setPort(1234);
        }

        if (commandLine.hasOption('b')) {
            int backlog = Integer.parseInt(commandLine.getOptionValue('b'));
            serverConfig.setBacklog(backlog);
        } else {
            serverConfig.setBacklog(1024);
        }


        ServerController serverController = new ServerController(serverConfig);
        serverController.start();
    }


}
