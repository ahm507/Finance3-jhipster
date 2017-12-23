package org.pf.service.dto;


import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the UserSettings entity.
 */
public class UserSettingsDTO implements Serializable {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserSettingsDTO userSettingsDTO = (UserSettingsDTO) o;
        if(userSettingsDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userSettingsDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserSettingsDTO{" +
            "id=" + getId() +
            "}";
    }
}
