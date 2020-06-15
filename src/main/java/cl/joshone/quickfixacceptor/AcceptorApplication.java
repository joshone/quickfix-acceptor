package cl.joshone.quickfixacceptor;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.Application;
import quickfix.DataDictionary;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.fix44.MessageCracker;
import quickfix.fix44.NewOrderSingle;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.UnsupportedMessageType;
import quickfix.Message.Header;
import quickfix.field.ClOrdID;
import quickfix.field.OnBehalfOfCompID;
import quickfix.field.OnBehalfOfSubID;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Price;
import quickfix.field.SecurityType;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.Text;
import quickfix.field.TransactTime;

public class AcceptorApplication extends MessageCracker implements Application{
	private static final Logger logger = LoggerFactory.getLogger(AcceptorApplication.class);
	
	private boolean connected;
	private DataDictionary dictionary;
	private SessionID currentSession;
	//private LogMessageSet messages;
	private SessionSettings settings;
	//private ExecutionSet executions = null;

	@Override
	public void fromAdmin(Message message, SessionID sessionID)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {

				logger.info("fromAdmin {}, {}", message, sessionID);
		
	}

	@Override
	public void fromApp(Message message, SessionID sessionID)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
				logger.info("fromApp {}, {}", message, sessionID);
		//messages.add(message, true, dictionary, sessionID);
        crack(message, sessionID);
		
	}

	@Override
	public void onCreate(SessionID sessionID) {
		logger.info("onCreate {} ", sessionID);
		
	}

	@Override
	public void onLogon(SessionID sessionID) {
		logger.info("onlogon {}", sessionID);
		connected = true;
		currentSession = sessionID;
		dictionary = Session.lookupSession(currentSession).getDataDictionary();
		
		
	}

	@Override
	public void onLogout(SessionID sessionID) {
		connected = false;
		currentSession = null;
		logger.info("onLogout {} ", sessionID);
		
	}

	@Override
	public void toAdmin(Message message, SessionID sessionID) {
		logger.info("toAdmin {}, {}", message, sessionID);
		
	}

	@Override
	public void toApp(Message message, SessionID sessionID) throws DoNotSend {
		logger.info("toApp {}, {}", message, sessionID);
		try {
			//messages.add(message, false, dictionary, sessionID);
			sendMessage();
			Thread.sleep(5000);
            crack(message, sessionID);
        } catch (Exception e) {	e.printStackTrace(); }
		
	}

	public void sendMessage(){

		NewOrderSingle order = new NewOrderSingle(
						new ClOrdID("APPL12456S"),
						new Side(Side.BUY),
						new TransactTime(new Date()), 
						new OrdType(OrdType.MARKET));
				
				order.set(new OrderQty(4500));
				order.set(new Price(200.9d));
		try {
            Session.sendToTarget( order, currentSession );
        } catch ( SessionNotFound e ) { e.printStackTrace(); }
	}

	

	// Message sending methods
    /*public void sendMessage( Message message ) {
        String oboCompID = "<UNKNOWN>";
        String oboSubID = "<UNKNOWN>";
        boolean sendoboCompID = false;
        boolean sendoboSubID = false;
        
        try {
            oboCompID = settings.getString(currentSession, "OnBehalfOfCompID");
            oboSubID = settings.getString(currentSession, "OnBehalfOfSubID");
            sendoboCompID = settings.getBool("FIXimulatorSendOnBehalfOfCompID");
            sendoboSubID = settings.getBool("FIXimulatorSendOnBehalfOfSubID");
        } catch ( Exception e ) {}
        
        // Add OnBehalfOfCompID
        if ( sendoboCompID && !oboCompID.equals("") ) {
            OnBehalfOfCompID onBehalfOfCompID = new OnBehalfOfCompID(oboCompID);
            Header header = (Header) message.getHeader();
            //header.set( onBehalfOfCompID );			
        }

        // Add OnBehalfOfSubID
        if ( sendoboSubID && !oboSubID.equals("") ) {
            OnBehalfOfSubID onBehalfOfSubID = new OnBehalfOfSubID(oboSubID);
            Header header = (Header) message.getHeader();
            //header.set( onBehalfOfSubID );			
        }
        
        // Send actual message
        try {
            Session.sendToTarget( message, currentSession );
        } catch ( SessionNotFound e ) { e.printStackTrace(); }
	}*/
	
	void sendOrderCancel() throws SessionNotFound{

		quickfix.fix44.OrderCancelRequest message = new quickfix.fix44.OrderCancelRequest();
		message.set(new OrigClOrdID("123"));
		message.set(new ClOrdID("321"));
		message.set(new Symbol("LNUX"));
		message.set(new Side(Side.BUY));
		message.set(new SecurityType("FXFWD"));

		message.set(new Text("Cancel my order!"));
		Session.sendToTarget(message, "TW", "TARGET");

	}

}
