package fpgrowth;

import java.awt.Color;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Scanner;

/**
 *
 * @author LocNgD <locndse160199@fpt.edu.vn>
 */

class StringCompare implements Comparator<String> {
    public int compare(String o1, String o2) {
        if (o1.length() < o2.length()) return -1;
        else if (o1.length() > o2.length()) return 1;
        else {
            for (int i = 0; i < o1.length(); i++) {
                if (o1.charAt(i) > o2.charAt(i)) return 1;
                else if (o1.charAt(i) < o2.charAt(i)) return -1;
            }
            return 0;
        }
    }
}

public class Test {
    
    private long startTime;
    private long endTime;
    
    String fileName, path;
    StringBuilder sb; 

    StringCompare strCmp = new StringCompare();

    public Test(String fileName, String path) {
        this.fileName = fileName;
        this.path = path;
    }

    public Test() {
    }

    public void CodeVSWeka(String wekaRes, String codeRes) throws Exception {
        startTime = System.currentTimeMillis();
        
        
        String codeOut = normalizeCodeOutput(codeRes, -1);
        String wekaOut = normalizeWekaOutput(wekaRes);
        
        compareLine(wekaOut, codeOut);
        
        endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Compare time: ~" + duration + "ms");
    }
    
    public void CodeVSTool(String toolRes, String codeRes, int minS) throws Exception {
        startTime = System.currentTimeMillis();
        
        String codeOut = normalizeCodeOutput(codeRes, -1);
        String toolOut = normalizeCodeOutput(toolRes, minS);
        
        compareLine(toolOut, codeOut);
        
        endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Compare time: ~" + duration + "ms");
    }
    
    public String getFilename(String s) {
        int it1 = 0, it2 = 0;
        for (int i = s.length() - 1; i >= 0; --i) {
            if (s.charAt(i)== '.') it2 = i;
            if (s.charAt(i) == '\\') {
                it1 = i + 1;
                break;
            }
        }
        path = s.substring(0, it1);
        return s.substring(it1, it2);
    }
    
    public String normalizeWekaOutput(String input) throws Exception {
        
        ArrayList<String> list = new ArrayList<>();
        sb = new StringBuilder("");
        
        try {
            File f = new File(input);
            Scanner fi = new Scanner(f);

            if (!f.exists()) {
                System.out.println("The file does not exist! Try another file name...");
                return "";
            }
            if (f.length() == 0) {
                System.out.println("The file is empty! Add some data and try again...");
                return "";
            }

            fileName = getFilename(input);
            System.out.println("Tool output file: " + fileName + ".out");
            
            while (fi.hasNext()) {
                String line = fi.nextLine().trim().replaceAll("\\s{2,}", " ");
                if (line.isEmpty() || line.isBlank()) continue;
                
                String[] fp = line.split("\\s");
                if (fp[0].equals("Size") || fp[0].equals("Large")) continue;
                
                String sup = fp[fp.length - 1];
                String[] ext = Arrays.copyOf(fp, fp.length - 1);
                
                Arrays.sort(ext);
                
                StringBuilder cur = new  StringBuilder("");
                for (int i = 0; i < ext.length; i++) {
                    String[] items = ext[i].split("=");
                    cur.append(items[0]);
                    if (i != ext.length - 1) cur.append(" ");
                }
                cur.append(":").append(sup);
                
                list.add(cur.toString());
            }
            
            Collections.sort(list, strCmp);
            
            for (String el : list) 
                sb.append(el).append("\n");
            
            String wekaOut = path + fileName + ".out";
            writeFile(wekaOut, sb);
            
            return wekaOut;
        } catch (IOException e) {
            return "";
        }
    }
    
    public String normalizeCodeOutput(String input, int minS) throws Exception {

        ArrayList<String> list = new ArrayList<>();
        sb = new StringBuilder("");
        
        try {
            File f = new File(input);
            Scanner fi = new Scanner(f);

            if (!f.exists()) {
                System.out.println("The file does not exist! Try another file name...");
                return "";
            }
            if (f.length() == 0) {
                System.out.println("The file is empty! Add some data and try again...");
                return "";
            }
            
            fileName = getFilename(input);
            System.out.println("Code output file: " + fileName + ".out");

            while (fi.hasNext()) {
                String line = fi.nextLine().trim().replaceAll("\\s{2,}", " ");
                
                if (line.isEmpty() || line.isBlank()) continue;
                
                String[] tmp = line.split(":");
                String[] fp = tmp[0].split("\\s");
                String last = tmp[1];
                int sup = Integer.parseInt(last);
                if (minS > 0 && sup < minS) continue;
                
                Arrays.sort(fp);
                
                StringBuilder cur = new StringBuilder("");
                for (int i = 0; i < fp.length; i++) {
                    cur.append(fp[i]);
                    if (i != fp.length - 1) cur.append(" ");
                }
                cur.append(":").append(last);
                
                list.add(cur.toString());
            }
            
            Collections.sort(list, strCmp);
            for (String el : list) 
                sb.append(el).append("\n");
            
            String codeOut = path + fileName + ".out";
            writeFile(codeOut, sb);
            return codeOut;
        } catch (IOException e) {
            return "";
        }
        
    }

    public void writeFile(String name, StringBuilder data) throws IOException {
        FileWriter pw = null;
        try {
            pw = new FileWriter(new File(name));
            pw.write(data.toString());
            pw.close();
        } catch (FileNotFoundException e) {
            System.err.println("WRITE FILE FAILED");
        }
    }
    
    public static final String ANSI_RED = "\u001B[33m";
    public static final String ANSI_RESET = "\u001B[0m";
    
    public void compareLine(String sampleOut, String codeOut) throws IOException {
        if (sampleOut.isEmpty()) {
            System.out.println(Color.RED + "Missing sample output file!\nCompare result: 0%");
            return;
        }
        File file1 = new File(sampleOut);
        File file2 = new File(codeOut);
        
        double cmp = isEqual(file1.toPath(), file2.toPath());
        String res = String.format("Compare result: %.2f%%\n", cmp);
        System.out.println(ANSI_RED + res + ANSI_RESET);
    }
    
    private double isEqual(Path file1, Path file2) {
        long cnt = 0;
        HashSet<String> set1 = new HashSet<>();
        HashSet<String> set2 = new HashSet<>();
        try {
            try (BufferedReader bf1 = Files.newBufferedReader(file1);
                 BufferedReader bf2 = Files.newBufferedReader(file2)) {
                String line;
                while ((line = bf1.readLine()) != null) {
                    String[] s = line.split(":");
                    set1.add(s[0]);
                }
                while ((line = bf2.readLine()) != null) {
                    String[] s = line.split(":");
                    set2.add(s[0]);
                }
            }
        } catch (IOException e) {
        }
        
        for (String s : set1) 
            if (set2.contains(s)) ++cnt;

        double res = ((double)cnt / (double)set1.size()) * 100.0;
        return res;
    }
}
