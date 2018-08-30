package ru.minesweeper.storage;

import org.springframework.data.repository.CrudRepository;
import ru.minesweeper.model.User;

public interface JpaUserStorage extends CrudRepository<User, Integer> {
    User findUserByUserName(String username);
}
