package cl.joshone.quickfixacceptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cl.joshone.quickfixacceptor.util.FIXMessageHelper;
import cl.joshone.quickfixacceptor.util.LogField;
import cl.joshone.quickfixacceptor.util.LogGroup;
import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.FieldConvertError;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.MsgType;

public class LogMessage implements Comparable<Object> {
	
	public static final char DEFAULT_DELIMETER = '|';
    public static final char SOH_DELIMETER = (char) 0x01;

    public static final String INCOMING = "incoming";
    public static final String MESSAGE_TYPE_NAME = "messageTypeName";
    public static final String SENDING_TIME = "sendingTime";
    public static final String RAW_MESSAGE = "rawMessage";

    private SessionID sessionId;
    private boolean incoming;
    private String rawMessage;
    private String messageTypeName;
    private Date sendingTime;
    private DataDictionary dictionary;
    //private List<ValidationError> validationErrors;
    private boolean isValid;
    private int messageIndex;
    
    public LogMessage(int messageIndex, boolean incoming, SessionID sessionId,
            String rawMessage, DataDictionary dictionary) {
        this.messageIndex = messageIndex;

        isValid = true;
        this.dictionary = dictionary;
        this.rawMessage = rawMessage.replace(SOH_DELIMETER, DEFAULT_DELIMETER);
        this.sessionId = sessionId;
        this.incoming = incoming;

        sendingTime = lookupSendingTime();
        messageTypeName = lookupMessageTypeName();
    }
    
    public SessionID getSessionId() {
        return sessionId;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public String getMessageTypeName() {
        return messageTypeName;
    }
    
    public int getMessageIndex() {
    	return messageIndex;
    }
    
    public boolean isValid() {
        return isValid;
    }

    public boolean isIncoming() {
        return incoming;
    }
    
    public Date getSendingTime() {
        return sendingTime;
    }
    
    public List<LogField> getLogFields() {


        Message message = createMessage();

        List<LogField> logFields = new ArrayList<LogField>();

        Map<Integer, Field> allFields = getAllFields(message);

        String[] fields = rawMessage.split("\\|");

        for (String fieldString : fields) {
            int indexOfEqual = fieldString.indexOf('=');
            int tag = Integer.parseInt(fieldString.substring(0, indexOfEqual));

            Field field = allFields.remove(tag);
            if (field != null) {
                logFields.add(createLogField(message, field));
            }
        }

        return logFields;
    }

	@Override
	public int compareTo(Object o) {
		LogMessage rhs = (LogMessage) o;
        int rhsMessageIndex = rhs.messageIndex;
        return (messageIndex < rhsMessageIndex ? -1 :
                (messageIndex == rhsMessageIndex ? 0 : 1));
	}
	
	@Override
    public String toString() {
        return "" + messageIndex;
    }
	
	@SuppressWarnings("unchecked")
	private LogField createLogField(Message message, Field field) {

        MsgType messageType = getMessageType(message);
        String messageTypeValue = messageType.getValue();

        LogField logField = 
                LogField.createLogField(messageType, field, dictionary);

        final DataDictionary.GroupInfo groupInfo = dictionary.getGroup(
                    messageTypeValue, field.getTag());
        if (groupInfo != null) {

            int delimeterField = groupInfo.getDelimeterField();
            Group group = new Group(field.getTag(), delimeterField);
            int numberOfGroups =  Integer.valueOf((String) field.getObject());
            for (int index = 0; index < numberOfGroups; index++) {
                LogGroup logGroup = 
                        new LogGroup(messageType, field, dictionary);

                try {

                    message.getGroup(index + 1, group);

                    Iterator groupIterator = group.iterator();
                    while (groupIterator.hasNext()) {
                        Field groupField = (Field) groupIterator.next();
                        logGroup.addField(LogField.createLogField(messageType,
                                groupField, dictionary));

                    }
                } catch (FieldNotFound fieldNotFound) {
                }

                logField.addGroup(logGroup);
            }
        }

        return logField;
    }
	
	private Message createMessage() {
        String sohMessage = 
                rawMessage.replace(DEFAULT_DELIMETER, SOH_DELIMETER);
        try {
            return new Message(sohMessage, dictionary, true);
        } catch (InvalidMessage invalidMessage) {
            try {
                return new Message(sohMessage, dictionary, false);
            } catch (InvalidMessage ugh) {
                return null;
            }
        }
    }
	
	@SuppressWarnings("unchecked")
	private Map<Integer, Field> getAllFields(Message genericMessage) {
        Map<Integer, Field> allFields = new LinkedHashMap<Integer, Field>();

        Iterator iterator = genericMessage.getHeader().iterator();
        while (iterator.hasNext()) {
            Field field = (Field) iterator.next();
            allFields.put(field.getTag(), field);
        }

        iterator = genericMessage.iterator();
        while (iterator.hasNext()) {
            Field field = (Field) iterator.next();
            int tag = field.getTag();
            if (!allFields.containsKey(tag)) {
                allFields.put(tag, field);
            }
        }

        iterator = genericMessage.getTrailer().iterator();
        while (iterator.hasNext()) {
            Field field = (Field) iterator.next();
            allFields.put(field.getTag(), field);
        }

        return allFields;
    }
	
	private String lookupMessageTypeName() {
        String messageTypeValue = FIXMessageHelper.getMessageType(rawMessage,
                DEFAULT_DELIMETER);
        if (messageTypeValue == null) {
            isValid = false;
            return null;
        }
        return dictionary.getValueName(MsgType.FIELD, messageTypeValue);
    }
	
	private Date lookupSendingTime() {
        try {
            Date date = FIXMessageHelper.getSendingTime(
                    rawMessage, DEFAULT_DELIMETER);
            if (date == null) {
                return date;
            }
            return date;
        } catch (FieldConvertError fieldConvertError) {
            return null;
        }
    }
	
	private MsgType getMessageType(Message message) {
        try {
            return (MsgType) message.getHeader().getField(new MsgType());
        } catch (FieldNotFound fieldNotFound) {
            throw new RuntimeException(fieldNotFound);
        }
    }

}
