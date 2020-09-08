package browser.controller;

import browser.Data;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class HistoryController implements Initializable {
    @FXML
    public ListView<String> historyListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> strList = FXCollections.observableArrayList();
        strList.addAll(Data.history);
        historyListView.setItems(strList);
    }

}
