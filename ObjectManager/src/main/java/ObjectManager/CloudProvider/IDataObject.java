package ObjectManager.CloudProvider;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public interface IDataObject {
    /**
     * CreateObject method, will upload an object on the remote storage
     * 
     * @param objectName The key of the file to upload
     * @param content   byte[] representing the content of the file
     * @return A public URL to download the file
     */
    public void CreateObject(String objectName, byte[] content) throws Exception;

    /**
     * GetURL method, will return a public URL to download the file
     * 
     * @param objectKey The key of the file to refer to with the URL
     * @return A public URL to download the file
     */
    public URL GetUrl(String objectKey) throws Exception;

    /**
     * DeleteObject method, will delete an object on the remote storage
     * 
     * @param objectKey The key of the file to delete
     */
    public void DeleteObject(String objectKey, boolean recursive) throws Exception;

    /**
     * GetObject method, will download an object from the remote storage
     * 
     * @param objectKey The key of the file to download
     * @return An InputStream from the downloaded file
     */
    public InputStream GetObject(String objectKey) throws Exception;
    
    /**
     * ListObjects method, will list all the objects on the remote storage
     * 
     * @return A list containing the keys of all the files on the remote storage
     */
    public List<String> ListObjects() throws Exception;
    
    /**
     * DoesObjectExists method, will return true if the object exists
     * on the remote storage, otherwise false.
     * 
     * @param objectKey The key of the file to check
     * @return true or false depending on object existance
     */
    public boolean DoesObjectExists(String objectKey) throws Exception;
}
