package com.github.ioridazo.selenium.sample.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeleniumControllerTest {

    private WebDriver webDriver;

    @BeforeEach
    public void createDriver() {
        System.setProperty("webdriver.chrome.driver", "C:/Program Files/Google/chromedriver_win32/chromedriver.exe");
        webDriver = new ChromeDriver();
    }

    @AfterEach
    public void quitDriver() {
        webDriver.close();
    }

    @DisplayName("EDINETからEDINETコードリストをダウンロードする")
    @Test
    void pageTitle_display1() throws InterruptedException {
        var uri = "https://disclosure.edinet-fsa.go.jp/E01EW/BLMainController.jsp?uji.bean=ee.bean.W1E62071.EEW1E62071Bean&uji.verb=W1E62071InitDisplay&TID=W1E62071&PID=W0EZ0001&SESSIONKEY=&lgKbn=2&dflg=0&iflg=0";

        //指定したURLに遷移する
        webDriver.get(uri);

        // 最大5秒間、ページが完全に読み込まれるまで待つ
        webDriver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);

        // ダウンロードする
        ((JavascriptExecutor) webDriver).executeScript("EEW1E62071EdinetCodeListDownloadAction('lgKbn=2&dflg=0&iflg=0&dispKbn=1')");

        // chromeダウンロードに移動する
        webDriver.get("chrome://downloads");

        var javascriptExecutor = (JavascriptExecutor) webDriver;
        // 100%完了するまでダウンロードを待つ
        double percentageProgress = 0;
        Thread.sleep(500);
        while (percentageProgress != 100) {
            percentageProgress = (Long) javascriptExecutor.executeScript("return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('#progress').value");
            Thread.sleep(100);
        }

        // Java Queryを使用してファイル名を取得する
        var fileName = (String) javascriptExecutor.executeScript("return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('div#content #file-link').text");
        assertTrue(fileName.contains("Edinetcode_"), "Downloaded File Name");
        // ダウンロードソースリンクのURLを取得する
        var downloadSourceLink = (String) javascriptExecutor.executeScript("return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('div#content #file-link').href");
        assertTrue(downloadSourceLink.contains(" https://disclosure.edinet-fsa.go.jp/E01EW/download"), "Download Link");
        System.out.println("Download Link : " + downloadSourceLink);
    }

    @DisplayName("正常系_動作_ページング_次へボタン")
    @Test
    void test_sample1() {
        // 指定したURLに遷移する
        webDriver.get("https://saikeblog.com");

        // 最大5秒間、ページが完全に読み込まれるまで待つ
        webDriver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);

        // 画面下部にスクロールするjavascriptを実行
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;
        javascriptExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");

        // 「次のページ」要素をクリックする
        WebElement webElement = webDriver.findElement(By.className("next"));
        webElement.click();

        // 検証
        assertEquals("https://saikeblog.com/page/2/", webDriver.getCurrentUrl());
    }

    @Test
    void testGoogleSearch() {
        webDriver.get("https://www.google.com");

        WebElement searchElement = webDriver.findElement(By.name("q"));
        searchElement.sendKeys("selenium");
        searchElement.submit();

        // ページが更新するまで待つ（Timeoutは10秒)
        new WebDriverWait(webDriver, 10)
                .until((ExpectedCondition<Boolean>) webDriver -> webDriver.getTitle().toLowerCase().startsWith("selenium"));

        System.out.println(webDriver.getTitle());
        assertEquals("selenium - Google 検索", webDriver.getTitle());
    }
}