package de.oth.othivity.service;

import org.springframework.stereotype.Service;
import de.oth.othivity.model.security.User;
import de.oth.othivity.dto.RegisterDto;

@Service
public interface IUserService {

    User registerNewUserAccount(RegisterDto registerDto);

}
