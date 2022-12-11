package ObjectManager.utils;

public class SuccessResponse<T> implements IResponse {
    private boolean success;
    private T data;
    private String type;

    public SuccessResponse(){}

    public SuccessResponse(
        T data
    ){
        this.type = T.class.toString();
        System.out.println(this.type);
        this.success = true;
        this.data = data;
    }

    public boolean getSuccess(){
        return this.success;
    }

    public T getData(){
        return this.data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setData(T data) {
        this.data = data;
    }
}
