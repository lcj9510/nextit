package com.study.free.web;

import java.util.List;

import javax.inject.Inject;
import javax.validation.groups.Default;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.study.code.service.CommCodeServiceImpl;
import com.study.code.service.ICommCodeService;
import com.study.code.vo.CodeVO;
import com.study.common.valid.Modify;
import com.study.common.vo.ResultMessageVO;
import com.study.exception.BizNotEffectedException;
import com.study.exception.BizNotFoundException;
import com.study.exception.BizPasswordNotMatchedException;
import com.study.free.service.FreeBoardServiceImpl;
import com.study.free.service.IFreeBoardService;
import com.study.free.vo.FreeBoardSearchVO;
import com.study.free.vo.FreeBoardVO;

@Controller
public class FreeController {
	//데이터 저장이 아닌 단순 기능이면 객체를 1개만 만드는게 좋음 singleton 패턴 말고 spring이 
	//객체 1개 만들면 스프링이 만든 객체만 계속 사용 (빈)
	@Inject
	IFreeBoardService freeBoardService;
	@Autowired //inject랑 똑같다
	ICommCodeService codeService;

	//list, edit, form에서는 categoryList를 model에 담아서 사용
	//그때그때 model에 담지말고 무조건 model categoryList를 담아도 문제없다.
	@ModelAttribute("cateList")
	public List<CodeVO> cateList(){
		return codeService.getCodeListByParent("BC00");
	}
	
	@RequestMapping("/free/freeList.wow")
	public String freeList(Model model, @ModelAttribute("searchVO") FreeBoardSearchVO searchVO) {
		//파라미터들이 searchVO의 필드이면 알아서 다 세팅 + model.addAttribute("searchVO") 까지 해줌
		List<FreeBoardVO > freeBoardList = freeBoardService.getBoardList(searchVO);
		model.addAttribute("freeBoardList", freeBoardList);
		return "free/freeList";
	}
	
	@RequestMapping("/free/freeView.wow")
	public String freeView(Model model, @RequestParam(required = true)int boNo) {
		try {
			FreeBoardVO freeBoard = freeBoardService.getBoard(boNo);
			model.addAttribute("freeBoard", freeBoard);
			freeBoardService.increaseHit(boNo);
		}catch(BizNotFoundException enf){
			//에러가 났을때 freeView에 있는 너무 간단한 화면 말고 message.jsp 로이동하자 
			ResultMessageVO resultMessageVO = new ResultMessageVO();
			resultMessageVO.messageSetting(false, "글 찾기 실패", "글을 찾는데 실패했습니다. 해당 글이 없습니다.", "/free/freeList.wow", "목록으로");
			model.addAttribute("resultMessageVO", resultMessageVO);
			return "common/message";
		}catch(BizNotEffectedException ene){
			ResultMessageVO resultMessageVO = new ResultMessageVO();
			resultMessageVO.messageSetting(false, "조회수 증가 실패", "조회수 증가 실패했습니다.", "/free/freeList.wow", "목록으로");
			model.addAttribute("resultMessageVO", resultMessageVO);
			return "common/message.jsp";
		}
		return "free/freeView";
	}
	
	@RequestMapping(value = "/free/freeEdit.wow", params = {"boNo"})
	public String freeEdit(Model model, int boNo) {
		try{
			FreeBoardVO freeBoard = freeBoardService.getBoard(boNo);
			model.addAttribute("freeBoard", freeBoard);
		}catch(BizNotFoundException enf){
			ResultMessageVO resultMessageVO = new ResultMessageVO();
			resultMessageVO.messageSetting(false, "글 찾기 실패", "글을 찾는데 실패했습니다. 해당 글이 없습니다.", "/free/freeList.wow", "목록으로");
			model.addAttribute("resultMessageVO", resultMessageVO);
			return "common/message";
		}
		return "free/freeEdit";
	}
	
	//검사결과 객체는 검사 객체 바로 뒤에 있어야 합니다.
	@PostMapping("/free/freeModify.wow")
//	@RequestMapping(value = "/free/freeModify.wow", method = RequestMethod.POST)
	public String freeModify(Model model, @ModelAttribute("freeBoard") @Validated(value = {Modify.class, Default.class})FreeBoardVO freeBoard, BindingResult error) {
		if(error.hasErrors()) {
			return "free/freeEdit";
		}
			try{
				freeBoardService.modifyBoard(freeBoard);
				ResultMessageVO resultMessageVO = new ResultMessageVO();
				resultMessageVO.messageSetting(true, "글 수정 성공", "글을 수정했습니다.", "/free/freeList.wow", "목록으로");
				model.addAttribute("resultMessageVO", resultMessageVO);
				return "common/message";
			} catch(BizNotFoundException enf){
				ResultMessageVO resultMessageVO = new ResultMessageVO();
				resultMessageVO.messageSetting(false, "글 찾기 실패", "글을 찾는데 실패했습니다. 해당 글이 없습니다.", "/free/freeList.wow", "목록으로");
				model.addAttribute("resultMessageVO", resultMessageVO);
				return "common/message";
			} catch(BizPasswordNotMatchedException epm){
				ResultMessageVO resultMessageVO = new ResultMessageVO();
				resultMessageVO.messageSetting(false, "글 수정 실패", "글을 수정하는데 실패했습니다. 비밀번호가 달라요.", "/free/freeList.wow", "목록으로");
				model.addAttribute("resultMessageVO", resultMessageVO);
				return "common/message";
			} catch(BizNotEffectedException ene){
				ResultMessageVO resultMessageVO = new ResultMessageVO();
				resultMessageVO.messageSetting(false, "조회수 증가 실패", "조회수 증가 실패했습니다.", "/free/freeList.wow", "목록으로");
				model.addAttribute("resultMessageVO", resultMessageVO);
				return "common/message";
			}
	}
	//delete, form, regist
	//delete는 post방식 Model FreeBoardVO, form은 get방식, regist는 post방식 Model FreeBoardVO
	
	@PostMapping("/free/freeDelete.wow")
	public String freeDelete(Model model, FreeBoardVO freeBoard) {
		try{
			freeBoardService.removeBoard(freeBoard);
			ResultMessageVO resultMessageVO = new ResultMessageVO();
			resultMessageVO.messageSetting(true, "글 삭제 성공", "글을 삭제했습니다.", "/free/freeList.wow", "목록으로");
			model.addAttribute("resultMessageVO", resultMessageVO);
			return "common/message";
		}catch(BizNotFoundException enf){
			ResultMessageVO resultMessageVO = new ResultMessageVO();
			resultMessageVO.messageSetting(false, "글 찾기 실패", "글을 찾는데 실패했습니다. 해당 글이 없습니다.", "/free/freeList.wow", "목록으로");
			model.addAttribute("resultMessageVO", resultMessageVO);
			return "common/message";
		} catch(BizPasswordNotMatchedException epm){
			ResultMessageVO resultMessageVO = new ResultMessageVO();
			resultMessageVO.messageSetting(false, "글 삭제 실패", "글을 삭제하는데 실패했습니다. 비밀번호가 달라요.", "/free/freeList.wow", "목록으로");
			model.addAttribute("resultMessageVO", resultMessageVO);
			return "common/message";
		} catch(BizNotEffectedException ene){
			ResultMessageVO resultMessageVO = new ResultMessageVO();
			resultMessageVO.messageSetting(false, "글 삭제 실패", "글을 삭제하는데 실패했습니다.", "/free/freeList.wow", "목록으로");
			model.addAttribute("resultMessageVO", resultMessageVO);
			return "common/message";
		}
	}
	
	@RequestMapping("/free/freeForm.wow")
	public String freeForm(Model model, @ModelAttribute("freeBoard") FreeBoardVO freeBoard) {
		//freeForm.wow올때 파라미터가 한개도 없습니다.
		//파라미터가 없을때 @ModelAttribute 쓰면
		//FreeBoardVO 새로 생성해서 model에 "freeBoard"라는 이름으로 저장
		return "free/freeForm";
	}
	
	//return 타입이 String 말고도 여러개 쓸 수 있다.
	
	/*
	 * @GetMapping("/free/freeForm.wow") public ModelAndView freeForm2(Model model)
	 * { ModelAndView mav = new ModelAndView(); mav.setViewName("free/freeForm");
	 * mav.addObject("data", "data"); return null; }
	 */
	
	//boNo가 파라미터로 안온다 -> int 라서 값이 0
	@PostMapping("/free/freeRegist.wow")
	public String freeRegist(Model model, @ModelAttribute("freeBoard")@Validated(Default.class)FreeBoardVO freeBoard, BindingResult error) {
		if(error.hasErrors()) {
			return "free/freeEdit";
		}
		try{
			freeBoardService.registBoard(freeBoard);
			ResultMessageVO resultMessageVO = new ResultMessageVO();
			resultMessageVO.messageSetting(true, "글 등록 성공", "글을 등록했습니다.", "/free/freeList.wow", "목록으로");
			model.addAttribute("resultMessageVO", resultMessageVO);
			return "common/message";
		}catch(BizNotEffectedException ebe){
			ResultMessageVO resultMessageVO = new ResultMessageVO();
			resultMessageVO.messageSetting(false, "글 등록 실패", "글을 등록하는데 실패했습니다.", "/free/freeList.wow", "목록으로");
			model.addAttribute("resultMessageVO", resultMessageVO);
			return "common/message";
		}
	}
	

	
}
