package LabelDetector.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
// élégant, je connaissait pas cette annotation merci !
@Accessors(chain = true)
public class LabelRequest {
    private String imageUrl;
    private int maxPattern = 10;
    private float minConfidence = 90;
}