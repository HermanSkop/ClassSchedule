package table;

public class Cell {
    protected String style;
    protected String content;
    protected int span;
    protected int rowId;
    protected int ColId;
    protected boolean description;
    public Cell() {
        this.style = "";
        this.content = "";
        rowId = -1;
        ColId = -1;
        description = false;
    }
    public void setStyle(String style) {
        this.style = style;
    }
    public String getStyle() {
        return style;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public void setColId(int rowCol) {
        this.ColId = rowCol;
    }

    public int getRowId() {
        return rowId;
    }

    public int getColId() {
        return ColId;
    }

    public int getSpan() {
        return span;
    }

    public void setSpan(int span) {
        this.span = span;
    }

    public boolean isDescription() {
        return description;
    }

    public void setDescription(boolean description) {
        this.description = description;
    }
}
