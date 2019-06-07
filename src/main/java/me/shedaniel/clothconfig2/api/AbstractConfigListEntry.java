package me.shedaniel.clothconfig2.api;

public abstract class AbstractConfigListEntry<T> extends AbstractConfigEntry<T> {
    private String fieldName;
    private boolean editable = true;
    
    public AbstractConfigListEntry(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public boolean isEditable() {
        return getScreen().isEditable() && editable;
    }
    
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    @Override
    public String getFieldName() {
        return fieldName;
    }
}
