package com.intellij.gitlab.ui.table;

import com.intellij.gitlab.helper.ColumnInfoHelper;
import com.intellij.util.ui.ListTableModel;
import org.gitlab4j.api.models.Pipeline;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GitLabPipelineListTableModel extends ListTableModel<Pipeline> {

    public GitLabPipelineListTableModel(@NotNull List<Pipeline> pipelines) {
        super();
        setColumnInfos(ColumnInfoHelper.getHelper().generateColumnsInfo());
        setItems(pipelines);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }


}
