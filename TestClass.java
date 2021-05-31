import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.SystemUtils.getEnvironmentVariable;


public class TestClass {
    AndroidDriver driver;
    protected AppiumDriverLocalService service;

    private static boolean isPortAvailable(final int port) {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            return true;
        } catch (final IOException e) {
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }
    public static int getFreePort(int minValue, int maxValue) {
        int port;
        do {
            Random rand = new Random();
            port = rand.nextInt(maxValue - minValue) + minValue;
        } while (!isPortAvailable(port));

        return port;
    }

    @BeforeTest
    public void setUp() {
        String systemPortString = getEnvironmentVariable("SYSTEM_PORT", String.valueOf(getFreePort(8201, 8501)));
        service = AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
                .usingAnyFreePort()
                .withIPAddress("127.0.0.1"));
        service.start();

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UIAutomator2");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "7.0");
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "SAMSUNG-SM-G920A");
        capabilities.setCapability(MobileCapabilityType.NO_RESET, true);
        capabilities.setCapability(MobileCapabilityType.UDID, "04157df4b4f2a70b");
        capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "com.swaglabsmobileapp");
        capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, "com.swaglabsmobileapp.MainActivity");
        capabilities.setCapability(AndroidMobileCapabilityType.SYSTEM_PORT, Integer.valueOf(systemPortString));
        driver = new AndroidDriver(service.getUrl(), capabilities);
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
    }

    @Test
    public void testSauceLabs() throws InterruptedException {
        driver.findElement(By.xpath("//android.widget.EditText[@text='Username']")).sendKeys("standard_user");
        driver.findElement(By.xpath("//android.widget.EditText[@text='Password']")).sendKeys("secret_sauce");
        driver.findElement(By.xpath("//android.widget.TextView[@text='LOGIN']")).click();
        driver.findElement(By.xpath("//android.widget.TextView[@text='ADD TO CART']")).click();
        //Adding additional sleep as the view is taking time to update
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.xpath("//android.widget.TextView[@text='ADD TO CART']")).click();
        driver.findElement(By.xpath("//android.widget.TextView[@text='2']")).click();
        driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"CHECKOUT\").instance(0))").click();
        //Negative scenario trying to chckout without giving first name and other details
        driver.findElement(By.xpath("//android.widget.TextView[@text='CONTINUE']")).click();
        //Now back to checkout
        Assert.assertEquals("First Name is required",driver.findElement(By.xpath("//android.widget.TextView[@text='First Name is required']")).getText());
        driver.findElement(By.xpath("//android.widget.EditText[@text='First Name']")).sendKeys("Praveen");
        driver.findElement(By.xpath("//android.widget.EditText[@text='Last Name']")).sendKeys("Reddy");
        driver.findElement(By.xpath("//android.widget.EditText[@text='Zip/Postal Code']")).sendKeys("123456");
        driver.findElement(By.xpath("//android.widget.TextView[@text='CONTINUE']")).click();
        driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"Finish\").instance(0))").click();
        //Asserting checkout is complete.
        Assert.assertEquals("CHECKOUT: COMPLETE!",driver.findElement(By.xpath("//android.widget.TextView[@text='CHECKOUT: COMPLETE!']")).getText());

    }
}

