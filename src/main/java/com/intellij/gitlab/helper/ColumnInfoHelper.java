package com.intellij.gitlab.helper;

import com.intellij.gitlab.ui.renders.GitLabIconAndTextTableCellRenderer;
import com.intellij.gitlab.ui.renders.GitLabPipelineStatusTableCellRenderer;
import com.intellij.gitlab.ui.renders.GitLabPipelineTableCellRenderer;
import com.intellij.util.ui.ColumnInfo;
import org.gitlab4j.api.models.Pipeline;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

import static com.intellij.gitlab.util.GitLabUtil.getPrettyDateTime;

public class ColumnInfoHelper {

    private static final String ID_COLUMN = "Id";
    private static final String STATUS_COLUMN = "Status";
    private static final String REF_COLUMN = "Ref";
    private static final String CREATED_COLUMN = "Created";
    private static final String UPDATED_COLUMN = "Updated";

    private static ColumnInfoHelper helper;

    private ColumnInfoHelper() {
    }

    @NotNull
    public static ColumnInfoHelper getHelper() {
        if (helper == null) {
            helper = new ColumnInfoHelper();
        }

        return helper;
    }

    @NotNull
    public ColumnInfo[] generateColumnsInfo() {
        return new ColumnInfo[]{
                new KeyColumnInfo(),
                new RefColumnInfo(),
                new StatusColumnInfo(),
                new CreatedColumnInfo(),
                new UpdatedColumnInfo(),
        };
    }


    private abstract static class AbstractColumnInfo extends ColumnInfo<Pipeline, String> {
        private static final TableCellRenderer ICON_AND_TEXT_RENDERER = new GitLabIconAndTextTableCellRenderer();
        private String columnName;

        AbstractColumnInfo(String name) {
            super(name);
            this.columnName = name;
        }

        @Nullable
        @Override
        public String getMaxStringValue() {
            return columnName;
        }

        @Nullable
        @Override
        public TableCellRenderer getRenderer(Pipeline pipeline) {
            return ICON_AND_TEXT_RENDERER;
        }
    }

    private abstract static class GitLabPipelineColumnInfo extends ColumnInfo<Pipeline, String> {
        private static final GitLabPipelineTableCellRenderer GIT_LAB_PIPELINE_TABLE_CELL_RENDERER = new GitLabPipelineTableCellRenderer();


        GitLabPipelineColumnInfo(@NotNull String name) {
            super(name);
        }

        @Nullable
        @Override
        public TableCellRenderer getRenderer(Pipeline issue) {
            return GIT_LAB_PIPELINE_TABLE_CELL_RENDERER;
        }

    }

    private static class KeyColumnInfo extends GitLabPipelineColumnInfo {

        KeyColumnInfo() {
            super(ID_COLUMN);
        }

        @Nullable
        @Override
        public String valueOf(Pipeline pipeline) {
            return String.valueOf(pipeline.getId());
        }

        @Nullable
        @Override
        public String getMaxStringValue() {
            return "";
        }

        @Override
        public int getAdditionalWidth() {
            return 90;
        }

        @Override
        public TableCellRenderer getCustomizedRenderer(Pipeline o, TableCellRenderer renderer) {
            if (renderer instanceof GitLabPipelineTableCellRenderer) {
                ((GitLabPipelineTableCellRenderer) renderer).setHorizontalAlignment(SwingUtilities.LEFT);
            }
            return renderer;
        }
    }

    private static class RefColumnInfo extends GitLabPipelineColumnInfo {

        RefColumnInfo() {
            super(REF_COLUMN);
        }

        @Nullable
        @Override
        public String valueOf(Pipeline pipeline) {
            return pipeline.getRef();
        }

        @Nullable
        @Override
        public String getMaxStringValue() {
            return "";
        }

        @Override
        public int getAdditionalWidth() {
            return 250;
        }
    }

    private static class StatusColumnInfo extends GitLabPipelineColumnInfo {

        StatusColumnInfo() {
            super(STATUS_COLUMN);
        }

        @Nullable
        @Override
        public String valueOf(Pipeline pipeline) {
            return pipeline.getStatus().toValue();
        }

        @Nullable
        @Override
        public TableCellRenderer getRenderer(Pipeline pipeline) {
            return new GitLabPipelineStatusTableCellRenderer(pipeline.getStatus());
        }

    }

    private static class CreatedColumnInfo extends GitLabPipelineColumnInfo {

        CreatedColumnInfo() {
            super(CREATED_COLUMN);
        }

        @Nullable
        @Override
        public String valueOf(Pipeline pipeline) {
            return getPrettyDateTime(pipeline.getCreatedAt());
        }
    }

    private static class UpdatedColumnInfo extends GitLabPipelineColumnInfo {

        UpdatedColumnInfo() {
            super(UPDATED_COLUMN);
        }

        @Nullable
        @Override
        public String valueOf(Pipeline pipeline) {
            return getPrettyDateTime(pipeline.getUpdatedAt());
        }
    }

}
