package com.fullstack.springboot.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fullstack.springboot.entity.Board;
import com.fullstack.springboot.repository.search.SearchBoardRepository;
/*
 * QueryDSL을 사용할때 QueryDSL을 사용하는 인터페이스와 구현체를 생성한 후 도메인 레포지토리(Board, Reply, Member 등..)
 * 에서는 SearchBoardRepository를 명시적으로 상속 받습니다.
 */
public interface BoardRepository extends JpaRepository<Board, Long>, SearchBoardRepository {

	//JPQL : Java Persistent Query Language
	/*
	 * 객체지향 쿼리에대해서 몇가지.
	 * 
	 * 객체지향 쿼리(Hiberanate)는 테이블이 아닌 엔티티를 대상으로 쿼리를 날리는건 같음.
	 * 그러나 쿼리를 구성할 때 형식은 아래와 같이 일반 쿼리와 약간 틀림.
	 * 
	 * 1. Inner Join : Join의 형태는 유사하나 조인을 맺는 방법이 field라는 점이 좀 틀림.
	 * 이 말은 기존의 조인 방식(ANSI)은 From 절에 Join될 테이블과 컬럼을 나열해서 조인을 하는데
	 * 이 방식은 조인을 걸 테이블(Many To One)에서 Many 쪽에서 참조하고 있는 컬럼을 조인 시 사용함.
	 * 이때 Inner는 생략 가능함.
	 * 
	 * ex> Select b.title,b.content,m.name,m.email from Board b Inner Join b.member m where b.bno = :bno
	 * --> 기본 리턴타입은 Object형입니다. 그런데 이 내부에 object[]이 값으로 들어가 있음.
	 * 
	 * 2. Left(Right) Join [ON] : JPA 2.3 이후부터는 연관관계가 없는 테이블에서도 조인을 걸 수 있음.
	 * 예를 들면 Member와 Reply는 객체간의 연관관계가 없음.
	 * 이럴때 서로 연관이 없는 테이블에도 join을 수행 할 수 있는데, 이때 반드시 On이라는 키워드를 사용 해야함.
	 * On은 서로 참조가 없는(클래스 상태에서) 테이블간의 공통 컬럼을 Join 하는데 사용함.
	 */
	//특정 글번호에 해당하는 글정보와 사용자 정보를 가져오는 조인쿼리 작성.
	@Query("select b.bno, b.title, m.email, m.name from Board b join b.member m where b.bno= :bno")
	Object getBoardWithMember(@Param("bno") Long bno);//Object[] 로 리턴됨
	
	//이번엔 집계함수를 수행하는 쿼리
	@Query("select count(u), sum(u.bno) from Board u")
	Object getUseFunc();
	
	//Board를 기준으로 On을 사용해서 Reply와 Join을 걸어볼게요.
	//On을 사용할때는 Join이 아닌 Outer join이기떄문에 반드시 조인 대상 엔티티 명을 명시해야 합니다.
	@Query("select b,r from Board b Left Join Reply r On r.board = b")
	Object getBoardWithReply();
	
	//group by 절을 사용해서 조건절에 추가를 해 볼게요.
	//이 쿼리는 특정 글에대한 모든 정보를 가져오도록 합니다.
	//이 중에는 특정 글에대한 댓글 수 를 count로 가져올건데 이때 사용되는 Query의 속성 중에 countquery를 사용할게요.
	@Query("select b.bno, count(r.board) from Board b left join Reply r on b = r.board group by b")
	Object[] getGet();
	
	//선생님 코드
	@Query(value="select b, m , count(r) from Board b left join b.member m left join Reply r On r.board = b group by b"
			,countQuery="select count(b) from Board b")
	Page<Object[]> getBoardWithReplyCount(Pageable pageable);
	
	//이번엔 글 상세에서 필요한 특정 글번호에 해당하는 모든 정보를 가져오는 쿼리를 작성합니다.
	@Query("select b,m,count(r) from Board b left join b.member m left join Reply r On r.board = b where b.bno = :bno")
	Object getBoardByBno(@Param("bno") Long bno);

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
