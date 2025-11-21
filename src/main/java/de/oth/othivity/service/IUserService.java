package de.oth.othivity.service;

import org.springframework.stereotype.Service;
import de.oth.othivity.model.security.User;
import de.oth.othivity.dto.RegisterDto;
import de.oth.othivity.exception.UserAlreadyExistException;

@Service
public interface IUserService {

    User registerNewUserAccount(RegisterDto registerDto) throws UserAlreadyExistException;

}
