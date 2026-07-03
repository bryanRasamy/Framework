package main.JavaFrame.java.controller;

import java.io.*;
import java.lang.annotation.ElementType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.JavaFrame.java.service.*;
import main.JavaFrame.java.modele.*;
import main.JavaFrame.web.annotations.controller.*;
import main.JavaFrame.java.listener.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class FrontControllerServlet extends HttpServlet {
    private Map<UrlMethod,RouteMapping> listeUrl;
    private Exception exception;

    public void init() throws ServletException {
        this.listeUrl = (Map<UrlMethod, RouteMapping>) this.getServletContext().getAttribute("listeUrl");
        this.exception = (Exception) this.getServletContext().getAttribute("exception");
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
            return;
        }

        UrlMethod urlMethod = new UrlMethod();
        urlMethod.setUrl(actionPath);
        urlMethod.setMethod("get");

        RouteMapping classeMethode = listeUrl.get(urlMethod);

        if (classeMethode!=null) {
            out.println(actionPath);
            out.println(classeMethode.getClasse().getSimpleName());
            out.println(classeMethode.getMethode().getName());
            out.println(urlMethod.getMethod());

            try {
                Utilitaire.callMethod(classeMethode);
            } catch (Exception e) {
                out.println(e.getMessage());
                return;
            }
        }else{
            out.println("Url:"+actionPath+" non trouver");
            out.println("Liste des url disponibles dans cette framework");

            for (Map.Entry<UrlMethod,RouteMapping> urlRoute : listeUrl.entrySet()) {
                try {
                    UrlMethod urlClasse = urlRoute.getKey();
                    RouteMapping route = urlRoute.getValue();

                    out.println(urlClasse.getUrl());
                    out.println(urlClasse.getMethod());
                    out.println(route.getClasse().getSimpleName());
                    out.println(route.getMethode().getName());
                } catch (Exception e) {
                    out.println(e.getMessage());
                }
            }
        }
    }
}



