package LabelDetector.CloudProvider;

import LabelDetector.CloudProvider.AWS.JSON.IAwsJsonResponse;

import java.net.URL;
import java.util.List;

public interface ILabelDetector<T> {

    // TODOR ajouter des explications, on comprends pas ce qu'il faut mettre dans
    // params, ici j'utiliserais la javadoc pour que ce soit disponible facilement à
    // l'utilisation et à l'implémentation
    // RES added javadoc
    /**
     * Execute method, detects the labels in an image
     * @param imageUri The AWS Key of the image to analyze 
     * @param params Some additional parameters if needed by Cloud Provider other than AWS. null if AWS.
     * @return La liste des labels detecte dans l'image
     */
    //public List<T> Execute(String imageKey, Map<String, Object> params);

    // TODOR Label detection with base64 picture
    // RES Added label detection with base64 picture
    /**
     * Execute method, detects the labels in an image
     * 
     * @param imageBase64 A base64 string representing an image
     * @return La liste des labels detecte dans l'image
     */

    public List<IAwsJsonResponse> Execute(URL imageUrl);
}
