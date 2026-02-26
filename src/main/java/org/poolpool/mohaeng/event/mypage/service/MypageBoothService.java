package org.poolpool.mohaeng.event.mypage.service;

import java.util.List;

import org.poolpool.mohaeng.event.mypage.dto.BoothMypageResponse;
import org.poolpool.mohaeng.event.mypage.repository.MypageBoothRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MypageBoothService {

    private final MypageBoothRepository repo;

    public List<BoothMypageResponse> getMyBooths(Long userId) {
        return repo.findMyBooths(userId);
    }
}
