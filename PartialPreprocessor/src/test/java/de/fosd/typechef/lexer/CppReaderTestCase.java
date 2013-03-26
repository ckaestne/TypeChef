package de.fosd.typechef.lexer;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.util.Collections;

public class CppReaderTestCase {

    private void testCppReader(String in, String out)
            throws Exception {
        System.out.println("Testing " + in + " => " + out);
        StringReader r = new StringReader(in);
        CppReader p = new CppReader(r);
        p.getPreprocessor().setSystemIncludePath(
                Collections.singletonList(
                        new File(this.getClass().getResource("/input").toURI()).getAbsolutePath())
        );
        p.getPreprocessor().getFeatures().add(Feature.LINEMARKERS);
        BufferedReader b = new BufferedReader(p);

        String line;
        while ((line = b.readLine()) != null) {
            System.out.println(" >> " + line);
        }
    }

    @Test
    public void testCppReader()
            throws Exception {
        testCppReader("#include <test0.h>\n", "ab");
    }

    @Test
    public void testNone() {
    }
}
