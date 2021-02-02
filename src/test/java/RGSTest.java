import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RGSTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void before(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        System.setProperty("webdriver.chrome.driver", "C:/Program Files/Java/chromedriver.exe");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);

        wait = new WebDriverWait(driver, 10, 1000);

        String baseURL = "https://www.rgs.ru/";
        driver.get(baseURL);
    }

    @Test
    public void test() throws InterruptedException {
        //Открываем меню
        String menuButtonXPath = "//a[@class='hidden-xs']";
        List<WebElement> menuButton = driver.findElements(By.xpath(menuButtonXPath));
        if (!menuButton.isEmpty()){
            waitUtilElementToBeClickable(menuButton.get(0));
            menuButton.get(0).click();
        }

        //Выбираем раздел "Компаниям"
        String forCompaniesButtonXpath = "//a[contains(text(),'Компаниям')]";
        WebElement forCompaniesButton = driver.findElement((By.xpath(forCompaniesButtonXpath)));
        waitUtilElementToBeClickable(forCompaniesButton);
        forCompaniesButton.click();

        //Переходим в раздел "Здоровье"
        String healthButtonXPath = "//a[@href='/products/juristic_person/health/index.wbp' and contains(text(),'Здоровье')]";
        WebElement healthButton = driver.findElement((By.xpath(healthButtonXPath)));
        waitUtilElementToBeClickable(healthButton);
        healthButton.click();

        //Переходим в раздел Добровольное медицинское страхование"
        String dmsButtonXPath = "//a[contains(text(),'Добровольное медицинское страхование')]";
        WebElement dmsButton = driver.findElement((By.xpath(dmsButtonXPath)));
        waitUtilElementToBeClickable(dmsButton);
        dmsButton.click();

        //Нажать кнопку "Отправить заявку"
        String sendApplicationButtonXPath = "//a[contains(text(),'Отправить заявку')]";
        WebElement sendApplicationButton = driver.findElement((By.xpath(sendApplicationButtonXPath)));
        waitUtilElementToBeClickable(sendApplicationButton);
        sendApplicationButton.click();

        //Проверить, открыта ли страница, на которой присутствует теуст "Заявка на добровольное медицинское страхование"
        String pageTitleXPath = "//h4";
        waitUtilElementToBeVisible(By.xpath(pageTitleXPath));
        WebElement pageTitle = driver.findElement(By.xpath(pageTitleXPath));
        Assert.assertEquals("Заголовок отсутствует/не соответствует требуемому",
                "Заявка на добровольное медицинское страхование", pageTitle.getText());

        //Заполнить поля
        String fieldXPath = "//input[@name='%s']";
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath,"LastName"))),"Минниханова");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath,"FirstName"))),"Диляра");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath,"MiddleName"))),"Илдаровна");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath,"Email"))),"qwertyqwerty");
        WebElement regionSelector= driver.findElement(By.xpath("//select[@name='Region']"));
        regionSelector=regionSelector.findElement(By.xpath("//option[@value=16]"));
        regionSelector.click();
        fillInputField(driver.findElement(By.xpath("//textarea[@name='Comment']")),"Я написала комментарий");
        //Отдельный блок для ввода телефона, так как предусмотрено форматирование
        WebElement phoneNumber = driver.findElement(By.xpath("//input[contains(@data-bind,'value: Phone')]"));
        waitUtilElementToBeClickable(phoneNumber);
        phoneNumber.click();
        String number = "9999999999";
        phoneNumber.sendKeys(number);
        Assert.assertEquals("Поле заполнено некорректно",
                "+7 ("+number.substring(0,3)+") "+number.substring(3,6)+"-"+number.substring(6,8)+"-"+number.substring(8),
                phoneNumber.getAttribute("value"));

        //Согласие на обработку данных
        WebElement checkbox = driver.findElement(By.xpath("//input[@type='checkbox']"));
        checkbox.click();
        Assert.assertEquals("Флаг не проставлен",true,checkbox.isSelected());

        //Отправить
        sendApplicationButton = driver.findElement(By.xpath("//button[contains(text(),'Отправить')]"));
        sendApplicationButton.click();

        //Проверка присутствия ошибки у поля Email
        WebElement error = driver.findElement(By.xpath("//span[@class='validation-error-text']"));//Такой поиск элемента неточен, так как в этом случае есть как минимум 2 подобных поля, но если бы я искала по сообщению, которое потом же проверяю, было бы странно, так что оставила только класс.
        Assert.assertEquals("Проверка ошибки у поля не была пройдена", "Введите адрес электронной почты", error.getText());
    }

    @After
    public void after(){
        driver.quit();
    }

    private void waitUtilElementToBeClickable(WebElement element){
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }


    private void waitUtilElementToBeVisible(By locator){
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private void fillInputField(WebElement element,String value){
        waitUtilElementToBeClickable(element);
        element.click();
        element.sendKeys(value);
        Assert.assertEquals("Поле заполнено некорректно", value, element.getAttribute("value"));
    }
}
