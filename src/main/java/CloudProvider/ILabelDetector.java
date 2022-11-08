package CloudProvider;

import java.util.List;
import java.util.Map;

public interface ILabelDetector<T> {
    public List<T> Execute(String imageUri, Map<String, Object> params);
}
