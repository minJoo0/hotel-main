package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.DTO.HotelDTO;
import com.exam.hotelmanage1.DTO.RoomDTO;
import com.exam.hotelmanage1.Entity.HotelEntity;
import com.exam.hotelmanage1.Entity.HotelImgEntity;
import com.exam.hotelmanage1.Entity.RoomEntity;
import com.exam.hotelmanage1.Entity.RoomImgEntity;
import com.exam.hotelmanage1.Repository.HotelImgRepository;
import com.exam.hotelmanage1.Repository.HotelRepository;
import com.exam.hotelmanage1.Repository.RoomImgRepository;
import com.exam.hotelmanage1.Repository.RoomRepository;
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
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class RoomImgService {

    private final HotelImgRepository hotelImgRepository;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final ResourceLoader resourceLoader;
    private final RoomImgRepository roomImgRepository;
    private final RoomRepository roomRepository;
    private final FTPService ftpService;

    @Value("${project.uploadPath}")
    private String uploadPath;

    public void saveRoomImg(RoomDTO roomDTO, MultipartFile itemImgFile) throws Exception {
        // 원본 이미지 파일명
        String oriImgName = itemImgFile.getOriginalFilename();
        // FTP 서버에 이미지 파일 저장 후, 서버에 저장된 이미지 파일명을 반환받음
        String savedFileName = ftpService.uploadFile(Objects.requireNonNull(oriImgName), itemImgFile.getBytes());
        RoomEntity roomEntity = roomRepository.findById(roomDTO.getRoomNo()).orElseThrow(() -> new IllegalArgumentException("Invalid room No"));

        // RoomImgEntity 객체에 원본 파일명과 서버에 저장된 파일명을 설정
        RoomImgEntity roomImgEntity = RoomImgEntity.builder()
                .oriImgName(oriImgName)
                .imgName(savedFileName)
                .repImgYn("Y")
                .roomEntity(roomEntity)
                .build();

        // 여기에 RoomImgEntity 객체를 데이터베이스에 저장하는 로직을 추가
        roomImgRepository.save(roomImgEntity);
    }
    public List<RoomImgEntity> findByRoomEntityRoomNo(Long roomNo){
        List<RoomImgEntity> roomImgEntityList = roomImgRepository.findByRoomEntity_RoomNoOrderByRoomImgNoAsc(roomNo);

        return roomImgEntityList;

    }
}
