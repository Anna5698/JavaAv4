package ru.netology;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderCardTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    static void setupAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().window().maximize();
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void shouldSubmitValidForm() {
        driver.get("http://localhost:9999");

        // Даем время на загрузку страницы
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Заполняем поле имени
        WebElement nameField = driver.findElement(By.cssSelector("[data-test-id='name'] input"));
        nameField.sendKeys("Иван Петров");

        // Заполняем поле телефона
        WebElement phoneField = driver.findElement(By.cssSelector("[data-test-id='phone'] input"));
        phoneField.sendKeys("+79991234567");

        // Кликаем чекбокс согласия
        WebElement agreementCheckbox = driver.findElement(By.cssSelector("[data-test-id='agreement']"));
        agreementCheckbox.click();

        // Нажимаем кнопку отправки
        WebElement submitButton = driver.findElement(By.cssSelector("button"));
        submitButton.click();

        // Ждем успешного сообщения - пробуем разные варианты селекторов
        try {
            // Вариант 1: проверяем по селектору
            WebElement successElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("[data-test-id='order-success'], [data-test-id='success-notification'], .notification_status_ok, .notification__content")
                    ));

            String text = successElement.getText();
            System.out.println("Найденный текст: " + text);
            assertTrue(text.contains("отправлена") || text.contains("успешно") || text.contains("заявка"));

        } catch (TimeoutException e) {
            // Если не нашли по селектору, попробуем найти по тексту на странице
            String pageSource = driver.getPageSource();
            System.out.println("Содержимое страницы: " + pageSource);
            assertTrue(pageSource.contains("отправлена") || pageSource.contains("успешно"));
        }
    }
}
