package com.fullstack.springboot.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDTO {

	private Long rno;
	private String text;
	private String replyer;
	private Long bno; //게시글 참조 필드
	private LocalDateTime regdate, moddate;
	
}
