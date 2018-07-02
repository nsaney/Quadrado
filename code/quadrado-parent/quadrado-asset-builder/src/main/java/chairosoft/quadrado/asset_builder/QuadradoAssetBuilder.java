package chairosoft.quadrado.asset_builder;

import chairosoft.quadrado.asset._resources.ResourceLoader;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.stream.Stream;

public class QuadradoAssetBuilder extends Application {
    
    ////// Constants //////
    public static final String APP_TITLE = "Quadrado Asset Builder";
    protected static final String[] APP_ICON_NAMES = {"16x16", "32x32", "64x64", "128x128" };
    public static final AppIconLoader APP_ICON_LOADER = new AppIconLoader();
    public static final String PATH_WEB_DIRECTORY = "web";
    public static final String PATH_INDEX_HTML = ResourceLoader.getAbsoluteResourcePath(
        true,
        PATH_WEB_DIRECTORY,
        "index.html"
    );
    
    
    ////// Main Method //////
    public static void main(String[] args) {
        launch(args);
    }
    
    
    ////// Instance Methods //////
    @Override
    public void start(Stage primaryStage) {
        // title
        primaryStage.setTitle(APP_TITLE);
        
        // icon(s)
        Stream.of(APP_ICON_NAMES)
            .map(APP_ICON_LOADER::loadOrNull)
            .filter(Objects::nonNull)
            .forEach(primaryStage.getIcons()::add);
        
        // web region
        BrowserRegion region = new BrowserRegion();
        WebEngine webEngine = region.browser.getEngine();
        String indexUrl = QuadradoAssetBuilder.class.getResource(PATH_INDEX_HTML).toExternalForm();
        webEngine.load(indexUrl);
        
        // scene
        Scene scene = new Scene(region);
        //scene.getStylesheets().add("TODO.css");
        primaryStage.setScene(scene);
        
        // show
        primaryStage.show();
    }
    
    
    ////// Static Inner Classes //////
    public static class BrowserRegion extends Region {
        
        //// Instance Fields ////
        public final WebView browser = new WebView();
        
        //// Constructor ////
        public BrowserRegion() {
            this.getChildren().add(this.browser);
        }
        
        //// Instance Methods ////
        @Override
        protected void layoutChildren() {
            double w = this.getWidth();
            double h = this.getHeight();
            this.layoutInArea(this.browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
        }
    }
}
