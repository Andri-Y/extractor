module com.app.extractor {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;
    requires org.apache.poi.ooxml;
    requires java.desktop;

    opens com.app.extractor.ui to javafx.fxml;
    exports com.app.extractor;
}