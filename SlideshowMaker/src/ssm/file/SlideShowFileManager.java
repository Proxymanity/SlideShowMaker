package ssm.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import static ssm.StartupConstants.PATH_SLIDE_SHOWS;
import ssm.model.Slide;
import ssm.model.SlideShowModel;
import ssm.view.SlideEditView;

/**
 * This class uses the JSON standard to read and write slideshow data files.
 * 
 * @author McKilla Gorilla & _____________
 */
public class SlideShowFileManager {
    // JSON FILE READING AND WRITING CONSTANTS
    public static String JSON_TITLE = "title";
    public static String JSON_SLIDES = "slides";
    public static String JSON_SEV = "SlideEditView";
    public static String CAPTION = "CAPTION";
    public static String JSON_IMAGE_FILE_NAME = "image_file_name";
    public static String JSON_IMAGE_PATH = "image_path";
    public static String JSON_EXT = ".json";
    public static String SLASH = "/";

    /**
     * This method saves all the data associated with a slide show to
     * a JSON file.
     * 
     * @param slideShowToSave The course whose data we are saving.
     * 
     * @throws IOException Thrown when there are issues writing
     * to the JSON file.
     */
    public void saveSlideShow(SlideShowModel slideShowToSave) throws IOException {
        // BUILD THE FILE PATH
        String slideShowTitle = "" + slideShowToSave.getTitle();
        String jsonFilePath = PATH_SLIDE_SHOWS + SLASH + slideShowTitle + JSON_EXT;
        
        // INIT THE WRITER
        OutputStream os = new FileOutputStream(jsonFilePath);
        JsonWriter jsonWriter = Json.createWriter(os);  
           
        // BUILD THE SLIDES ARRAY
        JsonArray slidesJsonArray = makeSlidesJsonArray(slideShowToSave.getSlides());
        //BUILD THE SEV ARRAY
        JsonArray SEVJsonArray = makeSEVJsonArray(slideShowToSave.getSEV());
        
        // NOW BUILD THE COURSE USING EVERYTHING WE'VE ALREADY MADE
        JsonObject courseJsonObject = Json.createObjectBuilder()
                                    .add(JSON_TITLE, slideShowToSave.getTitle())
                                    .add(JSON_SLIDES, slidesJsonArray)
                                    .add(JSON_SEV, SEVJsonArray)
                .build();
        
        // AND SAVE EVERYTHING AT ONCE
        jsonWriter.writeObject(courseJsonObject);
    }
    
    /**
     * This method loads the contents of a JSON file representing a slide show
     * into a SlideSShowModel objecct.
     * 
     * @param slideShowToLoad The slide show to load
     * @param jsonFilePath The JSON file to load.
     * @throws IOException 
     */
    public void loadSlideShow(SlideShowModel slideShowToLoad, String jsonFilePath) throws IOException {
        // LOAD THE JSON FILE WITH ALL THE DATA
        JsonObject json = loadJSONFile(jsonFilePath);
        
        // NOW LOAD THE COURSE
	slideShowToLoad.reset();
        slideShowToLoad.setTitle(json.getString(JSON_TITLE));
        JsonArray jsonSlidesArray = json.getJsonArray(JSON_SLIDES);
        JsonArray jsonSEVArray = json.getJsonArray(JSON_SEV);
        for (int i = 0; i < jsonSlidesArray.size(); i++) {
	    JsonObject slideJso = jsonSlidesArray.getJsonObject(i);
            JsonObject SEVJso = jsonSEVArray.getJsonObject(i);
	    slideShowToLoad.addSlide(	slideJso.getString(JSON_IMAGE_FILE_NAME),
					slideJso.getString(JSON_IMAGE_PATH),
                                        SEVJso.getString(CAPTION));
	}
    }

    // AND HERE ARE THE PRIVATE HELPER METHODS TO HELP THE PUBLIC ONES
    
    private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
        InputStream is = new FileInputStream(jsonFilePath);
        JsonReader jsonReader = Json.createReader(is);
        JsonObject json = jsonReader.readObject();
        jsonReader.close();
        is.close();
        return json;
    }    
    
    private ArrayList<String> loadArrayFromJSONFile(String jsonFilePath, String arrayName) throws IOException {
        JsonObject json = loadJSONFile(jsonFilePath);
        ArrayList<String> items = new ArrayList();
        JsonArray jsonArray = json.getJsonArray(arrayName);
        for (JsonValue jsV : jsonArray) {
            items.add(jsV.toString());
        }
        return items;
    }
    
    private JsonArray makeSlidesJsonArray(List<Slide> slides) {
        JsonArrayBuilder jsb = Json.createArrayBuilder();
        for (Slide slide : slides) {
	    JsonObject jso = makeSlideJsonObject(slide);
	    jsb.add(jso);
        }
        JsonArray jA = jsb.build();
        return jA;        
    }
    
    private JsonObject makeSlideJsonObject(Slide slide) {
        JsonObject jso = Json.createObjectBuilder()
		.add(JSON_IMAGE_FILE_NAME, slide.getImageFileName())
		.add(JSON_IMAGE_PATH, slide.getImagePath())
		.build();
	return jso;
    }
        private JsonArray makeSEVJsonArray(List<SlideEditView> sev) {
        JsonArrayBuilder jsb = Json.createArrayBuilder();
        for (SlideEditView SEV : sev) {
	    JsonObject jso = makeSEVJsonObject(SEV);
	    jsb.add(jso);
        }
        JsonArray jA = jsb.build();
        return jA;        
    }
    
    private JsonObject makeSEVJsonObject(SlideEditView SEV) {
        JsonObject jso = Json.createObjectBuilder()
		.add(CAPTION, SEV.getText())
		.build();
	return jso;
    }
}
