/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Document;
import Model.InvertedIndex;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author User
 */
public class testReadAll {

    public static void main(String[] args) throws IOException {
        InvertedIndex tempInd = new InvertedIndex();
        File folder = new File("C:\\Users\\User\\Documents\\NetBeansProjects\\Project-IR\\IR Tugas Mandiri\\Dokumen");
        tempInd.listAllFiles(folder);
        tempInd.listAllFiles("C:\\Users\\User\\Documents\\NetBeansProjects\\Project-IR\\IR Tugas Mandiri\\Dokumen");
      
        ArrayList<Document> listDoc = tempInd.getListOfDocument();
        for (int i = 0; i <listDoc.size(); i++) {
            Document doc = listDoc.get(i);
            System.out.println("ID ;"+doc.getId());
            System.out.println(doc.getContent());
        }

    }
}
