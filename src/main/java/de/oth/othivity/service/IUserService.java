package de.oth.othivity.service;

import org.springframework.stereotype.Service;
import de.oth.othivity.model.security.User;
import de.oth.othivity.dto.RegisterRequest;
import de.oth.othivity.exception.UserAlreadyExistException;

@Service
public interface IUserService {

    User registerNewUserAccount(RegisterRequest registerRequest) throws UserAlreadyExistException;

}
