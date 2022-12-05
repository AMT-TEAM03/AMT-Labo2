package LabelDetector.utils;


public class ErrorResponse implements IResponse {
    private String error;

    public ErrorResponse(String error){
        this.error = error;
    }

    public String getError(){
        return this.error;
    }
}
