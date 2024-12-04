package com.fullstack.springboot.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 글번호, 작성자, 내용, 작성자이메일, 작성자 이름, 작성일, 수정일, 댓글 수를 필드로 선언함.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {

	private Long bno;
	private String title;
	private String content;
	private String writerEmail;
	private String writerName;
	private LocalDateTime regdate;
	private LocalDateTime moddate;
	private int replyCount;
	
}
