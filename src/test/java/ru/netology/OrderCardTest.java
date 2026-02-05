package ru.netology;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

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

        // Заполняем поле имени (ожидаем появления элемента)
        WebElement nameField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id='name'] input"))
        );
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

        // Ждем успешного сообщения с использованием явного ожидания
        // Используем data-test-id='order-success' согласно тестовой метке
        WebElement successElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id='order-success']"))
        );

        // Проверяем видимость элемента и текст
        assertTrue(successElement.isDisplayed(), "Сообщение об успехе должно быть видимым");

        // Получаем текст из элемента успеха
        String actualText = successElement.getText().trim();

        // Проверяем, что текст содержит ключевые слова
        assertTrue(actualText.contains("успешно") || actualText.contains("отправлена"),
                "Текст должен содержать 'Ваша заявка успешно отправлена'");
    }
}