/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Document;
import Model.InvertedIndex2;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author User
 */
public class testReadAll {

    public static void main(String[] args) throws IOException {
        InvertedIndex2 tempInd = new InvertedIndex2();
        File folder = new File("C:\\Users\\User\\Documents\\NetBeansProjects\\Project-IR 8 mei\\IR Tugas Mandiri\\Dokumen Pemberantasan TIPIKOR");
        tempInd.readDirectory(folder);
        //tempInd.listAllFiles(folder);
        
        ArrayList<Document> listDoc = tempInd.getListOfDocument();
        for (int i = 0; i <listDoc.size(); i++) {
            Document doc = listDoc.get(i);
            System.out.println("ID :"+doc.getId());
            System.out.println(doc.getContent());
        }

    }
}

