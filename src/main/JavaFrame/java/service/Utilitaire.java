package main.JavaFrame.java.service;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.net.URL;

public class Utilitaire {
    public static List<String> getNameVariable(String namePackage,Class<? extends Annotation> annotation, ElementType typeAnnotation) throws Exception{
        List<String> liste = new ArrayList<>();

        if (typeAnnotation==ElementType.TYPE) {
            liste=getNameClasse(namePackage, annotation);
        } else {
            throw new Exception("Le type de l'annotation est introuvable");
        }

        return liste;
    }

    public static List<String> getNameClasse(String namePackage,Class<? extends Annotation> annotation) throws Exception{
        List<String> liste = new ArrayList<>();

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
                        liste.add(className);
                    }
                }
            }
        }

        return liste;
    }
}

