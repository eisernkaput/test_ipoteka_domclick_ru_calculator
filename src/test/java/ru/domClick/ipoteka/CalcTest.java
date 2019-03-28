package ru.domClick.ipoteka;

import org.junit.*;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.stqa.selenium.factory.WebDriverPool;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CalcTest {


    private WebElement element;


    @AfterAll
    public static void stopAllBrowsers() {
        WebDriverPool.DEFAULT.dismissAll();
    }


    @Test
    public void testFirefox() throws InterruptedException {
        doSomething(WebDriverPool.DEFAULT.getDriver(new FirefoxOptions()));
    }

    @Test
    public void testInternetExplorer() throws InterruptedException {
        doSomething(WebDriverPool.DEFAULT.getDriver(new InternetExplorerOptions()));
    }

    @Test
    public void testChrome() throws InterruptedException {
        doSomething(WebDriverPool.DEFAULT.getDriver(new ChromeOptions()));
    }


    private void doSomething(WebDriver driver) throws InterruptedException {

        driver.get("https://ipoteka.domclick.ru/");
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        String title = driver.getTitle();
        Assert.assertTrue(title.equals("Домклик - Ипотека от Сбербанка Онлайн"));
        driver.manage().deleteAllCookies();
        driver.navigate().refresh();


        element = driver.findElement(By.xpath("//nav[@class='shell_modeSwitcherNav']/a[2]"));
        element.click();

        element = driver.findElement(By.xpath("//label[@class='dcCalc_textfield dcCalc_textfield_size_large dcCalc_textfield_fluid dcCalc_textfield_status_idle dcCalc_textfield_suffixIcon']"));
        element.click();
        Thread.sleep(5000);

        MortgageCalculator calculator = new MortgageCalculator();

        int[] requestData = calculator.expectedPerMonthRateCalculator();


        int credType = requestData[0];

        WebDriverWait wait = new WebDriverWait(driver, 10);


        List<WebElement> listOfOptions = driver.findElements(By.cssSelector("div[role$='option']"));

        Thread.sleep(5000);


        switch (credType) {

            case 0:
                element = listOfOptions.get(0);
                break;
            case 1:
                element = listOfOptions.get(1);
                break;
            case 2:
                element = listOfOptions.get(2);
                break;
            case 3:
                element = listOfOptions.get(3);
                break;
            case 4:
                element = listOfOptions.get(4);
                break;
            case 5:
                element = listOfOptions.get(5);
                break;
            case 6:
                element = listOfOptions.get(6);
                break;
            case 7:
                element = listOfOptions.get(7);
                break;
            case 8:
                element = listOfOptions.get(8);
                break;
        }
        Actions actions = new Actions(driver);
        actions.moveToElement(wait.until(ExpectedConditions.elementToBeClickable(element))).perform();
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        Thread.sleep(5000);


        String realtyValue = String.valueOf(requestData[2]);
        String sumOfCredit = String.valueOf(requestData[5]);

        if (credType == 4) {
            textFieldFiller(driver, wait, "purposeLoanCost", realtyValue);
            textFieldFiller(driver, wait, "purposeLoanAmountOfCredit", sumOfCredit);

        } else if (credType == 5) {
            textFieldFiller(driver, wait, "refinCost", realtyValue);
            textFieldFiller(driver, wait, "refinOwed", sumOfCredit);
        } else {
            textFieldFiller(driver, wait, "estateCost", realtyValue);

            String initialFee = String.valueOf(requestData[3]);
            textFieldFiller(driver, wait, "initialFee", initialFee);
        }

        String creditTerm = String.valueOf(requestData[4]);

        if (credType == 5) {
            textFieldFiller(driver, wait, "refinTerm", creditTerm);
        } else {
            textFieldFiller(driver, wait, "creditTerm", creditTerm);
        }

        //Скидки  (кроме военн. ипотеки и с гос.поддержк.)

        //Скидка по страх. жизни
        if (credType <= 5 || credType == 8) {
            String lifeInsuranceDiscount = "//input[@data-test-id='lifeInsurance']";
            discountClicker(driver, actions, lifeInsuranceDiscount, requestData[8]);
        }


        //Скидка по наличию з/п карты  (кроме рефинанс)
        if (credType <= 4 || credType == 8) {
            int salaryCardDiscount = requestData[6];
            element = driver.findElement(By.xpath("//input[@data-test-id='paidToCard']"));
            if (!element.isSelected() && salaryCardDiscount == 0) {
                actions.moveToElement(element).click().perform();
                Thread.sleep(2000);
            } else if (element.isSelected() && salaryCardDiscount == 1) {
                actions.moveToElement(element).click().perform();
                Thread.sleep(2000);
                String incomeConfirmDiscount = "//input[@data-test-id='canConfirmIncome']";
                discountClicker(driver, actions, incomeConfirmDiscount, requestData[7]);

            }
        }


        //Условия по кред. на гот. жилье и новостройку
        //Скидка от домклик и для молодой семьи по гот.жилью
        if (credType == 0) {
            String domClickDiscount = "//input[@data-test-id='realtyDiscount']";
            discountClicker(driver, actions, domClickDiscount, requestData[9]);
            String youngFamilyDiscount = "//input[@data-test-id='youngFamilyDiscount']";
            discountClicker(driver, actions, youngFamilyDiscount, requestData[10]);
        }


        //Скидка застройщика (если новостройка+срок кредита менее 12 лет)
        if (credType == 1) {
            String developerDiscount = "//input[@data-test-id='developerDiscount']";
            discountClicker(driver, actions, developerDiscount, requestData[11]);
        }

        //Скидка по электроннной регистрации
        if (credType == 0 || credType == 1) {
            String regDiscount = "//input[@data-test-id='onRegDiscount']";
            discountClicker(driver, actions, regDiscount, requestData[12]);
        }
        Thread.sleep(5000);

        String monthlyPayment;

        if (credType == 6) {
            driver.findElement(By.xpath("//div[@class='dcCalc_calcResults_paymentListBtnWrap'][1]")).click();
            Thread.sleep(5000);
            monthlyPayment = driver.findElement(By.xpath("//div[@class='dcCalc_paymentList_mainListWrap']/div[1]/div[2]")).getText().replaceAll("[^0-9]", "");
        } else {
            monthlyPayment = driver.findElement(By.xpath("//span[@data-test-id='monthlyPayment']")).getText().replaceAll("[^0-9]", "");
            Thread.sleep(5000);
        }


        int actualMonthlyPayment = Integer.valueOf(monthlyPayment);

        int expectedMonthlyPayment = requestData[1];
        System.out.println(expectedMonthlyPayment + "   " + actualMonthlyPayment);

        Assert.assertEquals(expectedMonthlyPayment, actualMonthlyPayment);

    }

    private void discountClicker(WebDriver driver, Actions actions, String locator, int requestData) throws InterruptedException {
        element = driver.findElement(By.xpath(locator));
        if ((element.isSelected() && requestData == 1) || (!element.isSelected() && requestData == 0)) {
            actions.moveToElement(element).click().perform();
            Thread.sleep(2000);
        }
    }

    private void textFieldFiller(WebDriver driver, WebDriverWait wait, String locator, String argument) throws InterruptedException {
        element = driver.findElement(By.id(locator));
        wait.until(ExpectedConditions.elementToBeClickable(element)).clear();
        wait.until(ExpectedConditions.elementToBeClickable(element)).sendKeys(argument);
        Thread.sleep(2000);
    }


}

