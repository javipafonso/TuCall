package com.todoaplicaciones.tucall;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcSunHttpTransportFactory;
import org.apache.xmlrpc.serializer.XmlWriterFactory;
import org.apache.xmlrpc.webserver.Connection;
import org.xmlpull.v1.XmlPullParser;
import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;
import org.xmlrpc.android.XMLRPCFault;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.util.Xml;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class SendMessage extends Activity{
	
	
    private URI uri;
    private ListView lv;
	private ArrayAdapter<String> adaptador;
	private Vector<String> userprofile;
	private Socket socket;
	private XMLRPCClient client;
	//private static final int SERVERPORT = 8082;
	//private static final String SERVER_IP = "concurso.tucall.com";
	
	

	interface XMLRPCMethodCallback {
		void callFinished(Object result);
	}

	class XMLRPCMethod extends Thread {
		private String method;
		private Object[] params;
		private Handler handler;
		private XMLRPCMethodCallback callBack;
		
		public XMLRPCMethod(String method, XMLRPCMethodCallback callBack) {
			this.method = method;
			this.callBack = callBack;
			handler = new Handler();
		}
		public void call() {
			call(null);
		}
		public void call(Object[] params) {
			//txt_name.setTextColor(0xff80ff80);
			//txt_name.setError(null);
			Toast.makeText(getApplicationContext(),"Calling host " + uri.getHost(), Toast.LENGTH_LONG).show();
			//txt_name.setText("Calling host " + uri.getHost());
			//txt_name.setEnabled(false);
			this.params = params;
			start();
		}
		@Override
		public void run(){
			try {
    			final long t0 = System.currentTimeMillis();
    			final Object result = client.callEx(method, params);
    			final long t1 = System.currentTimeMillis();
    			handler.post(new Runnable() {
					public void run() {
					//	tests.setEnabled(true);
					//	txt_name.setText("XML-RPC call took " + (t1-t0) + "ms");
						Toast.makeText(getApplicationContext(), "XML-RPC call took " + (t1-t0) + "ms", Toast.LENGTH_LONG).show();
						callBack.callFinished(result);
					}
    			});
    		} catch (final XMLRPCFault e) {
    			handler.post(new Runnable() {
					public void run() {
						//testResult.setText("");
						//tests.setEnabled(true);
						//txt_name.setTextColor(0xffff8080);
						//txt_name.setError("", errorDrawable);
						//txt_name.setText("Fault message: " + e.getFaultString() + "\nFault code: " + e.getFaultCode());
						Toast.makeText(getApplicationContext(), "Fault message: " + e.getFaultString() + "\nFault code: " + e.getFaultCode(), Toast.LENGTH_LONG).show();
						Log.d("Test", "error", e);
					}
    			});
    		} catch (final XMLRPCException e) {
    			handler.post(new Runnable() {
					public void run() {
					//	testResult.setText("");
					//	tests.setEnabled(true);
					//	txt_name.setTextColor(0xffff8080);
					//	txt_name.setError("", errorDrawable);
						Throwable couse = e.getCause();
						if (couse instanceof HttpHostConnectException) {
							Toast.makeText(getApplicationContext(), "Cannot connect to " + uri.getHost() + "\nMake sure server.py on your development host is running !!!", Toast.LENGTH_LONG).show();
							//txt_name.setText("Cannot connect to " + uri.getHost() + "\nMake sure server.py on your development host is running !!!");
						} else {
							Toast.makeText(getApplicationContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
						//	txt_name.setText("Error " + e.getMessage());
						}
						Log.d("Test", "error", e);
					}
    			});
    		}
		
		}
		
		
	}		
		

	/* 
	 * Procedimiento donde enviamos el mensaje al servidor 
	 */
	
	private void SendMessage(Vector<String> userpf)  {
			
	/*		XmlRpcClient client = new XmlRpcClient();
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			try {
				config.setEncoding("UTF-8");
				config.setBasicUserName("demo");
				config.setBasicPassword("demo");
				//config.setServerURL(new URL("http://www.controlpyme.com/consulta/consulta1.php"));
				config.setServerURL(new URL("https","concurso.tucall.com",8082,""));
				config.setEnabledForExtensions(true);
				//config.getXmlRpcServer();
				Toast.makeText(getApplicationContext(), "config, primer try " + config.getServerURL(), Toast.LENGTH_LONG).show();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				Toast.makeText(getApplicationContext(), "config, dentro del exception primer try " + e.toString(), Toast.LENGTH_LONG).show();
			}
			client.setConfig(config);
			//client.setTransportFactory(new XmlRpcSunHttpTransportFactory(client));
			Toast.makeText(getApplicationContext(), "Configuración" + client.getClientConfig(),Toast.LENGTH_LONG).show();*/
			//uri = URI.create("http://www.controlpyme.com/consulta/consulta1.php");
			uri = URI.create("https://concurso.tucall.com:8082");
			
			client = new XMLRPCClient(uri);
			client.setBasicAuthentication("demo", "demo");
				
			XMLRPCMethod method = new XMLRPCMethod("concurso.movil", new XMLRPCMethodCallback() {
				
				@Override
				public void callFinished(Object result) {
					Toast.makeText(getApplicationContext(), "El Resultado obtenido es " + result.toString(), Toast.LENGTH_LONG).show();// TODO Auto-generated method stub
					
				}
			});
			
			
			Object[] params = new Object[]{"jorge","prueba","alba"};
			//Object[] params = new Object[]{new Integer(2)};
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
					// TODO Auto-generated method stub
					
				}

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return null;
				}
			}
			};
		
		
			// Install the all-trusting trust manager
			SSLContext sc = null;
			try {
				sc = SSLContext.getInstance("SSL");
				//Toast.makeText(getApplicationContext(), "SSL 1 ", Toast.LENGTH_LONG).show();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				Toast.makeText(getApplicationContext(), "SSL " + e.toString(), Toast.LENGTH_LONG).show();
			}
			HostnameVerifier hv = new HostnameVerifier() {

				@Override
				public boolean verify(String hostname, SSLSession session) {
					// TODO Auto-generated method stub
					//	Toast.makeText(getApplicationContext(), "VERIFIED ", Toast.LENGTH_LONG).show();
					return true; 	
				}
				};
				try {
					sc.init(null, trustAllCerts, new java.security.SecureRandom());
					//Toast.makeText(getApplicationContext(), "SSL 2 ", Toast.LENGTH_LONG).show();
				} catch (KeyManagementException e) {
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(), "SC 2 " + e.toString(), Toast.LENGTH_LONG).show();
				}
						
				
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(hv);
				
				//method.call(params);
				
				Object Result = null;
				Toast.makeText(getApplicationContext(), "RESULTADO ANTES DE EJECUTAR: " + Result, Toast.LENGTH_LONG).show(); 
				method.call(params);
				//Result = client.callEx("concurso.movil", params);
				//Toast.makeText(getApplicationContext(), "RESULTADO DENTRO DEL TRY: " + Result, Toast.LENGTH_LONG).show();
		
	}
	 
	/*
	 * Función donde leemos el fichero de usuario y devolvemos un string
	 */
    private Vector<String> rFileXML() {
    	
    	FileInputStream fin = null;
    	Vector<String> vector=new Vector<String>(2);
    	try {
    		File File_path = Environment.getExternalStorageDirectory();
    		File File = new File(File_path.getAbsolutePath(),"user_register.xml");
    		fin = new FileInputStream(File);
    	} catch (Exception e) {
    		Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    	}

    	XmlPullParser parser = Xml.newPullParser();
    	try {
    		parser.setInput(fin, "UTF-8");
    		int event = parser.next();
    		while(event != XmlPullParser.END_DOCUMENT) {
    			//if(event == XmlPullParser.START_TAG) {
    			//	Log.d(TAG, "<" + parser.getName() + ">");
    			//	for(int i = 0; i < parser.getAttributeCount(); i++) {
    			//		Log.d(TAG, "\t" + parser.getAttributeName(i) + " = " + parser.getAttributeValue(i));
    			//	}
    		//	}
    			if(event == XmlPullParser.TEXT && parser.getText().trim().length() != 0){
    				vector.add(parser.getText());//Log.d(TAG, "\t\t" + parser.getText());
    			
    			//	k++;
    			}
    			    		//	if(event == XmlPullParser.END_TAG)
    		//		Log.d(TAG, "</" + parser.getName() + ">");

    			event = parser.next();
    		}
    		fin.close();

    	//	Toast.makeText(this, "Leido correctamente", Toast.LENGTH_LONG).show();
    	} catch (Exception e) {
    		Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    	}
		return vector;
    }
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_lview);
        Bundle reicieveParams = this.getIntent().getExtras();
	    		
        userprofile = rFileXML();
    	userprofile.add(reicieveParams.getString("user"));
        
        lv = (ListView) findViewById(R.id.listView1);
		lv.setFastScrollEnabled(true);
        lv.setTextFilterEnabled(true);
    	
        String[] userpf = {reicieveParams.getString("user"),reicieveParams.getString("phone")};
        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userpf);
		lv.setAdapter(adaptador);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		
		SendMessage(userprofile);
		
		
        /*int duration = Toast.LENGTH_LONG;
	    Toast toast = Toast.makeText(getBaseContext(), "Position" + " " + userprofile[0], duration);
 	   	toast.show();*/
      //  txt_name = (TextView) findViewById(R.id.textView1);
      //  txt_name.setText(reicieveParams.getString("user"));
		   
        
	}
	
         
      
		
}
