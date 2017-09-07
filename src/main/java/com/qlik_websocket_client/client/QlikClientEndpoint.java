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
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import org.glassfish.tyrus.client.ClientManager;

@ClientEndpoint
public class QlikClientEndpoint {

    private static CountDownLatch latch;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connected ... " + session.getId());
        try {
            session.getBasicRemote().sendText("start");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnMessage
    public void onMessage(String response, Session session) {
        //BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
       
            logger.info("Response from server ...." + response);
           
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info(String.format("Session %s close because of %s", session.getId(), closeReason));
        latch.countDown();
    }

    public static void main(String[] args) {
        latch = new CountDownLatch(1);

        ClientManager client = ClientManager.createClient();
       
        
        try {
            client.connectToServer(QlikClientEndpoint.class, new URI("ws://localhost:8025/websockets/game"));
            latch.await();

        } catch (DeploymentException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
	 public  SSLSocketFactory setCertificateConfiguration() {
						SSLSocketFactory sslSocketFactory = null;
						String qlikCertificatePath = "/opt/certificates/analytics.1viewinsights.com/";
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
}
