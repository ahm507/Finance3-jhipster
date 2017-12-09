package org.pf.service.dto;


import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the Transaction entity.
 */
public class TransactionDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime date;

    @NotNull
    private Double amount;

    @Size(max = 100)
    private String description;

    private Long userId;

    private String userLogin;

    private Long withdrawAccountId;

    private String withdrawAccountText;

    private Long depositAccountId;

    private String depositAccountText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Long getWithdrawAccountId() {
        return withdrawAccountId;
    }

    public void setWithdrawAccountId(Long userAccountId) {
        this.withdrawAccountId = userAccountId;
    }

    public String getWithdrawAccountText() {
        return withdrawAccountText;
    }

    public void setWithdrawAccountText(String userAccountText) {
        this.withdrawAccountText = userAccountText;
    }

    public Long getDepositAccountId() {
        return depositAccountId;
    }

    public void setDepositAccountId(Long userAccountId) {
        this.depositAccountId = userAccountId;
    }

    public String getDepositAccountText() {
        return depositAccountText;
    }

    public void setDepositAccountText(String userAccountText) {
        this.depositAccountText = userAccountText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TransactionDTO transactionDTO = (TransactionDTO) o;
        if(transactionDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), transactionDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TransactionDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", amount=" + getAmount() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
