package main.JavaFrame.java.controller;

import java.io.*;
import java.lang.annotation.ElementType;
import java.util.List;
import java.util.Map;

import main.JavaFrame.java.service.*;
import main.JavaFrame.java.modele.*;
import main.JavaFrame.web.annotations.controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class FrontControllerServlet extends HttpServlet {
    private Map<String,RouteMapping> listeUrl;
    private Exception exception;

    public void init() throws ServletException {
        String namePackage = this.getInitParameter("package_controller");
    
        try {
            List<Class> listeClasse = Utilitaire.getNameClasse(namePackage, Controller.class, ElementType.TYPE);

            listeUrl = Utilitaire.getListURL(listeClasse);
        } catch (Exception e) {
            exception=e;
        }
        
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String url = req.getRequestURL().toString();

        String actionPath = req.getPathInfo().toString();

        if (actionPath == null) {
            actionPath = "/";
        }

        res.setContentType("text/plain");

        PrintWriter out = res.getWriter();
        out.println("Vous etes arriver dans cette url");
        out.println(actionPath);

        if (exception!=null) {
            out.println(exception.getMessage());
        }

        RouteMapping classeMethode = listeUrl.get(actionPath);

        if (classeMethode!=null) {
            out.println(actionPath);
            out.println(classeMethode.getNameClasse());
            out.println(classeMethode.getNameMethode());
        }else{
            out.println("Url:"+actionPath+" non trouver");
            out.println("Liste des url disponibles dans cette framework");

            for (Map.Entry<String,RouteMapping> urlRoute : listeUrl.entrySet()) {
                String urlClasse = urlRoute.getKey();
                RouteMapping route = urlRoute.getValue();

                out.println(urlClasse);
                out.println(route.getNameClasse());
                out.println(route.getNameMethode());
            }
        }

        

    }
}



