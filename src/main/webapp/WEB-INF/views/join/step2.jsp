<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>   
<!DOCTYPE html>
<html lang="ko">
<head>
<%@include file="/WEB-INF/inc/header.jsp"%>
<title>회원가입 2단계</title>
</head>
<body>
<%@include file="/WEB-INF/inc/top.jsp"%>
<div class="container">
<form:form modelAttribute="member" method="post" action="step3.wow">
<div class="row col-md-8 col-md-offset-2">
	<div class="page-header">
		<h3>회원가입 2단계</h3>
	</div>


	<table class="table" >
		<colgroup>
			<col width="20%" />
			<col />
		</colgroup>
		<tr>
			<th>ID</th>
			<td>
			<form:input path="memId" cssClass="form-control input-sm"/>
			<form:errors path="memId"/>
			<button type="button" class="idCheck" onclick="idCheck()">ID중복확인</button>
			</td>
		</tr>
		<tr>
			<th>비밀번호</th>
			<td>
			<form:password path="memPass" cssClass="form-control input-sm"/>
			<form:errors path="memPass"/>
			</td>
		</tr>
		
		<tr class="form-group-sm">
			<th>회원명</th>
			<td>
				<form:input path="memName" cssClass="form-control input-sm"/>
			<form:errors path="memName"/>
			</td>
		</tr>
		<tr class="form-group-sm">
			<th>이메일</th>
			<td>
				<form:input id="emailAdd" path="memMail" cssClass="form-control input-sm"/>
				<form:button type="button" onclick="fn_emailCheck()">이메일 인증</form:button>
			<input type="text" id="randomKey" value="" class="form-control input-sm" placeholder="이메일에 받은 6가지 랜덤숫자를 입력하세요.">
			<form:errors path="memMail"/>
			</td>
		</tr>		
		<tr>
			<td colspan="2">
				<div class="pull-left" >
					<a href="cancel" class="btn btn-sm btn-default" >
						<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
						&nbsp;&nbsp;취 소
					</a>
				</div>
				<div class="pull-right">
					<button type="submit" class="btn btn-sm btn-primary" >
						<span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span> 
						&nbsp;&nbsp;다 음 
					</button>
				</div>
			</td>
		</tr>	
	</table>

</div>
</form:form>
</div> <!-- END : 메인 콘텐츠  컨테이너  -->
</body>
<script type="text/javascript">
var randomKey = "";
var idCheckYn = "Y";

function fn_emailCheck(){
	//selector find closest val html text attr
	
	//ajax
	//url : 주소 : 보통 같은 서버에 있는 곳에 요청할 때는 c:url 태그와 함께
	//data : 파라미터들
	//type : get, post
	//dataType : 요청한 곳으로부터 받는 데이터 타입
	//success : function(data) 성공하고 나서 실행되는 함수, data는 요청한 곳으로부터 받은 데이터

	//서버는 브라우저한테 html 태그 전달해주고 역할 끝
	//html태그는 브라우저가 실행해줍니다. button이벤트, ajax 등등 전부 브라우저가 실행해줘요
	event.preventDefault(); //form:form 태그 안의 버튼이라서 submit되는거 막아줌
	var emailAdd = $("#emailAdd").val();
	$.ajax({
		url:"<c:url value='/join/emailSend.wow'/>",
		type:"post",
		data:{"emailAdd":emailAdd},
		success: function (data){
			randomKey = data; 
		}
	});
	//다음 버튼 눌렀을때 사용자가 입력한 값이랑 randomKey값이 같아야만
	//step3로 넘어가도록, 다르면 alert("이메일 인증키가 다릅니다.")
	//버튼에 함수걸어서 event.preventDefault();
}
$(".btn-sm").click(function(){
	event.preventDefault();
	inputKey = $("#randomKey").val();
	if (inputKey == null || inputKey == undefined || inputKey == "") {
		alert("인증키를 입력하세요.");
		return false;
	}
	if (idCheckYn == "N") {
		alert("중복된 아이디입니다.");
		return false;
	}
	if (randomKey != inputKey) {
		alert("이메일 인증키가 다릅니다.");
	} else {
		alert("이메일 인증키가 같습니다.");
		$("form").submit();
	}
});
//id중복체크 버튼 반들고 함수 만들기
//함수에서 ajax호출해서 내가 입력한 id값이 dB에 있는지 없는지 확인
//있으면 idCheckYn="N" 없으면 idCheckYn="Y"
//밑에 다음버튼 이벤트에서 이메일 인증과 함께 idCheckYn도 확인해서
//이메일도 인증했고 id도 체크해야 submit되도록
function idCheck(){
	let memId = $("#memId").val();
	console.log(memId);
	$.ajax({
		url:"<c:url value='/join/idCheck.wow'/>",
		type:"post",
		data:{"memId":memId},
		success: function (data){
			console.log(data);
			if (data == null || data == undefined || data =="") {
				idCheckYn = "Y";
				alert("사용가능한 아이디");
			} else  {
				idCheckYn = "N";
				alert("아이디 중복");
			}
			console.log(idCheckYn);
		}
	});
}

</script>
</html>



