package com.fullstack.springboot.service;

import org.springframework.stereotype.Service;

import com.fullstack.springboot.dto.BoardDTO;
import com.fullstack.springboot.dto.PageRequestDTO;
import com.fullstack.springboot.dto.PageResultDTO;
import com.fullstack.springboot.entity.Board;
import com.fullstack.springboot.entity.Member;

@Service
public interface BoardService {

	Long register(BoardDTO dto);
	
	//list 페이지 뿌려질 명세 정의. PageResult 객체를 이용함.
	PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO);
	
	//글 상세에서 필요한 기능 선언
	BoardDTO getRead(Long bno);
	
	void modify(BoardDTO boardDTO);
	
	//글삭제 기능 선언. 댓글과 같이 삭제 시킨다.
	void removeWithRelpies(Long bno);
	
	//아래는 entity --> dto
	default Board dtotoEntity(BoardDTO dto) {
		Member member = Member.builder()
				.email(dto.getWriterEmail()).build();
		
		Board entity = Board.builder()
				.bno(dto.getBno())
				.title(dto.getTitle())
				.content(dto.getContent())
				.member(member)
				.build();
		
		return entity;
	}
	
	//Qquery를 이용해서 리턴된 Object[]내의 Data를 dto로 변환하는 맵퍼 정의
	
	default BoardDTO entitytoDTO(Board board, Member member, Long replyCount) {
		BoardDTO boardDTO = BoardDTO.builder()
				.bno(board.getBno())
				.title(board.getTitle())
				.content(board.getContent())
				.regdate(board.getRegDate())
				.moddate(board.getModDate())
				.writerEmail(member.getEmail())
				.writerName(member.getName())
				.replyCount(replyCount.intValue())
				.build();
		return boardDTO;
	}
	
	
}
