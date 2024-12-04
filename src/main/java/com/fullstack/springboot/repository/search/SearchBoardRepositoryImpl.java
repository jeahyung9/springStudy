package com.fullstack.springboot.repository.search;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.fullstack.springboot.entity.Board;
import com.fullstack.springboot.entity.QBoard;
import com.fullstack.springboot.entity.QMember;
import com.fullstack.springboot.entity.QReply;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SearchBoardRepositoryImpl extends QuerydslRepositorySupport implements SearchBoardRepository {

	public SearchBoardRepositoryImpl() {
		super(Board.class); //반드시 사용될 도메인 클래스를 넘겨야함.
	}
	
	@Override
	public Board search1() {
		log.error("search1 호출됨 ----------------------------------------");
		
		//QueryDSL을 이용한 JPQL 사용법
		
		/*
		 * 1. Q도메인 클래스를 get
		 * 2. JPQLQuery 객체를 get.. 이건 from(Q도메인 객체)을 통해서 get 가능함.
		 * 3. 얻어낸 JPQLQuery를 대상으로 select등을 where, join 등을 메서드로 처리함.
		 * 4. 결과를 get 할때는 fetch()를 이용함. 오버로딩되어 있어서 다양한 컬렉션 타입으로 리턴됨.(기본적으로는 Object[])
		 */
		
		QBoard board = QBoard.board;
		QMember member = QMember.member;
		QReply reply = QReply.reply;
		JPQLQuery<Board> jpqlQuery = from(board);
		
		//jpqlQuery.select(board.title).where(board.bno.eq(10L));
		
		log.info("---------------------- JSQL 쿼리 수행 결과 ------------------------");
		log.error(jpqlQuery);
		
		jpqlQuery.leftJoin(reply).on(reply.board.eq(board));
		jpqlQuery.leftJoin(member).on(board.member.eq(member));
		
		JPQLQuery<Tuple> tuple = jpqlQuery.select(board, member.email, reply.count()).groupBy(board);
		
		System.out.println(tuple);
		
		
		List<Tuple> result = tuple.fetch();
		
		result.forEach(i -> {
			System.out.println(i);
		});
		
//		jpqlQuery.leftJoin(reply).on(reply.board.eq(board));
//		log.error(jpqlQuery);
		return null;
		
		/*
		 * Tuple 객체. 이 객체는 보통 조인을 하게 되면 내부적으로 리턴되는 데이터의 합 객체임.
		 * 튜플객체를 리턴받아서 처리할 수 있는데, 그 값의 형태는 Object배열 형태로 데이터가 저장되어짐.
		 * 이렇게 된 애들은 각 배열의 값을 분리해서 Entity 형태로 매핑해서 사용함.
		 */
	}
	
	@Override
	public Page<Object[]> searchPage(String type, String keyword, Pageable pageable) {
		QBoard board = QBoard.board;
		QMember member = QMember.member;
		QReply reply = QReply.reply;
		
		JPQLQuery<Board> jpqlQuery = from(board);
		jpqlQuery.leftJoin(member).on(board.member.eq(member));
		jpqlQuery.leftJoin(reply).on(reply.board.eq(board));
		
		JPQLQuery<Tuple> tuple = jpqlQuery.select(board, member, reply.count());
		
		BooleanBuilder booleanBuilder = new BooleanBuilder();
		BooleanExpression booleanExpression = board.bno.gt(0); //기본적으로 모든 Row를 다 조회함.
		
		booleanBuilder.and(booleanExpression);
		
		if(type != null) {
			//Tuple 분해.
			String[] typeArr = type.split("");
			
			//검색 조건 지정
			BooleanBuilder booleanBuilder2 = new BooleanBuilder();
			
			for(String t : typeArr) {
				switch(t) {
				case "t":
					booleanBuilder2.or(board.title.contains(keyword));
					break;
				case "w":
					booleanBuilder2.or(member.email.contains(keyword));
					break;
				case "c":
					booleanBuilder2.or(board.content.contains(keyword));
					break;
				}
			}
			
			booleanBuilder.and(booleanBuilder2);
		}
		
		tuple.where(booleanBuilder);
		tuple.groupBy(board);
		
		/*
		 * 기본적으로 JPQLQuery는 Sort를 지원하지 않음. 때문에 이걸 사용하기 위해서는 applyPagenation이라는 메서드를 통해서
		 * Page 객체를 따로 얻어내야함. 위 메서드는 this.getQuerydsl()을 이용하면 리턴됨
		 */
		this.getQuerydsl().applyPagination(pageable, tuple);
		
		List<Tuple> result = tuple.fetch();
		
		log.error(result);
		
		long count = tuple.fetchCount();
		
		//나머지는 PageImpl이라는 객체를 얻어내서 페이지, 정렬 처리를 수행함
		
		return new PageImpl<Object[]>(result.stream().map(Tuple::toArray).collect(Collectors.toList()), pageable, count);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
