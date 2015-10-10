package ssm.view;

import java.io.File;
import java.io.IOException;
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
import javafx.scene.layout.VBox;
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
        String slideShowTitle = (slides.getTitle());
        String SlideShowDir = PATH_SITES + slideShowTitle;
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
        File css = new File(cssDir);
        File js = new File(jsDir);
        File images = new File(imageDir);
        boolean a = false,b = false,c = false;
            a = css.mkdir();
            b = js.mkdir();
            c = images.mkdir();
            if(!a || !b || !c){
                ErrorHandler e = parentView.getErrorHandler();
                e.processError(LanguagePropertyType.ERROR_NOT_CREATED);
            }
    }

    /**
     * This method initializes the UI controls and opens the window with the
     * first slide in the slideshow displayed.
     */
    public void startSlideShow(){
	// FIRST THE TOP PANE
        if(slides.getSelected() == null){
            slides.setSelected(slides.getSEVList().get(0));
        }
        for(Slide slide: slides.getSlides()){
            File newImg = new File(imageDir + slide.getImageFileName());
            try {
                newImg.createNewFile();
            } catch (IOException ex) {
                ErrorHandler e = parentView.getErrorHandler();
                e.processError(LanguagePropertyType.ERROR_NOT_CREATED);
            }
            Path source =  Paths.get("images/slide_show_images/" + slide.getImageFileName());
            Path dest = Paths.get(imageDir);
            CopyOption A = StandardCopyOption.REPLACE_EXISTING;
            try {
                Path desti = dest.resolve(slide.getImageFileName());
                Files.copy(source, desti, A);
            } catch (IOException ex) {
                ErrorHandler e = parentView.getErrorHandler();
                e.processError(LanguagePropertyType.ERROR_NOT_CREATED);
            }
        }
        
        
	topPane = new FlowPane();
	topPane.setAlignment(Pos.CENTER);
	slideShowTitleLabel = new Label(slides.getTitle());
	slideShowTitleLabel.getStyleClass().add(LABEL_SLIDE_SHOW_TITLE);
	topPane.getChildren().add(slideShowTitleLabel);

	// THEN THE CENTER, START WITH THE FIRST IMAGE
	slideShowImageView = new ImageView();
	reloadSlideShowImageView();

	// THEN THE BOTTOM PANE
	bottomPane = new VBox();
	bottomPane.setAlignment(Pos.CENTER);
	captionLabel = new Label();
	if (slides.getSlides().size() > 0) {
	    captionLabel.setText(slides.getSelected().getText());
	}
	navigationPane = new FlowPane();
	bottomPane.getChildren().add(captionLabel);
	bottomPane.getChildren().add(navigationPane);

	// NOW SETUP THE CONTENTS OF THE NAVIGATION PANE
	navigationPane.setAlignment(Pos.CENTER);
	previousButton = parentView.initChildButton(navigationPane, ICON_PREVIOUS, LanguagePropertyType.TOOLTIP_PREVIOUS_SLIDE, CSS_CLASS_HORIZONTAL_TOOLBAR_BUTTON, false);
	nextButton = parentView.initChildButton(navigationPane, ICON_NEXT, LanguagePropertyType.TOOLTIP_NEXT_SLIDE, CSS_CLASS_HORIZONTAL_TOOLBAR_BUTTON, false);

	// NOW ARRANGE ALL OUR REGIONS
	borderPane = new BorderPane();
	borderPane.setTop(topPane);
	borderPane.setCenter(slideShowImageView);
	borderPane.setBottom(bottomPane);

	// NOW SETUP THE BUTTON HANDLERS
	previousButton.setOnAction(e -> {
	    slides.prev();
	    reloadSlideShowImageView();
	    reloadCaption();
	});
	nextButton.setOnAction(e -> {
	    slides.next();
	    reloadSlideShowImageView();
	    reloadCaption();
	});

	// NOW PUT STUFF IN THE STAGE'S SCENE
	Scene scene = new Scene(borderPane, 1000, 700);
	setScene(scene);
	this.showAndWait();
    }

    // HELPER METHOD
    private void reloadSlideShowImageView() {
	try {
	    Slide slide = slides.getSelectedSlide();
	    if (slide == null) {
		slides.setSelectedSlide(slides.getSlides().get(0));
	    }
	    slide = slides.getSelectedSlide();
	    String imagePath = slide.getImagePath() + SLASH + slide.getImageFileName();
	    File file = new File(imagePath);
	    
	    // GET AND SET THE IMAGE
	    URL fileURL = file.toURI().toURL();
	    Image slideImage = new Image(fileURL.toExternalForm());
	    slideShowImageView.setImage(slideImage);

	    // AND RESIZE IT
	    double scaledHeight = DEFAULT_SLIDE_SHOW_HEIGHT;
	    double perc = scaledHeight / slideImage.getHeight();
	    double scaledWidth = slideImage.getWidth() * perc;
	    slideShowImageView.setFitWidth(scaledWidth);
	    slideShowImageView.setFitHeight(scaledHeight);
	} catch (Exception e) {
	    // CANNOT SHOW A SLIDE SHOW WITHOUT ANY IMAGES
	    parentView.getErrorHandler().processError(LanguagePropertyType.ERROR_NO_SLIDESHOW_IMAGES);
	}
    }

    private void reloadCaption() {
	Slide slide = slides.getSelectedSlide();
        captionLabel.setText(slides.getSelected().getText());
    }
}
