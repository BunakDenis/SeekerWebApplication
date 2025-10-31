package com.example.server.service;


import com.example.data.models.entity.User;
import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.service.ModelMapperService;
import com.example.data.models.utils.EntityUtilsService;
import com.example.server.api.client.UserDataClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserDataClient client;
    private final ModelMapperService mapperService;

    public Mono<User> save(User user) {
        UserDTO dto = mapperService.toDTO(user, UserDTO.class);

        return client.save(dto)
                .flatMap(userDTO ->
                        Mono.just(
                                mapperService.toEntity(dto, User.class)
                        )
                );
    }

    public Mono<User> update(User user) {
        UserDTO dto = mapperService.toDTO(user, UserDTO.class);

        return client.update(dto)
                .flatMap(userDTO -> Mono.just(
                        mapperService.toEntity(userDTO, User.class)
                ));
    }

    public Mono<User> getById(Long id) {
        return client.getById(id)
                .flatMap(userDto -> Mono.just(
                                mapperService.toEntity(userDto, User.class)
                        )
                );
    }

    public Mono<User> getByIdWithUserDetails(Long id) {
        return client.getByIdWithUserDetails(id);
    }

    public Mono<Boolean> deleteById(Long id) {
        return client.deleteById(id);
    }

    public String getUserFullName(User user) {
        if (
                !EntityUtilsService.isNullOrEmpty(user.getUserDetails().getFirstName()) &&
                        !EntityUtilsService.isNullOrEmpty(user.getUserDetails().getLastname())
        ) {
            String firstName = user.getUserDetails().getFirstName();
            String lastname = user.getUserDetails().getLastname();

            return firstName + " " + lastname;
        }

        return user.getUsername();

    }

}
