package app.api.config;

public class ComparisonTableInfoResult {

    private String moreInfo;
    private String lessInfo;
    private String moreColumn;
    private String lessColumn;
    private ComparisonResultTypeEnum result;

    public String getMoreColumn() {
        return moreColumn;
    }

    public void setMoreColumn(String moreColumn) {
        this.moreColumn = moreColumn;
    }

    public String getLessColumn() {
        return lessColumn;
    }

    public void setLessColumn(String lessColumn) {
        this.lessColumn = lessColumn;
    }



    public ComparisonResultTypeEnum getResult() {
        return result;
    }

    public void setResult(ComparisonResultTypeEnum result) {
        this.result = result;
    }



    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public String getLessInfo() {
        return lessInfo;
    }

    public void setLessInfo(String lessInfo) {
        this.lessInfo = lessInfo;
    }

}
