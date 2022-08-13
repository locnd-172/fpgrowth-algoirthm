package fpgrowth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class FPTree {
    FPNode root;
    int supportCnt = 0;
    
    public FPTree() {
        root = new FPNode();
    }
    
    private FPNode insert(FPNode cur, ArrayList<String> arr, int id, HashMap<String, LinkedList<FPNode> > headerTable) {
        if(arr.size() == id) return cur;
        
        String product = arr.get(id);
        
        if(cur.child.get(product) != null )
            cur.child.get(product).supportCnt++;
        else {
            cur.child.put(product, new FPNode(product));
            cur.child.get(product).supportCnt = 1;
            cur.child.get(product).pre = cur;
            headerTable.get(product).add(cur.child.get(product));
        }
        if(id + 1 < arr.size())
            cur.child.put(product, insert(cur.child.get(product), arr, id + 1, headerTable));
        
        return cur;
    }
    
    public void insert(ArrayList<String> transaction, HashMap<String, LinkedList<FPNode> > headerTable) {
        root = insert(root, transaction, 0, headerTable);
    }
    
    public ArrayList<FPNode> reverse(FPNode node) {
        ArrayList<FPNode> result = new ArrayList<>();
        
        while(node != null) {
            result.add(node);
            node = node.pre;
        }
        
        return result;
    }
}
