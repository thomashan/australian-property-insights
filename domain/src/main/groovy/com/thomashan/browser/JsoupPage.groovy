package com.thomashan.browser

import geb.error.GebException
import geb.error.PageInstanceNotInitializedException
import geb.url.UrlFragment
import org.jsoup.nodes.Document

trait JsoupPage {
    /**
     * Defines the url for this page to be used when navigating directly to this page.
     * <p>
     * Subclasses can specify either an absolute url, or one relative to the browser's base url.
     * <p>
     * This implementation returns an empty string.
     *
     * @see #to(java.util.Map, geb.url.UrlFragment, java.lang.Object)
     */
    static url = ""

    /**
     * Defines the url fragment for this page to be used when navigating directly to this page.
     * <p>
     * Subclasses can specify either a {@code String} which will be used as is or a {@code Map} which will be translated into an application/x-www-form-urlencoded {@code String}.
     * The value used will be escaped appropriately so there is no need to escape it yourself.
     * <p>
     * This implementation does not define a page fragment (i.e. this property is {@code null})
     *
     * @see #to(java.util.Map, geb.url.UrlFragment, java.lang.Object)
     */
    static fragment = null

    JsoupBrowser jsoupBrowser
    Document document

    /**
     * Initialises this page instance, connecting it to the browser.
     * <p>
     * <b>This method is called internally, and should not be called by users of Geb.</b>
     */
    JsoupPage init(JsoupBrowser jsoupBrowser) {
        this.jsoupBrowser = jsoupBrowser
        return this
    }

    /**
     * Sends the browser to this page's url.
     *
     * @param params query parameters to be appended to the url
     * @param fragment optional url fragment identifier
     * @param args "things" that can be used to generate an extra path to append to this page's url
     * @see #convertToPath(java.lang.Object)
     * @see #getPageUrl(java.lang.String)
     */
    // FIXME: seems like Browser.via calls page.to(params, fragment, args) which calls this methods
    void jsoupTo(Map params, UrlFragment fragment = null, Object[] args) {
        String path = convertToPath(*args)
        if (path == null) {
            path = ""
        }
        getInitializedBrowser().go(params, getPageUrl(path), fragment ?: getPageFragment())
        getInitializedBrowser().page(this)
    }

    /**
     * Returns the fragment part of the url to this page.
     * <p>
     * This implementation returns the static {@code fragment} property of the class wrapped in a {@code UrlFragment} instance.
     *
     * @see geb.url.UrlFragment
     */
    UrlFragment getPageFragment() {
        return this.class.fragment ? UrlFragment.of(this.class.fragment) : null
    }

    /**
     * Returns the url to this page, with path appended to it.
     *
     * @see #getPageUrl()
     */
    String getPageUrl(String path) {
        String pageUrl = getPageUrl()
        return path ? (pageUrl ? pageUrl + path : path) : pageUrl
    }

    /**
     * Returns the constant part of the url to this page.
     * <p>
     * This implementation returns the static {@code url} property of the class.
     */
    String getPageUrl() {
        return this.class.url
    }

    /**
     * Converts the arguments to a path to be appended to this page's url.
     * <p>
     * This is called by the {@link #to(java.util.Map, geb.url.UrlFragment, java.lang.Object)} method and can be used for accessing variants of the page.
     * <p>
     * This implementation returns the string value of each argument, separated by "/"
     */
    // tag::convert_to_path[]
    String convertToPath(Object[] args) {
        return args ? '/' + args*.toString().join('/') : ""
    }
    // end::convert_to_path[]

    private JsoupBrowser getInitializedBrowser() {
        if (jsoupBrowser == null) {
            throw uninitializedException()
        }
        return jsoupBrowser
    }

    private GebException uninitializedException() {
        String message = "Instance of page ${getClass()} has not been initialized. Please pass it to Browser.to(), Browser.via(), Browser.page() or Browser.at() before using it."
        return new PageInstanceNotInitializedException(message)
    }
}
