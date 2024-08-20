package com.exam.hotelmanage1.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseDTO<E> {
    //화면에 DTO의 목록과 시작페이지, 끝페이지등에 대한 처리를 담당

    private int page;
    private int size;
    private int total;
    private int last;

    //시작페이지 번호
    private int start;
    //끝페이지 번호
    private int end;

    //이전페이지 존재 여부
    private boolean prev;
    //다음페이지 존재 여부
    private boolean next;

    private List<E> dtoList;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total){
        if (total <= 0){
            return;
        }
        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();

        this.total = total;
        this.dtoList = dtoList;

        this.end = (int)(Math.ceil(this.page / 10.0)) * 10;//화면에서의 마지막 번호
        this.start = this.end - 9; //화면에서의 시작 번호
        this.last = (int)(Math.ceil((total/(double)size))); //데이터의 개수를 계산한 마지막 페이지 번호
        this.end = Math.min(end, last);
        this.prev = this.start > 1;
        this.next = total > this.end * this.size;
    }
}
