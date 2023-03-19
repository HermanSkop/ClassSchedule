package table;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Table {
    protected List<Cell> cells;

    public Table() {
        this.cells = new LinkedList<>();
    }

    public Table(List<Cell> cells) {
        this.cells = cells;
    }
    public List<Cell> getCells(){
        return cells;
    }
    public void add(Cell cell){
        cells.add(cell);
    }
}
