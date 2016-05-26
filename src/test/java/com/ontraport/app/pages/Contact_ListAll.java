package com.ontraport.app.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.ontraport.app.tools.AbstractPage;

/**
 * Make jason update this comment block.
 *
 * @author jason
 * @since 5/24/16
 */
public class Contact_ListAll extends AbstractPage
{
    By newContactButton = By.cssSelector("#ontraport_panel_action_new");

    By collectionRow = By.cssSelector("tr.ussr-component-collection-row");

    public Contact_ListAll (RemoteWebDriver d)
    {
        super(d);
    }

    public Contact_Edit clickNewContact ()
    {
        waitForAjax();
        el(newContactButton).click();
        return new Contact_Edit(driver);
    }

    public boolean verifyContactExists (String contactInfo)
    {
        waitForAjax();
        wait.until(ExpectedConditions.visibilityOf(el(collectionRow)));
        for(WebElement row : els(collectionRow))
        {
            if ( row.getText().contains(contactInfo) )
            {
                return true;
            }
        }
        return false;
    }
}
