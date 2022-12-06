package LabelDetector.CloudProvider;

import LabelDetector.CloudProvider.AWS.JSON.IAwsJsonResponse;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface ILabelDetector<T> {

    /**
     * Execute method, detects the labels in an image
     * 
     * @param imageUrl link towards a pictures
     * @return La liste des labels detectés dans l'image ainsi que le temps de calcul des labels.
     * @exception IllegalArgumentException
     * @exception IOException
     */
    public List<IAwsJsonResponse> Execute(URL imageUrl) throws IllegalArgumentException, IOException;
}
