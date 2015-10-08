package ssm.controller;

import java.io.File;
import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import ssm.LanguagePropertyType;
import static ssm.LanguagePropertyType.TOOLTIP_NEXT_SLIDE;
import static ssm.LanguagePropertyType.TOOLTIP_PREVIOUS_SLIDE;
import static ssm.LanguagePropertyType.TOOLTIP_VIEW_SLIDE_SHOW;
import static ssm.StartupConstants.CSS_CLASS_HORIZONTAL_TOOLBAR_BUTTON;
import static ssm.StartupConstants.ICON_NEXT;
import static ssm.StartupConstants.ICON_PREVIOUS;
import static ssm.StartupConstants.ICON_VIEW_SLIDE_SHOW;
import static ssm.StartupConstants.PATH_ICONS;
import ssm.model.SlideShowModel;
import ssm.error.ErrorHandler;
import ssm.file.SlideShowFileManager;
import ssm.view.SlideShowMakerView;
import static ssm.StartupConstants.PATH_SLIDE_SHOWS;
import static ssm.StartupConstants.PATH_SLIDE_SHOW_IMAGES;
import static ssm.StartupConstants.STYLE_SHEET_UI;
import ssm.view.SlideEditView;
/**
 * This class serves as the controller for all file toolbar operations,
 * driving the loading and saving of slide shows, among other things.
 * 
 * @author McKilla Gorilla & _____________
 */
public class FileController {

    // WE WANT TO KEEP TRACK OF WHEN SOMETHING HAS NOT BEEN SAVED
    private boolean saved;
    boolean saveWork = true;
    boolean cancel = false;

    // THE APP UI
    private SlideShowMakerView ui;
    
    // THIS GUY KNOWS HOW TO READ AND WRITE SLIDE SHOW DATA
    private SlideShowFileManager slideShowIO;

    /**
     * This default constructor starts the program without a slide show file being
     * edited.
     *
     * @param initSlideShowIO The object that will be reading and writing slide show
     * data.
     */
    public FileController(SlideShowMakerView initUI, SlideShowFileManager initSlideShowIO) {
        // NOTHING YET
        saved = true;
	ui = initUI;
        slideShowIO = initSlideShowIO;
    }
    
    public void markAsEdited() {
        saved = false;
        ui.updateToolbarControls(saved);
    }

    /**
     * This method starts the process of editing a new slide show. If a pose is
     * already being edited, it will prompt the user to save it first.
     */
    public void handleNewSlideShowRequest() {
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToMakeNew = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE WITH A CANCEL
                continueToMakeNew = promptToSave();
            }

            // IF THE USER REALLY WANTS TO MAKE A NEW COURSE
            if (continueToMakeNew) {
                // RESET THE DATA, WHICH SHOULD TRIGGER A RESET OF THE UI
                SlideShowModel slideShow = ui.getSlideShow();
		slideShow.reset();
                ui.reloadSlideShowPane(slideShow);
                saved = false;

                // REFRESH THE GUI, WHICH WILL ENABLE AND DISABLE
                // THE APPROPRIATE CONTROLS
                ui.updateToolbarControls(saved);
                
                //disable the Save & View SS due to empty
                ui.disable();

                // TELL THE USER THE SLIDE SHOW HAS BEEN CREATED
                
           Stage temp = new Stage();
           Button Ok = new Button("Ok");
           Ok.setOnAction(new EventHandler<ActionEvent>(){
               @Override
               public void handle(ActionEvent e){
                   temp.close();
               }
           });
           HBox buttonBox = new HBox();  
           buttonBox.setAlignment(Pos.CENTER);
           buttonBox.getChildren().addAll(Ok);
           VBox pane = new VBox();
           PropertiesManager props = PropertiesManager.getPropertiesManager();
           Label text = new Label(props.getProperty(LanguagePropertyType.New_SLIDESHOW));
           text.setAlignment(Pos.CENTER);
           pane.getChildren().addAll(text);
           
           
           BorderPane main = new BorderPane();
           main.setTop(pane);
           main.setCenter(buttonBox);
           Scene scene = new Scene(main);
           temp.setScene(scene);
           temp.showAndWait();
            }
        } catch (IOException ioe) {
            ErrorHandler eH = ui.getErrorHandler();
            // @todo provide error message
            eH.processError(LanguagePropertyType.ERROR_UNEXPECTED);
        }
    }

    /**
     * This method lets the user open a slideshow saved to a file. It will also
     * make sure data for the current slideshow is not lost.
     */
    public void handleLoadSlideShowRequest() {
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToOpen = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE WITH A CANCEL
                continueToOpen = promptToSave();
            }

            // IF THE USER REALLY WANTS TO OPEN A POSE
            if (continueToOpen) {
                // GO AHEAD AND PROCEED MAKING A NEW POSE
                promptToOpen();
            }
        } catch (IOException ioe) {
            ErrorHandler eH = ui.getErrorHandler();
            //@todo provide error message
            eH.processError(LanguagePropertyType.ERROR_DATA_FILE_LOADING);
        }
    }

    /**
     * This method will save the current slideshow to a file. Note that we already
     * know the name of the file, so we won't need to prompt the user.
     */
    public boolean handleSaveSlideShowRequest() {
        try {
	    // GET THE SLIDE SHOW TO SAVE
	    SlideShowModel slideShowToSave = ui.getSlideShow();
	    
            // SAVE IT TO A FILE
            slideShowIO.saveSlideShow(slideShowToSave);

            // MARK IT AS SAVED
            saved = true;

            // AND REFRESH THE GUI, WHICH WILL ENABLE AND DISABLE
            // THE APPROPRIATE CONTROLS
            ui.updateToolbarControls(saved);
	    return true;
        } catch (IOException ioe) {
            ErrorHandler eH = ui.getErrorHandler();
            // @todo
            eH.processError(LanguagePropertyType.ERROR_UNEXPECTED);
	    return false;
        }
    }

     /**
     * This method will exit the application, making sure the user doesn't lose
     * any data first.
     */
    public void handleExitRequest() {
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToExit = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE
                continueToExit = promptToSave();
            }

            // IF THE USER REALLY WANTS TO EXIT THE APP
            if (continueToExit) {
                // EXIT THE APPLICATION
                System.exit(0);
            }
        } catch (IOException ioe) {
            ErrorHandler eH = ui.getErrorHandler();
            // @todo
            eH.processError(LanguagePropertyType.ERROR_UNEXPECTED);
        }
    }

    /**
     * This helper method verifies that the user really wants to save their
     * unsaved work, which they might not want to do. Note that it could be used
     * in multiple contexts before doing other actions, like creating a new
     * pose, or opening another pose, or exiting. Note that the user will be
     * presented with 3 options: YES, NO, and CANCEL. YES means the user wants
     * to save their work and continue the other action (we return true to
     * denote this), NO means don't save the work but continue with the other
     * action (true is returned), CANCEL means don't save the work and don't
     * continue with the other action (false is returned).
     *
     * @return true if the user presses the YES option to save, true if the user
     * presses the NO option to not save, false if the user presses the CANCEL
     * option to not continue.
     */
    private boolean promptToSave() throws IOException {
        // PROMPT THE USER TO SAVE UNSAVED WORK
         saveWork = true;
         cancel = false;
         PropertiesManager props = PropertiesManager.getPropertiesManager();
           Stage temp = new Stage();
           Button Save = new Button(props.getProperty(LanguagePropertyType.SAVE));
           Button No = new Button(props.getProperty(LanguagePropertyType.NO_SAVE));
           Button Cancel = new Button(props.getProperty(LanguagePropertyType.Cancel));;
           Save.setOnAction(new EventHandler<ActionEvent>(){
               @Override
               public void handle(ActionEvent e){
                   saveWork = true;
                   temp.close();
               }
           });
           No.setOnAction(new EventHandler<ActionEvent>(){
               @Override
               public void handle(ActionEvent e){
                   saveWork = false;
                   temp.close();
               }
           });
           Cancel.setOnAction(new EventHandler<ActionEvent>(){
               @Override
               public void handle(ActionEvent e){
                   saveWork = false;
                   cancel = true;
                   temp.close();
               }
           });
           HBox buttonBox = new HBox();  
           buttonBox.setAlignment(Pos.CENTER);
           buttonBox.getChildren().addAll(Save,No,Cancel);
           Pane pane = new Pane();
           Label text = new Label(props.getProperty(LanguagePropertyType.SAVEPROMPT));
           text.setAlignment(Pos.CENTER);
           pane.getChildren().addAll(text);
           
           BorderPane main = new BorderPane();
           main.setTop(pane);
           main.setCenter(buttonBox);
           Scene scene = new Scene(main);
           temp.setScene(scene);
           temp.showAndWait();
        // IF THE USER SAID YES, THEN SAVE BEFORE MOVING ON
        if (saveWork) {
            SlideShowModel slideShow = ui.getSlideShow();
            slideShowIO.saveSlideShow(slideShow);
            saved = true;
        } // IF THE USER SAID CANCEL, THEN WE'LL TELL WHOEVER
        // CALLED THIS THAT THE USER IS NOT INTERESTED ANYMORE
        else if (cancel == true) {
            saveWork = false;
            cancel = false;
            return false;
        }

        // IF THE USER SAID NO, WE JUST GO ON WITHOUT SAVING
        // BUT FOR BOTH YES AND NO WE DO WHATEVER THE USER
        // HAD IN MIND IN THE FIRST PLACE
            saveWork = false;
        return true;
    }

    /**
     * This helper method asks the user for a file to open. The user-selected
     * file is then loaded and the GUI updated. Note that if the user cancels
     * the open process, nothing is done. If an error occurs loading the file, a
     * message is displayed, but nothing changes.
     */
    private void promptToOpen() {
        // AND NOW ASK THE USER FOR THE COURSE TO OPEN
        FileChooser slideShowFileChooser = new FileChooser();
        slideShowFileChooser.setInitialDirectory(new File(PATH_SLIDE_SHOWS));
        File selectedFile = slideShowFileChooser.showOpenDialog(ui.getWindow());

        // ONLY OPEN A NEW FILE IF THE USER SAYS OK
        if (selectedFile != null) {
            try {
		SlideShowModel slideShowToLoad = ui.getSlideShow();
                slideShowIO.loadSlideShow(slideShowToLoad, selectedFile.getAbsolutePath());
                ui.reloadSlideShowPane(slideShowToLoad);
                saved = true;
                ui.updateToolbarControls(saved);
            } catch (Exception e) {
                ErrorHandler eH = ui.getErrorHandler();
                // @todo
                eH.processError(LanguagePropertyType.ERROR_UNEXPECTED);
            }
        }
    }

    /**
     * This mutator method marks the file as not saved, which means that when
     * the user wants to do a file-type operation, we should prompt the user to
     * save current work first. Note that this method should be called any time
     * the pose is changed in some way.
     */
    public void markFileAsNotSaved() {
        saved = false;
        ui.updateToolbarControls(saved);
        
    }

    /**
     * Accessor method for checking to see if the current pose has been saved
     * since it was last editing. If the current file matches the pose data,
     * we'll return true, otherwise false.
     *
     * @return true if the current pose is saved to the file, false otherwise.
     */
    public boolean isSaved() {
        return saved;
    }


    public Button initChildButton(
	    Pane toolbar, 
	    String iconFileName, 
	    LanguagePropertyType tooltip,
	    boolean disabled) {
	PropertiesManager props = PropertiesManager.getPropertiesManager();
	String imagePath = "file:" + PATH_ICONS + iconFileName;
	Image buttonImage = new Image(imagePath);
	Button button = new Button();
	button.setDisable(disabled);
	button.setGraphic(new ImageView(buttonImage));
	Tooltip buttonTooltip = new Tooltip(props.getProperty(tooltip.toString()));
	button.setTooltip(buttonTooltip);
	toolbar.getChildren().add(button);
	return button;
    }
}


