package ssm.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import ssm.LanguagePropertyType;
import static ssm.StartupConstants.CSS_CLASS_HORIZONTAL_TOOLBAR_BUTTON;
import static ssm.StartupConstants.DEFAULT_SLIDE_SHOW_HEIGHT;
import static ssm.StartupConstants.ICON_NEXT;
import static ssm.StartupConstants.ICON_PREVIOUS;
import static ssm.StartupConstants.LABEL_SLIDE_SHOW_TITLE;
import static ssm.StartupConstants.PATH_SITES;
import ssm.error.ErrorHandler;
import static ssm.file.SlideShowFileManager.SLASH;
import ssm.model.Slide;
import ssm.model.SlideShowModel;
import ssm.view.SlideEditView;

/**
 * This class provides the UI for the slide show viewer, note that this class is
 * a window and contains all controls inside.
 *
 * @author McKilla Gorilla & _____________
 */
public class SlideShowViewer extends Stage {

    // THE MAIN UI

    SlideShowMakerView parentView;

    // THE DATA FOR THIS SLIDE SHOW
    SlideShowModel slides;
    ObservableList<SlideEditView> SEV;

    // HERE ARE OUR UI CONTROLS
    BorderPane borderPane;
    FlowPane topPane;
    Label slideShowTitleLabel;
    ImageView slideShowImageView;
    VBox bottomPane;
    Label captionLabel;
    FlowPane navigationPane;
    Button previousButton;
    Button nextButton;
    
    String imageDir;
    String cssDir;
    String jsDir;
    String jSonDir;
    String slideShowTitle;
    String SlideShowDir;
    String iconDir;
    String s;
    /**
     * This constructor just initializes the parent and slides references, note
     * that it does not arrange the UI or start the slide show view window.
     *
     * @param initParentView Reference to the main UI.
     */
    public SlideShowViewer(SlideShowMakerView initParentView) {
	// KEEP THIS FOR LATER
	parentView = initParentView;
        
	// GET THE SLIDES
	slides = parentView.getSlideShow();
        SEV = parentView.getSlideShow().getSEVList();
        
        //Create the sites directory if not exist
        File sites = new File(PATH_SITES);
        if(!sites.exists()){
            sites.mkdir();
       }
        slideShowTitle = (slides.getTitle());
        SlideShowDir = PATH_SITES + slideShowTitle;
        File SlideShowFolder = new File(SlideShowDir);
        if(SlideShowFolder.exists()){
            String[]entries = SlideShowFolder.list();
            for(String s: entries){
            File currentFile = new File(SlideShowFolder.getPath(),s);
            
            if(currentFile.isDirectory()){
                String[]MoarEntries = currentFile.list();
                for(String str: MoarEntries){
                File MoarCurrentFile = new File(currentFile.getPath(),str);
                
                    if(MoarCurrentFile.isDirectory()){
                        String[]EvenMoarEntries = MoarCurrentFile.list();
                         for(String string: EvenMoarEntries){
                             File EvenMoarCurrentFile = new File(MoarCurrentFile.getPath(), string);
                             EvenMoarCurrentFile.delete();
                         }
                    }
                MoarCurrentFile.delete();
                }
            }
            
            currentFile.delete();
            }
            SlideShowFolder.delete();
            SlideShowFolder.mkdir();
        }else{
            SlideShowFolder.mkdir();
        }
        cssDir = new String(SlideShowDir + "/css/");
        jsDir = new String(SlideShowDir + "/js/");
        imageDir = new String(SlideShowDir + "/img/");
        iconDir = new String(imageDir + "icons/");
        jSonDir = new String(SlideShowDir + "/jSon/");
        File css = new File(cssDir);
        File js = new File(jsDir);
        File images = new File(imageDir);
        File icon = new File(iconDir);
        File jSon = new File(jSonDir);
        boolean a = false,b = false,c = false, d= false, e = false;
            a = css.mkdir();
            b = js.mkdir();
            c = images.mkdir();
            d = jSon.mkdir();
            e = icon.mkdir();
            if(!a || !b || !c || !d || !e){
                ErrorHandler er = parentView.getErrorHandler();
                er.processError(LanguagePropertyType.ERROR_NOT_CREATED);
            }
    }

    /**
     * This method initializes the UI controls and opens the window with the
     * first slide in the slideshow displayed.
     */
    public void startSlideShow(){
	
        boolean b = true;
        
        if(slides.getSelected() == null){
            slides.setSelected(slides.getSEVList().get(0));
        }
        // Now copy over the images
        for(Slide slide: slides.getSlides()){
            File newImg = new File(imageDir + slide.getImageFileName());
            try {
                newImg.createNewFile();
            } catch (IOException ex) {
                ErrorHandler e = parentView.getErrorHandler();
                e.processError(LanguagePropertyType.ERROR_NOT_CREATED);
            }
            Path source =  Paths.get(slide.getImagePath() + slide.getImageFileName());
            Path dest = Paths.get(imageDir);
            CopyOption A = StandardCopyOption.REPLACE_EXISTING;
            try {
                Path destination = dest.resolve(slide.getImageFileName());
                Files.copy(source, destination, A);
            } catch (IOException ex) {
                ErrorHandler e = parentView.getErrorHandler();
                e.processError(LanguagePropertyType.ERROR_NOT_CREATED);
            }
        }
        //Now copy the ICONS over
        String Next = ("Next.png");
        String Pause =("Pause.png");
        String Play = ("Play.png");
        String Previous =("Previous.png");
        String[] icons = {Next,Pause,Play,Previous};
        for(String icon: icons){
            File newIcon = new File(iconDir + icon);
            try {
                newIcon.createNewFile();
            } catch (IOException ex) {
                ErrorHandler e = parentView.getErrorHandler();
                e.processError(LanguagePropertyType.ERROR_NOT_CREATED);
            }
            Path source =  Paths.get("baseFiles/img/icons/" + icon);
            Path dest = Paths.get(iconDir);
            CopyOption A = StandardCopyOption.REPLACE_EXISTING;
            try {
                Path destination = dest.resolve(icon);
                Files.copy(source, destination, A);
            } catch (IOException ex) {
                ErrorHandler e = parentView.getErrorHandler();
                e.processError(LanguagePropertyType.ERROR_NOT_CREATED);
            }
        }
        //Now copy over the JSON object
        if(b){
        File JSON = new File(jSonDir + slideShowTitle + ".json");
            try {
                JSON.createNewFile();
            } catch (IOException ex) {
                ErrorHandler e = parentView.getErrorHandler();
                e.processError(LanguagePropertyType.ERROR_NOT_CREATED);
            }
            Path source =  Paths.get("data/slide_shows/" + slideShowTitle + ".json");
            Path dest = Paths.get(jSonDir);
            CopyOption A = StandardCopyOption.REPLACE_EXISTING;
            try {
                Path destination = dest.resolve(slideShowTitle + ".json");
                Files.copy(source, destination, A);
            } catch (IOException ex) {
                ErrorHandler e = parentView.getErrorHandler();
                e.processError(LanguagePropertyType.ERROR_NOT_CREATED);
            }
         }
         // Now copy over the SlideShow.js
        if(b){
           File JS = new File(jsDir + "Slideshow.js");
            try {
                JS.createNewFile();
            } catch (IOException ex) {
                ErrorHandler e = parentView.getErrorHandler();
                e.processError(LanguagePropertyType.ERROR_NOT_CREATED);
            }
            Path source =  Paths.get("baseFiles/js/Slideshow.js");
            Path dest = Paths.get(jsDir);
            CopyOption A = StandardCopyOption.REPLACE_EXISTING;
            try {
                Path destination = dest.resolve("Slideshow.js");
                Files.copy(source, destination, A);
            } catch (IOException ex) {
                ErrorHandler e = parentView.getErrorHandler();
                e.processError(LanguagePropertyType.ERROR_NOT_CREATED);
            } 
        }
        //JQuery file
        if(b){
           File JQ = new File(jsDir + "jquery-2.1.4.min.js");
            try {
                JQ.createNewFile();
            } catch (IOException ex) {
                ErrorHandler e = parentView.getErrorHandler();
                e.processError(LanguagePropertyType.ERROR_NOT_CREATED);
            }
            Path source =  Paths.get("baseFiles/js/jquery-2.1.4.min.js");
            Path dest = Paths.get(jsDir);
            CopyOption A = StandardCopyOption.REPLACE_EXISTING;
            try {
                Path destination = dest.resolve("jquery-2.1.4.min.js");
                Files.copy(source, destination, A);
            } catch (IOException ex) {
                ErrorHandler e = parentView.getErrorHandler();
                e.processError(LanguagePropertyType.ERROR_NOT_CREATED);
            } 
        }
        //CSS file
        if(b){
           File CSS = new File(cssDir + "slideshow_style.css");
            try {
                CSS.createNewFile();
            } catch (IOException ex) {
                ErrorHandler e = parentView.getErrorHandler();
                e.processError(LanguagePropertyType.ERROR_NOT_CREATED);
            }
            Path source =  Paths.get("baseFiles/css/slideshow_style.css");
            Path dest = Paths.get(cssDir);
            CopyOption A = StandardCopyOption.REPLACE_EXISTING;
            try {
                Path destination = dest.resolve("slideshow_style.css");
                Files.copy(source, destination, A);
            } catch (IOException ex) {
                ErrorHandler e = parentView.getErrorHandler();
                e.processError(LanguagePropertyType.ERROR_NOT_CREATED);
            } 
        }
        //HTML file
        if(b){
           File index = new File(SlideShowDir + "/index.html");
           String s = index.getAbsolutePath();
            try {
                index.createNewFile();
            } catch (IOException ex) {
                ErrorHandler e = parentView.getErrorHandler();
                e.processError(LanguagePropertyType.ERROR_NOT_CREATED);
            }
            Path source =  Paths.get("baseFiles/index.html");
            Path dest = Paths.get(SlideShowDir);
            CopyOption A = StandardCopyOption.REPLACE_EXISTING;
            try {
                Path destination = dest.resolve("index.html");
                Files.copy(source, destination, A);
            } catch (IOException ex) {
                ErrorHandler e = parentView.getErrorHandler();
                e.processError(LanguagePropertyType.ERROR_NOT_CREATED);
            } 
        }
  
        // Writes the JSON file name to a javaScript file
            FileWriter fWriter = null;
            BufferedWriter writer = null;
        try {
           fWriter = new FileWriter(SlideShowDir +"/js/jsonName.js");
           writer = new BufferedWriter(fWriter);
           writer.write("var Name = '"+ slideShowTitle +".json';");
           writer.newLine(); 
           writer.close(); 
        } catch (IOException ex) {
            ErrorHandler e = parentView.getErrorHandler();
                e.processError(LanguagePropertyType.ERROR_NOT_CREATED);
        }   
        
        
        
        String siteHTML = ("file:" +s);
        Stage webViewerStage = new Stage();
        WebViewer webBrowser = new WebViewer(webViewerStage, siteHTML);
        webViewerStage.show();
        
        
        
        
       
    }
}
