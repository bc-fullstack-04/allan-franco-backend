package br.com.sysmap.bootcamp.domain.repository;

import br.com.sysmap.bootcamp.domain.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

}
