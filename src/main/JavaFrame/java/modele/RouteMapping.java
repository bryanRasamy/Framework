package main.JavaFrame.java.modele;

public class RouteMapping {
    private String nameClasse;
    private String nameMethode;

    /*Constructeur*/
    public RouteMapping(){}

    /*Setters*/
    public void setNameClasse(String name){
        this.nameClasse=name;
    }

    public void setNameMethode(String name){
        this.nameMethode=name;
    }

    /*Getters*/
    public String getNameClasse(){
        return this.nameClasse;
    }

    public String getNameMethode(){
        return this.nameMethode;
    }
}
