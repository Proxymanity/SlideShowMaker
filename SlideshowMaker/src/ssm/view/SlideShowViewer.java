package ssm.view;

import java.io.File;
import java.net.URL;
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
    }

    /**
     * This method initializes the UI controls and opens the window with the
     * first slide in the slideshow displayed.
     */
    public void startSlideShow() {
	// FIRST THE TOP PANE
        if(slides.getSelected() == null){
            slides.setSelected(slides.getSEVList().get(0));
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
