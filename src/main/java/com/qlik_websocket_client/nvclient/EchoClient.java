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
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
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
			        JSONObject msg = new JSONObject();
			        msg.put("jsonrpc", "2.0");
					msg.put(	"id", 1);
			        msg.put(	"method", "GetDocList");
			        msg.put(	"handle", -1);
			        JSONArray jsonArr = new JSONArray();
			        msg.put("params", jsonArr);
			        ws.sendText(msg.toString());
        // Close the web socket.
       // ws.disconnect();
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
                    System.out.println("before message");
                	System.out.println(message);
                	System.out.println("after message");
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
            .addHeader("X-Qlik-User","UserDirectory=internal; UserId=sa_engine")
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
}