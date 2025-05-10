module com.example.apkpencatatankeuangan {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.apkpencatatankeuangan to javafx.fxml;
    exports com.example.apkpencatatankeuangan;
    exports com.example.apkpencatatankeuangan.controller;
    opens com.example.apkpencatatankeuangan.controller to javafx.fxml;
}