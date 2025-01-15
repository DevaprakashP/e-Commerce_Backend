package com.deva.dream_shops.service.image;

import com.deva.dream_shops.dto.ImageDto;
import com.deva.dream_shops.exceptions.ResourceNotFoundException;
import com.deva.dream_shops.model.Image;
import com.deva.dream_shops.model.Product;
import com.deva.dream_shops.repository.ImageRepository;
import com.deva.dream_shops.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageService implements IImageService{

    @Autowired
    private ImageRepository imageRespository;
    @Autowired
    private IProductService productService;
    @Override
    public Image getImageById(Long id) {
        return imageRespository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("No Image found with id "+id));
    }

    @Override
    public void deleteImageById(Long id) {
        imageRespository.findById(id)
                .ifPresentOrElse(imageRespository::delete,
                        ()->{throw new ResourceNotFoundException("No Image found with id "+id);});

    }

    @Override
    public List<ImageDto> saveImages(List<MultipartFile> files, Long productid) {
        Product product = productService.getProductById(productid);
        List<ImageDto> savedImageDtos = new ArrayList<>();
        for(MultipartFile file:files){
           try{
                Image image = new Image();
                image.setFilename(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);

                String buildDownloadUrl = "/api/v1/images/image/download/";
                String downloadUrl = buildDownloadUrl+image.getImage();
                image.setDownloadUrl(downloadUrl);
                Image savedImage = imageRespository.save(image);
                savedImage.setDownloadUrl(buildDownloadUrl+savedImage.getId());
                imageRespository.save(savedImage);

                ImageDto imageDto = new ImageDto();
                imageDto.setId(savedImage.getId());
                imageDto.setFileName(savedImage.getFilename());
                imageDto.setDownloadUrl(savedImage.getDownloadUrl());
               savedImageDtos.add(imageDto);
           }catch (IOException | SQLException e) {
               throw new RuntimeException(e.getMessage());
           }
        }
        return savedImageDtos;
    }

    @Override
    public void updateImage(MultipartFile file, Long imageId) {
        Image image = getImageById(imageId);
        try {
            image.setFilename(file.getOriginalFilename());
            image.setFilename(file.getOriginalFilename());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRespository.save(image);
        }catch (IOException | SQLException e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
