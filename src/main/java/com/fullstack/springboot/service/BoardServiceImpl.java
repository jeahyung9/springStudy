package com.fullstack.springboot.service;

import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fullstack.springboot.dto.BoardDTO;
import com.fullstack.springboot.dto.PageRequestDTO;
import com.fullstack.springboot.dto.PageResultDTO;
import com.fullstack.springboot.entity.Board;
import com.fullstack.springboot.entity.Member;
import com.fullstack.springboot.repository.BoardRepository;
import com.fullstack.springboot.repository.ReplyRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

	private final BoardRepository boardRepository;
	private final ReplyRepository replyRepository;
	
	@Override
	public Long register(BoardDTO dto) {
		System.out.println("게시글 등록 메서드");
		Board board = dtotoEntity(dto);
		
		boardRepository.save(board);
		return board.getBno();
	}

	@Override
	public PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO) {
		
		System.out.println("게시판 목록 요청(page 및 검색 정보) : " + pageRequestDTO);
		
		//PageResultDTO를 생성할 때, Entity와 변환 맵퍼 함수를 같이 줘야 하는데,
		//이 변화 매퍼는 service에 정의한 entityDTO()임
		//이때, 이 매퍼에는 Query로 조회된 Object[] 내의 값을 분해해서 Board, Member, 댓글 수를 파라미터로 전달해야하고
		//이 값을 받아서 하나의 DTO로 만드는 작업을 하도록 정의 되어있음. 따라서 위 파라미터 정보를 넘겨야함.
		
		Function<Object[], BoardDTO> fn = (en -> entitytoDTO((Board)en[0], (Member)en[1], (Long)en[2]));
		
		//Page<Object[]> result = boardRepository.getBoardWithReplyCount(pageRequestDTO.getPageable(Sort.by("bno").descending()));
		
		//검색기능을 추가한 Page 객체 얻기
		Page<Object[]> result = boardRepository.searchPage(pageRequestDTO.getType(), pageRequestDTO.getKeyword(), pageRequestDTO.getPageable(Sort.by("bno").descending()));
		
		return new PageResultDTO<>(result, fn);
	}
	
	@Override
	public BoardDTO getRead(Long bno) {
		Object obj = boardRepository.getBoardByBno(bno);
		Object[] arr = (Object[])obj;
		
		//Entity를 DTO로 변환.
		return entitytoDTO((Board)arr[0], (Member)arr[1], (Long)arr[2]);
	}
	
	//댓글과 함께 게시글 삭제 처리 구현
	@Transactional
	@Override
	public void removeWithRelpies(Long bno) {
		replyRepository.deleteByBno(bno);
		boardRepository.deleteById(bno);
	}
	
	@Transactional
	@Override
	public void modify(BoardDTO boardDTO) {
		//아래 getRef...()는 자연 로딩 처리메서드임. 따라서 이 메서드에는 Connection이 유지되야 하므로 Transa...선언
		Board board = boardRepository.getReferenceById(boardDTO.getBno());
		
		if(board != null) {
			board.changeContent(boardDTO.getContent());
			board.changeTitle(boardDTO.getTitle());
			
			boardRepository.save(board);
		}
	}

}
