package org.pf.service.mapper;

import org.pf.domain.*;
import org.pf.service.dto.UserSettingsDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity UserSettings and its DTO UserSettingsDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface UserSettingsMapper extends EntityMapper<UserSettingsDTO, UserSettings> {

    

    

    default UserSettings fromId(Long id) {
        if (id == null) {
            return null;
        }
        UserSettings userSettings = new UserSettings();
        userSettings.setId(id);
        return userSettings;
    }
}
