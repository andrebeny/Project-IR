/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;
import java.util.*;
import java.io.*;
import java.nio.*;
import javax.swing.JOptionPane;
import Model.Document;
import Model.InvertedIndex;
import Model.Posting;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import static sun.reflect.annotation.AnnotationParser.toArray;
/**
 *
 * @author User
 */
public class testreadfilefolder {

    public static void main(String[] args) {
        File folder = new File("C:\\Users\\User\\Documents\\NetBeansProjects\\Project-IR\\IR Tugas Mandiri\\Dokumen");
        testreadfilefolder listFiles = new testreadfilefolder();
        System.out.println("reading files before Java8 - Using listFiles() method");
        listFiles.listAllFiles(folder);
        System.out.println("-------------------------------------------------");
        System.out.println("reading files Java8 - Using Files.walk() method");
        listFiles.listAllFiles("C:\\Users\\User\\Documents\\NetBeansProjects\\Project-IR\\IR Tugas Mandiri\\Dokumen");

    }
    // Uses listFiles method  

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
        System.out.println("In listAllfiles(String path) method");
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
        System.out.println("read file " + file.getCanonicalPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String strLine;
            // Read lines from the file, returns null when end of stream 
            // is reached
            while ((strLine = br.readLine()) != null) {
                //doc.setContent(strLine);
                System.out.println("Line is - " + strLine);
            }
        }
    }

    public void readContent(Path filePath) throws IOException {
        System.out.println("read file " + filePath);
        List<String> fileList = Files.readAllLines(filePath);
        System.out.println("" + fileList);
    }

}

