package app.api.config;

public enum DataTypeEnum {
    LONGINT("LONGINT",0),
    INT("INT",1),
    STRING("STRING", 2),
    TIME("TIME", 3),
    DATETIME("DATETIME", 4),
    DATE("DATE", 5),
    DECIMAL("DECIMAL", 6),
    BOOLEAN("BOOLEAN", 7),
    NONE("NONE", 8);


    private String name;
    private int id;
    DataTypeEnum(String name, int id){
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString(){
        return name;
    }
}
