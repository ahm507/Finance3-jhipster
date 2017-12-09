package org.pf.service.mapper;

import org.pf.domain.*;
import org.pf.service.dto.UserSettingsDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity UserSettings and its DTO UserSettingsDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface UserSettingsMapper extends EntityMapper<UserSettingsDTO, UserSettings> {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.login", target = "userLogin")
    UserSettingsDTO toDto(UserSettings userSettings); 

    @Mapping(source = "userId", target = "user")
    UserSettings toEntity(UserSettingsDTO userSettingsDTO);

    default UserSettings fromId(Long id) {
        if (id == null) {
            return null;
        }
        UserSettings userSettings = new UserSettings();
        userSettings.setId(id);
        return userSettings;
    }
}
