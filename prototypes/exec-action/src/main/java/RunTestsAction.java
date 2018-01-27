import java.io.InputStream;

import com.google.gson.JsonObject;

public class RunTestsAction {
    
    public static JsonObject main(JsonObject args) {
        return runTests("tests.jar");
    }
    
    private static JsonObject runTests(String jarPath) {
        try {
            Process proc = Runtime.getRuntime().exec("java -jar " + jarPath);
            int code = proc.waitFor();
            InputStream in = proc.getInputStream();
            InputStream err = proc.getErrorStream();
    
            String stdLog, errLog;
            byte b[]=new byte[in.available()];
            in.read(b,0,b.length);
            stdLog = new String(b);
            byte c[]=new byte[err.available()];
            err.read(c,0,c.length);
            errLog = new String(c);
            
            return formatResults(code, stdLog, errLog);
        } catch(Exception e) {
            return formatResults(1, "", "Test execution failed");
        }
    }
        
    private static JsonObject formatResults(int resultCode, String std, String err) {
        JsonObject response = new JsonObject();
        response.addProperty("result", resultCode);
        response.addProperty("std", std);
        response.addProperty("err", err);
        return response;
    }
    
}