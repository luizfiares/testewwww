package com.magazine.magazine.repository;
import com.magazine.magazine.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {

    @Query("SELECT u FROM Users u WHERE EXISTS (SELECT o FROM Orders o WHERE o.users = u AND o.date BETWEEN ?1 AND ?2)")
    List<Users> findByDateBetween(String startDate, String endDate);




}
