package com.example.database.service;

import com.example.data.models.entity.dto.UserDetailsDTO;
import com.example.database.entity.UserDetails;
import com.example.database.repo.telegram.UserDetailsRepo;
import com.example.database.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsService {

    private final UserService userService;

    private UserDetailsRepo repo;

    public UserDetails getDetails(Long id) {
        Optional<UserDetails> userDetails = repo.findById(id);

        if (userDetails.isEmpty()) {
            throw new NoSuchElementException("Подробная информация о пользователе c id = " + id + ", не найдена.");
        }
        return userDetails.get();
    }

    public UserDetails create(UserDetails userDetails) {
        UserDetails result = repo.save(userDetails);

        return result;
    }

    public UserDetails update(UserDetails userDetails) {
        return create(userDetails);
    }

    public boolean delete(UserDetails userDetails) {

        try {
            repo.delete(userDetails);
            return true;
        } catch (Exception e) {
            log.debug("Ошибка удаления информации о пользователе " + userDetails);
            throw new NoSuchElementException("Ошибка удаления информации о пользователе " + userDetails);
        }

    }

    public UserDetails toEntity(UserDetailsDTO dto) {

        return UserDetails.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastname(dto.getLastname())
                .birthday(dto.getBirthday())
                .phoneNumber(dto.getPhoneNumber())
                .gender(dto.getGender())
                .location(dto.getLocation())
                .avatarLink(dto.getAvatarLink())
                .dateStartStudyingSchool(dto.getDateStartStudyingSchool())
                .curator(dto.getCurator())
                .build();

    }

    public UserDetailsDTO toDTO(UserDetails userDetails) {

            return UserDetailsDTO.builder()
                    .id(userDetails.getId())
                    .firstName(userDetails.getFirstName())
                    .lastname(userDetails.getLastname())
                    .birthday(userDetails.getBirthday())
                    .phoneNumber(userDetails.getPhoneNumber())
                    .gender(userDetails.getGender())
                    .location(userDetails.getLocation())
                    .avatarLink(userDetails.getAvatarLink())
                    .dateStartStudyingSchool(userDetails.getDateStartStudyingSchool())
                    .curator(userDetails.getCurator())
                    .build();
    }

}
