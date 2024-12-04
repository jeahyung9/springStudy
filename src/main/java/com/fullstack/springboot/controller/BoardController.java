package com.fullstack.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fullstack.springboot.dto.BoardDTO;
import com.fullstack.springboot.dto.PageRequestDTO;
import com.fullstack.springboot.service.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
public class BoardController {

	private final BoardService boardService;
	
	//게시물 삭제 처리
	@GetMapping("/delete")
	public String delete(@ModelAttribute("bno") Long bno, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, RedirectAttributes redirectAttributes) {
		
		boardService.removeWithRelpies(bno);
		
		redirectAttributes.addAttribute("page", requestDTO.getPage());
		redirectAttributes.addFlashAttribute("msg", bno + "번 글이 삭제되었습니다.");
		
		return "redirect:/board/list";
	}
	
	//게시물 수정 처리
	@PostMapping("/modify")
	public String modify(BoardDTO boardDTO, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, RedirectAttributes redirectAttributes) {
		
		boardService.modify(boardDTO);
		
		redirectAttributes.addAttribute("page", requestDTO.getPage());
		redirectAttributes.addAttribute("type", requestDTO.getType());
		redirectAttributes.addAttribute("keyword", requestDTO.getKeyword());
		
		redirectAttributes.addAttribute("bno", boardDTO.getBno());
		return "redirect:/board/list";
	}
	
	//게시물 조회 및 수정 연결
	@GetMapping({"/read", "/modify"})
	public void read(@ModelAttribute("requestDTO") PageRequestDTO requestDTO, @RequestParam("bno") Long bno, Model model) {
		log.error("상세 페이지 요청됨 " + bno);
		
		BoardDTO boardDTO = boardService.getRead(bno);
		model.addAttribute("dto", boardDTO);
	}
	
	//등록폼연결
	@GetMapping("/register")
	public void register() {
		log.error("등록폼 요청됨...");
	}
	
	@PostMapping("/registerPost")
	public String registerPost(BoardDTO boardDTO, RedirectAttributes redirectAttributes) {
		log.error("등록 처리 수행..");
		Long bno = boardService.register(boardDTO);
		log.error("신규 등록 글번호 : " + bno);
		
		redirectAttributes.addFlashAttribute("msg", "글번호 " + bno + " 가 잘 등록됨");
		return "redirect:/board/list";
	}
	
	//게시글 목록 요청 및 UI에 모델 연결
	@GetMapping("/list")
	public void list(PageRequestDTO pageRequestDTO, Model model) {
		log.error("리스트 요청됨..." + pageRequestDTO);
		
		model.addAttribute("result", boardService.getList(pageRequestDTO));
	}
	
	
}
