package ssm.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import properties_manager.PropertiesManager;
import ssm.LanguagePropertyType;
import ssm.controller.FileController;
import ssm.view.SlideShowMakerView;
import ssm.view.SlideEditView;

/**
 * This class manages all the data associated with a slideshow.
 * 
 * @author McKilla Gorilla & _____________
 */
public class SlideShowModel {
    SlideShowMakerView ui;
    String title;
    ObservableList<Slide> slides;
    ObservableList<SlideEditView> sev;
    SlideEditView selectedSEV;
    Slide selectedSlide;
    FileController FileControl;
    
    public SlideShowModel(SlideShowMakerView initUI) {
	ui = initUI;
        FileControl = ui.getFileController();
	slides = FXCollections.observableArrayList();
        sev = FXCollections.observableArrayList();
        ui.setTitle(title);
	reset();	
    }

    // ACCESSOR METHODS   
    public boolean isSlideSelected() {
	return selectedSlide != null;
    }
    
    public ObservableList<Slide> getSlides() {
	return slides;
    }
    
    public ObservableList<SlideEditView> getSEV(){
        return sev;
    }
    
    public Slide getSelectedSlide() {
	return selectedSlide;
    }

    public String getTitle() { 
	return ui.getTitle(); 
    }
    
    // MUTATOR METHODS
    public void setSelectedSlide(Slide initSelectedSlide) {
	selectedSlide = initSelectedSlide;
    }
    
    public void setTitle(String initTitle) { 
	title = initTitle;
        ui.setTitle(title);
    }

    // SERVICE METHODS
    
    /**
     * Resets the slide show to have no slides and a default title.
     */
    public void reset() {
	slides.clear();
        sev.clear();
	PropertiesManager props = PropertiesManager.getPropertiesManager();
	title = props.getProperty(LanguagePropertyType.DEFAULT_SLIDE_SHOW_TITLE);
        ui.setTitle(title);
	selectedSlide = null;
    }
    
    public void update(SlideEditView selected){
        int slideNumber;
        for (SlideEditView SEV : this.getSEV()) {
	    SEV.unselect();
	}
        this.selectedSEV = selected;
        slideNumber = sev.indexOf(selected);
        selectedSlide = slides.get(slideNumber);
        selected.select();
        ui.reloadSlideShowPane(this);
    }

    /**
     * Adds a slide to the slide show with the parameter settings.
     * @param initImageFileName File name of the slide image to add.
     * @param initImagePath File path for the slide image to add.
     */
    public void addSlide(   String initImageFileName,
			    String initImagePath) {
	Slide slideToAdd = new Slide(initImageFileName, initImagePath);
	slides.add(slideToAdd);
        SlideEditView SEVToAdd = new SlideEditView(slideToAdd);
        SEVToAdd.setFileController(this.FileControl);
        SEVToAdd.setSSM(this);
        sev.add(SEVToAdd);
	selectedSlide = slideToAdd;
	ui.reloadSlideShowPane(this);
    }
    
        /**
     * Adds a slide to the slide show with the parameter settings.
     * @param initImageFileName File name of the slide image to add.
     * @param initImagePath File path for the slide image to add.
     */
    public void addSlide(   String initImageFileName,
			    String initImagePath,
                            String Caption) {
	Slide slideToAdd = new Slide(initImageFileName, initImagePath);
	slides.add(slideToAdd);
        SlideEditView SEVToAdd = new SlideEditView(slideToAdd);
        SEVToAdd.setFileController(this.FileControl);
        SEVToAdd.setSSM(this);
        SEVToAdd.setText(Caption);
        sev.add(SEVToAdd);
	selectedSlide = slideToAdd;
	ui.reloadSlideShowPane(this);
    }

    public SlideEditView getSelected() {
        return this.selectedSEV;
    }
    public void setSelected(SlideEditView A){
        this.selectedSEV = A;
        this.selectedSlide = slides.get(sev.indexOf(A));
        this.update(selectedSEV);
        
    }
    public ObservableList<SlideEditView> getSEVList(){
        return this.sev;
    }
    
    public void next(){
        int i = sev.indexOf(selectedSEV);
        i++;
        selectedSEV = sev.get(i);
        update(selectedSEV);
    }
    
    public void prev(){
        int i = sev.indexOf(selectedSEV);
        i--;
        selectedSEV = sev.get(i);
        update(selectedSEV);
    }
    public void moveUp(){
        for (SlideEditView SEV : sev) {
	    if(SEV.equals(selectedSEV)){
                int i = sev.indexOf(SEV);
                if(i > 0){
                SlideEditView temp = SEV;
                int x = i -1;
                SlideEditView temp2 = sev.get(x);
                sev.set(i, temp2);
                sev.set(x, temp);
                }
                break;
            }
	}
        ui.reloadSlideShowPane(this);
    }
    public void moveDown(){
        for (SlideEditView SEV : sev) {
	    if(SEV.equals(selectedSEV)){
                int i = sev.indexOf(SEV);
                int x = i + 1;
                if(sev.get(x) != null){
                SlideEditView temp = SEV;
                SlideEditView temp2 = sev.get(x);
                sev.set(i, temp2);
                sev.set(x, temp);
                break;
                }
            }
	}
        ui.reloadSlideShowPane(this);
    }
    public void remove(){
        for (SlideEditView SEV : sev) {
	    if(SEV.equals(selectedSEV)){
                int i = sev.indexOf(SEV);
                sev.remove(SEV);
                Slide temp = slides.get(i);
                slides.remove(temp);
                ui.reloadSlideShowPane(this);
                }
	}
 //       ui.reloadSlideShowPane(this);
    }
    
    public SlideShowMakerView getUI(){
        return this.ui;
    }
}