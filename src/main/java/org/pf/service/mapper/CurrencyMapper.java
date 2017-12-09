package org.pf.service.mapper;

import org.pf.domain.*;
import org.pf.service.dto.CurrencyDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Currency and its DTO CurrencyDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CurrencyMapper extends EntityMapper<CurrencyDTO, Currency> {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.login", target = "userLogin")
    CurrencyDTO toDto(Currency currency); 

    @Mapping(source = "userId", target = "user")
    Currency toEntity(CurrencyDTO currencyDTO);

    default Currency fromId(Long id) {
        if (id == null) {
            return null;
        }
        Currency currency = new Currency();
        currency.setId(id);
        return currency;
    }
}
