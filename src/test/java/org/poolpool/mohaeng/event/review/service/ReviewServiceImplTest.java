package org.poolpool.mohaeng.event.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.poolpool.mohaeng.event.list.entity.EventEntity;
import org.poolpool.mohaeng.event.review.dto.ReviewCreateRequestDto;
import org.poolpool.mohaeng.event.review.dto.ReviewEditRequestDto;
import org.poolpool.mohaeng.event.review.repository.ReviewRepository;
import org.poolpool.mohaeng.user.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

// @Rollback(false)
@ActiveProfiles("test")
@DataJpaTest(properties = {
    "spring.datasource.url=jdbc:mysql://localhost:3306/mohaeng_test?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true",
    "spring.datasource.username=poolpool",          // ✅ 너 계정으로 변경
    "spring.datasource.password=poolpool9900*",     // ✅ 너 비번으로 변경
    "spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver",
    "spring.jpa.hibernate.ddl-auto=create",
    "spring.jpa.show-sql=true"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EntityScan(basePackages = {"org.poolpool.mohaeng"})
@EnableJpaRepositories(basePackages = {"org.poolpool.mohaeng"})
@Import(ReviewServiceImpl.class) // ✅ 서비스 빈을 테스트에서 직접 등록
class ReviewServiceImplTest {

  @Autowired ReviewService reviewService;
  @Autowired ReviewRepository reviewRepository;
  @Autowired TestEntityManager em;

  @Test
  void 리뷰작성_성공() {
    UserEntity u = new UserEntity();
    u.setName("tester");
    em.persist(u);

    EventEntity ev = new EventEntity();
    ev.setTitle("event");
    em.persist(ev);
    em.flush();

    ReviewCreateRequestDto req = new ReviewCreateRequestDto();
    req.setEventId(ev.getEventId());
    req.setRatingContent(5);
    req.setRatingProgress(4);
    req.setRatingMood(5);
    req.setContent("좋아요");

    long reviewId = reviewService.create(u.getUserId(), req);

    assertThat(reviewId).isPositive();
    assertThat(reviewRepository.existsByUser_UserIdAndEvent_EventId(u.getUserId(), ev.getEventId())).isTrue();
  }

  @Test
  void 같은이벤트_중복작성_막힘() {
    UserEntity u = new UserEntity();
    u.setName("tester");
    em.persist(u);

    EventEntity ev = new EventEntity();
    ev.setTitle("event");
    em.persist(ev);
    em.flush();

    ReviewCreateRequestDto req = new ReviewCreateRequestDto();
    req.setEventId(ev.getEventId());
    req.setRatingContent(5);
    req.setRatingProgress(4);
    req.setRatingMood(5);
    req.setContent("첫 리뷰");

    reviewService.create(u.getUserId(), req);

    assertThatThrownBy(() -> reviewService.create(u.getUserId(), req))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void 다른사람리뷰_수정삭제_불가() {
    UserEntity writer = new UserEntity();
    writer.setName("writer");
    em.persist(writer);

    UserEntity other = new UserEntity();
    other.setName("other");
    em.persist(other);

    EventEntity ev = new EventEntity();
    ev.setTitle("event");
    em.persist(ev);
    em.flush();

    ReviewCreateRequestDto createReq = new ReviewCreateRequestDto();
    createReq.setEventId(ev.getEventId());
    createReq.setRatingContent(5);
    createReq.setRatingProgress(5);
    createReq.setRatingMood(5);
    createReq.setContent("작성자 리뷰");

    long reviewId = reviewService.create(writer.getUserId(), createReq);

    ReviewEditRequestDto editReq = new ReviewEditRequestDto();
    editReq.setRatingContent(1);
    editReq.setRatingProgress(1);
    editReq.setRatingMood(1);
    editReq.setContent("남이 수정 시도");

    // 타인이 수정 -> 예외
    assertThatThrownBy(() -> reviewService.edit(other.getUserId(), reviewId, editReq))
    .isInstanceOf(IllegalArgumentException.class)
    .hasMessageContaining("본인 리뷰");

    // 타인이 삭제 -> false
    assertThat(reviewService.delete(other.getUserId(), reviewId)).isFalse();

    // 작성자가 삭제 -> true
     assertThat(reviewService.delete(writer.getUserId(), reviewId)).isTrue();
  }
}
