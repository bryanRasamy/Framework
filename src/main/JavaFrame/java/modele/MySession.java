package main.JavaFrame.java.modele;

import java.util.HashMap;
import java.util.Map;

public class MySession {
    private Map<String,Object> attributs;

    /*Constructeur*/
    public MySession(){
        attributs = new HashMap<>();
    }

    /*Setters*/
    public void setAttributs(Map<String,Object> attributs){
        this.attributs=attributs;
    }

    /*Getters*/
    public Map<String,Object> getAttributs(){
        return this.attributs;
    }

    /*Methodes utilitaire*/
    public void addAttribute(String cle, Object valeur){
        attributs.put(cle, valeur);
    }

    public Object getAttribute(String nom){
        return attributs.get(nom);
    }

    public void removeAttribute(String nom){
        attributs.remove(nom);
    }
}
