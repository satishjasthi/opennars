package nars.core;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * runs a subset of the test cases, selected by the boolean include(filename) function
 */
@RunWith(Parameterized.class)
public class NALTestSome extends NALTest {

    static {
        showOutput = false;
        showSuccess = showOutput;
        showTrace = false;               
    }
    
   public static boolean include(String filename) {
       return filename.startsWith("nal6.2.nal");
   }

   
    @Parameterized.Parameters
    public static Collection params() {
        List l = new LinkedList();
        
        File folder = new File("nal/test");
        
        for (final File file : folder.listFiles()) {
            if (file.getName().equals("README.txt"))
                continue;
            if (include(file.getName()))
                l.add(new Object[] { file.getAbsolutePath() } );
        }
                  
        return l;
    }
   
   public static void main(String[] args) {
        org.junit.runner.JUnitCore.runClasses(NALTestSome.class);
   }    

   public NALTestSome(String scriptPath) {
       super(scriptPath);//, true);

   }
//   
//   public NALTestSome(String scriptPath, boolean showOutput) {
//        super(scriptPath);
//        
//        this.showSuccess = showOutput;
//        this.showOutput = showOutput;
//        this.showTrace = false;
//        System.out.println("Running: "  + scriptPath);
//        //setOutput(true);
//   }
   
}
