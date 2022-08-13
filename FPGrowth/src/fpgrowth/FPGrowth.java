package fpgrowth;

public class FPGrowth {

    static int relMinSup = 0;
    
    public static void convertInpFiles(String inp) throws Exception {
        System.out.println("CONVERT INPUT FILES");
        TransactionEncode en = new TransactionEncode(inp);
        en.encode();
        System.out.println("------------------------------");
    }
    
    public static String runFPGrowth(String path, String fileName, double minSupp) {
        System.out.println("RUN FP GROWTH");
        
        FPGrowthLib lib = new FPGrowthLib();
        String input = path + fileName + ".inp";
        String output = path + fileName + ".out";
        lib.execution(input, output, minSupp);
        relMinSup = lib.relativeMinsupp;
        System.out.println("------------------------------");
        return output;
    }
    
    public static void compareWeka(String path, String fileName, String out) throws Exception {
        System.out.println("COMPARING RESULT");
        
        Test t = new Test();
        String wekaRes = path + fileName + "_tool.ou";
        t.CodeVSWeka(wekaRes, out);
    }
    
    public static void compareSPMF(String path, String fileName, String out) throws Exception {
        System.out.println("COMPARING RESULT");
        
        Test t = new Test();
        String toolRes = path + fileName + "_tool.ou";
        t.CodeVSTool(toolRes, out, relMinSup);
    }
    
    public static int[] getPos(String s) {
        int it1 = 0, it2 = 0;
        for (int i = s.length() - 1; i >= 0; --i) {
            if (s.charAt(i)== '.') it2 = i;
            if (s.charAt(i) == '\\') {
                it1 = i + 1;
                break;
            }
        }
        int[] res = {it1, it2};
        return res; 
    }
    
    public static void main(String[] args) throws Exception {
       
        test1();
        test2();
        test3();
        test4(); // sweden
        test5(); // portugal
        test6(); // france
        test7(); // UK
        
    }
    
    public static void test1() throws Exception {
        System.out.println("TEST #1:");
        String inp = "test\\test\\test1.csv";
        double minSupp = 0.5;
        
        String fileName, path;
        int[] pos = getPos(inp);
        
        path = inp.substring(0, pos[0]);
        fileName = inp.substring(pos[0], pos[1]);
        
        convertInpFiles(inp);
        String out = runFPGrowth(path, fileName, minSupp);
        compareWeka(path, fileName, out);
        
        System.out.println("______________________________\n\n");
    }
    
    public static void test2() throws Exception {
        System.out.println("TEST #2:");
        String inp = "test\\test\\test2.csv";
        double minSupp = 0.4;
        
        String fileName, path;
        int[] pos = getPos(inp);
        
        path = inp.substring(0, pos[0]);
        fileName = inp.substring(pos[0], pos[1]);
        
        convertInpFiles(inp);
        String out = runFPGrowth(path, fileName, minSupp);
        compareWeka(path, fileName, out);
        
        System.out.println("______________________________\n\n");
    }
    
    public static void test3() throws Exception {
        System.out.println("TEST #3:");
        String inp = "test\\test\\test3.csv";
        double minSupp = 0.2;
        
        String fileName, path;
        int[] pos = getPos(inp);
        
        path = inp.substring(0, pos[0]);
        fileName = inp.substring(pos[0], pos[1]);
        
        convertInpFiles(inp);
        String out = runFPGrowth(path, fileName, minSupp);
        compareWeka(path, fileName, out);
        
        System.out.println("______________________________\n\n");
    }
    
    public static void test4() throws Exception {
        System.out.println("TEST #4:");
        String inp = "test\\test\\test4.csv";
        double minSupp = 0.05;
        
        String fileName, path;
        int[] pos = getPos(inp);
        
        path = inp.substring(0, pos[0]);
        fileName = inp.substring(pos[0], pos[1]);
        
        convertInpFiles(inp);
        String out = runFPGrowth(path, fileName, minSupp);
        compareSPMF(path, fileName, out);
        
        System.out.println("______________________________\n\n");
    }
    
    public static void test5() throws Exception {
        System.out.println("TEST #5:");
        String inp = "test\\test\\test5.csv";
        double minSupp = 0.05;
        
        String fileName, path;
        int[] pos = getPos(inp);
        
        path = inp.substring(0, pos[0]);
        fileName = inp.substring(pos[0], pos[1]);
        
        convertInpFiles(inp);
        String out = runFPGrowth(path, fileName, minSupp);
        compareSPMF(path, fileName, out );
        
        System.out.println("______________________________\n\n");
    }
    
     public static void test6() throws Exception {
        System.out.println("TEST #6:");
        String inp = "test\\test\\test6.csv";
        double minSupp = 0.05;
        
        String fileName, path;
        int[] pos = getPos(inp);
        
        path = inp.substring(0, pos[0]);
        fileName = inp.substring(pos[0], pos[1]);
        
        convertInpFiles(inp);
        String out = runFPGrowth(path, fileName, minSupp);
        compareSPMF(path, fileName, out );
        
        System.out.println("______________________________\n\n");
    }
     
    public static void test7() throws Exception {
        System.out.println("TEST #7:");
        String inp = "test\\test\\test7.csv";
        double minSupp = 0.05;
        
        String fileName, path;
        int[] pos = getPos(inp);
        
        path = inp.substring(0, pos[0]);
        fileName = inp.substring(pos[0], pos[1]);
        
        convertInpFiles(inp);
        String out = runFPGrowth(path, fileName, minSupp);
        compareSPMF(path, fileName, out );
        
        System.out.println("______________________________\n\n");
    }
}
