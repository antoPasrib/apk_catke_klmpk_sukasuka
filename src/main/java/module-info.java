module com.example.apkpencatatankeuangan {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.apkpencatatankeuangan to javafx.fxml;
    exports com.example.apkpencatatankeuangan;
}