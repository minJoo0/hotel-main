package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.DTO.CartDTO;
import com.exam.hotelmanage1.DTO.HotelDTO;
import com.exam.hotelmanage1.DTO.MenuOptionDTO;
import com.exam.hotelmanage1.DTO.RoomDTO;
import com.exam.hotelmanage1.Service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Log4j2
@Tag(name = "Rest 객실", description = "객실 Rest API")
@RestController
public class RestRoomController {

    private final RoomService roomService;

    @Operation(summary = "호텔 번호에 따른 객실 목록을 출력하는 API",
            description = "호텔 번호에 따른 객실 목록을 출력하는 API")
    @GetMapping("/api/room")
    public List<RoomDTO> findRoomsByHotelNo(Long hotelNo, LocalDate date){
        log.info(hotelNo);
        List<RoomDTO> roomDTOList = roomService.findRoomsByHotelNo(hotelNo,date);
        log.info(roomDTOList);

        return roomDTOList;
    }

    @Operation(summary = "객실 상태에서 객실 버튼을 클릭 시 상세 정보를 출력하는 API",
            description = "객실 상태에서 객실 버튼을 클릭 시 상세 정보를 출력하는 API")
    @GetMapping("/api/status")
    public RoomDTO getRoomInfoByRoomNo(Long roomNo,LocalDate date){
        log.info(roomNo);
        RoomDTO roomDTO = roomService.findRoomByRoomNo(roomNo,date);

        return roomDTO;
    }
}
