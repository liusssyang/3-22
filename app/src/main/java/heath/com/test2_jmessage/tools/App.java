package heath.com.test2_jmessage.tools;

public class App {
    private App2 extras;
    private String text;
    private String promptText;
    private String height;
    private String localThumbnailPath;
    private String width;
    private String fsize;
    private String isFileUploaded;
    private String media_crc32;
    private String media_id;
    public void setPromptText(String promptText){this.promptText=promptText;}
    public String getPromptText(){return this.promptText;}
    public String getWidth(){
        return this.width;
    }
    public void setWidth(String width){
        this.width=width;
    }
    public String getHeight(){
        return this.height;
    }
    public void setHeight(String height){
        this.height=height;
    }
    public String getText(){
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public App2 getExtras(){
        return extras;
    }
    public void setExtras(App2 extras){
        this.extras=extras;
    }
    public String getLocalThumbnailPath(){
        return  localThumbnailPath;
    }
    public void setLocalThumbnailPath(String localThumbnailPath){
        this.localThumbnailPath=localThumbnailPath;
    }
    public String getIsFileUploaded(){
        return isFileUploaded;
    }
    public void setIsFileUploaded(String isFileUploaded){
        this.isFileUploaded=isFileUploaded;
    }
}
