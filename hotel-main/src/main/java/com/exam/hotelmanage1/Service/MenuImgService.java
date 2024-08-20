package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.DTO.MenuDTO;
import com.exam.hotelmanage1.Entity.MenuEntity;
import com.exam.hotelmanage1.Entity.MenuImgEntity;
import com.exam.hotelmanage1.Repository.MenuImgRepository;
import com.exam.hotelmanage1.Repository.MenuRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Getter
@Setter
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MenuImgService {

    private final MenuImgRepository menuImgRepository;
    private final MenuRepository menuRepository;
    private final ModelMapper modelMapper;
    private final ResourceLoader resourceLoader;
    private final FTPService ftpService;

    public void saveMenuImg(MenuDTO menuDTO, MultipartFile itemImgFile) throws Exception {
        // 원본 이미지 파일명
        String oriImgName = itemImgFile.getOriginalFilename();
        // FTP 서버에 이미지 파일 저장 후, 서버에 저장된 이미지 파일명을 반환받음
        String savedFileName = ftpService.uploadFile(oriImgName, itemImgFile.getBytes());
        MenuEntity menuEntity = menuRepository.findById(menuDTO.getMenuNo()).orElseThrow(() -> new IllegalArgumentException("Invalid menu No"));

        // MenuImgEntity 객체에 원본 파일명과 서버에 저장된 파일명을 설정
        MenuImgEntity menuImgEntity = MenuImgEntity.builder()
                .oriImgName(oriImgName)
                .imgName(savedFileName)
                .repImgYn("Y")
                .menuEntity(menuEntity)
                .build();

        // 여기에 MenuImgEntity 객체를 데이터베이스에 저장하는 로직을 추가
        menuImgRepository.save(menuImgEntity);
    }
    public List<MenuImgEntity> findByMenuEntityMenuNo(Long menuNo){
        List<MenuImgEntity> menuImgEntityList = menuImgRepository.findByMenuEntity_MenuNoOrderByMenuImgNoAsc(menuNo);

        return menuImgEntityList;

    }
}
