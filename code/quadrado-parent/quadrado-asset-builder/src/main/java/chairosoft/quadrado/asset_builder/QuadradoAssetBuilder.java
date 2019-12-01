package chairosoft.quadrado.asset_builder;

import chairosoft.quadrado.asset._resources.ResourceLoader;
import com.sun.javafx.application.LauncherImpl;
import com.sun.javafx.application.PlatformImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.apache.log4j.Logger;

import java.util.Objects;
import java.util.stream.Stream;

public class QuadradoAssetBuilder extends Application {
    
    ////// Constants //////
    private static final Logger logger = Logger.getLogger(QuadradoAssetBuilder.class);
    public static final String APP_TITLE = "Quadrado Asset Builder";
    private static final String[] APP_ICON_NAMES = {"16x16", "32x32", "64x64", "128x128" };
    public static final AppIconLoader APP_ICON_LOADER = new AppIconLoader();
    public static final String PATH_WEB_DIRECTORY = "web";
    public static final String PATH_INDEX_HTML = ResourceLoader.getAbsoluteResourcePath(
        true,
        PATH_WEB_DIRECTORY,
        "index.html"
    );
    
    
    ////// Main Method //////
    public static void main(String[] args) {
        LauncherImpl.launchApplication(
            QuadradoAssetBuilder.class,
            QuadradoAssetBuilderPreloader.class,
            args
        );
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
        
        // enable upcalls
        webEngine.getLoadWorker().stateProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != Worker.State.SUCCEEDED) { return; }
            JSObject window = (JSObject)webEngine.executeScript("window");
            window.setMember("backend", Backend.INSTANCE);
        }));
        
        // index page
        String indexUrl = QuadradoAssetBuilder.class.getResource(PATH_INDEX_HTML).toExternalForm();
        webEngine.load(indexUrl);
        
        // scene
        Scene scene = new Scene(region);
        scene.setOnKeyPressed(new QuadradoAssetBuilderKeyPressedEventHandler());
        primaryStage.setScene(scene);
        
        // show
        primaryStage.show();
        primaryStage.setMaximized(true);
    }
    
    
    ////// Static Inner Classes //////
    public static class QuadradoAssetBuilderPreloader extends Preloader {
        @Override
        public void start(Stage stage) throws Exception {
            // see: https://github.com/warmuuh/milkman/commit/ab05ea94a608b5e5476bfd234befaf9db6df5b39
            PlatformImpl.runAndWait(
                () -> com.sun.glass.ui.Application.GetApplication().setName(APP_TITLE)
            );
        }
    }
    
    public static class QuadradoAssetBuilderKeyPressedEventHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            if (event.isShortcutDown()) {
                switch (event.getCode()) {
                    case Q: Platform.exit(); break;
                }
            }
        }
    }
    
    public static class BrowserRegion extends Region {
        //// Instance Fields ////
        public final WebView browser = new WebView();
        
        //// Constructor ////
        public BrowserRegion() {
            this.browser.setContextMenuEnabled(false);
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
    
    public static class Backend {
        //// Constants ////
        public static final Backend INSTANCE = new Backend();
        
        //// Constructor ////
        private Backend() { }
        
        //// Instance Fields ////
        public final Logger logger = QuadradoAssetBuilder.logger;
        
    }
    
}
