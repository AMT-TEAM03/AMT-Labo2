package LabelDetector.CloudProvider;

import LabelDetector.CloudProvider.AWS.JSON.AwsReckognitionResult;

import java.io.IOException;
import java.net.URL;

public interface ILabelDetector<T> {

    /**
     * Execute method, detects the labels in an image
     * 
     * @param imageUrl link towards a pictures
     * @return La liste des labels detectés dans l'image ainsi que le temps de calcul des labels.
     * @exception IllegalArgumentException
     * @exception IOException
     */
    public AwsReckognitionResult Analyze(URL imageUrl, int maxPattern, float confidence_threshold) throws IllegalArgumentException, IOException;

    public AwsReckognitionResult Analyze(URL imageUrl, int maxPattern) throws IllegalArgumentException, IOException;

    public AwsReckognitionResult Analyze(URL imageUrl, float confidence_threshold) throws IllegalArgumentException, IOException;

    public AwsReckognitionResult Analyze(URL imageUrl) throws IllegalArgumentException, IOException;

}
