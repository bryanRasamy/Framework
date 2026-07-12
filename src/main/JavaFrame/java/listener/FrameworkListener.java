package main.JavaFrame.java.listener;

import java.lang.annotation.ElementType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebListener;
import main.JavaFrame.java.modele.*;
import main.JavaFrame.java.service.*;
import main.JavaFrame.web.annotations.controller.*;

@WebListener
public class FrameworkListener implements ServletContextListener{
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Map<UrlMethod,RouteMapping> listeUrl = new HashMap<>();

        ServletContext context = sce.getServletContext();

        String namePackage = context.getInitParameter("package_controller");
        String prefix = context.getInitParameter("prefix");
        String suffix = context.getInitParameter("suffix");
    
        try {
            List<Class> listeClasse = Utilitaire.getNameClasse(namePackage, Controller.class, ElementType.TYPE);

            Utilitaire.getListURL(listeClasse,listeUrl);

            sce.getServletContext().setAttribute("listeUrl", listeUrl);
            sce.getServletContext().setAttribute("prefix", prefix);
            sce.getServletContext().setAttribute("suffix", suffix);
        } catch (Exception e) {
            sce.getServletContext().setAttribute("exception", e);
        }
        
    }
}
