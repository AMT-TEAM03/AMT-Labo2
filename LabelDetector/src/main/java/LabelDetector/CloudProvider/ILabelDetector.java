package LabelDetector.CloudProvider;

import LabelDetector.CloudProvider.AWS.JSON.IAwsJsonResponse;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface ILabelDetector<T> {

    // TODOR Label detection with base64 picture
    // RES Added label detection with base64 picture
    /**
     * Execute method, detects the labels in an image
     * 
     * @param imageBase64 A base64 string representing an image
     * @return La liste des labels detecte dans l'image
     */

    public List<IAwsJsonResponse> Execute(URL imageUrl) throws IllegalArgumentException, IOException;
}
