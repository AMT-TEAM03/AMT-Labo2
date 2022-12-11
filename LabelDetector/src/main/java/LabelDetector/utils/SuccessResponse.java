package LabelDetector.utils;

public class SuccessResponse<T> implements IResponse {
    private boolean success;
    private T data;
    // private String type;

    public SuccessResponse(
            T data
    ){
        this.success = true;
        this.data = data;
    }

    public boolean getSuccess(){
        return this.success;
    }

    public T getData(){
        return this.data;
    }
}
