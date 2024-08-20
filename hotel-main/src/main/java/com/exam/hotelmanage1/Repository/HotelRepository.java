package com.exam.hotelmanage1.Repository;

import com.exam.hotelmanage1.Entity.HotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<HotelEntity, Long>, JpaSpecificationExecutor<HotelEntity> {


    // @Query("SELECT u FROM HotelEntity u WHERE u.name LIKE %:name%")

    List<HotelEntity> findBySido(String sido);

    @Query("SELECT DISTINCT h.sido FROM HotelEntity h WHERE h.sido IS NOT NULL")
    List<String> findAllSidos();

    @Query("SELECT DISTINCT h.sido FROM HotelEntity h WHERE h.adminEntity.adminNo = :adminNo AND h.sido IS NOT NULL")
    List<String> findSidosByAdminNo(@Param("adminNo") Long adminNo);

    List<HotelEntity> findBySidoAndAdminEntity_AdminNo(String sido, Long adminNo);
    @Query("SELECT u.adminNo FROM AdminEntity u WHERE u.userid = :userid")
    Long findCompanyNoByUsername(@Param("userid") String userid);


    @Query("SELECT s FROM HotelEntity s WHERE s.adminEntity.adminNo = :adminNo")
    List<HotelEntity> findHotelsByAdminNo(@Param("adminNo") Long adminNo);

    @Query("SELECT s FROM HotelEntity s WHERE s.hotelNo = :hotelNo")
    List<HotelEntity> findHotelsByHotelNo(@Param("hotelNo") Long hotelNo);

    @Query("SELECT DISTINCT h FROM HotelEntity h JOIN h.storeEntities s JOIN s.categoryEntity c WHERE h.adminEntity.adminNo = :adminNo AND c.parent IS NOT NULL")
    List<HotelEntity> findHotelsByAdminNoAndCategoryParentIDIsNotNull(@Param("adminNo") Long adminNo);

}
