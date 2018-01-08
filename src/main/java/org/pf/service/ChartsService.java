package org.pf.service;

import org.pf.domain.Transaction;
import org.pf.domain.UserAccount;
import org.pf.domain.enumeration.AccountType;
import org.pf.repository.TransactionRepository;
import org.pf.repository.UserAccountRepository;
import org.pf.service.dto.TransactionDTO;
import org.pf.service.mapper.TransactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.text.DateFormatSymbols;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service Implementation for managing Charts.
 */
@Service
@Transactional
public class ChartsService {

    private static final String MONTH = "Month";
    private static final String TOTAL = "Total";
    private static final String CAT_INCOME = "INCOME";
    private static final String CAT_LIABILITY = "LIABILITY";
    private static final String CAT_EXPENSE = "EXPENSE";
    private static final String CAT_ASSET = "ASSET";
    private static final String CAT_OTHER = "OTHER";

    private TransactionService transactionService;
    private UserAccountRepository userAccountRepository;
    private TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public ChartsService(TransactionService transactionService, UserAccountRepository userAccountRepository,
        TransactionRepository transactionRepository,
        TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.userAccountRepository = userAccountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    public String getTransactionsTrendHtml(String year, String type, @NotNull String login) {
        List<Map<String, Object>> out2;
        if(year == null || year.isEmpty() || year.equals(" ")) {
            out2 = getTrendDataForAllYears(login) ;
        } else {
            out2 = getTrendData(login, year, type);
        }
        return convertToHtml(out2);
    }

    private String convertToHtml(List<Map<String, Object>> data) {
        boolean headerRendered = false;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<table border=\"1\">");
        for(Map<String, Object> map : data) {
            Set<String> keys = map.keySet();
            if( ! headerRendered) {
                headerRendered = true;
                convertHeaderHtml(stringBuilder, keys);
            }
            convertRowHtml(stringBuilder, map, keys);
        }
        stringBuilder.append("</table>");
        return stringBuilder.toString();
    }

    private void convertHeaderHtml(StringBuilder stringBuilder, Set<String> keys) {
        stringBuilder.append("<tr>");
        for(String key : keys) {
            stringBuilder.append("<td>").append(key).append("</td>");
        }
        stringBuilder.append("</tr>\r\n");
    }

    private void convertRowHtml(StringBuilder stringBuilder, Map<String, Object> map, Set<String> keys) {
        stringBuilder.append("<tr>");
        for(String key : keys) {
            Object value = map.get(key);
            String strValue;
            if(value instanceof Double) {
                strValue = org.pf.service.impl.TransactionServiceImpl.formatMoney((Double)value);
            } else {
                strValue = value.toString();
            }
            stringBuilder.append("<td>").append(strValue).append("</td>");
        }
        stringBuilder.append("</tr>\r\n");
    }

    private List<Map<String,Object>> getTrendDataForAllYears(String login) {
        Map<String, Map<String, Object>> out = getTotalsAllYearsAllAccountTypes(login);
        List<Map<String, Object>> out2 = new ArrayList<>();
        //convert map to array for html/JS compatibility
        Set<String> keys = out.keySet();
        for(String key : keys) {
            out2.add(out.get(key));
        }
        return out2;
    }

    private Map<String, Map<String, Object>> getTotalsAllYearsAllAccountTypes(String login) {
        // Add All years Entries
        Map<String, Map<String, Object>> out = new HashMap<>();
        List<String> years = transactionService.getYearList(login);
        years.remove(0);
        for (String yearString : years) {
            HashMap<String, Object> year = new HashMap<>();
            year.put(MONTH, yearString);
            year.put(CAT_ASSET, 0.0);        //initialization
            year.put(CAT_LIABILITY, 0.0);    //initialization
            out.put(yearString, year);
        }

        getTotalsAllYears(login, years, CAT_EXPENSE, CAT_EXPENSE, out);
        getTotalsAllYears(login, years, CAT_INCOME, CAT_INCOME, out);
        getTotalsAllYears(login, years, CAT_OTHER, CAT_OTHER, out);
        getTotalsWithBalance(login, years, CAT_ASSET, CAT_ASSET, out);
        getTotalsWithBalance(login, years, CAT_LIABILITY, CAT_LIABILITY, out);
        return out;
    }

    private void getTotalsAllYears(String login,
        List<String> years, String type, String totalName,
        Map<String, Map<String, Object>> out) {
        List<UserAccount> expenseAccounts = userAccountRepository.findByUser_LoginAndTypeOrderByText
            (login, AccountType.valueOf(type));
        for (String year : years) {
            List<Transaction> t1 = transactionRepository.findByUserLoginAndDateBetween(login, DateHelper
                    .getStartDate(year),
                DateHelper.getEndDate(year));
            List<TransactionDTO> transactions = transactionMapper.toDto(t1);
            double total = 0;
            for (UserAccount account : expenseAccounts) {
                double balance = transactionService.computeBalance(account.getId(), account.getType(), 0, transactions);
                total += (balance * account.getCurrency().getConversionRate());
            }
            out.get(year).put(totalName, total);
        }
    }

    private void getTotalsWithBalance(String login,
        List<String> years, String type, String totalName,
        Map<String, Map<String, Object>> out) {
        List<UserAccount> accounts = userAccountRepository.findByUser_LoginAndTypeOrderByText(login, AccountType.valueOf(type));
        for (UserAccount account : accounts) {
            //Get all transactions with computed balance
            //for all years & one account
            List<TransactionDTO> transactions = transactionMapper.toDto(
                transactionRepository.findByUserLoginAndAccountId(login, account.getId()));
            transactionService.computeBalance(account.getId(), account.getType(), 0, transactions);
            for (String year : years) {
                double balance = fetchYearBalance(year, transactions);
                balance *= account.getCurrency().getConversionRate();
                double old = (Double) out.get(year).get(totalName);
                out.get(year).put(totalName, old + balance);
            }
        }
    }

    //1 based date is sent
    private double fetchYearBalance(String year, List<TransactionDTO> transactions) {
        String yearPlus = String.valueOf(Integer.parseInt(year) + 1);
        ZonedDateTime myDate = DateHelper.getZonedDateTime(Integer.parseInt(yearPlus));
        double balance = 0;
        for (TransactionDTO transaction : transactions) {
            if (transaction.getDate().isBefore(myDate)) {
                balance = transaction.getBalance();
            } else {
                break;
            }
        }
        return balance;
    }

    private List<Map<String, Object>> getTrendData(String login, String year,
        String type) {
        if (type == null || type.isEmpty()) {
            return getTrendDataTotals(login, year);
        }
        //Asset and Liabilities
        if (type.equals(CAT_ASSET) || type.equals(CAT_LIABILITY)) {
            return getTrendDataBalances(login, year, type);
        }
        //income and expenses
        return getDataSummation(login, year, type);
    }

    private List<Map<String, Object>> getTrendDataTotals(String login, String year) {
        // Add Months Entries
        List<Map<String, Object>> out = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            HashMap<String, Object> month = new HashMap<>();
            month.put(MONTH, getMonthName(i + 1));
            month.put(CAT_ASSET, 0.0);        //initialization
            month.put(CAT_LIABILITY, 0.0);    //initialization
            out.add(month);
        }

        getTotalSummation(login, year, AccountType.EXPENSE, CAT_EXPENSE, out);
        getTotalSummation(login, year, AccountType.INCOME, CAT_INCOME, out);
        getTotalSummation(login, year, AccountType.OTHER, CAT_OTHER, out);
        getTotalsWithBalanceForSingleYear(login, year, AccountType.ASSET, CAT_ASSET, out);
        getTotalsWithBalanceForSingleYear(login, year, AccountType.LIABILITY, CAT_LIABILITY, out);
        return out;
    }

    private String getMonthName(int month) {
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        String monthName = months[month - 1];
        return monthName.substring(0, 3); //return first three letters
    }

    private void getTotalSummation(String login,
        String year, AccountType type, String totalName,
        List<Map<String, Object>> out) {
        List<UserAccount> accounts = userAccountRepository.findByUser_LoginAndTypeOrderByText(login, type);
        for (int monthIndex = 1; monthIndex <= 12; monthIndex++) {
            List<TransactionDTO> transactions = transactionMapper.toDto(transactionRepository.findByUserLoginAndDateBetween(login,
                DateHelper.getStartDate(year, String.valueOf(monthIndex)), DateHelper
                    .getEndDate(year, String.valueOf(monthIndex))));

            double total = 0;
            for (UserAccount account : accounts) {
                double balance = transactionService.computeBalance(account.getId(), account.getType(), 0, transactions);
                total += (balance * account.getCurrency().getConversionRate());
            }
            out.get(monthIndex - 1).put(totalName, total);
        }
    }

    //In Assets and Liabilities, we must compute balance from the very start up to the target month
    //So the looping should be based on complete Account history
    private List<Map<String, Object>> getTrendDataBalances(String login, String year,
        String type) {
        List<Map<String, Object>> balanceData = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            HashMap<String, Object> month = new HashMap<>();
            month.put(MONTH, getMonthName(i + 1));
            month.put(TOTAL, 0.0);
            balanceData.add(month);
        }
        //determine interval, smallest and largest date
        List<UserAccount> accounts = userAccountRepository.findByUser_LoginAndTypeOrderByText(login, AccountType.valueOf(type));
        for (UserAccount account : accounts) {
            //Get all transactions with computed balance
            List<TransactionDTO> transactions = transactionMapper.toDto(
                transactionRepository.findByUserLoginAndAccountId(login, account.getId()));
            transactionService.computeBalance(account.getId(), account.getType(), 0, transactions);
            for (int month = 0; month <= 11; month++) {
                double monthBal = fetchMonthBalance(year, String.valueOf(month + 1), transactions);
                monthBal *= account.getCurrency().getConversionRate();
                Map<String, Object> map = balanceData.get(month);
                map.put(account.getText(), monthBal);
                Double total = (Double) map.get(TOTAL);
                map.put(TOTAL, total + monthBal);
            }
        }

        return balanceData;
    }

    //1 based date is sent
    private double fetchMonthBalance(String year, String month, List<TransactionDTO> ts) {
        ZonedDateTime myDate = DateHelper.getEndDate(year, month);
        double balance = 0;
        for (TransactionDTO t : ts) {
            if (t.getDate().isBefore(myDate)) {
                balance = t.getBalance();
            } else {
                break;
            }
        }
        return balance;
    }

    //PRIVATE
    //Income & Expenses
    private List<Map<String, Object>> getDataSummation(String login, String year, String type) {
        List<Map<String, Object>> out = new ArrayList<>();
        //for all months
        List<UserAccount> accs = userAccountRepository.findByUser_LoginAndTypeOrderByText(login, AccountType.valueOf(type));
        for (int month = 1; month <= 12; month++) {
            //get month transaction
            //List<TransactionDTO> trans = transactionService.getYearMonthTransactions(login, year, month); //for all accounts
            List<TransactionDTO> trans = transactionMapper.toDto(transactionRepository.findByUserLoginAndDateBetween(login,
                DateHelper.getStartDate(year, String.valueOf(month)), DateHelper
                    .getEndDate(year, String.valueOf(month))));
            //for all accounts
            HashMap<String, Object> map = new HashMap<>();
            map.put(MONTH, getMonthName(month));
            double monthTotal = 0;
            for (UserAccount a : accs) {
                double balance = transactionService.computeBalance(a.getId(), a.getType(), 0, trans);
                double rate = a.getCurrency().getConversionRate();
                balance *= rate;
                map.put(a.getText(), balance);
                monthTotal += balance;
            }
            map.put(TOTAL, monthTotal);
            out.add(map);
        }
        return out;
    }

    private void getTotalsWithBalanceForSingleYear(String login,
        String year, AccountType type, String totalName, List<Map<String, Object>> out) {

        List<UserAccount> accs = userAccountRepository.findByUser_LoginAndTypeOrderByText(login, type);
        for (UserAccount account : accs) {
            //Get all transactions with computed balance
            List<TransactionDTO> ts = transactionMapper.toDto(
                transactionRepository.findByUserLoginAndAccountId(login, account.getId()));
            transactionService.computeBalance(account.getId(), account.getType(), 0, ts);
            for (int month = 0; month <= 11; month++) {
                double balance = fetchMonthBalance(year, String.valueOf(month + 1), ts);
                balance *= account.getCurrency().getConversionRate();
                double old = (Double) out.get(month).get(totalName);
                out.get(month).put(totalName, old + balance);
            }
        }
    }

}
