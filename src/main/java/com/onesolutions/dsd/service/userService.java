package com.onesolutions.dsd.service;

import com.onesolutions.dsd.dto.UserRequestDTO;
import com.onesolutions.dsd.dto.UserResponseDTO;

public interface userService {

    UserResponseDTO registerUser(UserRequestDTO userRequest);


}
