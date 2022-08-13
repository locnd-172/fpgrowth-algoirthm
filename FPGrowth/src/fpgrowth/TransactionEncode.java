package fpgrowth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeSet;

public final class TransactionEncode {
    
    private long startTime;
    private long endTime;
    
    Scanner sc = new Scanner(System.in);
    
    int N, M;
    
    // key: TID | value: itemID
    private HashMap<String, ArrayList<String>> transaction;
    private ArrayList<ArrayList<String>> listItem = new ArrayList<>();
    
    public String input, fileName, path, suffix;
    
    private TreeSet<String> attribute = new TreeSet<>(); 
    private ArrayList<String> atb = new ArrayList<>();
    private ArrayList<ArrayList<String>> table = new ArrayList<>();

    public TransactionEncode(String input) throws Exception {
        this.input = input;
    }
    
    public void encode() throws Exception {
        startTime = System.currentTimeMillis();
        
        loadData();
        buildTable();
        writeWekaFile();
        writeTextFile();
        
        endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Encode input time: ~" + duration + "ms");
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
        suffix = s.substring(it2);
        return s.substring(it1, it2);
    }
    
    public void loadData() throws Exception {
        fileName = getFilename(input);
        System.out.println("File name: \t" + fileName + suffix);
        System.out.println("Converted file: " + fileName + ".inp");
        File f = new File(input);
        Scanner fi = new Scanner(f);
        transaction = new HashMap<>();
        
        try {
            if (!f.exists())
                throw new Exception("The file does not exist! Try another file name...");
            if (f.length() == 0)
                throw new Exception("The file is empty! Add some data and try again...");
            
            while (fi.hasNext()) {
                String[] s = fi.nextLine().split(",");
                String TID = s[0].trim();
                TID = TID.replaceAll("\\s", "_");
                String itemId = s[1].trim();
                itemId = itemId.replaceAll("\\s", "_");
                ArrayList<String> curItems;
                if (!transaction.containsKey(TID)) {
                    curItems = new ArrayList<>();
                    curItems.add(itemId);
                } else {
                    curItems = transaction.get(TID);
                    curItems.add(itemId);
                }
                attribute.add(itemId);
                transaction.put(TID, curItems);
            }
            
        } catch (IOException e) {
        }
    }
    
    public void buildTable() {
        N = transaction.size();
        M = attribute.size();
        
        System.out.println("Number of transactions: " + N);
        System.out.println("Number of items: " + M);
        
        transaction.entrySet().forEach(entry -> {
            ArrayList<String> items = entry.getValue();
            Collections.sort(items);
            listItem.add(items);
        });
        
        // set to array {attribute ~ item}
        for (String str : attribute) {
            atb.add(str);
        }
        
        // build t/f table
        for (int i = 0; i < N; ++i) {
            table.add(new ArrayList<>());
            for (int j = 0; j < M; ++j) 
                table.get(i).add("?");
            for (int j = 0; j < listItem.get(i).size(); ++j) {
                int pos = atb.indexOf(listItem.get(i).get(j));
                table.get(i).set(pos, "true");
            }
        }
    }
    
    public void writeWekaFile() {
        StringBuilder sb = new StringBuilder("@relation retail\n\n");
        
        for (String str : atb) {
            sb.append("@attribute ").append(str).append(" {true}\n");
        }
        
        sb.append("\n@data\n\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                sb.append(table.get(i).get(j));
                if (j != M - 1) sb.append(",");
            }
            sb.append("\n");
        }
        String file = path + fileName + ".arff";
        File f = new File(file);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(f);
            pw.print(sb);
            pw.close();
            //System.out.println("WRITE .arff FILE SUCCESSFUL!");
        } catch (FileNotFoundException e) {
            System.err.println("WRITE FILE FAILED");
        }
      
    }
    
    public void writeTextFile() {
        String s = "";
        StringBuilder sb = new StringBuilder("");
        for (ArrayList<String> el : listItem) {
            for (String str : el) {
                sb.append(str).append(" ");
            }
            sb.append("\n");
        }
        
        String file = path + fileName + ".inp";
        File f = new File(file);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(f);
            pw.print(sb);
            pw.close();
            //System.out.println("WRITE .inp FILE SUCCESSFUL!");
        } catch (FileNotFoundException e) {
            System.err.println("WRITE FILE FAILED");
        }
    }
}
