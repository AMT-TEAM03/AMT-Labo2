package LabelDetector.dto;

import java.net.URL;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class LabelDTO {
    private URL imageUrl;
    private int maxPattern = 10;
    private float minConfidence = 90;
}