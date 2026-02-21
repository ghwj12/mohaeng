package org.poolpool.mohaeng.admin.dormantmanage.service;

import org.poolpool.mohaeng.admin.dormantmanage.dto.DormantUserDto;
import org.poolpool.mohaeng.admin.dormantmanage.repository.AdminDormantManageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDormantManageServiceImpl implements AdminDormantManageService {

    private final AdminDormantManageRepository dormantRepository;

    //휴면 계정 관리 프로시저 호출
    @Override
    @Transactional
    public void callDormantUserProc() {
    	dormantRepository.callDormantUserProc();
    }
    
    //휴면 계정 관리 조회
    @Override
    @Transactional(readOnly = true)
    public Page<DormantUserDto> findDormantUsers(Pageable pageable) {

        return dormantRepository.findDormantUsers(pageable);
    }
}