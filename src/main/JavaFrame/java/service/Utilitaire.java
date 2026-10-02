package main.JavaFrame.java.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import main.JavaFrame.java.modele.*;
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
                String donnee=request.getParameter(parametres[i].getName());
                arguments[i]=convertir(donnee,parametres[i].getType());
            }

            Object valeur = methode.invoke(instance,arguments);

            return valeur;
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
}

