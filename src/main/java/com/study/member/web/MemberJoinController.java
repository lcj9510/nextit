package com.study.member.web;

import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.study.code.service.ICommCodeService;
import com.study.code.vo.CodeVO;
import com.study.common.valid.Step1;
import com.study.common.valid.Step2;
import com.study.common.valid.Step3;
import com.study.common.vo.ResultMessageVO;
import com.study.exception.BizDuplicateKeyException;
import com.study.exception.BizNotEffectedException;
import com.study.exception.BizNotFoundException;
import com.study.member.service.IMemberService;
import com.study.member.service.MailSendService;
import com.study.member.vo.MemberVO;
import com.sun.mail.imap.protocol.MailboxInfo;

@Controller
@SessionAttributes("member")
public class MemberJoinController {
	//@modelAttribute("member")는 session에 있ㄴ는 model에 "member"가 있으면 새로 생성안하고 있는거 사용
	//없으면 새로 생성
	//@SessionAttributes("member")는 controller안에서만 유지되는 session
	
	//step1.wow 처음 들어가기전에 모델이 "member"저장
	@ModelAttribute("member")
	public MemberVO member() {
		return new MemberVO();
	}
	
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
	
	@RequestMapping("/join/step1.wow")
	public String step1(@ModelAttribute("member") MemberVO member) {
		return "join/step1";
	}
	@RequestMapping("/join/step2.wow")
	public String step2(@ModelAttribute("member") @Validated(value = {Step1.class}) MemberVO member, BindingResult error) {
		if (error.hasErrors()) {
			return "join/step1";
		}
		return "join/step2";
	}
	@RequestMapping("/join/step3.wow")
	public String step3(@ModelAttribute("member") @Validated(value = {Step2.class}) MemberVO member, BindingResult error) {
		if (error.hasErrors()) {
			return "join/step2";
		}
		return "join/step3";
	}
	
	@RequestMapping("/join/regist.wow")
	public String regist(Model model, @ModelAttribute("member")@Validated(value = {Step3.class}) MemberVO member, BindingResult error, SessionStatus sessionStatus) {
		if (error.hasErrors()) {
			return "join/step3";
		}
		//검사통과 ->insert하면 된다.
		try{
			memberService.registMember(member);
			sessionStatus.setComplete();
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
	
	@RequestMapping("/join/cancel")
	public String cancel(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return "home";
	}
	
	@Autowired
	MailSendService mailSendService;
	
	@RequestMapping("/join/emailSend.wow")
	@ResponseBody //return값으로 jsp를 찾는게 아니라 요청 source(ajax)에 데이터를 전달
	public String emailSend(String emailAdd) {
		//이메일을 보내보자 mailSendService를 실행하면 된다
		String randomKey = mailSendService.sendAuthMail(emailAdd);
		return randomKey;
	}
	
	@RequestMapping("/join/idCheck.wow")
	@ResponseBody //return값으로 jsp를 찾는게 아니라 요청 source(ajax)에 데이터를 전달
	public String idCheck(String memId) {
		//이메일을 보내보자 mailSendService를 실행하면 된다
		String idCheck;
		try {
			idCheck = memberService.getMember(memId).getMemId();
			return idCheck;
		} catch (BizNotFoundException e) {
			return null;
		}
	}
}
