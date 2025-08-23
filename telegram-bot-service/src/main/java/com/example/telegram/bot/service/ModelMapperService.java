package com.example.telegram.bot.service;

import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.telegram.bot.entity.TelegramUser;
import com.example.telegram.bot.entity.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class ModelMapperService {

    private final ModelMapper mapper;

    public <T, K> T toEntity (K dto, Class<T> entity) {
        return mapper.map(dto, entity);
    }

    public <T, K> K toDTO (T entity, Class<K> dto) {
        return mapper.map(entity, dto);
    }

    public TelegramUser apiTelegramUserToEntity(org.telegram.telegrambots.meta.api.objects.User user) {
        return TelegramUser.builder()
                .id(user.getId())
                .isActive(true)
                .build();
    }

    public TelegramUserDTO apiTelegramUserEntityToDto(User userEntityTG) {
        return null;
    }

/*
    public User userDtoToEntity(UserDTO dto) {

        List<TelegramUser> telegramUsers = new ArrayList<>();
        UserDetails userDetails = new UserDetails();

        List<TelegramUserDTO> telegramUsersDTO = dto.getTelegramUsers();

        if (!telegramUsersDTO.isEmpty()) telegramUsersDTO.forEach(user -> telegramUsers.add(telegramUserDTOtoEntity(user)));

        if (Objects.nonNull(dto.getUserDetails()))
            userDetails = userDetailsDTOToEntity(dto.getUserDetails());

        return User.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .role(dto.getRole())
                .isActive(dto.getIsActive())
                .userDetails(userDetails)
                .telegramUsers(telegramUsers)
                .build();
    }
    public UserDTO userToDTO(User user) {

        List<TelegramUserDTO> telegramUserDTOList = new ArrayList<>();
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();

        if (Objects.nonNull(user.getUserDetails()))
            userDetailsDTO = userDetailsToDTO(user.getUserDetails());

        if (!user.getTelegramUsers().isEmpty()) user.getTelegramUsers().forEach(tu -> telegramUserDTOList.add(telegramUserToDto(tu)));

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .userDetails(userDetailsDTO)
                .telegramUsers(telegramUserDTOList)
                .build();
    }
    public UserDetails userDetailsDTOToEntity(UserDetailsDTO dto) {

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
    public UserDetailsDTO userDetailsToDTO(UserDetails userDetails) {

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
    public TelegramUser telegramUserDTOtoEntity(TelegramUserDTO dto) {

        List<TelegramChat> telegramChatList = new ArrayList<>();
        TelegramSession session = new TelegramSession();

        if (!telegramChatList.isEmpty()) dto.getTelegramChats().forEach(u -> telegramChatList.add(telegramChatDtoToEntity(u)));

        if (Objects.nonNull(dto.getTelegramSession())) session = telegramSessionDtoToEntity(dto.getTelegramSession());

        return TelegramUser.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .telegramSession(session)
                .telegramChats(telegramChatList)
                .build();
    }
    public List<TelegramUser> telegramUserDtoListToEntity(List<TelegramUserDTO> dtoList) {

        List<TelegramUser> result = new ArrayList<>();

        if (!dtoList.isEmpty()) dtoList.forEach(dto -> result.add(telegramUserDTOtoEntity(dto)));

        return result;
    }
    public TelegramUser apiTelegramUserToEntity(org.telegram.telegrambots.meta.api.objects.User user) {
        return TelegramUser.builder()
                .id(user.getId())
                .isActive(true)
                .build();
    }
    public TelegramUserDTO telegramUserToDto(TelegramUser user) {

        List<TelegramChatDTO> telegramChatDTOList = new ArrayList<>();

        TelegramSessionDTO telegramSessionDTO = new TelegramSessionDTO();

        if (!user.getTelegramChats().isEmpty())
            user.getTelegramChats().forEach(u -> telegramChatDTOList.add(telegramChatToDTO(u)));

        if (Objects.nonNull(user.getTelegramSession()))
            telegramSessionDTO = telegramSessionToDTO(user.getTelegramSession());

        return TelegramUserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .telegramSession(telegramSessionDTO)
                .telegramChats(telegramChatDTOList)
                .build();
    }
    public TelegramUserDTO apiTelegramUserEntityToDto(User userEntityTG) {
        return null;
    }
    public TelegramChat telegramChatDtoToEntity(TelegramChatDTO dto) {

        TelegramUser telegramUser = new TelegramUser();

        if (Objects.nonNull(dto.getTelegramUserDTO()))
            telegramUser = telegramUserDTOtoEntity(dto.getTelegramUserDTO());

        return TelegramChat.builder()
                .id(dto.getId())
                .uiElement(dto.getUiElement())
                .uiElementValue(dto.getUiElementValue())
                .chatState(dto.getChatState())
                .telegramUser(telegramUser)
                .build();
    }
    public TelegramChatDTO telegramChatToDTO(TelegramChat telegramChat) {

        TelegramUserDTO telegramUserDTO = new TelegramUserDTO();

        if (Objects.nonNull(telegramChat.getTelegramUser()))
            telegramUserToDto(telegramChat.getTelegramUser());

        return TelegramChatDTO.builder()
                .id(telegramChat.getId())
                .uiElement(telegramChat.getUiElement())
                .uiElementValue(telegramChat.getUiElementValue())
                .chatState(telegramChat.getChatState())
                .telegramUserDTO(telegramUserDTO)
                .build();

    }
    public TelegramSession telegramSessionDtoToEntity(TelegramSessionDTO dto) {

        TelegramUser telegramUser = new TelegramUser();

        if (Objects.nonNull(dto.getTelegramUserDTO()))
            telegramUserDTOtoEntity(dto.getTelegramUserDTO());

        return TelegramSession.builder()
                .id(dto.getId())
                .sessionData(dto.getSessionData())
                .expirationTime(dto.getExpirationTime())
                .isActive(dto.isActive())
                .telegramUser(telegramUser)
                .build();
    }
    public TelegramSessionDTO telegramSessionToDTO(TelegramSession session) {

        TelegramUserDTO telegramUserDTO = new TelegramUserDTO();

        if (Objects.nonNull(session.getTelegramUser()))
            telegramUserDTO = telegramUserToDto(session.getTelegramUser());

        return TelegramSessionDTO.builder()
                .id(session.getId())
                .sessionData(session.getSessionData())
                .expirationTime(session.getExpirationTime())
                .isActive(session.isActive())
                .telegramUserDTO(telegramUserDTO)
                .build();
    */
}
