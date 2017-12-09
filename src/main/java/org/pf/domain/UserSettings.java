package org.pf.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;


/**
 * A UserSettings.
 */
@Entity
@Table(name = "user_settings")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "usersettings")
public class UserSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "usd_rate", nullable = false)
    private Double usdRate;

    @NotNull
    @Column(name = "sar_rate", nullable = false)
    private Double sarRate;

    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getUsdRate() {
        return usdRate;
    }

    public UserSettings usdRate(Double usdRate) {
        this.usdRate = usdRate;
        return this;
    }

    public void setUsdRate(Double usdRate) {
        this.usdRate = usdRate;
    }

    public Double getSarRate() {
        return sarRate;
    }

    public UserSettings sarRate(Double sarRate) {
        this.sarRate = sarRate;
        return this;
    }

    public void setSarRate(Double sarRate) {
        this.sarRate = sarRate;
    }

    public User getUser() {
        return user;
    }

    public UserSettings user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserSettings userSettings = (UserSettings) o;
        if (userSettings.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userSettings.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserSettings{" +
            "id=" + getId() +
            ", usdRate=" + getUsdRate() +
            ", sarRate=" + getSarRate() +
            "}";
    }
}
