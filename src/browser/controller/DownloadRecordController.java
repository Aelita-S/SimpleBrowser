package browser.controller;

import browser.Data;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class DownloadRecordController implements Initializable {
    @FXML
    public ListView<String> URLListView;
    @FXML
    public ListView<String> filenameListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> strList1 = FXCollections.observableArrayList();
        strList1.addAll(Data.downloadRecord.keySet());
        filenameListView.setItems(strList1);
        ObservableList<String> strList2 = FXCollections.observableArrayList();
        strList2.addAll(Data.downloadRecord.values());
        URLListView.setItems(strList2);
    }
}
