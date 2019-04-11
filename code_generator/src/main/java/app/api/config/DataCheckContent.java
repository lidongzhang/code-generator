package app.api.config;

import java.math.BigInteger;

public class DataCheckContent {

    private Boolean isCheck = null;
    private BigInteger min = null;
    private BigInteger max = null;
    private Integer precision = null;
    private Boolean isCheckPrecision = null;

    public Boolean getCheckPrecision() {
        return isCheckPrecision;
    }

    public void setCheckPrecision(Boolean checkPrecision) {
        isCheckPrecision = checkPrecision;
    }



    public Boolean getCheck() {
        return isCheck;
    }

    public void setCheck(Boolean check) {
        isCheck = check;
    }

    public BigInteger getMin() {
        return min;
    }

    public void setMin(BigInteger min) {
        this.min = min;
    }

    public BigInteger getMax() {
        return max;
    }

    public void setMax(BigInteger max) {
        this.max = max;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }




}
