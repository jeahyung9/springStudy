package com.fullstack.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fullstack.springboot.entity.Board;
import com.fullstack.springboot.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

	
	//삭제 가능 쿼리
	@Modifying//select가 아닌 DML은 이 어노선언해야함.
	@Query("delete from Reply r where r.board.bno = :bno")
	void deleteByBno(@Param("bno") Long bno);
	
	//게시물의 댓글 리턴하도록 선언
	List<Reply> getRepliesByBoardOrderByRno(Board board); //쿼리 메서드 사용.
}
