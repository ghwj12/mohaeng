package org.poolpool.mohaeng.event.review.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.poolpool.mohaeng.event.list.entity.EventEntity;
import org.poolpool.mohaeng.event.review.entity.ReviewEntity;
import org.poolpool.mohaeng.user.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest(properties = {
  "spring.datasource.url=jdbc:mysql://localhost:3306/mohaeng_test?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true",
  "spring.datasource.username=poolpool",
  "spring.datasource.password=poolpool9900*",
  "spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver",
  "spring.jpa.hibernate.ddl-auto=create",
  "spring.jpa.show-sql=true"
})
@Rollback(false)
@EntityScan(basePackages = {"org.poolpool.mohaeng"})
@EnableJpaRepositories(basePackages = {"org.poolpool.mohaeng.event.review.repository"})
class ReviewRepositoryTest {

  @Autowired TestEntityManager em;
  @Autowired ReviewRepository reviewRepository;

  @Test
  void existsByUser_UserIdAndEvent_EventId_true() {
    UserEntity u = new UserEntity();
    u.setName("tester");
    em.persist(u);

    EventEntity ev = new EventEntity();
    ev.setTitle("event");
    em.persist(ev);

    ReviewEntity r = new ReviewEntity();
    r.setUser(u);
    r.setEvent(ev);
    r.setRatingContent(5);
    r.setRatingProgress(4);
    r.setRatingMood(5);
    r.setContent("good");
    em.persist(r);

    em.flush();

    boolean exists = reviewRepository.existsByUser_UserIdAndEvent_EventId(u.getUserId(), ev.getEventId());
    assertThat(exists).isTrue();
  }

  @Test
  void findByEvent_EventIdAndUser_UserIdNotOrderByCreatedAtDesc_returns_other_users() {
    UserEntity me = new UserEntity();
    me.setName("me");
    em.persist(me);

    UserEntity other = new UserEntity();
    other.setName("other");
    em.persist(other);

    EventEntity ev = new EventEntity();
    ev.setTitle("event");
    em.persist(ev);

    ReviewEntity myReview = new ReviewEntity();
    myReview.setUser(me);
    myReview.setEvent(ev);
    myReview.setRatingContent(5);
    myReview.setRatingProgress(5);
    myReview.setRatingMood(5);
    myReview.setContent("mine");
    em.persist(myReview);

    ReviewEntity otherReview = new ReviewEntity();
    otherReview.setUser(other);
    otherReview.setEvent(ev);
    otherReview.setRatingContent(3);
    otherReview.setRatingProgress(3);
    otherReview.setRatingMood(3);
    otherReview.setContent("others");
    em.persist(otherReview);

    em.flush();

    var page = reviewRepository.findByEvent_EventIdAndUser_UserIdNotOrderByCreatedAtDesc(
        ev.getEventId(), me.getUserId(), PageRequest.of(0, 10)
    );

    assertThat(page.getContent()).hasSize(1);
    assertThat(page.getContent().get(0).getUser().getUserId()).isEqualTo(other.getUserId());
  }
}
