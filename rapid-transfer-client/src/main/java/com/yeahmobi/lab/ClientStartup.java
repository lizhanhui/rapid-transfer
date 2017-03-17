package com.yeahmobi.lab;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ClientStartup {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientStartup.class);

    private static CommandLine parse(String[] args) {
        Options options = new Options();
        Option option = new Option("i", "ip", true, "Remote IP address");
        options.addOption(option);

        option = new Option("p", "port", true, "Port");
        options.addOption(option);

        option = new Option("f", "file", true, "File[Directory] to transfer");
        options.addOption(option);

        CommandLineParser parser = new DefaultParser();
        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            LOGGER.error("Failed to parse command line arguments", e);
        }

        return null;
    }

    public static void main(String[] args) {

        CommandLine commandLine = parse(args);

        if (null == commandLine) {
            return;
        }

        ClientConfig clientConfig = new ClientConfig();
        if (commandLine.hasOption("i")) {
            clientConfig.setIp(commandLine.getOptionValue("i"));
        } else {
            clientConfig.setIp("localhost");
        }

        if (commandLine.hasOption("p")) {
            clientConfig.setPort(Integer.parseInt(commandLine.getOptionValue("p")));
        } else {
            clientConfig.setPort(1234);
        }

        if (commandLine.hasOption("d")) {
            File file = new File(commandLine.getOptionValue("d"));
            if (file.exists() && file.canRead()) {
                clientConfig.setFile(file);
            }
        } else {
            LOGGER.error("-f option is required");
            return;
        }

        if (null == clientConfig.getFile()) {
            LOGGER.error("File: {} does not exist or cannot read", commandLine.getOptionValue("f"));
            return;
        }

        ClientController clientController = new ClientController(clientConfig);
        clientController.start();
    }

}
