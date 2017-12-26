package org.pf.service;

import org.pf.domain.Transaction;
import org.pf.domain.UserAccount;
import org.pf.domain.enumeration.AccountType;
import org.pf.repository.TransactionRepository;
import org.pf.repository.UserAccountRepository;
import org.pf.service.dto.TransactionDTO;
import org.pf.service.mapper.TransactionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.text.DateFormatSymbols;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    private final Logger log = LoggerFactory.getLogger(ChartsService.class);

    static public final String CAT_INCOME = "Income";
    static public final String CAT_LIABILITIES = "Liabilities";
    static public final String CAT_EXPENSES = "Expenses";
    static public final String CAT_ASSETS = "Assets";
    static public final String CAT_OTHER = "Other";

    public static final String YYYY_MM_DD_HH_MM_SS_S = "yyyy-MM-dd HH:mm:ss.S";
    public static final String END_OF_MONTH_STRING = "-12-31 23:59:59.0";

    final String TOTALS = "totals";

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

    public String getTransactionsTrendHtml(@Nullable String year, @NotNull String type, @NotNull String login) throws Exception {
        List<Map<String, Object>> out2;
        if(year == null) {
            out2 = getExpensesTrendForAllYears(login) ;
        } else {
            out2 = getExpensesTrend(year, type, login);
        }
        String string = convertToHtml(year, type, login, out2);
        return string;
    }

    private String convertToHtml(String year, String type, String login, List<Map<String, Object>> out2) {
        boolean headerRendered = false;
        StringBuffer stringBuffer = new StringBuffer();
        if(year == null) year = "all";
        stringBuffer.append("<h1>email: "+ login + ", type: " + type + ", for year: " + year + "</h1>\r\n");
        stringBuffer.append("<table border=\"1\">");
        for(Map<String, Object> map : out2) {
            Set<String> keys = map.keySet();
            if( ! headerRendered) {
                headerRendered = true;
                convertHeaderHtml(stringBuffer, keys);
            }
            convertRowHtml(stringBuffer, map, keys);
        }
        stringBuffer.append("</table>");
        return stringBuffer.toString();
    }

    private void convertHeaderHtml(StringBuffer stringBuffer, Set<String> keys) {
        stringBuffer.append("<tr>");
        for(String key : keys) {
            stringBuffer.append("<td>" + key + "</td>");
        }
        stringBuffer.append("</tr>\r\n");
    }

    private void convertRowHtml(StringBuffer stringBuffer, Map<String, Object> map, Set<String> keys) {
        stringBuffer.append("<tr>");
        for(String key : keys) {
            stringBuffer.append("<td>" + map.get(key) + "</td>");
        }
        stringBuffer.append("</tr>\r\n");
    }

    private List<Map<String,Object>> getExpensesTrendForAllYears(String login) throws Exception {
        List<Map<String, Object>> out2 = new ArrayList<>();
        Map<String, Map<String, Object>> out = getTrendDataAllYears(login); // type is empty
        //convert map to array for html/JS compatibility
        Set<String> keys = out.keySet();
        for(String key : keys) {
            out2.add(out.get(key));
        }
        return out2;
    }

    private List<Map<String,Object>> getExpensesTrend(String year, String type, String login) throws Exception {

        List<Map<String, Object>> out2 = new ArrayList<>();
        if("".equals(year)) { //NOT WORKING YET
            Map<String, Map<String, Object>> out = getTrendDataAllYears(login); // type is empty
            //convert map to array for html/JS compatibility
            Set<String> keys = out.keySet();
            for(String key : keys) {
                out2.add(out.get(key));
            }
        } else {
            AccountType accountType = AccountType.valueOf(type);
            out2 = getTrendData(login, year, accountType);
        }
        return out2;
    }

    private Map<String, Map<String, Object>> getTrendDataAllYears(String login) throws Exception {

//        usdRate = userRepo.findByEmail(email).getUsd_rate();
//        sarRate = userRepo.findByEmail(email).getSar_rate();
        return getTotalsAllYearsAllAccountTypes(login);
    }

    private Map<String, Map<String, Object>> getTotalsAllYearsAllAccountTypes(String login) throws Exception {
        // Add All years Entries
        //        List<Map<String, Object>> out = new ArrayList<Map<String, Object>>();
        Map<String, Map<String, Object>> out = new HashMap<>();
        List<String> years = transactionService.getYearList(login);
        //FIXME: I have to remove first " " as it is not a year actually.
        years.remove(0);
        for (String yearString : years) {
            HashMap<String, Object> year = new HashMap<>();
            year.put("Month", yearString);
            year.put(CAT_ASSETS, 0.0);        //initialization
            year.put(CAT_LIABILITIES, 0.0);    //initialization
            out.put(yearString, year);
        }

        //      //Expenses
        AccountType accountType = AccountType.EXPENSE;
        String totalName = CAT_EXPENSES;
        getTotalsAllYears(login, years, accountType, totalName, out);

        //        //Income
        accountType = AccountType.INCOME;
        totalName = CAT_INCOME;
        getTotalsAllYears(login, years, accountType, totalName, out);

        //        //Asset Balances
        accountType = AccountType.ASSET;
        totalName = CAT_ASSETS;
        getTotalsWithBalance(login, years, accountType, totalName, out);

        //        //Liabilities Balances
        accountType = AccountType.LIABILITY;
        totalName = CAT_LIABILITIES;
        getTotalsWithBalance(login, years, accountType, totalName, out);
        return out;
    }

    private void getTotalsAllYears(String login,
        List<String> years, AccountType accountType, String totalName,
        Map<String, Map<String, Object>> out) throws Exception {

        List<UserAccount> expenseAccounts = userAccountRepository.findByUser_LoginAndTypeOrderByText
            (login, accountType);

        for (String year : years) {
            List<Transaction> t1 = transactionRepository.findByUserLoginAndDateBetween(login, getStartDate(year),
                getEndDate(year));
            List<TransactionDTO> transactions = transactionMapper.toDto(t1);
            double total = 0;
            for (UserAccount account : expenseAccounts) {
                double balance = transactionService.computeBalance(account.getId(), account.getType(), 0, transactions);
//                total += (balance * getExchangeRate(account.getCurrency()));
                total += (balance * account.getCurrency().getConversionRate());
            }
            out.get(year).put(totalName, total);
        }
    }

    //TODO: extract into DateUtils
    private ZonedDateTime getStartDate(String year) {
        return ZonedDateTime.parse(year + "-01-01 00:00:00.0", DateTimeFormatter.ofPattern(
            YYYY_MM_DD_HH_MM_SS_S).withZone(ZoneId.systemDefault()));
    }

    private ZonedDateTime getEndDate(String year) {
        return ZonedDateTime.parse(year + END_OF_MONTH_STRING, DateTimeFormatter.ofPattern(
            YYYY_MM_DD_HH_MM_SS_S).withZone(ZoneId.systemDefault()));
    }

    private ZonedDateTime getStartDate(String year, String month) {
        String dateString = String.format("%s-%02d-01 00:00:00.0", year, Integer.parseInt(month));
        return ZonedDateTime.parse(dateString, DateTimeFormatter.ofPattern(
            YYYY_MM_DD_HH_MM_SS_S).withZone(ZoneId.systemDefault()));
    }

    private ZonedDateTime getEndDate(String year, String month) {
        return ZonedDateTime.parse(String.format("%s-%02d-31 23:59:59.0", year, Integer.parseInt(month)), DateTimeFormatter.ofPattern(
            YYYY_MM_DD_HH_MM_SS_S).withZone(ZoneId.systemDefault()));
    }

    private void getTotalsWithBalance(String login,
        List<String> years, AccountType type, String totalName,
        Map<String, Map<String, Object>> out) throws Exception {

        List<UserAccount> accounts = userAccountRepository.findByUser_LoginAndTypeOrderByText(login, type);
        for (UserAccount account : accounts) {
            //Get all transactions with computed balance
//            List<TransactionDTO> transactions = transactionService.getTransactions(login, account.getId()); //for all years & one account
            List<TransactionDTO> transactions = transactionMapper.toDto(
                transactionRepository.findByUserLoginAndAccountId(login, account.getId()));
            for (String year : years) {
                double balance = fetchYearBalance(year, transactions);
                balance *= account.getCurrency().getConversionRate();
                double old = (Double) out.get(year).get(totalName);
                out.get(year).put(totalName, old + balance);
            }
        }
    }

    //1 based date is sent
    private double fetchYearBalance(String year, List<TransactionDTO> transactions) throws Exception {
//      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String yearPlus = String.valueOf(Integer.parseInt(year) + 1);
//      Date myDate = sdf.parse(yearPlus + "-01" + "-01"); //the first invalid date
        ZonedDateTime myDate = getZonedDateTime(Integer.parseInt(yearPlus));
        double balance = 0;
        for (TransactionDTO transaction : transactions) {
//            Date transDate = sdf.parse(transaction.getDate());
            if (transaction.getDate().isBefore(myDate)) {
                balance = transaction.getBalance();
            } else {
                break;
            }
        }
        return balance;
    }

    private ZonedDateTime getZonedDateTime(Integer year) {
        return ZonedDateTime.parse(year + "-01-01 00:00:00.0", DateTimeFormatter.ofPattern(
                  YYYY_MM_DD_HH_MM_SS_S).withZone(ZoneId.systemDefault()));
    }

    private List<Map<String, Object>> getTrendData(String login, String year,
        AccountType type) throws Exception {

//        usdRate = userRepo.findByEmail(email).getUsd_rate();
//        sarRate = userRepo.findByEmail(email).getSar_rate();

        if (type.equals("totals")) {
            return getTrendDataTotals(login, year);
        }
        //Asset and Liabilities
        if (type.equals(AccountType.ASSET) || type.equals(AccountType.LIABILITY)) {
            return getTrendDataBalances(login, year, type);
        }
        //income and expenses
        return getDataSummation(login, year, type);
    }


    private List<Map<String, Object>> getTrendDataTotals(String login, String year) throws Exception {
        // Add Months Entries
        List<Map<String, Object>> out = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            HashMap<String, Object> month = new HashMap<>();
            month.put("Month", getMonthName(i + 1));
            month.put(CAT_ASSETS, 0.0);        //initialization
            month.put(CAT_LIABILITIES, 0.0);    //initialization
            out.add(month);
        }

        //Expenses
        AccountType type = AccountType.EXPENSE;
        String totalName = CAT_EXPENSES;
        getTotalSummation(login, year, type, totalName, out);

        //Income
        type = AccountType.INCOME;
        totalName = CAT_INCOME;
        getTotalSummation(login, year, type, totalName, out);

        //Asset Balances
        type = AccountType.ASSET;
        totalName = CAT_ASSETS;
        getTotalsWithBalanceForSingleYear(login, year, type, totalName, out);

        //Liabilities Balances
        type = AccountType.LIABILITY;
        totalName = CAT_LIABILITIES;
        getTotalsWithBalanceForSingleYear(login, year, type, totalName, out);
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
        List<Map<String, Object>> out) throws Exception {
        List<UserAccount> accounts = userAccountRepository.findByUser_LoginAndTypeOrderByText(login, type);
        for (int monthIndex = 1; monthIndex <= 12; monthIndex++) {
//            List<TransactionDTO> transactions = transactionService.getYearMonthTransactions(login, year, monthIndex); //for all accounts
            List<TransactionDTO> transactions = transactionMapper.toDto(transactionRepository.findByUserLoginAndDateBetween(login,
                getStartDate(year, String.valueOf(monthIndex)), getEndDate(year, String.valueOf(monthIndex))));

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
        AccountType type) throws Exception {
        List<Map<String, Object>> balanceData = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            HashMap<String, Object> month = new HashMap<>();
            month.put("Month", getMonthName(i + 1));
            month.put("Total", 0.0);
            balanceData.add(month);
        }
        //determine interval, smallest and largest date
        List<UserAccount> accounts = userAccountRepository.findByUser_LoginAndTypeOrderByText(login, type);
        for (UserAccount account : accounts) {
            //Get all transactions with computed balance
//            List<TransactionDTO> transactions = transactionService.getTransactions(login, account.getId());
            List<TransactionDTO> transactions = transactionMapper.toDto(
                transactionRepository.findByUserLoginAndAccountId(login, account.getId()));
            transactionService.computeBalance(account.getId(), account.getType(), 0, transactions);
            for (int month = 0; month <= 11; month++) {
                double monthBal = fetchMonthBalance(year, String.valueOf(month + 1), transactions);
                monthBal *= account.getCurrency().getConversionRate();
                Map<String, Object> map = balanceData.get(month);
                map.put(account.getText(), monthBal);
                Double total = (Double) map.get("Total");
                map.put("Total", total + monthBal);
            }
        }

        return balanceData;
    }

    //1 based date is sent
    private double fetchMonthBalance(String year, String month, List<TransactionDTO> ts) throws Exception {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date myDate = sdf.parse(year + "-" + String.valueOf(month + 1) + "-01"); //the first invalid date
        ZonedDateTime myDate = getStartDate(year, month);
        double balance = 0;
        for (TransactionDTO t : ts) {
//            Date transDate = sdf.parse(t.getDate());
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
    private List<Map<String, Object>> getDataSummation(String login, String year, AccountType type)
        throws Exception {
        List<Map<String, Object>> out = new ArrayList<>();
        //for all months
        List<UserAccount> accs = userAccountRepository.findByUser_LoginAndTypeOrderByText(login, type);
        for (int month = 1; month <= 12; month++) {
            //get month transaction
            //List<TransactionDTO> trans = transactionService.getYearMonthTransactions(login, year, month); //for all accounts
            List<TransactionDTO> trans = transactionMapper.toDto(transactionRepository.findByUserLoginAndDateBetween(login,
                getStartDate(year, String.valueOf(month)), getEndDate(year, String.valueOf(month))));
            //for all accounts
            HashMap<String, Object> map = new HashMap<>();
            map.put("Month", getMonthName(month));
            double monthTotal = 0;
            for (UserAccount a : accs) {
                double balance = transactionService.computeBalance(a.getId(), a.getType(), 0, trans);
                double rate = a.getCurrency().getConversionRate();
                balance *= rate;
                map.put(a.getText(), balance);
                monthTotal += balance;
            }
            map.put("Total", monthTotal);
            out.add(map);
        }
        return out;
    }

    private void getTotalsWithBalanceForSingleYear(String login,
        String year, AccountType type, String totalName, List<Map<String, Object>> out) throws Exception {

        List<UserAccount> accs = userAccountRepository.findByUser_LoginAndTypeOrderByText(login, type);
        for (UserAccount account : accs) {
            //Get all transactions with computed balance
            //List<TransactionDTO> ts = transactionService.getTransactions(userId, account.getId()); //for all years & one account
            List<TransactionDTO> ts = transactionMapper.toDto(
                transactionRepository.findByUserLoginAndAccountId(login, account.getId()));
            for (int month = 0; month <= 11; month++) {
                double balance = fetchMonthBalance(year, String.valueOf(month + 1), ts);
                balance *= account.getCurrency().getConversionRate();
                double old = (Double) out.get(month).get(totalName);
                out.get(month).put(totalName, old + balance);
            }
        }
    }

}
