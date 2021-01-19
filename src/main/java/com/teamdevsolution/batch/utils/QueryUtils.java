package com.teamdevsolution.batch.utils;

public interface QueryUtils {

    String FORMATEUR_INSERT_QUERY="INSERT INTO formateurs (id,nom,prenom,adresse_email) VALUES(?,?,?,?)";
    String FORMATION_INSERT_QUERY="INSERT INTO formations (code,libelle,descriptif) VALUES(?,?,?)";
    String SEANCE_INSERT_QUERY="INSERT INTO seances (code_formation,id_formateur,date_debut,date_fin) VALUES(?,?,?,?)";
}
