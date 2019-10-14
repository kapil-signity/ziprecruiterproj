package com.signity.ziprecruiterproj;

import com.signity.ziprecruiterproj.configs.CommandArguments;
import com.signity.ziprecruiterproj.services.ReportGenerator;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZiprecruiterprojApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ZiprecruiterprojApplication.class, args);
    }
    @Autowired
    private ReportGenerator reportGenerator;

    @Override
    public void run(String... args) throws Exception {
        argumentHandler(args);
        System.out.println(CommandArguments.jobSourceName);
        reportGenerator.generateReport();
        while(true){}
       //
    }


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

        CommandArguments.jobSourceName = cmd.getOptionValue("jobSource");

        if(CommandArguments.jobSourceName==null || CommandArguments.jobSourceName.equals("")){

        }
        try {
            CommandArguments.startDate = Integer.parseInt(cmd.getOptionValue("startDay"));
        } catch (NumberFormatException e) {
            System.out.println("startDay must be an Integer value");
            e.printStackTrace();
        }

        try {
            CommandArguments.endDate = Integer.parseInt(cmd.getOptionValue("endDay"));
        } catch (NumberFormatException e) {
            System.out.println("endDay must be an Integer value");
            e.printStackTrace();
        }
    }
}
