package cl.joshone.quickfixacceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import quickfix.Acceptor;
import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.Message.Header;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RuntimeError;
import quickfix.ScreenLogFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.field.BeginString;

@SpringBootApplication
public class QuickfixAcceptorApplication {
	private static final Logger logger = LoggerFactory.getLogger(QuickfixAcceptorApplication.class);
	
	public static void main(String[] args) {
		
		String fileName = "./acceptorSettings.cfg";
		
		SpringApplication.run(QuickfixAcceptorApplication.class, args);
		
		Application application = new AcceptorApplication();

	    SessionSettings settings = null;
		try {
			settings = new SessionSettings(fileName);
		} catch (ConfigError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    MessageStoreFactory storeFactory = new FileStoreFactory(settings);
		LogFactory logFactory = new FileLogFactory(settings);
		ScreenLogFactory screenLogFactory = new ScreenLogFactory(settings);
	    MessageFactory messageFactory = new DefaultMessageFactory();
	    Acceptor acceptor = null;
	    
		try {
			acceptor = new SocketAcceptor
			  (application, storeFactory, settings, screenLogFactory, messageFactory);
		} catch (ConfigError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			acceptor.start();
		} catch (RuntimeError | ConfigError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     while(true) { 
	    	 SessionID session =  acceptor.getSessions().get(0);
	    	 
			 Message m = new Message();
			 
			 Header header = m.getHeader();
			 //header.set(new quickfix.field.MsgType("A"));
			 header.setField(new BeginString("FIX.4.4"));
			 
	    	 
	    	 /*try {
				Session.sendToTarget(m, session);
			} catch (SessionNotFound e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
	    	 
	    	 
	     }
//	    acceptor.stop();
	}
	

}
