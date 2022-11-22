package CloudProvider;

import java.util.List;
import java.util.Map;

public interface ILabelDetector<T> {

    // TODOR ajouter des explications, on comprends pas ce qu'il faut mettre dans
    // params, ici j'utiliserais la javadoc pour que ce soit disponible facilement à
    // l'utilisation et à l'implémentation
    // RES added javadoc
    /**
     * Execute method
     * @param imageUri The AWS Key of the image to analyze 
     * @param params Some additional parameters if needed by Cloud Provider other than AWS. null if AWS.
     * @return La liste des labels detecte dans l'image
     */
    public List<T> Execute(String imageKey, Map<String, Object> params);

    // TODOR Label detection with base64 picture
    // RES Added label detection with base64 picture
    public List<T> Execute(String imageBase64);
}
