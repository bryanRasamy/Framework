#!/bin/bash

# Définition des variables
FRAMEWORK_NAME="JavaFrame"
SRC_DIR="src/main"
BUILD_DIR="build"
CLASSES_DIR="$BUILD_DIR/classes"

# Emplacement de l'API Servlet pour la compilation (Portée "provided")
LIB_DIR="/home/bryan/Documents/ITU/Tomcat/lib"
SERVLET_API_JAR="$LIB_DIR/servlet-api.jar:$LIB_DIR/jsp-api.jar"

# Nettoyage et création du répertoire de build
rm -rf $BUILD_DIR
mkdir -p $CLASSES_DIR

echo "1. Compilation des fichiers Java du framework..."
# On liste et on compile toutes les classes du framework
find $SRC_DIR -name "*.java" > sources.txt
javac -cp "$SERVLET_API_JAR" -d $CLASSES_DIR @sources.txt
rm sources.txt

echo "2. Création du fichier JAR du framework..."
# On se déplace dans le dossier des .class pour créer un JAR propre à la racine
cd $CLASSES_DIR || exit
jar -cvf ../$FRAMEWORK_NAME.jar *
cd ../..

echo ""
echo "========================================================="
echo " Génération terminée ! Fichier disponible :"
echo " --> $BUILD_DIR/$FRAMEWORK_NAME.jar"
echo "========================================================="
echo ""
