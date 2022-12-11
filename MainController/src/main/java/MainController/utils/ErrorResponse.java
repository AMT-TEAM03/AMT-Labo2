package MainController.utils;


public class ErrorResponse implements IResponse {
    private String error;

    public ErrorResponse(){}

    public ErrorResponse(String error){
        this.error = error;
    }

    public String getError(){
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
