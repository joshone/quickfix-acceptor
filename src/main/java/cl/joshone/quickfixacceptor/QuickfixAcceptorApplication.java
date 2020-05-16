package cl.joshone.quickfixacceptor;

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
import quickfix.Session;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.field.BeginString;

@SpringBootApplication
public class QuickfixAcceptorApplication {
	
	private static LogMessageSet messages = null;

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
	    MessageFactory messageFactory = new DefaultMessageFactory();
	    Acceptor acceptor = null;
	    
		try {
			acceptor = new SocketAcceptor
			  (application, storeFactory, settings, logFactory, messageFactory);
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
//	    	 SessionID session =  acceptor.getSessions().get(0);
	    	 
//	    	 Message m = new Message();
//	    	 Header header = m.getHeader();
//	    	 header.setField(new BeginString("FIX.4.4"));
//	    	 
//	    	 try {
//				Session.sendToTarget(m);
//			} catch (SessionNotFound e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	    	 
	    	 
	     }
//	    acceptor.stop();
	}

	public static LogMessageSet getMessagesSet() {
		return messages;
	}
	
	

}
