module com.example.class_schedule {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;
    requires java.net.http;


    opens com.example.class_schedule to javafx.fxml;
    exports com.example.class_schedule;
    exports parser;
    exports table;

}