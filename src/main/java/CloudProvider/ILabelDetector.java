package CloudProvider;

import java.util.List;
import java.util.Map;

public interface ILabelDetector {
    public List<String> Execute(String imageUri, Map<String, Object> params);
}
