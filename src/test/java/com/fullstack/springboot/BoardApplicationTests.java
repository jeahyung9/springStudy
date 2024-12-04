package com.fullstack.springboot;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.fullstack.springboot.dto.BoardDTO;
import com.fullstack.springboot.dto.PageRequestDTO;
import com.fullstack.springboot.dto.PageResultDTO;
import com.fullstack.springboot.dto.ReplyDTO;
import com.fullstack.springboot.entity.Board;
import com.fullstack.springboot.entity.Member;
import com.fullstack.springboot.entity.Reply;
import com.fullstack.springboot.repository.BoardRepository;
import com.fullstack.springboot.repository.MemberRepository;
import com.fullstack.springboot.repository.ReplyRepository;
import com.fullstack.springboot.service.BoardService;
import com.fullstack.springboot.service.BoardServiceImpl;
import com.fullstack.springboot.service.ReplyService;
import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;

import jakarta.transaction.Transactional;

@SpringBootTest
class BoardApplicationTests {

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private ReplyRepository replyRepository;
	@Autowired
	private BoardService boardService;
	@Autowired
	private ReplyService replyService;
	
	//@Test
	void inserMembers() {
		IntStream.rangeClosed(1, 100).forEach(i -> {
			Member member = Member.builder()
					.email("user" + i + "@aaa.com")
					.password("1111")
					.name("user" + i)
					.build();
			
			memberRepository.save(member);
		});
	}
	
	//@Test
	void insertBoard() {
		IntStream.rangeClosed(1, 100).forEach(i -> {
			Member member = Member.builder()
					.email("user" + i + "@aaa.com").build();
			
			Board board = Board.builder()
					.title("Title..." + i)
					.content("Content...." + i)
					.member(member)
					.build();
			
			boardRepository.save(board);
		});
	}

	//@Test
	void insertReply() {
		//replyer는 반드시 member email 중 하나여야 하고, 랜덤하게 생성해서 하나의 게세글에 하나 이상의 댓글을 구성하도록 합니다.
		IntStream.rangeClosed(1, 100).forEach(i -> {
			long bno = (long)(Math.random() * 100) + 1;
			Board board = Board.builder().bno(bno).build();
			
			Reply reply = Reply.builder()
					.text("댓글...." + i)
					.board(board)
					.replyer("guest")
					.build();
			
			replyRepository.save(reply);
		});
	}
	
	//@Transactional
	//@Test
	void testRead1() {
		Optional<Board> optional = boardRepository.findById(17L);
		
		Board board = optional.get();
		
		System.out.println(board);
		System.out.println(board.getMember());
	}
	
	//@Test
	void testReply1() {
		Optional<Reply> optional = replyRepository.findById(17L);
		
		Reply reply = optional.get();
		
		System.out.println(reply);
		System.out.println(reply.getBoard());
	}
	
	//Query 어노예시
	//@Test
	void testReadWithWriter() {
		Object res = boardRepository.getBoardWithMember(10L);
		
		System.out.println(boardRepository.getUseFunc());
		
		Object[] arr = (Object[]) res;
		
		System.out.println(Arrays.toString(arr));
		System.out.println(boardRepository.getBoardWithReply());
		Object[] ress = boardRepository.getGet();
	}
	
	//@Test
	void testReadWithReply() {
		Pageable pageable = PageRequest.of(1, 10, Sort.by("bno").descending());
		Page<Object[]> res = boardRepository.getBoardWithReplyCount(pageable);
		
		res.get().forEach(row -> {
			Object[] arr = (Object[])row;
			for(Object obj : arr) {
				System.out.println(obj.getClass());
			}
		});
	}
	
	//@Test
	void insertBoardTest() {
		BoardDTO boardDTO = BoardDTO.builder()
							.content("테스트용 내용")
							.title("테스트용 타이틀")
							.writerEmail("user33@aaa.com")
							.build();
		System.out.println(boardService.register(boardDTO));
	}
	
	/*
	 * 게시글의 리스트에 출력되는 DTO는 쿼리에서 Object[]로 구성됨.
	 * 따라서 이를 get해서 DTO로 변환을 해야 하는데 위 내용은 Board와 Memeber 그리고 댓글 수 Long으로 구성됨.
	 * 이를 파라미터로 전달해서 BoardDTO로 구성할 예정임.
	 * 이를 위해서 entitytoDTO를 오버로딩할 예정임.
	 */
	//@Test //리스트 페이지별 요청에 따른 글목록 리턴 테스트
	void testList() {
		PageRequestDTO pageRequestDTO = new PageRequestDTO();
//		PageResultDTO<BoardDTO, Object[]> result = boardService.getList(pageRequestDTO);
//		
//		for(BoardDTO dto : result.getDtoList()) {
//			System.out.println(dto);
//		}
		
		BoardDTO dto = boardService.getRead(10L);
		
		System.out.println(dto);
	}
	
	//게시글 및 댓글까지 삭제하는 테스트
	//@Test
	void delBoard() {
		Long bno = 3L;
		
		boardService.removeWithRelpies(bno); 
	}
	
	//@Test
	void upBoard() {
		
		BoardDTO dto = BoardDTO.builder().bno(101L).title("바뀐 제목").content("바뀐 내용").build();
		
		boardService.modify(dto);
	}
	
	//쿼리 DSL 초기화 테스트
	@Transactional
	void testSearch1() {
		boardRepository.search1();
	}
	
	void searchKeyword() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending().and(Sort.by("title").ascending()));
		
		Page<Object[]> result = boardRepository.searchPage("t", "1", pageable);
	}
	
	@Test
	//특정 글번호에 해당하는 게시글 list 테스트
	void testListReply() {
//		List<Reply> list = replyRepository.getRepliesByBoardOrderByRno(Board.builder().bno(35L).build());
//		
//		list.forEach(i -> {
//			System.out.println(i);
//		});
		
		ReplyDTO replyDTO = ReplyDTO.builder().bno(103L).text("여윽시 찬우 실망시키지 않아").replyer("재흥").build();
		replyService.register(replyDTO);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
