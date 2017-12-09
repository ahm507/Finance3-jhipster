package org.pf.service.mapper;

import org.pf.domain.*;
import org.pf.service.dto.TransactionDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Transaction and its DTO TransactionDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, UserAccountMapper.class})
public interface TransactionMapper extends EntityMapper<TransactionDTO, Transaction> {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.login", target = "userLogin")
    @Mapping(source = "withdrawAccount.id", target = "withdrawAccountId")
    @Mapping(source = "withdrawAccount.text", target = "withdrawAccountText")
    @Mapping(source = "depositAccount.id", target = "depositAccountId")
    @Mapping(source = "depositAccount.text", target = "depositAccountText")
    TransactionDTO toDto(Transaction transaction); 

    @Mapping(source = "userId", target = "user")
    @Mapping(source = "withdrawAccountId", target = "withdrawAccount")
    @Mapping(source = "depositAccountId", target = "depositAccount")
    Transaction toEntity(TransactionDTO transactionDTO);

    default Transaction fromId(Long id) {
        if (id == null) {
            return null;
        }
        Transaction transaction = new Transaction();
        transaction.setId(id);
        return transaction;
    }
}
