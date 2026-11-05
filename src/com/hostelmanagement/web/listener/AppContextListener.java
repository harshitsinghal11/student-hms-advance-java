package com.hostelmanagement.web.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Application startup logic: initialize global context, loggers, or pool
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Application shutdown logic: cleanup resources, close pools
    }
}