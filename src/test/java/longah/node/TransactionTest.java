package longah.node;

import longah.util.MemberList;
import longah.util.TransactionList;
import longah.exception.LongAhException;
import longah.exception.ExceptionMessage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;


public class TransactionTest {
    /**
     * Helper method to remove a directory and its contents.
     * 
     * @param dir The file to be removed
     */
    public void deleteDir(File dir) {
        File[] contents = dir.listFiles();
        if (contents != null) {
            for (File file : contents) {
                deleteDir(file);
            }
        }
        dir.delete();
    }

    /**
     * Tests the successful creation of a transaction with balances correctly updated.
     */
    @Test
    public void transactionConstructor_transaction_success() {
        try {
            Group group = new Group("testGroup");
            MemberList memberList = group.getMemberList();
            TransactionList transactionList = group.getTransactionList();
            memberList.addMember("Alice");
            memberList.addMember("Bob");
            Transaction transaction = new Transaction("Alice p/Bob a/5", memberList);
            transactionList.addTransaction(transaction);
            group.updateTransactionSolution();
            Member lender = transaction.getLender();
            assertEquals("Alice", lender.getName());
            assertEquals(5.0, lender.getBalance());
            Member borrower = memberList.getMember("Bob");
            assertEquals(-5.0, borrower.getBalance());
            deleteDir(new File("./data/testGroup"));
        } catch (LongAhException e) {
            fail();
        }
    }

    /**
     * Tests the unsuccessful creation of a transaction with < 2 persons involved.
     */
    @Test
    public void transactionConstructor_invalidFormat_exceptionThrown() {
        try {
            MemberList memberList = new MemberList();
            memberList.addMember("Alice");
            memberList.addMember("Bob");
            new Transaction("Alice a/5", memberList);
            fail();
        } catch (LongAhException e) {
            String expectedString = ExceptionMessage.INVALID_TRANSACTION_FORMAT.getMessage();
            assertEquals(expectedString, e.getMessage());
        }
    }

    @Test
    public void transactionConstructor_overflowAmount_exceptionThrown() {
        try {
            Group group = new Group("testGroup");
            MemberList memberList = group.getMemberList();
            TransactionList transactionList = group.getTransactionList();
            memberList.addMember("Alice");
            memberList.addMember("Bob");
            transactionList.addTransaction("Alice p/Bob a/1e309", memberList, group);
            fail();
        } catch (LongAhException e) {
            String expectedString = ExceptionMessage.BALANCE_OVERFLOW.getMessage();
            assertEquals(expectedString, e.getMessage());
            deleteDir(new File("./data/testGroup"));
        }
    }

    /**
     * Tests the unsuccessful creation of a transaction with invalid Date Time format.
     */
    @Test
    public void addTransactionTime_invalidTimeFormat_exceptionThrown() {
        try {
            MemberList memberList = new MemberList();
            memberList.addMember("Jack");
            memberList.addMember("Jane");
            new Transaction("Jack t/2359 p/Jane a/200", memberList);
            fail();
        } catch (LongAhException e) {
            String expected = ExceptionMessage.INVALID_TIME_FORMAT.getMessage();
            assertEquals(expected, e.getMessage());
        }
    }

    /**
     * Tests the unsuccessful creation of a transaction with an invalid command to denote amount.
     */
    @Test
    public void addBorrower_invalidAmountCommand_exceptionThrown() {
        try {
            MemberList memberList = new MemberList();
            memberList.addMember("Alice");
            memberList.addMember("Bob");
            Member lender = memberList.getMember("Alice");
            Transaction transaction = new Transaction("Alice p/Bob a/5", memberList);
            transaction.addBorrower("Bob b/5", memberList, lender);
            fail();
        } catch (LongAhException e) {
            String expectedString = ExceptionMessage.INVALID_TRANSACTION_FORMAT.getMessage();
            assertEquals(expectedString, e.getMessage());
        }
    }

    /**
     * Tests the unsuccessful creation of a transaction with an amount value specified in words
     * rather than a double value.
     */
    @Test
    public void addBorrower_invalidAmountValue_exceptionThrown() {
        try {
            MemberList memberList = new MemberList();
            memberList.addMember("Alice");
            memberList.addMember("Bob");
            Member lender = memberList.getMember("Alice");
            Transaction transaction = new Transaction("Alice p/Bob a/5", memberList);
            transaction.addBorrower("Bob a/five", memberList, lender);
            fail();
        } catch (LongAhException e) {
            String expectedString = ExceptionMessage.INVALID_TRANSACTION_VALUE.getMessage();
            assertEquals(expectedString, e.getMessage());
        }
    }

    /**
     * Tests the unsuccessful creation of a transaction with a negative amount
     * value for person that owes.
     */
    @Test
    public void addBorrower_negativeAmountValue_exceptionThrown() {
        try {
            MemberList memberList = new MemberList();
            memberList.addMember("Alice");
            memberList.addMember("Bob");
            Member lender = memberList.getMember("Alice");
            Transaction transaction = new Transaction("Alice p/Bob a/5", memberList);
            transaction.addBorrower("Bob a/-5", memberList, lender);
            fail();
        } catch (LongAhException e) {
            String expectedString = ExceptionMessage.INVALID_TRANSACTION_VALUE.getMessage();
            assertEquals(expectedString, e.getMessage());
        }
    }

}
