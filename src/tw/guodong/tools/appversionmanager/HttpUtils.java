package tw.guodong.tools.appversionmanager;

import java.io.IOException;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtils {		//負責從網路上取得內容
	
	static public String getUrlData(String url) {	//(輸入: URL   輸出:String)
		String data = "";
		HttpEntity entity = null;
		try {
			entity = requestInputStream(url);
			if(entity !=null){
				data = EntityUtils.toString(entity); 
			}
		}catch (Exception e) {
			Log.v("iMusee", "getUrlData: "+e.toString());
		}
		return data;
	}

	//用URL取得HttpEntity
	static private HttpEntity requestInputStream(String url) throws ClientProtocolException, IOException {  
		HttpEntity httpEntity = null;  
        HttpGet httpGet = new HttpGet(url);
	    String acceptLanguage = Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry();
	    httpGet.setHeader(new BasicHeader("Accept-Language",acceptLanguage));
        HttpClient httpClient = new DefaultHttpClient();  
        HttpResponse httpResponse = httpClient.execute(httpGet);  
        int httpStatusCode = httpResponse.getStatusLine().getStatusCode(); 
        if(httpStatusCode == HttpStatus.SC_OK) {  
            httpEntity = httpResponse.getEntity();  
        }  
        return httpEntity;  
    }  
}
