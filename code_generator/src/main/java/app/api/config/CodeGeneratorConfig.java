package app.api.config;

import java.util.ArrayList;
import java.util.List;

public class CodeGeneratorConfig {

    private List<TableConfig> tableList = new ArrayList<TableConfig>();

    public List<TableConfig> getTableList() {
        return tableList;
    }

    public void setTableList(List<TableConfig> tableList) {
        this.tableList = tableList;
    }

}
