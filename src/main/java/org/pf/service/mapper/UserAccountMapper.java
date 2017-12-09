package org.pf.service.mapper;

import org.pf.domain.*;
import org.pf.service.dto.UserAccountDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity UserAccount and its DTO UserAccountDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, CurrencyMapper.class})
public interface UserAccountMapper extends EntityMapper<UserAccountDTO, UserAccount> {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.login", target = "userLogin")
    @Mapping(source = "currency.id", target = "currencyId")
    @Mapping(source = "currency.name", target = "currencyName")
    UserAccountDTO toDto(UserAccount userAccount); 

    @Mapping(source = "userId", target = "user")
    @Mapping(source = "currencyId", target = "currency")
    UserAccount toEntity(UserAccountDTO userAccountDTO);

    default UserAccount fromId(Long id) {
        if (id == null) {
            return null;
        }
        UserAccount userAccount = new UserAccount();
        userAccount.setId(id);
        return userAccount;
    }
}
