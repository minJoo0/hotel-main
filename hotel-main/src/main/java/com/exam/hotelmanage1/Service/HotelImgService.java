package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.DTO.HotelDTO;
import com.exam.hotelmanage1.DTO.MenuDTO;
import com.exam.hotelmanage1.DTO.StoreDTO;
import com.exam.hotelmanage1.Entity.*;
import com.exam.hotelmanage1.Repository.HotelImgRepository;
import com.exam.hotelmanage1.Repository.HotelRepository;
import com.exam.hotelmanage1.Repository.MenuImgRepository;
import com.exam.hotelmanage1.Repository.MenuRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Transactional
@Log4j2
public class HotelImgService {

    private final HotelImgRepository hotelImgRepository;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final ResourceLoader resourceLoader;
    private final FTPService ftpService;


    public void saveHotelImg(HotelDTO hotelDTO, MultipartFile itemImgFile) throws Exception {
        // 원본 이미지 파일명
        String oriImgName = itemImgFile.getOriginalFilename();
        // FTP 서버에 이미지 파일 저장 후, 서버에 저장된 이미지 파일명을 반환받음
        String savedFileName = ftpService.uploadFile(oriImgName, itemImgFile.getBytes());
        HotelEntity hotelEntity = hotelRepository.findById(hotelDTO.getHotelNo()).orElseThrow(() -> new IllegalArgumentException("Invalid hotel No"));

        // HotelImgEntity 객체에 원본 파일명과 서버에 저장된 파일명을 설정
        HotelImgEntity hotelImgEntity = HotelImgEntity.builder()
                .oriImgName(oriImgName)
                .imgName(savedFileName)
                .repImgYn("Y")
                .hotelEntity(hotelEntity)
                .build();

        // 여기에 HotelImgEntity 객체를 데이터베이스에 저장하는 로직을 추가
        hotelImgRepository.save(hotelImgEntity);
    }
    public List<HotelImgEntity> findByHotelEntityHotelNo(Long hotelNo){
        List<HotelImgEntity> hotelImgEntityList = hotelImgRepository.findByHotelEntity_HotelNoOrderByHotelImgNoAsc(hotelNo);

        return hotelImgEntityList;

    }
}
