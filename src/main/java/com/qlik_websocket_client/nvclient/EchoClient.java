package com.qlik_websocket_client.nvclient;

/*
 * Copyright (C) 2015 Neo Visionaries Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
import java.io.*;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import com.neovisionaries.ws.client.*;


/**
 * A sample WebSocket client application using nv-websocket-client
 * library.
 *
 * <p>
 * This application connects to the echo server on websocket.org
 * ({@code ws://echo.websocket.org}) and repeats to (1) read a
 * line from the standard input, (2) send the read line to the
 * server and (3) print the response from the server, until
 * {@code exit} is entered.
 * </p>
 *
 * @see <a href="https://github.com/TakahikoKawasaki/nv-websocket-client"
 *      >nv-websocket-client</a>
 *
 * @author Takahiko Kawasaki
 */
public class EchoClient
{
	//Response from websocket.
	private static  String message = null;
    /**
     * The echo server on websocket.org.
     */
    private static final String SERVER = "wss://analytics.1viewinsights.com:4747/app";

    /**
     * The timeout value in milliseconds for socket connection.
     */
    private static final int TIMEOUT = 5000;

    /**
     * The entry point of this command line application.
     */
    public static void main(String[] args) throws Exception
    {
        // Connect to the echo server.
        WebSocket ws = connect();

        // The standard input via BufferedReader.
        BufferedReader in = getInput();

        // A text read from the standard input.
        String text;

//        // Read lines until "exit" is entered.
//        while ((text = in.readLine()) != null)
//        {
//            // If the input string is "exit".
//            if (text.equals("exit"))
//            {
//                // Finish this application.
//                break;
//            }
//
//            // Send the text to the server.
//            System.out.println("before sending text to server");		
//            		ws.sendText(text);
//            System.out.println("after sending text to server ");		
//        }
        if(ws.isOpen()) {
        		System.out.println("starting hits.-------------");
		        getDocList(ws);
		        Thread.sleep(3000);
		        openDoc(ws);
		        Thread.sleep(3000);
		        getAllInfos(ws);
		        Thread.sleep(3000);
		        getObject(ws);
		        Thread.sleep(3000);
		        exportData(ws);
		        Thread.sleep(3000);
		        JSONObject jsonObj = new JSONObject(message);
		        String resourceURL = "https://analytics.1viewinsights.com"+(String)jsonObj.getJSONObject("result").get("qUrl");
		        System.out.println(resourceURL);
		        URL url = new URL("https://analytics.1viewinsights.com:4243/qps/prodproxy/ticket?xrfkey=" + "7rBHABt65vFflaZ7");
			    HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
			    SSLSocketFactory sslSocketFactory = setCertificateConfiguration();
			    connection.setSSLSocketFactory(sslSocketFactory);
			    connection.setRequestProperty("x-qlik-xrfkey", "7rBHABt65vFflaZ7");
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Accept", "application/json");
				connection.setRequestMethod("POST");
				
				connection.setHostnameVerifier(new HostnameVerifier()
				{      
				    public boolean verify(String hostname, SSLSession session)
				    {
				        return true;
				    }
				});
				String userName = "GOPAL";
				String userDirectory = "QLIKSRV";
				String body = "{ 'UserId':'" + userName + "','UserDirectory':'" + userDirectory + "',";
				body += "'Attributes': [],";
				body += "}";
				System.out.println("Payload: " + body);
				
				OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
				wr.write(body);
				wr.flush(); // Get the response from the QPS BufferedReader
				BufferedReader inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuilder builder = new StringBuilder();
				String inputLine;
				while ((inputLine = inputStream.readLine()) != null) {
					builder.append(inputLine);
				}
				inputStream.close();
				String data = builder.toString();
				System.out.println("----------------------------");
				System.out.println("response from the api hit "+data);
				System.out.println("----------------------------");					
				String ticketKey = getTicketFromResponse(data);
				connection.disconnect();
				url = new URL(resourceURL+"?qlikTicket="+ticketKey);
				connection = (HttpsURLConnection)url.openConnection();
				print_content(connection);
		        
	    }  
        
        

// Close the web socket.
// ws.disconnect();
    }

    
	public static  String getTicketFromResponse(String jsonData) {
		 if(jsonData==null || jsonData.length()==0) {
			return null;
		 }
		 jsonData = jsonData.replace("{","");
		 jsonData = jsonData.replace("\"","");
		 String[] data1 = new String[7];
		 data1 = jsonData.split(",");
		
		List<TicketDto> ticketData = new ArrayList<TicketDto>();
		String ticketKey = "";
		for(int i = 0 ;i<data1.length;i++){
			TicketDto dto = new TicketDto();
			String[] obj = new String[2];
			obj = data1[i].split(":");
			dto.setKey( obj[0]);
			dto.setVal(obj[1]);
			
			ticketData.add(dto);
			if(i==3){
				ticketKey = obj[1];
			}
		}
		return ticketKey;
	}

    /**
     * Connect to the server.
     */
    private static WebSocket connect() throws Exception
    {
        WebSocket ws =  new WebSocketFactory()
            .setConnectionTimeout(TIMEOUT)
            .setSSLSocketFactory(setCertificateConfiguration())
            .setVerifyHostname(false)
            .createSocket(SERVER)
            .addListener(new WebSocketAdapter() {
                // A text message arrived from the server.
                public void onTextMessage(WebSocket websocket, String message) {
                    System.out.println("--------------------------------before response----------------------------");
                	System.out.println(message);
                	EchoClient.message = message;
                	System.out.println("--------------------------------after response------------------------------");
                }
                public void onConnected(WebSocket websocket, java.util.Map headers) {
                	System.out.println("websocket connected......");
                	
                }
                public void onMessageError(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketException cause, java.util.List frames) throws java.lang.Exception {
                	System.out.println("onmessage error handler :-");
                }
                
                public void onSendError(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketException cause, com.neovisionaries.ws.client.WebSocketFrame frame) throws java.lang.Exception {
                	System.out.println("onsend error handler :-");
                }
                
                public void onBinaryMessage(com.neovisionaries.ws.client.WebSocket websocket, byte[] binary) throws java.lang.Exception {
                	System.out.println("on binary message :-");
                }
                
                public void onDisconnected(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketFrame serverCloseFrame, com.neovisionaries.ws.client.WebSocketFrame clientCloseFrame, boolean closedByServer) throws java.lang.Exception {
                	System.out.println("--------------  on disconnected :-");
                }
                
            })
            .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
            .addHeader("X-Qlik-User","UserDirectory=QLISRV; UserId=GOPAL")
            .connect();
        System.out.println(ws.getAgreedProtocol() +"  "+ws.isOpen());
        if(ws.isOpen()) {
        	System.out.println("websocket is open ");
        }
        return ws;
    }

    
    public static  SSLSocketFactory setCertificateConfiguration() {
					SSLSocketFactory sslSocketFactory = null;
					String qlikCertificatePath = "E:/opt/certificates/analytics.1viewinsights.com/";
					String qlikPassword = "testtest";
						try {
								/************** BEGIN Certificate Acquisition **************/
								String certFolder = qlikCertificatePath.trim(); // This is a folder reference to
								String proxyCert = certFolder + "client.jks"; // Reference to the
								String proxyCertPass = qlikPassword.trim(); // This is the password to access
								String rootCert = certFolder + "root.jks"; // Reference to the root
								String rootCertPass = qlikPassword.trim(); // This is the password to access
								System.out.println("certificate configured---------------------------------------");									
								/************** END Certificate Acquisition **************/
								/************** BEGIN Certificate configuration for use in connection **************/
								KeyStore ks = KeyStore.getInstance("JKS");
								ks.load(new FileInputStream(new File(proxyCert)),
										proxyCertPass.toCharArray());
								KeyManagerFactory kmf = KeyManagerFactory
										.getInstance(KeyManagerFactory.getDefaultAlgorithm());
								kmf.init(ks, proxyCertPass.toCharArray());
								SSLContext context = SSLContext.getInstance("SSL");
								KeyStore ksTrust = KeyStore.getInstance("JKS");
								ksTrust.load(new FileInputStream(rootCert),
										rootCertPass.toCharArray());
								TrustManagerFactory tmf = TrustManagerFactory
										.getInstance(TrustManagerFactory.getDefaultAlgorithm());
								tmf.init(ksTrust);
								context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
								sslSocketFactory = context.getSocketFactory();
								/************** END Certificate configuration for use in connection **************/
					    }	catch (KeyStoreException e) { e.printStackTrace(); } 
					catch (IOException e) { e.printStackTrace(); } 
					catch (CertificateException e) { e.printStackTrace(); } 
					catch (NoSuchAlgorithmException e) { e.printStackTrace(); } 
					catch (UnrecoverableKeyException e) { e.printStackTrace(); } 
					catch (KeyManagementException e) { e.printStackTrace(); } 
			return sslSocketFactory;
			}
    

    /**
     * Wrap the standard input with BufferedReader.
     */
    private static BufferedReader getInput() throws IOException
    {
        return new BufferedReader(new InputStreamReader(System.in));
    }
    
    
    //This method is going to return only published apps
    public static void getDocList(WebSocket ws) {
    				System.out.println("---------------------GETDOCLIST----------------");
			    	JSONObject msg = new JSONObject();
			        msg.put("jsonrpc", "2.0");
					msg.put(	"id", 1);
			        msg.put(	"method", "GetDocList");
			        msg.put(	"handle", -1);
			        JSONArray jsonArr = new JSONArray();
			        msg.put("params", jsonArr);
			        ws.sendText(msg.toString());
			        
			       
    }
   
    
    public static void openDoc(WebSocket ws) {
    		System.out.println("---------------------------------------OPEN DOC----------------------");
    		JSONObject msg = new JSONObject();
    		msg.put("method", "OpenDoc");
    		msg.put("handle", -1);
    		JSONArray jsonArray = new JSONArray();
    		jsonArray.put("d9a01504-d14d-42dc-abe6-75f0824a18db");
    		msg.put("params",jsonArray);
    		msg.put("outKey", -1);
    		msg.put("jsonrpc", "2.0");
    		msg.put("id", 2);
    		ws.sendText(msg.toString());
    }
    
    public static void getAllInfos(WebSocket ws) {
    		System.out.println("---getALLINFOS-----");
    			JSONObject msg = new JSONObject();
    			msg.put("handle", 1);
    			msg.put("method", "GetAllInfos");
    			JSONArray jsonArray = new JSONArray();
    			System.out.println(jsonArray);
    			msg.put("params", jsonArray);
    			msg.put("outKey", -1);
    			msg.put("jsonrpc", "2.0");
    			msg.put("id", 3);
    			ws.sendText(msg.toString());
    	
    }
    
    public static void getObject(WebSocket ws) {
    			System.out.println("---getObject-----");
    			JSONObject msg = new JSONObject();
    			msg.put("handle",1);
    			msg.put("method","GetObject");
    			msg.put("params" , new JSONObject().put("qId","CXgReb"));
		    	msg.put("outKey", -1);
		    	msg.put("jsonrpc", "2.0");
		    	msg.put("id", 4);
		    	ws.sendText(msg.toString());
		    	
    }
    
    public static void exportData(WebSocket ws) {
			    	System.out.println("-------------------------- Export Data -------------------");
			    	JSONObject msg = new JSONObject();
			    	msg.put("handle", 2);
			    	msg.put("method", "ExportData");
			    	JSONObject params = new JSONObject();
			    	params.put("qFileType", 0);
			    	params.put("qPath", "/qHyperCubeDef");
			    	params.put("qFileName", "abc.csv");
			    	params.put(	"qExportState", 0);
			    	msg.put("params",params); 
			        ws.sendText(msg.toString());	
    }
    
   
    private static void print_content(HttpsURLConnection con){
    	if(con!=null){

    	try {

    	   System.out.println("****** Content of the URL ********");
    	   BufferedReader br =
    		new BufferedReader(
    			new InputStreamReader(con.getInputStream()));

    	   String input;

    	   while ((input = br.readLine()) != null){
    	      System.out.println(input);
    	   }
    	   br.close();

    	} catch (IOException e) {
    	   e.printStackTrace();
    	}

           }

       }

    public static class TicketDto {
    	
    	private String key;
    	private String val;
    	public String getKey() {
    		return key;
    	}
    	public void setKey(String key) {
    		this.key = key;
    	}
    	public String getVal() {
    		return val;
    	}
    	public void setVal(String val) {
    		this.val = val;
    	}


    	
    }

    
}