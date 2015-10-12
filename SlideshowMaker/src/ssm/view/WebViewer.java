package ssm.view;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/*
*   This class handles the WebView
*   Creates and sets the WebView
*/
public class WebViewer {
    Stage Stage;
    WebView View;
    WebEngine ViewerEngine;
    BorderPane ViewerPane;
    Scene ViewerScene;
    
/**
 * 
 * @param Stage The Stage to set the WebView in
 * @param HTMLDirectory The Directory of the HTML document
 */
    public WebViewer(Stage Stage, String HTMLDirectory) {
        Stage = Stage;
        View = new WebView();
        ViewerEngine = View.getEngine();
        ViewerEngine.load(HTMLDirectory);
     //Engine.setUserStyleSheetLocation("./index/public_html/slideshow_style.css");
        ViewerPane = new BorderPane();
        ViewerPane.setCenter(View);
        ViewerScene = new Scene(ViewerPane);
        Stage.setScene(ViewerScene);
    }
}

