package com.example.class_schedule;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.jsoup.nodes.Element;
import parser.ScheduleHandler;
import table.Cell;
import table.Table;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainController {
    protected final int cellHeight = 50;
    protected final int cellWidth = 100;
    public List<String> dates;
    @FXML
    public Button submitButton;
    @FXML
    public DatePicker startDate;
    @FXML
    public DatePicker endDate;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public VBox vbox;
    public TextField textField;

    @FXML
    public void initialize() {
        convert(startDate);
        convert(endDate);
        submitButton.setOnMouseClicked(this::refreshSchedule);

    }

    private List<String> createDates(LocalDate start, LocalDate end) {
        List<String> list = new ArrayList<>();
        for(LocalDate date = start; date.isBefore(end.plusDays(1)); date = date.plusDays(1)) {
            list.add(date.toString());
        }
        return list;
    }

    private void convert(DatePicker endDate) {
        endDate.setConverter(new StringConverter<>() {
            private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try {
                        return LocalDate.parse(string, dateFormatter);
                    }
                    catch (DateTimeParseException e){
                        return LocalDate.now();
                    }
                } else {
                    return null;
                }
            }
        });
    }

    private void constructTable(GridPane grid, Table table){
        int numberOfColumns = 0;
        for(Cell cell:table.getCells()){
            Pane pane = new Pane();
            Label label = new Label(cell.getContent().equals("") ? "" : cell.getContent());
            label.setStyle(cell.getStyle());
            label.setStyle(label.getStyle() + ";-fx-alignment: center;");
            label.setMinWidth(cellWidth);
            if(!cell.isDescription() && cell.getSpan()>1) {
                label.setStyle(label.getStyle()+
                        "-fx-border-style: solid;-fx-border-width: 1px 0px 0px 1px;-fx-border-color: black;");
                label.setMinHeight(cellHeight*cell.getSpan()+cell.getSpan()/2);
            }
            else if(cell.isDescription()){
                if(cell.getRowId()==0){
                    numberOfColumns = cell.getSpan();
                    label.setStyle("-fx-alignment: center-left;");
                    label.setMinHeight(cellHeight);
                    label.setMinWidth(cellWidth*cell.getSpan());
                }
                else if(cell.getSpan()==0){
                    label.setMinHeight(cellHeight+2);
                    label.setStyle(label.getStyle()+
                            "-fx-border-style: solid;-fx-border-width: 1px 0px 0px 0px;-fx-border-color: black;");
                }
                else {
                    label.setMinHeight(cell.getSpan()*cellHeight+2);
                    label.setStyle(label.getStyle()+
                            "-fx-border-style: solid;-fx-border-width: 1px 1px 0px 0px;-fx-border-color: black;");
                }
                label.setStyle(label.getStyle()+"-fx-background-color: #b0b6c9");
            }
            else {
                label.setMinHeight(cellHeight+1);
                label.setStyle(label.getStyle()+
                        "-fx-border-style: solid;-fx-border-width: 1px 0px 0px 1px;-fx-border-color: black;");
            }

            pane.getChildren().add(label);
            if(cell.getSpan()>0) grid.add(pane, cell.getColId(), cell.getRowId(), cell.getSpan(), 1);
            else grid.add(pane, cell.getColId(), cell.getRowId());
            ((Pane)grid.getChildren().get(grid.getChildren().toArray().length-1)).setMinHeight(cellHeight);
        }
        List<ColumnConstraints> columnConstraints = new LinkedList<>();
        for(int i=0; i<numberOfColumns; i++){
            ColumnConstraints columnConstraint = new ColumnConstraints();
            columnConstraint.setMinWidth(cellWidth);
            columnConstraints.add(columnConstraint);
        }
        grid.getColumnConstraints().addAll(columnConstraints);
    }
    private int isCorrectRange(LocalDate start, LocalDate end){
        if(start==null || end==null || end.isBefore(start)) return -1;
        Period period = Period.between(start, end);
        return period.getDays();
    }

    private void refreshSchedule(MouseEvent event) {
        try {
            vbox.getChildren().removeIf(node -> !(node instanceof BorderPane));
            LocalDate start = startDate.getValue();
            LocalDate end = endDate.getValue();
            int range = isCorrectRange(start, end) + 1;
            if (range > 0) {
                String filter = textField.getText()==null?"":textField.getText();
                dates = createDates(start, end);
                for (String date : dates) {
                    String page = ScheduleHandler.getPage(date);
                    Element table = ScheduleHandler.getTable(page);
                    Element body = ScheduleHandler.getBody(table);
                    GridPane gridPane = new GridPane();
                    constructTable(gridPane, ScheduleHandler.parseTable(body, filter));
                    gridPane.setStyle("-fx-border-style: solid; -fx-border-width: 1px; -fx-border-color: black; -fx-background-color: #c6c7d3");
                    vbox.getChildren().add(gridPane);
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}