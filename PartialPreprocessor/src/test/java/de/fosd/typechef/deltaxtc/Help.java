package de.fosd.typechef.deltaxtc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: ckaestne
 * Date: 12/12/12
 * Time: 2:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class Help {

    public static void copyStream(InputStream is, OutputStream os) throws IOException {

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        is.close();
        os.close();
    }


}
