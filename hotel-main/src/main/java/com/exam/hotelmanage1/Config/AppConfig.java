package com.exam.hotelmanage1.Config;

import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Entity.*;
import com.exam.hotelmanage1.DTO.RoomCodeDTO;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.hibernate.collection.spi.PersistentBag;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Log4j2
public class AppConfig {
    @Bean
    public ModelMapper modelMapper (){
        ModelMapper modelMapper = new ModelMapper();

        // PersistentBag를 List로 변환하는 커스텀 컨버터 정의
        Converter<Object, List<?>> toListConverter = new Converter<Object, List<?>>() {
            @Override
            public List<?> convert(MappingContext<Object, List<?>> context) {
                Object source = context.getSource();
                // 소스가 PersistentBag이고, 초기화되지 않았다면 초기화
                if (!Hibernate.isInitialized(source)) {
                    Hibernate.initialize(source);
                }
                // 이제 안전하게 스트림으로 변환 가능
                return ((List<?>) source).stream().collect(Collectors.toList());
            }
        };

        // ModelMapper에 커스텀 변환기 등록
        modelMapper.addConverter(toListConverter);


        modelMapper.typeMap(HotelDTO.class, HotelEntity.class).addMappings(mapper -> {
            mapper.map(HotelDTO::getAdminNo,
                    (dest, value) -> dest.getAdminEntity().setAdminNo((Long) value));
        });
        modelMapper.typeMap(CodeGroupDTO.class, CodeGroupEntity.class).addMappings(mapper -> {
            mapper.map(CodeGroupDTO::getAdminNo,
                    (dest, value) -> dest.getAdminEntity().setAdminNo((Long) value));
        });

        modelMapper.typeMap(CodeDTO.class, CodeEntity.class).addMappings(mapper -> {
            mapper.map(CodeDTO::getCodeGroupNo,
                    (dest, value) -> dest.getCodeGroupEntity().setCodeGroupNo((Long) value));
        });
        // StoreDTO -> StoreEntity 매핑 설정
        modelMapper.typeMap(StoreDTO.class, StoreEntity.class).addMappings(mapper -> {
            mapper.map(StoreDTO::getHotelNo,
                    (dest, value) -> dest.getHotelEntity().setHotelNo((Long) value));
            mapper.map(StoreDTO::getAdminNo,
                    (dest, value) -> dest.getHotelEntity().getAdminEntity().setAdminNo((Long) value));
        });
        //RoomDTO -> RoomEntity 매핑 설정
        modelMapper.typeMap(RoomDTO.class, RoomEntity.class).addMappings(mapper -> {
            mapper.map(RoomDTO::getHotelNo,
                    (dest, value) -> dest.getHotelEntity().setHotelNo((Long) value));
        });
        //RoomEntity -> RoomDTO 매핑 설정
        modelMapper.typeMap(RoomEntity.class, RoomDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getHotelEntity().getSido(), RoomDTO::setSido);
            mapper.map(src -> src.getHotelEntity().getHotelNo(), RoomDTO::setHotelNo);
        });
        //RoomCodeDTO ->RomCodeEntity 매핑 설정
        modelMapper.typeMap(RoomCodeDTO.class, RoomCodeEntity.class).addMappings(mapper -> {
            mapper.map(RoomCodeDTO::getRoomNo,
                    (dest, value) -> dest.getRoomEntity().setRoomNo((Long) value));
            mapper.map(RoomCodeDTO::getCodeGroupNo,
                    (dest, value) -> dest.getCodeGroupEntity().setCodeGroupNo((Long) value));
            mapper.map(RoomCodeDTO::getFullCode,
                    (dest, value) -> dest.setFullCode((String) value));
            mapper.map(RoomCodeDTO::getFullCodes,
                    (dest, value) -> dest.getRoomEntity().setFullCodes((String) value));
        });

        // MenuDTO -> MenuEntity 매핑 설정
        modelMapper.typeMap(MenuDTO.class, MenuEntity.class).addMappings(mapper -> {
            mapper.map(MenuDTO::getStoreNo,
                    (dest, value) -> dest.getCategory1().getStoreEntity().setStoreNo((Long) value));
            mapper.map(MenuDTO::getHotelNo,
                    (dest, value) -> dest.getCategory1().getStoreEntity().getHotelEntity().setHotelNo((Long) value));
            mapper.map(MenuDTO::getAdminNo,
                    (dest, value) -> dest.getCategory1().getStoreEntity().getHotelEntity().getAdminEntity().setAdminNo((Long) value));
        });
        // MenuEntity -> MenuDTO 매핑 설정
        modelMapper.typeMap(MenuEntity.class, MenuDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getCategory1().getStoreEntity().getStoreNo(), MenuDTO::setStoreNo);
            mapper.map(src -> src.getCategory1().getStoreEntity().getName(), MenuDTO::setStoreName);
            mapper.map(src -> src.getCategory1().getStoreEntity().getHotelEntity().getHotelNo(), MenuDTO::setHotelNo);
            mapper.map(src -> src.getCategory1().getStoreEntity().getHotelEntity().getName(), MenuDTO::setHotelName);
            mapper.map(src -> src.getCategory1().getStoreEntity().getHotelEntity().getAdminEntity().getAdminNo(), MenuDTO::setAdminNo);
        });

        // CategoryDTO -> CategoryEntity 매핑 설정
        modelMapper.typeMap(CategoryDTO.class, CategoryEntity.class).addMappings(mapper -> {
            mapper.map(CategoryDTO::getParentId,
                    CategoryEntity::setParent);
            mapper.map(CategoryDTO::getStoreNo,
                    (dest, value) -> dest.getStoreEntity().setStoreNo((Long) value));
            mapper.map(CategoryDTO::getHotelNo,
                    (dest, value) -> dest.getStoreEntity().getHotelEntity().setHotelNo((Long) value));
            mapper.map(CategoryDTO::getAdminNo,
                    (dest, value) -> dest.getStoreEntity().getHotelEntity().getAdminEntity().setAdminNo((Long) value));
        });
        modelMapper.typeMap(MenuOptionDTO.class, MenuOptionEntity.class).addMappings(mapper -> {
            mapper.map(MenuOptionDTO::getMenuNo,
                    (dest, value) -> dest.getMenuEntity().setMenuNo((Long) value));
            mapper.map(MenuOptionDTO::getCategory1,
                    (dest, value) -> dest.getMenuEntity().getCategory1().setCategoryNo((Long) value));
            mapper.map(MenuOptionDTO::getCategory2,
                    (dest, value) -> dest.getMenuEntity().getCategory1().setParent((CategoryEntity) value));
            mapper.map(MenuOptionDTO::getStoreNo,
                    (dest, value) -> dest.getMenuEntity().getCategory1().getStoreEntity().setStoreNo((Long) value));
            mapper.map(MenuOptionDTO::getHotelNo,
                    (dest, value) -> dest.getMenuEntity().getCategory1().getStoreEntity().getHotelEntity().setHotelNo((Long) value));
            mapper.map(MenuOptionDTO::getAdminNo,
                    (dest, value) -> dest.getMenuEntity().getCategory1().getStoreEntity().getHotelEntity().getAdminEntity().setAdminNo((Long) value));
        });
        // MenuOptionEntity -> MenuOptionDTO 매핑 설정
        modelMapper.typeMap(MenuOptionEntity.class, MenuOptionDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getMenuEntity().getCategory1().getCategoryNo(), MenuOptionDTO::setCategory1);
            mapper.map(src -> src.getMenuEntity().getCategory1().getStoreEntity().getStoreNo(), MenuOptionDTO::setStoreNo);
            mapper.map(src -> src.getMenuEntity().getCategory1().getStoreEntity().getHotelEntity().getHotelNo(), MenuOptionDTO::setHotelNo);
            mapper.map(src -> src.getMenuEntity().getCategory1().getStoreEntity().getHotelEntity().getName(), MenuOptionDTO::setHotelName);
            mapper.map(src -> src.getMenuEntity().getCategory1().getStoreEntity().getHotelEntity().getAdminEntity().getAdminNo(), MenuOptionDTO::setAdminNo);
        });
        // NoticeDTO -> NoticeEntity 매핑 설정
        modelMapper.typeMap(NoticeDTO.class, NoticeEntity.class).addMappings(mapper -> {
            mapper.map(NoticeDTO::getAdminNo,
                    (dest, value) -> dest.getAdminEntity().setAdminNo((Long) value));
            mapper.map(NoticeDTO::getHotelNo,
                    (dest, value) -> dest.getHotelEntity().setHotelNo((Long) value));

        });

        // FaqDTO -> FaqEntity 매핑 설정
        modelMapper.typeMap(FaqDTO.class, FaqEntity.class).addMappings(mapper -> {
            mapper.map(FaqDTO::getAdminNo,
                    (dest, value) -> dest.getAdminEntity().setAdminNo((Long) value));
        });
        // CartDTO -> CartEntity 매핑 설정
        modelMapper.typeMap(CartDTO.class, CartEntity.class).addMappings(mapper -> {
            mapper.map(CartDTO::getMenuOptionNo,
                    (dest, value) -> dest.getMenuOptionEntity().setMenuOptionNo((Long) value));
            mapper.map(CartDTO::getStoreNo,
                    (dest, value) -> dest.getMenuEntity().getCategory1().getStoreEntity().setStoreNo((Long) value));
            mapper.map(CartDTO::getMenuNo,
                    (dest, value) -> dest.getMenuEntity().setMenuNo((Long) value));
            mapper.map(CartDTO::getUserNo,
                    (dest, value) -> dest.getUserEntity().setUserNo((Long) value));
            mapper.map(CartDTO::getHotelNo,
                    (dest, value) -> dest.getMenuEntity().getCategory1().getStoreEntity().getHotelEntity().setHotelNo((Long) value));
        });
        // PaymentDTO -> PaymentEntity 매핑 설정
        modelMapper.typeMap(PaymentDTO.class, PaymentEntity.class).addMappings(mapper -> {
            mapper.map(PaymentDTO::getUserNo,
                    (dest, value) -> dest.getUserEntity().setUserNo((Long) value));
            mapper.map(PaymentDTO::getAdminNo,
                    (dest, value) -> dest.getReserveEntity().getRoomEntity().getHotelEntity().getAdminEntity().setAdminNo((Long) value));
            mapper.map(PaymentDTO::getHotelNo,
                    (dest, value) -> dest.getReserveEntity().getRoomEntity().getHotelEntity().setHotelNo((Long) value));
            mapper.map(PaymentDTO::getRoomNo,
                    (dest, value) -> dest.getReserveEntity().getRoomEntity().setRoomNo((Long) value));
            mapper.map(PaymentDTO::getReserveNo,
                    (dest, value) -> dest.getReserveEntity().setReserveNo((Long) value));
        });
        // PaymentEntity -> PaymentDTO 매핑 설정
        modelMapper.typeMap(PaymentEntity.class, PaymentDTO.class).addMappings(mapper -> {
            mapper.map(PaymentEntity::getPaymentItemEntity, PaymentDTO::setPaymentItemDTO);
        });

        // PaymentItemDTO -> PaymentItemEntity 매핑 설정
        modelMapper.typeMap(PaymentItemDTO.class, PaymentItemEntity.class).addMappings(mapper -> {
            mapper.map(PaymentItemDTO::getMenuNo,
                    (dest, value) -> dest.getMenuEntity().setMenuNo((Long) value));
            mapper.map(PaymentItemDTO::getMenuOptionNo,
                    (dest, value) -> dest.getMenuOptionEntity().setMenuOptionNo((Long) value));
            mapper.map(PaymentItemDTO::getPaymentNo,
                    (dest, value) -> dest.getPaymentEntity().setPaymentNo((Long) value));
        });

        // ReserveEntity -> ReserveDTO 매핑 설정
        modelMapper.typeMap(ReserveEntity.class, ReserveDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getRoomEntity().getHotelEntity().getName(), ReserveDTO::setHotelName);
            mapper.map(src -> src.getRoomEntity().getHotelEntity().getHotelNo(), ReserveDTO::setHotelNo);
        });

        // RevenueDTO -> RevenueEntity 매핑 설정
        modelMapper.typeMap(RevenueDTO.class, RevenueEntity.class).addMappings(mapper -> {
            mapper.map(RevenueDTO::getPaymentItemNo,
                    (dest, value) -> dest.getPaymentItemEntity().setPaymentItemNo((Long) value));
        });

        return modelMapper;
    }


}
