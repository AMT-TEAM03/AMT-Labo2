package ObjectManager.dto.mapper;

import ObjectManager.controller.request.ObjectRequest;
import ObjectManager.dto.ObjectDTO;

public class ObjectDTOMapper {
    public ObjectDTO mapToModel(ObjectRequest request) {
        return new ObjectDTO()
                .setName(request.getName())
                .setImage(request.getImage());
    }
}