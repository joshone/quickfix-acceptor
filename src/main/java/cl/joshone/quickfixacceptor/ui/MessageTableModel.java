package cl.joshone.quickfixacceptor.ui;

import cl.joshone.quickfixacceptor.LogMessage;
import cl.joshone.quickfixacceptor.LogMessageSet;
import cl.joshone.quickfixacceptor.QuickfixAcceptorApplication;
import quickfix.field.converter.UtcTimestampConverter;

public class MessageTableModel {
	
	private static LogMessageSet messages = QuickfixAcceptorApplication.getMessagesSet();
    private static String[] columns = 
        {"#", "Direction", "SendingTime", "Type", "Message"};
    
    public MessageTableModel(){
        messages.addCallback(this);
    }
    
    public int getColumnCount() {
        return columns.length;
    }

    public String getColumnName(int column) {
        return columns[column];
    }
    
    public Class getColumnClass(int column) {
        if (column == 0) return Integer.class;
        return String.class;
    }
    
    public int getRowCount() {
        return messages.getCount();
    }

    public Object getValueAt( int row, int column ) {
        LogMessage msg = messages.getMessage( row );
        if ( column == 0 ) return msg.getMessageIndex();
        if ( column == 1 ) return (msg.isIncoming() ? "incoming" : "outgoing");
        if ( column == 2 ) return UtcTimestampConverter.convert(msg.getSendingTime(),true);
        if ( column == 3 ) return msg.getMessageTypeName();
        if ( column == 4 ) return msg.getRawMessage();
        return new Object();
    }

}
