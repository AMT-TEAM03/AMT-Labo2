package LabelDetector.CloudProvider;

// pas pénalisé mais en java les packages sont en minuscule normalement. 
// Vous pouvez les mettre en majuscule si c'est une question d'honneur pour vous 
// MAIS vous devez à ce moment là mettre des majuscules a TOUS les packages que vous écrivez.
// en gros soyez uniforme
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
