package roshkahybrid.roshka.com.py.testwebviewinjection;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "PVS_LOGCAT1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d(TAG, consoleMessage.message() +
                        " -- From line " + consoleMessage.lineNumber() +
                        " of " + consoleMessage.sourceId());
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onLoadResource(WebView view, String url) {
                Log.d(TAG, "onLoadResource: " + url);
                view.stopLoading();

                if (url.endsWith(".js")) {
                    view.stopLoading();
                    try {
                        URL urlObj = new URL(url);
                        String path = urlObj.getPath();
                        path = path.substring(path.lastIndexOf("/") + 1);

                        if (url.endsWith(".js")) {
                            StringBuilder buf = new StringBuilder();

                            try {
                                InputStream js = getAssets().open(path);
                                BufferedReader in = new BufferedReader(new InputStreamReader(js, "UTF-8"));
                                String str;
                                while ((str = in.readLine()) != null) {
                                    buf.append(str);
                                }

                                in.close();
                            } catch (IOException e) {
                                Log.e(TAG, "File not read", e);
                            }

                            buf.insert(0, "meaning = 42;");
                            webView.loadDataWithBaseURL(url, "", "text/html", "UTF-8", null);
                            webView.loadUrl("javascript: " + buf.toString());
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onPageStarted (WebView view, String url, Bitmap favicon) {

                Log.d(TAG, "onPageStarted: " + url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading: " + url);
//                try {
//                    URL urlObj = new URL(url);
//                    String path = urlObj.getPath();
//
//                    if (url.endsWith(".js")) {
//                        StringBuilder buf = new StringBuilder();
//
//                        try {
//                            InputStream js = getAssets().open(path);
//                            BufferedReader in = new BufferedReader(new InputStreamReader(js, "UTF-8"));
//                            String str;
//                            while ((str = in.readLine()) != null) {
//                                buf.append(str);
//                            }
//
//                            in.close();
//                        } catch (IOException e) {
//                            Log.e(TAG, "File not read", e);
//                        }
//
//                        buf.insert(0, "meaning = 99;");
//
//                        return true;
//                    }
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }

                return  true;
            }
        });

        webView.loadUrl("file:///android_asset/index.html");
    }
}
