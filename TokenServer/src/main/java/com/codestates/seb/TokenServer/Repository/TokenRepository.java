package com.codestates.seb.TokenServer.Repository;

import com.codestates.seb.TokenServer.Entity.UserList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class TokenRepository {

    private final EntityManager entityManager;

    @Autowired
    public TokenRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // UserId를 기준으로 User 데이터를 불러옵니다.
    @Transactional
    public UserList UserFindByUserId(String userid) {
        List<UserList> list = entityManager
                .createQuery("SELECT user FROM UserList AS user WHERE user.userId='" + userid + "'", UserList.class)
                .getResultList();
        entityManager.close();
        return list.get(0);
    }

}
