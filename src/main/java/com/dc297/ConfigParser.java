package com.dc297;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.helper.HelpScreenException;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;


public class ConfigParser {
    public static ServerConfig parse(String[] args){
        ArgumentParser parser = initParser();
        try {
            Namespace res = parser.parseArgs(args);
            System.out.println(res);
            return new ServerConfig(
                    res.getInt("port"),
                    res.getInt("max_threads"),
                    res.getString("base_directory"));

        }
        catch(HelpScreenException ex){
            System.exit(0);
        }
        catch (ArgumentParserException e) {
            parser.handleError(e);
            e.printStackTrace();
            System.out.println("Using default config");
            return new ServerConfig(56923, 8, ".");
        }

        return null;
    }

    private static ArgumentParser initParser(){
        ArgumentParser parser = ArgumentParsers.newFor("Main").build()
                .defaultHelp(true)
                .description("Start a web server.");

        parser.addArgument("-p", "--port")
                .setDefault(56923)
                .help("Specify port number to use")
                .type(Integer.class);

        parser.addArgument("-d", "--base-directory")
                .setDefault(".")
                .help("Base directory to be used for serving the files");

        parser.addArgument("-n", "--max-threads")
                .setDefault(8)
                .help("Maximum number of threads to use")
                .type(Integer.class);

        return parser;
    }
}
