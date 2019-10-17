package com.signity.ziprecruiterproj;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.signity.ziprecruiterproj.configs.CommandArguments;
import com.signity.ziprecruiterproj.services.ReportGenerator;

@SpringBootApplication
public class ZiprecruiterprojApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ZiprecruiterprojApplication.class, args);
	}

	@Autowired
	private ReportGenerator reportGenerator;

	@Override
	public void run(String... args) throws Exception {
		init();
		argumentHandler(args);
		System.out.println(CommandArguments.jobSourceName);
		reportGenerator.generateReport();
	}

	/**
	 * Initialize initial Requirements
	 */
	private void init() {
		File parseDataFolder = new File("parseData");
		if (!parseDataFolder.exists()) {
			parseDataFolder.mkdir();
		}

		File jsonDir = new File("parseData/json");
		if (!jsonDir.exists()) {
			jsonDir.mkdir();
		}

		File redshiftDir = new File("parseData/redshift");
		if (!redshiftDir.exists()) {
			redshiftDir.mkdir();
		}
	}

	/**
	 * Handle Command Line Argument
	 *
	 * @param args
	 *            arg array
	 */
	private void argumentHandler(String[] args) {
		Options options = new Options();

		Option jobSource = new Option("js", "jobSource", true, "Job Source Name");
		jobSource.setRequired(true);
		options.addOption(jobSource);

		Option startDay = new Option("sd", "startDay", true, "Start Day of Month");
		startDay.setRequired(true);
		options.addOption(startDay);

		Option output = new Option("ed", "endDay", true, "End Day of Month");
		output.setRequired(true);
		options.addOption(output);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("utility-name", options);
			System.exit(1);
		}

		if (cmd != null) {
			CommandArguments.jobSourceName = cmd.getOptionValue("jobSource");

			if (CommandArguments.jobSourceName == null || CommandArguments.jobSourceName.equals("")) {
				throw new RuntimeException("jobSource arg is required");
			}

			try {
				CommandArguments.startDate = Integer.parseInt(cmd.getOptionValue("startDay"));
				if(CommandArguments.startDate<0){
					throw new RuntimeException("StartDate must not be -ve number");
				}
			} catch (NumberFormatException e) {
				System.out.println("startDay must be an Integer value");
				e.printStackTrace();
				System.exit(1);
			}

			try {
				CommandArguments.endDate = Integer.parseInt(cmd.getOptionValue("endDay"));
			} catch (NumberFormatException e) {
				System.out.println("endDay must be an Integer value");
				e.printStackTrace();
				System.exit(1);
			}
		} else
			System.out.println("Command Line Argument Null");

	}
}
