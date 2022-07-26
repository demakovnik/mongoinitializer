package org.demakov.mongoinitializer.generator;

public class FieldSpecification {
    private String name;
    private String type;

    public FieldSpecification(String fieldName, String fieldType) {
        this.name = fieldName;
        this.type = fieldType;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
