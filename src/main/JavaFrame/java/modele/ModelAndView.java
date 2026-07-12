package main.JavaFrame.java.modele;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {
    private String view;
    private Map<String,Object> attributs;
    
    /*Constructeur*/
    public ModelAndView(){
        attributs = new HashMap<>();
    }

    /*Setters*/
    public void setView(String view){
        this.view=view;
    }

    public void setAttributs(Map<String,Object> attributs){
        this.attributs=attributs;
    }

    /*Getters*/
    public String getView(){
        return this.view;
    }

    public Map<String,Object> getAttributs(){
        return this.attributs;
    }

    /*Methodes utilitaire*/
    public void addObject(String cle, Object valeur){
        attributs.put(cle, valeur);
    }
}
