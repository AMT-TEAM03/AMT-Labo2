package ObjectManager.service;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Service;

import ObjectManager.CloudProvider.IDataObject;
import ObjectManager.CloudProvider.AWS.AwsDataObjectHelper;
import ObjectManager.dto.ObjectDTO;

@Service
public class ObjectService {

    private IDataObject objectHelper = new AwsDataObjectHelper();

    public void CreateObject(ObjectDTO dto) throws Exception{
        objectHelper.CreateObject(dto.getName(), dto.getImage());
    }

    public InputStream DownloadObject(ObjectDTO dto) throws Exception{
        return objectHelper.GetObject(dto.getName());
    }

    public void DeleteObject(ObjectDTO dto) throws Exception {
        objectHelper.DeleteObject(dto.getName(), false);
    }

    public List<String> ListObjects(ObjectDTO dto) throws Exception{
        return objectHelper.ListObjects();
    }

    public URL GetObjectUrl(ObjectDTO dto) throws Exception{
        return objectHelper.GetUrl(dto.getName());
    }

    public boolean DoesObjectExists(ObjectDTO dto) throws Exception{
        return objectHelper.DoesObjectExists(dto.getName());
    }

    public void PrepareTestScenario(ObjectDTO dto) throws Exception {
        switch(dto.getName()){
            case "1":
                objectHelper.DeleteBucket(true);
                break;
            case "2":
                break;
            case "3":
                break;
        }
    }
}