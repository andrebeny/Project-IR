/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author admin
 */
public class Document implements Comparable<Document> {

    private int id;
    private String content;
    private String realContent;
    private double CosineSimilarity;

    public Document() {

    }

    public Document(String content) {
        this.content = content;
        this.realContent = content;
    }

    public Document(int id, String content) {
        this.id = id;
        this.content = content;
        this.realContent = content;
    }

    Document(int idDocument) {
        this.id = idDocument;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    public String[] getListofTerm() {
        String value = this.getContent();
        value = value.replaceAll("[.,?!]", "");
        return value.split(" ");
    }

    public ArrayList<Posting> getListofPosting() {
        // panggil fungsi getListOfTerm
        String tempString[] = getListofTerm();
        // buat objek ArrayList<Posting> result untuk menampung hasil
        ArrayList<Posting> result = new ArrayList<Posting>();
        // buat looping sebanyak listOfTerm
        for (int i = 0; i < tempString.length; i++) {
            // di dalam looping
            // jika term pertama maka
            if (i == 0) {
                // buat object tempPosting
                Posting temPosting = new Posting(tempString[0], this);
                // set atribut document, gunakan this
                // tambahkan ke ArrayList result
                result.add(temPosting);
            } else {
                // lainnya
                // sorting ArayList result
                Collections.sort(result);
                // cek apakah term sudah ada
                // gunakan fungsi search dengan luaran indeks obyek yang memenuhi
                // buat object tempPosting           
                Posting temPosting = new Posting(tempString[i], this);
                int indexCari = Collections.binarySearch(result, temPosting);
                // jika hasil cari kurang dari 0  (obyek tidak ada)
                if (indexCari < 0) {
                    // set atribut document, gunakan this
                    // tambahkan ke ArrayList result
                    result.add(temPosting);
                } else {
                    // lainnya   (obyek ada)
                    // ambil postingnya, 
                    // tambahkan atribut numberOfTerm dengan 1
                    // dgn fungsi get
                    // int tempNumber = get(indekshasilCari).getNumberOfTerm()+1;
                    int tempNumber = result.get(indexCari).getNumberOfTerm() + 1;
                    // atau get(indekshasilcari.setNumberOfTerm(tempNumber)
                    result.get(indexCari).setNumberOfTerm(tempNumber);
                }
            }
        }
        return result;
    }

    @Override
    public int compareTo(Document t) {
        //return Integer.compare(this.id, t.id);
        return id - t.getId();
    }

    public void readFile(int idDoc, File file) {

        this.id = idDoc;
        String strLine = null;
        try {
            FileReader inputDokumen = new FileReader(file);//membaca inputan sebuah dokumen
            BufferedReader br = new BufferedReader(inputDokumen);

            while ((strLine = br.readLine()) != null) {
                this.setContent(strLine);
            }
            br.close();
        } catch (FileNotFoundException ex) {
            System.out.println("File Not Found");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * @return the CosineSimilarity
     */
    public double getCosineSimilarity() {
        return CosineSimilarity;
    }

    /**
     * @param CosineSimilarity the CosineSimilarity to set
     */
    public void setCosineSimilarity(double CosineSimilarity) {
        this.CosineSimilarity = CosineSimilarity;
    }

    /**
     * @return the realContent
     */
    public String getRealContent() {
        return realContent;
    }

    /**
     * @param realContent the realContent to set
     */
    public void setRealContent(String realContent) {
        this.realContent = realContent;
    }

}
