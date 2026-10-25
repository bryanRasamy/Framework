package main.JavaFrame.java.service;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import main.JavaFrame.java.modele.*;
import main.JavaFrame.web.annotations.controller.ModelAttribute;

import java.net.URL;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class Utilitaire {
    public static List<Class> getNameClasse(String namePackage,Class<? extends Annotation> annotation, ElementType typeAnnotation) throws Exception{
        List<Class> liste = new ArrayList<>();

        if (typeAnnotation==ElementType.TYPE) {
            liste=getClasse(namePackage, annotation);
        }else {
            throw new Exception("Le type de l'annotation est introuvable");
        }

        return liste;
    }

    public static List<Class> getClasse(String namePackage,Class<? extends Annotation> annotation) throws Exception{
        List<Class> liste = new ArrayList<>();

        String chemin = namePackage.replace(".", "/");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        URL ressource = classLoader.getResource(chemin);

        if (ressource == null) {
            throw new Exception("Le package n'existe pas ou est vide");
        }

        File directory = new File(ressource.getFile());

        if (directory.exists()) {
            File[] fichiers = directory.listFiles();

            for (File fichier : fichiers) {
                String nomFichier = fichier.getName();

                if(nomFichier.endsWith(".class")){
                    String className = namePackage + '.' +nomFichier.substring(0, nomFichier.length() - 6);

                    Class<?> classe = Class.forName(className);

                    if (classe.isAnnotationPresent(annotation)) {
                        liste.add(classe);
                    }
                }
            }
        }

        return liste;
    }

    public static  void getListURL(List<Class> classes, Map<UrlMethod,RouteMapping> listeUrl) throws Exception{
        try {
            for (Class classe : classes) {
                Method[] listeMethods = classe.getMethods();

                for (Method methode : listeMethods) {
                    Annotation[] annotations = methode.getAnnotations();

                    for (Annotation annotation : annotations) {
                        try {
                            String url = (String) annotation.annotationType().getMethod("url").invoke(annotation);
                            String method = (String)  annotation.annotationType().getMethod("method").invoke(annotation);
                            
                            UrlMethod urlMethod = new UrlMethod();
                            urlMethod.setUrl(url);
                            urlMethod.setMethod(method);

                            if (listeUrl.containsKey(urlMethod)) {
                                throw new Exception("Erreur : L'URL '" + url + " avec la mehode "+method+" est déjà associée à une autre méthode !");
                            }
                            
                            RouteMapping route = new RouteMapping();
                            route.setClasse(classe);
                            route.setMethode(methode);

                            listeUrl.put(urlMethod, route);
                        } catch (NoSuchMethodException e) {
                            /**/
                        }
                        
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public static Object callMethod(RouteMapping route, HttpServletRequest request) throws Exception{
        try {
            Method methode=route.getMethode();

            Object instance = route.getClasse().getDeclaredConstructor().newInstance();

            Parameter[] parametres= methode.getParameters();
            Object[] arguments=new Object[parametres.length];

            for (int i = 0; i < parametres.length; i++) {
                Parameter parametre=parametres[i];
                Class<?> type = parametre.getType();

                if(annotationPresent(parametre, ModelAttribute.class)){
                    Object argument = type.getDeclaredConstructor().newInstance();

                    fillArgument(argument,parametre,type,request);
                    
                    arguments[i]=argument;
                } else if (type == MySession.class) {
                    Object objectSession = type.getDeclaredConstructor().newInstance();

                    MySession mySession = (MySession) objectSession;

                    HttpSession session = request.getSession();

                    Enumeration<String> nomAttributsSession = session.getAttributeNames();

                    while (nomAttributsSession.hasMoreElements()){
                        String nomAttribut = nomAttributsSession.nextElement();

                        mySession.addAttribute(nomAttribut,session.getAttribute(nomAttribut));
                    }
                    arguments[i]=mySession;
                } else{
                    String donnee=request.getParameter(parametres[i].getName());
                    arguments[i]=convertir(donnee,parametres[i].getType());
                }
            }

            try {
                Object valeur = methode.invoke(instance, arguments);

                for (Object arg : arguments) {
                    if (arg instanceof MySession mySession) {
                        HttpSession session = request.getSession();
                        Map<String, Object> map = mySession.getAttributs();

                        Enumeration<String> nomAttributsSession = session.getAttributeNames();

                        while (nomAttributsSession.hasMoreElements()){
                            String nomAttribut = nomAttributsSession.nextElement();

                            if (map.containsKey(nomAttribut)) continue;

                            session.removeAttribute(nomAttribut);
                        }

                        for (Map.Entry<String, Object> e : map.entrySet()) {
                            session.setAttribute(e.getKey(), e.getValue());
                        }
                    }
                }

                return valeur;
            } catch (InvocationTargetException e) {
                e.getCause().printStackTrace();
                throw e;
            }

    } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public static String createPathView(String prefix, String nomView, String suffix){
        String cheminVue = prefix+nomView+suffix;

        return cheminVue;
    }

    public static boolean isAPI(Method methode, Class< ? extends Annotation> annotation){
        return methode.isAnnotationPresent(annotation);
    }

    public static Object convertir(String valeur, Class<?> type) {
        if (valeur == null){
            return null;
        }else if (type == int.class || type == Integer.class){
            return Integer.parseInt(valeur);
        }else if (type == double.class || type == Double.class){
            return Double.parseDouble(valeur);
        }else if (type == boolean.class || type == Boolean.class){ 
            return Boolean.parseBoolean(valeur);
        }else if (type == long.class || type == Long.class){
            return Long.parseLong(valeur);
        }
        
        return valeur;
    }

    public static boolean annotationPresent(Parameter parametre, Class< ? extends Annotation> annotation){
        return  parametre.isAnnotationPresent(annotation);
    }

    public static void fillArgument(Object argument, Parameter parametre, Class<?> type,HttpServletRequest request) throws Exception{
        Field[] attribut = argument.getClass().getDeclaredFields();

        boolean estMapFile = false;
        Map<String,Object> mapFichier=new HashMap<>();

        if (type == MapFile.class) {
            estMapFile=true;

            Part part = request.getPart(parametre.getName());

            if (part == null || part.getSize() == 0) {
                throw new Exception("Aucun fichier reçu pour : " + parametre.getName());
            }

            String nomFichier = part.getSubmittedFileName();
            System.out.println(nomFichier);

            int indexPoint = nomFichier.lastIndexOf(".");

            String nom=nomFichier.substring(0,indexPoint);
            String extension=nomFichier.substring(indexPoint+1);
            byte[] contenu=part.getInputStream().readAllBytes();

            mapFichier.put("nom",nom);
            mapFichier.put("extension",extension);
            mapFichier.put("binaire",contenu);
        }

        for (Field field : attribut) {
            String nomAttribut=field.getName();

            String nomSetter="set"+Character.toUpperCase(nomAttribut.charAt(0))+nomAttribut.substring(1);
            System.out.println(nomSetter);

            Method setter=argument.getClass().getMethod(nomSetter, field.getType());

            if (estMapFile){
                setter.invoke(argument, mapFichier.get(nomAttribut));
                continue;
            }

            String nomParametre=parametre.getName()+"."+field.getName();

            String donnee=request.getParameter(nomParametre);

            if (donnee==null) continue;


            setter.invoke(argument, convertir(donnee, field.getType()));

        }
    }
}

