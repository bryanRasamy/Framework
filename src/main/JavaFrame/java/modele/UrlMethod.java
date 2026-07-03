package main.JavaFrame.java.modele;

import java.util.Objects;

public class UrlMethod {
    private String url;
    private String method;

    /*Constructeur*/
    public UrlMethod(){

    }

    /*Setters*/
    public void setUrl(String url){
        this.url=url;
    }

    public void setMethod(String method){
        this.method=method;
    }

    /*Getters*/
    public String getUrl(){
        return this.url;
    }

    public String getMethod(){
        return this.method;
    }


    @Override
    public boolean equals(Object objectUrlMethod) {
        UrlMethod urlMethod = (UrlMethod) objectUrlMethod;

        if (this.getUrl().equals(urlMethod.getUrl()) && this.getMethod().equals(urlMethod.getMethod())) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url,method);
    }
}
