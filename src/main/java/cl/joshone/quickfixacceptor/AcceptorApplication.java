package cl.joshone.quickfixacceptor;

import quickfix.Application;
import quickfix.DataDictionary;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.MessageCracker;
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
import quickfix.field.OrigClOrdID;
import quickfix.field.SecurityType;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.Text;

public class AcceptorApplication extends MessageCracker implements Application{
	
	private boolean connected;
	private DataDictionary dictionary;
	private SessionID currentSession;
	private LogMessageSet messages;
	private SessionSettings settings;
	private ExecutionSet executions = null;

	@Override
	public void fromAdmin(Message arg0, SessionID arg1)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
		System.out.println("fromAdmin");
		
	}

	@Override
	public void fromApp(Message message, SessionID sessionID)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
		System.out.println("fromApp");
		messages.add(message, true, dictionary, sessionID);
        crack(message, sessionID);
		
	}

	@Override
	public void onCreate(SessionID arg0) {
		System.out.println("onCreate");
		
	}

	@Override
	public void onLogon(SessionID sessionID) {
		connected = true;
		currentSession = sessionID;
		dictionary = Session.lookupSession(currentSession).getDataDictionary();
		System.out.println("onlogon");
		
	}

	@Override
	public void onLogout(SessionID arg0) {
		connected = false;
		currentSession = null;
		System.out.println("onLogout");
		
	}

	@Override
	public void toAdmin(Message arg0, SessionID arg1) {
		System.out.println("toAdmin");
		
	}

	@Override
	public void toApp(Message message, SessionID sessionID) throws DoNotSend {
		System.out.println("toApp");
		try {
            messages.add(message, false, dictionary, sessionID);
            crack(message, sessionID);
        } catch (Exception e) {	e.printStackTrace(); }
		
	}

	@Override
	protected void onMessage(Message message, SessionID sessionID)
			throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
		// TODO Auto-generated method stub
		super.onMessage(message, sessionID);
	}

	// Message sending methods
    public void sendMessage( Message message ) {
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
	}
	
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
