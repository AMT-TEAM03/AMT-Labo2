package CloudProvider;

import java.util.List;
import java.util.Map;

public interface ILabelDetector<T> {

    // TODO ajouter des explications, on comprends pas ce qu'il faut mettre dans
    // params, ici j'utiliserais la javadoc pour que ce soit disponible facilement à
    // l'utilisation et à l'implémentation
    public List<T> Execute(String imageUri, Map<String, Object> params);

    // TODO Label detection with base64 picture
}
