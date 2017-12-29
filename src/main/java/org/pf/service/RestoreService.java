package org.pf.service;

import au.com.bytecode.opencsv.CSVReader;
import org.pf.domain.Currency;
import org.pf.domain.Transaction;
import org.pf.domain.User;
import org.pf.domain.UserAccount;
import org.pf.domain.enumeration.AccountType;
import org.pf.repository.CurrencyRepository;
import org.pf.repository.UserAccountRepository;
import org.pf.repository.UserRepository;
import org.pf.service.mapper.TransactionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

@Service
@Transactional(readOnly = true)
public class RestoreService {
    private final Logger log = LoggerFactory.getLogger(RestoreService.class);

    private UserAccountRepository userAccountRepository;
    private UserAccountService userAccountService;
    private TransactionService transactionService;
    private UserRepository userRepository;
    private TransactionMapper transactionMapper;
    private CurrencyRepository currencyRepository;
	public RestoreService(UserAccountRepository userAccountRepository, UserAccountService userAccountService,
        TransactionService transactionService,
        UserRepository userRepository, TransactionMapper transactionMapper,
        CurrencyRepository currencyRepository) {
		this.userAccountRepository = userAccountRepository;
		this.userAccountService = userAccountService;
		this.transactionService = transactionService;
        this.userRepository = userRepository;
        this.transactionMapper = transactionMapper;
        this.currencyRepository = currencyRepository;
    }

	public List<String> importFile(String login, Reader reader) throws Exception {
		try (CSVReader csvReader = new CSVReader(reader)) {
            return importFile(csvReader, login);
        }
	}

	@Transactional
	public List<String> importFile(String login, String fileName) throws Exception {
		try (CSVReader reader  = new CSVReader(new FileReader(fileName))) {
		    return importFile(reader, login);
        }
	}

	@Transactional
	public List<String> importFile(CSVReader reader, String login) throws Exception {
		String[] nextLine;
		deleteAccountsAndTransactions(login);

		Vector<String> output = new Vector<>();
		// skip first line of field column name
		reader.readNext();
		int line = 0;
		while ((nextLine = reader.readNext()) != null) {
			if (nextLine[0].isEmpty()) {
				output.add("Finished, next line is blank" + "<br>");
				output.add("Import is finished successfully, I hope :)<br>");
				reader.close();
				return output;
			}
			line++;
			createTransactionAndAccounts(login, nextLine);
			String lineText = "Done " + String.valueOf(line) + ":, " + nextLine[0] + ", " + nextLine[1];
			output.add(lineText + "<br>");
            log.debug(lineText + "\r\n");
		}
		output.add("Import is finished successfully, I hope :)<br>");

		return output;

	}

	private void createTransactionAndAccounts(String login, String[] segments) throws Exception {
		String date = segments[0];
		String description = segments[1];
		String withdrawName = segments[2];
		String depositName = segments[3];
		String amount = segments[4];
		String currency = segments[5];
//		String withdrawId = getAccountId(login, withdrawName, currency);
//		String depositId = getAccountId(login, depositName, currency);

		amount = amount.replace("\"", "");
		amount = amount.replace(",", "");
		description = description.replace("\"", "");
        User user = userRepository.findOneByLogin(login).get();
		Transaction t = new Transaction().user(user).amount(Double.valueOf(amount)).date(DateHelper.getZonedDateTime(date))
            .description(description).depositAccount(getAccount(user, depositName, currency))
            .withdrawAccount(getAccount(user, withdrawName, currency));
        transactionService.save(transactionMapper.toDto(t));
	}

	// Two levels only: This limitation has many side effects, changing it must
	// be tested fully.
    HashMap<String, UserAccount> accountCache = new HashMap<>();

	private UserAccount getAccount(User user, String accountNamePath, String currencyName) throws Exception {
		// get from hash
		UserAccount account = accountCache.get(accountNamePath);
		if (account != null) {
			return account;
		}
		// else get from DB
		String[] seg = accountNamePath.split(":");
		if (seg.length != 2) {
			throw new Exception("Parser Error: " + accountNamePath);
		}
		String accountName = seg[1];
        AccountType accountType = getAccountType(seg[0]);
		List<UserAccount> accounts = userAccountRepository
            .findByUser_LoginAndTextAndType(user.getLogin(), accountName, accountType);
		if(accounts.size() > 1) {
            throw new Exception("Duplicate accunt error, " + accounts);
        }
        if(accounts.size() == 1) {
            accountCache.put(accountNamePath, accounts.get(0));
            return accounts.get(0);
        }
        //create one account, cache it and return it
        //if (accounts.isEmpty()) {
        Currency currency = getCurrency(user, currencyName);
        UserAccount newAccount = new UserAccount()
            .user(user).text(accountName).type(accountType).currency(currency);
        userAccountRepository.save(newAccount);
        accountCache.put(accountNamePath, newAccount);

		return newAccount;
	}

    private Currency getCurrency(User user, String currencyName) throws Exception {
        List<Currency> currency = currencyRepository.findByNameAndUser_Login(currencyName, user.getLogin());
        if(currency.size() > 1) {
            throw new Exception(
                "Duplicate currency error" + currencyName);
        }
        if(currency.isEmpty()) {
            //create currency
            Currency newCurrency = new Currency();
            newCurrency.user(user).name(currencyName).conversionRate(1.0);
            currencyRepository.save(newCurrency);
            return newCurrency;
        }

        return currency.get(0);
    }

    private AccountType getAccountType(String accountTypeString) throws Exception {
        AccountType accountType;
        switch (accountTypeString) {
            case "Assets":
                accountType = AccountType.ASSET; // singular to match the database
                break;
            case "Expenses":
                accountType = AccountType.EXPENSE;
                break;
            case "Income":
                accountType = AccountType.INCOME;
                break;
            case "Liabilities":
                accountType = AccountType.LIABILITY;
                break;
            case "Other":
                accountType = AccountType.OTHER;
                break;
            default:
                throw new Exception(
                    "Parent Account must be one of 'Assets/Expenses/Income/Liability/Other', " + accountTypeString + " is invalid!");
        }
        return accountType;
    }

    @Transactional
	private void deleteAccountsAndTransactions(String login) throws Exception {
      transactionService.deleteAllByUser(login);
		  userAccountRepository.deleteByUser_Login(login);
	}
}
