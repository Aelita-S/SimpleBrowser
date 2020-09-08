package browser.controller;

import browser.BrowserTab;
import browser.Data;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * 主界面控制器
 */
public class MainController implements Initializable {
    private static final String BLANK = "_blank";
    private static final String TARGET = "target";
    private static final String CLICK = "click";
    @FXML
    public ListView<String> bookmarkListView;

    @FXML
    private MenuItem downloadManageButton;
    @FXML
    private MenuItem viewHistoryButton;

    @FXML
    private MenuItem openMail;

    @FXML
    private AnchorPane pane;
    @FXML
    private VBox vBox;
    @FXML
    private ToolBar toolBar;
    @FXML
    private HBox hBox;
    @FXML
    private TabPane tabPane;
    @FXML
    private AnchorPane acPane;
    @FXML
    private Button backButton; // 上一级页面
    @FXML
    private Button nextButton; // 下一级页面
    @FXML
    private Button refreshButton; // 刷新
    @FXML
    private Button favoritesButton; // 书签按钮
    @FXML
    private Button downloadButton; // 下载管理按钮
    @FXML
    private MenuButton menuButton; // 菜单按钮

    @FXML
    public TextField URLBar; // 地址栏


    private WebHistory webHistory; // 浏览历史

    private ObservableList<WebHistory.Entry> history;

    public WebView browser; // 浏览器

    private Tab currentTab;

    /**
     * 初始化
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        homepage();//初始化首页
        currentTab = tabPane.getTabs().get(0);//初始化当前标签
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);//标签添加关闭功能

        tabChangeListener();// tab切换监听器
        GUISelfAdaption();
        setAllSelectedURL();//设置全选url

    }

    /**
     * 改变tab标签，相应改变显示内容
     *
     * @param newValue
     */
    private void update(Tab newValue) {
        System.out.println("更新WebView");
        int index = tabPane.getTabs().indexOf(newValue);
        if (index < 0)
            System.exit(0);
        browser = BrowserTab.getView(index); // 更新webview 内容
        update();
    }

    /**
     * 改变tab标签，相应改变显示内容
     */
    public void update() {
        String url = browser.getEngine().getLocation();
        URLBar.setText(url);//更新URL地址
        webHistory = browser.getEngine().getHistory(); // 更新历史记录
        history = webHistory.getEntries(); // 更新历史记录
        Data.history.add(url);
    }

    /**
     * 标签切换监听器
     */
    private void tabChangeListener() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            currentTab = newValue;
            update(newValue);//更新界面
        });
    }


    /**
     * 跳转到输入的URL, 同时更新地址栏
     */
    @FXML
    public void visitedURL() {
        String targetURL = URLBar.getText();
        if (targetURL.equals("")) {
            targetURL = "about:blank";
        }
        browser.getEngine().load(smartURL(targetURL));//加载url
        System.out.println("url:" + browser.getEngine().getLocation());
        URLBar.setText(browser.getEngine().getLocation());//更新url地址栏
        updateTabName();//更新标签名
    }

    /**
     * 跳转到输入的URL, 同时更新地址栏
     */
    @FXML
    public void visitedURL(String targetURL) {
        if (targetURL.equals("")) {
            targetURL = "about:blank";
        }
        browser.getEngine().load(smartURL(targetURL));//加载url
        update();
    }

    /**
     * 首页
     */
    @FXML
    public void homepage() {
        BrowserTab homeTab = new BrowserTab(this);
        tabPane.getTabs().add(homeTab);
        tabPane.getSelectionModel().select(homeTab);
        update(homeTab);
    }

    /**
     * 刷新页面
     */
    @FXML
    public void refresh() {
        visitedURL();
    }

    /**
     * 加入收藏夹
     */
    @FXML
    public void bookmark() {
        Data.bookmarks.add(URLBar.getText());
        ObservableList<String> strList = FXCollections.observableArrayList();
        strList.addAll(Data.bookmarks);
        bookmarkListView.setItems(strList);
    }


    /**
     * 跳转到上一页
     */
    @FXML
    public void prePage() {
        int index = webHistory.getCurrentIndex();
        if (index == 0) {
            System.out.println("没有上一页了");
            return;
        }
        if (index > 0) {
            webHistory.go(-1);//上一页
            URLBar.setText(history.get(webHistory.getCurrentIndex()).getUrl());//更新url地址栏
        }
    }

    /**
     * 跳转到下一页
     */
    @FXML
    public void nextPage() {
        int size = webHistory.getEntries().size();
        int index = webHistory.getCurrentIndex();
        if (index >= size - 1) {
            System.out.println("没有下一页了");
        } else {
            webHistory.go(1);//下一页
            URLBar.setText(history.get(webHistory.getCurrentIndex()).getUrl());//更新url地址栏
        }
    }


    /**
     * 更新标签页
     */
    private void updateTabName() {
        browser.getEngine().documentProperty().addListener((observable, ov, document) -> {
            if (document != null) {
                String title = document.getElementsByTagName("title").item(0).getTextContent();
                NodeList nodeList = document.getElementsByTagName("a");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    org.w3c.dom.Node node = nodeList.item(i);
                    EventTarget t = (EventTarget) node;
                    t.addEventListener(CLICK, (Event evt) -> {
                        currentTab.setText(title);
                    }, false);
                }
                currentTab.setText(title);
            }
        });
    }

    /**
     * 全选url
     */
    private void setAllSelectedURL() {
        //设置url点击事件
        URLBar.setOnMouseClicked((event) -> {
            if (URLBar.getText().length() == URLBar.getCaretPosition()) {
                URLBar.selectAll();
            }
        });
    }

    /**
     * 判断URL内容类型，没有加协议的自动加上，如果不是网址格式则跳转到百度搜索
     *
     * @param url
     * @return
     */
    public static String smartURL(String url) {
        String SEARCH = "https://www.baidu.com/s?wd=";
        String PROTOCOL = "^(http|ftp|https)://\\S*";
        String URL_REG_EXP = "((http|ftp|https)://)?[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-.,@?^=%&amp;:/~+#]*[\\w\\-@?^=%&amp;/~+#])?";
        Pattern p = Pattern.compile(URL_REG_EXP);
        if (p.matcher(url).matches()) {
            if (!Pattern.compile(PROTOCOL).matcher(url).matches()) {
                url = "http://" + url;
            }
        } else if (!url.trim().isEmpty()) {
            url = SEARCH + url;
        }
        return url;
    }

    /**
     * 自适应界面
     */
    private void GUISelfAdaption() {
        // 宽度自适应
        vBox.prefWidthProperty().bind(pane.widthProperty());
        hBox.prefWidthProperty().bind(pane.widthProperty());
        tabPane.prefWidthProperty().bind(pane.widthProperty());

        // 高度自适应
        vBox.prefHeightProperty().bind(pane.heightProperty());
    }

    @FXML
    public void openMail() throws IOException {
        Stage settingStage = new Stage();
        Parent sub = FXMLLoader.load(getClass().getResource("/browser/layout/mail.fxml"));
        Scene secondScene = new Scene(sub, 700, 400);
        settingStage.setTitle("发送邮件");
        settingStage.setScene(secondScene);
        settingStage.show();
    }


    @FXML
    public void openDownload(ActionEvent actionEvent) throws IOException {
        Stage downloadStage = new Stage();
        Parent sub = FXMLLoader.load(getClass().getResource("/browser/layout/download.fxml"));
        sub.getStylesheets().add(getClass().getResource("/browser/static/app.css").toExternalForm());
        Scene secondScene = new Scene(sub, 700, 400);
        downloadStage.setTitle("下载文件");
        downloadStage.setScene(secondScene);
        downloadStage.show();
    }

    @FXML
    public void visitBookmarks(MouseEvent mouseEvent) {
        visitedURL(Data.bookmarks.get(bookmarkListView.getSelectionModel().getSelectedIndex()));
    }

    @FXML
    public void openHistory(ActionEvent actionEvent) throws IOException {
        Stage historyStage = new Stage();
        Parent sub = FXMLLoader.load(getClass().getResource("/browser/layout/history.fxml"));
        sub.getStylesheets().add(getClass().getResource("/browser/static/app.css").toExternalForm());
        Scene secondScene = new Scene(sub, 700, 400);
        historyStage.setTitle("历史记录");
        historyStage.setScene(secondScene);
        historyStage.show();
    }

    /**
     * 下载管理
     */
    @FXML
    public void downloadManage(ActionEvent actionEvent) throws IOException{
        Stage historyStage = new Stage();
        Parent sub = FXMLLoader.load(getClass().getResource("/browser/layout/downloadRecord.fxml"));
        sub.getStylesheets().add(getClass().getResource("/browser/static/app.css").toExternalForm());
        Scene secondScene = new Scene(sub, 700, 400);
        historyStage.setTitle("下载管理");
        historyStage.setScene(secondScene);
        historyStage.show();
    }
}
