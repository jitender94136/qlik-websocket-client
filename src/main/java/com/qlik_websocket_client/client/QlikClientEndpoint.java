package com.qlik_websocket_client.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.websocket.ClientEndpoint;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.client.SslContextConfigurator;
import org.glassfish.tyrus.client.SslEngineConfigurator;

@ClientEndpoint
public class QlikClientEndpoint {

    private static CountDownLatch latch;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connected ... " + session.getId());
        System.out.println("in the on open method ");
//        try {
//            session.getBasicRemote().sendText("start");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    @OnMessage
    public void onMessage(String response, Session session) {
        //BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
    		logger.info("Session Id ...." + session.getId());
            logger.info("Response from server ...." + response);
            System.out.println("in the on message method ");
           
    }
    
    

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
    	System.out.println("in the on close method ");
        logger.info(String.format("Session %s close because of %s", session.getId(), closeReason));
        latch.countDown();
    }

    public static void main(String[] args) {
        latch = new CountDownLatch(1);
        ClientManager client = ClientManager.createClient();
       
        try {
        	System.out.println("before the hit");
        	client.connectToServer(QlikClientEndpoint.class, new URI("ws://localhost:4848/app"));
        	System.out.println("after hitting the server.......");
        	latch.await();

        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
	 public static SSLContext setCertificateConfiguration() {
						String qlikCertificatePath = "E:/opt/certificates/analytics.1viewinsights.com/";
						String qlikPassword = "testtest";
						SSLContext  context = null;
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
									context = SSLContext.getInstance("SSL");
									KeyStore ksTrust = KeyStore.getInstance("JKS");
									ksTrust.load(new FileInputStream(rootCert),
											rootCertPass.toCharArray());
									TrustManagerFactory tmf = TrustManagerFactory
											.getInstance(TrustManagerFactory.getDefaultAlgorithm());
									tmf.init(ksTrust);
									context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
									
									/************** END Certificate configuration for use in connection **************/
						    }	catch (KeyStoreException e) { e.printStackTrace(); } 
										catch (IOException e) { e.printStackTrace(); } 
										catch (CertificateException e) { e.printStackTrace(); } 
										catch (NoSuchAlgorithmException e) { e.printStackTrace(); } 
										catch (UnrecoverableKeyException e) { e.printStackTrace(); } 
										catch (KeyManagementException e) { e.printStackTrace(); } 
										return context;
							}
}
