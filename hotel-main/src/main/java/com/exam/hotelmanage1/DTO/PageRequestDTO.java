package com.exam.hotelmanage1.DTO;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Log4j2
public class PageRequestDTO {

    @Builder.Default //빌더 인터페이스의 Default 메서드를 실행
    //메서드 내용은 @Builder 를 통한 객체를 생성할 때 page의 값을 1로 유지하게함
    private int page = 1;

    @Builder.Default
    private int size = 10;

    public Pageable getPageable(String...props){

        return PageRequest
                .of(this.page -1, this.size, Sort.by(props)
                        .descending());
    }

    private String link;
    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
    }

//    public String addLink(List<AdminDTO> dtoList) throws UnsupportedEncodingException {
//        StringBuilder builder = new StringBuilder();
//        builder.append("page=").append(this.page);
//
//        if (dtoList != null && !dtoList.isEmpty()) {
//            String params = dtoList.stream().flatMap(dto -> {
//                try {
//                    return Stream.of(
//                            dto.getRoleType() != null ? "roleType=" + dto.getRoleType().name() : null,
//                            dto.getAdminNo() != null ? "adminNo=" + dto.getAdminNo() : null,
//                            dto.getUserid() != null && !dto.getUserid().isEmpty() ? "userid=" + encodeValue(dto.getUserid()) : null,
//                            dto.getName() != null && !dto.getName().isEmpty() ? "name=" + encodeValue(dto.getName()) : null,
//                            dto.getEmail() != null && !dto.getEmail().isEmpty() ? "email=" + encodeValue(dto.getEmail()) : null,
//                            dto.getPhone() != null && !dto.getPhone().isEmpty() ? "phone=" + encodeValue(dto.getPhone()) : null
//                    );
//                } catch (UnsupportedEncodingException e) {
//                    throw new RuntimeException(e);
//                }
//            }).filter(Objects::nonNull).collect(Collectors.joining("&"));
//
//            if (!params.isEmpty()) {
//                builder.append("&").append(params);
//            }
//        }
//
//        return builder.toString();
//    }
//public String getLink(){
//    //String 타입을 반환하는 getLink 메서드 생성. 외부에서 파라미터를 받지않음
//    if(link == null){// 링크 객체가 null 일 경우(link 변수에 값이 할당되지 않았을 경우)
//        StringBuilder builder = new StringBuilder();
//        //StringBuilder 타입의 builder 객체 생성.
//        //StringBuilder는 문자열을 효율적으로 더하거나 변경할때 사용
//        builder.append("page=" + this.page);
//        //append 란? StringBuilder 의 문자열 추가 메서드
//        //Page= 뒤에 현재 페이지를 추가해 문자열로 담음
//        builder.append("&size=" + this.size);
//        //&size= 뒤에 사이즈를 추가해 문자열로 담음
//        if (type != null && type.length() > 0){
//            //만약 type이 null값이 아니면서 0글자 이상인경우
//            builder.append("&type=" + type);
//            //&type= 뒤에 현재 타입을 추가해 문자열로 담음
//        }
//        if (keyword != null){
//            try{
//                //
//                builder.append("&keyword=" + URLEncoder.encode(keyword,"UTF-8"));
//                //try 블록 안에서는 URLEncoder.encode(keyword, "UTF-8")
//                // 메소드를 호출하여 keyword 변수의 값을
//                // "UTF-8" 문자 인코딩을 사용하여 URL 인코딩
//            } catch (UnsupportedEncodingException e){
//                //지정된 문자 인코딩("UTF-8")이 지원되지 않을 경우 발생하는
//                // UnsupportedEncodingException 예외를 처리
//
//            }
//        }
//        link = builder.toString();
//    }
//    return link;
//    //사용자의 입력값을 URL에 추가하기 위한 과정
//}


}
