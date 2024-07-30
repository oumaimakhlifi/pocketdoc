package pocketDock.com.pocketDock.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private Cloudinary cloudinary;

    public CloudinaryService() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dwrsbwxkd",
                "api_key", "292415286598483",
                "api_secret", "ftb70Ly_Or2wrbHb2ZMwTAmaLxo"));
    }

    public String uploadProfileImage(MultipartFile profileImageFile) throws IOException {
        // Téléchargez l'image vers Cloudinary et obtenez l'URL de l'image téléchargée
        Map uploadResult = cloudinary.uploader().upload(profileImageFile.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("url");
    }
}
