module com.ashish.frontend.marketdatafrontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.apache.avro;
    requires kafka.clients;
    requires schema;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens com.ashish.frontend to javafx.fxml;
    exports com.ashish.frontend;
}