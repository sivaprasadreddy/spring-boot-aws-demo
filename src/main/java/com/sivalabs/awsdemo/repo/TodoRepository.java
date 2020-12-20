package com.sivalabs.awsdemo.repo;

import com.sivalabs.awsdemo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}
