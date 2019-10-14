package com.signity.ziprecruiterproj.LibConfigs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Libs {

    @Bean
    public Gson gosn(){
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }
}
