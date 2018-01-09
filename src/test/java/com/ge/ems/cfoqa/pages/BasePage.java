package com.ge.ems.cfoqa.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.util.List;

public class BasePage {

    public WebDriver driver;

    public BasePage(WebDriver driver){
        this.driver = driver;
    }

    public void visit(String url){
        driver.get(url);
    }

    public WebElement find(By locator){
        return driver.findElement(locator);
    }

    public List<WebElement> findList(By locator){
        return driver.findElements(locator);
    }

    public void click(By locator){
        Actions actions = new Actions(driver);
        actions.moveToElement(find(locator)).click().perform();
    }

    public void clickAtCoordinates(By locator, int x, int y){}

    public void clickAtCoordinates(WebElement element, int x, int y){
        new Actions(driver).moveToElement(element, x, y).click().build().perform();
    }

    public void click(WebElement element){
        element.click();
    }

    public void doubleClick(By locator) {
        Actions actions = new Actions(driver);
        actions.moveToElement(find(locator)).doubleClick().build().perform();
    }

    public void type(String inputText, By locator){
        find(locator).sendKeys(inputText);
    }

    public void type(Keys inputKeys, By locator){find(locator).sendKeys(inputKeys);}

    public String text(By locator){
        return find(locator).getText();
    }

    public String text(WebElement element){ return element.getText();}

    public String getAttributeValue(WebElement element, String attribute){
        return element.getAttribute(attribute);
    }

    public Boolean isVisible(By locator){
        try{
            return find(locator).isDisplayed();
        } catch (NoSuchElementException nsee){
            Assert.fail("Web element " + locator.toString() + " not found.\n\n" + nsee);
            return false;
        }
    }

    public Boolean isVisible(WebElement element){
        return element.isDisplayed();
    }

    public Boolean isVisible(By locator, int waitTime){
        try{
            WebDriverWait wait = new WebDriverWait(driver, waitTime);
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException ex){
            return false;
        }
        return true;
    }

    public Boolean isVisible(WebElement element, int waitTime){
        try{
            WebDriverWait wait = new WebDriverWait(driver, waitTime);
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException ex){
            return false;
        }
        return true;
    }

    public Boolean isDisabled(By locator){
        return find(locator).getAttribute("disabled").equals("true");
    }

    public Boolean isPresent(By locator, int waitTime){
        try{
            WebDriverWait wait = new WebDriverWait(driver, waitTime);
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException ex){
            return false;
        }
        return true;
    }

    public Boolean isClickable(By locator, int waitTime){
        try{
            WebDriverWait wait = new WebDriverWait(driver, waitTime);
            wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException ex){
            return false;
        }

        return true;
    }

    public void scrollTo(By locator){
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public void refreshPage(){
        driver.navigate().refresh();
    }
}
