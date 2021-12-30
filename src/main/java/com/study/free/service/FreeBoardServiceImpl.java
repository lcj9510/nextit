package com.study.free.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;

import com.study.exception.BizNotEffectedException;
import com.study.exception.BizNotFoundException;
import com.study.exception.BizPasswordNotMatchedException;
import com.study.free.dao.IFreeBoardDao;
import com.study.free.vo.FreeBoardSearchVO;
import com.study.free.vo.FreeBoardVO;

@Service
public class FreeBoardServiceImpl implements IFreeBoardService {
	
	@Inject
	IFreeBoardDao freeBoardDao;


	@Override
	public List<FreeBoardVO> getBoardList(FreeBoardSearchVO searchVO) {

		/*
		 * Map<String, Object> map = new HashMap<String, Object>(); map.put("boNo",
		 * 150); map.put("boTitle", "어벤져스");
		 * System.out.println(freeBoardDao.getBoardMap(map));
		 * System.out.println(freeBoardDao.getBoardAnotation(150, "어벤져스"));
		 */
		int totalRowCount = freeBoardDao.getTotalRowCount(searchVO);
		searchVO.setTotalRowCount(totalRowCount);
		searchVO.pageSetting();
			return freeBoardDao.getBoardList(searchVO);
		
	}

	@Override
	public FreeBoardVO getBoard(int boNo) throws BizNotFoundException {
	
			FreeBoardVO freeBoard = freeBoardDao.getBoard(boNo);
			if (freeBoard == null) {
				throw new BizNotFoundException();
			}
			return freeBoard;
		
	}

	@Override
	public void increaseHit(int boNo) throws BizNotEffectedException {
		
			int cnt = freeBoardDao.increaseHit(boNo); 
			if (cnt == 0) {
				throw new BizNotEffectedException();
			}
		

	}

	@Override
	public void modifyBoard(FreeBoardVO freeBoard)
			throws BizNotFoundException, BizPasswordNotMatchedException, BizNotEffectedException {
			FreeBoardVO vo = freeBoardDao.getBoard(freeBoard.getBoNo());
			if (vo == null) {
				throw new BizNotFoundException();
			}
			if (freeBoard.getBoPass().equals(vo.getBoPass())) {
				int cnt = freeBoardDao.updateBoard(freeBoard);
				if (cnt == 0) {
					throw new BizNotEffectedException();
				}
			} else {
				throw new BizPasswordNotMatchedException();
			}
	}

	@Override
	public void removeBoard(FreeBoardVO freeBoard)
			throws BizNotFoundException, BizPasswordNotMatchedException, BizNotEffectedException {
			FreeBoardVO vo = freeBoardDao.getBoard(freeBoard.getBoNo()); 
			if (vo == null) {
				throw new BizNotFoundException();
			}
			if (freeBoard.getBoPass().equals(vo.getBoPass())) {
				int cnt = freeBoardDao.deleteBoard(freeBoard);
				if (cnt == 0) {
					throw new BizNotEffectedException();
				}
			} else {
				throw new BizPasswordNotMatchedException();
			}
	}

	@Override
	public void registBoard(FreeBoardVO freeBoard) throws BizNotEffectedException {
			int cnt = freeBoardDao.insertBoard(freeBoard);
			if (cnt == 0) {
				throw new BizNotEffectedException();
			}
	}

}
