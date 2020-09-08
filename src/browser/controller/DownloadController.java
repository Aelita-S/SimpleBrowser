package browser.controller;

import browser.Data;
import browser.util.Downloader;
import browser.util.FileUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 * 下载界面控制器
 */
public class DownloadController {

    public TextField fileURLText;
    public TextField filePathText;
    public Button selectPathButton;
    public Button startDownloadButton;
    public ProgressBar downloadProgressBar;
    URL url;

    public void startDownload(ActionEvent actionEvent) throws IOException {
        String path = filePathText.getText();
        if (filePathText.getText().equals("")) {
            filePathText.setText(FileUtil.getFilePathName(url));
        }
        Downloader d = new Downloader(url, path, downloadProgressBar);
        d.start();
        Data.downloadRecord.put(FileUtil.getFilename(url), String.valueOf(url));
    }

    public void selectPath(ActionEvent actionEvent) throws IOException {
        save();
        Stage downloadStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(filePathText.getText().split("/")[0]));
        fileChooser.setTitle("选择要保存的路径");
        File file = fileChooser.showSaveDialog(downloadStage);
        filePathText.setText(file.toString());
    }

    public void enterFileURL(ActionEvent actionEvent) throws Exception {
        save();
    }

    public void save() throws IOException {
        url = new URL(fileURLText.getText());
        if (filePathText.getText().equals("")) {
            filePathText.setText(FileUtil.getFilePathName(url));
        }
    }
}
