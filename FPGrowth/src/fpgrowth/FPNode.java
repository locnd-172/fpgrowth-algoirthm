package fpgrowth;

import java.util.HashMap;

public class FPNode {
    String item = null;
    int supportCnt = 0;
    
    HashMap<String, FPNode> child = new HashMap<>();
    FPNode pre = null;
    FPNode nxt = null;
    
    public FPNode() {}
    
    public FPNode(String itemID) {
        this.item = itemID;
    } 
}
