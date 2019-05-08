/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author admin
 */
public class InvertedIndex {

    private ArrayList<Document> listOfDocument = new ArrayList<Document>();
    private ArrayList<Term> dictionary = new ArrayList<Term>();
//    private ArrayList<SearchingResult> sr = new ArrayList<SearchingResult>();
//    

    public InvertedIndex() {

    }

    public void addNewDocument(Document document) {
        this.getListOfDocument().add(document);
    }

    public ArrayList<Document> getListOfDocument() {
        return listOfDocument;
    }

    public void setListOfDocument(ArrayList<Document> listOfDocument) {
        this.listOfDocument = listOfDocument;
    }

    public ArrayList<Term> getDictionary() {
        return dictionary;
    }

    public void setDictionary(ArrayList<Term> dictionary) {
        this.dictionary = dictionary;
    }

    //belum diurutkan berdasarkan term
    public ArrayList<Posting> getUnsortedPostingList() {
        ArrayList<Posting> list = new ArrayList<Posting>();

        // loop sebanyak term
        for (int i = 0; i < getListOfDocument().size(); i++) {
            String[] termResult = getListOfDocument().get(i).getListofTerm();
            //loop sebanyak term
            for (int j = 0; j < termResult.length; j++) {
                //buat object temp posting
                Posting tempPosting = new Posting(termResult[j], getListOfDocument().get(i));
                list.add(tempPosting);
            }
        }
        return list;
    }

    public ArrayList<Posting> getSortedPostingList() {
        // siapkan posting List
        ArrayList<Posting> list = new ArrayList<Posting>();
        // panggil list yang belum terurut
        list = this.getUnsortedPostingList();
        // urutkan
        Collections.sort(list);
        return list;
    }

    public ArrayList<Posting> getSortedPostingListWithTermNumber() {
        // siapkan posting List
        ArrayList<Posting> list = new ArrayList<Posting>();
        // panggil list yang belum terurut
        list = this.getUnsortedPostingList();
        // urutkan
        Collections.sort(list);
        return list;
    }

    public ArrayList<Posting> search(String query) {
        // buat index/dictionary
        makeDictionary();
        String[] tempQuery = query.split(" ");
        ArrayList<Posting> tempPosting = new ArrayList<>();

        for (int i = 0; i < tempQuery.length; i++) {
            String string = tempQuery[i];
            if (i == 0) {
                tempPosting = searchOneWord(tempQuery[i]);
            } else {
                ArrayList<Posting> tempPosting1 = searchOneWord(tempQuery[i]);
                tempPosting = intersection(tempPosting, tempPosting1);
            }
        }
        return tempPosting;
    }

    public ArrayList<Posting> intersection(ArrayList<Posting> p1, ArrayList<Posting> p2) {
        //initialize menggunakan object result
        ArrayList<Posting> result = new ArrayList<>();
        //variable baru indexP1 dan indexp2
        int indexP1 = 0;
        int indexP2 = 0;

        //membuat variable pst1 dan pst2
        Posting pst1 = p1.get(indexP1);
        Posting pst2 = p2.get(indexP2);

        //cek apakah p1 dan p2 sama dengan null
        if (p1 == null && p2 == null) {
            return new ArrayList<>();
        }

        while (true) {
            //cek apakah id dokumen pst1 == id odokumen pst2
            if (pst1.getDocument().getId() == pst2.getDocument().getId()) {
                try {
                    //pst1 ditambahkan ke result
                    result.add(pst1);
                    indexP1++;
                    indexP2++;
                    pst1 = p1.get(indexP1);
                    pst2 = p2.get(indexP2);
                } catch (Exception e) {
                    break;
                }

            } //cek apakah id dokumen pst1 < id dokumen pst2
            else if (pst1.getDocument().getId() < pst2.getDocument().getId()) {
                try {
                    indexP1++;
                    pst1 = p1.get(indexP1);
                } catch (Exception e) {
                    break;
                }

            } else {
                try {
                    indexP2++;
                    pst2 = p2.get(indexP2);
                } catch (Exception e) {
                    break;
                }
            }
        }
        //mengembalikan ke result
        return result;
    }

    public ArrayList<Posting> searchOneWord(String word) {
        Term tempTerm = new Term(word);
        if (getDictionary().isEmpty()) {
            // dictionary kosong
            return null;
        } else {
            int positionTerm = Collections.binarySearch(dictionary, tempTerm);
            if (positionTerm < 0) {
                // tidak ditemukan
                return null;
            } else {
                return dictionary.get(positionTerm).getPostingList();
            }
        }
    }

    public void makeDictionary() {
        //load sorted list
        ArrayList<Posting> list = this.getSortedPostingList();
        //cek dictionary apakah kosong
        for (int i = 0; i < list.size(); i++) {
            //cek dictionary kosong atau tidak
            if (getDictionary().isEmpty()) {
                Term term = new Term(list.get(i).getTerm());
                term.getPostingList().add(list.get(i));
                //tambah ke dictionary
                getDictionary().add(term);
            } else {
                // dictionary tidak kosong
                Term tempTerm = new Term(list.get(i).getTerm());
                // cek sudah ada di dictionary atau belum
                int position = Collections.binarySearch(getDictionary(), tempTerm);
                if (position < 0) {
                    //term baru
                    //tambah postinglist ke term
                    tempTerm.getPostingList().add(list.get(i));
                    //tambahkan term ke dictionary
                    getDictionary().add(tempTerm);
                } else {
                    //term sudah ada
                    //tambahkan psotinglist saja dari existing term
                    getDictionary().get(position).getPostingList().add(list.get(i));
                    //urutkan posting list
                    Collections.sort(getDictionary().get(position).getPostingList());
                }
                //urutkan term dictionary
                Collections.sort(getDictionary());
            }
        }

    }

    public void makeDictionaryWithTermNumber() {
        // cek deteksi ada term yang frekuensinya lebih dari 
        // 1 pada sebuah dokumen
        // buat posting list term terurut
        ArrayList<Posting> list = getSortedPostingListWithTermNumber();
        // looping buat list of term (dictionary)
        for (int i = 0; i < list.size(); i++) {
            // cek dictionary kosong?
            if (getDictionary().isEmpty()) {
                // buat term
                Term term = new Term(list.get(i).getTerm());
                // tambah posting ke posting list utk term ini
                term.getPostingList().add(list.get(i));
                // tambah ke dictionary
                getDictionary().add(term);
            } else {
                // dictionary sudah ada isinya
                Term tempTerm = new Term(list.get(i).getTerm());
                // pembandingan apakah term sudah ada atau belum
                // luaran dari binarysearch adalah posisi
                int position = Collections.binarySearch(getDictionary(), tempTerm);
                if (position < 0) {
                    // term baru
                    // tambah postinglist ke term
                    tempTerm.getPostingList().add(list.get(i));
                    // tambahkan term ke dictionary
                    getDictionary().add(tempTerm);
                } else {
                    // term ada
                    // tambahkan postinglist saja dari existing term
                    getDictionary().get(position).
                            getPostingList().add(list.get(i));
                    // urutkan posting list
                    Collections.sort(getDictionary().get(position)
                            .getPostingList());
                }
                // urutkan term dictionary
                Collections.sort(getDictionary());
            }

        }

    }

    public int getDocumentFrequency(String term) {
        Term tempTerm = new Term(term);
        // cek apakah term ada di dictionary
        int index = Collections.binarySearch(dictionary, tempTerm);
        if (index > 0) {
            // term ada
            // ambil ArrayList<Posting> dari object term
            ArrayList<Posting> tempPosting = dictionary.get(index)
                    .getPostingList();
            // return ukuran posting list
            return tempPosting.size();
        } else {
            // term tidak ada
            return -1;
        }
    }

//    public double getInverseDocumentFrequency(String term) {
//        double N = this.listOfDocument.size();
//        double n = getDocumentFrequency(term);
//        double idf = Math.log10(N / n);
//
//        return idf;
////    }
    /**
     * Fungsi untuk mencari inverse term dari sebuah index
     *
     * @param term
     * @return
     */
    public double getInverseDocumentFrequency(String term) {
        Term tempTerm = new Term(term);
        // cek apakah term ada di dictionary
        int index = Collections.binarySearch(dictionary, tempTerm);
        if (index >= 0) {
            // term ada
            // jumlah total dokumen
            int N = listOfDocument.size();
            // jumlah dokumen dengan term i
            int ni = getDocumentFrequency(term);
            // idf = log10(N/ni)
            double Nni = (double) N / ni;
            double tempa = Math.log10(Nni);
            if (tempa > 0) {
                return tempa;
            } else if (tempa < 0) {
                return tempa;
            } else {
                return 0;
            }
        } else {
            // term tidak ada
            // nilai idf = 0
            return 0.0;
        }
    }

    public ArrayList<Posting> makeTFIDF(int idDocument) {
        // buat posting list hasil
        ArrayList<Posting> result = new ArrayList<Posting>();
        // buat document temporary, sesuai passing parameter
        Document temp = new Document(idDocument);
        // cek document temp, ada di dalam list document?
        int cari = Collections.binarySearch(listOfDocument, temp);
        // jika ada, variable cari akan berisi indeks. nilai lebih dari 0
        if (cari >= 0) {
            // dokumen ada
            // baca dokumen sesuai indek di list dokumen
            temp = listOfDocument.get(cari);
            // buat posting list dengan bobot masih 0
            result = temp.getListofPosting();
            // isi bobot dari posting list
            for (int i = 0; i < result.size(); i++) {
                // ambil term
                String tempTerm = result.get(i).getTerm();
                // cari idf
                double idf = getInverseDocumentFrequency(tempTerm);
                // cari tf
                int tf = result.get(i).getNumberOfTerm();
                // hitung bobot
                double bobot = tf * idf;
                // set bobot pada posting
                result.get(i).setWeight(bobot);
            }
            Collections.sort(result);
        } else {
            // dokumen tidak ada
        }
        return result;
    }

    public int getTermFrequency(String term, int idDocument) {
        Document document = new Document();
        document.setId(idDocument);
        int pos = Collections.binarySearch(listOfDocument, document);
        if (pos >= 0) {
            ArrayList<Posting> tempPosting = listOfDocument.get(pos).getListofPosting();
            Posting posting = new Posting();
            posting.setTerm(term);
            int postingIndex = Collections.binarySearch(tempPosting, posting);
            if (postingIndex >= 0) {
                return tempPosting.get(postingIndex).getNumberOfTerm();
            }
            return 0;
        }

        return 0;
    }

//    public double getInnerProduct(ArrayList<Posting> p1,
//            ArrayList<Posting> p2) {
//
//        int newTempPost;
//        double hasil = 0;
//
//        for (int i = 0; i < p1.size(); i++) {
//            newTempPost = Collections.binarySearch(p2, p1.get(i));
//            if (newTempPost > 0) {
//                hasil = hasil + (p1.get(i).getWeight() * p2.get(i).getWeight());
//            }
//        }
//        return hasil;
//    }
    public double getInnerProduct(ArrayList<Posting> p1, ArrayList<Posting> p2) {
        // urutkan posting list
        Collections.sort(p2);
        Collections.sort(p1);
        // buat temp hasil
        double result = 0.0;
        // looping dari posting list p1
        for (int i = 0; i < p1.size(); i++) {
            // ambil temp
            Posting temp = p1.get(i);
            // cari posting di p2
            boolean found = false;
            for (int j = 0; j < p2.size() && found == false; j++) {
                Posting temp1 = p2.get(j);
                if (temp1.getTerm().equalsIgnoreCase(temp.getTerm())) {
                    // term sama
                    found = true;
                    // kalikan bobot untuk term yang sama
                    result = result + temp1.getWeight() * temp.getWeight();
                }
            }
        }
        return result;
    }

    public ArrayList<Posting> getQueryPosting(String query) {
        // buat dokumen
        Document temp = new Document(-1, query);
        // buat posting list
        ArrayList<Posting> result = temp.getListofPosting();
        // hitung bobot
        // isi bobot dari posting list
        for (int i = 0; i < result.size(); i++) {
            // ambil term
            String tempTerm = result.get(i).getTerm();
            // cari idf
            double idf = getInverseDocumentFrequency(tempTerm);
            // cari tf
            int tf = result.get(i).getNumberOfTerm();
            // hitung bobot
            double bobot = tf * idf;
            // set bobot pada posting
            result.get(i).setWeight(bobot);
        }
        Collections.sort(result);
        return result;
    }

//    public double getLengthOfPosting(ArrayList<Posting> posting) {
//        double tempResult = 0;
//        double resultFin = 0;
//        for (int i = 0; i < posting.size(); i++) {
//            tempResult = Math.pow(posting.get(i).getWeight(), 2);
//        }
//        resultFin = Math.sqrt(tempResult);
//        return resultFin;
//    }
    // cara pak puspo
    public double getLengthOfPosting(ArrayList<Posting> posting) {
        double result = 0.0;
        for (int i = 0; i < posting.size(); i++) {
            // ambil obyek posting
            Posting post = posting.get(i);
            // ambil bobot/weight
            double weight = post.getWeight();
            // kuadrat bobot
            weight = weight * weight;
            // jumlahkan ke result
            result = result + weight;
        }
        // keluarkan akar kuadrat
        return Math.sqrt(result);
    }

    /**
     * Fungsi untuk menghitung cosine similarity
     *
     * @param posting
     * @param posting1
     * @return
     */
    // Cara pak Puspo
    public double getCosineSimilarity(ArrayList<Posting> posting,
            ArrayList<Posting> posting1) {
        // cari jarak antara posting dan posting 1
        double jarak = getInnerProduct(posting, posting1);
        // cari panjang posting
        double panjang_posting = getLengthOfPosting(posting);
        // cari panjang posting1
        double panjang_posting1 = getLengthOfPosting(posting1);
        // hitung cosine similarity
        double result
                = jarak / Math.sqrt(panjang_posting * panjang_posting1);
        return result;
    }
//    public double getCosineSimilarity(ArrayList<Posting> posting, ArrayList<Posting> posting1) {
//
//        double dotProduct = getInnerProduct(posting, posting1);
//        double sumBawah1 = 0.0;
//        double sumBawah2 = 0.0;
//        double cosineSimilarity = 0.0;
//
//        for (int i = 0; i < posting.size(); i++) {
//            dotProduct += posting.get(i).getWeight() * posting1.get(i).getWeight();
//            sumBawah1 += Math.pow(posting.get(i).getWeight(), 2);
//            for (int j = 0; j < posting1.size(); j++) {
//                sumBawah2 += Math.pow(posting1.get(i).getWeight(), 2);
//            }
//        }
//        if (sumBawah1 != 0.0 | sumBawah2 != 0.0) {
//            cosineSimilarity = dotProduct / Math.sqrt(sumBawah1 * sumBawah2);
//        } else {
//            return 0.0;
//        }
//        return cosineSimilarity;
//    }

    /**
     * Fungsi untuk mencari berdasar nilai TFIDF
     *
     * @param query
     * @return
     */
//    public ArrayList<SearchingResult> searchTFIDF(String query) {
//        ArrayList<Posting> QPost = getQueryPosting(query);
//        ArrayList<SearchingResult> sr = new ArrayList<SearchingResult>();
//        //buat objek baru dengan mengambil panjang posting
//        double panjangQuery = getLengthOfPosting(QPost);
//        //looping pncarian di doc 1-3
//        for (int i = 0; i < getListOfDocument().size(); i++) {
//            //hitung tf-idf masing-masing doc
//            ArrayList<Posting> tempPost = makeTFIDF(getListOfDocument().get(i).getId());
//            ArrayList<Posting> newTempPost = new ArrayList<>();
//            //ambil value tf-idf yang sama dengan query
//            if (query.isEmpty()) {
//                return null;
//            } else {
//                //(QPost.get(i).getWeight() == tempPost.get(i).getWeight())
//                //newTempPost.get(i).setWeight(tempPost.get(i).getWeight());
//                //cari panjang tfidf, hitung weight/panjang
//                double panjangDoc = getLengthOfPosting(tempPost);
//                double result = tempPost.get(i).getWeight() / panjangDoc;
//                //buat innerproduct dari query similarity
//                double innerProduct = getInnerProduct(QPost, tempPost);
//                //hasil innerprodct dari dokumen dimasukkan ke array
//                SearchingResult tempSR = new SearchingResult(innerProduct, tempPost.get(i).getDocument());
//                sr.add(tempSR);
//            }
//        }
//        //return array searching result
//        return sr;
//    }
    // cara pak Puspo
    public ArrayList<SearchingResult> searchTFIDF(String query) {
        // buat list search document
        ArrayList<SearchingResult> result = new ArrayList<SearchingResult>();
        // ubah query menjadi array list posting
        ArrayList<Posting> queryPostingList = getQueryPosting(query);
        // buat posting list untuk seluruh dokumen
        for (int i = 0; i < listOfDocument.size(); i++) {
            // ambil obyek dokumen
            Document doc = listOfDocument.get(i);
            int idDoc = doc.getId();
            // buat posting list untuk dokumen
            ArrayList<Posting> tempDocWeight = makeTFIDF(idDoc);
            // hitung jarak antar posting list dokumen dengan posting list query
            double hasilDotProduct = getInnerProduct(tempDocWeight, queryPostingList);
            // isi result list
            if (hasilDotProduct > 0) {
                // buat obyek document hasil cari
                SearchingResult resultDoc = new SearchingResult(hasilDotProduct, doc);
                // tambahkan ke list hasil cari
                result.add(resultDoc);
            }
        }
        // urutkan hasil cari
        Collections.sort(result);
        return result;
    }

    /**
     * Fungsi untuk mencari dokumen berdasarkan cosine similarity
     *
     * @param query
     * @return
     */
//    public ArrayList<SearchingResult> searchCosineSimilarity(String query) {
//        //buat array list posting
//        ArrayList<Posting> tempPost = new ArrayList<>();
//        //buat arraylist searching result
//        ArrayList<SearchingResult> sr = new ArrayList<SearchingResult>();
//        //looping sepanjang dokumen
//        for (int i = 0; i < getListOfDocument().size(); i++) {
//            //buat arraylist posting memakai tfidf
//            ArrayList<Posting> tempPostt = makeTFIDF(getListOfDocument().get(i).getId());
//            //cari similarity
//            double similarity = getCosineSimilarity(tempPost, tempPostt);
//            //buat dokumen dengan tipe searching result parameter similarity dan dokumen yang dipakai
//            SearchingResult tempDoc = new SearchingResult(similarity, getListOfDocument().get(i));
//            //add dokumen ke arraylist searching result
//            sr.add(tempDoc);
//
//        }
//        //collection sort
//        Collections.sort(sr);
//        //collection reverse
//        Collections.reverse(sr);
//        //return searching result
//        return sr;
//    }
    // cara pak Puspo
    public ArrayList<SearchingResult> searchCosineSimilarity(String query) {
        // buat list search document
        ArrayList<SearchingResult> result = new ArrayList<SearchingResult>();
        // ubah query menjadi array list posting
        ArrayList<Posting> queryPostingList = getQueryPosting(query);
        // buat posting list untuk seluruh dokumen
        for (int i = 0; i < listOfDocument.size(); i++) {
            // ambil obyek dokumen
            Document doc = listOfDocument.get(i);
            int idDoc = doc.getId();
            // buat posting list untuk dokumen
            ArrayList<Posting> tempDocWeight = makeTFIDF(idDoc);
            // hitung cosin similarity antar posting list dokumen dengan posting list query
            double cosineSimilarity = getCosineSimilarity(tempDocWeight, queryPostingList);
            // isi result list
            if (cosineSimilarity > 0) {
                // buat obyek document hasil cari
                SearchingResult resultDoc = new SearchingResult(cosineSimilarity, doc);
                // tambahkan ke list hasil cari
                result.add(resultDoc);
            }
        }
        // urutkan hasil cari
        Collections.sort(result);
        return result;
    }

    public void readDirectory(File directory) {
        File files[] = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            // buat document baru
            Document doc = new Document();
            doc.setId(i); // set idDoc sama dengan i
            // baca isi file
            // Isi file disimpan di atribut content dari objeck document
            // variabel i merupakan idDocument;
            File file = files[i];
            doc.readFile((i + 1), file);
            // masukkan file isi directory ke list of document pada obye index
            this.addNewDocument(doc);
        }
        // lakukan indexing atau buat dictionary
        this.makeDictionaryWithTermNumber();
    }

    public void listAllFiles(File folder) {
        //System.out.println("In listAllfiles(File) method");
        File[] fileNames = folder.listFiles();
        for (File file : fileNames) {
            // if directory call the same method again
            if (file.isDirectory()) {
                listAllFiles(file);
            } else {
                try {
                    readContent(file);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }
    // Uses Files.walk method   

    public void listAllFiles(String path) {
        //System.out.println("In listAllfiles(String path) method");
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        readContent(filePath);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void readContent(File file) throws IOException {
        Document doc = new Document();
        int i = 1;
//        System.out.println("read file " + file.getCanonicalPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String strLine;
            // Read lines from the file, returns null when end of stream 
            // is reached
            while ((strLine = br.readLine()) != null) {
                doc.setContent(strLine);
                doc.setId(i);
                listOfDocument.add(doc);
                makeDictionary();
               // System.out.println("Line is - " + strLine);
                i++;
            }
        }
    }

    public void readContent(Path filePath) throws IOException {
//        System.out.println("read file " + filePath);
        List<String> fileList = Files.readAllLines(filePath);
//        System.out.println("" + fileList);
    }
}
