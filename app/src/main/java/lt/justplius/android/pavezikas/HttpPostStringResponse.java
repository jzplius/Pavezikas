package lt.justplius.android.pavezikas;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class HttpPostStringResponse {
    private static final String TAG = "ServerDbQuery";

    private String result = "";

    public HttpPostStringResponse(String url, ArrayList<NameValuePair> nvp){
	    // Http post action and retrieve of response entity
        InputStream is = null;
        try{
	    	HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost(url);
            if (nvp != null) {
                httppost.setEntity(new UrlEncodedFormEntity(nvp, "UTF-8"));
            }
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();
	    } catch(Exception e){
	        Log.e(TAG, "Error in http connection: ", e);
	    }
	    
	    // Convert response to string
	    try{
	        @SuppressWarnings("ConstantConditions")
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
	        StringBuilder sb1 = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            sb1.append(line).append("\n");
	        }
	        is.close();
	            
	        result=sb1.toString();	        
	    } catch(Exception e){
	        Log.e(TAG, "Error converting result: ", e);
	    }
    }
    
	public String returnJSON (){
    	return result;
    }
}
