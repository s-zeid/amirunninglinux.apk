/*
 * Copyright (c) 2014 Scott Zeid.
 * http://code.s.zeid.me/amirunninglinux.apk
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 * Except as contained in this notice, the name(s) of the above copyright holders
 * shall not be used in advertising or otherwise to promote the sale, use or
 * other dealings in this Software without prior written authorization.
 * 
 */

package com.amirunninglinux;

import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AmIRunningLinux extends Activity {
 /** Called when the activity is first created. */
 @Override
 public void onCreate(Bundle savedInstanceState) {
  this.requestWindowFeature(Window.FEATURE_NO_TITLE);
  super.onCreate(savedInstanceState);
  setContentView(R.layout.main);
  
  WebView webView = (WebView) this.findViewById(R.id.webview);
  WebSettings webSettings = webView.getSettings();
  
  String ua = webSettings.getUserAgentString()
              + String.format(this.getString(R.string.webview_ua_base), this.getVersion());
  
  // Regex for *accurately* checking the hostname to see if it's ours or a subdomain
  String host = Uri.parse(this.getString(R.string.webview_url)).getHost()
                .replaceAll("(\\.?)(\\:[0-9]+)?$", "");
  final String re = "^([^.]*\\.)*(" + Pattern.quote(host) + ")(\\.?)(\\:[0-9]+)?$";

  synchronized (webSettings) {
   webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
   webSettings.setJavaScriptEnabled(true);
   webSettings.setUseWideViewPort(true);
   webSettings.setUserAgentString(ua);
  }
  
  webView.setWebViewClient(new WebViewClient() {
   @Override
   public boolean shouldOverrideUrlLoading(WebView view, String url) {
    String host = Uri.parse(url).getHost();
    if (host.matches(re))
     return false;
    
    AmIRunningLinux.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    return true;
   }
  });
  
  webView.loadUrl(this.getString(R.string.webview_url));
 }
 
 public String getVersion() {
  try {
   return this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
  } catch (NameNotFoundException e) {
   return "null";
  }
 }
 
 /* Copypasta from
  * <https://developer.android.com/guide/webapps/webview.html#NavigatingHistory> */
 @Override
 public boolean onKeyDown(int keyCode, KeyEvent event) {
  WebView webView = (WebView) this.findViewById(R.id.webview);
  
  // Check if the key event was the Back button and if there's history
  if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
      webView.goBack();
      return true;
  }
  // If it wasn't the Back key or there's no web page history, bubble up to the default
  // system behavior (probably exit the activity)
  return super.onKeyDown(keyCode, event);
 }
}
