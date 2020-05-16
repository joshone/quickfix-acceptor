package cl.joshone.quickfixacceptor.util;

import java.util.ArrayList;
import java.util.List;

import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.field.MsgType;

public class LogGroup extends LogField {
	
	private List<LogField> fields;

	public LogGroup(MsgType messageType, Field field,
            DataDictionary dictionary) {
        super(messageType, field, dictionary);
        fields = new ArrayList<LogField>();
    }

    public void addField(LogField logField) {
        fields.add(logField);
    }

    public List<LogField> getFields() {
        return fields;
    }

}
