package app.api.config;

public class ColumnConfig {

    private String name ;
    private String javaFieldName;
    private String jdbcType;
    private Boolean autoIncrement;
    private Boolean primaryKey;
    private Integer length;
    private Boolean isNull;
    private Integer precision;

    private Boolean gen_generate;
    private Boolean gen_generateAsQuery;
    private DataTypeEnum gen_dataType;
    private String gen_title;
    private String gen_memo;
    private Boolean gen_require;
    private Long gen_min;
    private Long gen_max;
    private Integer gen_precision;



    public String getJavaFieldName() {
        return javaFieldName;
    }

    public void setJavaFieldName(String javaFieldName) {
        this.javaFieldName = javaFieldName;
    }

    public Boolean getGen_generateAsQuery() {
        return gen_generateAsQuery;
    }

    public void setGen_generateAsQuery(Boolean gen_generateAsQuery) {
        this.gen_generateAsQuery = gen_generateAsQuery;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public Boolean getAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(Boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public Boolean getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Boolean getIsNull() {
        return isNull;
    }

    public void setIsNull(Boolean isNull) {
        this.isNull = isNull;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isGen_generate() {
        return gen_generate;
    }

    public void setGen_generate(Boolean gen_generate) {
        this.gen_generate = gen_generate;
    }

    public DataTypeEnum getGen_dataType() {
        return gen_dataType;
    }

    public void setGen_dataType(DataTypeEnum gen_dataType) {
        this.gen_dataType = gen_dataType;
    }

    public String getGen_title() {
        return gen_title;
    }

    public void setGen_title(String gen_title) {
        this.gen_title = gen_title;
    }

    public String getGen_memo() {
        return gen_memo;
    }

    public void setGen_memo(String gen_memo) {
        this.gen_memo = gen_memo;
    }

    public Boolean isGen_require() {
        return gen_require;
    }

    public void setGen_require(Boolean gen_require) {
        this.gen_require = gen_require;
    }



    public Long getGen_min() {
        return gen_min;
    }

    public void setGen_min(Long gen_min) {
        this.gen_min = gen_min;
    }

    public Long getGen_max() {
        return gen_max;
    }

    public void setGen_max(Long gen_max) {
        this.gen_max = gen_max;
    }

    public Integer getGen_precision() {
        return gen_precision;
    }

    public void setGen_precision(Integer gen_precision) {
        this.gen_precision = gen_precision;
    }

}
