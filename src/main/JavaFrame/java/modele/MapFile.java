package main.JavaFrame.java.modele;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class MapFile {
    private String nom;
    private String extension;
    private byte[] binaire;

    /*Constructeur*/
    public MapFile(){

    }

    /*Setters*/
    public void setNom(String nom){
        this.nom=nom;
    }

    public void setExtension(String extension){
        this.extension=extension;
    }

    public void setBinaire(byte[] binaire){
        this.binaire=binaire;
    }

    /*Getters*/
    public String getNom(){
        return this.nom;
    }

    public String getExtension(){
        return this.extension;
    }

    public byte[] getBinaire(){
        return this.binaire;
    }

    /*Methodes utilitaire*/
    public void sauvegarderFichier(String cheminDossier) throws Exception{
        Path rep = Paths.get(cheminDossier);

        Files.createDirectories(rep);
        System.out.println(rep);

        String nomUnique = nom+"_"+UUID.randomUUID()+"."+extension;
        System.out.println(binaire);
        Files.write(rep.resolve(nomUnique), binaire);
    }

}
