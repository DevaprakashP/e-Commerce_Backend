package com.deva.dream_shops.service.image;

import com.deva.dream_shops.dto.ImageDto;
import com.deva.dream_shops.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {
    Image getImageById(Long id);
    void deleteImageById(Long id);
    List<ImageDto> saveImages(List<MultipartFile> file, Long productid);
    void updateImage (MultipartFile file,Long imageId);
}
