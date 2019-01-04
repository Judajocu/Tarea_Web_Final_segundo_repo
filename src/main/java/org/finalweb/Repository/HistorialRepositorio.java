package org.finalweb.Repository;

import org.finalweb.Entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface HistorialRepositorio extends JpaRepository<History, Integer> {

    History findByHistoryId(Integer historyId);

    @Query("select h from History h where h.user.email = :email")
    History findByUser(@Param("email") String email);
}
