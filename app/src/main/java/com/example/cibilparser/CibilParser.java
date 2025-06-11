package com.example.cibilparser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LoanDetails {
    String memberName, accountType, highCredit, currentBalance, overdueAmount;

    LoanDetails(String memberName, String accountType, String highCredit, String currentBalance, String overdueAmount) {
        this.memberName = memberName;
        this.accountType = accountType;
        this.highCredit = highCredit;
        this.currentBalance = currentBalance;
        this.overdueAmount = overdueAmount;
    }
}

public class CibilParser {

    public static List<LoanDetails> detectAndParse(String pdfPath) throws Exception {
        String text = extractText(pdfPath);

        if (text.contains("MEMBER NAME:")) {
            return parseFormat2(text);
        } else {
            return parseFormat1(text);
        }
    }

    private static String extractText(String pdfPath) throws Exception {
        PDDocument document = PDDocument.load(new File(pdfPath));
        String text = new PDFTextStripper().getText(document);
        document.close();
        return text;
    }

    private static List<LoanDetails> parseFormat1(String text) {
        List<LoanDetails> loans = new ArrayList<>();
        Pattern pattern = Pattern.compile("([\\w\\s]+)\\s+([\\w\\s-]+)\\s+₹([\\d,]+)\\s+₹([\\d,]+)\\s+₹([\\d,]+)?");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            loans.add(new LoanDetails(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5)));
        }
        return loans;
    }

    private static List<LoanDetails> parseFormat2(String text) {
        List<LoanDetails> loans = new ArrayList<>();
        Pattern pattern = Pattern.compile("MEMBER NAME:\\s*([\\w\\s]+).*?TYPE:\\s*([\\w\\s]+).*?HIGH CREDIT:\\s*₹([\\d,]+).*?CURRENT BALANCE:\\s*₹([\\d,]+).*?OVERDUE AMOUNT:\\s*₹([\\d,]+)?", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            loans.add(new LoanDetails(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5)));
        }
        return loans;
    }

    public static void saveToCsv(String csvPath, List<LoanDetails> loans) throws Exception {
        FileWriter writer = new FileWriter(csvPath);
        writer.write("Member Name,Account Type,High Credit (₹),Current Balance (₹),Overdue Amount (₹)\n");
        for (LoanDetails loan : loans) {
            writer.write(String.format("%s,%s,%s,%s,%s\n",
                    loan.memberName,
                    loan.accountType,
                    loan.highCredit,
                    loan.currentBalance,
                    loan.overdueAmount == null ? "N/A" : loan.overdueAmount));
        }
        writer.close();
    }
}
