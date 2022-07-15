package relevant_craft.vento.r_launcher.r_parser.manager.translator;

//import com.machinepublishers.jbrowserdriver.JBrowserDriver;
//import com.machinepublishers.jbrowserdriver.Settings;
//import com.machinepublishers.jbrowserdriver.Timezone;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;

public class WebGoogleTranslator {

//    private static JBrowserDriver driver;
//    private static WebElement input;

    public static void initWebTranslator() {
//        driver = new JBrowserDriver(Settings.builder().timezone(Timezone.EUROPE_MOSCOW).build());
//        driver.get("https://translate.google.com/#view=home&op=translate&sl=en&tl=ru");
//        input = driver.findElement(By.id("source"));
//        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    public static String translate(String text) {
//        try {
//            input.clear();
//            input.submit();
//
//            input.sendKeys(text);
//            input.submit();
//
//            return driver.findElementByClassName("tlid-translation translation").getText();
//        } catch (Exception e) {
//            return text;
//        }

        return null;
    }

    public static void stop() {
//        driver.quit();
    }
}
