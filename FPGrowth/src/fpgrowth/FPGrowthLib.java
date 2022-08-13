package fpgrowth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class FPGrowthLib {
    int cntTransaction, cntItems;
    
    public int relativeMinsupp;
    ArrayList<ArrayList<String> > itemsets = new ArrayList< >();
    HashMap<String, Integer> itemSupport = new HashMap<>();
    HashMap<String, LinkedList<FPNode> > headerTable = new HashMap<>();
    long startTime, endTime;
    
    FPTree myFPTree;
    
    public FPGrowthLib() {
        myFPTree = new FPTree();
    }
    
    String input, output;
    
    public void execution(String inp, String out, double minSupp) {
        startTime = System.currentTimeMillis();
        
        this.input = inp;
        this.output = out;
        
        readData();
        
        this.relativeMinsupp = (int) Math.ceil((double)minSupp * cntTransaction);
        
        calcFrequency();
        prepareData();
        buildFPTree();
        runFPGrowth();
        
        endTime = System.currentTimeMillis();
        
        printResult();
    }
    
    TreeSet<String> totItem = new TreeSet<>();
    private void convert(String data) {
        String[] list = data.split(" ");
        
        ArrayList<String> items = new ArrayList<>();
        for(String item : list) {
            items.add(item.trim());
            totItem.add(item);
        }
        
        itemsets.add(items);
        cntItems = totItem.size();
        ++cntTransaction;
    }
    
    // read data, store input in a ArrayList
    private void readData() {
        File f = new File(input);
        try {
            Scanner sc = new Scanner(f);
            while(sc.hasNextLine()) {
                String data = sc.nextLine();
                convert(data);
            }
        } catch (FileNotFoundException ex) {    
            System.out.println("File not found!");
        }  
    }
    
    // (1) First scan: calculate frequency of item
    private void calcFrequency() {
        for(ArrayList<String> itemset : itemsets)
            for(String item : itemset) 
                itemSupport.merge(item, 1, (a, b) -> a + b);
    }
    
    // (2) Second scan: build list L (ordered frequent itemset) of each transaction, build Header table
    public void prepareData() {
        for(ArrayList<String> itemset : itemsets) {
            Collections.sort(itemset, (var o1, var o2) -> {
                int cmp = itemSupport.get(o2) - itemSupport.get(o1);
                return (cmp == 0 ? o1.compareToIgnoreCase(o2) : cmp);
            }); 
            
            while(!itemset.isEmpty() && itemSupport.get(itemset.get(itemset.size() - 1)) < relativeMinsupp) 
                itemset.remove(itemset.size() - 1);
            
            for(String item : itemset) 
                if(headerTable.get(item) == null) 
                    headerTable.put(item, new LinkedList<>());
            }
    }
    
    // (3) Recursively add item in L to FP-tree
    public void buildFPTree() {
        for(int i = 0;i < cntTransaction;++i)
            myFPTree.insert(itemsets.get(i), headerTable);
    }
   
    ArrayList<ArrayList<String> > itemPrefixPaths = new ArrayList<>(); 
     // ~ conditional pattern base of item i
    
    ArrayList<Integer> prefixPathSupp = new ArrayList<>();
    ArrayList<ArrayList<String> > freqItemset = new ArrayList<>();
    ArrayList<Integer> freqResult = new ArrayList<>();
    
    void getFreqPattern(Set<String> list, String str, int freqCount) {
        Iterator<String> ptr = list.iterator();
        
        String tmp = null;
        ArrayList<String> ans = new ArrayList<>();
        
        while(ptr.hasNext()) {
            tmp = ptr.next();
            if(tmp == str) continue;
            ans.add(tmp);
        }
        ans.add(str);
        freqItemset.add(ans);
        freqResult.add(freqCount);
    }
    
    void mineData(String curItem) {
        HashMap<String, Integer> countFreq = new HashMap<>();
        
        // Accumulate the support of each item in the CPB
        for(int i = 0;i < itemPrefixPaths.size();++i){
            ArrayList<String> prefixPath = itemPrefixPaths.get(i); 
            // frequency of item j in prefix path = frequency of its suffix (item i)
            for(String item : prefixPath)
                countFreq.merge(item, prefixPathSupp.get(i), (a, b) -> a + b);
        }
        
        // (4.2) Build S - frequent itemset retrieved from CPB
        ArrayList<String> valid = new ArrayList<>();
        for(Map.Entry<String, Integer> item : countFreq.entrySet()) {
            if(item.getValue() >= relativeMinsupp) 
                valid.add(item.getKey());
        }
        
        int sz = valid.size();
        
        // (4.3) Use bitmask to generate S' (subset of S) - mine FP-conditinal-tree
        for(int state = 1; state <= Math.pow(2, sz) - 1; ++state) {
            Set<String> comb = new HashSet<>();
            for(int j = 0;j <= sz - 1;++j)
                if((state >> j & 1) == 1) {
                    comb.add(valid.get(j));
                }
            if(!comb.contains(curItem)) continue;
            
            // (4.4) Calculate frequency of S' in CPB
            int freqCount = 0;
            for(int i = 0;i < itemPrefixPaths.size();++i) {
                ArrayList<String> prefixPath = itemPrefixPaths.get(i);
                HashSet<String> prefix = new HashSet<>(prefixPath);
                prefix.retainAll(comb);
                if(prefix.size() == comb.size()) 
                    freqCount += prefixPathSupp.get(i);
            }
            if(freqCount >= relativeMinsupp) 
                getFreqPattern(comb, curItem, freqCount);
        }
    }
    
    // Add current path to list of all prefix path of item I 
    // ~ create conditional pattern base (CPB)
    void addToContainer(ArrayList<FPNode> prefixPath) {
        ArrayList<String> pattern = new ArrayList<>();
        
        for(FPNode node : prefixPath)
            if(node.item != null)
                pattern.add(node.item);
        
        Collections.reverse(pattern); 
        itemPrefixPaths.add(pattern); // add a itemset of CPB
        prefixPathSupp.add(prefixPath.get(0).supportCnt); // support of correspond itemset
    }
    
    public void reset() {
        itemPrefixPaths.clear();
        prefixPathSupp.clear();
    }
            
    // (4) Execution of FP Growth algorithm on FP-tree
    public void runFPGrowth() {
        ArrayList<String> freqItems = new ArrayList<>(headerTable.keySet());
        
        Collections.sort(freqItems, (String o1, String o2) -> {
            int cmp = itemSupport.get(o2) - itemSupport.get(o1);
            return (cmp == 0 ? o1.compareToIgnoreCase(o2) : cmp);
        }); 
        
        // (4.1) Iterate through Header Table ascending by support, create conditional pattern base 
        for(int i = freqItems.size() - 1; i >= 0; --i) {
            reset();
            String item = freqItems.get(i);
            
            // list of node represent item I in FP-tree
            LinkedList<FPNode> nodeLinks = headerTable.get(item);
            for(FPNode node : nodeLinks) {
                ArrayList<FPNode> prefixPath = myFPTree.reverse(node);
                addToContainer(prefixPath);
            }
            
            mineData(item);
        }
    }
    
    private void printFile() {
        try {
            PrintWriter fw = new PrintWriter(output);
            int it = 0;

            StringBuilder sb = new StringBuilder("");
            for(ArrayList<String> itemset : freqItemset) {
                for (int i = 0; i < itemset.size(); i++) {
                    sb.append(itemset.get(i));
                    if (i != itemset.size() - 1) sb.append(" ");
                }
                sb.append(":").append(freqResult.get(it++)).append("\n");
            }
            fw.write(sb.toString());
            fw.close();
        } catch (IOException ex) {
            System.out.println("Error occured");
        }
    }
    
    private void printResult() {
        printFile();
        
        long duration = endTime - startTime;
        System.out.println("Number of transactions: " + cntTransaction);
        System.out.println("Number of items: " + cntItems);
        System.out.println("Total number of frequent itemsets: " + freqItemset.size());
        System.out.println("FP Growth time: ~" + duration + "ms");
        
    }
}
