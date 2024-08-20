package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.DTO.RoomDTO;
import com.exam.hotelmanage1.DTO.RoomCodeDTO;
import com.exam.hotelmanage1.Entity.RoomCodeEntity;
import com.exam.hotelmanage1.Entity.RoomEntity;
import com.exam.hotelmanage1.Repository.RoomCodeRepository;
import com.exam.hotelmanage1.Repository.RoomRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class RoomCodeService {

    private final RoomCodeRepository roomCodeRepository;
    private final ModelMapper modelMapper;
    private final RoomRepository roomRepository;

    @Transactional
    public void saveRoomCodes(List<RoomCodeDTO> roomCodes, RoomEntity existingRoomEntity) {
        List<RoomCodeEntity> roomCodeEntities = roomCodes.stream()
                .map(roomCodeDTO -> modelMapper.map(roomCodeDTO, RoomCodeEntity.class))
                .peek(roomCodeEntity -> roomCodeEntity.setRoomEntity(existingRoomEntity)) // RoomEntity 설정
                .collect(Collectors.toList());

        // RoomCodeEntity 저장
        roomCodeRepository.saveAll(roomCodeEntities);

        // fullCodes 문자열 생성
        String fullCodes = roomCodeEntities.stream()
                .sorted(Comparator.comparingInt(RoomCodeEntity::getOrderIdx))
                .map(RoomCodeEntity::getFullCode)
                .collect(Collectors.joining());

        // existingRoomEntity의 fullCodes 업데이트
        existingRoomEntity.setFullCodes(fullCodes);
        roomRepository.save(existingRoomEntity); // 변경된 RoomEntity 저장 (이미 존재하는 엔티티를 업데이트)
    }

    public void updateRoomCodes(List<RoomCodeDTO> roomCodeDTOs,Long roomNo,RoomEntity existingRoomEntity) {
        //수정할 호텔의 방번호 조회

        List<RoomCodeEntity> roomCodeEntityList = roomCodeRepository.findByRoomEntityRoomNo(roomNo);
        roomCodeRepository.deleteAllInBatch(roomCodeEntityList);



        // DTO 리스트를 Entity 리스트로 변환
        List<RoomCodeEntity> newRoomCodeEntities = roomCodeDTOs.stream()
                .map(dto -> modelMapper.map(dto, RoomCodeEntity.class))
                .collect(Collectors.toList());

        // 각 Entity에 roomNo 설정 (필요한 경우)
        newRoomCodeEntities.forEach(entity -> entity.getRoomEntity().setRoomNo(roomNo));

        // 변환된 Entity 리스트 저장
        roomCodeRepository.saveAll(newRoomCodeEntities);

        String fullCodes = newRoomCodeEntities.stream()
                .sorted(Comparator.comparingInt(RoomCodeEntity::getOrderIdx))
                .map(RoomCodeEntity::getFullCode)
                .collect(Collectors.joining());

        // existingRoomEntity의 fullCodes 업데이트
        existingRoomEntity.setFullCodes(fullCodes);
        roomRepository.save(existingRoomEntity); // 변경된 RoomEntity 저장 (이미 존재하는 엔티티를 업데이트)
    }
}