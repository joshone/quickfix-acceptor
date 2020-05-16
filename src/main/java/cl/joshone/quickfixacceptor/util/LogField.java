package cl.joshone.quickfixacceptor.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.FieldType;
import quickfix.field.MsgType;

public class LogField {

    private Field field;
    private FieldType fieldType;
    private String fieldName;
    private String fieldValueName;
    private boolean required;
    private boolean header;
    private boolean trailer;
    private List<LogGroup> groups;

    private DataDictionary dictionary;

    public static LogField createLogField(MsgType messageType, Field field,
            DataDictionary dictionary) {
        return new LogField(messageType, field, dictionary);
    }

    /**
     * @param messageType what message the field is part of.
     * @param field the actual field we are wrapping.
     * @param dictionary dictionary used to look up field information.
     */
    protected LogField(
            MsgType messageType, Field field, DataDictionary dictionary) {
        this.dictionary = dictionary;
        this.field = field;

        final String messageTypeString = messageType.getValue();
        final int fieldTag = field.getTag();

        fieldType = dictionary.getFieldTypeEnum(fieldTag);
        fieldName = dictionary.getFieldName(fieldTag);
        fieldValueName = dictionary.getValueName(fieldTag,
                field.getObject().toString());
        required = getDataDictionary().isRequiredField(messageTypeString,
                fieldTag);
        header = getDataDictionary().isHeaderField(fieldTag);
        if (!header) {
            trailer = getDataDictionary().isTrailerField(fieldTag);
        }
    }

    public Iterator<LogField> group() {
        return null;
    }

    public Field getField() {
        return field;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public int getTag() {
        return field.getTag();
    }

    public Object getValue() {
        return field.getObject();
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValueName() {
        return fieldValueName;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isHeaderField() {
        return header;
    }

    public boolean isTrailerField() {
        return trailer;
    }

    public boolean isRepeatingGroup() {
        return groups != null;
    }

    /**
     * @return true if this this field is not a header field or a trailer field.
     */
    public boolean isBodyField() {
        return !isHeaderField() || !isTrailerField();
    }

    public DataDictionary getDataDictionary() {
        return dictionary;
    }

    public void addGroup(LogGroup group) {
        if (groups == null) {
            groups = new ArrayList<LogGroup>();
        }

        groups.add(group);
    }

    public List<LogGroup> getGroups() {
        return groups;
    }

}
