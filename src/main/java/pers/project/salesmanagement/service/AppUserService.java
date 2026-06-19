package pers.project.salesmanagement.service;

import pers.project.salesmanagement.dto.request.CreateAppUserRequest;
import pers.project.salesmanagement.dto.request.UpdateAppUserRequest;
import pers.project.salesmanagement.dto.response.AppUserResponse;

import java.util.List;
import java.util.UUID;

public interface AppUserService {
    AppUserResponse createUser(CreateAppUserRequest request);

    List<AppUserResponse> getAllUsers();

    AppUserResponse updateUser(UpdateAppUserRequest request);
}
