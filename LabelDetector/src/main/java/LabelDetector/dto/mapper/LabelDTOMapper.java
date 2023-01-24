package LabelDetector.dto.mapper;

import java.net.MalformedURLException;
import java.net.URL;

import LabelDetector.controller.request.LabelRequest;
import LabelDetector.dto.LabelDTO;

public class LabelDTOMapper {
    // pas pénalisé mais pourquoi pas static ?
    public LabelDTO mapToModel(LabelRequest request) throws MalformedURLException {
        return new LabelDTO()
                .setImageUrl(new URL(request.getImageUrl()))
                .setMaxPattern(request.getMaxPattern())
                .setMinConfidence(request.getMinConfidence());
    }
}
