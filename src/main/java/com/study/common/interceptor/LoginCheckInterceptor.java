package com.study.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.study.login.vo.UserVO;

public class LoginCheckInterceptor extends HandlerInterceptorAdapter{
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//mypage로 뭔가 요청이 왔는데 로그인이 되어있으면 mypage로 가고
		//안되어있으면 login페이지로 가도록
		HttpSession session = request.getSession(false); //getSession(true)
		//session이 없는 경우 브라우저에서 맨 처음 서버로 요청할때
		//-> 브라우저가 쿠키 JSESSIONID가 없는 상태에서 서버에 요청할때
		if (session == null) {
			//맨 처음 요청
			response.sendRedirect(request.getContextPath() + "/login/login.wow");
			return false;
		}
		UserVO user = (UserVO)session.getAttribute("USER_INFO");
		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/login/login.wow");
			return false;
		}
		return true;
	}
}
