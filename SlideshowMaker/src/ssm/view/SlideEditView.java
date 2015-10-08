package ssm.view;

import java.io.File;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import properties_manager.PropertiesManager;
import ssm.LanguagePropertyType;
import static ssm.StartupConstants.CSS_CLASS_SLIDE_EDIT_VIEW;
import static ssm.StartupConstants.Selected_SEV;
import static ssm.StartupConstants.DEFAULT_THUMBNAIL_WIDTH;
import ssm.controller.ImageSelectionController;
import ssm.model.Slide;
import static ssm.file.SlideShowFileManager.SLASH;
import ssm.controller.FileController;
import ssm.model.SlideShowModel;


/**
 * This UI component has the controls for editing a single slide
 * in a slide show, including controls for selected the slide image
 * and changing its caption.
 * 
 * @author McKilla Gorilla & _____________
 */
public class SlideEditView extends HBox {
    // SLIDE THIS COMPONENT EDITS
    FileController FileControl;
    Slide slide;
    String TEXT = "";
    SlideShowModel slideShowModel;
    
    // DISPLAYS THE IMAGE FOR THIS SLIDE
    ImageView imageSelectionView;
    
    // CONTROLS FOR EDITING THE CAPTION
    VBox captionVBox;
    Label captionLabel;
    TextField captionTextField = new TextField();
    
    // PROVIDES RESPONSES FOR IMAGE SELECTION
    ImageSelectionController imageController;
    
    public boolean selected = false;

    /**
     * THis constructor initializes the full UI for this component, using
     * the initSlide data for initializing values./
     * 
     * @param initSlide The slide to be edited by this component.
     */
    public SlideEditView(Slide initSlide) {
	// FIRST SELECT THE CSS STYLE CLASS FOR THIS CONTAINER
        if(selected == false){
	this.getStyleClass().add(CSS_CLASS_SLIDE_EDIT_VIEW);
        }else{
            this.getStyleClass().add(Selected_SEV);
        }
	
	// KEEP THE SLIDE FOR LATER
	slide = initSlide;
	
	// MAKE SURE WE ARE DISPLAYING THE PROPER IMAGE
	imageSelectionView = new ImageView();
	updateSlideImage();

	// SETUP THE CAPTION CONTROLS
	captionVBox = new VBox();
	PropertiesManager props = PropertiesManager.getPropertiesManager();
	captionLabel = new Label(props.getProperty(LanguagePropertyType.LABEL_CAPTION));
	captionTextField = new TextField(TEXT);
	captionVBox.getChildren().add(captionLabel);
	captionVBox.getChildren().add(captionTextField);
	// LAY EVERYTHING OUT INSIDE THIS COMPONENT
	getChildren().add(imageSelectionView);
	getChildren().add(captionVBox);

	// SETUP THE EVENT HANDLERS
	imageController = new ImageSelectionController();
	imageSelectionView.setOnMousePressed(e -> {
	    imageController.processSelectImage(slide, this); 
            FileControl.markFileAsNotSaved();
	});
        
        
        this.setOnMouseClicked(e ->{
            slideShowModel.update(this); 
        });
    }
    
    // SETS THE PARENT CLASS SO WE KNOW WHAT IT IS
    public void setSSM(SlideShowModel SSM){
        this.slideShowModel = SSM;
    }
    
    // Unselects  this SEV
    public void unselect(){
        this.selected = false;
        this.getStyleClass().clear();
        this.getStyleClass().add(CSS_CLASS_SLIDE_EDIT_VIEW);
    }
    
    public Slide getSlide(){
        return this.slide;
    }
    
    // Selects this SEV
    public void select(){
        this.selected = true;
        this.getStyleClass().clear();
        this.getStyleClass().add(Selected_SEV);
    }
    
    /**
     * This function gets the image for the slide and uses it to
     * update the image displayed.
     */
    public void updateSlideImage() {
	String imagePath = slide.getImagePath() + SLASH + slide.getImageFileName();
	File file = new File(imagePath);
	try {
	    // GET AND SET THE IMAGE
	    URL fileURL = file.toURI().toURL();
	    Image slideImage = new Image(fileURL.toExternalForm());
	    imageSelectionView.setImage(slideImage);
	    
	    // AND RESIZE IT
	    double scaledWidth = DEFAULT_THUMBNAIL_WIDTH;
	    double perc = scaledWidth / slideImage.getWidth();
	    double scaledHeight = slideImage.getHeight() * perc;
	    imageSelectionView.setFitWidth(scaledWidth);
	    imageSelectionView.setFitHeight(scaledHeight);
	} catch (Exception e) {
	    // @todo - use Error handler to respond to missing image
	}
    }    
    public void setText(String str){
        this.TEXT = str;
        captionTextField.setText(this.TEXT);
    }
    public void setText(){
               TEXT = captionTextField.getText();
        }
    
    public String getText(){
        String s = captionTextField.getText();
        return s;
    }
    
    public void setFileController(FileController FC){
        FileControl = FC;
    }
    
    public SlideShowModel getSSM(){
        return this.slideShowModel;
    }
            
            
}