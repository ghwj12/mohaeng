package org.poolpool.mohaeng.admin.dormantmanage.service;

import org.poolpool.mohaeng.admin.dormantmanage.dto.DormantUserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminDormantManageService {

	//휴면 계정 관리 프로시저 호출
	void callDormantUserProc();
	
	//휴면 계정 관리 조회
	Page<DormantUserDto> findDormantUsers(Pageable pageable);
}
