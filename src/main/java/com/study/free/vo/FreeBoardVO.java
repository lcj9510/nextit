package com.study.free.vo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.study.common.valid.Modify;



public class FreeBoardVO {
	//int 에는 notEmpty, notBlank안됨
	@Positive(message = "글 번호는 필수, 0이면 안됨", groups = {Modify.class})
	private int boNo;                       /*글 번호*/
	
	@NotEmpty(message = "글 제목은 필수")
	private String boTitle;                 /*글 제목*/
	
	@NotEmpty(message = "글 분류는 필구")
	private String boCategory;              /*글 분류 코드*/
	
	@Size(min = 1, max = 10)
	private String boWriter;                /*작성자명*/
	
	@Size(min = 4, message = "비밀번호는 4글자 이상을 입력")
	private String boPass;                  /*비밀번호*/
	
	@Size(min = 10, message = "글 내용은 10글자 이상")
	private String boContent;               /*글 내용*/
	
	private String boIp;                    /*등록자 IP*/
	private int boHit;                      /*조회수*/
	private String boRegDate;               /*등록 일자*/
	private String boModDate;               /*수정 일자*/
	private String boDelYn;                 /*삭제 여부*/
	
	private String boCategoryNm;            /*boCategory로 comm_code테이블이랑 조인한 코드이름*/
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	
	
	public int getBoNo() {
		return boNo;
	}
	public void setBoNo(int boNo) {
		this.boNo = boNo;
	}
	public String getBoTitle() {
		return boTitle;
	}
	public void setBoTitle(String boTitle) {
		this.boTitle = boTitle;
	}
	public String getBoCategory() {
		return boCategory;
	}
	public void setBoCategory(String boCategory) {
		this.boCategory = boCategory;
	}
	public String getBoWriter() {
		return boWriter;
	}
	public void setBoWriter(String boWriter) {
		this.boWriter = boWriter;
	}
	public String getBoPass() {
		return boPass;
	}
	public void setBoPass(String boPass) {
		this.boPass = boPass;
	}
	public String getBoContent() {
		return boContent;
	}
	public void setBoContent(String boContent) {
		this.boContent = boContent;
	}
	public String getBoIp() {
		return boIp;
	}
	public void setBoIp(String boIp) {
		this.boIp = boIp;
	}
	public int getBoHit() {
		return boHit;
	}
	public void setBoHit(int boHit) {
		this.boHit = boHit;
	}
	public String getBoRegDate() {
		return boRegDate;
	}
	public void setBoRegDate(String boRegDate) {
		this.boRegDate = boRegDate;
	}
	public String getBoModDate() {
		return boModDate;
	}
	public void setBoModDate(String boModDate) {
		this.boModDate = boModDate;
	}
	public String getBoDelYn() {
		return boDelYn;
	}
	public void setBoDelYn(String boDelYn) {
		this.boDelYn = boDelYn;
	}
	public String getBoCategoryNm() {
		return boCategoryNm;
	}
	public void setBoCategoryNm(String boCategoryNm) {
		this.boCategoryNm = boCategoryNm;
	}
	
}
