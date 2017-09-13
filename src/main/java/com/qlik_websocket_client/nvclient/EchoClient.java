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
import java.util.Arrays;
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

import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.BarChart;
import com.googlecode.charts4j.BarChartPlot;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.Fills;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.Plots;
import com.neovisionaries.ws.client.*;
import com.qlik_websocket_client.email.EmailSender;
import com.qlik_websocket_client.email.MailMessage;
import com.qlik_websocket_client.writer.WriteCSV;


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
    //private static final String SERVER = "wss://analytics.1viewinsights.com:4747/app";
	private static final String SERVER = "wss://analytics.1viewinsights.com:4747/app/63777428-ecc1-4fce-9b4f-97be6e2c84ec";

    /**
     * The timeout value in milliseconds for socket connection.
     */
    private static final int TIMEOUT = 5000;

    /**
     * The user details..................
     * 
     */
    //"X-Qlik-User","UserDirectory=ONECHANNELUDC; UserId=QLIK219912SACHINONECHANNEL"
    private static String userDirectory = "ONECHANNELUDC";
    private static String userId = "QLIK219912SACHINONECHANNEL";
    
    /**
     * The entry point of this command line application.
     */
    public static void main(String[] args) throws Exception {
    				workWithCharts();
	}  
        
	
   public static void workWithTable() {
	 WebSocket ws = null;
     try { 
			       ws = connect();
			       if(ws.isOpen()) {
			       		    //getDocList(ws);
					        //Thread.sleep(3000);
					        openDoc(ws);
					        Thread.sleep(3000);
					        getAllInfos(ws);
					        Thread.sleep(3000);
					        getObject(ws);
					        Thread.sleep(3000);
					        getProperties(ws);
					        Thread.sleep(3000);
					        StringBuilder  headers = new StringBuilder();
					        retreiveHeaders(headers);
					        getHyperCubeData(ws);
					        Thread.sleep(5000);
					        //converHyperCubeToTable(headers);
					        //sendMail();
					        String html = converHyperCubeToHTML(headers);
					        sendMail(html);
       	}  
     } catch(Exception e) {
    	 		e.printStackTrace();
     } finally {
    	 		if(ws != null) {
    	 			ws.disconnect();
    	 		}
     }
}

   public static void workWithCharts() {
	 WebSocket ws = null;
     try { 
			       ws = connect();
			       if(ws.isOpen()) {
			       		System.out.println("starting hits.-------------");
					        //getDocList(ws);
					        //Thread.sleep(3000);
					        openDoc(ws);
					        Thread.sleep(3000);
					        getAllInfos(ws);
					        Thread.sleep(3000);
					        getObject(ws);
					        Thread.sleep(3000);
					        getProperties(ws);
					        Thread.sleep(3000);
					        StringBuilder  headers = new StringBuilder();
					        retreiveHeaders(headers);
					        getHyperCubeData(ws);
					        Thread.sleep(5000);
					        //converHyperCubeToTable(headers);
					        //sendMail();
					        String labels[] = headers.toString().substring(1).split(",");
					        List<List<String>> hyperCubeList = convertHyperCubeToList();
					        String barChartUrl = generateBarChart(labels,hyperCubeList);
					        String html = "<br/><br/>"+"<img src='"+barChartUrl+"'  />"+"<br/><br/>";
					        sendMail(html);
       	}  
     } catch(Exception e) {
    	 		e.printStackTrace();
     } finally {
    	 		if(ws != null) {
    	 			ws.disconnect();
    	 		}
     }
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
            //.addHeader("X-Qlik-User","UserDirectory=QLIKSRV; UserId=GOPAL")
            .addHeader("X-Qlik-User","UserDirectory="+userDirectory+"; UserId="+userId+"")
            .connect();
        System.out.println(ws.getAgreedProtocol() +"  "+ws.isOpen());
        if(ws.isOpen()) {
        	System.out.println("websocket is open ");
        }
        return ws;
    }
    
    
    public static void converHyperCubeToTable(StringBuilder headers) {
    	 	List<List<String>> stringList = convertHyperCubeToList();
	        System.out.println("Size of the list "+stringList.size());
	        System.out.println("headers ---> "+headers.toString());
	        WriteCSV.writeCSV(headers,stringList);
	}

  
    public static String converHyperCubeToHTML(StringBuilder headers) {
		String strHTML = "";
		List<List<String>> stringList = convertHyperCubeToList();
        WriteCSV.writeCSV(headers,stringList);
        strHTML = WriteCSV.generateHTML(headers, stringList); 
        return strHTML;
}
    
    
    public static List<List<String>>  convertHyperCubeToList() {
    	JSONObject jsonObj = new JSONObject(message);
        JSONObject result =  jsonObj.getJSONObject("result");
        JSONObject arrayObj = (JSONObject)result.getJSONArray("qDataPages").get(0);
        JSONArray qMatrixArray = arrayObj.getJSONArray("qMatrix");
        JSONObject qAreaObject = arrayObj.getJSONObject("qArea");
        int width  = qAreaObject.getInt("qWidth");
        List<List<String>> stringList = new ArrayList<>();
        for(int i = 0; i < qMatrixArray.length(); i++) {
        			System.out.println(qMatrixArray.getJSONArray(i).toString());
        			JSONArray row = qMatrixArray.getJSONArray(i);
        			List<String> qTextList = new ArrayList<>();
        			for(int j = 0; j < width; j++) {
        					String qText = row.getJSONObject(j).getString("qText");
        					qTextList.add(qText);
        			}
        			stringList.add(qTextList);
        }
        System.out.println("Size of the list "+stringList.size());
        return stringList;
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
    		//JSONArray jsonArray = new JSONArray();
    		//63777428-ecc1-4fce-9b4f-97be6e2c84ec  Bose_nPrinting
    		//d9a01504-d14d-42dc-abe6-75f0824a18db  Bose_nPrinting_new
    		//jsonArray.put("63777428-ecc1-4fce-9b4f-97be6e2c84ec");
    		JSONObject jsonObj = new JSONObject();
    		jsonObj.put("qDocName", "63777428-ecc1-4fce-9b4f-97be6e2c84ec");
    		//jsonObj.put("qUserName", "QLIKSRV\\GOPAL");
    		msg.put("params",jsonObj);
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
    			//msg.put("params" , new JSONObject().put("qId","CXgReb"));   request for table.
    			msg.put("params" , new JSONObject().put("qId","hWArAV"));  // request for barchart.
    			msg.put("outKey", -1);
		    	msg.put("jsonrpc", "2.0");
		    	msg.put("id", 4);
		    	ws.sendText(msg.toString());
		    	
    }
    

    public static void getProperties(WebSocket ws) {
					System.out.println("------------------Get Properties----------");
					JSONObject msg = new JSONObject();
					msg.put("handle",2);
					msg.put("method","GetProperties");
					msg.put("params" , new JSONObject());
			    	msg.put("outKey", -1);
			    	msg.put("jsonrpc", "2.0");
			    	msg.put("id", 5);
			    	ws.sendText(msg.toString());    	
    }
    
    
    public static void getHyperCubeData(WebSocket ws) {
		    	System.out.println("-------------------------- GET Hypercube Data -------------------");
		    	JSONObject msg = new JSONObject();
		    	msg.put("handle", 2);
		    	msg.put("method", "GetHyperCubeData");
		    	JSONObject params = new JSONObject();
		    	params.put("qPath", "/qHyperCubeDef");
		    	JSONArray a = new JSONArray();
		    	JSONObject  b = new JSONObject();
		    	b.put("qLeft", 0);
		    	b.put("qTop", 0);
		    	b.put("qWidth", 20);
		    	b.put("qHeight", 500);
		    	a.put(b);
		    	params.put("qPages",a);
		    	msg.put("params",params);
		    	msg.put("outKey", -1);
		    	msg.put("jsonrpc", "2.0");
		    	msg.put("id", 6);
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
    
    
	public static void retreiveHeaders(StringBuilder headers) {
		JSONObject jsonObj = new JSONObject(message);
        JSONObject qHyperCubDefinition =  jsonObj.getJSONObject("result").getJSONObject("qProp").getJSONObject("qHyperCubeDef");
        JSONArray qDimensions = qHyperCubDefinition.getJSONArray("qDimensions");
        for(int i = 0; i < qDimensions.length(); i++) {
        			JSONArray labels = qDimensions.getJSONObject(i).getJSONObject("qDef").getJSONArray("qFieldLabels");
        			if(labels.length() > 0) {		
        				headers.append(","+labels.getString(0));
        			}		
        }
        JSONArray qMeasures = qHyperCubDefinition.getJSONArray("qMeasures");
        for(int i = 0; i < qMeasures.length(); i++) {
        				headers.append(","+qMeasures.getJSONObject(i).getJSONObject("qDef").getString("qLabel"));
        }
	}
    
   
    private static void print_content(HttpsURLConnection con){
    	if(con!=null){

    	try {
			    	   System.out.println("****** Content of the URL ********");
			    	   BufferedReader br =
			    		new BufferedReader(new InputStreamReader(con.getInputStream()));
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
    
    public static void sendMail() {
    	try {
				MailMessage message = new MailMessage();
				message.setSender("1Printing Team");
				message.setInternetAddress("no-reply@channelplay.in");
				String emailId = "jitender.singh1@channelplay.in";
				message.setRecipientTo(emailId);
				String ccEmailIds = "lalit.giani@channelplay.in";
				System.out.println(ccEmailIds);
				message.setRecipientCC(ccEmailIds);
				StringBuilder mailBody = new StringBuilder();
				mailBody.append("Hi Team \n");
				mailBody.append("This is a test mail.\n");
				mailBody.append("Please Ignore.\n");
				message.setTextMessage(mailBody.toString());
				message.setSubject("Test Mail From 1Printing");
				File[] files = new File[1];
				files[0] = new File("E:/Github3/Output.csv");
				message.setFileAttachment(files);
				String fileAttachmentContentType [] = new String[]  {"text/csv"};
				message.setFileAttachmentContentType(fileAttachmentContentType);
				String filesName[] = new String[] {"TestFile.csv"};
				message.setFileAttachmentFileName(filesName);
				System.out.println(".................before sending mail...........");
				EmailSender.sendMail(message);
    	} catch(Exception e) {
    			e.printStackTrace();
    	}
    }
    
    public static void sendMail(String html) {
    	try {
				MailMessage message = new MailMessage();
				message.setSender("1Printing Team");
				message.setInternetAddress("no-reply@channelplay.in");
				String emailId = "jitender.singh1@channelplay.in";
				message.setRecipientTo(emailId);
				String ccEmailIds = "lalit.giani@channelplay.in";
				System.out.println(ccEmailIds);
				message.setRecipientCC(ccEmailIds);
				StringBuilder mailBody = new StringBuilder();
				mailBody.append("Hi Team <br/>");
				mailBody.append("This is a test mail. <br/>");
				mailBody.append("Please Ignore. <br/>");
				mailBody.append(html);
				message.setTextMessage(mailBody.toString());
				message.setSubject("Test Mail From 1Printing");
				File[] files = new File[1];
				files[0] = new File("E:/Github3/Output.csv");
				message.setFileAttachment(files);
				String fileAttachmentContentType [] = new String[]  {"text/csv"};
				message.setFileAttachmentContentType(fileAttachmentContentType);
				String filesName[] = new String[] {"TestFile.csv"};
				message.setFileAttachmentFileName(filesName);
				System.out.println(".................before sending mail...........");
				EmailSender.sendMail(message);
    	} catch(Exception e) {
    			e.printStackTrace();
    	}
    }
    
    
    public static String generateBarChart(String[] labels,List<List<String>> hyperCubeDataList)
    {
      List<String> rowData = new ArrayList<String>();	
      List<Integer> colData = new ArrayList<Integer>();
      for(List<String> rowList : hyperCubeDataList) {
    	  			for(int i = 0; i < rowList.size(); i++) {
    	  						if(i == 0) 
    	  							rowData.add(rowList.get(0));
    	  						else 
    	  							colData.add(Integer.parseInt(rowList.get(i)));
    	  			}
      }
      BarChartPlot data1 = Plots.newBarChartPlot(Data.newData(colData), Color.newColor("35ff31"), "");

//		This code is for setting bar color according to the score value.      
//      int j = 0;
//      for (Integer score : scoreList) {
//        if (score.intValue() >= 85) {
//          data1.setColor(Color.newColor("04FF42"), j);
//        } else
//          data1.setColor(Color.newColor("FD0001"), j);
//        j++;
//      }
      
//		This code is for setting text marker on the bar.
//      for (i = 0; i < campaignScoreArray.length - 4; i++) {
//        data1.addTextMarker(campaignScoreArray[i] + "%", Color.BLACK, 10, i);
//      }
      
      BarChart chart = GCharts.newBarChart(new BarChartPlot[] { data1 });
      
      AxisLabels xAxisLabel = AxisLabelsFactory.newAxisLabels(labels[0], 50.0D);
      AxisLabels yAxisLabel = AxisLabelsFactory.newAxisLabels(labels[1], 50.0D);
      
      chart.addXAxisLabels(AxisLabelsFactory.newAxisLabels(rowData));
      chart.addXAxisLabels(xAxisLabel);
      List<String> yAxisLabels = new ArrayList<String>();
      chart.addYAxisLabels(AxisLabelsFactory.newAxisLabels(yAxisLabels));
      chart.addYAxisLabels(yAxisLabel);
      
      chart.setBarWidth(BarChart.AUTO_RESIZE);
      chart.setSpaceBetweenGroupsOfBars(20);
      
      chart.setSize(600, 300);
      chart.setDataStacked(false);
      
      chart.setTitle("Test Chart", Color.GREEN, 15);
      chart.setBackgroundFill(Fills.newSolidFill(Color.WHITE));
      chart.setAreaFill(Fills.newSolidFill(Color.WHITE));
      System.out.println("chart url "+chart.toURLString());
      return chart.toURLForHTML();
    }
    
    
}