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

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class FrontControllerServlet extends HttpServlet {
    private Map<UrlMethod,RouteMapping> listeUrl;
    private Exception exception;
    private String prefix;
    private String suffix;
    private ServletContext servletContext;

    public void init() throws ServletException {
        this.listeUrl = (Map<UrlMethod, RouteMapping>) this.getServletContext().getAttribute("listeUrl");
        this.exception = (Exception) this.getServletContext().getAttribute("exception");
        this.prefix = (String) this.getServletContext().getAttribute("prefix");
        this.suffix = (String) this.getServletContext().getAttribute("suffix");

        this.servletContext = this.getServletContext();

    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (req.getDispatcherType() == DispatcherType.FORWARD) {
            return;
        }

        String url = req.getRequestURL().toString();

        String actionPath = req.getServletPath();

        if (actionPath == null || actionPath.isEmpty()) {
            actionPath = req.getPathInfo();
        }

        if (actionPath == null || actionPath.isEmpty()) {
            actionPath = "/";
        }

        res.setContentType("text/plain");

        PrintWriter out = res.getWriter();

        if (exception!=null) {
            out.println(exception.getMessage());
            return;
        }

        UrlMethod urlMethod = new UrlMethod();
        urlMethod.setUrl(actionPath);
        urlMethod.setMethod("get");

        RouteMapping classeMethode = listeUrl.get(urlMethod);

        if (classeMethode!=null) {
            try {
                Object instance = classeMethode.getClasse().getDeclaredConstructor().newInstance();

                Utilitaire.injectContext(instance, servletContext);
                
                Object valeur=Utilitaire.callMethod(classeMethode, instance);

                if(valeur instanceof ModelAndView){
                    ModelAndView modelView = (ModelAndView) valeur;

                    String cheminVue = Utilitaire.createPathView(prefix, modelView.getView(), suffix);

                    for (Map.Entry<String,Object> attribut : modelView.getAttributs().entrySet()) {
                        String key = attribut.getKey();
                        Object value = attribut.getValue();

                        req.setAttribute(key,value);
                    }

                    System.out.println(cheminVue);
                    System.out.println(actionPath);

                    RequestDispatcher dispat = req.getRequestDispatcher(cheminVue);
                    System.out.println(dispat);
                    dispat.forward(req,res);

                    return;
                }else{
                    out.println("probleme");
                }
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



