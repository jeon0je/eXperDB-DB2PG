package com.k4m.experdb.db2pg.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.k4m.experdb.db2pg.common.DevUtils;
import com.k4m.experdb.db2pg.common.LogUtils;
import com.k4m.experdb.db2pg.rebuild.RebuildSummary;
import com.k4m.experdb.db2pg.sample.SampleFileLoader;
import com.k4m.experdb.db2pg.unload.UnloadSummary;


public class ArgsParser {
	Options options;
	CommandLineParser parser;
	HelpFormatter formatter;
	CommandLine cmd;
	
	public ArgsParser() {
		options = new Options();
		parser = new DefaultParser();
		formatter = new HelpFormatter();
		init();
	}
	
	private void init() {
		Option option = null;
		option = new Option("c", "config",true, "config file path");
		option.setRequired(false);
		options.addOption(option);
		option = new Option("M", "make-templates",false, "make template files");
		option.setRequired(false);
		options.addOption(option);
		option = new Option(null, "rebuild-summary",true, "rebuild log files summary");
		option.setRequired(false);
		options.addOption(option);
		option = new Option(null, "unload-summary",true, "unload log file summary");
		option.setRequired(false);
		options.addOption(option);
	}
	
	public void parse (String[] args) {
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			LogUtils.error(e.getMessage(),ArgsParser.class);
			System.out.println(e.getMessage());
			formatter.printHelp("eXperDB-DB2PG", options);
			System.exit(-1);
		}
		boolean summarized = false;
		if(cmd.hasOption("make-templates")) {
			try {
				InputStream is = SampleFileLoader.getResourceInputStream("com/k4m/experdb/db2pg/sample/db2pg.config");
				OutputStream out = new FileOutputStream(new File("db2pg.config"));
				
				int r = -1;
				while((r=is.read()) != -1) {
					out.write(r);
				}
				
				is.close();
				out.close();
				
				is = SampleFileLoader.getResourceInputStream("com/k4m/experdb/db2pg/sample/queries.xml");
				out = new FileOutputStream(new File("queries.xml"));
				
				r = -1;
				while((r=is.read()) != -1) {
					out.write(r);
				}
				
				is.close();
				out.close();
				
			} catch (Exception e) {
				LogUtils.error(e.getMessage(),ArgsParser.class);
				System.out.println(e.getMessage());
			}
			System.exit(0);
		} 
		if(cmd.hasOption("rebuild-summary")) {
			String[] rebuildLogs = cmd.getOptionValue("rebuild-summary").split(",");
			
			
			for(int i=0;i<rebuildLogs.length;i++) {
				rebuildLogs[i] = rebuildLogs[i].trim();
			}
			(new RebuildSummary(".",rebuildLogs)).run();
			summarized = true;
		}
		if(cmd.hasOption("unload-summary")) {
			String unloadLog = cmd.getOptionValue("unload-summary");
			(new UnloadSummary(".",unloadLog)).run();
			summarized = true;
		}
		
		if(summarized) {
			System.exit(0);
		}
		if(cmd.hasOption("config")) {
			ConfigInfo.Loader.load(cmd.getOptionValue("config"));
		} else {
			System.out.println("Enter the config file path");
			formatter.printHelp("DB2PG", options);
			System.exit(-1);
			return;
		}
	}
	
//	public static void main(String[] args) throws Exception {
//		(new ArgsParser()).parse(args);
//	}

}