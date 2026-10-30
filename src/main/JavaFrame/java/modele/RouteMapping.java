package main.JavaFrame.java.modele;

import java.lang.reflect.Method;

public class RouteMapping {
    private Class classe;
    private Method methode;
    private Object instance;

    /*Constructeur*/
    public RouteMapping(){}

    /*Setters*/
    public void setClasse(Class classe){
        this.classe=classe;
    }

    public void setMethode(Method method){
        this.methode=method;
    }

    public void setInstance(Object instance){
        this.instance=instance;
    }

    /*Getters*/
    public Class getClasse(){
        return this.classe;
    }

    public Method getMethode(){
        return this.methode;
    }

    public Object getInstance() {
        return this.instance;
    }
}
