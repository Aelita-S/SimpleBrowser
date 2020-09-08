package browser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("layout/frame.fxml"));
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("static/app.css").toExternalForm());
        primaryStage.setTitle("Simple Browser");
        primaryStage.getIcons().add(new Image("browser/static/app.png"));//设置程序图标
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

}

/**
 * 如果Main无法运行，可以使用这个类的main函数
 */
class APP {
    public static void main(String[] args) {
        Application.launch(Main.class,args);
    }
}
