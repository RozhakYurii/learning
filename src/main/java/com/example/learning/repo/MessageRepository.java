package com.example.learning.repo;

import com.example.learning.model.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface MessageRepository extends CrudRepository<Message, Integer> {


    Set<Message> findAllByTag(String tag);


}
