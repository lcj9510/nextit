package com.study.member.web;

import java.util.List;

import javax.inject.Inject;
import javax.validation.groups.Default;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.study.code.service.CommCodeServiceImpl;
import com.study.code.service.ICommCodeService;
import com.study.code.vo.CodeVO;
import com.study.common.valid.Modify;
import com.study.common.vo.ResultMessageVO;
import com.study.exception.BizDuplicateKeyException;
import com.study.exception.BizNotEffectedException;
import com.study.exception.BizNotFoundException;
import com.study.member.service.IMemberService;
import com.study.member.service.MemberServiceImpl;
import com.study.member.vo.MemberSearchVO;
import com.study.member.vo.MemberVO;

@Controller
public class MemberController {

	@Inject
	IMemberService memberService;
	@Inject
	ICommCodeService codeService;
	
	@ModelAttribute("jobList")
	public List<CodeVO> jobList(){
		return codeService.getCodeListByParent("JB00");
	}
	@ModelAttribute("hobbyList")
	public List<CodeVO> hobbyList(){
		return codeService.getCodeListByParent("HB00");
	}
	
	@RequestMapping("/member/memberList.wow")
	public String memberList(Model model, @ModelAttribute("searchVO") MemberSearchVO searchVO) {
		List<MemberVO> memberList = memberService.getMemberList(searchVO);
		model.addAttribute("memberList", memberList);
		return "member/memberList";
	}
	
	@RequestMapping("/member/memberView.wow")
	public String memberView(Model model, @RequestParam(required = true) String memId) {
		try{
			MemberVO member = memberService.getMember(memId);
			model.addAttribute("member", member);
		} catch(BizNotFoundException nfe){
			ResultMessageVO resultMessageVO = new ResultMessageVO();
			resultMessageVO.messageSetting(false, "회원 찾기 실패", "회원을 찾는데 실패했습니다. 해당 회원이 없습니다.", "/member/memberList.wow", "목록으로");
			model.addAttribute("resultMessageVO", resultMessageVO);
			return "common/message";
		}
		return "member/memberView";
	}
	
	@RequestMapping("/member/memberEdit.wow")
	public String memberEdit(Model model, @RequestParam(required = true) String memId) {
		try{
			MemberVO member = memberService.getMember(memId);
			model.addAttribute("member", member);
		} catch(BizNotFoundException e){
			ResultMessageVO resultMessageVO = new ResultMessageVO();
			resultMessageVO.messageSetting(false, "회원 찾기 실패", "회원을 찾는데 실패했습니다. 해당 회원이 없습니다.", "/member/memberList.wow", "목록으로");
			model.addAttribute("resultMessageVO", resultMessageVO);
			return "common/message";
		}
		return "member/memberEdit";
	}
	
	@RequestMapping("/member/memberDelete.wow")
	public String memberDelete(Model model, MemberVO member) {
		try{
			memberService.removeMember(member);
			ResultMessageVO resultMessageVO = new ResultMessageVO();
			resultMessageVO.messageSetting(true, "회원 삭제 성공", "회원을 삭제했습니다.", "/member/memberList.wow", "목록으로");
			model.addAttribute("resultMessageVO", resultMessageVO);
			return "common/message";
		}catch(BizNotEffectedException nee){
			ResultMessageVO resultMessageVO = new ResultMessageVO();
			resultMessageVO.messageSetting(false, "회원 삭제 실패", "회원을 삭제하는데 실패했습니다.", "/member/memberList.wow", "목록으로");
			model.addAttribute("resultMessageVO", resultMessageVO);
			return "common/message";
		} catch(BizNotFoundException nfe){
			ResultMessageVO resultMessageVO = new ResultMessageVO();
			resultMessageVO.messageSetting(false, "회원 찾기 실패", "회원을 찾는데 실패했습니다. 해당 회원이 없습니다.", "/member/memberList.wow", "목록으로");
			model.addAttribute("resultMessageVO", resultMessageVO);
			return "common/message";
		}
	}
	
	@PostMapping("/member/memberModify.wow")
	public String memberModify(Model model, @ModelAttribute("member")@Validated(value = {Default.class, Modify.class})MemberVO member, BindingResult error) {
		if (error.hasErrors()) {
			return "member/memberEdit";
		}
		System.out.println(member);
		try {
			memberService.modifyMember(member);
			ResultMessageVO resultMessageVO = new ResultMessageVO();
			resultMessageVO.messageSetting(true, "회원 정보 수정 성공", "회원 정보를 수정했습니다.", "/member/memberList.wow", "목록으로");
			model.addAttribute("resultMessageVO", resultMessageVO);
			return "common/message";
		} catch (BizNotEffectedException nee) {
			ResultMessageVO resultMessageVO = new ResultMessageVO();
			resultMessageVO.messageSetting(false, "회원 정보 수정 실패", "회원 정보를 수정하는데 실패했습니다.", "/member/memberList.wow", "목록으로");
			model.addAttribute("resultMessageVO", resultMessageVO);
			return "common/message";
		} catch (BizNotFoundException nfe) {
			ResultMessageVO resultMessageVO = new ResultMessageVO();
			resultMessageVO.messageSetting(false, "회원 찾기 실패", "회원을 찾는데 실패했습니다. 해당 회원이 없습니다.", "/member/memberList.wow", "목록으로");
			model.addAttribute("resultMessageVO", resultMessageVO);
			return "common/message";
		}
	}
	
	@RequestMapping("/member/memberForm.wow")
	public String memberForm(Model model) {
		return "member/memberForm";
	}
	
	@PostMapping("/member/memberRegist.wow")
	public String memberRegist(Model model, MemberVO member) {
			try{
				memberService.registMember(member);
				ResultMessageVO resultMessageVO = new ResultMessageVO();
				resultMessageVO.messageSetting(true, "회원 가입 성공", "회원 가입했습니다.", "/member/memberList.wow", "목록으로");
				model.addAttribute("resultMessageVO", resultMessageVO);
				return "common/message";
			} catch(BizDuplicateKeyException dke){
				ResultMessageVO resultMessageVO = new ResultMessageVO();
				resultMessageVO.messageSetting(false, "아이디 중복", "중복된 아이디가 있습니다.", "/member/memberList.wow", "목록으로");
				model.addAttribute("resultMessageVO", resultMessageVO);
				return "common/message";
			} catch(BizNotEffectedException nee){
				ResultMessageVO resultMessageVO = new ResultMessageVO();
				resultMessageVO.messageSetting(false, "회원 등록 실패", "회원을 등록하는데 실패했습니다.", "/member/memberList.wow", "목록으로");
				model.addAttribute("resultMessageVO", resultMessageVO);
				return "common/message";
			}
	}
	
}
