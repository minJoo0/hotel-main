package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Long>, JpaSpecificationExecutor<StoreEntity> {

    // @Query("SELECT u FROM HotelEntity u WHERE u.name LIKE %:name%")

    //호텔의 매장 갯수
    @Query("SELECT COUNT(s) FROM StoreEntity s WHERE s.hotelEntity.hotelNo = :hotelNo")
    Long countByHotelEntityHotelNo(@Param("hotelNo") Long hotelNo);

    @Query("SELECT s FROM StoreEntity s WHERE s.hotelEntity.adminEntity.adminNo = :adminNo")
    List<StoreEntity> findStoresByAdminNo(@Param("adminNo") Long adminNo);

    @Query("SELECT s FROM StoreEntity s WHERE s.hotelEntity.hotelNo = :hotelNo")
    List<StoreEntity> findStoresByHotelNo(@Param("hotelNo") Long hotelNo);

    @Query("SELECT s FROM StoreEntity s JOIN FETCH s.hotelEntity WHERE s.storeNo = :storeNo")
    Optional<StoreEntity> findByStoreNoWithHotel(Long storeNo);

    Optional<StoreEntity> findByUserid(String userid);
    @Query("SELECT s.storeNo FROM StoreEntity s WHERE s.userid = :userid")
    Long findStoreNoByUserid(String userid);



}