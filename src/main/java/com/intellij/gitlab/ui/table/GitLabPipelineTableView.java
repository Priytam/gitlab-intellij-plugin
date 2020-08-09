package com.intellij.gitlab.ui.table;

import com.intellij.ui.table.TableView;
import org.gitlab4j.api.models.Pipeline;

import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public class GitLabPipelineTableView extends TableView<Pipeline> {

    private GitLabPipelineListTableModel model;

    public GitLabPipelineTableView(List<Pipeline> pipelines) {
        super();
        model = new GitLabPipelineListTableModel(pipelines);
        setModelAndUpdateColumns(model);
        setSelectionMode(SINGLE_SELECTION);
        setIntercellSpacing(new Dimension());
        setShowGrid(false);
        setRowHeight(25);
    }


    @Override
    protected TableColumnModel createDefaultColumnModel() {
        TableColumnModel columnModel = super.createDefaultColumnModel();
        columnModel.setColumnMargin(0);
        return columnModel;
    }

    public void updateModel(List<Pipeline> pipelines){
        model.setItems(pipelines);
    }

    @Override
    public GitLabPipelineListTableModel getModel() {
        return model;
    }
}
