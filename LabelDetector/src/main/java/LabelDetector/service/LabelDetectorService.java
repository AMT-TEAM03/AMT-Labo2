package LabelDetector.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import LabelDetector.CloudProvider.ILabelDetector;
import LabelDetector.CloudProvider.AWS.AwsLabelDetectorHelper;
import LabelDetector.CloudProvider.AWS.JSON.AwsPatternDetected;
import LabelDetector.CloudProvider.AWS.JSON.AwsReckognitionResult;
import LabelDetector.dto.LabelDTO;

@Service
public class LabelDetectorService {
    private ILabelDetector<AwsPatternDetected> labelHelper = new AwsLabelDetectorHelper();

    public AwsReckognitionResult Analyze(LabelDTO dto) throws IllegalArgumentException, IOException {
        return labelHelper.Analyze(
            dto.getImageUrl(),
            dto.getMaxPattern(),
            dto.getMinConfidence());
    }
}
