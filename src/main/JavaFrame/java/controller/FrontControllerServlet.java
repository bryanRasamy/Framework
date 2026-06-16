package main.JavaFrame.java.controller;

import java.io.*;
import java.lang.annotation.ElementType;
import java.util.List;
import main.JavaFrame.java.service.*;
import main.JavaFrame.web.annotations.controller.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class FrontControllerServlet extends HttpServlet {
    private List<String> listeClasse;

    public void init() throws ServletException {
        String namePackage = this.getInitParameter("package_controller");
    
        try {
            listeClasse = Utilitaire.getNameVariable(namePackage, Controller.class, ElementType.TYPE);
        } catch (Exception e) {
            System.out.println(e);
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



        out.println("Liste des classes controller");

        for (String classe : listeClasse) {
            out.println(classe);
        }
    }
}



