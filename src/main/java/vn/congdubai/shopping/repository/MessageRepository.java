package vn.congdubai.shopping.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.congdubai.shopping.domain.Messages;

@Repository
public interface MessageRepository extends JpaRepository<Messages, Integer> {

    List<Messages> findByReceiverOrderByTimestampDesc(String receiver);

    long countByReceiverAndIsRead(String receiver, boolean isRead);

    List<Messages> findAll();
}
