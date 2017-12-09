package org.pf.service.dto;


import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the UserSettings entity.
 */
public class UserSettingsDTO implements Serializable {

    private Long id;

    @NotNull
    private Double usdRate;

    @NotNull
    private Double sarRate;

    private Long userId;

    private String userLogin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getUsdRate() {
        return usdRate;
    }

    public void setUsdRate(Double usdRate) {
        this.usdRate = usdRate;
    }

    public Double getSarRate() {
        return sarRate;
    }

    public void setSarRate(Double sarRate) {
        this.sarRate = sarRate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
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
            ", usdRate=" + getUsdRate() +
            ", sarRate=" + getSarRate() +
            "}";
    }
}
