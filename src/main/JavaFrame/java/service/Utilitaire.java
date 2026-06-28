package main.JavaFrame.java.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Method;

import main.JavaFrame.web.annotations.controller.*;
import main.JavaFrame.java.modele.*;
import java.net.URL;

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

    public static Map<String,RouteMapping> getListURL(List<Class> classes) throws Exception{
        Map<String,RouteMapping> listeUrl = new HashMap<>();

        try {
            for (Class classe : classes) {
                Method[] listeMethods = classe.getMethods();

                for (Method methode : listeMethods) {
                    Annotation[] annotations = methode.getAnnotations();

                    for (Annotation annotation : annotations) {
                        try {
                            Method methodeUrl = annotation.annotationType().getMethod("url");
                        
                            String url = (String) methodeUrl.invoke(annotation);
                            
                            if (listeUrl.containsKey(url)) {
                                throw new Exception("Erreur : L'URL '" + url + "' est déjà associée à une autre méthode !");
                            }
                            
                            RouteMapping route = new RouteMapping();
                            route.setNameClasse(classe.getSimpleName());
                            route.setNameMethode(methode.getName());

                            listeUrl.put(url, route);
                        } catch (NoSuchMethodException e) {
                            
                        }
                        
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        

        return listeUrl;
    }
}

